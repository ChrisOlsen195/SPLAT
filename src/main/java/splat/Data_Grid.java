/************************************************************
 *                          Data_Grid                       *
 *                           12/26/25                        *
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
/*****************************************************************************
* dgCol and dgRow keep track of the position of the cursor in the DataGrid.  *
* The tracker is responsible elsewhere.                                      *
*****************************************************************************/
package splat;

import java.io.File;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import static javafx.geometry.Pos.CENTER_LEFT;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import utilityClasses.PrintExceptionInfo;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class Data_Grid {
    
    boolean firstEntryDone;
    
    boolean comingFromLeftRightClick, weAreTabbing;
    
    private int nVarsInStruct, nCasesInStruct, nCasesInGrid, nVarsInGrid;
    
    private int maxVisualCasesInGrid, maxVisualVarsInGrid, firstDG_Var, lastDG_Var,
                firstDS_Var, firstDS_Case, firstDG_Case, lastDG_Case;
    
    int dgCol, dgRow, pxCellWidth;
    
    // tabRect information is all zero-based
    int tabRectLeftIndex,       //  struct column where tab begins
        tabRectRightIndex,      //  struct columne where tab ends
        tabRectLeftDelta,       //  inset from tabRectLeftIndex
        tabRectLeftFirstVar,    //  struct column of window boundary left
        tabRectCurrentIndex,    //  struct column of cursor
        lastWindowColumn;
    
    private String eventType, currentCellContents, pendingCellContents;
    private String fromStorage, fixedString, tabOrEnter;
    
    // Make empty if no-print
    //String waldoFile = "Data_Grid";
    String waldoFile = "";
    
    //  My classes
    private Data_Manager dm;
    private PositionTracker positionTracker;
    private final DataCommit_NotTabbing dataCommit_NotTabbing;
    private final DataCommit_WhileTabbing dataCommit_WhileTabbing;
    // POJOs / FX
    private final GridPane gridPane;
    
    private ArrayList<ArrayList<TextField>>theGridCells;
    
    EventHandler<MouseEvent> mouseHandler = (MouseEvent me) -> {
        Object tfObject = me.getSource();
        dgRow = GridPane.getRowIndex((TextField) tfObject);
        dgCol = GridPane.getColumnIndex((TextField) tfObject); 
        positionTracker.set_Current_DG_DS(dgCol, dgRow, "79 Data_Grid");
        dm.sendDataStructToGrid(dgCol, dgRow);
        positionTracker.set_Current_DG_DS(dgCol, dgRow, "81 Data_Grid");
        prepareCellForAction(dgCol, dgRow);
    };
    
    public Data_Grid(Data_Manager dm, PositionTracker positionTracker) { 
        dm.whereIsWaldo(86, waldoFile, "Data_Grid(Data_Manager dm, PositionTracker tracker)");
        this.dm = dm; 
        this.positionTracker = positionTracker;

        dataCommit_NotTabbing = new DataCommit_NotTabbing(positionTracker, this, dm);
        dataCommit_WhileTabbing = new DataCommit_WhileTabbing(positionTracker, this, dm);
        pxCellWidth = 105;
        
        gridPane = new GridPane();
        
        // ---------------  Drag and Drop starts here  -------------------
        gridPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        
        // Dropping over surface
        gridPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;                
                if (db.hasFiles()) {
                    success = true;
                    String filePath = null;                    
                    for (File file:db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        String lastFourChars = StringUtilities.getRightMostNChars(filePath, 4);                       
                        if (lastFourChars.equals(".csv") || lastFourChars.equals(".CSV")) {
                            File_Ops fileOps = new File_Ops(filePath, dm);
                        } else {
                            MyAlerts.nonCSVFileAlert();
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });        

        maxVisualVarsInGrid = dm.getMaxVisVars();
        maxVisualCasesInGrid = dm.getMaxVisCases();
        nCasesInStruct = positionTracker.getNCasesInStruct();
        nVarsInStruct = positionTracker.getNVarsInStruct();
        firstEntryDone = false;
        currentCellContents = "";
        pendingCellContents = "";
        weAreTabbing = false;
        
        theGridCells = new ArrayList<>();   //  This is a 2-dimension array of Strings
        
        gridPane.setOnScroll(
            new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                    dm.setHasBeenScrolled(true);
                    double yScale = event.getDeltaY();
                    
                    if (yScale < 0) { goDownOneRow(); }
                    else if (yScale > 0) { goUpOneRow(); }
                    
                    event.consume();
                }
            });
 
        EventHandler<KeyEvent> pressFilter = (KeyEvent e) -> {
            eventType = e.getEventType().getName();
            String theKeyCode = e.getCode().toString();
            nCasesInStruct = positionTracker.getNCasesInStruct();
            nVarsInStruct = positionTracker.getNVarsInStruct();
            
            if(eventType.equals("KEY_PRESSED")) {  
                // -----------------------------------------------------------
                // This is just a test to see if ControlDown can be captured.
                // It is anticipated to use this for copy and paste.
                //if (theKeyCode.equals("EQUALS") 
                //    && e.isControlDown()) {
                //    System.out.println("Hallelujah!!!");
                //}
                // -----------------------------------------------------------
                //System.out.println("177 DataGrid, theKeyCode = " + theKeyCode);
                switch (theKeyCode) {
                    case "ESCAPE":
                        dm.setHasBeenScrolled(false);
                        e.consume();
                        break;
                    case "ENTER":
                        dm.whereIsWaldo(181, waldoFile, "case = Enter");
                        tabOrEnter = "ENTER";
                        positionTracker.set_Current_DG_DS(dgCol, dgRow, "183 Data_Grid");
                        doDataCommit();
                        if (!weAreTabbing) {
                            dm.whereIsWaldo(186, waldoFile, "ENTER, !weAreTabbing");
                            // Append that last character before the ENTER
                            // If Cell contents are empty, just restore the DataGrid
                            
                            if (pendingCellContents.isEmpty()) {
                                dm.whereIsWaldo(191, waldoFile, "ENTER, pendingCellContents.isEmpty()");
                                replaceEmpty();
                                goDownOneRow();
                            } 
                            else {  //  not Empty
                                dm.whereIsWaldo(196, waldoFile, "ENTER, !pendingCellContents.isEmpty()");
                                setFromGridToStruct("From not Empty Enter", pendingCellContents);
                                nCasesInStruct = positionTracker.getNCasesInStruct();
                                goDownOneRow();
                            }
                        }   //  end Enter and are NOT Tabbing
                        else {  //  start Enter and ARE Tabbing
                            if (positionTracker.cursorIsAtBottomOfGrid()) {
                                dm.whereIsWaldo(204, waldoFile, "ENTER, tracker.cursorIsAtBottomOfGrid()");
                                dm.addNCasesToStruct(1);
                                positionTracker.setFirstCaseIdentifier(positionTracker.getFirstCaseIdentifier() + 1);
                                positionTracker.setFirstVarIdentifier(tabRectLeftFirstVar);
                                dgCol = tabRectLeftDelta;
                                dm.sendDataStructToGrid(dgCol, dgRow);
                                positionTracker.set_Current_DG_DS(dgCol, dgRow, "210 Data_Grid"); 
                            } else {
                                dm.whereIsWaldo(212, waldoFile, "ENTER, !tracker.cursorIsAtBottomOfGrid()");
                                tabRectRightIndex = positionTracker.getFirstVarIdentifier() + positionTracker.getCurrentDGCol();
                                dm.sendDataStructToGrid(dgCol, dgRow);                            
                                dgRow++;
                                positionTracker.setFirstVarIdentifier(tabRectLeftFirstVar);
                                dgCol = tabRectLeftDelta;                                 
                                tabRectCurrentIndex = positionTracker.getFirstVarIdentifier() + dgCol;
                                dm.sendDataStructToGrid(dgCol, dgRow);
                                prepareCellForAction(dgCol, dgRow);  
                            }
                        }   //  w/ tabbing
                        e.consume();
                        break;  // End case ENTER       
                    case "DOWN": 
                        weAreTabbing = false;  goDownOneRow(); e.consume();
                        break;  //  End cse DOWN
                        
                    case "UP": 
                        weAreTabbing = false; goUpOneRow(); e.consume();
                        break;  //  End case UP
                        
                    case "LEFT": 
                        weAreTabbing = false; goLeftOneCol(); e.consume();
                        break;  //End case LEFT
                        
                    case "RIGHT":
                        weAreTabbing = false; goRightOneCol(); e.consume();
                        break;  //`End case RIGHT
                        
                    case "TAB": 
                        dm.whereIsWaldo(242, waldoFile, "Case TAB");
                        tabOrEnter = "TAB";
                        //weAreTabbing = true;  We may or may not already be tabbing
                        doDataCommit();
                        
                        if (getGridCellContents(dgCol, dgRow).isEmpty() || getGridCellContents(dgCol, dgRow).equals("*") ) {
                            replaceEmpty();
                        }
                        
                        if (!weAreTabbing) {    //  Are NOT tabbing
                            dm.whereIsWaldo(252, waldoFile, "!weAreTabbing");
                            weAreTabbing = true;
                            tabRectLeftDelta = dgCol;
                            tabRectLeftFirstVar = positionTracker.getFirstVarIdentifier();
                            tabRectLeftIndex = tabRectLeftFirstVar + tabRectLeftDelta;
                            tabRectRightIndex = -1;   // Initialize right tab
                            // Append that last character before the ENTER
                            // If Cell contents are empty, just restore the DataGrid
                            if (pendingCellContents.isEmpty()) {
                                dm.whereIsWaldo(261, waldoFile, "pendingCellContents.isEmpty()");
                                if (getGridCellContents(dgCol, dgRow).isEmpty() || getGridCellContents(dgCol, dgRow).equals("*")) {
                                    replaceEmpty();
                                }
                                dm.sendDataStructToGrid(dgCol, dgRow);
                                goRightOneCol(); 
                            } 
                            else {  //  Cell NOT empty
                                dm.whereIsWaldo(269, waldoFile, "!pendingCellContents.isEmpty()");
                                
                                if (tabRectRightIndex == -1) {  //  NOT at end of tab-rect
                                    goRightOneCol();
                                }
                                else {  //  At end of tab-rect
                                    dm.whereIsWaldo(275, waldoFile, "At end of tab-rect");
                                    setFromGridToStruct("         276 dg, case TAB, not tabbing", pendingCellContents);
                                    dgRow++;
                                    positionTracker.set_Current_DG_DS(dgCol, dgRow, "278 Data_Grid");
                                    System.out.println("279 dg, send dataStructToGrid");
                                    dm.sendDataStructToGrid(dgCol, dgRow);
                                    prepareCellForAction(dgCol, dgRow);
                                }
                            } //  end else cell NOT empty
                        }   //  End NOT tabbing
                        else { //  Start Rectangle are already tabbing
                            dm.whereIsWaldo(286, waldoFile, "weAreTabbing");
                                
                                if (getGridCellContents(dgCol, dgRow).isEmpty() || getGridCellContents(dgCol, dgRow).equals("*")) {
                                    replaceEmpty();
                                }

                            tabRectCurrentIndex = positionTracker.getCurrentStructColumn(); // dsCol
                            
                            if (tabRectCurrentIndex  == tabRectRightIndex ) { //  At end of tab-rect
                                dm.whereIsWaldo(295, waldoFile, "tabRectCurrentIndex  == tabRectRightIndex");
                                setFromGridToStruct("         296 dg, case TAB, not tabbing", pendingCellContents);
                                positionTracker.setFirstVarIdentifier(tabRectLeftFirstVar);
                                dm.sendDataStructToGrid(dgCol, dgRow);
                                
                                if (positionTracker.cursorIsAtBottomOfGrid()) {
                                    dm.whereIsWaldo(301, waldoFile, "tracker.cursorIsAtBottomOfGrid()");
                                    dm.addNCasesToStruct(1);
                                    positionTracker.setFirstCaseIdentifier(positionTracker.getFirstCaseIdentifier() + 1);
                                    dgCol = tabRectLeftDelta;
                                    dm.sendDataStructToGrid(dgCol, dgRow);
                                    positionTracker.set_Current_DG_DS(dgCol, dgRow, "306 Data_Grid");
                                }
                                else {
                                    dm.whereIsWaldo(309, waldoFile, "!tracker.cursorIsAtBottomOfGrid()");
                                    dgCol = tabRectLeftDelta;
                                    dgRow++;
                                    positionTracker.set_Current_DG_DS(dgCol, dgRow, "312 Data_Grid");
                                }

                                positionTracker.set_Current_DG_DS(dgCol, dgRow, "315 Data_Grid");
                                prepareCellForAction(dgCol, dgRow);
                            }
                            else {
                                dm.whereIsWaldo(319, waldoFile, "!tabRectCurrentIndex  == tabRectRightIndex");
                                
                                if (pendingCellContents.isEmpty()) {
                                    dm.whereIsWaldo(322, waldoFile, "pendingCellContents.isEmpty()");
                                    pendingCellContents = "*";
                                }
                                
                                setFromGridToStruct("          326 dg, dgCol  NOT = dgRightTabRectangle", pendingCellContents);
                                dm.sendDataStructToGrid(dgCol, dgRow);
                                goRightOneCol();
                                positionTracker.set_Current_DG_DS(dgCol, dgRow, "329 Data_Grid");
                                prepareCellForAction(dgCol, dgRow);
                            }
                        }   //  End are tabbling
                        
                        e.consume();
                        break;  // End case 

                    case "PAGE_DOWN": weAreTabbing = false; goDownOnePage(); break;
                    case "PAGE_UP": weAreTabbing = false; goUpOnePage(); break;
                    case "HOME": weAreTabbing = false; goHome(); break;
                    case "END": weAreTabbing = false; goToEnd(); break;                    
                    case "BACK_SPACE":
                    case "DELETE":
                        setGridCellContents(dgCol, dgRow, "");
                        break;
                    default:
                        pendingCellContents = theGridCells.get(dgCol).get(dgRow).getText();
                        e.consume();                        
                }
            }
        }; 
  
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, pressFilter);
        gridPane.addEventFilter(KeyEvent.KEY_RELEASED, pressFilter);
        gridPane.addEventFilter(KeyEvent.KEY_TYPED, pressFilter);
        
        // ***** ***** Lay out the grid ***** *****        
        for (int thisVar = 0; thisVar < maxVisualVarsInGrid; thisVar++) {
            ArrayList<TextField> newAL = new ArrayList<>();            
            for (int thisCase = 0; thisCase < maxVisualCasesInGrid; thisCase++) {
                TextField tf = new TextField("");
                tf.setPrefColumnCount(15);
                tf.getStyleClass().add("cells");
                tf.setEditable(true);
                tf.setMinWidth(pxCellWidth);
                tf.setMaxWidth(pxCellWidth);
                tf.setOnMousePressed(mouseHandler);
                tf.setPrefColumnCount(20);  // ********************************
                tf.setAlignment(CENTER_LEFT);  // ********************************
                newAL.add(tf);
            }
            theGridCells.add(newAL);
        } 
        
        for (int thisVar = 0; thisVar < maxVisualVarsInGrid; thisVar++) {            
            for (int thisCase = 0; thisCase < maxVisualCasesInGrid; thisCase++) {
                gridPane.add(theGridCells.get(thisVar).get(thisCase), thisVar, thisCase);
            }
        }  
    } 
    
    public void complete_DG_Initialization() {
        dm.whereIsWaldo(380, waldoFile, "complete_DG_Initialization()");
        positionTracker.set_Current_DG_DS_Contents(0, 0, "", "381 Data_Grid");
        weAreTabbing = false;    
    }
    
    public void adjustGridHeightAndWidth(int newMaxCasesInGrid, int newMaxVarsInGrid) {
        dm.whereIsWaldo(386, waldoFile, "adjustGridHeightAndWidth(int newMaxCasesInGrid, int newMaxVarsInGrid)");
        // ***** ***** Lay out the grid ***** *****
        for (int thisVar = 0; thisVar < newMaxVarsInGrid; thisVar++) {            
            if (thisVar > maxVisualVarsInGrid - 1) { // append new column if we've gone beyond the former boundary
                ArrayList<TextField> newAL = new ArrayList<>();                
                for (int thisCase = 0; thisCase < maxVisualCasesInGrid; thisCase++) { // use the old value here.  Will add on new rows all at once later.
                    TextField tf = new TextField("");
                    tf.getStyleClass().add("cells");
                    tf.setEditable(true);
                    tf.setMinWidth(pxCellWidth);
                    tf.setMaxWidth(pxCellWidth);
                    tf.setOnMousePressed(mouseHandler);

                    newAL.add(tf);                  
                }
                theGridCells.add(thisVar,newAL);
                
                for (int thisCase = 0; thisCase < maxVisualCasesInGrid; thisCase++) {
                    try {
                        gridPane.add(theGridCells.get(thisVar).get(thisCase), thisVar, thisCase);
                    } catch (Exception ex)
                    {
                        PrintExceptionInfo pei = new PrintExceptionInfo(ex, "Exception in DataGrid, line 408");                    
                    }                      
                }
            } else if (thisVar < maxVisualVarsInGrid)
                for (int localVar = maxVisualVarsInGrid - 1; localVar >= newMaxVarsInGrid; localVar--) {
                    for (int localRow = 0; localRow < maxVisualCasesInGrid; localRow++)           
                        gridPane.getChildren().remove(theGridCells.get(localVar).get(localRow));
                }               
            if (newMaxCasesInGrid > maxVisualCasesInGrid) {
                for (int thisCase = maxVisualCasesInGrid; thisCase < newMaxCasesInGrid; thisCase++) {
                
                    TextField tf = new TextField("");
                    tf.getStyleClass().add("-fx-text-alignment: center;");
                    tf.setEditable(true);
                    tf.getStyleClass().add("cells");
                    tf.setMinWidth(pxCellWidth);
                    tf.setMaxWidth(pxCellWidth);
                    tf.setOnMousePressed(mouseHandler);

                    ArrayList<TextField> al = theGridCells.get(thisVar);
                    al.add(tf);
                    gridPane.add(theGridCells.get(thisVar).get(thisCase), thisVar, thisCase);
                }                             
            } else if (newMaxCasesInGrid < maxVisualCasesInGrid)              
                for (int thisCase = maxVisualCasesInGrid-1; thisCase >= newMaxCasesInGrid; thisCase--)  {
                    gridPane.getChildren().remove(theGridCells.get(thisVar).get(thisCase));  
                }
        }

        maxVisualCasesInGrid = newMaxCasesInGrid;
        maxVisualVarsInGrid = newMaxVarsInGrid;
    }
    
    public void setPosTracker(PositionTracker tracker) { this.positionTracker = tracker; }
     
    private void doDataCommit() {  
        dm.whereIsWaldo(444, waldoFile, "doDataCommit()");
        
        if (dm.getHasBeenScrolled()) {
            //System.out.println("\n447 dg, START doDataCommit()");
        }
        
        dm.setDataAreClean(false);
        positionTracker.set_Current_DG_DS(dgCol, dgRow, "451 Data_Grid");
        pendingCellContents = theGridCells.get(dgCol).get(dgRow).getText();
        // Parse the pendingCellContents for any asterisks. 
        handleAnyAsterisk();
        
        if (weAreTabbing) {
            dataCommit_WhileTabbing.handleTheTabbingCommit (pendingCellContents);
        }
        else {
            dataCommit_NotTabbing.handleTheNotTabbingCommit (pendingCellContents); 
        }
        
        if (weAreTabbing) {
            fixedString = dataCommit_WhileTabbing.handleCommit_GetTheFinalString();
        }
        else {
            fixedString = dataCommit_NotTabbing.handleCommit_GetTheFinalString();
        }
        
        if (positionTracker.getNVarsInStruct() > positionTracker.getNVarsCommitted()) {
            positionTracker.setNVarsCommitted(positionTracker.getNVarsInStruct());
        }

        if (positionTracker.getNCasesInStruct() > positionTracker.getNCasesCommitted()) {
            positionTracker.setNCasesCommitted(positionTracker.getNCasesInStruct());
        }
        
        if (dm.getHasBeenScrolled()) {
            //System.out.println("\n479 dg, END doDataCommit()");
        } 
        
        //System.out.println("482 DataGrid, End DataCommit");
    } // End doDataCommit() {   

    public void goUpOneRow() { 
        dm.whereIsWaldo(486, waldoFile, "goUpOneRow()");
        if (positionTracker.cursorIsAtFirstCase()) { return; }
        if (dm.getHasBeenScrolled()) {
            //System.out.println("\n489 dg, START goUpOneRow()");
        }
        
        setGridCellContents(dgCol, dgRow, fromStorage);
        
        if (!positionTracker.cursorIsAtFirstCase() && !positionTracker.cursorIsAtTopOfGrid()) {
            positionTracker.set_Current_DG_DS(dgCol, dgRow - 1, "495 Data_Grid");
            dgRow--;
            positionTracker.set_Current_DG_DS(dgCol, dgRow, "497 Data_Grid");
            dm.sendDataStructToGrid(dgCol, dgRow);
            prepareCellForAction(dgCol, dgRow);
        }
        else {
            if(!positionTracker.cursorIsAtFirstCase() && positionTracker.cursorIsAtTopOfGrid()) {               
                positionTracker.set_CurrentDS(positionTracker.get_CurrentDS().getCol(), positionTracker.get_CurrentDS().getRow() - 1);
                positionTracker.setFirstCaseIdentifier(positionTracker.getFirstCaseIdentifier() - 1);
                dm.sendDataStructToGrid(dgCol, dgRow);
                positionTracker.set_Current_DG_DS(dgCol, dgRow, "506 Data_Grid");
            }
        }
        //if (dm.getHasBeenScrolled()) {
        //}
    } 
    
    public void goDownOneRow() {
        dm.whereIsWaldo(514, waldoFile, "Data_Grid, START goDownOneRow()");
        //positionTracker.printCurrentGridInformation("Data_Grid: CurrentGrid at start of goDownOneRow()");

        if (dm.getHasBeenScrolled()) {
            //System.out.println("---517 dg, START goDownOneRow(), SCROLLED nCasesInStruct = " + nCasesInStruct);
        }
        if (dgRow >= nCasesInStruct) { 
            //System.out.println("---520 dg, START goDownOneRow(), SCROLLED, dgRow >= nCasesInStruct trapped");
            return; }
        //if (dgRow >= 14) { 
        if (dgRow >= dm.getMaxVisCases()) { 
            //System.out.println("---523 dg, START goDownOneRow(), SCROLLED, dgRow >= 13 trapped");
            return; }
        if (!positionTracker.cursorIsAtBottomOfGrid()) {  //  Cursor not at bottom of grid
            //System.out.println("526 Data_Grid, Not at bottom, dgRow = " + dgRow);
            dgRow++;
            positionTracker.set_Current_DG_DS(dgCol, dgRow, "530 Data_Grid");
            dm.sendDataStructToGrid(dgCol, dgRow);
            prepareCellForAction(dgCol, dgRow);
        }
        else {  //  Cursor at bottom of grid
            //System.out.println("533 Data_Grid, At bottom, dgRow = " + dgRow);
            positionTracker.setFirstCaseIdentifier(positionTracker.getFirstCaseIdentifier() + 1);
            positionTracker.set_Current_DG_DS(dgCol, dgRow, "537 Data_Grid");
            dm.sendDataStructToGrid(dgCol, dgRow);
            prepareCellForAction(dgCol, dgRow);
        } 
        
        if (dm.getHasBeenScrolled()) {
            //System.out.println("---541 dg, END SCROLLED goDownOneRow()");
        }
        //positionTracker.printCurrentGridInformation("Data_Grid: CurrentGrid at end of goDownOneRow()");
    }

    public void goLeftOneCol() {
        dm.whereIsWaldo(548, waldoFile, "goLeftOneCol()");
        //if (dgCol <= 0) { return; }
        if (dm.getHasBeenScrolled()) {
            //System.out.println("\n551 dg, START goLeftOneCol()");
        }
        
        if (!positionTracker.cursorIsAtFirstVariable() && !positionTracker.cursorIsAtLeftOfGrid()) {
            positionTracker.set_Current_DG_DS(dgCol - 1, dgRow, "556 Data_Grid");
            dgCol--;
            positionTracker.set_Current_DG_DS(dgCol, dgRow, "558 Data_Grid");
            dm.sendDataStructToGrid(dgCol, dgRow);
            prepareCellForAction(dgCol, dgRow);
        }
        else {
            if(!positionTracker.cursorIsAtFirstVariable() && positionTracker.cursorIsAtLeftOfGrid()) { 
                positionTracker.set_CurrentDS(positionTracker.get_CurrentDS().getCol() - 1, positionTracker.get_CurrentDS().getRow());
                positionTracker.setFirstVarIdentifier(positionTracker.getFirstVarIdentifier() - 1);
                dm.sendDataStructToGrid(dgCol, dgRow);
                positionTracker.set_Current_DG_DS(dgCol, dgRow, "567 Data_Grid");
            }
        }
        
        //if (dm.getHasBeenScrolled()) {
           // System.out.println("\n597 dg, END goLeftOneCol()");
        //}
    } // leftOne

    private void goRightOneCol() {
        dm.whereIsWaldo(576, waldoFile, "goRightOneCol()");
        //if (dgCol >= nVarsInStruct - 1) { return; }
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n605 dg, START goRightOneCol()");
        //}
        
        if (positionTracker.cursorIsAtLastVariable()) {
            dm.addToStructOneColumnWithNoData();    
        }        
        
        if (positionTracker.cursorIsAtRightOfGrid()) {
            positionTracker.setFirstVarIdentifier(positionTracker.getFirstVarIdentifier() + 1);
            dm.sendDataStructToGrid(dgCol, dgRow);
            positionTracker.set_Current_DG_DS(dgCol, dgRow, "590 Data_Grid");
            prepareCellForAction(dgCol, dgRow);        
        } else {
            dgCol++;
            positionTracker.set_Current_DG_DS(dgCol, dgRow, "594 Data_Grid");
            dm.sendDataStructToGrid(dgCol, dgRow);
            prepareCellForAction(dgCol, dgRow);                
        }
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n627 dg, END goRightOneCol()");
        //}
    } 
    
    private void goUpOnePage() {
        dm.whereIsWaldo(604, waldoFile, "goUpOnePage()");
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n635 dg, START goUpOnePage()");
        //}
        
        if (positionTracker.getFirstCaseIdentifier() >= maxVisualCasesInGrid) { 
            positionTracker.setFirstCaseIdentifier(positionTracker.getFirstCaseIdentifier()- maxVisualCasesInGrid);
            positionTracker.set_CurrentDS(positionTracker.get_CurrentDS().getCol(), positionTracker.get_CurrentDS().getRow() - maxVisualCasesInGrid);
            dm.sendDataStructToGrid(dgCol, dgRow);
        }
        else {
            goHome();
        }
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n611 dg, END goUpOnePage()");
        //}
    } 
    
    private void goDownOnePage() { 
        dm.whereIsWaldo(625, waldoFile, "goDownOnePage()");
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n628 dg, START goDownOnePage()");
        //}
        
        if (positionTracker.getLastCaseInGrid() < nCasesInStruct) { 
            positionTracker.setFirstCaseIdentifier(positionTracker.getFirstCaseIdentifier()+ maxVisualCasesInGrid);
            positionTracker.set_CurrentDS(positionTracker.get_CurrentDS().getCol(), positionTracker.get_CurrentDS().getRow() + maxVisualCasesInGrid);
            dm.sendDataStructToGrid(dgCol, dgRow);
        }
        else {
            goToEnd();
        }
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n632 dg, END goDownOnePage()");
        //}
    } // downOnePage

    public void goHome() {
        dm.whereIsWaldo(646, waldoFile, "goHome()");
        //positionTracker.printCurrentGridInformation("Data_Grid: CurrentGrid at start of goHome");
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n640 dg, START goHome()");
        //}
        dgCol = 0;
        dgRow = 0;
        positionTracker.set_ulDG(0, 0);
        positionTracker.set_ulDS(0, 0);
        positionTracker.set_Current_DG_DS(0, 0, "654 Data_Grid");
        dm.sendDataStructToGrid(0, 0);
        resetBlueCellPosition(0, 0);
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n649 dg, END goHome()");
        //}  
        //positionTracker.printCurrentGridInformation("Data_Grid: CurrentGrid at end of goHome");
    } 

    private void goToEnd() {
        dm.whereIsWaldo(665, waldoFile, "goToEnd()");
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n657 dg, START goToEnd()");
        //}
        
        nVarsInStruct = positionTracker.getNVarsInStruct();
        nCasesInStruct = positionTracker.getNCasesInStruct();
        maxVisualVarsInGrid = positionTracker.getMaxVarsInGrid();
        maxVisualCasesInGrid = positionTracker.getMaxCasesInGrid();
        
        if (nVarsInStruct <= maxVisualVarsInGrid) {
            firstDG_Var = 0; 
            lastDG_Var = nVarsInStruct - 1;
            firstDS_Var = 0;
        }
        else {
            firstDG_Var = nVarsInStruct - maxVisualVarsInGrid; 
            lastDG_Var = maxVisualVarsInGrid - 1;
            firstDS_Var = nVarsInStruct - maxVisualVarsInGrid;         
        }
        
        if (nCasesInStruct <= maxVisualCasesInGrid) {
            firstDG_Case = 0; 
            lastDG_Case = nCasesInStruct - 1;
            firstDS_Case = 0;
        }
        else {
            firstDG_Case = nCasesInStruct - maxVisualCasesInGrid;
            lastDG_Case = maxVisualCasesInGrid - 1;
            firstDS_Case = nCasesInStruct - maxVisualCasesInGrid;        
        }        

        positionTracker.set_ulDG(firstDG_Var, firstDS_Case);
        positionTracker.set_ulDS(firstDS_Var, firstDS_Case);
        positionTracker.set_CurrentDS(firstDS_Var, firstDS_Case);
        positionTracker.setFirstCaseIdentifier(firstDS_Case);
        positionTracker.setFirstVarIdentifier(firstDS_Var);
        dm.sendDataStructToGrid(dgCol, dgRow);
        positionTracker.set_Current_DG_DS(lastDG_Var, lastDG_Case, "704 Data_Grid");
        dgCol = lastDG_Var;
        dgRow = lastDG_Case;
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n696 dg, END goToEnd()");
        //}
    } // goEnd
     
    // ***********************************************************************
    // *            Methods to cut down on repetitive code                   *
    // **********************************************************************/
    // ***********************************************************************
    // *  The if () { reset} is b/c variables created during ANOVA or from a *
    //  *  data transformation are added, possibly off the visual grid.      * 
    // **********************************************************************/
    public void resetBlueCellPosition(int toThisCol, int toThisRow) {
        //dm.whereIsWaldo(721, waldoFile, "Data_Grid(Data_Manager dm, resetBlueCellPosition to Col/Rowl)  " + toThisCol + " / " + toThisRow);    
        //System.out.println("722 Data_Grid(Data_Manager dm, resetBlueCellPosition to Col/Rowl)  " + toThisCol + " / " + toThisRow);
        /*********************************************************************
         *   Prevent accessing a column off the grid.  For reasons unknown,  *
         *   a row off the grid does not bother this method. This was found  *
         *   out when calculating a linear combination of variables.         *
         ********************************************************************/
        if (toThisCol > maxVisualVarsInGrid - 1) { return; }
        
        if (dm.getHasBeenScrolled()) {
            //System.out.println("\n731 dg, START SCROLLED resetBlueCellPosition, col/row = " + toThisCol + " / " + toThisRow);
        }
        
        if (toThisRow > maxVisualCasesInGrid - 1) {   // Generates a statck trace for some reason
            return;  
        }
        
        if (!positionTracker.getSneakingInANewColumn()) {
            //System.out.println("739 DataGrid, toThisRow = " + toThisRow);
            makeEmAllWhite();
            theGridCells.get(toThisCol).get(toThisRow).requestFocus();
            theGridCells.get(toThisCol).get(toThisRow).setStyle("-fx-background-color: lightblue;"); 
        }
        
        //if (dm.getHasBeenScrolled()) {
            //System.out.println("\n746 dg, END resetBlueCellPosition");
        //}
        
        // System.out.println("749 Data_Grid, toThisCol / toThisRow = " + toThisCol + " / " + toThisRow);
        currentCellContents = theGridCells.get(toThisCol).get(toThisRow).getText();
    } 
    
    private void makeEmAllWhite() {
        for (int col = 0 ; col < maxVisualVarsInGrid; col++) {            
            for (int row = 0; row < maxVisualCasesInGrid; row++) {
                theGridCells.get(col).get(row).setStyle("-fx-background-color: white;");     
            }
        }
    }
    
    private void replaceEmpty() {    //  with *
        dm.whereIsWaldo(762, waldoFile, "Data_Grid replaceEmpty()");
        setFromGridToStruct("FromEmptyEnter", "*");
        dm.sendDataStructToGrid(dgCol, dgRow);
    }
    
    /*********************************************************************
     *   Asterisks may be alone (missing data) or occur when a formerly  *
     *   missing data cell is edited. If not alone, strip the asterisk   *
     *   from pendingCellContents. If alone, the data is 'missing.'      *
     ********************************************************************/
    private void handleAnyAsterisk() {
        dm.whereIsWaldo(773, waldoFile, "handleAnyAsterisk()");

        if ((pendingCellContents.contains("*") && (!pendingCellContents.equals("*")))) {
            StringBuffer buffy = new StringBuffer();   
            for (int ithChar = 0; ithChar < pendingCellContents.length(); ithChar++) {
                char temp = pendingCellContents.charAt(ithChar);                
                if (temp != '*') {
                    buffy.append(temp);
                } 
            }
            pendingCellContents = new String(buffy);
        }
        // User could change existing number to "missing" = '*'.  If so, 
        // leave pendingCellContents as is.
    } 
    
    /**********************************************************************
    *                    prepareCellForAction()                           *
    *   Set both the contents of the visual cell and the string to hold   *
    *   the anticipated input to blank. Upon ENTER, the new cell contents *
    *   will be sent to the Data Struct and the DataGrid refreshed with   *
    *   a call to sendCellInfoToStruct().                                 *
    **********************************************************************/
 
    private void prepareCellForAction(int thisCol, int thisRow) {  
        positionTracker.set_Current_DG_DS(thisCol, thisRow, "796 Data_Grid");
        pendingCellContents = "";
    }

    public String getGridCellContents(int col, int row) { 
        return theGridCells.get(col).get(row).getText(); 
    }

    public void setGridCellContents(int col, int row, String toTheseContents) { 
        //System.out.println("795 setGridCellContents, col/row/ToThis = " + col + " / " + row + " / " + toThis);
        theGridCells.get(col).get(row).setText(toTheseContents);
        positionTracker.set_Current_DG_DS_Contents(col, row, toTheseContents, "807 Data_Grid");
    }
    
    public int get_dgRow() { return dgRow; }

    //  Called only by EnterHandler
    public void set_dgRow(int toThis) { 
        dgRow = toThis;
        positionTracker.set_Current_DG_DS(dgCol, dgRow, "815 Data_Grid");
    }
    
    public int get_dgCol() { return dgCol; }

    //  Called only by EnterHandler
    public void set_dgCol(int toThis) { 
        dgCol = toThis;
        positionTracker.set_Current_DG_DS(dgCol, dgRow, "823 Data_Grid");
    }
    
    private void setFromGridToStruct(String message, String toThisValue) {
        dm.whereIsWaldo(829, waldoFile, "setFromGridToStruct(String message, String toThisValue)");
        positionTracker.set_Current_DG_DS(dgCol, dgRow, "828 Data_Grid");
        int structCol = positionTracker.getCurrentStructColumn();
        int structRow = positionTracker.getCurrentStructRow();
        dm.setDataInStruct(message, structCol, structRow, toThisValue);
    }
    
    public GridPane getGridPane() {return gridPane; }
    
    public TextField getGridCellTextField(int col, int row) { 
        return theGridCells.get(col).get(row);
    }
    
    public boolean getComingFromLeftRightClick() {
        return comingFromLeftRightClick;
    }
    
    public void setComingFromLeftRightClick( boolean toThis) {
        comingFromLeftRightClick = toThis;
    }
    
    public void setNCasesInStruct(int toThis) {
        nCasesInStruct = toThis; 
    }
    
    public void setNVarsInStruct(int toThis) {
        nVarsInStruct = toThis; 
    }

    public int getNCasesInGrid() { return nCasesInGrid; }
    public int getNVarsInGrid() { return nVarsInGrid; }   
    public void setNCasesInGrid(int toThis) { nCasesInGrid = toThis; }
    public void setNVarsInGrid(int toThis) { nVarsInGrid = toThis; }    
    public boolean getFirstEntryDone() { return firstEntryDone; }
    public void setFirstEntryDone(boolean toThis) { firstEntryDone = toThis; }   
    public String getTabOrEnter() { return tabOrEnter; }   
    public int getTabRectFirstVar() {return tabRectLeftFirstVar; }
    public int getTabRectLeftIndex() { return tabRectLeftIndex; }
    public int getTabRectRightIndex() { return tabRectRightIndex; } 
    public int getTabRectLeftDelta() {return tabRectLeftDelta; }
    public int getTabRectCurrentIndex() {return tabRectCurrentIndex; }
    public int getRightWindowIndex() { return lastWindowColumn; }

    // This is currently used only at initialization after a file read
    public void setCurrentCellContents( String ccs)  { 
        currentCellContents = ccs; 
    }
}
