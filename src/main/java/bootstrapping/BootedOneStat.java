/**********************************************************************
 *                           BootedOneStat                            *
 *                             08/27/25                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class BootedOneStat {
    
    Boolean statChosen;
    Boolean[] checkBoxValues;
    
    int nInOriginalSample, nCheckBoxes, nReplications, theStatisticToCalculate;
    
    double valueOfStat;
    double[] copyOfSample, bootedSample;
    
    String returnStatus, chosenStat, theStatLabel;
    String[] cbArrStatDescriptions;
    
    // Make empty if no-print
    //String waldoFile = "BootedOneStat";
    String waldoFile = "";

    Data_Manager dm;
    QuantitativeDataVariable qdv_bootedSample, qdv_bootstrappedStats;
    
    public BootedOneStat(ChooseStats_Controller chooseStats_Controller, double[] theOriginalSample) {
        dm = chooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(34, waldoFile, "Constructing"); 
        returnStatus = "OK";
        statChosen = false;
        nInOriginalSample = theOriginalSample.length;
        copyOfSample = new double[nInOriginalSample];
        bootedSample = new double[nInOriginalSample];
        cbArrStatDescriptions = chooseStats_Controller.getRepAndStatCheckBoxDescriptions();
        System.arraycopy(theOriginalSample, 0, copyOfSample, 0, nInOriginalSample);
        nCheckBoxes = chooseStats_Controller.getNCheckBoxes();
        checkBoxValues = new Boolean[nCheckBoxes];
        checkBoxValues = chooseStats_Controller.getRepAndStatCheckBoxValues();
        nCheckBoxes = checkBoxValues.length;
        qdv_bootstrappedStats = new QuantitativeDataVariable("BootedStat", "BootedStat", theOriginalSample);
        nReplications = chooseStats_Controller.getNReps();        
    }
    
public String constructTheBootstrapSample() {  
        dm.whereIsWaldo(51, waldoFile, "constructTheBootstrapSample()"); 
        determineTheStatisticToCalculate();
        if (!statChosen) {
            MyAlerts.showZeroStatsChosenAlert();
            returnStatus = "Cancel";
            return "Cancel";
        }
        
        for (int ithReplication = 0; ithReplication < nReplications; ithReplication++) {
            makeASample();   
            valueOfStat = calculateTheStatistic();  
            qdv_bootstrappedStats.addADouble(valueOfStat); 
        }    
        qdv_bootstrappedStats.makeTheUCDO();
        return returnStatus;
    } 
    
    private void determineTheStatisticToCalculate() {
        dm.whereIsWaldo(69, waldoFile, "determineTheStatisticsToCalculate()"); 
            
        for (int ith = 0; ith < nCheckBoxes; ith++) {
            if (checkBoxValues[ith] == true) {  //  If this statistic is desired
                theStatisticToCalculate = ith;
                theStatLabel = cbArrStatDescriptions[ith]; 
                statChosen = true;
            }
        }        
    }
    
    private void makeASample() {
        for (int ithUnit = 0; ithUnit < nInOriginalSample; ithUnit++) {
            int obsUnit = (int)(nInOriginalSample * Math.random());
            bootedSample[ithUnit] = copyOfSample[obsUnit];
        } 

        qdv_bootedSample = new QuantitativeDataVariable("Label", "Descr", bootedSample);  
    }
    
    private double calculateTheStatistic() {
        dm.whereIsWaldo(90, waldoFile, "calculateTheStatistic()");
        double theStat = 0.0;   // Happy compiler
        switch(theStatisticToCalculate) {

            case 0:
                // calculate the mean
                theStat = qdv_bootedSample.getTheMean();
                chosenStat = "The mean";
                break;

            case 1:
                // calculate the variance
                theStat = qdv_bootedSample.getTheVariance();
                chosenStat = "The variance";
                break;

            case 2:
                // calculate the stDev
                theStat = qdv_bootedSample.getTheStandDev();
                chosenStat = "The standard deviation";
                break;

            case 3:
                // calculate the skew
                theStat = qdv_bootedSample.getTheSkew();
                chosenStat = "The skew";
                break;       

            case 4:
                // calculate the Trimmed mean
                theStat = qdv_bootedSample.getTheTrimmedMean(0.05);
                chosenStat = "The trimmed mean";
                break;

            case 5:
                // calculate the Kurtosis
                theStat = qdv_bootedSample.getTheKurtosis();
                chosenStat = "The kurtosis";
                break;

            case 6:
                // calculate the CV
                theStat = qdv_bootedSample.getTheCV();
                chosenStat = "The coefficient of variation";
                break;

            case 7:
                // calculate the min
                theStat = qdv_bootedSample.getMinValue();
                chosenStat = "The minimum value";
                break;    

            case 8:
                // calculate tQ1
                theStat = qdv_bootedSample.getTheQ1();
                chosenStat = "The first quartile";
                break;

            case 9:
                // calculate the median
                theStat = qdv_bootedSample.getTheMedian();
                chosenStat = "The median";
                break;  

            case 10:
                // calculate Q3
                theStat = qdv_bootedSample.getTheQ3();
                chosenStat = "The third quartile";
                break;

            case 11:
                // calculate the max
                theStat = qdv_bootedSample.getMaxValue();
                chosenStat = "The maximum";
                break;

            case 12:
                // calculate the IQR
                theStat = qdv_bootedSample.getTheIQR();
                chosenStat = "The interquartile range";
                break;

            case 13:
                // calculate the range
                theStat = qdv_bootedSample.getTheRange();
                chosenStat = "The range";
                break;       

            case 14:
                // calculate the Tri-mean
                theStat = qdv_bootedSample.getTheTriMean();
                chosenStat = "The tri-mean";
                break;

            default:
            String switchFailure = "Switch failure: BootTheChosenStat 185" + Integer.toString(theStatisticToCalculate);
            MyAlerts.showUnexpectedErrorAlert(switchFailure);

        }   //  End switch 
        
        return theStat;
    }  
    
    public String getTheChosenStat() { return chosenStat; }
    
    public QuantitativeDataVariable getTheQDV() { 
        return qdv_bootstrappedStats; 
    }
}