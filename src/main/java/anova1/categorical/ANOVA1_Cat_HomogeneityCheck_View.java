/**************************************************
 *         ANOVA1_Cat_HomogeneityCheck_View       *
 *                    02/19/24                    *
 *                     12:00                      *
 *************************************************/
package anova1.categorical;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import splat.Data_Manager;
import superClasses.QuantCat_View;

public class ANOVA1_Cat_HomogeneityCheck_View extends QuantCat_View { 
    // POJOs
    private double treatMean, allDataStandDev,
            initial_MinResid, initial_MaxResid;

    private String leveneStat, levenePValue;
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Cat_HomogeneityCheck_View";
    String waldoFile = "";
    
    // My classes
    Data_Manager dm;
    
    private UnivariateContinDataObj batchUCDO;
    
    ANOVA1_Cat_HomogeneityCheck_View(ANOVA1_Cat_Model anova1_Cat_Model, ANOVA1_Cat_Dashboard anova1_Cat_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(anova1_Cat_Model, anova1_Cat_Dashboard, "Homogeneity Check",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);    
        dm = anova1_Cat_Model.getDataManager();
        dm.whereIsWaldo(47, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.anova1_Cat_Model = anova1_Cat_Model;
        this.anova1_Cat_Dashboard = anova1_Cat_Dashboard;
        allTheLabels = anova1_Cat_Model.getCategoryLabels();
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();  
        explanVar = anova1_Cat_Dashboard.getExplanVar();
        responseVar = anova1_Cat_Dashboard.getResponseVar();
        String strForTitle1 = "Residuals' Homogeneity Check";
        String strForTitle2 = responseVar + " vs. " + explanVar;
        txtTitle1 = new Text (60, 30, strForTitle1);
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void completeTheDeal() { 
        dm.whereIsWaldo(65, waldoFile, "completeTheDeal()");
        initializeGraphParameters();   
        setUpAnchorPane();
        setHandlers();
        quantCat_ContainingPane = dragableAnchorPane.getTheContainingPane();
        yAxis.setLowerBound(-3.5);
        yAxis.setUpperBound(3.5);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .remove(checkBoxRow);
        
        initial_MinResid = Double.MAX_VALUE;
        initial_MaxResid = -Double.MAX_VALUE;
        
        for (int theBatch = 0; theBatch < nEntities; theBatch++) {
            batchQDV = new QuantitativeDataVariable();
            batchQDV = anova1_Cat_Model.getIthQDV(theBatch);
            batchUCDO = new UnivariateContinDataObj("78 ANOVA1_Cat_Homog_Check_View", batchQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch));
            nDataPoints = batchUCDO.getLegalN();
            treatMean = batchQDV.getTheMean();
            
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double temp1 = batchUCDO.getIthSortedValue(dataPoint);
                double standResid = (temp1 - treatMean) / allDataStandDev;
                initial_MinResid = Math.min(initial_MinResid, standResid); 
                initial_MaxResid = Math.max(initial_MaxResid, standResid);
            }
        }   //  Loop through batches  
        
        if (initial_MaxResid >0) { initial_MaxResid *= 1.125; }
        
        if (initial_MinResid < 0) { initial_MinResid *= 1.125; }

        deltaY = .005 * (initial_MaxResid - initial_MinResid);
        yAxis.setLowerBound(initial_MinResid);
        yAxis.setUpperBound(initial_MaxResid); 
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
        
        leveneStat = "Levene's W = " + String.format("%5.3f", anova1_Cat_Model.getLevenesStat());
        levenePValue = "pValue = " + String.format("%5.3f", anova1_Cat_Model.getLevenesPValue());
        gcQuantCat.fillText(leveneStat, 10, 10);
        gcQuantCat.fillText(levenePValue, 10, 25);
        
        allData_QDV = new QuantitativeDataVariable();
        allData_QDV  = anova1_Cat_Model.getIthQDV(0);
            
        allDataStandDev = anova1_Cat_Model.getMSError();
            
        for (int theBatch = 0; theBatch < nEntities; theBatch++) {
            batchQDV = new QuantitativeDataVariable();
            batchQDV = anova1_Cat_Model.getIthQDV(theBatch);
            batchUCDO = new UnivariateContinDataObj("160 ANOVA1_Cat_Homog_Check_View", batchQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch));

            nDataPoints = batchUCDO.getLegalN();
            treatMean = batchQDV.getTheMean();            
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double temp1 = batchUCDO.getIthSortedValue(dataPoint);
                double standResid = (temp1 - treatMean) / allDataStandDev;
                double yy = yAxis.getDisplayPosition(standResid);
                gcQuantCat.strokeOval(xx - 5, yy - 5, 10, 10);
            }
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
