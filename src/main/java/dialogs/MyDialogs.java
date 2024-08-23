/****************************************************************************
 *                          MyDialogs                                       * 
 *                           10/15/23                                       *
 *                            18:00                                         *
 ***************************************************************************/
package dialogs;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


public class MyDialogs {
    //  POJOs
    //int intResp;
    //String stringResp;

    public void NoChoiceMessage(int type, String title, String announcement) {
        Alert.AlertType aType = Alert.AlertType.INFORMATION;
        if (type == 1) {
            aType = Alert.AlertType.WARNING;
        } else if (type == 2) {
            aType = Alert.AlertType.ERROR;
        }
        Alert alert = new Alert(aType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(announcement);
        alert.showAndWait();

    } // msgDiag
    
    public String YesNo(int type, String title, String question) {
        String yesNo = "Yes";
        Alert.AlertType aType = Alert.AlertType.CONFIRMATION;
        if (type == 1) {
            aType = Alert.AlertType.WARNING;
        }
        Alert alert = new Alert(aType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(question);
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            yesNo = "Yes";
        } else if (result.get() == buttonTypeTwo) {
            yesNo = "No";
        }

        return yesNo;

    } // YesNo    
}
