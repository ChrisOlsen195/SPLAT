/**************************************************
 *              ANOVA2_CirclePlotView             *
 *                  02/19/24                      *
 *                    12:00                       *
 *************************************************/
package anova2;

import dataObjects.UnivariateContinDataObj;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import utilityClasses.*;

public class ANOVA2_CirclePlotView extends ANOVA2_BoxCircleInterActView { 
    
        String waldoFile = "";
        //String waldoFile = "ANOVA2_CirclePlotView";

    ANOVA2_CirclePlotView(ANOVA2_Factorial_Model anova2_Model, 
        ANOVA2_RCB_Dashboard anova2_Dashboard,
        double placeHoriz, double placeVert,
        double withThisWidth, double withThisHeight) {
        super(anova2_Model, anova2_Dashboard, placeHoriz, placeVert,
        withThisWidth, withThisHeight);
        dm.whereIsWaldo(26, waldoFile, "Constructing");
        strTitle2 = "Circly Wirkly Twosie";
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
        AnchorPane.setLeftAnchor(categoryAxis_X, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(categoryAxis_X, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(categoryAxis_X, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
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
                tempUCDO = new UnivariateContinDataObj();
                tempUCDO = anova2_Factorial_Model.getPrelimAB().getIthUCDO(theAppropriateLevel);
                nDataPoints = tempUCDO.getLegalN();
                setColor(theWithinBatch - 1);
                horizPosition = horizontalPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);                

                for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                    double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                    double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                    gc.strokeOval(xx - 5, yy - 5, 10, 10);
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


