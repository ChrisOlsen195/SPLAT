/**************************************************
 *                 DotPlot_2Ind_View              *
 *                     01/16/25                   *
 *                      12:00                     *
 *************************************************/
// Two DotPlots are created and passed to DotPlot_2Ind_View
package proceduresTwoUnivariate;

import genericClasses.DragableAnchorPane;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import proceduresOneUnivariate.DotPlot_Model;
import proceduresOneUnivariate.DotPlot_View;

public class DotPlot_2Ind_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double initHoriz, initVert, initWidth, initHeight;

    // FX classes
    //Canvas graphCanvas;
    DragableAnchorPane dragableAnchorPane;
    Text txtTitle1, txtTitle2;
    public String graphsCSS;
    
    // My classes
    DotPlot_Model dotPlot_Model_1, dotPlot_Model_2;
    DotPlot_View dotPlot_View_1, dotPlot_View_2;
    
    Pane containingPane_01, containingPane_02;

    AnchorPane anchorPane;
    Pane theContainingPane;

    public DotPlot_2Ind_View(DotPlot_2Ind_Model dotPlot_2Ind_Model, Explore_2Ind_Dashboard explore_2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("52 *** DotPlot_2Ind_View, Constructing");
        }
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();         
        dotPlot_Model_1 = dotPlot_2Ind_Model.getModel_01();
        dotPlot_Model_2 = dotPlot_2Ind_Model.getModel_02();
        dotPlot_View_1 = new DotPlot_View(dotPlot_Model_1, null, 25, 25, 650, 300);
        dotPlot_View_2 = new DotPlot_View(dotPlot_Model_2, null, 50, 50, 650, 300);       
        theContainingPane = new Pane();
    }
        
    public void completeTheDeal() {
        txtTitle1 = new Text(50, 20, " Comparative Dotplots ");
        txtTitle2 = new Text (60, 45, "This is txtTitle2");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));  
       
        dotPlot_View_1.completeTheDeal();
        dotPlot_View_2.completeTheDeal();

        dotPlot_View_1.doTheGraph();
        dotPlot_View_2.doTheGraph();
        
        containingPane_01 = dotPlot_View_1.getTheContainingPane();
        containingPane_02 = dotPlot_View_2.getTheContainingPane();

        dragableAnchorPane = new DragableAnchorPane();
        
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);  
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(dotPlot_View_1.getTheContainingPane(), dotPlot_View_2.getTheContainingPane());
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth); 
        theContainingPane = dragableAnchorPane.getTheContainingPane();  
    }
    
    public void doTheGraph() {          
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(dotPlot_View_1.getTheContainingPane(), 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(dotPlot_View_1.getTheContainingPane(), 0.1 * tempWidth);
        AnchorPane.setRightAnchor(dotPlot_View_1.getTheContainingPane(), 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(dotPlot_View_1.getTheContainingPane(), 0.5 * tempHeight);
        
        AnchorPane.setTopAnchor(dotPlot_View_2.getTheContainingPane(), 0.6 * tempHeight);
        AnchorPane.setLeftAnchor(dotPlot_View_2.getTheContainingPane(), 0.0 * tempWidth);
        AnchorPane.setRightAnchor(dotPlot_View_2.getTheContainingPane(), 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(dotPlot_View_2.getTheContainingPane(), 0.1 * tempHeight);
        
        /*
        theContainingPane.requestFocus();
        theContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = theContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        })); 
        */
    }   //  end doThePlot

   public Pane getTheContainingPane() { return theContainingPane; }
}

