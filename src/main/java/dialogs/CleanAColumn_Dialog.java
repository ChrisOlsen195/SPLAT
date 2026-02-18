/************************************************************
 *                      CleanAColumn_Dialog                 *
 *                          12/12/25                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class CleanAColumn_Dialog extends One_Variable_Dialog {
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
        
    public CleanAColumn_Dialog(Data_Manager dm, String dataType) {
        super(dm, dataType);
        if (printTheStuff) {
            System.out.println("*** 17 CleanAColumn_Dialog, Constructing");
        }
        setTitle("Univariate Data Exploration");
    }   
}
