/************************************************************
 *                ANOVA1_Quant_Stacked_Dialog               *
 *                          05/36/24                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class ANOVA1_Quant_Stacked_Dialog extends Two_Variables_Dialog{ 
    public ANOVA1_Quant_Stacked_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "ANOVA1_Quant_Stacked", "None");
        this.dm = dm;
        //waldoFile = "ANOVA1_Quant_Stacked_Dialog";
        waldoFile = "";
        dm.whereIsWaldo(16, waldoFile, "ANOVA1_Quant_Stacked_Dialog, Constructing");
        lblTitle.setText("Analysis of Variance (Stacked data)");
        lblFirstVar.setText("Group / Treatment Variable:");
        lblSecondVar.setText("        Response Variable:");
        setTitle("One way ANOVA");
    }
}
