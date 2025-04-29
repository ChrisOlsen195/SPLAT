/************************************************************
 *                       X2GOF_Controller                   *
 *                          01/15/25                        *
 *                            12:00                         *
 ***********************************************************/
package chiSquare.GOF;

import dialogs.chisquare.X2GOF_ChooseVariable;
import dataObjects.ColumnOfData;
import splat.Data_Manager;

public class X2GOF_Controller {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    String returnStatus;   
    
    // Make empty if no-print
    //String waldoFile = "X2GOF_Controller";
    String waldoFile = "";
    
    // My classes
    ColumnOfData columnOfData;
    Data_Manager dm;
    X2GOF_Model x2GOF_Model;
    X2GOF_Dashboard x2GOF_Dashboard;
    
    // POJOs / FX

    public X2GOF_Controller() { 
        if (printTheStuff == true) {
            System.out.println("34 *** X2GOF_Controller(), Constructing");
        }
    }
    
    public String doGOF_ByHand() {
        if (printTheStuff == true) {
            System.out.println("40 --- X2GOF_Controller, doGOF_ByHand()");
        }
        returnStatus = "OK";
        x2GOF_Model = new X2GOF_Model();
        returnStatus = x2GOF_Model.analyzeGOF_DataByHand();
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
        this.dm = dm;
        dm.whereIsWaldo(49, waldoFile, "doGOF_FromFileData");
        X2GOF_ChooseVariable x2GOF_Dialog = new X2GOF_ChooseVariable(dm, "Categorical");
        x2GOF_Dialog.showAndWait();
        returnStatus = x2GOF_Dialog.getReturnStatus();
        
        if (returnStatus.equals("OK")) {
            columnOfData = x2GOF_Dialog.getData();
            columnOfData.cleanTheColumn(dm, x2GOF_Dialog.getVarIndex());
            x2GOF_Model = new X2GOF_Model();
            returnStatus = x2GOF_Model.analyzeGOF_DataFromFile(this);
            if (returnStatus.equals("OK")) {
                x2GOF_Dashboard = new X2GOF_Dashboard(this, x2GOF_Model);
                x2GOF_Dashboard.populateTheBackGround();
                x2GOF_Dashboard.putEmAllUp();
                x2GOF_Dashboard.showAndWait();
                returnStatus = x2GOF_Dashboard.getReturnStatus();
            }            
        }
        dm.whereIsWaldo(75, waldoFile, "  *** END doGOF_FromFileData(Data_Manager dm)");
        return returnStatus;
    }
    
    public ColumnOfData getColumnOfData() { return columnOfData; }
}
