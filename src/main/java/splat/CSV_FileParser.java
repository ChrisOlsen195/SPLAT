// **************************************************
// *                 CSV_FileParser                 *
// *                    11/11/24                    *
// *                     09:00                      *
// *************************************************/

package splat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import utilityClasses.*;

public class CSV_FileParser {
    // POJOs
    boolean firstLineIsLabels, duplicateLabelsExist, blankLabelFound;
    
    //char aComma;
    int nColumnsThisFile, nDataRecordsThisFile;
    int nDataElementsThisLine;
    
    String currently_Read_String, returnStatus;
    String missingDataString = "*";
    String contentText;
    ArrayList<String> preParsedDataLine, parsedDataLine, 
                      al_ListOf_Alleged_Labels, al_ListOf_Concocted_Labels;  
    
    // Make empty if no-print
    // String waldoFile = "CSV_FileParser";
    String waldoFile = "";
    
    ObservableList<ObservableList<String>> allParsedLines;

    Alert alert;
    BufferedReader bufferedReader;    
    File selectedFile;
    FileReader fileReader;
    
    // My classes
    CSV_LineParser lineParser;
    
    public CSV_FileParser (File selectedFile, char fieldSeparator) { 
        if (!waldoFile.equals("")) {
            System.out.println("!!WW!! " + 49 + " / " + waldoFile + " / " + "CSV_FileParser (File selectedFile, char fieldSeparator)");
        }
        this.selectedFile = selectedFile;
        //aComma = ',';
        nColumnsThisFile = 0; 
        nDataRecordsThisFile = 0;
        blankLabelFound = false;
        lineParser = new CSV_LineParser(fieldSeparator);
        allParsedLines = FXCollections.observableArrayList();
        parsedDataLine = new ArrayList();
        preParsedDataLine = new ArrayList();
        firstLineIsLabels = true;
        returnStatus = "Ok";
    }
    
    public String parseTheFile() {
        if (!waldoFile.equals("")) {
            System.out.println("!!WW!! " + 66 + " / " + waldoFile + " / " + "parseTheFile()");
        }
        try {
            fileReader = new FileReader(selectedFile);
            bufferedReader = new BufferedReader(fileReader);     
            
            // ***********************************************************
            // ************ Read and Parse the Labels Line ***************
            // ***********************************************************
            currently_Read_String = bufferedReader.readLine();
            al_ListOf_Alleged_Labels = new ArrayList<>(lineParser.parse(currently_Read_String));
            nColumnsThisFile = al_ListOf_Alleged_Labels.size();
            String tmpString;
            // Do initial check for missing labels -- could be just missing data
            for (int iCol = 0; iCol < nColumnsThisFile; iCol++) {
                tmpString = al_ListOf_Alleged_Labels.get(iCol);
                if ((tmpString == null) || tmpString.trim().isEmpty()){
                    al_ListOf_Alleged_Labels.set(iCol, "Var # " + (iCol + 1));
                }    
            }
            for (int iCol = 0; iCol < nColumnsThisFile; iCol++) {
                tmpString = al_ListOf_Alleged_Labels.get(iCol);
                if ((tmpString == null) || tmpString.trim().isEmpty()){
                    blankLabelFound = true;
                }    
            }
            if (blankLabelFound) {
                MyAlerts.showBlankLabelsAlert();
                blankLabelFound = true; // Do alert only once
            }

// ****************************************************************************
// *  The first line must be parsed carefully because for ANOVA1 this program *
// *  Allows numeric values as labels for the variables (e.g. 'dose.')  The   *
// *  more usual situation would be that a data file has been downloaded w/o  *
// *  labels.                                                                 *
// ****************************************************************************

            firstLineIsLabels = false;
            duplicateLabelsExist = false;
            boolean someAreNotNumbers = false;
            boolean someAreNumbers = false;
            for (int ithColumn = 0; ithColumn < nColumnsThisFile; ithColumn++) {
                String parsedLabel = al_ListOf_Alleged_Labels.get(ithColumn);
                if (ithColumn > 0) { // Check for duplicate labels
                    for (int ithPreviousColumn = 0; ithPreviousColumn < ithColumn; ithPreviousColumn++) {
                        if (parsedLabel.equals(al_ListOf_Alleged_Labels.get(ithPreviousColumn))) {
                            duplicateLabelsExist = true;
                        }
                    }
                }
                if (!DataUtilities.strIsADouble(parsedLabel)) {
                    someAreNotNumbers = true;
                    firstLineIsLabels = true;                    
                }
                if (DataUtilities.strIsADouble(parsedLabel)) {
                    someAreNumbers = true;                    
                }
            }

            ArrayList<String> tmpParsedLine = new ArrayList();
            
            if (someAreNumbers == true) {
                MyYesNoAlerts myYesNoAlerts = new MyYesNoAlerts();
                myYesNoAlerts.showFirstLineContainsNumbersAlert("Yup", "Nope"); 
                String yesOrNo = myYesNoAlerts.getYesOrNo();                
                if (yesOrNo.equals("Yes")) {
                    returnStatus = "Ok";
                    firstLineIsLabels = true;
                    addToAllParsedLines(al_ListOf_Alleged_Labels);                    
                } else {
                    returnStatus = "Ok";
                    MyAlerts.showAcknowledgeQuantLabelsAlert();

                    al_ListOf_Concocted_Labels = new ArrayList();
                    for (int iCol = 0; iCol < nColumnsThisFile; iCol++) {
                        tmpString = "NotVar # " + (iCol + 1);
                        al_ListOf_Concocted_Labels.add(tmpString);            
                    }
                    returnStatus = "Ok";
                    firstLineIsLabels = true;

                    addToAllParsedLines(al_ListOf_Concocted_Labels);
                    addToAllParsedLines(al_ListOf_Alleged_Labels);
                } 
            } else {
                addToAllParsedLines(al_ListOf_Alleged_Labels);
            }
           
            //  *****************   Now read the data   *********************************
            while(currently_Read_String != null) {
                currently_Read_String = bufferedReader.readLine();   
                preParsedDataLine = lineParser.parse(currently_Read_String); 
                parsedDataLine = new ArrayList<>(adjustTheArrayList(preParsedDataLine));            
                nDataElementsThisLine = parsedDataLine.size(); 
                if (nDataElementsThisLine < nColumnsThisFile) { // append last element 
                    int thisManyDataElementsShort = nColumnsThisFile - nDataElementsThisLine;
                    for (int missingData = 0; missingData < thisManyDataElementsShort; missingData++) {
                        currently_Read_String += missingDataString;
                    }
                    // reparse
                    parsedDataLine = new ArrayList<>(lineParser.parse(currently_Read_String)); 
                }
                if (nDataElementsThisLine > nColumnsThisFile) {
                    returnStatus = "OK";
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Yikes!  Problem reading the file...");
                    alert.setHeaderText("Mismatch of labels and data");
                    contentText = "It appears that at least one data line has more values " + 
                                "\nthan the number of labels.  I, SPLAT, though being " +
                                "\nvirtually omniscient, cannot fix this problem.  Please" + 
                                "\ncheck this file in your favorite word processor and "  +
                                "\ntry, try, again. ";
                    alert.setContentText(contentText);
                    alert.showAndWait();
                    returnStatus = "Cancel";
                    return returnStatus;
                }
                // re-parse
                addToAllParsedLines(parsedDataLine);
            }  // end while(currently_Read_String != null)

            fileReader.close();
        }   
        catch (Exception ex) {
            // System.out.println("191 CSV_FileParser, ex = " + ex);
            //MyAlerts.showFileReadErrorAlert();
            //returnStatus = "Cancel";
        }
        nDataRecordsThisFile = allParsedLines.size() - 1;  // Data rows   
        if (nDataRecordsThisFile == 0) {
            MyAlerts.showMissingDataAlert();
            returnStatus = "Cancel";
        }
        return returnStatus;
    }   //  End try parse the file
       
    private ArrayList<String> adjustTheArrayList(ArrayList<String> thePreStringArray) {
        if (!waldoFile.equals("")) {
            //System.out.println("!!WW!! " + 205 + " / " + waldoFile + " / " + "adjustTheArrayList(ArrayList<String> thePreStringArray)");
        }
        int iCol;
        ArrayList<String> adjustedArrayList = new ArrayList(nColumnsThisFile);
        int preStringArraySize = thePreStringArray.size();
        if (preStringArraySize >= nColumnsThisFile) {
            // IF GREATER, ALERT USER TO ADJUSTMENT!
            // Copy first nLegal columns
            for (iCol = 0; iCol < nColumnsThisFile; iCol++) {
                if (!waldoFile.equals("")) {
                    //System.out.println("!!WW!! " + 215 + " / " + waldoFile + " / " + "icol = " + iCol);
                }
                adjustedArrayList.add(thePreStringArray.get(iCol));  
            }
        } else {
            //  Pad on end with Missing
            for (iCol = 0; iCol < preStringArraySize; iCol++) {
                if (!waldoFile.equals("")) {
                    //System.out.println("!!WW!! " + 223 + " / " + waldoFile + " / " + "icol = " + iCol);
                }
                adjustedArrayList.add(iCol, thePreStringArray.get(iCol));   // simple copy
            }  
            
            for (iCol = preStringArraySize; iCol < nColumnsThisFile; iCol++) {
                if (!waldoFile.equals("")) {
                    //System.out.println("!!WW!! " + 230 + " / " + waldoFile + " / " + "icol = " + iCol);
                }
                adjustedArrayList.add(missingDataString);
            }             
        }
        
        for (iCol = 0; iCol < nColumnsThisFile; iCol++) {
            if (!waldoFile.equals("")) {
                //System.out.println("!!WW!! " + 238 + " / " + waldoFile + " / " + "icol = " + iCol);
            }
            if (adjustedArrayList.get(iCol).equals(""))
                adjustedArrayList.set(iCol, missingDataString);   
        }  
        
        return adjustedArrayList;
    }
    
    public int getNVariables()  {return nColumnsThisFile; }
    
    //  *****  First row is Labels  *****
    public int getNCases()  {return nDataRecordsThisFile; }
    
     public String getIthDataVariableName(int ithVariable) {
        return al_ListOf_Alleged_Labels.get(ithVariable);
    }   

    private void addToAllParsedLines(ArrayList<String> thisParsedLine) {
        ObservableList<String> observedParsedLine;
        observedParsedLine = FXCollections.observableArrayList();
        for (int ith = 0; ith < thisParsedLine.size(); ith++) {
            if (!waldoFile.equals("")) {
                System.out.println("!!WW!! " + 261 + " / " + waldoFile + " / " + "ith = " + ith);
            }
            String tempString = thisParsedLine.get(ith).trim();
            observedParsedLine.add(tempString);
        }
        if (!waldoFile.equals("")) {
            System.out.println("!!WW!! " + 267 + " / " + waldoFile + " / " + "observedParsedLine = " + observedParsedLine);
        }
        allParsedLines.add(observedParsedLine); 
    }

    // The data rows begin after the label and data type lines
    public String getDataElementColRow(int col, int row) {
        String tempString = allParsedLines.get(row).get(col);
        return tempString;
    }

    public void setDataElementColRow(int col, int row, String stringValue) {
        allParsedLines.get(row).set(col, stringValue);
    }    
    
    public ObservableList<String> getParsedLine(int ithLine) {
        return allParsedLines.get(ithLine);
    }
              
    public ObservableList<ObservableList<String>> getAllParsedLines() {
       return allParsedLines;
    }
    
    public boolean getDuplicateLabelsExist() { return duplicateLabelsExist; }
    
    /*               For debugging
    private void printArrayListOfStrings(ArrayList<String> alString) {
        System.out.println("\n\n303 CSV_FileParser");
        int nStrings = alString.size();
        for (int ithStr = 0; ithStr < nStrings; ithStr++) {
            System.out.println("x " + alString.get(ithStr) + " x");
        }        
    }
    */
 }