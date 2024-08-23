/**************************************************
 *           IndepProps_Power_Model               *
 *                  11/02/23                      *
 *                    15:00                       *
 *************************************************/
package power_twoprops;

import genericClasses.Point_2D;
import noncentrals.JDistr_Noncentrals.*;
import java.util.ArrayList;
import probabilityDistributions.*;
import utilityClasses.*;

public class IndepProps_Power_Model {
    
    int n_1, n_2, archived_n_1, archived_n_2;
    
    double alpha, meanAltDiffProps, altDiffInProps, nullDiffInProps, effectSize,
           null_Prop_1, null_Prop_2, nullVar_1, nullVar_2, altProp_1, altProp_2, 
           nullLowerLimit, nullUpperLimit, loCum, power, //zAltDiffProps,
           archivedNullDiffInProps, archivedNullVar_1, archivedNullVar_2,
           archivedAltDiffInProps, archivedAlpha, archivedEffectSize, isq_Sigma, 
           meanNullDiffProps, standErrNullDiffProps, standErrAltDiffProps, 
           epsilon, prePhi, critZForOneTailedAlpha, critZForTwoTailedAlpha, 
           criticalZ, archivedNullProp_1,archivedNullProp_2;
    
    Point_2D nonRejectionRegion;
    String rejectionCriterion, sourceString, printedNullHypoth, printedAltHypoth;    
    
    ArrayList<String> powerReport;
    
    IndepProps_Power_Controller indepProps_Power_Controller;
    StandardNormal standardNormal;
    
    public IndepProps_Power_Model(IndepProps_Power_Controller indepMeans_Power_Controller) {
        //System.out.println("36 IndepProps_Power_Model, constructing");
        this.indepProps_Power_Controller = indepMeans_Power_Controller;
        powerReport = new ArrayList();
        standardNormal = new StandardNormal();
    }
    
    public void doTheStandardErrStuff() {
        calculateNullDistribution();
        calculateAltDistribution();        
    }
    
    /**********************************************************************
     *   Power formulae from Chow, S.  (2018).  Sample Size Calculations  *
     *   in Clinical Research (3rd).  CRC Press.                          *
     *********************************************************************/
    
    public double calculatePower() {
        doTheStandardErrStuff();
        constructNonRejectionRegion();
        
        if (rejectionCriterion.equals("NotEqual")) {
            criticalZ = critZForTwoTailedAlpha;
        } else { criticalZ = critZForOneTailedAlpha; }
        
        double criticalValue = criticalZ * standErrNullDiffProps;
          
        switch(rejectionCriterion) {
            case "LessThan": 
                epsilon = 0.0;  // p1 - p2???  Null diff?
                prePhi = (effectSize - epsilon)/standErrNullDiffProps - critZForOneTailedAlpha;
                power = Normal.cumulative(prePhi, 0.0, 1.0, true, false);                 
                loCum = Normal.cumulative(-criticalValue, meanAltDiffProps, standErrAltDiffProps, true, false);
                power = loCum;
                break;
                
            case "NotEqual":   
                epsilon = 0.0;  // p1 - p2???  Null diff?
                prePhi = Math.abs(effectSize)/standErrNullDiffProps - critZForTwoTailedAlpha;
                power = Normal.cumulative(prePhi, 0.0, 1.0, true, false); 
                break;
                
            case "GreaterThan":
                epsilon = 0.0;  // p1 - p2???  Null diff?
                prePhi = (effectSize - epsilon)/standErrNullDiffProps - critZForOneTailedAlpha;
                power = Normal.cumulative(prePhi, 0.0, 1.0, true, false);   
                break;   
                
            default:
                String switchFailure = "Switch failure: IndepProps_Power_Model 84 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        return power;        
    }
       
    public void calculateNullDistribution() {
        double p1, p2, q1, q2, /*pooled_P, pooled_Q,*/ dbl_n1, dbl_n2;
        p1 = null_Prop_1;
        p2 = null_Prop_2;
        dbl_n1 = n_1;
        dbl_n2 = n_2;
        
        q1 = 1.0 - p1;
        q2 = 1.0 - p2;

        meanNullDiffProps = 0.0;
        standErrNullDiffProps = Math.sqrt(p1 * q1 / dbl_n1 + p2 * q2 / dbl_n2);  
    }
    
    public void calculateAltDistribution() {
        double p1, p2, q1, q2, dbl_n1, dbl_n2;
        //System.out.println("117 IndPropsPowMod, effectSize = " + effectSize);
        p1 = null_Prop_1;
        p2 = null_Prop_2;
        p2 = p1 - effectSize;
        dbl_n1 = n_1;
        dbl_n2 = n_2;

        q1 = 1.0 - p1;
        q2 = 1.0 - p2;

        meanAltDiffProps = p1 - p2;
        standErrAltDiffProps = Math.sqrt(p1 * q1 / dbl_n1 + p2 * q2 / dbl_n2); 
        //zAltDiffProps = meanAltDiffProps / standErrAltDiffProps;
    }
        
    
    public void constructNonRejectionRegion() {
        nullLowerLimit = 0.0; nullUpperLimit = 0.0;//  Happy compiler, happy runs
        
        switch (rejectionCriterion) {
            case "LessThan":
                nullLowerLimit = Normal.quantile(alpha, meanNullDiffProps, 
                                             standErrNullDiffProps, true, false);
                nullUpperLimit = Double.POSITIVE_INFINITY; 
            break;
                    
            case "NotEqual":
                nullLowerLimit = Normal.quantile(alpha / 2., meanNullDiffProps, 
                                             standErrNullDiffProps, true, false);
                nullUpperLimit = Normal.quantile(1.0 - alpha / 2., meanNullDiffProps, 
                                             standErrNullDiffProps, true, false);  
            break;
                            
            case "GreaterThan":
                nullLowerLimit = Double.NEGATIVE_INFINITY;
                nullUpperLimit = Normal.quantile(1.0 - alpha, meanNullDiffProps, 
                                             standErrNullDiffProps, true, false); 
            break;
            
            default:
                String switchFailure = "Switch failure: IndepProps_Power_Model 146 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }

        nonRejectionRegion = new Point_2D(nullLowerLimit, nullUpperLimit);
    }
    
    public double getProp_1() { return null_Prop_1; }
    public void setProp_1(double toThis) { null_Prop_1 = toThis; }
    
    public double getProp_2() { return null_Prop_2; }
    public void setProp_2(double toThis) { null_Prop_2 = toThis; }
    
    public double getAltProp_1() { return altProp_1; }
    public void setAltProp_1(double toThis) { altProp_1 = toThis;  }
    
    public double getAltProp_2() { return altProp_2; }
    public void setAltProp_2(double toThis) { altProp_2 = toThis; }
    
    public int getSampleSize_1() { return n_1; }
    public void setSampleSize_1(int toThis) { n_1 = toThis; }
    
    public int getSampleSize_2() { return n_2; }
    public void setSampleSize_2(int toThis) { n_2 = toThis; }
    
    public double getAlpha() { return alpha; }
    public void setAlpha(double toThis) { 
        alpha = toThis; 
        critZForOneTailedAlpha = standardNormal.getInvRightTailArea(alpha);
        critZForTwoTailedAlpha = standardNormal.getInvRightTailArea(alpha / 2.0);    
    }
    
    public String getPrintedNullHypothesis() { return printedNullHypoth;}
    public void setPrintedNullHypothesis(String toThis) {
        printedNullHypoth = toThis;
    }
    
    public String getPrintedAltHypothesis() { return printedAltHypoth;}
    public void setPrintedAltHypothesis(String toThis) {
        printedAltHypoth = toThis;
    }
   
    // getSENull and Alt are for graphing in the PDFView
    public double getStandErrNullDiffProps() {return standErrNullDiffProps; }
    public void setStandErrNullDiffProps(double toThis) {
        standErrNullDiffProps = toThis; 
    }
    
    public double getStandErrAltDiffProps() {return standErrAltDiffProps; }
    public void setStandErrAltDiffProps(double toThis) {
        standErrAltDiffProps = toThis; 
    }
    
    public double getIsq_Sigma() {return isq_Sigma; }
    public double getPower() { return power; }    
    public String getRejectionCriterion() { return rejectionCriterion; }
    public void setRejectionCriterion(String toThis) {
        rejectionCriterion = toThis;
    }
    
    public double getEffectSize() { return effectSize; }
    public void setEffectSize(double toThis) {
        effectSize = toThis;        
        if (rejectionCriterion.equals("LessThan")) {
            altDiffInProps = nullDiffInProps - effectSize;
        }  else {
            altDiffInProps = nullDiffInProps + effectSize;   
        }
    }
    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
 
    public void archiveNullValues() {
        archivedNullDiffInProps = nullDiffInProps;
        archivedAltDiffInProps = altDiffInProps;
        archived_n_1 = n_1;
        archived_n_2 = n_2;
        archivedNullProp_1 = null_Prop_1;
        archivedNullProp_2 = null_Prop_2;
        archivedNullVar_1 = nullVar_1;
        archivedNullVar_2 = nullVar_2;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    

    public void restoreNullValues() {
        nullDiffInProps = archivedNullDiffInProps;
        altDiffInProps = archivedAltDiffInProps ;
        n_1 = archived_n_1;
        n_2 = archived_n_2;
        null_Prop_1 = archivedNullProp_1;
        null_Prop_2 = archivedNullProp_2;
        nullVar_1 = archivedNullVar_1;
        nullVar_2 = archivedNullVar_2;
        alpha  = archivedAlpha; 
        effectSize = archivedEffectSize;
    } 
    
   public void print_Power_Table() {
        addNBlankLinesToPowerReport(2);
        powerReport.add("       Power Statistics");
        addNBlankLinesToPowerReport(2);
        powerReport.add(String.format("Two independent proportions"));
        addNBlankLinesToPowerReport(1);
        sourceString = "       Null hypothesis: " + printedNullHypoth;
        addNBlankLinesToPowerReport(1);
        powerReport.add(String.format("%15s ", sourceString));
        sourceString = "Alternative hypothesis: " + printedAltHypoth;
        addNBlankLinesToPowerReport(1);
        powerReport.add(String.format("%15s ", sourceString));
        addNBlankLinesToPowerReport(1);
        sourceString = "Sample size 1 =";
        powerReport.add(String.format("%20s %4d", sourceString, n_1));
        addNBlankLinesToPowerReport(1);
        sourceString = "Sample size 2 =";
        powerReport.add(String.format("%20s %4d", sourceString, n_2));
        addNBlankLinesToPowerReport(1);
        sourceString = "Effect Size =";
        powerReport.add(String.format("%20s %8.3f", sourceString, effectSize));       
        addNBlankLinesToPowerReport(1);
        sourceString = "Power =";
        powerReport.add(String.format("%20s %8.2f", sourceString, power));        
        addNBlankLinesToPowerReport(1);
   }    
   
    private void addNBlankLinesToPowerReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(powerReport, thisMany);
    }
    
    public ArrayList<String> getPowerReport() { return powerReport; }
    
    public IndepProps_Power_Controller getController() { return indepProps_Power_Controller; }
    
    public void printModelStuff() {
        /*
        System.out.println("\n334 TwoPropPower, printModelStuff()");
        System.out.println(" nullProp 1  = " + null_Prop_1);
        System.out.println(" nullProp 2  = " + null_Prop_2);
        System.out.println("sampleSize_1 = " + sampleSize_1); 
        System.out.println("sampleSize_2 = " + sampleSize_2);   
        System.out.println("   nHarmonic = " + nHarmonic);
        System.out.println("       power = " + power);
        */
    }   
}
