/************************************************************
 *                    Two-Variables_Dialog                  *
 *                          08/21/24                        *
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
    
    private String strSelected, strSubTitle; //, callingProc;
    String strDataType_1, strDataType_2;
    public String strNullChangeQuery;
    private ArrayList<String> str_al_VarLabels;
    
    //public String waldoFile = "Two_Variables_Dialog";
    public String waldoFile  = "";
    
    // My classes
    private ArrayList<ColumnOfData> col_al;
    protected Var_List var_List;    
    
    // POJOs / FX
    private final Button btnSelect_X_Var;
    private final Button btnSelect_Y_Var;
    protected Button btnReset;
    protected CheckBox[] chBoxDashBoardOptions;
    protected GridPane gridPaneChoicesMade;
    private final HBox hBox_Middle_Panel; //, hBox_Toggle;
    private final VBox vBoxMainPanel; 
    private final VBox vBoxVars2ChooseFrom;
    private final VBox vBox_X_VarChoices;
    private final VBox vBox_Y_VarChoices;
    protected VBox vBox_LeftPanel, vBox_RightPanel;
    protected Label lblTitle, lblFirstVar, lblSecondVar; 
    private final Label lblVarsInData;
    protected TextField tf_FirstVarLabel_InFile, tf_SecondVarLabel_InFile;
    protected TextField tf_PreferredFirstVarDescription, tf_PreferredSecondVarDescription;
    protected Label lblExplanVar, lblResponseVar;
    
    public Two_Variables_Dialog(Data_Manager dm,  String callingProc, String procInfo) {
        super(dm);
        this.dm = dm;;
        dm.whereIsWaldo(70, waldoFile, "\nConstructing: callingProc = " + callingProc);
        dm.whereIsWaldo(71, waldoFile, "\nConstructing: procInfo = " + procInfo);
        
        switch (callingProc) {
            case "LogRegDialog":
            case "Explore_2Ind_Dialog":
            case "ANOVA1_Quant_Stacked":
            case "RegressionDialog":
            case "Indep_t_Dialog":
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
            
            case "ANOVA1_Cat_Stacked":
            case "Explore_2Ind_Stacked":
            case "MultUni_Stacked_Dialog":
            case "Ind_t_Stacked_Dialog":
                strDataType_1 = "Categorical";
                strDataType_2 = "Quantitative";    
                break;                                
            
            case "Logistic_Dialog":
                strDataType_1 = "Quantitative";
                strDataType_2 = "OK";   //  Type could be cat or quant  
                break;               
            
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog 106 " + callingProc;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);           
        }

        strReturnStatus = "OK";
        boolQuantLabelChecked = false;
        boolGoodToGo = true;
        col_al = new ArrayList<>();   
        str_al_VarLabels = new ArrayList<>();
        lblTitle = new Label("Two-variable dialog");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        strNullChangeQuery = "What!?!?  My hypothesized null difference of zero is not good"
                            + "\nenough for you? It is pretty unusual that a non-zero difference"
                            + "\nwould be hypothesized, but I'm willing to believe you know what"
                            + "\nyou're doing. No skin off MY nose...";
        
        boolQuantLabelChecked = false;
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

        vBox_X_VarChoices = new VBox();
        vBox_X_VarChoices.setAlignment(Pos.TOP_LEFT);
        lblFirstVar = new Label();
        lblFirstVar.setPadding(new Insets(0, 0, 5, 0));
        tf_FirstVarLabel_InFile = new TextField("");
        tf_FirstVarLabel_InFile.setPrefWidth(125.0);
        vBox_X_VarChoices.getChildren().addAll(lblFirstVar, tf_FirstVarLabel_InFile);

        vBox_Y_VarChoices = new VBox();
        vBox_Y_VarChoices.setAlignment(Pos.TOP_LEFT);
        lblSecondVar = new Label();
        lblSecondVar.setPadding(new Insets(0, 0, 5, 0));
        tf_SecondVarLabel_InFile = new TextField("");
        tf_SecondVarLabel_InFile.setPrefWidth(125.0);
        vBox_Y_VarChoices.getChildren().addAll(lblSecondVar, tf_SecondVarLabel_InFile);

        switch (callingProc) {
            case "RegressionDialog":
            case "Logistic_Dialog":
            case "ANOVA1_Cat_Stacked":
            case "ANOVA1_Quant_Stacked":
                lblExplanVar =   new Label(" Explanatory variable: ");
                lblResponseVar = new Label("    Response variable: ");
                tf_PreferredFirstVarDescription = new TextField("Explanatory variable");
                tf_PreferredSecondVarDescription = new TextField("Response variable");
                break;
            case "EpiDialog":
                lblExplanVar =   new Label(" Exposure variable: ");
                lblResponseVar = new Label("  Outcome variable: ");
                tf_PreferredFirstVarDescription = new TextField("Exposure variable");
                tf_PreferredSecondVarDescription = new TextField("Outcome variable");
                break;
            default:
                lblExplanVar =   new Label("  First variable: ");
                lblResponseVar = new Label(" Second variable: ");
                tf_PreferredFirstVarDescription = new TextField("First variable");
                tf_PreferredSecondVarDescription = new TextField("Second variable");
                break;            
        }
        
        tf_PreferredFirstVarDescription.setPrefColumnCount(15);
        tf_PreferredSecondVarDescription.setPrefColumnCount(15);

        tf_PreferredFirstVarDescription.textProperty().addListener(this::changeFirstVar);
        tf_PreferredSecondVarDescription.textProperty().addListener(this::changeSecondVar);

        gridPaneChoicesMade = new GridPane();
        gridPaneChoicesMade.setHgap(10);
        gridPaneChoicesMade.setVgap(15);
        gridPaneChoicesMade.add(btnSelect_X_Var, 0, 0);
        gridPaneChoicesMade.add(vBox_X_VarChoices, 1, 0);
        gridPaneChoicesMade.add(btnSelect_Y_Var, 0, 1);
        gridPaneChoicesMade.add(vBox_Y_VarChoices, 1, 1);
        
        if (!callingProc.equals("Ind_t_Stacked_Dialog") && (!callingProc.equals("Explore_2Ind_Stacked"))) {
            gridPaneChoicesMade.add(lblExplanVar, 0, 3);
            gridPaneChoicesMade.add(lblResponseVar, 0, 4);
            tf_PreferredFirstVarDescription.setText(" ");
            tf_PreferredSecondVarDescription.setText(" ");
            gridPaneChoicesMade.add(tf_PreferredFirstVarDescription, 1, 3);
            gridPaneChoicesMade.add(tf_PreferredSecondVarDescription, 1, 4);
        }
        
        GridPane.setValignment(btnSelect_X_Var, VPos.BOTTOM);
        GridPane.setValignment(btnSelect_Y_Var, VPos.BOTTOM);
        gridPaneChoicesMade.setPadding(new Insets(0, 10, 0, 0));

        vBox_LeftPanel = new VBox(10);
        vBox_LeftPanel.setAlignment(Pos.CENTER_LEFT);
        vBox_LeftPanel.setPadding(new Insets(0, 25, 0, 10));
        
        vBox_RightPanel = new VBox(10);
        vBox_RightPanel.setAlignment(Pos.CENTER_LEFT);
        vBox_RightPanel.setPadding(new Insets(0, 25, 0, 10));
        vBox_RightPanel.getChildren().add(gridPaneChoicesMade);
        
        hBox_Middle_Panel = new HBox();
        hBox_Middle_Panel.setAlignment(Pos.CENTER);
        hBox_Middle_Panel.getChildren().add(vBox_LeftPanel);     
        hBox_Middle_Panel.getChildren().add(vBoxVars2ChooseFrom);
        hBox_Middle_Panel.getChildren().add(vBox_RightPanel);
        hBox_Middle_Panel.setPadding(new Insets(10, 0, 10, 0));

        HBox hBoxButtonPanel = new HBox(10);
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
        
        btnReset.setOnAction((ActionEvent event) -> {
            var_List.resetList();
            tf_FirstVarLabel_InFile.setText("");
            tf_SecondVarLabel_InFile.setText("");
            tf_PreferredFirstVarDescription.setText("");
            tf_PreferredSecondVarDescription.setText("");
            varIndexFor_X = -1;
            varIndexFor_Y = -1;
        });
        
        btnSelect_X_Var.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(253, waldoFile, "selectXVariable");
            if (var_List.getNamesSelected().size() == 1) {
                dm.whereIsWaldo(255, waldoFile, "XvarsSelected = 1");
                String tempIndicator = var_List.getNamesSelected().get(0);
                tf_FirstVarLabel_InFile.setText(tempIndicator);
                var_List.delVarName(var_List.getNamesSelected());
                strSelected = tf_FirstVarLabel_InFile.getText();
                varIndexFor_X = dm.getVariableIndex(strSelected);
                bool_X_VarType_Ok = checkDataType(1, strDataType_1);
                
                if (!isCorrectType) { btnReset.fire(); }
            }
        });
        
        btnSelect_Y_Var.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(268, waldoFile, "selectYVariable");
            if (var_List.getNamesSelected().size() == 1) {
                dm.whereIsWaldo(271, waldoFile, "YvarsSelected = 1");
                String tempIndicator = var_List.getNamesSelected().get(0);
                tf_SecondVarLabel_InFile.setText(tempIndicator);
                var_List.delVarName(var_List.getNamesSelected());
                strSelected = tf_SecondVarLabel_InFile.getText();
                varIndexFor_Y = dm.getVariableIndex(strSelected);
                bool_Y_VarType_Ok = checkDataType(2, strDataType_2);
                dm.whereIsWaldo(278, waldoFile, "YvarsSelected = 1");
                if (!isCorrectType) { btnReset.fire(); }
                dm.whereIsWaldo(280, waldoFile, "YvarsSelected = 1");
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(283, waldoFile, "okButton.setOnAction()");
            boolGoodToGo = true;
            strSelected = tf_FirstVarLabel_InFile.getText();
            varIndexFor_X = dm.getVariableIndex(strSelected);

            //  Check that both variables have been selected
            if (varIndexFor_X == -1) {
                MyAlerts.showFewerThanTwoVariablesAlert();
                btnReset.fire();
                boolGoodToGo = false;
            }
            
            if (boolGoodToGo) {
                strSelected = tf_SecondVarLabel_InFile.getText();
                varIndexFor_Y = dm.getVariableIndex(strSelected);
                
                if ((varIndexFor_Y == -1) && (varIndexFor_X > -1)) {
                   MyAlerts.showFewerThanTwoVariablesAlert();
                   btnReset.fire();
                   boolGoodToGo = false;
                }
            }

            if (boolGoodToGo) {
                if (strDataType_1.equals("Categorical")) {
                    ColumnOfData col_x = dm.getAllTheColumns().get(varIndexFor_X);
                    col_x.cleanTheColumn(dm, varIndexFor_X);
                }

                if (strDataType_2.equals("Categorical")) {
                    ColumnOfData col_y = dm.getAllTheColumns().get(varIndexFor_Y);
                    col_y.cleanTheColumn(dm, varIndexFor_Y);
                }  
            }
            
            if ((varIndexFor_X > -1 && varIndexFor_Y > -1)){
                str_al_VarLabels.add(dm.getVariableName(varIndexFor_X));
                col_al.add(dm.getSpreadsheetColumn(varIndexFor_X));    
                str_al_VarLabels.add(dm.getVariableName(varIndexFor_Y));
                col_al.add(dm.getSpreadsheetColumn(varIndexFor_Y));
            }
            else 
            {
                boolGoodToGo = false; 
            }
            
            if (!boolGoodToGo) { 
                strReturnStatus = "Cancel";
            } else {
                strReturnStatus = "OK"; 
            }

            if(boolGoodToGo) {
                String explanVarLabel, explanVarDescr, 
                       responseVarLabel, responseVarDescr;
                
                explanVarLabel = tf_FirstVarLabel_InFile.getText();
                responseVarLabel = tf_SecondVarLabel_InFile.getText();
                explanVarDescr =  tf_PreferredFirstVarDescription.getText();
                responseVarDescr =  tf_PreferredSecondVarDescription.getText();
                
                if (StringUtilities.stringIsEmpty(explanVarDescr) || StringUtilities.stringIsEmpty(responseVarDescr))  {
                    strSubTitle = responseVarLabel  + " vs. " + explanVarLabel;  
                    setPreferredFirstVarDescription(explanVarLabel);
                    setPreferredSecondVarDescription(responseVarLabel);
                } else {
                    strSubTitle = responseVarDescr  + " vs. " + explanVarDescr; 
                }     
                strReturnStatus = "OK";
                close();
            }
        });     
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
        isCorrectType = true;
        dm.whereIsWaldo(373, waldoFile, "checkDataType");
        switch (variableNowChecking) {
            case 1:
                strSelected = tf_FirstVarLabel_InFile.getText();
                break;
                
            case 2:
                strSelected = tf_SecondVarLabel_InFile.getText();
                break;
                
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog 383 " + variableNowChecking;
                dm.whereIsWaldo(385, waldoFile, "checkDataType");
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        
        varIndex = dm.getVariableIndex(strSelected);    
        String colDataType = dm.getAllTheColumns().get(varIndex).getDataType();
 
        isCorrectType = true;
        dm.whereIsWaldo(394, waldoFile, "checkDataType");
        if (strDataType.equals("OK")) {
            isCorrectType = true;
            return isCorrectType;
        }
        dm.whereIsWaldo(400, waldoFile, "checkDataType");
        if (colDataType.equals("Quantitative") && !strDataType.equals("Quantitative")) {
            isCorrectType = false;
            MyAlerts.showInappropriateNumericVariableAlert();
        }
        dm.whereIsWaldo(406, waldoFile, "checkDataType");
        if (!colDataType.equals("Quantitative") && strDataType.equals("Quantitative")) {
            isCorrectType = false;
            MyAlerts.showInappropriateNonNumericVariableAlert();
        } 
        dm.whereIsWaldo(409, waldoFile, "checkDataType");      
        return isCorrectType;
    }
    
    public ArrayList<ColumnOfData> getData() { return col_al; }
    public String getFirstVarLabel_InFile() { return tf_FirstVarLabel_InFile.getText(); }
    public String getSecondVarLabel_InFile() {  return tf_SecondVarLabel_InFile.getText(); }    
    public String getPreferredFirstVarDescription() {  return tf_PreferredFirstVarDescription.getText(); }
    public void setPreferredFirstVarDescription(String toThis) { tf_PreferredFirstVarDescription.setText(toThis); }
    public String getPreferredSecondVarDescription() {  return tf_PreferredSecondVarDescription.getText(); }
    public void setPreferredSecondVarDescription(String toThis) { tf_PreferredSecondVarDescription.setText(toThis); }
    public String getSubTitle() {  return strSubTitle; }
}
