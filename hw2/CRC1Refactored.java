import java.util.ArrayList;

public class CRC1Refactored {
    public static void main(String[] args) {
        // this is unchangeable
        String dst_mac = "FF:FF:FF:FF:FF:FF";       //broadcast
        String etherType = "08:06";                 // this is ARP
        String dst_mac2 = "00:00:00:00:00:00";

        // Разделяю : чтобы проще было делать split (как у mac-адреса)
        String data = "00:01:08:00:06:04:00:01";

        // this is data from the task
        String src_mac = "9E:C7:F6:58:B7:56";
        String src_ip = "63.30.75.0";
        String dst_ip = "131.88.231.30";

        // These results we calculate
        String arp_head = dst_mac + src_mac + etherType;
        String arp_main = data + src_mac + src_ip + dst_mac2 + dst_ip;
        String arp_pad = "";

        // now we don't take FCS into account
        String all_message = arp_head + arp_main + arp_pad;

        // постепенно добавляем в сообщение все данные (поля) в двоичном виде
        ArrayList<Integer> myMessage = new ArrayList<>();
        myMessage = addMACtoBits(myMessage, dst_mac);
        myMessage = addMACtoBits(myMessage, src_mac);
        myMessage = addMACtoBits(myMessage, etherType);
        myMessage = addMACtoBits(myMessage, data);
        myMessage = addMACtoBits(myMessage, src_mac);
        myMessage = addIPtoBits(myMessage, src_ip);
        myMessage = addMACtoBits(myMessage, dst_mac2);
        myMessage = addIPtoBits(myMessage, dst_ip);

        int myLen = myMessage.size();

        for (int i = 0; i < 56 * 8 - myLen; i++) {
            // padding: to have sum length of 60 oktets
            myMessage.add(0);
        }

        // place for CRC
        for (int i = 0; i < 32; i++) {
            myMessage.add(0);
        }

        // дополнение от первых 32 бит + перевернуть октеты
        refactorMessage(myMessage);

        // эти биты добавляем на будущее: когда надо делать xor, чтобы в конце пройти все биты сообщения
        for (int i = 0; i < 32; i++) {
            myMessage.add(0);
        }

        ArrayList<Integer> result3 = countRest32(myMessage, returnPoly());

        // берем дополнение от результата
        convertMess(result3);

        System.out.println("CRC result: " + result3);
        System.out.println("CRC result in 10th format: " + turn2to10(result3));
    }


    public static long turn2to10(ArrayList<Integer> num) {
        long res = 0;
        for (int i = 0; i < num.size(); i++) {
            res += num.get(num.size() - i - 1) * Math.pow(2, i);
        }
        return res;
    }

    public static int[] turn10to2Octet(int num) {
        ArrayList<Integer> myRes = new ArrayList<>();
        if (num == 0) {
            int[] r = {0, 0, 0, 0, 0, 0, 0, 0};
            return r;
        }
        else {
            while (num > 0) {

                int rest = num % 2;
                myRes.add(0, rest);
                num = (int) num / 2;
            }
            int[] res = new int[8];

            if (myRes.size() < 8) {
                for (int i = 0; i < 8 - myRes.size(); i++) {
                    res[i] = 0;
                }
            }

            for (int i = 8 - myRes.size(); i < 8; i++) {
                res[i] = myRes.get(i - 8 + myRes.size());
            }

            return res;
        }
    }


    // полином из задания в двоичном виде
    public static ArrayList<Integer> returnPoly() {
        int[] powers = {32, 30, 29, 28, 26, 20, 19, 17, 16, 15, 11, 10, 7, 6, 4, 2, 1, 0};
        ArrayList<Integer> poly = new ArrayList<>();
        for (int i = 0; i < 33; i++) {
            poly.add(0);
        }
        for (int i = 0; i < powers.length; i++) {
            poly.set(32 - powers[i], 1);
        }
        return poly;
    }


    public static ArrayList<Integer> turn16to2(String input) {
        ArrayList<Integer> output = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '0') {
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(0);
            } else if (input.charAt(i) == '1') {
                output.add(0);
                output.add(0);
                output.add(0);
                output.add(1);
            } else if (input.charAt(i) == '2') {
                output.add(0);
                output.add(0);
                output.add(1);
                output.add(0);
            } else if (input.charAt(i) == '3') {
                output.add(0);
                output.add(0);
                output.add(1);
                output.add(1);
            } else if (input.charAt(i) == '4') {
                output.add(0);
                output.add(1);
                output.add(0);
                output.add(0);
            } else if (input.charAt(i) == '5') {
                output.add(0);
                output.add(1);
                output.add(0);
                output.add(1);
            } else if (input.charAt(i) == '6') {
                output.add(0);
                output.add(1);
                output.add(1);
                output.add(0);
            } else if (input.charAt(i) == '7') {
                output.add(0);
                output.add(1);
                output.add(1);
                output.add(1);
            } else if (input.charAt(i) == '8') {
                output.add(1);
                output.add(0);
                output.add(0);
                output.add(0);
            } else if (input.charAt(i) == '9') {
                output.add(1);
                output.add(0);
                output.add(0);
                output.add(1);
            } else if (input.charAt(i) == 'A') {
                output.add(1);
                output.add(0);
                output.add(1);
                output.add(0);
            } else if (input.charAt(i) == 'B') {
                output.add(1);
                output.add(0);
                output.add(1);
                output.add(1);
            } else if (input.charAt(i) == 'C') {
                output.add(1);
                output.add(1);
                output.add(0);
                output.add(0);
            } else if (input.charAt(i) == 'D') {
                output.add(1);
                output.add(1);
                output.add(0);
                output.add(1);
            } else if (input.charAt(i) == 'E') {
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(0);
            } else if (input.charAt(i) == 'F') {
                output.add(1);
                output.add(1);
                output.add(1);
                output.add(1);
            } else {
                //pass
            }
        }

        return output;
    }


    public static ArrayList<Integer> addIPtoBits(ArrayList<Integer> currentBits, String ip) {
        String[] ipParts = ip.split("\\.");
        for (int i = 0; i < ipParts.length; i++) {
            int num = Integer.parseInt(ipParts[i]);
            int[] num2 = turn10to2Octet(num);
            for (int j = 0; j < num2.length; j++) {
                currentBits.add(num2[j]);
            }
        }
        return currentBits;
    }

    public static ArrayList<Integer> addMACtoBits(ArrayList<Integer> currentBits, String mac) {
        String[] macParts = mac.split(":");
        for (int i = 0; i < macParts.length; i++) {
            for (int j = 0; j < macParts[i].length(); j++) {
                String num = macParts[i].substring(j, j + 1);
                ArrayList<Integer> num2 = turn16to2(num);
                for (int k = 0; k < num2.size(); k++) {
                    currentBits.add(num2.get(k));
                }
            }
        }
        return currentBits;
    }


    // делаем xor на 32-битном регистре
    public static void doXorRegister(ArrayList<Integer> register, ArrayList<Integer> poly) {
        for (int i = 0; i < poly.size(); i++) {
            if (poly.get(i) == 1) {
                int prevVal = register.get(i);
                register.set(i, 1 - prevVal);
            }
        }
    }

    public static ArrayList<Integer> countRest32(ArrayList<Integer> message, ArrayList<Integer> poly) {
        ArrayList<Integer> finalRes = new ArrayList<>();
        ArrayList<Integer> subPoly = new ArrayList<Integer>(poly.subList(1, poly.size()));
        for (int i = 0; i < subPoly.size(); i++) {
            finalRes.add(0);
        }

        for (int i = 0; i < message.size(); i++) {
            // проходим все сообщение и если старший бит = 1, то сдвигаем и ксорим регистр с полиномом
            int greaterBit = finalRes.get(0);
            finalRes.remove(0);
            finalRes.add(message.get(i));
            if (greaterBit == 1) {
                doXorRegister(finalRes, subPoly);
            }
        }
        return finalRes;
    }


    public static void refactorMessage(ArrayList<Integer> message){
        // чтобы перевести в тот вид, о каком говорится в условии
        for (int i = 0; i < 32; i++) {
            int curValue = message.get(i);
            message.set(i, 1 - curValue);
        }

        for (int i = 0; i < message.size()/8; i++) {
            int[] myPart = new int[8];
            for (int j = 0; j < 8; j++) {
                myPart[j] = message.get(i*8+j);
            }
            for (int j = 0; j < 8; j++) {
                message.set(i*8+j, myPart[8-1-j]);
            }
        }
    }

    public static void convertMess(ArrayList<Integer> message){
        // переворот сообщения
        for (int i = 0; i < message.size(); i++) {
            int cur = message.get(i);
            message.set(i, 1-cur);
        }
    }
}
