/************************************************************
 *                      Epi_AssocDialog                     *
 *                          12/17/25                        *
 *                            00:00                         *
 ***********************************************************/
package epidemiologyProcedures;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class Epi_AssocDialog extends Two_Variables_Dialog{ 
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public Epi_AssocDialog(Data_Manager dm, String procCaller) {
        super(dm, "Epi_Assoc_Dlg", procCaller);
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 19 Epi_AssocDialog, Constructing");
        }
        lblTitle.setText("Epidemiological Association");
        lblExplanVar.setText("Exposure");
        lblResponseVar.setText("Outcome");
        setTitle("Categorical Association");
    }  
}
