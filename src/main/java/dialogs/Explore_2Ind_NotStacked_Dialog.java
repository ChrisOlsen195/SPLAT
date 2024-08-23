/**************************************************
 *          Explore_2Ind_NotStacked_Dialog        *
 *                    05/26/24                    *
 *                     18:00                      *
 *************************************************/
package dialogs;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import splat.*;
import utilityClasses.MyAlerts;

public class Explore_2Ind_NotStacked_Dialog extends Splat_Dialog {
    // POJOs

    private int nLevels;
    final int TWO;

    private ArrayList<Integer> al_IndexVarsSelected;
    private ArrayList<String> al_VarSelected, str_ChosenLabels, preData;
    
    // My classes
    private ColumnOfData col_UnivData;
    private ArrayList<ColumnOfData> col_al_Data;
    
    // FX objects
    Button btnReset, btnSelectVariable;
    GridPane gridPane_RightPanel;
    private HBox hBoxButtonPanel, hBoxMiddlePanel;
    private Label lblTitle, lbl_Var_1, lbl_Var_2, lblExplanVar, lblResponseVar;
    Scene myScene;
    Separator sepTitle, sepDirections, sepButtons;
    private Stage stageDialog;
    TextField tfFirstVar, tfSecondVar;
    Var_List varListAvailable, varListSelected;
    VBox vBoxList_1, vBoxList_2;

    // ******************************************************************
    // *            The col_al_Data are in separate columns                    *
    // ******************************************************************
    
    public Explore_2Ind_NotStacked_Dialog(Data_Manager dm) {
        this.dm = dm;
        // Make empty if no-print
        //waldoFile = "Explor_2Ind_NotStacked_Dialog(dm)";
        waldoFile = "";
        dm.whereIsWaldo(62, waldoFile, "Constructing");
        TWO = 2;
        Explore_2Ind_NS_Dialog();
    }

    private void Explore_2Ind_NS_Dialog() {
        dm.whereIsWaldo(68, waldoFile, "Explore_2Ind_NS_Dialog()");
        boolGoodToGo = true;
        strReturnStatus = "OK";
        str_ChosenLabels = new ArrayList();

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);

        lblTitle = new Label("");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        sepTitle = new Separator();
        sepTitle = new Separator();
        sepDirections = new Separator();
        
        String directions = "Compare 2 Distributions";
        Text txtDirections = new Text(directions);
        mainPanel.getChildren().addAll(lblTitle, sepTitle, txtDirections, sepDirections);

        hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setAlignment(Pos.CENTER);

        vBoxList_1 = new VBox();
        vBoxList_1.setAlignment(Pos.TOP_LEFT);
        lbl_Var_1 = new Label();
        lbl_Var_1.setPadding(new Insets(0, 0, 5, 0));
        varListAvailable = new Var_List(dm, null, null);
        vBoxList_1.getChildren().add(lbl_Var_1);
        vBoxList_1.getChildren().add(varListAvailable.getPane());
        vBoxList_1.setPadding(new Insets(0, 10, 0, 10));
        hBoxMiddlePanel.getChildren().add(vBoxList_1);

        btnSelectVariable = new Button("===>");
        vBoxList_2 = new VBox();
        vBoxList_2.setAlignment(Pos.TOP_LEFT);
        lbl_Var_2 = new Label();
        lbl_Var_2.setPadding(new Insets(0, 0, 5, 0));
        varListSelected = new Var_List(dm, 125.0, 125.0);
        varListSelected.clearList();
        vBoxList_2.getChildren().add(lbl_Var_2);
        vBoxList_2.getChildren().add(varListSelected.getPane());
        
        lblExplanVar =   new Label(" Variable #1: ");
        lblResponseVar = new Label(" Variable #2: ");
        
        tfFirstVar = new TextField(" Variable #1: ");
        tfSecondVar = new TextField(" Variable #2: ");
        
        tfFirstVar.setPrefColumnCount(15);
        tfSecondVar.setPrefColumnCount(15);
        
        tfFirstVar.textProperty().addListener(this::changeFirstVar);
        tfSecondVar.textProperty().addListener(this::changeSecondVar);

        gridPane_RightPanel = new GridPane();
        gridPane_RightPanel.setHgap(10);
        gridPane_RightPanel.setVgap(15);
        gridPane_RightPanel.add(btnSelectVariable, 0, 0);
        gridPane_RightPanel.add(vBoxList_2, 1, 0);
        gridPane_RightPanel.add(lblExplanVar, 0, 3);
        gridPane_RightPanel.add(lblResponseVar, 0, 4);
        gridPane_RightPanel.add(tfFirstVar, 1, 3);
        gridPane_RightPanel.add(tfSecondVar, 1, 4);
        gridPane_RightPanel.setPadding(new Insets(0, 10, 0, 0));

        hBoxMiddlePanel.getChildren().add(gridPane_RightPanel);
        hBoxMiddlePanel.setPadding(new Insets(10, 0, 10, 0));

        mainPanel.getChildren().add(hBoxMiddlePanel);
        sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);

        hBoxButtonPanel = new HBox(10);
        hBoxButtonPanel.setAlignment(Pos.CENTER);
        hBoxButtonPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        hBoxButtonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);

        mainPanel.getChildren().add(hBoxButtonPanel);
        myScene = new Scene(mainPanel);
        myScene.getStylesheets().add(strCSS);

        stageDialog = new Stage();
        stageDialog.setResizable(true);
        stageDialog.setScene(myScene);

        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            stageDialog.close();
        });

        btnReset.setOnAction((ActionEvent event) -> {
            varListAvailable.resetList();
            varListSelected.clearList();
            nLevels = 0;
            boolGoodToGo = true;
        });

        btnSelectVariable.setOnAction((ActionEvent event) -> {
            al_VarSelected = varListAvailable.getNamesSelected();
            boolGoodToGo = true;
            
            for (String tmpVar : al_VarSelected) {
                if (!dm.getVariableIsNumeric(dm.getVariableIndex(tmpVar))) {
                    MyAlerts.showVariableIsNotQuantAlert();
                    btnReset.fire();
                    boolGoodToGo = false;
                }
            }

            if (boolGoodToGo) {
                varListSelected.addVarName(al_VarSelected);
                varListAvailable.delVarName(al_VarSelected);
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;

            al_IndexVarsSelected = varListSelected.getVarIndices();
            nLevels = al_IndexVarsSelected.size();
              
            if (nLevels != TWO) {
                MyAlerts.showNVars_NE2_LevelsAlert();
                btnReset.fire();
                boolGoodToGo = false;
            }            

            if (boolGoodToGo) {
                col_al_Data =  new ArrayList(); // ArrayList[] of chosen variables?
                
                for (int j = 0; j < nLevels; j++) {
                    str_ChosenLabels.add(dm.getVariableName(al_IndexVarsSelected.get(j)));
                    col_UnivData = new ColumnOfData();
                    preData = dm.getSpreadsheetColumnAsStrings(al_IndexVarsSelected.get(j), -1, null);
                    col_UnivData = new ColumnOfData(dm, str_ChosenLabels.get(j), "Explore_2Ind_NotStacked", preData);
                    col_UnivData = col_UnivData.getColumnOfData();                       
                    col_al_Data.add(col_UnivData);
                }
                
                nLevels = col_al_Data.size();
                stageDialog.close();
                strReturnStatus = "OK";
            }   
        });
    } 
    
    public void changeFirstVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tfFirstVar.setText(newValue); 
    }

    public void changeSecondVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tfSecondVar.setText(newValue); 
    }

    public void show_ANOVA1_NS_Dialog() {
        lblTitle.setText("Compare 2 Distributions");
        stageDialog.setTitle("Compare 2 Distributions");
        stageDialog.showAndWait();
    }

    public String getFirstVariable() { return tfFirstVar.getText(); }
    public String getSecondVariable() { return tfSecondVar.getText(); } 
    public int getNLevels() { return nLevels; }
    public ArrayList<ColumnOfData> getData() { return col_al_Data;  }    
    public boolean runTheAnalysis() { return true; }
}


