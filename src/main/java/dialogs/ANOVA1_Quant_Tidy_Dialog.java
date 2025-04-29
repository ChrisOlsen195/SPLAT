/************************************************************
 *                   ANOVA1_Quant_Tidy_Dialog               *
 *                          02/14/25                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class ANOVA1_Quant_Tidy_Dialog extends Two_Variables_Dialog{ 
    public ANOVA1_Quant_Tidy_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "ANOVA1_Quant_Tidy", "None");
        this.dm = dm;
        //waldoFile = "ANOVA1_Quant_Tidy_Dialog";
        waldoFile = "";
        dm.whereIsWaldo(16, waldoFile, "ANOVA1_Quant_Tidy_Dialog, Constructing");
        lblTitle.setText("Analysis of Variance (Tidy data)");
        lblExplanVar.setText("Group / Treatment Variable:");
        lblResponseVar.setText("        Response Variable:");
        setTitle("One way ANOVA");
    }
}
