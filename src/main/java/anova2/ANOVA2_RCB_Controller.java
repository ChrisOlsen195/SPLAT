/**************************************************
 *            ANOVA2_RCB_Controller               *
 *                  05/24/24                      *
 *                   06:00                        *
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

    String waldoFile = "";
    //String waldoFile = "ANOVA2_Controller";
    
    // My classes
    ANOVA2_RCB_Dashboard anova2_RCB_Dashboard;
    ANOVA2_Factorial_Model anova2_Factorial_Model;
    ANOVA2_RCB_wReplicates_Dashboard anova2_RCB_wReplicates_Dashboard;
    ANOVA2_RCB_Model anova2_RCB_Model;
    ANOVA2_RCB_Dialog anova2_RCB_Dialog;
    Data_Manager dm;
            
    public ANOVA2_RCB_Controller (Data_Manager dm, String whichANOVA2) {
        this.whichANOVA2 = whichANOVA2;
        this.dm = dm; 
        dm.whereIsWaldo(41, waldoFile, "Constructing");
        anova2_RCB_Dialog = new ANOVA2_RCB_Dialog(dm, whichANOVA2);
        anova2_RCB_Dialog.doTheDialog();
    }
        
public String doTheANOVA2() {
        dm.whereIsWaldo(47, waldoFile, "doTheANOVA2()");
        returnStatus = "OK";
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            // showAintGotNoDataAlert(); -- Alert would already be shown in Step0
            return "Cancel";
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
        String[] rowVar= new String[nDataPoints];
        ArrayList<String> responseVar= new ArrayList();

        String asterisk = "*";
        int ithDataIndex = 0;
        
        for (int i = 0; i < nLevelsA; i++) { // Rows -- levels of A            
            for (int j = 0; j < nLevelsB; j++) { // Columns -- levels of B
                nReplications = data[j][i].size();
                for (int k = 0; k < nReplications; k++) {
                    rowVar[ithDataIndex] = factorA_Levels[i];
                    columnVar[ithDataIndex] = factorB_Levels[j];
                    String dataValue = data[j][i].get(k);
                    
                    if (dataValue.equals(asterisk)) { dataAreMissing = true; }
                    responseVar.add(dataValue);
                    ithDataIndex++;      
                }               
            } // j loop            
        } // i loop 

        CategoricalDataVariable cdv_FactorA = new CategoricalDataVariable(factorB_Name, columnVar);
        CategoricalDataVariable cdv_FactorB = new CategoricalDataVariable(factorA_Name, rowVar);  
        QuantitativeDataVariable qdv_Response = new QuantitativeDataVariable(varLabel, varDescription, responseVar); 
        
        switch (whichANOVA2) {
            case "Factorial":
                System.out.println("112 ANOVA2_RCB_Controller -- (factorial) switch attained error?!?!?!?");
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
                    dm.whereIsWaldo(140, waldoFile, "Data are Balanced");
                    if (replicatesExist) {
                        dm.whereIsWaldo(142, waldoFile, "Replicates Exist");
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
                    String switchFailure = "Switch failure: ANOVA2_RCB_Controller 155 " + whichANOVA2;
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
