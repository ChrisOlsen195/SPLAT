/**************************************************
 *             Two_Prop_MosaicPlotView            *
 *                    01/16/25                    *
 *                     18:00                      *
 *************************************************/
package the_z_procedures;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import utilityClasses.*;

public class Two_Prop_Inf_MosaicPlotView {
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int TWO, THREE, nRowsCat, nColsCat, nLittleSquares;
    
    double initHoriz, initVert, initWidth, initHeight, text1Width, text2Width;

    double[] cumRowProps, cumColProps, cumMarginalRowProps, columnProps;
    double[][] cumProps;
    
    String strTopVariable, strLeftVariable, graphsCSS;
    String[] leftLabels, topLabels;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;   
    JustAnAxis xAxis, yAxis;  
    
    // POJOs / FX 
    AnchorPane anchorPane;
    Canvas mosaicCanvas;
    // Colors_and_CSS_Strings colorsAndLabels;
    Color[] graphColors;  
    GraphicsContext mosaicGC;     
    GridPane mosaicCategoryBoxes;    
    HBox[] squaresNText;   
    Pane theContainingPane; 
    Rectangle[] littleSquares;
    Text txtTitle1, txtTitle2;
    Text[] littleSquaresText;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;

    public Two_Prop_Inf_MosaicPlotView(TwoProp_Inf_Model twoProp_Inf_Model, 
                      TwoProp_Inf_Dashboard twoProp_Inf_Dashboard,
                      double placeHoriz, double placeVert,
                      double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("78 *** Two_Prop_MosaicPlotView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane(); 
        TWO = 2; THREE = 3;
    }
    
    public void completeTheDeal() {
        //constructMosaicInfo();
        txtTitle1 = new Text("Mosaic Plot"); 
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
        theContainingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);        
    }
    
    private void initializeGraphParams() {
        xAxis = new JustAnAxis(-0.15, 1.25);
        xAxis.setSide(Side.BOTTOM);

        xAxis.setLabel("This is xAxis");
        xAxis.setVisible(false);
        xAxis.forceLowScaleEndToBe(-0.15);
        xAxis.forceHighScaleEndToBe(1.25);

        yAxis = new JustAnAxis(0.0, 1.05);
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.forceHighScaleEndToBe(1.05);
        yAxis.setSide(Side.LEFT);

        yAxis.setVisible(false);    //  Used only for positioning other stuff  
    }
    
    private void setUpUI() {
        mosaicCanvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        mosaicGC = mosaicCanvas.getGraphicsContext2D();   
        
        mosaicCategoryBoxes = new GridPane();
        mosaicCategoryBoxes.setAlignment(Pos.CENTER);
        mosaicCategoryBoxes.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        
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
            
            mosaicCategoryBoxes.add(squaresNText[i], nGridCol, nGridRow);
            
            if (nGridCol == 5) { nGridRow++; }  
        }        
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        mosaicCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        mosaicCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        
        anchorPane = dragableAnchorPane.getTheAP();
        
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .addAll(txtTitle1, txtTitle2, mosaicCategoryBoxes, mosaicCanvas, yAxis, xAxis);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doThePlot() {    
        double x1, y1, x2, y2, height, width;
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = mosaicCategoryBoxes.getWidth();
        
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
        
        AnchorPane.setTopAnchor(mosaicCategoryBoxes, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(mosaicCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(mosaicCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(mosaicCategoryBoxes, 0.80 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.85 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(mosaicCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(mosaicCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(mosaicCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(mosaicCanvas, 0.2 * tempHeight);
        
        mosaicGC.clearRect(0, 0 , mosaicCanvas.getWidth(), mosaicCanvas.getHeight());
        mosaicGC.setLineWidth(3);
        mosaicGC.setFill(Color.BLACK);
        mosaicGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 12));   
        
        for (int col = 0; col < nColsCat; col++) {
            x1 = xAxis.getDisplayPosition(cumColProps[col]);   
            x2 = xAxis.getDisplayPosition(cumColProps[col + 1]);  
            width = x2 - x1;
            
            for (int row = 0; row < nRowsCat; row++) {
                mosaicGC.setFill(graphColors[row]);                 
                double preY1 = cumProps[row][col] / columnProps[col];
                double preY2 = cumProps[row + 1][col] / columnProps[col];

                y1 = yAxis.getDisplayPosition(preY1);                
                y2 = yAxis.getDisplayPosition(preY2);  
                height = (y2 - y1);

                mosaicGC.fillRect(x1, y1, width, height);   
                mosaicGC.setStroke(Color.WHITE);
                mosaicGC.strokeRect(x1, y1, width, height);
            } 
        } 
        
        mosaicGC.setStroke(Color.BLACK);
        mosaicGC.setFill(Color.BLACK);
        mosaicGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));
        mosaicGC.setLineWidth(2);
        
        double xText = xAxis.getDisplayPosition(0.0) - 45.;
        double xText35 = xText + 38;
        double xText50 = xText + 46;

        String prop025 = "0.25";
        double yText025 = yAxis.getDisplayPosition(0.25) + 2.5;
        mosaicGC.fillText(prop025, xText, yText025 + 2);
        mosaicGC.strokeLine(xText35, yText025, xText50, yText025);
        
        String prop050 = "0.50";
        double yText050 = yAxis.getDisplayPosition(0.50) + 2.5;
        mosaicGC.fillText(prop050, xText, yText050 + 2);
        mosaicGC.strokeLine(xText35, yText050, xText50, yText050);
        
        String prop075 = "0.75";
        double yText075 = yAxis.getDisplayPosition(0.75) + 2.5;
        mosaicGC.fillText(prop075, xText, yText075 + 2);
        mosaicGC.strokeLine(xText35, yText075, xText50, yText075);
        
        String prop100 = "1.00";
        double yText100 = yAxis.getDisplayPosition(1.00) + 2.5;
        mosaicGC.fillText(prop100, xText, yText100 + 2);
        mosaicGC.strokeLine(xText35, yText100, xText50, yText100);
        
        mosaicGC.setStroke(Color.BLACK);
        double leftXBaseLine = xAxis.getDisplayPosition(0.01);
        double rightXBaseLine = xAxis.getDisplayPosition(0.99);
        double bottomYBaseLine = yAxis.getDisplayPosition(0.0);
        double topYBaseLine = yAxis.getDisplayPosition(0.99);

        mosaicGC.strokeLine(leftXBaseLine, bottomYBaseLine - 1., 
                            rightXBaseLine, bottomYBaseLine - 1.);        
        mosaicGC.strokeLine(leftXBaseLine - 2., bottomYBaseLine, 
                            leftXBaseLine - 2., topYBaseLine + 1.);        
        doTheMarginalPlot();
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
    
    private void doTheMarginalPlot() {
        double mPlotx1, mPloty1, mPlotx2, mPloty2, mPlotHeight, mPlotWidth;
        //  Marginal rows
        mPlotx1 = xAxis.getDisplayPosition(1.05);   
        mPlotx2 = xAxis.getDisplayPosition(1.15);
       
        mPlotWidth = mPlotx2 - mPlotx1;
        
        for (int row = 0; row < nRowsCat; row++) {
            mosaicGC.setFill(graphColors[row]); 
            mPloty1 = yAxis.getDisplayPosition(cumMarginalRowProps[row]);                
            mPloty2 = yAxis.getDisplayPosition(cumMarginalRowProps[row + 1]);  

            //  Labels for cumulative proportions
            double ylabel_x = mPlotx2 + 2;
            double labelHeight = (mPloty1 + mPloty2)/2.0;
            mosaicGC.fillText(leftLabels[row], ylabel_x, labelHeight + 2);
            
            mPlotHeight = mPloty2 - mPloty1;

            mosaicGC.fillRect(mPlotx1, mPloty1, mPlotWidth, mPlotHeight);   
            mosaicGC.setStroke(Color.WHITE);
            mosaicGC.strokeRect(mPlotx1, mPloty1, mPlotWidth, mPlotHeight);
        }   //  End row               
    }
    
    private void doTheXAxis() {
        double x1, x2, topLabelXValue, topLabelYValue, preTopLabelXValue;
        mosaicGC.setFill(Color.BLACK);
        
        for (int col = 0; col < nColsCat; col++) {            
            x1 = cumColProps[col];   
            x2 = cumColProps[col + 1];  
            String stringToPrint = topLabels[col];
            int lenString = stringToPrint.length();
            
            if (lenString > 8) {
                stringToPrint = StringUtilities.getleftMostNChars(stringToPrint, 8);
            }
            
            stringToPrint = StringUtilities.centerTextInString(stringToPrint, 8);
            //  .015 is a hack hack to center the labels under the bars
            preTopLabelXValue = (x1 + x2) / 2. - 0.02 - 0.0025 * lenString;  //  Hack to center string
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.14);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.08);
            }
            mosaicGC.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent)  { }
    }; 
    
    private void constructMosaicInfo()
    {
        /*
        xMin = -.10; xMax = 1.40; yMin = -0.15; yMax = 1.0;
        xRange = xMax - xMin; yRange = yMax - yMin;  
        //nRowsCat = twoProp_Inf_Model.getNumberOfRows();
        //nColsCat = twoProp_Inf_Model.getNumberOfColumns();
        nRowsCat = TWO;
        nColsCat = TWO;
        cumRowProps = new double[THREE];
        cumRowProps = twoProp_Inf_Model.getCumRowProps(); 
        columnProps = new double[TWO];
        columnProps = twoProp_Inf_Model.getColumnProportions();      
        
        cumMarginalRowProps = new double[THREE];
        cumMarginalRowProps = twoProp_Inf_Model.getCumMarginalRowProps();  
        cumColProps = new double[THREE];
        cumColProps = twoProp_Inf_Model.getCumColProps();
  
        cumProps = new double[THREE][THREE];
        cumProps = twoProp_Inf_Model.getCellCumProps();
        
        topLabels = new String[TWO];
        leftLabels = new String[TWO];
        strTopVariable = twoProp_Inf_Model.getTopVariable();
        strLeftVariable = twoProp_Inf_Model.getLeftVariable();
        leftLabels = twoProp_Inf_Model.getLeftLabels();
        */
    }      
    public Pane getTheContainingPane() { return theContainingPane; }
}
