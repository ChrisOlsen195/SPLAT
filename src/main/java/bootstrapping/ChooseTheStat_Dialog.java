/**********************************************************************
 *                        ChooseTheStat_Dialog                        *
 *                             08/03/26                               *
 *                               18:00                                *
 *********************************************************************/
package bootstrapping; 

import dialogs.One_Variable_Dialog;
import splat.Data_Manager;

public class ChooseTheStat_Dialog extends One_Variable_Dialog { 
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
public ChooseTheStat_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Quantitative");
        if (printTheStuff) {
            System.out.println(" *** 19 ChooseTheVar_Dialog, ChooseTheVar_Dialog");
        }
        lbl_Title.setText("Bootstrapping");
        lblFirstVar.setText("Variable choice:");
        setTitle("Bootstrapping");
    }  
}

