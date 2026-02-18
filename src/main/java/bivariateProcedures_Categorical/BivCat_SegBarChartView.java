/**************************************************
 *              BivCat_SegBarChartView            *
 *                    03/20/25                    *
 *                     09:00                      *
 *************************************************/
package bivariateProcedures_Categorical;

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
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import utilityClasses.*;

public class BivCat_SegBarChartView  {
    // POJOs

    double width, halfWidth, initHoriz, initVert, initWidth,
           initHeight, text1Width, text2Width;
    
    double[] columnProps;
    double[][] cumRowProps;
    
    int nRowsCat, nColsCat, nLittleSquares;

    String strTopVariable, strLeftVariable, graphsCSS;
    String[] strLeftLabels, strTopLabels;
    
    //  My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis, yAxis;
    BivCat_Model bivCat_Model;
    
    //  POJOs / FX
    AnchorPane theContainingPane;
    Canvas segBarCanvas;

    Color[] graphColors;
    
    GraphicsContext segBarGC; // Required for drawing on the Canvas
    GridPane gridPane_SegBar;
    HBox[] squaresNText;
    Pane containingPane;
    Rectangle[] littleSquares;
    Text txtTitle1, txtTitle2;
    Text[] littleSquaresText;    
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public BivCat_SegBarChartView(BivCat_Model bivCat_Model, 
            BivCat_Dashboard association_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) 
    {
        //System.out.println("\n78 BivCat_SegBarChartView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        this.bivCat_Model = bivCat_Model;
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        containingPane = new Pane();
    }
    
    public void completeTheDeal() {
        constructSegBarInfo(); 
        txtTitle1 = new Text("Segmented Bar Chart"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text(strLeftVariable + " vs. " + strTopVariable); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 8));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() {        
        for (int lab = 0; lab < nColsCat; lab++) {
            strTopLabels = bivCat_Model.getStrTopLabels();
        }

        xAxis = new JustAnAxis(-0.15, 1.25);
        xAxis.setSide(Side.BOTTOM);

        xAxis.setLabel("This is xAxis");
        xAxis.setVisible(false);    //  Used only for positioning other stuff
        xAxis.forceLowScaleEndToBe(-0.15);
        xAxis.forceHighScaleEndToBe(1.25);

        yAxis = new JustAnAxis(0.0, 1.05);
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.forceHighScaleEndToBe(1.05);
        yAxis.setSide(Side.LEFT);

        yAxis.setVisible(false);    //  Used only for positioning other stuff
    }
    
    private void setUpUI() {
        segBarCanvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        segBarGC = segBarCanvas.getGraphicsContext2D();
        gridPane_SegBar = new GridPane();
        gridPane_SegBar.setAlignment(Pos.CENTER);
        gridPane_SegBar.setStyle("-fx-padding: 2;"+
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
            littleSquaresText[i] = new Text(0, 0, strLeftLabels[i]);
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
            gridPane_SegBar.add(squaresNText[i], nGridCol, nGridRow);
            
            if (nGridCol == 5) { nGridRow++; }  
        } 
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        segBarCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        segBarCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        theContainingPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .addAll(txtTitle1, txtTitle2, gridPane_SegBar, segBarCanvas, yAxis, xAxis);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doThePlot() {    
        double daXPosition, y1, y2, height;
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = gridPane_SegBar.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.00 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.90 * tempHeight);

        AnchorPane.setTopAnchor(txtTitle2, 0.05 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.85 * tempHeight);
        
        AnchorPane.setTopAnchor(gridPane_SegBar, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(gridPane_SegBar, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(gridPane_SegBar, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(gridPane_SegBar, 0.80 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.85 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(segBarCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(segBarCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(segBarCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(segBarCanvas, 0.2 * tempHeight);
        
        segBarGC.clearRect(0, 0 , segBarCanvas.getWidth(), segBarCanvas.getHeight());
        segBarGC.setLineWidth(2.5);
        segBarGC.setFill(Color.BLACK);
        segBarGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));
        
        for (int col = 0; col < nColsCat; col++) {            
            double daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);
            width = 40.;
            
            for (int row = 0; row < nRowsCat; row++) {
                segBarGC.setFill(graphColors[row]); 
                double preY1 = cumRowProps[row][col] / columnProps[col];
                double preY2 = cumRowProps[row + 1][col] / columnProps[col];

                y1 = yAxis.getDisplayPosition(preY1);                
                y2 = yAxis.getDisplayPosition(preY2);  
                height = (y2 - y1);
                
                segBarGC.fillRect(daXPosition - halfWidth, y1, width, height);   
                segBarGC.setStroke(Color.BLACK);
                segBarGC.strokeRect(daXPosition - halfWidth, y1, width, height);
            }   //  End row
        }   //  End col

        segBarGC.setStroke(Color.BLACK);
        doTheYAxis();
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
        segBarGC.setFill(Color.BLACK);
        
        for (int col = 0; col < nColsCat; col++) {  
            String stringToPrint = strTopLabels[col];
            int lenString = stringToPrint.length();
            
            if (lenString > 8) {
                stringToPrint = StringUtilities.getleftMostNChars(stringToPrint, 8);
            }
            
            stringToPrint = StringUtilities.centerTextInString(stringToPrint, 8);
            //  .01 is a hack hack to center the labels under the bars
            double pre_pre = ((double)col + 0.5)/((double)nColsCat + 1.0);
            preTopLabelXValue = pre_pre + .01 - 0.0025 * lenString ;  //  Hack to center string
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.14);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.08);
            }
            segBarGC.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }    
    private void doTheYAxis() {
        segBarGC.setStroke(Color.BLACK);
        segBarGC.setFill(Color.BLACK);
        segBarGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));
        segBarGC.setLineWidth(2);
        
        double xText = xAxis.getDisplayPosition(0.0) - 45.;
        double xText35 = xText + 38;
        double xText50 = xText + 46;

        String prop025 = "0.25";
        double yText025 = yAxis.getDisplayPosition(0.25) + 2.5;
        segBarGC.fillText(prop025, xText, yText025 + 2);
        segBarGC.strokeLine(xText35, yText025, xText50, yText025);
        
        String prop050 = "0.50";
        double yText050 = yAxis.getDisplayPosition(0.50) + 2.5;
        segBarGC.fillText(prop050, xText, yText050 + 2);
        segBarGC.strokeLine(xText35, yText050, xText50, yText050);
        
        String prop075 = "0.75";
        double yText075 = yAxis.getDisplayPosition(0.75) + 2.5;
        segBarGC.fillText(prop075, xText, yText075 + 2);
        segBarGC.strokeLine(xText35, yText075, xText50, yText075);
        
        String prop100 = "1.00";
        double yText100 = yAxis.getDisplayPosition(1.00) + 2.5;
        segBarGC.fillText(prop100, xText, yText100 + 2);
        segBarGC.strokeLine(xText35, yText100, xText50, yText100);
        
        //mosaicGC.strokeLine(xText50, yText000, xText50, yText100);
        segBarGC.setStroke(Color.BLACK);
        double leftXBaseLine = xAxis.getDisplayPosition(0.01);
        double rightXBaseLine = xAxis.getDisplayPosition(0.99);
        double bottomYBaseLine = yAxis.getDisplayPosition(0.0);
        double topYBaseLine = yAxis.getDisplayPosition(0.99);

        segBarGC.strokeLine(leftXBaseLine, bottomYBaseLine, 
                            rightXBaseLine, bottomYBaseLine - 0.5);        
        segBarGC.strokeLine(leftXBaseLine - 2., bottomYBaseLine, 
                            leftXBaseLine - 2., topYBaseLine + 1.);   
    }
    
    private void constructSegBarInfo()
    {
        nRowsCat = bivCat_Model.getNumberOfRows();
        nColsCat = bivCat_Model.getNumberOfColumns();

        cumRowProps = new double[nRowsCat + 1][nColsCat + 1];
        cumRowProps = bivCat_Model.getCellCumProps();
        
        columnProps = new double[nColsCat];
        columnProps = bivCat_Model.getColumnProportions();
        
        strLeftLabels = new String[nRowsCat];
        
        strTopVariable = bivCat_Model.getTopVariable();
        strLeftVariable = bivCat_Model.getLeftVariable();
        strLeftLabels = bivCat_Model.getStrLeftLabels();
    }  
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent)  { }
    }; 

    public Pane getTheContainingPane() { return containingPane; }
}
