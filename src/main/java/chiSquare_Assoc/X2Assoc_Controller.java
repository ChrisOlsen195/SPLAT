/************************************************************
 *                      X2Assoc_Controller                  *
 *                          12/11/25                        *
 *                            12:00                         *
 ***********************************************************/
package chiSquare_Assoc;

import splat.Data_Manager;
import dataObjects.BivariateCategoricalDataObj;
import dataObjects.ColumnOfData;
import dialogs.Two_Variables_Dialog;
import java.util.ArrayList;
import utilityClasses.MyAlerts;

public class X2Assoc_Controller {
    // POJOs

    String returnStatus, assocType, xDescr, yDescr;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
    BivariateCategoricalDataObj bivCatDataObj;
    ArrayList<ColumnOfData> outData;
    Data_Manager dm;
    X2Assoc_Model x2Assoc_Model;
    X2Assoc_Dashboard x2Assoc_Dashboard;

    // FX Objects

    public X2Assoc_Controller(Data_Manager dm, String assocType) { 
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 35 X2Assoc_Controller, Constructing ");
        }
        this.assocType = assocType;
        
        if (assocType.equals("INDEPENDENCE")) {
            assocType = "INDEPENDENCE";
        }
        
        if (assocType.equals("HOMOGENEITY")) {
            assocType = "HOMOGENEITY";
        }
    }
    
    public String doAssoc_FromTable() {
        if (printTheStuff == true) {
            System.out.println("50 --- X2Assoc_Controller, doAssoc_FromTable() ");
        }
        x2Assoc_Model = new X2Assoc_Model(this, assocType);
        returnStatus = x2Assoc_Model.doModelFromTable();
    
        if (returnStatus.equals("OK")) {
            x2Assoc_Model.doChiSqAnalysisCalculations();
            x2Assoc_Dashboard = new X2Assoc_Dashboard(this, x2Assoc_Model); 
            x2Assoc_Dashboard.populateTheBackGround();
            x2Assoc_Dashboard.putEmAllUp();
            x2Assoc_Dashboard.showAndWait();
            returnStatus = x2Assoc_Dashboard.getStrReturnStatus();
            return returnStatus;
        }
        else {
            return "Cancel";
        }
    }  

    public String doAssoc_FromFile(Data_Manager dm) {
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("72 --- X2Assoc_Controller, doAssoc_FromFile(Data_Manager dm) ");
        }
        returnStatus = "";
        
        // Why is this not changed in the constructor???
        if (assocType.equals("INDEPENDENCE")) {
            assocType = "INDEPENDENCE";
        }
        
        if (assocType.equals("HOMOGENEITY")) {
            assocType = "HOMOGENEITY";
        }
        
        Two_Variables_Dialog x2Assoc_Dialog = new Two_Variables_Dialog(dm, "X2Assoc_Dialog", assocType);
        x2Assoc_Dialog.showAndWait();

        returnStatus = x2Assoc_Dialog.getStrReturnStatus();

        if (returnStatus.equals("OK")) {
        if (printTheStuff == true) {
            System.out.println("92 --- X2Assoc_Controller, doAssoc_FromFile(Data_Manager dm) ");
        }
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
        if (printTheStuff == true) {
            System.out.println("110 --- X2Assoc_Controller, doAssoc_FromFile(Data_Manager dm) ");
        }
                x2Assoc_Model.doChiSqAnalysisCalculations();
                x2Assoc_Dashboard = new X2Assoc_Dashboard(this, x2Assoc_Model); 
                x2Assoc_Dashboard.populateTheBackGround();
                x2Assoc_Dashboard.putEmAllUp();
                x2Assoc_Dashboard.showAndWait();
                returnStatus = x2Assoc_Dashboard.getStrReturnStatus();
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
