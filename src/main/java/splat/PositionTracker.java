/************************************************************
 *                      PositionTracker                     *
 *                          05/25/24                        *
 *                            15:00                         *
 ***********************************************************/
// It is possible (i.e. allowed) to click 'outside' the data structure in
// the DataGrid. This will of  course happen at initial data entry, but
// could also occur by  accident or if the data is 'sparse.'

// All coordinate systems are zero-based:
// 1.  The DataStruct of mCases x nCols
// 2.  The DataGrid

// Only the presented-to-user cases and variables will be 1-based, and will
// be coded with xxx + 1 subscripts.
package splat;
import dataObjects.Cell_Information;
import utilityClasses.MyAlerts;

public class PositionTracker {    
    boolean printTheCursorStatus, sneakingInANewColumn;

    int nResidualsCalculated, nPredsCalculated;
    
    private int maxCasesInGrid, maxVarsInGrid, nVarsCommitted, nCasesCommitted;
    
    //  Make empty if no-print
    //String waldoFile = "PositionTracker";
    String waldoFile = "";
    
    // My Classes
    Cell_Information cellInfo_lrDG, cellInfo_CurrentGrid;
    Cell_Information cellInfo_ulDS, cellInfo_ulDG;
    Cell_Information  cellInfo_lrDS, cellInfo_CurrentStruct;
    Data_Grid dg;
    Data_Manager dm;
    
    int firstCaseInGrid, lastCaseInGrid, firstVarInGrid, lastVarInGrid;
    int nCasesInStruct;
    
    public PositionTracker() { }    //  Needed by Var_List??

    // Establishes a graphical window for the display of the data under consideration (??).
    public PositionTracker(Data_Manager dm, int max_var, int max_case) {
        dm.whereIsWaldo(45, waldoFile, "Constructing");
        printTheCursorStatus = false;
        maxCasesInGrid = max_case;
        maxVarsInGrid = max_var;
        /*
            In regression, more than 1 set of residuals might be calculated;
            they need to have unique names in the data structure to be found
        */
        nResidualsCalculated = 0;
        nPredsCalculated = 0;
        this.dm = dm;
        cellInfo_lrDG = new Cell_Information();
        cellInfo_ulDG = new Cell_Information();
        cellInfo_ulDS = new Cell_Information();
        cellInfo_lrDS = new Cell_Information();
        cellInfo_CurrentGrid = new Cell_Information();
        cellInfo_CurrentStruct = new Cell_Information();
        cellInfo_CurrentStruct = new Cell_Information();
        
        /*******************************************************************
        *  Initialize ulDG and ulDS to (-1, -1) so the first data entry,   *
        *  presumably at (0, 0) is "beyond current data rectangle."        *
        *******************************************************************/
        cellInfo_ulDG.setColAndRow(-1, -1);
        cellInfo_ulDS.setColAndRow(-1, -1);
        cellInfo_lrDS.setColAndRow(-1, -1);
        
        cellInfo_lrDG.setCol(max_var - 1);  //  At init this is 6 columns
        cellInfo_lrDG.setRow(max_case - 1);  //  At init this is 12 rows
      
        firstCaseInGrid = 0;
        lastCaseInGrid = max_case - 1;
        firstVarInGrid = 0;
        lastVarInGrid = max_var - 1;
        
        setNVarsCommitted(0); setNCasesCommitted(0);   
    }
    
    public void updateMaxCases(int newMax) { 
        //System.out.println("84 Position tracker, maxCasesInGrid = " + maxCasesInGrid);
        maxCasesInGrid = newMax; }
    
    public void updateMaxVars(int newMax) { 
        //System.out.println("88 Position tracker, maxVarsInGrid = " + maxVarsInGrid);
        maxVarsInGrid = newMax;}    
    
    public void setTrackerDataGrid(Data_Grid dg) {this.dg = dg; }         
    public Cell_Information get_ulDG() {return cellInfo_ulDG; } 
    public Cell_Information get_lrDG() {return cellInfo_lrDG; }    
    public Cell_Information get_ulDS() {return cellInfo_ulDS; } 
    public Cell_Information get_lrDS() {return cellInfo_lrDS; }
    
    public void set_lrDS(int toThis_DSCol, int toThis_DSRow) {
        cellInfo_lrDS.setCol(toThis_DSCol);
        cellInfo_lrDS.setRow(toThis_DSRow);
        int dsRow = cellInfo_lrDS.getRow();
        nCasesInStruct = dsRow + 1;
    }
    
    public void set_ulDS(int toThis_DSCol, int toThis_DSRow) {
        cellInfo_ulDS.setCol(toThis_DSCol);
        cellInfo_ulDS.setRow(toThis_DSRow);
    } 
    
    public void set_lrDG(int toThis_DGCol, int toThis_DGRow) {
        cellInfo_lrDG.setCol(toThis_DGCol);
        cellInfo_lrDG.setRow(toThis_DGRow); 
    }  
    
    public void set_ulDG(int toThis_DGCol, int toThis_DGRow) {
        cellInfo_ulDG.setCol(toThis_DGCol);
        cellInfo_ulDG.setRow(toThis_DGRow);
        firstVarInGrid = toThis_DGCol;
        firstCaseInGrid = toThis_DGRow;
    } 
    
    public Cell_Information get_CurrentDG() { return cellInfo_CurrentGrid; }
    
    public void set_CurrentDG_and_DS(int toThisCol, int toThisRow) {
        dm.whereIsWaldo(124, waldoFile, "set_CurrentDG_and_DS");
        //System.out.println("125 PosTrack, toThisCol/Row = " + toThisCol + " / " + toThisRow);
        if (toThisRow > maxCasesInGrid) { 
            MyAlerts.showUnexpectedErrorAlert("PosTracker 128 Attempt to set DG/DS off the grid");
            return; }
        cellInfo_CurrentGrid.setColAndRow(toThisCol, toThisRow);
        cellInfo_CurrentStruct.setColAndRow(toThisCol + firstVarInGrid, toThisRow + firstCaseInGrid);
        //System.out.println("131 PosTrack, toThisCol/Row = " + toThisCol + " / " + toThisRow);
        dg.resetBlueCellPosition(toThisCol, toThisRow);
    } 
    
    public void set_CurrentDG_DS_Contents(int toThisCol, int toThisRow, String toThisContent) {
        cellInfo_CurrentGrid.setColAndRow(toThisCol, toThisRow);
        cellInfo_CurrentStruct.setColAndRow(toThisCol + firstVarInGrid, toThisRow + firstCaseInGrid);
        dg.resetBlueCellPosition(toThisCol, toThisRow);
    } 
    
    public Cell_Information get_CurrentDS() { return cellInfo_CurrentStruct; }
  
    public void set_CurrentDS(int toThisCol, int toThisRow) {
        cellInfo_CurrentStruct.setColAndRow(toThisCol, toThisRow);   
    }
    
    public int getMaxCasesInGrid() { 
        //System.out.println("148 Position tracker, GetMaxCasesInGrid = " + maxCasesInGrid);
        return maxCasesInGrid; }
    public int getMaxVarsInGrid() { 
        //System.out.println("151 Position tracker, GetMaxVarsInGrid = " + maxVarsInGrid);
        return maxVarsInGrid; }


    public Cell_Information cpiDG_to_cpiDS(Cell_Information cpi_DG) {
        Cell_Information cpi_transformed = new Cell_Information();
        cpi_transformed.setCol(cpi_DG.getCol() + cellInfo_ulDG.getCol());
        cpi_transformed.setCol(cpi_DG.getRow() + cellInfo_ulDG.getRow());
        return cpi_transformed;
    } 
   
    public Cell_Information cpiDS_to_cpiDG(Cell_Information cpi_DS) {
        Cell_Information cpi_transformed = new Cell_Information();
        cpi_transformed.setCol(cpi_DS.getCol() - cellInfo_ulDG.getCol());
        cpi_transformed.setRow(cpi_DS.getRow() - cellInfo_ulDG.getRow());
        return cpi_transformed;
    }     
    
    /**********************************************************************
    *              getStructCol() and getStructRow()                      *
    *                                                                     *
    *   These methods return the DataStruct locations of the DataGrid     *
    *   cell.  These are called from sendCellInfoToStruct() in DataGrid.  *
    *                                                                     * 
    **********************************************************************/
    
    public int getCurrentStructColumn() { return get_CurrentDS().getCol(); }    
    public int getCurrentStructRow() { return get_CurrentDS().getRow(); }    
    public int getCurrentGridColumn() { return cellInfo_CurrentGrid.getCol(); }    
    public int getCurrentGridRow() { return cellInfo_CurrentGrid.getRow(); }    
    public int getNCasesInStruct() { return cellInfo_lrDS.getRow() + 1; } 
    
    public void setNCasesInStruct(int toThis) {
        cellInfo_lrDS.setRow(toThis - 1); 
    }
    
    public int getNVarsInStruct() { return cellInfo_lrDS.getCol() + 1; }
    
    public void setNVarsInStruct(int toThis) {
        cellInfo_lrDS.setCol( toThis - 1); 
    }   

    public int getFirstCaseIdentifier() { return firstCaseInGrid; }
    public int getFirstVarIdentifier() { return firstVarInGrid; }
    public int getLastCaseInGrid() { return lastCaseInGrid;  }  
    
    public void setFirstVarIdentifier(int toThisCol) { 
        dm.whereIsWaldo(198, waldoFile, "setFirstVarIdentifier");
        firstVarInGrid = toThisCol;
        cellInfo_ulDG.setCol(toThisCol);
        cellInfo_ulDS.setCol(toThisCol);
        lastVarInGrid = firstVarInGrid + maxVarsInGrid - 1;
        cellInfo_lrDG.setCol(lastVarInGrid);
    }
 
    // To do:  Untangle this first/last case mess!!!
    public void setFirstCaseIdentifier(int toThis) { 
        dm.whereIsWaldo(208, waldoFile, "setFirstCaseIdentifier");
        firstCaseInGrid = toThis; 
        lastCaseInGrid = firstCaseInGrid + maxCasesInGrid - 1;
        set_ulDG(cellInfo_ulDG.getCol(), firstCaseInGrid);
        set_ulDS(cellInfo_ulDS.getCol(), firstCaseInGrid);
    }
    
    public boolean getSneakingInANewColumn() { return sneakingInANewColumn; }
    
    public void setSneakingInANewColumn(boolean toThis) {
        sneakingInANewColumn = toThis;
    }
    
    public int getCurrentDGCol() { return dg.get_dgCol(); }
    public int getCurrentDGRow() { return dg.get_dgCol(); }    
    public int getTabRectFirstVar() {return dg.tabRectLeftFirstVar; }
    public int getTabRectLeftIndex() { return dg.tabRectLeftIndex; }
    public int getTabRectRightIndex() { return dg.tabRectRightIndex; } 
    public int getTabRectLeftDelta() {return dg.tabRectLeftDelta; }
    public int getTabRectCurrentIndex() {return dg.tabRectCurrentIndex; }    
    public int getNVarsCommitted() {return nVarsCommitted; }
    public void setNVarsCommitted(int toThis) { nVarsCommitted = toThis; }    
    public int getNCasesCommitted() {return nCasesCommitted; }
    public void setNCasesCommitted(int toThis) { nCasesCommitted = toThis; }
    public int getNResidualsCalculated() { return nResidualsCalculated; }
    
    public void setNResidualsCalculated(int toThis) { 
        nResidualsCalculated = toThis;
    }
    
    public int getNPredictedsCalculated() { return nPredsCalculated; }
    public void setNPredictedsCalculated(int toThis) { 
        nPredsCalculated = toThis;
    }
    
    //  These methods have a relatively ambiguous names.  The intended sense
    //  is to indicate where the cursor was at the time the mouse was clicked,
    //  not where it landed as a response to the mouse click.
    
    public boolean cursorIsAtFirstCase() {
        boolean cursorIsAtFirstCase = (cellInfo_CurrentStruct.getRow() == 0);
        return cursorIsAtFirstCase;       
    }

    public boolean cursorIsAtLastCase() {
        boolean cursorIsAtLastCase = (cellInfo_CurrentStruct.getRow() == cellInfo_lrDS.getRow());
        //System.out.println("254 PositionTracker, cursorIsAtLastCase = " + cursorIsAtLastCase);
        return cursorIsAtLastCase;       
    }
    
    public boolean cursorIsAtFirstVariable() {
        boolean cursorIsAtFirstVariable = (cellInfo_CurrentStruct.getCol() == 0);
        return cursorIsAtFirstVariable;       
    }
    
    public boolean cursorIsAtLastVariable() {
        boolean cursorIsAtLastVariable = (cellInfo_CurrentStruct.getCol() == cellInfo_lrDS.getCol());
        //System.out.println("265 PositionTracker, cursorIsAtLastVariable = " + cursorIsAtLastVariable);
        return cursorIsAtLastVariable;          
    }
    
    public boolean cursorIsAtTopOfGrid() {
        boolean cursorIsAtTopOfGrid = (cellInfo_CurrentGrid.getRow() == 0);
        return cursorIsAtTopOfGrid;       
    }
    
    public boolean cursorIsAtBottomOfGrid() {
        boolean cursorIsAtBottomOfGrid = (cellInfo_CurrentGrid.getRow() == (maxCasesInGrid - 1));
        return cursorIsAtBottomOfGrid;       
    }   
     
    public boolean cursorIsAtLeftOfGrid() {
        boolean cursorIsAtLeftOfGrid = (cellInfo_CurrentGrid.getCol() == 0);
        return cursorIsAtLeftOfGrid;       
    }
     
    public boolean cursorIsAtRightOfGrid() {
        boolean cursorIsAtRightOfGrid = (cellInfo_CurrentGrid.getCol() == (maxVarsInGrid - 1)); 
        return cursorIsAtRightOfGrid;   
    } 
    
    public boolean cursorIsInCurrentDataRectangle() {
        boolean cursorIsInCurrentDataRectangle = (!cursorIsBeyondLastCase() && !cursorIsBeyondLastVariable());
        return cursorIsInCurrentDataRectangle; 
    }
    
    public boolean cursorIsBeyondLastCase() {
        int currentDSRow = get_CurrentDS().getRow();
        boolean cursorIsBeyondLastCase = (currentDSRow > cellInfo_lrDS.getRow());
        return cursorIsBeyondLastCase;       
    }
    
    public boolean cursorIsBeyondLastVariable() {
        boolean cursorIsBeyondLastVariable = (get_CurrentDS().getCol() > cellInfo_lrDS.getCol());
        return cursorIsBeyondLastVariable; 
    }
    
    public boolean cursorIsOutsideDataRectangle() {
        boolean cursorIsOutsideDataRectangle = (cursorIsBeyondLastCase() && cursorIsBeyondLastVariable());
        return cursorIsOutsideDataRectangle; 
    }
    
    //  For calling for printing from the DataGrid class
    public void printCursorStatusViaDataGrid(String withThisMessage) {
        boolean tempPrtCursorStatus = printTheCursorStatus;
        printTheCursorStatus = true;
        printCursorStatus(withThisMessage);
        printTheCursorStatus = tempPrtCursorStatus;
    }
    
    // For diagnostic purposes    
    public void printCursorStatus(String withThisMessage) {
        if (printTheCursorStatus) {
            //int getDSRow = cellInfo_CurrentStruct.getRow();
            //int getDSCol = cellInfo_CurrentStruct.getCol();
            //int getDGRow = cellInfo_CurrentGrid.getRow();
            //int getDGCol = cellInfo_CurrentGrid.getCol();
            
            //boolean cursorIsAtFirstCase = (getDSRow == 0);     

            //boolean cursorIsAtLastCase = (getDSRow == cellInfo_lrDS.getRow());

            //boolean cursorIsAtFirstVariable = (getDSCol == 0);  

            //boolean cursorIsAtLastVariable = (cellInfo_CurrentStruct.getCol() == cellInfo_lrDS.getCol());       

            //boolean cursorIsAtTopOfGrid = (getDSRow == 0);    

            //boolean cursorIsAtBottomOfGrid = (getDGRow == (maxCasesInGrid - 1));

            //boolean cursorIsAtLeftOfGrid = (getDSCol == 0);    

            //boolean cursorIsAtRightOfGrid = (getDSCol == maxVarsInGrid - 1); 

            //boolean cursorIsInCurrentDataRectangle = (!cursorIsBeyondLastCase() && !cursorIsBeyondLastVariable());

            //boolean cursorIsBeyondLastCase = (getDSRow > cellInfo_lrDS.getRow() - 1);

            //boolean cursorIsBeyondLastVariable = (get_CurrentDS().getCol() > cellInfo_lrDS.getCol() - 1);

            //boolean cursorIsOutsideDataRectangle = (cursorIsBeyondLastCase() && cursorIsBeyondLastVariable());
        }
    }
}
