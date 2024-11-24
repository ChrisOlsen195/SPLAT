/**************************************************
 *             ANOVA2_Views_Super                 *
 *                  05/24/24                      *
 *                    12:00                       *
 *************************************************/
package anova2;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dataObjects.UnivariateContinDataObj;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import splat.Data_Manager;
import utilityClasses.*;

public class ANOVA2_Views_Super extends Region {
    // POJOs
    boolean dragging;
    
    int nRowsCat, nLittleSquares, nFactorA_Levels, nFactorB_Levels, 
        nDataPoints, nSquaresRow1, nSquaresRow2;
    int[] whiskerEndRanks;
    
    double  initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
            xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
            yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
            newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
            bottomOfLowWhisker, topOfHighWhisker, tempPos0, tempPos1, 
            bigTickInterval, startSquareFactor_1, 
            jumpSquareFactor_1, startSquareFactor_2, jumpSquareFactor_2,
            text1Width, text2Width, initHoriz, initVert, initWidth, initHeight; 
    
    double[] fiveNumberSummary;

    String  strTitle1, strTitle2, strResponseVar,
            strFactorA, strFactorB, graphsCSS;
    
    String waldoFile;
    
    ObservableList<String> preStrTopLabels, preStrLeftLabels, strTopLabels, 
                           strLeftLabels, categoryLabels, factorA_Levels, 
                           factorB_Levels;
    
    // My classes
    ANOVA2_Factorial_Model anova2_Factorial_Model;
    Data_Manager dm;
    DragableAnchorPane dragableAnchorPane;
    HorizontalPositioner horizontalPositioner;
    JustAnAxis yAxis;
    UnivariateContinDataObj tempUCDO;    
    Text txtTitle1, txtTitle2;
        
    UnivariateContinDataObj allData_UCDO;

    Color[] graphColors = { Color.BROWN, Color.OLIVE, Color.TEAL,  Color.NAVY, 
                            Color.RED, Color.BLACK, Color.ORANGE,Color.MAROON, 
                            Color.GREEN, Color.CYAN, Color.BLUE, Color.PURPLE, 
                            Color.LINEN, Color.MAGENTA}; 
    
    //  FX Classes
    AnchorPane anchorPane_TitleInfo, anchorPane_BoxPlot;
    Canvas canvas_ANOVA2;
    CategoryAxis categoryAxis_X;
    GraphicsContext gc;
    HBox anova2CategoryBoxes;
    HBox[] squaresNText;
    Line line;
    Pane theContainingPane;
    Point2D horizPosition;
    Rectangle[] littleSquares;
    Text[] textForSquares, littleSquaresText;
    
    public SnapshotParameters params;
    public WritableImage image;
    public Clipboard clipboard;
    public ClipboardContent content;    
    
    ANOVA2_Views_Super() {
        //System.out.println("\n97 ANOVA2_Views_Super, Constructing");
    }
    
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {           
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                dragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging) {
                    yAxis.setLowerBound(newY_Lower ); 
                    yAxis.setUpperBound(newY_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double yPix_Dragging = mouseEvent.getY();  
                newY_Lower = yAxis.getLowerBound();
                newY_Upper = yAxis.getUpperBound(); 
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
                double frac = mouseEvent.getY() / dispLowerBound;
                
                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5) {
                        if (!yAxis.getHasForcedHighScaleEnd()) {
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                        }
                    }
                    else {
                        if (!yAxis.getHasForcedLowScaleEnd()) {
                            newY_Lower = yAxis.getLowerBound() + deltaY;
                        }
                    }
                }
                else 
                    if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)) {   
                        if (frac < 0.5) {
                            if (!yAxis.getHasForcedHighScaleEnd()) {
                                newY_Upper = yAxis.getUpperBound() - deltaY;
                            }
                        }
                        else {
                            if (!yAxis.getHasForcedLowScaleEnd()) {
                                newY_Lower = yAxis.getLowerBound() - deltaY;
                            }
                        }
                    }    

                if (yAxis.getHasForcedLowScaleEnd()) {
                    newY_Lower = yAxis.getForcedLowScaleEnd();
                }
            
                if (yAxis.getHasForcedHighScaleEnd()) {
                    newY_Upper = yAxis.getForcedHighScaleEnd();
                }
                
                yAxis.setLowerBound(newY_Lower ); 
                yAxis.setUpperBound(newY_Upper ); 

                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
   
                yPix_MostRecentDragPoint = mouseEvent.getY();
                
                doThePlot();
            }   // end if mouse dragged
        }   //  end handle
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                xPix_InitialPress = mouseEvent.getX();  
                yPix_InitialPress = mouseEvent.getY();  
            }
        }
    }; 
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {  }
    };
    
    public void positionTopInfo() {
        //System.out.println("187 ANOVA2_Views_Super, positionTopInfo()");
        int i; 
        double atIWidth, tempPosition, startRect, jumpRect, k1, k2;
        atIWidth = anchorPane_TitleInfo.getWidth();
        
        for (i = 0; i < nSquaresRow1; i++) {
            startRect = atIWidth * startSquareFactor_1;
            jumpRect = atIWidth * jumpSquareFactor_1;
            tempPosition = startRect + i * jumpRect;
            littleSquares[i].setX(tempPosition);
            littleSquares[i].setY(60);
            textForSquares[i].setX(tempPosition + 20);
            textForSquares[i].setY(70);
        }

        if (nSquaresRow2 > 0) {
            
            for (i = 0; i < nSquaresRow2; i++) {
                startRect = anchorPane_TitleInfo.getWidth() * startSquareFactor_2;
                jumpRect = anchorPane_TitleInfo.getWidth() * jumpSquareFactor_2;
                tempPosition = startRect + i * jumpRect;
                littleSquares[nSquaresRow1 + i].setX(tempPosition);
                littleSquares[nSquaresRow1 + i].setY(80);
                textForSquares[nSquaresRow1 + i].setX(tempPosition + 20);
                textForSquares[nSquaresRow1 + i].setY(90);
            }
        }
        
        k1 = 12.0;  //  Hack for font 25 
        k2 = 10.0;    //  Hack for font 20
        txtTitle1.setX(atIWidth / 2. - k1 * strTitle1.length() / 2.);
        txtTitle2.setX(atIWidth / 2. - k2 * strTitle2.length() / 2.);
        txtTitle2.setY(50);
    }
    
    public void doThePlot() { }
}
