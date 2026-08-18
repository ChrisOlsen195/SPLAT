/************************************************************
 *                        ColumnOfData                      *
 *                          08/12/26                        *
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
    boolean containsBlanks, containsNumerics, //containsZeroOnes, 
            containsCats, hasBeenFormatted, hasMissingData,
            columnIsClean;
    
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
           strFormatString, strDataType;    
    
    String strReturnStatus;

    ArrayList<String> str_al_TheCases, str_al_DistinctValues, str_al_FormattedCases;
    
    // My classes
    MyYesNoAlerts myYesNoAlerts;
    Data_Manager dm;

    public ColumnOfData() { 
        if (printTheStuff) {
            System.out.println("*** 48 ColOfData, constructing blank");
        }
        doSomeCommonInitializations();
        significantDigits = 0;
        strFormatString = "%.0f";
        hasBeenFormatted = false;
    } 

    // This constructor should not have to look at the data??  Where called??
    public ColumnOfData (ColumnOfData dataColumn) {  // Copy constructor
        if (printTheStuff) {
            System.out.println("*** 59 ColumnOfData, constructing from ColOfData");
        }
        doSomeCommonInitializations();
        strDataType = dataColumn.getStrDataType();
        strVarLabel = dataColumn.getVarLabel();
        nCasesInColumn = dataColumn.getColumnSize();
        strVarLabel = dataColumn.getVarLabel();
        strVarDescription = dataColumn.getVarDescription(); 
        nDistinctLegalValues = dataColumn.getNumberOfDistinctValues();

        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String textToAdd = dataColumn.getStringInIthRow(ithCase);
            str_al_TheCases.add(textToAdd);
        } 
        determineDataType();
    }
    
    public ColumnOfData(int nCasesInColumn, String strVarLabel) {
        if (printTheStuff) {
            System.out.println("*** 78 ColumnOfData, constructing from nCases and varLabel");
        }
        doSomeCommonInitializations();
        this.nCasesInColumn = nCasesInColumn;
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = strVarLabel;
        significantDigits = 0;
        strFormatString = "%.0f";
        determineDataType();
    }
    
    // This constructor creates an empty column of data; only used at startup.
    public ColumnOfData(Data_Manager dm, int nCasesInColumn, String strVarLabel) {
        if (printTheStuff) {
            System.out.println("\n*** 96 ColumnOfData, constructing from dm, nCases and strVarLabel");
            System.out.println("... 97 ColumnOfData, strVarLabel = " + strVarLabel);
        }
        doSomeCommonInitializations();
        this.nCasesInColumn = nCasesInColumn;
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = strVarLabel;
        significantDigits = 0;
        strFormatString = "%.0f";
        determineDataType();
    }

    // This constructor is used when doing two-way ANOVA
    public ColumnOfData(CategoricalDataVariable catDatVar) {
        if (printTheStuff) {
            System.out.println("*** 115 ColumnOfData, constructing from catDatVar");
        }
        doSomeCommonInitializations();
        nCasesInColumn = catDatVar.get_N();
        String daData[] = new String[nCasesInColumn];
        daData = catDatVar.getDataAsStrings();
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) { 
            str_al_TheCases.add(daData[ithCase]);
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = catDatVar.getTheDataLabel();
        significantDigits = 0;
        strFormatString = "%.0f";
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }

    public ColumnOfData(QuantitativeDataVariable qdv) {
        if (printTheStuff) {
            System.out.println("*** 135 ColumnOfData, constructing from qdv");
        }
        doSomeCommonInitializations();
        nCasesInColumn = qdv.getLegalN();
        str_al_TheCases = qdv.getLegalCases_AsALStrings();
        strVarLabel = qdv.getTheVarLabel();
        strVarDescription = qdv.getTheVarDescription();
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by the BivariateCatagoricalDataObj
    public ColumnOfData(Data_Manager dm, String strVarLabel, String strVarDescription, ArrayList<String> theData) {
        if (printTheStuff) {
            System.out.println("*** 149 ColumnOfData, constructing from dm, varLabel, varDescr, ArrayList<String> theData");
        }
        doSomeCommonInitializations();
        nCasesInColumn = theData.size();
        this.strVarLabel = strVarLabel;
        this.strVarDescription = strVarDescription;
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData.get(iCase));
            str_al_FormattedCases.add("*");
        }

        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
        if (printTheStuff) {
            System.out.println("... 163 ColumnOfData, END constructing from dm, varLabel, varDescr, ArrayList<String> theData");
        }
    }
    
    public ColumnOfData(String strVarLabel, String strVarDescription, ArrayList<String> al_theData) {
        if (printTheStuff) {
            System.out.println("*** 169 ColumnOfData, constructing from varLabel, varDescr, al_Data");
        }
        doSomeCommonInitializations();
        nCasesInColumn = al_theData.size();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(al_theData.get(iCase));
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = strVarLabel;
        this.strVarDescription = strVarDescription;
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by Logistic_Controller
    public ColumnOfData(Data_Manager dm, String strVarLabel, String strVarDescription, String[] theData) {
        if (printTheStuff) {
            System.out.println("*** 187 ColumnOfData, constructing from dm, varLabel, varDescr, String[] theData");
        }
        doSomeCommonInitializations();
        nCasesInColumn = theData.length;
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData[iCase]);
            str_al_FormattedCases.add("*");
        }
        
        this.strVarLabel = strVarLabel;
        this.strVarDescription = strVarDescription;
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
        if (printTheStuff) {
            System.out.println("--- 213 addNCasesOfThese, nNewCases / ofThese = " + nNewCases + " / " + ofThese);
        } 
        for (int ithNewCase = 0; ithNewCase < nNewCases; ithNewCase++) { 
            str_al_TheCases.add(ofThese);
            str_al_FormattedCases.add("*");
        }
        
        nCasesInColumn += nNewCases;
        formatTheColumn();
    }
    
    public String getTheDataType() { 
        determineDataType();
        return strDataType; 
    }

    public void determineDataType() {
        if (printTheStuff) {
            System.out.println("--- 229 ColumnOfData, determineDataType()");
            System.out.println("... 230 ColumnOfData, strVarLabel = " + strVarLabel);
            System.out.println("... 231 ColumnOfData, strVarDescription = " + strVarDescription);
        }
        setStrDataType("Quantitative"); //  The default
        containsNumerics = false;
        if (printTheStuff) {
            System.out.println("... 236 ColumnOfData, determineDataType(), contains numerics init to false");
        }
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String ithString = str_al_TheCases.get(ithCase);
            boolean isaDouble = DataUtilities.strIsADouble(ithString);
            
            if (isaDouble) { 
                if (!containsNumerics) {
                    containsNumerics = true; 
                    if (printTheStuff) {
                        System.out.println("... 246 ColumnOfData, determineDataType(), contains numerics => true");
                    }
                }
            }
            
            if (!ithString.equals("*") && (!isaDouble)) {
                if (!containsCats) {
                    containsCats = true;
                    if (printTheStuff) {
                        System.out.println("... 255 ColumnOfData, determineDataType(), containsCats => true");
                    }
                }
            }
        }

        if (containsCats && containsNumerics) {
        if (printTheStuff) {
            System.out.println("... 263 ColumnOfData, determineDataType(): contains Cats and Numerics");
        }
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
                setStrDataType("Quantitative");
                if (printTheStuff) {
                    System.out.println("... 278 ColumnOfData, determineDataType() = Quantitative");
                }
            } else {
                setStrDataType("Categorical");
                if (printTheStuff) {
                    System.out.println("... 283 ColumnOfData, determineDataType() = Categorical");
                }
            }
        } else if (containsNumerics) { 
            setStrDataType("Quantitative");
            if (printTheStuff) {
                System.out.println("... 289 ColumnOfData, determineDataType() = Quantitative");
            }
        }
        else { 
            setStrDataType("Categorical");
            if (printTheStuff) {
                System.out.println("... 295 ColumnOfData, determineDataType() = Categorical");
            }
        }
    } 

    public String cleanTheColumn(Data_Manager dm, int thisCol) {
        if (printTheStuff) {
            System.out.println("--- 304 ColumnOfData, cleanTheColumn()");
        }
        DataCleaner dc = new DataCleaner(dm, dm.getAllTheColumns()
                                               .get(thisCol));
        nCasesInColumn = dm.getNCasesInStruct();
        dc.cleanAway();
        strReturnStatus = dc.getStrReturnStatus();
        columnIsClean = dc.getColumnIsClean();
        if (printTheStuff) {
            System.out.println("--- 314 ColumnOfData, strReturnStatus = " + strReturnStatus);
            System.out.println("--- 315 ColumnOfData, columnIsClean = " + columnIsClean);
        }
        if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
        
        String[] fixedData = new String[nCasesInColumn];
        fixedData = dc.getFixedData();
        
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
             setStringInIthRow(ithCase, fixedData[ithCase]); 
        } 
         
        dm.resetTheGrid();
        return "OK";
    }
    
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
            System.out.println("--- 334 ColOfData, calculateNumberOfDistinctLegalValues()");
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
            System.out.println("... 361 ColOfData, END calculateNumberOfDistinctLegalValues()");
            System.out.println("... 362 ColOfData, nDistinctLegalValues = " + nDistinctLegalValues);
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
    
    private void doSomeCommonInitializations(){
        str_al_TheCases = new ArrayList<>(); //
        str_al_FormattedCases = new ArrayList<>(); //
        nCasesInColumn = 0; //
        strVarLabel = "No Label";
        strVarDescription = "No Description";
        strMissingValue = "*";
        containsBlanks = false;
        containsNumerics = false;
        containsCats = false;
        //containsZeroOnes = true;    // Rendered false if non-zero real is found
        significantDigits = 0;
        strFormatString = "%.0f";
        hasBeenFormatted = false;
        nDistinctLegalValues = 0;
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();       
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
        
    public String getStrDataType() { 
        if (printTheStuff) {
            System.out.println("--- 545 ColumnOfData, getDataType(), " + strDataType);
        }        
        return strDataType; 
    }
    
    /************************************************************************
     *                  editColumnHeader() is in Data_Manager               *
     *                setDataType() is in Data_Manager line 791             *
     *              initial DataType set is in File_Ops 181-ish             *
     *                  DataTypes are set in DataCommits also               *
     ***********************************************************************/
    public void setStrDataType(String toThis) {
        if (printTheStuff) {
            System.out.println("--- 558 ColumnOfData, setStrDataType() to " + toThis);
        } 
        strDataType = "Undetermined";
        if (toThis.equals("Quantitative")) { strDataType = "Quantitative"; }
        if (toThis.equals("Categorical")) { strDataType = "Categorical"; }
        if (printTheStuff) {
            System.out.println("... 564 ColumnOfData, setDataType() to" + strDataType);
        } 
    }
    
    public int getNCasesInColumn() { return nCasesInColumn; }
    
    public int getNLegalQuantCasesInColumn() {
        if (printTheStuff) {
            System.out.println("--- 572 ColumnOfData, getNLegalQuantCasesInColumn()");
        }
        int numberOfQuants = 0;
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++ ) {
            if (DataUtilities.strIsADouble(getStringInIthRow(ithCase))) {
                numberOfQuants++;
            }
        }
        return numberOfQuants;
    }
    public int getColumnSize() { return str_al_TheCases.size(); }  
    
    public double[] getLegalCases_asDoubles() { 
        int nLegalCases = getNLegalQuantCasesInColumn();
        double[] theDblCases = new double[nLegalCases]; 
        int thisCase = 0;
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++ ) {
            if (DataUtilities.strIsADouble(getStringInIthRow(ithCase))) {
                theDblCases[ithCase] = Double.parseDouble(getStringInIthRow(ithCase));
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
    
    public String getReturnStatus() { return strReturnStatus; }
    
    public boolean getColumnIsClean() { return columnIsClean; }    


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
