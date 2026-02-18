/************************************************************
 *                   ANOVA1_Quant_Tidy_Dialog               *
 *                          12/12/25                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class ANOVA1_Quant_Tidy_Dialog extends Two_Variables_Dialog{ 
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
    public ANOVA1_Quant_Tidy_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "ANOVA1_Quant_Tidy", "None");
        if (printTheStuff) {
            System.out.println("*** 17 ANOVA1_Quant_Tidy_Dialog, Constructing");
        }
        lblTitle.setText("Analysis of Variance (Tidy data)");
        lblExplanVar.setText("Group / Treatment Variable:");
        lblResponseVar.setText("        Response Variable:");
        setTitle("One way ANOVA");
    }
}
