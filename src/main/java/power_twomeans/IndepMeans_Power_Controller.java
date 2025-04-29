/**************************************************
 *         IndepMeans_Power_Controller            *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
package power_twomeans;

import genericClasses.Point_2D;
import dialogs.power.*;
import utilityClasses.MyAlerts;

public class IndepMeans_Power_Controller {
    
    //POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int n_1, n_2;
    double alpha, effectSize;
    
    double nullMu_1, nullMu_2, altDiff_InMeans, nullSigma_1, nullSigma_2,
           nullDiff_InMeans, power;
    
    String strRejectionCriterion, /*rejectionStrategy,*/ strPrinted_Null, 
           strPrinted_Alt, strReturnStatus;
    
    // My Classes
    Point_2D nonRejectionRegion;
    IndepMeans_Power_Model indepMeans_Power_Model;
    Power_IndMeans_Dialog power_IndMeans_Dialog;
    IndepMeans_Power_Dashboard indepMeans_Power_Dashboard;
    
    public IndepMeans_Power_Controller() {
        power_IndMeans_Dialog = new Power_IndMeans_Dialog();
        if (printTheStuff == true) {
            System.out.println("36 *** IndepMeans_Power_Controller, Constructing");
        }
    }
    
    public String ShowNWait() {
        power_IndMeans_Dialog.showAndWait();
        strReturnStatus = power_IndMeans_Dialog.getReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        nullMu_1 = power_IndMeans_Dialog.getMean_1();
        nullMu_2 = power_IndMeans_Dialog.getMean_2();
        
        nullSigma_1 = power_IndMeans_Dialog.getStDev1();
        nullSigma_2 = power_IndMeans_Dialog.getStDev2();
        
        n_1 = power_IndMeans_Dialog.getN1();
        n_2 = power_IndMeans_Dialog.getN2();

        alpha = power_IndMeans_Dialog.getAlpha();
        effectSize = power_IndMeans_Dialog.getMinEffectSize();
        nullDiff_InMeans = power_IndMeans_Dialog.getTheNullDiff();
        altDiff_InMeans = power_IndMeans_Dialog.getAltDiff();

        strRejectionCriterion = power_IndMeans_Dialog.getRejectionCriterion();
  
        indepMeans_Power_Model = new IndepMeans_Power_Model(this);

        switch (strRejectionCriterion) {
            case "LessThan":
                altDiff_InMeans = nullDiff_InMeans - effectSize ;
                break;
                
            case "NotEqual":
                altDiff_InMeans = nullDiff_InMeans + effectSize ;
                //System.out.println("74 controller, altDiffInMeans = " + altDiffInMeans);
                break;
                
            case "GreaterThan":
                altDiff_InMeans = nullDiff_InMeans + effectSize ;
                break;
                
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_Controller 75 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }

        indepMeans_Power_Model.setRejectionCriterion(strRejectionCriterion);
        indepMeans_Power_Model.setNullDiffMu(nullDiff_InMeans);
        indepMeans_Power_Model.setAltMuDiff(altDiff_InMeans);
        indepMeans_Power_Model.setSampleSize_1(n_1);
        indepMeans_Power_Model.setSampleSize_2(n_2);
        indepMeans_Power_Model.setNullSigma_1(nullSigma_1);
        indepMeans_Power_Model.setNullSigma_2(nullSigma_2);
        indepMeans_Power_Model.setAlpha(alpha);  
        indepMeans_Power_Model.setEffectSize(effectSize);
        // printed Strings for Power Report
        strPrinted_Null = "\u03BC = " + String.valueOf(nullDiff_InMeans);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                strPrinted_Alt = "\u03BC < " + String.valueOf(nullDiff_InMeans);
                break;
                
            case "NotEqual":
                strPrinted_Alt = "\u03BC \u2260 " + String.valueOf(nullDiff_InMeans);
                break;
                
            case "GreaterThan":
                strPrinted_Alt = "\u03BC > " + String.valueOf(nullDiff_InMeans);
                break;

            default:
                String switchFailure = "Switch failure: IndepMeans_Power_Controller 105 " + strRejectionCriterion;
                System.exit(150);
        }

        indepMeans_Power_Model.setPrintedNullHypothesis(strPrinted_Null);
        indepMeans_Power_Model.setPrintedAltHypothesis(strPrinted_Alt);
        indepMeans_Power_Model.archiveNullValues();
        indepMeans_Power_Model.constructNonRejectionRegion();
        power = indepMeans_Power_Model.calculatePower();
        indepMeans_Power_Model.print_Power_Table();
        indepMeans_Power_Dashboard = new IndepMeans_Power_Dashboard(this);
        indepMeans_Power_Dashboard.initializeFurther();
        indepMeans_Power_Dashboard.populateTheBackGround();
        indepMeans_Power_Dashboard.putEmAllUp();
        indepMeans_Power_Dashboard.showAndWait();
        return strReturnStatus; 
    }  
    
    public IndepMeans_Power_Model get_power_Model_Z() { return indepMeans_Power_Model;}

    public String getRejectionCriterion() { return strRejectionCriterion; }
    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
    
    public double getMean_1() {return nullMu_1; }
    public double getMean_2() {return nullMu_2; }
    
    public double getNullSigma_1() {return nullSigma_1; }
    public double getNullSigma_2() {return nullSigma_2; }
    
    public double getMinEffectSize() { return effectSize; }
    
    public double getNullDifferenceInMeans() { return nullDiff_InMeans; }
    
    public String toString() {
        /*
        System.out.println("\n\n*****************IndepMeansPowerController toString");
        System.out.println("nullDiffInMeans = " + nullDiffInMeans);        
        System.out.println(" altDiffInMeans = " + altDiffInMeans);
        System.out.println(   "      Mean_1 = " + nullMu_1);        
        System.out.println(   "      Mean_2 = " + nullMu_2);
        System.out.println("   sampleSize_1 = " + sampleSize_1);        
        System.out.println("   sampleSize_2 = " + sampleSize_2);
        System.out.println("    nullSigma_1 = " + nullSigma_1);        
        System.out.println("    nullSigma_2 = " + nullSigma_2);
        System.out.println("          alpha = " + alpha);        
        System.out.println("     effectSize = " + effectSize);
        System.out.println("   rejCriterion = " + rejectionCriterion + "***************\n\n\n");            
        */
        return "Donesy Wonesy";
    }
}
