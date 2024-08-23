/**************************************************
 *             GeometricDistribution              *
 *                  04/22/20                      *
 *                    00:00                       *
 *************************************************/

package probabilityDistributions;

public class GeometricDistribution 
{
    double pSuccess;
    double logP, log_1_P;
    
    static RVUtilities rvUtilities;
    
    public GeometricDistribution()
    {
        //System.out.println("Constructing Geometric Distribution A");
        pSuccess = 0.0;
    }
    
    public GeometricDistribution(double probSuccess)
    {
        //System.out.println("Constructing Geometric Distribution B, probSuccess = " + probSuccess);
        logP = Math.log(pSuccess);
        log_1_P = Math.log(1.0 - pSuccess);
    }
    
    public void setPSuccess(double p)
    {
        pSuccess = p;
        logP = Math.log(pSuccess);
        log_1_P = Math.log(1.0 - pSuccess);
    }
    
    /*********************************************************************
     *                                                                   *
     *  This definition of the geometric distribution is that P(X=x) is  *
     *  the probability of the first success occurring on trial x.       *
     *                                                                   *
     ********************************************************************/
    
    public double getPDF(int x)
    {
        //System.out.println("37 geomdistr, pSuccess = " + pSuccess);
        double pdf = Math.exp(logP + (x - 1) * log_1_P);
        //System.out.println("37 Geometric Distribution, x / pdf = " + x + " / " + pdf);
        return pdf;
    }
    
    public double getCDF(int x)
    {
        double cdf = 1.0 - Math.pow((1.0 - pSuccess), x);
        //System.out.println("44 Geometric Distribution, x / cdf = " + x + " / " + cdf);
	return cdf;
    }
 
    public int getPercentile(double p) {
        for (int ithBin = 0; ithBin <= 500; ithBin++) {
            if (getCDF(ithBin) > p) {
                return ithBin;
            } 
        }
        return 500; //  Should never get here!!
    }
    public int generateRandom()
    {
        //  Law, p465
        int randy;
        double logNum = Math.log(rvUtilities.getUniformZeroOne());
        double logDen = log_1_P;
        randy = (int) Math.floor(logNum / logDen);
        return randy;
    }
}
