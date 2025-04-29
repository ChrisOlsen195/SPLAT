/**************************************************
 *              X2Assoc_PieChartView              *
 *                    01/15/25                    *
 *                     12:00                      *
 *************************************************/
package chiSquare_Assoc;

import dialogs.Change_Radius_Dialog;
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
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.ArcType;
import utilityClasses.*;

public class X2Assoc_PieChartView 
{
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nRowsCat, nColsCat, nLittleSquares;
    
    double initHoriz, initVert, initWidth, initHeight, sqrt2, halfRoot2, 
           onePlusOver, initialCircleRadii, radii, piOver180, text1Width; 
    
    double relRad;  //relRad is set elsewhere
    
    double[] columnProps;
    double[][] cumRowProps;

    String graphsCSS, returnStatus;
    String[] leftLabels, topLabels;
    
    //  My classes
    Change_Radius_Dialog chRadius_Dialog;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis, yAxis;
    X2Assoc_Model x2Assoc_Model;

    //  FX objects
    AnchorPane pieChartAnchorPane;
    Button btn_RadiusReset;
    Canvas x2PieChartCanvas;
    Color[] graphColors;
    
    GraphicsContext pieChartGC; // Required for drawing on the Canvas
    GridPane pieChartCategoryBoxes;
    HBox[] squaresNText;
    Pane theContainingPane;
    Rectangle[] littleSquares;
    Text txtTitle1, txtTitle2;
    Text[] littleSquaresText;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public X2Assoc_PieChartView(X2Assoc_Model x2Assoc_Model, 
            X2Assoc_Dashboard x2Assoc_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) 
    {
        if (printTheStuff == true) {
            System.out.println("89 *** X2Assoc_PieChartView, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        this.x2Assoc_Model = x2Assoc_Model;
        chRadius_Dialog = new Change_Radius_Dialog(this);
        chRadius_Dialog.setRelativeRadius(1.0);
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        
        txtTitle1 = new Text("Pie Chart"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text("Title2Text"); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 8));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        
        radii = 1.0;

        btn_RadiusReset = new Button("Change radius");
        btn_RadiusReset.setOnAction(e -> {
            chRadius_Dialog = new Change_Radius_Dialog(this);
            chRadius_Dialog.showAndWait();
            returnStatus = chRadius_Dialog.getReturnStatus();
            
            if (returnStatus.equals("OK")) {
                chRadius_Dialog.close();
                doTheGraph();
            }
        });       

        sqrt2 = Math.sqrt(2.0);        
        halfRoot2 = sqrt2 / 2.0;
        onePlusOver = (1.0 + halfRoot2);
        piOver180 = Math.PI / 180.0;
    }
    
    public void completeTheDeal() {
        if (printTheStuff == true) {
            System.out.println("130 --- X2Assoc_PieChartView, completeTheDeal()");
        }
        constructPieInfo(); 
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        
        dragableAnchorPane.heightProperty()
                          .addListener(ov-> {
                                doTheGraph();
                          });
        dragableAnchorPane.widthProperty()
                          .addListener(ov-> {
                              doTheGraph();});
        theContainingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() {
        
        for (int lab = 0; lab < nColsCat; lab++) {
            topLabels = x2Assoc_Model.getTopLabels();
        }

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
        x2PieChartCanvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        pieChartGC = x2PieChartCanvas.getGraphicsContext2D();
        pieChartCategoryBoxes = new GridPane();
        pieChartCategoryBoxes.setAlignment(Pos.CENTER);
        pieChartCategoryBoxes.setStyle("-fx-padding: 2;"+
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
        
        for (int i = 0; i < nRowsCat; i++) {
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

            squaresNText[i].getChildren().addAll(littleSquares[i], littleSquaresText[i]);
            nGridCol = i % 6;
            
            pieChartCategoryBoxes.add(squaresNText[i], nGridCol, nGridRow);
            
            if (nGridCol == 5) { nGridRow++; }  
        }
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        x2PieChartCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        x2PieChartCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        pieChartAnchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .addAll(txtTitle1, pieChartCategoryBoxes, x2PieChartCanvas, 
                                  yAxis, xAxis, btn_RadiusReset);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doTheGraph() {    
        double daXPosition, angleSizeInProps, yPosition, yPositionUp,
                yPositionDown, daPreXPosition, pixRadius,
                radiusPerWidth, radiusPerHeight, xxCosineHalfAngle, yySineHalfAngle,
                xxHalfCircleForPercent, yyHalfCircleForPercent;

        //Positions the up/down for circles
        yPositionUp = yAxis.getDisplayPosition(0.65);
        yPositionDown = yAxis.getDisplayPosition(0.20);
        
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        //text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = pieChartCategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.95 * tempHeight);
        
        AnchorPane.setTopAnchor(pieChartCategoryBoxes, 0.05 * tempHeight);
        AnchorPane.setLeftAnchor(pieChartCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(pieChartCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(pieChartCategoryBoxes, 0.90 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.05 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(x2PieChartCanvas, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(x2PieChartCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(x2PieChartCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(x2PieChartCanvas, 0.10 * tempHeight);
       
        AnchorPane.setTopAnchor(btn_RadiusReset, 0.95 * tempHeight);
        AnchorPane.setLeftAnchor(btn_RadiusReset, 0.75 * tempWidth);
        AnchorPane.setRightAnchor(btn_RadiusReset, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(btn_RadiusReset, 0.0 * tempHeight);
        
        pieChartGC.clearRect(0, 0 , x2PieChartCanvas.getWidth(), x2PieChartCanvas.getHeight());
        pieChartGC.setLineWidth(2.5);
        pieChartGC.setFill(Color.BLACK);
        pieChartGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));

        double fudgeFactorForWidth = 1.15;
        double fudgeFactorForHeight = 1.25;
        
        if (nColsCat % 2 == 0) {
            radiusPerWidth = fudgeFactorForWidth * x2PieChartCanvas.getWidth() /(tempWidth * (nColsCat * onePlusOver));
        } else {
            radiusPerWidth = fudgeFactorForWidth * x2PieChartCanvas.getWidth() / (tempWidth * (nColsCat * (nColsCat - 1.) * onePlusOver));
        }
        
        radiusPerHeight = fudgeFactorForHeight * x2PieChartCanvas.getHeight() / (2. * tempHeight * (1. + sqrt2));
        
        // This will be modified via the slider
        initialCircleRadii = Math.min(radiusPerWidth, radiusPerHeight);
        radii = chRadius_Dialog.getRelativeRadius() * initialCircleRadii;
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

            for (int row = 0; row < nRowsCat; row++) {
                pieChartGC.setFill(graphColors[row]); 

                double preY1 = cumRowProps[row][col] / columnProps[col];
                double preY2 = cumRowProps[row + 1][col] / columnProps[col];

                angleSizeInProps = preY2 - preY1;

                double xx = daXPosition - pixRadius;
                double yy = yPosition - pixRadius;
                double ww = 2. * pixRadius;
                double hh = 2. * pixRadius;

                double startAngle = 360.0 * preY1;
                double arcAngle = 360.0 * angleSizeInProps;

                int xxInt = (int)Math.round(xx);
                int yyInt = (int)Math.round(yy);
                int wwInt = (int)Math.round(ww);
                int hhInt = (int)Math.round(hh);
                int intSA = (int)Math.round(startAngle);
                int intAA = (int)Math.round(arcAngle);
                pieChartGC.fillArc(xxInt, yyInt, wwInt, hhInt, intSA, intAA, ArcType.ROUND);                          
            }   //  End row
        }   //  End col
        
        for (int col = 0; col < nColsCat; col++) {
        
            if (col % 2 == 0) {
                yPosition = yPositionUp;
            }
            else {
                yPosition = yPositionDown;  
            }
            
            daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);

            for (int row = 0; row < nRowsCat; row++) {
                double preY1 = cumRowProps[row][col] / columnProps[col];
                double preY2 = cumRowProps[row + 1][col] / columnProps[col];          

                double centerX = daXPosition;
                double centerY = yPosition;

                double halfAngleInProps = 0.5 * (preY1  + preY2);
                double halfAngle = 360 * halfAngleInProps;

                double startAngle = 360.0 * preY1;

                double xxCosineStartAngle = Math.cos(startAngle * piOver180);
                double yySineStartAngle = Math.sin(startAngle* piOver180);

                xxCosineHalfAngle = Math.cos(halfAngle * piOver180);
                yySineHalfAngle = Math.sin(halfAngle* piOver180);

                double xxCircleEdge = centerX + 1.0 * pixRadius * xxCosineStartAngle;
                double yyCircleEdge = centerY - 1.0 * pixRadius * yySineStartAngle;

                double xxHalfCircleEdge = centerX + 1.01 * pixRadius * xxCosineHalfAngle;
                double yyHalfCircleEdge = centerY - 1.01 * pixRadius * yySineHalfAngle;

                double xxHalfCircleOtherEnd = centerX + 1.15 * pixRadius * xxCosineHalfAngle;
                double yyHalfCircleOtherEnd = centerY - 1.15 * pixRadius * yySineHalfAngle;

                pieChartGC.setStroke(Color.BLACK);
                pieChartGC.setLineWidth(1.6);
                pieChartGC.strokeLine(centerX, centerY, xxCircleEdge, yyCircleEdge);
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

                double percent = 100.0 * (preY1 - preY2);
                String strPercent = String.format("%4.1f", percent) + "%";
                pieChartGC.setFill(Color.BLACK);
                pieChartGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 12));
                pieChartGC.fillText(strPercent, xxHalfCircleForPercent, yyHalfCircleForPercent);     
            }   //  End row
        }   //  End col

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
        
    }   //  End doThePlot

    private void doTheXAxis() {
        double topLabelXValue, topLabelYValue, preTopLabelXValue;
        pieChartGC.setFill(Color.BLACK);
        
        for (int col = 0; col < nColsCat; col++) {  
            String stringToPrint = topLabels[col];
            int lenString = topLabels[col].length();
            if (lenString > 12) {
                stringToPrint = StringUtilities.getleftMostNChars(stringToPrint, 12);
            }
            stringToPrint = StringUtilities.centerTextInString(stringToPrint, 12);
            double pre_pre = ((double)col + 0.5)/((double)nColsCat + 1.0);
            preTopLabelXValue = pre_pre - .047 - 0.0025 * lenString;  //  Center string
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.16);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.10);
            }
            pieChartGC.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }  
    
    private void constructPieInfo() {
        nRowsCat = x2Assoc_Model.getNumberOfRows();
        nColsCat = x2Assoc_Model.getNumberOfColumns();

        cumRowProps = new double[nRowsCat + 1][nColsCat + 1];
        cumRowProps = x2Assoc_Model.getCellCumProps();
        
        columnProps = new double[nColsCat];
        columnProps = x2Assoc_Model.getColumnProportions();

        leftLabels = new String[nRowsCat];
        leftLabels = x2Assoc_Model.getLeftLabels();
    }  
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent)  { }
    }; 

    public void setRelRad(double theNewRad) { relRad = theNewRad; }
    public Pane getTheContainingPane() { return theContainingPane; }
}
