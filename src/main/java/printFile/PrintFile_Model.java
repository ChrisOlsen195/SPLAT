/**************************************************
 *                 PrintFile_Model                *
 *                    05/15/24                    *
 *                     00:00                      *
 *************************************************/

package printFile;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import utilityClasses.StringUtilities;
import splat.*;
import utilityClasses.DataUtilities;

public class PrintFile_Model {     
    // POJOs
    
    boolean[] varIsNumeric, varIsInteger;
    int nVarsChosen, nRows, digitsLeft, digitsRight;
    int[] maxDataStringLen, maxDigitsLeft, maxDigitsRight;
    
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
        dm.whereIsWaldo(40, waldoFile, "Constructing");
        this.printFile_Controller = printFile_Controller;
        prtFile_Report = new ArrayList();
    }
   
   public void printFile() {
        dm.whereIsWaldo(46, waldoFile, "printFile()");
        setTheTable();
        constructVariableFormats();
        
        addNBlankLinesToPrintFileReport(1);
        prtFile_Report.add(String.format("-----------------------------"));
        addNBlankLinesToPrintFileReport(3);
        prtFile_Report.add(String.format("   Note: Points with Cook's D"));
        
        // Last two lines to give space in the scrollPane
        addNBlankLinesToPrintFileReport(2);
        prtFile_Report.add(strVarsLine);
        addNBlankLinesToPrintFileReport(1);
        constructDataLineFormats();
        processDataLines();
   }
   
   private void setTheTable() {
       dm.whereIsWaldo(64, waldoFile, "setTheTable()");
       data = printFile_Controller.getData();
       nVarsChosen = data.size();
       for (int ithVar = 0; ithVar < nVarsChosen; ithVar++) {
           System.out.println("68 PrintFile_Model, var = " + data.get(ithVar).getVarLabel());
       }
       nRows = data.get(0).getNCasesInColumn();         
       addNBlankLinesToPrintFileReport(2);
        prtFile_Report.add("  Print File");
        addNBlankLinesToPrintFileReport(1);
        prtFile_Report.add(String.format("--------------------------------"));
        addNBlankLinesToPrintFileReport(1);
        prtFile_Report.add(String.format("--------------------------------"));
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
            varIsNumeric[ithVar] = data.get(ithVar).getIsNumeric();
            varIsInteger[ithVar] = true;
        }
        sbVarsLine = new StringBuilder();
   }
   
   private void constructVariableFormats() {
        dm.whereIsWaldo(96, waldoFile, "constructVariableFormats()");
        // Determine the format for this variable
        for (int ithVar = 0; ithVar < nVarsChosen; ithVar++) {
            System.out.println("\n\n\n96 PrintFile_Model, ithVar = " + ithVar + "\n");
            maxDataStringLen[ithVar] = data.get(ithVar).getVarLabel().length();
            maxDigitsLeft[ithVar] = 0; 
            maxDigitsRight[ithVar] = 0;
            digitsLeft = 0;
            digitsRight = 0;
            for (int jthRow = 0; jthRow < nRows; jthRow++) {
                //String tempStr = dm.getFromDataStruct(ithVar, jthRow);
                String tempStr = data.get(ithVar).getIthCase(jthRow);
                //System.out.println("108 PrintFile_Model, tempStr = " + tempStr);
                int strLength = tempStr.length();
                maxDataStringLen[ithVar] = Math.max(maxDataStringLen[ithVar], strLength);
                if (varIsNumeric[ithVar]) {
                    int decimalPosition = tempStr.indexOf('.');
                    //System.out.println("113 PrintFile_Model, decimalPos = " + decimalPosition);
                    if (decimalPosition >= 0) { 
                        //System.out.println("115 PrintFile_Model, Changing ithVar to NOT Integer ***************************");
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
        dm.whereIsWaldo(158, waldoFile, "constructDataLineFormats()");
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
            //System.out.println("178 PrintFile_Model, thisVarFormat[ithVar] = " + thisVarFormat[ithVar]);
        }       
   }    //  constructDataLineFormats()
   
   private void processDataLines() {
        dm.whereIsWaldo(183, waldoFile, "processDataLines()");
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            
            sbDataLine = new StringBuilder();
            for (int jthVar = 0; jthVar < nVarsChosen; jthVar++) {
                String strDataValue = data.get(jthVar).getIthCase(ithRow);
                //System.out.println("189 PrintFile_Model, jthVar/ithRow/strDataValue = " + jthVar + " / " + ithRow + " / " + strDataValue);
                if (varIsNumeric[jthVar] && DataUtilities.strIsNumeric(strDataValue)) {
                    dm.whereIsWaldo(191, waldoFile, "varIsNumeric[jthVar] && DataUtilities.strIsNumeric(strDataValue)");
                    if (varIsInteger[jthVar]) {
                        //System.out.println("193 PrintFile_Model, thisVarFormat[jthVar]/Integer.valueOf(strDataValue)"
                        //                     + " / " + thisVarFormat[jthVar] 
                        //                     + " / " + Integer.valueOf(strDataValue));

                        formattedDataValue = String.format(thisVarFormat[jthVar], Integer.valueOf(strDataValue));
                        System.out.println("198 PrintFile_Model, formattedDataValue = " + formattedDataValue);
                    }
                    else {
                        //System.out.println("201 PrintFile_Model, thisVarFormat[jthVar]/Integer.valueOf(strDataValue)"
                        //                    + " / " + thisVarFormat[jthVar] 
                        //                    + " / " + Double.valueOf(strDataValue));

                        formattedDataValue = String.format(thisVarFormat[jthVar], Double.valueOf(strDataValue));  
                        //System.out.println("206 PrintFile_Model, formattedDataValue = " + formattedDataValue);
                    }
                    sbDataLine.append(StringUtilities.centerTextInString(formattedDataValue, maxDataStringLen[jthVar] + 4));
                } else  // var is cat
                {
                    dm.whereIsWaldo(211, waldoFile, "data is cat");
                    //System.out.println("212 PrintFile_Model, thisVarFormat[jthVar] = " + thisVarFormat[jthVar]);
                    //System.out.println("213 PrintFile_Model, strDataValue = " + strDataValue);
                    formattedDataValue = String.format("%10s", strDataValue);
                    //System.out.println("215 PrintFile_Model, formattedDataValue = " + formattedDataValue);
                    sbDataLine.append(StringUtilities.centerTextInString(formattedDataValue, maxDataStringLen[jthVar] + 4));
                }
            }            
            strDataLine = sbDataLine.toString();
            prtFile_Report.add(strDataLine);
            addNBlankLinesToPrintFileReport(1);
        }  
   }    //  processDataLines()
   
    private void addNBlankLinesToPrintFileReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(prtFile_Report, thisMany);
    }
    
    public ArrayList<String> getPrintFileReport() { return prtFile_Report; }
         
   public int getNRows()    {return nRows;}
   
   public Data_Manager getDataManager() { return dm; }
}