/**************************************************
 *             PrintFile_ChooseVars_Dialog        *
 *                    05/13/24                    *
 *                     09:00                      *
 *************************************************/
package printFile;

import dataObjects.ColumnOfData;
import dialogs.MyDialogs;
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

public class PrintFile_ChooseVars_Dialog extends Splat_Dialog {
    // POJOs
    // private boolean runAnalysis = false;
    private boolean ok = true; 
    
    private int numSelected;
    private final int minVars = 2, maxIVs = 12;    
    private ArrayList<Integer> ivSelected; 

    private ArrayList<String> varLabel;    
    private ArrayList<String>[] alStr_DataValues;    
    
    // My classes
    ColumnOfData colOfData_Temp;
    Label lbl_Title;
    private MyDialogs msgDiag;    
    ArrayList<ColumnOfData> theChosenColumns;
    
    // POJOs / FX
    // private Stage dialogStage;

    public PrintFile_ChooseVars_Dialog(Data_Manager dm) {
        super(dm);
        // waldoFile = "PrintFile_ChooseVars_Dialog";
        waldoFile = "";
        dm.whereIsWaldo(54, waldoFile, "Constructing");
        msgDiag = new MyDialogs();
        ivSelected = new ArrayList();
        varLabel = new ArrayList();

        VBox vBox_MainPanel = new VBox();
        vBox_MainPanel.setAlignment(Pos.CENTER);

        lbl_Title = new Label("Print File");
        lbl_Title.getStyleClass().add("dialogTitle");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));
        Separator sepTitle = new Separator();
        vBox_MainPanel.getChildren().addAll(lbl_Title, sepTitle);

        HBox hBox_MiddlePanel = new HBox();
        hBox_MiddlePanel.setAlignment(Pos.CENTER);

        VBox vBox_VarList_ToChoose = new VBox();
        vBox_VarList_ToChoose.setAlignment(Pos.TOP_LEFT);
        Label lbl_ExplanVars = new Label("Variables in Data:");
        lbl_ExplanVars.setPadding(new Insets(0, 0, 5, 0));
        Var_List varList_ToChoose = new Var_List(dm, null, null);
        vBox_VarList_ToChoose.getChildren().add(lbl_ExplanVars);
        vBox_VarList_ToChoose.getChildren().add(varList_ToChoose.getPane());
        vBox_VarList_ToChoose.setPadding(new Insets(0, 10, 0, 10));
        hBox_MiddlePanel.getChildren().add(vBox_VarList_ToChoose);

        Button btn_selectIV = new Button("===>");

        VBox vBox_VarList_Chosen = new VBox();
        vBox_VarList_Chosen.setAlignment(Pos.TOP_LEFT);
        Label lbl_PredVars = new Label("Predictor Variables");
        lbl_PredVars.setPadding(new Insets(0, 0, 5, 0));
        Var_List varList_Chosen = new Var_List(dm, 125.0, 125.0);
        varList_Chosen.clearList();
        vBox_VarList_Chosen.getChildren().add(lbl_PredVars);
        vBox_VarList_Chosen.getChildren().add(varList_Chosen.getPane());

        VBox vBox_Resp_Var = new VBox();
        vBox_Resp_Var.setAlignment(Pos.TOP_LEFT);
        Label lbl_OutcomeVar = new Label("Outcome Variable");
        lbl_OutcomeVar.setPadding(new Insets(0, 0, 5, 0));
        TextField ivText = new TextField("");
        ivText.setPrefWidth(125.0);
        vBox_Resp_Var.getChildren().addAll(lbl_OutcomeVar, ivText);

        GridPane gridPane_RightPanel = new GridPane();
        gridPane_RightPanel.setHgap(10);
        gridPane_RightPanel.setVgap(15);
        //gridPane_RightPanel.add(btn_selectDV, 0, 0);
        gridPane_RightPanel.add(vBox_Resp_Var, 1, 0);
        gridPane_RightPanel.add(btn_selectIV, 0, 1);
        gridPane_RightPanel.add(vBox_VarList_Chosen, 1, 1);
        //GridPane.setValignment(btn_selectDV, VPos.BOTTOM);
        GridPane.setValignment(btn_selectIV, VPos.CENTER);
        gridPane_RightPanel.setPadding(new Insets(0, 10, 0, 0));

        hBox_MiddlePanel.getChildren().add(gridPane_RightPanel);
        hBox_MiddlePanel.setPadding(new Insets(10, 0, 10, 0));

        vBox_MainPanel.getChildren().add(hBox_MiddlePanel);
        Separator sepButtons = new Separator();
        vBox_MainPanel.getChildren().add(sepButtons);

        HBox hBox_Btn_Panel = new HBox(10);
        hBox_Btn_Panel.setAlignment(Pos.CENTER);
        hBox_Btn_Panel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        Button btn_Reset = new Button("Reset");
        hBox_Btn_Panel.getChildren().addAll(btnOK, btnCancel, btn_Reset);

        vBox_MainPanel.getChildren().add(hBox_Btn_Panel);
        Scene myScene = new Scene(vBox_MainPanel);
        myScene.getStylesheets().add(strCSS);

        setScene(myScene);

        btn_Reset.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(136, waldoFile, "btnReset.setOnAction");
            varList_ToChoose.resetList();
            varList_Chosen.clearList();
            ivText.setText("");
            ivSelected.clear();
            numSelected = 0;
        });

        btn_selectIV.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(141, waldoFile, "btn_selectIV.setOnAction");
            ArrayList<String> selected = varList_ToChoose.getNamesSelected();
            ok = true;
            //System.out.println("144 PrintFile_ChooseVars_Dialog, selected = " + selected);
            
            int tempNum = varList_Chosen.getVarIndices().size();

            if ((ok) && (tempNum < maxIVs)) {
                varList_Chosen.addVarName(selected);
                varList_ToChoose.delVarName(selected);
            }
        });
        
        btnOK.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(159, waldoFile, "okButton.setOnAction");
            ok = true;

            ivSelected = varList_Chosen.getVarIndices();
            numSelected = ivSelected.size();
            //int dvPresent = 0;

            /*
            if ((numSelected + dvPresent) < minVars) {
                msgDiag.NoChoiceMessage(2, "Variable Selection",
                        "You must select at least " + minVars + " variable(s) to analyze.");
                ok = false;
            }
            */
            
            dm.whereIsWaldo(170, waldoFile, "okButton.setOnAction");
            theChosenColumns = new ArrayList<>();

            
            if (ok) {
                alStr_DataValues = new ArrayList[numSelected];
                //alStr_YValues = new ArrayList();
                
                dm.whereIsWaldo(178, waldoFile, "okButton.setOnAction");
                for (int j = 0; j < numSelected; j++) {   
                    //System.out.println("176 PrintFile_ChooseVars_Dialog, j / get(j) = " + j + " / " + ivSelected.get(j));
                    varLabel.add(dm.getVariableName(ivSelected.get(j)));
                    alStr_DataValues[j] = new ArrayList();
                    alStr_DataValues[j] = dm.getSpreadsheetColumnAsStrings(ivSelected.get(j), -1, null);
                    
                    for (int ithJ = 0; ithJ < alStr_DataValues[j].size(); ithJ++) {
                        //System.out.println("182 PrintFile_ChooseVars_Dialog, alStr_DataValues[j] = " + alStr_DataValues[j].get(ithJ));
                    }
                    
                    colOfData_Temp = new ColumnOfData(dm, dm.getVariableName(ivSelected.get(j)), "PrintFileDial", alStr_DataValues[j]);
                    //System.out.println("86 PrintFile_ChooseVars_Dialog");
                    theChosenColumns.add(colOfData_Temp);
                }
                dm.whereIsWaldo(189, waldoFile, "okButton.setOnAction");
                strReturnStatus = "Ok";
                close();
            }
        });

        setTitle("Choose Variables");


    } // twoSampDialog
    
    public void setTitleLabel (String toThis) { lbl_Title.setText(toThis);}        
    public ArrayList<ColumnOfData> getData() {  return theChosenColumns; }
    public ArrayList<String> getVarLabels() { return varLabel; }
    public int getNumVars() { return numSelected; }
    
    public boolean getOK() {return true; }
} // class

