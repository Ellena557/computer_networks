import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Hamming1refactored {
    private static int octLen = 14;

    // Число контрольных бит: log_2(19), получаем 5
    // они стоят на местах 1, 2, 4, 8, 16 (считаем начиная с 1)
    // но в коде нумерация с нуля, так что это будут 0, 1, 3, 7 и 15
    private static int cBits = 5;

    // вся длина = 14+5
    private static int allLen = 19;
    private static boolean needToDelete;


    public static void main(String[] args) throws UnsupportedEncodingException {
        needToDelete = true;
        int[] message = {149, 99, 108, 27, 76, 201, 22, 161, 23, 118, 136, 209, 138, 226, 14, 228, 25, 50, 214, 88, 16,
                120, 134, 28, 198, 48, 199, 11, 198, 151, 198, 170, 206, 141, 192, 75, 240, 143};

        int[][] helpseq = turn10toOktetSequence(message);

        String decoded = "";
        for (int i = 0; i < helpseq.length; i++) {
            decoded += decodeOneSymb(helpseq[i]);
        }

        String result = allResult(decoded);

        System.out.println("result message: " + result);

        // ответ на пример в строчке выше
        String answer = "3341087058225";
        ArrayList<Integer> ansMessage = encode(answer);
        System.out.println("Our message: " + ansMessage);
    }

    public static ArrayList<Integer> encode(String s) throws UnsupportedEncodingException {
        ArrayList<Integer> bytes = new ArrayList<>();

        // Превращаем входную десятичную строку в двоичную последовательность октетов
        for (int i = 0; i < s.length(); i++) {
            byte[] numUTF = s.substring(i, i + 1).getBytes("UTF-8");
            int[] numIn2Sys = turn10to2(numUTF[0]);
            for (int j = 0; j < 8 - numIn2Sys.length; j++) {
                bytes.add(0);
            }
            for (int j = 0; j < numIn2Sys.length; j++) {
                bytes.add(numIn2Sys[j]);
            }
        }

        // Смотрим, чтобы уложилось целое число частей длины octLen
        int r = bytes.size() % octLen;
        if (r > 0) {
            for (int i = 0; i < octLen - r; i++) {
                bytes.add(0);
            }
        }

        // Делим на эти части
        int[][] msgParts = new int[bytes.size() / octLen][octLen];
        for (int i = 0; i < bytes.size() / octLen; i++) {
            for (int j = 0; j < octLen; j++) {
                msgParts[i][j] = bytes.get(i * octLen + j);
            }
        }

        // Добавляем в запись контрольные биты
        int[][] encodedMsg = new int[msgParts.length][allLen];
        for (int i = 0; i < msgParts.length; i++) {
            encodedMsg[i] = encodePart(msgParts[i]);
        }

        // Переворачиваем последовательности по allLen бит в каждой
        int[][] outReversed = new int[encodedMsg.length][allLen];
        for (int i = 0; i < encodedMsg.length; i++) {
            for (int j = 0; j < allLen; j++) {
                outReversed[i][j] = encodedMsg[i][allLen - 1 - j];
            }
        }

        // конкатенируем
        ArrayList<Integer> allAnswerEncoding = new ArrayList<>();
        for (int i = 0; i < outReversed.length; i++) {
            for (int j = 0; j < allLen; j++) {
                allAnswerEncoding.add(outReversed[i][j]);
            }
        }

        // проверяем, чтобы делилось на 8 (чтобы разбить на октеты)
        int rest = allAnswerEncoding.size() % 8;
        if (rest > 0) {
            for (int i = 0; i < rest; i++) {
                allAnswerEncoding.add(0);
            }
        }

        ArrayList<Integer> answerMessage = new ArrayList<>();

        // получаем выходной результат из десятичного вида октетов
        for (int i = 0; i < allAnswerEncoding.size() / 8; i++) {
            int[] part = new int[8];
            for (int j = 0; j < 8; j++) {
                part[j] = allAnswerEncoding.get(i * 8 + j);
            }
            int numIn10Sys = turn2to10(part);
            answerMessage.add(numIn10Sys);
        }
        return answerMessage;
    }

    // в этой функции мы добавляем контрольные биты в одну последовательность длины octLen
    public static int[] encodePart(int[] part) {

        // матрица преобразования
        int[][] matrix1 = new int[cBits][allLen];
        for (int i = 0; i < allLen; i++) {
            int[] numIn2Sys = turn10to2(i + 1);
            if (numIn2Sys.length < cBits) {
                int[] myRes = new int[cBits];
                for (int j = 0; j < cBits - numIn2Sys.length; j++) {
                    myRes[j] = 0;
                }
                for (int j = 0; j < numIn2Sys.length; j++) {
                    myRes[j + cBits - numIn2Sys.length] = numIn2Sys[j];
                }
                numIn2Sys = myRes;
            }
            for (int j = 0; j < cBits; j++) {
                matrix1[cBits - j - 1][i] = numIn2Sys[j];
            }
        }

        // позиции контольных битов
        ArrayList<Integer> countPositions = new ArrayList<>();
        for (int i = 0; i < cBits; i++) {
            countPositions.add((int) Math.pow(2, i) - 1);
        }

        // добавляем пока что нулевые контрольные биты в итоговый результат
        int[] encoded = new int[allLen];
        int counter = 0;
        for (int i = 0; i < allLen; i++) {
            if (countPositions.contains(i)) {
                encoded[i] = 0;

            } else {
                encoded[i] = part[counter];
                counter++;
            }
        }

        // значения контрольных битов
        int[] contByteValues = new int[cBits];

        for (int i = 0; i < cBits; i++) {
            contByteValues[i] = 0;
            for (int j = 0; j < allLen; j++) {
                contByteValues[i] += matrix1[i][j] * encoded[j];
            }
            contByteValues[i] = contByteValues[i] % 2;
        }

        // добавляем реальные контрольные биты в итоговый результат
        for (int i = 0; i < allLen; i++) {
            if (countPositions.contains(i)) {
                encoded[i] = contByteValues[countPositions.indexOf(i)];
            }
        }

        return encoded;
    }


    // перевод из двоичной системы (на вход подается массив бит) в десятичную
    public static int turn2to10(int[] num) {
        int res = 0;
        for (int i = 0; i < num.length; i++) {
            res += num[num.length - i - 1] * Math.pow(2, i);
        }
        return res;
    }

    // перевод из десятичной системы в двоичную
    public static int[] turn10to2(int num) {
        ArrayList<Integer> myRes = new ArrayList<>();
        if (num == 0) {
            int[] r = {0};
            return r;
        } else {
            while (num > 0) {

                int rest = num % 2;
                myRes.add(0, rest);
                num = (int) num / 2;
            }
            int[] res = new int[myRes.size()];
            for (int i = 0; i < myRes.size(); i++) {
                res[i] = myRes.get(i);
            }
            return res;
        }
    }


    public static int[][] turn10toOktetSequence(int[] input) {
        ArrayList<Integer> seq = new ArrayList<>();
        // перевод из десятичной системы в двоичную в форме октета
        // таким образом, например, число 2 будет выглядеть не как "10", а как "00000010"
        for (int i = 0; i < input.length; i++) {
            int[] in2 = turn10to2(input[i]);
            if (in2.length < 8) {
                for (int j = 0; j < 8 - in2.length; j++) {
                    seq.add(0);
                }
            }
            for (int j = 0; j < in2.length; j++) {
                seq.add(in2[j]);
            }
        }

        // добавляем нули, чтобы разбить на целое число частей длины allLen
        if (seq.size() % allLen > 0) {
            for (int i = 0; i < allLen - (seq.size() % allLen); i++) {
                seq.add(0);
            }
        }

        int[][] out = new int[(int) seq.size() / allLen][allLen];
        for (int i = 0; i < (int) seq.size() / allLen; i++) {
            for (int j = 0; j < allLen; j++) {
                out[i][j] = seq.get(allLen * i + j);
            }
        }

        // переворачиваем полученные 35-битные части
        int[][] outReversed = new int[(int) seq.size() / allLen][allLen];
        for (int i = 0; i < (int) seq.size() / allLen; i++) {
            for (int j = 0; j < allLen; j++) {
                outReversed[i][j] = out[i][allLen - j - 1];
            }
        }

        return outReversed;
    }

    public static String decodeOneSymb(int[] in) {
        ArrayList<Integer> controlBits = new ArrayList<>();
        for (int i = 0; i < cBits; i++) {
            controlBits.add(in[(int) Math.pow(2, i) - 1]);
        }

        ArrayList<Integer> controlBitsPositions = new ArrayList<>();
        for (int i = 0; i < cBits; i++) {
            controlBitsPositions.add((int) Math.pow(2, i) - 1);
        }

        // строим матрицу преобразования
        int[][] matrix = new int[cBits][allLen];
        for (int i = 0; i < allLen; i++) {
            int[] numIn2Sys = turn10to2(i + 1);
            if (numIn2Sys.length < cBits) {
                int[] myRes = new int[cBits];
                for (int j = 0; j < cBits - numIn2Sys.length; j++) {
                    myRes[j] = 0;
                }
                for (int j = 0; j < numIn2Sys.length; j++) {
                    myRes[j + cBits - numIn2Sys.length] = numIn2Sys[j];
                }
                numIn2Sys = myRes;
            }
            for (int j = 0; j < cBits; j++) {
                matrix[cBits - j - 1][i] = numIn2Sys[j];
            }
        }

        int[] r = new int[cBits];
        for (int i = 0; i < cBits; i++) {
            r[i] = 0;
            for (int j = 0; j < allLen; j++) {
                r[i] += matrix[i][j] * in[j];
            }
            r[i] = r[i] % 2;
        }

        // контрольная сумма
        int contSum = 0;
        for (int i = 0; i < cBits; i++) {
            contSum += r[i];
        }

        if (contSum > 0) {
            // есть ошибка, ищем ее (исправляем ошибочный бит)
            in = findMistake(in);
        }
        String res = "";
        for (int i = 0; i < in.length; i++) {
            if (controlBitsPositions.contains(i)) {
                // pass
            } else {
                res += in[i] + "";
            }
        }

        return res;
    }


    // поиск ошибки в полученной последовательности
    public static int[] findMistake(int[] in) {
        // строим матрицу преобразования
        int[][] matrix = new int[cBits][allLen];
        for (int i = 0; i < allLen; i++) {
            int[] numIn2Sys = turn10to2(i + 1);
            if (numIn2Sys.length < cBits) {
                int[] myRes = new int[cBits];
                for (int j = 0; j < cBits - numIn2Sys.length; j++) {
                    myRes[j] = 0;
                }
                for (int j = 0; j < numIn2Sys.length; j++) {
                    myRes[j + cBits - numIn2Sys.length] = numIn2Sys[j];
                }
                numIn2Sys = myRes;
            }
            for (int j = 0; j < cBits; j++) {
                matrix[cBits - j - 1][i] = numIn2Sys[j];
            }
        }

        ArrayList<Integer> myControlBits = new ArrayList<>();
        for (int i = 0; i < cBits; i++) {
            myControlBits.add((int) (Math.pow(2, i) - 1));
        }

        ArrayList<Integer> controlValsMustBe = new ArrayList<>();
        for (int i = 0; i < cBits; i++) {
            int curSum = 0;
            for (int j = 0; j < allLen; j++) {
                //if (!myControlBits.contains(j))
                curSum += matrix[i][j] * in[j];
            }
            curSum = curSum % 2;
            controlValsMustBe.add(curSum);
        }

        int wrongBit = 0;

        for (int i = 0; i < cBits; i++) {
            wrongBit += Math.pow(2, i) * controlValsMustBe.get(i);
        }

        in[wrongBit - 1] = 1 - in[wrongBit - 1];
        return in;
    }

    public static String allResult(String msg) throws UnsupportedEncodingException {
        String res = "";
        if (msg.length() % 8 > 0) {
            for (int i = 0; i < 8 - (msg.length() % 8); i++) {
                msg += "0";
            }
        }

        // убираем нулевые октеты
        while (needToDelete) {
            int j = (int) msg.length() / 8 - 1;
            String part = msg.substring(j * 8, (j + 1) * 8);
            for (int k = 0; k < 8; k++) {
                if (part.charAt(k) != '0') {
                    needToDelete = false;
                }
            }
            if (needToDelete) {
                msg = msg.substring(0, msg.length() - 8);
            }
        }

        for (int i = 0; i < (int) msg.length() / 8; i++) {
            String curSymb = msg.substring(i * 8, (i + 1) * 8);
            int[] bytes = new int[8];
            for (int j = 0; j < 8; j++) {
                if (curSymb.charAt(j) == '0') {
                    bytes[j] = 0;
                } else bytes[j] = 1;
            }
            int posUTF = turn2to10(bytes);
            byte[] b = {(byte) posUTF};
            String symbol = new String(b, StandardCharsets.UTF_8);
            res += symbol;
        }

        return res;
    }
}
