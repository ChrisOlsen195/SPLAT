/**************************************************
 *            ANOVA2_PrintReportView              *
 *                   05/24/24                     *
 *                     06:00                      *
 *************************************************/
package anova2;

import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.control.TextArea;

public class ANOVA2_PrintReportView {
    // POJOs
    
    double initHoriz, initVert, initWidth, initHeight;    
    
    String /*tempString,*/ strThisLine;

    static ArrayList<String> anova1StringsToPrint; 
    
    // My Classes 
    DragableAnchorPane dragableAnchorPane;
    
    // FX Classes
    Pane containingPane;   
    TextArea txtArea4Strings; 
    Text txtTitle; //, thisText;
    AnchorPane thePSAnchorPane;
 
    ANOVA2_PrintReportView(ANOVA2_Factorial_Model anova2Model,  ANOVA2_RCB_Dashboard anova2Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        anova1StringsToPrint = new ArrayList<>();
        anova1StringsToPrint = anova2Model.getANOVA2Report();
    }
    
    public void completeTheDeal() {
        setUpUI();                 
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    private void setUpUI() {
        txtTitle = new Text(250, 20, "Two-way Factorial Analysis of Variance");
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20)); 
        txtArea4Strings = new TextArea();  // Where text will be drawn
        txtArea4Strings.setWrapText(false);
        txtArea4Strings.setEditable(false);
        txtArea4Strings.setPrefColumnCount(50);
        txtArea4Strings.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        
        for (int printLines = 0; printLines < anova1StringsToPrint.size(); printLines++) {
            //tempString = anova1StringsToPrint.get(printLines);
            //strThisLine = tempString;
            //  Replaces 59, 60
            strThisLine = anova1StringsToPrint.get(printLines); 
            //thisText = new Text(20, 19 * printLines + 40, strThisLine);
            //thisText.setFont(Font.font("Courier New", FontWeight.NORMAL, FontPosture.REGULAR, 15));
            txtArea4Strings.appendText(strThisLine);
        }
 
        containingPane = new Pane();
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        // Construct the draggable
        thePSAnchorPane = dragableAnchorPane.getTheAP();
        // Finish the job -- make it dragable
        dragableAnchorPane.makeDragable();
        thePSAnchorPane.getChildren().addAll(txtTitle, txtArea4Strings);     
        double paneWidth = initWidth;
        double titleWidth = txtTitle.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        AnchorPane.setTopAnchor(txtTitle, 0.0 * initHeight);        
        AnchorPane.setLeftAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.075 * initHeight);
        AnchorPane.setTopAnchor(txtArea4Strings, 0.075 * initHeight);
        AnchorPane.setLeftAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setRightAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(txtArea4Strings,0.0 * initHeight);

        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public Pane getTheContainingPane() { return containingPane; }
}
