package theRProbDists;

public class Binom_Functions {

    public double pBinomial(int n, double p, int x) {

        double PX = 1.00;
        int i;

        for (i = 1; i <= n; i++) {
            PX = PX * (double)i;
            if (i <= x) {
                PX = PX / (double)i;
            }
            if (i <= (n - x)) {
                PX = PX / (double)i;
            }
        }

        for (i = 1; i <= x; i++) {
            PX = PX * p;
        }

        for (i = 1; i <= (n - x); i++) {
            PX = PX * (1.0 - p);
        }

        return PX;

    } // pBinomial

    public double sigBinomial(int n, double p, int x) {

        // n = number of scores in binomial distribution
        // the max n is 200 -- the procedure crashes after 350
        // p = probability of success on any one trial
        // x = score in binomial distribution
        // this procedure will always return the upper tail
        
        double tailP = 0.00;

        if (n > 200) {
            tailP = -1.00;
            return tailP;
        }

        int expectedP = (int) Math.round(n * p);

        if (x >= expectedP) {
            for (int i = x; i <= n; i++) {
                tailP = tailP + pBinomial(n, p, i);
            }
        } else {
            for (int i = x; i >= 0; i--) {
                tailP = tailP + pBinomial(n, p, i);
            }
        }

        return tailP;

    } // sigBinomial

    public int lowerBinomCI(int n, double p, double pTail) {

        double prob = 0.00, temp = 0.00;
        int i = 0;

        do {

            temp = pBinomial(n, p, i);
            prob = prob + temp;
            i++;
            
        } while (prob < pTail);

        return i-1;

    } // lowerBinomCI

    public int upperBinomCI(int n, double p, double pTail) {

        double prob = 0.00, temp = 0.00;
        int i = n;

        do {

            temp = pBinomial(n, p, i);
            prob = prob + temp;
            i--;

        } while (prob < pTail);

        return i+1;

    } // upperBinomCI

    public double trueBinomCI(int n, double p, double pTail) {

        double prob = 0.00, temp = 0.00;
        int lower = lowerBinomCI(n, p, pTail / 2.00);
        int upper = upperBinomCI(n, p, pTail / 2.00);

        for (int i = lower; i <= upper; i++) {

            temp = pBinomial(n, p, i);
            prob = prob + temp;

        }

        return prob;

    } // trueBinomCI

}
