/**************************************************
 *              CumulativeFrequency_Model         *
 *                     10/15/23                   *
 *                      21:00                     *
 *************************************************/
package proceduresOneUnivariate;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.Point_2D;
import utilityClasses.DataUtilities;

public class CumulativeFrequency_Model {  
    
    double ithBinLow, ithBinHigh;
    
    String dataLabel, descrOfVar;
    
    // My classes
    //Exploration_Dashboard univ_Dashboard;
    Point_2D ithBinLimits;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;

    public CumulativeFrequency_Model() { }
        
    public CumulativeFrequency_Model(String descriptionOfVariable, QuantitativeDataVariable qdv) {  
        this.qdv = qdv;
        this.descrOfVar = descriptionOfVariable;
        dataLabel = qdv.getTheVarLabel();
        ucdo = new UnivariateContinDataObj("CumulativeFrequency_Model", qdv);
        
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

