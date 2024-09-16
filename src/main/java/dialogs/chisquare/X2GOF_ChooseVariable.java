/************************************************************
 *                        X2GOF_Dialog                      *
 *                          09/07/24                        *
 *                            06:00                         *
 ***********************************************************/
package dialogs.chisquare;

import dialogs.One_Variable_Dialog;
import javafx.scene.control.CheckBox;
import splat.Data_Manager;

public class X2GOF_ChooseVariable extends One_Variable_Dialog{ 
    
    public X2GOF_ChooseVariable(Data_Manager myData, String variableType) {
        super(myData, "Categorical"); 
        System.out.println("16 X2GOF_ChooseVariable, Constructing");
        lbl_Title.setText("Chi square Goodness Of Fit");
        lblFirstVar.setText("  Variable:");
        defineTheCheckBoxes();
        setTitle("Chi square Goodness Of Fit");
        // showAndWait();
    }  
    
    private void defineTheCheckBoxes() {
        // Check box strings must match the order of dashboard strings
        // Perhaps pass them to dashboard in future?
        nCheckBoxes = 4;
        String[] chBoxStrings = { " xxx ", " yyy ",
                                  " zzz ", " X2GOFDial "}; 
        dashBoardOptions = new CheckBox[nCheckBoxes];
        for (int ithCBx = 0; ithCBx < nCheckBoxes; ithCBx++) {
            dashBoardOptions[ithCBx] = new CheckBox(chBoxStrings[ithCBx]);
        }
    } 
}
