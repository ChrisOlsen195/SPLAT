/**************************************************
 *           MultUni_MeanAndErrorView             *
 *                    01/16/25                    *
 *                      12:00                     *
 *************************************************/
package proceduresManyUnivariate;

import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import javafx.collections.FXCollections;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import superClasses.*;
import utilityClasses.MyAlerts;

public class MultUni_MeanAndErrorView extends QuantCat_View { 
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean[] radioButtonSettings;
    
    int nRadioButtons, errBarChoice;
    
    String errorString;
    String[] strRadioButtonDescriptions;
    
    // My classes
    QuantitativeDataVariable tempQDV;
    UnivariateContinDataObj tempUCDO;

    // POJOs / FX
    AnchorPane radioButtonRow;
    RadioButton[] anova1_RadioButtons;

    MultUni_MeanAndErrorView(MultUni_Model multiUni_Model, MultUni_Dashboard multiUni_Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(multiUni_Model, multiUni_Dashboard, "MeanAndError",
              placeHoriz, placeVert,  withThisWidth, withThisHeight); 
        if (printTheStuff == true) {
            System.out.println("60 *** MultUni_MeanAndErrorView, Constructing");
        }
        this.multiUni_Model = multiUni_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = multiUni_Model.getCatLabels();
        quantCatCanvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        quantCatCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        quantCatCanvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcQuantCat = quantCatCanvas.getGraphicsContext2D();
        gcQuantCat.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        quantCat_ContainingPane = new Pane();
        
        txtTitle1 = new Text(50, 25, " Mean +/- Error bars ");
        subTitle = multiUni_Dashboard.getVarDescr();
        String strForTitle2 = subTitle;
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));
        
        nRadioButtons = 4;
        strRadioButtonDescriptions = new String[4];
        strRadioButtonDescriptions[0] = " Standard Error ";
        strRadioButtonDescriptions[1] = " Margin of Error ";
        strRadioButtonDescriptions[2] = " Standard Deviation ";   
        strRadioButtonDescriptions[3] = " 95% Conf Int ";        
        makeTheCheckBoxes();
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        quantCatCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        quantCatCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

        for (int iBtns = 0; iBtns < nRadioButtons; iBtns++) {
            anova1_RadioButtons[iBtns].translateXProperty()
                                        .bind(quantCatCanvas.widthProperty()
                                        .divide(250.0)
                                        .multiply(65. * iBtns)
                                        .subtract(225.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(radioButtonRow, txtTitle1, txtTitle2, xAxis, yAxis, quantCatCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void makeTheCheckBoxes() { makeTheRadioButtons(); }
    
    public void makeTheRadioButtons() { 
        // Determine which graphs are initially shown
        radioButtonSettings = new boolean[nRadioButtons];
        
        for (int ithBox = 0; ithBox < nRadioButtons; ithBox++) {
            radioButtonSettings[ithBox] = false;
        }
        
        radioButtonRow = new AnchorPane();
        radioButtonRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        anova1_RadioButtons = new RadioButton[nRadioButtons];
        
        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < nRadioButtons; i++) {
            anova1_RadioButtons[i] = new RadioButton(strRadioButtonDescriptions[i]);
            group.getToggles().add(anova1_RadioButtons[i]);
            anova1_RadioButtons[i].setMaxWidth(Double.MAX_VALUE);
            anova1_RadioButtons[i].setId(strRadioButtonDescriptions[i]);
            anova1_RadioButtons[i].setSelected(radioButtonSettings[i]);
            anova1_RadioButtons[i].setTextFill(Color.BLUE);
            anova1_RadioButtons[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            anova1_RadioButtons[i].setOnAction(e->{
                RadioButton tb = ((RadioButton) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                for (int ithID = 0; ithID < nRadioButtons; ithID++) {
                    if (daID.equals(strRadioButtonDescriptions[ithID])) {
                        radioButtonSettings[ithID] = (checkValue == true);
                        errBarChoice = ithID;
                        doTheGraph();
                    }
                }

            }); //  end setOnAction
        }  
        anova1_RadioButtons[0].setSelected(true);
        radioButtonRow.getChildren().addAll(anova1_RadioButtons);
        }

    public void doTheGraph() {
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(radioButtonRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(radioButtonRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(radioButtonRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(radioButtonRow, 0.95 * tempHeight);
       
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
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(anova1_RadioButtons[chex], (chex) * tempWidth / 5.0);
        }

        gcQuantCat.clearRect(0, 0 , quantCatCanvas.getWidth(), quantCatCanvas.getHeight());
        
        for (int theBatch = 0; theBatch < nEntities; theBatch++) {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = multiUni_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj("MultUni_MeanAndErrorView", tempQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch));
  
            nDataPoints = tempUCDO.getLegalN();
            
            double spacing = 100.;

            gcQuantCat.setLineWidth(2);
            gcQuantCat.setStroke(Color.GREEN);
            gcQuantCat.setFill(Color.GREEN);
            double spaceFraction = 0.15 * spacing;

            gcQuantCat.setStroke(Color.BLACK);

            switch (errBarChoice) {
                // Standard Error
                case 0:
                    errorBarLength = tempUCDO.getStandErrMean();
                    errorString = "Mean +/- Standard Error";                    
                    break;
                
                // Margin of Error
                case 1:
                    errorBarLength = tempUCDO.getTheMarginOfErr(0.95);
                    errorString = "Mean +/- Margin of Error";                    
                    break;
                
                // Standard deviation
                case 2:
                    errorBarLength = tempUCDO.getTheStandDev();
                    errorString = "Mean +/- Standard Deviation";                   
                    break;
                
                // 95% confidence interval
                case 3:
                    errorBarLength = tempUCDO.getTheMarginOfErr(0.95);
                    errorString = "Mean +/- 95% Confidence Interval";                    
                    break;
                
                default:
                    String switchFailure = "Switch failure: MultUni_MeanAndErrorView 242 " + errBarChoice;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }

            errorBarDescription = new Text(0, 0, errorString);

            //  Factors below (15 & 10) are functions of the text size
            errorBarDescription.setX(anchorTitleInfo.getWidth()/2. - 8 * errorString.length()/2);
            // Horizontal lines
            double topOfBar = yAxis.getDisplayPosition(tempUCDO.getTheMean() + errorBarLength);
            double meanOfBar = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            double bottomOfBar = yAxis.getDisplayPosition(tempUCDO.getTheMean() - errorBarLength);
            gcQuantCat.strokeLine(daXPosition - spaceFraction, topOfBar, daXPosition + spaceFraction, topOfBar);
            gcQuantCat.strokeOval(daXPosition - 5, meanOfBar - 5, 10, 10);
            gcQuantCat.strokeLine(daXPosition - spaceFraction, bottomOfBar, daXPosition + spaceFraction, bottomOfBar);
            // ErrorBar
            gcQuantCat.strokeLine(daXPosition, topOfBar, daXPosition, bottomOfBar);  //  Low whisker
        }   //  Loop through batches   
        
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

