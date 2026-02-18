/************************************************************
 *                        ColumnOfData                      *
 *                          12/16/25                        *
 *                           12:00                          *
 ***********************************************************/
package dataObjects;

import utilityClasses.DataCleaner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import splat.*;
import utilityClasses.StringUtilities;
import utilityClasses.DataUtilities;
import utilityClasses.MyYesNoAlerts;

public class ColumnOfData {
    //  POJOs
    boolean containsBlanks, containsNumerics, containsZeroOnes, 
            containsCats, hasBeenFormatted, hasMissingData;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nStrings, decimalPosition, lengthOfString, adjusted_sigDecimals,
        maxSigDecimals, maxLen_FormattedString, maxOrdMag, textBoxLen,
        necessarylength, overFlow, minOrdMag, nDistinctLegalValues; 

    private int nCasesInColumn, nCategorical, nLegals, nMissing, 
            significantDigits;
    
    double dbl_ParsedValue;
    
    String str_ValueOfString, strNumericStringFormat, strFormatted, strRawCase,
           strVarLabel, strVarDescription, strMissingValue, strVarDisplayFormat,
           strFormatString, dataType;    
    
    String strReturnStatus;

    ArrayList<String> str_al_TheCases, str_al_DistinctValues, str_al_FormattedCases;
    
    // My classes
    MyYesNoAlerts myYesNoAlerts;
    Data_Manager dm;

    public ColumnOfData() { 
        if (printTheStuff) {
            System.out.println("*** 48 ColOfData, constructing blank");
        }
        str_al_TheCases = new ArrayList<>(); 
        str_al_FormattedCases = new ArrayList<>();
        nCasesInColumn = 0;
        strVarLabel = "No Label";
        strVarDescription = "No Description";
        strMissingValue = "*";
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        significantDigits = 0;
        strFormatString = "%.0f";
        hasBeenFormatted = false;
        nDistinctLegalValues = 0;
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();
    } 

    // This constructor should not have to look at the data??  Where called??
    public ColumnOfData (ColumnOfData dataColumn) {  // Copy constructor
        if (printTheStuff) {
            System.out.println("*** 71 ColOfData, constructing from ColOfData");
        }
        dataType = dataColumn.getDataType();
        strVarLabel = dataColumn.getVarLabel();
        str_al_TheCases = new ArrayList<>(); 
        str_al_FormattedCases = new ArrayList<>();
        nCasesInColumn = dataColumn.getColumnSize();
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        strReturnStatus = "OK";
        strVarLabel = dataColumn.getVarLabel();
        strVarDescription = dataColumn.getVarDescription();
        strMissingValue = "*"; 
        nDistinctLegalValues = dataColumn.getNumberOfDistinctValues();

        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String textToAdd = dataColumn.getStringInIthRow(ithCase);
            str_al_TheCases.add(textToAdd);
        } 
        myYesNoAlerts = new MyYesNoAlerts();
        determineDataType();
    }
    
    public ColumnOfData(int nCasesInColumn, String varLabel) {
        if (printTheStuff) {
            System.out.println("*** 98 ColOfData, constructing from nCases and varLabel");
        }
        this.nCasesInColumn = nCasesInColumn;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        strReturnStatus = "OK";
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        strVarDescription = varLabel;
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        //containsGuesses = false;
        strMissingValue = "*";
        significantDigits = 0;
        strFormatString = "%.0f";
        nDistinctLegalValues = 0;
        myYesNoAlerts = new MyYesNoAlerts();
        determineDataType();
    }
    
    // This constructor creates an empty column of data; only used at startup.
    public ColumnOfData(Data_Manager dm, int nCasesInColumn, String varLabel) {
        if (printTheStuff) {
            System.out.println("*** 127 ColOfData, constructing from dm, nCases and varLabel");
        }
        this.nCasesInColumn = nCasesInColumn;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        strReturnStatus = "OK";
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        strVarDescription = varLabel;
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        strMissingValue = "*";
        significantDigits = 0;
        strFormatString = "%.0f";
        nDistinctLegalValues = 0;
        myYesNoAlerts = new MyYesNoAlerts();
        determineDataType();
    }

    // This constructor is used when doing two-way ANOVA
    public ColumnOfData(CategoricalDataVariable catDatVar) {
        if (printTheStuff) {
            System.out.println("*** 155 ColOfData, constructing from catDatVar");
        }
        nCasesInColumn = catDatVar.get_N();
        String daData[] = new String[nCasesInColumn];
        daData = catDatVar.getDataAsStrings();
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        strReturnStatus = "OK";
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) { 
            str_al_TheCases.add(daData[ithCase]);
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = catDatVar.getTheDataLabel();
        strVarDescription = strVarLabel;
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        strMissingValue = "*";
        significantDigits = 0;
        strFormatString = "%.0f";
        myYesNoAlerts = new MyYesNoAlerts();
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }

    public ColumnOfData(QuantitativeDataVariable qdv) {
        if (printTheStuff) {
            System.out.println("*** 185 ColOfData, constructing from qdv");
        }
        nCasesInColumn = qdv.getLegalN();
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        str_al_TheCases = qdv.getLegalCases_AsALStrings();
        strVarLabel = qdv.getTheVarLabel();
        strVarDescription = qdv.getTheVarDescription();
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        strMissingValue = "*";
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by the BivariateCatagoricalDataObj
    public ColumnOfData(Data_Manager dm, String varLabel, String varDescription, ArrayList<String> theData) {
        if (printTheStuff) {
            System.out.println("*** 208 ColOfData, constructing from dm, varLabel, varDescr, ArrayList<String> theData");
        }
        nCasesInColumn = theData.size();
        str_al_TheCases = new ArrayList<>();
        strVarLabel = varLabel;
        str_al_FormattedCases = new ArrayList<>();
        strReturnStatus = "OK";
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData.get(iCase));
            str_al_FormattedCases.add("*");
        }
        this.strVarLabel = varLabel;
        this.strVarDescription = varDescription;
        containsBlanks = false;
        containsNumerics = true;
        strMissingValue = "*";
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        myYesNoAlerts = new MyYesNoAlerts();
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
        if (printTheStuff) {
            System.out.println("--- 229 ColOfData, END constructing from dm, varLabel, varDescr, ArrayList<String> theData");
        }
    }
    
    public ColumnOfData(String varLabel, String varDescription, ArrayList<String> al_theData) {
        if (printTheStuff) {
            System.out.println("*** 234 ColOfData, constructing from varLabel, varDescr, al_Data");
        }
        nCasesInColumn = al_theData.size();
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        strReturnStatus = "OK";
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(al_theData.get(iCase));
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        this.strVarDescription = varDescription;
        containsBlanks = false;
        containsNumerics = true;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        strMissingValue = "*";
        myYesNoAlerts = new MyYesNoAlerts();
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by Logistic_Controller
    public ColumnOfData(Data_Manager dm, String varLabel, String varDescription, String[] theData) {
        if (printTheStuff) {
            System.out.println("*** 260 ColOfData, constructing from dm, varLabel, varDescr, String[] theData");
        }
        nCasesInColumn = theData.length;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        strReturnStatus = "OK";
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData[iCase]);
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        this.strVarDescription = varDescription;
        containsBlanks = false;
        containsNumerics = true;
        containsZeroOnes = true;    // Rendered false if non-zero real is found
        strMissingValue = "*";
        myYesNoAlerts = new MyYesNoAlerts();
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    public void setAssociatedDataManager(Data_Manager dm) {this.dm = dm; }
    
    public void addUntilNCases(int thisTargetNumber) {
        do {
            addNCasesOfThese(1, "*");
        } while (nCasesInColumn < thisTargetNumber);
        formatTheColumn();
    } 

    public void addNCasesOfThese(int nNewCases, String ofThese) {
        
        for (int ithNewCase = 0; ithNewCase < nNewCases; ithNewCase++) { 
            str_al_TheCases.add(ofThese);
            str_al_FormattedCases.add("*");
        }
        
        nCasesInColumn += nNewCases;
        formatTheColumn();
    }

    public void determineDataType() {
        if (printTheStuff) {
            System.out.println("*** 304 ColumnOfData, determineDataType()");
        }
        containsNumerics = false;
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String ithString = str_al_TheCases.get(ithCase);
            boolean isaDouble = DataUtilities.strIsADouble(ithString);
            
            if (isaDouble) { containsNumerics = true; }
            
            if ((!ithString.equals("0") && !ithString.equals("1"))) { 
                containsZeroOnes = false;     
            }

            if (!ithString.equals("*") && (!isaDouble)) {
                containsCats = true;
            }
        }

        if (containsCats && containsNumerics) {
            myYesNoAlerts.setTheYes("Convert away!");
            myYesNoAlerts.setTheNo("Don't you dare!");
            myYesNoAlerts.showAmbiguousColumnAlert(strVarLabel);                               
            String replaceMissing = myYesNoAlerts.getYesOrNo();
            if (replaceMissing.equals("Yes")) {
                for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
                    String ithString = str_al_TheCases.get(ithCase);
                    
                    if (!ithString.equals("*") && (!DataUtilities.strIsADouble(ithString))) {
                        str_al_TheCases.set(ithCase, "*");
                    }
                }  
                setDataType("Quantitative");
            } else {
                setDataType("Categorical");
            }
        } else if (containsNumerics) { setDataType("Quantitative");}
        else { setDataType("Categorical");}
    } 

    public void cleanTheColumn(Data_Manager dm, int thisCol) {
        DataCleaner dc = new DataCleaner(dm, dm.getAllTheColumns()
                                               .get(thisCol));
        nCasesInColumn = dm.getNCasesInStruct();
        dc.cleanAway();
        strReturnStatus = dc.getReturnStatus();
        String[] fixedData = new String[nCasesInColumn];
        fixedData = dc.getFixedData();
        
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
             setStringInIthRow(ithCase, fixedData[ithCase]); 
        } 
         
        dm.resetTheGrid(); 
    }
    
    // retrieve or store data
    public String getStringInIthRow(int ithRow) {
        String dataString = str_al_TheCases.get(ithRow);
        
        if (dataString.equals(" ")) { dataString = "*"; }
        return dataString;
    }

    public void setStringInIthRow(int row, String toThisValue) {
        
        if (row >= nCasesInColumn) {
            int casesToAdd = row - nCasesInColumn + 1;
            addNCasesOfThese(casesToAdd, "*");
        }
        
        str_al_TheCases.set(row, toThisValue);  
    }
    
    private int calculateNumberOfDistinctLegalValues() {
        if (printTheStuff) {
            System.out.println("*** 379 ColOfData, calculateNumberOfDistinctLegalValues()");
        }
        str_al_DistinctValues = new ArrayList();
        int nCases = str_al_TheCases.size();
        nDistinctLegalValues = 1;
        String[] tempData = new String[nCases];
        tempData[0] = str_al_TheCases.get(0);
        
        for (int ith = 0; ith < nCases; ith++) {
            tempData[ith] = str_al_TheCases.get(ith);
            
            if (tempData[ith].equals("*")) {
                hasMissingData = true;
            }
        }    
        
        Arrays.sort(tempData, 0, nCases);
        str_al_DistinctValues.add(tempData[0]);
        
        for (int i = 1; i < nCases; i++) {            
            if (!(tempData[i].equals(tempData[i - 1]))) {
                nDistinctLegalValues++;
                str_al_DistinctValues.add(tempData[i]);
            }
        }
        
        if (hasMissingData) { nDistinctLegalValues--; }
        if (printTheStuff) {
            System.out.println("--- 407 ColOfData, END calculateNumberOfDistinctLegalValues()");
            System.out.println("--- 408 ColOfData, nDistinctLegalValues = " + nDistinctLegalValues);
        }        
        return nDistinctLegalValues;
    }  
    
    public void formatTheColumn() {
        nStrings = str_al_TheCases.size();
        maxSigDecimals = 0;
        textBoxLen = 7;
        maxLen_FormattedString = 0;
        minOrdMag = 100;
        maxOrdMag = 0;

        determineMaxOrdOfMag();
         
        necessarylength = maxOrdMag + maxSigDecimals + 1;
        overFlow = necessarylength - textBoxLen;
        
        if (overFlow > 0) {
            maxSigDecimals = maxSigDecimals - overFlow;
            necessarylength = maxOrdMag + maxSigDecimals + 1;
        }
        
        if (maxSigDecimals < 0) { maxSigDecimals = 0; }

        strNumericStringFormat = "%" + necessarylength + "." + String.valueOf(maxSigDecimals) + "f";
        determineMaxLengthOfFormattedString();

        maxOrdMag++;

        formatTheCases();
        hasBeenFormatted = true;
    }

    private void determineMaxOrdOfMag() {
        for (int ithString = 0; ithString < nStrings; ithString++) { 
            if (DataUtilities.strIsADouble(str_al_TheCases.get(ithString))) {              
                str_ValueOfString = str_al_TheCases.get(ithString);
                decimalPosition = str_ValueOfString.indexOf('.');
                dbl_ParsedValue = Double.parseDouble(str_ValueOfString);
                int thisOrdMag = (int)Math.log10(Math.abs(dbl_ParsedValue));
                
                if (thisOrdMag > maxOrdMag) {
                    maxOrdMag = thisOrdMag;
                }
                
                if (thisOrdMag < minOrdMag) {
                    minOrdMag = thisOrdMag;
                }

                lengthOfString = str_ValueOfString.length();
                
                if (decimalPosition == -1) {
                    adjusted_sigDecimals = maxSigDecimals;
                } else {
                    adjusted_sigDecimals = lengthOfString - decimalPosition - 1; 
                }

                if (adjusted_sigDecimals > maxSigDecimals) {
                    maxSigDecimals = adjusted_sigDecimals;
                }
            }
        }   //  End ithString loop        
    }
    
    private void determineMaxLengthOfFormattedString() {
        for (int jthString = 0; jthString < nStrings; jthString++) {               
            if (DataUtilities.strIsADouble(str_al_TheCases.get(jthString))) {                
                str_ValueOfString = str_al_TheCases.get(jthString);
                dbl_ParsedValue = Double.parseDouble(str_ValueOfString);
                strFormatted = String.format(strNumericStringFormat, dbl_ParsedValue);  
                
                if (strFormatted.length() > maxLen_FormattedString) {
                    maxLen_FormattedString = strFormatted.length();
                }
            }
        }         
    }  
    
    private void formatTheCases() {        
        if (maxLen_FormattedString > textBoxLen) {
            adjusted_sigDecimals = textBoxLen - maxOrdMag - 1;
        }
        else {
            adjusted_sigDecimals = maxLen_FormattedString - maxOrdMag - 1;
        }    
        
        for (int kthString = 0; kthString < nStrings; kthString++) {
            
            strRawCase = str_al_TheCases.get(kthString);
            strFormatted = "";
            
            if (DataUtilities.strIsADouble(strRawCase)) {
                str_ValueOfString = strRawCase; 
                dbl_ParsedValue = Double.parseDouble(str_ValueOfString);
                strFormatted = String.format(strNumericStringFormat, dbl_ParsedValue);
                strFormatted = StringUtilities.getStringOfNSpaces(textBoxLen - strFormatted.length()) + strFormatted; 
            }
            else {
                if (strFormatted.equals("")) {
                    strFormatted = strRawCase;
                } 
            } 
            str_al_FormattedCases.set(kthString, strFormatted);
        }        
    }
    
    public void deleteThisRow(int thisOne) {
        str_al_TheCases.remove(thisOne);
        nCasesInColumn--;
    }
    
    public void insertInThisRow(int thisOne) {
        str_al_TheCases.add(thisOne, "*");
        str_al_FormattedCases.add(thisOne, "*");
        nCasesInColumn++;
    }   
    
    /************************************************************
     *   DANGER!  This routine only randomizes all the cases,   *
     *   intended to make randomization code more efficient. No *
     *   other action is anticipated, and the object should be  *
     *   regarded as unstable and contain incorrect information *
     *   Best practice would be to only use this procedure for  *
     *   very temporary objects with minimal scope.             *
     ***********************************************************/
    public void randomizeTheCases() { Collections.shuffle(str_al_TheCases);}
    
    public int getSigDig() { return significantDigits; }
    public void setSigDig(int toThisNumberOfDigits) { 
        significantDigits = toThisNumberOfDigits;
        strFormatString = "%." + String.valueOf(significantDigits)+"f";
    }
    
    public String getFormatString() { return strFormatString; }
    public void setFormatString(int toThisSigDig) { setSigDig(toThisSigDig);  }
    
    public int getNCategorical() { return nCategorical; }
    public int getNMissing() { 
        nLegals = getNLegalQuantCasesInColumn();
        nMissing = nCasesInColumn - nLegals;
        return nMissing; 
    }
    public boolean getHasBeenFormatted() { return hasBeenFormatted; }
    
    public ColumnOfData getColumnOfData() {return this; }
    
    public String getStrMissing() { return strMissingValue; }
    public void setStrMissing(String toThisInfo) {
        strMissingValue = toThisInfo;
    }
    
    public String getVarLabel() { return strVarLabel; } 
    public void setVarLabel(String toThis) { strVarLabel = toThis; }    
    
    public String getVarDescription () { return strVarDescription; }
    public void setVarDescription(String toThis) { strVarDescription = toThis; }
    
    public boolean getIsBlank() { return containsBlanks; }
    public void setIsBlank(boolean yn_IsBlank) { containsNumerics = yn_IsBlank; }

    public boolean getAnyonesGuess() { return containsNumerics; }
    
    //public boolean getIsZeroOne() { return containsZeroOnes; }
        
    public String getDataType() { 
        if (printTheStuff == true) {
            //System.out.println("564 --- ColumnOfData, getDataType(), " + dataType);
        }        
        return dataType; 
    }
    
    public void setDataType(String toThis) {
        if (printTheStuff == true) {
            //System.out.println("571 --- ColumnOfData, setDataType() to" + toThis);
        } 
        dataType = "Undetermined";
        if (toThis.equals("Quantitative")) { dataType = toThis; }
        if (toThis.equals("Categorical")) { dataType = toThis; }
    }
    
    public int getNCasesInColumn() { return nCasesInColumn; }
    
    public int getNLegalQuantCasesInColumn() {
        //System.out.println("581 ColumnOfData, dataType = " + dataType);
        int numberOfQuants = 0;
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++ ) {
            //System.out.println("584 ColumnOfData, getStringInIthRow(ithCase) = " + getStringInIthRow(ithCase));
            if (DataUtilities.strIsADouble(getStringInIthRow(ithCase))) {
                //System.out.println("586 ColumnOfData, getStringInIthRow(ithCase) = " + getStringInIthRow(ithCase));
                numberOfQuants++;
            }
        }
        //System.out.println("590 ColumnOfData, nLegals = " + numberOfQuants);
        return numberOfQuants;
    }
    public int getColumnSize() { return str_al_TheCases.size(); }  
    
    public double[] getLegalCases_asDoubles() { 
        int nLegalCases = getNLegalQuantCasesInColumn();
        //System.out.println("597 ColumnOfData, nLegalCases = " + nLegalCases);
        //System.out.println("598 ColumnOfData, nCasesInColumn = " + nCasesInColumn);
        double[] theDblCases = new double[nLegalCases]; 
        int thisCase = 0;
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++ ) {
            //System.out.println("602 ColumnOfData, getStringInIthRow(ithCase) = " + getStringInIthRow(ithCase));
            if (DataUtilities.strIsADouble(getStringInIthRow(ithCase))) {
                theDblCases[ithCase] = Double.parseDouble(getStringInIthRow(ithCase));
                //System.out.println("605 ColumnOfData, theDblCases[ithCase] = " + theDblCases[ithCase]);
                thisCase++;
            }
        }        
        return theDblCases; 
    }

    public String[] getTheCases_asStrings() { 
        int nCases = this.getColumnSize();
        String[] theStrCases = new String[nCases];        
        for (int ithCase = 0; ithCase < nCases; ithCase++ ) {
            theStrCases[ithCase] = getStringInIthRow(ithCase);
        }        
        return theStrCases; 
    }

    public ArrayList<String> getTheCases_ArrayList() { return str_al_TheCases; }
    
    public ArrayList<String> getTheFormattedCases() {
        formatTheColumn();
        return str_al_FormattedCases; 
    }
    
    // For data as is
    public String getIthCase(int ithCase) {
        if (ithCase < getColumnSize()) {
            return str_al_TheCases.get(ithCase);
        } else {
            return "*";
        }
    }
    
    // For formatted quant data
    public String getIthFormattedCase(int ithCase) {
        if (ithCase < getColumnSize()) {
            return str_al_FormattedCases.get(ithCase);
        } else {
            return "*";
        }
    }
    
    public int getNumberOfDistinctValues() { 
        return calculateNumberOfDistinctLegalValues(); 
    }
    
    public boolean getHasMissingData() {
        hasMissingData = false;        
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {            
            if (str_al_TheCases.get(ithCase).equals(strMissingValue)) {
               nMissing++;
               hasMissingData = true;
            }
        }  
        return hasMissingData;
    }
    
    public boolean getColumnIsEmpty() {
        boolean colIsEmpty = true;        
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {            
            if (!str_al_TheCases.get(ithCase).equals("*")) {
                colIsEmpty = false;
            }
        }
        return colIsEmpty;
    }
    
    public String getReturnStatus() { 
        //System.out.println("658 --- ColumnOfData, getReturnStatus() = " + strReturnStatus);
        return strReturnStatus; }


    @Override
    public String toString() {  
        System.out.println("\n  Col of Data -- toString =========================");
        System.out.println("Var Label = " + strVarLabel + "; nCasesInColumn = " + nCasesInColumn + "\n");
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++){
           System.out.print("\n x  " + str_al_TheCases.get(ithCase) + " x ");
        }
        System.out.println("\n  =============== Col of Data -- end toString");
        return "ColumnOfData.toString() -- end";
    }      
}
