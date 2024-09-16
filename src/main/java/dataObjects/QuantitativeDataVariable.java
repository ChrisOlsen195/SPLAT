/**************************************************
 *             QuantitativeDataVariable           *
 *                    05/24/24                    *
 *                      15:00                     *
 *************************************************/
package dataObjects;

import utilityClasses.DataUtilities;
import java.util.ArrayList;
import matrixProcedures.*;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class QuantitativeDataVariable {
    // POJOs
    private boolean doublesFound = false;
    boolean variableIsQuant = true;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private int nOriginalDataPoints, nLegalDataPoints;
    private double[] dbl_legalData;
    ArrayList<Double> alDouble_theLegalCases; // Need to be Double??    
    private String varLabel, varDescription;    
    ArrayList<String> alString_theLegalCases, alString_AllTheCases; 
    
    // My classes
    UnivariateContinDataObj ucdo;
    ColumnOfData colOfData;
    QuantitativeDataVariable qdv;
    
    public QuantitativeDataVariable () { 
        varLabel = "";
        varDescription = "";
    }
    
    //  Copy constructor

    public QuantitativeDataVariable(QuantitativeDataVariable original_qdv) {
        
        System.out.println("43 QDV, Copy Constructor!!!");
         this.varLabel = original_qdv.getTheVarLabel();
         this.varDescription = original_qdv.getTheVarDescription();
         this.doublesFound = original_qdv.getDoublesFound();   
         this.variableIsQuant = original_qdv.getVariableIsQuant();
         this.nOriginalDataPoints = original_qdv.get_nDataPointsOriginal();
         this.nLegalDataPoints = original_qdv.getLegalN();

         this.dbl_legalData = new double[nLegalDataPoints];
         // arrayCopy dbl_legalData
         for (int i = 0; i < this.nLegalDataPoints; i++) {
            this.dbl_legalData[i] = original_qdv.getIthDataPtAsDouble(i);
         }
         
         // alDouble_theLegalCases
         alDouble_theLegalCases = new ArrayList();
         for (int i = 0; i<  nLegalDataPoints; i++) {
             alDouble_theLegalCases.add(original_qdv.alDouble_theLegalCases.get(i));
         }
         
         // alString_theLegalCases
         alString_theLegalCases = new ArrayList();
         for (int i = 0; i<  nLegalDataPoints; i++) {
             alString_theLegalCases.add(original_qdv.alString_theLegalCases.get(i));
         }
         
         // alString_allTheCases
         alString_AllTheCases = new ArrayList();
         for (int i = 0; i<  nLegalDataPoints; i++) {
             alString_AllTheCases.add(original_qdv.alString_AllTheCases.get(i));
         }

        this.ucdo = new UnivariateContinDataObj("QDV", this);
        System.out.println("80 QDV");
        System.out.println("81, new qdv = " + this.toString());
    }

    // The data will be added as we go...  (bootstrapping needs this).
    public QuantitativeDataVariable (String varLabel, String varDescription) {
        if (printTheStuff == true) {
            System.out.println("82 *** QuantitativeDataVariable, constructing");
        }
        this.varLabel = varLabel;
        this.varDescription = varDescription;
        alString_AllTheCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        alString_theLegalCases = new ArrayList();
        nOriginalDataPoints = 0;
    }

    
    public QuantitativeDataVariable(String varLabel, String varDescription, ColumnOfData colOfData) {
        if (printTheStuff == true) {
            System.out.println("95 *** QuantitativeDataVariable, constructing");
        }
        this.colOfData = new ColumnOfData();
        this.colOfData = colOfData;
        this.varLabel = varLabel;
        varDescription = colOfData.getVarDescription();
        nOriginalDataPoints = colOfData.getColumnSize();
        alString_AllTheCases = colOfData.getTheCases_ArrayList();
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj("106 QDV", this);
    }
    
    // Incoming Matrix must be m x 1, all legal
    // Used in multiple and noIntercept regression
    public QuantitativeDataVariable(Data_Manager dm, String varLabel, String varDescription, Matrix inMatrix) {
        if (printTheStuff == true) {
            System.out.println("113 *** QuantitativeDataVariable, constructing");
        }
        this.varLabel = varLabel;
        this.varDescription = varDescription;
        alString_AllTheCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        alString_theLegalCases = new ArrayList();
        nLegalDataPoints = inMatrix.getRowDimension();
        nOriginalDataPoints = nLegalDataPoints;
        
        for (int ithRow = 0; ithRow < nLegalDataPoints; ithRow++) {
            double daIthRowValue = inMatrix.get(ithRow, 0);
            String strOfDouble = daIthRowValue + "";
            alString_AllTheCases.add(strOfDouble);
        }
        
        stripTheNonDoubles();
        colOfData = new ColumnOfData(dm, varLabel, "QDV", alString_theLegalCases);
        ucdo = new UnivariateContinDataObj("131 QDV",this);
    }
    
    // This constructor is used by the VerticalBoxPlotPlatform, matchedPairs, and bootstrapping.
    public QuantitativeDataVariable (String varLabel, String varDescription, double[] dbl_IncomingData)  {
        if (printTheStuff == true) {
            System.out.println("137 *** QuantitativeDataVariable, constructing");
        }
        this.varLabel = varLabel;
        this.varDescription = varDescription;
        nOriginalDataPoints = dbl_IncomingData.length;
        alString_AllTheCases = new ArrayList<>();
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        
        for (int ith = 0; ith < nOriginalDataPoints; ith++) {
            alString_AllTheCases.add(String.valueOf(dbl_IncomingData[ith]));
        }
        
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj("133 QDV",this);
    }

    public QuantitativeDataVariable (String varLabel, String varDescription, ArrayList<String> inDataStrings)  {
        if (printTheStuff == true) {
            System.out.println("156 QuantitativeDataVariable, constructing");
        }
        this.varLabel = varLabel;
        this.varDescription = varDescription;
        nOriginalDataPoints = inDataStrings.size();
        alString_AllTheCases = inDataStrings;
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj("165 QDV",this);
    }
    
    public QuantitativeDataVariable (String varLabel, String varDescription, String[] inDataStrings)  {
        if (printTheStuff == true) {
            System.out.println("170 QuantitativeDataVariable, constructing");
        }
        this.varLabel = varLabel;
        this.varDescription = varDescription;
        nOriginalDataPoints = inDataStrings.length;
        alString_AllTheCases = new ArrayList();
        
        for (int ithString = 0; ithString < nOriginalDataPoints; ithString++) {
            alString_AllTheCases.add(inDataStrings[ithString]);
        }
        
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj("166 QDV",this);
    }
    
    public void doMedianBasedCalculations() { ucdo.doMedianBasedCalculations(); }
    public void doMeanBasedCalculations() { ucdo.doMeanBasedCalculations(); }
    
    public void addADouble(double thisDouble) {
        alDouble_theLegalCases.add(thisDouble);
        alString_AllTheCases.add(Double.toString(thisDouble));
        alString_theLegalCases.add(Double.toString(thisDouble)); 
        nOriginalDataPoints = alDouble_theLegalCases.size();
        nLegalDataPoints = alDouble_theLegalCases.size();
    }
    
    /*
    public void addAString(String thisString) {
        // Should never be a problem, but check anyway
        if (!DataUtilities.strIsADouble(thisString)) {
            System.out.println("201 QDV, trying to add a non-double String");
            System.exit(202);
        }
        alDouble_theLegalCases.add(Double.valueOf(thisString));
        alString_AllTheCases.add(thisString);
        alString_theLegalCases.add(thisString); 
        nOriginalDataPoints = alDouble_theLegalCases.size();
        nLegalDataPoints = alDouble_theLegalCases.size();
    }
    */
    
    public boolean checkForVariability() {        
        for (int ith = 0; ith < nLegalDataPoints - 1; ith++) {            
            if(!alDouble_theLegalCases.get(ith).equals(alDouble_theLegalCases.get(ith + 1))) {
                return true; 
            }
        }
        return false;
    }
        
    // This method also determines the existence of categorical data
    // Classes should check for categorical data at construction.
    private void stripTheNonDoubles() {
        //System.out.println("225 QDV, stripTheNonDoubles");
        doublesFound = false;
        
        for (int ithCase = 0; ithCase < nOriginalDataPoints; ithCase++) {
            //System.out.println("229 QDV, ithCase = " + ithCase);
            String tempString = alString_AllTheCases.get(ithCase);
            //System.out.println("231 QDV, tempString = " + tempString);
            if (DataUtilities.strIsADouble(tempString)) {
                doublesFound = true;
                alString_theLegalCases.add(tempString);
                alDouble_theLegalCases.add(Double.valueOf(tempString));
            }  
            else { /*No op */}
        }        
        
        if (doublesFound) {
            nLegalDataPoints = alDouble_theLegalCases.size(); 
            dbl_legalData = new double[nLegalDataPoints];
            
            for (int ithPoint = 0; ithPoint < nLegalDataPoints; ithPoint++) {
                dbl_legalData[ithPoint] = Double.parseDouble(alString_theLegalCases.get(ithPoint));
            }
            
            ucdo = new UnivariateContinDataObj("248 QDV",this);           
        } else {
            variableIsQuant = false;
        }        
    }
    
    public ColumnOfData getColumnOfData() { return colOfData; }
    public String getTheVarLabel() {return varLabel; }
    public void setTheVarLabel(String toThis) { varLabel = toThis; }
    public String getTheVarDescription() { return varDescription; }
    public void setTheVarDescription(String toThis) { varDescription = toThis; } 
    public boolean getDoublesFound() { return doublesFound; }
    public boolean getVariableIsQuant() { return variableIsQuant; }
    public int get_nDataPointsOriginal () {return nOriginalDataPoints;}
    //public int get_nLegalDataPoints() {return ucdo.getLegalN(); }
    public int getOriginalN () {return nOriginalDataPoints;}
    public int getLegalN() { return ucdo.getLegalN(); }
    public double getMinValue() { return ucdo.getMinValue(); }
    public double getMaxValue() { return ucdo.getMaxValue(); } 
    public double getTheSum() { return ucdo.getSumX(); }  
    public double getTheSumX2() { return ucdo.getSumX2(); } 
    public double getTheSS() { return ucdo.getTheSS();}
    public double getTheMean() { return ucdo.getTheMean(); }
    public double getTheMedian() { return ucdo.getTheMedian(); }
    
    public double getTheTrimmedMean( double trimProp) {
        return ucdo.getTheTrimmedMean(trimProp);
    }
    
    public double getTheStandDev() {return ucdo.getTheStandDev(); }
    public double getTheVariance() {return ucdo.getTheVariance(); } 
    public double getTheSkew() { return ucdo.getFisherPearsonSkew(); }
    public double getTheKurtosis() { return ucdo.getTheKurtosis(); }
    public double getStandErrMean() {return ucdo.getStandErrMean(); }    
    public double getTheMarginOfError() {return ucdo.getTheMarginOfErr(0.95);}
    public double getTheIQR() {return ucdo.getTheIQR(); } 
    public double getTheRange() {return ucdo.getTheRange(); }
    public double[] getTheDeviations() {return ucdo.getTheDeviations(); }
    public double[] getTheDataSorted() { return ucdo.getTheDataSorted(); }
    public double[] get_5NumberSummary() { return ucdo.get_5NumberSummary(); }
    
    public double getIthPercentile(double p) {
        return ucdo.fromPercentileRank_toPercentile(p);
    }
    
    public double getIthPercentileRank(double ithDataPoint) {
        return ucdo.fromPercentile_toPercentileRank(ithDataPoint);
    }
      
    public ArrayList<String> getOriginalDataAs_alString() { return alString_AllTheCases; }
    public int get_nDataPointsLegal() { return nLegalDataPoints; }  

    public double[] getLegalDataAsDoubles() { 
        dbl_legalData = new double[nLegalDataPoints];        
        for (int ithPoint = 0; ithPoint < nLegalDataPoints; ithPoint++) {
            dbl_legalData[ithPoint] = alDouble_theLegalCases.get(ithPoint);
        }
        
        return dbl_legalData; 
    }
    
    public String getIthDataPtAsString(int ith) {
        String ithCase;        
        if (ith < alString_AllTheCases.size()) {    //  Proper request
            ithCase = alString_AllTheCases.get(ith);
        }
        else {  // Improper request
            ithCase = "*";
        }
        return ithCase; 
    }
    
    public void makeTheUCDO() { ucdo = new UnivariateContinDataObj("299 QDV",this); }
    public UnivariateContinDataObj getTheUCDO() { return ucdo; };   
    public QuantitativeDataVariable getTheQDV() { return this; }
    public double[] getTheSortedDoubles() { return ucdo.getTheSortedArray(); }
    
    public double getIthDataPtAsDouble(int ith) {
        if (dbl_legalData == null) {
            dbl_legalData = new double[nLegalDataPoints];        
            for (int ithPoint = 0; ithPoint < nLegalDataPoints; ithPoint++) {
                dbl_legalData[ithPoint] = alDouble_theLegalCases.get(ithPoint);
            }
        }
        double tempDouble = dbl_legalData[ith]; 
        return tempDouble; 
    }
      
    public double[] getADStats() {return ucdo.getAndersonDarling(); } 
    
    public boolean checkIfQuant() {         
        if (!variableIsQuant) {
            MyAlerts.showVariableIsNotQuantAlert(qdv);   
        }            
        return variableIsQuant;
    }
    
    public ArrayList<String> getAllTheCasesAsALStrings() { return alString_AllTheCases; }
    public ArrayList<Double> getLegalCases_AsALDoubles() { return alDouble_theLegalCases; }
    
    public ArrayList<String> getLegalCases_AsALStrings() { return alString_theLegalCases; }  

    public String toString() {
        System.out.println ("\n\nQuantitativeDataVariable LEGAL data points toString(), varLabel = " + varLabel);
        System.out.println ("QuantitativeDataVariable LEGAL data points toString(), varDescription = " + varDescription);
        System.out.println("n = " + nOriginalDataPoints);
        System.out.println("Legal n = " + getLegalN());    
        
        for (int ithDataPoint = 0; ithDataPoint < nLegalDataPoints; ithDataPoint++)  {
            System.out.println("ith = " + ithDataPoint + ", " + dbl_legalData[ithDataPoint]);
        }
        
        return "\n";
    } 
}