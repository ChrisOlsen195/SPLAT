/**********************************************************************
 *                        ChooseOneMean_Dialog                        *
 *                             04/13/24                               *
 *                               00:00                                *
 *********************************************************************/
package bootstrapping; 

import dialogs.One_Variable_Dialog;
import splat.Data_Manager;

public class ChooseOneMean_Dialog extends One_Variable_Dialog { 
    
    String waldoFile = "Boot_ChooseOneMean_Dialog";
    //String waldoFile = "";
    
public ChooseOneMean_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Quantitative");
        dm.whereIsWaldo(18, waldoFile, "Constructing");
        lbl_Title.setText("Bootstrapping");
        lblFirstVar.setText("Variable choice:");
        setTitle("Boot_OneMean");
    }  
}

