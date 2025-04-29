// **************************************************
// *                 CSV_FileParser                 *
// *                    03/08/25                    *
// *                     03:00                      *
// *************************************************/

/***************************************************
*       Checks for non asterisks are made in       *
*            the ColumnOfData class                *
***************************************************/

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
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    //char aComma;
    int nColumnsThisFile, nDataRecordsThisFile;
    int nDataElementsThisLine;
    
    String currently_Read_String, strReturnStatus;
    String missingDataString = "*";
    String contentText;
    ArrayList<String> preParsedDataLine, parsedDataLine, 
                      al_ListOf_Alleged_Labels, al_ListOf_Concocted_Labels;  
    
    ObservableList<ObservableList<String>> allParsedLines;

    Alert alert;
    BufferedReader bufferedReader;    
    File selectedFile;
    FileReader fileReader;
    MyYesNoAlerts myYesNoAlerts;
    
    // My classes
    CSV_LineParser lineParser;
    
    public CSV_FileParser (File selectedFile, char fieldSeparator) { 
        if (printTheStuff == true) {
            System.out.println("54 *** CSV_FileParser, constructing");
        }
        myYesNoAlerts = new MyYesNoAlerts();
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
        strReturnStatus = "Ok";
    }
    
    public String parseTheFile() {
        if (printTheStuff == true) {
            //System.out.println("72 *** CSV_FileParser, parseTheFile()");
        }
        try {
            if (printTheStuff == true) {
                //System.out.println("76 --- CSV_FileParser, parseTheFile(), trying");
            }
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
            }   // End labels check
                            
            ArrayList<String> tmpParsedLine = new ArrayList();
            
            if (someAreNumbers == true) {
                myYesNoAlerts.setTheYes("Yup");
                myYesNoAlerts.setTheNo("Nope");
                myYesNoAlerts.showFirstLineContainsNumbersAlert(); 
                String yesOrNo = myYesNoAlerts.getYesOrNo(); 
                if (printTheStuff == true) {
                    //System.out.println("144 --- parseTheFile(), yesOrNo = " + yesOrNo);
                }                
                if (yesOrNo.equals("Yes")) {
                    strReturnStatus = "Ok";
                    firstLineIsLabels = true;
                    addToAllParsedLines(al_ListOf_Alleged_Labels);                    
                } else {
                    strReturnStatus = "Ok";
                    MyAlerts.showAcknowledgeQuantLabelsAlert();

                    al_ListOf_Concocted_Labels = new ArrayList();
                    for (int iCol = 0; iCol < nColumnsThisFile; iCol++) {
                        tmpString = "NotVar # " + (iCol + 1);
                        al_ListOf_Concocted_Labels.add(tmpString);            
                    }
                    strReturnStatus = "Ok";
                    firstLineIsLabels = true;

                    addToAllParsedLines(al_ListOf_Concocted_Labels);
                    addToAllParsedLines(al_ListOf_Alleged_Labels);
                } 
            } else {
                addToAllParsedLines(al_ListOf_Alleged_Labels);
            }   // End consideration of labels
           
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
                    strReturnStatus = "OK";
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
                    strReturnStatus = "Cancel";
                    return strReturnStatus;
                }
                // re-parse
                addToAllParsedLines(parsedDataLine);
            }  // end while(currently_Read_String != null)

            fileReader.close();
        }   
        catch (Exception ex) {
            // System.out.println("204 CSV_FileParser, ex = " + ex);
            //MyAlerts.showFileReadErrorAlert();
            //returnStatus = "Cancel";
        }
        nDataRecordsThisFile = allParsedLines.size() - 1;  // Data rows   
        if (nDataRecordsThisFile == 0) {
            MyAlerts.showMissingDataAlert();
            strReturnStatus = "Cancel";
        }
        return strReturnStatus;
    }   //  End try parse the file
       
    private ArrayList<String> adjustTheArrayList(ArrayList<String> thePreStringArray) {
        if (printTheStuff == true) {
            //System.out.println("218 --- CSV_FileParser, adjustTheArrayList");
        }
        int ithColumn;
        ArrayList<String> adjustedArrayList = new ArrayList(nColumnsThisFile);
        int preStringArraySize = thePreStringArray.size();
        if (preStringArraySize >= nColumnsThisFile) {
            // IF GREATER, ALERT USER TO ADJUSTMENT!
            // Copy first nLegal columns
            for (ithColumn = 0; ithColumn < nColumnsThisFile; ithColumn++) {
                if (printTheStuff == true) {
                     //System.out.println("228 --- CSV_FileParser, ithColumn = " + ithColumn);
                 }
                adjustedArrayList.add(thePreStringArray.get(ithColumn));  
            }
        } else {
            //  Pad on end with Missing
            for (ithColumn = 0; ithColumn < preStringArraySize; ithColumn++) {
                if (printTheStuff == true) {
                     //System.out.println("236 --- parseTheFile(), ithColumn = " + ithColumn);
                 }
                adjustedArrayList.add(ithColumn, thePreStringArray.get(ithColumn));   // simple copy
            }  
            
            for (ithColumn = preStringArraySize; ithColumn < nColumnsThisFile; ithColumn++) {
                if (printTheStuff == true) {
                     //System.out.println("243 --- parseTheFile(), ithColumn = " + ithColumn);
                 }
                adjustedArrayList.add(missingDataString);
            }             
        }
        
        for (ithColumn = 0; ithColumn < nColumnsThisFile; ithColumn++) {
            if (printTheStuff == true) {
                 //System.out.println("251 --- parseTheFile(), ithColumn = " + ithColumn);
             }
            if (adjustedArrayList.get(ithColumn).equals(""))
                adjustedArrayList.set(ithColumn, missingDataString);   
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
        String tempString = "";
        ObservableList<String> observedParsedLine;
        observedParsedLine = FXCollections.observableArrayList();
        for (int ith = 0; ith < thisParsedLine.size(); ith++) {
            tempString = thisParsedLine.get(ith).trim();
            observedParsedLine.add(tempString);
            if (printTheStuff == true) {
                //System.out.println("276 --- addToAllParsedLines, thisParsedLine = " + tempString);
            }
        }
        allParsedLines.add(observedParsedLine); 
        //System.out.println("280 CSV_FileParser, allParsedLines.size = " + allParsedLines.size());
    }

    // The data rows begin after the label and data type lines
    public String getDataElementColRow(int col, int row) {
        String oldValue = allParsedLines.get(row).get(col);
        if (printTheStuff == true) {
            //System.out.println("286 *** getDataElementColRow, oldValue = " + oldValue);
        }
        return oldValue;
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