/************************************************************
 *                 RandomAssignment_RBD_Dialog              *
 *                          12/31/25                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class RandomAssignment_RBD_Dialog extends RandomAssignment_Dialog{ 
        //boolean printTheStuff = true;
        boolean printTheStuff = false;
    
    public RandomAssignment_RBD_Dialog(Data_Manager dm) {
        super(dm, "RandomAssign_RBD");
        if (printTheStuff) {
            System.out.println("*** 17 RandomAssignment_RBD_Dialog, Constructing");
        }
        lbl_Title.setText("Random Assignment (RBD)");
        lblFirstVar.setText("Subjects variable:");
        lblSecondVar.setText("Blocking Variable:");
        setTitle("Random assignment (RBD");
    }  
}
