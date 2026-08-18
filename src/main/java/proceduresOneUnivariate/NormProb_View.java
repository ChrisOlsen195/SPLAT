/**************************************************
 *                  NormProb_View                 *
 *                    06/21/26                    *
 *                      18:00                     *
 *************************************************/
package proceduresOneUnivariate;

import simpleRegression.Inf_Regr_Dashboard;
import multipleRegression.MultReg_Dashboard;
import simpleLogisticRegression.Logistic_Dashboard;
import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import superClasses.BivariateScale_View;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import quadraticRegression.*;
import anova1.categorical.*;
import anova1.quantitative.ANOVA1_Quant_Dashboard;
import anova2.ANCOVA_Dashboard;
import simpleRegression.Regr_Compare_Dashboard;
import the_t_procedures.Matched_t_Dashboard;

public class NormProb_View extends BivariateScale_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nDataPoints, nCheckBoxes;

    double[] dataArraySorted, normalScoresSorted, adStats;
    
    String adString, adPValue;
    AnchorPane anchorPane;    
  
    // My classes
    QuantitativeDataVariable qdv_Data, qdv_NormalScores;
    NormProb_Model normProb_Model;
    Text title1Text, title2Text;
    
    // FX
    Pane theContainingPane;

    public NormProb_View(NormProb_Model normProb_Model, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);   
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        if (printTheStuff) {
            System.out.println("56 *** NormProb_View, Exploration Dashboard, Constructing");
        }
        setMinHeight(100);
        setMinWidth(100);
        this.normProb_Model = normProb_Model;
        constructTheModel();
        String title2String = "Z  vs.  " + normProb_Model.getSubTitle();
        title2Text = new Text (60, 45, title2String);
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, MultReg_Dashboard multReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("73 *** NormProb_View, m MultReg Dashboard, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        setMinHeight(100);
        setMinWidth(100);
        this.normProb_Model = normProb_Model;
        constructTheModel();
        title2Text = new Text (60, 45, normProb_Model.getSubTitle());
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, Matched_t_Dashboard matched_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("91 *** NormProb_View, Matched t Dashboard, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        setMinHeight(100);
        setMinWidth(100);

        this.normProb_Model = normProb_Model;
        constructTheModel();
        title2Text = new Text (60, 45, normProb_Model.getSubTitle());
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, QuadReg_Dashboard quadReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("110 *** NormProb_View, QuadReg Dashboard, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        setMinHeight(100);
        setMinWidth(100);
        this.normProb_Model = normProb_Model;
        constructTheModel();
        title2Text = new Text (60, 45, normProb_Model.getSubTitle());
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, Inf_Regr_Dashboard reg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("128 *** NormProb_View, Inf Reg Dashboard, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        setMinHeight(100);
        setMinWidth(100);
        this.normProb_Model = normProb_Model; 
        constructTheModel();
        title2Text = new Text (60, 45, "Standardized residuals vs. " + normProb_Model.getNormProbLabel()); 
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, Logistic_Dashboard logReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("146 *** NormProb_View, Logistic Dashboard, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        setMinHeight(100);
        setMinWidth(100);
        this.normProb_Model = normProb_Model; 
        constructTheModel();
        title2Text = new Text (60, 45, "Standardized residuals vs. " + normProb_Model.getNormProbLabel());       
        makeTheCheckBoxes();    
        makeItHappen();         
    }
      
    public NormProb_View(NormProb_Model normProb_Model, ANOVA1_Cat_Dashboard anova1_Cat_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("164 *** NormProb_View, ANOVA_1_Cat, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        setMinHeight(100);
        setMinWidth(100);
        this.normProb_Model = normProb_Model;
        constructTheModel();        
        title2Text = new Text (60, 45, "Standardized residuals vs. " + normProb_Model.getNormProbLabel());        
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, ANOVA1_Quant_Dashboard anova1_Quant_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("182 *** NormProb_View, ANOVA_1_Quant, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.normProb_Model = normProb_Model;
        constructTheModel();        
        title2Text = new Text (60, 45, "Standardized residuals vs. " + normProb_Model.getNormProbLabel());        
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, ANCOVA_Dashboard ancova_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("198 *** NormProb_View, ANCOVA, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.normProb_Model = normProb_Model;
        constructTheModel();        
        title2Text = new Text (60, 45, "Standardized residuals vs. " + normProb_Model.getNormProbLabel());        
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, Regr_Compare_Dashboard regr_Compare_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("214 *** NormProb_View, Regr_Compare, constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.normProb_Model = normProb_Model;
        constructTheModel();        
        title2Text = new Text (60, 45, normProb_Model.getNormProbLabel() + " vs Standardized residuals");        
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    private void constructTheModel() {
        if (printTheStuff) {
            System.out.println("227 --- NormProb_View, constructTheModel()");
        }
        qdv_Data = new QuantitativeDataVariable();
        qdv_Data = normProb_Model.getData();
        title2Text = new Text (60, 45, " Normal Prob Plot ");
        qdv_NormalScores = new QuantitativeDataVariable();
        qdv_NormalScores = normProb_Model.getNormalScores();  
        
        nDataPoints = qdv_Data.getLegalN();
        dataArraySorted = new double[nDataPoints];
        dataArraySorted = qdv_Data.getTheDataSorted();
        adStats = new double[3];
        adStats = qdv_Data.getADStats();
        normalScoresSorted = new double[nDataPoints];
        normalScoresSorted = qdv_NormalScores.getTheDataSorted();
        
        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();        
    }
    
    private void makeItHappen() {       
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void completeTheDeal() { 
        if (printTheStuff) {
            System.out.println("257 --- NormProb_View, completeTheDeal()");
        }
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();   
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }

    public void setUpUI() {
        title1Text = new Text(50, 25, " Normal Probability Plot ");
        title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
   
    public void initializeGraphParameters() {  
        constructDataArray();
        xAxis = new genericClasses.JustAnAxis(xDataMin, xDataMax);
        xAxis.setSide(Side.BOTTOM);     
        xAxis.setLabel(normProb_Model.getSubTitle());
        yAxis = new genericClasses.JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setLabel("Standard Score (Z)");
        
        newX_Lower = xDataMin; newX_Upper = xDataMax;
        newY_Lower = yDataMin; newY_Upper = yDataMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );            
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);            
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(title1Text, title2Text, xAxis, yAxis, graphCanvas);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    
    public void doTheGraph() {    
        double text1Width = title1Text.getLayoutBounds().getWidth();
        double text2Width = title2Text.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(title1Text, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(title1Text, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(title1Text, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(title1Text, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(title2Text, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(title2Text, 0.2 * tempHeight);
        
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

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        for (int i = 0; i < nDataPoints; i++) {
            double xx = xAxis.getDisplayPosition(dataArraySorted[i]);
            double yy = yAxis.getDisplayPosition(normalScoresSorted[i]);
            gc.fillOval(xx - 4, yy - 4, 8, 8); //  0.5*radius to get dot to center
        }

            adString = "Anderson-Darling = " + String.format("%5.3f", adStats[1]);
            adPValue = "pValue = " + String.format("%5.3f", adStats[2]);
            gc.fillText(adString, 10, 10);
            gc.fillText(adPValue, 75, 25);
    }
    
    private void makeTheCheckBoxes() { /* No op */}
    
    private void constructDataArray() {
        xDataMin = xDataMax = dataArraySorted[0];
        yDataMin = yDataMax = normalScoresSorted[0];

        for (int iRow = 0; iRow < nDataPoints; iRow++) {
            double tempDoubleX = dataArraySorted[iRow];
            double tempDoubleY = normalScoresSorted[iRow];
  
            if (tempDoubleX < xDataMin) xDataMin = tempDoubleX;
            if (tempDoubleY < yDataMin) yDataMin = tempDoubleY;
            if (tempDoubleX > xDataMax) xDataMax = tempDoubleX;
            if (tempDoubleY > yDataMax) yDataMax = tempDoubleY;
        } 
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;
        
        //  Make room for the circles
        xDataMin = xDataMin - .02 * xRange; xDataMax = xDataMax + .02 * xRange;
        yDataMin = yDataMin - .02 * yRange; yDataMax = yDataMax + .02 * yRange;                 
    }

   public Pane getTheContainingPane() { return theContainingPane; }      
}