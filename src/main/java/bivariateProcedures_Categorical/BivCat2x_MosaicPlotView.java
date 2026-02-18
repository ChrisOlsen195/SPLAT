/**************************************************
 *             BivCat2x_MosaicPlotView            *
 *                    12/10/25                    *
 *                      18:00                     *
 *************************************************/
package bivariateProcedures_Categorical;

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
import utilityClasses.*;
import genericClasses.*;
import the_z_procedures.TwoProp_Inf_Dashboard;

public class BivCat2x_MosaicPlotView {
    
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    double initHoriz, initVert, initWidth, initHeight, text1Width, text2Width;
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
    
    int nRowsCat, nColsCat, nLittleSquares;
    
    String strTopVariable, strLeftVariable, graphsCSS;
    String[] strLeftLabels, strTopLabels;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;   
    BivCat2x_Model bivCat2x_Model;
    HorizontalMosaicScale horizontalMosaicScale;
    VerticalMosaicScale verticalMosaicScale;   
    
    // POJOs / FX 
    Pane ex_AnchorPane;
    Color[] graphColors;      
    Font yLabelFont;
    GridPane mosaicCategoryBoxes;    
    HBox[] squaresNText;   
    Pane theContainingPane, mosaicPane, hScalePane, vScalePane, root;
    Rectangle[] littleSquares, marginalRectangles;
    Rectangle[][] mosaicRectangles;
    Text txtTitle1, txtTitle2;
    Text[] littleSquaresText, txtMarginalRows, txtTopLabels, txtLeftLabels;

    public BivCat2x_MosaicPlotView(BivCat2x_Model bivCat2x_Model, 
                      TwoProp_Inf_Dashboard twoProp_Inf_Dashboard,
                      double placeHoriz, double placeVert,
                      double withThisWidth, double withThisHeight) {
        if (printTheStuff) {
            System.out.println("84 *** BivCat2x_MosaicPlotView, Constructing");
        }
        this.bivCat2x_Model = bivCat2x_Model;
        pxMPVHeight = withThisHeight;
        pxMPVWidth = withThisWidth;
        nRowsCat = 2;
        nColsCat = 2;
        strTopVariable = bivCat2x_Model.getTopVariable();
        strLeftVariable = bivCat2x_Model.getLeftVariable();
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.bivCat2x_Model = bivCat2x_Model;
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();  
        yLabelFont = Font.font("NewTimesRoman", FontWeight.BOLD, 16);
    }
    
    private void makeItHappen() {
        
    }
    
    public void completeTheDeal() {
        if (printTheStuff) {
            System.out.println("*** 104 BivCat2x_MosaicPlotView, completeTheDeal()");
        }
        
        strLeftLabels = new String[nColsCat];
        strLeftLabels[0] = "Fail";
        strLeftLabels[1] = "Succ";
        txtLeftLabels = new Text[nColsCat];
        txtLeftLabels[0] = new Text(strLeftLabels[0]);
        txtLeftLabels[1] = new Text(strLeftLabels[1]);
        
        strTopLabels = new String[nColsCat];
        strTopLabels[0] = strTopVariable;
        strTopLabels[1] = strLeftVariable;
        txtTopLabels = new Text[nRowsCat];
        txtTopLabels[0] = new Text(strTopLabels[0]);
        txtTopLabels[1] = new Text(strTopLabels[1]);
        
        setUpViewParams();
        txtTitle1 = new Text("Difference in Proportions"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));        
        txtTitle1.getStyleClass().add("titleLabel");  
        String strTitle2 = strTopVariable + " and " + strLeftVariable;
        txtTitle2 = new Text(strTitle2); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));        
        txtTitle2.getStyleClass().add("titleLabel"); 
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();  
        double pxTitle_1_HalfWidth = 0.5 * txtTitle1.getLayoutBounds().getWidth();
        double pxTitle_2_HalfWidth = 0.5 * txtTitle2.getLayoutBounds().getWidth();
        txtTitle1.setLayoutX(0.5 * pxViewWidth - pxTitle_1_HalfWidth) ;
        txtTitle1.setLayoutY(gimmeA_PixelY(1.15)); 
        txtTitle2.setLayoutX(0.5 * pxViewWidth - pxTitle_2_HalfWidth);
        txtTitle2.setLayoutY(gimmeA_PixelY(1.10));  

        columnProps = new double[nColsCat];
        columnProps = bivCat2x_Model.getColumnProportions();      
        
        cumulativeMarginalRowProps = new double[nRowsCat + 1];
        cumulativeMarginalRowProps = bivCat2x_Model.getCumMarginalRowProps();  
        cumulativeColProps = new double[nColsCat + 1];
        cumulativeColProps = bivCat2x_Model.getCumulativeColProps();
  
        cumProps = new double[nRowsCat + 1][nColsCat + 1];
        cumProps = bivCat2x_Model.getCumulativeProportions();
        
        mosaicRectangles = new Rectangle[nColsCat][nRowsCat];
        marginalRectangles = new Rectangle[nRowsCat];
        txtMarginalRows = new Text[nRowsCat];
        setUpLittleSquares();
        setUpAnchorPane();

        dragableAnchorPane.heightProperty().addListener(ov-> {doTheGraph();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doTheGraph();});

        theContainingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler); 
    }
    
    private void setUpViewParams() {
        if (printTheStuff) {
            System.out.println("*** 164 BivCat2x_MosaicPlotView, setUpViewParams()");
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
        String varOfInterest = bivCat2x_Model.getTheVariable();
        horizontalMosaicScale = new HorizontalMosaicScale(this, pxHorizScaleWidth, varOfInterest, strTopLabels);
        hScalePane = horizontalMosaicScale.getHorizontalPane();
        
        pxVertScaleHeight =  pxMosaicPaneHeight;
        verticalMosaicScale = new VerticalMosaicScale(this, pxVertScaleHeight, pxVertScaleWidth, "   ");
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
            //System.out.println("*** 206 BivCat2x_MosaicPlotView, setUpLittleSquares()");
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
        nLittleSquares = graphColors.length;
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
            //System.out.println("*** 252 BivCat2x_MosaicPlotView, setUpAnchorPane()");
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
    
    public void doTheGraph() { 
        if (printTheStuff) {
            //System.out.println("*** 282 BivCat2x_MosaicPlotView, doTheGraph()");
        }
        double x1, y1, x2, y2;
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        
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
            //System.out.println("*** 315 BivCat2x_MosaicPlotView, doTheMarginalPlot()");
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
            double daYBias = 0.0;
            txtMarginalRows[ithRow].setY(daPoint.getSecondValue() - daYBias);
            mosaicPane.getChildren().add(txtMarginalRows[ithRow]);       

            marginalRectangles[ithRow] = gimmeARect(gimmeA_PixelX(1.05), 
                                         //marginalCumulativeProp_1, 
                                         gimmeA_PixelY(marginalCumulativeProp_1),
                                         gimmeA_PixelX(1.10), 
                                         //marginalCumulativeProp_2, 
                                         gimmeA_PixelY(marginalCumulativeProp_2),
                                         graphColors[ithRow]);
            
            mosaicPane.getChildren().add(marginalRectangles[ithRow]);
        } 
    }
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) { }
    }; 
    
    
    public Rectangle gimmeARect(double pxLowX, double pxLowY, 
                                double pxHighX, double pxHighY,
                                Color daColor) {
        //if (printTheStuff) {
            //System.out.println("--- 368 BivCat2x_MosaicPlotView, gimmeARect, pxPoint1 = " + pxLowX + " / " + pxLowY);
            //System.out.println("--- 369 BivCat2x_MosaicPlotView, gimmeARect pxPoint2 = " + pxHighX + " / " + pxHighY);
        //}

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
    
    public Pane getTheContainingPane() { return theContainingPane; }
    public BivCat2x_Model getBivCat_Model() { return bivCat2x_Model; }
    public BivCat2x_MosaicPlotView getBivCat2x_MosaicPlotView() { return this; }
}