/************************************************************
 *                           EditOps                        *
 *                          12/17/25                        *
 *                            00:00                         *
 ***********************************************************/
package splat;

import dialogs.CleanAColumn_Dialog;
import dialogs.InsertOrDeleteColumn_Dialog;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;
import dataObjects.*;
import utilityClasses.*;

public class Edit_Ops {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;    
    
    int indexOfVar;
    
    String strReturnStatus;
    
    // My classes
    Data_Manager dm;

    public Edit_Ops(Data_Manager dm) { 
        this.dm = dm; 
        if (printTheStuff) {
            System.out.println("*** 30 Edit Ops, Constructing");
        }
    }
    
    public void insertRow() {
        if (printTheStuff) {
            System.out.println("--- 36 Edit Ops, insertRow()");
        }
        if (dm.getTheFile() == null) {
            MyAlerts.showAintGotNoDataAlert();
            //return;
        } else {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Insert a Row");
            dialog.setHeaderText("Wow, I can insert a row! Way cool!");
            dialog.setContentText("Row#:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                dm.insertARow(result.get());
                dm.setDataAreClean(false);
            });
        }
    }
    
    public void deleteRow() {
        if (printTheStuff) {
            System.out.println("--- 56 Edit Ops, deleteRow()");
        }
        if (dm.getTheFile() == null) {
            MyAlerts.showAintGotNoDataAlert();
            //return;
        } else {
            String txtResult;
            TextInputDialog txtInputDialog = new TextInputDialog("");
            txtInputDialog.setTitle("Delete a Row");
            txtInputDialog.setHeaderText("Wow, I can delete a row! Way cool!");
            txtInputDialog.setContentText("Row#:");
            Optional<String> result = txtInputDialog.showAndWait();        
            if  (result.isPresent()) {
                txtResult = txtInputDialog.getEditor().getText();            
                if (!DataUtilities.strIsAPosInt(txtResult)) {
                    MyAlerts.showGenericBadNumberAlert(" a positive integer ");
                }
                else {
                    dm.deleteARow(txtResult);
                    dm.setDataAreClean(false);
                }
            }   
        }
    }
    
    public void insertColumn() {
        if (printTheStuff) {
            System.out.println("--- 83 Edit Ops, insertColumn()");
        }
        InsertOrDeleteColumn_Dialog inOrOut = new InsertOrDeleteColumn_Dialog(dm, "INSERT");
        inOrOut.showAndWait();
        String returnStatus = inOrOut.getStrReturnStatus();        
        if (returnStatus.equals("OK")) {
            indexOfVar = inOrOut.getIndexOfVariable();
            String descrOfVar = inOrOut.getDescriptionOfVariable();            
            CheckForDuplicateStrings check4DupLabels = new CheckForDuplicateStrings(dm.getVariableNames(), descrOfVar);
            String haveDups = check4DupLabels.CheckTheStrings();            
            if (!haveDups.equals("OK")) { 
                MyAlerts.showDuplicateLabelAttemptedAlert(); 
                return;
            }
            
            dm.insertAColumn(indexOfVar, descrOfVar);
            dm.setDataAreClean(false);
        }
    }
    
    public void insertColumn(ColumnOfData colOfData) {
        if (printTheStuff) {
            System.out.println("--- 105 Edit Ops, insertColumn(ColumnOfData colOfData)");
        }
        InsertOrDeleteColumn_Dialog inOrOut = new InsertOrDeleteColumn_Dialog(dm, "INSERT");
        inOrOut.showAndWait();
        String returnStatus = inOrOut.getStrReturnStatus();        
        if (returnStatus.equals("OK")) {
            indexOfVar = inOrOut.getIndexOfVariable();
            String descrOfVar = inOrOut.getDescriptionOfVariable();            
            CheckForDuplicateStrings check4DupLabels = new CheckForDuplicateStrings(dm.getVariableNames(), descrOfVar);
            String haveDups = check4DupLabels.CheckTheStrings();            
            if (!haveDups.equals("OK")) { 
                MyAlerts.showDuplicateLabelAttemptedAlert(); 
                return;
            }
            
            dm.insertAColumn(indexOfVar, descrOfVar);
            dm.setDataAreClean(false);
        }
    }

    //  Seems to work if nVars > maxVarsInGrid, but not if not
    public void deleteColumn() {
        if (printTheStuff) {
            System.out.println("--- 128 Edit Ops, deleteColumn()");
        }
        InsertOrDeleteColumn_Dialog inOrOut = new InsertOrDeleteColumn_Dialog(dm, "DELETE");
        inOrOut.showAndWait();
        String returnStatus = inOrOut.getStrReturnStatus();        
        if (returnStatus.equals("OK")) {
            indexOfVar = inOrOut.getIndexOfVariable();
            dm.deleteAColumn(indexOfVar);
            dm.setDataAreClean(false);
        }
    }
    
    public String cleanDataInColumn() {
       if (printTheStuff) {
            System.out.println("--- 142 Edit Ops, deleteColumn()");
        }
       strReturnStatus = "OK";
        CleanAColumn_Dialog cleanData_Dialog = new CleanAColumn_Dialog(dm, "Either");
        cleanData_Dialog.showAndWait();
        if (!strReturnStatus.equals("OK")) { return strReturnStatus; }
        int col = cleanData_Dialog.getVarIndex();
        ColumnOfData col_x = dm.getAllTheColumns().get(col);
        col_x.cleanTheColumn(dm, col);
        return strReturnStatus;
    }
    
    public int getIndexOfVar() { return indexOfVar; }
    
    public String getStrReturnStatus() { return strReturnStatus; }
    public void setStrReturnStatus(String toThis) { strReturnStatus = toThis; }
} 

