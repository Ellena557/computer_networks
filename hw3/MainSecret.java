import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MainSecret {
    // I WILL TAKE PACKETS 13, 14: request, accept
    static String mesType = "0800";
    static int maxLen = 6;
    static String mmAUTH = "4b218cb4079ca3f53cd02dba076a2d87"; //pack 13: request: autenticator

    // одна из частей пакеты
    static String mEAP = "4f0603020004";

    // просто часть пакета
    //static String mCl = "19205fa3069b000001370001c0a83c6701c19319eabffa400000000000000005";
    static String mCl = "19203ed204d70000013700017f00000101c1923e5a3cf3fa0000000000000008";

    // реальный message autenticator, мы будем сравнивать с ним то, что получаем
    static String mAut = "e85e895d360a162db31f8f3041b26839";

    // When the message integrity check is calculated the signature
    // string should be considered to be sixteen octets of zero.
    static String nulMes = "00000000000000000000000000000000";

    // Разделила на части, чтобы было понятнее: это просто весь пакет, только с заменой обычного autenticator
    // и нули стоят там, где message autenticator
    static String hexDump = "020b0052" + mmAUTH + "060600000002" + mEAP + mCl + "5012" + nulMes;

    /*
    Алфавит перебираю, для начала взяв тот же, что и при поиске пароля:
    если бы он не подошел, то итеративно добавляла бы по паре букв обоих регистров, до тех пор пока результат
    не был бы получен.
    Однако символов хватило.
    Оставляю здесь этот вариант, так как основной работает очень долго (перебор очень большого числа символов).
     */
    static String myAlphabet = "0123456789AaBbCcDdEe";


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        int alLen = myAlphabet.length();
        for (int i = 0; i < Math.pow(alLen, maxLen); i++) {
            String currentKey = makeString(i);
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
