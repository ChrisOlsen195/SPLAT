/************************************************************
 *                         Regr_Dialog                      *
 *                          12/13/25                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs.regression;

import dialogs.TwoVars_Dialog_One;
import splat.Data_Manager;

public class Regr_Dialog extends TwoVars_Dialog_One { 
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public Regr_Dialog(Data_Manager dm, String variableType, String labelAndTitle) {
        super(dm, "RegressionDialog", "None");   
        if (printTheStuff) {
            System.out.println("*** 19 Regr_Dialog, Constructing");
        }
        lblTitle.setText(labelAndTitle);
        setTitle(labelAndTitle);        
        lblExplanVar.setText("'Explanatory' variable:");
        lblResponseVar.setText("  'Response' variable:");
    }
    
    public String getSaveTheResids() { return strSaveTheResids; }
    public String getSaveTheHats() { return strSaveTheHats; }
        
}
