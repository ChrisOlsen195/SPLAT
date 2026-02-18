/************************************************************
 *                     Indep_t_Tidy_Dialog                  *
 *                          02/01/25                        *
 *                            09:00                         *
 ***********************************************************/
package dialogs.t_and_z;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class Indep_t_Tidy_Dialog extends Two_Variables_Dialog{
    public Indep_t_Tidy_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "Indep_t_Tidy", "None");
        this.dm = dm;
        
        waldoFile = "Indep_t_Tidy_Dialog";
        //waldoFile = "";
        
        dm.whereIsWaldo(19, waldoFile, " *** Constructing");
        lblTitle.setText("Independent Samples (Tidy data)");
        lblExplanVar.setText("Group / Treatment Variable:");
        lblResponseVar.setText("        Response Variable:");
        setTitle("Independent Samples");
    }    
}
