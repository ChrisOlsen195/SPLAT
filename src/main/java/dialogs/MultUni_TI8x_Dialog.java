/**************************************************
 *               MultUni_TI8x_Dialog              *
 *                    12/12/25                    *
 *                     12:00                      *
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

public class MultUni_TI8x_Dialog extends Splat_Dialog {

    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false; 
    
    private int nVarsSelected; //, quantVar, minVars;
    final int TWO;
    Separator sepTitle, sepDirections;
    
    Label lblVarTitle;
    TextField tfVarTitle;
    
    private ArrayList<Integer> al_IndexVarsSelected;
    
    private ArrayList<String> str_al_QuantsSelected, str_al_ChosenLabels, 
            str_al_PreData;
    
    // My classes
    private ColumnOfData colOfData;
    private ArrayList<ColumnOfData> col_al_Data;
    
    // FX objects
    private HBox hBoxMiddlePanel;
    private Label lblTitle, lblVar_1, lblVar_2;
    private Stage stageDialog;
    
    // ******************************************************************
    // *            The data are in separate columns                    *
    // ******************************************************************
    public MultUni_TI8x_Dialog(Data_Manager dm) {
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 61 MultUni_TI8x_Dialog, Constructing");
        }
        TWO = 2;
        createMultUni_TI8x_Dialog();
        strReturnStatus = "Cancel";
    }

    private void createMultUni_TI8x_Dialog() {
        if (printTheStuff) {
            System.out.println("*** 19 MultUni_TI81x_Dialog, createMultUni_TI8x_Dialog()");
        }
        str_al_ChosenLabels = new ArrayList();

        VBox vBox_MainPanel = new VBox();
        vBox_MainPanel.setAlignment(Pos.CENTER);

        lblTitle = new Label("");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        sepTitle = new Separator();
        sepDirections = new Separator();
        
        String directions = "These are directions for the circumstance" +
                            "\nthat you have data in separate columns.";
        Text txt_Directions = new Text(directions);
        vBox_MainPanel.getChildren().addAll(lblTitle, sepTitle, txt_Directions, sepDirections);

        hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setAlignment(Pos.CENTER);

        VBox vBox_List_1 = new VBox();
        vBox_List_1.setAlignment(Pos.TOP_LEFT);
        lblVar_1 = new Label();
        lblVar_1.setPadding(new Insets(0, 0, 5, 0));
        Var_List varsAvailable = new Var_List(dm, 125.0, 125.0);
        vBox_List_1.getChildren().addAll(lblVar_1, varsAvailable.getPane());
        vBox_List_1.setPadding(new Insets(0, 10, 0, 10));
        hBoxMiddlePanel.getChildren().add(vBox_List_1);

        Button btn_SelectQuantVariable = new Button("===>");
        VBox vBox_List_2 = new VBox();
        vBox_List_2.setAlignment(Pos.TOP_LEFT);
        lblVar_2 = new Label();
        lblVar_2.setPadding(new Insets(0, 0, 5, 0));
        Var_List varsSelected = new Var_List(dm, 125.0, 125.0);
        varsSelected.clearList();
        vBox_List_2.getChildren().add(lblVar_2);
        vBox_List_2.getChildren().add(varsSelected.getPane());
        
        lblVarTitle =   new Label(" Title for variables: ");        
        tfVarTitle = new TextField("");
        tfVarTitle.setPrefColumnCount(15);        
        tfVarTitle.textProperty().addListener(this::changeExplanVar);

        GridPane gridPaneRightPanel = new GridPane();
        gridPaneRightPanel.setHgap(10);
        gridPaneRightPanel.setVgap(15);
        gridPaneRightPanel.add(btn_SelectQuantVariable, 0, 0);
        gridPaneRightPanel.add(vBox_List_2, 1, 0);
        gridPaneRightPanel.add(lblVarTitle, 0, 3);
        gridPaneRightPanel.add(tfVarTitle, 1, 3);
        gridPaneRightPanel.setPadding(new Insets(0, 10, 0, 0));

        hBoxMiddlePanel.getChildren().add(gridPaneRightPanel);
        hBoxMiddlePanel.setPadding(new Insets(10, 0, 10, 0));

        vBox_MainPanel.getChildren().add(hBoxMiddlePanel);
        Separator sepButtons = new Separator();
        vBox_MainPanel.getChildren().add(sepButtons);

        HBox hBox_ButtonPanel = new HBox(10);
        hBox_ButtonPanel.setAlignment(Pos.CENTER);
        hBox_ButtonPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        Button btn_Reset = new Button("Reset");
        hBox_ButtonPanel.getChildren().addAll(btnOK, btnCancel, btn_Reset);

        vBox_MainPanel.getChildren().add(hBox_ButtonPanel);
        Scene myScene = new Scene(vBox_MainPanel);
        myScene.getStylesheets().add(strCSS);

        stageDialog = new Stage();
        stageDialog.setResizable(true);
        stageDialog.setScene(myScene);

        btnCancel.setStyle("-fx-text-fill: red;");
        btnCancel.setOnAction(e -> {  
            boolGoodToGo = false;
            strReturnStatus = "Cancel";
            stageDialog.hide();
        });

        btn_Reset.setOnAction((ActionEvent event) -> {
            varsAvailable.resetList();
            varsSelected.clearList();
            nVarsSelected = 0;
        });

        btn_SelectQuantVariable.setOnAction((ActionEvent event) -> {
            str_al_QuantsSelected = varsAvailable.getNamesSelected();
            boolGoodToGo = true;
            
            // If only one quant is selected, it may be a Tidy variable            
            if (varsAvailable.getNamesSelected().size() == 1) { 
                //str_QuantVar = varsAvailable.getNamesSelected().get(0);
                //quantVar = dm.getVariableIndex(str_QuantVar);                            
            }                  

            for (String tmpVar : str_al_QuantsSelected) {   
                if (!dm.getDataType(dm.getVariableIndex(tmpVar)).equals("Quantitative")) {
                //if (!dm.getVariableIsNumeric(dm.getVariableIndex(tmpVar))) {
                    MyAlerts.showInappropriateNonNumericVariableAlert();
                    btn_Reset.fire();                    
                    boolGoodToGo = false;
                }
            }

            if (boolGoodToGo) {
                varsSelected.addVarName(str_al_QuantsSelected);
                varsAvailable.delVarName(str_al_QuantsSelected);
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;

            al_IndexVarsSelected = varsSelected.getVarIndices();
            nVarsSelected = al_IndexVarsSelected.size();
            if (nVarsSelected < TWO) {
                MyAlerts.showFewerThanTwoVariablesAlert();
                boolGoodToGo = false;
                btn_Reset.fire();
            }

            if (boolGoodToGo) {
                col_al_Data =  new ArrayList(); // ArrayList[] of chosen variables?                
                for (int j = 0; j < nVarsSelected; j++) {
                    str_al_ChosenLabels.add(dm.getVariableName(al_IndexVarsSelected.get(j)));
                    colOfData = new ColumnOfData();
                    str_al_PreData = dm.getSpreadsheetColumnAsStrings(al_IndexVarsSelected.get(j), -1, null);
                    colOfData = new ColumnOfData(dm, str_al_ChosenLabels.get(j), "MultUni_NotStacked_Dial", str_al_PreData);

                    // an ArrayList of Strings
                    colOfData = colOfData.getColumnOfData();                       
                    col_al_Data.add(colOfData);
                }
                
                stageDialog.close();
                strReturnStatus = "OK";                
                
            } 
        });
    } 
    
    public void changeExplanVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tfVarTitle.setText(newValue); 
    }
    
    public void show_NS_Dialog() {
        lblTitle.setText("Comparing 2 or more distributions");
        stageDialog.setTitle("Comparing 2 or more distributions");
        stageDialog.showAndWait();
    }
    
    public String getSubTitle() { return tfVarTitle.getText(); }    
    public ArrayList<ColumnOfData> getData() {return col_al_Data; }    
    public boolean runTheAnalysis() { return true; }  
}


