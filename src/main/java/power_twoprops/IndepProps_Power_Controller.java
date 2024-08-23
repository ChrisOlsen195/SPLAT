/**************************************************
 *         IndepProps_Power_Controller            *
 *                  05/30/24                      *
 *                    00:00                       *
 *************************************************/
package power_twoprops;

import genericClasses.Point_2D;
import utilityClasses.*;
import dialogs.power.*;

public class IndepProps_Power_Controller {
    
   // POJOs    
    int sampleSize_1, sampleSize_2;
    
    double nullProp_1, nullProp_2,// null_Var_1, null_Var_2, altDiffInProps,
           /*nullDiffInProps, power, alt_Var_1, alt_Var_2,*/ altProp_1, altProp_2,
           /*null_Sigma_1, null_Sigma_2,*/ alpha, effectSize;
    
    String rejectionCriterion, printedNull, printedAlt, returnStatus;
    
    // My classes
    IndepProps_Power_Dashboard indepProps_Power_Dashboard;
    IndepProps_Power_Model indepProps_Power_Model;
    Point_2D nonRejectionRegion;
    Power_IndProps_Dialog power_IndProps_Dialog;
    
    public IndepProps_Power_Controller() {
        //System.out.println("30 IndepProps_Power_Controller, constructing");
        power_IndProps_Dialog = new Power_IndProps_Dialog();
    }
    
    public String ShowNWait() {
        power_IndProps_Dialog.showAndWait();
        returnStatus = power_IndProps_Dialog.getReturnStatus();
        
        if (returnStatus.equals("Cancel")) { return returnStatus; }
        
        alpha = power_IndProps_Dialog.getLevelOfSignificance();
        effectSize = power_IndProps_Dialog.getEffectSize();
        
        nullProp_1 = power_IndProps_Dialog.getProp_1();
        nullProp_2 = power_IndProps_Dialog.getProp_2();
 
        //nullDiffInProps = nullProp_1 - nullProp_2;
        
        altProp_1 = nullProp_1 + effectSize;
        altProp_2 = nullProp_2 + effectSize;
        
        //altDiffInProps = power_IndProps_Power_Controller.getAltDiff();
        
        sampleSize_1 = power_IndProps_Dialog.getN1();
        sampleSize_2 = power_IndProps_Dialog.getN2();
 
        //null_Var_1 = nullProp_1 * (1 - nullProp_1) / sampleSize_1;
        //null_Var_2 = nullProp_2 * (1 - nullProp_2) / sampleSize_2;  
        
        //null_Sigma_1 = Math.sqrt(null_Var_1);
        //null_Sigma_2 = Math.sqrt(null_Var_2);
        
        //alt_Var_1 = altProp_1 * (1 - altProp_1) / sampleSize_1;
        //alt_Var_2 = altProp_2 * (1 - altProp_2) / sampleSize_2; 
        
        rejectionCriterion = power_IndProps_Dialog.getAltHypothesis();
  
        indepProps_Power_Model = new IndepProps_Power_Model(this);

        switch (rejectionCriterion) {
            case "LessThan":
                //altDiffInProps = nullDiffInProps - effectSize ;
                break;
                
            case "NotEqual":
                //altDiffInProps = nullDiffInProps + effectSize ;
                break;
                
            case "GreaterThan":
                //altDiffInProps = nullDiffInProps + effectSize ;
                break;

            default: 
                String switchFailure = "Switch failure: IndProps_Power_Controller 83 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
                returnStatus = "Cancel";
                break;
        }

        indepProps_Power_Model.setRejectionCriterion(rejectionCriterion);
        indepProps_Power_Model.setProp_1(nullProp_1);
        indepProps_Power_Model.setProp_2(nullProp_2);   
        indepProps_Power_Model.setAltProp_1(altProp_1);
        indepProps_Power_Model.setAltProp_2(altProp_2); 
        indepProps_Power_Model.setSampleSize_1(sampleSize_1);
        indepProps_Power_Model.setSampleSize_2(sampleSize_2);
        indepProps_Power_Model.setAlpha(alpha);  
        indepProps_Power_Model.setEffectSize(effectSize);


        // printed Strings for Power Report
        printedNull = "p1 - p2 = 0.00";
        
        switch (rejectionCriterion) {            
            case "LessThan":
                printedAlt = "p1 - p2 < 0.00";
                break;
                
            case "NotEqual":
                printedAlt = "p1 - p2 \u2260 0.00";
                break;
                
            case "GreaterThan":
                printedAlt = "p1 - p2 > 0.00";
                break;

            default: 
                String switchFailure = "Switch failure: IndProps_Power_Controller 117 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
                returnStatus = "Cancel";
                break;
        }

        indepProps_Power_Model.setPrintedNullHypothesis(printedNull);
        indepProps_Power_Model.setPrintedAltHypothesis(printedAlt);
        indepProps_Power_Model.archiveNullValues();
        indepProps_Power_Model.doTheStandardErrStuff();
        indepProps_Power_Model.constructNonRejectionRegion();
        //power = indepProps_Power_Model.calculatePower();
        indepProps_Power_Model.print_Power_Table();
        indepProps_Power_Dashboard = new IndepProps_Power_Dashboard(this);
        indepProps_Power_Dashboard.initializeFurther();
        indepProps_Power_Dashboard.populateTheBackGround();
        indepProps_Power_Dashboard.putEmAllUp();
        indepProps_Power_Dashboard.showAndWait();
        return returnStatus; 
    }  
    
    public IndepProps_Power_Model get_power_Model_Z() { return indepProps_Power_Model;}

    public String getRejectionCriterion() { return rejectionCriterion; }
    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
    
    public String toString() {
        /*
        System.out.println("\n\n\n\n*****************IndepMeansPowerController toString");
        System.out.println("nullDiffInProps = " + nullDiffInProps);        
        System.out.println("altDiffInProps = " + altDiffInProps);
        System.out.println("null_StErrDiffProps = " + null_StErrDiffProps);
        System.out.println("sampleSize_1 = " + sampleSize_1);        
        System.out.println("sampleSize_2 = " + sampleSize_2);
        System.out.println("nullSigma_1 = " + nullSigma_1);        
        System.out.println("nullSigma_2 = " + nullSigma_2);
        System.out.println("alpha = " + alpha);        
        System.out.println("effectSize = " + effectSize);
        System.out.println("rejectionCriterion = " + rejectionCriterion + "***************\n\n\n");         
        */            
        return "Donesy Wonesy";
    }
}
