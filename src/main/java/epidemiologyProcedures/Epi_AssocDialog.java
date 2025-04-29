/************************************************************
 *                      Epi_AssocDialog                     *
 *                          02/01/25                        *
 *                            09:00                         *
 ***********************************************************/
package epidemiologyProcedures;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class Epi_AssocDialog extends Two_Variables_Dialog{ 
    public Epi_AssocDialog(Data_Manager dm, String procCaller) {
        super(dm, "EpiDialog", procCaller);
        this.dm = dm;
        //System.out.println("15 EpiAssocDialog, Constructing");
        waldoFile = "";
        lblTitle.setText("Epidemiological Association");
        lblExplanVar.setText("Outcome Variable:");
        lblResponseVar.setText("Exposure Variable:");
        setTitle("Categorical Association");
    }  
}
