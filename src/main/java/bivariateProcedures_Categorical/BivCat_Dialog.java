/************************************************************
 *                       BivCat_Dialog                      *
 *                          09/21/24                        *
 *                            18:00                         *
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
            lblFirstVar.setText("'Response' Variable:");
            lblSecondVar.setText("'Explanatory' Variable:");
        } else {
            if (procCaller.equals("EpiAssocFromFile")) {
                lblTitle.setText("Epidemiological Association");
                lblFirstVar.setText("'Outcome' Variable:");
                lblSecondVar.setText("'Exposure' Variable:");            
            }
        }

        setTitle("Categorical Association");
    }  
}

