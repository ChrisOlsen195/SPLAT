/************************************************************
 *                 Explore_2Ind_Stacked_Dialog              *
 *                          11/17/23                        *
 *                            21:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;
public class Explore_2Ind_Stacked_Dialog extends Two_Variables_Dialog{ 

    public Explore_2Ind_Stacked_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "Explore_2Ind_Stacked", "None");
        this.dm = dm;
        //waldoFile = "Explore_2Ind_Stacked_Dialog";
        waldoFile = "";
        dm.whereIsWaldo(16, waldoFile, "Constructing...");
        lblTitle.setText("Independent Samples (Stacked data)");
        lblFirstVar.setText("Group / Treatment Variable:");
        lblSecondVar.setText("        Response Variable:");
        setTitle("Independent Samples");
        // showAndWait();
    }
}
