/**************************************************
 *           ChooseStats_DotPlot_DistrView        *
 *                    02/24/25                    *
 *                     09:00                      *
 *************************************************/
package bootstrapping;

import dialogs.Change_Bins_Dialog;
import dialogs.Change_Radius_Dialog;
import genericClasses.DragableAnchorPane;
import genericClasses.Point_2D;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import smarttextfield.*;

public class ChooseStats_DotPlot_DistrView extends Super_ChooseStats_DistrView {
    
    double thisMuchIsGreen, thisMuchIsRed, ratioRed, binFrequency;
    double relRad, xCenter, yCenter, leftPercentile, rightPercentile;
    
    String descrOfVar;
    
    // Make empty if no-print
    //String waldoFile = "ChooseStats_DotPlot_DistrView";
    String waldoFile = "";
    
    // My classes 
    Change_Radius_Dialog chRadius_Dialog;

    //  POJOs / FX   
    Button btn_BinReset, btn_RadiusReset;
    HBox hBox_BinAndRadReset;   
    Point_2D ithBinLimits;

    public ChooseStats_DotPlot_DistrView(ChooseStats_DistrModel chooseStats_DistrModel,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(chooseStats_DistrModel, placeHoriz, placeVert, withThisWidth, withThisHeight); 
        dm = chooseStats_DistrModel.getDataManager();
        dm.whereIsWaldo(51, waldoFile, "Constructing"); 
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        descrOfVar = chooseStats_DistrModel.getDescriptionOfVariable();
        this.chooseStats_DistrModel = chooseStats_DistrModel;
        ithBinLimits = this.chooseStats_DistrModel.getBinLimits();
        ithBinLow = ithBinLimits.getFirstValue();
        ithBinHigh = ithBinLimits.getSecondValue();
        chooseStats_Controller = chooseStats_DistrModel.getBootStrapController();
        chooseStats_Dashboard = chooseStats_Controller.getThe_Boot_Dashboard(); 
        nLegalDataPoints = this.chooseStats_DistrModel.getTheQDV().getLegalN();
        sortedData = new double[nLegalDataPoints];
        sortedData = this.chooseStats_DistrModel.getUCDO().getTheDataSorted();
    }

    public void continueConstruction() {    
        dm.whereIsWaldo(67, waldoFile, "continueConstruction()");
        chooseStats_Controller.set_Boot_OriginalDotPlot_DistrView(this);
        chooseStats_DialogView = chooseStats_Controller.get_Boot_DialogView();
        xPrintPosLeft = 0.05;
        xPrintPosCenter = 0.325;
        xPrintPosRight = 0.5;
        yPrintPosLeftRight = 0.9;
        yPrintPosCenter = 0.95;   
        maxOfYScale = 0.45; 
        
        newX_Lower = chooseStats_Controller.getOriginalXLower();
        newX_Upper =  chooseStats_Controller.getOriginalXUpper(); 
        initializeGraphParameters();

        binWidth = ithBinHigh - ithBinLow;

        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();  
        btn_BinReset = new Button("Change bin width");
        btn_RadiusReset = new Button("Change radius");
        btn_BinReset.setPadding(new Insets(5, 10, 5, 10));
        btn_RadiusReset.setPadding(new Insets(5, 10, 5, 10));
        minDataRange = chooseStats_DistrModel.getUCDO().getMinValue();
        maxDataRange = chooseStats_DistrModel.getUCDO().getMaxValue();
        
        relRad = 0.975;
        
        btn_BinReset.setOnAction(e -> {
            change_Bins_Dialog = new Change_Bins_Dialog(minDataRange, maxDataRange);
            change_Bins_Dialog.showAndWait();
            ithBinLow = change_Bins_Dialog.getDblLeftBin();
            ithBinHigh = change_Bins_Dialog.getDblRightBin();
            binWidth = ithBinHigh - ithBinLow;
            returnStatus = change_Bins_Dialog.getReturnStatus();
            change_Bins_Dialog.close();
            
            if (!returnStatus.equals("Cancel")) {
                constructBinInformation();
                doTheGraph();
            }
        });
        
        btn_RadiusReset.setOnAction(e -> {
            chRadius_Dialog = new Change_Radius_Dialog(this);
            chRadius_Dialog.showAndWait();
            returnStatus = chRadius_Dialog.getReturnStatus();
            
            if (!returnStatus.equals("Cancel")) {
                relRad = chRadius_Dialog.getRelativeRadius();
                chRadius_Dialog.close();
                doTheGraph();
            }
        });

        hBox_BinAndRadReset = new HBox();
        hBox_BinAndRadReset.getChildren().addAll(btn_BinReset, btn_RadiusReset);  
    
        // There are no check boxes, but superclass constructs a CheckBoxRow
        nCheckBoxes = 0;
        initializing = true;
        al_ProbCalcs_STF = new SmartTextFieldDoublyLinkedSTF();
        chooseStats_DialogView = chooseStats_Dashboard.get_Boot_ChooseStats_DialogView();
        chooseStats_DialogView.getBootstrapOneStat_DialogView();
        al_ProbCalcs_STF = chooseStats_DialogView.getAllTheSTFs();
        chooseStats_Controller = chooseStats_Dashboard.get_Boot_Controller();
        tailChoice = "NotEqual";
        respondToChanges();
        graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes();
        makeItHappen();
    }
    
    public void doTheGraph() {
        shadeLeft = chooseStats_DistrModel.get_ShadeLeft();
        shadeRight = chooseStats_DistrModel.get_ShadeRight();
        
        if (chooseStats_DistrModel.get_TwoTail_IsChecked()) {
            shadeLeft = true;
            shadeRight = true;
        }
        
        leftPercentile = chooseStats_DistrModel.get_LeftPercentile();
        rightPercentile = chooseStats_DistrModel.get_RightPercentile();   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        paneWidth = dragableAnchorPane.getWidth();
        txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        tempHeight = dragableAnchorPane.getHeight();
        tempWidth = dragableAnchorPane.getWidth();
         
        AnchorPane.setTopAnchor(hBox_BinAndRadReset, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(hBox_BinAndRadReset, 0.0 * txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(hBox_BinAndRadReset, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(hBox_BinAndRadReset, 0.95 * tempHeight);
        
        AnchorPane.setTopAnchor(txtTitle1, 0.07 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.12 * tempHeight);
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
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        
        gc.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight()); 
        
        binWidth = leftBinEnd[1] - leftBinEnd[0];
        displayBinWidth = xAxis.getDisplayPosition(leftBinEnd[1]) - xAxis.getDisplayPosition(leftBinEnd[0]);  

        diameter = relRad * displayBinWidth;
        radius = 0.5 * diameter;
        
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++ ) {   
            gc.setFill(Color.GREEN);
            double preXCenter = (leftBinEnd[ithBin] + rightBinEnd[ithBin]) / 2.0;
            xCenter = xAxis.getDisplayPosition(preXCenter);
            binFrequency = frequencies[ithBin];
            
            if (binFrequency > 0.) { 
                double leftEndOfBin = leftBinEnd[ithBin];
                double rightEndOfBin = rightBinEnd[ithBin];

                bILT = (rightEndOfBin < leftPercentile);
                bSLP = ((leftEndOfBin <= leftPercentile) && (leftPercentile < rightEndOfBin));
                bITM = ((leftPercentile <= leftEndOfBin) && (rightEndOfBin < rightPercentile));
                bSRP = ((leftEndOfBin <= rightPercentile) && (rightPercentile < rightEndOfBin));
                bIRT = (rightPercentile <= leftEndOfBin);
                
                gc.setFill(Color.GREEN);
                //  No tails are selected
                if (!shadeLeft && !shadeRight) { 
                    for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                        yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                        gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                    }                       
                }
                else 
                {
                //  Bin is totally in the left tail
                    if (bILT) {
                        gc.setFill(Color.GREEN);
                        if (shadeLeft) { gc.setFill(Color.RED);}
                        for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                            yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                            gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                        }                        
                    }

                    //  Bin contains the left percentile
                    if (bSLP && shadeLeft) {
                        thisMuchIsRed = leftPercentile - leftEndOfBin;
                        thisMuchIsGreen = rightEndOfBin - leftPercentile; 
                        ratioRed = thisMuchIsRed / (thisMuchIsRed + thisMuchIsGreen);

                        for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                            gc.setFill(Color.BLACK);
                            if (ithCircle > binFrequency * ratioRed) {
                                gc.setFill(Color.BLUE);
                            }
                            yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                            gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                        } 
                    }
                    
                    if (bSLP && !shadeLeft) {
                        for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                            yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                            gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                        }   
                    }
                    
                    //  Bin is totally in the middle
                    if (bITM) {
                        gc.setFill(Color.GREEN);
                        for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                            yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                            gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                        } 
                    }

                    //  Bin contains the right percentile
                    if (bSRP && shadeRight) {
                        thisMuchIsRed = rightPercentile - leftEndOfBin;
                        thisMuchIsGreen = rightEndOfBin - rightPercentile; 
                        ratioRed = thisMuchIsRed / (thisMuchIsRed + thisMuchIsGreen);   
                        
                        for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                            gc.setFill(Color.BLUE);
                            if (ithCircle > binFrequency * ratioRed) {
                                gc.setFill(Color.BLACK);
                            }
                            yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                            gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                        }                          
                    }
                    
                    if (bSRP && !shadeRight) {
                        for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                            yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                            gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                        }   
                    }
                    
                    //  Bin is totally in the right tail
                    if (bIRT) {
                        gc.setFill(Color.GREEN);
                        if (shadeRight) { gc.setFill(Color.RED);}
                        for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                            yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                            gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                        }                          
                    }
                }
            }   // end else shading somewhere
        }   // for iBin    
        
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
        
    }   //  end doThePlot   
    
    public void initializeGraphParameters() {  
        initial_xMin = chooseStats_DistrModel.getTheQDV().getMinValue();
        initial_xMax = chooseStats_DistrModel.getTheQDV().getMaxValue();
        initial_xRange = initial_xMax - initial_xMin;
        // Adjust to better bring the histogram into the window
        xMin = initial_xMin - 0.05 * initial_xRange;   
        xMax = initial_xMax + 0.05 * initial_xRange;   
        xRange = xMax - xMin;
        // This constant controls the rate of scale change when dragging
        deltaX = 0.005 * xRange;
        constructBinInformation();
        setUpTheAxes();
    }
    
    public void constructBinInformation() { 
        binWidth = ithBinHigh - ithBinLow;
        nBinsToLeft = (int)Math.floor((ithBinLow - xMin) / binWidth);
        nBinsToRight = (int)Math.floor((xMax - ithBinHigh) / binWidth);
        if (nBinsToLeft < 2) {nBinsToLeft = 2;}
        if (nBinsToRight < 2) {nBinsToRight = 2;}
        
        nBinsTotal = nBinsToLeft + 2 + nBinsToRight;

        leftBinEnd = new double[nBinsTotal];
        rightBinEnd = new double[nBinsTotal];
        minScale = ithBinLow - nBinsToLeft * binWidth;
        
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
            leftBinEnd[ithBin] = minScale + ithBin * binWidth;
            rightBinEnd[ithBin] = leftBinEnd[ithBin] + binWidth;
        }
        
        frequencies = new double[nBinsTotal];
        for (int ithData = 0; ithData < nLegalDataPoints; ithData++) {
            double ithDataPoint = sortedData[ithData];
            for (int jthBin = 0; jthBin < nBinsTotal; jthBin++) {
                if ((leftBinEnd[jthBin] <= ithDataPoint) && (ithDataPoint < rightBinEnd[jthBin])) {
                    frequencies[jthBin] = frequencies[jthBin] + 1.0;
                    break;
                }
            }
        }
        
        maximumFreq = 0.;
        
        for (int ithFreq = 0; ithFreq < nBinsTotal; ithFreq++) {
            if (frequencies[ithFreq] > maximumFreq) {
                maximumFreq = frequencies[ithFreq];
            }
        }
        
        if (yAxis != null) {    // If changing existing bins
            yAxis.setUpperBound(maximumFreq);
        }
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(hBox_BinAndRadReset,txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
 
    public ChooseStats_Controller get_Bootstrap_Controller() {
        return chooseStats_Controller;
    }
    
    public void setBootstrapOneStat_DialogView(ChooseStats_DialogView boot_DialogView) {
        this.chooseStats_DialogView = boot_DialogView;
    }

    public void setInitializingToTrue() { initializing = true; }
    public void setRelRad(double theNewRad) { relRad = theNewRad; }
}
