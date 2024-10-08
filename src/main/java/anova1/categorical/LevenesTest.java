/**************************************************
 *                   LevenesTest                  *
 *                    11/01/23                    *
 *                      21:00                     *
 *************************************************/

// ***********************************************************************
// *  https://www.itl.nist.gov/div898/handbook/eda/section3/eda35a.htm   *
// ***********************************************************************  
package anova1.categorical;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import probabilityDistributions.*;

public class LevenesTest {
    
    private int nGroups, totalN;
    private int[] legalN;
    private double grandSumZIJ, grandMeanZIJ, levenes_W, sumYIJ, levenes_W_PValue,
                   brownForsyth_Stat, brownForsyth_PValue,
                   levenes_W_Trimmed;
    private double[] groupMean, groupMedian, trimmedMean;
    private double[] zBarI;
    private double[][] yIJ, zIJ;
    
ArrayList<QuantitativeDataVariable> allTheQDVs;    

    public LevenesTest(ArrayList<QuantitativeDataVariable> allTheQDVs) {
        this.allTheQDVs = allTheQDVs;
        totalN = allTheQDVs.get(0).getLegalN();
        nGroups = allTheQDVs.size() - 1;
        legalN = new int[nGroups + 1];
        groupMean = new double[nGroups + 1];
        groupMedian = new double[nGroups + 1];
        trimmedMean = new double[nGroups + 1];
        zBarI = new double[nGroups + 1];   //  0 not used
        sumYIJ = 0;

        for (int qdv = 0; qdv <= nGroups; qdv++) {
            legalN[qdv] = allTheQDVs.get(qdv).getLegalN();
            groupMean[qdv] = allTheQDVs.get(qdv).getTheMean();
            groupMedian[qdv] = allTheQDVs.get(qdv).getTheMedian();
            // Hard coded for 10% trimming until further guidance and research
            trimmedMean[qdv] = allTheQDVs.get(qdv).getTheTrimmedMean(0.10);
        }
        
        int largestGroupSize = 0;        
        for (int qdv = 1; qdv <= nGroups; qdv++) {
            largestGroupSize = Math.max(largestGroupSize, legalN[qdv]);
        }
        
        yIJ = new double[nGroups + 1][largestGroupSize];
        zIJ = new double[nGroups + 1][largestGroupSize];
        
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {            
            for(int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
                yIJ[ithGroup][jthElement] = allTheQDVs.get(ithGroup)
                                                      .getIthDataPtAsDouble(jthElement);
                sumYIJ += yIJ[ithGroup][jthElement];   
            }
           
        }
        
        FDistribution fDist = new FDistribution(nGroups - 1, totalN - nGroups);
        brownForsyth_Stat = doModifiedLevene();
        brownForsyth_PValue = fDist.getRightTailArea(brownForsyth_Stat);
        
        levenes_W = doLevenesForMeans();
        levenes_W_PValue = fDist.getRightTailArea(levenes_W);
        
        // Trimming for Levenes is hard coded for 10% trim until guidance discovered
        levenes_W_Trimmed = doLevenesForTrimmedMeans(0.10);       
    }
    
   private double doLevenesForMeans() {
        grandSumZIJ = 0.0;
        grandMeanZIJ = 0.0;
        
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {
            double tempSumZIJ = 0.0;            
            for(int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
                zIJ[ithGroup][jthElement] = Math.abs(yIJ[ithGroup][jthElement] - groupMean[ithGroup]);
                tempSumZIJ += zIJ[ithGroup][jthElement];
                grandSumZIJ += zIJ[ithGroup][jthElement];
            }            
            zBarI[ithGroup] = tempSumZIJ / legalN[ithGroup];
        }  
        
        grandMeanZIJ = grandSumZIJ / legalN[0];

        double numerSum = 0.0;
        double denomSum = 0.0;
       
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {
            double temp1 = zBarI[ithGroup] - grandMeanZIJ;
            numerSum += legalN[ithGroup] * temp1 * temp1;
        }
        
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {            
           for (int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
               double temp2 = zIJ[ithGroup][jthElement] - zBarI[ithGroup];
               denomSum += temp2 * temp2;
           }           
        }  

       double W = (totalN - nGroups) * numerSum / ((nGroups - 1) * denomSum);
       return W;
    }

   // ***********************************************************************
   // *     Brown-Forsythe agrees with Kirk, Experimental Design (3rd)      *
   // *                     12/16/18                                        *
   // ***********************************************************************
   // ***********************************************************************
   // * Brown, M. & Forsyth, A.  Robust Tests for the Equality of Variances *
   // * (1974).  Journal of the American Statistical Association Vol 69,.   *
   // * No 346. p364-367.                                                   *
   // ***********************************************************************
   
   private double doModifiedLevene() {  //  Brown-Forsythe
        grandSumZIJ = 0.0;
        grandMeanZIJ = 0.0;        
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {
            double tempSumZIJ = 0.0;            
            for(int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
                zIJ[ithGroup][jthElement] = Math.abs(yIJ[ithGroup][jthElement] - groupMedian[ithGroup]);
                tempSumZIJ += zIJ[ithGroup][jthElement];
                grandSumZIJ += zIJ[ithGroup][jthElement];
            }            
            zBarI[ithGroup] = tempSumZIJ / legalN[ithGroup];
        }  
        
        grandMeanZIJ = grandSumZIJ / legalN[0];

        double numerSum = 0.0;
        double denomSum = 0.0;
       
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {
            double temp1 = zBarI[ithGroup] - grandMeanZIJ;
            numerSum += legalN[ithGroup] * temp1 * temp1;
        }
        
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {            
           for (int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
               double temp2 = zIJ[ithGroup][jthElement] - zBarI[ithGroup];
               denomSum += temp2 * temp2;
           }           
        }   
        
       double W = (totalN - nGroups) * numerSum / ((nGroups - 1) * denomSum);
       return W;
       }


    private double doLevenesForTrimmedMeans(double trimProp) { 
        grandSumZIJ = 0.0;
        grandMeanZIJ = 0.0;
        
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {            
            double tempSumZIJ = 0.0;            
            for(int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
                zIJ[ithGroup][jthElement] = Math.abs(yIJ[ithGroup][jthElement] - trimmedMean[ithGroup]);
                tempSumZIJ += zIJ[ithGroup][jthElement];
                grandSumZIJ += zIJ[ithGroup][jthElement];
            }            
            zBarI[ithGroup] = tempSumZIJ / legalN[ithGroup];
        } 
        
        grandMeanZIJ = grandSumZIJ / legalN[0];

       double numerSum = 0.0;
       double denomSum = 0.0;
       
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {
            double temp1 = zBarI[ithGroup] - grandMeanZIJ;
            numerSum += legalN[ithGroup] * temp1 * temp1;
        }
        
        for (int ithGroup = 1; ithGroup <= nGroups; ithGroup++) {
           for (int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
               double temp2 = zIJ[ithGroup][jthElement] - zBarI[ithGroup];
               denomSum += temp2 * temp2;
           }
        }   
        
       double W = (totalN - nGroups) * numerSum / ((nGroups - 1) * denomSum);
       return W;        
    }
    
    public double getLevenes_W() { return brownForsyth_Stat; }
    public double getLevenes_PValue() { return brownForsyth_PValue; }
}
