import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;

public class TestPasswordRefactored {
    int maxLen = 5;
    static String md5ChallengeValue = "ce79ec7f045c5a9356c828b11f93c101";
    static String md5ResponceValue = "99740999119bb0c6021d0741ff9365b4";
    static int[] responce = {0, 0, 0, 0, 0, 0, 0, 2};

    public static void main(String[] args) throws NoSuchAlgorithmException {
        for (int i = 0; i < Math.pow(10, 6); i++) {

            String currentPwd = "";
            if (i < 10) {
                currentPwd = "0000" + i;
            } else if (i < 100) {
                currentPwd = "000" + i;
            } else if (i < 1000) {
                currentPwd = "00" + i;
            } else if (i < 10000) {
                currentPwd = "0" + i;
            } else {
                currentPwd = "" + i;
            }
            String hashed = checkOnePassword(currentPwd);
            if (hashed.equals(md5ResponceValue)) {
                System.out.println("REAL PASSWORD: " + currentPwd);
            }
            if (i % 20000 == 0) {
                //System.out.println("Iteration " + i + " passed.");
            }
        }

    }


    public static String checkOnePassword(String password) throws NoSuchAlgorithmException {
        String allPassword = "2"; //ID + password + md5ChallengeValue;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] myBytes = password.getBytes();
        byte[] newbytes = new byte[1 + myBytes.length];
        newbytes[0] = 2; // this is req ID
        for (int i = 1; i <= myBytes.length ; i++) {
            newbytes[i] = myBytes[i-1];
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
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
