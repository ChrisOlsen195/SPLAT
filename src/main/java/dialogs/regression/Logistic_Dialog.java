/************************************************************
 *                       Logistic_Dialog                    *
 *                           02/01/25                       *
 *                            09:00                         *
 ***********************************************************/
package dialogs.regression;

import dialogs.Two_Variables_Dialog;
import javafx.event.ActionEvent;
import splat.Data_Manager;

public class Logistic_Dialog extends Two_Variables_Dialog{ 

    public Logistic_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Logistic_Dialog", "None");
        // Make empty if no-print
        
        //waldoFile = "Logistic_Dialog";
        waldoFile = "";
        
        dm.whereIsWaldo(21, waldoFile, "Constructing");
        lblTitle.setText("Logistic Regression");
        lblExplanVar.setText("Explanatory variable:");
        lblResponseVar.setText("  Response variable:");

        btnReset.setOnAction((ActionEvent event) -> {
            var_List.resetList();
            tf_Var_1_InFile.setText("");
            tf_Var_2_InFile.setText("");
        });
        
        setTitle("Logistic Regression");
    } 
    
    public void setTitleLabel(String toThis) {lblTitle.setText(toThis); }
}
