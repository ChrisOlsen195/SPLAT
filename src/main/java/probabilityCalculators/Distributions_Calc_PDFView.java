/**************************************************
 *             Distributions_Calc_PDFView         *
 *                   12/31/24                     *
 *                     12:00                      *
 *************************************************/
package probabilityCalculators;

//import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import superClasses.BivariateScale_W_CheckBoxes_View;

public class Distributions_Calc_PDFView extends BivariateScale_W_CheckBoxes_View {

    // POJOs
    boolean printTheStuff = true;
    
    public boolean dragging, initializing, okToGraph,
            leftTailChecked, midTailChecked, rightTailChecked;
    
    public int spacesNeeded, maxSpaces;
    
    public int q1, q2, q3;

    //     ********  Left and Right base X positions ********
    double left_Middle_Boundary, middle_Right_Boundary, leftArea, midArea, rightArea;
    
    public double xPrintPosLeft, xPrintPosCenter,  xPrintPosRight, 
           yPrintPosLeftRight,  yPrintPosCenter, maxOfYScale;
    
    public double daPDF;

    //                 ***********  Printing positions   ***********************
    double leftArea_XPosition, middleArea_XPosition, rightArea_XPosition;
 
    //                 *********  Do-The-Graph variables   *********************
    public double dTG_xx0, dTG_yy0, dTG_xAsDouble, dTG_xx1, dTG_yy1;
    
    double tempWidth, tempHeight;
    
    double text1Width, text2Width, paneWidth, txt1Edge, txt2Edge;

    public Label lbl_LeftProb, lbl_MidProb, lbl_RightProb, lbl_N_Equals, lbl_P_Equals,
          lbl_LeftParenProbX_LT, lbl_LeftParenProbX_LE, lbl_LeftParen_ProbX_EQ,
          lbl_LeftProbX_LT_RightParen, lbl_LeftProbX_LE_RightParen,
          lbl_LeftProbX_EQ_RightParen, lbl_LeftParenProbX_GE,
          lbl_LeftProbX_GT_RightParen, lbl_LeftProbX_GE_RightParen,
          lbl_LeftParenProbX_GT, lbl_Range_Prob_LTLT_Left_Paren,
          lbl_Range_Prob_LTLE_Left_Paren, lbl_Range_Prob_LELE_Left_Paren,
          lbl_Range_Prob_LELT_Left_Paren, lbl_RangeIs_LTLT, lbl_RangeIs_LTLE, 
          lbl_RangeIs_LELE, lbl_RangeIs_LELT, lbl_RangeIs_LTLT_RightParen, 
          lbl_RangeIs_LTLE_RightParen, lbl_RangeIs_LELE_RightParen,  
          lbl_RangeIs_LELT_RightParen;      
    
    public String LT, EQ, GT, LE, GE, leftProb, rightProb, LT_AND_LT, LT_AND_LE, 
           LE_AND_LE, LE_AND_LT, str_LeftParenProb_Is_LT, 
           str_LeftParenProbX_Is_LE, str_LeftParenProbX_Is_EQ,
           str_LeftParenProbX_Is_GE, str_LeftParenProbX_Is_GT,
           str_Range_Prob_LeftParen, str_Range_Prob_LTLT, str_Range_Prob_LTLE, 
           str_Range_Prob_LELT, str_Range_Prob_LELE,
           strDaChoice, strDaLeftChoice, strDaRightChoice, strAnswer;    
    
    public String theModelName, tailChoice, prtLeftArea, prtMidArea, prtRightArea;
    //ArrayList<String> stringOfNSpaces; 
    
    ProbCalc_Dashboard probCalc_Dashboard;    
    
    public Pane theContainingPane;
    
    String title2String;

    public Distributions_Calc_PDFView(ProbCalc_Dashboard probCalc_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
    super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        if (printTheStuff) {
            System.out.println("\n84 *** Distributions_Calc_PDFView, Constructing");
        }
        xPrintPosLeft = 0.05;
        xPrintPosCenter = 0.325;
        xPrintPosRight = 0.5;
        yPrintPosLeftRight = 0.9;
        yPrintPosCenter = 0.95;  
        
        nCheckBoxes = 3;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Probabilities ";
        scatterPlotCheckBoxDescr[1] = " Quartiles ";
        scatterPlotCheckBoxDescr[2] = " Mean & StDev ";      
        checkBoxHeight = 350.0;       
        
        LT = " < "; EQ = " = "; GT = " > "; LE = " \u2266 "; GE = " \u2267 ";

        leftProb = "P(x"; 
        rightProb = ") = ";

        LT_AND_LT = LT + " x " + LT; LT_AND_LE = LT + " x " + LE;
        LE_AND_LE = LE + " x " + LE; LE_AND_LT = LE + " x " + LT;  
        
        str_Range_Prob_LeftParen = "P(";
        str_Range_Prob_LTLT = " < x < ";
        str_Range_Prob_LTLE = " < x " + LE;
        str_Range_Prob_LELT = LE + " x <";
        str_Range_Prob_LELE = LE + " x <" + LE;                       
    }
    
    protected void makeItHappen() {
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    protected void completeTheDeal() {
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();  //  Done in super
        setHandlers();      // Done in super
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
    
    public void prepareTheSupportAxis() {
        bigDelta = (newX_Upper - newX_Lower) / NUMBER_OF_DXs;
        delta = bigDelta;
        xDataMin = xDataMax = newX_Lower;
        xRange = newX_Upper - newX_Lower;        
        yRange = yDataMax = maxOfYScale;
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;
    }
    
    public void dTG_Continuous() {        
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        paneWidth = dragableAnchorPane.getWidth();
        txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        tempHeight = dragableAnchorPane.getHeight();
        tempWidth = dragableAnchorPane.getWidth();
       
        AnchorPane.setTopAnchor(txtTitle1, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * tempHeight);        
    }
    
public void dTG_Discrete() {        
        //double dTG_xx0, dtG_yy0, dtG_xAsDouble, dTG_yy1;
        //String tempString;
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        paneWidth = dragableAnchorPane.getWidth();
        txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        tempHeight = dragableAnchorPane.getHeight();
        tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(checkBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(checkBoxRow, 0.95 * tempHeight);
       
        AnchorPane.setTopAnchor(txtTitle1, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * tempHeight);
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex - 1) * tempWidth / 250.0 + 50);
        }    
    }

    public void makeARectangle(double xStart, double yStart, double xStop, double yStop) {
            xStart = xAxis.getDisplayPosition(xStart); 
            yStart = yAxis.getDisplayPosition(0.0); 
            xStop = xAxis.getDisplayPosition(xStop);
            yStop = yAxis.getDisplayPosition(daPDF);          
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart,yStart, xStart, yStop);   
            gc.strokeLine(xStop, yStart, xStop, yStop); 
            gc.strokeLine(xStart, yStart, xStop, yStart); 
            gc.strokeLine(xStart, yStop, xStop, yStop);         
    }
    
    public Pane getTheContainingPane() { return theContainingPane; }
}
