/************************************************************
 *                       X2Assoc_Dialog                     *
 *                          02/01/25                        *
 *                            06:00                         *
 ***********************************************************/
package dialogs.chisquare;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class X2Assoc_Dialog extends Two_Variables_Dialog{ 
    
    public X2Assoc_Dialog(Data_Manager myData, String variableType1, String variableType2, String assocType) {
        super(myData, "X2Assoc_Dialog", assocType);
        //System.out.println("15 *** X2Assoc_Dialg, constructing");
        lblTitle.setText(assocType);
        lblExplanVar.setText("X Variable:");
        lblResponseVar.setText("Y Variable:");
        //leftPanel.getChildren().addAll(chBoxDashBoardOptions);
        setTitle(assocType);
    }  
}
