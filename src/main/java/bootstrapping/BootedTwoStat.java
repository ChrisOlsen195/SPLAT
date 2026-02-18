/**********************************************************************
 *                           BootedTwoStat                            *
 *                             12/31/25                               *
 *                               18:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import dataObjects.*;
import utilityClasses.DataUtilities;

public class BootedTwoStat {
    Boolean statChosen;
    Boolean[] checkBoxValues;
    
    int nLegalFirst, nLegalSecond, nTotal, nCheckBoxes, nReplications, 
        theStatIndex, theStatisticToCalculate;

    double[] ar_DataFirst, ar_DataSecond, ar_OriginalDataCombined, ar_bootstrappedStat;
    double[] ar_slicedSample_First, ar_slicedSample_Second;
    String returnStatus, chosenStat, tidyOrTI8x, theStatLabel;
    String[] cbArrStatDescriptions;
    
    // Make empty if no-print
    //String waldoFile = "BootedTwoStat";
    String waldoFile = "";

    ArrayList<Integer> theStatsToCalculate;
    ArrayList<String> theLabels;

    Data_Manager dm;
    QuantitativeDataVariable qdv_FirstSlice, qdv_SecondSlice, qdv_CombinedData,
                             qdv_bootstrappedStats;
    
    public BootedTwoStat(ChooseStats_Controller chooseStats_Controller, ArrayList <ColumnOfData> alCol_twoVars) {
        dm = chooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(40, waldoFile, "***  Constructing"); 
        returnStatus = "OK";
        tidyOrTI8x = chooseStats_Controller.getTidyOrTI8x();
        if (tidyOrTI8x.equals("TI8x")) {
            //alCol_twoVars.get(0).toString();
            //alCol_twoVars.get(1).toString();

            nLegalFirst = alCol_twoVars.get(0).getNLegalQuantCasesInColumn();
            ar_DataFirst = new double[nLegalFirst];
            ar_DataFirst = alCol_twoVars.get(0).getLegalCases_asDoubles();

            //
            nLegalSecond = alCol_twoVars.get(1).getNLegalQuantCasesInColumn();
            ar_DataSecond = new double[nLegalSecond];
            ar_DataSecond = alCol_twoVars.get(1).getLegalCases_asDoubles();
            nTotal = nLegalFirst + nLegalSecond;
        } else {
            //alCol_twoVars.get(0).toString();
            //alCol_twoVars.get(1).toString();
            
            nTotal = alCol_twoVars.get(0).getNCasesInColumn();
            for (int ithValue = 0; ithValue < nTotal; ithValue++) {
                String ithString = alCol_twoVars.get(0).getIthCase(ithValue);
                String jthString = alCol_twoVars.get(0).getIthCase(ithValue + 1);
                if (!ithString.equals(jthString)) {
                    nLegalFirst = ithValue;
                    System.out.println("67 BootedTwoStat, nLegalFirst = " + nLegalFirst);
                    break;
                }
            }
            nLegalSecond = nTotal - nLegalFirst;
            System.out.println("72 BootedTwoStat, nLegalFirst = " + nLegalSecond);   
            
            ar_DataFirst = new double[nLegalFirst];
            ar_DataSecond = new double[nLegalSecond];
            
            for (int ithValue = 0; ithValue < nLegalFirst; ithValue++) {
                ar_DataFirst[ithValue] = Double.parseDouble(alCol_twoVars.get(1).getIthCase(ithValue));
            }   
            for (int ithValue = 0; ithValue < nLegalSecond; ithValue++) {
                ar_DataSecond[ithValue] = Double.parseDouble(alCol_twoVars.get(1).getIthCase(nLegalFirst + ithValue));
            }
        }
        ar_OriginalDataCombined = new double[nTotal];
        
        System.arraycopy(ar_DataFirst, 0, ar_OriginalDataCombined, 0, nLegalFirst);
        
        System.arraycopy(ar_DataSecond, 0, ar_OriginalDataCombined, nLegalFirst, nLegalSecond);
        
        //DataUtilities.printArrayOfDoubles("89 BootedTwoStat, ar_DataCombined", ar_OriginalDataCombined);
        
        cbArrStatDescriptions = chooseStats_Controller.getRepAndStatCheckBoxDescriptions();      
        cbArrStatDescriptions = chooseStats_Controller.getRepAndStatCheckBoxDescriptions();
        nCheckBoxes = chooseStats_Controller.getNCheckBoxes();
        checkBoxValues = new Boolean[nCheckBoxes];
        checkBoxValues = chooseStats_Controller.getRepAndStatCheckBoxValues();
        nCheckBoxes = checkBoxValues.length;
        qdv_CombinedData = new QuantitativeDataVariable("BootedStat", "BootedStat", ar_OriginalDataCombined);
        nReplications = chooseStats_Controller.getNReps();        
    }
    
    // Called from Controller
    public String constructTheBootstrapSample() {  
        dm.whereIsWaldo(103, waldoFile, "--- constructTheBootstrapSample()"); 
        determineTheStatisticsToCalculate();
        if (!statChosen) {
            MyAlerts.showZeroStatsChosenAlert();
            returnStatus = "Cancel";
            return returnStatus;
        }
        
        //  *****************   Put them together here  *********************
        ar_bootstrappedStat = new double[nReplications];
        for (int ithReplication = 0; ithReplication < nReplications; ithReplication++) {
            sliceTheData();  
            // Calculate one bootstrapped stat
            ar_bootstrappedStat[ithReplication] = calculateTheStatistic();
        }    
        qdv_bootstrappedStats = new QuantitativeDataVariable("sliced", "sliced", ar_bootstrappedStat);
        dm.whereIsWaldo(119, waldoFile, "--- End constructTheBootstrapSample()"); 
        return returnStatus;
    } 
    
    private void determineTheStatisticsToCalculate() {
        dm.whereIsWaldo(124, waldoFile, "---  determineTheStatisticsToCalculate()");             
        for (int ithCBox = 0; ithCBox < nCheckBoxes; ithCBox++) {
            if (checkBoxValues[ithCBox] == true) {  //  If this statistic is desired
                theStatisticToCalculate = ithCBox;
                theStatLabel = cbArrStatDescriptions[ithCBox]; 
                statChosen = true;
            }
        }   
        dm.whereIsWaldo(132, waldoFile, "---  End of determineTheStatisticsToCalculate()");
    }
    
    private void sliceTheData() {
        dm.whereIsWaldo(136, waldoFile, "--- sliceTheData()");
        DataUtilities.arrayShuffle(ar_OriginalDataCombined);
        //DataUtilities.printArrayOfDoubles("Sliced original combined", ar_OriginalDataCombined);
        ar_slicedSample_First = new double[nLegalFirst];
        ar_slicedSample_Second = new double[nLegalSecond];
        
        System.arraycopy(ar_OriginalDataCombined, 0, ar_slicedSample_First, 0, nLegalFirst);
        
        //DataUtilities.printArrayOfDoubles("Slice first", ar_slicedSample_First);

        for (int ith = 0; ith < nLegalSecond; ith++) {
            ar_slicedSample_Second[ith] = ar_OriginalDataCombined[ith + nLegalFirst];
        }
        
        //DataUtilities.printArrayOfDoubles("Slice second", ar_slicedSample_Second);
        
        qdv_FirstSlice = new QuantitativeDataVariable("FirstSlice", "FirstSlice", ar_slicedSample_First);
        qdv_SecondSlice = new QuantitativeDataVariable("SecondSlice", "SecondSlice", ar_slicedSample_Second);     
        dm.whereIsWaldo(154, waldoFile, "--- End sliceTheData()");
    }
    
    private double calculateTheStatistic() {
        //dm.whereIsWaldo(158, waldoFile, ---  calculateTheStatistic()");
        double theStat = 0.0;   // Happy compiler
            switch(theStatisticToCalculate) {
                case 0:
                    theStat = qdv_FirstSlice.getTheMean() - qdv_SecondSlice.getTheMean();
                    chosenStat = "The mean";
                    break;

                case 1:
                    // calculate the variance
                    theStat = qdv_FirstSlice.getTheVariance() - qdv_SecondSlice.getTheVariance();
                    chosenStat = "The variance";
                    break;

                case 2:
                    // calculate the stDev
                    theStat = qdv_FirstSlice.getTheStandDev() - qdv_SecondSlice.getTheStandDev();
                    chosenStat = "The standard deviation";
                    break;

                case 3:
                    // calculate the skew
                    theStat = qdv_FirstSlice.getTheSkew() - qdv_SecondSlice.getTheSkew();
                    chosenStat = "The skew";
                    break;       

                case 4:
                    // calculate the Trimmed mean
                    theStat = qdv_FirstSlice.getTheTrimmedMean(0.05) - qdv_SecondSlice.getTheTrimmedMean(0.05);
                    chosenStat = "The trimmed mean";
                    break;

                case 5:
                    // calculate the Kurtosis
                    theStat = qdv_FirstSlice.getTheKurtosis() - qdv_SecondSlice.getTheKurtosis();
                    chosenStat = "The kurtosis";
                    break;

                case 6:
                    // calculate the CV
                    theStat = qdv_FirstSlice.getTheCV() - qdv_SecondSlice.getTheCV();
                    chosenStat = "The coefficient of variation";
                    break;

                case 7:
                    // calculate the min
                    theStat = qdv_FirstSlice.getMinValue() - qdv_SecondSlice.getMinValue();
                    chosenStat = "The minimum value";
                    break;    

                case 8:
                    // calculate tQ1
                    theStat = qdv_FirstSlice.getTheQ1() - qdv_SecondSlice.getTheQ1();
                    chosenStat = "The first quartile";
                    break;

                case 9:
                    // calculate the median
                    theStat = qdv_FirstSlice.getTheMedian() - qdv_SecondSlice.getTheMedian();
                    chosenStat = "The median";
                    break;  

                case 10:
                    // calculate Q3
                    theStat = qdv_FirstSlice.getTheQ3() - qdv_SecondSlice.getTheQ3();
                    chosenStat = "The third quartile";
                    break;

                case 11:
                    // calculate the max
                    theStat = qdv_FirstSlice.getMaxValue() - qdv_SecondSlice.getMaxValue();
                    chosenStat = "The maximum";
                    break;

                case 12:
                    // calculate the IQR
                    theStat = qdv_FirstSlice.getTheIQR() - qdv_SecondSlice.getTheIQR();
                    chosenStat = "The interquartile range";
                    break;

                case 13:
                    // calculate the range
                    theStat = qdv_FirstSlice.getTheRange() - qdv_SecondSlice.getTheRange();
                    chosenStat = "The range";
                    break;       

                case 14:
                    // calculate the Tri-mean
                    theStat = qdv_FirstSlice.getTheTriMean() - qdv_SecondSlice.getTheTriMean();
                    chosenStat = "The tri-mean";
                    break;

                default:
                String switchFailure = "Switch failure: BootTheChosenStat 237" + String.valueOf(theStatIndex);
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }   //  End switch   
        
        return theStat;
    }  
    
    public String getTheChosenStat() { return chosenStat; }
    
    public QuantitativeDataVariable getTheQDV() { 
        return qdv_bootstrappedStats; 
    }
}