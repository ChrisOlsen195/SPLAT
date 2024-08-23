package theRProbDists;

/**************************************************
 *        SamplingDistPearsonsR_Power             *
 *                  02/02/17                      *
 *                    21:00                       *
 *************************************************/
public class SampDistR {
    
    //int n;
    int sampleSize;
    
    double rho, double_SampleSize, incrementBy, maxFofR ;
    
    public SampDistR (int sampleSize, double rho) {
        this.sampleSize = sampleSize;
        double_SampleSize = sampleSize;
        this.rho = rho;
        incrementBy = .001;
    }
    
    public double getDensityAt_rEquals ( double r) {
        // System.out.println("sd 22, rho = " + rho);
        if (rho == 0) {
            return densityOfr_ZeroRho(r);
        }
        else {
            // return densityOfrNonZeroRho (r);
            return altDensityOfrNonZeroRho (r);
        }
    }
    
    public double calculateAreaUnderSampDist( double fromHere, double toThere, double rho) {
        double area = 0.0; 
        double temp_r = fromHere;
        
        do {
            temp_r += incrementBy;
            area += incrementBy * getDensityAt_rEquals (temp_r);
        } while (temp_r  < toThere);  
        
        return area;
    }
    
    public double leftTailArea( double upToHere) {
        double area = 0.0; 
        double temp_r = upToHere;

        do {
            temp_r += incrementBy;
            area += incrementBy * getDensityAt_rEquals (temp_r);
        } while (temp_r  < upToHere);  
        
        return area;
    }    
    
    public double rightTailArea( double downToHere) {
        double area = 0.0; 
        double temp_r = downToHere;

        do {
            temp_r += incrementBy;
            area += incrementBy * getDensityAt_rEquals (temp_r);
        } while (temp_r  < downToHere);  
        
        return area;
    } 
    
    public double areaBetween( double thisLo, double thisHi) {
        double area = 0.0; 
        double temp_r = thisLo;

        do {
            temp_r += incrementBy;
            area += incrementBy * getDensityAt_rEquals (temp_r);
        } while (temp_r  < thisHi);  
        
        return area;
    } 
    
    public double rForQuantile( double daQuantile) { 
        double cdf = 0.0;
        double temp_r = -1.0;
        do {
            temp_r += incrementBy;
            // System.out.println("sd 84, temp_r / getDensityAt_r (temp_r) = " 
            //                    + temp_r + " / " + getDensityAt_r (temp_r));
            cdf += incrementBy * getDensityAt_rEquals (temp_r);
            // System.out.println("sd 87, cdf = " + cdf);
        } while (cdf  < daQuantile);  
        
        return temp_r;
    }
    
    public double densityOfr_ZeroRho (double r) {
        double f_of_r = 0.0;
        setRho(0.0);
        double f_of_r_Numerator = Math.pow(1 - r * r, 0.5 * (double_SampleSize - 4.0));
        double f_of_r_Denominator = MathFunctions.beta(0.5, 0.5*(double_SampleSize - 2.0));
        
        f_of_r = f_of_r_Numerator / f_of_r_Denominator;
        return f_of_r;
    }  
    
    /*********************************************************************
     *                                                                   *
     *            Need t methods for zero rho                            *
     *                                                                   *
     ********************************************************************/
    
    public double densityOfrNonZeroRho (int n, double r, double rho) {
        double sumOfSeries;
        
    /************************************************************************
    *                                                                       *
    *  Johnson, N. L., et al.  (1995). Continuous Univariate Distributions  *
    *  Formula 32.5, p 548-9.  (Two possible formulas                       *
    *                                                                       *
    ************************************************************************/
        double ln_fofr_numer = 0.5 * (double_SampleSize - 1.0) * Math.log(1.0 - rho * rho)
                             + 0.5 * (double_SampleSize - 4.0) * Math.log(1.0 - r * r);    


        //double ln_fofr_denom = 0.5 * JDistr_MathyStuff.Constants.M_LOG_PI
        //                     + JDistr_MathyStuff.MathFunctions.lgammafn(0.5  * (double_n - 1.0))
        //                     + JDistr_MathyStuff.MathFunctions.lgammafn(0.5  * double_n - 1.0);        
 
       
        // This one only has one log gamma call
        double ln_fofr_denom = (3.0 - double_SampleSize) * Math.log(2.0) //  OK
                                  + Constants.M_LOG_PI    //  OK
                                  + MathFunctions.lgammafn(double_SampleSize - 2.0);      
        
        sumOfSeries = 0.0;
        for (int j = 0; j < 100; j++) {
            double double_j = j;
            double lnGammaFactor = 2.0 * MathFunctions.lgammafn((double_SampleSize - 1.0 + double_j) / 2.0);            
            double twoRhoR_ToJ = Math.pow(2.0 * rho * r, double_j);
            double lnJFact = MathFunctions.lgammafn(double_j + 1.0);  //  OK
            double lnJthTerm = lnGammaFactor - lnJFact;
            double jthTerm = Math.exp(lnJthTerm) * twoRhoR_ToJ;
            sumOfSeries += jthTerm;
        }
        double f_of_r = Math.exp(ln_fofr_numer - ln_fofr_denom) * sumOfSeries;
        return f_of_r;
    }
    
    /************************************************************************
    *                                                                       *
    *      Two possible denominators -- one faster than the other??         *
    *                                                                       *
    ************************************************************************/
    
    public double altDensityOfrNonZeroRho (double r) {
        double sumOfSeries;

    /************************************************************************
    *                                                                       *
    *      https://en.wikipedia.org/wiki/Pearson_correlation_coefficient    *
    *                                                                       *
    ************************************************************************/
    
        double ln_fofr_numer = Math.log(double_SampleSize - 2.0) 
                             + MathFunctions.lgammafn(double_SampleSize - 1.0)
                             + 0.5 * (double_SampleSize - 1.0) * Math.log(1.0 - rho * rho)
                             + 0.5 * (double_SampleSize - 4.0) * Math.log(1.0 - r * r);   
        
        double ln_fofr_denom = Constants.M_LN_SQRT_2PI
                             + MathFunctions.lgammafn(double_SampleSize - 0.5)
                             + (double_SampleSize - 1.5) * Math.log(1.0 - rho * r);
  
    /************************************************************************
    *                                                                       *
    *      Abramowitz & Stegun, Handbook of Mathematical Functions          *                   
    *            Hypergeometric Functions (2F1) 15.1.1, p556                *
    *        Speed note for later:  Gamma(0.5) = sqrt(pi)--> denom = pi     *
    *                                                                       *
    ************************************************************************/
        double a = 0.5;
        double b = 0.5;
        double c = 0.5 * (2 * double_SampleSize - 1.0);
        double z = 0.5 * (rho * r + 1.0);
        
        double lnGammaC = MathFunctions.lgammafn(c); 
        double lnGammaA = MathFunctions.lgammafn(a);
        double lnGammaB = MathFunctions.lgammafn(b);
        double factor = Math.exp(lnGammaC) / (Math.exp(lnGammaA) * Math.exp(lnGammaB));
        
        sumOfSeries = 0.0;
        for (int j = 0; j < 100; j++) {
            double double_j = j;
            double lnFraction = MathFunctions.lgammafn(a + double_j) 
                              + MathFunctions.lgammafn(b + double_j)
                              - MathFunctions.lgammafn(c + double_j);   
            
            double ln_zToJ = double_j * Math.log(z); 
            double lnJFact = MathFunctions.lgammafn(double_j + 1.0);
            double lnJthTermInSeries = lnFraction + ln_zToJ - lnJFact;
            double jthTerm = Math.exp(lnJthTermInSeries);
            sumOfSeries += jthTerm;
        }

        double gaussHyper = factor * sumOfSeries;
        
        double f_of_r = Math.exp(ln_fofr_numer - ln_fofr_denom) * gaussHyper;
        return f_of_r;
    }  
    
    public double getMaxFofR() {
        maxFofR = 0.0;
        double tempMaxR = 0.0; 
        double temp_r = -1.0;
        do {
            temp_r += incrementBy;
            tempMaxR = getDensityAt_rEquals (temp_r);
            if (tempMaxR > maxFofR)
                maxFofR = tempMaxR;
        } while (temp_r  < 0.995);  

        return maxFofR;
    }
    
    public void setIncrementBy_To( double thisValue) { incrementBy = thisValue; }
    
    public void setSampleSize( int toThisValue) {
        sampleSize = toThisValue;
        double_SampleSize = sampleSize;
    }
    
    public void setRho(double toThisValue) { rho = toThisValue; }
}