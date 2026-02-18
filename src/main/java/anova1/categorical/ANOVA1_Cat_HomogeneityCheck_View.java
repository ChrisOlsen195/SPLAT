/**************************************************
 *         ANOVA1_Cat_HomogeneityCheck_View       *
 *                    05/13/24                    *
 *                     00:00                      *
 *************************************************/
package anova1.categorical;

import anova1.quantitative.ANOVA1_Quant_Dashboard;
import anova1.quantitative.ANOVA1_Quant_Model;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.CheckBoxRow;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import proceduresManyUnivariate.MultUni_Model;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class ANOVA1_Cat_HomogeneityCheck_View extends Region { 
    // POJOs
    
    public boolean dragging, yAxisHasForcedLowEnd, yAxisHasForcedHighEnd;
    //public boolean[] checkBoxSettings;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public int nEntities, nCheckBoxes, nDataPoints;
    private double treatMean, allDataStandDev,
            initial_MinResid, initial_MaxResid;
    
    // *******************************************************
    public double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper,
           deltaY, dispLowerBound, dispUpperBound, yDispLowerBound, 
           yDispUpperBound, errorBarLength, initHoriz, initVert, initWidth, 
           initHeight, yAxisForcedLowEnd, yAxisForcedHighEnd,
           inityMin_4_Errors, inityMax_4_Errors;  
    
    public double[] means, stDevs;
    
    public String whichView, explanVar, responseVar, subTitle, whichModel;
    public String[] strCheckBoxDescriptions, axisLabels;
    public ObservableList<String> allTheLabels, categoryLabels; 
 
    // My Classes
    public ANOVA1_Cat_Dashboard anova1_Cat_Dashboard;
    public ANOVA1_Cat_Model anova1_Cat_Model;
    public ANOVA1_Quant_Dashboard anova1_Quant_Dashboard;
    public ANOVA1_Quant_Model anova1_Quant_Model;
    public CheckBoxRow checkBoxRow;
    public HBox theActualRow;
    public DragableAnchorPane dragableAnchorPane;
    public JustAnAxis yResidAxis;
    public MultUni_Model multiUni_Model;
    public QuantitativeDataVariable allData_QDV, batchQDV;
    public ArrayList<QuantitativeDataVariable> allTheQDVs;
    public ArrayList<UnivariateContinDataObj> allTheUCDOs;
    public VerticalBoxPlot_Model vBoxModel;
    
    // POJOs FX
    public AnchorPane anchorTitleInfo, anchorPane;    
    public Canvas quantCatCanvas;
    public CategoryAxis xAxis;  
    public CheckBox[] quantCat_CheckBoxes;    
    public GraphicsContext gcQuantCat; // Required for drawing on the Canvas
    public Pane quantCat_ContainingPane;
    public Text errorBarDescription, txtTitle1, txtTitle2;  
    // *******************************************************

    private String leveneStat, levenePValue;
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Cat_HomogeneityCheck_View";
    String waldoFile = "";
    
    // My classes
    public Data_Manager dm;
    
    private UnivariateContinDataObj batchUCDO;
    
    ANOVA1_Cat_HomogeneityCheck_View(ANOVA1_Cat_Model anova1_Cat_Model, ANOVA1_Cat_Dashboard anova1_Cat_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        if (printTheStuff == true) {
            System.out.println("112 *** ANOVA1_Cat_HomogeneityCheck_View, Constructing");
        }   
        dm = anova1_Cat_Model.getDataManager();
        dm.whereIsWaldo(115, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        whichModel = "anovaCat"; 
        this.anova1_Cat_Model = anova1_Cat_Model;
        this.anova1_Cat_Dashboard = anova1_Cat_Dashboard;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = anova1_Cat_Model.getAllTheQDVs();
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = anova1_Cat_Model.getCategoryLabels();
        quantCatCanvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        quantCatCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        quantCatCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();
        gcQuantCat.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        quantCat_ContainingPane = new Pane();
        nCheckBoxes = 0;
        whichModel = "anovaCat";
        whichView = "Homogeneity Check";
        nCheckBoxes = 0;
        explanVar = anova1_Cat_Dashboard.getExplanVar();
        responseVar = anova1_Cat_Dashboard.getResponseVar();
        String strForTitle1 = "Residuals' Homogeneity Check";
        String strForTitle2 = "Residuals vs. " + explanVar;
        txtTitle1 = new Text (60, 30, strForTitle1);
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void completeTheDeal() { 
        dm.whereIsWaldo(148, waldoFile, "completeTheDeal()");
        initializeGraphParameters();   
        setUpAnchorPane();
        setHandlers();
        quantCat_ContainingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .remove(checkBoxRow);
        
        initial_MinResid = Double.MAX_VALUE;
        initial_MaxResid = -Double.MAX_VALUE;
        
        for (int theBatch = 0; theBatch < nEntities; theBatch++) {
            batchQDV = new QuantitativeDataVariable();
            batchQDV = anova1_Cat_Model.getIthQDV(theBatch);
            batchUCDO = new UnivariateContinDataObj("78 ANOVA1_Cat_Homog_Check_View", batchQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch));
            nDataPoints = batchUCDO.getLegalN();
            treatMean = batchQDV.getTheMean();
            
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double temp1 = batchUCDO.getIthSortedValue(dataPoint);
                double resid = temp1 - treatMean;
                initial_MinResid = Math.min(initial_MinResid, resid); 
                initial_MaxResid = Math.max(initial_MaxResid, resid);
            }
        }   //  Loop through batches  
        
        if (printTheStuff) {
            System.out.println("178 *** ANOVA1_Cat_HomogeneityCheck_View, init_MinMax_Resid = " + initial_MinResid + " / " + initial_MaxResid);
        }
        
        if (initial_MaxResid >0) { initial_MaxResid *= 1.125; }
        
        if (initial_MinResid < 0) { initial_MinResid *= 1.125; }

        deltaY = .005 * (initial_MaxResid - initial_MinResid);
        yResidAxis.setLowerBound(initial_MinResid);
        yResidAxis.setUpperBound(initial_MaxResid); 
        doTheGraph();   
    }
    
    public void initializeGraphParameters() {  
        if (printTheStuff == true) {
            System.out.println("200 *** ANOVA1_Cat_HomogeneityCheck_View, initializeGraphParameters()");
        }
        double tempLow, tempHigh;
        nEntities = allTheQDVs.size();
        means = new double[nEntities];
        stDevs = new double[nEntities];
        allTheUCDOs = new ArrayList<>();
        
        for (int iVars = 0; iVars < nEntities; iVars++) {
            UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj("QuantCat_View", allTheQDVs.get(iVars));
            allTheUCDOs.add(tempUCDO);
            tempUCDO.doMedianBasedCalculations(); 
        }
        
        // Reconstruct category labels w/o the "All"
        for (int ithLab = 0; ithLab < allTheLabels.size(); ithLab++) {
            categoryLabels.add(allTheLabels.get(ithLab));
        }

        for (int iVars = 0; iVars < nEntities; iVars++) {
            means[iVars] = allTheQDVs.get(iVars).getTheMean();
            stDevs[iVars] = allTheQDVs.get(iVars).getTheStandDev();
        }
        
        anchorPane = new AnchorPane();
        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setAutoRanging(true);
        
        xAxis.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        xAxis.setPrefWidth(40);
        
        xAxis.setPrefSize(anchorPane.getWidth() - 50, 40);   
        xAxis.setLayoutX(500); xAxis.setLayoutY(50); 

        initial_yMin = Double.MAX_VALUE;
        initial_yMax = -Double.MAX_VALUE;

        inityMin_4_Errors = allTheUCDOs.get(0).getTheMean();
        inityMax_4_Errors = allTheUCDOs.get(0).getTheMean();        
        
        for (int ithLevel = 0; ithLevel < nEntities; ithLevel++) {
            switch(whichModel) {
                case "vBox":
                    batchQDV = vBoxModel.getIthQDV(ithLevel);
                    break;
                
                case "multUni":
                    batchQDV = multiUni_Model.getIthQDV(ithLevel);
                    break;
                
                case "anovaCat":
                    batchQDV = anova1_Cat_Model.getIthQDV(ithLevel);
                    break;
                    
                default:
                    String switchFailure = "Switch failure: QuantCat_View 249 " + whichModel;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                break;                 
            }
            
            tempLow = batchQDV.getTheMean() - anova1_Cat_Model.getPostHocPlusMinus();
            tempHigh = batchQDV.getTheMean() + anova1_Cat_Model.getPostHocPlusMinus();
            initial_yMin = Math.min(initial_yMin, tempLow); 
            initial_yMax = Math.max(initial_yMax, tempHigh); 
            checkBoxRow = new CheckBoxRow();
            theActualRow = checkBoxRow.getTheCBRow();
        }
            
        initial_yRange = initial_yMax - initial_yMin;
        yResidAxis = new JustAnAxis(initial_yMin, initial_yMax);

        yResidAxis.setSide(Side.LEFT);
        yResidAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yResidAxis.setLayoutX(500); yResidAxis.setLayoutY(50);
               
        yMin = initial_yMin - .05 * initial_yRange;
        yMax = initial_yMax + .05 * initial_yRange;
        yRange = initial_yRange;    
        deltaY = .005 * yRange;
        newY_Lower = yMin; newY_Upper = yMax;

        // For some graphs the LowerBound will be reset to 0.0
        yResidAxis.setLowerBound(newY_Lower ); 
        yResidAxis.setUpperBound(newY_Upper );
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
        
        leveneStat = "Levene's W = " + String.format("%5.3f", anova1_Cat_Model.getLevenesStat());
        levenePValue = "pValue = " + String.format("%5.3f", anova1_Cat_Model.getLevenesPValue());
        gcQuantCat.fillText(leveneStat, 10, 10);
        gcQuantCat.fillText(levenePValue, 10, 25);
        
        allData_QDV = new QuantitativeDataVariable();
        allData_QDV  = anova1_Cat_Model.getIthQDV(0);
            
        allDataStandDev = anova1_Cat_Model.getMSError();
            
        for (int theBatch = 0; theBatch < nEntities; theBatch++) {
            batchQDV = new QuantitativeDataVariable();
            batchQDV = anova1_Cat_Model.getIthQDV(theBatch);
            batchUCDO = new UnivariateContinDataObj("157 ANOVA1_Cat_Homog_Check_View", batchQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch));

            nDataPoints = batchUCDO.getLegalN();
            treatMean = batchQDV.getTheMean();            
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double temp1 = batchUCDO.getIthSortedValue(dataPoint);
                double resid = temp1 - treatMean;
                double yy = yResidAxis.getDisplayPosition(resid);
                gcQuantCat.strokeOval(xx - 5, yy - 5, 10, 10);
            }
        }   //  Loop through batches
        
        quantCat_ContainingPane.requestFocus();
        quantCat_ContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                //System.out.println("Doing it -- Best Fit");
                WritableImage writableImage = quantCat_ContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));       
    }
    
    public void setUpAnchorPane() {
        if (printTheStuff) {
            System.out.println("404 *** ANOVA1_Cat_HomogeneityCheck_View, setUpAnchorPane()");
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
                           .addAll(theActualRow, txtTitle1, txtTitle2, xAxis, yResidAxis, quantCatCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void setHandlers() {
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
                            newY_Upper = yResidAxis.getUpperBound() + deltaY;
                    }
                    else  
                    {
                        if (!yAxisHasForcedLowEnd)
                            newY_Lower = yResidAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                    if (frac < 0.5) {
                        if (!yAxisHasForcedHighEnd)
                            newY_Upper = yResidAxis.getUpperBound() - deltaY;
                    }
                    else
                    {
                        if (!yAxisHasForcedLowEnd)
                            newY_Lower = yResidAxis.getLowerBound() - deltaY;
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
     
    EventHandler<MouseEvent> quantCatMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) {  }
    }; 
    
    public Pane getTheContainingPane() { return quantCat_ContainingPane; } 
}
