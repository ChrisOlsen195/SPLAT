/*******************************************************************************
 *               UnivCat_DataFromFileDialog                                    *
 *                        11/01/23                                             *
 *                         09:00                                               *
 ******************************************************************************/
package univariateProcedures_Categorical;

import dialogs.One_Variable_Dialog;
import splat.Data_Manager;

public class UnivCat_DataFromFileDialog extends One_Variable_Dialog {
    
    public UnivCat_DataFromFileDialog(Data_Manager dm, String variableType) {
        super(dm, "Categorical");
        System.out.println("15 UnivCat_DataFromFileDialog, dm = " + dm);
        minSampleSize = 3;
        setTitle("Univariate Data Exploration");
    }   
}