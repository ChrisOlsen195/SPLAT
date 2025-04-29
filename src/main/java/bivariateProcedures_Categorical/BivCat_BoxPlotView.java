/**************************************************
 *              BivCat_BoxPlotView                *
 *                  03/22/25                      *
 *                    12:00                       *
 *************************************************/
package bivariateProcedures_Categorical;

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

public class BivCat_BoxPlotView extends BivCat_BoxCircleInterActView { 
    
        double dbl_Zero = 0.0;
        //String waldoFile = "";
        String waldoFile = "BivCat_BoxPlotView";

    BivCat_BoxPlotView(BivCat_Model bivCat_Model, 
            BivCat_Dashboard anova2_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {        
            super(bivCat_Model, anova2_Dashboard, placeHoriz, placeVert,
                withThisWidth, withThisHeight);
        dm.whereIsWaldo(27, waldoFile, "Constructing");
        strTitle2 = "Boxy Woxy Twozie";
        observedProportions = bivCat_Model.getObservedProportions();
    }
    
    public void doThePlot() {  
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = hBox_BivCat_CategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        //double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        //System.out.println("45 BivCat_BoxPlot, hBoxEdge = " + hBoxEdge);
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.85 * tempHeight);
        
        AnchorPane.setTopAnchor(hBox_BivCat_CategoryBoxes, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(hBox_BivCat_CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(hBox_BivCat_CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(hBox_BivCat_CategoryBoxes, 0.70 * tempHeight);        
        
        AnchorPane.setTopAnchor(categoryAxis_X, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(categoryAxis_X, 0.15 * tempWidth);
        AnchorPane.setRightAnchor(categoryAxis_X, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(categoryAxis_X, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(canvas_BivCat, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(canvas_BivCat, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(canvas_BivCat, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(canvas_BivCat, 0.2 * tempHeight);
        
        tempPos1 = categoryAxis_X.getDisplayPosition(categoryLabels.get(1));
        tempPos0 = categoryAxis_X.getDisplayPosition(categoryLabels.get(0));
        bigTickInterval = tempPos1 - tempPos0;

        yAxis.setForcedAxisEndsFalse();
        
        positionTopInfo();
        
        horizontalPositioner = new HorizontalPositioner(nTopLabels, nLeftLabels, bigTickInterval);        
        gc.clearRect(0, 0 , canvas_BivCat.getWidth(), canvas_BivCat.getHeight());
        for (int ithTopLabel = 0; ithTopLabel < nTopLabels; ithTopLabel++) {
            double daMiddleXPosition = categoryAxis_X.getDisplayPosition(categoryLabels.get(ithTopLabel));           
            for (int jthLeftLabel = 0; jthLeftLabel < nLeftLabels; jthLeftLabel++) {
                int theAppropriateLevel = (ithTopLabel) * nLeftLabels + jthLeftLabel + 1;
                double proportion = observedProportions[jthLeftLabel][ithTopLabel];
                double dispZero = yAxis.getDisplayPosition(dbl_Zero);
                
                double dispProportion = yAxis.getDisplayPosition(proportion);
                double dispChange = dispProportion - dispZero;
                gc.setLineWidth(2);
                gc.setStroke(Color.BLACK);

                setColor(jthLeftLabel);              
                horizPosition = horizontalPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                gc.strokeRect(horizPosition.getX(), dispProportion, horizPosition.getY(), -dispChange);
                gc.fillRect(horizPosition.getX(), dispProportion, horizPosition.getY(), -dispChange); 
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


