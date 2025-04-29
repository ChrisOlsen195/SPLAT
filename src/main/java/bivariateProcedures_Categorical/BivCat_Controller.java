/************************************************************
 *                      BivCat_Controller                  *
 *                          03/22/25                        *
 *                            21:00                         *
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
        int casesInStruct = dm.getNCasesInStruct();
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }
        strReturnStatus = "OK";
        
        BivCat_Dialog bivCat_Dialog = new BivCat_Dialog(dm, "BivCatAssocFromFile");
        bivCat_Dialog.showAndWait();

        strReturnStatus = bivCat_Dialog.getReturnStatus();
        if (strReturnStatus.equals(OK)) {
            xDescr = bivCat_Dialog.getXLabel();
            yDescr = bivCat_Dialog.getYLabel();
            System.out.println("73 BivCat_Controller, x/yDescr = " + xDescr + " / " + yDescr);
            bivCatDataObj = new BivariateCategoricalDataObj(dm, xDescr, 
            yDescr, bivCat_Dialog.getData());
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
