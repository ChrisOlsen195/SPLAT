/************************************************************
 *                     TwoProp_Inf_CI_View                  *
 *                          01/16/25                        *
 *                            18:00                         *
 ***********************************************************/
package the_z_procedures;

import genericClasses.DragableAnchorPane;
import genericClasses.JustAnAxis;
import genericClasses.ZoomieThing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import genericClasses.Point_2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;

public class TwoProp_Inf_CI_View extends Region {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean dragging;

    double initial_xMin, initial_xMax, initial_xRange, xMin, xMax, xRange,
           xPix_InitialPress, xPix_MostRecentDragPoint, newX_Lower, newX_Upper, 
           deltaX, dispLowerBound, dispUpperBound, lowEndCI_1, highEndCI_1, 
           lowEndCI_2, highEndCI_2, initHoriz, initVert, initWidth, initHeight;

    String descriptionOfVariable, p1_Descr, p2_Descr;

    ObservableList<String> categoryLabels;
        
    // My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis;    
    Point_2D  lowEnds, highEnds, pHats;
    TwoProp_Inf_Model twoProp_Inf_Model;
    
    // POJOs / FX
    AnchorPane anchorPane; //, checkBoxRow;
    Canvas graphCanvas;
    CategoryAxis yAxis;
    GraphicsContext gcHBox; // Required for drawing on the Canvas
    Pane theContainingPane;    
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public TwoProp_Inf_CI_View () { }
    
    public TwoProp_Inf_CI_View(TwoProp_Inf_Model twoProp_Inf_Model, TwoProp_Inf_Dashboard twoProp_Inf_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("78 *** TwoProp_Inf_CI_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.twoProp_Inf_Model = twoProp_Inf_Model;
        descriptionOfVariable = twoProp_Inf_Model.getTheTitle();
        p1_Descr = twoProp_Inf_Model.getP1_Descr();
        p2_Descr = twoProp_Inf_Model.getP2_Descr();
        initStuff();
    }
    
    private void initStuff() {
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.addAll(p1_Descr, p2_Descr);        
        lowEnds = twoProp_Inf_Model.getLowEnds();
        highEnds = twoProp_Inf_Model.getHighEnds();
        pHats = twoProp_Inf_Model.getPHats();
        graphCanvas = new Canvas(600, 600);
        gcHBox = graphCanvas.getGraphicsContext2D();   
        makeItHappen();        
    }
    
    private void makeItHappen() {    
        theContainingPane = new Pane();
        gcHBox = graphCanvas.getGraphicsContext2D();
        gcHBox.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void completeTheDeal() { 
        initializeGraphParameters();
        yAxis.setTickLabelFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        yAxis.setTickLabelRotation(270);
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();           
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
        
    public void initializeGraphParameters() { 
        initial_xMin = 0.0;
        initial_xMax = 1.0;
        initial_xRange = initial_xMax - initial_xMin; 
        
        xMin = initial_xMin;
        xMax = initial_xMax;
        xRange = initial_xRange;
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);    
        xAxis.setLabel(" ");
        deltaX = 0.005 * xRange;  
        
        lowEndCI_1 = xAxis.getDisplayPosition(lowEnds.getFirstValue());
        highEndCI_1 = xAxis.getDisplayPosition(highEnds.getFirstValue());        
        
        yAxis = new CategoryAxis(categoryLabels);
        yAxis.setSide(Side.LEFT);  
        yAxis.setAutoRanging(true);
        yAxis.setMinWidth(40);  //  Controls the Min Y Axis width (for labels)
        yAxis.setPrefWidth(40);                 
    }
    
    private void constructDataArray() { /*  */ }
    
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Separate Confidence intervals ");
        txtTitle2 = new Text (60, 45, descriptionOfVariable);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doTheGraph() {
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(txtTitle1, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * tempHeight);

        gcHBox.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight());
        
        double daYPosition_1 = yAxis.getDisplayPosition(categoryLabels.get(0));
        lowEndCI_1 = xAxis.getDisplayPosition(lowEnds.getFirstValue());
        highEndCI_1 = xAxis.getDisplayPosition(highEnds.getFirstValue());
        double position_pHat_1 = xAxis.getDisplayPosition(pHats.getFirstValue());
        
        double daYPosition_2 = yAxis.getDisplayPosition(categoryLabels.get(1));

        lowEndCI_2 = xAxis.getDisplayPosition(lowEnds.getSecondValue());
        highEndCI_2 = xAxis.getDisplayPosition(highEnds.getSecondValue());
        
        double position_pHat_2 = xAxis.getDisplayPosition(pHats.getSecondValue());

        gcHBox.setLineWidth(2);
        gcHBox.setStroke(Color.BLACK);
        
        double upDown = 7;
        
        // Note:  Up is down, down is up -- hail, Lewis Carroll!!!
        double upper_1 = daYPosition_1 + upDown;
        double lower_1 = daYPosition_1 - upDown;
        
        double upper_2 = daYPosition_2 + upDown;
        double lower_2 = daYPosition_2 - upDown;

        gcHBox.strokeLine(lowEndCI_1, daYPosition_1, highEndCI_1, daYPosition_1);
        gcHBox.strokeLine(lowEndCI_1, upper_1, lowEndCI_1, lower_1);
        gcHBox.strokeLine(highEndCI_1, upper_1, highEndCI_1, lower_1);    
        gcHBox.strokeOval(position_pHat_1 - 5, daYPosition_1 - 5., 10., 10.);
        
        gcHBox.strokeLine(lowEndCI_2, daYPosition_2, highEndCI_2, daYPosition_2);
        gcHBox.strokeLine(lowEndCI_2, upper_2, lowEndCI_2, lower_2);
        gcHBox.strokeLine(highEndCI_2, upper_2, highEndCI_2, lower_2); 
        gcHBox.strokeOval(position_pHat_2 - 5, daYPosition_2 - 5., 10., 10.);
        
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
    }
    
    public void setHandlers()  {
        xAxis.setOnMouseDragged(xAxisMouseHandler); 
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler);         
        dragableAnchorPane.setOnMouseReleased(scatterplotMouseHandler);        
    }
    
    EventHandler<MouseEvent> xAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent)  {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                xPix_InitialPress = mouseEvent.getX();  
                xPix_MostRecentDragPoint = mouseEvent.getX();
                dragging = false;   
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging) {
                    xAxis.setLowerBound(newX_Lower ); 
                    xAxis.setUpperBound(newX_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;                
                double xPix_Dragging = mouseEvent.getX();
                newX_Lower = xAxis.getLowerBound();
                newX_Upper = xAxis.getUpperBound(); 

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());

                double frac = mouseEvent.getX() / dispUpperBound;
                
                // Still dragging right
                if((xPix_Dragging > xPix_InitialPress) && (xPix_Dragging > xPix_MostRecentDragPoint)) {    
                    // Which half of scale?
                    if (frac > 0.5) { //  Right of center -- OK
                        newX_Upper = xAxis.getUpperBound() - deltaX;
                    }
                    else  // Left of Center
                    {
                        newX_Lower = xAxis.getLowerBound() - deltaX;
                    }
                }
                else 
                if ((xPix_Dragging < xPix_InitialPress) && (xPix_Dragging < xPix_MostRecentDragPoint)) {   
                    if (frac < 0.5) { // Left of center
                        newX_Lower = xAxis.getLowerBound() + deltaX;
                    }
                    else {   // Right of center -- OK
                        newX_Upper = xAxis.getUpperBound() + deltaX;
                    }
                }    

                xAxis.setLowerBound(newX_Lower ); 
                xAxis.setUpperBound(newX_Upper );

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());
                xPix_MostRecentDragPoint = mouseEvent.getX();
                
                doTheGraph();
            }
        }
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                ZoomieThing zoomieThing = new ZoomieThing(dragableAnchorPane);
            }
        }
    };  
    
   public Pane getTheContainingPane() { return theContainingPane; }       
}
