/************************************************************
 *                     MultReg_Controller                   *
 *                          10/15/23                        *
 *                            12:00                         *
 ***********************************************************/
package multipleRegression;

import dataObjects.MultiVariateContinDataObj;
import dialogs.regression.MultReg_Dialog;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class MultReg_Controller {
    // POJOs
    String returnStatus, theYVariable;
    
    //String waldoFile = "MultReg_Controller";
    String waldoFile = "";
    
    // My classes
    MultiVariateContinDataObj multVarContinObj;
    MultReg_Model multRegModel;
    MultReg_Dashboard multRegDashboard;
    Data_Manager dm;
    ArrayList<ColumnOfData> data;    
    // POJOs / FX
    
    public MultReg_Controller() { }
    
    public MultReg_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(34, waldoFile, "Constructing");
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(38, waldoFile, "doTheProcedure()");
        try {
            MultReg_Dialog multReg_Dialog = new MultReg_Dialog(dm);
            multReg_Dialog.showAndWait();          
            returnStatus = multReg_Dialog.getReturnStatus();
            
            if (returnStatus.equals("Cancel")) { return "Cancel"; }
            data = new ArrayList<>();
            theYVariable = multReg_Dialog.getYVariable();
            data = multReg_Dialog.getData();
            if (data.size() < 3) {
                MyAlerts.showMultReg_LT3_ThreeVariablesAlert();
                return "Cancel";                
            }
            
            multVarContinObj = new MultiVariateContinDataObj(dm, data);
            multRegModel = new MultReg_Model(dm, this, multVarContinObj);
            multRegModel.setupRegressionAnalysis();
            multRegModel.doRegressionAnalysis();
            multRegModel.printStatistics();

            multRegDashboard = new MultReg_Dashboard(this, multRegModel);
            multRegDashboard.populateTheBackGround();
            multRegDashboard.putEmAllUp();
            multRegDashboard.showAndWait();
            returnStatus = multRegDashboard.getReturnStatus();
            return returnStatus;  
        }
        catch(Exception ex) {
            // ex.printStackTrace();  ?? p466 Liang
            System.out.println("\n" + ex.getMessage());
            System.out.println("\n" + ex.toString());
            System.out.println("\nTrace Info Obtained from getStackTrace");
            StackTraceElement[] traceElements = ex.getStackTrace();
            
            for (StackTraceElement traceElement : traceElements) {
                System.out.print("\nmethod " + traceElement.getMethodName());
                System.out.print("(" + traceElement.getClassName() + ":");
                System.out.print(traceElement.getLineNumber() + ")");
            }             
        }
        return returnStatus;
    }
    
    public String getYVariable() { return theYVariable; }
}
