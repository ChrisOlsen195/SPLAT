/**************************************************
 *            ANOVA2_RM_Controller                *
 *                  11/25/25                      *
 *                   12:00                        *
 *            Tested against Cohen, p513          *
 *                  02/12/24                      *
 *************************************************/
package anova2;

import dataObjects.CategoricalDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import splat.*;
import utilityClasses.*;

public class ANOVA2_RM_Controller {
    
    // POJOs
    boolean dataAreBalanced, replicatesExist, fileStructureIsOK;
    
    int nMeasures, nSubjects;
        
    String strReturnStatus, strFactorA_Name, strFactorB_Name, strVarLabel, 
           strVarDescription;
    
    String waldoFile = "";
    //String waldoFile = "ANOVA2_RM_Controller";
    
    // My classes
    ANOVA2_RM_Dashboard anova2_RM_Dashboard;
    ANOVA2_RM_Model anova2_RM_Model;
    ANOVA2_RM_Calculations anova2_RM_Calculations;
    Data_Manager dm;
            
    public ANOVA2_RM_Controller (Data_Manager dm) {
        this.dm = dm; 
        dm.whereIsWaldo(37, waldoFile, "Constructing");
        
        int casesInStruct = dm.getNCasesInStruct();
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
        } else {    
            nMeasures = dm.getNVarsInStruct() - 1;
            nSubjects= dm.getNCasesInStruct();
            anova2_RM_Calculations = new ANOVA2_RM_Calculations(dm, this);
            fileStructureIsOK = anova2_RM_Calculations.checkTheFileStructure();
            if (fileStructureIsOK) {
                anova2_RM_Calculations.doTheRMStuff();
            }
            else { 
                MyAlerts.showBadRMFileStructureAlert(); 
            }
        }
    }
        
    public String doTheANOVA2() {
        dm.whereIsWaldo(57, waldoFile, "doTheANOVA2()");
        strReturnStatus = "OK";
        dataAreBalanced = anova2_RM_Calculations.getDesignIsBalanced();
        replicatesExist = anova2_RM_Calculations.getThereAreReplications();

        strFactorA_Name = "Subjects";
        strFactorB_Name = "Treatments";   
        strVarLabel = anova2_RM_Calculations.getResponse_Name(); 

        ColumnOfData colSubj = anova2_RM_Calculations.getCol_Subjects();
        ColumnOfData colTreat = anova2_RM_Calculations.getCol_Treatments();   
        ColumnOfData colResp = anova2_RM_Calculations.getCol_Responses(); 
        CategoricalDataVariable cdv_Treatments = new CategoricalDataVariable(strFactorB_Name, colTreat);
        CategoricalDataVariable cdv_Subjects = new CategoricalDataVariable(strFactorA_Name, colSubj);  

        nSubjects = dm.getNCasesInStruct();
        anova2_RM_Calculations.doLocallyBestInvariant();

        anova2_RM_Calculations.doMauchlyTest();
        QuantitativeDataVariable qdv_Response = new QuantitativeDataVariable(strVarLabel, strVarDescription, colResp); 

        anova2_RM_Model = new ANOVA2_RM_Model( dm,
                                   this, 
                                   cdv_Subjects,
                                   cdv_Treatments,
                                   qdv_Response); 

        strReturnStatus = anova2_RM_Model.doTwoWayANOVA();
        
        if (!strReturnStatus.equals("OK")) {  return strReturnStatus;  }

        anova2_RM_Calculations.adjustTheDistributions();
        anova2_RM_Model.print_ANOVA2_Results_1();
        anova2_RM_Model.print_ANOVA2_Results_2();                       
        anova2_RM_Dashboard = new ANOVA2_RM_Dashboard(this, anova2_RM_Model);
        
        if (dataAreBalanced) {
            anova2_RM_Dashboard = new ANOVA2_RM_Dashboard(this, anova2_RM_Model);
            anova2_RM_Dashboard.populateTheBackGround();
            anova2_RM_Dashboard.putEmAllUp();
            anova2_RM_Dashboard.showAndWait();
        } else { MyAlerts.showUnbalancedRBAlert(); } 

        return strReturnStatus;
    } 
   
    public ANOVA2_RM_Model get_RM_Model() { return anova2_RM_Model; }
    public ANOVA2_RM_Calculations get_RM_Calculations() {return anova2_RM_Calculations; }
    public int getNMeasures() { return nMeasures; }
    public int getNSubjects() { return nSubjects; }
    public boolean getDataAreBalanced() { return dataAreBalanced; }
    public boolean getReplicatesExist() { return replicatesExist; }
    public boolean getFileStructureIsOK() { return fileStructureIsOK; }
}
