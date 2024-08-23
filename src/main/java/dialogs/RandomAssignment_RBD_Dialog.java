/************************************************************
 *                 RandomAssignment_RBD_Dialog              *
 *                          02/14/24                        *
 *                            03:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class RandomAssignment_RBD_Dialog extends RandomAssignment_Dialog{ 
    
    public RandomAssignment_RBD_Dialog(Data_Manager dm) {
        super(dm, "RandomAssign_RBD");
        //waldoFile = "RandomAssignment_RBD_Dialog"; 
        waldoFile = "";
        dm.whereIsWaldo(16, waldoFile, "Constructing");
        lbl_Title.setText("Random Assignment (RBD)");
        lblFirstVar.setText("Subjects variable:");
        lblSecondVar.setText("Blocking Variable:");
        setTitle("Random assignment (RBD");
    }  
}
