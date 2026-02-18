/**************************************************
 *              UnivCat_PieChartView              *
 *                    02/19/24                    *
 *                     15:00                      *
 *************************************************/
package univariateProcedures_Categorical;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.ArcType;
import splat.Data_Manager;
import utilityClasses.*;

public class UnivCat_PieChart 
{
    // POJOs
 
    int nCatValues, nColsCat, nSquaresRow1, nSquaresRow2, nLittleSquares,
        nRowsCat;
    

    double initHoriz, initVert, initWidth, initHeight, sqrt2, halfRoot2, 
           onePlusOver, initialCircleRadii, radii, piOver180, text1Width, 
           text2Width, tempDouble;

    double[] columnProps, cumRowProps, startingAngles, arcLengths, middlesOfAngles;

    String strLeftVariable, graphsCSS, tempStr;
    String[] leftLabels, topLabels;
    
    //String waldoFile = "UnivCat_PieChartView";
    String waldoFile = ""; 
    
    //  My classes
    Data_Manager dm;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis, yAxis;
    UnivCat_Model univCat_Model;

    //  FX objects
    AnchorPane pieChartAnchorPane;
    Canvas pieChartCanvas;
    Color[] graphColors;
    
    GraphicsContext pieChartGC; // Required for drawing on the Canvas
    GridPane gridPane_CategoryBoxes;
    HBox[] squaresNText;
    Pane theContainingPane;
    Rectangle[] littleSquares;
    Slider radiiSlider;
    Text txtTitle1, txtTitle2, sliderControlText;
    Text[] littleSquaresText;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public UnivCat_PieChart(UnivCat_Model univCat_Model, UnivCat_Dashboard univCat_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        dm = univCat_Model.getDataManager();
        dm.whereIsWaldo(84, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        this.univCat_Model = univCat_Model;
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        graphColors = Colors_and_CSS_Strings.getGraphColors_06();
        dm.whereIsWaldo(94, waldoFile, "Constructing, nGraphColors = " + graphColors.length);
        nColsCat = 1;
        
        txtTitle1 = new Text("Pie Chart"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));        
        txtTitle1.getStyleClass().add("titleLabel"); 
        txtTitle2 = new Text(univCat_Model.getDescriptionOfVariable()); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        
        radiiSlider = new Slider(0.1, 2.0, 1.0);
        radiiSlider.setValue(1.0);
        
        radiiSlider.valueProperty()
                   .addListener(ov ->
                        {
                            radii = radiiSlider.getValue();
                            doTheGraph();  
                        });
        
        sliderControlText = new Text("Fine radius control " + "\u2192");
        sliderControlText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));
        
        sqrt2 = Math.sqrt(2.0);        
        halfRoot2 = sqrt2 / 2.0;
        onePlusOver = (1.0 + halfRoot2);
        piOver180 = Math.PI / 180.0;
    }
    
    public void completeTheDeal() {
        constructPieInfo(); 
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty()
                          .addListener(ov-> {
                                radiiSlider.setValue(1.0);
                                doTheGraph();
                          });        
        dragableAnchorPane.widthProperty()
                          .addListener(ov-> {
                              radiiSlider.setValue(1.0);
                              doTheGraph();});        
        theContainingPane = dragableAnchorPane.getTheContainingPane();         
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() {
        topLabels = new String[univCat_Model.getNCategories()];
        topLabels = univCat_Model.getCategoriesAsStrings();

        xAxis = new JustAnAxis(-0.05, 0.95);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel("This is xAxis");
        xAxis.setVisible(false);    //  Used only for positioning other stuff
        xAxis.forceLowScaleEndToBe(-0.05);
        xAxis.forceHighScaleEndToBe(0.95);

        yAxis = new JustAnAxis(0.0, 1.01);
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.forceHighScaleEndToBe(1.01);
        yAxis.setSide(Side.LEFT);
        yAxis.setVisible(false);    //  Used only for positioning other stuff
    }
    
    private void setUpUI() {
        pieChartCanvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        pieChartGC = pieChartCanvas.getGraphicsContext2D();
        gridPane_CategoryBoxes = new GridPane();
        gridPane_CategoryBoxes.setAlignment(Pos.CENTER);
        gridPane_CategoryBoxes.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        littleSquaresText = new Text[nLittleSquares];
        squaresNText = new HBox[nLittleSquares];
        
        int nGridRow = 0; 
        int nGridCol = 0;
        
        for (int i = 0; i < nCatValues; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);
            littleSquaresText[i] = new Text(0, 0, leftLabels[i]);
            littleSquaresText[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,12));
            littleSquaresText[i].setFill(graphColors[i]);
            squaresNText[i] = new HBox(12);
            squaresNText[i].setFillHeight(false);
            squaresNText[i].setAlignment(Pos.CENTER);
            squaresNText[i].setStyle("-fx-padding: 2;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-insets: 5;" +
                                     "-fx-border-radius: 5;");
            sliderControlText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));
            squaresNText[i].getChildren().addAll(littleSquares[i], littleSquaresText[i]);
            nGridCol = i % 6;
            
            gridPane_CategoryBoxes.add(squaresNText[i], nGridCol, nGridRow);
            if (nGridCol == 5) { nGridRow++; }  
        }    
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        pieChartCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        pieChartCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        pieChartAnchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .addAll(txtTitle1, txtTitle2, gridPane_CategoryBoxes, pieChartCanvas, 
                               yAxis, xAxis, radiiSlider, sliderControlText);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doTheGraph() {    
        double daXPosition, angleSizeInProps, yPosition, yPositionUp,
                yPositionDown, daPreXPosition, pixRadius,
                radiusPerWidth, radiusPerHeight, xxCosineHalfAngle, yySineHalfAngle,
                xxHalfCircleForPercent, yyHalfCircleForPercent;
        
        //Positions the up/down for circles
        yPositionUp = yAxis.getDisplayPosition(0.35);
        yPositionDown = yAxis.getDisplayPosition(0.35);
        
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = gridPane_CategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.95 * tempHeight);
        

        AnchorPane.setTopAnchor(txtTitle2, 0.03 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.2 * tempHeight);
   
        AnchorPane.setTopAnchor(gridPane_CategoryBoxes, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(gridPane_CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(gridPane_CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(gridPane_CategoryBoxes, 0.90 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.05 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(pieChartCanvas, 0.20 * tempHeight);
        AnchorPane.setLeftAnchor(pieChartCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(pieChartCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(pieChartCanvas, 0.10 * tempHeight);
       
        AnchorPane.setTopAnchor(radiiSlider, 0.95 * tempHeight);
        AnchorPane.setLeftAnchor(radiiSlider, 0.75 * tempWidth);
        AnchorPane.setRightAnchor(radiiSlider, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(radiiSlider, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(sliderControlText, 0.96 * tempHeight);
        AnchorPane.setLeftAnchor(sliderControlText, 0.50 * tempWidth);
        AnchorPane.setRightAnchor(sliderControlText, 0.74 * tempWidth);
        AnchorPane.setBottomAnchor(sliderControlText, 0.01 * tempHeight);
        
        pieChartGC.clearRect(0, 0 , pieChartCanvas.getWidth(), pieChartCanvas.getHeight());
        pieChartGC.setLineWidth(2.5);
        pieChartGC.setFill(Color.BLACK);
        pieChartGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));

        double fudgeFactorForWidth = 1.15;
        double fudgeFactorForHeight = 1.25;
        
        if (nColsCat % 2 == 0) {
            radiusPerWidth = fudgeFactorForWidth * pieChartCanvas.getWidth() /(tempWidth * (nColsCat * onePlusOver));
        } else {
            radiusPerWidth = fudgeFactorForWidth * pieChartCanvas.getWidth() / (tempWidth * (nColsCat * (nColsCat - 1.) * onePlusOver));
        }
        
        radiusPerHeight = fudgeFactorForHeight * pieChartCanvas.getHeight() / (2. * tempHeight * (1. + sqrt2));
        
        // This will be modified via the slider
        initialCircleRadii = Math.min(radiusPerWidth, radiusPerHeight);
        radii = radiiSlider.getValue() * initialCircleRadii;
        pixRadius = radii * tempWidth;
        
        for (int col = 0; col < nColsCat; col++) {
            if (col % 2 == 0) {
                yPosition = yPositionUp;
            }
            else {
                yPosition = yPositionDown;  
            }
      
            daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);

            for (int row = 0; row < nCatValues; row++) {
                pieChartGC.setFill(graphColors[row]); 
                angleSizeInProps = columnProps[row];

                double xx = daXPosition - pixRadius;    //  x, y is 
                double yy = yPosition - pixRadius;      //  Center of circle
                double ww = 2. * pixRadius;
                double hh = 2. * pixRadius;

                double startAngleAt = startingAngles[row];
                double arcAngleSize = arcLengths[row];

                int xxInt = (int)Math.round(xx);
                int yyInt = (int)Math.round(yy);
                int wwInt = (int)Math.round(ww);
                int hhInt = (int)Math.round(hh);
                int intSA = (int)Math.round(startAngleAt);
                int intAA = (int)Math.round(arcAngleSize);
                pieChartGC.fillArc(xxInt, yyInt, wwInt, hhInt, intSA, intAA, ArcType.ROUND);                          
            } 
        } 
        
        for (int col = 0; col < nColsCat; col++) {        
            if (col % 2 == 0) {
                yPosition = yPositionUp;
            }
            else {
                yPosition = yPositionDown;  
            }
            
            daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);
            for (int row = 0; row < nCatValues; row++) {        
                double centerX = daXPosition;
                double centerY = yPosition;
                double halfAngle  = middlesOfAngles[row];

                double startAngle = 360.0 * startingAngles[row];

                xxCosineHalfAngle = Math.cos(halfAngle * piOver180);
                yySineHalfAngle = Math.sin(halfAngle* piOver180);

                double xxHalfCircleEdge = centerX + 1.01 * pixRadius * xxCosineHalfAngle;
                double yyHalfCircleEdge = centerY - 1.01 * pixRadius * yySineHalfAngle;

                double xxHalfCircleOtherEnd = centerX + 1.15 * pixRadius * xxCosineHalfAngle;
                double yyHalfCircleOtherEnd = centerY - 1.15 * pixRadius * yySineHalfAngle;

                pieChartGC.setStroke(graphColors[row]);
                pieChartGC.setLineWidth(1.6);
                pieChartGC.strokeLine(xxHalfCircleEdge, yyHalfCircleEdge, 
                           xxHalfCircleOtherEnd, yyHalfCircleOtherEnd);

                // Print the percent
                if (xxCosineHalfAngle >= 0) {
                    xxHalfCircleForPercent = centerX + 1.17 * pixRadius * xxCosineHalfAngle;
                } else {
                    xxHalfCircleForPercent = centerX + 1.17 * pixRadius * xxCosineHalfAngle - 35;  
                }

                if (yySineHalfAngle >= 0) {
                    yyHalfCircleForPercent = centerY - 1.17 * pixRadius * yySineHalfAngle;
                } else { 
                    yyHalfCircleForPercent = centerY - 1.17 * pixRadius * yySineHalfAngle + 10;
                }            

                double percent = 100.0 * columnProps[row];
                
                String strPercent = String.format("%4.1f", percent) + "%";
                pieChartGC.setFill(Color.BLACK);
                pieChartGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 12));
                pieChartGC.fillText(strPercent, xxHalfCircleForPercent, yyHalfCircleForPercent);     
            } 
        } 

        pieChartGC.setStroke(Color.BLACK);
        doTheXAxis();

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

    private void doTheXAxis() {
        double topLabelXValue, topLabelYValue, preTopLabelXValue;
        pieChartGC.setFill(Color.BLACK);
        for (int col = 0; col < nColsCat; col++) {  
            String stringToPrint = topLabels[col];
            int lenString = topLabels[col].length();
            if (lenString > 12) {
                stringToPrint = StringUtilities.getleftMostNChars(stringToPrint, 12);
            }
            double pre_pre = ((double)col + 0.5)/((double)nColsCat + 1.0);
            preTopLabelXValue = pre_pre - .047 - 0.0025 * lenString;  //  Center string
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.16);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.10);
            }
        }
    }  
    
    private void constructPieInfo() {
            strLeftVariable = univCat_Model.getTheVariable();  // Var description
            nRowsCat = univCat_Model.getNCategories();         // Number of categories in data
            nCatValues = nRowsCat;
            leftLabels = new String[nRowsCat];                  // Category labels
            leftLabels = univCat_Model.getCategoriesAsStrings();
            columnProps = univCat_Model.getObservedProps();  // Proportions
            cumRowProps = new double[nRowsCat];
            startingAngles = new double[nRowsCat];
            arcLengths = new double[nRowsCat];
            middlesOfAngles = new double[nRowsCat];
            
        for (int kth = 1; kth < nRowsCat; kth++) {            
            for (int ith = 0; ith < nRowsCat - kth ; ith++) {                
                if (columnProps[ith] > columnProps[ith + 1]) {                    
                   tempDouble = columnProps[ith];
                   columnProps[ith] = columnProps[ith + 1];
                   columnProps[ith + 1] = tempDouble;

                   tempStr = leftLabels[ith];
                   leftLabels[ith] = leftLabels[ith + 1];
                   leftLabels[ith + 1] = tempStr;
                }
            }
        }
        
        cumRowProps[0] = columnProps[0];
        for (int ithCum = 1; ithCum < nRowsCat; ithCum++) {
            cumRowProps[ithCum] = cumRowProps[ithCum - 1] + columnProps[ithCum];
        }
        
        startingAngles[0] = 0.0;
        arcLengths[0] = 360.0 * cumRowProps[0];
        middlesOfAngles[0] = 360.0 * 0.5 * columnProps[0];
        
        for (int ithArc = 1; ithArc < nRowsCat; ithArc++) {
            startingAngles[ithArc] = 360.0 * cumRowProps[ithArc - 1];
            arcLengths[ithArc] = 360.0 * columnProps[ithArc];
            middlesOfAngles[ithArc] = startingAngles[ithArc] + 0.5 * arcLengths[ithArc];
        }  
    }  
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent)  { }
    }; 

    public Pane getTheContainingPane() { return theContainingPane; }
}

