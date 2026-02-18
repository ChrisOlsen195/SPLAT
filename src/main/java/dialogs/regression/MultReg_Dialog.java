/**************************************************
 *                 MultReg_Dialog                 *
 *                    12/13/25                    *
 *                     15:00                      *
 *************************************************/
package dialogs.regression;

import dataObjects.ColumnOfData;
import dialogs.Splat_Dialog;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import splat.*;
import utilityClasses.MyAlerts;

public class MultReg_Dialog extends Splat_Dialog {
    // POJOs
    private boolean ok = true; 
    private boolean rowIsLegal;
    private int nIVsSelected, nRows, nCols, nLegalRows;
    private final int maxIVs;    
    private int dvSelected = -1;    
    private ArrayList<Integer> str_al_IVsSelected; 

    private String strDVText; 
    private ArrayList<String> str_al_YValues, strVarLabels;    
    private ArrayList<String>[] str_al_DataValues;    
    
    // My classes
    ColumnOfData colTemp;
    Label lblTitle;   
    ArrayList<ColumnOfData> theMultRegData;
    
    // POJOs / FX

    public MultReg_Dialog(Data_Manager dm) {
        super(dm);
        //waldoFile = "MultReg_Dialog";
        waldoFile = "";
        this.dm = dm;
        
        dm.whereIsWaldo(52, waldoFile, "Constructing");
        maxIVs = 12;
        str_al_IVsSelected = new ArrayList();
        strVarLabels = new ArrayList();

        VBox vBoxMainPanel = new VBox();
        vBoxMainPanel.setAlignment(Pos.CENTER);

        lblTitle = new Label("Multiple Regression");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        Separator sepTitle = new Separator();
        vBoxMainPanel.getChildren().addAll(lblTitle, sepTitle);

        HBox hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setAlignment(Pos.CENTER);

        VBox vBoxVarList_ToChoose = new VBox();
        vBoxVarList_ToChoose.setAlignment(Pos.TOP_LEFT);
        Label lblExplanVars = new Label("Variables in Data:");
        lblExplanVars.setPadding(new Insets(0, 0, 5, 0));
        Var_List var_List_ToChoose = new Var_List(dm, null, null);
        vBoxVarList_ToChoose.getChildren().add(lblExplanVars);
        vBoxVarList_ToChoose.getChildren().add(var_List_ToChoose.getPane());
        vBoxVarList_ToChoose.setPadding(new Insets(0, 10, 0, 10));
        hBoxMiddlePanel.getChildren().add(vBoxVarList_ToChoose);

        Button btn_selectDV = new Button("===>");
        Button btn_selectIV = new Button("===>");

        VBox vBoxVarList_Chosen = new VBox();
        vBoxVarList_Chosen.setAlignment(Pos.TOP_LEFT);
        Label lbl_PredVars = new Label("Predictor Variables");
        lbl_PredVars.setPadding(new Insets(0, 0, 5, 0));
        Var_List var_List_Chosen = new Var_List(dm, 125.0, 125.0);
        var_List_Chosen.clearList();
        vBoxVarList_Chosen.getChildren().add(lbl_PredVars);
        vBoxVarList_Chosen.getChildren().add(var_List_Chosen.getPane());

        VBox vBoxResp_Var = new VBox();
        vBoxResp_Var.setAlignment(Pos.TOP_LEFT);
        Label lblOutcomeVar = new Label("Outcome Variable");
        lblOutcomeVar.setPadding(new Insets(0, 0, 5, 0));
        TextField tfIVText = new TextField("");
        tfIVText.setPrefWidth(125.0);
        vBoxResp_Var.getChildren().addAll(lblOutcomeVar, tfIVText);

        GridPane gridPane_RightPanel = new GridPane();
        gridPane_RightPanel.setHgap(10);
        gridPane_RightPanel.setVgap(15);
        gridPane_RightPanel.add(btn_selectDV, 0, 0);
        gridPane_RightPanel.add(vBoxResp_Var, 1, 0);
        gridPane_RightPanel.add(btn_selectIV, 0, 1);
        gridPane_RightPanel.add(vBoxVarList_Chosen, 1, 1);
        GridPane.setValignment(btn_selectDV, VPos.BOTTOM);
        GridPane.setValignment(btn_selectIV, VPos.CENTER);
        gridPane_RightPanel.setPadding(new Insets(0, 10, 0, 0));

        hBoxMiddlePanel.getChildren().add(gridPane_RightPanel);
        hBoxMiddlePanel.setPadding(new Insets(10, 0, 10, 0));

        vBoxMainPanel.getChildren().add(hBoxMiddlePanel);
        Separator sepButtons = new Separator();
        vBoxMainPanel.getChildren().add(sepButtons);

        HBox hBoxBtnPanel = new HBox(10);
        hBoxBtnPanel.setAlignment(Pos.CENTER);
        hBoxBtnPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        Button btnReset = new Button("Reset");
        hBoxBtnPanel.getChildren().addAll(btnOK, btnCancel, btnReset);

        vBoxMainPanel.getChildren().add(hBoxBtnPanel);
        Scene myScene = new Scene(vBoxMainPanel);
        myScene.getStylesheets().add(strCSS);

        setScene(myScene);

        btnReset.setOnAction((ActionEvent event) -> {;
            var_List_ToChoose.resetList();
            var_List_Chosen.clearList();
            tfIVText.setText("");
            str_al_IVsSelected.clear();
            nIVsSelected = 0;
        });

        btn_selectIV.setOnAction((ActionEvent event) -> {
            ArrayList<String> selected = var_List_ToChoose.getNamesSelected();
            ok = true;
            
            for (String tmpVar : selected) {
                
                if (!dm.getDataType(dm.getVariableIndex(tmpVar)).equals("Quantitative")) {
                    MyAlerts.showInappropriateNonNumericVariableAlert();
                    btnReset.fire();
                    ok = false;
                }
            }

            int tempNum = var_List_Chosen.getVarIndices().size();

            if ((ok) && (tempNum < maxIVs)) {
                var_List_Chosen.addVarName(selected);
                var_List_ToChoose.delVarName(selected);
            }
        });

        btn_selectDV.setOnAction((ActionEvent event) -> {
            ok = true;
            
            if (var_List_ToChoose.getNamesSelected().size() == 1) {
                strDVText = var_List_ToChoose.getNamesSelected().get(0);
                dvSelected = dm.getVariableIndex(strDVText);
                tfIVText.setText(strDVText);
                var_List_ToChoose.delVarName(var_List_ToChoose.getNamesSelected());
            }
            
            if (!dm.getDataType(dvSelected).equals("Quantitative")) {
                MyAlerts.showInappropriateNonNumericVariableAlert();
                btnReset.fire();
                ok = false;
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            ok = true;

            str_al_IVsSelected = var_List_Chosen.getVarIndices();
            nIVsSelected = str_al_IVsSelected.size();
            int dvPresent = 0;
            
            if (dvSelected > -1) { dvPresent = 1; }

            theMultRegData = new ArrayList<>();

            if (ok) {
                str_al_DataValues = new ArrayList[nIVsSelected];
                str_al_YValues = new ArrayList();
                str_al_YValues = dm.getSpreadsheetColumnAsStrings(dvSelected, -1, null);
                strVarLabels.add(dm.getVariableName(dvSelected));
                
                // The label for the variable and the ArrayList of strings
                colTemp = new ColumnOfData(dm, dm.getVariableName(dvSelected), "MultRegDial", str_al_YValues);
                
                theMultRegData.add(colTemp);
                
                for (int j = 0; j < nIVsSelected; j++) {                    
                    strVarLabels.add(dm.getVariableName(str_al_IVsSelected.get(j)));
                    str_al_DataValues[j] = new ArrayList();
                    str_al_DataValues[j] = dm.getSpreadsheetColumnAsStrings(str_al_IVsSelected.get(j), -1, null);
                    colTemp = new ColumnOfData(dm, dm.getVariableName(str_al_IVsSelected.get(j)), "MultRegDial", str_al_DataValues[j]);
                    theMultRegData.add(colTemp);
                }

                nRows = theMultRegData.get(0).getNCasesInColumn();
                nCols = theMultRegData.size();
                nLegalRows = 0;
                for (int ithRow = 0; ithRow < nRows; ithRow++) {
                    rowIsLegal = true;
                    for (int ithCol = 0; ithCol < nCols; ithCol++) {
                        if (theMultRegData.get(ithCol).getIthCase(ithRow).equals("*")) {
                            rowIsLegal = false;
                        }
                    }
                    if (rowIsLegal) {nLegalRows++; }
                }

                if (nLegalRows - nIVsSelected - 2 < 1) {
                    MyAlerts.showMultReg_TooFewRowsAlert();
                    btnReset.fire();
                } else {
                    strReturnStatus = "Ok";
                    close();
                }
            }
        });

        setTitle("Multiple Regression");
    } 
    
    public void setTitleLabel (String toThis) { lblTitle.setText(toThis);}    
    public String getYVariable() { return strDVText; };    
    public ArrayList<ColumnOfData> getData() {  return theMultRegData; }
    public ArrayList<String> getVarLabels() { return strVarLabels; }
    public ArrayList<String>[] getXMatrix() { return str_al_DataValues; }
    public ArrayList<String> getYMatrix() { return str_al_YValues; }
    public int getNumVars() { return nIVsSelected; }

    public boolean getOK() {return true; }
} 

