/************************************************************
 *                       Epi_Controller                   *
 *                          08/19/24                        *
 *                            00:00                         *
 ***********************************************************/
package epidemiologyProcedures;

import splat.Data_Manager;
import dataObjects.BivariateCategoricalDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import utilityClasses.MyAlerts;

public class Epi_Controller {
    // POJOs
    String strReturnStatus, strAssocType, strXDescr, strYDescr;
    
    // Make empty if no-print
    String waldoFile = "Epi_Controller";
    //String waldoFile = "";
    
    // My classes
    BivariateCategoricalDataObj bivCatDataObj;
    ArrayList<ColumnOfData> col_OutData;
    Data_Manager dm;
    Epi_Model epi_Model;
    Epi_Dashboard epi_Dashboard;
    
    // FX Objects
    
    public Epi_Controller(Data_Manager dm, String assocType) { 
        dm.whereIsWaldo(32, waldoFile, "\nConstructing");
        //System.out.println("33 Epi_Controller, assocType = " + assocType);
        this.strAssocType = assocType;
        this.dm = dm;
        dm.whereIsWaldo(36, waldoFile, "Constructing");
    } 
    
    public String doEpidemiology_FromTable() {
        dm.whereIsWaldo(40, waldoFile, "EpiController, doEpidemiology_FromTable()");
        strReturnStatus = "";
        epi_Model = new Epi_Model(this, strAssocType);
        strReturnStatus = epi_Model.doEpiFromTable();
        
        if (strReturnStatus.equals("OK")) {
            epi_Dashboard = new Epi_Dashboard(this, epi_Model); 
            epi_Dashboard.populateTheBackGround();
            epi_Dashboard.putEmAllUp();
            epi_Dashboard.showAndWait();
            strReturnStatus = epi_Dashboard.getReturnStatus();
            return strReturnStatus;
        }
        else {
            return "Cancel";
        }
    }  

    public String doEpidemiology_FromFile(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(60, waldoFile, "doEpidemiology_FromFile(Data_Manager dm)");
        strReturnStatus = "";
        
        // *****  Choose the two variables  *****
        Epi_AssocDialog epi_Assoc_Dialog = new Epi_AssocDialog(dm, "EPIDEMIOLOGY");
        epi_Assoc_Dialog.showAndWait();
        strReturnStatus = epi_Assoc_Dialog.getReturnStatus();
   
        //  Change this to if-else?
        switch (strReturnStatus) {
            case "OK":    
                strXDescr = epi_Assoc_Dialog.getPreferredFirstVarDescription();
                strYDescr = epi_Assoc_Dialog.getPreferredSecondVarDescription();
                bivCatDataObj = new BivariateCategoricalDataObj(dm, strXDescr, strYDescr, epi_Assoc_Dialog.getData());
                col_OutData = new ArrayList();
                col_OutData = bivCatDataObj.getLegalColumns(); // Missing data deleted
                epi_Model = new Epi_Model(this, strAssocType);
                
                strReturnStatus = epi_Model.doEpiFromFile();
                if (strReturnStatus.equals("OK")) {
                    epi_Dashboard = new Epi_Dashboard(this, epi_Model); 
                    epi_Dashboard.populateTheBackGround();
                    epi_Dashboard.putEmAllUp();
                    epi_Dashboard.showAndWait();
                    strReturnStatus = epi_Dashboard.getReturnStatus();
                }
                break;
                
            case "Cancel":
                break; 
                
            default:
                String switchFailure = "Switch failure: Epi_Controller 92 " + strReturnStatus;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
            }
        return strReturnStatus;
    }

    public ArrayList<ColumnOfData> getData() { return col_OutData; }    
    public Data_Manager getDataManager() { return dm; }
}


