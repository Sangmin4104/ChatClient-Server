import javax.swing.*;

public class ChatClient
{
    public static String getLogonID(){
        String logonID = "";
        try{
            while(logonID.trim().isEmpty() || containsSpecialCharacters(logonID)){
                logonID = JOptionPane.showInputDialog("로그온 ID를 입력하세요.");
                if (containsSpecialCharacters(logonID)) {
                    JOptionPane.showMessageDialog(null, "ID에는 자음, 모음, 특수문자를 포함할 수 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                }
            }
        }catch(NullPointerException e){
            System.exit(0);
        }
        return logonID;
    }
    private static boolean containsSpecialCharacters(String str) {
        String specialCharacters = "[ㄱㄴㄷㄹㅁㅂㅅㅃㅉㄸㄲㅆㅇㅈㅊㅋㅌㅍㅎㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣㅢㅟㅚㅐㅖㅒ@!%+#$%^&*(),.?\":{}|<>_-]";

        return str.matches(".*" + specialCharacters + ".*");
    }
    public static void main(String args[]){
        String id = getLogonID();
        try{
            if (args.length == 0){
                ClientThread thread = new ClientThread();
                thread.start();
                thread.requestLogon(id);
            } else if (args.length == 1){
                ClientThread thread = new ClientThread(args[0]);
                thread.start();
                thread.requestLogon(id);
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
