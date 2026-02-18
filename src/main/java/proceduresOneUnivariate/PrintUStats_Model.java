/**************************************************
 *                PrintUStats_Model               *
 *                    01/16/25                    *
 *                      12:00                     *
 *************************************************/
package proceduresOneUnivariate;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import java.util.ArrayList;
import utilityClasses.StringUtilities;

public class PrintUStats_Model {
    // POOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int originalN, legalN, missingN;
    
    double kurtosis, excessKurtosis, iqr, range, coefOfVar, trimmedMean, variance,
           standDev, fp_Skew, adj_fp_Skew;
    
    double[] theMoments, theFive;
    
    String varLabel, varDescription;
    
    ArrayList<String> stringsToPrint;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;
    
    public PrintUStats_Model() { }
    
    public PrintUStats_Model(String varDescription, QuantitativeDataVariable qdv, boolean trimOrNot) {  
        this.qdv = new QuantitativeDataVariable();
        this.qdv = qdv;
        if (printTheStuff == true) {
            System.out.println("37 *** ColumnOfData, Constructing");
        }
        
        if (trimOrNot) {
            this.varDescription = varDescription.trim();
        }
        else {  this.varDescription = varDescription; }
        
        varLabel = qdv.getTheVarLabel();
        ucdo = new UnivariateContinDataObj("PrintUStats_Model", qdv);
        theMoments = new double[4];
        theFive = new double[5] ;
        stringsToPrint = new ArrayList();
        originalN = qdv.getOriginalN();
        legalN = qdv.getLegalN();
        missingN = originalN - legalN;
        getTheStats();    
    }
    
    private void getTheStats() {
        theMoments = ucdo.getTheMoments(); // mean, stDev, skew, kurtosis
        variance = theMoments[1] * theMoments[1];
        standDev = theMoments[1];
        trimmedMean = ucdo.getTheTrimmedMean(0.10);
        
        if (theMoments[0] != 0.0) {
            coefOfVar = ucdo.getCoefOfVar();
            fp_Skew = ucdo.getTheSkew();
            adj_fp_Skew = ucdo.getAdjustedFisherPearsonSkew();
            kurtosis = ucdo.getTheKurtosis();
            excessKurtosis = ucdo.getTheExcessKurtosis();
        }
        else {
            coefOfVar = Double.NaN;
            fp_Skew = Double.NaN;
            adj_fp_Skew = Double.NaN;
            kurtosis = Double.NaN;
            excessKurtosis = Double.NaN;
        }

        theFive = ucdo.get_5NumberSummary();
        iqr = ucdo.getTheIQR();
        range = ucdo.getTheRange();
    }
    
    public void constructThePrintLines() {
        addNBlankLinesToReport(1);
        stringsToPrint.add("        Univariate statistics");
        addNBlankLinesToReport(2);        
        stringsToPrint.add(String.format(" *****  File information  *****"));
        addNBlankLinesToReport(1);   
        stringsToPrint.add(String.format("      Variable: %2s", varDescription));
        addNBlankLinesToReport(1);   
        stringsToPrint.add(String.format("      N in file: %4d", originalN));
        addNBlankLinesToReport(1);   
        stringsToPrint.add(String.format("      N missing: %4d", missingN));        
        addNBlankLinesToReport(1);   
        stringsToPrint.add(String.format("        N Legal: %4d", legalN));         
        addNBlankLinesToReport(2); 
        
        stringsToPrint.add(String.format("*****  Basic mean based statistics  *****"));
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("           Mean:   %9.3f", theMoments[0]));
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("       Variance:   %9.3f", variance));        
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("      Stand dev:   %9.3f", standDev));        
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("           Skew:   %9.3f", fp_Skew));
        //addNBlankLinesToReport(1);
        //stringsToPrint.add(String.format("  Adjusted Skew:   %9.3f", adj_fp_Skew));        
        addNBlankLinesToReport(2);
        
        stringsToPrint.add(String.format("  *****  Other mean based statistics  *****"));        
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("   Trimmed mean:   %9.3f", trimmedMean)); 
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("       Kurtosis:   %9.3f", theMoments[3])); 
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("Excess Kurtosis:   %9.3f", excessKurtosis));
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("             CV:   %9.3f", coefOfVar));                        
        addNBlankLinesToReport(2); 
        
        stringsToPrint.add(String.format(" *****  Five-number summary  *****"));
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("        Minimum:   %9.3f", theFive[0])); 
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("             Q1:   %9.3f", theFive[1]));  
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("         Median:   %9.3f", theFive[2]));        
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("             Q3:   %9.3f", theFive[3]));        
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("        Maximum:   %9.3f", theFive[4]));          
        addNBlankLinesToReport(2);
        
        stringsToPrint.add(String.format("  *****  Other median based statistics  *****"));  
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("            IQR:   %9.3f", iqr));        
        addNBlankLinesToReport(1);
        stringsToPrint.add(String.format("          Range:   %9.3f", range));         
    }
    
    private void addNBlankLinesToReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(stringsToPrint, thisMany);
    } 
    
    /*      These would be called for  bivariate printing     */
    public String getVarDescr() { return varDescription; }
    public int getOriginalN() { return originalN; }
    public int getMissingN() { return missingN; }
    public int getLegalN() { return legalN; }
    public double getTheMean() { return theMoments[0]; }
    public double getTheStDev() { return standDev; }
    public double getTheVariance() { return variance; }
    public double getTheSkew() { return fp_Skew; }
    public double getTheAdjSkew() { return adj_fp_Skew; }
    public double getTheTrimmedMean() { return trimmedMean; }    
    public double getTheKurtosis() { return theMoments[3]; }
    public double getTheCV() { return coefOfVar; }
    public double getTheMin() { return theFive[0]; }
    public double getQ1() { return theFive[1]; }
    public double getTheMedian() { return theFive[2]; }
    public double getQ3() { return theFive[3]; }
    public double getTheMax() { return theFive[4]; }  
    public double getTheIQR() { return iqr; }
    public double getTheRange() { return range; }
    
    /*      This would be called for univariate printing        */
    public ArrayList<String> getStringsToPrint() { 
        return stringsToPrint; }
}
