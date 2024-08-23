/************************************************************
 *                    UnivCatOneVar_Dialog                  *
 *                          11/01/23                        *
 *                            18:00                         *
 ***********************************************************/
package univariateProcedures_Categorical;

import dialogs.One_Variable_Dialog;
import javafx.scene.control.CheckBox;
import splat.Data_Manager;

public class UnivCatOneVar_Dialog extends One_Variable_Dialog{ 
    
    public UnivCatOneVar_Dialog(Data_Manager myData, String variableType) {
        super(myData, "CATEGORICAL"); 
        System.out.println("16 UnivCatOneVar_Dialog, constructing");
        minSampleSize = 3;
        lbl_Title.setText("Univariate Categorical Analysis");
        lblFirstVar.setText("X Variable:");
        // Check box strings must match the order of dashboard strings
        // Perhaps pass them to dashboard in future?
        nCheckBoxes = 4;
        String[] chBoxStrings = { " Best fit line ", " Residuals ",
                                         " RegrReport ", " DiagReport "}; 
        dashBoardOptions = new CheckBox[nCheckBoxes];
        
        for (int ithCBx = 0; ithCBx < nCheckBoxes; ithCBx++) {
            dashBoardOptions[ithCBx] = new CheckBox(chBoxStrings[ithCBx]);
        }
        leftPanel.getChildren().addAll(dashBoardOptions);
        setTitle("Univariate Categorical");
        showAndWait();
    }  
}
