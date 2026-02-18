/*************************************************
*           BivariateScale_W_CheckBoxes_View     *
*                    09/02/25                    *
*                      18:00                     *
*************************************************/
package superClasses;

import genericClasses.DragableAnchorPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import matrixProcedures.Matrix;
import javafx.scene.paint.Color;
import simpleRegression.*;
import quadraticRegression.*;
import utilityClasses.*;

public abstract class BivariateScale_W_CheckBoxes_View extends BivariateScale_View {     
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public boolean shadeLeftTail, shadeMiddleTail, shadeRightTail,
                   identifyPValueIsDesired, assumptionCheckIsDesired, hasLeftTailStat, 
                   hasRightTailStat;
    
    public boolean[] checkBoxSettings;
    
    final public int NUMBER_OF_DXs = 600;   
    public int nDataPoints, nCheckBoxes, regionSize, checkWidth;
    
    public double leftTailCutPoint, rightTailCutPoint, fromHere, toThere, 
           delta, bigDelta, pValue, theCriticalValue, alpha, alphaOverTwo,
           middle_ForGraph, constraintFactor, radius, diameter, fudgeTerm,
           fudgeFactor, chBoxDescrSpace;
    
    protected double dataArray[][];
    
    public String respVsExplanVar, cssLabel_01, cssLabel_02, cssLabel_03, 
           cssLabel_04;
    public String[] scatterPlotCheckBoxDescr;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    // My classes
    public Inf_Regr_Model regrModel;
    public Matrix X, Y;    
    public NoInf_Regr_Dashboard noInf_RegrDashboard;
    public NoInf_Regr_Model noInf_RegrModel;     
    public QuadReg_Dashboard quadReg_Dashboard;
    public QuadReg_Model quadReg_Model;    
    public Regr_Dashboard regrDashboard;

    //  POJO / FX
    public AnchorPane anchorPane;
    public CheckBox[] scatterPlotCheckBoxes;
    ColumnConstraints[] columnConstraints;
    public GridPane checkBoxGridPane;
    public HBox checkBoxRow;
    Pane regrContainingPane;
    public Text txtTitle1, txtTitle2;
    
    public BivariateScale_W_CheckBoxes_View(double placeHoriz, double placeVert,
                                                double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("85 *** BivariateScale_W_CheckBoxes_View (Super), Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        regionSize = 1;
        checkWidth = 1;
        constraintFactor = 20.0;
        checkBoxGridPane = new GridPane();
        cssLabel_01 = Colors_and_CSS_Strings.get_cssLabel_01();
        cssLabel_02 = Colors_and_CSS_Strings.get_cssLabel_02();
        cssLabel_03 = Colors_and_CSS_Strings.get_cssLabel_03();
        cssLabel_04 = Colors_and_CSS_Strings.get_cssLabel_04();
    } 
    
    protected void setUpUI() {
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
   
    public void initializeGraphParameters() { 
        constructDataArray();
        xAxis = new genericClasses.JustAnAxis(xDataMin, xDataMax);
        xAxis.setSide(Side.BOTTOM); 
        yAxis = new genericClasses.JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        newX_Lower = xDataMin; newX_Upper = xDataMax;
        double smidgen = 0.05 * (yDataMax - yDataMin);  // lower dots under r
        newY_Lower = yDataMin; newY_Upper = yDataMax + smidgen;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );            
    }
    
    public void makeTheCheckBoxes() {   
        
        if (nCheckBoxes == 0) { return; }
        
        chBoxDescrSpace = 0;
        checkBoxSettings = new boolean[nCheckBoxes];
        
        for (int ithBox = 0; ithBox < nCheckBoxes; ithBox++) {
            checkBoxSettings[ithBox] = false;
            chBoxDescrSpace += scatterPlotCheckBoxDescr[ithBox].length();
        }

        columnConstraints = new ColumnConstraints[nCheckBoxes];
        
        for (int ithPair = 0; ithPair < nCheckBoxes; ithPair++) {
            columnConstraints[ithPair] = new ColumnConstraints();
            double tempPrefWidth = constraintFactor * scatterPlotCheckBoxDescr[ithPair].length();
            columnConstraints[ithPair].setPrefWidth(tempPrefWidth);
        }
        
        scatterPlotCheckBoxes = new CheckBox[nCheckBoxes];
        
        for (int i = 0; i < nCheckBoxes; i++) {
            scatterPlotCheckBoxes[i] = new CheckBox(scatterPlotCheckBoxDescr[i]);
            scatterPlotCheckBoxes[i].setMinWidth(7.0 * scatterPlotCheckBoxDescr[i].length());  // Apparently controls the spacing of the 
            scatterPlotCheckBoxes[i].setMaxWidth(7.5 * scatterPlotCheckBoxDescr[i].length());  // CheckBoxes.
            scatterPlotCheckBoxes[i].setId(scatterPlotCheckBoxDescr[i]);
            scatterPlotCheckBoxes[i].setSelected(checkBoxSettings[i]);

            scatterPlotCheckBoxes[i].setStyle(
                                "-fx-font-size: 12;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (scatterPlotCheckBoxes[i].isSelected() == true) 
                scatterPlotCheckBoxes[i].setTextFill(Color.GREEN);
            else {
                scatterPlotCheckBoxes[i].setTextFill(Color.RED);
            }
            
            scatterPlotCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue) {
                    tb.setTextFill(Color.GREEN);
                }
                else {
                    tb.setTextFill(Color.RED);
                }
                
                for (int ithID = 0; ithID < nCheckBoxes; ithID++) {
                    if (daID.equals(scatterPlotCheckBoxDescr[ithID])) {
                        checkBoxSettings[ithID] = (checkValue == true);
                        doTheGraph();
                    }
                }

            }); //  end setOnAction   
        }  
        
        for (int ithCB = 0; ithCB < nCheckBoxes; ithCB++) {
            checkBoxGridPane.add(scatterPlotCheckBoxes[ithCB], ithCB, 0);
        }
        
        checkBoxRow = new HBox();
        checkBoxRow.getChildren().add(checkBoxGridPane);             
    }
    
    private void constructDataArray() {
        nDataPoints = X.getRowDimension();
        dataArray = new double[nDataPoints][2];
        
        xDataMin = xDataMax = X.get(0, 0);
        yDataMin = yDataMax = Y.get(0, 0);
        
        for (int iRow = 0; iRow < nDataPoints; iRow++)  {
            double tempDoubleX = X.get(iRow, 0);
            double tempDoubleY = Y.get(iRow, 0);
            
            dataArray[iRow][0] = tempDoubleX;
            dataArray[iRow][1] = tempDoubleY;

            if (tempDoubleX < xDataMin) xDataMin = tempDoubleX;
            if (tempDoubleY < yDataMin) yDataMin = tempDoubleY;
            if (tempDoubleX > xDataMax) xDataMax = tempDoubleX;
            if (tempDoubleY > yDataMax) yDataMax = tempDoubleY;
        } 
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;
        
        //  Make room for the circles
        xDataMin = xDataMin - .02 * xRange; xDataMax = xDataMax + .02 * xRange;
        yDataMin = yDataMin - .02 * yRange; yDataMax = yDataMax + .02 * yRange;  
    }
    

    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));        
        dragableAnchorPane.widthProperty().addListener(new ChangeListener<Number>() {

        public void changed(ObservableValue<? extends Number> observableValue, Number oldRootWidth, Number newRootWidth) {
            double tempDouble_1 = 2 * regionSize + nCheckBoxes * checkWidth + chBoxDescrSpace;
            double tempDouble_2 = (double)newRootWidth;
            int checkBoxGap = (int)(0.25*((tempDouble_2 - tempDouble_1) / nCheckBoxes));
            checkBoxGridPane.setHgap(checkBoxGap);    // Set gap between checkboxes
        }
    });

        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        
        // Some subclasses do not have checkBoxes
        if (nCheckBoxes > 0) {
            dragableAnchorPane.getTheAP()
                              .getChildren()
                              .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        } else {
            dragableAnchorPane.getTheAP()
                              .getChildren()
                              .addAll(txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);            
        }
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

   public Pane getTheContainingPane() { return regrContainingPane; }
}