import java.text.DecimalFormat;
import java.util.ArrayList;

public class CatDONE {
    // numbers are from the task
    static int numPackets = 13;
    static int credits = 23;
    static double p = 0.0429349155342;
    static double okProb = 1 - p;
    static ArrayList<Integer> okPacks = new ArrayList<>();
    static ArrayList<Integer> packetsToSend;
    static int repetitivePackets = credits - numPackets;

    public static void main(String[] args) {
        double result = countProb(numPackets, repetitivePackets);
        System.out.println("Result probability: " + new DecimalFormat("#0.000000").format(result));
    }

    // here arguments are: number of packets we need to send and number of credits that are left after it
    static double countProb(int packetsToSend, int leftCredits) {
        double currentOkProb = 0;
        if (packetsToSend == 0) {
            // that's great, we don't need to send anything else
            currentOkProb = 1;
        } else if (leftCredits < 0) {
            //currentOkProb = 0; -- this is already initialized, bul I will keep it to save logic
            // there is no way to send all the rest packets, so we give 0
        } else {
            // we also take into account message from another side (that is why *p)
            // and we count the prob that we will send all the left packets with our credits
            double p1 = countProb(packetsToSend, leftCredits - packetsToSend);
            p1 *= p;

            // probability that we will send all the rest packets successfully
            double p2 = 0;
            for (int i = 0; i < packetsToSend; i++) {
                // probability that we'll send directly i packets out of packetsToSend
                double i_prob_math = oneProb(i, packetsToSend);

                // the probability upper is a mathematical one but this line occurs because of the task:
                // we recurrently find the probability that i packets will be delivered
                double i_prob_net = countProb(packetsToSend - i, leftCredits - packetsToSend + i);

                //find the product of these probabilities as they must happen simultaneously
                p2 +=  i_prob_math * i_prob_net;
            }

            /*
            probability that:
            - all packets are sent (1st line)
            - we get the answer (2nd line)
             */
            double p2_full = (Math.pow(okProb, packetsToSend) + p2);
            p2_full *= okProb;

            // we sum the probabilities
            currentOkProb = p1 + p2_full;
        }

        return currentOkProb;
    }

    // counts C_n^k coeff
    public static long binomial(int n, int k) {
        long res = 1;
        for (int i = n - k + 1; i <= n; i++) {
            res *= i;
        }
        for (int i = 1; i <= k; i++) {
            res /= i;
        }
        return res;
        //return factorial(n)/(factorial(k)*factorial(n-k));
    }

    // simple probability that the event with probability p happens exactly k times out of n
    public static double oneProb(int k, int n) {
        double res = binomial(n, k) * Math.pow(p, n - k) * Math.pow(1 - p, k);
        return res;
    }

    public static long factorial(int n) {
        if (n == 0) return 1;
        else return n * factorial(n - 1);
    }
}

