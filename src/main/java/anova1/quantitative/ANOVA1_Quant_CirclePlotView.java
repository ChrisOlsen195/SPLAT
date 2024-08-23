/**************************************************
 *           ANOVA1_Quant_CirclePlotView          *
 *                    02/19/24                    *
 *                      12:00                     *
 *************************************************/
package anova1.quantitative;

import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ANOVA1_Quant_CirclePlotView extends ANOVA1_Quant_View { 
    // POJOs
    double theMean;
    
    //String waldoFile = "ANOVA1_Quant CirclePlotView";
    String waldoFile = "";

    // My classes
    UnivariateContinDataObj tempUCDO;
    
    ANOVA1_Quant_CirclePlotView(ANOVA1_Quant_Model anova1_Quant_Model, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {        
        super(anova1_Quant_Model, anova1_Quant_Dashboard, "CirclePlot",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);   
        dm = anova1_Quant_Model.getDataManager();
        dm.whereIsWaldo(32, waldoFile, "Contstrucing...");
        nCheckBoxes = 0;     
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.anova1_Quant_Model = anova1_Quant_Model;
        this.anova1_Quant_Dashboard = anova1_Quant_Dashboard;
        gc_Quant_ANOVA1 = anova1_Quant_Canvas.getGraphicsContext2D();  
        gc_Quant_ANOVA1.setFont(Font.font("Courier New",
                                    FontWeight.BOLD,
                                    FontPosture.REGULAR,
                                    12));
        txtTitle1 = new Text(50, 25, "   Circle Plot ");
        txtTitle2 = new Text (60, 45, " Treatments/Groups ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
   
    public void doTheGraph() {
        dm.whereIsWaldo(50, waldoFile, "doTheGraph()");
        double downShift;
        
        yAxis.setForcedAxisEndsFalse(); // Just in case
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
        
        AnchorPane.setTopAnchor(anova1_Quant_Canvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(anova1_Quant_Canvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(anova1_Quant_Canvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(anova1_Quant_Canvas, 0.1 * tempHeight);
        
        gc_Quant_ANOVA1.clearRect(0, 0 , anova1_Quant_Canvas.getWidth(), anova1_Quant_Canvas.getHeight());
              
        for (int theBatch = 0; theBatch < nLevels; theBatch++) {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = anova1_Quant_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj("ANOVA1_Quant_CirclePlotView", tempQDV);
            double daXPosition = xAxis.getDisplayPosition(Double.valueOf(allTheLabels.get(theBatch)));
            nDataPoints = tempUCDO.getLegalN();
            
            // Get the Batch label and position it at the top of the graph
            String batchLabel = tempQDV.getTheVarLabel();
            int labelSize = batchLabel.length();
            double leftShift = 5.0 * labelSize;
            double labelXPosition = daXPosition - leftShift;
            double tempYAxisRange = yAxis.getUpperBound() - yAxis.getLowerBound();
            double baseYPosition = yAxis.getUpperBound() - 0.10 * tempYAxisRange;
            boolean theBatchIsEven = (theBatch % 2 == 0);
            if (theBatchIsEven) { downShift = 0.0; }
            else { downShift = 0.05 * tempYAxisRange; }
            double labelYPosition = yAxis.getDisplayPosition(baseYPosition - downShift);
            gc_Quant_ANOVA1.fillText(batchLabel, labelXPosition, labelYPosition);
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                gc_Quant_ANOVA1.strokeOval(xx - 5, yy - 5, 10, 10);
            }
            theMean = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            gc_Quant_ANOVA1.fillRect(daXPosition - 15, theMean - 1, 30, 2);
        }   //  Loop through batches
        
        qanova1_ContainingPane.requestFocus();
        qanova1_ContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                //System.out.println("Doing it -- Best Fit");
                WritableImage writableImage = qanova1_ContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));
    }
}
