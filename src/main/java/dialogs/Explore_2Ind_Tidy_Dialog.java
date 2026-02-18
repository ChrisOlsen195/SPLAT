/************************************************************
 *                  Explore_2Ind_Tidy_Dialog                *
 *                          02/01/25                        *
 *                            09:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;
public class Explore_2Ind_Tidy_Dialog extends Two_Variables_Dialog{ 
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    public Explore_2Ind_Tidy_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "Explore_2Ind_Tidy", "None");
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 17 Explore_2Ind_Tidy_Dialog, Constructing");
        }
        lblTitle.setText("Independent Samples (Tidy data)");
        lblExplanVar.setText("Group / Treatment Variable:");
        lblResponseVar.setText("        Response Variable:");
        setTitle("Independent Samples");
    }
}
