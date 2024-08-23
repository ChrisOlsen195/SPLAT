/**************************************************
 *               Epi_PieChartView                 *
 *                   08/21/24                     *
 *                     12:00                      *
 *************************************************/
package epidemiologyProcedures;

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

public class Epi_PieChartView 
{
    // POJOs
    
    int nRowsCat, nColsCat, nLittleSquares;
    
    double initHoriz, initVert, initWidth, initHeight, sqrt2, halfRoot2, 
           onePlusOver, initialCircleRadii, radii, piOver180, text1Width,     
           relRad;
    
    double[] columnProps;
    double[][] cumRowProps;

    String graphsCSS, returnStatus;
    String[] leftLabels, topLabels;
    
    //  My classes
    Change_Radius_Dialog chRadius_Dialog;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis, yAxis;
    Epi_Model epi_Model;

    //  FX objects
    AnchorPane anchorPane_PieChart;
    Button btn_RadiusReset;
    Canvas canvas_X2PieChart;
    Color[] graphColors;
    
    GraphicsContext gc; // Required for drawing on the Canvas
    GridPane gridPane_PieChartView;
    HBox[] squaresNText;
    Pane containingPane;
    Rectangle[] littleSquares;
    Text txtTitle1, txtTitle2;
    Text[] txtLittleSquares;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public Epi_PieChartView(Epi_Model bivCat_Model, 
            Epi_Dashboard epi_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight)  {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        this.epi_Model = bivCat_Model;
        chRadius_Dialog = new Change_Radius_Dialog(this);
        chRadius_Dialog.setRelativeRadius(1.0);
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        containingPane = new Pane();
        
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
                relRad = chRadius_Dialog.getRelativeRadius();
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
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() {        
        //for (int lab = 0; lab < nColsCat; lab++) {
            topLabels = epi_Model.getOutcomeValues();
        //}

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
        canvas_X2PieChart = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        gc = canvas_X2PieChart.getGraphicsContext2D();
        gridPane_PieChartView = new GridPane();
        gridPane_PieChartView.setAlignment(Pos.CENTER);
        gridPane_PieChartView.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        txtLittleSquares = new Text[nLittleSquares];
        squaresNText = new HBox[nLittleSquares];

        int nGridRow = 0; 
        int nGridCol = 0;
        
        for (int i = 0; i < nRowsCat; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);
            txtLittleSquares[i] = new Text(0, 0, leftLabels[i]);
            txtLittleSquares[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,12));
            txtLittleSquares[i].setFill(graphColors[i]);
            squaresNText[i] = new HBox(12);
            squaresNText[i].setFillHeight(false);
            squaresNText[i].setAlignment(Pos.CENTER);
            squaresNText[i].setStyle("-fx-padding: 2;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-insets: 5;" +
                                     "-fx-border-radius: 5;");
            //sliderControlText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));
            squaresNText[i].getChildren().addAll(littleSquares[i], txtLittleSquares[i]);
            nGridCol = i % 6;
            
            gridPane_PieChartView.add(squaresNText[i], nGridCol, nGridRow);
            
            if (nGridCol == 5) { nGridRow++; }  
        }
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        canvas_X2PieChart.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvas_X2PieChart.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane_PieChart = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .addAll(txtTitle1, gridPane_PieChartView, canvas_X2PieChart, 
                                  yAxis, xAxis, btn_RadiusReset);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doTheGraph() {    
        double daXPosition, angleSizeInProps, yPosition, yPositionUp,
                yPositionDown, daPreXPosition, pixRadius,
                radiusPerWidth, radiusPerHeight, xxCosineHalfAngle, yySineHalfAngle,
                xxHalfCircleForPercent, yyHalfCircleForPercent;

        yPositionUp = yAxis.getDisplayPosition(0.65);
        yPositionDown = yAxis.getDisplayPosition(0.20);
        
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = gridPane_PieChartView.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.95 * tempHeight);
        
        AnchorPane.setTopAnchor(gridPane_PieChartView, 0.05 * tempHeight);
        AnchorPane.setLeftAnchor(gridPane_PieChartView, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(gridPane_PieChartView, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(gridPane_PieChartView, 0.90 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.05 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(canvas_X2PieChart, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(canvas_X2PieChart, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(canvas_X2PieChart, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(canvas_X2PieChart, 0.10 * tempHeight);
       
        AnchorPane.setTopAnchor(btn_RadiusReset, 0.95 * tempHeight);
        AnchorPane.setLeftAnchor(btn_RadiusReset, 0.75 * tempWidth);
        AnchorPane.setRightAnchor(btn_RadiusReset, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(btn_RadiusReset, 0.0 * tempHeight);
        
        gc.clearRect(0, 0 , canvas_X2PieChart.getWidth(), canvas_X2PieChart.getHeight());
        gc.setLineWidth(2.5);
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));

        double fudgeFactorForWidth = 1.15;
        double fudgeFactorForHeight = 1.25;
        
        if (nColsCat % 2 == 0) {
            radiusPerWidth = fudgeFactorForWidth * canvas_X2PieChart.getWidth() /(tempWidth * (nColsCat * onePlusOver));
        } else {
            radiusPerWidth = fudgeFactorForWidth * canvas_X2PieChart.getWidth() / (tempWidth * (nColsCat * (nColsCat - 1.) * onePlusOver));
        }
        
        radiusPerHeight = fudgeFactorForHeight * canvas_X2PieChart.getHeight() / (2. * tempHeight * (1. + sqrt2));
        
        initialCircleRadii = Math.min(radiusPerWidth, radiusPerHeight);
        radii = chRadius_Dialog.getRelativeRadius() * initialCircleRadii;
        pixRadius = radii * tempWidth;
        
        for (int col = 0; col < nColsCat; col++) {
            if (col % 2 == 0) { yPosition = yPositionUp; }
            else { yPosition = yPositionDown; }
      
            daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);

            for (int row = 0; row < nRowsCat; row++) {
                gc.setFill(graphColors[row]); 

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
                gc.fillArc(xxInt, yyInt, wwInt, hhInt, intSA, intAA, ArcType.ROUND);                          
            } 
        } 
        
        for (int col = 0; col < nColsCat; col++) {            
            if (col % 2 == 0) { yPosition = yPositionUp; }
            else { yPosition = yPositionDown; }
            
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

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.6);
                gc.strokeLine(centerX, centerY, xxCircleEdge, yyCircleEdge);
                gc.strokeLine(xxHalfCircleEdge, yyHalfCircleEdge, 
                           xxHalfCircleOtherEnd, yyHalfCircleOtherEnd);

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
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 12));
                gc.fillText(strPercent, xxHalfCircleForPercent, yyHalfCircleForPercent);     
            }   //  End row
        }   //  End col

        gc.setStroke(Color.BLACK);
        doTheXAxis();
        
        anchorPane_PieChart.requestFocus();
        anchorPane_PieChart.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = anchorPane_PieChart.snapshot(new SnapshotParameters(), null);
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
        gc.setFill(Color.BLACK);
        
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
            gc.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }  
    
    private void constructPieInfo() {
        nRowsCat = 2;
        nColsCat = 2;
        cumRowProps = new double[nRowsCat + 1][nColsCat + 1];
        cumRowProps = epi_Model.getCellCumProps();        
        columnProps = new double[nColsCat];
        columnProps = epi_Model.getColumnProportions();
        
        leftLabels = new String[nRowsCat];
        leftLabels = epi_Model.getExposureValues();
    }  
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent)  { /* No op */ }
    }; 

    public void setRelRad(double theNewRad) { relRad = theNewRad; }
    public Pane getTheContainingPane() { return containingPane; }
}
