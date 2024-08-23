/**************************************************
 *            MultRegression_Dashboard            *
 *                    05/13/24                    *
 *                     00:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package printFile;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PrintFile_Dashboard extends Dashboard {
    // POJOs
    
    final String[] regrCheckBoxDescr = { " PrintTheFile "};
    
    // My classes
    PrintFile_PrintReportView printFile_PrintReportView;
    PrintFile_Model printFile_Model;         

    Pane printFile_ContainingPane; 
            
    public PrintFile_Dashboard(PrintFile_Controller printFile_Controller, PrintFile_Model printFile_Model) {
        super(1);  // nCheckBoxes = 1;
        this.printFile_Model = printFile_Model;
        System.out.println("30 PrintFile_Dashboard, constructing");
        dm = printFile_Model.getDataManager();
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Print file dashboard"); 
    }  
    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            printFile_ContainingPane.setVisible(true);
        }
        else
            printFile_ContainingPane.setVisible(false);
    }
    
    public void populateTheBackGround() {
        initHeight[0] = 675;
        initWidth[0] = 700;
        printFile_PrintReportView = new PrintFile_PrintReportView(printFile_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        printFile_PrintReportView.completeTheDeal();
        printFile_ContainingPane = printFile_PrintReportView.getTheContainingPane(); 
        printFile_ContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().add(printFile_ContainingPane);   
    }
}
