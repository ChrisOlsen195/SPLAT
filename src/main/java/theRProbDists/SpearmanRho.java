package theRProbDists;

public class SpearmanRho {

    public double prho(int n, int is) {

//   Algorithm AS 89   Appl. Statist. (1975) Vol.24, No. 3, P377.
//   To evaluate the probability of obtaining a value greater than or
//   equal to is, where is=(n**3-n)*(1-r)/6, r=Spearman's rho and n
//   must be greater than 1
//   Auxiliary function required: ALNORM = algorithm AS66
//   Code coverted to Java by Victor Bissonnette, Berry College        
    
        double b, x, y, z, u;
        double c1 = 0.2274, c2 = 0.2531, c3 = 0.1745,
                c4 = 0.0758, c5 = 0.1033, c6 = 0.3932,
                c7 = 0.0879, c8 = 0.0151, c9 = 0.0072,
                c10 = 0.0831, c11 = 0.0131, c12 = 0.00046;

        double temp = 1.0;
        final int maxN = 12;

//        Test admissibility of arguments and initialize
        if (n <= 1) {
            return temp;
        }
        if (is <= 0.0) {
            return temp;
        }
        temp = 0.0;

        int js = is;
        if (js > n * (n * n - 1) / 3) {
            return temp;
        }

        if (js != (2 * (js / 2))) { // only allow even numbers for js
            js = js + 1;
        }
        boolean exact = true;
        if (n > maxN) {
            exact = false;
        }

//        Exact evaluation of probability
        if (exact) {

            int nfac = 1;
            int i;
            int[] l = new int[maxN + 1];

            for (i = 1; i <= n; i++) {
                nfac = nfac * i;
                l[i] = i;
            }

            temp = 1.0 / nfac;

            if (js == (n * (n * n - 1) / 3)) {
                return temp;
            }

            int ifr = 0;

            for (int m = 1; m <= nfac; m++) {
                mloop:
                {

                    int ise = 0;

                    for (i = 1; i <= n; i++) {

                        ise = ise + (int) Math.pow((i - l[i]), 2.0);
                    }

                    if (js < ise) {
                        ifr = ifr + 1;
                    }

                    int n1 = n;

                    do {

                        int mt = l[1];
                        int nn = n1 - 1;

                        for (i = 1; i <= nn; i++) {
                            l[i] = l[i + 1];
                        }

                        l[n1] = mt;

                        if (l[n1] != n1) {
                            break mloop;
                        }
                        if (n1 == 2) {
                            break mloop;
                        }

                        n1 = n1 - 1;
                    } while (m != nfac);
                } // mloop block
            } // m loop

            temp = Double.valueOf(ifr) / Double.valueOf(nfac);

        } else {   // exact test

//        Evaluation by Edgeworth series expansion
            b = 1.0 / Double.valueOf(n);

            x = (6.0 * (Double.valueOf(js) - 1.0) * b / (1.0 / (b * b) - 1.0)
                    - 1.0) * Math.sqrt(1.0 / b - 1.0);

            y = x * x;

            u = x * b * (c1 + b * (c2 + c3 * b) + y * (-c4
                    + b * (c5 + c6 * b) - y * b * (c7 + c8 * b
                    - y * (c9 - c10 * b + y * b * (c11 - c12 * y)))));

            //  Call to algorithm AS 66
            Normal norm = new Normal();
            temp = u / Math.exp(y / 2.0) + norm.getRightArea(x);

            if (temp < 0.0) {
                temp = 0.0;
            }
            if (temp > 1.0) {
                temp = 1.0;
            }

        } // aproximate p

        return temp;

    } // prho

    public int CritIS(int n, double p) {

        int is = -0;

        double check = 0.0;
        int increment = 10;
        int i = 0;

        while (check < p) {
            is = is + increment;
            check = 1.0 - prho(n, is);
        }
        is = is - increment;
        check = 0.0;
        increment = 2;

        while (check < p) {
            is = is + increment;
            check = 1.0 - prho(n, is);
        }
        is = is - increment;

        return is;

    } // critrho

}
