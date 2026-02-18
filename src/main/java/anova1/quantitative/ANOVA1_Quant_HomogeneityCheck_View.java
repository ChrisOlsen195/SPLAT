/**************************************************
 *        ANOVA1_Quant_HomogeneityCheck_View      *
 *                    05/13/25                    *
 *                     15:00                      *
 *************************************************/
package anova1.quantitative;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.DragableAnchorPane;
import genericClasses.JustAnAxis;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import splat.Data_Manager;
import superClasses.BivariateScale_View;

public class ANOVA1_Quant_HomogeneityCheck_View extends BivariateScale_View { 
    // POJOs
    
   public boolean dragging, yAxisHasForcedLowEnd, yAxisHasForcedHighEnd;
   
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
   
    public int nGroups, nCheckBoxes, nThisGroup;
    private double treatMean, initial_MinResid, initial_MaxResid;
    
    // *******************************************************
    public double initial_yResidRange,  deltaResidY, 
                  yDispLowerBound, yDispUpperBound, errorBarLength, 
                  yAxisForcedLowEnd, yAxisForcedHighEnd,
                  inityMin_4_Errors, inityMax_4_Errors, initial_xMin, 
                  initial_xMax;
    
    public double[] means, stDevs;
    
    //String waldoFile = "ANOVA1_Quant_Homog_Check_View";
    String waldoFile = "";
    
    public String whichView, explanVar, responseVar, subTitle, whichModel;
    private String leveneStat, levenePValue;
    public String[] strCheckBoxDescriptions, axisLabels;
    public ObservableList<String> allTheLabels, categoryLabels; 

    // My classes
    public ANOVA1_Quant_Dashboard anova1_Quant_Dashboard;
    public ANOVA1_Quant_Model anova1_Quant_Model;
    public Data_Manager dm;
    public JustAnAxis yResidAxis;   // xAxis is defined in BivariateScale_View
    public QuantitativeDataVariable allData_QDV, batchQDV;
    private UnivariateContinDataObj groupUCDO;
    public ArrayList<QuantitativeDataVariable> allTheQDVs;
    
    // POJOs FX
    public AnchorPane anchorTitleInfo, anchorPane;    
    public Canvas quantCatCanvas;  
    public GraphicsContext gcQuantCat; // Required for drawing on the Canvas
    public Pane qanova1_ContainingPane;
    public Text errorBarDescription, txtTitle1, txtTitle2;  
    
    ANOVA1_Quant_HomogeneityCheck_View(ANOVA1_Quant_Model anova1_Quant_Model, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {  
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = anova1_Quant_Model.getDataManager();
        dm.whereIsWaldo(85, waldoFile, "Constructing...");
        nCheckBoxes = 0;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.anova1_Quant_Model = anova1_Quant_Model;
        this.anova1_Quant_Dashboard = anova1_Quant_Dashboard;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = anova1_Quant_Model.getAllTheQDVs();
        allTheLabels = FXCollections.observableArrayList();
        allTheLabels = anova1_Quant_Model.getCategoryLabels();
        quantCatCanvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        quantCatCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        quantCatCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();
        gcQuantCat.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        qanova1_ContainingPane = new Pane();
        nCheckBoxes = 0;
        whichModel = "anovaCat";
        nCheckBoxes = 0;
        String strForTitle1 = "Residuals' Homogeneity Check";
        explanVar = anova1_Quant_Model.getExplanatoryVariable();
        String strForTitle2 = "Residuals vs. " + explanVar;
        txtTitle1 = new Text(50, 25, strForTitle1);
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void completeTheDeal() { 
        dm.whereIsWaldo(115, waldoFile, "*** completeTheDeal()");
        initializeGraphParameters();   
        setUpAnchorPane();
        setHandlers();
        qanova1_ContainingPane = dragableAnchorPane.getTheContainingPane();  
        
        dragableAnchorPane.getTheAP()
                           .getChildren();
        doTheGraph();   
    }
    
    public void initializeGraphParameters() {  
        if (printTheStuff) {
            System.out.println("128 *** ANOVA1_Quant_HomogeneityCheck_View, initializeGraphParameters()");
        }
        nGroups = allTheQDVs.size();
        means = new double[nGroups];
        stDevs = new double[nGroups];

        for (int iVars = 0; iVars < nGroups; iVars++) {
            means[iVars] = allTheQDVs.get(iVars).getTheMean();
            stDevs[iVars] = allTheQDVs.get(iVars).getTheStandDev();
        }
        
        anchorPane = new AnchorPane();
        xDataMin = Double.MAX_VALUE;
        xDataMax = Double.MIN_VALUE;  

        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            double daValue = Double.parseDouble(allTheQDVs.get(ithGroup).getTheVarLabel());
            if (daValue < xDataMin) { xDataMin = daValue; }
            if (daValue > xDataMax) { xDataMax = daValue; }
        }
        xAxis = new genericClasses.JustAnAxis(xDataMin, xDataMax);
        xAxis.setSide(Side.BOTTOM); 
        
        xAxis.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        xAxis.setPrefWidth(40);
        
        xAxis.setPrefSize(anchorPane.getWidth() - 50, 40);   
        xAxis.setLayoutX(500); xAxis.setLayoutY(50); 
        
        initial_xMin = xDataMin;
        initial_xMax = xDataMax;

        initializeResidAxis();
    }
    
    private void initializeResidAxis() {
        if (printTheStuff) {
            System.out.println("165 *** ANOVA1_Quant_HomogeneityCheck_View, initializeResidAxis()");
        }        
        initial_MinResid = Double.MAX_VALUE;
        initial_MaxResid = -Double.MAX_VALUE;
       
        for (int theBatch = 0; theBatch < nGroups; theBatch++) {
            batchQDV = new QuantitativeDataVariable();
            batchQDV = anova1_Quant_Model.getIthQDV(theBatch);
            groupUCDO = new UnivariateContinDataObj("--- 173 ANOVA1_Cat_Homog_Check_View", batchQDV);

            nThisGroup = groupUCDO.getLegalN();
            treatMean = batchQDV.getTheMean();
            
            for (int dataPoint = 0; dataPoint < nThisGroup; dataPoint++) {
                double temp1 = groupUCDO.getIthSortedValue(dataPoint);
                double resid = temp1 - treatMean;
                initial_MinResid = Math.min(initial_MinResid, resid); 
                initial_MaxResid = Math.max(initial_MaxResid, resid);
            }
        }

        if (initial_MaxResid >0) { initial_MaxResid *= 1.125; }
        
        if (initial_MinResid < 0) { initial_MinResid *= 1.125; }

        deltaResidY = .005 * (initial_MaxResid - initial_MinResid);

        initial_yResidRange = initial_MaxResid - initial_MinResid;
        yResidAxis = new JustAnAxis(initial_MinResid, initial_MaxResid);

        yResidAxis.setSide(Side.LEFT);
        yResidAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yResidAxis.setLayoutX(500); yResidAxis.setLayoutY(50);  
    }
    
    public void setUpAnchorPane() {
        if (printTheStuff) {
            System.out.println("202 *** ANOVA1_Quant_HomogeneityCheck_View, setUpAnchorPane()");
        }
        dragableAnchorPane = new DragableAnchorPane();
        quantCatCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        quantCatCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle1, txtTitle2, xAxis, yResidAxis, quantCatCanvas);    
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
   
    public void doTheGraph() {   
        yResidAxis.setForcedAxisEndsFalse(); // Just in case
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
        
        AnchorPane.setTopAnchor(yResidAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yResidAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yResidAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yResidAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(quantCatCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(quantCatCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(quantCatCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(quantCatCanvas, 0.1 * tempHeight);
        
        gcQuantCat.clearRect(0, 0 , quantCatCanvas.getWidth(), quantCatCanvas.getHeight());

        leveneStat = "Levene's W = " + String.format("%5.3f", anova1_Quant_Model.getLevenesStat());
        levenePValue = "pValue = " + String.format("%5.3f", anova1_Quant_Model.getLevenesPValue());
        gcQuantCat.fillText(leveneStat, 10, 10);
        gcQuantCat.fillText(levenePValue, 10, 25);
        
        allData_QDV = new QuantitativeDataVariable();
        allData_QDV  = anova1_Quant_Model.getIthQDV(0);
     
        for (int theBatch = 0; theBatch < nGroups; theBatch++) {
            batchQDV = new QuantitativeDataVariable();
            batchQDV = anova1_Quant_Model.getIthQDV(theBatch);
            groupUCDO = new UnivariateContinDataObj("--- 267 ANOVA1_Cat_Homog_Check_View", batchQDV);
            
            String daQuantLabel = batchQDV.getTheVarLabel();
            double daScalePosition = Double.parseDouble(daQuantLabel);
            double daXPosition = xAxis.getDisplayPosition(daScalePosition);
            nThisGroup = groupUCDO.getLegalN();
            treatMean = batchQDV.getTheMean();            
            for (int dataPoint = 0; dataPoint < nThisGroup; dataPoint++) {
                double xx = daXPosition;
                double temp1 = groupUCDO.getIthSortedValue(dataPoint);
                double resid = temp1 - treatMean;
                double yy = yResidAxis.getDisplayPosition(resid);
                gcQuantCat.strokeOval(xx - 5, yy - 5, 10, 10);
            }
        }   //  Loop through batches

        qanova1_ContainingPane.requestFocus();
        qanova1_ContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = qanova1_ContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));       
    }
    
    public void setHandlers() {
        xAxis.setOnMouseDragged(xAxisMouseHandler);  
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler);        
        yResidAxis.setOnMouseDragged(yAxisMouseHandler); 
        yResidAxis.setOnMousePressed(yAxisMouseHandler); 
        yResidAxis.setOnMouseReleased(yAxisMouseHandler);
        dragableAnchorPane.setOnMouseReleased(quantCatMouseHandler);
    }
    
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)  { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                dragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging) {
                    yResidAxis.setLowerBound(newY_Lower ); 
                    yResidAxis.setUpperBound(newY_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double yPix_Dragging = mouseEvent.getY();  

                newY_Lower = yResidAxis.getLowerBound();
                newY_Upper = yResidAxis.getUpperBound(); 
 
                dispLowerBound = yResidAxis.getDisplayPosition(yResidAxis.getLowerBound());
                dispUpperBound = yResidAxis.getDisplayPosition(yResidAxis.getUpperBound());

                double frac = mouseEvent.getY() / dispLowerBound;

                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5)  {
                        if (!yAxisHasForcedHighEnd)
                            newY_Upper = yResidAxis.getUpperBound() + deltaResidY;
                    }
                    else  
                    {
                        if (!yAxisHasForcedLowEnd)
                            newY_Lower = yResidAxis.getLowerBound() + deltaResidY;
                    }
                }
                else 
                    if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                        if (frac < 0.5) {
                            if (!yAxisHasForcedHighEnd)
                                newY_Upper = yResidAxis.getUpperBound() - deltaResidY;
                        }
                        else
                        {
                            if (!yAxisHasForcedLowEnd)
                                newY_Lower = yResidAxis.getLowerBound() - deltaResidY;
                        }
                    }    

                if (yAxisHasForcedLowEnd) {
                    newY_Lower = yAxisForcedLowEnd;                   
                }
            
                if (yResidAxis.getHasForcedHighScaleEnd()) {
                    newY_Upper = yAxisForcedHighEnd;                    
                }
                
                yResidAxis.setLowerBound(newY_Lower ); 
                yResidAxis.setUpperBound(newY_Upper ); 

                dispLowerBound = yResidAxis.getDisplayPosition(yResidAxis.getLowerBound());
                dispUpperBound = yResidAxis.getDisplayPosition(yResidAxis.getUpperBound());
                
                yPix_MostRecentDragPoint = mouseEvent.getY();
                
                doTheGraph();
            }
        }
    };
     
    EventHandler<MouseEvent> quantCatMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {  }
    }; 
    
    public Pane getTheContainingPane() { return qanova1_ContainingPane; } 
}

