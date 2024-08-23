/**************************************************
 *               Matched-t-DiffView               *
 *                    02/19/24                    *
 *                     15:00                      *
 *************************************************/
package the_t_procedures;

import genericClasses.*;
import genericClasses.JustAnAxis;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
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
import splat.Data_Manager;

public class Matched_t_DiffView extends Region {

    // POJOs
    private boolean dragging;    
    
    private int nLegalDataPoints;
    
    private double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper, 
           deltaY, dispLowerBound, dispUpperBound, initHoriz, initVert, initWidth, initHeight;
    
    private double[] preData, postData;
    
    private String descriptionOfDifference, strPreLabel, strPostLabel;
    
    // Make empty if no-print
    // String waldoFile = "Matched_t_DiffView";
    String waldoFile = "";
    
    String graphsCSS;
    private ObservableList<String> categoryLabels;
    
    // My classes
    Data_Manager dm;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;   
    Matched_t_DiffModel matched_t_DiffModel;
    
    // POJOs / FX
    AnchorPane anchorPane;
    Canvas graphCanvas;
    GraphicsContext gcMatched_t; // Required for drawing on the Canvas
    CategoryAxis xAxis;
    Pane theContainingPane;    
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;    

    public Matched_t_DiffView ( Matched_t_DiffModel matched_t_DiffModel, Matched_t_Dashboard matched_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        dm = matched_t_DiffModel.getDataManager();
        dm.whereIsWaldo(78, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        setMinWidth(250); setMinHeight(250);
        this.matched_t_DiffModel = matched_t_DiffModel;
        nLegalDataPoints = matched_t_DiffModel.getNLegalDataPoints();
        preData = new double[nLegalDataPoints];
        postData = new double[nLegalDataPoints];
        preData = matched_t_DiffModel.getThePostData();
        postData = matched_t_DiffModel.getThePreData();
        initStuff();
    }
 
    private void initStuff() {
        dm.whereIsWaldo(92, waldoFile, "initStuff()");
        strPreLabel = matched_t_DiffModel.getPreLabel();
        strPostLabel = matched_t_DiffModel.getPostLabel();
        descriptionOfDifference = strPreLabel + " & " + strPostLabel;
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.addAll(strPreLabel, strPostLabel);
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        graphCanvas = new Canvas(600, 600);
        gcMatched_t = graphCanvas.getGraphicsContext2D();  
        makeItHappen();        
    }
    
    private void makeItHappen() {       
        dm.whereIsWaldo(109, waldoFile, "makeItHappen()");        
        theContainingPane = new Pane();
        gcMatched_t = graphCanvas.getGraphicsContext2D();
        gcMatched_t.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void completeTheDeal() { 
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();           
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
      
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Difference Plot ");
        txtTitle2 = new Text (60, 45, descriptionOfDifference);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void initializeGraphParameters() {  
        dm.whereIsWaldo(145, waldoFile, "initializeGraphParameters()");
        initial_yMin = matched_t_DiffModel.getMinScale();
        initial_yMax = matched_t_DiffModel.getMaxScale();
        initial_yRange = initial_yMax - initial_yMin; 

        yMin = initial_yMin;
        yMax = initial_yMax;
        yRange = initial_yRange;

        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setLabel(descriptionOfDifference);
        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);  
        xAxis.setAutoRanging(true);
        
        xAxis.setMinWidth(40);  //  Controls the Min Y Axis width (for labels)
        xAxis.setPrefWidth(40);     
    }
    
    public void doTheGraph(){
        yAxis.setForcedAxisEndsFalse(); // Just in case
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
        
        gcMatched_t.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight());
        
        double startX = xAxis.getDisplayPosition(strPreLabel);
        double endX = xAxis.getDisplayPosition(strPostLabel);   
        
        //  Loop through the points
        for (int theIthPair = 0; theIthPair < nLegalDataPoints; theIthPair++) {
            double startY = yAxis.getDisplayPosition(postData[theIthPair]);
            double endY = yAxis.getDisplayPosition(preData[theIthPair]);      

            gcMatched_t.setLineWidth(2);
            gcMatched_t.setStroke(Color.BLACK);

                                          // x, y, w, h
            gcMatched_t.strokeLine(startX, startY, endX, endY);
            
            gcMatched_t.setFill(Color.RED);
            gcMatched_t.fillOval(startX - 4, startY - 4, 8, 8);
            gcMatched_t.fillOval(endX - 4, endY - 4, 8, 8);  
        }   
        
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
    
    public void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler);  
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler);        
        dragableAnchorPane.setOnMouseReleased(scatterplotMouseHandler);
    }
    
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>()  {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                dragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging) {
                    yAxis.setLowerBound(newY_Lower ); 
                    yAxis.setUpperBound(newY_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double yPix_Dragging = mouseEvent.getY();  

                newY_Lower = yAxis.getLowerBound();
                newY_Upper = yAxis.getUpperBound(); 
 
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());

                double frac = mouseEvent.getY() / dispLowerBound;
                
                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5) { newY_Upper = yAxis.getUpperBound() + deltaY; }
                    else { newY_Lower = yAxis.getLowerBound() + deltaY;}
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                    if (frac < 0.5) { newY_Upper = yAxis.getUpperBound() - deltaY;
                    } else { newY_Lower = yAxis.getLowerBound() - deltaY; }
                }    

                yAxis.setLowerBound(newY_Lower ); 
                yAxis.setUpperBound(newY_Upper ); 

                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
                
                yPix_MostRecentDragPoint = mouseEvent.getY();
                doTheGraph();
            }
        }
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>()  {
        public void handle(MouseEvent mouseEvent)   {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                ZoomieThing zoomieThing = new ZoomieThing(dragableAnchorPane);
            }
        }
    };  
    
   public Pane getTheContainingPane() { return theContainingPane; }    
}
