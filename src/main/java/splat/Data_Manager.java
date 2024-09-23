/************************************************************
 *                        Data_Manager                      *
 *                          09/21/24                        *
 *                            18:00                         *
 ***********************************************************/
package splat;

import dataObjects.CategoricalDataVariable;
import dataObjects.QuantitativeDataVariable;
import dataObjects.ColumnOfData;
import java.io.File;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public final class Data_Manager {

    boolean dataExists, dataIsClean, goodToGo, scrollEventInitiated;

    int colHeadSelected, nCasesInGrid, nCasesInStruct, nVarsInStruct,
        pxCellWidth, nCharsInLabel;
    private int maxCasesInGrid, maxVarsInGrid;   // Set the initial size of the grid displayed

    final int SIX = 6;
    private File fileName = null;
    private File lastPath = new File(System.getProperty("user.dir") + File.separator);

    public String currentVersion;
    public final String newMissingData = "*";
    
    //String waldoFile = "Data_Manager";
    String waldoFile = "";

    private char delimiter = ','; // default: csv files

    // My classes
    Data_Grid dataGrid;
    Data_Manager dm;
    ArrayList<ColumnOfData> dataStruct;
    MainMenu mainMenu;
    PositionTracker positionTracker;
    ObservableList<String> varNames;

    // POJOs / FX
    private final BorderPane mainPane;  // this is sent to the main program
    public ArrayList<TextField> colHeader, rowHeader;
    
    private final TextField corner;

    // Data_Manager is called from splat at launch
    public Data_Manager(int nCasesInVisualGrid, int nVariablesInVisualGrid) {
        dm = this;

        whereIsWaldo(73, waldoFile, "Constructing");
        this.maxCasesInGrid = nCasesInVisualGrid;
        this.maxVarsInGrid = nVariablesInVisualGrid;
        currentVersion = "11/10/23";
        dataExists = false;
        positionTracker = new PositionTracker(this, this.maxVarsInGrid, this.maxCasesInGrid);
        positionTracker.set_ulDG(0, 0);
        positionTracker.setNVarsInStruct(0);
        positionTracker.setNCasesInStruct(0);
        pxCellWidth = 105;
        dataGrid = new Data_Grid(this, positionTracker);

        positionTracker.setTrackerDataGrid(dataGrid);
        dataGrid.complete_DG_Initialization();
        nCharsInLabel = 20;
            
        colHeader = new ArrayList<>(); rowHeader = new ArrayList<>();
        // Generate all the headings and cells for the data manager:
        corner = new TextField("OBSERV.");
        corner.getStyleClass().add("rowHeadings");
        corner.setMinWidth(50); corner.setMaxWidth(50);
        corner.setEditable(false);

        for (int thisVar = 0; thisVar < nVariablesInVisualGrid; thisVar++) {
            String tempVarName = "V#" + (thisVar + 1);
            TextField element = new TextField(tempVarName);
            element.setMinWidth(pxCellWidth);   // I changed these and that seems to 
            element.setMaxWidth(pxCellWidth);   // change the col header grid widths.
            element.setPrefColumnCount(10); //  Changing this to 15 or 20 does not seem to work
            element.getStyleClass().add("colHeadings");
            element.setEditable(false);
            colHeader.add(element);
            colHeader.get(thisVar).setOnMousePressed((MouseEvent me) -> {
                editColumnHeader();
                sendDataStructToGrid(dataGrid.get_dgCol(), dataGrid.get_dgRow());
            });
        }
        
        for (int caseIndex = 0; caseIndex < nCasesInVisualGrid; caseIndex++) {
            TextField element = new TextField(String.format("%4d", (caseIndex + 1)));
            element.getStyleClass().add("rowHeadings");
            element.setEditable(false);
            rowHeader.add(element);
        }

        // Lay out the data manager.  First, layout the top row of column heading cells ('OBS', Var 1, Var 2, ... Var N)
        HBox colHeadingCells = new HBox(0);
        colHeadingCells.getChildren().add(corner);
        
        for (int thisVar = 0; thisVar < this.maxVarsInGrid; thisVar++) {
            colHeadingCells.getChildren()
                           .add(colHeader.get(thisVar));
        }
        
        // Next, lay out the numeric row 'header cells' on the left-hand side of the area by adding them to a Vertical Box.
        VBox rowHeadingCells = new VBox(0);
        
        for (int caseIndex = 0; caseIndex < nCasesInVisualGrid; caseIndex++) {
            rowHeadingCells.getChildren()
                           .add(rowHeader.get(caseIndex));
        }

        rowHeadingCells.setMinWidth(50); rowHeadingCells.setMaxWidth(50);

        mainPane = new BorderPane();
        mainPane.setTop(colHeadingCells);
        mainPane.setLeft(rowHeadingCells);
        mainPane.setCenter(dataGrid.getGridPane());
        initializeGrid(nCasesInVisualGrid);  //  public to allow FileOps to clear
        dataGrid.setGridCellContents(0, 0, "");
    } // End constructor

    public void initializeGrid(int maxCasesInGrid) {
        whereIsWaldo(146, waldoFile, "initializeGrid(int maxCasesInGrid)");
        dataStruct = new ArrayList();
        positionTracker.setNVarsInStruct(0);
        positionTracker.setNCasesInStruct(0);
        dataIsClean = true;
        positionTracker.set_CurrentDG_and_DS(0, 0);

        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {
            colHeader.get(ithGridCol).setText("Var #" + (ithGridCol + 1));
        }

        for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) {
            rowHeader.get(jthGridRow).setText(String.format("%4d", jthGridRow + 1));
        }

        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {
            
            for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) {
                //dm.whereIsWaldo(164, waldoFile, "setGridCellContents: col / row / toThis = " + ithGridCol + " / " + jthGridRow + " / " + "Blank");
                dataGrid.setGridCellContents(ithGridCol, jthGridRow, "");
            }
            
        }

        for (int ithVar = 0; ithVar < maxVarsInGrid; ithVar++) {
            String tempString = "Var #" + (ithVar + 1);
            colHeader.add(new TextField(tempString));          
            ColumnOfData veryTemp  = new ColumnOfData(this, positionTracker.getNCasesInStruct(), tempString);
            veryTemp.setIsNumeric(true);
            dataStruct.add(veryTemp);
        }
        whereIsWaldo(177, waldoFile, "Sending dataStruct to Grid (0, 0)");
        sendDataStructToGrid(0, 0);
        fileName = null;
        delimiter = ',';
    } // end initialize grid

    public void resizeColumnHeaderCellsArray(int newMaxVarsInGrid) {
        whereIsWaldo(184, waldoFile, "resizeColumnHeaderCellsArray(int newMaxVarsInGrid)");
        int oldMaxVars = colHeader.size();

        if (newMaxVarsInGrid > oldMaxVars) {            
            for (int varIndex = oldMaxVars; varIndex < newMaxVarsInGrid; varIndex++) {
                TextField element = new TextField(String.format("Var #%d", + (varIndex + 1)));
                element.setMinWidth(pxCellWidth);
                element.setMaxWidth(pxCellWidth);
                element.getStyleClass().add("colHeadings");
                element.setEditable(false);
                colHeader.add(element);
                colHeader.get(varIndex).setOnMousePressed((MouseEvent me) -> {
                    editColumnHeader();
                    sendDataStructToGrid(dataGrid.get_dgCol(), dataGrid.get_dgRow());
                });
            }
            AttachColumnHeaders(newMaxVarsInGrid);
        } else if (newMaxVarsInGrid < oldMaxVars) {  
            
            for (int rowIndex = oldMaxVars - 1; rowIndex >= newMaxVarsInGrid; rowIndex--) {
                colHeader.remove(rowIndex);
            }  
            
            AttachColumnHeaders(newMaxVarsInGrid);
        }
    }
   
    private void AttachColumnHeaders(int newMaxVarsInGrid) {
        HBox colHeadingCells = new HBox(0);
        colHeadingCells.getChildren().add(corner);
        for (int thisVar = 0; thisVar < newMaxVarsInGrid; thisVar++) {
            colHeadingCells.getChildren()
                           .add(colHeader.get(thisVar));
        }        
        mainPane.setTop(colHeadingCells);
    }
    
    public void resizeRowHeaderCellsArray(int newMaxCasesInGrid) {
        int oldMaxCases = rowHeader.size();

        if (newMaxCasesInGrid > oldMaxCases) {            
            for (int caseIndex = oldMaxCases; caseIndex < newMaxCasesInGrid; caseIndex++) {
                TextField element = new TextField(String.format("%4d", (caseIndex + 1)));
                element.getStyleClass().add("rowHeadings");
                element.setEditable(false);
                rowHeader.add(element);
            }
            AttachRowHeaders(newMaxCasesInGrid);
        } else if (newMaxCasesInGrid < oldMaxCases) {            
            for (int caseIndex = oldMaxCases - 1; caseIndex >= newMaxCasesInGrid; caseIndex--) {
                rowHeader.remove(caseIndex);
            }            
            AttachRowHeaders(newMaxCasesInGrid);
        }
    }    

    private void AttachRowHeaders(int newMaxCasesInGrid) {  
        VBox rowHeadingCells = new VBox(0);        
        for (int caseIndex = 0; caseIndex < newMaxCasesInGrid; caseIndex++) {
            rowHeadingCells.getChildren()
                           .add(rowHeader.get(caseIndex));
        }       
        mainPane.setLeft(rowHeadingCells);
    }

    public void resizeGrid(int newMaxRowCount, int newMaxColCount) {   
        dataGrid.adjustGridHeightAndWidth(newMaxRowCount, newMaxColCount);
        positionTracker.setTrackerDataGrid(dataGrid);
        positionTracker.updateMaxCases(newMaxRowCount);
        positionTracker.updateMaxVars(newMaxColCount);
    }
    
    public void initalizeForFileRead(int numVariables, int nDataLines) {
        whereIsWaldo(257, waldoFile, "initalizeForFileRead(int numVariables, int nDataLines)");
        dataStruct = new ArrayList();
        positionTracker.setNVarsInStruct(numVariables);
        positionTracker.setNCasesInStruct(nDataLines);
        positionTracker.set_CurrentDG_and_DS(0, 0);
        positionTracker.setFirstCaseIdentifier(0);
        positionTracker.setFirstVarIdentifier(0);
        
        for (int ithInitVar = 0; ithInitVar < numVariables; ithInitVar++) {
            String tempString = "Var #" + (ithInitVar + 1);
            ColumnOfData veryTemp  = new ColumnOfData(this, positionTracker.getNCasesInStruct(), tempString);
            veryTemp.setIsNumeric(true);
            dataStruct.add(veryTemp);            
            TextField tempTF = new TextField(tempString);
            colHeader.add(tempTF); 
        }
        
        // Fill the rest with generic variable names
        if (positionTracker.getNVarsInStruct() <= maxVarsInGrid) {            
            for (int thisVar = positionTracker.getNVarsInStruct(); thisVar < maxVarsInGrid; thisVar++) {
                String tempString = "Var #" + (thisVar + 1);
                ColumnOfData veryTemp  = new ColumnOfData(this, positionTracker.getNCasesInStruct(), tempString);
                veryTemp.setIsNumeric(true);
                dataStruct.add(veryTemp); 
                TextField tempTF = new TextField(tempString);
                colHeader.add(tempTF);
            }
        }
        sendDataStructToGrid(0, 0);
        dataGrid.setFirstEntryDone(true);
    } 

    public BorderPane getMainPane() { return mainPane; }

    public void editColumnHeader() {
        whereIsWaldo(292, waldoFile, "editColumnHeader()");
        nVarsInStruct = positionTracker.getNVarsInStruct();
        RadioButton rbNumericData = new RadioButton("Numeric Data");
        RadioButton rbTextData = new RadioButton("Text Data");
        ToggleGroup tgButtons = new ToggleGroup();
        rbNumericData.setToggleGroup(tgButtons);
        rbTextData.setToggleGroup(tgButtons);
        
        for (int col = 0; col < maxVarsInGrid; col++) {           
            if (colHeader.get(col).isFocused()) {  //  colHeader is zero-based
                colHeadSelected = col;
                break;
            }
        }

        if (colHeadSelected >= nVarsInStruct) {
            int nVarsToAdd = colHeadSelected + 1 - nVarsInStruct;
            addToStructNColumnsWithNoData(nVarsToAdd);
        }
        
        if (getVariableIsNumeric(positionTracker.getFirstVarIdentifier() + colHeadSelected)) {
            rbNumericData.setSelected(true);
        } else { rbTextData.setSelected(true); }
        
        Label title = new Label("Edit Variable Information");
        title.getStyleClass()
             .add("dialogTitle");
        Label labelName = new Label("Variable Name: ");
        TextField tfTextName = new TextField();
        Button btnUpdate = new Button("Update Variable");
        Button btnRestoreDefault = new Button("Restore Default");
        Button btnClose = new Button("Close");
        tfTextName.setText(getVariableName(positionTracker.getFirstVarIdentifier() + colHeadSelected));

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);
        title.setPadding(new Insets(10, 0, 10, 0));
        Separator sep1 = new Separator();
        mainPanel.getChildren().addAll(title, sep1);

        GridPane centerPanel = new GridPane();
        centerPanel.setPadding(new Insets(10, 10, 10, 10));
        centerPanel.setHgap(0);
        centerPanel.setVgap(5);
        centerPanel.add(labelName, 0, 0);
        centerPanel.add(tfTextName, 1, 0);
        Separator sep2 = new Separator();
        mainPanel.getChildren()
                 .addAll(centerPanel, sep2);

        HBox hBx_RBPanel = new HBox(10);
        hBx_RBPanel.setAlignment(Pos.CENTER);
        hBx_RBPanel.setPadding(new Insets(10, 10, 10, 10));
        hBx_RBPanel.getChildren()
                   .addAll(rbNumericData, rbTextData);
        Separator sep3 = new Separator();
        mainPanel.getChildren()
                 .addAll(hBx_RBPanel, sep3);

        HBox hBx_ButtonPanel = new HBox(10);
        hBx_ButtonPanel.setAlignment(Pos.CENTER);
        hBx_ButtonPanel.setPadding(new Insets(10, 5, 10, 5));
        hBx_ButtonPanel.getChildren()
                       .addAll(btnUpdate, btnRestoreDefault, btnClose);
        mainPanel.getChildren()
                 .add(hBx_ButtonPanel);

        Scene changeScene = new Scene(mainPanel);
        String css = getClass().getClassLoader().getResource("DataManager.css").toExternalForm();
        changeScene.getStylesheets()
                   .add(css);
        Stage changeStage = new Stage();
        changeStage.setScene(changeScene);
        changeStage.show();

        btnClose.setOnAction((ActionEvent event) -> {
            changeStage.close();
        });

        btnRestoreDefault.setOnAction((ActionEvent event) -> {
            colHeader.get(colHeadSelected)
                     .setText("Var #" + (positionTracker.getFirstVarIdentifier() + colHeadSelected + 1));
            dataStruct.get(positionTracker.getFirstVarIdentifier() + colHeadSelected)
                      .setVarLabel(colHeader.get(colHeadSelected).getText());
            changeStage.close();
        });

        btnUpdate.setOnAction((ActionEvent event) -> {
            int colSelected = positionTracker.getFirstVarIdentifier() + colHeadSelected;
            String temp = tfTextName.getText();
            temp = StringUtilities.truncateString(temp, nCharsInLabel);
            colHeader.get(colHeadSelected).setText(temp);
            dataStruct.get(colSelected)
                      .setVarLabel(temp);
            changeStage.close();
        });

        //  Goes to here on Enter
        tfTextName.setOnAction((ActionEvent event) -> {
            boolean duplicateLabelEntered = false;
            String daNewName = tfTextName.getText();
            
            //  Check for duplicate labels
            for (int ithVarName = 0; ithVarName < nVarsInStruct; ithVarName++) {                
                if (ithVarName != colHeadSelected) {
                    String existingName = colHeader.get(ithVarName).getText();                    
                    if (existingName.equals(daNewName)) {
                        duplicateLabelEntered = true;
                        MyAlerts.showDuplicateLabelAttemptedAlert();
                    }
                }
            }
 
            if (!duplicateLabelEntered) {
                daNewName = StringUtilities.truncateString(daNewName, nCharsInLabel);
                int colSelected = positionTracker.getFirstVarIdentifier() + colHeadSelected;
                colHeader.get(colSelected).setText(daNewName);
                dataStruct.get(colSelected)
                          .setVarLabel(daNewName);
                changeStage.close();
            }
            
        });

        rbNumericData.setOnAction((ActionEvent event) -> {
            setVariableNumeric(positionTracker.getFirstVarIdentifier() + colHeadSelected, true);
        });
        
        rbTextData.setOnAction((ActionEvent event) -> {
            setVariableNumeric(positionTracker.getFirstVarIdentifier() + colHeadSelected, false);
        });
    }

    public void sendDataStructToGrid(int theDG_Col, int theDG_Row) {        
        whereIsWaldo(426, waldoFile, "sendDataStructToGrid(int theDG_Col, int theDG_Row)");
        //System.out.println("427, int theDG_Col, int theDG_Row = " + theDG_Col + " / " + theDG_Row);
        String tempText;
        int rowInDataStruct, columnInDataStruct, firstCaseId, firstVarId;
        ArrayList<String> casesInColumn;
        firstCaseId = positionTracker.getFirstCaseIdentifier(); // this is the number that appears at the beginning of the row    
        firstVarId = positionTracker.getFirstVarIdentifier(); 
        nCasesInStruct = positionTracker.getNCasesInStruct(); 
        nVarsInStruct = positionTracker.getNVarsInStruct(); 
        nCasesInGrid = Math.min(nCasesInStruct - firstCaseId, maxCasesInGrid);

        // Render the column headers.
        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {
            columnInDataStruct = ithGridCol + firstVarId;   
            if (columnInDataStruct < nVarsInStruct) {
                String tempString = dataStruct.get(columnInDataStruct)
                                              .getVarLabel();
                colHeader.get(ithGridCol).setText(tempString);
            } else {
                colHeader.get(ithGridCol).setText("Var #" + (columnInDataStruct + 1));
            }            
        }
        //System.out.println("448 Data_Manager.....");
        // Render the row headers.
        //System.out.println("450, maxCasesInGrid = " + maxCasesInGrid);
        for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) {
            rowInDataStruct = jthGridRow + firstCaseId;            
            if (rowInDataStruct < nCasesInGrid) {
                rowHeader.get(jthGridRow).setText(String.format("%4d", (rowInDataStruct + 1)));
            } else {
                rowHeader.get(jthGridRow).setText(String.format("%4d", (rowInDataStruct + 1)));
            }
            
        }    
        
        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {  
            columnInDataStruct = ithGridCol + firstVarId;            
            if (ithGridCol < nVarsInStruct) {
                //System.out.println("464 Data_Manager.....");
                if (0 < nCasesInStruct) {
                    casesInColumn = new ArrayList(dataStruct.get(columnInDataStruct).getTheFormattedCases());
                } else {
                    casesInColumn = new ArrayList(dataStruct.get(columnInDataStruct).getTheCases_ArrayList());
                }
            } else {
                casesInColumn = new ArrayList();  
                for (int blankCases = 0; blankCases < positionTracker.getNCasesInStruct(); blankCases++) {
                    casesInColumn.add(" ");
                }
            }
            
            for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) { 
                rowInDataStruct = jthGridRow + firstCaseId;                
                if (rowInDataStruct < 0) { continue; }                
                // tempText only exists when there is data, NOT at initialization   
                dm.whereIsWaldo(482, waldoFile, "setGridCellContents: col / row = " + ithGridCol + " / " + jthGridRow);
                dm.whereIsWaldo(483, waldoFile, "rowInDataStruct / nCasesInStruct = " + rowInDataStruct + " / " + nCasesInStruct);
                if ((0 < nCasesInStruct) && (rowInDataStruct < nCasesInStruct)) {
                    tempText = casesInColumn.get(rowInDataStruct); 
                    dm.whereIsWaldo(486, waldoFile, "setGridCellContents: col / row / toThis = " + ithGridCol + " / " + jthGridRow + " / " + tempText);
                    dataGrid.setGridCellContents(ithGridCol, jthGridRow, tempText);
                } else {
                    dm.whereIsWaldo(489, waldoFile, "setGridCellContents: col / row / toThis = " + ithGridCol + " / " + jthGridRow + " / " + "Blank");
                    dataGrid.setGridCellContents(ithGridCol, jthGridRow, " ");
                }                
            }   // end row
            dm.whereIsWaldo(493, waldoFile, "setPosTracker: col / row / toThis = " + theDG_Col + " / " + theDG_Row);
            //positionTracker.set_CurrentDG_and_DS(theDG_Col, theDG_Row);
            positionTracker.set_CurrentDG_and_DS(theDG_Col, theDG_Row);
        }   // Send data struct to grid 
    }
    
    public int numDistinctVals(int groupingVar) {
        return dataStruct.get(groupingVar).getNumberOfDistinctValues();
    }

    public String getFromDataStruct(int col, int row) {
        String dataString = dataStruct.get(col)
                                      .getTheCases_ArrayList()
                                      .get(row);
        
        if (dataString.equals(" ")) { dataString = "";  }        
        return dataString;
    }
    
    //*************************************************************************
    // *   The input column and row are the _dataStruct_  values, not those    *
    // *   from the grid.  The structCol and struckRow are zero-based, thus    *    
    // *   the adding of 1 when comparing to the number of cases and vars.     *
    // ************************************************************************/
    public void setDataInStruct(String whenceHenceCometh, 
                                int forThisCol,
                                int forThisRow,
                                String toThis) {
        whereIsWaldo(521, waldoFile, "setDataInStruct");
        int structCol, structRow, gridCol, gridRow;
        structCol = forThisCol;
        structRow = forThisRow;
        gridRow = positionTracker.get_CurrentDG().getRow();
        gridCol = positionTracker.get_CurrentDG().getCol();

        nVarsInStruct = positionTracker.getNVarsInStruct();
        nCasesInStruct = positionTracker.getNCasesInStruct();

        if (structCol + 1 >= nVarsInStruct) {
            //System.out.println("532 Data_Manager.....");
            int nVariablesToAdd = structCol + 1 - nVarsInStruct;
            addToStructNColumnsWithNoData(nVariablesToAdd);
            nVarsInStruct = positionTracker.getNVarsInStruct(); // Just checking
        }

        if (structRow + 1 >= nCasesInStruct) {
            //System.out.println("539 Data_Manager.....");
            int nCasesToAdd = structRow + 1 - nCasesInStruct;
            addNCasesToStruct(nCasesToAdd);
            nCasesInStruct = positionTracker.getNCasesInStruct();
        }
        
        dataStruct.get(structCol)
                  .getTheCases_ArrayList()
                  .set(structRow, toThis);
    }

    /**************************************************************************
     *  Variables can be added by clicking in a 'new' column in the data      *
     *  grid, or they may be added by procedures such as regression, e.g.     *
     *  adding residuals.  ANOVA may also create new columns when unstacking  *
     *  a categorical variable.                                               * 
     *************************************************************************/
    
    public void addToStructNColumnsWithExistingData(ArrayList<QuantitativeDataVariable> allTheQDVs) {
        whereIsWaldo(558, waldoFile, "addToStructNColumnsWithExistingData");
        
        for (int ithStacked = 0; ithStacked < allTheQDVs.size(); ithStacked++) {
            addToStructOneColumnWithExistingQuantData(allTheQDVs.get(ithStacked));
        }
        
    }
    
    public void addToStructNColumnsWithNoData(int nCols) {
        //whereIsWaldo(567, waldoFile, "addToStructNColumnsWithNoData");
        
        for (int ithColToAdd = 0; ithColToAdd < nCols; ithColToAdd++) {
            addToStructOneColumnWithNoData();
        }
        
    }
    
    public void addToStructOneColumnWithNoData() {
        whereIsWaldo(576, waldoFile, "addToStructOneColumnWithNoData");
        int nVarsNow, nCases;
        nVarsNow = positionTracker.getNVarsInStruct();
        nCases = positionTracker.getNCasesInStruct();

        int columnIndex = nVarsNow;     //  The index of the new column
        int varNumber = nVarsNow + 1;   //  The variable number of the new variable
        
        if (varNumber <= SIX) {   //    Six were created at initialization
            nVarsNow++;
            positionTracker.setNVarsInStruct(nVarsNow);
        } else {
            ColumnOfData newCol = new ColumnOfData(this, nCases, "Var #" + varNumber);
            newCol.setIsNumeric(true);
            dataStruct.add(newCol);
            nVarsNow++;
            positionTracker.setNVarsInStruct(nVarsNow);
        }

        for (int ithCase = 0; ithCase < nCases; ithCase++) {  
            dataStruct.get(columnIndex)
                      .setStringInIthRow(ithCase, "*");
        }
        sendDataStructToGrid(positionTracker.getCurrentGridColumn(), positionTracker.getCurrentGridRow());
    }
    
    /************************************************************************
     *   addAColumn(QuantitativeDataVariable qdv) is adding a column of     *
     *   already defined and calculated values (e.g. in a qdv).             *
     *   addNVariables(int nVariablesToAdd) adds columns for future data    *
     *   entry.                                                             *
     ***********************************************************************/

     public void addToStructOneColumnWithExistingQuantData(QuantitativeDataVariable qdv) {
        whereIsWaldo(610, waldoFile, "addToStructOneColumnWithExistingQuantData");
        int nVarsNow, nCases;
        nVarsNow = positionTracker.getNVarsInStruct();
        nCases = positionTracker.getNCasesInStruct();

        int columnIndex = nVarsNow;     //  The index of the new column        
        int varNumber = nVarsNow + 1;   //  The variable number of the new variable

        if (varNumber <= SIX) {   //    Six were created at initialization
            nVarsNow++;
            positionTracker.setNVarsInStruct(nVarsNow);
            String newColLabel = qdv.getTheVarLabel();
            dataStruct.get(nVarsNow - 1).setVarLabel(newColLabel);
        } else {
            String newColLabel = qdv.getTheVarLabel();
            ColumnOfData newCol = new ColumnOfData(this, nCases, newColLabel);
            newCol.setIsNumeric(true);
            dataStruct.add(newCol);
            nVarsNow++;
            positionTracker.setNVarsInStruct(nVarsNow);
        }
        
        for (int ithCase = 0; ithCase < nCases; ithCase++) {   
            dataStruct.get(columnIndex)
                      .setStringInIthRow(ithCase, qdv.getIthDataPtAsString(ithCase));
        }
        sendDataStructToGrid(0, 0);
    } 
     
     public void addToStructOneColumnWithExistingCatData(CategoricalDataVariable cat_dv) {
         whereIsWaldo(640, waldoFile, "addToStructOneColumnWithExistingCatData(");
        int nVarsNow, nCases;
        nVarsNow = positionTracker.getNVarsInStruct();
        nCases = positionTracker.getNCasesInStruct();

        int columnIndex = nVarsNow;     //  The index of the new column        
        int varNumber = nVarsNow + 1;   //  The variable number of the new variable
        
        if (varNumber <= SIX) {   //    Six were created at initialization
            nVarsNow++;
            positionTracker.setNVarsInStruct(nVarsNow);
            String newColLabel = cat_dv.getTheDataLabel();
            dataStruct.get(nVarsNow - 1).setVarLabel(newColLabel);
            dataStruct.get(nVarsNow - 1).setIsNumeric(false);
        }
        else {
            String newColLabel = cat_dv.getTheDataLabel();
            ColumnOfData newCol = new ColumnOfData(this, nCases, newColLabel);
            // Set Column info to false
            newCol.setIsNumeric(false);
            dataStruct.add(newCol);
            nVarsNow++;
            positionTracker.setNVarsInStruct(nVarsNow);
            //  Set Grid label info the false
            dataStruct.get(nVarsNow - 1).setIsNumeric(false);
        }
        
        for (int ithCase = 0; ithCase < nCases; ithCase++) {   
            dataStruct.get(columnIndex)
                      .setStringInIthRow(ithCase, cat_dv.getIthDataPtAsString(ithCase));
        }
        sendDataStructToGrid(0, 0);
    }

    public void addNCasesToStruct(int nCasesToAdd) {
        whereIsWaldo(675, waldoFile, "addNCasesToStruct");     
        for (int iVar = 0; iVar < positionTracker.getNVarsInStruct(); iVar++) {
            dataStruct.get(iVar)
                      .addNCasesOfThese(nCasesToAdd, "*");
        }        
        positionTracker.setNCasesInStruct(nCasesToAdd + positionTracker.getNCasesInStruct());
    }
    
    public void insertARow(String strThisRow) {
        whereIsWaldo(684, waldoFile, "insertARow");
        goodToGo = checkTheColOrRowEntry(strThisRow);
        
        if (goodToGo) {
            int intThisRow = Integer.parseInt(strThisRow);  //  dm is 0 based
            /*******************************************************************
            *   At initial creation, 6 columns are created.  (Legacy code!)    *
            *   The number of variables needing adjustment is thus at least 6. *
            *******************************************************************/
            int nColsToAdjust = positionTracker.getNVarsInStruct();           
            for (int ithCol = 0; ithCol < nColsToAdjust; ithCol++) {
                dataStruct.get(ithCol).insertInThisRow(intThisRow - 1);
            }              
            positionTracker.setNCasesInStruct(positionTracker.getNCasesInStruct() + 1);
            resetTheGrid();
            sendDataStructToGrid(0, 0);
        }        
    }
    
    public void deleteARow(String strThisRow) {
        whereIsWaldo(704, waldoFile, "deleteARow");
        goodToGo = checkTheColOrRowEntry(strThisRow);
        
        if (goodToGo) {
            int intThisRow = Integer.parseInt(strThisRow);  //  dm is 0 based
            /*******************************************************************
            *   At initial creation, 6 columns are created.  (Legacy code!)    *
            *   The number of variables needing adjustment is thus at least 6. *
            *******************************************************************/
            int nColsToAdjust = positionTracker.getNVarsInStruct();            
            for (int ithCol = 0; ithCol < nColsToAdjust; ithCol++) {
                dataStruct.get(ithCol).deleteThisRow(intThisRow - 1);
            }              
            positionTracker.setNCasesInStruct(positionTracker.getNCasesInStruct() - 1);
            resetTheGrid();
            sendDataStructToGrid(0, 0);
        }        
    }
    
    private boolean checkTheColOrRowEntry(String strThisRow) {
        whereIsWaldo(724, waldoFile, "checkTheColOrRowEntry");
        
        if (strThisRow.isBlank() || strThisRow.isEmpty()) { 
            MyAlerts.showBlankRowAlert();
            return false;
        }
        
        if (!DataUtilities.strIsAPosInt(strThisRow)) { 
            MyAlerts.showNonPositiveRowAlert();
            return false;
        }         
        return true;
    }
    
    public void insertAColumn(int indexOfCol, String strThisLabel) {
        whereIsWaldo(739, waldoFile, "insertAColumn");
        ColumnOfData veryTemp  = new ColumnOfData(this, nCasesInStruct, strThisLabel);
        veryTemp.setIsNumeric(true);
        dataStruct.add(indexOfCol + 1, veryTemp); 
        positionTracker.setNVarsInStruct(positionTracker.getNVarsInStruct() + 1);
        resetTheGrid();
        sendDataStructToGrid(0, 0);  
    }
    
    public void insertAColumn(int indexOfCol, ColumnOfData colOfData) {
        whereIsWaldo(749, waldoFile, "insertAColumn");
        ColumnOfData veryTemp  = new ColumnOfData(colOfData);
        veryTemp.setIsNumeric(true);
        dataStruct.add(indexOfCol + 1, veryTemp); 
        positionTracker.setNVarsInStruct(positionTracker.getNVarsInStruct() + 1);
        resetTheGrid();
        sendDataStructToGrid(0, 0);  
    }

    public void deleteAColumn(int atThisLocation) {
        whereIsWaldo(759, waldoFile, "deleteAColumn");
        dataStruct.remove(atThisLocation);
        positionTracker.setNVarsInStruct(positionTracker.getNVarsInStruct() - 1);
        resetTheGrid();
        sendDataStructToGrid(0, 0);
    }

    public void resetTheGrid() {
        whereIsWaldo(767, waldoFile, "resetTheGrid");
        positionTracker.setFirstVarIdentifier(0);
        positionTracker.setFirstCaseIdentifier(0);
        sendDataStructToGrid(0, 0);
    }

    public Data_Grid getDataGrid() { return dataGrid; }
    public ArrayList<ColumnOfData> getAllTheColumns() { return dataStruct; }
    public ArrayList<TextField> getIthColumnHeading() { return colHeader; }
    public ArrayList<TextField> getJthRowHeading() {return rowHeader; }
    
    public boolean getVariableIsNumeric(int curr) {
        return dataStruct.get(curr).getIsNumeric();
    }
    
    public void setVariableNumeric(int curr, boolean setMe) {
        dataStruct.get(curr).setIsNumeric(setMe);
    }
    
    public String getVariableName(int col) {
        return dataStruct.get(col).getVarLabel();
    }
    
    public void setVariableNameInStruct(int col, String toThis) {
        dataStruct.get(col).setVarLabel(toThis);
    }
    
    public int getVariableIndex(String varName) {
        int found = -1;
        int varsThisTime = positionTracker.getNVarsInStruct();        
        for (int i = 0; i < varsThisTime; i++) {
            String checkMe = getVariableName(i);            
            if (checkMe.equals(varName)) {
                found = i;
            }            
        }        
        return found;
    } 
    
    public int getSampleSize(int col) {
        return dataStruct.get(col).getColumnSize();
    }    
    public ArrayList<ColumnOfData> getDataStruct() {return dataStruct; }
    public boolean getDataAreClean() { return dataIsClean; }
    
    public void setDataAreClean(boolean newStatus) { dataIsClean = newStatus; }
    
    public boolean getdataExists() { return dataExists; }
    public void setDataExists(boolean trueOrFalse) { dataExists = trueOrFalse; }    
    public File getFileName() { return fileName; }        
    public void setFileName(File file) { fileName = file; }    
    public char getDelimiter() { return delimiter; }
    public void setDelimiter(char delimit) { delimiter = delimit; }
    
    public File getLastPath() { return lastPath; }    
    
    public void setLastPath(File pathName) {
        String tempString = pathName.getParent();
        lastPath = new File(tempString);
    }    
    
    public ColumnOfData getSpreadsheetColumn(int dataVar) {
        return dataStruct.get(dataVar);
    }    
    
    public void addColumnHeading(int toHere, String thisString) {
        TextField thisTF = new TextField(thisString);
        colHeader.add(toHere, thisTF);
    }   
    
    public ObservableList<String> getVariableNames() {
        varNames = FXCollections.observableArrayList();        
        for (int i = 0; i < nVarsInStruct; i++) {
            varNames.add(getVariableName(i));
        }          
        return varNames;
    }
    
    // This is used by many methods: retrieve a data vector from the data matrix:
    public ArrayList<String> getSpreadsheetColumnAsStrings(int dataVar, int indicatorVar, String indicatorVal) {
        indicatorVar = getVariableIndex(indicatorVal);
        ColumnOfData tempColumn = dataStruct.get(dataVar);
        return tempColumn.getTheCases_ArrayList();
    } 
    
    public int getMaxVisCases() {return maxCasesInGrid; }
    public void setMaxVisCases(int toThisMany) { maxCasesInGrid = toThisMany; }    
    public int getNVarsInStruct() { return positionTracker.getNVarsInStruct(); }
    
    public void setNVarsInStruct(int toThis) { 
        nVarsInStruct = toThis; 
        positionTracker.setNVarsInStruct(toThis);
    } 
    
    public int getNCasesInStruct() { return positionTracker.getNCasesInStruct(); }
    
    public void setNCasesInStruct(int toThis) { 
        nCasesInStruct = toThis; 
        positionTracker.setNCasesInStruct(toThis);
    }  
    
    public void setHasBeenScrolled(boolean yesNo) { 
        scrollEventInitiated = yesNo;  
    } 
    
    public boolean getHasBeenScrolled() { return scrollEventInitiated; }    
    public int getDataStructSize() { return dataStruct.size(); }
    public int getMaxVisVars() { return maxVarsInGrid; }
    public void setMaxVisVars(int toThisMany) { maxVarsInGrid = toThisMany; }
    public Data_Manager getDataManager() { return dm; }
    public PositionTracker getPositionTracker() { return positionTracker; }
    public Data_Grid getTheGrid() { return dataGrid; }    
    public MainMenu getMainMenu() { return mainMenu; };
    public void setMainMenu(MainMenu mainMenu) { this.mainMenu = mainMenu; }
    
    public boolean getDataExists() {
        dataExists = true;
        if (getNCasesInStruct() == 0) {
            dataExists = false;
        }
        return dataExists;
    }
            
    
    // For diagnostic purposes only!!!    
    public void whereIsWaldo(int waldoLine, String waldoFile, String waldoWhere) {  
        if (!waldoFile.equals("")) {
            System.out.println("!!WW!! " + waldoLine + " / " + waldoFile + " / " + waldoWhere);
        }        
    }  
} 
