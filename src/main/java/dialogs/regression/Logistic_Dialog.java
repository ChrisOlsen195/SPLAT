/************************************************************
 *                       Logistic_Dialog                    *
 *                           05/26/24                       *
 *                            21:00                         *
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
        dm.whereIsWaldo(19, waldoFile, "Constructing");
        lblTitle.setText("Logistic Regression");
        lblFirstVar.setText("Explanatory variable:");
        lblSecondVar.setText("Zero-One variable:");

        btnReset.setOnAction((ActionEvent event) -> {
            var_List.resetList();
            tf_FirstVarLabel_InFile.setText("");
            tf_SecondVarLabel_InFile.setText("");
        });
        
        setTitle("Logistic Regression");
    } 
    
    public void setTitleLabel(String toThis) {lblTitle.setText(toThis); }
}
