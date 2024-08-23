/************************************************************
 *                       X2GOF_Controller                   *
 *                          05/25/24                        *
 *                            15:00                         *
 ***********************************************************/
package chiSquare.GOF;

import dialogs.chisquare.X2GOF_Dialog;
import dataObjects.ColumnOfData;
import splat.Data_Manager;

public class X2GOF_Controller {
    // POJOs
    String returnStatus;   
    
    // Make empty if no-print
    String waldoFile = "X2GOF_Controller";
    //String waldoFile = "";
    
    // My classes
    ColumnOfData columnOfData;
    //Data_Manager dm;
    X2GOF_Model x2GOF_Model;
    X2GOF_Dashboard x2GOF_Dashboard;
    
    // POJOs / FX

    public X2GOF_Controller() { }
    
    public String doGOF_FromCounts() {
        System.out.println("31 X2GOFModel, doGOF_FromCounts()");
        returnStatus = "OK";
        x2GOF_Model = new X2GOF_Model();
        returnStatus = x2GOF_Model.doX2FromTable();
        if (returnStatus.equals("OK")) {
            x2GOF_Dashboard = new X2GOF_Dashboard(this, x2GOF_Model);
            x2GOF_Dashboard.populateTheBackGround();
            x2GOF_Dashboard.putEmAllUp();
            x2GOF_Dashboard.showAndWait();
            returnStatus = x2GOF_Dashboard.getReturnStatus();
        }
        return returnStatus;
    }
    
    public String doGOF_FromFileData(Data_Manager dm) {
        //this.dm = dm;
        dm.whereIsWaldo(47, waldoFile, "doGOF_FromFileData");
        X2GOF_Dialog x2GOF_Dialog = new X2GOF_Dialog(dm, "Categorical");
        x2GOF_Dialog.showAndWait();
        returnStatus = x2GOF_Dialog.getReturnStatus();
        
        if (returnStatus.equals("OK")) {
            columnOfData = x2GOF_Dialog.getData();
            columnOfData.toString();
            columnOfData.cleanTheColumn(dm, x2GOF_Dialog.getVarIndex());
            columnOfData.toString();
            dm.whereIsWaldo(57, waldoFile, "doGOF_FromFileData");
            x2GOF_Model = new X2GOF_Model();
            returnStatus = x2GOF_Model.doX2FromFile(this);
            
            if (returnStatus.equals("OK")) {
                dm.whereIsWaldo(62, waldoFile, "doGOF_FromFileData");
                x2GOF_Dashboard = new X2GOF_Dashboard(this, x2GOF_Model);
                x2GOF_Dashboard.populateTheBackGround();
                x2GOF_Dashboard.putEmAllUp();
                x2GOF_Dashboard.showAndWait();
                returnStatus = x2GOF_Dashboard.getReturnStatus();
            }            
        }
        return returnStatus;
    }
    
    public ColumnOfData getColumnOfData() { return columnOfData; }
}
