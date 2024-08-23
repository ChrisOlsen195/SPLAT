/**************************************************
 *            DataCommit_NotTabbing               *
 *                    11/05/23                    *
 *                     18:00                      *
 *************************************************/
package splat;

import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class DataCommit_NotTabbing extends DataCommit_Handler {
    
    //  Make empty if no-print
    //String waldoFile = "DataCommit_NotTabbing";
    String waldoFile = "";
            
    public DataCommit_NotTabbing( PositionTracker tracker, Data_Grid dg, Data_Manager dm) {
        super( tracker, dg, dm);
        dm.whereIsWaldo(19, waldoFile, "DataCommit_NotTabbing, Constructing"); 
        printTheCellStuff = false; printTheInfo = false;
        this.tracker = tracker;
        this.dataGrid = dg;
        this.dm = dm;      

        maxVarsInGrid = dm.getMaxVisVars();
        maxCasesInGrid = dm.getMaxVisCases();
        handlingRightAndDown = false;
        firstEntryDone = false;
    }
    
    public void handleTheNotTabbingCommit (String theStringEntered) {
        dm.whereIsWaldo(32, waldoFile, "handleTheNotTabbingCommit (String theStringEntered)");
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

        if (nCasesInStruct > 0) { firstEntryDone = true; }
            
        String eval = handleCommit_ParseTheString();
        
        if(DataUtilities.strIsADouble(theEntry)) { fixedString = eval; } 
        
        // Advise the user of text entry into a hitherto numeric variable
        else if ((dm.getVariableIsNumeric(ehStructCol) == true) && (!theEntry.equals("") && (!theEntry.equals("*")))) {
            dm.setVariableNumeric(ehGridCol, false);
            MyAlerts.showTextEntryAdvisoryAlert();
        } 
        
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
            handleNonTabbingCommit_RightAndDown();
        }
        //  else, if it is either below or to the right of existing data...
        else if (tracker.cursorIsBeyondLastVariable() || tracker.cursorIsBeyondLastCase()) {
            if (tracker.cursorIsBeyondLastVariable()) { 
                handleNonTabbingCommit_ToRight();
            }
            if (tracker.cursorIsBeyondLastCase()) {
                handleNonTabbingCommit_Below();
            }
        }
        else if (tracker.cursorIsAtLastCase() && tracker.cursorIsAtBottomOfGrid()) {
            setFromGridToStruct("          80 eh, tracker.cursorIsAtLastCase() && tracker.cursorIsAtBottomOfGrid()", theEntry) ;
            parsedString = handleCommit_ParseTheString();
            dataGrid.setCurrentCellContents(parsedString);
            dm.sendDataStructToGrid(ehGridCol, ehGridRow);
        }
        else {  //  ... it must be a change in an existing data cell
            parsedString = handleCommit_ParseTheString();
            dataGrid.setCurrentCellContents(fixedString);
        }
} 
    
    public void handleNonTabbingCommit_RightAndDown() {
        dm.whereIsWaldo(91, waldoFile, "handleNonTabbingCommit_RightAndDown()");
        handlingRightAndDown = true;  
        handleNonTabbingCommit_ToRight();
        handleNonTabbingCommit_Below();
        handlingRightAndDown = false;
    }
    private void handleNonTabbingCommit_ToRight() {
        dm.whereIsWaldo(98, waldoFile, "handleNonTabbingCommit_ToRight()");
        
        if (!handlingRightAndDown) {
            setFromGridToStruct("          102 nteh, !handlingRightAndDown", theEntry) ;
            parsedString = handleCommit_ParseTheString();
            dataGrid.setCurrentCellContents(fixedString);
            dm.sendDataStructToGrid(ehGridCol, ehGridRow);
        }
    }
    
    private void handleNonTabbingCommit_Below() {
        dm.whereIsWaldo(109, waldoFile, "handleNonTabbingCommit_Below()");
        setFromGridToStruct("     110 eh, handleEnter_Below(), NOT tabbing", theEntry);
        parsedString = handleCommit_ParseTheString();
        dataGrid.setCurrentCellContents(parsedString);
        dm.sendDataStructToGrid(ehGridCol, ehGridRow); 
    } 
    
    private void setFromGridToStruct(String message, String toThisValue) {
        dm.whereIsWaldo(117, waldoFile, "setFromGridToStruct(String message, String toThisValue)");
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
