/**************************************************
 *               Indep_t_TI8x_Dialog              *
 *                    02/15/25                    *
 *                     12:00                      *
 *************************************************/
package dialogs.t_and_z;

import dataObjects.ColumnOfData;
import dialogs.Splat_Dialog;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import splat.Data_Manager;
import splat.Var_List;
import utilityClasses.MyAlerts;

public class Indep_t_TI8x_Dialog extends Splat_Dialog{

    // POJOs
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
    private int nLevels;
    final int TWO;
    Separator sepTitle, sepDirections, sepButtons;
    
    Label lbFirstVar, lblSecondVar;
    TextField tfFirstVar, tfSecondVar;
    
    private ArrayList<Integer> al_IndexVarsSelected;    
    private ArrayList<String> al_QuantsSelected, str_ChosenLabels, preData;
    
    // My classes
    private ColumnOfData col_UnivData;
    private ArrayList<ColumnOfData> data;
    
    // FX objects
    Button resetButton, selectQuantVariable;
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
    public Indep_t_TI8x_Dialog(Data_Manager dm) {
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 65 Indep_t_TI8x_Dialog, constructing");
        }        
        TWO = 2;
        create_Indep_t_TI8x_Dialog();
    }

    private void create_Indep_t_TI8x_Dialog() {
        if (printTheStuff) {
            System.out.println("*** 73 Explore_2Ind_TI8x_Dialog, create_Indep_t_TI8x_Dialog()");
        }
        dm.whereIsWaldo(75, waldoFile, "create_Indep_t_TI8x_Dialog()");
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
        
        String directions = "Indep t: Quantitative variables are in separate columns.";
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
        
        lbFirstVar =   new Label("    First variable: ");
        lblSecondVar = new Label("   Second variable: ");
        
        tfFirstVar = new TextField("First variable");
        tfSecondVar = new TextField("Second variable");
        
        tfFirstVar.setPrefColumnCount(15);
        tfSecondVar.setPrefColumnCount(15);
        
        tfFirstVar.textProperty().addListener(this::changeFirstVar);
        tfSecondVar.textProperty().addListener(this::changeSecondVar);

        rightPanel = new GridPane();
        rightPanel.setHgap(10);
        rightPanel.setVgap(15);
        rightPanel.add(selectQuantVariable, 0, 0);
        rightPanel.add(vBoxList_2, 1, 0);
        rightPanel.add(lbFirstVar, 0, 3);
        rightPanel.add(lblSecondVar, 0, 4);
        rightPanel.add(tfFirstVar, 1, 3);
        rightPanel.add(tfSecondVar, 1, 4);
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
        resetButton = new Button("Reset");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, resetButton);

        mainPanel.getChildren().add(buttonPanel);
        myScene = new Scene(mainPanel);
        myScene.getStylesheets().add(strCSS);

        stageDialog = new Stage();
        stageDialog.setResizable(true);
        stageDialog.setScene(myScene);

        btnCancel.setStyle("-fx-text-fill: red;");
        btnCancel.setOnAction(e -> {  
            System.out.println("165 Indep_t_TI8x_Dlg, btnCancel clicked...");
            boolGoodToGo = false;
            strReturnStatus = "Cancel";
            hide();
        });

        resetButton.setOnAction((ActionEvent event) -> {
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
                    resetButton.fire();
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
              
            if (nLevels != TWO) {
                MyAlerts.showExplore2Ind_NE2_LevelsAlert();
                resetButton.fire();
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
                stageDialog.hide();
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

    public void show_Indep_t_2Ind_TI8x_Dialog() {
        lblTitle.setText("Explore 2 Vars");
        stageDialog.setTitle("Explore 2 Vars");
        stageDialog.showAndWait();
    }

    public String getFirstVariable() { return tfFirstVar.getText(); }
    public String getSecondVariable() { return tfSecondVar.getText(); } 
    public int getNLevels() { return nLevels; }
    public String getIthLabel(int ith) { return data.get(ith).getVarLabel(); }
    public String getIthDescription(int ith) { return data.get(ith).getVarDescription(); }
    public ColumnOfData getIthColumnOfData(int ithCol) {
        return data.get(ithCol);
    }
    public ArrayList<ColumnOfData> getData() { return data; }    
}
