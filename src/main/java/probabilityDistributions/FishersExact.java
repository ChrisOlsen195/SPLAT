/**************************************************
 *                FishersExact                    *
 *                  07/27/20                      *
 *                    00:00                       *
 *************************************************/
/**************************************************
 *  This is a utility class for performing the    *
 *  calculations related to Fisher's Exact test.  *
 *************************************************/
package probabilityDistributions;

public class FishersExact {
    
    int a, b, c, d;
    double pValue;
    double newA, newB, newC, newD;
    double pValueBoth, pValuePositive, pValueNegative, probFromExistingTable;
    double probNegative, probPositive;
    
    public FishersExact(int a, int b, int c, int d) { 
        this.a = a; 
        this.b = b;
        this.c = c;
        this.d = d;  
    }
    
    /*************************************************************************
    *  P-values tested against Coutlt, Allan D.  A Note on Fisher's Exact    *
    *  Test. (1965). American Anthropologist, New Series.  67(6) Part I,     *
    *  p1537-41.        July 26, 2020                                        *
    *************************************************************************/
    
    public double getPValue(String altHypoth) {
        switch (altHypoth) {
            case "NotEqual":
                if (a * d == b * c) {
                    pValueBoth  = 1.0;
                } else {
                    pValueNegative = getPValueAltLessThan();
                    pValuePositive = getPValueAltGreaterThan();
                    // probFromExistingTable was counted twice
                    pValueBoth = pValueNegative + pValuePositive - probFromExistingTable;
                }
                pValue = pValueBoth;
            break;

            case "LessThan":
                if (a * d >= b * c) {
                    pValueNegative  = 1.0;
                } 
                else {
                    pValueNegative= getPValueAltLessThan();
                }
                pValue = pValueNegative;
            break;

            case "GreaterThan":
                if (a * d <= b * c) {
                    pValuePositive = 1.0;
                }
                else {
                    pValuePositive = getPValueAltGreaterThan();
                }
                pValue = pValuePositive;
            break;

            default:
                System.out.println("Ack!!  Case failure 102 TwoProp_Inf_PDFView");
                System.exit(103);
        }  
        
        return pValue;
    }

    
    public double getPValueAltLessThan(/* int a, int b, int c, int d */) {
        probFromExistingTable = getExactFisherProb (a, b, c, d);
        pValueNegative = probFromExistingTable;

            newA = a; newB = b; newC = c; newD = d;
            do {
                newB++; newC++; newA--; newD--;
                probNegative = getExactFisherProb(newA, newB, newC, newD);
                if (probNegative < probFromExistingTable) {
                    pValueNegative += probNegative;
                    pValueBoth += probNegative;
                }
            } while ((newA > 0) && (newD > 0));          
        return pValueNegative;
    }    
    
    
    public double getPValueAltGreaterThan() {
        probFromExistingTable = getExactFisherProb (a, b, c, d);
        pValuePositive = probFromExistingTable;

        newA = a; newB = b; newC = c; newD = d;
        do {
            newB--; newC--; newA++; newD++;
            probPositive = getExactFisherProb(newA, newB, newC, newD);
            if (probPositive < probFromExistingTable) {
                pValuePositive += probPositive;
                pValueBoth += probPositive;
            }
        }  while ((newB > 0) && (newC > 0));

        return pValuePositive;
    }
    
    public double getExactFisherProb( double aa, double bb, double cc, double dd) {
        double exactFisherProb;
        newA = aa; newB = bb; newC = cc; newD = dd;

        double newN = newA + newB + newC + newD;
        double abLnFact = GammaDistribution.gammln(newA + newB + 1.);
        double cdLnFact = GammaDistribution.gammln(newC + newD + 1.);
        double acLnFact = GammaDistribution.gammln(newA + newC + 1.);        
        double bdLnFact = GammaDistribution.gammln(newB + newD + 1.0);
        
        int n = a + b + c + d;
        double nLnFact = GammaDistribution.gammln(newN + 1.0);
        double aLnFact = GammaDistribution.gammln(newA + 1.0);
        double bLnFact = GammaDistribution.gammln(newB + 1.0);
        double cLnFact = GammaDistribution.gammln(newC + 1.0);      
        double dLnFact = GammaDistribution.gammln(newD + 1.0);
        
        double lnNumerator = abLnFact + cdLnFact + acLnFact + bdLnFact;
        double lnDenominator = nLnFact + aLnFact + bLnFact + cLnFact + dLnFact;
        
        double lnProb = lnNumerator - lnDenominator;
        exactFisherProb = Math.exp(lnProb);    
        return exactFisherProb;
    }
    
}
