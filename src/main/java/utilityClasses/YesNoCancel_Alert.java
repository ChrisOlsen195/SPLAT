/**************************************************
 *               YesNoCancel_Alert                *
 *                   11/01/23                     *
 *                     18:00                      *
 *************************************************/
package utilityClasses;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class YesNoCancel_Alert {
    
    String returnString;
    
    public YesNoCancel_Alert (String title,
                      String headerText) {
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
 
        ButtonType yesButton = new ButtonType("   Yes   ");
        ButtonType noButton = new ButtonType("   No   ");
        ButtonType cancelButton = new ButtonType("  Cancel  ");
 
        // Remove default ButtonTypes
        alert.getButtonTypes().clear();
 
        alert.getButtonTypes().addAll(yesButton, noButton, cancelButton);
 
        Optional<ButtonType> option = alert.showAndWait();
        
        if (option.get() == null) {
            returnString = "Cancel";
        } else if (option.get() == yesButton) {
            returnString = "Yes";
        } else if (option.get() == noButton) {
            returnString = "No";
        }  else if (option.get() == noButton) {
            returnString = "Cancel";
        }
    }
    
    public String getReturnString () { return returnString; }     
}
