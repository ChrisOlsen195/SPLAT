/**************************************************
 *                Epidemiology_View               *
 *                    08/19/24                    *
 *                      00:00                     *
 *************************************************/
package epidemiologyProcedures;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Epi_View {
    
    // POJOs
    double initHoriz, initVert, initWidth, initHeight, text1Width;

    String graphsCSS;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;   
    JustAnAxis xAxis, yAxis;
    Epi_Model epi_Model; 
    
    // POJOs / FX 
    AnchorPane anchorPane;
    RiskAnalysisPane riskAnalysisPane;
      
    Pane containingPane;
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public Epi_View (Epi_Model epi_Model, 
                      Epi_Dashboard epi_Dashboard,
                      double placeHoriz, double placeVert,
                      double withThisWidth, double withThisHeight) {
        System.out.println("\n53 Epidemiology_View, Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.epi_Model = epi_Model;
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        containingPane = new Pane();
        txtTitle1 = new Text("**********   Epidemiology   **********"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text("Title2Text"); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 8));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();            
    }
    
    public void completeTheDeal() {
        //System.out.println("69 Epidemiology_View, completeTheDeal()");
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        containingPane = dragableAnchorPane.getTheContainingPane();          
    }
    
    private void initializeGraphParams() {
        xAxis = new JustAnAxis(-0.15, 1.25);
        xAxis.setSide(Side.BOTTOM);

        xAxis.setLabel("This is xAxis");
        xAxis.setVisible(false);    //  Used only for positioning other stuff
        xAxis.forceLowScaleEndToBe(-0.15);
        xAxis.forceHighScaleEndToBe(1.25);

        yAxis = new JustAnAxis(0.0, 1.05);
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.forceHighScaleEndToBe(1.05);
        yAxis.setSide(Side.LEFT);
        yAxis.setVisible(false);    //  Used only for positioning other stuff  
    }
    
    private void setUpUI() {
        //System.out.println("95 Epidemiology_View, setUpUI()");
        riskAnalysisPane = new RiskAnalysisPane();
        riskAnalysisPane = epi_Model.getRiskAnalysisPane();
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        riskAnalysisPane.prefHeightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        riskAnalysisPane.prefHeightProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(txtTitle1, riskAnalysisPane, yAxis, xAxis);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doThePlot() {    
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.15 * tempHeight);

        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(riskAnalysisPane, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(riskAnalysisPane, 0.05 * tempWidth);
        AnchorPane.setRightAnchor(riskAnalysisPane, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(riskAnalysisPane, 0.2 * tempHeight);
        
        anchorPane.requestFocus();
        anchorPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = anchorPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));       
    }
 
    public Pane getTheContainingPane() { return containingPane; }
}
