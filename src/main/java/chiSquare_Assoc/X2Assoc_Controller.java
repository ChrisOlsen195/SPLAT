/************************************************************
 *                      X2Assoc_Controller                  *
 *                          08/14/24                        *
 *                            09:00                         *
 ***********************************************************/
package chiSquare_Assoc;

import splat.Data_Manager;
import dataObjects.BivariateCategoricalDataObj;
import dataObjects.ColumnOfData;
import dialogs.chisquare.X2Assoc_Dialog;
import java.util.ArrayList;
import utilityClasses.MyAlerts;

public class X2Assoc_Controller {
    // POJOs
    String returnStatus, assocType, xDescr, yDescr;
    
    // Make empty if no-print
    //String waldoFile = "X2Assoc_Controller ";
    String waldoFile = "";
    
    // My classes
    BivariateCategoricalDataObj bivCatDataObj;
    ArrayList<ColumnOfData> outData;
    Data_Manager dm;
    X2Assoc_Model x2Assoc_Model;
    X2Assoc_Dashboard x2Assoc_Dashboard;

    // FX Objects

    public X2Assoc_Controller(Data_Manager dm, String assocType) { 
        this.dm = dm;
        dm.whereIsWaldo(34, waldoFile, "Constructing");
        this.assocType = assocType;
        
        if (assocType.equals("INDEPENDENCE")) {
            assocType = "Chi Square test of Independence";
        }
        
        if (assocType.equals("HOMOGENEITY")) {
            assocType = "Chi Square test of Homogeneity";
        }
    }
    
    public String doAssoc_FromTable() {
        dm.whereIsWaldo(47, waldoFile, "Constructing");
        x2Assoc_Model = new X2Assoc_Model(this, assocType);
        returnStatus = x2Assoc_Model.doModelFromTable();
    
        if (returnStatus.equals("OK")) {
            x2Assoc_Model.doChiSqAnalysisCalculations();
            x2Assoc_Dashboard = new X2Assoc_Dashboard(this, x2Assoc_Model); 
            x2Assoc_Dashboard.populateTheBackGround();
            x2Assoc_Dashboard.putEmAllUp();
            x2Assoc_Dashboard.showAndWait();
            returnStatus = x2Assoc_Dashboard.getReturnStatus();
            return returnStatus;
        }
        else {
            return "Cancel";
        }
    }  

    public String doAssoc_FromFile(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(67, waldoFile, "doAssoc_FromFile");
        returnStatus = "";
        
        // Why is this not changed in the constructor???
        if (assocType.equals("INDEPENDENCE")) {
            assocType = "Chi Square test of Independence";
        }
        
        if (assocType.equals("HOMOGENEITY")) {
            assocType = "Chi Square test of Homogeneity";
        }
        
        X2Assoc_Dialog x2Assoc_Dialog = new X2Assoc_Dialog(dm, "Categorical", "Categorical", assocType);
        x2Assoc_Dialog.showAndWait();

        returnStatus = x2Assoc_Dialog.getReturnStatus();

        if (returnStatus.equals("OK")) {
            xDescr = x2Assoc_Dialog.getPreferredFirstVarDescription();
            yDescr = x2Assoc_Dialog.getPreferredSecondVarDescription();
            bivCatDataObj = new BivariateCategoricalDataObj(dm, xDescr, 
            yDescr, x2Assoc_Dialog.getData());
            outData = new ArrayList();
            outData = bivCatDataObj.getLegalColumns(); // Missing data deleted
            x2Assoc_Model = new X2Assoc_Model(this, assocType);
            returnStatus = x2Assoc_Model.doModelFromFile(); 
            
            if ((x2Assoc_Model.getNumberOfRows() > 12)  ) {
                MyAlerts.showTooManyRowsAlert();
                return "Cancel";
            }

            if (returnStatus.equals("OK")) {
                x2Assoc_Model.doChiSqAnalysisCalculations();
                x2Assoc_Dashboard = new X2Assoc_Dashboard(this, x2Assoc_Model); 
                x2Assoc_Dashboard.populateTheBackGround();
                x2Assoc_Dashboard.putEmAllUp();
                x2Assoc_Dashboard.showAndWait();
                returnStatus = x2Assoc_Dashboard.getReturnStatus();
            }            
        }
        return returnStatus;
    }
    
    public ArrayList<ColumnOfData> getData() { return outData; } 
    public String getXDescr() { return xDescr; }
    public String getYDescr() { return xDescr; }
    public Data_Manager getDataManager() { 
        // if (dmExists)
            return dm; 
    }
}
