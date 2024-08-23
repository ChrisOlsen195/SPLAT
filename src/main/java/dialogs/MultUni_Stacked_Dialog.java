/************************************************************
 *                   MultUni_Stacked_Dialog                 *
 *                          10/15/23                        *
 *                            00:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;
public class MultUni_Stacked_Dialog extends Two_Variables_Dialog{ 

    public MultUni_Stacked_Dialog(Data_Manager dm) {
        super(dm, "MultUni_Stacked_Dialog", "None");
        waldoFile = "MultUni_Stacked_Dialog";
        //waldoFile = "";
        dm.whereIsWaldo(15, waldoFile, "ContinueConstruction()");
        lblTitle.setText("Comparing distributions");
        lblFirstVar.setText("Group / Treatment Variable:");
        lblSecondVar.setText("            Data Variable:");
        setTitle("Stacked data");
        // showAndWait();
    }
}
