/**************************************************
 *              BinomialDistribution              *
 *                  03/02/2020                    *
 *                    15:00                       *
 *************************************************/
package probabilityDistributions;

public class BinomialDistribution 
{
    int nTrials;
    double pSuccess;
    RVUtilities rvUtilities;
    
    public BinomialDistribution()
    {
        nTrials = 0; pSuccess = 0.0;
        rvUtilities = new RVUtilities();
    }
    
    public BinomialDistribution(int n, double p)
    {
        //System.out.println("22 binom, initializing: n/p = " + n + " / " + p);
        nTrials = n; pSuccess = p;
        rvUtilities = new RVUtilities();
    }
    
    public double getPDF(int x)
    {   
        //System.out.println("22 binom, getPDF(int x): n/p = " + nTrials + " / " + pSuccess);
        if ((x < 0) || (x > nTrials)) { return 0.0;}
        
        double log_nCr = RVUtilities.log_nCx(nTrials, x);
        double log_TheRest =  x * Math.log(pSuccess)
                                + (nTrials - x) * Math.log(1.0 - pSuccess);
        return Math.exp(log_nCr + log_TheRest);
    }
    
    public double getCDF(int x)
    {
        if ((x < 0) || (x > nTrials)) { return 0.0;}
        
        double cdf = 0.0;
	for (int i = 0; i <= x; i++)
		cdf += getPDF(i);
	return cdf;
    }
    
    public int getPercentile(double p) {
        for (int ithBin = 0; ithBin <= nTrials; ithBin++) {
            if (getCDF(ithBin) > p) {
                return ithBin;
            } 
        }
        return nTrials; //  Should never get here
    }
    
    public double getAreaFrom_X_To_Y(int a, int b) {
        if (b < a) {
            System.out.println("Ack!!!  Bad values into binomialDist.getAreaFrom_X_To_Y");
            System.exit(51);
        }
        double areaFromXToY =  getCDF(b) - getCDF(a - 1);
        return areaFromXToY;
    }
    
    public double generateRandom()
    {
        int randomBinomial = 0;
	for (int i = 1; i <= nTrials; i++)
        {
            double randy = rvUtilities.getUniformZeroOne();
	    if (randy <= pSuccess)
			randomBinomial++;
        }
	return randomBinomial;

    }
    
    public void setBinomial_n_and_p(int n, double p) {
        nTrials = n; pSuccess = p;
    }
   
    
}
