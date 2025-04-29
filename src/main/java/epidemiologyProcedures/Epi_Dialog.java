/************************************************************
 *                         Epi_Dialog                      *
 *                          02/01/25                        *
 *                            09:00                         *
 ***********************************************************/
package epidemiologyProcedures;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class Epi_Dialog extends Two_Variables_Dialog{ 
    public Epi_Dialog(Data_Manager dm, String variableType) {
        super(dm, "BivCatDialog", "None");
        
        //String waldoFile = "Epi_Dialog"; 
        String waldoFile = "";
        
        dm.whereIsWaldo(18, waldoFile, "\nConstructing");
        lblTitle.setText("Chi square association");
        lblExplanVar.setText("X Variable:");
        lblResponseVar.setText("Y Variable:");
        setTitle("Chi square association");
    }  
}
