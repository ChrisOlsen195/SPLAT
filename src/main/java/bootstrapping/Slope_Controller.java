/**************************************************
 *               Slope_Controller                 *
 *                   01/08/25                     *
 *                     15:00                      *
 *************************************************/
package bootstrapping;

import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.*;
import utilityClasses.*;

public class Slope_Controller {
    // POJOs  
    
    int nReplications;
    
    double hypothesizedSlope;
    double[] theLegals_0, theLegals_1;
            
    String strReturnStatus, firstVarDescription, secondVarDescription;
    
    //String waldoFile = "Slope_Controller";
    String waldoFile = "";
        
    // My classes
    ArrayList<ColumnOfData> quantColsOfData;
    Slope_Dialog slope_Dialog;  
    NonGenericBootstrap_Info nonGen;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Data_Manager dm;

    // ******  Constructor called from Main Menu  ******
    public Slope_Controller(Data_Manager dm) {
        this.dm = dm; 
        dm.whereIsWaldo(38, waldoFile, " *** Constructing");
        quantColsOfData = new ArrayList();
        nonGen = new NonGenericBootstrap_Info("Slope");
    }
  
    public String doPrepColumnsFromNonStacked() {
        dm.whereIsWaldo(44, waldoFile, " --- doPrepColumnsFromNonStacked()");
        strReturnStatus = "Cancel";
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        slope_Dialog = new Slope_Dialog(dm, "QUANTITATIVE");
        slope_Dialog.showAndWait();
        strReturnStatus = slope_Dialog.getReturnStatus();
        if (strReturnStatus.equals("Cancel")) { 
            System.out.println("57 Slope_Controller, strReturnStatus = " + strReturnStatus);
            return strReturnStatus; 
        }
        
        nonGen.setTheBootingStat("Slope");
        nonGen.setAlpha(0.95);
        nReplications = slope_Dialog.getNReplications();

        hypothesizedSlope = slope_Dialog.getHypothesizedDiff();
        System.out.println("66 Slope_Controller, hypothesizedSlope = " + hypothesizedSlope);

        if (nReplications == 0 ) {
            MyAlerts.showZeroReplicationsAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }        
        nonGen.setNReplications(nReplications);
        quantColsOfData = slope_Dialog.getData();
        
        BivariateContinDataObj bivCon = new BivariateContinDataObj(quantColsOfData);
        bivCon.continueConstruction();
        theLegals_0 = new double[bivCon.getNLegalDataPoints()];
        theLegals_1 = new double[bivCon.getNLegalDataPoints()];
        theLegals_0 = bivCon.getXAs_arrayOfDoubles();
        theLegals_1 = bivCon.getYAs_arrayOfDoubles();
        BootTheSlope bootTheSlope = new BootTheSlope(nonGen,
                                                     nReplications,
                                                     theLegals_0,
                                                     theLegals_1);

        firstVarDescription = slope_Dialog.getPreferredFirstVarDescription();
        secondVarDescription = slope_Dialog.getPreferredSecondVarDescription();
        
        nonGen.setBootedStatText("Slope");
        Slope_PostController slope_PostController = new Slope_PostController (
                                                nonGen,
                                                this,
                                                "VarDescr",
                                                bootTheSlope.getTheBooteds()
        );
        slope_PostController.doThePostControllerThing();
        
        return strReturnStatus;        
    }
    
    public String getFirstVarDescription() { return firstVarDescription; }
    public String getSecondVarDescription() { return secondVarDescription; }
    
    /* ************  Independent t WITH DATA  ************************** */
    public double getSlope_Alpha() { return slope_Dialog.getAlpha(); }    
    public String getSlope_AltHypothesis() { 
        return slope_Dialog.getHypotheses(); 
    }
        
    public double getInd_T_HypothesizedDiff() { 
        return slope_Dialog.getHypothesizedDiff();
    }
    
    public ArrayList<QuantitativeDataVariable> getTheQDVs() { return allTheQDVs; }    
    public Slope_Dialog getSlope_Dialog() { return slope_Dialog; }    
    public Data_Manager getDataManager() { return dm; }
    public String getReturnStatus() { return strReturnStatus; }
}
