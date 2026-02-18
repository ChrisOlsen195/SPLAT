/**************************************************
 *             ANOVA1_Cat_TI8x_Dialog             *
 *                    12/16/25                    *
 *                     09:00                      *
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

public class ANOVA1_Cat_TI8x_Dialog extends Splat_Dialog {

    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private int nLevels;
    final int THREE;
    Separator sepTitle, sepDirections, sepButtons;
    
    Label lblExplanVar, lblResponseVar;
    TextField tfExplanVar, tfResponseVar;
    
    private ArrayList<Integer> al_IndexVarsSelected;    
    private ArrayList<String> al_QuantsSelected, str_ChosenLabels, preData;
    
    // My classes
    private ColumnOfData col_UnivData;
    private ArrayList<ColumnOfData> data;
    
    // FX objects
    Button btnReset, selectQuantVariable;
    GridPane rightPanel;
    private HBox buttonPanel, middlePanel;
    private Label lblTitle, vLabel_1, vLabel_2;
    Scene myScene;
    private Stage stageDialog;
    Var_List varList_VarsAvailable, varList_VarsSelected;
    VBox vBoxList_1, vBoxList_2;
    
    // ******************************************************************
    // *            The data are in separate columns                    *
    // ******************************************************************
    public ANOVA1_Cat_TI8x_Dialog(Data_Manager dm) {
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 63 ANOVA1_Cat_TI8x_Dialog, Constructing");
        }
        THREE = 3;
        createANOVA_TI8x_Dialog();
    }

    private void createANOVA_TI8x_Dialog() {
        if (printTheStuff) {
            System.out.println("*** 71 ANOVA1_Cat_TI8x_Dialog, createANOVA_TI8x_Dialog()");
        }
        boolGoodToGo = true;
        strReturnStatus = "OK";
        str_ChosenLabels = new ArrayList();

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);

        lblTitle = new Label("");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        sepTitle = new Separator();
        sepDirections = new Separator();
        
        String directions = "ANOVA:  Quantitative variables are in separate columns.";
        Text txtDirections = new Text(directions);
        mainPanel.getChildren().addAll(lblTitle, sepTitle, txtDirections, sepDirections);

        middlePanel = new HBox();
        middlePanel.setAlignment(Pos.CENTER);

        vBoxList_1 = new VBox();
        vBoxList_1.setAlignment(Pos.TOP_LEFT);
        vLabel_1 = new Label();
        vLabel_1.setPadding(new Insets(0, 0, 5, 0));
        varList_VarsAvailable = new Var_List(dm, null, null);
        vBoxList_1.getChildren().add(vLabel_1);
        vBoxList_1.getChildren().add(varList_VarsAvailable.getPane());
        vBoxList_1.setPadding(new Insets(0, 10, 0, 10));
        middlePanel.getChildren().add(vBoxList_1);

        selectQuantVariable = new Button("===>");
        vBoxList_2 = new VBox();
        vBoxList_2.setAlignment(Pos.TOP_LEFT);
        vLabel_2 = new Label();
        vLabel_2.setPadding(new Insets(0, 0, 5, 0));
        varList_VarsSelected = new Var_List(dm, 125.0, 125.0);
        varList_VarsSelected.clearList();
        vBoxList_2.getChildren().add(vLabel_2);
        vBoxList_2.getChildren().add(varList_VarsSelected.getPane());
        
        lblExplanVar =   new Label(" Explanatory variable: ");
        lblResponseVar = new Label("    Response variable: ");
        
        tfExplanVar = new TextField("Explanatory variable");
        tfResponseVar = new TextField("Response variable");
        
        tfExplanVar.setPrefColumnCount(15);
        tfResponseVar.setPrefColumnCount(15);
        
        tfExplanVar.textProperty().addListener(this::changeExplanVar);
        tfResponseVar.textProperty().addListener(this::changeResponseVar);

        rightPanel = new GridPane();
        rightPanel.setHgap(10);
        rightPanel.setVgap(15);
        rightPanel.add(selectQuantVariable, 0, 0);
        rightPanel.add(vBoxList_2, 1, 0);
        rightPanel.add(lblExplanVar, 0, 3);
        rightPanel.add(lblResponseVar, 0, 4);
        rightPanel.add(tfExplanVar, 1, 3);
        rightPanel.add(tfResponseVar, 1, 4);
        rightPanel.setPadding(new Insets(0, 10, 0, 0));

        middlePanel.getChildren().add(rightPanel);
        middlePanel.setPadding(new Insets(10, 0, 10, 0));

        mainPanel.getChildren().add(middlePanel);
        sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);

        buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        btnReset.setStyle("-fx-text-fill: red;");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);

        mainPanel.getChildren().add(buttonPanel);
        myScene = new Scene(mainPanel);
        myScene.getStylesheets().add(strCSS);

        stageDialog = new Stage();
        stageDialog.setResizable(true);
        stageDialog.setScene(myScene);


        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            stageDialog.hide();
        });

        
        btnReset.setOnAction((ActionEvent event) -> {
            varList_VarsAvailable.resetList();
            varList_VarsSelected.clearList();
            nLevels = 0;
            boolGoodToGo = true;
        });

        selectQuantVariable.setOnAction((ActionEvent event) -> {
            al_QuantsSelected = varList_VarsAvailable.getNamesSelected();
            boolGoodToGo = true;     
            
            for (String tmpVar : al_QuantsSelected) {                
                if (!dm.getDataType(dm.getVariableIndex(tmpVar)).equals("Quantitative")) {
                    MyAlerts.showVariableIsNotQuantAlert();
                    btnReset.fire();
                    boolGoodToGo = false;
                }
            }

            if (boolGoodToGo) {
                varList_VarsSelected.addVarName(al_QuantsSelected);
                varList_VarsAvailable.delVarName(al_QuantsSelected);
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;
            al_IndexVarsSelected = varList_VarsSelected.getVarIndices();
            nLevels = al_IndexVarsSelected.size();
              
            if (nLevels < THREE) {
                MyAlerts.showAnova1_LT3_LevelsAlert();
                btnReset.fire();
                boolGoodToGo = false;
            }            

            if (boolGoodToGo) {
                data =  new ArrayList(); // ArrayList[] of chosen variables?
                
                for (int j = 0; j < nLevels; j++) {
                    str_ChosenLabels.add(dm.getVariableName(al_IndexVarsSelected.get(j)));
                    col_UnivData = new ColumnOfData();
                    preData = dm.getSpreadsheetColumnAsStrings(al_IndexVarsSelected.get(j), -1, null);
                    col_UnivData = new ColumnOfData(dm, str_ChosenLabels.get(j), "ANOVA1_Cat_NotStacked", preData);
                    col_UnivData = col_UnivData.getColumnOfData();                       
                    data.add(col_UnivData);
                }
                nLevels = data.size();
                stageDialog.close();
                strReturnStatus = "OK";
            }   
        });
    }
    
    public void changeExplanVar(ObservableValue<? extends String> prop,
            String oldValue,
            String newValue) {
            tfExplanVar.setText(newValue); 
    }

    public void changeResponseVar(ObservableValue<? extends String> prop,
            String oldValue,
            String newValue) {
            tfResponseVar.setText(newValue); 
    }

    public void show_ANOVA1_TI8x_Dialog() {
        lblTitle.setText("One way ANOVA");
        stageDialog.setTitle("One way ANOVA");
        stageDialog.showAndWait();
    }

    public String getExplanatoryVariable() { return tfExplanVar.getText(); }
    public String getResponseVariable() { return tfResponseVar.getText(); } 
    public int getNLevels() { return nLevels; }
    public ArrayList<ColumnOfData> getData() { return data; }
}

