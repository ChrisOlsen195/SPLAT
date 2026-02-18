/************************************************************
 *                    MultUni_Tidy_Dialog                   *
 *                          02/01/25                        *
 *                            09:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;
public class MultUni_Tidy_Dialog extends Two_Variables_Dialog{ 
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
    public MultUni_Tidy_Dialog(Data_Manager dm) {
        super(dm, "MultUni_Tidy_Dialog", "None");
       if (printTheStuff) {
            System.out.println("*** 16 MultUni_Tidy_Dialog, Constructing");
        }
        lblTitle.setText("Comparing distributions");
        lblExplanVar.setText("Group / Treatment Variable:");
        lblResponseVar.setText("            Data Variable:");
        setTitle("Tidy data");
    }
}
