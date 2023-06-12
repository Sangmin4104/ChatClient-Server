import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

class ChatRoomDisplay extends JFrame implements ActionListener, KeyListener,
        ListSelectionListener, ChangeListener
{
    private ClientThread cr_thread;
    private String idTo;
    private boolean isSelected;
    public boolean isAdmin;

    private JLabel roomer;
    public JList roomerInfo;
    private JButton coerceOut, PopMassage, sendWord, sendFile, quitRoom;
    private Font font;
    private JViewport view;
    private JScrollPane jsp3;
    public JTextArea messages;
    public JTextField message;

    public ChatRoomDisplay(ClientThread thread){
        super("Chat-Application-대화방");

        cr_thread = thread;
        isSelected = false;
        isAdmin = false;
        font = new Font("SanSerif", Font.PLAIN, 12);

        Container c = getContentPane();
        c.setLayout(null);

        JPanel p = new JPanel();
        p.setLayout(null);
        p.setBounds(425, 10, 140, 155);
        p.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "참여자"));


        roomerInfo = new JList();
        roomerInfo.setFont(font);
        JScrollPane jsp2 = new JScrollPane(roomerInfo);
        roomerInfo.addListSelectionListener(this);
        jsp2.setBounds(15, 25, 110, 105);
        p.add(jsp2);

        c.add(p);

        p = new JPanel();
        p.setLayout(null);
        p.setBounds(10, 10, 410, 340);
        p.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.LOWERED), "채팅창"));

        view = new JViewport();
        messages = new JTextArea();
        messages.setFont(font);
        messages.setEditable(false);
        view.add(messages);
        view.addChangeListener(this);
        jsp3 = new JScrollPane(view);
        jsp3.setBounds(15, 25, 380, 270);
        p.add(jsp3);

        message = new JTextField();
        message.setFont(font);
        message.addKeyListener(this);
        message.setBounds(15, 305, 380, 20);
        message.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        p.add(message);

        c.add(p);

        coerceOut = new JButton("강 제 퇴 장");
        coerceOut.setFont(font);
        coerceOut.addActionListener(this);
        coerceOut.setBounds(445, 170, 100, 30);
        coerceOut.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        c.add(coerceOut);

        PopMassage = new JButton("펑 메 시 지");
        PopMassage.setFont(font);
        PopMassage.addActionListener(this);
        PopMassage.setBounds(445, 205, 100, 30);
        PopMassage.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        c.add(PopMassage);

        sendWord = new JButton("귓말보내기");
        sendWord.setFont(font);
        sendWord.addActionListener(this);
        sendWord.setBounds(445, 240, 100, 30);
        sendWord.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        c.add(sendWord);

        sendFile = new JButton("파 일 전 송");
        sendFile.setFont(font);
        sendFile.addActionListener(this);
        sendFile.setBounds(445, 275, 100, 30);
        sendFile.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        c.add(sendFile);

        quitRoom = new JButton("퇴 실 하 기");
        quitRoom.setFont(font);
        quitRoom.addActionListener(this);
        quitRoom.setBounds(445, 310, 100, 30);
        quitRoom.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        c.add(quitRoom);

        Dimension dim = getToolkit().getScreenSize();
        setSize(580, 400);
        setLocation(dim.width/2 - getWidth()/2,
                dim.height/2 - getHeight()/2);
        show();

        addWindowListener(
                new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                        message.requestFocusInWindow();
                    }
                }
        );

        addWindowListener(
                new WindowAdapter(){
                    public void windowClosing(WindowEvent e){
                        cr_thread.requestQuitRoom();
                    }
                }
        );
    }

    public void resetComponents(){
        messages.setText("");
        message.setText("");
        message.requestFocusInWindow();
    }

    public void keyPressed(KeyEvent ke){
        if (ke.getKeyChar() == KeyEvent.VK_ENTER){
            String words = message.getText();
            String data;
            String idTo;
            if(words.startsWith("/w")){
                StringTokenizer st = new StringTokenizer(words, " ");
                String command = st.nextToken();
                idTo = st.nextToken();
                data = st.nextToken();
                cr_thread.requestSendWordTo(data, idTo);
                message.setText("");
            } else {
                cr_thread.requestSendWord(words);
                message.requestFocusInWindow();
            }
        }
    }

    public void valueChanged(ListSelectionEvent e){
        isSelected = true;
        idTo = String.valueOf(((JList)e.getSource()).getSelectedValue());
    }
    public void CheckAdmin(Vector users){
        System.out.println(users);
        DefaultListModel model = new DefaultListModel();


        HashMap userMap = new HashMap();
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).toString().equals(users.firstElement())){
                userMap.put(users.get(i), true);
            }
            else{
                userMap.put(users.get(i), false);
            }
            model.addElement(users.get(i).toString());
        }
        System.out.println(userMap);
        this.roomerInfo.setModel(model);
        this.roomerInfo.setCellRenderer(new ButtonsRenderer(model, userMap, this.isAdmin));

        if(this.isAdmin){

            this.roomerInfo.addMouseListener(new CellButtonsMouseListener<>(this.roomerInfo));
        }
    }


    public void actionPerformed(ActionEvent ae){
        if (ae.getSource() == coerceOut) {
            if (!isAdmin) {
                JOptionPane.showMessageDialog(this, "당신은 방장이 아닙니다.",
                        "강제퇴장", JOptionPane.ERROR_MESSAGE);
            } else if (!isSelected) {
                JOptionPane.showMessageDialog(this, "강제퇴장 ID를 선택하세요.",
                        "강제퇴장", JOptionPane.ERROR_MESSAGE);
            } else {
                cr_thread.requestCoerceOut(idTo);
                isSelected = false;
            }
        } else if (ae.getSource() == PopMassage) {
            String msg;
            if ((msg = JOptionPane.showInputDialog("펑메세지를 입력하세요.(5초 뒤에 사라집니다.)")) != null) {
                cr_thread.requestPopMessage(msg);
            }

        }else if (ae.getSource() == quitRoom) {
            cr_thread.requestQuitRoom();
        } else if (ae.getSource() == sendWord) {
            String idTo, data;
            if ((idTo = JOptionPane.showInputDialog("아이디를 입력하세요.")) != null){
                if ((data = JOptionPane.showInputDialog("메세지를 입력하세요.")) != null) {
                    cr_thread.requestSendWordTo(data, idTo);
                }
            }
        } else if (ae.getSource() == sendFile) {
            String idTo;
            if ((idTo = JOptionPane.showInputDialog("상대방 아이디를 입력하세요.")) != null){
                cr_thread.requestSendFile(idTo);
            }
        }
    }

    public void stateChanged(ChangeEvent e){
        jsp3.getVerticalScrollBar().setValue((jsp3.getVerticalScrollBar().getValue() + 20));
    }
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}

    class CellButtonsMouseListener<E> extends MouseInputAdapter {
        private int prevIndex = -1;
        private JButton prevButton;
        private final JList<E> list;

        protected CellButtonsMouseListener(JList<E> list) {
            super();
            this.list = list;
        }

        @Override public void mouseMoved(MouseEvent e) {
            // JList<?> list = (JList<?>) e.getComponent();
            Point pt = e.getPoint();
            int index = list.locationToIndex(pt);
            if (!list.getCellBounds(index, index).contains(pt)) {
                if (prevIndex >= 0) {
                    rectRepaint(list, list.getCellBounds(prevIndex, prevIndex));
                }
                prevButton = null;
                return;
            }
            ListCellRenderer<? super E> lcr = list.getCellRenderer();
            if (index >= 0 && lcr instanceof ButtonsRenderer) {
                ButtonsRenderer<?> renderer = (ButtonsRenderer<?>) lcr;
                JButton button = getButton(list, pt, index);
                renderer.button = button;
                if (Objects.nonNull(button)) {
                    repaintCell(renderer, button, index);
                } else {
                    repaintPrevButton(renderer, index);
                }
                prevButton = button;
            }
            prevIndex = index;
        }

        private void repaintCell(ButtonsRenderer<?> renderer, JButton button, int index) {
            button.getModel().setRollover(true);
            renderer.rolloverIndex = index;
            if (!Objects.equals(button, prevButton)) {
                rectRepaint(list, list.getCellBounds(prevIndex, index));
            }
        }

        private void repaintPrevButton(ButtonsRenderer<?> renderer, int index) {
            renderer.rolloverIndex = -1;
            if (prevIndex == index) {
                if (Objects.nonNull(prevButton)) {
                    rectRepaint(list, list.getCellBounds(prevIndex, prevIndex));
                }
            } else {
                rectRepaint(list, list.getCellBounds(index, index));
            }
            prevIndex = -1;
        }

        @Override public void mousePressed(MouseEvent e) {
            // JList<?> list = (JList<?>) e.getComponent();
            Point pt = e.getPoint();
            int index = list.locationToIndex(pt);
            if (index >= 0) {
                JButton button = getButton(list, pt, index);
                ListCellRenderer<? super E> renderer = list.getCellRenderer();
                if (Objects.nonNull(button) && renderer instanceof ButtonsRenderer) {
                    ButtonsRenderer<?> r = (ButtonsRenderer<?>) renderer;
                    r.pressedIndex = index;
                    r.button = button;
                    rectRepaint(list, list.getCellBounds(index, index));
                }
            }
        }

        @Override public void mouseReleased(MouseEvent e) {
            // JList<?> list = (JList<?>) e.getComponent();
            Point pt = e.getPoint();
            int index = list.locationToIndex(pt);
            if (index >= 0) {
                JButton button = getButton(list, pt, index);
                ListCellRenderer<? super E> renderer = list.getCellRenderer();
                if (Objects.nonNull(button) && renderer instanceof ButtonsRenderer) {
                    ButtonsRenderer<?> r = (ButtonsRenderer<?>) renderer;
                    r.pressedIndex = -1;
                    r.button = null;
                    button.doClick();
                    rectRepaint(list, list.getCellBounds(index, index));
                }
            }
        }

        private  void rectRepaint(JComponent c, Rectangle rect) {
            Optional.ofNullable(rect).ifPresent(c::repaint);
        }

        private  <E> JButton getButton(JList<E> list, Point pt, int index) {
            E proto = list.getPrototypeCellValue();
            ListCellRenderer<? super E> cr = list.getCellRenderer();
            Component c = cr.getListCellRendererComponent(list, proto, index, false, false);
            Rectangle r = list.getCellBounds(index, index);
            c.setBounds(r);
            // c.doLayout(); // may be needed for other layout managers (eg. FlowLayout) // *1
            pt.translate(-r.x, -r.y);
            // Component b = SwingUtilities.getDeepestComponentAt(c, pt.x, pt.y);
            // if (b instanceof JButton) {
            //   return (JButton) b;
            // } else {
            //   return null;
            // }
            return Optional.ofNullable(SwingUtilities.getDeepestComponentAt(c, pt.x, pt.y))
                    .filter(JButton.class::isInstance).map(JButton.class::cast).orElse(null);
        }
    }

    class ButtonsRenderer<E> implements ListCellRenderer<E> {
        private  final Color EVEN_COLOR = new Color(0xE6_FF_E6);
        private final JLabel textArea = new JLabel();
        private final JButton deleteButton = new JButton();

        private final JPanel renderer = new JPanel(new BorderLayout()) { // *1
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width = 0; // VerticalScrollBar as needed
                return d;
            }
        };
        private int targetIndex;
        protected int pressedIndex = -1;
        protected int rolloverIndex = -1;
        protected JButton button;
        private boolean isAdmin;
        public HashMap<String, Boolean> userMap;
        public DefaultListModel<E> model;
        protected ButtonsRenderer(DefaultListModel<E> model, HashMap userMap, boolean isAdmin) {
            this.userMap = userMap;
            this.model = model;
            this.isAdmin = isAdmin;
            renderer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
            renderer.setOpaque(true);
//            textArea.setLineWrap(true);
            textArea.setOpaque(false);
            renderer.add(textArea);
            deleteButton.addActionListener(e -> {
                boolean oneOrMore = model.getSize() > 1;
                if (oneOrMore) {
                    cr_thread.requestChatBan(model.get(targetIndex).toString());
                }
            });

            if (isAdmin){
                ImageIcon img = new ImageIcon("img_1.png");
                img = imageSetSize(img, 20, 20);
                deleteButton.setIcon(img);
                deleteButton.setFocusable(false);
                deleteButton.setRolloverEnabled(false);

                renderer.add(deleteButton, BorderLayout.EAST);

            }

        }

        @Override public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
            ImageIcon img = new ImageIcon("img.png");
            img = imageSetSize(img, 10, 10);
            ImageIcon img1 = new ImageIcon("img_3.png");
            img1 = imageSetSize(img1, 10, 10);
            textArea.setText(Objects.toString(value, ""));
            System.out.println(value);
            this.targetIndex = index;

            if(userMap.containsKey(Objects.toString(value, ""))){
                Optional<Boolean> optional = Optional.ofNullable(this.userMap.get(Objects.toString(value, "")));
                if (optional.isPresent()){
                    if(optional.get()){
                        textArea.setIcon(img);
                    }
                    else{
                        textArea.setIcon(img1);
                    }
                }
            }

            this.targetIndex = index;
            if (isSelected) {
                renderer.setBackground(list.getSelectionBackground());
                textArea.setForeground(list.getSelectionForeground());
            } else {
                renderer.setBackground(index % 2 == 0 ? EVEN_COLOR : list.getBackground());
                textArea.setForeground(list.getForeground());
            }
            if (Objects.nonNull(button)) {
                if (index == pressedIndex) {
                    button.getModel().setSelected(true);
                    button.getModel().setArmed(true);
                    button.getModel().setPressed(true);
                } else if (index == rolloverIndex) {
                    button.getModel().setRollover(true);
                }
            }
            return renderer;
        }

        private void resetButtonStatus(AbstractButton button) {
            ButtonModel model = button.getModel();
            model.setRollover(false);
            model.setArmed(false);
            model.setPressed(false);
            model.setSelected(false);
        }
        ImageIcon imageSetSize(ImageIcon icon, int i, int j) { // image Size Setting
            Image ximg = icon.getImage();  //ImageIcon을 Image로 변환.
            Image yimg = ximg.getScaledInstance(i, j, java.awt.Image.SCALE_SMOOTH);
            ImageIcon xyimg = new ImageIcon(yimg);
            return xyimg;
        }
    }
}
