/************************************************************
 *                   Indep_t_Stacked_Dialog                 *
 *                          11/17/23                        *
 *                            00:00                         *
 ***********************************************************/
package dialogs.t_and_z;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

// **************   Called by independent t procedure *****************
public class Indep_t_Stacked_Dialog extends Two_Variables_Dialog{ 
    
    public Indep_t_Stacked_Dialog(Data_Manager dm) {
        super(dm, "Ind_t_Stacked_Dialog", "None");
        waldoFile = "Ind_t_Stacked_Dialog ";
        //waldoFile = "";
        dm.whereIsWaldo(18, waldoFile, "Constructing");
        lblTitle.setText("Independent t procedure (Stacked data)");
        lblFirstVar.setText("Group / Treatment Variable:");
        lblSecondVar.setText("            Data Variable:");
        setTitle("Independent t");
        // showAndWait();
    }  
}
