/****************************************************************************
 *                  MultUni_PrintReport_View                                * 
 *                         01/16/25                                         *
 *                          12:00                                           *
 ***************************************************************************/
package proceduresManyUnivariate;

import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import proceduresOneUnivariate.PrintUStats_Model;

public class MultUni_PrintReportView {
    // POOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public int nVariables, nPrintLines;
    public double initHoriz, initVert, initWidth, initHeight; 
    
    // public String strRTPTitle;
    public String sourceString, strTextPaneTitle, strTitleText;
    public ArrayList<String> stringsToPrint; 
    String[] arrayOfStrings;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    ArrayList<PrintUStats_Model> printUStats_Models;
    
    // FX Objects
    public AnchorPane thePSAnchorPane;
    HBox textAreas;
    public Pane containingPane;   
    public ArrayList<TextArea> txtArea4Strings; 
    public Text txtTitle, thisText;
    
    public MultUni_PrintReportView (MultUni_Model multUni_Model,  MultUni_Dashboard multUni_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("48 *** MultUni_PrintReportView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        nVariables = multUni_Model.getNVariables();
        stringsToPrint = new ArrayList();
        txtArea4Strings = new ArrayList();
        printUStats_Models = new ArrayList();
        printUStats_Models = multUni_Model.getPrintUStatsModels();
        containingPane = new Pane();     
    }
    
    public void setUpUI() {
        strTitleText = "Univariate statistics";
        txtTitle = new Text(250, 20, strTitleText);       
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20)); 
        
        for (int ithVariable = 0; ithVariable < nVariables; ithVariable++) {
            TextArea tempTA = new TextArea();
            tempTA.setWrapText(false);
            tempTA.setEditable(false);
            tempTA.setPrefColumnCount(80);
            tempTA.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
            
            printUStats_Models.get(ithVariable).constructThePrintLines();
            stringsToPrint  =  printUStats_Models.get(ithVariable).getStringsToPrint();            
            nPrintLines = stringsToPrint.size();            
            arrayOfStrings = new String[nPrintLines];
            
            for (int ithPrintLine = 0; ithPrintLine < nPrintLines; ithPrintLine++) {
                String tempString = stringsToPrint.get(ithPrintLine);
                arrayOfStrings[ithPrintLine] = tempString;
                String strThisLine = tempString;
                thisText = new Text(20, 19 * ithPrintLine + 40, strThisLine);
                thisText.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 15));
                tempTA.appendText(strThisLine);
            }
            txtArea4Strings.add(tempTA);
        }        
        textAreas = new HBox();
        textAreas.getChildren().addAll(txtArea4Strings);
    }
    
    public void completeTheDeal() {
        setUpUI();                 
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane();   
    }    
       
    public void setUpAnchorPane() {       
        dragableAnchorPane = new DragableAnchorPane();
        // Construct the draggable
        thePSAnchorPane = dragableAnchorPane.getTheAP();
        // Finish the job -- make it dragable
        dragableAnchorPane.makeDragable();
        thePSAnchorPane.getChildren().addAll(txtTitle, textAreas);  
        
        double paneWidth = initWidth;
        double titleWidth = txtTitle.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        
        AnchorPane.setTopAnchor(txtTitle, 0.0 * initHeight);        
        AnchorPane.setLeftAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.075 * initHeight);
        
        AnchorPane.setTopAnchor(textAreas, 0.075 * initHeight);
        AnchorPane.setLeftAnchor(textAreas, 0.0 * initWidth);
        AnchorPane.setRightAnchor(textAreas, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(textAreas,0.0 * initHeight);

        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public Pane getTheContainingPane() { return containingPane; }    
}
