/************************************************************
 *                     StemAndLeaf_Model                    *
 *                          01/16/25                        *
 *                            12:00                         *
 ***********************************************************/
package proceduresOneUnivariate;

import utilityClasses.StringUtilities;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import utilityClasses.MyAlerts;

public class StemNLeaf_Model {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private boolean posNumbersExist, negNumbersExist, ordMagIsPreSet,
            witchesWarned;
    
    private int orderOfMagnitude, nDataPoints, firstNonZeroColumn, 
        firstNonConstantColumn, lengthOfStems, nStems, le_AsInteger, 
        he_AsInteger, bbslFirstNonZeroColumn, bbslFirstNonConstantColumn;
    
    int maxCharsInLine, maxLineInSL, nStemsNeeded_1, nStemsNeeded_2, 
        nStemsNeeded_5, nLeavesNeeded_1, nLeavesNeeded_2, nLeavesNeeded_5;
 
    private double[] data_Sorted, data_ReverseSorted;
    
    private String highestStem, lowestStem, descriptionOfVariable, bbslCheck;
    private String strDaStrippedNumber[];
    private ArrayList<String> data_AsStrings, theStems_With_Vert, theStems_WO_Vert, 
                      oneLineStems, twoLineStems, fiveLineStems;

    // My classes
    private StemNLeaf_View sandL_View;    
    private QuantitativeDataVariable theQDV;
    
    // POJOs / FX
    private TextArea txtArea1, txtArea2, txtArea5;
    
    public StemNLeaf_Model() { }
    
    public StemNLeaf_Model(String descriptionOfVariable,
                           QuantitativeDataVariable theQDV, 
                           boolean presetOrdMag, 
                           int ordMag,
                           int presetFirstNonZero,
                           int presetFirstNonConstant) {
        if (printTheStuff == true) {
            System.out.println("54 *** StemNLeaf_Model, Constructing");
        }
        bbslCheck = descriptionOfVariable;  // if Null, BBSL
        ordMagIsPreSet = presetOrdMag;
        
        if (ordMagIsPreSet) {
            orderOfMagnitude = ordMag;
            bbslFirstNonZeroColumn = presetFirstNonZero;
            bbslFirstNonConstantColumn = presetFirstNonConstant;
        }
        
        this.theQDV = theQDV;
        this.descriptionOfVariable = descriptionOfVariable;
        maxCharsInLine = 100;
        maxLineInSL = 60;
        witchesWarned = false;
        doAllThatSLStuff();
    }
    
    private void doAllThatSLStuff() {
        if (printTheStuff == true) {
            System.out.println("75 --- StemNLeaf_Model, doAllThatSLStuff()");
        }
        txtArea1 = new TextArea();
        txtArea1.setFont(Font.font("Courier New"));
    
        txtArea2 = new TextArea();
        txtArea2.setFont(Font.font("Courier New"));
        
        txtArea5 = new TextArea();
        txtArea5.setFont(Font.font("Courier New"));
        
        nDataPoints = theQDV.get_nDataPointsLegal();
        
        // Data is reverse sorted for the stem and leaf plot.  
        data_ReverseSorted = new double[nDataPoints];
        
        // Sort here is lo to hi
        data_Sorted = theQDV.getTheDataSorted();
        
        initialize();
        sortDataReversedToStringArray();
        constructTheStems();    //  120
        addTheVerticalLine();   //  201
        sortLeavesWithinStems();    //  224
        construct_2LinesPerStem();  //  275
        construct_5LinesPerStem();  //  314
    }
    
    private void initialize() {
        if (printTheStuff == true) {
            System.out.println("105 --- StemNLeaf_Model, initialize()");
        }
        data_AsStrings = new ArrayList<>();
        theStems_With_Vert = new ArrayList<>();
        oneLineStems = new ArrayList<>();
        theStems_WO_Vert = new ArrayList<>();
        twoLineStems = new ArrayList<>();
        fiveLineStems = new ArrayList<>();
        
        posNumbersExist = false; negNumbersExist = false; // leEqualsHe = false;
        
        if (data_Sorted[0] < 0.) {
            negNumbersExist = true;
        }
        
        if (data_Sorted[nDataPoints -1] > 0.) {
            posNumbersExist = true; 
        }
    }
    
    // Data as doubles is sorted before S&L is constructed
    private void sortDataReversedToStringArray() {
        if (printTheStuff == true) {
            System.out.println("128 --- StemNLeaf_Model, sortDataReversedToStringArray()");
        }
        int iData;
        nDataPoints = data_Sorted.length;
        
        for(iData = 0; iData < nDataPoints; iData++) {
            data_ReverseSorted[iData] = data_Sorted[(nDataPoints - 1) - iData];
        }

        for (iData = 0; iData < nDataPoints; iData++) {
            String tempString = String.format("%+012.5f ", data_ReverseSorted[iData]);
            data_AsStrings.add(tempString);
        }               
    }
    
    private void constructTheStems() {
        if (printTheStuff == true) {
            System.out.println("145 --- StemNLeaf_Model, constructTheStems()");
        }
        int iStem;
        
        strDaStrippedNumber = new String[nDataPoints];
        for (int daNumber = 0; daNumber < nDataPoints; daNumber++) {
            strDaStrippedNumber[daNumber] = new String();

            String strDaNumber = data_AsStrings.get(daNumber);
            strDaStrippedNumber[daNumber] = strDaNumber.substring(0, 6)
                                          + strDaNumber.substring(7, 12);
        }
        
        int strippedColLength = strDaStrippedNumber[0].length();        
        // Find first nonZero digit column -- iCol = 0 is sign
        for (int iCol = 1; iCol < strippedColLength; iCol++) {
            boolean nonZerosFound = false;
            
            for (int iDat = 0; iDat < nDataPoints; iDat++) {
                char tempChar = strDaStrippedNumber[iDat].charAt(iCol);
                if (tempChar != '0') {
                    nonZerosFound = true;
                    firstNonZeroColumn = iCol;
                    break;
                }
            }
            if (nonZerosFound) { break; }
        }
        
        if (!ordMagIsPreSet) {
            orderOfMagnitude = 5 - firstNonZeroColumn;
        }    
        
        // Now find the first non-constant (& nonZero) column
        for (int iCol = firstNonZeroColumn; iCol < strippedColLength; iCol++) {
            boolean nonConstantColumnFound = false;
            
            for (int iDat = 1; iDat < nDataPoints; iDat++) {
                char prevTempChar = strDaStrippedNumber[iDat - 1].charAt(iCol);
                char thisTempChar = strDaStrippedNumber[iDat].charAt(iCol);
                
                if (prevTempChar != thisTempChar) {
                    nonConstantColumnFound = true;
                    firstNonConstantColumn = iCol;
                    break;
                }
            }
            if (nonConstantColumnFound) { break; }
        } 
        //System.out.println(" 181 StemNLeafModel, constructTheStems()");
        // If doing a back-to-back, with stems of different ordMags, these
        // variables are preset and found by appealing to the BBSL
        
        if (ordMagIsPreSet) {
            firstNonZeroColumn = bbslFirstNonZeroColumn;
            firstNonConstantColumn = bbslFirstNonConstantColumn;
        }
        
        if ((!ordMagIsPreSet) && (firstNonZeroColumn != firstNonConstantColumn)) {
            orderOfMagnitude = 5 - firstNonConstantColumn;
        }  
        //System.out.println(" 193 StemAndLeaf_Model, constructTheStems()");
        highestStem = new String();
        highestStem = strDaStrippedNumber[0].substring(0,1) 
                      + strDaStrippedNumber[0].substring(firstNonZeroColumn, firstNonConstantColumn + 1);

        lowestStem = new String();
        lowestStem = strDaStrippedNumber[nDataPoints - 1].substring(0,1) 
                      + strDaStrippedNumber[nDataPoints - 1].substring(firstNonZeroColumn, firstNonConstantColumn + 1);        

        he_AsInteger = Integer.parseInt(highestStem);
        le_AsInteger = Integer.parseInt(lowestStem);
        
        String tempString = "";
        for (iStem = he_AsInteger; iStem >= le_AsInteger; iStem--){
            tempString = String.format("%+d", iStem);
            theStems_With_Vert.add(tempString);
            if ((iStem == 0) && posNumbersExist && negNumbersExist) {
                theStems_With_Vert.add("-0");
            }
        }
        
        for (iStem = 0; iStem < theStems_With_Vert.size(); iStem++){
            tempString = theStems_With_Vert.get(iStem);
            theStems_WO_Vert.add(tempString);   //  Needed for 2/5 lines/stem
            tempString += "|";
            theStems_With_Vert.set(iStem, tempString);
        }     
        lengthOfStems = tempString.length();
    }
    
    private void addTheVerticalLine() {
        if (printTheStuff == true) {
            System.out.println("238 --- StemNLeaf_Model, addTheVerticalLine()");
        }
        int iStem, iData;
        // ****************** Construct the stem&leaf initial strings
        for (iStem = 0; iStem < theStems_With_Vert.size(); iStem++) {
            oneLineStems.add(theStems_With_Vert.get(iStem));
        }
        
        // **************  Loop through the strings and construct the plot
        //  for dataString highest through dataString lowest
        for (iData = 0; iData < nDataPoints; iData++) {
            String tempInString = strDaStrippedNumber[iData];
            String tempOutString = constructIndividualStem(tempInString);            
            for (iStem = 0; iStem < theStems_With_Vert.size(); iStem++) {
                if (theStems_With_Vert.get(iStem).equals(tempOutString + "|")) {
                    StringBuilder tempString = new StringBuilder(oneLineStems.get(iStem));
                    tempString.append(getTheLeafAsAString(tempInString));
                    oneLineStems.set(iStem, tempString.toString());
                }
            }  
        }    
    }
    
    private void sortLeavesWithinStems() {
        if (printTheStuff == true) {
            System.out.println("263 --- StemNLeaf_Model, sortLeavesWithinStems()");
        }
        //boolean canDoSL_1 = true;
        // int startPosition = lengthOfStems;
        //nStemsNeeded = theStems_With_Vert.size();
        nStemsNeeded_1 = theStems_With_Vert.size();
        nStemsNeeded_2 = 2 * nStemsNeeded_1;
        nStemsNeeded_5 = 5 * nStemsNeeded_1;

        //   Display alert
        if (( nStemsNeeded_5 > maxLineInSL) && (!bbslCheck.equals("Null"))){
            MyAlerts.showStemAndLeafAlert();
            witchesWarned = true;
        }
        
        for (int iStem = 0; iStem < nStemsNeeded_1; iStem++) {
            String tempStem = oneLineStems.get(iStem);
            int lengthOfStemNLeaves = tempStem.length();
            String stemPart = tempStem.substring(0, lengthOfStems);
            
            if (lengthOfStemNLeaves - lengthOfStems > 1) {
                //  More than one leaf -- sort.
                // int endPosition = lengthOfStemNLeaves;
                String leafPart = tempStem.substring(lengthOfStems, lengthOfStemNLeaves);
                char[] leefs = leafPart.toCharArray();
                Arrays.sort(leefs);
                leafPart = String.valueOf(leefs);
                
                if (stemPart.substring(0,1).equals("-")) {
                    leafPart = StringUtilities.reverseStringCharacters(leafPart);
                }
                          
                String tempString = stemPart + leafPart;
                oneLineStems.set(iStem, tempString);
            }
        }
        int maxLeafsNeeded_1 = 0;
        
        for (int ithStem = 0; ithStem < nStemsNeeded_1; ithStem++) {
            
            if (oneLineStems.get(ithStem).length() > maxLeafsNeeded_1) {
                maxLeafsNeeded_1 = oneLineStems.get(ithStem).length();
            }
        } 
         //System.out.println("291 SNLModel, maxLeafsNeeded_1 = " + maxLeafsNeeded_1);    
        if ((maxCharsInLine < maxLeafsNeeded_1) 
                && (!bbslCheck.equals("Null")) 
                && !witchesWarned ){
            //System.out.println("280 SL, sortLeavesWithinStems -- too many Leaves");
            MyAlerts.showStemAndLeafAlert();
        }   
    }
    
    private void construct_2LinesPerStem() {
        if (printTheStuff == true) {
            System.out.println("319 --- StemNLeaf_Model, construct_2LinesPerStem()");
        }
        String hiSL, loSL;
        String[] twoline_leafOptions = {"01234", "56789"};
        nStems = theStems_With_Vert.size();
        
        for (int iStem = 0; iStem < nStems; iStem++) {
            String oneLineStem = oneLineStems.get(iStem);
            String tempString = theStems_WO_Vert.get(iStem);
            String hiSB = tempString + "H|";
            String loSB = tempString + "L|";
            
            if (tempString.charAt(0) == '+') {
                hiSL = hiSB + constructLeaves(twoline_leafOptions[1], oneLineStem);
                loSL = loSB + constructLeaves(twoline_leafOptions[0], oneLineStem);
                twoLineStems.add(hiSL);
                twoLineStems.add(loSL);
            }
            else {    // char is '-''
                String preReversedLoSL = constructLeaves(twoline_leafOptions[0], oneLineStem);
                String reversedLoSL = StringUtilities.reverseStringCharacters(preReversedLoSL);
                loSL = loSB + reversedLoSL;                
                
                String preReversedHiSL = constructLeaves(twoline_leafOptions[1], oneLineStem);               
                String reversedHiSL = StringUtilities.reverseStringCharacters(preReversedHiSL);
                hiSL = hiSB + reversedHiSL;
                
                twoLineStems.add(loSL); //  Stems are reversed for negative
                twoLineStems.add(hiSL); //  numbers.  Also for 5-line stems
            }
        }   //  next iStem
        
        int maxLeafsNeeded_2 = 0;
        
        for (int ithStem = 0; ithStem < nStemsNeeded_2; ithStem++) {
            if (twoLineStems.get(ithStem).length() > maxLeafsNeeded_2) {
                maxLeafsNeeded_2 = twoLineStems.get(ithStem).length();
            }
        } 
        //System.out.println("339 StemAndLeaf_Model, maxLeafsNeeded_2 = " + maxLeafsNeeded_2);
    }
    
    private void construct_5LinesPerStem() {
        if (printTheStuff == true) {
            System.out.println("363 --- StemNLeaf_Model, construct_5LinesPerStem()");
        }
        String[] fivePerSL = new String[5];
        StringBuilder[] fivePerSB = new StringBuilder[5];
        
        String[] fiveline_leafOptions = {"01", "23", "45", "67", "89"};
        String[] fiveline_stemOptions = {".|", "t|", "f|", "s|", "*|"};
        
        nStems = theStems_With_Vert.size();
        
        for (int iStem = 0; iStem < nStems; iStem++) {
            String oneLineStem = oneLineStems.get(iStem);
            String tempString = theStems_WO_Vert.get(iStem);

            if (tempString.charAt(0) == '+') {
                
                for (int leafOptions = 0; leafOptions < 5; leafOptions++) {
                    fivePerSB[leafOptions] = new StringBuilder(tempString + fiveline_stemOptions[4 - leafOptions]);
                    fivePerSL[leafOptions] = fivePerSB[leafOptions].toString() + constructLeaves(fiveline_leafOptions[4 - leafOptions], oneLineStem);
                    fiveLineStems.add(fivePerSL[leafOptions]);
                }
            } else {    // char is '-''
                
                for (int leafOptions = 0; leafOptions < 5; leafOptions++) {
                    fivePerSB[leafOptions] = new StringBuilder(tempString + fiveline_stemOptions[leafOptions]);
                    
                    String preReversedSL = constructLeaves(fiveline_leafOptions[leafOptions], oneLineStem);
                    String reversedSL = StringUtilities.reverseStringCharacters(preReversedSL);

                    fivePerSL[leafOptions] = fivePerSB[leafOptions].toString() + reversedSL;                    
                }   
                
                for (int leafOptions = 0; leafOptions < 5; leafOptions++) { 
                   fiveLineStems.add(fivePerSL[leafOptions]); 
                }
            }
        }   //  next iStem  
        
        int maxLeafsNeeded_5 = 0;
        
        for (int ithStem = 0; ithStem < nStemsNeeded_5; ithStem++) {            
            if (fiveLineStems.get(ithStem).length() > maxLeafsNeeded_5) {
                maxLeafsNeeded_5 = fiveLineStems.get(ithStem).length();
            }
        } 
    }
    
    private String constructLeaves(String charsToChooseFrom, String oneLiner) {
        if (printTheStuff == true) {
            System.out.println("412 --- StemNLeaf_Model, constructLeaves");
        }
        StringBuilder wholeSL = new StringBuilder();
        
        int firstOccurence = oneLiner.indexOf('|');       
        for (int iChar = 0; iChar < charsToChooseFrom.length(); iChar++) {           
            for (int iLeaf = firstOccurence; iLeaf < oneLiner.length(); iLeaf++) {
                if (charsToChooseFrom.charAt(iChar) == oneLiner.charAt(iLeaf)) {
                    wholeSL.append(charsToChooseFrom.charAt(iChar));
                }
            }
        }
        return wholeSL.toString();
    }
       
    private String constructIndividualStem(String strippedNumber) {
        if (printTheStuff == true) {
            System.out.println("429 --- StemNLeaf_Model, constructIndividualStem");
        }
        StringBuilder stem = new StringBuilder();
        stem.append(strippedNumber.substring(0,1));
        stem.append(strippedNumber.substring(firstNonZeroColumn, firstNonConstantColumn + 1));       
        return stem.toString(); 
    }
    
    private String getTheLeafAsAString(String stringyWingy) {
        if (printTheStuff == true) {
            System.out.println("439 --- StemNLeaf_Model, getTheLeafAsAString");
        }
        // Leaf digit is one past the last stem digit
        int leafDigit = firstNonConstantColumn + 1;
        String theLeaf = stringyWingy.substring(leafDigit, leafDigit + 1);
        return theLeaf;
    } 
    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    
    public StemNLeaf_View getStemNLeaf_View() { return sandL_View; }
   
    // This is called by BBSL 
    public int getOrderOfMagnitude()  { return orderOfMagnitude; }
    
    public int getFirstNonZeroColumn() { return firstNonZeroColumn; }
    public int getFirstNonConstantColumn() { return firstNonConstantColumn; }
    
    public ArrayList<String> get_1_LineSL() { return oneLineStems; }
    public ArrayList<String> get_2_LineSL() { return twoLineStems; }
    public ArrayList<String> get_5_LineSL() { return fiveLineStems; }
    
    public int getNStemsNeeded_1() { return nStemsNeeded_1; }
    public int getNStemsNeeded_2() { return nStemsNeeded_2; }
    public int getNStemsNeeded_5() { return nStemsNeeded_5; }
    
    public int getNLeavesNeeded_1() { return nLeavesNeeded_1; }   
    public int getNLeavesNeeded_2() { return nLeavesNeeded_2; }
    public int getNLeavesNeeded_5() { return nLeavesNeeded_5; }
    
    public QuantitativeDataVariable getTheQDV() {return theQDV; }
}
