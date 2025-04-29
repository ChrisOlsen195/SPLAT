/************************************************************
 *                 InsertOrDeleteColumn_Dialog              *
 *                          01/15/25                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import dataObjects.ColumnOfData;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import splat.Data_Manager;
import splat.Var_List;

public class InsertOrDeleteColumn_Dialog extends Splat_Dialog {
    // POJOs
    
    public int minVars, maxVars, varIndexForX, nCheckBoxes, minSampleSize;
    
    String descriptionOfVariable, subTitle, strSelected, 
           strInsertRemorseMsgTitle, strInsertRemorseMsg,
           strDeleteRemorseMsgTitle, strDeleteRemorseMsg;
    
    // My classes
    public ColumnOfData col_Data;
    Var_List var_List;    
    
    // POJOs / FX
    public Button btnReset, btnSelectVariable;
    public CheckBox[] chBoxDashBoardOptions;
    public GridPane gridPaneChoicesMade;
    public HBox hBoxMiddlePanel, hBoxDataDescriptions;
    public VBox vBoxMainPanel, vBoxLeftPanel, vBoxRightPanel, vBoxVars2ChooseFrom, 
                vBoxVarChoices;
    public Label lblTitle, lblVarsInData, lblFirstVar, lblExplanVar, 
                 lblQuant_Real, lblQuant_Int, lblCategorical,
                 lblSelectionDirections;
    
    public Rectangle quantReal, quantInt, categorical;
    public TextField tfFirstVar, tfExplanVar;

    public InsertOrDeleteColumn_Dialog(Data_Manager dm, String insertOrDelete) {
        super(dm);
        this.dm = dm;
        
        waldoFile = "InsertOrDeleteColumn_Dialog";
        //waldoFile = "";
        
        dm.whereIsWaldo(61, waldoFile, "Constructing");
        strReturnStatus = "Cancel";
        boolGoodToGo = true;
        
        strInsertRemorseMsgTitle = "Ack! Insert Remorse??";
        strInsertRemorseMsg = "So, having second thoughts about inserting, are we?  Well," + 
                           "\nIf so, the PROPER course of action is to click on the " +
                           "\n'Cancel', not the 'Insert' button. That is why I, SPLAT," +
                           "\nput the 'Cancel' button there. Now, let's get with the " +
                           "\nprogram and try clicking again...";
        
        strDeleteRemorseMsgTitle = "Ack! Delete Remorse??";
        strDeleteRemorseMsg = "So, having second thoughts about deleting, are we?  Well," + 
                           "\nIf so, the PROPER course of action is to click on the " +
                           "\n'Cancel', not the 'Insert' button. That is why I, SPLAT," +
                           "\nput the 'Cancel' button there. Now, let's get with the " +
                           "\nprogram and try clicking again...";

        if (insertOrDelete.equals("INSERT")) {
            lblTitle = new Label("Insert a Column dialog");
        } else {
            lblTitle = new Label("Delete a Column dialog");   
        }
        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        //if (dm != null) {
            if (insertOrDelete.equals("INSERT")) {
                lblSelectionDirections = new Label("Please select the variable AFTER which you wish to insert...");
            } else {
                lblSelectionDirections = new Label("Please select the variable you wish to delete...");   
            }            
            lblSelectionDirections.setPadding(new Insets(25, 0, 0, 0));      
            vBoxVars2ChooseFrom = new VBox();
            vBoxVars2ChooseFrom.setAlignment(Pos.TOP_LEFT);
            lblVarsInData = new Label("Variables in Data:");
            lblVarsInData.setPadding(new Insets(0, 0, 5, 0));
            var_List = new Var_List(dm, null, null);
            vBoxVars2ChooseFrom.getChildren().add(lblVarsInData);
            vBoxVars2ChooseFrom.getChildren().add(var_List.getPane());
            vBoxVars2ChooseFrom.setPadding(new Insets(0, 10, 0, 10));

            btnSelectVariable = new Button("===>");

            vBoxVarChoices = new VBox();
            vBoxVarChoices.setAlignment(Pos.TOP_LEFT);
            lblFirstVar = new Label();
            lblFirstVar.setPadding(new Insets(0, 0, 5, 0));
            tfFirstVar = new TextField("");
            tfFirstVar.setPrefWidth(125.0);
            vBoxVarChoices.getChildren().addAll(lblFirstVar, tfFirstVar);
        //} 
        lblExplanVar =   new Label(" Label for Variable: ");
        tfExplanVar = new TextField("Label for Variable");
        
        tfExplanVar.setPrefColumnCount(15);
        tfExplanVar.textProperty().addListener(this::changeExplanVar);
                
        //if( dm != null) {
            gridPaneChoicesMade = new GridPane();
            gridPaneChoicesMade.setHgap(10);
            gridPaneChoicesMade.setVgap(15);
            gridPaneChoicesMade.add(btnSelectVariable, 0, 0);
            gridPaneChoicesMade.add(vBoxVarChoices, 1, 0);
            
            if (insertOrDelete.equals("INSERT")) {
                gridPaneChoicesMade.add(lblExplanVar, 0, 3);
                gridPaneChoicesMade.add(tfExplanVar, 1, 3);      
            }
            
            GridPane.setValignment(btnSelectVariable, VPos.BOTTOM);
            gridPaneChoicesMade.setPadding(new Insets(0, 10, 0, 0));
        //}
        vBoxLeftPanel = new VBox(10);
        vBoxLeftPanel.setAlignment(Pos.CENTER_LEFT);
        vBoxLeftPanel.setPadding(new Insets(0, 25, 0, 10));
        
        //if (dm != null) {
            vBoxRightPanel = new VBox(10);
            vBoxRightPanel.setAlignment(Pos.CENTER_LEFT);
            vBoxRightPanel.setPadding(new Insets(0, 25, 0, 10));
            vBoxRightPanel.getChildren().add(gridPaneChoicesMade);
        //}
        hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setAlignment(Pos.CENTER);
        hBoxMiddlePanel.getChildren().add(vBoxLeftPanel);    
        
        //if (dm != null) {
            hBoxMiddlePanel.getChildren().add(vBoxVars2ChooseFrom);
            hBoxMiddlePanel.getChildren().add(vBoxRightPanel);
            hBoxMiddlePanel.setPadding(new Insets(10, 0, 10, 0));
        //}
        
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        if (insertOrDelete.equals("INSERT")) {
        btnOK.setText("Insert");
        } else {
            btnOK.setText("Delete");
        }
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
        
        vBoxMainPanel = new VBox();
        vBoxMainPanel.setAlignment(Pos.CENTER);    
        Separator sepTitle = new Separator();
        
        //if (dm != null) {
            vBoxMainPanel.getChildren().addAll(lblTitle, sepTitle, lblSelectionDirections);   
        //}
        
        vBoxMainPanel.getChildren().add(hBoxMiddlePanel);
        Separator sepButtons = new Separator();
        vBoxMainPanel.getChildren().add(sepButtons);    
        vBoxMainPanel.getChildren().add(buttonPanel);
        
        Scene myScene = new Scene(vBoxMainPanel);
        myScene.getStylesheets().add(strCSS);
        setScene(myScene);

        btnReset.setOnAction((ActionEvent event) -> {
            var_List.resetList();
            tfFirstVar.setText("");
        });

        // if (dm != null) {
            btnSelectVariable.setOnAction((ActionEvent event) -> {
                
                if (var_List.getNamesSelected().size() == 1) {
                    String tempIndicator = var_List.getNamesSelected().get(0);
                    tfFirstVar.setText(tempIndicator);
                    var_List.delVarName(var_List.getNamesSelected());
                    boolean varType_Ok = true;
                    strSelected = tfFirstVar.getText();
                    varIndexForX = dm.getVariableIndex(strSelected);
                }
            });
        // }

        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;
            strSelected = tfFirstVar.getText();
            varIndexForX = dm.getVariableIndex(strSelected);

             if (varIndexForX == -1) {
                MyDialogs remorseDialog = new MyDialogs();
                if (insertOrDelete.equals("INSERT")) {
                remorseDialog.NoChoiceMessage(2, strInsertRemorseMsgTitle, strInsertRemorseMsg);  
                } else {
                    remorseDialog.NoChoiceMessage(2, strDeleteRemorseMsgTitle, strDeleteRemorseMsg);
                }
                boolGoodToGo = false;
                btnReset.fire();
            }

            if (boolGoodToGo) {
                varIndexForX = dm.getVariableIndex(strSelected);
                col_Data = new ColumnOfData(dm.getSpreadsheetColumn(varIndexForX));
                descriptionOfVariable = tfExplanVar.getText();
                subTitle = "Variable: " + descriptionOfVariable; 
                strReturnStatus = "OK";
                close();
            } 
        });       
    }
    
    public void changeExplanVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tfExplanVar.setText(newValue); 
    }
        
    public int getIndexOfVariable() { return varIndexForX; }
    public String getDescriptionOfVariable() { return descriptionOfVariable; }    
    public ColumnOfData getData() { return col_Data; }    
    public CheckBox[] getCheckBoxes() { return chBoxDashBoardOptions; }
    
    // Used by regression -- change to ReturnStatus
    public boolean getOK() { return boolGoodToGo; }    
}
