/************************************************************
 *                        ColumnOfData                      *
 *                          11/17/24                        *
 *                           12:00                          *
 ***********************************************************/
package dataObjects;

import utilityClasses.DataCleaner;
import java.util.ArrayList;
import java.util.Arrays;
import splat.*;
import utilityClasses.StringUtilities;
import utilityClasses.DataUtilities;
import utilityClasses.MyYesNoAlerts;

public class ColumnOfData {
    //  POJOs
    boolean containsNumerics, containsCats, hasMissingData, columnIsFormatted,
            containsData;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nStrings, decimalPosition, lengthOfString, adjusted_sigDecimals,
        maxSigDecimals, maxLen_FormattedString, maxOrdMag, textBoxLen,
        necessarylength, overFlow, minOrdMag, nDistinctLegalValues; 

    private int nCasesInColumn, nCategorical, nMissing, significantDigits;
    
    double dbl_ParsedValue;
    
    String str_ValueOfString, strNumericStringFormat, strFormatted, strRawCase,
           strVarLabel, strVarDescr, strMissingValue, strFormatString;    

    ArrayList<String> str_al_TheCases, str_al_DistinctValues, str_al_FormattedCases;
    
    // My classes
    Data_Manager dm;

    public ColumnOfData() { 
        if (printTheStuff) {
            System.out.println("\n42 *** ColumnOfData, constructing");
        }
        str_al_TheCases = new ArrayList<>(); 
        str_al_FormattedCases = new ArrayList<>();
        nCasesInColumn = 0;
        strVarLabel = "No Label";
        strVarDescr = "No Description";
        containsCats = false;
        significantDigits = 0;
        strFormatString = "%.0f";
        nDistinctLegalValues = 0;
        columnIsFormatted = false;
    } 

    // This constructor should not have to look at the data??  Where called??
    public ColumnOfData (ColumnOfData dataColumn) {  // Copy constructor
        if (printTheStuff) {
            System.out.println("\n59 *** ColumnOfData, constructing");
            System.out.println("60 Label = " + dataColumn.getVarLabel());
        }
        doSomeInitializations();
        containsNumerics = dataColumn.getIsNumeric();
        strVarLabel = dataColumn.getVarLabel();
        str_al_TheCases = new ArrayList<>(); 
        str_al_FormattedCases = new ArrayList<>();
        nCasesInColumn = dataColumn.getColumnSize();
        strVarLabel = dataColumn.getVarLabel();
        strVarDescr = dataColumn.getVarDescription();
        columnIsFormatted = dataColumn.getDataAreFormatted();
        nDistinctLegalValues = dataColumn.getNumberOfDistinctValues();
        
        if (printTheStuff) {
            System.out.println("74 --- ColOfData, nCasesInColumn = " + nCasesInColumn);
        }

        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String textToAdd = dataColumn.getStringInIthRow(ithCase);
            str_al_TheCases.add(textToAdd);
        } 
        determineDataType();
    }
    
    public ColumnOfData(int nCasesInColumn, String varLabel) {
        if (printTheStuff) {
            System.out.println("\n86 *** ColumnOfData, constructing");
            System.out.println("87 --- Label = " + varLabel);
            System.out.println("88 --- ColOfData, nCasesInColumn = " + nCasesInColumn);
        }
        doSomeInitializations();
        this.nCasesInColumn = nCasesInColumn;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = varLabel;
        strVarDescr = varLabel;
        determineDataType();
    }
    
    // This constructor creates an empty column of data; only used at startup.
    public ColumnOfData(Data_Manager dm, int nCasesInColumn, String varLabel) {
        if (printTheStuff) {
            System.out.println("1\n107 *** ColumnOfData, constructing");
            System.out.println("108 --- Label = " + varLabel);
            System.out.println("109 --- nCasesInColumn = " + nCasesInColumn);
        }
        
        doSomeInitializations();
        this.nCasesInColumn = nCasesInColumn;
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add("*");
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = varLabel;
        strVarDescr = varLabel;
    }

    // This constructor is used when doing two-way ANOVA
    public ColumnOfData(CategoricalDataVariable catDatVar) {
        if (printTheStuff) {
            System.out.println("128 *** ColumnOfData, constructing from CatDataVar");
            System.out.println("129 --- Label = " + catDatVar.getTheDataLabel());   
        }
        doSomeInitializations();
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
        strVarDescr = strVarLabel;
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }

    public ColumnOfData(QuantitativeDataVariable qdv) {
        if (printTheStuff) {
            System.out.println("\n150 *** ColumnOfData, constructing from qdv");
            System.out.println("151 --- Label = " + qdv.getTheVarLabel()); 
        }
        doSomeInitializations();
        nCasesInColumn = qdv.getLegalN();
        if (printTheStuff) {
            System.out.println("156 --- ColumnOfData, nCasesInCol = " + nCasesInColumn);
        }
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        str_al_TheCases = qdv.getLegalCases_AsALStrings();
        strVarLabel = qdv.getTheVarLabel();
        strVarDescr = qdv.getTheVarDescription();
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by the BivariateCatagoricalDataObj
    public ColumnOfData(Data_Manager dm, String varLabel, String varDescription, ArrayList<String> theData) {
        if (printTheStuff) {
            System.out.println("\n170 *** ColumnOfData, constructing fromn dm with ArrayList<String> theData");
            System.out.println("171 --- Label = " + varLabel); 
        }
        doSomeInitializations();
        nCasesInColumn = theData.size();
        if (printTheStuff) {
            System.out.println("176 --- ColOfData, nCasesInColumn = " + nCasesInColumn);
        }
        
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData.get(iCase));
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = varLabel;
        this.strVarDescr = varDescription;
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        if (printTheStuff) {
            System.out.println("190 --- ColumnOfData, constructing");
        }
        
        determineDataType();
        
        if (printTheStuff) {
            System.out.println("196 --- ColumnOfData, constructing");
        }
    }
    
    public ColumnOfData(String varLabel, String varDescription, ArrayList<String> al_theData) {
        if (printTheStuff) {
            System.out.println("\n202 *** ColumnOfData, constructing ArrayList<String> al_theData");
            System.out.println("203 --- Label = " + varLabel); 
        }
        doSomeInitializations();
        nCasesInColumn = al_theData.size();
        if (printTheStuff) {
            System.out.println("208 --- ColOfData, nCasesInColumn = " + nCasesInColumn);
        }
        
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(al_theData.get(iCase));
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = varLabel;
        this.strVarDescr = varDescription;
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
    // Needed by Logistic_Controller
    public ColumnOfData(Data_Manager dm, String varLabel, String varDescription, String[] theData) {
        if (printTheStuff) {
            System.out.println("\n227 *** ColumnOfData, constructing from dm and String[] theData");
            System.out.println("228 --- Label = " + varLabel); 
        }
        doSomeInitializations();
        nCasesInColumn = theData.length;
        if (printTheStuff) {
            System.out.println("233 ColOfData, nCasesInColumn = " + nCasesInColumn);
        }
        
        str_al_TheCases = new ArrayList<>();
        str_al_FormattedCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            str_al_TheCases.add(theData[iCase]);
            str_al_FormattedCases.add("*");
        }
        
        strVarLabel = varLabel;
        this.strVarDescr = varDescription;
        nDistinctLegalValues = calculateNumberOfDistinctLegalValues();
        determineDataType();
    }
    
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
            System.out.println("267 --- ColumnOfData, determineDataType()");
        }
        doSomeInitializations();
        if (printTheStuff) {
            System.out.println("271 --- ColumnOfData, constructing from qdv");
        }
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String ithString = str_al_TheCases.get(ithCase);
            boolean isaDouble = DataUtilities.strIsADouble(ithString);
            
            if (isaDouble) { containsNumerics = true; }

            if (!ithString.equals("*") && (!isaDouble)) {
                containsCats = true;
            }
            
            /*************************************************************
            *  Guard against choosing columns with no data. E.g. column  *
            *  added but never populated with data.                      *
            *************************************************************/
            if (!ithString.equals("*")) {
                containsData = true;
            }
        }

        if (containsCats && containsNumerics) {
            MyYesNoAlerts myYesNoAlerts = new MyYesNoAlerts();
            myYesNoAlerts.showAmbiguousColumnAlert(strVarLabel, "Yes", "No"); 
            String yesOrNo = myYesNoAlerts.getYesOrNo();
            if (yesOrNo.equals("Yes")) {
                for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
                    String ithString = str_al_TheCases.get(ithCase);
                    if (!ithString.equals("*") && (!DataUtilities.strIsADouble(ithString))) {
                        str_al_TheCases.set(ithCase, "*");
                    }
                }  
                containsNumerics = true;
            } else {
                setIsNumeric(false);
            }
        }
    } 

    public void cleanTheColumn(Data_Manager dm, int thisCol) {
        if (printTheStuff) {
            System.out.println("312 --- ColumnOfData, cleanTheColumn");
        }
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
        if (printTheStuff) {
            System.out.println("346 --- ColumnOfData, calculateNumberOfDistinctLegalValues()");
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
        columnIsFormatted = true;
    }

    public void determineMaxOrdOfMag() {
        if (printTheStuff) {
            System.out.println("405 --- ColumnOfData, determineMaxOrdOfMag()");
        } 
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
        if (printTheStuff) {
            System.out.println("439 --- ColumnOfData, determineMaxLengthOfFormattedString()");
        } 
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
       //if (getDataAreFormatted()) { return; }
        if (printTheStuff) {
            System.out.println("457 --- ColumnOfData, formatTheCases()");
        } 
        if (maxLen_FormattedString > textBoxLen) {
            adjusted_sigDecimals = textBoxLen - maxOrdMag - 1;
        } else {
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
            } else {
                if (strFormatted.equals("")) {
                    strFormatted = strRawCase;
                } 
            } 
            str_al_FormattedCases.set(kthString, strFormatted);
        } 
        
        columnIsFormatted = true;
    }
    
    private void doSomeInitializations() {
        strMissingValue = "*";
        containsNumerics = false;
        containsCats = false;
        significantDigits = 0;
        strFormatString = "%.0f";
        columnIsFormatted = false;  
        containsData = false;
        nDistinctLegalValues = 0;
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
    public boolean getDataAreFormatted() { return columnIsFormatted; }
    
    public ColumnOfData getColumnOfData() {return this; }
    
    public boolean getContainsData() {return containsData; }
    
    public String getGenericVarInfo() { return strMissingValue; }
    public void setGenericVarInfo(String toThisInfo) {
        strMissingValue = toThisInfo;
    }
    
    public String getVarLabel() { return strVarLabel; } 
    public void setVarLabel(String toThis) { strVarLabel = toThis; }    
    
    public String getVarDescription () { return strVarDescr; }
    public void setVarDescription(String toThis) { strVarDescr = toThis; }

    public boolean getIsNumeric() { return containsNumerics; }
    public void setIsNumeric(boolean yn_IsNumeric) { 
        containsNumerics = yn_IsNumeric; 
    }
    
    public String getDataType() { 
        if (containsNumerics) {
            return "Quantitative";
        }
        else 
        if (containsCats) {
            return "Categorical";
        }
        else 
        return "Clueless";
    }
    
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
        System.out.println("\n  Col of Data -- toString()");
        System.out.println("Var Label = " + strVarLabel + "; nCasesInColumn = " + nCasesInColumn + "\n");
        
        //for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++){
        
        for (int ithCase = 0; ithCase < 5; ithCase++){
           System.out.print("\n x  " + str_al_TheCases.get(ithCase) + " x ");
        }
        System.out.println("Col of Data -- end toString\n");
        return "ColumnOfData.toString() -- end";
    }      
}
