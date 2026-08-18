/**************************************************
 *           LinReg_Power_Controller              *
 *                  06/13/26                      *
 *                    15:00                       *
 *************************************************/
package power_LinReg;

import dialogs.power.*;
import genericClasses.Point_2D;
import simpleRegression.Inf_Regr_Controller;
import simpleRegression.Inf_Regr_Dashboard;
import utilityClasses.MyAlerts;

public class LinReg_Power_Controller {
    // POJOs
    // boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize, maxSampleSize;
    double alpha, nullSlope, altSlope, msResid, stErrSlope, effectSize,
           Sxx, varX, sigmaX;

    String strRejectionCriterion, strPrinted_Null, strPrinted_Alt, 
           strReturnStatus;
    
    // My classes
    LinReg_Power_Dashboard linReg_Power_Dashboard;
    LinReg_Power_Model linReg_Power_Model;
    Point_2D nonRejectionRegion;
    Power_LinReg_Dialog power_LinReg_Dialog;
    Inf_Regr_Controller inf_Regr_Controller;
    Inf_Regr_Dashboard inf_Regr_Dashboard;

    public LinReg_Power_Controller(Inf_Regr_Dashboard inf_Regr_Dashboard, Inf_Regr_Controller inf_Regr_Controller) {
        if (printTheStuff) {
            System.out.println("36 --- LinReg_Power_Controller, constructing");
        }
        this.inf_Regr_Dashboard = inf_Regr_Dashboard;
        this.inf_Regr_Controller = inf_Regr_Controller;
        power_LinReg_Dialog = new Power_LinReg_Dialog(this);
        sampleSize = inf_Regr_Controller.getSampleSize();
        if (printTheStuff) {
            System.out.println("43 ... LinReg_Power_Controller, sampleSize = " + sampleSize) ;
        }
    }
    
    public String ShowNWait() {
        if (printTheStuff) {
            System.out.println("49 ... LinReg_Power_Controller, power_LinReg_Dialog ShowNWait() = ") ;
            System.out.println("50 ... LinReg_Power_Controller, isVisible = " + power_LinReg_Dialog.isShowing()) ;
        }
        if (!power_LinReg_Dialog.isShowing()) {
            power_LinReg_Dialog.showAndWait();
        }
        strReturnStatus = power_LinReg_Dialog.getStrReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        maxSampleSize = power_LinReg_Dialog.getMaxSampleSize();
       if (printTheStuff) {
            System.out.println("61 ... LinReg_Power_Controller, sampleSize = " + sampleSize) ;
            System.out.println("62 ... LinReg_Power_Controller, maxSampleSize = " + maxSampleSize) ;
        }
        nullSlope = power_LinReg_Dialog.getNullParam();
        setMSResid(inf_Regr_Controller.get_MSResid());
        varX = inf_Regr_Controller.get_VarX();
        setVarX(inf_Regr_Controller.get_VarX());
        setSigmaX(Math.sqrt(varX));
        Sxx = inf_Regr_Controller.get_SSX();
        if (printTheStuff) {
            System.out.println("71 ... LinReg_Power_Controller, msResid / ssX / sigmaX = " + msResid + " / " + Sxx + " / " + sigmaX);
        }        
        
        alpha = power_LinReg_Dialog.getAlpha();
        effectSize = power_LinReg_Dialog.getEffectSize();
        
        strRejectionCriterion = power_LinReg_Dialog.getRejectionCriterion();        
        stErrSlope = Math.sqrt(msResid / Sxx);
        linReg_Power_Model = new LinReg_Power_Model(this);
        linReg_Power_Model.setStErr_Beta(stErrSlope);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                altSlope = nullSlope - effectSize;
                break;
                
            case "NotEqual":
                altSlope = nullSlope + effectSize;
                break;
                
            case "GreaterThan":
                altSlope = nullSlope + effectSize;
                break;
                
            default:
                String switchFailure = "Switch failure: OneMean_Power_Controller 96 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);                 
        }
        
        if (printTheStuff) {
            //System.out.println("101 --- LinReg_Power_Controller, msResid = " + msResid);
            //System.out.println("102 --- LinReg_Power_Controller, Sxx = " + Sxx);
            //System.out.println("103 --- LinReg_Power_Controller, effectSize = " + effectSize);
            //System.out.println("104 --- LinReg_Power_Controller, altSlope = " + altSlope);
            //System.out.println("105 --- LinReg_Power_Controller, strRejectionCriterion = " + strRejectionCriterion);
            //System.out.println("106 --- LinReg_Power_Controller, stErrSlope = " + stErrSlope);
        }
        
        linReg_Power_Model.setNullBeta(0.0);
        linReg_Power_Model.setRejectionCriterion(strRejectionCriterion);
        linReg_Power_Model.setAltBeta(altSlope);
        linReg_Power_Model.setSampleSize(sampleSize);
        linReg_Power_Model.setAlpha(alpha);  
        linReg_Power_Model.setEffectSize(effectSize);
        linReg_Power_Model.setStErr_Beta(stErrSlope);         //  StandErr is 
        linReg_Power_Model.setStErr_AltBeta(stErrSlope);     //  Homogeneous
        linReg_Power_Model.calculatePower();
        // printed Strings for Power Report
        strPrinted_Null = "\u03B2 = " + String.valueOf(nullSlope);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                strPrinted_Alt = "\u03B2 < " + String.valueOf(nullSlope);
                break;
                
            case "NotEqual":
                strPrinted_Alt = "\u03B2 \u2260 " + String.valueOf(nullSlope);
                break;
                
            case "GreaterThan":
                strPrinted_Alt = "\u03B2 > " + String.valueOf(nullSlope);
                break;

            default:
                String switchFailure = "Switch failure: OneMean_Power_Controller 135 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }

        linReg_Power_Model.setPrintedNullHypothesis(strPrinted_Null);
        linReg_Power_Model.setPrintedAltHypothesis(strPrinted_Alt);
        linReg_Power_Model.archiveNullValues();
        linReg_Power_Model.constructNonRejectionRegion();
        linReg_Power_Model.print_Power_Table();
        
        linReg_Power_Dashboard = new LinReg_Power_Dashboard(this);
        linReg_Power_Dashboard.initializeFurther();
        linReg_Power_Dashboard.populateTheBackGround();
        linReg_Power_Dashboard.putEmAllUp();
        linReg_Power_Dashboard.showAndWait();
        return strReturnStatus; 
    }  
    
    public double getMSResid() { return msResid; }
    public void setMSResid(double toThis) {
        if (printTheStuff) {
            System.out.println("156 --- LinReg_Power_Controller,setting msResid = " + msResid);
        }
        msResid = toThis;
    }
    
    public double getSigmaX() { return sigmaX; }
    public void setSigmaX(double toThis) {
        if (printTheStuff) {
            System.out.println("164 --- LinReg_Power_Controller,setting sigmaX = " + msResid);
        }
        sigmaX = toThis;
    }
    
    public double getVarX() { return varX; }
    public void setVarX(double toThis) {
       if (printTheStuff) {
            System.out.println("172 --- LinReg_Power_Controller,setting sigmaX = " + varX);
        }
        varX = toThis;
    }
    
    public Power_LinReg_Dialog get_Power_LinReg_Dialog() { return power_LinReg_Dialog; }
    public Inf_Regr_Dashboard get_Inf_Regr_Dashboard() { return inf_Regr_Dashboard; }
    public int getMaxSampleSize() { return maxSampleSize; }
    public LinReg_Power_Model get_LinReg_Power_Model() { return linReg_Power_Model;}
    public String getRejectionCriterion() { return strRejectionCriterion; }    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
}
