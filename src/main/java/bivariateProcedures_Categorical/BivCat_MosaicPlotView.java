/**************************************************
 *              BivCat_MosaicPlotView             *
 *                    10/15/24                    *
 *                      18:00                     *
 *************************************************/
package bivariateProcedures_Categorical;

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

public class BivCat_MosaicPlotView {
    
    // POJOs
    //boolean dragging;
    
    double initHoriz, initVert, initWidth, initHeight, text1Width, text2Width;
    
    double[] cumColProps, cumMarginalRowProps, columnProps;
    double[][] cumProps;
    
    int nRowsCat, nColsCat, nLittleSquares;
    
    String strTopVariable, strLeftVariable, graphsCSS;
    String[] leftLabels, topLabels;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;   
    JustAnAxis xAxis, yAxis;
    BivCat_Model bivCat_Model;    
    
    // POJOs / FX 
    AnchorPane anchorPane;
    Canvas mosaicCanvas;
    Color[] graphColors;  
    GraphicsContext mosaicGC;     
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

    public BivCat_MosaicPlotView(BivCat_Model association_Model, 
                      BivCat_Dashboard association_Dashboard,
                      double placeHoriz, double placeVert,
                      double withThisWidth, double withThisHeight) {
        //System.out.println("\n76 BivCat_MosaicPlotView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.bivCat_Model = association_Model;
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        containingPane = new Pane(); 
    }
    
    public void completeTheDeal() {
        constructMosaicInfo();
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
        containingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);        
    }
    
    private void initializeGraphParams() {        
        for (int lab = 0; lab < nColsCat; lab++) {
            topLabels = bivCat_Model.getTopLabels();
        }

        xAxis = new JustAnAxis(0.00, 1.01);
        xAxis.setSide(Side.BOTTOM);

        xAxis.setLabel(strTopVariable);
        xAxis.setVisible(true);    //  Used only for positioning other stuff
        xAxis.forceLowScaleEndToBe(0.00);
        xAxis.forceHighScaleEndToBe(1.01);

        yAxis = new JustAnAxis(0.0, 1.05);
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.forceHighScaleEndToBe(1.05);
        yAxis.setSide(Side.LEFT);

        yAxis.setVisible(true);    //  Used only for positioning other stuff  
    }
    
    private void setUpUI() {
        mosaicCanvas = new Canvas(0.95 * initWidth, 0.95 * initHeight);
        mosaicGC = mosaicCanvas.getGraphicsContext2D();   
        
        gridPane_SegBar = new GridPane();
        gridPane_SegBar.setAlignment(Pos.CENTER);
        gridPane_SegBar.setStyle("-fx-padding: 2;"+
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
            
            gridPane_SegBar.add(squaresNText[i], nGridCol, nGridRow);
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
                          .addAll(txtTitle1, txtTitle2, gridPane_SegBar, mosaicCanvas, yAxis, xAxis);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doThePlot() {    
        double x1, y1, x2, y2, height, width;
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
        
        AnchorPane.setTopAnchor(gridPane_SegBar, 0.10 * tempHeight);
        AnchorPane.setLeftAnchor(gridPane_SegBar, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(gridPane_SegBar, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(gridPane_SegBar, 0.85 * tempHeight);     
        
        AnchorPane.setTopAnchor(mosaicCanvas, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(mosaicCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(mosaicCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(mosaicCanvas, 0.30 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.70 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.10 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.30 * tempHeight);
        
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
            }   //  End row
        }   //  End col
        
        mosaicGC.setStroke(Color.BLACK);
        mosaicGC.setFill(Color.BLACK);
        mosaicGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));
        mosaicGC.setLineWidth(2);
        
        doTheMarginalPlot();
        doTheXAxis();
        
        anchorPane.requestFocus();
        anchorPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = anchorPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));
        
    }   //  End doThePlot
    
    private void doTheMarginalPlot() {
        double mPlotx1, mPloty1, mPlotx2, mPloty2, mPlotHeight, mPlotWidth;
        mPlotx1 = xAxis.getDisplayPosition(1.05);   
        mPlotx2 = xAxis.getDisplayPosition(1.15);
        mPlotWidth = mPlotx2 - mPlotx1;
        
        for (int row = 0; row < nRowsCat; row++) {
            mosaicGC.setFill(graphColors[row]); 
            mPloty1 = yAxis.getDisplayPosition(cumMarginalRowProps[row]);                
            mPloty2 = yAxis.getDisplayPosition(cumMarginalRowProps[row + 1]);  

            //  Labels for cumulative proportions
            //double ylabel_x = mPlotx2 + 2;
            //double labelHeight = (mPloty1 + mPloty2)/2.0;
            //mosaicGC.fillText(leftLabels[row], ylabel_x, labelHeight + 2);
            
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
            preTopLabelXValue = (x1 + x2) / 2. - 0.02 - 0.015 * lenString;  //  Hack to center string 
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.20);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.14);
            }
            mosaicGC.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) { }
    }; 
    
    private void constructMosaicInfo() {  
        nRowsCat = bivCat_Model.getNumberOfRows();
        nColsCat = bivCat_Model.getNumberOfColumns();
        columnProps = new double[nColsCat];
        columnProps = bivCat_Model.getColumnProportions();      
        
        cumMarginalRowProps = new double[nRowsCat + 1];
        cumMarginalRowProps = bivCat_Model.getCumMarginalRowProps();  
        cumColProps = new double[nColsCat + 1];
        cumColProps = bivCat_Model.getCumColProps();
  
        cumProps = new double[nRowsCat + 1][nColsCat + 1];
        cumProps = bivCat_Model.getCellCumProps();
        
        topLabels = new String[nColsCat];
        leftLabels = new String[nRowsCat];
        strTopVariable = bivCat_Model.getTopVariable();
        strLeftVariable = bivCat_Model.getLeftVariable();
        leftLabels = bivCat_Model.getLeftLabels();
    }  
    
    public Pane getTheContainingPane() { return containingPane; }
}