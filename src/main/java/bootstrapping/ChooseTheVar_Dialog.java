/**********************************************************************
 *                         ChooseTheVar_Dialog                        *
 *                             02/24/25                               *
 *                               09:00                                *
 *********************************************************************/
package bootstrapping; 

import dialogs.One_Variable_Dialog;
import splat.Data_Manager;

public class ChooseTheVar_Dialog extends One_Variable_Dialog { 
    
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
public ChooseTheVar_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Quantitative");
        if (printTheStuff) {
            System.out.println("*** 19 ChooseTheVar_Dialog, ChooseTheVar_Dialog");
        }
        lbl_Title.setText("Bootstrapping");
        lblFirstVar.setText("Variable choice:");
        setTitle("Bootstrapping");
    }  
}

