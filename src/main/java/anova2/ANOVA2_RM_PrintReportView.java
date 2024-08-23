/**************************************************
 *           ANOVA2_RM_PrintReportView            *
 *                   05/24/24                     *
 *                     12:00                      *
 *************************************************/
package anova2;

import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.control.TextArea;
import splat.Data_Manager;

public class ANOVA2_RM_PrintReportView {
    // POJOs    
    double initHoriz, initVert, initWidth, initHeight;    
    
    String tempString, strThisLine, strTitle1;
    
    String waldoFile = "";
    //String waldoFile = "ANOVA2_RM_PrintReportView";

    static ArrayList<String> rmStringsToPrint; 
    
    // My Classes
    Data_Manager dm;
    DragableAnchorPane dragableAnchorPane;
    //ANOVA2_RM_Model anova2_RM_Model;
    //RCB_wReplicates_Dashboard rcbDashboard;
    
    // FX Classes
    Pane containingPane;   
    TextArea txtArea4Strings; 
    Text titleText, thisText;
    AnchorPane thePSAnchorPane;
 
    ANOVA2_RM_PrintReportView(ANOVA2_RM_Model anova2_RM_Model, ANOVA2_RM_Dashboard anova2_RM_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        dm = anova2_RM_Model.getDataManager();
        dm.whereIsWaldo(46, waldoFile, "Constructing");
        rmStringsToPrint = new ArrayList<>();
        rmStringsToPrint = anova2_RM_Model.getANOVA2Report();
    }
    
    public void completeTheDeal() {
        setUpUI();                 
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    private void setUpUI() {
        strTitle1 = "Repeated Measures Design";
        titleText = new Text(250, 20, strTitle1);
        titleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20)); 
        txtArea4Strings = new TextArea();  // Where text will be drawn
        txtArea4Strings.setWrapText(false);
        txtArea4Strings.setEditable(false);
        txtArea4Strings.setPrefColumnCount(50);
        txtArea4Strings.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        
        for (int printLines = 0; printLines < rmStringsToPrint.size(); printLines++) {
            tempString = rmStringsToPrint.get(printLines);
            strThisLine = tempString;
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            thisText.setFont(Font.font("Courier New", FontWeight.NORMAL, FontPosture.REGULAR, 15));
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
        thePSAnchorPane.getChildren().addAll(titleText, txtArea4Strings);     
        double paneWidth = initWidth;
        double titleWidth = titleText.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        AnchorPane.setTopAnchor(titleText, 0.0 * initHeight);        
        AnchorPane.setLeftAnchor(titleText, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(titleText, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(titleText, 0.075 * initHeight);
        AnchorPane.setTopAnchor(txtArea4Strings, 0.075 * initHeight);
        AnchorPane.setLeftAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setRightAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(txtArea4Strings,0.0 * initHeight);

        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public Pane getTheContainingPane() { return containingPane; }
}


