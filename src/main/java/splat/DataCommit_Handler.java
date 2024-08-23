/**************************************************
 *              DataCommit_Handler_Alpha          *
 *                    11/05/23                    *
 *                     18:00                      *
 *************************************************/
package splat;

import utilityClasses.*;

public class DataCommit_Handler {
    
    boolean printTheCellStuff, printTheInfo, firstEntryDone, weAreTabbing,
            handlingRightAndDown;
    
    int nVarsInStruct, nCasesInStruct, ehGridCol, ehGridRow, ehStructRow, 
        ehStructCol, maxVarsInGrid, maxCasesInGrid, lengthOfString, 
        tabRectLeftIndex, tabRectRightIndex, charsPastDecimal,
        structCol, structRow;
    
    String theEntry, frmtString, fixedString, parsedString, tabOrEnter;
    String evaluation;
    PositionTracker tracker;
    Data_Grid dataGrid;
    Data_Manager dm;
    
     //  Make empty if no-print
    //String waldoFile = "DataCommit_Handler (Super)";
    String waldoFile = "";
            
    public DataCommit_Handler( PositionTracker tracker, Data_Grid dg, Data_Manager dm) {
        printTheCellStuff = false; printTheInfo = false;
        this.tracker = tracker;
        this.dataGrid = dg;
        this.dm = dm;
        dm.whereIsWaldo(35, waldoFile, "DataCommit_Handler/Constructing");
        maxVarsInGrid = dm.getMaxVisVars();
        maxCasesInGrid = dm.getMaxVisCases();
        handlingRightAndDown = false;
        firstEntryDone = false;
    }
    
    public void handleCommit_DoFirstDataEntry() {
        dm.whereIsWaldo(43, waldoFile, "DataCommit_Handler/handleCommit_DoFirstDataEntry()");
        tracker.set_ulDG(0, 0);
        tracker.set_ulDS(0, 0);
        for (int ithFirstCols = 0;  ithFirstCols < ehGridCol + 1; ithFirstCols++) {
            dm.getDataStruct().get(ithFirstCols).addNCasesOfThese(ehGridRow + 1, "*");
        }
        
        tracker.set_lrDG(ehGridCol, ehGridRow);
        tracker.set_lrDS(ehGridCol, ehGridRow);

        dm.setDataInStruct("53 DataCommit_Handler/handleEnter_DoFirstDataEntry()", 
                ehGridCol,
                ehGridRow,
                theEntry); 
        dm.sendDataStructToGrid(ehGridCol, ehGridRow);
        firstEntryDone = true;
    }  
    
    public String handleCommit_ParseTheString() {
        dm.whereIsWaldo(62, waldoFile, "DataCommit_Handler/handleCommit_ParseTheString()");
        int lenTempString;
        String tempString;
        evaluation = "x";
        tempString = theEntry;
        lenTempString = tempString.length();
        // Check the last char for an asterisk -- if found, check the preceding
        // for a legal double.
        if (lenTempString == 0) { return "*"; }
        char lastChar = tempString.charAt(lenTempString - 1);
        if (lastChar == '*') {
            String preAsterisk = tempString.substring(0, lenTempString - 1);    
            
            if (DataUtilities.strIsADouble(preAsterisk)) {
                theEntry = preAsterisk;
                handleCommit_FormatTheString();
                evaluation = "Ok";
                return evaluation;
            } 
            else {
                theEntry = preAsterisk;  //  Still strip the asterisk
                evaluation = "Text";
                return evaluation;
            }
        }
        else {// Last char not an asterisk
            if (!DataUtilities.strIsADouble(tempString)) {
                evaluation = "Text";
                return evaluation;
            }  
             else {
                 handleCommit_FormatTheString();
                 evaluation = theEntry;
                 return evaluation;
             }
        }
    }
    
    private void handleCommit_FormatTheString() {
        dm.whereIsWaldo(101, waldoFile, "DataCommit_Handler/handleCommit_FormatTheString()");
        theEntry = theEntry.trim();
        
        if (DataUtilities.strIsADouble(theEntry)) {
            int curCol = ehGridCol;
            int curDecPos = dm.getAllTheColumns().get(curCol).getSigDig();
            frmtString = dm.getAllTheColumns().get(curCol).getFormatString();
            int decimalPosition = theEntry.indexOf('.');
            
            if (decimalPosition == -1) { decimalPosition = 0; } // An integer
            
            lengthOfString = theEntry.length();
            charsPastDecimal = lengthOfString - decimalPosition - 1;
            double daRefinedValue = Double.parseDouble(theEntry); 
            
            if (charsPastDecimal > curDecPos) {
                frmtString = "%." + String.valueOf(charsPastDecimal)+"f";
                dm.getAllTheColumns().get(curCol).setSigDig(charsPastDecimal);
            }
            
            String strToFormat = String.format(frmtString, daRefinedValue);
            theEntry = strToFormat;
            
            if (theEntry.length() > 10) {
                theEntry = theEntry.substring(0, 9);
            }           
        }

        int nBlanks = 5 - charsPastDecimal;
        
        if (nBlanks > 0) {            
            for (int ithBlank = 0; ithBlank < nBlanks; ithBlank++) {
                theEntry = " " + theEntry;
            }
        }
    }
    
    public String handleCommit_GetTheFinalString() {return theEntry; }
}
