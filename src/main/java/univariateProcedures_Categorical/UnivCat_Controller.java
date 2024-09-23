/************************************************************
 *                    UnivCat_Controller                    *
 *                          10/15/23                        *
 *                            12:00                         *
 ***********************************************************/
package univariateProcedures_Categorical;

import dataObjects.ColumnOfData;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class UnivCat_Controller {
    // POJOs
    String returnStatus;   
    
    //String waldoFile = "UnivCat_Controller";
    String waldoFile = "";  
    
    // My classes
    private ColumnOfData columnOfData;
    Data_Manager dm;
    UnivCat_Model univCat_Model;
    UnivCat_Dashboard univCat_Dashboard;
    
    // POJOs / FX

    public UnivCat_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(29, waldoFile, "\nUnivCat_Controller, Constructing");
    }
    
    public String doUnivCat_FromFileData(Data_Manager dm) {
        dm.whereIsWaldo(33, waldoFile, "UnivCat_Controller, doUnivCat_FromFileData(Data_Manager dm)");
        this.dm = dm;
        returnStatus = "";
        univCat_Model = new UnivCat_Model();
        returnStatus = univCat_Model.doUnivCat_FromFile(this);
        
        switch(returnStatus) {
            case "OK":
                univCat_Dashboard = new UnivCat_Dashboard(this, univCat_Model);
                univCat_Dashboard.populateTheBackGround();
                univCat_Dashboard.putEmAllUp();
                univCat_Dashboard.showAndWait();
                returnStatus = univCat_Dashboard.getReturnStatus();
                break;
                
            case "Cancel":
                dm.setDataAreClean(false);
                MyAlerts.showDataHaveBeenCleanedAlert();
                break;
                
            default:
                String switchFailure = "Switch failure: UnivCat_Controller 54 " + returnStatus;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        return returnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }    
    public String getDescriptionOfVariable() { return univCat_Model.getDescriptionOfVariable(); }    
    public ColumnOfData getColumnOfData() { return columnOfData; }
}

