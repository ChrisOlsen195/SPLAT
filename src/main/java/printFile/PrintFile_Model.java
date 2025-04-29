/**************************************************
 *                 PrintFile_Model                *
 *                    03/01/25                    *
 *                     18:00                      *
 *************************************************/

package printFile;

import dataObjects.ColumnOfData;
import java.io.File;
import java.util.ArrayList;
import utilityClasses.StringUtilities;
import splat.*;
import utilityClasses.DataUtilities;

public class PrintFile_Model {     
    // POJOs
    
    boolean[] varIsNumeric, varIsInteger;
    int nVarsChosen, nRows, digitsLeft, digitsRight;
    int[] maxDataStringLen, maxDigitsLeft, maxDigitsRight;
    
    File fileName;
    String strVarsLine, strDataLine, formattedDataValue;
    StringBuilder sbVarsLine, sbDataLine;
    
    //String waldoFile = "PrintFile_Model";
    String waldoFile = "";
    String[] strLabels, thisVarFormat;

    ArrayList<String> prtFile_Report;
    
    // My classes
    ArrayList<ColumnOfData> data;
    Data_Manager dm;
    PrintFile_Controller printFile_Controller;

    // POJOs / FX

    public PrintFile_Model(Data_Manager dm, PrintFile_Controller printFile_Controller) {   
        this.dm = dm;
        dm.whereIsWaldo(42, waldoFile, "Constructing");
        this.printFile_Controller = printFile_Controller;
        fileName = dm.getFileName();
        prtFile_Report = new ArrayList();
    }
   
   public void printFile() {
        dm.whereIsWaldo(49, waldoFile, "printFile()");
        setTheTable();
        constructVariableFormats();
        
        addNBlankLinesToPrintFileReport(1);
        prtFile_Report.add(String.format("-----------------------------"));
        addNBlankLinesToPrintFileReport(2);
        prtFile_Report.add(strVarsLine);
        addNBlankLinesToPrintFileReport(1);
        constructDataLineFormats();
        processDataLines();
   }
   
   private void setTheTable() {
        dm.whereIsWaldo(63, waldoFile, "setTheTable()");
        data = printFile_Controller.getData();
        nVarsChosen = data.size();
        nRows = data.get(0).getNCasesInColumn();         
        String tempString = "  Print File" + fileName;
        prtFile_Report.add(tempString);
        addNBlankLinesToPrintFileReport(1);
        // Construct format strings
        // Need label formats and cat/number formats
        strLabels = new String[nVarsChosen];
        maxDataStringLen = new int[nVarsChosen];
        maxDigitsLeft = new int[nVarsChosen]; 
        maxDigitsRight = new int[nVarsChosen];
        thisVarFormat = new String[nVarsChosen];
        varIsNumeric = new boolean[nVarsChosen]; 
        varIsInteger = new boolean[nVarsChosen]; 
        for (int ithVar = 0; ithVar < nVarsChosen; ithVar++) {
            varIsNumeric[ithVar] = data.get(ithVar).getDataType().equals("Quantitative");
            varIsInteger[ithVar] = true;
        }
        sbVarsLine = new StringBuilder();
   }
   
   private void constructVariableFormats() {
        dm.whereIsWaldo(87, waldoFile, "constructVariableFormats()");
        // Determine the format for this variable
        for (int ithVar = 0; ithVar < nVarsChosen; ithVar++) {
            maxDataStringLen[ithVar] = data.get(ithVar).getVarLabel().length();
            maxDigitsLeft[ithVar] = 0; 
            maxDigitsRight[ithVar] = 0;
            digitsLeft = 0;
            digitsRight = 0;
            for (int jthRow = 0; jthRow < nRows; jthRow++) {
                //String tempStr = dm.getFromDataStruct(ithVar, jthRow);
                String tempStr = data.get(ithVar).getIthCase(jthRow);
                int strLength = tempStr.length();
                maxDataStringLen[ithVar] = Math.max(maxDataStringLen[ithVar], strLength);
                if (varIsNumeric[ithVar]) {
                    int decimalPosition = tempStr.indexOf('.');
                    if (decimalPosition >= 0) { 
                        varIsInteger[ithVar] = false; 
                    }
                    if (decimalPosition > 0) {
                        digitsRight = strLength - decimalPosition - 1;
                        maxDigitsRight[ithVar] = Math.max(maxDigitsRight[ithVar], digitsRight);
                        digitsLeft = strLength - digitsRight - 1;
                        maxDigitsLeft[ithVar] = Math.max(maxDigitsLeft[ithVar], digitsLeft);
                    } 
                    else if (decimalPosition == 0) {
                        digitsRight = strLength - 1;
                        maxDigitsRight[ithVar] = Math.max(maxDigitsRight[ithVar], digitsRight);
                        digitsLeft = 0;
                        maxDigitsLeft[ithVar] = Math.max(maxDigitsLeft[ithVar], digitsLeft);               
                    }
                    else {
                        digitsRight = 0;
                        if (DataUtilities.strIsAnInteger(tempStr)) {
                            maxDigitsRight[ithVar] = Math.max(maxDigitsRight[ithVar], digitsRight);
                            digitsLeft = strLength;
                            maxDigitsLeft[ithVar] = Math.max(maxDigitsLeft[ithVar], digitsLeft);                      
                        }
                        else { }
                    }
                }   //  end numeric -- no need for else
            }   // end for
        }
        
        for (int ithVar = 0; ithVar < nVarsChosen; ithVar++) {
            if (varIsNumeric[ithVar]) {
                strLabels[ithVar] = data.get(ithVar).getVarLabel();
                sbVarsLine.append(StringUtilities.centerTextInString(strLabels[ithVar], maxDataStringLen[ithVar] + 4));            
            }
            else {
                strLabels[ithVar] = data.get(ithVar).getVarLabel();
                //  Room for 2 blanks before and after
                sbVarsLine.append(StringUtilities.centerTextInString(strLabels[ithVar], maxDataStringLen[ithVar] + 4));                
            }            
        }
        strVarsLine = sbVarsLine.toString();       
   }    // constructVariableFormats()
   
   private void constructDataLineFormats() {
        dm.whereIsWaldo(145, waldoFile, "constructDataLineFormats()");
        // Create data line format
        for (int ithVar = 0; ithVar < nVarsChosen; ithVar++) {
            if (varIsNumeric[ithVar]) {
                if (varIsInteger[ithVar]) {
                    thisVarFormat[ithVar] = "%" 
                                          + String.valueOf(maxDataStringLen[ithVar]) 
                                          + "d";
                }
                else {
                    thisVarFormat[ithVar] = "%" 
                                          + String.valueOf(maxDataStringLen[ithVar]) 
                                          + "." + String.valueOf(maxDigitsRight[ithVar]) 
                                          + "f";
                }  
            }
            else 
            {
                thisVarFormat[ithVar] = "%" + String.valueOf(maxDataStringLen[ithVar] + 4) + "s";
            }
        }       
   }
   
   private void processDataLines() {
        dm.whereIsWaldo(169, waldoFile, "processDataLines()");
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            
            sbDataLine = new StringBuilder();
            for (int jthVar = 0; jthVar < nVarsChosen; jthVar++) {
                String strDataValue = data.get(jthVar).getIthCase(ithRow);
                if (varIsNumeric[jthVar] && DataUtilities.strIsNumeric(strDataValue)) {
                    dm.whereIsWaldo(176, waldoFile, "varIsNumeric[jthVar] && DataUtilities.strIsNumeric(strDataValue)");
                    if (varIsInteger[jthVar]) {
                        formattedDataValue = String.format(thisVarFormat[jthVar], Integer.valueOf(strDataValue));
                    }
                    else {
                        formattedDataValue = String.format(thisVarFormat[jthVar], Double.valueOf(strDataValue));  
                    }
                    sbDataLine.append(StringUtilities.centerTextInString(formattedDataValue, maxDataStringLen[jthVar] + 4));
                } else  // var is cat
                {
                    dm.whereIsWaldo(186, waldoFile, "data is cat");
                    formattedDataValue = String.format("%10s", strDataValue);
                    sbDataLine.append(StringUtilities.centerTextInString(formattedDataValue, maxDataStringLen[jthVar] + 4));
                }
            }            
            strDataLine = sbDataLine.toString();
            prtFile_Report.add(strDataLine);
            addNBlankLinesToPrintFileReport(1);
        }  
   }
   
    private void addNBlankLinesToPrintFileReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(prtFile_Report, thisMany);
    }
    
    public ArrayList<String> getPrintFileReport() { return prtFile_Report; }
         
   public int getNRows()    {return nRows;}
   
   public Data_Manager getDataManager() { return dm; }
}