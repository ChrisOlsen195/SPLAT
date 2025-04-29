/************************************************************
 *                      Regression_Dialog                   *
 *                          04/15/25                        *
 *                            03:00                         *
 ***********************************************************/
package dialogs.regression;

import dialogs.TwoVars_Dialog_One;
import splat.Data_Manager;

public class Regr_Dialog extends TwoVars_Dialog_One { 
    
    public Regr_Dialog(Data_Manager dm, String variableType, String labelAndTitle) {
        super(dm, "RegressionDialog", "None");   
        this.dm = dm;
        
        //waldoFile = "Regression_Dialog";
        waldoFile = "";
        
        dm.whereIsWaldo(25, waldoFile, "Constructing");
        lblTitle.setText(labelAndTitle);
        setTitle(labelAndTitle);        
        lblExplanVar.setText("'Explanatory' variable:");
        lblResponseVar.setText("  'Response' variable:");
    }
    
    public String getSaveTheResids() { return strSaveTheResids; }
    public String getSaveTheHats() { return strSaveTheHats; }
        
}
