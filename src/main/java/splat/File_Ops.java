/************************************************************
 *                        Splat_FileOps                     *
 *                           09/13/24                       *
 *                            18:00                         *
 ***********************************************************/
/**************************************************
*  All coordinate systems are zero-based:         *
*  1.  The DataStruct of mCases x nCols           *
*  2.  The DataGrid                               *
*                                                 *
*  Only the presented-to-user cases and variables *
*  will be 1-based, and will appear in the code   *
*  with xxx + 1 subscripts.                       *
*                                                 *
**************************************************/
package splat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import utilityClasses.PrintExceptionInfo;
import javafx.stage.FileChooser;
import printFile.*;
import utilityClasses.MyAlerts;
import utilityClasses.MyYesNoAlerts;

public class File_Ops {
    // POJOs
    
    boolean duplicateLabelsExist;
    
    int nVarsInFile, nCasesInFile, maxCasesInGrid, startVariable;
    
    String returnStatus, yesOrNo;
    
    //  Make empty if no-print
    String waldoFile = "File_Ops";
    //String waldoFile = "";
    
    // My classes
    CSV_FileParser fileParser;
    Data_Manager dm;
    MyYesNoAlerts myYesNoAlerts;
    PositionTracker tracker;
    PrintFile_Controller printFile_Controller;
    
    File theFile;

    public File_Ops(Data_Manager dm) {
        dm.whereIsWaldo(52, waldoFile, "\nConstructing File_Ops, no file name");
        this.dm = dm;
        tracker = dm.getPositionTracker();
        dm.setDataAreClean(true);
        returnStatus = "Ok";
        myYesNoAlerts = new MyYesNoAlerts();
    }
    
    public File_Ops( String fileName, Data_Manager dm) {
        dm.whereIsWaldo(61, waldoFile, "\nConstructing File_Ops with file name");
        this.dm = dm;
        tracker = dm.getPositionTracker();
        theFile = new File(fileName);
        dm.getMainMenu().setFileLabel(fileName);
        parseAndRead(theFile);
        myYesNoAlerts = new MyYesNoAlerts();
    }

    public void ClearTable() {
        dm.whereIsWaldo(71, waldoFile, "Clear table");        
        if (!dm.getDataAreClean()) {
            myYesNoAlerts.showUnsavedDataAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();
            if (yesOrNo.equals("No")) { return; }
        }

        maxCasesInGrid = dm.getMaxVisCases();
        dm.initializeGrid(maxCasesInGrid);
        dm.setDataExists(false);
    } // clearTable
    

    public String getDataFromFile(int startVariable) throws Exception {
        dm.whereIsWaldo(85, waldoFile, "getDataFromFile(int startVariable)");
        try {
            FileChooser fChoose = new FileChooser();
            fChoose.setTitle("Get Data");
            fChoose.setInitialDirectory(dm.getLastPath());
            FileChooser.ExtensionFilter extFilter1
                    = new FileChooser.ExtensionFilter("CSV", "*.csv");
            FileChooser.ExtensionFilter extFilter2
                    = new FileChooser.ExtensionFilter("Tab-Delimited", "*.*");
            fChoose.getExtensionFilters().addAll(extFilter1, extFilter2);

            theFile = fChoose.showOpenDialog(null);            
            if (theFile != null) {                
                if ("CSV".equals(fChoose.getSelectedExtensionFilter().getDescription())) {
                    dm.setDelimiter(',');
                } else {
                    dm.setDelimiter('\t');
                }
            }

            if ((theFile == null) || (theFile.getName().equals(""))) {
                returnStatus = "NoFileName";
                return returnStatus;
            }
            
            returnStatus = parseAndRead(theFile);
            return returnStatus;
        }
        catch(Exception ex) {
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "File_Ops.getDataFromFile()");
            returnStatus = "ExceptionThrown";
            return returnStatus;
        }
    }

    
    private String parseAndRead(File fileName) {
        dm.whereIsWaldo(122, waldoFile, "parseAndRead(File fileName)");
        fileParser = new CSV_FileParser(fileName, dm.getDelimiter());
        returnStatus = fileParser.parseTheFile();
        
        if (returnStatus.equals("Cancel")) { return "Cancel"; }
        
        nVarsInFile = fileParser.getNVariables();
        nCasesInFile = fileParser.getNCases();

        tracker.setNVarsInStruct(nVarsInFile);
        tracker.setNVarsCommitted(nVarsInFile);
        tracker.setNCasesInStruct(nCasesInFile);
        tracker.setNCasesCommitted(nCasesInFile);

        dm.getTheGrid().setNVarsInGrid(Math.min(nVarsInFile, dm.getMaxVisVars()));
        dm.getTheGrid().setNCasesInGrid(Math.min(nCasesInFile, dm.getMaxVisCases()));     
        dm.initalizeForFileRead(nVarsInFile, nCasesInFile);
        
        for (int j = 0; j < nVarsInFile; j++) {
            dm.setVariableNameInStruct(j + startVariable, fileParser.getDataElementColRow(j, 0));
        }
        
        for (int iRow = 0; iRow < nCasesInFile; iRow++) {           
            for (int jCol = 0; jCol < nVarsInFile; jCol++) {                
                tracker.set_CurrentDS(jCol, iRow);  //  DS only b/c dataGrid doesn't exist yet
                dm.setDataInStruct("147 file_Ops", 
                        jCol,
                        iRow,
                        fileParser.getDataElementColRow(jCol, iRow + 1));
            }
        }
        dm.setFileName(fileName);
        dm.setLastPath(fileName);
        tracker.set_CurrentDG_and_DS(0, 0);
        tracker.set_ulDG(0, 0);
        tracker.set_ulDS(0, 0);
        dm.sendDataStructToGrid(0, 0);
        tracker.set_lrDS(nVarsInFile - 1, nCasesInFile - 1);
        
        // So that the cell is not lost after arrow or scroll event
        dm.getDataGrid().setCurrentCellContents(fileParser.getDataElementColRow(0, 1));
        
        tracker.set_CurrentDG_and_DS(0, 0);
        dm.setDataExists(true);

        for (int ithInitColumn = 0; ithInitColumn < nVarsInFile; ithInitColumn++) {
            dm.getAllTheColumns().get(ithInitColumn).determineDataType();
            boolean isNumeric = dm.getAllTheColumns().get(ithInitColumn).getIsNumeric();            
            if (isNumeric) { 
                dm.setVariableNumeric(ithInitColumn, true);
            } else {
                dm.setVariableNumeric(ithInitColumn, false);
            }  
        }
        
        int nInitCols = dm.dataStruct.size();
        int sizeOfCol_0 = dm.getDataStruct().get(0).getColumnSize();        
        for (int ithCol = 1; ithCol < nInitCols; ithCol++) {
            int ithCol_nCases = dm.getDataStruct().get(ithCol).getColumnSize();
            
            if (ithCol_nCases < sizeOfCol_0) {
                dm.getDataStruct().get(ithCol).addUntilNCases(sizeOfCol_0);
            }            
        }
        duplicateLabelsExist = fileParser.getDuplicateLabelsExist();
        return returnStatus;
    } // OpenData

    public void SaveData(Data_Manager dm, boolean getFileName) {
        dm.whereIsWaldo(191, waldoFile, "  *** SaveData(Data_Manager dm, boolean getFileName)");
        int i, j, currVars, currCases;
        
        if (tracker.getNVarsInStruct() == 0) {            
            MyAlerts.showAintGotNoDataAlert(); 
            return;
        }

        File fileName = dm.getFileName();
        
        if (getFileName || (fileName == null)) {
            FileChooser fChoose = new FileChooser();
            fChoose.setInitialDirectory(dm.getLastPath());
            fChoose.setTitle("Save Data");
            FileChooser.ExtensionFilter extFilter1
                    = new FileChooser.ExtensionFilter("CSV", "*.csv");
            FileChooser.ExtensionFilter extFilter2
                    = new FileChooser.ExtensionFilter("Tab-Delimited", "*.*");
            fChoose.getExtensionFilters().addAll(extFilter1, extFilter2);

            fileName = fChoose.showSaveDialog(null);

            if (fChoose.getSelectedExtensionFilter() == null) {
                return;
            }

            if (fChoose.getSelectedExtensionFilter().getDescription() == null) {
                return;
            }
            
            if ("CSV".equals(fChoose.getSelectedExtensionFilter().getDescription())) {
                dm.setDelimiter(',');
            } else {
                dm.setDelimiter('\t');
            }

            if (fileName == null) { return; }
        }

        if (fileName.getName().equals("")) { return; }
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            currVars =tracker.getNVarsInStruct();
            currCases = tracker.getNCasesInStruct();
            for (j = 0; j < currVars; j++) {
                writer.write(dm.getVariableName(j));
                
                if (j < (currVars - 1)) {
                    writer.write(dm.getDelimiter());
                }
            }
            writer.write("\n");
            
            for (i = 0; i < currCases; i++) {                
                for (j = 0; j < currVars; j++) {                    
                    writer.write(dm.getFromDataStruct(j, i));                    
                    if (j < (currVars - 1)) {
                        writer.write(dm.getDelimiter());
                    }
                }
                writer.write("\n");
            }
            writer.close();

            dm.setFileName(fileName);
            dm.setLastPath(fileName);

        } catch (IOException e) { String str = this.toString();}           
    } // SaveData
    
    public String PrintFile(Data_Manager dm) {
        printFile_Controller = new PrintFile_Controller(dm) ;
        returnStatus = printFile_Controller.doTheProcedure();
        return returnStatus;        
    }

    public void ExitProgram(Data_Manager dm) {
        dm.whereIsWaldo(269, waldoFile, "  *** ExitProgram(Data_Manager dm)");
        boolean exit = true;
        System.out.println("269 File_Ops, dataAreClean = " + dm.getDataAreClean());
        if (dm.getDataExists()) {           
            myYesNoAlerts.showUnsavedDataAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();
            if (yesOrNo.equals("No")) { exit = false; }
        }

        if (exit) { System.exit(0); }
    }
    
    public boolean getDuplicateLabelsExist() { return duplicateLabelsExist; }
}
