/**************************************************
 *             RCB_MainEffect_BView               *
 *                   05/24/24                     *
 *                     12:00                      *
 *************************************************/
package anova2;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class ANOVA2_RCB_MainEffect_BView extends ANOVA2_Views_Super { 
   
    // POJOs
    double middleXPosition;
    
    String waldoFile = "";
    //String waldoFile = "ANOVA2_RCB_MainEffect_BView";
    
    //  My Classes
    ANOVA2_RCB_Model anova2_RCB_Model;

    // FX Classes
    AnchorPane anchorPane_MainEffectB;

    ANOVA2_RCB_MainEffect_BView(ANOVA2_RCB_Model anova2_RCB_Model, 
            ANOVA2_RCB_wReplicates_Dashboard anova2_RCB_wReplicates_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super();
        this.anova2_RCB_Model = anova2_RCB_Model;
        dm = anova2_RCB_Model.getDataManager();
        dm.whereIsWaldo(47, waldoFile, "Constructing");        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        nFactorA_Levels = anova2_RCB_Model.getNFactorA_Levels();
        nFactorB_Levels = anova2_RCB_Model.getNFactorB_Levels();
        
        categoryLabels = FXCollections.observableArrayList();        
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();  
        strTopLabels = FXCollections.observableArrayList();
        strLeftLabels = FXCollections.observableArrayList();
        
        preStrTopLabels = anova2_RCB_Model.getFactorBLevels();
        preStrLeftLabels = anova2_RCB_Model.getFactorALevels();
        int nTopLabels = preStrTopLabels.size() - 1;
        int nLeftLabels = preStrLeftLabels.size() - 1;
       
        for (int ithTopLabel = 0; ithTopLabel < nTopLabels; ithTopLabel++) {
            strTopLabels.add(preStrTopLabels.get(ithTopLabel + 1)); 
        }
        
        for (int ithLeftLabel = 0; ithLeftLabel < nLeftLabels; ithLeftLabel++) {
            strLeftLabels.add(preStrLeftLabels.get(ithLeftLabel + 1));
        }
        
        categoryLabels.addAll(strTopLabels);
        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        
        factorA_Levels.addAll(anova2_RCB_Model.getFactorALevels());
        factorB_Levels.addAll(anova2_RCB_Model.getFactorBLevels()); 
    }
    
    public void completeTheDeal() {
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        theContainingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        canvas_ANOVA2.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvas_ANOVA2.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane_MainEffectB = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .addAll(anchorPane_TitleInfo, canvas_ANOVA2, categoryAxis_X, yAxis);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void setUpUI() {        
        canvas_ANOVA2 = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        gc = canvas_ANOVA2.getGraphicsContext2D();        
    }
        
    public void doThePlot() {
        double xx0, yy0, xx, yy;
        xx0 = 0.0; yy0 = 0.0;   //  Satisfy the compiler

        text1Width = txtTitle1.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        //double hBoxWidth = paneWidth;        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.85 * tempHeight);      
        
        AnchorPane.setTopAnchor(categoryAxis_X, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(categoryAxis_X, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(categoryAxis_X, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(categoryAxis_X, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(canvas_ANOVA2, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(canvas_ANOVA2, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(canvas_ANOVA2, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(canvas_ANOVA2, 0.2 * tempHeight);
        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        tempPos1 = categoryAxis_X.getDisplayPosition(factorB_Levels.get(1));      
        tempPos0 = categoryAxis_X.getDisplayPosition(factorB_Levels.get(0));      
        bigTickInterval = tempPos1 - tempPos0;
   
        gc.clearRect(0, 0 , canvas_ANOVA2.getWidth(), canvas_ANOVA2.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorB_Levels; theBetweenBatch++) {
            middleXPosition = categoryAxis_X.getDisplayPosition(factorB_Levels.get(theBetweenBatch));
            int theAppropriateLevel = theBetweenBatch;
            tempUCDO = anova2_RCB_Model.getPrelimB().getIthUCDO(theAppropriateLevel);   
            nDataPoints = tempUCDO.getLegalN();
            gc.setLineWidth(2); 
            double theMean = tempUCDO.getTheMean();
            xx = middleXPosition;
            yy = yAxis.getDisplayPosition(theMean); 
            gc.fillOval(xx - 6, yy - 6, 12, 12);
            
            if (theBetweenBatch == 1) {
                xx0 = xx;
                yy0 = yy;
                gc.moveTo(xx0, yy0);
            } 
            else {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(2);
                
                gc.strokeLine(xx, yy, xx0, yy0);
                xx0 = xx;
                yy0 = yy;
            }
        }   //  Loop through between batches 
        
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
    }   // End Plot
   
    private void initializeGraphParams() { 
        /****************************************************************
        * Find range of means for both levels.  This will determine the *
        * initial vertical scale limits.                                *
        ****************************************************************/        
        anchorPane_TitleInfo = new AnchorPane();
        strTitle1 = "Main effect: " + anova2_RCB_Model.getTreatmentLabel();
        txtTitle1 = new Text(strTitle1);         
        txtTitle1.getStyleClass().add("titleLabel");                 
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        anchorPane_TitleInfo.getChildren().add(txtTitle1 /*, txtTitle2 */);

        initial_yMin = Double.MAX_VALUE;
        initial_yMax = Double.MIN_VALUE;
        
        for (int levelsA = 1; levelsA < nFactorA_Levels; levelsA++) {
            tempUCDO = anova2_RCB_Model.getPrelimA().getIthUCDO(levelsA);
            double tempMean = tempUCDO.getTheMean();
            initial_yMin = Math.min(tempMean, initial_yMin);
            initial_yMax = Math.max(tempMean, initial_yMax);
        }

        for (int levelsB = 1; levelsB < nFactorB_Levels; levelsB++) {
            tempUCDO = anova2_RCB_Model.getPrelimB().getIthUCDO(levelsB);
            double tempMean = tempUCDO.getTheMean();
            initial_yMin = Math.min(tempMean, initial_yMin);
            initial_yMax = Math.max(tempMean, initial_yMax);
        }        
       
        initial_yRange = initial_yMax - initial_yMin;
        
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setBounds(initial_yMin, initial_yMax);
        yMin = initial_yMin; yMax = initial_yMax; yRange = initial_yRange;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        categoryAxis_X = new CategoryAxis(categoryLabels);
        categoryAxis_X.setSide(Side.BOTTOM); 
        categoryAxis_X.setAutoRanging(true);
        categoryAxis_X.setLabel(anova2_RCB_Model.getTreatmentLabel());
        categoryAxis_X.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        categoryAxis_X.setPrefWidth(40);   
        categoryAxis_X.setLayoutX(500); categoryAxis_X.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(anova2_RCB_Model.getResponseLabel());
        
        setHandlers();
    }
    
    private void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }

    public Pane getTheContainingPane() { return theContainingPane; }
}

