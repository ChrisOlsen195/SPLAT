/**************************************************
 *              BivariateContinDataObj            *
 *                    01/05/25                    *
 *                      00:00                     *
 *************************************************/
package dataObjects;

import genericClasses.Point_2D;
import utilityClasses.DataUtilities;
import java.util.ArrayList;
import utilityClasses.MyAlerts;
import splat.*;

public class BivariateContinDataObj {
    // POJOs
    
    boolean hasBadData, rIsCalculated;
    int nOriginalDataPoints, nLegalDataPoints, nMissingDataPoints;
    
    double covariance, correlation, slope, intercept;
    double meanX, meanY, stDevX, stDevY;
    
    double sumOfSquares_xx, sumOfSquares_xy, sumOfSquares_yy, ss_yHatDevSquared;
    
    double[] dbl_xData, dbl_yData, rawResiduals, studentized_Residuals,
             yHat, ith_dev_x, h_sub_i, yHat_Devs_Squared;
    ArrayList<double[]> theMatched;
    
    String xLabel, yLabel, xDescr, yDescr; 
    ArrayList<Point_2D> al_Point2Ds;
    
    // Make empty if no-print
    //String waldoFile = "BivariateContinDataObj";
    String waldoFile = "";
    
    //My classes
    Data_Manager dm;
    
    public BivariateContinDataObj(Data_Manager dm, ArrayList<ColumnOfData> inBivDat) {
        this.dm = dm;
        dm.whereIsWaldo(40, waldoFile, "\n  *** Constructing");
        rIsCalculated = false;
        al_Point2Ds = new ArrayList();
        xLabel = inBivDat.get(0).getVarLabel();
        xDescr = inBivDat.get(0).getVarDescription();
        yLabel = inBivDat.get(1).getVarLabel();
        yDescr = inBivDat.get(1).getVarDescription();
        convertToLegals(inBivDat);
    }
    
    // For ANCOVA object -- no dm
    public BivariateContinDataObj(ArrayList<ColumnOfData> inBivDat) {
        rIsCalculated = false;
        al_Point2Ds = new ArrayList();
        xLabel = inBivDat.get(0).getVarLabel();
        xDescr = inBivDat.get(0).getVarDescription();
        yLabel = inBivDat.get(1).getVarLabel();
        yDescr = inBivDat.get(1).getVarDescription();
        convertToLegals(inBivDat);
    }
    
    private void convertToLegals(ArrayList<ColumnOfData> inBivDat) {
        nOriginalDataPoints = inBivDat.get(0).getNCasesInColumn();
        
        for (int ithPoint = 0; ithPoint < nOriginalDataPoints; ithPoint++) {
            String xTemp = inBivDat.get(0).getStringInIthRow(ithPoint);
            String yTemp = inBivDat.get(1).getStringInIthRow(ithPoint);
            boolean xTempIsDouble = DataUtilities.strIsADouble(xTemp);
            boolean yTempIsDouble = DataUtilities.strIsADouble(yTemp);
            
            if (xTempIsDouble && yTempIsDouble) {
                Point_2D tempPoint = new Point_2D(Double.parseDouble(xTemp), Double.parseDouble(yTemp));
                al_Point2Ds.add(tempPoint);
                nLegalDataPoints++;
            }
            
            //  Check for unequally missing in the two variables            
            if ((xTempIsDouble && !yTempIsDouble) 
                    || (!xTempIsDouble && yTempIsDouble)) {
                hasBadData = true;
            } 
        }
        
        dbl_xData = new double[nLegalDataPoints];
        dbl_yData = new double[nLegalDataPoints];
        for (int ithLegal = 0; ithLegal < nLegalDataPoints; ithLegal++) {
            dbl_xData[ithLegal] = al_Point2Ds.get(ithLegal).getFirstValue();
            dbl_yData[ithLegal] = al_Point2Ds.get(ithLegal).getSecondValue();
        }
        
        nMissingDataPoints = nOriginalDataPoints - nLegalDataPoints;
        if (hasBadData) {  MyAlerts.showUnequalNsInBivariateProcessAlert(); }
    }
 
    public boolean getDataExists() { 
        boolean dataIsOK = (nLegalDataPoints > 0);
        return dataIsOK;
    }
    
    public void continueConstruction() { 
        if (dm != null) {
            dm.whereIsWaldo(101, waldoFile, "\n  --- continueConstruction()");
        }
        calculateStatistics();
    }    
    
    private void calculateStatistics() { //  and covariance;
        if (dm != null) {
            dm.whereIsWaldo(108, waldoFile, "calculateR()");
        }
        
        int ith;
        
        double sumX = 0.0; double sumY = 0.0;
        sumOfSquares_xx = 0.0; sumOfSquares_xy = 0.0; sumOfSquares_yy = 0.0;
       
        for (ith = 0; ith < nLegalDataPoints; ith++) {
            sumX += dbl_xData[ith];
            sumY += dbl_yData[ith];
        }
        
        meanX = sumX / nLegalDataPoints;
        meanY = sumY / nLegalDataPoints;
        
        ith_dev_x = new double[nLegalDataPoints];
        h_sub_i = new double[nLegalDataPoints];
        for (ith = 0; ith < nLegalDataPoints; ith++) {
            double devX = dbl_xData[ith] - meanX;
            ith_dev_x[ith] = devX;
            double devY = dbl_yData[ith] - meanY;
            sumOfSquares_xx += (devX * devX);
            sumOfSquares_yy += (devY * devY);
            sumOfSquares_xy += (devX * devY);
        }
        
        covariance = sumOfSquares_xy / (nLegalDataPoints - 1.);
        stDevX = Math.sqrt(sumOfSquares_xx / (nLegalDataPoints - 1.));
        stDevY = Math.sqrt(sumOfSquares_yy / (nLegalDataPoints - 1.));
        correlation = covariance / (stDevX * stDevY);
        slope  = sumOfSquares_xy / sumOfSquares_xx;
        intercept = meanY - slope * meanX;
        rIsCalculated = true;
        
        yHat = new double[nLegalDataPoints];
        yHat_Devs_Squared = new double[nLegalDataPoints];
        rawResiduals = new double[nLegalDataPoints];
        ss_yHatDevSquared = 0.0;
        for (ith = 0; ith < nLegalDataPoints; ith++) {
            double tempX = dbl_xData[ith];
            double tempY = dbl_yData[ith];
            yHat[ith] = slope * tempX + intercept;
            rawResiduals[ith] = tempY - yHat[ith];
            yHat_Devs_Squared[ith] = (dbl_yData[ith] - yHat[ith]) * (dbl_yData[ith] - yHat[ith]);
            ss_yHatDevSquared += yHat_Devs_Squared[ith];
        }
        
        // Calculate the studentized residuals
        double tempRecip_1 = 1.0 / nLegalDataPoints;
        for (ith = 0; ith < nLegalDataPoints; ith++) {
            h_sub_i[ith] = tempRecip_1 + ith_dev_x[ith] * ith_dev_x[ith] / sumOfSquares_xx; 
        }
        
        studentized_Residuals = new double[nLegalDataPoints];
        double tempRecip_2 = 1.0 / (nLegalDataPoints - 3.0);
        for (ith = 0; ith < nLegalDataPoints; ith++) {
            double ss_with_pointDeleted = tempRecip_2 * (ss_yHatDevSquared - yHat_Devs_Squared[ith]);
            studentized_Residuals[ith] = rawResiduals[ith] / (ss_with_pointDeleted * Math.sqrt(1.0 - h_sub_i[ith]));       
        }
    }
    
    public BivariateContinDataObj getContinDataObj() { return this; } 
    
    public double getCovariance() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return covariance; }
    
    public double getCorrelation() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return correlation; 
    }
    
    public double getSlope() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return slope; 
    }    
    
    public double getIntercept() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return intercept; 
    }
    
    public double getMeanX() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return meanX; 
    }
    
    public double getMeanY() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return meanY; 
    }
    
    public double getStDevX() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return stDevX; 
    }
    
    public double getStDevY() { 
        if (!rIsCalculated) { calculateStatistics(); }
        return stDevY; 
    }

    public String getXLabel() { return xLabel; }
    public String getYLabel() { return yLabel; }
    public String getXDescr() { return xDescr; }
    public String getYDescr() { return yDescr; }
    
    public double[] getXAs_arrayOfDoubles() { return dbl_xData; }   
    public double[] getYAs_arrayOfDoubles() { return dbl_yData; }

    public double[] getRawResiduals() { return rawResiduals; }
    public double[] getStudentizedResiduals() { return studentized_Residuals; }
    
    public int getNLegalDataPoints() { return nLegalDataPoints; }
    public int getNMissingDataPoints() { return nMissingDataPoints; }
    
    public double getIthX(int ith) {return dbl_xData[ith]; }
    public double getIthY(int ith) {return dbl_yData[ith];}
    
    public double getSumOfSquares_XX() { return sumOfSquares_xx; }
    public double getSumOfSquares_XY() { return sumOfSquares_xy; }
    public double getSumOfSquares_YY() { return sumOfSquares_yy; }
     
    public ArrayList<Point_2D> getBivDatAs_AL_Point2Ds() {return al_Point2Ds; }
    
    public String toString() {
        String toReturn = "BivConDataObj String to return";
        System.out.println("\n\n237 BivariateContinDataObj, x/y Labels = " + xLabel + " / " + yLabel);
        System.out.println("238 BCDO, meanX/Y = " + meanX + " / " + meanY);
        System.out.println("239 BCDO, stDevX/Y = " + stDevX + " / " + stDevY);
        System.out.println("240 BCDO, slope / intercept = " + slope + " / " + intercept);

        return toReturn;
    }
}
