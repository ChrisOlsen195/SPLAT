/************************************************************
 *                       X2Assoc_Dialog                     *
 *                          10/15/23                        *
 *                            00:00                         *
 ***********************************************************/
package dialogs.chisquare;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class X2Assoc_Dialog extends Two_Variables_Dialog{ 
    
    public X2Assoc_Dialog(Data_Manager myData, String variableType1, String variableType2, String assocType) {
        super(myData, "X2Assoc_Dialog", assocType);
        System.out.println("15 X2Assoc_Dialg, constructing");
        lblTitle.setText(assocType);
        lblFirstVar.setText("X Variable:");
        lblSecondVar.setText("Y Variable:");
        //leftPanel.getChildren().addAll(chBoxDashBoardOptions);
        setTitle(assocType);
    }  
}
