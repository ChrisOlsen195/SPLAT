package theRProbDists;

public class R {
    
    T_double_df tDist;
    
    public R (double df) {
        tDist = new T_double_df(df);
    }

    public double getTwoTails(double df, double r) {
        double t = Math.abs(r * Math.sqrt(df) / Math.sqrt(1.0 - Math.pow(r, 2.0)));
        return (1.0 - tDist.cumulative(t)) * 2.0;
    }

    public double getOneTail(double df, double r) {
        double t = Math.abs(r * Math.sqrt(df) / Math.sqrt(1.0 - Math.pow(r, 2.0)));
        return 1.0 - tDist.cumulative(t);
    }

    public double getRightArea(double df, double r) {
        double t = r * Math.sqrt(df) / Math.sqrt(1.0 - Math.pow(r, 2.0));
        return 1.0 - tDist.cumulative(t);
    }

    public double getLeftArea(double df, double r) {
        double t = r * Math.sqrt(df) / Math.sqrt(1.0 - Math.pow(r, 2.0));
        return tDist.cumulative(t);
    }

    public double getInvTwoTails(double df, double p) {
        double t = tDist.quantile(1.0 - (p / 2.0));
        return Math.sqrt(Math.pow(t, 2.0) / (Math.pow(t, 2.0) + df));
    }

    public double getInvOneTail(double df, double p) {
        double t = tDist.quantile(1.0 - p);
        return Math.sqrt(Math.pow(t, 2.0) / (Math.pow(t, 2.0) + df));
    }

    public double getInvRightArea(double df, double p) {
        double t = tDist.quantile(1.0 - p);
        return Math.sqrt(Math.pow(t, 2.0) / (Math.pow(t, 2.0) + df));
    }

    public double getInvLeftArea(double df, double p) {
        return getInvRightArea(df, p) * -1.0;
    }

    public double getPower(double n, double R, double alpha, boolean twoTails) {

        double power = 0.0, tObt, tCrit, df = n - 2;

        tObt = R * Math.sqrt(df) / Math.sqrt(1.0 - Math.pow(R, 2.0));
        
        if (twoTails) {
            tCrit = tDist.quantile(1.0 - (alpha / 2.0));
        } else {
            tCrit = tDist.quantile(1.0 - alpha);
        }

        double diff = tCrit - tObt;
        power = 1.0 - tDist.cumulative(diff);

        if (twoTails) {
            diff = (tCrit * -1.0) - tObt;
            power = power + tDist.cumulative(diff);
        }

        return power;
        
    }

} //RDistribution
