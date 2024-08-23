/************************************************************
 *                 RandomAssignment_CRD_Dialog              *
 *                          02/14/24                        *
 *                            03:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class RandomAssignment_CRD_Dialog extends RandomAssignment_Dialog{ 
    
    public RandomAssignment_CRD_Dialog(Data_Manager dm) {
        super(dm, "RandAssign_CRD");
        //waldoFile = "RandomAssignment_CRD_Dialog"; 
        waldoFile = "";
        dm.whereIsWaldo(16, waldoFile, "Constructing");
        lbl_Title.setText("Random Assignment (CRD)");
        lblFirstVar.setText("Subjects variable:");
        lblSecondVar.setText("Blocking Variable:");
        setTitle("Random assignment (CRD");
    }  
}
