/**************************************************
 *              MultUni_CirclePlotView            *
 *                    04/02/24                    *
 *                      09:00                     *
 *************************************************/
package proceduresManyUnivariate;

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
import superClasses.*;

public class MultUni_CirclePlotView extends QuantCat_View  { 
    // POJOs
    
    // Make empty if no-print
    //String waldoFile = "MultUni_CirclePlotView";
    String waldoFile = "";
    
    private double theMean; 
    
    // My classes
    Data_Manager dm;
    public MultUni_Dashboard multUni_Dashboard;
    public MultUni_Model multUni_Model;
    private UnivariateContinDataObj tempUCDO;
    private QuantitativeDataVariable tempQDV;
    
    MultUni_CirclePlotView(MultUni_Model multUni_Model, MultUni_Dashboard multUni_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {        
        super(multUni_Model, multUni_Dashboard, "CirclePlot",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);   
        dm = multUni_Model.getDataManager();
        dm.whereIsWaldo(47, waldoFile, "Constructing with dm");
        this.multiUni_Model = multUni_Model;
        this.multUni_Dashboard = multUni_Dashboard;
        nCheckBoxes = 3;
        strCheckBoxDescriptions = new String[3];
        strCheckBoxDescriptions[0] = "";
        strCheckBoxDescriptions[1] = "";
        strCheckBoxDescriptions[2] = "";      
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        allTheLabels = multUni_Model.getCatLabels();
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();  
        subTitle = multUni_Dashboard.getVarDescr();
        responseVar = multUni_Dashboard.getResponseVar();
        
        String strForTitle1 = "Circle Plot";
        String strForTitle2 = subTitle;
        
        txtTitle1 = new Text (60, 30, strForTitle1);
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
        dm.whereIsWaldo(69, waldoFile, "end Constructing with dm");
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
            tempQDV = new QuantitativeDataVariable();
            tempQDV = multiUni_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj("MultUni_CirclePlotView", tempQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch));

            nDataPoints = tempUCDO.getLegalN();
            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                gcQuantCat.strokeOval(xx - 5, yy - 5, 10, 10);
            }
            
            theMean = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            gcQuantCat.fillRect(daXPosition - 15, theMean - 1, 30, 2);
        }
        
        quantCat_ContainingPane.requestFocus();
        quantCat_ContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = quantCat_ContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));         
    }
}
