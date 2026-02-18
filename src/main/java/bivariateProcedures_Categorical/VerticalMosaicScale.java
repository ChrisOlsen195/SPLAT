/**************************************************
 *               VerticalMosaicScale              * 
 *                    12/10/25                    *
 *                      15:00                     *
 *************************************************/
package bivariateProcedures_Categorical;

import chiSquare_Assoc.X2Assoc_MosaicPlotView;
import epidemiologyProcedures.Epi_MosaicPlotView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;

public class VerticalMosaicScale {
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    double verticalLinePlacement, majorTickStart, minorTickStart, 
           majorTickLength, minorTickLength, pxPaneWidth, pxPaneHeight,
           roomForScaleValues, labelStartAt_x, labelStartAt_y;
    
    String strYLabel;
        
    Font tickValueFont, yLabelFont;
    Line verticalLine;
    Line[] majorTickMarks, minorTickMarks;
    Pane vPane;
    Text txtYLabel;
    Text[] majorTickValues;

    public VerticalMosaicScale(BivCat_MosaicPlotView bivCat_MosaicPlotView, 
                               double pxPaneHeight, double pxPaneWidth, 
                               String strYLabel) {
        if (printTheStuff) {
            System.out.println("*** 38 VerticalMosaicScale, Constructing");
        }
        this.pxPaneHeight = pxPaneHeight;
        this.pxPaneWidth = pxPaneWidth;
        this.strYLabel = strYLabel;
        finishConstruction();
    }
    
    public VerticalMosaicScale(BivCat2x_MosaicPlotView bivCat2x_MosaicPlotView, 
                               double pxPaneHeight, double pxPaneWidth, 
                               String strYLabel) {
        if (printTheStuff) {
            System.out.println("*** 50 VerticalMosaicScale, Constructing");
        }
        this.pxPaneHeight = pxPaneHeight;
        this.pxPaneWidth = pxPaneWidth;
        this.strYLabel = strYLabel;
        finishConstruction();
    }
    
    public VerticalMosaicScale(Epi_MosaicPlotView epi_MosaicPlotView, 
                               double pxPaneHeight, double pxPaneWidth, 
                               String strYLabel) {
        if (printTheStuff) {
            System.out.println("*** 50 VerticalMosaicScale, Constructing");
        }
        this.pxPaneHeight = pxPaneHeight;
        this.pxPaneWidth = pxPaneWidth;
        this.strYLabel = strYLabel;
        finishConstruction();
    }
    
    public VerticalMosaicScale(X2Assoc_MosaicPlotView x2Assoc_MosaicPlotView, 
                               double pxPaneHeight, double pxPaneWidth, 
                               String strYLabel) {
        if (printTheStuff) {
            System.out.println("*** 50 VerticalMosaicScale, Constructing");
        }
        this.pxPaneHeight = pxPaneHeight;
        this.pxPaneWidth = pxPaneWidth;
        this.strYLabel = strYLabel;     
        finishConstruction();
    }
    
    private void finishConstruction() {
        if (printTheStuff) {
            System.out.println("*** 60 VerticalMosaicScale, continueConstruction()");
        }
        majorTickLength = 15;
        minorTickLength = 7;
        roomForScaleValues = 29;
        yLabelFont = Font.font("NewTimesRoman", FontWeight.BOLD, 16);
        tickValueFont = Font.font("NewTimesRoman", FontWeight.SEMI_BOLD, 12);
       
        vPane = new Pane();
        vPane.setPrefSize(pxPaneWidth, pxPaneHeight);
        
        verticalLinePlacement = pxPaneWidth - 3;
        labelStartAt_x = verticalLinePlacement - 100.0;
        labelStartAt_y = pxPaneHeight/2.0;  // Need to account for label length
        txtYLabel = new Text(labelStartAt_x, labelStartAt_y, strYLabel);
        txtYLabel.setFont(yLabelFont);
        txtYLabel.setRotate(-90.);
        majorTickStart = verticalLinePlacement - majorTickLength;
        minorTickStart = verticalLinePlacement - minorTickLength;
        verticalLine = new Line(verticalLinePlacement, 0.0, verticalLinePlacement, pxPaneHeight);
        verticalLine.setStroke(Color.BLACK);
        verticalLine.setStrokeWidth(2.5);
        //  **************   Tick Marks & Values  *****************
        majorTickMarks = new Line[11];
        majorTickValues = new Text[11];
        
        minorTickMarks = new Line[10];
        
        for (int ithTick = 0; ithTick < 11; ithTick++) {
            double dbl_ithTick = ithTick;
            double bigTickYPlacement = 0.1 * pxPaneHeight * dbl_ithTick;
            majorTickMarks[ithTick] = new Line(majorTickStart, bigTickYPlacement, verticalLinePlacement, bigTickYPlacement);
            majorTickMarks[ithTick].setStroke(Color.BLUE);
            majorTickMarks[ithTick].setStrokeWidth(1.0);
            
            majorTickValues[ithTick] = new Text();
            double daTickValue = 1.0 - (double)ithTick / 10.0;
            String tickString = String.format("%3.2f", daTickValue);
            
            majorTickValues[ithTick].setText(tickString);
            majorTickValues[ithTick].setFont(tickValueFont);
            double startMajorValuesAt = verticalLinePlacement - majorTickLength - roomForScaleValues;
            majorTickValues[ithTick].setX(startMajorValuesAt);
            majorTickValues[ithTick].setY(bigTickYPlacement + 5);
        }
        
        for (int ithTick = 0; ithTick < 10; ithTick++) {
            double dbl_ithTick = ithTick;
            double smallTickYPlacement = 0.1 * pxPaneHeight * dbl_ithTick + .05 * pxPaneHeight;
            minorTickMarks[ithTick] = new Line(minorTickStart, smallTickYPlacement, verticalLinePlacement, smallTickYPlacement);
            minorTickMarks[ithTick].setStroke(Color.BLUE);
            minorTickMarks[ithTick].setStrokeWidth(1.0);        
        }
                
        vPane.getChildren().addAll(majorTickMarks);
        vPane.getChildren().addAll(minorTickMarks);
        vPane.getChildren().add(verticalLine);
        vPane.getChildren().add(txtYLabel);
        vPane.getChildren().addAll(majorTickValues);
    } 
    
    public Pane getVerticalPane() {return vPane; }
}
