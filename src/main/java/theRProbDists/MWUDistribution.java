package theRProbDists;

public class MWUDistribution {

    public double udist(int M, int N, int U, double P, int purpose) {

//     ALGORITHM AS 62  APPL. STATIST. (1973) VOL.22, NO.2
//
//     The distribution of the Mann-Whitney U-statistic is generated for
//     the two given sample sizes
//
//     The original Fortran code was translated and adapted to Java by
//     Victor Bissonnette, Department of Psychology, Berry College
//     Last updated: 1/28/2010
//
//     Input parameters:
//
//       M,N:     sample size of each group
//       U:       value of Mann-Whitney U statistic (if computing probability)
//                (pass a zero if computing critical value)
//       P:       tail probability value (if computing critical value of U)
//                (pass a zero if computing probability of U)
//       purpose: purpose of analysis -- when:
//                1: udist will return the tail probability of U
//                2: udist will return the critical value of U given p
        int IFAULT = 0;
        int MINMN, MN1, MAXMN, N1, I, IN, L, K, J;
        double ZERO = 0.0, ONE = 1.0, SUM;
        double temp = 0.0;

        MINMN = Math.min(M, N);

        int LFR = (M * N) + 1;
        int LWRK = (1 + MINMN + (M * N / 2));

        double[] FRQNCY = new double[LFR];
        double[] WORK = new double[LWRK];

        //      Check smaller sample size
        IFAULT = 1;
        if (MINMN < 1) {
            return temp;
        }

        //      Check size of results array
        IFAULT = 2;
        MN1 = M * N + 1;
        if (LFR < MN1) {
            return temp;
        }

        //     Set up results for 1st cycle and return if MINMN = 1
        MAXMN = Math.max(M, N);
        N1 = MAXMN + 1;

        for (I = 1; I <= N1; I++) {
            FRQNCY[I - 1] = ONE;
        }

        block1:
        {

            if (MINMN == 1) {
                break block1;
            }

            //      Check length of work array
            IFAULT = 3;

            if (LWRK < (((MN1 + 1) / 2) + MINMN)) {
                return temp;
            }

            //      Clear rest of FREQNCY
            N1 = N1 + 1;

            for (I = N1; I <= MN1; I++) {

                FRQNCY[I - 1] = ZERO;

            }

            //      Generate successively higher order distributions
            WORK[0] = ZERO;

            IN = MAXMN;

            for (I = 2; I <= MINMN; I++) {

                WORK[I - 1] = ZERO;
                IN = IN + MAXMN;
                N1 = IN + 2;
                L = 1 + (IN / 2);
                K = I;

                //        Generate complete distribution from outside inwards
                for (J = 1; J <= L; J++) {

                    K = K + 1;
                    N1 = N1 - 1;
                    SUM = FRQNCY[J - 1] + WORK[J - 1];
                    FRQNCY[J - 1] = SUM;
                    WORK[K - 1] = SUM - FRQNCY[N1 - 1];
                    FRQNCY[N1 - 1] = SUM;

                } // J loop

            } // I loop

        } // block1:

        //  Convert frequencies to probabilities
        SUM = ZERO;

        for (I = 1; I <= MN1; I++) {

            SUM = SUM + FRQNCY[I - 1];
            FRQNCY[I - 1] = SUM;

        }

        for (I = 1; I <= MN1; I++) {

            FRQNCY[I - 1] = FRQNCY[I - 1] / SUM;

        }

        IFAULT = 0;

        //  Find chance p of a particular U:
        if (purpose == 1) {

            temp = FRQNCY[U];

            return temp;

        }

        //  Find critical value of U given p
        if (purpose == 2) {

            for (I = 0; I <= (LFR - 1); I++) {

                if (FRQNCY[I] >= P) {

                    temp = I - 1;

                    return temp;
                }
            }

        }

        return temp;

    } // udist

}
