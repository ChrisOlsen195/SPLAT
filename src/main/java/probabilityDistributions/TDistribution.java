/******************************************************************************
 *                       t Distribution                                       *
 *                          12/17/22                                          *
 *                            00:00                                           *
 * ***************************************************************************/
/******************************************************************************
 *  TDistribution is a wrapper for theRProbDists. It is legacy code that      *
 *  originally performed t functions until I found theRProbDists. The primary *
 *  use is to allow t-distribution calls for natural numbers of dfs that      *
 *  permeate the pre-discovery-written code.                                  *
 * ***************************************************************************/
package probabilityDistributions;

import theRProbDists.*;

public class TDistribution {

    double double_df;
    double[] middleInterval;
  
    RVUtilities rvUtil;

    T_double_df t_double_df;

    public TDistribution(int df) { 
        double_df = df; 
        t_double_df = new T_double_df(double_df);
    }
    
    public TDistribution(double double_df) { 
        t_double_df = new T_double_df(double_df);
    }
    
    public double getLeftTailArea(double t_Value) {
        return t_double_df.cumulative(t_Value);
    }
    
    public double getInvLeftTailArea(double p) {
        return t_double_df.quantile(p);
     }

     public double getRightTailArea(double t_Value) {
         double rta = 1.0 - getLeftTailArea(t_Value);
         return rta;
     }

    public double getInvRightTailArea(double p) {
        return getInvLeftTailArea(1. - p);
    }
    
    public double getMiddleArea(double lowT, double highT) {
        double middleArea = getLeftTailArea(highT) - getLeftTailArea(lowT);
        return middleArea;
    }
    
    public double generateRandom() {
        double randy = rvUtil.getUniformZeroOne();
        double preT = getInvLeftTailArea(randy);
        if (Math.abs(preT) > 10.0) {
            preT = 0.0;
        }
        return preT;  
    }

    public double getDensity(double t_Value) {
        return t_double_df.density(t_Value, false);
    }
    
    public double[] getInverseMiddleArea(double middleArea) {
        middleInterval = new double[2];
        double critical_t = t_double_df.quantile(0.5 + middleArea / 2.0);
        middleInterval[1] = critical_t;
        middleInterval[0] = -critical_t;
        return middleInterval;
    }
    
    public double getCriticalT(double alpha) {
        return t_double_df.quantile(1 - alpha);
    }
}