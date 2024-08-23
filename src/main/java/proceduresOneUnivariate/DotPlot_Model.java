/**************************************************
 *                   DotPlotModel                 *
 *                     04/25/24                   *
 *                      15:00                     *
 *************************************************/
package proceduresOneUnivariate;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.Point_2D;
import utilityClasses.DataUtilities;

public class DotPlot_Model {  
    // POJOs
    
    double ithBinLow, ithBinHigh;
    double daMin, daMax;
        
    String descrOfVar;
    
    // My classes
    Point_2D ithBinLimits;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;

    public DotPlot_Model() { }
        
    public DotPlot_Model(String descriptionOfVariable, QuantitativeDataVariable qdv) {  
        //System.out.println("29 DotPlot_Model, constructing");
        this.qdv = qdv;
        this.descrOfVar = descriptionOfVariable;

        ucdo = new UnivariateContinDataObj("DotPlot_Model", qdv);
        double dataMin = ucdo.getMinValue();
        double dataMax = ucdo.getMaxValue();
        
        Point_2D scaleRange = DataUtilities.makeAScaleIntervalFor(dataMin, dataMax);

        double daRange = scaleRange.getSecondValue() - scaleRange.getFirstValue();
        ithBinLow = scaleRange.getFirstValue() + 0.5 * daRange;
        ithBinHigh = scaleRange.getFirstValue() + 0.6 * daRange;
        ithBinLimits = new Point_2D(ithBinLow, ithBinHigh); 
    }
    
    public Point_2D getBinLimits() { return ithBinLimits; }
    public String getDescrOfVar() { return descrOfVar; }
    public QuantitativeDataVariable getQDV() { return qdv; }
    public UnivariateContinDataObj getUCDO()  {return ucdo; }    
}

