/************************************************************
 *                        ColumnOfData                      *
 *                          05/27/24                        *
 *                           12:00                          *
 ***********************************************************/
package dataObjects;

import utilityClasses.DataCleaner;
import java.util.ArrayList;
import java.util.Arrays;
import splat.*;
import utilityClasses.StringUtilities;
import dialogs.*;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class ColumnOfData {
    //  POJOs
    boolean containsBlanks, containsNumerics, containsZeroOnes, 
            containsCats, hasBeenFormatted, hasMissingData;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nStrings, decimalPosition, lengthOfString, adjusted_sigDecimals,
        maxSigDecimals, maxLen_FormattedString, maxOrdMag, textBoxLen,
        necessarylength, overFlow, minOrdMag, nDistinctLegalValues; 

    private int nCasesInColumn, nCategorical, nMissing, significantDigits;
    
    double dbl_ParsedValue;
    
    String str_ValueOfString, strNumericStringFormat, strFormatted, strRawCase,
           strVarLabel, strVarDescription, strMissingValue, strVarDisplayFormat,
           strFormatString;    

    ArrayList<String> str_al_TheCases, str_al_DistinctValues, str_al_FormattedCases;
    
    // My classes
    Data_Manager dm;

    public ColumnOfData() { 
        if (printTheStuff == true) {
            System.out.println("44 *** ColumnOfData, constructing");
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
    } 

    // This constructor should not have to look at the data??  Where called??
    public ColumnOfData (ColumnOfData dataColumn) {  // Copy constructor
        if (printTheStuff == true) {
            System.out.println("65 *** ColumnOfData, constructing");
        }
        containsNumerics = dataColumn.getIsNumeric();
        strVarLabel = dataColumn.getVarLabel();
        str_al_TheCases = new ArrayList<>(); 
        str_al_FormattedCases = new ArrayList<>();
        nCasesInColumn = dataColumn.getColumnSize();
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found;
        //containsGuesses = false;
        strVarLabel = dataColumn.getVarLabel();
        strVarDescription = dataColumn.getVarDescription();
        strMissingValue = "*"; 
        nDistinctLegalValues = dataColumn.getNumberOfDistinctValues();
        
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String textToAdd = dataColumn.getStringInIthRow(ithCase);
            str_al_TheCases.add(textToAdd);
        } 
        determineDataType();
    }
    
    public ColumnOfData(int nCasesInColumn, String varLabel) {
        if (printTheStuff == true) {
            System.out.println("91 *** ColumnOfData, constructing");
        }
        this.nCasesInColumn = nCasesInColumn;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        strVarDescription = varLabel;
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found;
        //containsGuesses = false;
        strMissingValue = "*";
        significantDigits = 0;
        strFormatString = "%.0f";
        nDistinctLegalValues = 0;
        determineDataType();
    }
    
    // This constructor creates an empty column of data; only used at startup.
    public ColumnOfData(Data_Manager dm, int nCasesInColumn, String varLabel) {
        if (printTheStuff == true) {
            System.out.println("119 *** ColumnOfData, constructing");
        }
        this.nCasesInColumn = nCasesInColumn;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        strVarDescription = varLabel;
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found;
        strMissingValue = "*";
        significantDigits = 0;
        strFormatString = "%.0f";
        nDistinctLegalValues = 0;
        determineDataType();
    }

    // This constructor is used when doing two-way ANOVA
    public ColumnOfData(CategoricalDataVariable catDatVar) {
        if (printTheStuff == true) {
            System.out.println("146 *** ColumnOfData, constructing");
        }
        nCasesInColumn = catDatVar.get_N();
        String daData[] = new String[nCasesInColumn];
        daData = catDatVar.getDataAsStrings();
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) { 
            str_al_TheCases.add(daData[ithCase]);
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = catDatVar.getTheDataLabel();
        strVarDescription = strVarLabel;
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        containsZeroOnes = true;    // Rendered false if non-zero real is found;
        strMissingValue = "*";
        significantDigits = 0;
        strFormatString = "%.0f";
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }

    public ColumnOfData(QuantitativeDataVariable qdv) {
        if (printTheStuff == true) {
            System.out.println("174 *** ColumnOfData, constructing");
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
        containsZeroOnes = true;    // Rendered false if non-zero real is found;
        strMissingValue = "*";
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by the BivariateCatagoricalDataObj
    public ColumnOfData(Data_Manager dm, String varLabel, String varDescription, ArrayList<String> theData) {
        if (printTheStuff == true) {
            System.out.println("194 *** ColumnOfData, constructing");
        }
        this.nCasesInColumn = theData.size();
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData.get(iCase));
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        this.strVarDescription = varDescription;
        containsBlanks = false;
        containsNumerics = true;
        containsZeroOnes = true;
        strMissingValue = "*";
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    public ColumnOfData(String varLabel, String varDescription, ArrayList<String> al_theData) {
        if (printTheStuff == true) {
            System.out.println("217 *** ColumnOfData, constructing");
        }
        this.nCasesInColumn = al_theData.size();
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(al_theData.get(iCase));
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        this.strVarDescription = varDescription;
        containsBlanks = false;
        containsNumerics = true;
        containsZeroOnes = true;
        strMissingValue = "*";
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by Logistic_Controller
    public ColumnOfData(Data_Manager dm, String varLabel, String varDescription, String[] theData) {
        if (printTheStuff == true) {
            System.out.println("241 *** ColumnOfData, constructing");
        }
        this.nCasesInColumn = theData.length;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData[iCase]);
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = varLabel;
        this.strVarDescription = varDescription;
        containsBlanks = false;
        containsNumerics = true;
        containsZeroOnes = true;
        strMissingValue = "*";
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
            String secondLine = "Change categorical values to 'Missing' for this variable?";
            MyAlerts.showAmbiguousColumnAlert(strVarLabel);                               
            MyDialogs newDiag = new MyDialogs();
            String replaceMissing = newDiag.YesNo(1, "Ambiguous data?", secondLine);
            
            if (replaceMissing.equals("Yes")) {
                
                for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
                    String ithString = str_al_TheCases.get(ithCase);
                    
                    if (!ithString.equals("*") && (!DataUtilities.strIsADouble(ithString))) {
                        str_al_TheCases.set(ithCase, "*");
                    }
                }  
                containsNumerics = true;
            }
        }
    } 

    public void cleanTheColumn(Data_Manager dm, int thisCol) {
        DataCleaner dc = new DataCleaner(dm, dm.getAllTheColumns()
                                               .get(thisCol));
        nCasesInColumn = dm.getNCasesInStruct();
        dc.cleanAway();
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

    public void determineMaxOrdOfMag() {
        
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
    
    public void determineMaxLengthOfFormattedString() {
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
    
    public void formatTheCases() {        
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
    
    public int getSigDig() { return significantDigits; }
    public void setSigDig(int toThisNumberOfDigits) { 
        significantDigits = toThisNumberOfDigits;
        strFormatString = "%." + String.valueOf(significantDigits)+"f";
    }
    
    public String getFormatString() { return strFormatString; }
    public void setFormatString(int toThisSigDig) { setSigDig(toThisSigDig);  }
    
    public int getNCategorical() { return nCategorical; }
    public int getNMissing() { return nMissing; }
    public boolean getHasBeenFormatted() { return hasBeenFormatted; }
    
    public ColumnOfData getColumnOfData() {return this; }
    
    public String getGenericVarInfo() { return strMissingValue; }
    public void setGenericVarInfo(String toThisInfo) {
        strMissingValue = toThisInfo;
    }
    
    public String getVarLabel() { return strVarLabel; } 
    public void setVarLabel(String toThis) { strVarLabel = toThis; }    
    
    public String getVarDescription () { return strVarDescription; }
    public void setVarDescription(String toThis) { strVarDescription = toThis; }
    
    public boolean getIsBlank() { return containsBlanks; }
    public void setIsBlank(boolean yn_IsBlank) { containsNumerics = yn_IsBlank; }

    public boolean getIsNumeric() { return containsNumerics; }
    public void setIsNumeric(boolean yn_IsNumeric) { 
        containsNumerics = yn_IsNumeric; 
    }
    
    public boolean getAnyonesGuess() { return containsNumerics; }
    public boolean getIsZeroOne() { return containsZeroOnes; }
    
    public String getDataType() { 
        if (containsNumerics) {
            return "Quantitative";
        }
        else 
        if (containsCats) {
            return "Categorical";
        }
        else 
        if (containsZeroOnes) {
            return "ZeroOne";
        }
        else 
        return "Clueless";
    }
    
    /*
    public String xgetDisplayFormat() { return strVarDisplayFormat; }
    public void setDisplayFormat(String toThisFormat) {
        strVarDisplayFormat = toThisFormat;
    }
    */
    
    public int getNCasesInColumn() { return nCasesInColumn; }
    
    public int getNLegalCasesInColumn() {
        int nLegals;
        if (!getHasMissingData()) {
            nLegals = nCasesInColumn; 
        } else {
            nLegals = nCasesInColumn - nMissing;    
        }
        return nLegals;
    }
    public int getColumnSize() { return str_al_TheCases.size(); }  
    
    public double[] getTheCases_asDoubles() { 
        int nCases = getColumnSize();
        double[] theDblCases = new double[nCases];        
        for (int ithCase = 0; ithCase < nCases; ithCase++ ) {
            theDblCases[ithCase] = Double.parseDouble(getStringInIthRow(ithCase));
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
