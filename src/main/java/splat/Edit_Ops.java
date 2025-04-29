/************************************************************
 *                           EditOps                        *
 *                          11/11/23                        *
 *                            12:00                         *
 ***********************************************************/
package splat;

import dialogs.CleanAColumn_Dialog;
import dialogs.InsertOrDeleteColumn_Dialog;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;
import dataObjects.*;
import utilityClasses.*;

public class Edit_Ops {
    
    int indexOfVar;
    
    Data_Manager dm;

    public Edit_Ops(Data_Manager dm) { 
        this.dm = dm; 
        //System.out.println("23 Edit_Ops, constructing");
    }
    
    public void insertRow() {
        if (dm.getFileName() == null) {
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
        if (dm.getFileName() == null) {
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
        System.out.println("60 Edit_Ops, insertColumn");
        InsertOrDeleteColumn_Dialog inOrOut = new InsertOrDeleteColumn_Dialog(dm, "INSERT");
        inOrOut.showAndWait();
        String returnStatus = inOrOut.getReturnStatus();        
        if (returnStatus.equals("OK")) {
            indexOfVar = inOrOut.getIndexOfVariable();
            System.out.println("64 Edit_Ops, indexOfVar = " + indexOfVar);
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
        System.out.println("81 Edit_Ops, insertColumn(col)");
        InsertOrDeleteColumn_Dialog inOrOut = new InsertOrDeleteColumn_Dialog(dm, "INSERT");
        inOrOut.showAndWait();
        String returnStatus = inOrOut.getReturnStatus();        
        if (returnStatus.equals("OK")) {
            indexOfVar = inOrOut.getIndexOfVariable();
            System.out.println("87 Edit_Ops, indexOfVar = " + indexOfVar);
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
        System.out.println("103 Edit_Ops, deleteColumn");
        InsertOrDeleteColumn_Dialog inOrOut = new InsertOrDeleteColumn_Dialog(dm, "DELETE");
        inOrOut.showAndWait();
        String returnStatus = inOrOut.getReturnStatus();        
        if (returnStatus.equals("OK")) {
            indexOfVar = inOrOut.getIndexOfVariable();
            System.out.println("109 Edit_Ops, indexOfVar = " + indexOfVar);
            dm.deleteAColumn(indexOfVar);
            dm.setDataAreClean(false);
        }
    }
    
    public void cleanDataInColumn() {
        System.out.println("116 Edit_Ops, cleanDataInColumn");
        CleanAColumn_Dialog cleanData_Dialog = new CleanAColumn_Dialog(dm, "Either");
        cleanData_Dialog.showAndWait();
        int col = cleanData_Dialog.getVarIndex();
        ColumnOfData col_x = dm.getAllTheColumns().get(col);
        col_x.cleanTheColumn(dm, col);
    }
    
    public int getIndexOfVar() { return indexOfVar; }
} 

