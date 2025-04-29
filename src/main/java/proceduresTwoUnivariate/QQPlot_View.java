/**************************************************
 *                   QQPlot_View                  *
 *                    01/16/25                    *
 *                     12:00                      *
 *************************************************/
/******************************************************************
*                                                                 *
*  A clearly stated algorithm for the general QQ plot is VERY     *
*    difficult to find.  The algorithm coded here is from         *
*   Chambers, J. M., et al.  (1983) Graphical methods for Data    *
*   Analysis.  Duxbury Pres: Boston.  Chapter 3.                  *
*                                                                 *
******************************************************************/
package proceduresTwoUnivariate;

import the_t_procedures.Indep_t_Dashboard;
import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import superClasses.BivariateScale_View;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class QQPlot_View extends BivariateScale_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nOneDataPoints, nTwoDataPoints, smallerN, largerN;
    
    String subTitle, firstVarDescription, secondVarDescription;

    double[] dataArrayOne, dataArrayTwo, quantSmall, quantLarge, xSmaller, 
             xLarger, slope, intercept,  tempLarger;

    AnchorPane anchorPane;    
 
    // My classes
    Explore_2Ind_Dashboard explore2Ind_Dashboard;
    Indep_t_Dashboard independent_t_Dashboard;
    QuantitativeDataVariable qdv_0, qdv_1;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    QQPlot_Model qqPlot_Model;    
    // FX
    AnchorPane checkBoxRow;
    Pane theContainingPane;

    Text title1Text, title2Text;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;


    public QQPlot_View(QQPlot_Model qqPlot_Model, Explore_2Ind_Dashboard explore2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("75 *** QQPlot_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphCanvas = new Canvas(initWidth, initHeight); 
        gc = graphCanvas.getGraphicsContext2D();
        this.qqPlot_Model = qqPlot_Model;
        firstVarDescription = qqPlot_Model.getFirstVarDescription();
        secondVarDescription = qqPlot_Model.getSecondVarDescription();
        subTitle = qqPlot_Model.getSubTitle();
        this.explore2Ind_Dashboard = explore2Ind_Dashboard;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = qqPlot_Model.getAllTheQDVs();
        
        qdv_0 = new QuantitativeDataVariable();
        qdv_0 = allTheQDVs.get(0);
        nOneDataPoints = qdv_0.getLegalN();
        dataArrayOne = new double[nOneDataPoints];
        dataArrayOne = qdv_0.getTheDataSorted();        
        qdv_1 = new QuantitativeDataVariable();
        qdv_1 = allTheQDVs.get(1); 
        nTwoDataPoints = qdv_1.getLegalN();
        dataArrayTwo = new double[nTwoDataPoints];
        dataArrayTwo = qdv_1.getTheDataSorted();
        
        xDataMin = dataArrayOne[0];
        xDataMax = dataArrayOne[dataArrayOne.length - 1];
        yDataMin = dataArrayTwo[0];
        yDataMax = dataArrayTwo[dataArrayTwo.length - 1];  
        
        makeTheCheckBoxes();    
        makeItHappen();  
        
        if (nOneDataPoints == nTwoDataPoints) {
            doEqualSizeProcedure(); 
        } else {
            doUnEqualSizeProcedure(); 
        }
    }

    public QQPlot_View(QQPlot_Model qqPlot_Model, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("120 *** QQPlot_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.qqPlot_Model = qqPlot_Model;
        firstVarDescription = qqPlot_Model.getFirstVarDescription();
        secondVarDescription = qqPlot_Model.getSecondVarDescription();
        subTitle = qqPlot_Model.getSubTitle();
        this.independent_t_Dashboard = independent_t_Dashboard;
        graphCanvas = new Canvas(initWidth, initHeight); 
        gc = graphCanvas.getGraphicsContext2D();        
        allTheQDVs = new ArrayList<>();
        allTheQDVs = qqPlot_Model.getAllTheQDVs();
        
        qdv_0 = new QuantitativeDataVariable();
        qdv_0 = allTheQDVs.get(0);
        nOneDataPoints = qdv_0.getLegalN();
        dataArrayOne = new double[nOneDataPoints];
        dataArrayOne = qdv_0.getTheDataSorted();        

        qdv_1 = new QuantitativeDataVariable();
        qdv_1 = allTheQDVs.get(1); 
        nTwoDataPoints = qdv_1.getLegalN();
        dataArrayTwo = new double[nTwoDataPoints];
        dataArrayTwo = qdv_1.getTheDataSorted();
   
        // -----------------------------------------------------
        xDataMin = dataArrayOne[0];
        xDataMax = dataArrayOne[dataArrayOne.length - 1];
        yDataMin = dataArrayTwo[0];
        yDataMax = dataArrayTwo[dataArrayTwo.length - 1];        
        // -----------------------------------------------------
        
        makeTheCheckBoxes();    
        makeItHappen(); 
        
        if (nOneDataPoints == nTwoDataPoints) { doEqualSizeProcedure(); } 
        else { doUnEqualSizeProcedure(); }

        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();  
    }
    
    private void doEqualSizeProcedure() {
        if (printTheStuff == true) {
            System.out.println("164 --- QQPlot_View, doEqualSizeProcedure()");
        }
        smallerN = nOneDataPoints;  // These should be equal; distinction is
        largerN = nTwoDataPoints;   // for the plotting
        xSmaller = new double[smallerN];  
        xLarger = new double[smallerN];   
        System.arraycopy(dataArrayOne, 0, xSmaller, 0, nOneDataPoints);
        System.arraycopy(dataArrayTwo, 0, xLarger, 0, nTwoDataPoints);
    }
    
    private void doUnEqualSizeProcedure() {
        if (printTheStuff == true) {
            System.out.println("176 --- QQPlot_View, doUnEqualSizeProcedure()");
        }
        int /*smallSize,*/ leftQIndex; //, indexLeftInterval;
        double oneThird = 1.0 / 3.0;
        double smallDenom;
        
        if (nOneDataPoints < nTwoDataPoints) {
            smallerN = nOneDataPoints;  // These should be equal; distinction is
            largerN = nTwoDataPoints;   // for the plotting
            xSmaller = new double[smallerN]; 
            xLarger = new double[smallerN]; 
            System.arraycopy(dataArrayOne, 0, xSmaller, 0, nOneDataPoints);
            // hold only smallerN points for graphing 
            tempLarger = new double[nTwoDataPoints];
            System.arraycopy(dataArrayTwo, 0, tempLarger, 0, nTwoDataPoints);
            
        } else {
            smallerN = nTwoDataPoints;  // These should be equal; distinction is
            largerN = nOneDataPoints;   // for the plotting
            xSmaller = new double[smallerN];  // These should be
            xLarger = new double[smallerN];   // equal in size
            System.arraycopy(dataArrayTwo, 0, xSmaller, 0, nTwoDataPoints);
            // hold only smallerN points for graphing 
            tempLarger = new double[nOneDataPoints];
            System.arraycopy(dataArrayOne, 0, tempLarger, 0, nOneDataPoints);
        }
        
        /********************************************************************
        *   At this point xSmaller should be finished, but not xLarger.     *
        *   Now get percentiles for the xSmaller and xLarger values, using  *
        *   definition 8 from Hyndman, R., & Fan, Y.  Sample quantiles in   *
        *   Statistical Packages (1996).  The American Statistician,        * 
        *   50(4): 361-5.                                                   *
        ********************************************************************/
        // quantiles for smaller data set
        smallDenom = smallerN + oneThird;
        quantSmall = new double[smallerN];
        
        for (int qth = 0; qth < smallerN; qth++) {
            quantSmall[qth] = ((qth+1) - oneThird) / smallDenom;
        }     
        
        // quantiles for larger data set
        double largeDenom = largerN + oneThird;
        quantLarge = new double[largerN];
        slope = new double[largerN];
        intercept = new double[largerN];
        
        for (int qth = 0; qth < largerN; qth++) {
            quantLarge[qth] = ((qth+1) - oneThird) / largeDenom;
        }
        
        /********************************************************************
        *  Now prepare for estimation of xLarge values via interpolation    *
        *  We will be estimating xLarge from the xLarge percentiles.            *
        ********************************************************************/
        slope = new double[largerN - 1];
        
        for (int qth = 0; qth < largerN - 1; qth++) {
            double tempNumer = tempLarger[qth + 1] - tempLarger[qth];
            double tempDenom = quantLarge[qth + 1] - quantLarge[qth];
            slope[qth] = (tempNumer / tempDenom);
            intercept[qth] = tempLarger[qth] - slope[qth] * quantLarge[qth];
        }
        
        /********************************************************************
        *            Estimate the xSmall quantiles for xLarge               *
        ********************************************************************/        

        for (int smallQuant = 0; smallQuant < smallerN; smallQuant++) {
            //  Find the left end of the interval where the large quantile
            //  crosses the small quantile
            leftQIndex = 0;            
            for (int smallQIndex = 0; smallQIndex < smallerN - 1; smallQIndex++) {                
                for (int largeQIndex = 0; largeQIndex < largerN - 1; largeQIndex++) {                    
                    if ((quantLarge[largeQIndex] <= quantSmall[smallQIndex])
                       && ((quantLarge[largeQIndex + 1] > quantSmall[smallQIndex])))  {
                        leftQIndex = smallQIndex;
                    }
                    xLarger[smallQIndex] = slope[leftQIndex] * quantSmall[leftQIndex] + intercept[leftQIndex]; 
                }
             }
        } 
    }    
    
    private void makeItHappen() {       
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void completeTheDeal() { 
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers(); 
        xAxis.setLabel(firstVarDescription);
        yAxis.setLabel(secondVarDescription);        
        doTheGraph();   
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }

    public void setUpUI() {
        title1Text = new Text(50, 25, " QQ Plot "); 
        title2Text = new Text (60, 45, subTitle);
        title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
   
    public void initializeGraphParameters() {  
        if (printTheStuff == true) {
            System.out.println("289 --- QQPlot_View, initializeGraphParameters()");
        }
        constructDataArray();
        xAxis = new genericClasses.JustAnAxis(xDataMin, xDataMax);
        xAxis.setSide(Side.BOTTOM);       
        yAxis = new genericClasses.JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
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
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
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

        for (int i = 0; i < smallerN; i++) {
            double xx = xAxis.getDisplayPosition(dataArrayOne[i]);
            double yy = yAxis.getDisplayPosition(dataArrayTwo[i]);
            gc.fillOval(xx - 4, yy - 4, 8, 8); //  0.5*radius to get dot to center
        }
        
        double x1 = xAxis.getDisplayPosition(xSmaller[0]);
        double y1 = yAxis.getDisplayPosition(xSmaller[0]);
        double x2 = xAxis.getDisplayPosition(xSmaller[smallerN - 1]);
        double y2 = yAxis.getDisplayPosition(xSmaller[smallerN - 1]);
        gc.setLineWidth(2);
        gc.setStroke(Color.TOMATO);
        gc.strokeLine(x1, y1, x2, y2);  
        
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
    
    private void makeTheCheckBoxes() {} // Can dump?
    
    private void constructDataArray() { 
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;
        
        //  Make room for the circles
        xDataMin = xDataMin - .02 * xRange; xDataMax = xDataMax + .02 * xRange;
        yDataMin = yDataMin - .02 * yRange; yDataMax = yDataMax + .02 * yRange;  

        xDataMin = Math.min(xDataMin, yDataMin);
        yDataMin = xDataMin;
        xDataMax = Math.max(xDataMax, yDataMax);
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;      
    }

    public Pane getTheContainingPane() { return theContainingPane; }      
}