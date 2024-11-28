/**************************************************
 *           DataCommit_While_Tabbing             *
 *                    11/27/23                    *
 *                     12:00                      *
 *************************************************/

package splat;

import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class DataCommit_WhileTabbing extends DataCommit_Handler {
    
     //  Make empty if no-print
    //String waldoFile = "DataCommit_WhileTabbing";
    String waldoFile = "";
            
    public DataCommit_WhileTabbing( PositionTracker tracker, Data_Grid dg, Data_Manager dm) {
        super( tracker, dg, dm);
        dm.whereIsWaldo(20, waldoFile, "DataCommit_WhileTabbing, Constructing");
        printTheCellStuff = false; printTheInfo = false;
        this.tracker = tracker;
        this.dataGrid = dg;
        this.dm = dm;
        
    //  Make empty if no-print
        //waldoFile = "DataCommit_WhileTabbing";
        waldoFile = "";
        
        maxVarsInGrid = dm.getMaxVisVars();
        maxCasesInGrid = dm.getMaxVisCases();
        handlingRightAndDown = false;
        firstEntryDone = false;
    }
    
    public void handleTheTabbingCommit (String theStringEntered) {
        dm.whereIsWaldo(37, waldoFile, "DataCommit_WhileTabbing, handleTheTabbingCommit ");
        tabOrEnter = dataGrid.getTabOrEnter();
        fixedString = theStringEntered;  //  Initialize
        ehGridCol = tracker.getCurrentGridColumn();
        ehGridRow = tracker.getCurrentGridRow();
        tracker.set_CurrentDG_and_DS(ehGridCol, ehGridRow);
        ehStructCol = tracker.getCurrentStructColumn();
        ehStructRow = tracker.getCurrentStructRow();
        theEntry = theStringEntered;

        nCasesInStruct = tracker.getNCasesInStruct();
        nVarsInStruct = tracker.getNVarsInStruct();

        tabRectLeftIndex = dataGrid.getTabRectLeftIndex();
        tabRectRightIndex = dataGrid.getTabRectRightIndex();

        if (nCasesInStruct > 0) { firstEntryDone = true; }
        
        dm.whereIsWaldo(55, waldoFile, "DataCommit_WhileTabbing, handleTheTabbingCommit ");    
        String eval = handleCommit_ParseTheString();
        if(DataUtilities.strIsADouble(theEntry)) {
            fixedString = eval;
        }   //  endif
        // Advise the user of text entry into a hitherto numeric variable
        else if ((dm.getVariableIsNumeric(ehStructCol) == true) && (!theEntry.equals("") && (!theEntry.equals("*")))) {
            dm.setVariableNumeric(ehGridCol, false);
            MyAlerts.showTextEntryAdvisoryAlert();
        }   //  end elseif
        
        /******************************************************************
        *       Locate the cursor in the DataGrid                         *
        ******************************************************************/
        
        if (!firstEntryDone) {
            handleCommit_DoFirstDataEntry();
            firstEntryDone = true;
            return;
        }

        //  If the cursor is both below and to the right of existing data...
        if (tracker.cursorIsOutsideDataRectangle()) {
            handleTabbingCommit_RightAndDown();
        }
        //  else, if it is either below or to the right of existing data...
        else if (tracker.cursorIsBeyondLastVariable() || tracker.cursorIsBeyondLastCase()) {  
            if (tracker.cursorIsBeyondLastVariable()) { 
                handleTabbingCommit_ToRight();
            }
            
            if (tracker.cursorIsBeyondLastCase()) {
                handleTabbingCommit_Below();
            }
        }
        else {  //  ... it must be a change in an existing data cell

            parsedString = handleCommit_ParseTheString();
            setFromGridToStruct("     106 DataCommit_WhileTabbing, parsedString", parsedString) ;
            dataGrid.setCurrentCellContents(fixedString);
            dm.sendDataStructToGrid(ehGridCol, ehGridRow);
        }
}   //  handleTheCommit
    
    public void handleTabbingCommit_RightAndDown() {
        dm.whereIsWaldo(100, waldoFile, "DataCommit_WhileTabbing, handleTabbingCommit_RightAndDown()");
        handlingRightAndDown = true;  
        handleTabbingCommit_ToRight();
        handleTabbingCommit_Below();
        handlingRightAndDown = false;
    }

    
    private void handleTabbingCommit_ToRight() {
        dm.whereIsWaldo(109, waldoFile, "DataCommit_WhileTabbing, handleTabbingCommit_ToRight()");       
        if (!handlingRightAndDown) {
            parsedString = handleCommit_ParseTheString();
            dataGrid.setCurrentCellContents(fixedString);
            setFromGridToStruct("          125 teh, !handlingRightAndDown", theEntry) ;
            dm.sendDataStructToGrid(ehGridCol, ehGridRow);           
        }       

        tracker.set_CurrentDG_and_DS(ehGridCol, ehGridRow);
        
        if (!tracker.cursorIsAtRightOfGrid()) {
            tracker.set_CurrentDG_and_DS(ehGridCol + 1, ehGridRow); //  !!!!
        }
        else {  //  At right of grid
            int currentULDG_Col = tracker.get_ulDG().getCol();
            int currentULDG_Row = tracker.get_ulDG().getRow();
            tracker.set_ulDG(currentULDG_Col + 1, currentULDG_Row);
            tracker.set_CurrentDG_and_DS(ehGridCol, ehGridRow);
            setFromGridToStruct("          140 teh, Tabbing, at right of grid", theEntry) ;
            dataGrid.setCurrentCellContents(fixedString);
            dm.sendDataStructToGrid(ehGridCol, ehGridRow);
        }
    }
    
    private void handleTabbingCommit_Below() {
        dm.whereIsWaldo(134, waldoFile, "DataCommit_WhileTabbing, handleTabbingCommit_Below()");
    }   
    
    private void setFromGridToStruct(String message, String toThisValue) {
        dm.whereIsWaldo(138, waldoFile, "DataCommit_WhileTabbing, setFromGridToStruct(String message, String toThisValue) ");
        structCol = ehStructCol;
        structRow = ehStructRow;
        dm.setDataInStruct(message, structCol, structRow, toThisValue);
        
        if (structCol + 1 > tracker.getNVarsCommitted()) {
            tracker.setNVarsCommitted(structCol + 1);
        }
        
        if (structRow + 1 > tracker.getNCasesCommitted()) {
            tracker.setNCasesCommitted(structRow + 1);
        }   
    }
}
