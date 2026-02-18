/************************************************************
 *                    UnivCat_Controller                    *
 *                          05/11/25                        *
 *                            18:00                         *
 ***********************************************************/
package univariateProcedures_Categorical;

import dataObjects.ColumnOfData;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class UnivCat_Controller {
    // POJOs
    String strReturnStatus; 
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
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
        dm.whereIsWaldo(32, waldoFile, " *** UnivCat_Controller, constructing");
    }
    
    public String doUnivCat_FromFileData(Data_Manager dm) {
        dm.whereIsWaldo(36, waldoFile, "UnivCat_Controller, doUnivCat_FromFileData(Data_Manager dm)");
        this.dm = dm;
        int casesInStruct = dm.getNCasesInStruct();
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }
        strReturnStatus = "OK";
        univCat_Model = new UnivCat_Model();
        strReturnStatus = univCat_Model.doUnivCat_FromFile(this);
        if (printTheStuff) {
            System.out.println("48 --- UnivCat_Controller, strReturnStatus = " + strReturnStatus);
        }  
        if (!strReturnStatus.equals("OK")) { return "Cancel"; }
        switch(strReturnStatus) {
            case "OK":
                univCat_Dashboard = new UnivCat_Dashboard(this, univCat_Model);
                univCat_Dashboard.populateTheBackGround();
                univCat_Dashboard.putEmAllUp();
                univCat_Dashboard.showAndWait();
                strReturnStatus = univCat_Dashboard.getStrReturnStatus();
                if (printTheStuff) {
                    System.out.println("59 --- UnivCat_Controller, strReturnStatus = " + strReturnStatus);
                } 
                break;
                
            case "Cancel":
                dm.setDataAreClean(false);
                MyAlerts.showDataHaveBeenCleanedAlert();
                break;
                
            default:
                String switchFailure = "Switch failure: UnivCat_Controller 69 " + strReturnStatus;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        return strReturnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }    
    public String getDescriptionOfVariable() { return univCat_Model.getDescriptionOfVariable(); }    
    public ColumnOfData getColumnOfData() { return columnOfData; }
}

