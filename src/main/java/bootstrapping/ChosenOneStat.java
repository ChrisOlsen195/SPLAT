/**********************************************************************
 *                           ChooseOneStat                            *
 *                             01/08/25                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import java.util.ArrayList;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class ChosenOneStat {
    
    Boolean[] checkBoxValues;
    
    int sampleSize, nCheckBoxes, nStatsToCalculate, nBooties, theStatIndex;
    
    double oneBootedStat;
    double[] copyOfSample, bootedSample;
    
    String returnStatus, chosenStat;
    String[] cbArrStatDescriptions;
    
    // Make empty if no-print
    //String waldoFile = "TheChosenStat";
    String waldoFile = "";

    ArrayList<Integer> theStatsToCalculate;
    ArrayList<String> theLabels;
    
    OneStat_Controller boot_ChooseStats_Controller;
    Data_Manager dm;
    QuantitativeDataVariable qdv_simSample, qdv_theBootedStatDistr;
    UnivariateContinDataObj ucdo_SimSample;
    
    public ChosenOneStat(OneStat_Controller boot_ChooseStats_Controller, double[] theOriginalSample) {
        dm = boot_ChooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(40, waldoFile, "Constructing"); 
        this.boot_ChooseStats_Controller = boot_ChooseStats_Controller;
        returnStatus = "OK";
        sampleSize = theOriginalSample.length;
        copyOfSample = new double[sampleSize];
        bootedSample = new double[sampleSize];
        cbArrStatDescriptions = boot_ChooseStats_Controller.getRepAndStatCheckBoxDescriptions();
        theLabels = new ArrayList<>();
        theStatsToCalculate = new ArrayList<>();
        System.arraycopy(theOriginalSample, 0, copyOfSample, 0, sampleSize);
        nCheckBoxes = boot_ChooseStats_Controller.getNCheckBoxes();
        checkBoxValues = new Boolean[nCheckBoxes];
        checkBoxValues = boot_ChooseStats_Controller.getRepAndStatCheckBoxValues();
        nCheckBoxes = checkBoxValues.length;
        qdv_theBootedStatDistr = new QuantitativeDataVariable("BootedStat", "BootedStat");
        nBooties = boot_ChooseStats_Controller.getNReps();        
    }
    
public String constructTheBootstrapSample() {  
        dm.whereIsWaldo(59, waldoFile, "constructTheBootstrapSample()"); 
        determineTheStatisticsToCalculate();
        if (nStatsToCalculate == 0) {
            MyAlerts.showZeroStatsChosenAlert();
            returnStatus = "Cancel";
            return "Cancel";
        }
        
        for (int ithBootie = 0; ithBootie < nBooties; ithBootie++) {
            makeASample();   
            oneBootedStat = makeTheStatistic();  
            qdv_theBootedStatDistr.addADouble(oneBootedStat); 
        }    
        qdv_theBootedStatDistr.makeTheUCDO();
        return returnStatus;
    } 
    
    // Originally more than one stat was allowed.  This code is now vestigial,
    // to allow a possible return.  nStatsToCalculate should now always be 1.
    private void determineTheStatisticsToCalculate() {
        dm.whereIsWaldo(79, waldoFile, "determineTheStatisticsToCalculate()"); 
            
        nStatsToCalculate = 0;
        for (int ith = 0; ith < nCheckBoxes; ith++) {
            if (checkBoxValues[ith] == true) {  //  If the statistic is desired
                nStatsToCalculate++;
                theStatsToCalculate.add(ith);   //  Index of stat to calc
                theLabels.add(cbArrStatDescriptions[ith]);
            }
        }        
    }
    
    private void makeASample() {
        for (int ithUnit = 0; ithUnit < sampleSize; ithUnit++) {
            int obsUnit = (int)(sampleSize * Math.random());
            bootedSample[ithUnit] = copyOfSample[obsUnit];
        } 

        qdv_simSample = new QuantitativeDataVariable("Label", "Descr", bootedSample);
        ucdo_SimSample = new UnivariateContinDataObj("BootStrap", qdv_simSample);   
    }
    
    private double makeTheStatistic() {
        dm.whereIsWaldo(102, waldoFile, "makeTheStatistic()");
        double theStat = 0.0;   // Happy compiler
        for (int ithStatToCalc = 0; ithStatToCalc < nStatsToCalculate; ithStatToCalc++ ) {
            theStatIndex = theStatsToCalculate.get(ithStatToCalc);
            switch(theStatIndex) {

                case 0:
                    // calculate the mean
                    theStat = ucdo_SimSample.getTheMean();
                    chosenStat = "The mean";
                    break;

                case 1:
                    // calculate the variance
                    theStat = ucdo_SimSample.getTheVariance();
                    chosenStat = "The variance";
                    break;

                case 2:
                    // calculate the stDev
                    theStat = ucdo_SimSample.getTheStandDev();
                    chosenStat = "The standard deviation";
                    break;

                case 3:
                    // calculate the skew
                    theStat = ucdo_SimSample.getFisherPearsonSkew();
                    chosenStat = "The skew";
                    break;       

                case 4:
                    // calculate the Trimmed mean
                    theStat = ucdo_SimSample.getTheTrimmedMean(0.05);
                    chosenStat = "The trimmed mean";
                    break;

                case 5:
                    // calculate the Kurtosis
                    theStat = ucdo_SimSample.getTheKurtosis();
                    chosenStat = "The kurtosis";
                    break;

                case 6:
                    // calculate the CV
                    theStat = ucdo_SimSample.getTheCV();
                    chosenStat = "The coefficient of variation";
                    break;

                case 7:
                    // calculate the min
                    theStat = ucdo_SimSample.getMinValue();
                    chosenStat = "The minimum value";
                    break;    

                case 8:
                    // calculate tQ1
                    theStat = ucdo_SimSample.getTheQ1();
                    chosenStat = "The first quartile";
                    break;

                case 9:
                    // calculate the median
                    theStat = ucdo_SimSample.getTheMedian();
                    chosenStat = "The median";
                    break;  

                case 10:
                    // calculate Q3
                    theStat = ucdo_SimSample.getTheQ3();
                    chosenStat = "The third quartile";
                    break;

                case 11:
                    // calculate the max
                    theStat = ucdo_SimSample.getMaxValue();
                    chosenStat = "The maximum";
                    break;

                case 12:
                    // calculate the IQR
                    theStat = ucdo_SimSample.getTheIQR();
                    chosenStat = "The interquartile range";
                    break;

                case 13:
                    // calculate the range
                    theStat = ucdo_SimSample.getTheRange();
                    chosenStat = "The range";
                    break;       

                case 14:
                    // calculate the Tri-mean
                    theStat = ucdo_SimSample.getTheTriMean();
                    chosenStat = "The tri-mean";
                    break;

                default:
                String switchFailure = "Switch failure: BootTheChosenStat 199" + String.valueOf(theStatIndex);
                MyAlerts.showUnexpectedErrorAlert(switchFailure);

            }   //  End switch
        }   //  End calculation of stats     
        
        return theStat;
    }  
    
    public String getTheChosenStat() { return chosenStat; }
    
    public QuantitativeDataVariable getTheQDV() { 
        return qdv_theBootedStatDistr; 
    }
}