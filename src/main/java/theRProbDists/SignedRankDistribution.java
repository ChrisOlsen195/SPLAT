package theRProbDists;

public class SignedRankDistribution {

    private long numsr(int n, int k) {

        long temp = 0;

        if (k < 0) {
            temp = 0;
        } else if ((k == 0) && (n == 0)) {
            temp = 1;
        } else if ((k != 0) && (n == 0)) {
            temp = 0;
        } else {
            temp = numsr(n - 1, k) + numsr(n - 1, k - n);
        }

        return temp;

    }

    /* numsr */


    public double sigsr(int sr, int n) {

        // This procedure will return the chance probability of the signed-
        // rank test.  Be careful -- as n increases, the time it takes to
        // run this procedure goes up dramatically.  
        // Written by Victor Bissonnette, Berry College
        double temp = 0.0, p = 0.0;
        int i, j;

        i = 0;

        while (i <= sr) {
            p = numsr(n, i);
            p = p * 2;
            for (j = 1; j <= n; j++) {
                p = p / 2;
            }
            temp = temp + p;
            i = i + 1;
        }

        return temp;

    }

}
