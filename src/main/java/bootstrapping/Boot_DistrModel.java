/**************************************************
 *                  Boot_DistrModel               *
 *                     01/08/25                   *
 *                      15:00                     *
 *************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import genericClasses.Point_2D;
import splat.Data_Manager;

public class Boot_DistrModel {  
    // POJOs
    boolean printTheStuff = false;
    boolean leftTail_IsChecked, twoTail_IsChecked, rightTail_IsChecked;
    boolean shadeLeft, shadeRight;
    double ithBinLow, ithBinHigh;
    
    String descrOfVar;
    
    // My classes
    Data_Manager dm;
    NonGenericBootstrap_Info nonGen;
    Point_2D binLimits, percentiles_05, percentiles, percentileRanks;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;

    public Boot_DistrModel() { }
        
    public Boot_DistrModel(NonGenericBootstrap_Info nonGen,
                           QuantitativeDataVariable qdv ) {  
        if (printTheStuff) {
            System.out.println("34 *** Boot_DistrModel, Constructing");
        } 
        this.qdv = qdv;
        this.nonGen = nonGen;
        ucdo = new UnivariateContinDataObj("Boot_DistrModel", qdv);
        
        double daMin = ucdo.getMinValue();
        double daMax = ucdo.getMaxValue();
        double leftPercentile = ucdo.fromPercentileRank_toPercentile(.05);
        double rightPercentile = ucdo.fromPercentileRank_toPercentile(0.95);
        
        percentiles_05 = new Point_2D(leftPercentile, leftPercentile);
        double leftPCRank = ucdo.fromPercentile_toPercentileRank(leftPercentile);
        double rightPCRank = ucdo.fromPercentile_toPercentileRank(rightPercentile);
        percentiles = new Point_2D(leftPercentile, rightPercentile);
        percentileRanks = new Point_2D(leftPCRank, rightPCRank);
        double daRange = daMax - daMin;
        ithBinLow = daMin + 0.4 * daRange;
        ithBinHigh = daMin + 0.6 * daRange;
        binLimits = new Point_2D(ithBinLow, ithBinHigh);  
        nonGen.setBinLimits(binLimits);
    }
        
    public Boot_DistrModel getBootStrap_DistrModel() { return this; }
    public Point_2D getBinLimits() { return binLimits; }
    public Point_2D get_05_Percentiles() {return percentiles_05; }
    
    public Point_2D get_Percentiles() {
        return percentiles; }
    
    public void set_Percentiles(Point_2D toThese) {
        percentiles.setFirstValue(toThese.getFirstValue());
        percentiles.setSecondValue(toThese.getSecondValue());
    }
    
    // Shading does not always agree with check boxes;
    public boolean get_ShadeLeft() { return shadeLeft; }
    
    public void set_ShadeLeft(boolean toThis) {
        shadeLeft = toThis;
    }
    
    public boolean get_ShadeRight() { return shadeRight; }
    
    public void set_ShadeRight(boolean toThis) {
        shadeRight = toThis;
    } 
    
    public double get_LeftPercentile() { 
        //System.out.println("78 *** Boot_1Stat_Distr_Model, getLeftPercentile = " + percentiles.getFirstValue());        
        return percentiles.getFirstValue(); }
    
    public void set_LeftPercentile( double toThis) {
        percentiles.setFirstValue(toThis);
    }
    
    public double get_RightPercentile() { 
        return percentiles.getSecondValue(); }
    
    public void set_RightPercentile( double toThis) {
        percentiles.setSecondValue(toThis);
    }
        
    public boolean get_LeftTail_IsChecked() { return leftTail_IsChecked; }
    public void set_LeftTail_IsChecked(boolean toThis) { 
        leftTail_IsChecked = toThis; 
        shadeLeft = toThis;
    }
    
    public boolean get_TwoTail_IsChecked() { return twoTail_IsChecked; }  
    public void set_TwoTail_IsChecked(boolean toThis) { 
        twoTail_IsChecked = toThis; 
        shadeLeft = toThis;
        shadeRight = toThis;
    }
    public boolean get_RightTail_IsChecked() { return rightTail_IsChecked; }
    public void set_RightTail_IsChecked(boolean toThis) { 
        rightTail_IsChecked = toThis; 
        shadeRight = toThis;
    }
    
    public String getDescrOfVar() { return descrOfVar; }
    public QuantitativeDataVariable getTheQDV() { return qdv; }
    public UnivariateContinDataObj getUCDO()  {return ucdo; }
    public Data_Manager getDataManager() { return dm; }
    public NonGenericBootstrap_Info getNonGen() { return nonGen; }
}

