/************************************************************
 *                      BivCat_Controller                  *
 *                          08/19/24                        *
 *                            00:00                         *
 ***********************************************************/
package bivariateProcedures_Categorical;

import splat.Data_Manager;
import dataObjects.BivariateCategoricalDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import utilityClasses.MyAlerts;

public class BivCat_Controller {
    // POJOs
    String strReturnStatus, assocType, xDescr, yDescr;
    final String OK;
    
    // Make empty if no-print
    //String waldoFile = "BivCat_Controller ";
    String waldoFile = "";
    
    // My classes
    BivariateCategoricalDataObj bivCatDataObj;
    ArrayList<ColumnOfData> outData;
    Data_Manager dm;
    BivCat_Model bivCat_Model;
    BivCat_Dashboard bivCat_Dashboard;

    // FX Objects

    public BivCat_Controller(Data_Manager dm, String assocType) { 
        this.dm = dm;
        OK = "OK";
        dm.whereIsWaldo(35, waldoFile, "\nConstructing");       
    }
    
    public String doAssoc_FromTable() {
        dm.whereIsWaldo(39, waldoFile, "\nConstructing");
        bivCat_Model = new BivCat_Model(this, assocType);
        strReturnStatus = bivCat_Model.doBivCatModelFromTable();
        if (strReturnStatus.equals(OK)) {
            bivCat_Dashboard = new BivCat_Dashboard(this, bivCat_Model); 
            bivCat_Dashboard.populateTheBackGround();
            bivCat_Dashboard.putEmAllUp();
            bivCat_Dashboard.showAndWait();
            strReturnStatus = bivCat_Dashboard.getReturnStatus();
            return strReturnStatus;
        }
        else {
            return "Cancel";
        }
    }  

    public String doAssoc_FromFile(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(57, waldoFile, "doAssoc_FromFile");
        strReturnStatus = "";
        
        BivCat_Dialog x2Assoc_Dialog = new BivCat_Dialog(dm, "BivCatAssocFromFile");
        x2Assoc_Dialog.showAndWait();

        strReturnStatus = x2Assoc_Dialog.getReturnStatus();
        if (strReturnStatus.equals(OK)) {
            xDescr = x2Assoc_Dialog.getPreferredFirstVarDescription();
            yDescr = x2Assoc_Dialog.getPreferredSecondVarDescription();
            bivCatDataObj = new BivariateCategoricalDataObj(dm, xDescr, 
            yDescr, x2Assoc_Dialog.getData());
            outData = new ArrayList();
            outData = bivCatDataObj.getLegalColumns(); // Missing data deleted
            bivCat_Model = new BivCat_Model(this, assocType);
            strReturnStatus = bivCat_Model.doBivCatModelFromFile(); 
            
            if ((bivCat_Model.getNumberOfRows() > 12)  ) {
                MyAlerts.showTooManyRowsAlert();
                return "Cancel";
            }

            if (strReturnStatus.equals(OK)) {
                bivCat_Model.calculateTheProportions();
                bivCat_Dashboard = new BivCat_Dashboard(this, bivCat_Model); 
                bivCat_Dashboard.populateTheBackGround();
                bivCat_Dashboard.putEmAllUp();
                bivCat_Dashboard.showAndWait();
                strReturnStatus = bivCat_Dashboard.getReturnStatus();
            }            
        }
        return strReturnStatus;
    }
    
    public ArrayList<ColumnOfData> getData() { return outData; } 
    public String getXDescr() { return xDescr; }
    public String getYDescr() { return xDescr; }
    public Data_Manager getDataManager() { 
        // if (dmExists)
            return dm; 
    }
}
