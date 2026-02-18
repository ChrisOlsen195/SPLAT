/************************************************************
 *                 RandomAssignment_CRD_Dialog              *
 *                          12/12/25                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class RandomAssignment_CRD_Dialog extends RandomAssignment_Dialog{ 
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public RandomAssignment_CRD_Dialog(Data_Manager dm) {
        super(dm, "RandAssign_CRD");
        if (printTheStuff) {
            System.out.println("*** 17 RandomAssignment_CRD_Dialog, Constructing");
        }
        lbl_Title.setText("Random Assignment (CRD)");
        lblFirstVar.setText("Subjects variable:");
        lblSecondVar.setText("Blocking Variable:");
        setTitle("Random assignment (CRD");
    }  
}
