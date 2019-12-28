import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SpaceCatSpy {
    public static void main(String[] args) throws IOException {
        // 881
        String pack1 = "accdefghijklmnopqrstuvwabceegghijklmnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijklmnnpqrstuvwabcdefghhkklmnopqrsttwwabcdefgihjjlmnopqrsttvwabcdefghikklmnopqrstuwwabbdefghijklmnnqqrstuwwabcddfghijjlmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghijklmnnpqrstuvwaccdefghijklmnopqrruuvwabcdefgihjklmnopqrsutvwabcdefghhjklmnopqrstuvwabcdefghhjklmnopqrsttvwabcdegfhijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghikkllnopqrstuvwabcdefghijkmmnopqrstuvw`cbdefghikklmonpqrstuwwabcdefghijjlmnopqrstuvvaccdefghijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdeggiijklmnnqprstuvwabbeefghijklmnoqqrsttvwabcdegfiijklmnopqssuuwwabcdeffhhjjlmnopprruuwv`bcdeggiijklmnopqrstuvwabcdefghijklmnopqr";

        // 895
        String pack2 = "`bbeefghijjlmnopqrstuvvabcdefghijkllnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijklmnnpqrstuwwabceefghhjjlmnoqqrsutvwabcdefgihjjlmnopqrstuvvabcdefghijkmmoopqrstuwvabbeefghijjmmnoqprstuvv`bcdeffhijjlmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghijklmnopqrstuvwaccedffhijklmnopprruuvwabcdegghijklmnopqrstuvwabcdefghhkklmnopqrsttwwabcdefghhjklmnopqsstuvwabcdegghijklmnopqrstuvwabcdefghijklmnopqrstuvvabcdefghikjmlnopqrstuvv`bcdefghijkllnopqrstuvwaccdefghijklmnopqrstuvwabceefghijjlmnopqrstuvvaccdefghijkmmnopqrstuvwabcdefghijklmnopqrstuvwabcdeggiijklmnoqprsuuvwabbdefghhjklmnoqqrrttvwabcddffhikklmnoppsstuvwabcddgghhjklmnopprsuuvw`bcdeggiijklmnopqrstuvwabcdefghijklmnopqr";

        // 980
        String pack3 = "acbdefghijjlmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijklmnopqrstuwwaccdefgihjjlmooqqrstuvwabbddfgiijjllnopprstuvwacbdefghijjmmnnpqrstuvv`bceefghijklmnopprstuvv`bcdegfhijjlmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghijklmnopqsstuvwaccddffiijklmnopqrsuuvwabcdegghijklmnopqsstuvwabcdefghijklmnoqqrsutvwabcddfghhkjlmnoppsstuvvabcdeggiijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghhkjllnopqrsttvvabcdefghijkmmnopqrstuvv`cbdefghikkllonpqrstuwwabceefghijklmnoqqrstuvwaccdefghijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefgiijklmnoqpssuuvwabbdefghijklmnoqqrrtuvwabcdeffhijklmnopqssuuvwabcddggihjklmnopqsruuvw`bcdegfiijklmnopqrstuvwabcdefghijklmnopqr";

        // 1166
        String pack4 = "accdefghijjlmnnpqrstuvwabcdefghijkmmnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijkmmnopqrstuww`ccdefgihjkllnopqrstuvw`cbddfgiijjllonpprsuuwwabbdegghijjlmnopqsstuvwabcdefghijjllnopqrstuvv`ccdegfhijjlmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghijklmnoqqsstuvwaccddfgiijklmoopqrstuvwabbdefghijklmnnpqsrtuvwabceefgiikklmnoqqrsuuwwabcdefghikjlmnopqsstuvwabcdeggihjklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghikkllnopqrsttvwaccdefghhkklmoopqrsttwvabbdefghikjllnopqrstuvwacbdefghijklmonqqrstuvwacbeefghijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghijklmnoqqsstuvwabbddgfhijklmnoqqrrtuvwabcdeffihjklmnoqqrrutvwabcddfgihkjlmnopqsrttvw`bcdegfiijklmnopqrstuvwabcdefghijklmnopqr";

        // 1508
        String pack5 = "abbdefghijjlmnopqrrtuvvabcdefghijkmmnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijklmnopqrstuwwaccdefgihjkmmnopqrstuvw`bcddfghikjlmnnpqrsuuwvabbeefghijkmlnnpqsstuvw`cceeffhijjllooqprrtuww`cbdegfhijjlmnopqrstuvwabcdefghijklmnopqrstuvwabcdegghijklmnoqqsstuvwabcdegghijklmnnpqssuuvwabceeffhhjklmnoqqsrttvwabcddfgiikklmnopprstuvwabcdefghikklmnopqrstuwwabcdeggihjklmnopqrstuvwabcdefghijkllnopqrstuvw`ccdefghikkllnopqrsuuvwaccdefghhjklmnopqrstuvv`bcdefghijjmlnopqrstuvvaccdefghijkmmnopqrstuvw`ccdefghijklloopqrstuvwabcdefghijklmnopqrstuvwabcdeffhhjklmnopqsrtuvwabbdegfiijklmnoqqsruuvwabcedgfiijjlmnoqpssuuvwabcdegfhijjlmnopqrrtuvw`bcdegghijklmnopqrstuvwabcdefghijklmnopqr";

        // 1518
        String pack6 = "abcdefghijjlmnnpqrstuvvabcdefghijklmnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijklmnopqrstuwwaccdeffihkjmmnnpqrstuwv`bcedfghikjmmnoqqrsuuvv`bcedfghhjkllnoqprsttwwabcddgfhikklmnoqpsrtuww`bcddffiijjlmnopqrstuvwabcdefghijklmnopqrrtuvwabcddgghijklmnoqprrtuvwabbdefgiijklmnnpqrsuuvwabcdefghijklmnopqssuuvwabcdeffihjklmnopqrsutvwabcdegghhjklmnopqrstuvwabcdefgihjklmnopqrstuvwabcdefghijklmnopqrrtuvwaccdefgiikjlmoopqrsuuvvabcdefghijkmmnopqrstuvw`bcdefghijkmmnopqrstuvv`bcdefghijkmlnnpqrstuvw`cceefghijkllnopqrstuvwabcdefghijklmnopprstuvwabcdegghhjklmnopqsstuvwabbedfghijklmnnqprstuvwabcedgghijjlmnopqrstuvwabcdefghijjlmnopqrstuvv`bcdegghijklmnopqrstuvwabcdefghijklmnopqr";

        // 1588
        String pack7 = "`ccdefghijklmnopqrrtuvwabcdefghijkmmnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijklmnopqrstuvwaccdeffihjjlmnopqrrutvv`bcdefgihjkllnopprsuuvwaccdegghikklmnopqsstuvwabcdefghijklmnopqrstuvwabcdeffhijklmnopqrstuvwabcdefghijklmnopqrrtuvwabcdeggiijklmnoqqsruuvwabbdeffhhjklmnopqrrtuvwabcdefgiijklmnopqrsuuvwabcdeffhijklmnopqrstuvwabcdegghijklmnopqssutvwabcdefgiijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghikklmnopqrstuwwabbdefghijjlmnnpqrstuvvabcdefghijjmmoopqrstuvvabbeefghijklmnnqqrstuvwabcdefghijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghhjklmnopqrstuvwabbdefghhjklmnnpqrsttvwabcdefghikjlmnopqrstuwwabcdefghijjlmnopqrsttvvabcdefghijkllnopqrstuvwabcdefghijklmnopqr";

        // 1590
        String pack8 = "abbdefghijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghijkllonpqrstuvwabcdefghijklmnopqrstuvwacceefghhjklmnoqqrsttvwabcddfghhjklmnopqrsttvwaccdefghikklmoopqrstuwwabbdefghijjlmnnpqrstuvvabcdeffhijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdegghijklmnoqqsstuvwabbeeffhhjklmnoqqrsttvwabcddfghijklmnopprstuvwabcdegghijklmnopqsrtuvwabcdeffiijklmnopqsruuvwabcdefgiijklmnopqrstuvwabcdefghijklmnopqrstuvwabcdefghikklmoopqrstuwwabcdefghijklmnnpqrstuvwacceefghijjlmonpqrstuvwabceefghijklmnopqrstuvwabbdefghijklmnopqrstuvwabcdefghijklmnopqsstuvwabcdeffihjklmnoqprsuuvwabcdegghhjklmnopqsstuwwabcdeffhijjlmnopqrstuvwabcdefghijklmnopqrsttvvaccdefghijkllnopqrstuvwabcdefghijklmnopqr";

        String oneAlphabet = "abcdefghijklmnopqrstuvw";
        String alphabetMessage = "";
        for (int i = 0; i < 40; i++) {
            alphabetMessage += oneAlphabet;
        }

        // here is alphabet of a..w with which I will compare other data
        alphabetMessage = alphabetMessage.substring(0, pack1.length());

        // here are strings that consist of "0" and "1"
        String p1 = checkPosEquals2(pack1, alphabetMessage);
        String p2 = checkPosEquals2(pack2, alphabetMessage);
        String p3 = checkPosEquals2(pack3, alphabetMessage);
        String p4 = checkPosEquals2(pack4, alphabetMessage);
        String p5 = checkPosEquals2(pack5, alphabetMessage);
        String p6 = checkPosEquals2(pack6, alphabetMessage);
        String p7 = checkPosEquals2(pack7, alphabetMessage);
        String p8 = checkPosEquals2(pack8, alphabetMessage);

        // here I put these strings into one string-array to work with it after that
        String[] img = new String[8];
        img[0] = p1;
        img[1] = p2;
        img[2] = p3;
        img[3] = p4;
        img[4] = p5;
        img[5] = p6;
        img[6] = p7;
        img[7] = p8;

//        for (int i = 0; i < 8; i++) {
//            System.out.println(img[i]);
//        }
        doImg(img);
    }

    // the different symbols in 2 strings
    public static String checkEquals(String s1, String s2) {
        if (s1.equals(s2)) {
            return "";
        } else {
            String res = "";
            for (int i = 0; i < s1.length(); i++) {
                if (s1.charAt(i) != s2.charAt(i)) {
                    res += s1.charAt(i);
                }
            }
            return res;
        }
    }


    // 1: this position is not equal in strings, 0: else (if it is equal)
    public static String checkPosEquals2(String s1, String s2) {
        String res = "";
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                res += "1";
            } else {
                res += "0";
            }
        }
        return res;
    }


    // this function turns a symbol from a string into integer
    public static int charToBit(char cc) {
        String sybs = "10";
        if (cc == sybs.charAt(0)) {
            return 1;
        } else {
            return 0;
        }
    }

    // this function creates a BMP-file with the image
    public static void doImg(String[] img) throws IOException {
        byte[] bytes = new byte[662];

        // here I fill the array of bytes
        for (int i = 0; i < 662; i++) {
            byte b = 0;
            for (int j = 0; j < 8; j++) {
                b += (Math.pow(2, j)) * charToBit(img[j].charAt(i));
            }
            bytes[i] = b;
        }

        // image creation
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        ImageIO.write(image, "BMP", new File("catMsg.bmp"));
    }
}
