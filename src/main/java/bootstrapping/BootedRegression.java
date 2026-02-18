/**********************************************************************
 *                          BootedRegression                            *
 *                             08/27/25                               *
 *                               21:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import dataObjects.*;

public class BootedRegression {
    Boolean statChosen;
    Boolean[] checkBoxValues;
    
    int nCheckBoxes, nReplications, theStatisticToCalculate;
    double sampleSlope;
    double[] ar_bootstrappedStat;
    String returnStatus, theStatLabel;
    String[] cbArrStatDescriptions;
    
    // Make empty if no-print
    String waldoFile = "BootedRegression";
    //String waldoFile = "";

    ArrayList <ColumnOfData> alCol_regrData, alCol_LegalPairs;

    BivariateContinDataObj bcdo_original;
    Data_Manager dm;
    QuantitativeDataVariable qdv_FirstSlice, qdv_SecondSlice, qdv_CombinedData,
                             qdv_bootstrappedStats;
    
    public BootedRegression(ChooseStats_Controller chooseStats_Controller, ArrayList <ColumnOfData> alCol_regrData) {
        this.dm = chooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(37, waldoFile, "***  Constructing"); 
        this.alCol_regrData = alCol_regrData;
        returnStatus = "OK";
        //alCol_regrData.get(0).toString();
        //alCol_regrData.get(1).toString();
        
        bcdo_original = new BivariateContinDataObj(dm, alCol_regrData);
        bcdo_original.continueConstruction();
        sampleSlope = bcdo_original.getSlope();
        System.out.println("46 BootedRegression, sampleSlope = " + sampleSlope);
        dm.whereIsWaldo(44, waldoFile, "... bcdo Original..."); 
        //bcdo_original.toString();
        cbArrStatDescriptions = chooseStats_Controller.getRepAndStatCheckBoxDescriptions();      
        cbArrStatDescriptions = chooseStats_Controller.getRepAndStatCheckBoxDescriptions();
        nCheckBoxes = chooseStats_Controller.getNCheckBoxes();
        checkBoxValues = new Boolean[nCheckBoxes];
        checkBoxValues = chooseStats_Controller.getRepAndStatCheckBoxValues();
        nCheckBoxes = checkBoxValues.length;
        nReplications = chooseStats_Controller.getNReps();     
        System.out.println("53 BootedRegression, nReplications = " + nReplications);
        alCol_LegalPairs = new ArrayList();
        alCol_LegalPairs.add(new ColumnOfData("xVarLabel", "xVarDescr", bcdo_original.getLegalXsAs_AL_OfStrings()));
        alCol_LegalPairs.add(new ColumnOfData("yVarLabel", "yVarDescr", bcdo_original.getLegalYsAs_AL_OfStrings()));
        //dm.whereIsWaldo(57, waldoFile, "... Legal pairs toString()");
        //alCol_LegalPairs.get(0).toString();
        //alCol_LegalPairs.get(1).toString();
        dm.whereIsWaldo(60, waldoFile, "***  End Constructing"); 
    }
    
    // Called from Controller
    public String constructTheBootstrapSample() {  
        dm.whereIsWaldo(64, waldoFile, "--- constructTheBootstrapSample()"); 
        determineTheStatisticsToCalculate();
        if (!statChosen) {
            MyAlerts.showZeroStatsChosenAlert();
            returnStatus = "Cancel";
            return returnStatus;
        }

        ar_bootstrappedStat = new double[nReplications];

        dm.whereIsWaldo(74, waldoFile, "... Gimme a bcdo!!!");
        
        alCol_LegalPairs = new ArrayList();
        alCol_LegalPairs.add(new ColumnOfData("xVar", "yVar", bcdo_original.getLegalXsAs_AL_OfStrings()));
        alCol_LegalPairs.add(new ColumnOfData("xVar", "yVar", bcdo_original.getLegalYsAs_AL_OfStrings()));
        dm.whereIsWaldo(79, waldoFile, "... Legal pairs toString()");
        //alCol_LegalPairs.get(0).toString();
        //alCol_LegalPairs.get(1).toString();
        
        for (int ithReplication = 0; ithReplication < nReplications; ithReplication++) {
            // Randomize the Y's 
            alCol_LegalPairs.get(1).randomizeTheCases();
            BivariateContinDataObj tempBCDO = new BivariateContinDataObj(dm, alCol_LegalPairs); 
            tempBCDO.continueConstruction();
            ar_bootstrappedStat[ithReplication] = tempBCDO.getSlope();
            //System.out.println("90 BootedRegression, ar_bootstrappedStat[ithReplication] = " + ar_bootstrappedStat[ithReplication]);
        }   
        qdv_bootstrappedStats = new QuantitativeDataVariable("regression", "regression", ar_bootstrappedStat);
        qdv_bootstrappedStats.makeTheUCDO();
        dm.whereIsWaldo(93, waldoFile, "... qdv_bootstrappedStats.toString()");
        //qdv_bootstrappedStats.toString();
        dm.whereIsWaldo(95, waldoFile, "--- End constructTheBootstrapSample()"); 
        return returnStatus;
    } 
    
    private void determineTheStatisticsToCalculate() {
        dm.whereIsWaldo(102, waldoFile, "---  determineTheStatisticsToCalculate()");             
        for (int ithCBox = 0; ithCBox < nCheckBoxes; ithCBox++) {
            if (checkBoxValues[ithCBox] == true) {  //  If this statistic is desired
                theStatisticToCalculate = ithCBox;
                theStatLabel = cbArrStatDescriptions[ithCBox]; 
                statChosen = true;
            }
        }  
        dm.whereIsWaldo(110, waldoFile, "---  End determineTheStatisticsToCalculate()");
    }
    
    private double calculateTheStatistic() {
        dm.whereIsWaldo(114, waldoFile, "--- calculateTheStatistic()"); 
        double theStat = 0.0;   // Happy compiler
        switch(theStatisticToCalculate) {
            case 0:
                theStat = qdv_bootstrappedStats.getTheMean();
                theStatLabel = "The mean";
                break;

            case 1:
                // calculate the variance
                theStat = qdv_bootstrappedStats.getTheVariance();
                theStatLabel = "The variance";
                break;

            case 2:
                // calculate the stDev
                theStat = qdv_bootstrappedStats.getTheStandDev();
                theStatLabel = "The standard deviation";
                break;

            case 3:
                // calculate the skew
                theStat = qdv_bootstrappedStats.getTheSkew();
                theStatLabel = "The skew";
                break;       

            case 4:
                // calculate the Trimmed mean
                theStat = qdv_bootstrappedStats.getTheTrimmedMean(0.05);
                theStatLabel = "The trimmed mean";
                break;

            case 5:
                // calculate the Kurtosis
                theStat = qdv_bootstrappedStats.getTheKurtosis();
                theStatLabel = "The kurtosis";
                break;

            case 6:
                // calculate the CV
                theStat = qdv_bootstrappedStats.getTheCV();
                theStatLabel = "The coefficient of variation";
                break;

            case 7:
                // calculate the min
                theStat = qdv_bootstrappedStats.getMinValue();
                theStatLabel = "The minimum value";
                break;    

            case 8:
                // calculate tQ1
                theStat = qdv_bootstrappedStats.getTheQ1();
                theStatLabel = "The first quartile";
                break;

            case 9:
                // calculate the median
                theStat = qdv_bootstrappedStats.getTheMedian();
                theStatLabel = "The median";
                break;  

            case 10:
                // calculate Q3
                theStat = qdv_bootstrappedStats.getTheQ3();
                theStatLabel = "The third quartile";
                break;

            case 11:
                // calculate the max
                theStat = qdv_bootstrappedStats.getMaxValue();
                theStatLabel = "The maximum";
                break;

            case 12:
                // calculate the IQR
                theStat = qdv_FirstSlice.getTheIQR();
                theStatLabel = "The interquartile range";
                break;

            case 13:
                // calculate the range
                theStat = qdv_FirstSlice.getTheRange();
                theStatLabel = "The range";
                break;       

            case 14:
                // calculate the Tri-mean
                theStat = qdv_FirstSlice.getTheTriMean();
                theStatLabel = "The tri-mean";
                break;

            default:
            String switchFailure = "Switch failure: BootTheChosenStat 206" + Integer.toString(theStatisticToCalculate);
            MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }   //  End switch    
            
        dm.whereIsWaldo(210, waldoFile, "--- End calculateTheStatistic()");
        return theStat;
    }  
    
    public String getTheChosenStat() { return theStatLabel; }
    
    public double getTheSampleSlope() { return sampleSlope; }
    
    public QuantitativeDataVariable getTheQDV() { 
        //dm.whereIsWaldo(217, waldoFile, "--- getTheQDV(), toString");
        //qdv_bootstrappedStats.toString();
        return qdv_bootstrappedStats; 
    }
}