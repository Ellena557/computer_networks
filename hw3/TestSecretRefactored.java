import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestSecretRefactored {
    static String mesType = "0800";
    static String mmAUTH = "1815ccaa3f2fb940562b39282fb59d4a"; //pack 3: request: autenticator

    // одна из частей пакеты
    static String mEAP = "4f0603020004";

    // просто часть пакета
    static String mCl = "19205fa3069b000001370001c0a83c6701c19319eabffa400000000000000005";

    // реальный message autenticator, мы будем сравнивать с ним то, что получаем
    static String mAut = "b3116060e890dbd59fa5f8ba04e64106";

    // When the message integrity check is calculated the signature
    // string should be considered to be sixteen octets of zero.
    static String nulMes = "00000000000000000000000000000000";

    // Разделила на части, чтобы было понятнее: это просто весь пакет, только с заменой обычного autenticator
    // и нули стоят там, где message autenticator
    static String hexDump = "02070052" + mmAUTH + "060600000002" + mEAP + mCl + "5012" + nulMes;


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        for (int i = 0; i < Math.pow(10, 6); i++) {
            String currentKey = "";
            if (i < 10) {
                currentKey = "0000" + i;
            } else if (i < 100) {
                currentKey = "000" + i;
            } else if (i < 1000) {
                currentKey = "00" + i;
            } else if (i < 10000) {
                currentKey = "0" + i;
            } else {
                currentKey = "" + i;
            }
            String mesAuthCurrent = hmacMD5(hexDump, currentKey);
            if (mesAuthCurrent.equals(mAut)) {
                System.out.println("RESULT: " + currentKey);
                break;
            }
        }
    }


    public static String hmacMD5(String message, String currentKey) throws NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException {
        String myHash = "";
        byte[] keyBytes = (currentKey).getBytes("UTF-8");
        byte[] mesBytes = turnStringToBytes(message);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(key);
        byte[] bytes = mac.doFinal(mesBytes);

        for (int i = 0; i < bytes.length; i++) {
            String res = Integer.toHexString(0xFF & bytes[i]);
            if (res.length() == 1) {
                myHash += "0";
            }
            myHash += res;
        }
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
}
