/******************************************************************************
 *                                                                            *
 *                    ChiSquareDistribution                                   *
 *                          11/26/22                                          *
 *                            12:00                                           *
 * ***************************************************************************/

package probabilityDistributions;

import theRProbDists.*;

public class ChiSquareDistribution 
{
    int df; 
    double dbl_df;

    double[] middleInterval;
    //RVUtilities rvUtilities;
    //GammaDistribution gammaDist;
    //StandardNormal aNormal;
    
    ChiSquare chi2Distr;
    
    public ChiSquareDistribution(int df) {
        this.df = df;
        dbl_df = df;
        // The Chi square distribution with v df is a gamma (v/2, 1/2)
        //gammaDist = new GammaDistribution((double)df / 2., 0.5);
        chi2Distr = new ChiSquare(dbl_df);
    }

    
    public ChiSquareDistribution(double double_df) {
        dbl_df = double_df;
        // The Chi square distribution with v df is a gamma (v/2, 1/2)
        //gammaDist = new GammaDistribution(double_df / 2., 0.5);
        chi2Distr = new ChiSquare(dbl_df);
    }
    
    public double getLeftTailArea(double x2Value)
    {
        //if(x2Value == 0.0) {
        //    return 0.0;  
        //}
        //return gammaDist.getLeftTailArea(x2Value);
        return chi2Distr.cumulative(x2Value);    
    }
    
    public double getInvLeftTailArea(double p) {
        //return gammaDist.getInvLeftTailArea(p);
        return chi2Distr.quantile(p);
    }

    public double getRightTailArea( double x2Value)  {
        //if(x2Value == 0.0) { return 1.0;  }
        //return gammaDist.getRightTailArea(x2Value);
        return 1.0 - getLeftTailArea(x2Value);
    }
    
    public double getInvRightTailArea(double p) {
        //double iRTA = gammaDist.getInvLeftTailArea(1 - p);
        //return iRTA;
        return getInvLeftTailArea(1 - p);
    }
     
    public double[] getInverseMiddleArea(double middleArea)  {
        middleInterval = new double[2];
        double leftArea = (1.0 - middleArea) / 2.0;
        double rightArea = 1.0 - leftArea;
        //middleInterval[0] = invcdf(leftArea);
        //middleInterval[1] = invcdf(rightArea);
        middleInterval[0] = chi2Distr.quantile(leftArea);
        middleInterval[1] = chi2Distr.quantile(rightArea);
        return middleInterval;
    }
    
    public double getDensity(double x2Value) {
        return chi2Distr.density(x2Value, false);
    }

    public int getDegreesOfFreedom() { return df; }
    
    public void set_df_for_X2(int toThisDF) {
        df = toThisDF;
        dbl_df = df;
        //gammaDist = new GammaDistribution(dbl_df / 2., 0.5);
        chi2Distr = new ChiSquare(dbl_df);
    }
    
    /*
   public double generateRandom()
    {
        aNormal = new StandardNormal();
        double randChiSquare = 0.0;
        for (int i = 1; i<= df; i++)
        {
            double aUnitNormal = aNormal.generateRandom();
            randChiSquare += aUnitNormal * aUnitNormal;
        }
        return randChiSquare;
    } 
    */
}