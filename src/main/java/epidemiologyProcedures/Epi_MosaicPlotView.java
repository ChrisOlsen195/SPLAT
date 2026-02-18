/**************************************************
 *               Epi_MosaicPlotView               *
 *                    12/17/25                    *
 *                      00:00                     *
 *************************************************/
package epidemiologyProcedures; 

import genericClasses.DragableAnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import bivariateProcedures_Categorical.*;
import utilityClasses.*;
import genericClasses.*;

public class Epi_MosaicPlotView {
    
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    double initHoriz, initVert, initWidth, initHeight;
    double pxVertScaleWidth, pxVertScaleHeight, pxHorizScaleWidth;
    double[] cumulativeColProps, cumulativeMarginalRowProps, columnProps;
    
    
    double pxMPVHeight, // Height of MosaicPlotView
           pxMPVWidth,  // Width of MosaicPlotView
           pxViewHeight, // Height of view in panel
           pxViewWidth,  // Width of view in panel
           pxHorizontalScaleHeight, // height of horizontal scale pane
           pxMarginalWidth, // Width of marginal proportions pane
           pxTitleHeight, // Height of Title pane
           pxTop, pxBottom,  // Top and bottom of view
           pxLeft, pxRight, //  Left and Right of view
           pxMosaicPaneLeft, pxMosaicPaneRight, // Left and Right of Mosaic pane
           pxMosaicPaneTop, pxMosaicPaneBottom, // Top and Bottom of Mosaic pane
           pxMosaicPaneWidth, pxMosaicPaneHeight,
            
           m_prop2px_RightToLeft, b_prop2px_RightToLeft,        
           m_prop2px_BottomToTop, b_prop2px_BottomToTop,
           m_px2prop_RightToLeft, b_px2prop_RightToLeft,
           m_px2Prop_BottomToTop, b_pxToProp_BottomToTop;

    double[][] cumProps;
    
    final int nRowsCat, nColsCat, nLittleSquares;
    
    String strTopVariable, strLeftVariable, graphsCSS;
    String[] strLeftLabels, strTopLabels;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;   
    Epi_Model epi_Model;   
    
    // POJOs / FX 
    Pane ex_AnchorPane;
    Color[] graphColors;      
    Font yLabelFont;
    GridPane mosaicCategoryBoxes;    
    HBox[] squaresNText;   
    Pane containingPane, mosaicPane, hScalePane, vScalePane, root;
    Rectangle[] littleSquares, marginalRectangles;
    Rectangle[][] mosaicRectangles;
    Text txtTitle1, txtTitle2;
    Text[] littleSquaresText, txtMarginalRows, txtTopLabels;
    
    HorizontalMosaicScale horizontalMosaicScale;
    VerticalMosaicScale verticalMosaicScale;

    public Epi_MosaicPlotView(Epi_Model x2Assoc_Model, 
                      Epi_Dashboard association_Dashboard,
                      double placeHoriz, double placeVert,
                      double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("*** 86 Epi_MosaicPlotView, Constructing");
        }
        pxMPVHeight = withThisHeight;
        pxMPVWidth = withThisWidth;
        nRowsCat = 2; nColsCat = 2; nLittleSquares = 2;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.epi_Model = x2Assoc_Model;
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        containingPane = new Pane();  
        yLabelFont = Font.font("NewTimesRoman", FontWeight.BOLD, 16);
    }
    
    public void completeTheDeal() {
        if (printTheStuff) {
            System.out.println("*** 102 Epi_MosaicPlotView, completeTheDeal()");
        }
        strTopLabels = new String[nColsCat];
        strLeftLabels = new String[nRowsCat];
        strTopVariable = epi_Model.getTopVariable();
        strLeftVariable = epi_Model.getLeftVariable();
        strLeftLabels = epi_Model.getOutcomeValues();
        
        txtTopLabels = new Text[nColsCat];
        strTopLabels = epi_Model.getExposureValues();
        for (int ithLabel = 0; ithLabel < nColsCat; ithLabel++) {
            txtTopLabels[ithLabel] = new Text(strTopLabels[ithLabel]);
        }
        
        setUpViewParams();
        txtTitle1 = new Text("Epidemiology Risk Plot"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text(strLeftVariable + " vs. " + strTopVariable); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));        
        txtTitle2.getStyleClass().add("titleLabel");  

        double pxMiddle = gimmeA_PixelX(0.5);
 
        double pxTitle_1_HalfWidth = 0.5 * txtTitle1.getLayoutBounds().getWidth();
        double pxTitle_2_HalfWidth = 0.5 * txtTitle2.getLayoutBounds().getWidth();
        txtTitle1.setLayoutX(0.5 * pxViewWidth - pxTitle_1_HalfWidth) ;
        txtTitle1.setLayoutY(gimmeA_PixelY(1.15)); 
        txtTitle2.setLayoutX(0.5 * pxViewWidth - pxTitle_2_HalfWidth);
        txtTitle2.setLayoutY(gimmeA_PixelY(1.10));  
        columnProps = new double[nColsCat];
        columnProps = epi_Model.getColumnProportions();      
        
        cumulativeMarginalRowProps = new double[nRowsCat + 1];
        cumulativeMarginalRowProps = epi_Model.getCumMarginalRowProps();  
        cumulativeColProps = new double[nColsCat + 1];
        cumulativeColProps = epi_Model.getCumColProps();
  
        cumProps = new double[nRowsCat + 1][nColsCat + 1];
        cumProps = epi_Model.getCellCumProps();
        
        mosaicRectangles = new Rectangle[nColsCat][nRowsCat];
        marginalRectangles = new Rectangle[nRowsCat];
        txtMarginalRows = new Text[nRowsCat];
        setUpLittleSquares();
        setUpAnchorPane();

        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});

        containingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler); 
    }
    
    private void setUpViewParams() {
        if (printTheStuff) {
            System.out.println("*** 158 Epi_MosaicPlotView, setUpViewParams()");
        }        
        pxVertScaleWidth = 90;
        pxViewHeight = 0.95 * pxMPVHeight;
        pxViewWidth = 0.95 * pxMPVWidth;
        pxHorizontalScaleHeight = 150.0;
        pxMarginalWidth = 75.0;
        pxTitleHeight = 30.0;
        pxTitleHeight = 40.0;
        pxTop = 0.075 * pxMPVHeight;
        pxBottom = 0.975 * pxMPVHeight;
        pxLeft = 0.025 * pxViewWidth; 
        pxRight = 0.975 * pxViewWidth;
        pxMosaicPaneLeft = 0.025 * pxMPVWidth + pxVertScaleWidth;
        pxMosaicPaneRight = pxRight - pxMarginalWidth;
        pxMosaicPaneTop = pxTop + pxTitleHeight;
        pxMosaicPaneBottom = pxViewHeight - pxHorizontalScaleHeight; 
        pxMosaicPaneWidth = pxMosaicPaneRight - pxMosaicPaneLeft;
        pxMosaicPaneHeight = pxMosaicPaneBottom - pxMosaicPaneTop; 
        
        pxHorizScaleWidth = pxMosaicPaneWidth;
        horizontalMosaicScale = new HorizontalMosaicScale(this, pxHorizScaleWidth, strTopVariable, strTopLabels);
        hScalePane = horizontalMosaicScale.getHorizontalPane();
        
        pxVertScaleHeight =  pxMosaicPaneHeight;
        verticalMosaicScale = new VerticalMosaicScale(this, pxVertScaleHeight, pxVertScaleWidth, strLeftVariable);
        vScalePane = verticalMosaicScale.getVerticalPane();
        
        // Transformations for props and pixels for Mosaic Plot proper
        m_prop2px_RightToLeft = pxMosaicPaneRight - pxMosaicPaneLeft;
        b_prop2px_RightToLeft = pxMosaicPaneLeft; 
        
        m_px2prop_RightToLeft = 1.0 / (pxMosaicPaneRight - pxMosaicPaneLeft);
        b_px2prop_RightToLeft = -m_px2prop_RightToLeft;
        
        m_prop2px_BottomToTop = pxMosaicPaneTop - pxMosaicPaneBottom;
        b_prop2px_BottomToTop = pxMosaicPaneBottom;
    }
    
    private void setUpLittleSquares() {
        if (printTheStuff) {
            System.out.println("*** 199 Epi_MosaicPlotView, setUpLittleSquares()");
        }  
        mosaicPane = new Pane();
        mosaicPane.setPrefSize(0.95 * initWidth, 0.8 * initHeight);
        mosaicCategoryBoxes = new GridPane();
        mosaicCategoryBoxes.setAlignment(Pos.CENTER);
        mosaicCategoryBoxes.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        littleSquares = new Rectangle[nLittleSquares];
        littleSquaresText = new Text[nLittleSquares];
        squaresNText = new HBox[nLittleSquares];
        
        int nGridRow = 0; 
        int nGridCol = 0;
        
        mosaicCategoryBoxes.setLayoutX(200);
        mosaicCategoryBoxes.setLayoutY(50);
        for (int ithRow = 0; ithRow < nRowsCat; ithRow++) {
            littleSquares[ithRow] = new Rectangle(10, 10, 10, 10);
            littleSquares[ithRow].setStroke(graphColors[ithRow]);
            littleSquares[ithRow].setFill(graphColors[ithRow]);
            littleSquaresText[ithRow] = new Text(0, 0, strLeftLabels[ithRow]);
            littleSquaresText[ithRow].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,12));
            littleSquaresText[ithRow].setFill(graphColors[ithRow]);
            squaresNText[ithRow] = new HBox(12);
            squaresNText[ithRow].setFillHeight(false);
            squaresNText[ithRow].setAlignment(Pos.CENTER);
            squaresNText[ithRow].setStyle("-fx-padding: 2;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-insets: 5;" +
                                     "-fx-border-radius: 5;");
            squaresNText[ithRow].getChildren().addAll(littleSquares[ithRow], littleSquaresText[ithRow]);
            nGridCol = ithRow % 6;
            
            mosaicCategoryBoxes.add(squaresNText[ithRow], nGridCol, nGridRow);
            if (nGridCol == 5) { nGridRow++; }  
        }  
    }
    
    private void setUpAnchorPane() {
        if (printTheStuff) {
            System.out.println("*** 244 Epi_MosaicPlotView, setUpAnchorPane()");
        }
        dragableAnchorPane = new DragableAnchorPane();
        ex_AnchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);  
        
        vScalePane.setLayoutX(gimmeA_PixelX(0.0) - pxVertScaleWidth);
        vScalePane.setLayoutY(gimmeA_PixelY(1.0));
  
        hScalePane.setLayoutX(gimmeA_PixelX(0.0));
        hScalePane.setLayoutY(gimmeA_PixelY(0.0));

        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .add(vScalePane);
        
        dragableAnchorPane.getTheAP()
                .getChildren()
                .add(hScalePane);

        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .addAll(txtTitle1, txtTitle2, mosaicCategoryBoxes, mosaicPane);

        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doThePlot() { 
        if (printTheStuff) {
            System.out.println("*** 274 Epi_MosaicPlotView, doThePlot()");
        }
        double x1, y1, x2, y2;

        for (int ithCol = 0; ithCol < nColsCat; ithCol++) {
            x1 = cumulativeColProps[ithCol];
            x2 = cumulativeColProps[ithCol + 1];
 
            for (int jthRow = 0; jthRow < nRowsCat; jthRow++) {
                y1 = cumProps[jthRow][ithCol] / columnProps[ithCol];
                y2 = cumProps[jthRow + 1][ithCol] / columnProps[ithCol];
                Point_2D px_x1y1 = gimmeA_pxPoint(x1, y1);
                Point_2D px_x2y2 = gimmeA_pxPoint(x2, y2);
                
                mosaicRectangles[ithCol][jthRow] = gimmeARect(px_x1y1.getFirstValue(), 
                                                              px_x1y1.getSecondValue(), 
                                                              px_x2y2.getFirstValue(), 
                                                              px_x2y2.getSecondValue(), 
                                                              graphColors[jthRow]);
                
                mosaicPane.getChildren().add(mosaicRectangles[ithCol][jthRow]);
                
            }
        }

        doTheMarginalPlot();
        
    }
    
    private void doTheMarginalPlot() {
        if (printTheStuff) {
            System.out.println("*** 305 Epi_MosaicPlotView, doTheMarginalPlot()");
        }
        double marginalCumulativeProp_1, marginalCumulativeProp_2;

        marginalRectangles = new Rectangle[nRowsCat];
        for (int ithRow = 0; ithRow < nRowsCat; ithRow++) {
            marginalCumulativeProp_1 = cumulativeMarginalRowProps[ithRow];
            marginalCumulativeProp_2 = cumulativeMarginalRowProps[ithRow + 1]; 

            txtMarginalRows[ithRow] = new Text(strLeftLabels[ithRow]);  
            txtMarginalRows[ithRow].setFont(yLabelFont);
            txtMarginalRows[ithRow].setFill(graphColors[ithRow]);
            double propBandMiddle = 0.5 * (marginalCumulativeProp_1 + marginalCumulativeProp_2);
            
            
            Point_2D daPoint = gimmeA_pxPoint(1.10 /*dummy*/, propBandMiddle);
            
            txtMarginalRows[ithRow].setX(daPoint.getFirstValue());
            txtMarginalRows[ithRow].setY(daPoint.getSecondValue());
            mosaicPane.getChildren().add(txtMarginalRows[ithRow]);       

            marginalRectangles[ithRow] = gimmeARect(gimmeA_PixelX(1.05), 
                                         //marginalCumulativeProp_1, 
                                         gimmeA_PixelY(marginalCumulativeProp_1),
                                         gimmeA_PixelX(1.10), 
                                         //marginalCumulativeProp_2, 
                                         gimmeA_PixelY(marginalCumulativeProp_2),
                                         graphColors[ithRow]);
            
            mosaicPane.getChildren().add(marginalRectangles[ithRow]);
        }   //  End row  
    }
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) { }
    }; 
    
    
    public Rectangle gimmeARect(double pxLowX, double pxLowY, 
                                double pxHighX, double pxHighY,
                                Color daColor) {
        Rectangle rect;
        
        double upperLeftX = Math.min(pxHighX, pxLowX);
        double upperLeftY = Math.min(pxHighY, pxLowY);
        
        double pxRectWidth = Math.abs(pxHighX - pxLowX);
        double pxRectHeight = Math.abs(pxHighY - pxLowY);

        rect = new Rectangle(upperLeftX, upperLeftY, pxRectWidth, pxRectHeight);
        Color rectColor = daColor;
        rect.setFill(rectColor);
        rect.setStroke(Color.WHITE);
        rect.setStrokeWidth(2.0);
        return rect; 
    }
    
    public double gimmeA_PixelX(double propX) {
        double pixelX = m_prop2px_RightToLeft * propX + b_prop2px_RightToLeft;  
        return pixelX;
    }
    
    public double gimmeA_PixelY(double propY) {
        double pixelY = m_prop2px_BottomToTop * propY + b_prop2px_BottomToTop;   
        return pixelY;
    }
    
    /***********************************************************************
     *               The GimmeA_Props are not checked yet                  *
     **********************************************************************/
    private double gimmeA_PropX(double pixelX) {
        double propX = m_px2prop_RightToLeft * pixelX + b_px2prop_RightToLeft;  
        return propX;
    }
    
    private double gimmeA_propY(double pixelY) {
        double propY = m_px2Prop_BottomToTop * pixelY + b_pxToProp_BottomToTop;     
        return propY;
    }
    
    public Point_2D gimmeA_PropPoint(double pixelX, double pixelY) {
        double propX = gimmeA_PropX(pixelX);
        double propY = gimmeA_propY(pixelY);
        Point_2D propPoint = new Point_2D(propX, propY);
        return propPoint;
    } 
    
     public Point_2D gimmeA_pxPoint(double propX, double propY) {
        double pixelX = gimmeA_PixelX(propX);
        double pixelY = gimmeA_PixelY(propY); 
        Point_2D pixelPoint = new Point_2D(pixelX , pixelY);      
        return pixelPoint;
    } 
    
    public String get_strTopVariable() { return strTopVariable; }
    public String get_strLeftVariable() { return strLeftVariable; }
    
    public double get_pxVertScaleWidth() {return pxVertScaleWidth; }
    public double get_pxMosaicPaneLeft() { return pxMosaicPaneLeft; }
    
    public Pane getTheContainingPane() { return containingPane; }
    public Epi_Model getEpi_Model() { return epi_Model; }
    public Epi_MosaicPlotView getX2Assoc_MosaicPlotView() { return this; }
}