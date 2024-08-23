/************************************************************
 *                      Epi_AssocDialog                     *
 *                          08/19/24                        *
 *                            00:00                         *
 ***********************************************************/
package epidemiologyProcedures;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class Epi_AssocDialog extends Two_Variables_Dialog{ 
    public Epi_AssocDialog(Data_Manager dm, String procCaller) {
        super(dm, "EpiDialog", procCaller);
        this.dm = dm;
        System.out.println("\n15 EpiAssocDialog, Constructing");
        waldoFile = "";
        lblTitle.setText("Epidemiological Association");
        lblFirstVar.setText("Outcome Variable:");
        lblSecondVar.setText("Exposure Variable:");
        setTitle("Categorical Association");
    }  
}
