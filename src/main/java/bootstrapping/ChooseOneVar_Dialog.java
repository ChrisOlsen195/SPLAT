/**********************************************************************
 *                      Boot_ChooseTheVar_Dialog                      *
 *                             01/08/25                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping; 

import dialogs.One_Variable_Dialog;
import splat.Data_Manager;

public class ChooseOneVar_Dialog extends One_Variable_Dialog { 
    
    //String waldoFile = "ChooseTheVar_Dialog";
    String waldoFile = "";
    
public ChooseOneVar_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Quantitative");
        dm.whereIsWaldo(18, waldoFile, "Constructing");
        lbl_Title.setText("Bootstrapping");
        lblFirstVar.setText("Variable choice:");
        setTitle("Bootstrapping");
    }  
}

