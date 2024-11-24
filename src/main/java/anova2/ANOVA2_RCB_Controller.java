/**************************************************
 *            ANOVA2_RCB_Controller               *
 *                  11/17/24                      *
 *                   12:00                        *
 *************************************************/
/**************************************************
 *    Tested against Kirk p289  02/12/24          *
 *************************************************/
package anova2;

import dataObjects.CategoricalDataVariable;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.*;
import utilityClasses.*;

public class ANOVA2_RCB_Controller {
    
    // POJOs
    boolean dataAreBalanced, replicatesExist, dataAreMissing;
    
    int nReplications;
            
    String whichANOVA2, returnStatus, factorA_Name, factorB_Name, 
           varLabel, varDescription;

    //String waldoFile = "ANOVA2_RCB_Controller";
    String waldoFile = "";

    // My classes
    ANOVA2_RCB_Dashboard anova2_RCB_Dashboard;
    ANOVA2_RCB_Dialog anova2_RCB_Dialog;
    ANOVA2_Factorial_Model anova2_Factorial_Model;
    ANOVA2_RCB_Model anova2_RCB_Model;
    ANOVA2_RCB_wReplicates_Dashboard anova2_RCB_wReplicates_Dashboard;

    Data_Manager dm;
            
    public ANOVA2_RCB_Controller (Data_Manager dm, String whichANOVA2) {
        this.whichANOVA2 = whichANOVA2;
        this.dm = dm; 
        dm.whereIsWaldo(42, waldoFile, "\n ***Constructing");
        anova2_RCB_Dialog = new ANOVA2_RCB_Dialog(dm, whichANOVA2);
        anova2_RCB_Dialog.doTheDialog();
    }
        
public String doTheANOVA2() {
        dm.whereIsWaldo(48, waldoFile, "doTheANOVA2()");
        returnStatus = "OK";
        int casesInStruct = dm.getNCasesInStruct();

        if (anova2_RCB_Dialog.getDataAreMissing()) {
            MyAlerts.showANOVA_missingDataAlert();     
            returnStatus = "Cancel";
            return returnStatus;
        }    
        
        if (anova2_RCB_Dialog.getReturnStatus().equals("Cancel")) {
            returnStatus = "Cancel";
            return returnStatus;
        }

        dataAreBalanced = anova2_RCB_Dialog.getDesignIsBalanced();
        replicatesExist = anova2_RCB_Dialog.getThereAreReplications();
        dataAreMissing = anova2_RCB_Dialog.getDataAreMissing();
        
        if (dataAreMissing) {
            returnStatus = "MissingData";
            return returnStatus;
        }
        
        int nLevelsA = anova2_RCB_Dialog.getNumRows();
        int nLevelsB = anova2_RCB_Dialog.getNumCols();

        String[] factorA_Levels = anova2_RCB_Dialog.getFactorA_Labels();
        String[] factorB_Levels = anova2_RCB_Dialog.getFactorB_Labels();  
        
        factorA_Name = anova2_RCB_Dialog.getFactorA_Name();
        factorB_Name = anova2_RCB_Dialog.getFactorB_Name();   
        varLabel = anova2_RCB_Dialog.getResponse_Name(); 
        
        // data is a row x col ArrayList of values;
        ArrayList <String> [][] data = anova2_RCB_Dialog.getData();
     
        int nDataPoints = dm.getNCasesInStruct();
        String[] columnVar = new String[nDataPoints];
        String[] rowVar = new String[nDataPoints];
        ArrayList<String> responseVar = new ArrayList();

        String asterisk = "*";
        int ithDataIndex = 0;
        
        for (int ithLevelA = 0; ithLevelA < nLevelsA; ithLevelA++) { // Rows -- levels of A            
            for (int jthLevelB = 0; jthLevelB < nLevelsB; jthLevelB++) { // Columns -- levels of B
                nReplications = data[jthLevelB][ithLevelA].size();
                for (int k = 0; k < nReplications; k++) {
                    String dataValue = data[jthLevelB][ithLevelA].get(k);
                    if (!dataValue.equals(asterisk)) { // Should be true always
                        rowVar[ithDataIndex] = factorA_Levels[ithLevelA];
                        columnVar[ithDataIndex] = factorB_Levels[jthLevelB];
                        responseVar.add(dataValue);
                        ithDataIndex++; 
                    } else {
                        dataAreMissing = true;
                    }
                }               
            } // jthLevelB loop            
        } // ithLevelA loop 

        CategoricalDataVariable cdv_FactorA = new CategoricalDataVariable(factorB_Name, columnVar);
        CategoricalDataVariable cdv_FactorB = new CategoricalDataVariable(factorA_Name, rowVar);  
        QuantitativeDataVariable qdv_Response = new QuantitativeDataVariable(varLabel, varDescription, responseVar); 

        switch (whichANOVA2) {
            case "Factorial":
                anova2_Factorial_Model = new ANOVA2_Factorial_Model(dm,
                                               this, 
                                               cdv_FactorB,
                                               cdv_FactorA,
                                               qdv_Response); 
                returnStatus = anova2_Factorial_Model.doTwoWayANOVA();
                
                if (!returnStatus.equals("OK")) { return returnStatus; }

                anova2_RCB_Dashboard = new ANOVA2_RCB_Dashboard(this, anova2_Factorial_Model);
                anova2_RCB_Dashboard.populateTheBackGround();
                anova2_RCB_Dashboard.putEmAllUp();
                anova2_RCB_Dashboard.showAndWait();
                break;  
            
            case "RCB":     // Randomized complete block 
                anova2_RCB_Model = new ANOVA2_RCB_Model( dm,
                                           this, 
                                           cdv_FactorB,
                                           cdv_FactorA,
                                           qdv_Response); 
                
                returnStatus = anova2_RCB_Model.doTwoWayANOVA();
                
                if (!returnStatus.equals("OK")) { return returnStatus; }
                
                if (dataAreBalanced) {
                    dm.whereIsWaldo(143, waldoFile, "Data are Balanced");
                    if (replicatesExist) {
                        dm.whereIsWaldo(145, waldoFile, "Replicates Exist");
                        anova2_RCB_wReplicates_Dashboard = new ANOVA2_RCB_wReplicates_Dashboard(this, anova2_RCB_Model);
                        anova2_RCB_wReplicates_Dashboard.populateTheBackGround();
                        anova2_RCB_wReplicates_Dashboard.putEmAllUp();
                        anova2_RCB_wReplicates_Dashboard.showAndWait();
                    } else {
                        MyAlerts.showNoReplicationInANOVA2Alert();
                    }
                } else { MyAlerts.showUnbalancedRBAlert(); }
                
                break;
                
                default:
                    String switchFailure = "Switch failure: ANOVA2_RCB_Controller 158 " + whichANOVA2;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);                 
        }
        return returnStatus;
    } // TwoFactor CR    
    
    public String getFactorAName() { return factorA_Name; }
    public String getFactorBName() { return factorB_Name;}
    public String getResponseName() { return varLabel;}
    public boolean getDataAreBalanced() { return dataAreBalanced; }
    public boolean getReplicatesExist() { return replicatesExist; }
    public int getNReplications() {return nReplications; }
    public String getReturnStatus() { return returnStatus; }
    public String getWhichANOVA2() { return whichANOVA2; }
    public Data_Manager getDataManager() { return dm; }
}
