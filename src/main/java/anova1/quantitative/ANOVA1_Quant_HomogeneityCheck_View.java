/**************************************************
 *        ANOVA1_Quant_HomogeneityCheck_View      *
 *                    02/19/24                    *
 *                     12:00                      *
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
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ANOVA1_Quant_HomogeneityCheck_View extends ANOVA1_Quant_View { 
    // POJOs
    private double treatMean, allDataStandDev, initial_MinResid, 
            initial_MaxResid, daXPosition;

    private String leveneStat, levenePValue;
    
    //String waldoFile = "ANOVA1_Quant Homogeneity Check";
    String waldoFile = "";

    // My classes
    UnivariateContinDataObj tempUCDO;
    
    ANOVA1_Quant_HomogeneityCheck_View(ANOVA1_Quant_Model anova1_Quant_Model, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {        
        super(anova1_Quant_Model, anova1_Quant_Dashboard, "Homogeneity Check",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);   
        dm = anova1_Quant_Model.getDataManager();
        dm.whereIsWaldo(38, waldoFile, "Contstrucing...");
        
        nCheckBoxes = 0;

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
        txtTitle1 = new Text(50, 25, " Homogeneity Check ");
        txtTitle2 = new Text (60, 45, " Treatments/Groups ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void completeTheDeal() { 
        dm.whereIsWaldo(59, waldoFile, "completeTheDeal()");
        initializeGraphParameters();
        makeTheCheckBoxes();    
        setUpAnchorPane();
        setHandlers();
        qanova1_ContainingPane = dragableAnchorPane.getTheContainingPane();
        yAxis.setLowerBound(-3.5);
        yAxis.setUpperBound(3.5);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .remove(checkBoxRow);
        
        initial_MinResid = Double.MAX_VALUE;
        initial_MaxResid = -Double.MAX_VALUE;
        
        for (int theBatch = 0; theBatch < nLevels; theBatch++) {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = anova1_Quant_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj("80 ANOVA1_Quant_Homog_Check_View", tempQDV);

            nDataPoints = tempQDV.getLegalN();
            treatMean = tempQDV.getTheMean();
            
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double temp1 = tempUCDO.getIthSortedValue(dataPoint);
                double standResid = (temp1 - treatMean) / allDataStandDev;
                initial_MinResid = Math.min(initial_MinResid, standResid); 
                initial_MaxResid = Math.max(initial_MaxResid, standResid);
            }
        }
        
        initial_MaxResid *= 1.125;
        initial_MinResid *= 1.125;
        
        deltaY = .005 * (initial_MaxResid - initial_MinResid);
        yAxis.setLowerBound(initial_MinResid);
        yAxis.setUpperBound(initial_MaxResid); 
        doTheGraph();   
    }
   
    public void doTheGraph() {   
        dm.whereIsWaldo(100, waldoFile, "doTheGraph()");
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

        leveneStat = "Levene's W = " + String.format("%5.3f", anova1_Quant_Model.getLevenesStat());
        levenePValue = "pValue = " + String.format("%5.3f", anova1_Quant_Model.getLevenesPValue());
        gc_Quant_ANOVA1.fillText(leveneStat, 10, 10);
        gc_Quant_ANOVA1.fillText(levenePValue, 10, 25);
        
        allData_QDV = new QuantitativeDataVariable();
        allData_QDV  = anova1_Quant_Model.getIthQDV(0);
            
        allDataStandDev = anova1_Quant_Model.getMSError();
            
        for (int theBatch = 0; theBatch < nLevels; theBatch++) {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = anova1_Quant_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj("156 ANOVA1_Quant_Homog_Check_View",tempQDV);
            daXPosition = xAxis.getDisplayPosition(Double.valueOf(allTheLabels.get(theBatch)));
            nDataPoints = tempUCDO.getLegalN();
            treatMean = tempUCDO.getTheMean();
            
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double temp1 = tempUCDO.getIthSortedValue(dataPoint);
                double standResid = (temp1 - treatMean) / allDataStandDev;
                double yy = yAxis.getDisplayPosition(standResid);
                gc_Quant_ANOVA1.strokeOval(xx - 5, yy - 5, 10, 10);
            }
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

