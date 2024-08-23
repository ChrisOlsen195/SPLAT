/************************************************************
 *                 ANOVA1_Cat_Stacked_Dialog                *
 *                          10/15/23                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;
public class ANOVA1_Cat_Stacked_Dialog extends Two_Variables_Dialog{ 
    public ANOVA1_Cat_Stacked_Dialog(Data_Manager dm, String explanVarType) {
        super(dm, "ANOVA1_Cat_Stacked", "None");
        this.dm = dm;
        // waldoFile = "ANOVA1_Cat_Stacked_Dialog";
        waldoFile = "";
        dm.whereIsWaldo(15, waldoFile, "Constructing...");
        lblTitle.setText("Analysis of Variance (Stacked data)");
        lblFirstVar.setText("Group / Treatment Variable:");
        lblSecondVar.setText("        Response Variable:");
        setTitle("One way ANOVA");
        // showAndWait();
    }
}