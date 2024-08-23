/**************************************************
 *             MultReg_ResidsVsFit_View           *
 *                    07/04/24                    *
 *                      00:00                     *
 *************************************************/
package multipleRegression;

import superClasses.BivariateScale_View;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import matrixProcedures.Matrix;
import genericClasses.*;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class MultReg_ResidsVsFit_View extends BivariateScale_View {
    // POJOs
    boolean[] checkBoxSettings;    
    int nRows, nCheckBoxes;
    double outlierCircleRadius, radius, diameter;  
    double dataArray[][];
        
    String[] residsVsFitsCheckBoxDescr;
    
    //String waldoFile = "MultReg_ResidsVsFit_View";
    String waldoFile = "";

    // My classes    
    Data_Manager dm;
    Matrix mat_Fits, mat_RStudent;

    //  POJO / FX
    Pane theContainingPane;
    Text txtTitle1, txtTitle2;
    AnchorPane checkBoxRow;
    CheckBox[] residsVsFitCheckBoxes;
    AnchorPane anchorPane;

    public MultReg_ResidsVsFit_View(MultReg_Model multRegModel, MultReg_Dashboard multRegDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = multRegModel.getDataManager();
        dm.whereIsWaldo(52, waldoFile, "Constructing");
        nRows = multRegModel.getNRows();
        mat_Fits = new Matrix(nRows, 1);
        mat_Fits = multRegModel.getYHats();
        
        mat_RStudent = new Matrix(nRows, 1);
        mat_RStudent = multRegModel.getR_StudentizedResids();
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        nCheckBoxes = 3;
        residsVsFitsCheckBoxDescr = new String[nCheckBoxes];
        residsVsFitsCheckBoxDescr[0] = " Observation # ";
        residsVsFitsCheckBoxDescr[1] = " Outliers ";
        residsVsFitsCheckBoxDescr[2] = " Influential points ";
        
        txtTitle1 = new Text(50, 25, " Residuals vs Fit ");
        txtTitle2 = new Text (60, 45, " Studentized Residual ");
        
        radius = 4.0; diameter = 2.0 * radius;  //  For the dots
        outlierCircleRadius = 1.75;  // drawing factor 
        
        checkBoxHeight = 350.0;
        graphCanvas = new Canvas(initWidth, initHeight);  
        
        makeTheCheckBoxes();    
        makeItHappen();
    }  
    
    public void makeTheCheckBoxes() {       
        dm.whereIsWaldo(83, waldoFile, "---makeTheCheckBoxes()");
        // Determine which graphs are initially shown
        checkBoxSettings = new boolean[nCheckBoxes];
        
        for (int ithBox = 0; ithBox < nCheckBoxes; ithBox++) {
            checkBoxSettings[ithBox] = false;
        }
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        residsVsFitCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            residsVsFitCheckBoxes[i] = new CheckBox(residsVsFitsCheckBoxDescr[i]);            
            residsVsFitCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            residsVsFitCheckBoxes[i].setId(residsVsFitsCheckBoxDescr[i]);
            residsVsFitCheckBoxes[i].setSelected(checkBoxSettings[i]);
            residsVsFitCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (residsVsFitCheckBoxes[i].isSelected() == true) {
                residsVsFitCheckBoxes[i].setTextFill(Color.GREEN);
            }
            else {
                residsVsFitCheckBoxes[i].setTextFill(Color.RED);
            }
            
            residsVsFitCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);
                
                for (int ithID = 0; ithID < nCheckBoxes; ithID++) {
                    
                    if (daID.equals(residsVsFitsCheckBoxDescr[ithID])) {
                        checkBoxSettings[ithID] = (checkValue == true);
                        doTheGraph();
                    }
                }

            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(residsVsFitCheckBoxes);
        dm.whereIsWaldo(138, waldoFile, "--- END makeTheCheckBoxes()");
    }
  
        private void makeItHappen() { 
        dm.whereIsWaldo(142, waldoFile, "---makeItHappen()");
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        dm.whereIsWaldo(148, waldoFile, "--- END makeItHappen()");
    }
        
    public void setUpUI() {
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
        
    public void initializeGraphParameters() {  
        dm.whereIsWaldo(157, waldoFile, "---initializeGraphParameters()");
        constructDataArray();
        xAxis = new genericClasses.JustAnAxis(xDataMin, xDataMax);
        xAxis.setSide(Side.BOTTOM);    
        yAxis = new genericClasses.JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        newX_Lower = xDataMin; newX_Upper = xDataMax;
        newY_Lower = yDataMin; newY_Upper = yDataMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper ); 
        dm.whereIsWaldo(169, waldoFile, "--- END initializeGraphParameters()");
    }
    
    private void constructDataArray() {
        dm.whereIsWaldo(173, waldoFile, "---constructDataArray()");
        dataArray = new double[nRows][2];
        xDataMin = xDataMax = mat_Fits.get(0, 0);
        yDataMin = yDataMax = mat_RStudent.get(0, 0);
        for (int iRow = 0; iRow < nRows; iRow++) {
            double tempDoubleX = mat_Fits.get(iRow, 0);
            double tempDoubleY = mat_RStudent.get(iRow, 0);
            
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
        dm.whereIsWaldo(197, waldoFile, "--- END constructDataArray()");
    }
        
    public void completeTheDeal() { 
        dm.whereIsWaldo(200, waldoFile, "---completeTheDeal()");
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();        
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();  
        dm.whereIsWaldo(207, waldoFile, "--- END completeTheDeal()");
    }
    
    public void setUpAnchorPane() {
        dm.whereIsWaldo(211, waldoFile, "---setUpAnchorPane()");
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            // Position checkboxes in the more or less middle
            switch (nCheckBoxes) {                
                case 1:  //  Etched in lemon marangue
                    residsVsFitCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                    break;
            
                case 2: //  Etched in stone
                    residsVsFitCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                    break;
                
                case 3:  //  Etched in stone
                    residsVsFitCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(25. * iChex)
                                            .subtract(175.0));
                    break;
                
                case 4:  //  Etched in stone
                    residsVsFitCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(225.0));
                    break;
                    
                default:
                    String switchFailure = "Switch failure: Transformations_Calculations 252 " + nCheckBoxes;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure); 
            }
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);       
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
        dm.whereIsWaldo(264, waldoFile, "--- END setUpAnchorPane()");
    }
    
    public void doTheGraph() {    
        dm.whereIsWaldo(268, waldoFile, "--- doTheGraph()");
        //double xx0, yy0, xx1, yy1;
        //String tempString;
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(checkBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(checkBoxRow, 0.95 * tempHeight);
       
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
        
        for (int chex = 0; chex < 3; chex++) {
            AnchorPane.setLeftAnchor(residsVsFitCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean caseNumbersDesired = checkBoxSettings[0];
        boolean outlierPlotDesired = checkBoxSettings[1];
        boolean influencePlotDesired = checkBoxSettings[2];
        
        for (int i = 0; i < nRows; i++) {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);            
            //  radius, diameter for centering the dots on point
            gc.fillOval(xx - radius, yy - radius, diameter, diameter); 
            
            if (outlierPlotDesired) {
                gc.setFill(Color.BLUE);
                double outRadius = outlierCircleRadius * Math.abs(mat_RStudent.get(i, 0));
                double outDiameter = 2.5 * outRadius;
                
                if (outRadius > 3.0) {  //  Arbitrary!
                    gc.fillOval(xx - outRadius, yy - outRadius, outDiameter, outDiameter);
                }
            }
            
            if (influencePlotDesired) {
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(2);
                double rStudent = mat_RStudent.get(i, 0);
                double influenceDiameter = 10.0 * rStudent + .5;
                
                // Cooks recommendations are that point with a CooksD > 0.5 
                // might be usefully studied, and cooksD > 1.0 are always 
                // important to study.  Cook, R., & Weisberg, S.  (1999).  
                // Applied Regression Including Computing and Graphicss. p357.
                // Wiler-Interscience, New York.
                
                if (rStudent > 0.5) {
                    gc.setStroke(Color.ORANGE);
                }
                if (rStudent > 1.0) {
                    gc.setStroke(Color.RED);
                }

                if (rStudent > 0.5) {
                    gc.strokeLine(xx, yy - influenceDiameter, xx, yy + influenceDiameter);
                    gc.strokeLine(xx - influenceDiameter, yy, xx + influenceDiameter, yy);
                }

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
            }   
            
            if (caseNumbersDesired) {
                gc.strokeText(String.valueOf(i + 1), xx - 10.0, yy - 10.0);
            }            
        }  
        /*
        theContainingPane.requestFocus();
        theContainingPane.setOnKeyPressed((new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
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
            }
        }));        
        */
        dm.whereIsWaldo(386, waldoFile, "--- END doTheGraph()");
    }   // end doTheGraph

   public Pane getTheContainingPane() { return theContainingPane; }
}
