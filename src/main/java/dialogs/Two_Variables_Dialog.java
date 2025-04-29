/************************************************************
 *                    Two-Variables_Dialog                  *
 *                          04/16/25                        *
 *                           00:00                          *
 ***********************************************************/
package dialogs;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
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
import splat.Data_Manager;
import splat.Var_List;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class Two_Variables_Dialog extends Splat_Dialog {
    // POJOs
    private boolean boolQuantLabelChecked, bool_X_VarType_Ok, bool_Y_VarType_Ok; 
    boolean isCorrectType;
    
    private int varIndex, varIndexFor_X, varIndexFor_Y;
    protected int nCheckBoxes;
    
    private String strSelected, strSubTitle; 
    String xLabelFromFile, yLabelFromFile;
    private String strReturnStatusX, strReturnStatusY, strReturnStatus;
    String strDataType_1, strDataType_2;
    String callingProcedure, procInfo;
    public String strNullChangeQuery;
    private ArrayList<String> str_al_VarLabels;
    
    //public String waldoFile = "Two_Variables_Dialog";
    public String waldoFile  = "";
    
    // My classes
    private ArrayList<ColumnOfData> al_ColOfData;
    protected Var_List var_List;    
    
    // POJOs / FX
    private Button btnSelect_X_Var;
    private Button btnSelect_Y_Var;
    protected Button btnReset;
    protected CheckBox[] chBoxDashBoardOptions;
    CheckBox cbxSaveTheResids, cbxSaveTheHats;
    protected GridPane gridPaneChoicesMade;
    private HBox hBox_Middle_Panel, hBoxButtonPanel;
    private VBox vBoxMainPanel; 
    private VBox vBoxVars2ChooseFrom;
    private VBox vBox_X_VarChoice;
    private VBox vBox_Y_VarChoice;
    protected VBox vBoxLeftPanel, vBoxRightPanel;
    protected Label lblTitle; //, lblFirstVar, lblSecondVar; 
    private Label lblVarsInData;
    protected TextField tf_Var_1_InFile, tf_Var_2_InFile;
    protected TextField tf_Var_1_Pref, tf_Var_2_Pref;
    protected Label lblExplanVar, lblResponseVar;
    
    public Two_Variables_Dialog(Data_Manager dm,  String callingProcedure, String procInfo) {
        super(dm);
        this.dm = dm;
        this.callingProcedure = callingProcedure;
        this.procInfo = procInfo;
        dm.whereIsWaldo(75, waldoFile, " *** Constructing: callingProcedure = " + callingProcedure);
        dm.whereIsWaldo(76, waldoFile, " *** Constructing: procInfo = " + procInfo);
        strReturnStatus = "OK";
        strReturnStatusX = "OK";
        strReturnStatusY = "OK";
        dm.whereIsWaldo(80, waldoFile, " *** Constructing: strReturnStatus = " + strReturnStatus);
        strReturnStatus = doTheRest();
        dm.whereIsWaldo(82, waldoFile, " *** Constructing: strReturnStatus = " + strReturnStatus);
    }

    private String doTheRest() {
        dm.whereIsWaldo(86, waldoFile, " --- doTheRest()");
        dm.whereIsWaldo(87, waldoFile, " --- callingProcedure = " + callingProcedure);
        switch (callingProcedure) {
            case "Explore_2Ind_Dialog":
            case "ANOVA1_Quant_Tidy":
            case "Indep_t_ti8x":
            case "MatchedPairs_Dialog":
                strDataType_1 = "Quantitative";
                strDataType_2 = "Quantitative";    
                break;
            
            case "BivCatAssoc":
            case "BivCatDialog":
            case "EpiDialog":
            case "X2Assoc_Dialog":
                strDataType_1 = "Categorical";
                strDataType_2 = "Categorical";  
                break;             
            
            case "ANOVA1_Cat_Tidy":
            case "Explore_2Ind_Tidy":
            case "MultUni_Tidy_Dialog":
            case "Indep_t_tidy":
                strDataType_1 = "Categorical";
                strDataType_2 = "Quantitative";    
                break;                                
            
            case "Logistic_Dialog":
                strDataType_1 = "Quantitative";
                strDataType_2 = "OK";   //  Type could be cat or quant  
                break;               
            
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog 120: " + callingProcedure;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);           
        }

        strReturnStatus = "OK";
        dm.whereIsWaldo(125, waldoFile, " strReturnStatus = " + strReturnStatus);
        dm.whereIsWaldo(126, waldoFile, " strDataType_1 = " + strDataType_1);
        dm.whereIsWaldo(127, waldoFile, " strDataType_2 = " + strDataType_2);
        boolQuantLabelChecked = false;
        boolGoodToGo = true;
        al_ColOfData = new ArrayList<>();   
        str_al_VarLabels = new ArrayList<>();
        lblTitle = new Label("Two-variable dialog");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        strNullChangeQuery = "What!?!?  My hypothesized null difference of zero is not good"
                            + "\nenough for you? It is pretty unusual that a non-zero difference"
                            + "\nwould be hypothesized, but I'm willing to believe you know what"
                            + "\nyou're doing. No skin off MY nose...";
        
        //boolQuantLabelChecked = false;
        vBoxVars2ChooseFrom = new VBox();
        vBoxVars2ChooseFrom.setAlignment(Pos.TOP_LEFT);
        lblVarsInData = new Label("Variables in File:");
        lblVarsInData.setPadding(new Insets(0, 0, 5, 0));
        var_List = new Var_List(dm, null, null);
        vBoxVars2ChooseFrom.getChildren().add(lblVarsInData);
        vBoxVars2ChooseFrom.getChildren().add(var_List.getPane());
        vBoxVars2ChooseFrom.setPadding(new Insets(0, 10, 0, 10));
        
        btnSelect_X_Var = new Button("===>");
        btnSelect_Y_Var = new Button("===>");

        vBox_X_VarChoice = new VBox();
        vBox_X_VarChoice.setAlignment(Pos.TOP_LEFT);
        lblExplanVar = new Label();
        lblExplanVar.setPadding(new Insets(0, 0, 5, 0));       
        
        tf_Var_1_InFile = new TextField("");
        tf_Var_1_InFile.setPrefWidth(125.0);
        vBox_X_VarChoice.getChildren().addAll(lblExplanVar, tf_Var_1_InFile);

        vBox_Y_VarChoice = new VBox();
        vBox_Y_VarChoice.setAlignment(Pos.TOP_LEFT);
        lblResponseVar = new Label();
        lblResponseVar.setPadding(new Insets(0, 0, 5, 0));       
        
        tf_Var_2_InFile = new TextField("");
        tf_Var_2_InFile.setPrefWidth(125.0);
        vBox_Y_VarChoice.getChildren().addAll(lblResponseVar, tf_Var_2_InFile);

        switch (callingProcedure) {
            case "Logistic_Dialog":
            case "ANOVA1_Cat_Tidy":
            case "ANOVA1_Quant_Tidy":
                dm.whereIsWaldo(175, waldoFile, " --- case 1:");
                lblExplanVar =   new Label(" Explanatory variable: ");
                lblResponseVar = new Label("    Response variable: ");
                tf_Var_1_Pref = new TextField("Explanatory variable");
                tf_Var_2_Pref = new TextField("Response variable");
                break;
            case "EpiDialog":
                dm.whereIsWaldo(182, waldoFile, " --- case 2:");
                lblExplanVar =   new Label(" Exposure variable: ");
                lblResponseVar = new Label("  Outcome variable: ");
                tf_Var_1_Pref = new TextField(" Exposure variable: ");
                tf_Var_2_Pref = new TextField("  Outcome variable: ");
                break;
            case "Indep_t_tidy":
                dm.whereIsWaldo(189, waldoFile, " --- case 3:");
                lblExplanVar =   new Label("Group/Treat var: ");
                lblResponseVar = new Label("   Response var: ");
                tf_Var_1_Pref = new TextField("Group/Treat var: ");
                tf_Var_2_Pref = new TextField("   Response var: ");
                break;
            case "BivCatDialog":
                dm.whereIsWaldo(196, waldoFile, " --- case 4:");
                lblExplanVar =   new Label("  First variable: ");
                lblResponseVar = new Label(" Second variable: ");
                tf_Var_1_Pref = new TextField("Group/Treat var: ");
                tf_Var_2_Pref = new TextField("   Response var: ");
                break;
            default:
                dm.whereIsWaldo(203, waldoFile, " --- case 5:");
                lblExplanVar =   new Label("  First variable: ");
                lblResponseVar = new Label(" Second variable: ");
                tf_Var_1_Pref = new TextField("First variable");
                tf_Var_2_Pref = new TextField("Second variable");
                break;            
        }
        
        tf_Var_1_Pref.setPrefColumnCount(15);
        tf_Var_2_Pref.setPrefColumnCount(15);

        tf_Var_1_Pref.textProperty().addListener(this::changeFirstVar);
        tf_Var_2_Pref.textProperty().addListener(this::changeSecondVar);

        gridPaneChoicesMade = new GridPane();
        gridPaneChoicesMade.setHgap(10);
        gridPaneChoicesMade.setVgap(15);
        Label lblTopExplanVar = new Label("Explanatory variable");
        gridPaneChoicesMade.add(lblTopExplanVar, 1, 0);
        gridPaneChoicesMade.add(btnSelect_X_Var, 0, 1);
        gridPaneChoicesMade.add(vBox_X_VarChoice, 1, 1);
        gridPaneChoicesMade.add(btnSelect_Y_Var, 0, 3);
        gridPaneChoicesMade.add(vBox_Y_VarChoice, 1, 3);
        Label lblTopResponseVar = new Label("Response variable");
        gridPaneChoicesMade.add(lblTopResponseVar, 1, 2);
        gridPaneChoicesMade.add(lblExplanVar, 0, 4);
        gridPaneChoicesMade.add(lblResponseVar, 0, 5);
        tf_Var_1_Pref.setText(" ");
        tf_Var_2_Pref.setText(" ");
        gridPaneChoicesMade.add(tf_Var_1_Pref, 1, 4);
        gridPaneChoicesMade.add(tf_Var_2_Pref, 1, 5);
        
        GridPane.setValignment(btnSelect_X_Var, VPos.BOTTOM);
        GridPane.setValignment(btnSelect_Y_Var, VPos.BOTTOM);
        GridPane.setValignment(lblExplanVar, VPos.TOP);
        GridPane.setValignment(lblResponseVar, VPos.TOP);
        //GridPane.setValignment(lblExplanVar_top, VPos.BOTTOM);
        //GridPane.setValignment(lblResponseVar_top, VPos.BOTTOM);
        GridPane.setValignment(tf_Var_1_InFile, VPos.TOP);
        GridPane.setValignment(tf_Var_2_InFile, VPos.TOP);
        gridPaneChoicesMade.setPadding(new Insets(0, 10, 0, 0));

        vBoxLeftPanel = new VBox(10);
        vBoxLeftPanel.setAlignment(Pos.CENTER_LEFT);
        vBoxLeftPanel.setPadding(new Insets(0, 25, 0, 10));
        
        vBoxRightPanel = new VBox(10);
        vBoxRightPanel.setAlignment(Pos.CENTER_LEFT);
        vBoxRightPanel.setPadding(new Insets(0, 25, 0, 10));
        vBoxRightPanel.getChildren().add(gridPaneChoicesMade);
        
        hBox_Middle_Panel = new HBox();
        hBox_Middle_Panel.setAlignment(Pos.CENTER);
        hBox_Middle_Panel.getChildren().add(vBoxLeftPanel);     
        hBox_Middle_Panel.getChildren().add(vBoxVars2ChooseFrom);
        hBox_Middle_Panel.getChildren().add(vBoxRightPanel);
        hBox_Middle_Panel.setPadding(new Insets(0, 0, 10, 0));

        hBoxButtonPanel = new HBox(10);
        hBoxButtonPanel.setAlignment(Pos.CENTER);
        hBoxButtonPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        hBoxButtonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
        
        vBoxMainPanel = new VBox();
        vBoxMainPanel.setAlignment(Pos.CENTER);    
        Separator sepTitle = new Separator();
        vBoxMainPanel.getChildren().addAll(lblTitle, sepTitle);    
        vBoxMainPanel.getChildren().add(hBox_Middle_Panel);
        Separator sepButtons = new Separator();
        vBoxMainPanel.getChildren().add(sepButtons);    
        vBoxMainPanel.getChildren().add(hBoxButtonPanel);
        
        Scene myScene = new Scene(vBoxMainPanel);
        myScene.getStylesheets().add(strCSS);
        setScene(myScene);
        
        btnCancel.setStyle("-fx-text-fill: red;");
        btnCancel.setOnAction(e -> {  
            boolGoodToGo = false;
            strReturnStatus = "Cancel";
            hide();
        });
        
        btnReset.setStyle("-fx-text-fill: black;");
        btnReset.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(287, waldoFile, "btnReset");
            var_List.resetList();
            tf_Var_1_InFile.setText("");
            tf_Var_2_InFile.setText("");
            tf_Var_1_Pref.setText("");
            tf_Var_2_Pref.setText("");
            varIndexFor_X = -1;
            varIndexFor_Y = -1;
        });
        
        btnSelect_X_Var.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(298, waldoFile, "selectXVariable");
            if (var_List.getNamesSelected().size() == 1) {
                dm.whereIsWaldo(300, waldoFile, "XvarsSelected = 1");
                xLabelFromFile = var_List.getNamesSelected().get(0);
                
                tf_Var_1_InFile.setText(xLabelFromFile);
                var_List.delVarName(var_List.getNamesSelected());
                strSelected = tf_Var_1_InFile.getText();
                varIndexFor_X = dm.getVariableIndex(strSelected);
                bool_X_VarType_Ok = checkDataType(1, strDataType_1);
                
                if (!isCorrectType) { btnReset.fire(); }
            }
        });
        
        btnSelect_Y_Var.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(314, waldoFile, "selectYVariable");
            if (var_List.getNamesSelected().size() == 1) {
                dm.whereIsWaldo(316, waldoFile, "YvarsSelected = 1");
                yLabelFromFile = var_List.getNamesSelected().get(0);
                
                tf_Var_2_InFile.setText(yLabelFromFile);
                var_List.delVarName(var_List.getNamesSelected());
                strSelected = tf_Var_2_InFile.getText();
                varIndexFor_Y = dm.getVariableIndex(strSelected);
                bool_Y_VarType_Ok = checkDataType(2, strDataType_2);
                
                if (!isCorrectType) { btnReset.fire(); }
            }
        });

        btnOK.setStyle("-fx-text-fill: black;");
        btnOK.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(330, waldoFile, "okButton");
            boolGoodToGo = true;
            strSelected = tf_Var_1_InFile.getText();
            varIndexFor_X = dm.getVariableIndex(strSelected);

            //  Check that both variables have been selected
            if (varIndexFor_X == -1) {
                MyAlerts.showFewerThanTwoVariablesAlert();
                btnReset.fire();
                boolGoodToGo = false;
            }
            dm.whereIsWaldo(341, waldoFile, " strReturnStatus = " + strReturnStatus);
            dm.whereIsWaldo(342, waldoFile, " boolGoodToGo = " + boolGoodToGo);
            if (boolGoodToGo) {
                strSelected = tf_Var_2_InFile.getText();
                varIndexFor_Y = dm.getVariableIndex(strSelected);
                
                if ((varIndexFor_Y == -1) && (varIndexFor_X > -1)) {
                   MyAlerts.showFewerThanTwoVariablesAlert();
                   btnReset.fire();
                   boolGoodToGo = false;
                }
            }
            dm.whereIsWaldo(353, waldoFile, " strReturnStatus = " + strReturnStatus);
            dm.whereIsWaldo(354, waldoFile, " boolGoodToGo = " + boolGoodToGo);
            if (boolGoodToGo) {
                if (strDataType_1.equals("Categorical")) {
                    ColumnOfData col_x = dm.getAllTheColumns().get(varIndexFor_X);
                    col_x.cleanTheColumn(dm, varIndexFor_X);
                    strReturnStatusX = col_x.getReturnStatus();
                    dm.whereIsWaldo(360, waldoFile, " strReturnStatusX = " + strReturnStatusX);
                }
                if (strReturnStatusX.equals("OK") &&strDataType_2.equals("Categorical")) {
                    ColumnOfData col_y = dm.getAllTheColumns().get(varIndexFor_Y);
                    col_y.cleanTheColumn(dm, varIndexFor_Y);
                    strReturnStatusX = col_y.getReturnStatus();
                    dm.whereIsWaldo(366, waldoFile, " strReturnStatusY = " + strReturnStatusY);
                }  
            }
            dm.whereIsWaldo(369, waldoFile, " strReturnStatus = " + strReturnStatus);            
            if ((varIndexFor_X > -1 && varIndexFor_Y > -1)){
                str_al_VarLabels.add(dm.getVariableName(varIndexFor_X));
                al_ColOfData.add(dm.getSpreadsheetColumn(varIndexFor_X));    
                str_al_VarLabels.add(dm.getVariableName(varIndexFor_Y));
                al_ColOfData.add(dm.getSpreadsheetColumn(varIndexFor_Y));
            }
            else 
            {
                boolGoodToGo = false; 
            }
            dm.whereIsWaldo(380, waldoFile, " boolGoodToGo = " + boolGoodToGo);
            if (!boolGoodToGo) { 
                strReturnStatus = "Cancel";
                dm.whereIsWaldo(383, waldoFile, " strReturnStatus = " + strReturnStatus);
            } else {
                strReturnStatus = "OK"; 
                dm.whereIsWaldo(36, waldoFile, " strReturnStatus = " + strReturnStatus);
            }
            dm.whereIsWaldo(388, waldoFile, " boolGoodToGo = " + boolGoodToGo);
            if(boolGoodToGo) {
                String explanVarLabel, explanVarDescr, 
                       responseVarLabel, responseVarDescr;
                dm.whereIsWaldo(392, waldoFile, " strReturnStatus = " + strReturnStatus);
                explanVarLabel = tf_Var_1_InFile.getText();
                responseVarLabel = tf_Var_2_InFile.getText();
                explanVarDescr =  tf_Var_1_Pref.getText();
                responseVarDescr =  tf_Var_2_Pref.getText();
                
                if (StringUtilities.stringIsEmpty(explanVarDescr) || StringUtilities.stringIsEmpty(responseVarDescr))  {
                    strSubTitle = responseVarLabel  + " vs. " + explanVarLabel;  
                    setPreferredFirstVarDescription(explanVarLabel);
                    setPreferredSecondVarDescription(responseVarLabel);
                } else {
                    strSubTitle = responseVarDescr  + " vs. " + explanVarDescr; 
                }     
                strReturnStatus = "OK";
                dm.whereIsWaldo(406, waldoFile, " strReturnStatus = " + strReturnStatus);
                hide();
            }
        });  
        

        dm.whereIsWaldo(412, waldoFile, " strReturnStatus = " + strReturnStatus);
        dm.whereIsWaldo(413, waldoFile, " --- END doTheRest()");
        return strReturnStatus;
    }
    
    public void changeFirstVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        setPreferredFirstVarDescription(newValue);
    }

    public void changeSecondVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        setPreferredSecondVarDescription(newValue); 
    }
    
    public boolean checkDataType(int variableNowChecking, String strDataType) {
        dm.whereIsWaldo(430, waldoFile, " --- checkDataType()");
        isCorrectType = true;
        
        switch (variableNowChecking) {
            case 1:
                strSelected = tf_Var_1_InFile.getText();
                break;
                
            case 2:
                strSelected = tf_Var_2_InFile.getText();
                break;
                
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog 443; checking Type of " + variableNowChecking;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        
        varIndex = dm.getVariableIndex(strSelected);    
        String colDataType = dm.getAllTheColumns().get(varIndex).getDataType();
        dm.whereIsWaldo(449, waldoFile, " --- TwoVarsDlg, colDataType = " + colDataType);
        isCorrectType = true;
        dm.whereIsWaldo(451, waldoFile, " --- TwoVarsDlg, isCorrectType = " + isCorrectType);
        if (colDataType.equals("Quantitative") && !strDataType.equals("Quantitative")) {
            isCorrectType = false;
            MyAlerts.showInappropriateNumericVariableAlert();
        }
        
        if (!colDataType.equals("Quantitative") && strDataType.equals("Quantitative")) {
            isCorrectType = false;
            MyAlerts.showInappropriateNonNumericVariableAlert();
        }    
        dm.whereIsWaldo(461, waldoFile, " --- checkDataType(), isCorrectType = " + isCorrectType);
        return isCorrectType;
    }
    
    public String getReturnStatus() { 
        if (strReturnStatusX.equals("Cancel")
                || strReturnStatusY.equals("Cancel")) 
                   {strReturnStatus = "Cancel"; }
        return strReturnStatus; }
    
    public String getXLabel() { return xLabelFromFile; }
    public String getYLabel() { return yLabelFromFile; }
    public String getReturnStatusX() { return strReturnStatusX;}
    public String getReturnStatusY() { return strReturnStatusY;}
    public ArrayList<ColumnOfData> getData() { return al_ColOfData; }
    public String getFirstVarLabel_InFile() { return tf_Var_1_InFile.getText(); }
    public String getSecondVarLabel_InFile() {  return tf_Var_2_InFile.getText(); }    
    public String getPreferredFirstVarDescription() {  return tf_Var_1_Pref.getText(); }
    public void setPreferredFirstVarDescription(String toThis) { tf_Var_1_Pref.setText(toThis); }
    public String getPreferredSecondVarDescription() {  return tf_Var_2_Pref.getText(); }
    public void setPreferredSecondVarDescription(String toThis) { tf_Var_2_Pref.setText(toThis); }
    public String getSubTitle() {  return strSubTitle; }
}
