/**************************************************
 *           ANOVA2_BoxCircleInterActView         *
 *                  10/14/24                      *
 *                    00:00                       *
 *************************************************/
package anova2;

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

public class ANOVA2_BoxCircleInterActView extends ANOVA2_Views_Super { 
        // POJO's
        String waldoFile = "ANOVA2_BoxCircleInterActView";
        //String waldoFile = "";

    ANOVA2_BoxCircleInterActView(ANOVA2_Factorial_Model anova2_FactorialModel, 
            ANOVA2_RCB_Dashboard anova2_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super();
        this.anova2_Factorial_Model = anova2_FactorialModel;
        dm = anova2_FactorialModel.getDataManager();
        dm.whereIsWaldo(36, waldoFile, ", Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        theContainingPane = new Pane();
        
        nFactorA_Levels = anova2_FactorialModel.str_ALevels.size();
        nFactorB_Levels = anova2_FactorialModel.str_BLevels.size();
        
        categoryLabels = FXCollections.observableArrayList();        
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();  
        strTopLabels = FXCollections.observableArrayList();
        strLeftLabels = FXCollections.observableArrayList();
        
        preStrTopLabels = anova2_FactorialModel.getFactorALevels();
        preStrLeftLabels = anova2_FactorialModel.getFactorBLevels();
        int nTopLabels = preStrTopLabels.size() - 1;
        int nLeftLabels = preStrLeftLabels.size() - 1;
       
        for (int ithTopLabel = 0; ithTopLabel < nTopLabels; ithTopLabel++) {
            strTopLabels.add(preStrTopLabels.get(ithTopLabel + 1)); 
        }
        
        for (int ithLeftLabel = 1; ithLeftLabel < nLeftLabels; ithLeftLabel++) {
            strLeftLabels.add(preStrLeftLabels.get(ithLeftLabel + 1));
        }
        
        categoryLabels.addAll(strTopLabels);
        factorA_Levels.addAll(anova2_FactorialModel.getFactorALevels());
        factorB_Levels.addAll(anova2_FactorialModel.getFactorBLevels()); 

        allData_UCDO = anova2_FactorialModel.getAllDataUCDO();
        
        strResponseVar = anova2_FactorialModel.getResponseLabel();
        strFactorA = anova2_FactorialModel.getFactorALabel();
        strFactorB = anova2_FactorialModel.getFactorBLabel();
        
        strTitle1 = strResponseVar + " vs " + strFactorA + " & " + strFactorB;
        strTitle2 = " ";
        
        txtTitle1 = new Text(strTitle1);        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text(strTitle2);         
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();  
        nFactorA_Levels = this.anova2_Factorial_Model.getNFactorA_Levels();
        nFactorB_Levels = this.anova2_Factorial_Model.getNFactorB_Levels();
    }
    
    public void completeTheDeal() {
        dm.whereIsWaldo(88, waldoFile, "   *** completeTheDeal()");
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
        dm.whereIsWaldo(100, waldoFile, "   *** initializeGraphParams()");
        canvas_ANOVA2 = new Canvas(625, 625);
        gc = canvas_ANOVA2.getGraphicsContext2D(); 
        
        initial_yMin = anova2_Factorial_Model.getMinVertical();
        initial_yMax = anova2_Factorial_Model.getMaxVertical();
        initial_yRange = initial_yMax - initial_yMin;
        
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setBounds(initial_yMin, initial_yMax);
        yMin = initial_yMin; yMax = initial_yMax; yRange = initial_yRange;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        categoryAxis_X = new CategoryAxis(categoryLabels);
        categoryAxis_X.setSide(Side.BOTTOM); 
        categoryAxis_X.setAutoRanging(true);
        categoryAxis_X.setLabel(anova2_Factorial_Model.getFactorALabel());
        categoryAxis_X.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        categoryAxis_X.setPrefWidth(40);   
        categoryAxis_X.setLayoutX(500); categoryAxis_X.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(anova2_Factorial_Model.getResponseLabel());
        
        setHandlers();
    }
    
    private void setUpUI() {
        dm.whereIsWaldo(130, waldoFile, "   *** setUpUI()");        
        anova2CategoryBoxes = new HBox(nLittleSquares);
        anova2CategoryBoxes.setAlignment(Pos.CENTER);
        anova2CategoryBoxes.setStyle("-fx-padding: 2;"+
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
            littleSquaresText[i] = new Text(0, 0, strLeftLabels.get(i));
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
            anova2CategoryBoxes.getChildren().add(squaresNText[i]);
        }       
    }
    
    private void setUpAnchorPane() {
        dm.whereIsWaldo(166, waldoFile, "   *** setUpAnchorPane()");
        dragableAnchorPane = new DragableAnchorPane();
        canvas_ANOVA2.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        canvas_ANOVA2.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane_BoxPlot = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(anchorPane_TitleInfo, anova2CategoryBoxes, canvas_ANOVA2, categoryAxis_X, yAxis);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void initialize() { 
        dm.whereIsWaldo(180, waldoFile, "   *** initialize()");
        // These values are for positioning the colored squares
        nSquaresRow1 = Math.min(4, nFactorB_Levels);
        nSquaresRow2 = nFactorB_Levels - nSquaresRow1;
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
        
        for (int i = 0; i < nFactorB_Levels; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);

            // Formula for placement?  Odd/Even?
            //  The zeroth textForSquares is "All"
            textForSquares[i] = new Text(0, 0, factorB_Levels.get(i + 1));
            textForSquares[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,14));
            textForSquares[i].setFill(graphColors[i]);
            
            anchorPane_TitleInfo.getChildren().addAll(littleSquares[i]);
            anchorPane_TitleInfo.getChildren().addAll(textForSquares[i]);
        }
    }
    
    public void doThePlot() {}
    
    private void setHandlers() {
        dm.whereIsWaldo(216, waldoFile, "   *** setHandlers()");
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
      
    public void setColor( int relPos) { 
        dm.whereIsWaldo(223, waldoFile, "   *** setColor( int relPos)");
        gc.setStroke(graphColors[relPos]);
        gc.setFill(graphColors[relPos]);     
    } 

    public Pane getTheContainingPane() { return theContainingPane; }
}


