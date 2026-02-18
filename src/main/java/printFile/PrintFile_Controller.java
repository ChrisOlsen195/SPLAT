/************************************************************
 *                    PrintFile_Controller                  *
 *                          03/01/25                        *
 *                            18:00                         *
 ***********************************************************/
package printFile;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import splat.Data_Manager;

public class PrintFile_Controller {
    // POJOs
    
    int nVarsChosen;
    String returnStatus;
    
    // String waldoFile = "PrintFile_Controller";
    String waldoFile = "";
    
    // My classes
    PrintFile_ChooseVars_Dialog printFile_ChooseVars_Dialog;
    PrintFile_Dashboard printFile_Dashboard;
    Data_Manager dm;
    PrintFile_Model printFile_Model;
    ArrayList<ColumnOfData> data;    
    // POJOs / FX
    
    public PrintFile_Controller() { }
    
    public PrintFile_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(33, waldoFile, "Constructing");
        printFile_Model = new PrintFile_Model(dm, this);  
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(38, waldoFile, "doTheProcedure()");
        try {
            printFile_ChooseVars_Dialog = new PrintFile_ChooseVars_Dialog(dm);
            printFile_ChooseVars_Dialog.showAndWait();          
            returnStatus = printFile_ChooseVars_Dialog.getStrReturnStatus();
            
            if (returnStatus.equals("Cancel")) { return "Cancel"; }
            data = new ArrayList<>();
            data = printFile_ChooseVars_Dialog.getData();
            nVarsChosen = data.size();
            printFile_Model.printFile();
            printFile_Dashboard = new PrintFile_Dashboard(this, printFile_Model);
            printFile_Dashboard.populateTheBackGround();
            printFile_Dashboard.putEmAllUp();
            printFile_Dashboard.showAndWait();
            returnStatus = printFile_Dashboard.getStrReturnStatus();
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
    
    public ColumnOfData getIthColumn(int ith) { return data.get(ith); }
    public int getNVarsChosen() { return nVarsChosen; }
    public ArrayList<ColumnOfData> getData() { return data; }
}
