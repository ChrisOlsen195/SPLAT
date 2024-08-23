/**************************************************
 *         ANOVA1_Quant_NotStacked_Dialog         *
 *                    05/26/24                    *
 *                     15:00                      *
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
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class ANOVA1_Quant_NotStacked_Dialog extends Splat_Dialog {

    // POJOs
    private int nLevels;
    final int THREE;
    Separator sepTitle, sepDirections, sepButtons;
    
    Label lblExplanVar, lblResponseVar;
    TextField tfExplanVar, tfResponseVar;
    
    private ArrayList<Integer> al_IndexVarsSelected;
    private ArrayList<String> str_al_QuantsSelected, str_ChosenLabels, preData;
    
    private String strQuantVarText;    //  Used in btnSelectQuantVariable
    
    // My classes
    private ColumnOfData col_UnivData;
    private ArrayList<ColumnOfData> col_al_Data;
    
    // FX objects
    Button btnReset, btnSelectQuantVariable;
    GridPane gridPaneRightPanel;
    private HBox hBoxButtonPanel, hBoxMiddlePanel;
    private Label lblTitle, lbl_Var1, lbl_Var2;
    Scene myScene;
    private Stage stageDialog;
    //Var_List varList_Available, varList_Selected;
    VBox vBox_List1, vBox_List2;
    
    // ******************************************************************
    // *            The col_al_Data are in separate columns                    *
    // ******************************************************************
    public ANOVA1_Quant_NotStacked_Dialog(Data_Manager dm) {
        this.dm = dm;
        
        // Make empty if no-print
        //waldoFile = "ANOVA1_Quant_NotStacked_Dialog";
        waldoFile = "";
        
        dm.whereIsWaldo(67, waldoFile, "Constructing");
        THREE = 3;
        createANOVA_NS_Dialog();
    }

    private void createANOVA_NS_Dialog() {
        dm.whereIsWaldo(73, waldoFile, "createANOVA_NS_Dialog()");
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

        hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setAlignment(Pos.CENTER);

        vBox_List1 = new VBox();
        vBox_List1.setAlignment(Pos.TOP_LEFT);
        lbl_Var1 = new Label();
        lbl_Var1.setPadding(new Insets(0, 0, 5, 0));
        Var_List varsAvailable = new Var_List(dm, null, null);
        vBox_List1.getChildren().add(lbl_Var1);
        vBox_List1.getChildren().add(varsAvailable.getPane());
        vBox_List1.setPadding(new Insets(0, 10, 0, 10));
        hBoxMiddlePanel.getChildren().add(vBox_List1);

        btnSelectQuantVariable = new Button("===>");
        vBox_List2 = new VBox();
        vBox_List2.setAlignment(Pos.TOP_LEFT);
        lbl_Var2 = new Label();
        lbl_Var2.setPadding(new Insets(0, 0, 5, 0));
        Var_List varsSelected = new Var_List(dm, 125.0, 125.0);
        varsSelected.clearList();
        vBox_List2.getChildren().add(lbl_Var2);
        vBox_List2.getChildren().add(varsSelected.getPane());
        
        lblExplanVar =   new Label(" Explanatory variable: ");
        lblResponseVar = new Label("    Response variable: ");
        
        tfExplanVar = new TextField("Explanatory variable");
        tfResponseVar = new TextField("Response variable");
        
        tfExplanVar.setPrefColumnCount(15);
        tfResponseVar.setPrefColumnCount(15);
        
        tfExplanVar.textProperty().addListener(this::changeExplanVar);
        tfResponseVar.textProperty().addListener(this::changeResponseVar);

        gridPaneRightPanel = new GridPane();
        gridPaneRightPanel.setHgap(10);
        gridPaneRightPanel.setVgap(15);
        gridPaneRightPanel.add(btnSelectQuantVariable, 0, 0);
        gridPaneRightPanel.add(vBox_List2, 1, 0);
        gridPaneRightPanel.add(lblExplanVar, 0, 3);
        gridPaneRightPanel.add(lblResponseVar, 0, 4);
        gridPaneRightPanel.add(tfExplanVar, 1, 3);
        gridPaneRightPanel.add(tfResponseVar, 1, 4);
        gridPaneRightPanel.setPadding(new Insets(0, 10, 0, 0));

        hBoxMiddlePanel.getChildren().add(gridPaneRightPanel);
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
            varsAvailable.resetList();
            varsSelected.clearList();
            nLevels = 0;
            boolGoodToGo = true;
        });

        btnSelectQuantVariable.setOnAction((ActionEvent event) -> {
            str_al_QuantsSelected = varsAvailable.getNamesSelected();
            boolGoodToGo = true;
            
            // If only one quant is selected, it may be a stacked variable
            if (varsAvailable.getNamesSelected().size() == 1) { 
                strQuantVarText = varsAvailable.getNamesSelected().get(0);
                // For quantitative non-stacked, the label must be numeric.                
                if (!DataUtilities.strIsADouble(strQuantVarText)) {
                    MyAlerts.showQuantANOVABadLabelAlert();
                    boolGoodToGo = false;
                    btnReset.fire();
                }                              
            }                  
            // **************   Verify Quant Values  *********************
            if (boolGoodToGo) {
                for (String tmpVar : str_al_QuantsSelected) {                    
                    if (!dm.getVariableIsNumeric(dm.getVariableIndex(tmpVar))) {
                        MyAlerts.showDataTypeErrorAlert();
                        boolGoodToGo = false;
                        btnReset.fire();
                    }
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
            nLevels = al_IndexVarsSelected.size();
              
            if (nLevels < THREE) {
                MyAlerts.showAnova1_LT3_LevelsAlert();
                btnReset.fire();
                boolGoodToGo = false;
            }            

            if (boolGoodToGo) {
                col_al_Data =  new ArrayList(); // ArrayList[] of chosen variables?                
                for (int j = 0; j < nLevels; j++) {
                    str_ChosenLabels.add(dm.getVariableName(al_IndexVarsSelected.get(j)));
                    col_UnivData = new ColumnOfData();
                    preData = dm.getSpreadsheetColumnAsStrings(al_IndexVarsSelected.get(j), -1, null);
                    col_UnivData = new ColumnOfData(dm, str_ChosenLabels.get(j), "ANOVA1_Quant_NotStacked", preData);
                    // an ArrayList of Strings
                    col_UnivData = col_UnivData.getColumnOfData();                       
                    col_al_Data.add(col_UnivData);
                }
                nLevels = col_al_Data.size();
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
    
    public void show_ANOVA1_NS_Dialog() {
        lblTitle.setText("One way ANOVA");
        stageDialog.setTitle("One way ANOVA");
        stageDialog.showAndWait();
    }
    
    public String getExplanatoryVariable() { return tfExplanVar.getText(); }
    public String getResponseVariable() { return tfResponseVar.getText(); } 
    public int getNLevels() { return nLevels; }
    public ArrayList<ColumnOfData> getData() { return col_al_Data; }
    public String getReturnStatus() { return strReturnStatus; }
} 


