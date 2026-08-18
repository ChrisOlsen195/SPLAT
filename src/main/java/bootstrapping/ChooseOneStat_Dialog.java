/**********************************************************************
 *                        ChooseOneStat_Dialog                        *
 *                             08/03/26                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping; 

import dialogs.One_Variable_Dialog;
import splat.Data_Manager;

public class ChooseOneStat_Dialog extends One_Variable_Dialog { 
    
    //String waldoFile = "Boot_ChooseOneStat_Dialog";
    String waldoFile = "";
    
public ChooseOneStat_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Quantitative");
        dm.whereIsWaldo(18, waldoFile, "Constructing");
        lbl_Title.setText("Bootstrapping");
        lblFirstVar.setText("Variable choice:");
        setTitle("Boot_OneMean");
    }  
}

