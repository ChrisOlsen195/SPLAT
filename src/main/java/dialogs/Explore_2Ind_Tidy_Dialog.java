/************************************************************
 *                  Explore_2Ind_Tidy_Dialog                *
 *                          02/01/25                        *
 *                            09:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;
public class Explore_2Ind_Tidy_Dialog extends Two_Variables_Dialog{ 

    public Explore_2Ind_Tidy_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "Explore_2Ind_Tidy", "None");
        this.dm = dm;
        
        //waldoFile = "Explore_2Ind_Tidy_Dialog";
        waldoFile = "";
        
        dm.whereIsWaldo(18, waldoFile, " *** Constructing");
        lblTitle.setText("Independent Samples (Tidy data)");
        lblExplanVar.setText("Group / Treatment Variable:");
        lblResponseVar.setText("        Response Variable:");
        setTitle("Independent Samples");
        // showAndWait();
    }
}
