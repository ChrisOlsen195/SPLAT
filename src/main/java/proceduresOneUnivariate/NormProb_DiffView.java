/**************************************************
 *               NormProb-DiffView                *
 *                    11/10/23                    *
 *                     15:00                      *
 *************************************************/
package proceduresOneUnivariate;

import anova1.categorical.ANOVA1_Cat_Dashboard;
import anova1.quantitative.ANOVA1_Quant_Dashboard;
import genericClasses.*;
import genericClasses.JustAnAxis;
import java.util.Arrays;
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
import probabilityDistributions.*;
import the_t_procedures.Matched_t_Dashboard;

public class NormProb_DiffView extends Region {

    // POJOs
    private boolean dragging;    
    
    private int nLegalDataPoints;

    private double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
        yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper, 
        deltaY, dispLowerBound, dispUpperBound, initHoriz, initVert, initWidth, 
        initHeight, lowZ, highZ, theMean, theStDev;
    
    // normalScores = what qqnorm in R would give you
    // translatedNormalScores = are z's from qqnorm translated to the data scale
    private double[] normalScores, translatedNormalScores, rawScores;

    private String descriptionOfDifference;
    private String strPreLabel, strPostLabel;
    private ObservableList<String> categoryLabels;
    
    // My classes
    //Data_Manager dm;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;   
    NormProb_DiffModel normProb_DiffModel;
    StandardNormal standardNormal;
    
    // POJOs / FX
    AnchorPane anchorPane;
    Canvas graphCanvas;
    GraphicsContext gc; // Required for drawing on the Canvas

    CategoryAxis xAxis;
    Pane theContainingPane;    
    Text txtTitle1, txtTitle2;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;    

    public NormProb_DiffView ( NormProb_DiffModel normProb_DiffModel, Exploration_Dashboard exploration_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        //System.out.println("81 NormProb_DiffView, constructing");
        //System.out.println("82 NormProb_DiffView, normProb_DiffModel = " + normProb_DiffModel);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        standardNormal = new StandardNormal();
        this.normProb_DiffModel = normProb_DiffModel;
        nLegalDataPoints = normProb_DiffModel.getNLegalDataPoints();
        normalScores = new double[nLegalDataPoints];
        translatedNormalScores = new double[nLegalDataPoints];
        rawScores = new double[nLegalDataPoints];
        normalScores = normProb_DiffModel.getTheNormalScores();
        rawScores = normProb_DiffModel.getTheRawData();
        
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        
        Arrays.sort(normalScores);
        Arrays.sort(rawScores);

        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            translatedNormalScores[ith] = theStDev * normalScores[ith] + theMean;
        }
        
        strPreLabel = "Normal Distribution";
        strPostLabel = normProb_DiffModel.getSubTitle();
        descriptionOfDifference = strPreLabel + " & " + strPostLabel;

        initStuff();
    }
    
    public NormProb_DiffView ( NormProb_DiffModel normProb_DiffModel, ANOVA1_Cat_Dashboard anova1_Cat_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        //System.out.println("115 NormProb_DiffView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        standardNormal = new StandardNormal();
        this.normProb_DiffModel = normProb_DiffModel;
        nLegalDataPoints = normProb_DiffModel.getNLegalDataPoints();
        normalScores = new double[nLegalDataPoints];
        translatedNormalScores = new double[nLegalDataPoints];
        rawScores = new double[nLegalDataPoints];
        normalScores = normProb_DiffModel.getTheNormalScores();
        rawScores = normProb_DiffModel.getTheRawData();
        
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        
        Arrays.sort(normalScores);
        Arrays.sort(rawScores);

        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            translatedNormalScores[ith] = theStDev * normalScores[ith] + theMean;
        }
        
        strPreLabel = "Normal Distribution";
        strPostLabel = "Residuals";
        descriptionOfDifference = strPreLabel + " vs. " + strPostLabel;

        initStuff();
    }
    
    public NormProb_DiffView ( NormProb_DiffModel normProb_DiffModel, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        //System.out.println("149 NormProb_DiffView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        standardNormal = new StandardNormal();
        this.normProb_DiffModel = normProb_DiffModel;
        nLegalDataPoints = normProb_DiffModel.getNLegalDataPoints();
        normalScores = new double[nLegalDataPoints];
        translatedNormalScores = new double[nLegalDataPoints];
        rawScores = new double[nLegalDataPoints];
        normalScores = normProb_DiffModel.getTheNormalScores();
        rawScores = normProb_DiffModel.getTheRawData();
        
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        
        Arrays.sort(normalScores);
        Arrays.sort(rawScores);

        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            translatedNormalScores[ith] = theStDev * normalScores[ith] + theMean;
        }
        
        strPreLabel = "Normal Distribution";
        strPostLabel = "Residuals";
        descriptionOfDifference = strPreLabel + " vs. " + strPostLabel;

        initStuff();
    }
    
    public NormProb_DiffView ( NormProb_DiffModel normProb_DiffModel, Matched_t_Dashboard matched_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        //System.out.println("183 NormProb_DiffView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        standardNormal = new StandardNormal();
        this.normProb_DiffModel = normProb_DiffModel;
        nLegalDataPoints = normProb_DiffModel.getNLegalDataPoints();
        normalScores = new double[nLegalDataPoints];
        translatedNormalScores = new double[nLegalDataPoints];
        rawScores = new double[nLegalDataPoints];
        normalScores = normProb_DiffModel.getTheNormalScores();
        rawScores = normProb_DiffModel.getTheRawData();
        
        theMean = normProb_DiffModel.getTheMean();
        theStDev = normProb_DiffModel.getTheStDev();
        
        Arrays.sort(normalScores);
        Arrays.sort(rawScores);

        // Translate from standard normal to the raw data scale
        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            translatedNormalScores[ith] = theStDev * normalScores[ith] + theMean;
        }
        
        strPreLabel = "Normal Distribution";
        strPostLabel = normProb_DiffModel.getSubTitle();
        descriptionOfDifference = strPreLabel + " & " + strPostLabel;

        initStuff();
    }
 
    private void initStuff() {
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.addAll(strPreLabel, strPostLabel);
        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();
  
        makeItHappen();        
    }
    
    private void makeItHappen() {              
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
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
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void initializeGraphParameters() {  
        initial_yMin = Math.min(rawScores[0], translatedNormalScores[0]);
        initial_yMax = Math.max(rawScores[nLegalDataPoints - 1], translatedNormalScores[nLegalDataPoints - 1]);
        initial_yRange = initial_yMax - initial_yMin; 

        yMin = initial_yMin;
        yMax = initial_yMax;
        yRange = initial_yRange;

        yAxis = new JustAnAxis(yMin - 0.075 * yRange, yMax + 0.075 * yRange);
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
        double vertLineAt = 145.;
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
        
        gc.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight());
        
        double startX = xAxis.getDisplayPosition(strPreLabel);
        double endX = xAxis.getDisplayPosition(strPostLabel);
        
        //  Loop through the points
        for (int theIthPair = 0; theIthPair < nLegalDataPoints; theIthPair++) {
            double startY = yAxis.getDisplayPosition(translatedNormalScores[theIthPair]);
            double endY = yAxis.getDisplayPosition(rawScores[theIthPair]);      

            gc.setLineWidth(1);
            gc.setStroke(Color.BLACK);

            // x, y, w, h
            
            //gc.strokeLine(startX, startY, endX, endY);
            gc.strokeLine(vertLineAt, startY, endX, endY);
            gc.setFill(Color.RED);
            // gc.fillOval(startX - 4, startY - 4, 8, 8);
            gc.fillOval(vertLineAt - 4, startY - 4, 8, 8);
            gc.fillOval(endX - 4, endY - 4, 8, 8);  

        }
        
        // Draw the normal curve
        lowZ = theMean - 3.25 * theStDev;
        highZ = theMean +3.25 * theStDev;
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLUE);
        //double vertLineAt = 145.;
        gc.setLineWidth(2);
        gc.strokeLine(vertLineAt, yAxis.getDisplayPosition(lowZ), vertLineAt, yAxis.getDisplayPosition(highZ));
        
        for (int ith = -325; ith < 325; ith++) {
            double theZ = (double)ith / 100.0;
            double theRaw = theStDev * theZ + theMean;
            double zDensity = standardNormal.getDensity(theZ);
            double dotAtX = 140. - 160.0 * zDensity;
            double dotAtY = yAxis.getDisplayPosition(theRaw);
            gc.fillOval(dotAtX, dotAtY, 2, 2);  
        }
        
        String meanString =       "  Mean = " + String.format("%9.3f", theMean);
        String standDevString =   "St Dev = " + String.format("%9.3f", theStDev);
        gc.fillText(meanString, 10, 10);
        gc.fillText(standDevString, 10, 30);        
        
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
        // yAxis.setOnMouseClicked(yAxisMouseHandler); 
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        // yAxis.setOnMouseEntered(yAxisMouseHandler); 
        // yAxis.setOnMouseExited(yAxisMouseHandler); 
        // yAxis.setOnMouseMoved(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler);         
        dragableAnchorPane.setOnMouseReleased(scatterplotMouseHandler);
    }
    
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
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
                    
                    if (frac < 0.5)  {
                        newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else  
                    {
                        newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) { 
                    
                    if (frac < 0.5)  {
                        newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else
                    {
                        newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
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
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                ZoomieThing zoomieThing = new ZoomieThing(dragableAnchorPane);
            }
        }
    };  
    
   public Pane getTheContainingPane() { return theContainingPane; }    
}
