/**************************************************
 *            Regr_Compare_Dialog                 *
 *                  11/27/24                      *
 *                   12:00                        *
 *************************************************/
package simpleRegression;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utilityClasses.*;
import splat.*;
import dataObjects.*;
import dialogs.Splat_Dialog;
import java.util.Collections;

public class Regr_Compare_Dialog extends Splat_Dialog {
    //  POJOs
    private boolean codedData = false;    
    private boolean readyForAnalysis = false;
    private boolean designIsBalanced, thereAreReplications, dataAreMissing;

    private int index_Treatment, index_Covariate, index_ResponseVar, numCols, numRows;
    
    //String waldoFile = "ANCOVA_Dialog";
    String waldoFile = "";
    
    private String str_NameTreatment, str_NameCovariate, str_NameResponse;
    private String[] str_Treatment_Label;
    private String[][] str_Cells;
    private ArrayList<ColumnOfData> al_Data;  
    ArrayList<String> originalLevels;
     
    // My classes
    ColumnOfData colOfData_Treatment, colOfData_Covariate, colOfData_Response;
    
    // POJOs / FX
    Button arrow_SelectFactorA, arrow_SelectFactorB, arrow_SelectResponse;
    public CheckBox doES, doCrit, doSupp, doChart;
    Label lbl_Treatment, lbl_FactorB;
    Label titleLabel;
    
    private Scene diagScene1;
    private Stage diagStage;
    private TextField tf_FactorB, tf_FactorA, tf_ResponseVar;
    
    public Regr_Compare_Dialog(Data_Manager dm) {
        super(dm);
        this.dm = dm;
        strReturnStatus = "OK";
    }

    public String doTheDialog() { 
        dm.whereIsWaldo(69, waldoFile, "doTheDialog()");
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert_1Var();
            return "Cancel";
        }
        
        diagStage = new Stage();
        diagStage.setScene(diagScene1);
        
        diagStage.setOnCloseRequest((WindowEvent we) -> {
            boolGoodToGo = false;
            strReturnStatus = "Cancel";
            close();
        });
        
        diagStage.setTitle("Analysis of Covariance");
        showTheDialog();
        diagStage.showAndWait();
        return "OK";
    } // showStep0
    
    public void showTheDialog() {
        dm.whereIsWaldo(93, waldoFile, "showTheDialog()");
        VBox mainPanel = new VBox(10);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPadding(new Insets(10, 10, 10, 10));
        titleLabel = new Label(""); //  Initializing to null
        titleLabel.getStyleClass().add("dialogTitle");
        Separator sepTitle = new Separator();
        
        mainPanel.getChildren().addAll(titleLabel, sepTitle);
        HBox hBox_MiddlePanel = new HBox(15);
        hBox_MiddlePanel.setAlignment(Pos.CENTER);
        VBox vBox_LeftPanel = new VBox(10);
        vBox_LeftPanel.setAlignment(Pos.CENTER_LEFT);

        VBox vList1 = new VBox();
        vList1.setAlignment(Pos.TOP_LEFT);
        Label vlable1 = new Label("Variables in Data:");
        vlable1.setPadding(new Insets(0, 0, 5, 0));
        Var_List varList1 = new Var_List(dm, null, null);
        vList1.getChildren().add(vlable1);
        vList1.getChildren().add(varList1.getPane());
        vList1.setPadding(new Insets(0, 10, 0, 10));
        hBox_MiddlePanel.getChildren().add(vList1);

        arrow_SelectFactorA = new Button("===>");
        arrow_SelectFactorB = new Button("===>");
        arrow_SelectResponse = new Button("===>");

        VBox vBox_FactorA_Panel = new VBox();
        vBox_FactorA_Panel.setAlignment(Pos.TOP_LEFT);
        dm.whereIsWaldo(123, waldoFile, "doTheDialog()");
        
        titleLabel = new Label("Analysis of Covariance");
        lbl_Treatment = new Label("Treatment:");
        dm.whereIsWaldo(127, waldoFile, "doTheDialog()");
        
        lbl_Treatment.setPadding(new Insets(0, 0, 5, 0));
        tf_FactorA = new TextField("");
        tf_FactorA.setPrefWidth(125.0);
        vBox_FactorA_Panel.getChildren().addAll(lbl_Treatment, tf_FactorA);

        VBox vBox_FactorB_Panel = new VBox();
        vBox_FactorB_Panel.setAlignment(Pos.TOP_LEFT);
        lbl_FactorB = new Label("Explanatory:");
        
        dm.whereIsWaldo(138, waldoFile, "doTheDialog()");
        lbl_FactorB.setPadding(new Insets(0, 0, 5, 0));
        tf_FactorB = new TextField("");
        tf_FactorB.setPrefWidth(125.0);
        vBox_FactorB_Panel.getChildren().addAll(lbl_FactorB, tf_FactorB);

        VBox vBox_Response_Panel = new VBox();
        vBox_Response_Panel.setAlignment(Pos.TOP_LEFT);
        Label lbl_ResponseVar = new Label("Response:");
        lbl_ResponseVar.setPadding(new Insets(0, 0, 5, 0));
        tf_ResponseVar = new TextField("");
        tf_ResponseVar.setPrefWidth(125.0);
        vBox_Response_Panel.getChildren().addAll(lbl_ResponseVar, tf_ResponseVar);
        
        GridPane gp_RightPanel = new GridPane();
        gp_RightPanel.setHgap(10);
        gp_RightPanel.setVgap(15);
        gp_RightPanel.add(arrow_SelectFactorA, 0, 0);
        gp_RightPanel.add(vBox_FactorA_Panel, 1, 0);
        gp_RightPanel.add(arrow_SelectFactorB, 0, 1);
        gp_RightPanel.add(vBox_FactorB_Panel, 1, 1);
        gp_RightPanel.add(arrow_SelectResponse, 0, 2);
        gp_RightPanel.add(vBox_Response_Panel, 1, 2);
        GridPane.setValignment(arrow_SelectFactorA, VPos.BOTTOM);
        GridPane.setValignment(arrow_SelectFactorB, VPos.BOTTOM);
        GridPane.setValignment(arrow_SelectResponse, VPos.BOTTOM);

        hBox_MiddlePanel.getChildren().add(gp_RightPanel);

        mainPanel.getChildren().add(hBox_MiddlePanel);
        Separator sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);

        HBox hBox_BtnPanel = new HBox(10);
        hBox_BtnPanel.setAlignment(Pos.CENTER);

        Button btn_Compute = new Button("Compute");
        btnCancel = new Button("Cancel");
        Button btn_Reset = new Button("Reset");
        hBox_BtnPanel.getChildren().addAll(btn_Compute, btnCancel, btn_Reset);

        mainPanel.getChildren().add(hBox_BtnPanel);
        diagScene1 = new Scene(mainPanel);
        diagScene1.getStylesheets().add(strCSS);
        
        diagScene1.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                String kCode = ke.getCode().toString();
                if (kCode.equals("ESCAPE")) {
                    strReturnStatus = "Cancel";
                    diagStage.close();
                }
            }
        });

        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            diagStage.close();
        });

        btn_Reset.setOnAction((ActionEvent event) -> {
            varList1.resetList();
            tf_FactorA.setText("");
            tf_FactorB.setText("");
            tf_ResponseVar.setText("");
        });

        arrow_SelectFactorA.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(207, waldoFile, "showTheDialog()");
            str_NameTreatment = varList1.getNamesSelected().get(0);
            index_Treatment = dm.getVariableIndex(str_NameTreatment);
            
            if (dm.getAllTheColumns().get(index_Treatment).getIsNumeric()) {
                MyAlerts.showANCOVA_NumericTreatmentAlert();
                strReturnStatus = "Cancel";
                diagStage.close();
            } 
            
            int numGroups = dm.numDistinctVals(index_Treatment); 
            if (numGroups < 2) {
                MyAlerts.showCompare_Regr_LT2_LevelsAlert();
                strReturnStatus = "Cancel";
                diagStage.close();
            } else {
                tf_FactorA.setText(str_NameTreatment);
                varList1.delVarName(varList1.getNamesSelected());
            }    
        });

        arrow_SelectFactorB.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(229, waldoFile, "showTheDialog()");
            str_NameCovariate = varList1.getNamesSelected().get(0);
            index_Covariate = dm.getVariableIndex(str_NameCovariate);
            if (dm.getAllTheColumns().get(index_Covariate).getIsNumeric()) {
                tf_FactorB.setText(str_NameCovariate);
                varList1.delVarName(varList1.getNamesSelected());
            } else {
                MyAlerts.showRegrCompare_nonNumericExplanVarAlert();
                strReturnStatus = "Cancel";
                diagStage.close();
            }
        });

        arrow_SelectResponse.setOnAction((ActionEvent event) -> {    
            dm.whereIsWaldo(243, waldoFile, "showTheDialog()");
            str_NameResponse = varList1.getNamesSelected().get(0);
            index_ResponseVar = dm.getVariableIndex(str_NameResponse);                
            if (dm.getAllTheColumns().get(index_ResponseVar).getIsNumeric()){
                tf_ResponseVar.setText(str_NameResponse);
                varList1.delVarName(varList1.getNamesSelected());
            } else {
                MyAlerts.showRegrCompare_nonNumericResponseVarAlert();
                strReturnStatus = "Cancel";
                diagStage.close();
            }
        });

        btn_Compute.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(257, waldoFile, "btn_Compute");
            boolean treatmentIsEmpty = (tf_FactorA.getText()).isEmpty();
            boolean covariateIsEmpty = (tf_FactorB.getText()).isEmpty();
            boolean responseIsEmpty = (tf_ResponseVar.getText()).isEmpty();

            if (treatmentIsEmpty || covariateIsEmpty || responseIsEmpty){
               MyAlerts.showANCOVA_missingChoicesAlert();
               strReturnStatus = "Cancel";
               diagStage.close();
            } else {
                dm.whereIsWaldo(267, waldoFile, "showTheDialog()");
 
                colOfData_Treatment = new ColumnOfData(dm.getAllTheColumns().get(index_Treatment));
                colOfData_Covariate = new ColumnOfData(dm.getAllTheColumns().get(index_Covariate));
                colOfData_Response = new ColumnOfData(dm.getAllTheColumns().get(index_ResponseVar));

                originalLevels = getDummyCodes(index_Treatment);
                numRows = originalLevels.size();
               
                al_Data = new ArrayList();   
                al_Data.add(colOfData_Covariate);
                al_Data.add(colOfData_Response);
                al_Data.add(colOfData_Treatment);

                readyForAnalysis = true;
                codedData = true;
                diagStage.close();
            }
        });

        diagStage.setScene(diagScene1);
        diagStage.setTitle("Step #1");
        dm.whereIsWaldo(289, waldoFile, "End ShowStep1()");
    }
    
    public ArrayList<String> getDummyCodes(int groupingVar) {
        dm.whereIsWaldo(293, waldoFile, "getDummyCodes()");
        ArrayList<String> alstr_SortedTempCodes = new ArrayList();
        ArrayList<String> alstr_DumsToReturn = new ArrayList();
        ArrayList<String> alstr_TempData = dm.getSpreadsheetColumnAsStrings(groupingVar, -1, null);

        for (int ithDumDum = 0; ithDumDum < alstr_TempData.size(); ithDumDum++) {
            alstr_SortedTempCodes.add(alstr_TempData.get(ithDumDum));
        }

        Collections.sort(alstr_SortedTempCodes);

        alstr_DumsToReturn.add(alstr_SortedTempCodes.get(0));        
        for (int i = 1; i < alstr_TempData.size(); i++) {            
            if (!alstr_SortedTempCodes.get(i).equals(alstr_SortedTempCodes.get(i - 1))) {
                alstr_DumsToReturn.add(alstr_SortedTempCodes.get(i));
            }
        }
        return alstr_DumsToReturn;
    } 
    
    public ArrayList<String> getOriginalLevels() { return originalLevels; }
    public boolean getDesignIsBalanced() { return designIsBalanced; } 
    public boolean getThereAreReplications() { return thereAreReplications; }
    public boolean getDataAreMissing() { return dataAreMissing; }
    public String getReturnStatus() { return strReturnStatus; }
    public int getNumRows() {return numRows; }
    public int getNumCols() { return numCols; }
    public ArrayList<ColumnOfData> getData() {return al_Data; }
    public String getTreatment_Name() {return str_NameTreatment; }
    public String getCovariate_Name() { return str_NameCovariate; }
    public String getResponse_Name() { return str_NameResponse; }    
    public String[] getTreatment_Labels() {return str_Treatment_Label; }
    public boolean amReady() { return readyForAnalysis; }
    public boolean isCoded () { return codedData; }
    public String[][] getCellLabels() { return str_Cells; }
}
