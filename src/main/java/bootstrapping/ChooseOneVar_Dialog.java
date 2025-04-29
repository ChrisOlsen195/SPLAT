/**********************************************************************
 *                        ChooseOneVar_Dialog                        *
 *                             02/24/25                               *
 *                               06:00                                *
 *********************************************************************/
package bootstrapping; 

import dialogs.One_Variable_Dialog;
import splat.Data_Manager;

public class ChooseOneVar_Dialog extends One_Variable_Dialog { 
    
    String waldoFile = "Boot_ChooseOneMean_Dialog";
    //String waldoFile = "";
    
public ChooseOneVar_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Quantitative");
        dm.whereIsWaldo(18, waldoFile, "Constructing");
        lbl_Title.setText("Bootstrapping");
        lblFirstVar.setText("Variable choice:");
        setTitle("Boot_OneMean");
    }  
}

