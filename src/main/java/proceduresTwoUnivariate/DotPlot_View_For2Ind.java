/**************************************************
 *                DotPlotView_For2Ind             *
 *                     11/01/23                   *
 *                      18:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import genericClasses.JustAnAxis;
import dialogs.Change_Bins_Dialog;
import dialogs.Change_Radius_Dialog;
import genericClasses.Point_2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import proceduresOneUnivariate.DotPlot_Model;

public class DotPlot_View_For2Ind {
    // POJOs

    int nLegalDataPoints, nBinsToLeft, nBinsToRight, nBinsTotal;
    
    double xMin, xMax, minScale, maximumFreq, ithBinLow, ithBinHigh, binWidth,
           xCenter, yCenter, minDataRange, maxDataRange, relRad, radius, 
            diameter; 
    
    double[] sortedData, leftBinEnd, rightBinEnd, frequencies;
    
    String returnStatus;  
    
    public Canvas graphCanvas;
    public GraphicsContext gc;
    
    // My classes
    Change_Bins_Dialog chBins_Dialog;
    Change_Radius_Dialog chRadius_Dialog;
    DotPlot_Model dotPlot_Model;
    JustAnAxis xAxis, yAxis;
    Point_2D ithBinLimits;
    Button btn_BinReset, btn_RadiusReset;
    HBox hBox_BinReset;
    AnchorPane anchorPane;
    
    // FX Classes
    public Text txtTitle1, txtTitle2;

    public DotPlot_View_For2Ind(DotPlot_Model dotPlot_Model) {
        System.out.println("54 DotPlot_View_For2Ind, Constructing");
        this.dotPlot_Model = dotPlot_Model;
        nLegalDataPoints = dotPlot_Model.getQDV().getLegalN();
        ithBinLimits = this.dotPlot_Model.getBinLimits();
        ithBinLow = ithBinLimits.getFirstValue();
        ithBinHigh = ithBinLimits.getSecondValue();
        sortedData = new double[nLegalDataPoints];
        sortedData = dotPlot_Model.getUCDO().getTheDataSorted();
        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();
        btn_BinReset = new Button("Change bin limits");
        btn_RadiusReset = new Button("Change radius");
        minDataRange = dotPlot_Model.getUCDO().getMinValue();
        maxDataRange = dotPlot_Model.getUCDO().getMaxValue();
        
        relRad = 0.90;  //  Default

        btn_BinReset.setOnAction(e -> {
            System.out.println("72 DotPlot_View_For2Ind, Bin Reset");
            chBins_Dialog = new Change_Bins_Dialog(minDataRange, maxDataRange);
            chBins_Dialog.showAndWait();
            ithBinLow = chBins_Dialog.getDblLeftBin();
            ithBinHigh = chBins_Dialog.getDblRightBin();
            returnStatus = chBins_Dialog.getReturnStatus();
            chBins_Dialog.close();
            
            if (!returnStatus.equals("Cancel")) {
                constructBinInformation();
                doTheGraph();
            }
        });
        
        btn_RadiusReset.setOnAction(e -> {
            System.out.println("87 ----------------------  DotPlot_View_For2Ind, Radius Reset");
            chRadius_Dialog = new Change_Radius_Dialog(this);
            chRadius_Dialog.showAndWait();
            returnStatus = chRadius_Dialog.getReturnStatus();
            
            if (!returnStatus.equals("Cancel")) {
                relRad = chRadius_Dialog.getRelativeRadius();
                chRadius_Dialog.close();
                doTheGraph();
            }
        });

        hBox_BinReset = new HBox();
        hBox_BinReset.getChildren().addAll(btn_BinReset, btn_RadiusReset);
        System.out.println("101 -----------------------  DotPlot_View_For2Ind, makeItHappen()");
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void doTheGraph() {
        System.out.println("161 DotPlot_View_For2Ind, doTheGraph()");
        double binFrequency;  
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = anchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = anchorPane.getHeight();
        double tempWidth = anchorPane.getWidth();
        
        AnchorPane.setTopAnchor(hBox_BinReset, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(hBox_BinReset, 0.0 * txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(hBox_BinReset, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(hBox_BinReset, 0.95 * tempHeight);

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

        gc.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight()); 
        
        gc.setFill(Color.GREEN);
        binWidth = xAxis.getDisplayPosition(leftBinEnd[1]) - xAxis.getDisplayPosition(leftBinEnd[0]);  
        diameter = relRad * binWidth;
        radius = 0.5 * diameter;
        
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++ ) {
            double preXCenter = (leftBinEnd[ithBin] + rightBinEnd[ithBin]) / 2.0;
            xCenter = xAxis.getDisplayPosition(preXCenter);
            binFrequency = frequencies[ithBin];
            
            if (binFrequency > 0.) {
                
                for (int ithCircle = 0; ithCircle < binFrequency; ithCircle++) {
                    yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                    gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);
                }       
            }
        }   // for iBin  
        
        /*
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
        */
        
    }   //  end doThePlot 
    
    public void constructBinInformation() {
        System.out.println("175 DotPlot_View_For2Ind, constructBinInformation()");
        binWidth = ithBinHigh - ithBinLow;
        nBinsToLeft = (int)Math.floor((ithBinLow - xMin) / binWidth) + 1;
        nBinsToRight = (int)Math.floor((xMax - ithBinHigh) / binWidth) + 1;
        
        if (nBinsToLeft < 3) {nBinsToLeft = 3;}
        if (nBinsToRight < 3) {nBinsToRight = 3;}       
        
        nBinsTotal = nBinsToLeft + 1 + nBinsToRight;
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
            for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
                if ((leftBinEnd[ithBin] <= ithDataPoint) && (ithDataPoint < rightBinEnd[ithBin])) {
                    frequencies[ithBin] = frequencies[ithBin] + 1.0;
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
    }
    
    public void setRelRad(double theNewRad) { relRad = theNewRad; }
    public AnchorPane getAnchorPane() { return anchorPane; }
    public Canvas getCanvas() { return graphCanvas; }
}