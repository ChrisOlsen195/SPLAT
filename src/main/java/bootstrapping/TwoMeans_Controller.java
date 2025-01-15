/**************************************************
 *               TwoMean_Controller               *
 *                   01/08/25                     *
 *                     15:00                      *
 *************************************************/
package bootstrapping;

import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.*;
import utilityClasses.*;

public class TwoMeans_Controller {
    // POJOs  
    
    int nReplications;
    
    double hypothesizedDiff;
            
    String strReturnStatus, firstVarDescription, secondVarDescription;
    //String waldoFile = "TwoMeans_Controller";
    String waldoFile = "";
        
    // My classes
    ArrayList<ColumnOfData> quantColsOfData;
    TwoMeans_Dialog twoMeans_Dialog;  
    NonGenericBootstrap_Info nonGen;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Data_Manager dm;

    // ******  Constructor called from Main Menu  ******
    public TwoMeans_Controller(Data_Manager dm) {
        this.dm = dm; 
        dm.whereIsWaldo(31, waldoFile, " *** Constructing");
        quantColsOfData = new ArrayList();
        nonGen = new NonGenericBootstrap_Info("TwoMeans");
    }
  
    public String doPrepColumnsFromNonStacked() {
        dm.whereIsWaldo(41, waldoFile, " --- doPrepColumnsFromNonStacked()");
        strReturnStatus = "Cancel";
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        twoMeans_Dialog = new TwoMeans_Dialog(dm, "QUANTITATIVE");
        twoMeans_Dialog.showAndWait();
        strReturnStatus = twoMeans_Dialog.getReturnStatus();
        if (strReturnStatus.equals("Cancel")) { 
            System.out.println("54 TwoMeans_Controller, strReturnStatus = " + strReturnStatus);
            return strReturnStatus; 
        }
        nReplications = twoMeans_Dialog.getNReplications();

        hypothesizedDiff = twoMeans_Dialog.getHypothesizedDiff();

        if (nReplications == 0 ) {
            MyAlerts.showZeroReplicationsAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }        
        nonGen.setNReplications(nReplications);
        quantColsOfData = twoMeans_Dialog.getData();
        int nLegals_0 = quantColsOfData.get(0).getNLegalCasesInColumn();
        int nLegals_1 = quantColsOfData.get(1).getNLegalCasesInColumn();
        
        System.out.println("71 TwoMeans_Controller, Legals = " + nLegals_0 + " / " + nLegals_1);
        
        double[] theLegals_0 = new double[nLegals_0];
        theLegals_0 = quantColsOfData.get(0).getTheLegalCases_asDoubles();
        
        double[] theLegals_1 = new double[nLegals_1];
        theLegals_1 = quantColsOfData.get(1).getTheLegalCases_asDoubles();
        
        BootTheDiffs bootTheDiffs = new BootTheDiffs(nReplications,
                                                     theLegals_0,
                                                     theLegals_1);

        firstVarDescription = twoMeans_Dialog.getPreferredFirstVarDescription();
        secondVarDescription = twoMeans_Dialog.getPreferredSecondVarDescription();
        
        TwoMeans_PostController twoMeans_PostController = new TwoMeans_PostController (
                                                nonGen,
                                                this,
                                                "VarDescr",
                                                bootTheDiffs.getTheBooteds()
        );
        twoMeans_PostController.doThePostControllerThing();
        
        return strReturnStatus;        
    }
    
    
    
    public String getFirstVarDescription() { return firstVarDescription; }
    public String getSecondVarDescription() { return secondVarDescription; }
    
    /* ************  Independent t WITH DATA  ************************** */
    public double getInd_T_Alpha() { return twoMeans_Dialog.getAlpha(); }    
    public String getInd_T_AltHypothesis() { 
        return twoMeans_Dialog.getHypotheses(); 
    }
        
    public double getInd_T_HypothesizedDiff() { 
        return twoMeans_Dialog.getHypothesizedDiff();
    }
    
    public ArrayList<QuantitativeDataVariable> getTheQDVs() { return allTheQDVs; }    
    public TwoMeans_Dialog getTDialog() { return twoMeans_Dialog; }    
    public Data_Manager getDataManager() { return dm; }
    public String getReturnStatus() { return strReturnStatus; }
}
