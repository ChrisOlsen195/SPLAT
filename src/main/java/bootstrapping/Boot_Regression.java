/**********************************************************************
 *                          Boot_Regression                          *
 *                             08/09/26                               *
 *                               12:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.Data_Manager;
import dataObjects.*;
import java.util.Random;

public class Boot_Regression {
    Boolean statChosen;
    Boolean[] checkBoxValues;
    
    int nRepetitions, nOriginalPairs, nLegalPairs, nLegalPairs0, nLegalPairs1;
    double sampleSlope;
    double[] ar_bootstrappedStat;
    
    Random random;
        
  String returnStatus, theStatLabel, temp_X, temp_Y;
    String[] cbArrStatDescriptions;
    
    // Make empty if no-print
    //String waldoFile = "Boot_Regression";
    String waldoFile = "";
    
    ColumnOfData jackXCol, jackYCol;
    ArrayList <ColumnOfData> alCol_regrData, alCol_LegalPairs, alCol_LegalPairs2;

    BivariateContinDataObj bivContin_LegalPairs_Original, bivContin_LegalPairs_Boot;
    Data_Manager dm;
    QuantitativeDataVariable qdv_bootstrappedSlope;
    
    public Boot_Regression(Boot_Controller boot_Controller, ArrayList <ColumnOfData> alCol_regrData) {
        this.dm = boot_Controller.getTheDataManager();
        dm.whereIsWaldo(37, waldoFile, "***  Constructing"); 
        this.alCol_regrData = alCol_regrData;
        nOriginalPairs = alCol_regrData.get(0).getColumnSize();
        returnStatus = "OK";
        //alCol_regrData.get(0).toString();
        //alCol_regrData.get(1).toString();
        
        bivContin_LegalPairs_Original = new BivariateContinDataObj(dm, alCol_regrData);
        bivContin_LegalPairs_Original.continueConstruction();
        nLegalPairs = bivContin_LegalPairs_Original.getNLegalDataPoints();
        sampleSlope = bivContin_LegalPairs_Original.getSlope();
        System.out.println("46 BootedRegression, sampleSlope = " + sampleSlope);
        dm.whereIsWaldo(44, waldoFile, "... bcdo Original..."); 
        //bcdo_original.toString();
        cbArrStatDescriptions = boot_Controller.getRepAndStatCheckBoxDescriptions();      
        cbArrStatDescriptions = boot_Controller.getRepAndStatCheckBoxDescriptions();
        nRepetitions = boot_Controller.getNReps();    
        /*
        System.out.println("53 BootedRegression, nReplications = " + nRepetitions);
        alCol_LegalPairs = new ArrayList();
        alCol_LegalPairs.add(new ColumnOfData("xVarLabel", "xVarDescr", bivContin_LegalPairs_Original.getLegalXsAs_AL_OfStrings()));
        alCol_LegalPairs.add(new ColumnOfData("yVarLabel", "yVarDescr", bivContin_LegalPairs_Original.getLegalYsAs_AL_OfStrings()));
        //dm.whereIsWaldo(57, waldoFile, "... Legal pairs toString()");
        //alCol_LegalPairs.get(0).toString();
        //alCol_LegalPairs.get(1).toString();
        dm.whereIsWaldo(60, waldoFile, "***  End Constructing"); 
        */
    }
    
    // Called from Controller
    public String constructTheBootstrapSample() {  
        dm.whereIsWaldo(52, waldoFile, "--- constructTheBootstrapSample()"); 

        ar_bootstrappedStat = new double[nRepetitions];
        alCol_LegalPairs = new ArrayList();
        /********************************************************
        *            x and y values of the legal pairs          *
        ********************************************************/
        /*               xValues of legal pairs                */
        alCol_LegalPairs.add(new ColumnOfData("xVarLabel", "xVarDescr", bivContin_LegalPairs_Original.getLegalXsAs_AL_OfStrings()));
        /*               yValues of legal pairs                */
        alCol_LegalPairs.add(new ColumnOfData("yVarLabel", "yVarDescr", bivContin_LegalPairs_Original.getLegalYsAs_AL_OfStrings()));
        int nLegalPairs0 = alCol_LegalPairs.get(0).getNCasesInColumn();
        int nLegalPairs1 = alCol_LegalPairs.get(1).getNCasesInColumn();

        for (int ithRepetition = 0; ithRepetition < nRepetitions; ithRepetition++) {
            random = new Random();
            jackXCol = new ColumnOfData(nLegalPairs, "jackXValues");
            jackYCol = new ColumnOfData(nLegalPairs, "jackYValues");
            
            for (int ithJackData = 0; ithJackData < nLegalPairs; ithJackData++) {
                int randomInt = random.nextInt(nLegalPairs - 1);  // 0 to (n - 1)

                temp_X = alCol_LegalPairs.get(0).getIthCase(randomInt);
                temp_Y = alCol_LegalPairs.get(1).getIthCase(randomInt);
                jackXCol.setStringInIthRow(ithJackData, temp_X);
                jackYCol.setStringInIthRow(ithJackData, temp_Y);
            }
            
            alCol_LegalPairs2 = new ArrayList();
            alCol_LegalPairs2.add(jackXCol);
            alCol_LegalPairs2.add(jackYCol);
            bivContin_LegalPairs_Boot = new BivariateContinDataObj(alCol_LegalPairs2);
            bivContin_LegalPairs_Boot.continueConstruction();
            ar_bootstrappedStat[ithRepetition] = bivContin_LegalPairs_Boot.getSlope();   
        }
            qdv_bootstrappedSlope = new QuantitativeDataVariable("regression", "regression", ar_bootstrappedStat);
            qdv_bootstrappedSlope.makeTheUCDO(); 
            return returnStatus;
    } 

    
    public String getTheChosenStat() { return theStatLabel; }
    
    public double getTheSampleSlope() { return sampleSlope; }
    
    public QuantitativeDataVariable getTheQDV() { 
        //dm.whereIsWaldo(217, waldoFile, "--- getTheQDV(), toString");
        //qdv_bootstrappedStats.toString();
        return qdv_bootstrappedSlope; 
    }
}