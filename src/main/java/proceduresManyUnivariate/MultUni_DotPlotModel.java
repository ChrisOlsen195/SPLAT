/**************************************************
 *               MultUni_DotPlotModel             *
 *                     01/16/25                   *
 *                      12:00                     *
 *************************************************/
package proceduresManyUnivariate;

import dataObjects.CatQuantDataVariable;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.Point_2D;

public class MultUni_DotPlotModel {  
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double ithBinLow, ithBinHigh, daMin, daMax;
        
    String descriptionOfVariable;
    
    // My classes
    CatQuantDataVariable cqdv;
    Point_2D ithBinLimits;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;
    
    //  POJO FX
        
    public MultUni_DotPlotModel(String genVarDescr, CatQuantDataVariable cqdv) {  
        if (printTheStuff == true) {
            System.out.println("32 *** MultUni_DotPlotModel, Constructing");
        }
        this.cqdv = cqdv;
        descriptionOfVariable = genVarDescr;
        
        //  ********   Create a Quantitative Data Variable   *********
        //  ********         Make an array of doubles        *********
        int nLegalCQPairs = cqdv.getNLegalCQPairs();
        
        double[] arrayOfDoubles = new double[nLegalCQPairs];
        for (int ithDouble = 0; ithDouble < nLegalCQPairs; ithDouble++) {
            arrayOfDoubles[ithDouble] = cqdv.getIthCatQuantPair_Double(ithDouble);
        }
        
        qdv = new QuantitativeDataVariable("Label", "Description", arrayOfDoubles);
        ucdo = new UnivariateContinDataObj("DiffPlot_Model", qdv);
        daMin = ucdo.getMinValue();
        daMax = ucdo.getMaxValue();
        double daRange = daMax - daMin;
        // This should give an initial bin in the range of the data
        ithBinLow = daMin + 0.1 * daRange;
        ithBinHigh = daMin + 0.2 * daRange;
        ithBinLimits = new Point_2D(ithBinLow, ithBinHigh);  
    }
    
    public Point_2D getBinLimits() { return ithBinLimits; }
    public String getDescrOfVar() { return descriptionOfVariable; }
    public CatQuantDataVariable getCQDV() { return cqdv; }  // CatQuant
    public QuantitativeDataVariable getQDV() { return qdv; }    // Quant
    public UnivariateContinDataObj getUCDO()  {return ucdo; }    
}

