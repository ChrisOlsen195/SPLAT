/************************************************************
 *                            FileOps                       *
 *                           04/13/25                       *
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
    //String waldoFile = "File_Ops";
    String waldoFile = "";
    
    // My classes
    CSV_FileParser fileParser;
    Data_Manager dm;
    MyYesNoAlerts myYesNoAlerts;
    PositionTracker tracker;
    PrintFile_Controller printFile_Controller;
    
    File theFile;

    public File_Ops(Data_Manager dm) {
        dm.whereIsWaldo(50, waldoFile, " *** Constructing File_Ops, no file name");
        this.dm = dm;
        tracker = dm.getPositionTracker();
        dm.setDataAreClean(true);
        returnStatus = "Ok";
        myYesNoAlerts = new MyYesNoAlerts();
        dm.setRawOrSummary(("NULL"));
        dm.setTIorTIDY("NULL");
    }
    
    public File_Ops( String fileName, Data_Manager dm) {
        dm.whereIsWaldo(61, waldoFile, " *** Constructing File_Ops with file name");
        this.dm = dm;
        tracker = dm.getPositionTracker();
        theFile = new File(fileName);
        dm.getMainMenu().setFileLabel(fileName);
        parseAndRead(theFile);
        myYesNoAlerts = new MyYesNoAlerts();
        dm.setRawOrSummary(("NULL"));
        dm.setTIorTIDY("NULL");
    }

    public void ClearTable() {
        dm.whereIsWaldo(73, waldoFile, "Clear table"); 
        if (!dm.getDataAreClean()) {
            myYesNoAlerts.setTheYes("Trash it!");     //  Trash the data
            myYesNoAlerts.setTheNo("Save it!");    //  Keep the data
            myYesNoAlerts.showFileIsDirtyAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();
            if (yesOrNo.equals("No")) { return; }
        }
        // Reinitialize values for 'no data'
        dm.setRawOrSummary(("NULL"));
        dm.setTIorTIDY("NULL");
        maxCasesInGrid = dm.getMaxVisCases();
        dm.initializeGrid(maxCasesInGrid);
        dm.setDataExists(false);
    } // clearTable
    

    public String getDataFromFile(int startVariable) throws Exception {
        dm.whereIsWaldo(91, waldoFile, "getDataFromFile(int startVariable)");
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
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "113 File_Ops.getDataFromFile()");
            returnStatus = "ExceptionThrown";
            return returnStatus;
        }
    }

    
    private String parseAndRead(File fileName) {
        dm.whereIsWaldo(128, waldoFile, "parseAndRead(File fileName)");
        /*************************************************************
         *    New file -->  format (Tidy or TI8x) unknown            *
         ************************************************************/
        dm.setTIorTIDY(("NULL"));
        fileParser = new CSV_FileParser(fileName, dm.getDelimiter());
        returnStatus = fileParser.parseTheFile();
        
        if (returnStatus.equals("Cancel")) { return "Cancel"; }
        
        nVarsInFile = fileParser.getNVariables();
        nCasesInFile = fileParser.getNCases();

        tracker.setNVarsInStruct(nVarsInFile);
        dm.getTheGrid().setNVarsInStruct(nVarsInFile);
        tracker.setNVarsCommitted(nVarsInFile);
        tracker.setNCasesInStruct(nCasesInFile);
        dm.getTheGrid().setNCasesInStruct(nCasesInFile);
        tracker.setNCasesCommitted(nCasesInFile);

        dm.getTheGrid().setNVarsInGrid(Math.min(nVarsInFile, dm.getMaxVisVars()));
        dm.getTheGrid().setNCasesInGrid(Math.min(nCasesInFile, dm.getMaxVisCases()));     
        dm.initalizeForFileRead(nVarsInFile, nCasesInFile);
        dm.getTheGrid().goHome();    // ****************************
        
        for (int j = 0; j < nVarsInFile; j++) {
            dm.setVariableNameInStruct(j + startVariable, fileParser.getDataElementColRow(j, 0));
        }
        
        for (int iRow = 0; iRow < nCasesInFile; iRow++) {           
            for (int jCol = 0; jCol < nVarsInFile; jCol++) {                
                tracker.set_CurrentDS(jCol, iRow);  //  DS only b/c dataGrid doesn't exist yet
                dm.setDataInStruct("157 file_Ops", 
                        jCol,
                        iRow,
                        fileParser.getDataElementColRow(jCol, iRow + 1));
            }
        }
        dm.setTheFile(fileName);
        dm.setLastPath(fileName);
        tracker.set_Current_DG_DS(0, 0, "170 File_Ops");
        tracker.set_ulDG(0, 0);
        tracker.set_ulDS(0, 0);
        dm.sendDataStructToGrid(0, 0);
        tracker.set_lrDS(nVarsInFile - 1, nCasesInFile - 1);
        
        // So that the cell is not lost after arrow or scroll event
        dm.getDataGrid().setCurrentCellContents(fileParser.getDataElementColRow(0, 1));
        
        tracker.set_Current_DG_DS(0, 0, "177 File_Ops");
        dm.setDataExists(true);

        for (int ithInitColumn = 0; ithInitColumn < nVarsInFile; ithInitColumn++) {
            dm.getAllTheColumns().get(ithInitColumn).determineDataType();
            String dataType = dm.getAllTheColumns().get(ithInitColumn).getDataType();            
            if (dataType.equals("Quantitative")) { 
                dm.setDataType(ithInitColumn, "Quantitative");
            } else {
                dm.setDataType(ithInitColumn, "Categorical");
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
        dm.whereIsWaldo(204, waldoFile, "  --- SaveData(Data_Manager dm, boolean getFileName)");
        int i, j, currVars, currCases;
        
        if (tracker.getNVarsInStruct() == 0) {            
            MyAlerts.showAintGotNoDataAlert(); 
            return;
        }

        File fileName = dm.getTheFile();
        
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

            if (fChoose.getSelectedExtensionFilter() == null) { return; }

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

            dm.setTheFile(fileName);
            dm.setLastPath(fileName);

        } catch (IOException e) { String str = this.toString();}           
    } // SaveData
    
    public String PrintFile(Data_Manager dm) {
        printFile_Controller = new PrintFile_Controller(dm) ;
        returnStatus = printFile_Controller.doTheProcedure();
        return returnStatus;        
    }

    public void ExitProgram(Data_Manager dm) {
        dm.whereIsWaldo(280, waldoFile, "  --- ExitProgram(Data_Manager dm)");
        boolean exit = true;

        if (!dm.getDataAreClean()) {  
            myYesNoAlerts.setTheYes("Trash it!");
            myYesNoAlerts.setTheNo("Save it!");
            myYesNoAlerts.showFileIsDirtyAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();
            if (yesOrNo.equals("No")) { exit = false; }
        }

        if (exit) { System.exit(0); }
    }
    
    public boolean getDuplicateLabelsExist() { return duplicateLabelsExist; }
}
