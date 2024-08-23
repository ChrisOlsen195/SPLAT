/************************************************************
 *                      Regression_Dialog                   *
 *                          08/20/24                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs.regression;

import dialogs.Two_Variables_Dialog;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import splat.Data_Manager;

public class Regression_Dialog extends Two_Variables_Dialog{ 
    
    String strSaveTheResids, strSaveTheHats;
    CheckBox cbxSaveTheResids, cbxSaveTheHats;
    
    public Regression_Dialog(Data_Manager dm, String variableType, String labelAndTitle) {
        super(dm, "RegressionDialog", "None");   
        this.dm = dm;
        // waldoFile = "Regression_Dialog";
        waldoFile = "";
        dm.whereIsWaldo(26, waldoFile, "Constructing");
        lblTitle.setText(labelAndTitle);
        setTitle(labelAndTitle);        
        lblFirstVar.setText("'Explanatory' variable:");
        lblSecondVar.setText("  'Response' variable:");

        cbxSaveTheResids = new CheckBox("Save the residuals");
        cbxSaveTheResids.selectedProperty().addListener(this::changed_SaveTheResids);
        cbxSaveTheResids.setSelected(false);
        strSaveTheResids = "No";
        gridPaneChoicesMade.add(cbxSaveTheResids, 0, 5);
        
        cbxSaveTheHats = new CheckBox("Save the Fits");
        cbxSaveTheHats.selectedProperty().addListener(this::changed_SaveTheHats);
        cbxSaveTheHats.setSelected(false);
        strSaveTheHats = "No";
        gridPaneChoicesMade.add(cbxSaveTheHats, 1, 5);
    }        
    
    public void changed_SaveTheResids(ObservableValue < ? extends Boolean> observable,
            Boolean oldValue,
            Boolean newValue) {
        String state = null;
        if (cbxSaveTheResids.isSelected() ) {
            strSaveTheResids = "Yes";
        } else { 
            strSaveTheResids = "No"; 
        }
    }
    
    public void changed_SaveTheHats(ObservableValue < ? extends Boolean> observable,
            Boolean oldValue,
            Boolean newValue) {
        String state = null;
        if (cbxSaveTheHats.isSelected() ) {
            strSaveTheHats = "Yes";
        } else { 
            strSaveTheHats = "No"; 
        }
    }
    
    public String getSaveTheResids() { return strSaveTheResids; }
    
    public String getSaveTheHats() { return strSaveTheHats; }
}
