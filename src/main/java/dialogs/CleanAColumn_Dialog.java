/************************************************************
 *                      CleanAColumn_Dialog                 *
 *                          10/15/23                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class CleanAColumn_Dialog extends One_Variable_Dialog {
    public CleanAColumn_Dialog(Data_Manager myData, String dataType) {
        super(myData, dataType);
        System.out.println("13 CleanAColumn_Dialog, constructing");
        setTitle("Univariate Data Exploration");
    }   
}
