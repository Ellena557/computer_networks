import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;

public class MainPassword {
    int maxLen = 6;
    // 170 и 171 пакеты
    static String md5ChallengeValue = "adb41a1ecae4e29c14736b804b92c101";
    static String md5ResponceValue = "427bfb8a766398310de44bfd36471a8b";

    /*
    Пара слов про алфавит: каждый раз добавляла к цифрам по очереди по 2 символа: большой и маленький регистр
    (чтобы сразу весь алфавит не добавлять, а то работает довольно долго). И так до тех пор, пока не получим
    нужный нам пароль. В итоге, на букве "е" получилось остановиться, сработало довольно быстро.
    Вообще правильным решением было бы сделать:
    static String myAlphabet = "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz!?$@+=-";
    (и еще какое-то число символов в конце)
    Либо же для начала просто написать весь алфавит в двух регистрах: но работает это довольно долго, так что
    эффективнее действовать итеративно, по очереди приписывая символы и проверяя, не достигли ли мы ответа.
     */

    static String myAlphabet = "0123456789AaBbCcDdEe";

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int len = myAlphabet.length();
        for (int i = 0; i < Math.pow(len, 6); i++) {

            String currentPwd = makeString(i);
            String hashed = checkOnePassword(currentPwd);
            if (hashed.equals(md5ResponceValue)) {
                System.out.println("REAL PASSWORD: " + currentPwd);
                break;
            }

//            if (i%100000 == 0) {
//                System.out.println(makeString(i));
//            }
        }

    }


    public static String checkOnePassword(String password) throws NoSuchAlgorithmException {
        String allPassword = "2"; //request + password + md5ChallengeValue;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] myBytes = password.getBytes();
        byte[] newbytes = new byte[1 + myBytes.length];
        newbytes[0] = 2; // this is req ID
        for (int i = 1; i <= myBytes.length; i++) {
            newbytes[i] = myBytes[i - 1];
        }

        byte[] bytes16 = turnStringToBytes(md5ChallengeValue);
        byte[] mFinalBytes = new byte[newbytes.length + bytes16.length];
        for (int i = 0; i < newbytes.length; i++) {
            mFinalBytes[i] = newbytes[i];
        }
        for (int i = 0; i < bytes16.length; i++) {
            mFinalBytes[newbytes.length + i] = bytes16[i];
        }

        md.update(mFinalBytes);
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
        //System.out.println(myHash);
        return myHash;
    }


    public static byte[] turnStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    // делает за счет номера уникальную строку из символов алфавита, так перебираются все возможные строки
    public static String makeString(int i) {
        int len = myAlphabet.length();
        String res = "";
        int currentI = i;
        int pos = i % len;
        res += myAlphabet.substring(pos, pos + 1);
        for (int j = 1; j <= 5; j++) {
            currentI = (int) currentI / len;
            pos = currentI % len;
            res += myAlphabet.substring(pos, pos + 1);
        }
        // actually, it is a reversed res, but it doesn't matter in this case
        return res;
    }
}
