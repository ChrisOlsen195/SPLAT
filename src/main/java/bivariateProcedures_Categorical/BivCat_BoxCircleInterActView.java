/**************************************************
 *           BivCat_BoxCircleInterActView         *
 *                  03/22/25                      *
 *                    12:00                       *
 *************************************************/
package bivariateProcedures_Categorical;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.FXCollections;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class BivCat_BoxCircleInterActView extends BivCat_Views_Super { 
        // POJO's
    
        int nTopLabels, nLeftLabels;
        String waldoFile = "";
        //String waldoFile = "BivCat_BoxCircleInterActView";
        
        String graphsCSS, strFactorA, strFactorB;

    BivCat_BoxCircleInterActView(BivCat_Model bivCat_Model, 
            BivCat_Dashboard bivCat_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super();
        this.bivCat_Model = bivCat_Model;
        dm = bivCat_Model.getDataManager();
        dm.whereIsWaldo(42, waldoFile, " *** Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        
        categoryLabels = FXCollections.observableArrayList();
        
        strTopLabels = bivCat_Model.getStrTopLabels();
        strLeftLabels = bivCat_Model.getStrLeftLabels();
        
        nTopLabels = strTopLabels.length;
        nLeftLabels = strLeftLabels.length;
        
        /*
        dm.whereIsWaldo(62, waldoFile, " --- Constructing, nTopLabels = " + nTopLabels);
        dm.whereIsWaldo(63, waldoFile, " --- Constructing, nLeftLabels = " + nLeftLabels);
        
        for (int ithLeftLabel = 0; ithLeftLabel < nLeftLabels; ithLeftLabel++){
            System.out.println("68 BivCat_BoxCircle, ithLabel = " + strLeftLabels[ithLeftLabel]);
        }
        
        for (int ithTopLabel = 0; ithTopLabel < nTopLabels; ithTopLabel++){
            System.out.println("72 BivCat_BoxCircle,, ithLabel = " + strTopLabels[ithTopLabel]);
        }
        */
        
        categoryLabels.addAll(strTopLabels);
        
        strFactorA = bivCat_Model.getTopVariable();
        strFactorB = bivCat_Model.getLeftVariable();
        
        //System.out.println("80 strFactorA = " + strFactorA);
        //System.out.println("81 strFactorB = " + strFactorB);
        
        strTitle1 = strFactorA + " & " + strFactorB;
        strTitle2 = " ";
        
        txtTitle1 = new Text(strTitle1);        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text(strTitle2);         
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();  
        
        dm.whereIsWaldo(79, waldoFile, "Constructing");
        
    }
    
    public void completeTheDeal() {
        initializeGraphParams();
        setUpUI();
        initialize();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        theContainingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() { 
        canvas_BivCat = new Canvas(625, 625);
        gc = canvas_BivCat.getGraphicsContext2D(); 
        
        initial_yMin = 0.0;
        initial_yMax = bivCat_Model.getMaxProportion();
        initial_yRange = initial_yMax - initial_yMin;
        
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setBounds(initial_yMin, initial_yMax);
        yAxis.forceLowScaleEndToBe(0.0);
        yMin = initial_yMin; yMax = initial_yMax; yRange = initial_yRange;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        categoryAxis_X = new CategoryAxis(categoryLabels);
        categoryAxis_X.setSide(Side.BOTTOM); 
        categoryAxis_X.setAutoRanging(true);
        categoryAxis_X.setLabel(bivCat_Model.getTopVariable());
        categoryAxis_X.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        categoryAxis_X.setPrefWidth(40);   
        categoryAxis_X.setLayoutX(500); categoryAxis_X.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(bivCat_Model.getLeftVariable());
        
        setHandlers();
    }
    
    private void setUpUI() {        
        hBox_BivCat_CategoryBoxes = new HBox(nLittleSquares);
        hBox_BivCat_CategoryBoxes.setAlignment(Pos.CENTER);
        hBox_BivCat_CategoryBoxes.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        littleSquaresText = new Text[nLittleSquares];
        squaresNText = new HBox[nLittleSquares];
        
        for (int i = 0; i < nRowsCat; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);
            littleSquaresText[i] = new Text(0, 0, strLeftLabels[i]);
            littleSquaresText[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,18));
            littleSquaresText[i].setFill(graphColors[i]);
            squaresNText[i] = new HBox(10);
            squaresNText[i].setFillHeight(false);
            squaresNText[i].setAlignment(Pos.CENTER);
            squaresNText[i].setStyle("-fx-padding: 2;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-insets: 5;" +
                                     "-fx-border-radius: 5;");
            squaresNText[i].getChildren().addAll(littleSquares[i], littleSquaresText[i]);
            hBox_BivCat_CategoryBoxes.getChildren().add(squaresNText[i]);
        }       
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        canvas_BivCat.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvas_BivCat.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane_BoxPlot = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(anchorPane_TitleInfo, hBox_BivCat_CategoryBoxes, canvas_BivCat, categoryAxis_X, yAxis);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void initialize() { 
        // These values are for positioning the colored squares
        nSquaresRow1 = Math.min(4, nLeftLabels);
        nSquaresRow2 = nLeftLabels - nSquaresRow1;
        startSquareFactor_1 = 1.0 / (2. * (nSquaresRow1 + 1.));
        jumpSquareFactor_1 = 1.0 / (nSquaresRow1 + 1.);
        startSquareFactor_2 = 1.0 / (2. * (nSquaresRow2 + 1.));
        jumpSquareFactor_2 = 1.0 / (nSquaresRow2 + 1.);
        
        anchorPane_TitleInfo = new AnchorPane();
        anchorPane_TitleInfo.getChildren().addAll(txtTitle1, txtTitle2);
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        textForSquares = new Text[nLittleSquares];
        
        for (int i = 0; i < nLeftLabels; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);

            // Formula for placement?  Odd/Even?
            //  The zeroth textForSquares is "All"
            textForSquares[i] = new Text(0, 0, strLeftLabels[i]);
            textForSquares[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,14));
            textForSquares[i].setFill(graphColors[i]);
            
            anchorPane_TitleInfo.getChildren().addAll(littleSquares[i]);
            anchorPane_TitleInfo.getChildren().addAll(textForSquares[i]);
        }
    }
    
    private void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
      
    public void setColor( int relPos) {           
        gc.setStroke(graphColors[relPos]);
        gc.setFill(graphColors[relPos]);     
    } 

    public Pane getTheContainingPane() { return theContainingPane; }
}



