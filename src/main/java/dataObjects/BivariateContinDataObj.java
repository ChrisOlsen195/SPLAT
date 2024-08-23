/**************************************************
 *              BivariateContinDataObj            *
 *                    05/24/24                    *
 *                      15:00                     *
 *************************************************/
package dataObjects;

import utilityClasses.DataUtilities;
import java.util.ArrayList;
import utilityClasses.MyAlerts;
import splat.*;

public class BivariateContinDataObj {
    // POJOs
    
    boolean hasBadData, rIsCalculated;
    int nOrigDataPoints, nLegalDataPoints, nMissingDataPoints;
    
    double covariance, correlation, slope, intercept;
    double meanX, meanY, stDevX, stDevY;
    
    double sumOfSquares_xx, sumOfSquares_xy, sumOfSquares_yy, ss_yHatDevSquared;
    
    double[] xDataAsDoubles, yDataAsDoubles, rawResiduals, studentized_Residuals,
             yHat, ith_dev_x, h_sub_i, yHat_Devs_Squared;
    ArrayList<double[]> al_bivDataAsDoubles;  
    
    String xLabel, yLabel;
    String[] xDataAsStrings, yDataAsStrings;
    ArrayList<String> al_xVariable, al_yVariable, al_bivLegalXVariable,
                      al_bivLegalYVariable;
    ArrayList<String[]> al_bivDataAsStrings;
    
    // Make empty if no-print
    //String waldoFile = "BivariateContinDataObj";
    String waldoFile = "";
    
    //My classes
    Data_Manager dm;
    
    public BivariateContinDataObj(Data_Manager dm, ArrayList<ColumnOfData> inBivDat) {
        this.dm = dm;
        dm.whereIsWaldo(43, waldoFile, "\n\nConstructing");
        rIsCalculated = false;
        al_xVariable = new ArrayList<>();
        al_yVariable = new ArrayList<>();
        al_bivLegalXVariable = new ArrayList<>();
        al_bivLegalYVariable = new ArrayList<>();
        al_bivDataAsStrings = new ArrayList<>();
        al_bivDataAsDoubles = new ArrayList<>();
        xLabel = inBivDat.get(0).getVarLabel();
        yLabel = inBivDat.get(1).getVarLabel();
        al_xVariable = inBivDat.get(0).getTheCases_ArrayList();   // Whole column
        al_yVariable = inBivDat.get(1).getTheCases_ArrayList();   //  Whole column
        nOrigDataPoints = al_xVariable.size();      // Whole column
        nLegalDataPoints = 0;
        hasBadData = false;
        
        for (int ithPoint = 0; ithPoint < nOrigDataPoints; ithPoint++) {
            String xTemp = al_xVariable.get(ithPoint);
            String yTemp = al_yVariable.get(ithPoint);
            boolean xTempIsDouble = DataUtilities.strIsADouble(xTemp);
            boolean yTempIsDouble = DataUtilities.strIsADouble(yTemp);
            
            if (xTempIsDouble && yTempIsDouble) {
                al_bivLegalXVariable.add(xTemp);    //  Legal points
                al_bivLegalYVariable.add(yTemp);    //  Legal points
                nLegalDataPoints++;
            }
            
            //  Check for unequally missing in the two variables            
            if ((xTempIsDouble && !yTempIsDouble) 
                    || (!xTempIsDouble && yTempIsDouble)) {
                hasBadData = true;
            }
        }

        nMissingDataPoints = nOrigDataPoints - nLegalDataPoints;
        if (hasBadData) {  MyAlerts.showUnequalNsInBivariateProcessAlert(); }
    }
    
    // For ANCOVA object
    public BivariateContinDataObj(ArrayList<ColumnOfData> inBivDat) {
        //System.out.println("84 *** BivariateContinDataObj, constructing");
        rIsCalculated = false;
        al_xVariable = new ArrayList<>();
        al_yVariable = new ArrayList<>();
        al_bivLegalXVariable = new ArrayList<>();
        al_bivLegalYVariable = new ArrayList<>();
        al_bivDataAsStrings = new ArrayList<>();
        al_bivDataAsDoubles = new ArrayList<>();
        xLabel = inBivDat.get(0).getVarLabel();
        yLabel = inBivDat.get(1).getVarLabel();
        al_xVariable = inBivDat.get(0).getTheCases_ArrayList();   // Whole column
        al_yVariable = inBivDat.get(1).getTheCases_ArrayList();   //  Whole column
        nOrigDataPoints = al_xVariable.size();      // Whole column
        nLegalDataPoints = 0;
        hasBadData = false;
        for (int ithPoint = 0; ithPoint < nOrigDataPoints; ithPoint++) {
            String xTemp = al_xVariable.get(ithPoint);
            String yTemp = al_yVariable.get(ithPoint);
            boolean xTempIsDouble = DataUtilities.strIsADouble(xTemp);
            boolean yTempIsDouble = DataUtilities.strIsADouble(yTemp);
            
            if (xTempIsDouble && yTempIsDouble) {
                al_bivLegalXVariable.add(xTemp);    //  Legal points
                al_bivLegalYVariable.add(yTemp);    //  Legal points
                nLegalDataPoints++;
            }
            
            //  Check for unequally missing in the two variables            
            if ((xTempIsDouble && !yTempIsDouble) 
                    || (!xTempIsDouble && yTempIsDouble)) {
                hasBadData = true;
            }
        }

        nMissingDataPoints = nOrigDataPoints - nLegalDataPoints;
        if (hasBadData) {  MyAlerts.showUnequalNsInBivariateProcessAlert(); }
    }
    
    public boolean getDataExists() { 
        boolean dataIsOK = (nLegalDataPoints > 0);
        return dataIsOK;
    }
    
    public void continueConstruction() { 
        if (dm != null) {
            //System.out.println("129 *** BivariateContinDataObj, continueConstruction()");
        }
        xDataAsStrings = new String[nLegalDataPoints];
        yDataAsStrings = new String[nLegalDataPoints];
        xDataAsDoubles = new double[nLegalDataPoints];
        yDataAsDoubles = new double[nLegalDataPoints];
        
        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            xDataAsStrings[ith] = al_bivLegalXVariable.get(ith);                        // Legal
            yDataAsStrings[ith] = al_bivLegalYVariable.get(ith);                        // Legal
            xDataAsDoubles[ith] = Double.parseDouble(al_bivLegalXVariable.get(ith));    // Legal
            yDataAsDoubles[ith] = Double.parseDouble(al_bivLegalYVariable.get(ith));    // Legal
        }  
        
        al_bivDataAsStrings.add(xDataAsStrings);    // Legal
        al_bivDataAsStrings.add(yDataAsStrings);    // Legal
        al_bivDataAsDoubles.add(xDataAsDoubles);    // Legal
        al_bivDataAsDoubles.add(yDataAsDoubles);    // Legal
        calculateR();
    }    
    
    private void calculateR() { //  and covariance;
        if (dm != null) {
            dm.whereIsWaldo(152, waldoFile, "calculateR()");
        }
        
        int ith;
        
        double sumX = 0.0; double sumY = 0.0;
        sumOfSquares_xx = 0.0; sumOfSquares_xy = 0.0; sumOfSquares_yy = 0.0;
       
        for (ith = 0; ith < nLegalDataPoints; ith++) {
            sumX += xDataAsDoubles[ith];
            sumY += yDataAsDoubles[ith];
        }
        
        meanX = sumX / nLegalDataPoints;
        meanY = sumY / nLegalDataPoints;
        
        ith_dev_x = new double[nLegalDataPoints];
        h_sub_i = new double[nLegalDataPoints];
        for (ith = 0; ith < nLegalDataPoints; ith++) {
            double devX = xDataAsDoubles[ith] - meanX;
            ith_dev_x[ith] = devX;
            double devY = yDataAsDoubles[ith] - meanY;
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
            double tempX = xDataAsDoubles[ith];
            double tempY = yDataAsDoubles[ith];
            yHat[ith] = slope * tempX + intercept;
            rawResiduals[ith] = tempY - yHat[ith];
            yHat_Devs_Squared[ith] = (yDataAsDoubles[ith] - yHat[ith]) * (yDataAsDoubles[ith] - yHat[ith]);
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
        if (!rIsCalculated) { calculateR(); }
        return covariance; }
    
    public double getCorrelation() { 
        if (!rIsCalculated) { calculateR(); }
        return correlation; 
    }
    
    public double getSlope() { 
        if (!rIsCalculated) { calculateR(); }
        return slope; 
    }    
    
    public double getIntercept() { 
        if (!rIsCalculated) { calculateR(); }
        return intercept; 
    }
    
    public double getMeanX() { 
        if (!rIsCalculated) { calculateR(); }
        return meanX; 
    }
    
    public double getMeanY() { 
        if (!rIsCalculated) { calculateR(); }
        return meanY; 
    }
    
    public double getStDevX() { 
        if (!rIsCalculated) { calculateR(); }
        return stDevX; 
    }
    
    public double getStDevY() { 
        if (!rIsCalculated) { calculateR(); }
        return stDevY; 
    }

    public String getXLabel() { return xLabel; }
    public String getYLabel() { return yLabel; }
    
    public double[] getXAs_arrayOfDoubles() {  return xDataAsDoubles;  }    // Legal
    public double[] getYAs_arrayOfDoubles() {  return yDataAsDoubles;  }    // Legal
    public double[] getRawResiduals() { return rawResiduals; }
    public double[] getStudentizedResiduals() { return studentized_Residuals; }
    
    public int getNLegalDataPoints() { return nLegalDataPoints; }
    public int getNMissingDataPoints() { return nMissingDataPoints; }
    
    public double getSumOfSquares_XX() { return sumOfSquares_xx; }
    public double getSumOfSquares_XY() { return sumOfSquares_xy; }
    public double getSumOfSquares_YY() { return sumOfSquares_yy; }
    
    public ArrayList<String> getLegalXsAs_AL_OfStrings() { return al_bivLegalXVariable; }   // Legal
    public ArrayList<String> getLegalYsAs_AL_OfStrings() { return al_bivLegalYVariable; }   // Legal
    public ArrayList<String[]> getBivDataAsStrings() { return al_bivDataAsStrings; }        // Legal
    public ArrayList<double[]> getBivDataAsDoubles() { return al_bivDataAsDoubles; }        // Legal
    
    public String toString() {
        String toReturn = "BivConDataObj String to return";
        System.out.println("\n\n257 BivariateContinDataObj, x/y Labels = " + xLabel + " / " + yLabel);
        System.out.println("258 BCDO, meanX/Y = " + meanX + " / " + meanY);
        System.out.println("259 BCDO, stDevX/Y = " + stDevX + " / " + stDevY);
        System.out.println("260 BCDO, slope / intercept = " + slope + " / " + intercept);

        return toReturn;
    }
}
