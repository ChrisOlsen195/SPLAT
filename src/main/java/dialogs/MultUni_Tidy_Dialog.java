/************************************************************
 *                    MultUni_Tidy_Dialog                   *
 *                          02/01/25                        *
 *                            09:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;
public class MultUni_Tidy_Dialog extends Two_Variables_Dialog{ 

    public MultUni_Tidy_Dialog(Data_Manager dm) {
        super(dm, "MultUni_Tidy_Dialog", "None");
        //waldoFile = "MultUni_Tidy_Dialog";
        waldoFile = "";
        dm.whereIsWaldo(15, waldoFile, "ContinueConstruction()");
        lblTitle.setText("Comparing distributions");
        lblExplanVar.setText("Group / Treatment Variable:");
        lblResponseVar.setText("            Data Variable:");
        setTitle("Tidy data");
        // showAndWait();
    }
}
