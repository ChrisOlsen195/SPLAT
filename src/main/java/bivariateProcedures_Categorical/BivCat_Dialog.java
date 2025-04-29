/************************************************************
 *                       BivCat_Dialog                      *
 *                          03/22/25                        *
 *                            21:00                         *
 ***********************************************************/
package bivariateProcedures_Categorical;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class BivCat_Dialog extends Two_Variables_Dialog{ 
    public BivCat_Dialog(Data_Manager dm, String procCaller) {
        super(dm, "BivCatDialog", procCaller);
        this.dm = dm;
        //System.out.println("\n15 BivCatDialog, Constructing");
        //System.out.println("16 BivCat_Dialog, variableType = " + procCaller);
        waldoFile = "";
        if (procCaller.equals("BivCatAssocFromFile")) {
            lblTitle.setText("Categorical Association");
            lblExplanVar.setText("'Response' Variable:");
            lblResponseVar.setText("'Explanatory' Variable:");
        } else {
            if (procCaller.equals("EpiAssocFromFile")) {
                lblTitle.setText("Epidemiological Association");
                lblExplanVar.setText("'Outcome' Variable:");
                lblResponseVar.setText("'Exposure' Variable:");            
            }
        }
        //System.out.println("21 BivCatAssocDialog, lblFirst/Sec = " + lblFirstVar.getText()
        //                                                           + " / " 
        //                                                           + lblSecondVar.getText()
        // );
        setTitle("Categorical Association");
    }  
}

