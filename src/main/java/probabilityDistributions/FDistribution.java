/**************************************************
 *                F-Distribution                  *
 *                  11/25/22                      *
 *                    21:00                       *
 *************************************************/
/******************************************************************************
 *  FDistribution is a wrapper for theRProbDists. It is legacy code that      *
 *  originally performed t functions until I found theRProbDists. The primary *
 *  use is to allow t-distribution calls for natural numbers of dfs that      *
 *  permeate the pre-discovery-written code.                                  *
 * ***************************************************************************/
package probabilityDistributions;

import java.util.Random; 
import theRProbDists.*;

public class FDistribution 
{
    static Random randomNumberGenerator;
    int dfNumerator, dfDenominator;
    boolean closeEnough;
    final double tolerance = 0.0000000000001;
    double lowEnd, highEnd;
    double dbl_df_1, dbl_df_2;
    double iBParam1, iBParam2, iBParam3;
    double[] middleInterval;
    
    F fDistr;

    static RVUtilities rvUtil;
    
    public FDistribution(int dfNumerator, int dfDenominator) {
        this.dfNumerator = dfNumerator;
        this.dfDenominator = dfDenominator;
        dbl_df_1 = dfNumerator;
        dbl_df_2 = dfDenominator;
        fDistr = new F(dbl_df_1, dbl_df_2);
    }

    public double getLeftTailArea(double fValue) {
        return fDistr.getLeftArea(dbl_df_1, dbl_df_2, fValue);
    }

    public double getRightTailArea(double fValue) {
        return fDistr.getRightArea(dbl_df_1, dbl_df_2, fValue);
    }
    
    public double getInvLeftTailArea(double p)  {
        return fDistr.getInvLeftArea(dbl_df_1, dbl_df_2, p);
    }
    
    public double getInvRightTailArea(double p) {
        return fDistr.getInvRightArea(dbl_df_1, dbl_df_2, p);
    }
    
    public double[] getInverseMiddleArea(double middleArea)  {
        middleInterval = new double[2];
        double leftArea = (1.0 - middleArea) / 2.0;
        double rightArea = 1.0 - leftArea;
        middleInterval[0] = getInvLeftTailArea(leftArea);
        middleInterval[1] = getInvLeftTailArea(rightArea);
        return middleInterval;
    }
    
    public double generateRandom()
    {
        double randy = rvUtil.getUniformZeroOne();
        double preF = getInvLeftTailArea(randy);
        //if (Math.abs(preT) > 10.0)
        //    preT = 0.0;
        return preF;  
    }

    public double getDensity(double fValue) {
        return fDistr.density(fValue, false);
    }
    
    public int getDFNumerator() { return dfNumerator; }
    public int getDFDenominator() { return dfDenominator; }    
}
