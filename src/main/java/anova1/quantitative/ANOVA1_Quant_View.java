/**************************************************
 *                ANOVA1_Quant_View               *
 *                    10/07/24                    *
 *                      15:00                     *
 *************************************************/
package anova1.quantitative;

import superClasses.BivariateScale_W_CheckBoxes_View;
import genericClasses.JustAnAxis;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import splat.Data_Manager;
import utilityClasses.*;

public class ANOVA1_Quant_View extends BivariateScale_W_CheckBoxes_View { 
    // POJOs  
    int nVariables, nLevels;
    
    double initial_xMin, initial_xMax, initial_yMin, initial_yMax,
           initial_yRange;
    
    double[] means, stDevs;
    
    String whichView;
    String[] strCheckBoxDescriptions;
    
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Quant_View";
    String waldoFile = "";
    
    ObservableList<String> allTheLabels;

    // My classes
    ANOVA1_Quant_Dashboard anova1_Quant_Dashboard;
    ANOVA1_Quant_Model anova1_Quant_Model;
    Data_Manager dm;
    QuantitativeDataVariable allData_QDV;
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    ArrayList<UnivariateContinDataObj> allTheUCDOs;
    
    //  POJOs / FX
    AnchorPane anchorTitleInfo, theCheckBoxRow;
    Canvas anova1_Quant_Canvas;
    CheckBox[] anova1_Quant_CheckBoxes;
    GraphicsContext gc_Quant_ANOVA1; // Required for drawing on the Canvas
    Pane qanova1_ContainingPane;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content; 

    ANOVA1_Quant_View(ANOVA1_Quant_Model anova1_Quant_Model, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard,
                         String whichView, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = anova1_Quant_Model.getDataManager();
        dm.whereIsWaldo(78, waldoFile, " \nConstructing...");        
        allTheLabels = FXCollections.observableArrayList();
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.whichView = whichView;
        this.anova1_Quant_Model = anova1_Quant_Model;
        allTheLabels = anova1_Quant_Model.getCategoryLabels();
        anova1_Quant_Canvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        anova1_Quant_Canvas.heightProperty().addListener(ov-> {doTheGraph();});
        anova1_Quant_Canvas.widthProperty().addListener(ov-> {doTheGraph();});
        gc_Quant_ANOVA1 = anova1_Quant_Canvas.getGraphicsContext2D();
        gc_Quant_ANOVA1.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        qanova1_ContainingPane = new Pane();
        
    }
        
    public void completeTheDeal() { 
        dm.whereIsWaldo(96, waldoFile, "*** completeTheDeal()");
        initializeGraphParameters();
        makeTheCheckBoxes();    
        setUpAnchorPane();
        setHandlers();
        qanova1_ContainingPane = dragableAnchorPane.getTheContainingPane();
        doTheGraph();   
    }
    
    public void initializeGraphParameters() {  
        double tempUpDown;
        dm.whereIsWaldo(107, waldoFile, "  *** initializeGraphParameters()");
        allTheQDVs = anova1_Quant_Model.getAllTheQDVs();
        nVariables = allTheQDVs.size() - 1;   //  Excluding 0
        means = new double[nVariables];
        stDevs = new double[nVariables];
        allTheUCDOs = new ArrayList<>();
        
        for (int iVars = 0; iVars <= nVariables; iVars++) {
            UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj("ANOVA1_Quant_View", allTheQDVs.get(iVars));
            allTheUCDOs.add(tempUCDO);
            tempUCDO.doMedianBasedCalculations();
        }

        for (int iVars = 0; iVars < nVariables; iVars++) {
            means[iVars] = allTheQDVs.get(iVars + 1).getTheMean();
            stDevs[iVars] = allTheQDVs.get(iVars + 1).getTheStandDev();
        }
        
        nLevels = anova1_Quant_Model.getNLevels();
        anchorPane = new AnchorPane();
        
        xDataMin = Double.MAX_VALUE;
        xDataMax = Double.MIN_VALUE;  
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            double daValue = Double.parseDouble(allTheQDVs.get(ithLevel).getTheVarLabel());
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

        //  Find the maximum mean plus standard error
        initial_yMin = Double.MAX_VALUE;
        initial_yMax = -Double.MAX_VALUE;
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            tempQDV = anova1_Quant_Model.getIthQDV(ithLevel);
            
            switch(whichView) {
                case "BoxPlot":
                    initial_yMin = Math.min(initial_yMin, tempQDV.getMinValue());
                    initial_yMax = Math.max(initial_yMax, tempQDV.getMaxValue());                    
                break;
                    
                case "CirclePlot":
                    initial_yMin = Math.min(initial_yMin, tempQDV.getMinValue());
                    initial_yMax = Math.max(initial_yMax, tempQDV.getMaxValue());
                break;

                case "Homogeneity Check":
                    double tempLow = tempQDV.getTheMean() - anova1_Quant_Model.getPostHocPlusMinus();
                    double tempHigh = tempQDV.getTheMean() + anova1_Quant_Model.getPostHocPlusMinus();
                    initial_yMin = Math.min(initial_yMin, tempLow); 
                    initial_yMax = Math.max(initial_yMax, tempHigh);                         
                break;

                default:
                    String switchFailure = "Switch failure: ANOVA1 QuantView 175 " + whichView;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                break;
            }
        }
            
        initial_yRange = initial_yMax - initial_yMin;
        
        // Make room for the labels
        initial_yMax += .25 * initial_yRange;
        
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);

        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(anova1_Quant_Model.getResponseVariable());        
        yDataMin = initial_yMin - .05 * initial_yRange;
        yDataMax = initial_yMax + .05 * initial_yRange;
        yRange = initial_yRange;    
        deltaY = .005 * yRange;
        // Make room for labels
        newY_Lower = yDataMin; newY_Upper = yDataMax;

        // For some graphs the LowerBound will be reset to 0.0
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
    }
   
    public void setUpAnchorPane() {
        dm.whereIsWaldo(205, waldoFile, "  *** setUpAnchorPane()");
        dragableAnchorPane = new DragableAnchorPane();
        anova1_Quant_Canvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        anova1_Quant_Canvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            switch (nCheckBoxes) {
                case 1:  //  Etched in lemon marangue
                    anova1_Quant_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1_Quant_Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
            
                case 2: //  Etched in stone
                    anova1_Quant_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1_Quant_Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
                
                case 3:  //  Etched in stone

                    anova1_Quant_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1_Quant_Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(25. * iChex)
                                            .subtract(175.0));
                break;

                
                case 4:  //  Etched in stone
                    anova1_Quant_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1_Quant_Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(225.0));
                break;
                
                default:
                    String switchFailure = "Switch failure: ANOVA1 QuantView 249  " + nCheckBoxes;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                break;         
            }
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(theCheckBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, anova1_Quant_Canvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doTheGraph() { }    //  Subclassed
    
    public void makeTheCheckBoxes() {  
        dm.whereIsWaldo(266, waldoFile, "  *** makeTheCheckBoxes()");
        checkBoxSettings = new boolean[nCheckBoxes];
        
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            checkBoxSettings[ithSetting] =  false;
        }   
        
        theCheckBoxRow = new AnchorPane();
        theCheckBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        anova1_Quant_CheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            anova1_Quant_CheckBoxes[i] = new CheckBox(strCheckBoxDescriptions[i]);           
            anova1_Quant_CheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            anova1_Quant_CheckBoxes[i].setId(strCheckBoxDescriptions[i]);
            anova1_Quant_CheckBoxes[i].setSelected(checkBoxSettings[i]);

            anova1_Quant_CheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            if (anova1_Quant_CheckBoxes[i].isSelected() == true) {
                anova1_Quant_CheckBoxes[i].setTextFill(Color.GREEN);
            }
            else { anova1_Quant_CheckBoxes[i].setTextFill(Color.RED); }
            
            anova1_Quant_CheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                // Reset selected color
                if (checkValue == true) {
                    tb.setTextFill(Color.GREEN); }
                else { tb.setTextFill(Color.RED); }
                
                switch (daID) {    
                    case " Means diamond ":
                        checkBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Extreme Outliers ":  
                        checkBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        String switchFailure = "Switch failure: ANOVA1 QuantView 319 " + daID;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);
                }
            }); //  end setOnAction
        }         
        theCheckBoxRow.getChildren().addAll(anova1_Quant_CheckBoxes);
    }

    public Pane getTheContainingPane() { return qanova1_ContainingPane; }
}
