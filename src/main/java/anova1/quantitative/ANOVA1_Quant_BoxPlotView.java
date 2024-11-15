/**************************************************
 *             ANOVA1_Quant_BoxPlotView           *
 *                    10/07/24                    *
 *                      15:00                     *
 *************************************************/
package anova1.quantitative;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ANOVA1_Quant_BoxPlotView extends ANOVA1_Quant_View { 
    // POJOs
    
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Quant_BoxPlotView";
    String waldoFile = "";
    
    double bottomOfLowWhisker, topOfHighWhisker;
    double[] fiveNumberSummary;
    
    int[] whiskerEndRanks;

    //  My classes
    UnivariateContinDataObj tempUCDO;
    
    // FX classes

    ANOVA1_Quant_BoxPlotView(ANOVA1_Quant_Model anova1_Quant_Model, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {  
        super(anova1_Quant_Model, anova1_Quant_Dashboard, "BoxPlot",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);
        dm = anova1_Quant_Model.getDataManager();
        dm.whereIsWaldo(47, waldoFile, "Constructing");
        nCheckBoxes = 2;
        strCheckBoxDescriptions = new String[3];
        strCheckBoxDescriptions[0] = " Means diamond ";
        strCheckBoxDescriptions[1] = " Extreme Outliers ";
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.anova1_Quant_Model = anova1_Quant_Model;
        this.anova1_Quant_Dashboard = anova1_Quant_Dashboard;
        allTheLabels = anova1_Quant_Model.getCategoryLabels();
        gc_Quant_ANOVA1 = anova1_Quant_Canvas.getGraphicsContext2D(); 
        gc_Quant_ANOVA1.setFont(Font.font("Courier New",
                                    FontWeight.BOLD,
                                    FontPosture.REGULAR,
                                    12));
        txtTitle1 = new Text(50, 25, " One way ANOVA ");
        txtTitle2 = new Text (60, 45, " Treatments/Groups ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
    }
    
    public void doTheGraph() {    
        double daXPosition, text1Width, text2Width, paneWidth,
               txt1Edge, txt2Edge, downShift;
        yAxis.setForcedAxisEndsFalse(); // Just in case
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        paneWidth = dragableAnchorPane.getWidth();
        txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(theCheckBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(theCheckBoxRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(theCheckBoxRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(theCheckBoxRow, 0.95 * tempHeight);
       
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
            tempUCDO = new UnivariateContinDataObj("ANOVA1_Quant_BoxPlotView", tempQDV);
            daXPosition = xAxis.getDisplayPosition(Double.valueOf(allTheLabels.get(theBatch)));
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
            else { downShift = 0.05 * tempYAxisRange;  }
            
            double labelYPosition = yAxis.getDisplayPosition(baseYPosition - downShift);
            gc_Quant_ANOVA1.fillText(batchLabel, labelXPosition, labelYPosition);

            fiveNumberSummary = new double[5];
            fiveNumberSummary = tempUCDO.get_5NumberSummary();
            whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
            double theMean = tempUCDO.getTheMean();
            double theStDev = tempUCDO.getTheStandDev();
            bottomOfLowWhisker = yAxis.getDisplayPosition(fiveNumberSummary[0]);
 
            if (whiskerEndRanks[0] != -1)
                bottomOfLowWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));

            topOfHighWhisker = yAxis.getDisplayPosition(fiveNumberSummary[4]);

            if (whiskerEndRanks[1] != -1)
                topOfHighWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));

            double q1_display = yAxis.getDisplayPosition(fiveNumberSummary[1]);
            double q2_display = yAxis.getDisplayPosition(fiveNumberSummary[2]);
            double q3_display = yAxis.getDisplayPosition(fiveNumberSummary[3]);
            
            double mean_display = yAxis.getDisplayPosition(theMean);
            double upperStDev_display = yAxis.getDisplayPosition(theMean + theStDev);
            double lowerStDev_display = yAxis.getDisplayPosition(theMean - theStDev);
            
            double iqr_display = q3_display - q1_display;
            double iqr = fiveNumberSummary[3] - fiveNumberSummary[1];
            
            double spacing = 100.;

            gc_Quant_ANOVA1.setLineWidth(2);
            gc_Quant_ANOVA1.setStroke(Color.BLACK);
            //double spaceFraction = 0.25 * spacing;
            double spaceFraction = 0.15 * spacing;
            // x, y, w, h
            gc_Quant_ANOVA1.strokeRect(daXPosition - spaceFraction, q3_display, 2 * spaceFraction, -iqr_display);    //  box
            gc_Quant_ANOVA1.strokeLine(daXPosition - spaceFraction, q2_display, daXPosition + spaceFraction, q2_display);    //  Median

            gc_Quant_ANOVA1.strokeLine(daXPosition, bottomOfLowWhisker, daXPosition, q1_display);  //  Low whisker
            gc_Quant_ANOVA1.strokeLine(daXPosition, q3_display, daXPosition, topOfHighWhisker);  //  High whisker
            
            // means & stDev diamond            
            if (anova1_Quant_CheckBoxes[0].isSelected()){
                gc_Quant_ANOVA1.setLineWidth(1);
                gc_Quant_ANOVA1.setStroke(Color.RED);
                gc_Quant_ANOVA1.strokeLine(daXPosition - spaceFraction, mean_display, daXPosition, upperStDev_display);  
                gc_Quant_ANOVA1.strokeLine(daXPosition - spaceFraction, mean_display, daXPosition, lowerStDev_display);
                gc_Quant_ANOVA1.strokeLine(daXPosition + spaceFraction, mean_display, daXPosition, upperStDev_display);  
                gc_Quant_ANOVA1.strokeLine(daXPosition + spaceFraction, mean_display, daXPosition, lowerStDev_display);
                gc_Quant_ANOVA1.setLineWidth(2);
                gc_Quant_ANOVA1.setStroke(Color.BLACK);
            }

            // Low outliers
            if (whiskerEndRanks[0] != -1) {    //  Are there low outliers?
                int dataPoint = 0;
                
                while (dataPoint < whiskerEndRanks[0]) {
                    double xx = daXPosition;
                    double tempY = tempUCDO.getIthSortedValue(dataPoint);
                    double yy = yAxis.getDisplayPosition(tempY);
                    
                    // Extreme outlier
                    double tempLowBall = fiveNumberSummary[1] - 1.5 * iqr;
                    //double tempHighBall = fiveNumberSummary[3] + 1.5 * iqr; 
                    
                    if ((tempY < tempLowBall) && (anova1_Quant_CheckBoxes[1].isSelected() == true)) {
                        gc_Quant_ANOVA1.strokeOval(xx - 6, yy - 6, 12, 12);
                    }
                    else {
                        gc_Quant_ANOVA1.fillOval(xx - 3, yy - 3, 6, 6);
                    }
                    
                    dataPoint++;
                }
            }

            // High outliers
            if (whiskerEndRanks[1] != -1) {   //  Are there high outliers?

                for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++) {
                    double xx = daXPosition;
                    double tempY = tempUCDO.getIthSortedValue(dataPoint);
                    double yy = yAxis.getDisplayPosition(tempY);                    
                    double tempHighBall = fiveNumberSummary[3] + 1.5 * iqr; 
                    if ((tempY > tempHighBall) && (anova1_Quant_CheckBoxes[1].isSelected() == true)){
                        gc_Quant_ANOVA1.strokeOval(xx - 6, yy - 6, 12, 12);
                    }
                    else {
                        gc_Quant_ANOVA1.fillOval(xx - 3, yy - 3, 6, 6);   
                    }
                }    
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
}
