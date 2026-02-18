/************************************************************
 *                     TwoVars_Dialog_One                   *
 *                          12/12/25                        *
 *                           12:00                          *
 ***********************************************************/
package dialogs;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import splat.Data_Manager;
import splat.Var_List;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class TwoVars_Dialog_One extends Splat_Dialog {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private boolean boolQuantLabelChecked, bool_X_VarType_Ok, bool_Y_VarType_Ok; 
    boolean isCorrectType;
    
    private int varIndex, varIndexFor_X, varIndexFor_Y;
    protected int nCheckBoxes;
    
    private String strSelected, strSubTitle; 
    String xLabelFromFile, yLabelFromFile;
    public String strReturnStatus_X, strReturnStatus_Y;
    String strDataType_1, strDataType_2;
    String callingProcedure, procInfo;
    public String strNullChangeQuery;
    private ArrayList<String> str_al_VarLabels;
    
    Label lblExplanPref,lblResponsePref;
    public String strSaveTheResids, strSaveTheHats;
    CheckBox cbxSaveTheResids, cbxSaveTheHats;
    
    double height_VBoxes = 400.;
    double width_MainHBox = 50.0;
    
    //public String waldoFile = "Two_Variables_Dialog_One";
    public String waldoFile  = "";
    
    // My classes
    private ArrayList<ColumnOfData> al_ColOfData;
    protected Var_List var_List;    
    
    // POJOs / FX
    private AnchorPane vBox_Left, vBox_Middle, vBox_Right, hBoxButtonPanel;
    private Button btnSelect_X_Var, btnSelect_Y_Var, btnReset;
    protected CheckBox[] chBoxDashBoardOptions;
    private HBox hBox_Middle_Panel; 
    private VBox vBoxMainPanel, vBox_X_VarChoice, vBox_Y_VarChoice;
    Label lblTopExplanVar, lblTopResponseVar; 
    protected Label lblTitle, lblVarsInData;
    protected TextField tf_Var_1_InFile, tf_Var_2_InFile,
                        tf_Var_1_Pref, tf_Var_2_Pref;
    protected Label lblExplanVar, lblResponseVar;
    
    public TwoVars_Dialog_One(Data_Manager dm,  String callingProcedure, String procInfo) {
        super(dm);
        if (printTheStuff) {
            System.out.println("*** 77 TwoVars_Dialog_One, Constructing");
        }
        this.dm = dm;
   
        this.callingProcedure = callingProcedure;
        this.procInfo = procInfo;
        strReturnStatus = "OK";
        strReturnStatus_X = "OK";
        strReturnStatus_Y = "OK";
        lblExplanPref = new Label("Your preference for");
        lblResponsePref = new Label("Your preference for");
        strDataType_1 = "Quantitative";
        strDataType_2 = "Quantitative";
        doTheXYVars();
        doResidsAndHats();
        strReturnStatus = doTheRest();
    }

    private String doTheRest() {
        if (printTheStuff) {
            System.out.println("*** 97 TwoVars_Dialog_One, doTheRest()");
        }
        switch (callingProcedure) {
            case "RegressionDialog":
                break;
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog 103: " + callingProcedure;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);           
        }

        strReturnStatus = "OK";
        boolGoodToGo = true;
        al_ColOfData = new ArrayList<>();   
        str_al_VarLabels = new ArrayList<>();
        lblTitle = new Label("Two-variable dialog");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));

        vBox_Left = new AnchorPane();
        vBox_Left.setMinHeight(height_VBoxes);
        vBox_Left.setMaxHeight(height_VBoxes);
        
        lblVarsInData = new Label("Variables in File:");
        var_List = new Var_List(dm, null, null);
        AnchorPane.setTopAnchor(lblVarsInData, 25.0);
 
        AnchorPane.setTopAnchor(var_List.getPane(), 75.0);

        vBox_Left.getChildren().addAll(lblVarsInData, var_List.getPane());
        
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
            case "RegressionDialog":
                lblExplanVar =   new Label(" Explanatory variable: ");
                lblResponseVar = new Label("    Response variable: ");
                tf_Var_1_Pref = new TextField("Explanatory variable");
                tf_Var_2_Pref = new TextField("Response variable");
                break;
            default:
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
        
        tf_Var_1_Pref.setText(" ");
        tf_Var_2_Pref.setText(" ");
        
        lblTopExplanVar = new Label("Explanatory variable");
        lblTopResponseVar = new Label("Response variable");
        
        vBox_Middle = new AnchorPane();
        vBox_Middle.setMinHeight(height_VBoxes);
        vBox_Middle.setMaxHeight(height_VBoxes);
        
        AnchorPane.setTopAnchor(btnSelect_X_Var, 50.0);
        AnchorPane.setTopAnchor(btnSelect_Y_Var, 125.0);
        AnchorPane.setTopAnchor(lblExplanPref, 180.0);
        AnchorPane.setTopAnchor(lblExplanVar, 195.0);
        AnchorPane.setTopAnchor(lblResponsePref, 240.0);
        AnchorPane.setTopAnchor(lblResponseVar, 255.0);
        AnchorPane.setTopAnchor(cbxSaveTheResids, 300.0);
        AnchorPane.setTopAnchor(cbxSaveTheHats, 325.0);
        
        vBox_Middle.getChildren().addAll(btnSelect_X_Var, btnSelect_Y_Var,
                                         lblExplanPref, lblExplanVar, 
                                         lblResponsePref, lblResponseVar,
                                         cbxSaveTheResids, cbxSaveTheHats);
        
        vBox_Right = new AnchorPane();
        vBox_Right.setMinHeight(height_VBoxes);
        vBox_Right.setMaxHeight(height_VBoxes);
        
        AnchorPane.setTopAnchor(lblTopExplanVar, 18.0);
        AnchorPane.setTopAnchor(vBox_X_VarChoice, 25.0);
        AnchorPane.setTopAnchor(lblTopResponseVar, 92.0);
        AnchorPane.setTopAnchor(vBox_Y_VarChoice, 100.0);
        AnchorPane.setTopAnchor(tf_Var_1_Pref, 190.0);
        AnchorPane.setTopAnchor(tf_Var_2_Pref, 250.0);       
        
        vBox_Right.getChildren().addAll(lblTopExplanVar, vBox_X_VarChoice,
                                        lblTopResponseVar,vBox_Y_VarChoice,
                                        tf_Var_1_Pref, tf_Var_2_Pref);

        hBox_Middle_Panel = new HBox(width_MainHBox);
        hBox_Middle_Panel.getChildren().addAll(vBox_Left, vBox_Middle, vBox_Right);
        
        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        hBoxButtonPanel = new AnchorPane();
        AnchorPane.setLeftAnchor(btnOK, 100.0);
        AnchorPane.setLeftAnchor(btnCancel, 250.0);
        AnchorPane.setLeftAnchor(btnReset, 400.0);
        
        hBoxButtonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
        
        vBoxMainPanel = new VBox();
        vBoxMainPanel.setAlignment(Pos.CENTER); 
        vBoxMainPanel.setPadding(new Insets(15, 15, 15, 15));
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
            var_List.resetList();
            tf_Var_1_InFile.setText("");
            tf_Var_2_InFile.setText("");
            tf_Var_1_Pref.setText("");
            tf_Var_2_Pref.setText("");
            varIndexFor_X = -1;
            varIndexFor_Y = -1;
        });
        
        btnSelect_X_Var.setOnAction((ActionEvent event) -> {
            if (var_List.getNamesSelected().size() == 1) {
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
            if (var_List.getNamesSelected().size() == 1) {
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
            boolGoodToGo = true;
            strSelected = tf_Var_1_InFile.getText();
            varIndexFor_X = dm.getVariableIndex(strSelected);

            //  Check that both variables have been selected
            if (varIndexFor_X == -1) {
                MyAlerts.showFewerThanTwoVariablesAlert();
                btnReset.fire();
                boolGoodToGo = false;
            }
            if (boolGoodToGo) {
                strSelected = tf_Var_2_InFile.getText();
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
                    strReturnStatus_X = col_x.getReturnStatus();
                    dm.whereIsWaldo(306, waldoFile, " strReturnStatusX = " + strReturnStatus_X);
                }
                if (strReturnStatus_X.equals("OK") &&strDataType_2.equals("Categorical")) {
                    ColumnOfData col_y = dm.getAllTheColumns().get(varIndexFor_Y);
                    col_y.cleanTheColumn(dm, varIndexFor_Y);
                    strReturnStatus_Y = col_y.getReturnStatus();
                    dm.whereIsWaldo(312, waldoFile, " strReturnStatusY = " + strReturnStatus_Y);
                }  
            }            
            if ((varIndexFor_X > -1 && varIndexFor_Y > -1)){
                str_al_VarLabels.add(dm.getVariableName(varIndexFor_X));
                al_ColOfData.add(dm.getSpreadsheetColumn(varIndexFor_X));    
                str_al_VarLabels.add(dm.getVariableName(varIndexFor_Y));
                al_ColOfData.add(dm.getSpreadsheetColumn(varIndexFor_Y));
            }
            else  { boolGoodToGo = false; }
            
            if (!boolGoodToGo) { 
                strReturnStatus = "Cancel";
            } else { strReturnStatus = "OK"; }
            
            if(boolGoodToGo) {
                String explanVarLabel, explanVarDescr, 
                       responseVarLabel, responseVarDescr;
                explanVarLabel = tf_Var_1_InFile.getText();
                responseVarLabel = tf_Var_2_InFile.getText();
                explanVarDescr =  tf_Var_1_Pref.getText();
                responseVarDescr =  tf_Var_2_Pref.getText();
                
                if (StringUtilities.isEmpty(explanVarDescr) || StringUtilities.isEmpty(responseVarDescr))  {
                    strSubTitle = responseVarLabel  + " vs. " + explanVarLabel;  
                    setPreferredFirstVarDescription(explanVarLabel);
                    setPreferredSecondVarDescription(responseVarLabel);
                } else {
                    strSubTitle = responseVarDescr  + " vs. " + explanVarDescr; 
                }     
                strReturnStatus = "OK";
                hide();
            }
        });  
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
        isCorrectType = true;
        
        switch (variableNowChecking) {
            case 1:
                strSelected = tf_Var_1_InFile.getText();
                break;
                
            case 2:
                strSelected = tf_Var_2_InFile.getText();
                break;
                
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog_One 376; checking Type of " + variableNowChecking;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        
        varIndex = dm.getVariableIndex(strSelected);    
        String colDataType = dm.getAllTheColumns().get(varIndex).getDataType();
        isCorrectType = true;
        if (!colDataType.equals("Quantitative")) {
            isCorrectType = false;
            MyAlerts.showInappropriateNumericVariableAlert();
        }    
        return isCorrectType;
    }
    
    private void doTheXYVars() {
        switch (callingProcedure) {
            case "RegressionDialog":
                lblExplanVar = new Label("'Explanatory' variable:");
                lblResponseVar= new Label("  'Response' variable:");    
                break;
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog_One 397: " + callingProcedure;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);           
        }        
    }    

    private void doResidsAndHats() {
        cbxSaveTheResids = new CheckBox("Save the residuals");
        cbxSaveTheResids.selectedProperty().addListener(this::changed_SaveTheResids);
        cbxSaveTheResids.setSelected(false);
        strSaveTheResids = "No";
        
        cbxSaveTheHats = new CheckBox("Save the Fits");
        cbxSaveTheHats.selectedProperty().addListener(this::changed_SaveTheHats);
        cbxSaveTheHats.setSelected(false);
        strSaveTheHats = "No";
    }
    
    public void changed_SaveTheResids(ObservableValue < ? extends Boolean> observable,
            Boolean oldValue,
            Boolean newValue) {
        String state = null;
        if (cbxSaveTheResids.isSelected() ) {
            strSaveTheResids = "Yes";
        } else { strSaveTheResids = "No"; }
    }
    
    public void changed_SaveTheHats(ObservableValue < ? extends Boolean> observable,
            Boolean oldValue,
            Boolean newValue) {
        String state = null;
        if (cbxSaveTheHats.isSelected() ) {
            strSaveTheHats = "Yes";
        } else { strSaveTheHats = "No"; }
    }
    
    public String getStrReturnStatus() { 
        if (strReturnStatus_X.equals("Cancel")
                || strReturnStatus_Y.equals("Cancel")) 
                   {strReturnStatus = "Cancel"; }
        return strReturnStatus; }
    
    public String getXLabel() { return xLabelFromFile; }
    public String getYLabel() { return yLabelFromFile; }
    public String getReturnStatusX() {  return strReturnStatus_X; }
    public String getReturnStatusY() {  return strReturnStatus_Y; }
    public ArrayList<ColumnOfData> getData() { return al_ColOfData; }
    public String getFirstVarLabel_InFile() { return tf_Var_1_InFile.getText(); }
    public String getSecondVarLabel_InFile() {  return tf_Var_2_InFile.getText(); }    
    public String getPreferredFirstVarDescription() {  return tf_Var_1_Pref.getText(); }
    public void setPreferredFirstVarDescription(String toThis) { tf_Var_1_Pref.setText(toThis); }
    public String getPreferredSecondVarDescription() {  return tf_Var_2_Pref.getText(); }
    public void setPreferredSecondVarDescription(String toThis) { tf_Var_2_Pref.setText(toThis); }
    public String getSubTitle() {  return strSubTitle; }
}
