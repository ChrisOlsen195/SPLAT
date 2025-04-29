/**************************************************
 *                RCB_BoxPlotView                 *
 *                  03/22/25                      *
 *                    21:00                       *
 *************************************************/
package anova2;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import utilityClasses.*;

public class ANOVA2_RCB_BoxPlotView extends ANOVA2_RCB_BoxCircleInterActView { 
    ANOVA2_RCB_BoxPlotView(ANOVA2_RCB_Model anova2_Model, 
    ANOVA2_RCB_wReplicates_Dashboard rcb_Dashboard,double placeHoriz, double placeVert,
    double withThisWidth,  double withThisHeight) {
        super(anova2_Model, rcb_Dashboard, placeHoriz, placeVert,
            withThisWidth, withThisHeight);

        //waldoFile = "";
        waldoFile = "RCB_BoxPlotView";
        dm.whereIsWaldo(24, waldoFile, "Constructing");
        strTitle2 = "Boxy Woxy Twozie";
        whiskerEndRanks = new int[2]; 
    }
    
    public void doThePlot() {
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = anova2CategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.85 * tempHeight);
        
        AnchorPane.setTopAnchor(anova2CategoryBoxes, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(anova2CategoryBoxes, 0.70 * tempHeight);        
        
        AnchorPane.setTopAnchor(categoryAxis_X, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(categoryAxis_X, 0.15 * tempWidth);
        AnchorPane.setRightAnchor(categoryAxis_X, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(categoryAxis_X, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(canvas_ANOVA2, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(canvas_ANOVA2, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(canvas_ANOVA2, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(canvas_ANOVA2, 0.2 * tempHeight);
        
        tempPos1 = categoryAxis_X.getDisplayPosition(categoryLabels.get(1));
        tempPos0 = categoryAxis_X.getDisplayPosition(categoryLabels.get(0));
        bigTickInterval = tempPos1 - tempPos0;
        yAxis.setForcedAxisEndsFalse();
        
        positionTopInfo();
        
        horizontalPositioner = new HorizontalPositioner(nFactorA_Levels, nFactorB_Levels, bigTickInterval);        
        gc.clearRect(0, 0 , canvas_ANOVA2.getWidth(), canvas_ANOVA2.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++) {
            double daMiddleXPosition = categoryAxis_X.getDisplayPosition(categoryLabels.get(theBetweenBatch - 1));            
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                tempUCDO = anova2_RCB_Model.getPrelimAB().getIthUCDO(theAppropriateLevel);
                nDataPoints = tempUCDO.getLegalN();
                fiveNumberSummary = new double[5];
                fiveNumberSummary = tempUCDO.get_5NumberSummary();
                whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
                bottomOfLowWhisker = yAxis.getDisplayPosition(fiveNumberSummary[0]);

                if (whiskerEndRanks[0] != -1) {
                    bottomOfLowWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));
                }
                
                topOfHighWhisker = yAxis.getDisplayPosition(fiveNumberSummary[4]);

                if (whiskerEndRanks[1] != -1) {
                    topOfHighWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));
                }
                
                double q1_display = yAxis.getDisplayPosition(fiveNumberSummary[1]);
                double q2_display = yAxis.getDisplayPosition(fiveNumberSummary[2]);
                double q3_display = yAxis.getDisplayPosition(fiveNumberSummary[3]);
                double iqr_display = q3_display - q1_display;

                gc.setLineWidth(2);
                gc.setStroke(Color.BLACK);

                setColor(theWithinBatch - 1);              
                horizPosition = horizontalPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                gc.strokeRect(horizPosition.getX(), q3_display, horizPosition.getY(), -iqr_display);    //  box
                gc.strokeLine(horizPosition.getX(), q2_display, horizPosition.getX() + horizPosition.getY(), q2_display);    //  Median

                gc.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), bottomOfLowWhisker, horizPosition.getX() + 0.5 * horizPosition.getY(), q1_display);  //  Low whisker
                gc.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), q3_display, horizPosition.getX() + 0.5 * horizPosition.getY(), topOfHighWhisker);  //  High whisker

                //  Top & bottom of whisker
                double topBottomLength = horizontalPositioner.getCIEndWidthFrac();
                double midBar = horizontalPositioner.getMidBarPosition(theAppropriateLevel, daMiddleXPosition);
                gc.strokeLine(midBar - topBottomLength, bottomOfLowWhisker, midBar + topBottomLength, bottomOfLowWhisker);  
                gc.strokeLine(midBar - topBottomLength, topOfHighWhisker, midBar + topBottomLength, topOfHighWhisker);  

                if (whiskerEndRanks[0] != -1) {   //  Are there low outliers?
                    int dataPoint = 0;
                    while (dataPoint < whiskerEndRanks[0]) {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        gc.fillOval(xx - 3, yy - 3, 6, 6);
                        dataPoint++;
                    }
                }

                if (whiskerEndRanks[1] != -1) { //  Are there high outliers?
                    for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++) {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        gc.fillOval(xx - 3, yy - 3, 6, 6);
                    }
                }
            }  
        }  
        
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
    }   
}


