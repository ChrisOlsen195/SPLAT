/**************************************************
 *                HorizontalMosaicScale           * 
 *                    12/10/25                    *
 *                      18:00                     *
 **************************************************************/
package bivariateProcedures_Categorical;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;
import chiSquare_Assoc.*;
import epidemiologyProcedures.Epi_MosaicPlotView;
import javafx.scene.transform.Rotate;

public class HorizontalMosaicScale {
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nInFirstRow, nInSecondRow, nXLabels;

    double horizontalLinePlacement, majorTickStart, minorTickStart, 
           majorTickLength, minorTickLength, paneWidth, paneHeight,
           roomForScaleValues, roomForLabel, xVar_LabelStartAt_x, 
           xVar_LabelStartAt_y, pxShiftTo, propBandLength, prop_MiddleIthLabel;
    
    double[] preCumulativeColProps, cumulativeColPropBands;
    
    double yValueForRowLabels;
    String strXLabel;
    String[] strXValues;
    Font tickValueFont, xLabelFont;
    Line horizontalLine;
    Line[] majorTickMarks, minorTickMarks;
    Pane hPane;
    Text txtXVarLabel;
    Text[] majorTickValues, txtRowOfLabels, txtSecondRowOfLabels;
    
    X2Assoc_MosaicPlotView x2Assoc_MosaicPlotView;
    BivCat_MosaicPlotView bivCat_MosaicPlotView;
    BivCat2x_MosaicPlotView bivCat2x_MosaicPlotView;
    Epi_MosaicPlotView epi_MosaicPlotView;

    public HorizontalMosaicScale(BivCat_MosaicPlotView bivCat_MosaicPlotView, double paneWidth, String strXLabel, String[] strXValues) {
        if (printTheStuff) {
            System.out.println("*** 47 HorizontalMosaicScale, Constructing");
            System.out.println("--- 48, strXLabel  = " + strXLabel);
            System.out.println("--- 49, strXVallues  = " + strXValues);
        }        
        this.paneWidth = paneWidth;
        this.strXLabel = strXLabel;
        this.strXValues = strXValues;
        if (printTheStuff) {
            System.out.println("*** 55 HorizontalMosaicScale, Constructing");
            System.out.println("--- 56, strXLabel  = " + strXLabel);
            System.out.println("--- 57, strXVallues  = " + strXValues);
        } 
        this.bivCat_MosaicPlotView = bivCat_MosaicPlotView;
        nXLabels = strXValues.length;
        preCumulativeColProps = bivCat_MosaicPlotView.getBivCat_Model().getCumulativeColProps();
        int nPreCumulativeProps = preCumulativeColProps.length;
        cumulativeColPropBands = new double[nPreCumulativeProps + 1];
        for (int ithpre = 0; ithpre < nPreCumulativeProps; ithpre ++) {
            cumulativeColPropBands[ithpre] = preCumulativeColProps[ithpre];
        }
        cumulativeColPropBands[nPreCumulativeProps] = 1.0;
        
        txtXVarLabel = new Text(strXLabel);

        continueConstruction();
    }
    
    public HorizontalMosaicScale(BivCat2x_MosaicPlotView bivCat2x_MosaicPlotView, double paneWidth, String strXLabel, String[] strXValues) {
        this.paneWidth = paneWidth;
        this.strXLabel = strXLabel;
        this.strXValues = strXValues;
        if (printTheStuff) {
            System.out.println("*** 79 HorizontalMosaicScale, Constructing");
            System.out.println("--- 80, strXLabel  = " + strXLabel);
            System.out.println("--- 81, strXVallues  = " + strXValues);
        }
        this.bivCat2x_MosaicPlotView = bivCat2x_MosaicPlotView;
        nXLabels = strXValues.length;
        preCumulativeColProps = bivCat2x_MosaicPlotView.getBivCat_Model().getCumulativeColProps();
        int nPreCumulativeProps = preCumulativeColProps.length;
        cumulativeColPropBands = new double[nPreCumulativeProps + 1];
        for (int ithpre = 0; ithpre < nPreCumulativeProps; ithpre ++) {
            cumulativeColPropBands[ithpre] = preCumulativeColProps[ithpre];
        }
        cumulativeColPropBands[nPreCumulativeProps] = 1.0;
        
        txtXVarLabel = new Text(strXLabel);

        continueConstruction();
    }
    
    public HorizontalMosaicScale(Epi_MosaicPlotView epi_MosaicPlotView, double paneWidth, String strXLabel, String[] strXValues) {
        this.paneWidth = paneWidth;
        this.strXLabel = strXLabel;
        this.strXValues = strXValues;
        if (printTheStuff) {
            System.out.println("*** 79 HorizontalMosaicScale, Constructing");
            System.out.println("--- 80, strXLabel  = " + strXLabel);
            System.out.println("--- 81, strXVallues  = " + strXValues);
        }
        this.epi_MosaicPlotView = epi_MosaicPlotView;
        nXLabels = strXValues.length;
        preCumulativeColProps = epi_MosaicPlotView.getEpi_Model().getCumColProps();
        int nPreCumulativeProps = preCumulativeColProps.length;
        cumulativeColPropBands = new double[nPreCumulativeProps + 1];
        for (int ithpre = 0; ithpre < nPreCumulativeProps; ithpre ++) {
            cumulativeColPropBands[ithpre] = preCumulativeColProps[ithpre];
        }
        cumulativeColPropBands[nPreCumulativeProps] = 1.0;
        
        txtXVarLabel = new Text(strXLabel);

        continueConstruction();
    }
    
    public HorizontalMosaicScale(X2Assoc_MosaicPlotView x2Assoc_MosaicPlotView, double paneWidth, String strXLabel, String[] strXValues) {
        if (printTheStuff) {
            System.out.println("*** 95 HorizontalMosaicScale, Constructing");
            System.out.println("--- 96, strXVallues  = " + strXValues);
        }          
        this.paneWidth = paneWidth;
        this.strXValues = strXValues;
        this.x2Assoc_MosaicPlotView = x2Assoc_MosaicPlotView;
        //pxVertScaleWidth = x2Assoc_MosaicPlotView.get_pxVertScaleWidth();
        //pxMosaicPaneLeft = x2Assoc_MosaicPlotView.get_pxMosaicPaneLeft();
        nXLabels = strXValues.length;
        //pxMosaicPaneLeft = x2Assoc_MosaicPlotView.get_pxMosaicPaneLeft();
        //pxVertScaleWidth =x2Assoc_MosaicPlotView.get_pxVertScaleWidth();
        preCumulativeColProps = x2Assoc_MosaicPlotView.getX2Assoc_Model().getCumulativeColProps();
        int nPreCumulativeProps = preCumulativeColProps.length;
        cumulativeColPropBands = new double[nPreCumulativeProps + 1];
        for (int ithpre = 0; ithpre < nPreCumulativeProps; ithpre ++) {
            cumulativeColPropBands[ithpre] = preCumulativeColProps[ithpre];
        }
        cumulativeColPropBands[nPreCumulativeProps] = 1.0;
        
            if (printTheStuff) {
            for (int ithccpb = 0; ithccpb < nPreCumulativeProps; ithccpb++) {
                System.out.println("113 --- HorizMosScale, cumulativeColPropBands = " + cumulativeColPropBands[ithccpb]);
            }
        }
        txtXVarLabel = new Text(strXLabel);
        continueConstruction();        
    }
    
    private void continueConstruction() {
        if (printTheStuff) {
            System.out.println("*** 125 HorizontalMosaicScale, continueConstruction()");
        }
        paneHeight = 10.;
        majorTickLength = 15;
        minorTickLength = 7;
        roomForScaleValues = 25;
        roomForLabel = 110;

        nInFirstRow = nXLabels;
        if (printTheStuff) {
            for (int ithColProp = 0; ithColProp <= nXLabels; ithColProp++) {
                System.out.println("--- 136 HorMosScale, ithCumProp = " + cumulativeColPropBands[ithColProp]);
            }
        }
        xLabelFont = Font.font("NewTimesRoman", FontWeight.BOLD, 16);
        tickValueFont = Font.font("NewTimesRoman", FontWeight.SEMI_BOLD, 12);
        yValueForRowLabels = 55;

        hPane = new Pane();
        hPane.setPrefSize(paneWidth, paneHeight);
        if (printTheStuff) {
            System.out.println("--- 143 HorMosScale, hPane = " + hPane.toString());
        }        
        horizontalLinePlacement = 3;

        txtRowOfLabels = new Text[nXLabels];

        txtRowOfLabels[0] = new Text(strXValues[0]);
        cumulativeColPropBands[0] = 0.;
        
        for (int ithLabel = 0; ithLabel < nXLabels; ithLabel++) {
            txtRowOfLabels[ithLabel] = new Text(strXValues[ithLabel]);  
            txtRowOfLabels[ithLabel].setFont(xLabelFont);
            double pxTextWidth = txtRowOfLabels[ithLabel].getLayoutBounds().getWidth();
            
            propBandLength = cumulativeColPropBands[ithLabel + 1] - cumulativeColPropBands[ithLabel];
            prop_MiddleIthLabel = 0.5 * (cumulativeColPropBands[ithLabel] + cumulativeColPropBands[ithLabel + 1]);
            pxShiftTo = prop_MiddleIthLabel * paneWidth;

            txtRowOfLabels[ithLabel].setLayoutX(pxShiftTo);
            txtRowOfLabels[ithLabel].setLayoutY(yValueForRowLabels);

            txtRowOfLabels[ithLabel].setFont(xLabelFont);
            Rotate rotate = new Rotate(90, 0, 0);
            txtRowOfLabels[ithLabel].getTransforms().add(rotate);
        }

        double pxLabel_HalfWidth = 0.5 * txtXVarLabel.getLayoutBounds().getWidth();
        pxShiftTo = 0.5 * paneWidth - pxLabel_HalfWidth;

        xVar_LabelStartAt_y = horizontalLinePlacement + roomForScaleValues + 1.00 * roomForLabel;
        xVar_LabelStartAt_x = pxShiftTo;
        
        txtXVarLabel.setLayoutX(xVar_LabelStartAt_x);
        txtXVarLabel.setLayoutY(xVar_LabelStartAt_y);
        txtXVarLabel.setFont(xLabelFont);
        
        txtXVarLabel.setFont(xLabelFont);
        majorTickStart = horizontalLinePlacement;
        minorTickStart = horizontalLinePlacement;
        horizontalLine = new Line(0., horizontalLinePlacement, paneWidth, horizontalLinePlacement);
        horizontalLine.setStroke(Color.BLACK);
        horizontalLine.setStrokeWidth(2.5);
        
        //  **************   Tick Marks & Values  *****************
        majorTickMarks = new Line[11];
        majorTickValues = new Text[11]; 
        minorTickMarks = new Line[10];

        for (int ithTick = 0; ithTick < 11; ithTick++) {
            double dbl_ithTick = ithTick;
            double bigTickXPlacement = 0.1 * paneWidth * dbl_ithTick;
            majorTickMarks[ithTick] = new Line(bigTickXPlacement, majorTickStart, bigTickXPlacement, majorTickStart + majorTickLength);
            majorTickMarks[ithTick].setStroke(Color.BLUE);
            majorTickMarks[ithTick].setStrokeWidth(1.0);
            
            majorTickValues[ithTick] = new Text();
            double daTickValue = (double)ithTick / 10.0;
            String tickString = String.format("%3.2f", daTickValue);
            
            majorTickValues[ithTick].setText(tickString);
            majorTickValues[ithTick].setFont(tickValueFont);
            double startMajorValuesAt = horizontalLinePlacement + majorTickLength + roomForScaleValues;
            majorTickValues[ithTick].setX(bigTickXPlacement - 10.);
            majorTickValues[ithTick].setY(startMajorValuesAt);   
        
            if (ithTick < 10) {
                double smallTickXPlacement = 0.1 * paneWidth * dbl_ithTick + .05 * paneWidth;
                minorTickMarks[ithTick] = new Line( smallTickXPlacement, minorTickStart, smallTickXPlacement, majorTickStart + minorTickLength);
                minorTickMarks[ithTick].setStroke(Color.BLUE);
                minorTickMarks[ithTick].setStrokeWidth(1.0);        
            }
        }

        hPane.getChildren().addAll(txtRowOfLabels);
        hPane.getChildren().addAll(majorTickMarks);
        hPane.getChildren().addAll(minorTickMarks);
        hPane.getChildren().add(horizontalLine);
        hPane.getChildren().add(txtXVarLabel);

        hPane.getChildren().addAll(majorTickValues);
    }
    
    public Pane getHorizontalPane() {return hPane; }
}
