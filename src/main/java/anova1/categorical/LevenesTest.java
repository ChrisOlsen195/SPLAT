/**************************************************
 *                   LevenesTest                  *
 *                    05/12/25                    *
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
    
    // Make empty if no-print
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private int nGroups, totalN;
    private int[] legalN;
    private double grandSumZIJ, kirk_A, kirk_AS, grandMeanZIJ, levenes_W, 
                   sumYIJ, brownForsyth_Stat, 
                   brownForsyth_PValue, levenes_W_Trimmed;
    
    double kirk_SSBG, kirk_MSBG, kirk_MSWG, kirk_SSWG, kirk_F;
    private double[] groupMean, groupMedian, trimmedMean, sumZIJ_Group;
    private double[] zBarI;
    private double[][] yIJ, zIJ;
    
ArrayList<QuantitativeDataVariable> allTheQDVs;    

    public LevenesTest(ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("35 *** Levene's W, Constructing");
        }
        this.allTheQDVs = allTheQDVs;
        nGroups = allTheQDVs.size();
        totalN = 0;
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            totalN += allTheQDVs.get(ithGroup).getLegalN();
        }
        if (printTheStuff == true) {
            System.out.println("46 --- Levene's W, Constructing totalN = " + totalN);
        }
        legalN = new int[nGroups];
        groupMean = new double[nGroups];
        groupMedian = new double[nGroups];
        trimmedMean = new double[nGroups];
        sumZIJ_Group = new double[nGroups];
        
        zBarI = new double[nGroups];
        sumYIJ = 0;

        for (int qdv = 0; qdv < nGroups; qdv++) {
            //allTheQDVs.get(qdv).toString();  // ************************
            legalN[qdv] = allTheQDVs.get(qdv).getLegalN();
            groupMean[qdv] = allTheQDVs.get(qdv).getTheMean();
            groupMedian[qdv] = allTheQDVs.get(qdv).getTheMedian();
            // Hard coded for 10% trimming until further guidance and research
            trimmedMean[qdv] = allTheQDVs.get(qdv).getTheTrimmedMean(0.10);
        }
        
        int largestGroupSize = 0;        
        for (int qdv = 0; qdv < nGroups; qdv++) {
            largestGroupSize = Math.max(largestGroupSize, legalN[qdv]);
        }
        
        yIJ = new double[nGroups][largestGroupSize];
        zIJ = new double[nGroups][largestGroupSize];
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {            
            for(int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
                yIJ[ithGroup][jthElement] = allTheQDVs.get(ithGroup)
                                                      .getIthDataPtAsDouble(jthElement);
                sumYIJ += yIJ[ithGroup][jthElement];   
            }      
        }
        
        FDistribution fDist = new FDistribution(nGroups - 1, totalN - nGroups);
        brownForsyth_Stat = doModifiedLevene();
        brownForsyth_PValue = fDist.getRightTailArea(brownForsyth_Stat);
        
        //levenes_W = doLevenesForMeans();
        //levenes_W_PValue = fDist.getRightTailArea(levenes_W);
        
        // Trimming for Levenes is hard coded for 10% trim until guidance discovered
        //levenes_W_Trimmed = doLevenesForTrimmedMeans(0.10); 
        //levenes_W_PValue = fDist.getRightTailArea(levenes_W);
    }
    
   private double doLevenesForMeans() {
       if (printTheStuff) {
            System.out.println("108 *** Levene's W with Means, doLevenesForMeans()");
        }
        grandSumZIJ = 0.0;
        grandMeanZIJ = 0.0;
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
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
       
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            double temp1 = zBarI[ithGroup] - grandMeanZIJ;
            numerSum += legalN[ithGroup] * temp1 * temp1;
        }
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {            
           for (int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
               double temp2 = zIJ[ithGroup][jthElement] - zBarI[ithGroup];
               denomSum += temp2 * temp2;
           }           
        }  

       double W = (totalN - nGroups) * numerSum / ((nGroups - 1) * denomSum);
       return W;
    }

   // ***********************************************************************
   // *     Brown-Forsythe agrees with Kirk, Experimental Design (4th),     *
   // *             and Cannon, et al.  STAT2 (2nd)                         *   
   // *                     05/12/25                                        *
   // ***********************************************************************
   // ***********************************************************************
   // * Brown, M. & Forsyth, A.  Robust Tests for the Equality of Variances *
   // * (1974).  Journal of the American Statistical Association Vol 69,.   *
   // * No 346. p364-367.                                                   *
   // ***********************************************************************
   
   private double doModifiedLevene() {  //  Brown-Forsythe
       if (printTheStuff) {
            System.out.println("162 *** Levene's W with Medians, doModifiedLevene()");
        }
        grandSumZIJ = 0.0;
        kirk_A = 0.0;
        kirk_AS = 0;
        grandMeanZIJ = 0.0;        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            double tempSumZIJ = 0.0; 
            sumZIJ_Group[ithGroup] = 0.0;
            for(int jthElement = 0; jthElement < legalN[ithGroup]; jthElement++) {
                zIJ[ithGroup][jthElement] = Math.abs(yIJ[ithGroup][jthElement] - groupMedian[ithGroup]);
                double tempyWempy = zIJ[ithGroup][jthElement];
                sumZIJ_Group[ithGroup] += tempyWempy;
                tempSumZIJ += tempyWempy;
                grandSumZIJ += tempyWempy;
                kirk_AS += (tempyWempy * tempyWempy);
            }            
            zBarI[ithGroup] = tempSumZIJ / legalN[ithGroup];
            kirk_A += (sumZIJ_Group[ithGroup] * sumZIJ_Group[ithGroup] / legalN[ithGroup]);
        }  
        
        grandMeanZIJ = grandSumZIJ / legalN[0];
        
        double kirk_Z = grandSumZIJ * grandSumZIJ / totalN;

        kirk_SSBG = kirk_A - kirk_Z;
        kirk_SSWG = kirk_AS - kirk_A;
        kirk_MSBG = kirk_SSBG / ( nGroups - 1);
        kirk_MSWG = kirk_SSWG / (totalN - nGroups);
        
       double W = kirk_MSBG / kirk_MSWG;
       return W;
       }

    private double doLevenesForTrimmedMeans(double trimProp) { 
       if (printTheStuff == true) {
            System.out.println("181 *** Levene's W, doLevenesForTrimmedMeans(double trimProp)");
        }
        grandSumZIJ = 0.0;
        grandMeanZIJ = 0.0;
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {            
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
       
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            double temp1 = zBarI[ithGroup] - grandMeanZIJ;
            numerSum += legalN[ithGroup] * temp1 * temp1;
        }
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
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
