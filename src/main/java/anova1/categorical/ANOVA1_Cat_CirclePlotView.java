/**************************************************
 *           ANOVA1_Cat_CirclePlotView            *
 *                    02/19/24                    *
 *                      12:00                     *
 *************************************************/
package anova1.categorical;

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
import splat.Data_Manager;
import superClasses.QuantCat_View;

public class ANOVA1_Cat_CirclePlotView extends QuantCat_View{ 
    // POJOs
    private double theMean;

    //String waldoFile = "ANOVA1_Cat_CirclePlotView";
    String waldoFile = "";
    
    // My classes
    private UnivariateContinDataObj tempUCDO;
    Data_Manager dm;
    
    ANOVA1_Cat_CirclePlotView(ANOVA1_Cat_Model anova1_Cat_Model, ANOVA1_Cat_Dashboard anova1_Cat_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(anova1_Cat_Model, anova1_Cat_Dashboard, "CirclePlot",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);   
        dm = anova1_Cat_Model.getDataManager();
        dm.whereIsWaldo(43, waldoFile, "Constructing");
        nCheckBoxes = 0;    
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.anova1_Cat_Model = anova1_Cat_Model;
        this.anova1_Cat_Dashboard = anova1_Cat_Dashboard;
        allTheLabels = anova1_Cat_Model.getVarLabels();
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();  
        explanVar = anova1_Cat_Dashboard.getExplanVar();
        responseVar = anova1_Cat_Dashboard.getResponseVar();
        String strForTitle1 = "Circle Plot of Raw Data";
        String strForTitle2 = responseVar + " vs. " + explanVar;
        txtTitle1 = new Text (60, 30, strForTitle1);
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
     public void completeTheDeal() { 
        initializeGraphParameters();    
        setUpAnchorPane();
        setHandlers();
        quantCat_ContainingPane = dragableAnchorPane.getTheContainingPane();    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .remove(checkBoxRow);
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);
        dm.whereIsWaldo(71, waldoFile, "completeTheDeal()");
        doTheGraph();   
    }   
    
    public void doTheGraph() {   
        yAxis.setForcedAxisEndsFalse(); // Just in case
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(theActualRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(theActualRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(theActualRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(theActualRow, 0.95 * tempHeight);
       
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
        
        AnchorPane.setTopAnchor(quantCatCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(quantCatCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(quantCatCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(quantCatCanvas, 0.1 * tempHeight);
        
        gcQuantCat.clearRect(0, 0 , quantCatCanvas.getWidth(), quantCatCanvas.getHeight());
        
        for (int theBatch = 0; theBatch < nEntities; theBatch++) {
            batchQDV = new QuantitativeDataVariable();
            batchQDV = anova1_Cat_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj("ANOVA1_CatCirclePlotView", batchQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch));

            nDataPoints = tempUCDO.getLegalN();

            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                gcQuantCat.strokeOval(xx - 5, yy - 5, 10, 10);
            }
            
            theMean = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            gcQuantCat.fillRect(daXPosition - 15, theMean - 1, 30, 2);
        }   //  Loop through batches
        
        quantCat_ContainingPane.requestFocus();
        quantCat_ContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                //System.out.println("Doing it -- Best Fit");
                WritableImage writableImage = quantCat_ContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));        
    }
}
