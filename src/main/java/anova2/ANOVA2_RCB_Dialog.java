/**************************************************
 *             ANOVA2_RCB_Dialog                  *
 *                  09/12/24                      *
 *                   21:00                        *
 *************************************************/
package anova2;

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
import bivariateProcedures_Categorical.*;
import dialogs.Splat_Dialog;
import java.util.Collections;

public class ANOVA2_RCB_Dialog extends Splat_Dialog {
    //  POJOs
    private boolean isCodedData = false;    
    private boolean isReadyForAnalysis = false;
    private boolean designIsBalanced, thereAreReplications, dataAreMissing;

    private int index_FactorA, index_FactorB, index_ResponseVar, numCols, numRows;
    
    // String waldoFile = "ANOVA2_RCB_Dialog";
    String waldoFile = "";
    
    private String str_NameFactorA, str_NameFactorB, str_NameResponse, 
                   whichANOVA2;
    private String[] strFactorA_Labels, strFactorB_Labels;
    private String[][] str_Cells;
    private ArrayList<String>[][] al_Data;    
     
    // My classes
    ColumnOfData col_FactorA, col_FactorB;
    
    // POJOs / FX
    Button btnFactorA, btnFactorB, btnResponse;
    public CheckBox chBoxDoES, chBoxDoCrit, chBoxDoSupp, chBoxDoChart;
    Label lblFactorA, lblFactorB;
    Label lblTitle;
    
    private Scene sceneDialog;
    private Stage stageDialog;
    private TextField tf_FactorB, tf_FactorA, tf_ResponseVar;
    
    public ANOVA2_RCB_Dialog(Data_Manager dm, String whichANOVA2) {
        super(dm);
        dm.whereIsWaldo(65, waldoFile, "\nConstructing");
        this.whichANOVA2 = whichANOVA2; 
        strReturnStatus = "OK";
    }

    public String doTheDialog() { 
        dm.whereIsWaldo(71, waldoFile, "doTheDialog()");
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        stageDialog = new Stage();
        stageDialog.setScene(sceneDialog);
        
        stageDialog.setOnCloseRequest((WindowEvent we) -> {
            boolGoodToGo = false;
            strReturnStatus = "Cancel";
            close();
        });
        
        stageDialog.setTitle("Two-way ANOVA");
        showTheDialog();
        stageDialog.showAndWait();
        return "OK";
    } // showStep0
    
    public void showTheDialog() {
        dm.whereIsWaldo(95, waldoFile, "showTheDialog()");
        VBox mainPanel = new VBox(10);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPadding(new Insets(10, 10, 10, 10));
        lblTitle = new Label(""); //  Initializing to null
        lblTitle.getStyleClass().add("dialogTitle");
        Separator sepTitle = new Separator();
        
        mainPanel.getChildren().addAll(lblTitle, sepTitle);
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

        btnFactorA = new Button("===>");
        btnFactorB = new Button("===>");
        btnResponse = new Button("===>");

        VBox vBox_FactorA_Panel = new VBox();
        vBox_FactorA_Panel.setAlignment(Pos.TOP_LEFT);
        dm.whereIsWaldo(125, waldoFile, "doTheDialog()");
        
        switch(whichANOVA2) {
            case "Factorial":
                lblTitle = new Label("Two-way ANOVA: Factorial");
                lblFactorA = new Label("Factor A:");
            break;
        
            case "RCB":     // Randomized complete block
                lblTitle = new Label("Two-way ANOVA: Randomized Block");
                lblFactorA = new Label("  Factor:");
            break;
            
            default:
                String switchFailure = "Switch failure: ANOVA2_RCB_Dialog 139 " + whichANOVA2;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }   
        
        dm.whereIsWaldo(143, waldoFile, "doTheDialog()");
        
        lblFactorA.setPadding(new Insets(0, 0, 5, 0));
        tf_FactorA = new TextField("");
        tf_FactorA.setPrefWidth(125.0);
        vBox_FactorA_Panel.getChildren().addAll(lblFactorA, tf_FactorA);

        VBox vBox_FactorB_Panel = new VBox();
        vBox_FactorB_Panel.setAlignment(Pos.TOP_LEFT);
        
        switch(whichANOVA2) {
            case "Factorial":
                lblFactorB = new Label("Factor B:");
            break;
        
            case "RCB":     // Randomized complete block
                lblFactorB = new Label("  Block:");
            break;

            default:
                String switchFailure = "Switch failure:  ANOVA2_RCB_Dialog 163 " + whichANOVA2;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }
        
        dm.whereIsWaldo(167, waldoFile, "doTheDialog()");
        lblFactorB.setPadding(new Insets(0, 0, 5, 0));
        tf_FactorB = new TextField("");
        tf_FactorB.setPrefWidth(125.0);
        vBox_FactorB_Panel.getChildren().addAll(lblFactorB, tf_FactorB);

        VBox vBox_Response_Panel = new VBox();
        vBox_Response_Panel.setAlignment(Pos.TOP_LEFT);
        Label lbl_ResponseVar = new Label("Response Variable:");
        lbl_ResponseVar.setPadding(new Insets(0, 0, 5, 0));
        tf_ResponseVar = new TextField("");
        tf_ResponseVar.setPrefWidth(125.0);
        vBox_Response_Panel.getChildren().addAll(lbl_ResponseVar, tf_ResponseVar);
        
        GridPane gp_RightPanel = new GridPane();
        gp_RightPanel.setHgap(10);
        gp_RightPanel.setVgap(15);
        gp_RightPanel.add(btnFactorA, 0, 0);
        gp_RightPanel.add(vBox_FactorA_Panel, 1, 0);
        gp_RightPanel.add(btnFactorB, 0, 1);
        gp_RightPanel.add(vBox_FactorB_Panel, 1, 1);
        gp_RightPanel.add(btnResponse, 0, 2);
        gp_RightPanel.add(vBox_Response_Panel, 1, 2);
        GridPane.setValignment(btnFactorA, VPos.BOTTOM);
        GridPane.setValignment(btnFactorB, VPos.BOTTOM);
        GridPane.setValignment(btnResponse, VPos.BOTTOM);

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
        sceneDialog = new Scene(mainPanel);
        sceneDialog.getStylesheets().add(strCSS);
        
        sceneDialog.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                String kCode = ke.getCode().toString();
                if (kCode.equals("ESCAPE")) {
                    strReturnStatus = "Cancel";
                    stageDialog.close();
                }
            }
        });

        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            stageDialog.close();
        });

        btn_Reset.setOnAction((ActionEvent event) -> {
            varList1.resetList();
            tf_FactorA.setText("");
            tf_FactorB.setText("");
            tf_ResponseVar.setText("");
        });

        btnFactorA.setOnAction((ActionEvent event) -> {
            boolean ok = true;
            
            if (varList1.getNamesSelected().size() == 1) {
                str_NameFactorA = varList1.getNamesSelected().get(0);
                index_FactorA = dm.getVariableIndex(str_NameFactorA);
                int numGroups = dm.numDistinctVals(index_FactorA);
                
               if ((numGroups < 2) || (numGroups > 5)) {
                    MyAlerts.showRCB_2_5_VarAlert();
                    ok = false;
                } else {
                    tf_FactorA.setText(str_NameFactorA);
                    varList1.delVarName(varList1.getNamesSelected());
                }
            }
        });

        btnFactorB.setOnAction((ActionEvent event) -> {
            boolean ok = true;
            
            if (varList1.getNamesSelected().size() == 1) {
                str_NameFactorB = varList1.getNamesSelected().get(0);
                index_FactorB = dm.getVariableIndex(str_NameFactorB);
                int numGroups = dm.numDistinctVals(index_FactorB);
                
                if ((numGroups < 2) || (numGroups > 5)) {
                    MyAlerts.showRCB_2_5_VarAlert();
                    ok = false;
                } else {
                    tf_FactorB.setText(str_NameFactorB);
                    varList1.delVarName(varList1.getNamesSelected());
                }
            }
        });

        btnResponse.setOnAction((ActionEvent event) -> {            
            if (varList1.getNamesSelected().size() == 1) {
                str_NameResponse = varList1.getNamesSelected().get(0);
                index_ResponseVar = dm.getVariableIndex(str_NameResponse);
                
                if (!dm.getVariableIsNumeric(dm.getVariableIndex(str_NameResponse))) {
                    MyAlerts.showDataTypeErrorAlert();
                    btn_Reset.fire();
                } else {
                    tf_ResponseVar.setText(str_NameResponse);
                    varList1.delVarName(varList1.getNamesSelected());
                }
            }
        });

        btn_Compute.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(287, waldoFile, "btn_Compute");
            boolean ok = true;
            
            if ((tf_FactorA.getText().equals(""))
                    || (tf_FactorB.getText().equals(""))
                    || (tf_ResponseVar.getText().equals(""))) {
                ok = false;
                MyAlerts.showRCB_TooFewVarsAlert();                    
            }

            if (ok) {
                // ********************************************************
                col_FactorA = new ColumnOfData(dm.getAllTheColumns().get(index_FactorA));
                col_FactorB = new ColumnOfData(dm.getAllTheColumns().get(index_FactorB));

                BivCat_Model bivCatModel = new BivCat_Model( col_FactorA, col_FactorB, "RCB");
                designIsBalanced = bivCatModel.getDesignIsBalanced();
                thereAreReplications = bivCatModel.getThereAreReplications();
                dataAreMissing = bivCatModel.getDataAreMissing();
                // ********************************************************

                ArrayList<String> tmpCodes1;
                tmpCodes1 = getDummyCodes(index_FactorA);
                numRows = tmpCodes1.size();
                strFactorB_Labels = new String[numRows];
                
                for (int i = 0; i < numRows; i++) {
                    strFactorB_Labels[i] = tmpCodes1.get(i);
                }

                ArrayList<String> tmpCodes2;
                tmpCodes2 = getDummyCodes(index_FactorB);
                numCols = tmpCodes2.size();
                strFactorA_Labels = new String[numCols];

                for (int i = 0; i < numCols; i++) {
                    strFactorA_Labels[i] = tmpCodes2.get(i);
                }

                al_Data = new ArrayList[numCols][numRows];                
                for (int row = 0; row < numRows; row++) {                    
                    for (int col = 0; col < numCols; col++) {
                        al_Data[col][row] = new ArrayList();
                        for (int i = 0; i < dm.getSampleSize(index_ResponseVar); i++) {
                            if ((dm.getFromDataStruct(index_FactorA, i).equals(strFactorB_Labels[row]))
                                    && (dm.getFromDataStruct(index_FactorB, i).equals(strFactorA_Labels[col]))) {
                                al_Data[col][row].add(dm.getFromDataStruct(index_ResponseVar, i));
                            }
                        }
                    }
                }

                isReadyForAnalysis = true;
                isCodedData = true;
                stageDialog.close();
            }
        });

        stageDialog.setScene(sceneDialog);
        stageDialog.setTitle("Step #1");
        dm.whereIsWaldo(347, waldoFile, "End ShowStep1()");
    }
    
    public ArrayList<String> getDummyCodes(int groupingVar) {
        dm.whereIsWaldo(351, waldoFile, "getDummyCodes()");
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
    
    public boolean getDesignIsBalanced() { return designIsBalanced; } 
    public boolean getThereAreReplications() { return thereAreReplications; }
    public boolean getDataAreMissing() { return dataAreMissing; }
    public String getReturnStatus() { return strReturnStatus; }
    public int getNumRows() {return numRows; }
    public int getNumCols() { return numCols; }
    public ArrayList<String>[][] getData() {return al_Data; }
    public String getFactorA_Name() {return str_NameFactorA; }
    public String getFactorB_Name() { return str_NameFactorB; }
    public String getResponse_Name() { return str_NameResponse; }    
    public String[] getFactorA_Labels() {return strFactorB_Labels; }
    public String[] getFactorB_Labels() { return strFactorA_Labels; }
    public boolean amReady() { return isReadyForAnalysis; }
    public boolean isCoded () { return isCodedData; }
    public String[][] getCellLabels() { return str_Cells; }
}