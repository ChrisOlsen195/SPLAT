/**************************************************
 *                  QuantCat_View                 *
 *                    09/03/24                    *
 *                      00:00                     *
 *************************************************/
package superClasses;

import anova1.categorical.ANOVA1_Cat_Dashboard;
import anova1.categorical.ANOVA1_Cat_Model;
import anova1.quantitative.ANOVA1_Quant_Dashboard;
import anova1.quantitative.ANOVA1_Quant_Model;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.DragableAnchorPane;
import genericClasses.JustAnAxis;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import proceduresManyUnivariate.MultUni_Dashboard;
import proceduresManyUnivariate.MultUni_Model;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import utilityClasses.MyAlerts;
import genericClasses.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;

public class QuantCat_View extends Region {
    
    // POJOs
    public boolean dragging, yAxisHasForcedLowEnd, yAxisHasForcedHighEnd;
    public boolean[] checkBoxSettings;
    
    public int nEntities, nCheckBoxes, nDataPoints;
    
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
    public JustAnAxis yAxis;
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
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;    
    
    public QuantCat_View(VerticalBoxPlot_Model vBoxModel, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  {
        System.out.println("\n97 QuantCat_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = vBoxModel.getAllTheQDVs();
        quantCatCanvas = new Canvas(withThisWidth, withThisHeight);
        quantCatCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        quantCatCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();
        gcQuantCat.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));        
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = vBoxModel.getVarLabels();
        allTheUCDOs = new ArrayList<>();
        allTheQDVs = new ArrayList<>();
        allTheQDVs = vBoxModel.getAllTheQDVs();
        whichView = "BoxPlot";
        nCheckBoxes = 2;
        subTitle = vBoxModel.getSubTitle();
        txtTitle1 = new Text (60, 30, " ");
        txtTitle2 = new Text (60, 45, " ");
        whichModel = "vBox";
    }
    
    public QuantCat_View(MultUni_Model multiUni_Model, MultUni_Dashboard multiUni_Dashboard,
                         String whichView, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        //System.out.println("\n126 QuantCat_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.whichView = whichView;
        allTheUCDOs = new ArrayList<>();
        allTheQDVs = new ArrayList<>();
        allTheQDVs = multiUni_Model.getAllQDVs();
        quantCatCanvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = multiUni_Model.getCatLabels();   // returns varLabels
        quantCatCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        quantCatCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();
        gcQuantCat.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        quantCat_ContainingPane = new Pane();   
        whichModel = "multUni";
    }
    
    public QuantCat_View(ANOVA1_Cat_Model anova1_Cat_Model, ANOVA1_Cat_Dashboard anova1_Cat_Dashboard,
                         String whichView, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        //System.out.println("\n150 QuantCat_View, Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.whichView = whichView;
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
    }
    
    public QuantCat_View(ANOVA1_Quant_Model anova1_Quant_Model, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard,
                         String whichView, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight)  {
        System.out.println("\n176 QuantCat_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.whichView = whichView;
        this.anova1_Quant_Model = anova1_Quant_Model;
        this.anova1_Quant_Dashboard = anova1_Quant_Dashboard;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = anova1_Quant_Model.getAllTheQDVs();
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = anova1_Quant_Model.getCategoryLabels();
        quantCatCanvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        quantCatCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        quantCatCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();
        gcQuantCat.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        quantCat_ContainingPane = new Pane();
        nCheckBoxes = 0;
        whichModel = "anovaCat";
    }
    
    public void completeTheDeal() { 
        initializeGraphParameters();  
        setUpAnchorPane();
        setHandlers();
        axisLabels = new String[2];
        xAxis.setLabel(axisLabels[0]);
        yAxis.setLabel(axisLabels[1]); 
        quantCat_ContainingPane = dragableAnchorPane.getTheContainingPane();    
        doTheGraph();   
    }
    
    public void initializeGraphParameters() {  
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
                    String switchFailure = "Switch failure: QuantCat_View 264 " + whichModel;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                break;                 
            }

            switch(whichView) {
                case "BoxPlot":
                    initial_yMin = Math.min(initial_yMin, batchQDV.getMinValue());
                    initial_yMax = Math.max(initial_yMax, batchQDV.getMaxValue());  
                    break;

                case "CirclePlot":
                    initial_yMin = Math.min(initial_yMin, batchQDV.getMinValue());
                    initial_yMax = Math.max(initial_yMax, batchQDV.getMaxValue());
                    checkBoxRow = new CheckBoxRow();
                    theActualRow = checkBoxRow.getTheCBRow();
                    break;
                
                case "Homogeneity Check":
                    tempLow = batchQDV.getTheMean() - anova1_Cat_Model.getPostHocPlusMinus();
                    tempHigh = batchQDV.getTheMean() + anova1_Cat_Model.getPostHocPlusMinus();
                    initial_yMin = Math.min(initial_yMin, tempLow); 
                    initial_yMax = Math.max(initial_yMax, tempHigh); 
                    checkBoxRow = new CheckBoxRow();
                    theActualRow = checkBoxRow.getTheCBRow();
                    break;

                //  Need to make the min/max the lowestBarEnd / highestBarEnd
                case "MeanAndError":
                    double tempLowBar = batchQDV.getTheMean() - 1.25 * batchQDV.getTheStandDev();
                    double tempHighBar = batchQDV.getTheMean() + 1.25 * batchQDV.getTheStandDev();       
                    inityMin_4_Errors = Math.min(inityMin_4_Errors, tempLowBar);
                    inityMax_4_Errors = Math.max(inityMax_4_Errors, tempHighBar);
                    initial_yMin = inityMin_4_Errors;
                    initial_yMax = inityMax_4_Errors;
                    checkBoxRow = new CheckBoxRow();
                    theActualRow = checkBoxRow.getTheCBRow();
                    break;

                default:
                    String switchFailure = "Switch failure: QuantCat_View 304 " + whichView;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }   
        }
            
        initial_yRange = initial_yMax - initial_yMin;
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);

        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
               
        yMin = initial_yMin - .05 * initial_yRange;
        yMax = initial_yMax + .05 * initial_yRange;
        yRange = initial_yRange;    
        deltaY = .005 * yRange;
        newY_Lower = yMin; newY_Upper = yMax;

        // For some graphs the LowerBound will be reset to 0.0
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        quantCatCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        quantCatCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(theActualRow, txtTitle1, txtTitle2, xAxis, yAxis, quantCatCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doTheGraph() { }
    
    public void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler);
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
                        if (!yAxisHasForcedHighEnd)
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else  
                    {
                        if (!yAxisHasForcedLowEnd)
                            newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                    if (frac < 0.5) {
                        if (!yAxisHasForcedHighEnd)
                            newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else
                    {
                        if (!yAxisHasForcedLowEnd)
                            newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
                }    

                if (yAxisHasForcedLowEnd) {
                    newY_Lower = yAxisForcedLowEnd;                   
                }
            
                if (yAxis.getHasForcedHighScaleEnd()) {
                    newY_Upper = yAxisForcedHighEnd;                    
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
     
    EventHandler<MouseEvent> quantCatMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) {  }
    }; 
    
    public Pane getTheContainingPane() { return quantCat_ContainingPane; }    
}
