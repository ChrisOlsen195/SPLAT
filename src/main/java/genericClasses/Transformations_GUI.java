/************************************************************
 *                    Transformations_GUI                   *
 *                          01/20/25                        *
 *                            21:00                         *
 ***********************************************************/
package genericClasses;

import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import splat.PositionTracker;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import smarttextfield.SmartTextFieldDoublyLinkedSTF;
import smarttextfield.SmartTextField;
import smarttextfield.SmartTextFieldsController;
import splat.*;
import utilityClasses.*;

public class Transformations_GUI {
    // POJOs
    
    Boolean isNumeric, numericVariableFound;    
    ArrayList<Boolean> varIsNumeric, varContainsZero, varHasNegatives,
                       varHasNonPositives;
    
    int var1Index, var2Index, functionIndex, unaryIndex,
        nOriginalDataPoints, nLinTransVarsParams, nlinTransFuncsParams, 
        nLinCombVarsParams, firstNumericVariable, maxStrSize, 
        controlWidth, controlHeight, numVars, nUnaryFuncsParams,
        nBinaryFuncsParams;
        
    double alphaValue, betaValue, widthFudgeFactor;
    
    String strAlphaValue, strBetaValue, strVar_1_Value, strVar_2_Value, 
            strUnaryValue, strFunctionValue, strOperationValue, controlTitle, 
            chosenProcedure;
    
    // Make empty if no-print
    //String waldoFile = "Transformations_GUI";
    String waldoFile = "";
    
    String newVarName = "New Var Name";
    String[] varNames, funcNames, binaryOpNames, unaryOpNames, strTransformedData, 
             linTransVarsParams, linTransFuncsParams, linCombVarsParams,
             strAdvisory;
    
    ArrayList<String> alStr_Var_1_Data, alStr_Var_2_Data;  
    ObservableList<String> variableNames, functionNames, operationNames;
   
    // My classes
    Data_Manager dm;
    PositionTracker tracker;
    SmartTextField stfUnaryOp, stfBinaryOp;
    ArrayList<SmartTextField> alSTF_LinTransVars, alSTF_linCombVars, 
                              alSTF_LinTransFuncs;    
    SmartTextFieldsController stf_LinTransVar_Controller, stf_LinComb_Controller,
                              stf_LinTransFunc_Controller;
    SmartTextFieldDoublyLinkedSTF al_LinTransVar_STF, al_LinComb_STF, 
                                  al_LinTransFunc_STF;
    
    Transformations_Calculations transCalc;

    // POJOs / FX
    ChoiceBox <String> cbVarChoice1, cbVarChoice2, cbFuncChoice, cbUnaryChoice,
                       cbOperationChoice;
    HBox hBoxOkCancel;
    HBox  hBoxLinTransVarsChoices, hBoxLinCombVarsChoices, hBoxLinTransFuncsChoices,
          hBoxUnaryOpsChoices, hBoxBinaryOpsChoices;
    Label lblEqual, lblPlus, lblMult1, lblMult2, lblLeftParen, lblRightParen;
    Region[] spacer;
    Scene scene;
    Stage controlStage;
    Text txtResizableText;
    TextField tfNewLinTransVarsVariable, tfNewLinCombVariable,
              tfNewLinTransFuncsVariable, tfNewUnaryOpsVariable,
              tfNewBinaryOpsVariable;
    TextField tfAlpha_LinTransVars, tfAlpha_LinComb, tfAlpha_LinTransFuncs;
    TextField tfBeta_LinTransVars, tfBeta_LinComb, tfBeta_LinTransFuncs;
    VBox root;

    private final DoubleProperty fontSize = new SimpleDoubleProperty(10);
    private final IntegerProperty color = new SimpleIntegerProperty(50);
    
    public Transformations_GUI(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(105, waldoFile, " *** Constructing");
        tracker = dm.getPositionTracker();
        numVars = tracker.getNVarsInStruct();
        nOriginalDataPoints = tracker.getNCasesInStruct();
        varNames = new String[numVars];
        
        varIsNumeric = new ArrayList<>();
        varContainsZero = new ArrayList<>();
        varHasNegatives = new ArrayList<>();
        varHasNonPositives = new ArrayList<>();
        
        stf_LinTransVar_Controller = new SmartTextFieldsController();
        // stf_Controllers are empty until size is set
        stf_LinTransVar_Controller.setSize(3);
        stf_LinTransVar_Controller.finish_TF_Initializations();
        al_LinTransVar_STF = stf_LinTransVar_Controller.getLinkedSTF();
        al_LinTransVar_STF.makeCircular();         

        stf_LinComb_Controller = new SmartTextFieldsController();
        stf_LinComb_Controller.setSize(3);
        stf_LinComb_Controller.finish_TF_Initializations();
        al_LinComb_STF = stf_LinComb_Controller.getLinkedSTF();
        al_LinComb_STF.makeCircular();    
 
        stf_LinTransFunc_Controller = new SmartTextFieldsController();
        stf_LinTransFunc_Controller.setSize(3);
        stf_LinTransFunc_Controller.finish_TF_Initializations();
        al_LinTransFunc_STF = stf_LinTransFunc_Controller.getLinkedSTF();
        al_LinTransFunc_STF.makeCircular();  

        /******************************************************************
        *      Find the first numeric variable.                           *
        ******************************************************************/
        firstNumericVariable = -1; 
        numericVariableFound = false;
        
        for (int i = 0; i < numVars; i++) {
            varNames[i] = dm.getVariableName(i);
            
            boolean columnIsEmpty = dm.getAllTheColumns().get(i).getColumnOfData().getColumnIsEmpty();            
            if (columnIsEmpty) { isNumeric = false; }   // Must be SOME reason for this repeat ?????????????????
            
            isNumeric = dm.getAllTheColumns().get(i).getDataType().equals("Quantitative");             
            if (columnIsEmpty) { isNumeric = false; }   // Must be SOME reason for this repeat  ????????????????
            
            if ((firstNumericVariable == -1) && (isNumeric)) {
                firstNumericVariable = i;
                numericVariableFound = true;
            }
            varIsNumeric.add(isNumeric);

            if (isNumeric) {
                QuantitativeDataVariable ithQDV = new QuantitativeDataVariable("DummyA", "DummyB", dm.getAllTheColumns().get(i).getTheCases_ArrayList());
                varContainsZero.add(ithQDV.getTheUCDO().getContainsAZero());
                varHasNegatives.add(ithQDV.getTheUCDO().getContainsANegative());
                varHasNonPositives.add(ithQDV.getTheUCDO().getContainsANonPositive());
            }
            else {
               varContainsZero.add(false);
               varHasNegatives.add(false);
               varHasNonPositives.add(false);
            }
        }    
        
        if (firstNumericVariable == -1) {
            MyAlerts.showNoQuantVariablesAlert();
        } else {
            var1Index = firstNumericVariable;
            var2Index = firstNumericVariable;
            transCalc = new Transformations_Calculations();
            initGUI();
        }
    }

    public void linTransVars() {
        dm.whereIsWaldo(180, waldoFile, " --- linTransVars()");
        linTransVarsParams = new String[] {newVarName, "0.0", "1.0"};
        // Initialize numeric choices
        strAlphaValue = linTransVarsParams[1];
        strBetaValue = linTransVarsParams[2];
        nLinTransVarsParams = linTransVarsParams.length;    
        alSTF_LinTransVars = new ArrayList<>();
        initParameters("linearTransformationOfAVariable");

        for (int ithLinTransVars = 0; ithLinTransVars < nLinTransVarsParams; ithLinTransVars++) {
            al_LinTransVar_STF.get(ithLinTransVars).setIsEditable(true); 
            al_LinTransVar_STF.get(ithLinTransVars).getTextField().setText(linTransVarsParams[ithLinTransVars]);
            alSTF_LinTransVars.add(ithLinTransVars, al_LinTransVar_STF.get(ithLinTransVars));
        }
        tfNewLinTransVarsVariable = al_LinTransVar_STF.get(0).getTextField();
        al_LinTransVar_STF.get(1).setSmartTextField_MB_REAL(true);
        al_LinTransVar_STF.get(2).setSmartTextField_MB_REAL(true);
        
        tfAlpha_LinTransVars = new TextField();
        tfBeta_LinTransVars = new TextField();
        
        tfAlpha_LinTransVars = al_LinTransVar_STF.get(1).getTextField();  // Just to shorten variable name
        tfBeta_LinTransVars = al_LinTransVar_STF.get(2).getTextField();   // for easier reading
        setUpAlphaAndBeta(tfAlpha_LinTransVars, tfBeta_LinTransVars);
        
        hBoxLinTransVarsChoices = new HBox();
        hBoxLinTransVarsChoices.getChildren().addAll(tfNewLinTransVarsVariable, lblEqual, tfAlpha_LinTransVars,
                                     lblPlus, tfBeta_LinTransVars, lblMult1, cbVarChoice1);
        
        Button okBtnLinTransVars = new Button("OK");
        okBtnLinTransVars.setOnAction(e -> okLinTransVar());
        
        Button cancelBtnLinTransVars = new Button("Cancel");
        cancelBtnLinTransVars.setOnAction(e -> closeControlStage());      
        makeTheHBoxAndSpacers();
        hBoxOkCancel.getChildren().addAll(spacer[0], okBtnLinTransVars, spacer[1], cancelBtnLinTransVars,spacer[2]); 
        showTheControlAndWait(hBoxLinTransVarsChoices);
    }
    
    public void linearCombOfVariables() {
        dm.whereIsWaldo(220, waldoFile, " --- linearCombOfVariables()");
        linCombVarsParams = new String[] {newVarName, "1.0", "1.0"};
        strAlphaValue = linCombVarsParams[1];
        strBetaValue = linCombVarsParams[2];
        nLinCombVarsParams = linCombVarsParams.length;         
        alSTF_linCombVars = new ArrayList<>();          

        initParameters("linearCombinationOfVariables");
     
        for (int ithLinCombVar = 0; ithLinCombVar < nLinCombVarsParams; ithLinCombVar++) {
            al_LinComb_STF.get(ithLinCombVar).setIsEditable(true); 
            al_LinComb_STF.get(ithLinCombVar).getTextField().setText(linCombVarsParams[ithLinCombVar]);
            alSTF_linCombVars.add(ithLinCombVar, al_LinComb_STF.get(ithLinCombVar));
        }

        tfNewLinCombVariable = al_LinComb_STF.get(0).getTextField();
        al_LinComb_STF.get(1).setSmartTextField_MB_REAL(true);
        al_LinComb_STF.get(2).setSmartTextField_MB_REAL(true);
        
        tfAlpha_LinComb = new TextField();
        tfBeta_LinComb = new TextField();
        
        tfAlpha_LinComb = al_LinComb_STF.get(1).getTextField(); 
        tfBeta_LinComb = al_LinComb_STF.get(2).getTextField(); 
        
        setUpAlphaAndBeta(tfAlpha_LinComb, tfBeta_LinComb);

        hBoxLinCombVarsChoices = new HBox();
        hBoxLinCombVarsChoices.setMinWidth(500);
        hBoxLinCombVarsChoices.getChildren().addAll(tfNewLinCombVariable, lblEqual, tfAlpha_LinComb, lblMult1, cbVarChoice1,
                                     lblPlus, tfBeta_LinComb, lblMult2, cbVarChoice2);
        
        Button okBtnLinComVars = new Button("OK");
        okBtnLinComVars.setOnAction(e -> okLinComVars());
        Button cancelBtnLinComVars = new Button("Cancel");
        cancelBtnLinComVars.setOnAction(e -> closeControlStage());     
        makeTheHBoxAndSpacers();
        hBoxOkCancel.getChildren().addAll(spacer[0], okBtnLinComVars, spacer[1], cancelBtnLinComVars, spacer[2]);
        showTheControlAndWait(hBoxLinCombVarsChoices);
    }
    
    public void linTransFuncs() {
        dm.whereIsWaldo(261, waldoFile, "--- linTransFuncs()");        
        linTransFuncsParams = new String[] {newVarName, "0.0", "1.0"}; 
        strAlphaValue = linTransFuncsParams[1];
        strBetaValue = linTransFuncsParams[2];
        nlinTransFuncsParams = linTransFuncsParams.length;    
        alSTF_LinTransFuncs = new ArrayList<>();   

        initParameters("linearTransformationWithFunction");
        
        for (int ithLinTransFunc = 0; ithLinTransFunc < nlinTransFuncsParams; ithLinTransFunc++) {
            al_LinTransFunc_STF.get(ithLinTransFunc).setIsEditable(true);
            al_LinTransFunc_STF.get(ithLinTransFunc).getTextField().setText(linTransFuncsParams[ithLinTransFunc]); 
            alSTF_LinTransFuncs.add(ithLinTransFunc, al_LinTransFunc_STF.get(ithLinTransFunc));
        }

        tfNewLinTransFuncsVariable = al_LinTransFunc_STF.get(0).getTextField();
        al_LinTransFunc_STF.get(1).setSmartTextField_MB_REAL(true);
        al_LinTransFunc_STF.get(2).setSmartTextField_MB_REAL(true);
        
        tfAlpha_LinTransFuncs = new TextField();
        tfBeta_LinTransFuncs = new TextField();
        
        tfAlpha_LinTransFuncs = al_LinTransFunc_STF.get(1).getTextField();  // Short variable name
        tfBeta_LinTransFuncs = al_LinTransFunc_STF.get(2).getTextField();  // Short variable name
        setUpAlphaAndBeta(tfAlpha_LinTransFuncs, tfBeta_LinTransFuncs);

        hBoxLinTransFuncsChoices = new HBox();
        hBoxLinTransFuncsChoices.getChildren().addAll(tfNewLinTransFuncsVariable, lblEqual, tfAlpha_LinTransFuncs,
                                     lblPlus, tfBeta_LinTransFuncs, lblMult1, cbFuncChoice, 
                                     lblLeftParen, cbVarChoice1, lblRightParen);
        
        Button okBtnLinTransFuncs = new Button("OK");
        okBtnLinTransFuncs.setOnAction(e -> okLinTransWithFuncs(strFunctionValue));
        
        Button cancelBtnLinTransFuncs = new Button("Cancel");
        cancelBtnLinTransFuncs.setOnAction(e -> closeControlStage());
        makeTheHBoxAndSpacers();
        hBoxOkCancel.getChildren().addAll(spacer[0], okBtnLinTransFuncs, spacer[1], cancelBtnLinTransFuncs, spacer[2]);
        showTheControlAndWait(hBoxLinTransFuncsChoices);
    }
    
    public void unaryOperationOnVar() {
        dm.whereIsWaldo(304, waldoFile, "--- unaryOperationOnVar()");
        nUnaryFuncsParams = unaryOpNames.length;   // Names in 598
        stfUnaryOp = new SmartTextField();
        stfUnaryOp.getTextField().setText(newVarName);   

        initParameters("unaryOperationOnVariable");
        stfUnaryOp.getTextField().setEditable(true);        
        tfNewUnaryOpsVariable = stfUnaryOp.getTextField(); 
        
        hBoxUnaryOpsChoices = new HBox();
        hBoxUnaryOpsChoices.getChildren().addAll(tfNewUnaryOpsVariable, lblEqual, cbUnaryChoice, 
                                     lblLeftParen, cbVarChoice1, lblRightParen);
        
        Button okBtnUnaryFuncs = new Button("OK");
        okBtnUnaryFuncs.setOnAction(e -> okUnaryOperation(chosenProcedure));
        
        Button cancelBtnUnaryFuncs = new Button("Cancel");
        cancelBtnUnaryFuncs.setOnAction(e -> closeControlStage());
        makeTheHBoxAndSpacers();
        hBoxOkCancel.getChildren().addAll(spacer[0], okBtnUnaryFuncs, spacer[1], cancelBtnUnaryFuncs,  spacer[2]);
        showTheControlAndWait(hBoxUnaryOpsChoices);          
    }    
    
    public void binaryOpsWithVariables() {
        dm.whereIsWaldo(328, waldoFile, " --- binaryOpsWithVariables()");
        nBinaryFuncsParams = unaryOpNames.length;   // Names in 598
        stfBinaryOp = new SmartTextField();
        stfBinaryOp.getTextField().setText(newVarName); 

        initParameters("binaryOperationOfVariables");  
        stfBinaryOp.getTextField().setEditable(true);        
        tfNewBinaryOpsVariable = stfBinaryOp.getTextField();         
        
        hBoxBinaryOpsChoices = new HBox();
        hBoxBinaryOpsChoices.getChildren().addAll(tfNewBinaryOpsVariable, lblEqual, cbVarChoice1, 
                                         cbOperationChoice, cbVarChoice2);
        
        Button okButtonOpsWithVars = new Button("OK");
        okButtonOpsWithVars.setOnAction(e -> okBinaryOpsWithVars(strOperationValue));
        Button cancelButtonOpsWithVars = new Button("Cancel");
        cancelButtonOpsWithVars.setOnAction(e -> closeControlStage());  
        makeTheHBoxAndSpacers();
        hBoxOkCancel.getChildren().addAll(spacer[0], okButtonOpsWithVars, spacer[1], cancelButtonOpsWithVars, spacer[2]);
        showTheControlAndWait(hBoxBinaryOpsChoices);
    }
    
    public void var1ItemChanged(ObservableValue <? extends String> observable,
                                String oldValue,
                                String newValue) {
        strVar_1_Value = newValue;
    }
    
    public void var1IndexChanged(ObservableValue <? extends Number> observable,
                                 Number oldValue,
                                 Number newValue) {
        var1Index = (int)newValue;
        boolean columnIsEmpty = dm.getAllTheColumns().get(var1Index).getColumnIsEmpty();
        
        if (columnIsEmpty) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showEmptyColumnAlert(varLabel);
        }  else {        
            if (varIsNumeric.get(var1Index) == true) {
                alStr_Var_1_Data = dm.getAllTheColumns().get(var1Index).getTheCases_ArrayList();
            } else {
                String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
                MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            }
        }
    }
    
    public void var2ItemChanged(ObservableValue <? extends String> observable,
                                String oldValue,
                                String newValue) {
        strVar_2_Value = newValue;     
    }
    
    public void var2IndexChanged(ObservableValue <? extends Number> observable,
                                 Number oldValue,
                                 Number newValue) {
        var2Index = (int)newValue;
        boolean columnIsEmpty = dm.getAllTheColumns().get(var2Index).getColumnIsEmpty();
        if (columnIsEmpty) {
            String varLabel = dm.getAllTheColumns().get(var2Index).getVarLabel();
            MyAlerts.showEmptyColumnAlert(varLabel);
        }  else {
            if (varIsNumeric.get(var2Index)) {
                alStr_Var_2_Data = dm.getAllTheColumns().get(var2Index).getTheCases_ArrayList();
            } else {
                String varLabel = dm.getAllTheColumns().get(var2Index).getVarLabel();
                MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            }
        }
    }
    
    public void functionItemChanged(ObservableValue <? extends String> observable,
                                    String oldValue,
                                    String newValue) {
        strFunctionValue = newValue;
    }
    
    public void functionIndexChanged(ObservableValue <? extends Number> observable,
                                     Number oldValue,
                                     Number newValue) {
        functionIndex = (int)newValue;
    }
    
    public void unaryItemChanged(ObservableValue <? extends String> observable,
                                    String oldValue,
                                    String newValue) {
        strUnaryValue = newValue;
    }
    
    public void unaryIndexChanged(ObservableValue <? extends Number> observable,
                                     Number oldValue,
                                     Number newValue) {
        unaryIndex = (int)newValue;
        chosenProcedure = unaryOpNames[unaryIndex];
    }
    
    public void operationItemChanged(ObservableValue <? extends String> observable,
                                    String oldValue,
                                    String newValue) {
        strOperationValue = newValue;
    }
    
    public void okLinTransVar() {
        if (varIsNumeric.get(var1Index) == false) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            return;
        } 
        
        setUpTransformation();       
        strTransformedData = transCalc.linearTransformation(alStr_Var_1_Data, alphaValue, betaValue);
        addVarToStructure(tfNewLinTransVarsVariable);
        tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
        dm.setDataAreClean(false);
        closeControlStage();   
    }
    
    public void okLinComVars() {
        if (varIsNumeric.get(var1Index) == false) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            return;
        } 
        
        if (varIsNumeric.get(var2Index) == false) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            return;
        } 

        setUpTransformation();
        strTransformedData = transCalc.linearCombinationOfVars(alStr_Var_1_Data, 
                                                         alStr_Var_2_Data, 
                                                         alphaValue, 
                                                         betaValue);
        addVarToStructure(tfNewLinCombVariable);
        tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
        dm.setDataAreClean(false);
        closeControlStage();
    }
        
    public void okLinTransWithFuncs(String ltfProcedure) {
        if (varIsNumeric.get(var1Index) == false) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            return;
        } 
        
        //  Check for illegal transformation here
        if ((ltfProcedure.equals("ln") && varHasNonPositives.get(var1Index) == true)) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToLogABadNumberAlert(varLabel);
            return;
        }
        
        if ((ltfProcedure.equals("log10") && varHasNonPositives.get(var1Index) == true)) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToLogABadNumberAlert(varLabel);
            return;
        }
        
        if ((ltfProcedure.equals("sqrt") && varHasNegatives.get(var1Index) == true)) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttempAtSqrRtOfNegAlert(varLabel);
            return;
        }   
        
        if ((ltfProcedure.equals("recip") && varContainsZero.get(var1Index) == true)) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToDivideByZeroAlert(varLabel);
            return;
        } 
        
        setUpTransformation();
        strTransformedData = transCalc.linTransWithFunc(alStr_Var_1_Data, 
                                                 ltfProcedure, 
                                                 alphaValue, 
                                                 betaValue);       
        addVarToStructure(tfNewLinTransFuncsVariable);
        tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
        dm.setDataAreClean(false);
        closeControlStage();
    }

    public void okUnaryOperation(String uOpProcedure) {
        if (varIsNumeric.get(var1Index) == false) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            return;
        } 
        
        if ((uOpProcedure.equals("recip") && varContainsZero.get(var1Index) == true)) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToDivideByZeroAlert(varLabel);
            return;
        }
        
        strTransformedData = new String[nOriginalDataPoints];  
        strTransformedData = transCalc.unaryOpsOfVars(alStr_Var_1_Data,
                                                      uOpProcedure);      
        addVarToStructure(tfNewUnaryOpsVariable);
        tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
        dm.setDataAreClean(false);
        closeControlStage();
    }   //  end of unary
    
    public void okBinaryOpsWithVars(String strOperationValue) {
        if (varIsNumeric.get(var1Index) == false) {
            String varLabel = dm.getAllTheColumns().get(var1Index).getVarLabel();
            MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            return;
        } 
        
        if (varIsNumeric.get(var2Index) == false) {
            String varLabel = dm.getAllTheColumns().get(var2Index).getVarLabel();
            MyAlerts.showAttemptToTransformCatVariableAlert(varLabel);
            return;
        }
        
        if ((strOperationValue.equals("/") && varContainsZero.get(var2Index) == true)) {
            String varLabel = dm.getAllTheColumns().get(var2Index).getVarLabel();
            MyAlerts.showAttemptToDivideByZeroAlert(varLabel);
            return;
        }
        
        strTransformedData = new String[nOriginalDataPoints];
        strTransformedData = transCalc.binaryOpsOfVars(alStr_Var_1_Data,
                                                       strOperationValue,
                                                       alStr_Var_2_Data);
        addVarToStructure(tfNewBinaryOpsVariable);
        varIsNumeric.add(true);
        tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
        dm.setDataAreClean(false);
        closeControlStage();
    }
    
    public void closeControlStage() { controlStage.close(); }
    
    private void initGUI() {
        funcNames = new String[] {"ln", "log10", "sqrt", "recip", "10^x", "e^x"};
        unaryOpNames = new String[] {"z-score", "percentile rank", "rank", "rankits"};
        binaryOpNames = new String[] {"+", "-", "*", "/"};
        
        strAdvisory = new String[4];
        
        variableNames = FXCollections.<String>observableArrayList(varNames);
        functionNames = FXCollections.<String>observableArrayList(funcNames);
        operationNames = FXCollections.<String>observableArrayList(binaryOpNames);
        
        cbVarChoice1 = new ChoiceBox();
        cbVarChoice1.getItems().addAll(variableNames);
        cbVarChoice1.setMinWidth(150);
        cbVarChoice1.setMaxWidth(300);
        cbVarChoice1.getSelectionModel().select(firstNumericVariable);
        cbVarChoice1.getSelectionModel().selectedItemProperty()
                                        .addListener(this::var1ItemChanged);
        cbVarChoice1.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::var1IndexChanged);
        
        cbVarChoice2 = new ChoiceBox();
        cbVarChoice2.getItems().addAll(variableNames);
        cbVarChoice2.setMinWidth(150);
        cbVarChoice2.setMaxWidth(300);     
        cbVarChoice2.getSelectionModel().select(firstNumericVariable);
        cbVarChoice2.getSelectionModel().selectedItemProperty()
                                        .addListener(this::var2ItemChanged);
        cbVarChoice2.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::var2IndexChanged);   
        
        cbFuncChoice = new ChoiceBox();
        cbFuncChoice.getItems().addAll(functionNames);
        cbFuncChoice.setMinWidth(75);
        cbFuncChoice.setMaxWidth(150);    
        cbFuncChoice.getSelectionModel().select(0);
        cbFuncChoice.getSelectionModel().selectedItemProperty()
                                        .addListener(this::functionItemChanged);
        cbFuncChoice.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::functionIndexChanged);
        
        cbUnaryChoice = new ChoiceBox();
        cbUnaryChoice.getItems().addAll(unaryOpNames);
        cbUnaryChoice.setMinWidth(75);
        cbUnaryChoice.setMaxWidth(150);    
        cbUnaryChoice.getSelectionModel().select(0);
        cbUnaryChoice.getSelectionModel().selectedItemProperty()
                                         .addListener(this::unaryItemChanged);
        cbUnaryChoice.getSelectionModel().selectedIndexProperty()
                                         .addListener(this::unaryIndexChanged);
        
        cbOperationChoice = new ChoiceBox();
        cbOperationChoice.getItems().addAll(operationNames);
        cbOperationChoice.setMinWidth(75);
        cbOperationChoice.setMaxWidth(150);   
        cbOperationChoice.getSelectionModel().select(0);
        cbOperationChoice.getSelectionModel().selectedItemProperty()
                                             .addListener(this::operationItemChanged);

        lblEqual = new Label("  =  ");
        lblEqual.setPadding(new Insets(5, 5, 5, 5));
        lblPlus = new Label(" + ");
        lblPlus.setPadding(new Insets(5, 5, 5, 5));
        
        // Two mults needed for linear combination
        lblMult1 = new Label(" x "); lblMult1.setPadding(new Insets(5, 5, 5, 5));
        lblMult2 = new Label(" x "); lblMult2.setPadding(new Insets(5, 5, 5, 5));
        lblLeftParen = new Label("("); lblLeftParen.setPadding(new Insets(5, 5, 5, 5));
        lblRightParen = new Label(")"); lblRightParen.setPadding(new Insets(5, 5, 5, 5));       
    }
    
    public void initParameters(String forThisMethod) {
        dm.whereIsWaldo(638, waldoFile, " --- initParameters(String forThisMethod)");
        String textToResize;
        switch (forThisMethod) {            
            case "linearTransformationOfAVariable":
                controlTitle = "Linear transformation of a variable";
                controlWidth = 950; controlHeight = 300; widthFudgeFactor = 0.60;
                strAdvisory[0] = "\n    This control is used to create a linear transformation of an existing variable.  Enter the new variable\n";
                strAdvisory[1] = "    label and coefficients, then choose the variable to transform from the drop down menu.\n\n" ;
                strAdvisory[2] = "      The format for the choices is: \n\n";
                strAdvisory[3] = "          <NewVariable name> = <a> + <b> x <variable>\n\n";   
                alStr_Var_1_Data = dm.getAllTheColumns().get(var1Index).getTheCases_ArrayList();
                break;
            
            case "linearCombinationOfVariables":
                controlTitle = "Linear Combination of Variables";                
                controlWidth = 950; controlHeight = 325; widthFudgeFactor = 0.60;
                strAdvisory[0] = "\n    This control is used to calculate a linear combination of variables.  Enter the name of the new variable, \n";
                strAdvisory[1] = "      the coefficients of the existing variables, and your choice of variables from the drop down menus.\n\n" ;
                strAdvisory[2] = "      The format for the choices is: \n\n";
                strAdvisory[3] = "          <NewVariable name> = <a> * <variable> + <b> * <variable>\n\n";    
                alStr_Var_1_Data = dm.getAllTheColumns().get(var1Index).getTheCases_ArrayList();
                alStr_Var_2_Data = dm.getAllTheColumns().get(var2Index).getTheCases_ArrayList();
                cbVarChoice2.getSelectionModel().select(var2Index);  
                break;
                
            case "linearTransformationWithFunction":
                controlTitle = "Linear Transformation with Function";                
                controlWidth = 950; controlHeight = 325; widthFudgeFactor = 0.60;
                strAdvisory[0] = "\n    This control is used to create a linear transformation, f(an existing variable).  Enter the new variable\n";
                strAdvisory[1] = "    label and coefficients, then choose the function and the variable to transform from the drop down menu.\n\n" ;
                strAdvisory[2] = "      The format for the choices is: \n\n";
                strAdvisory[3] = "          <NewVariable name> = <a> + <b> x <variable>\n\n";    
                chosenProcedure= "ln";
                alStr_Var_1_Data = dm.getAllTheColumns().get(var1Index).getTheCases_ArrayList();
                strFunctionValue = "ln";   
                break;

            case "binaryOperationOfVariables":
                controlTitle = "Binary Operation of Variables";                
                controlWidth = 950; controlHeight = 300; widthFudgeFactor = 0.60;
                strAdvisory[0] = "\n    This control is used to perform arithmetic operations on variables. Enter the new variable label,\n";
                strAdvisory[1] = "    and choose the variables and the operation from the drop down menus.\n\n"  ;
                strAdvisory[2] = "      The format for the choices is: \n\n";
                strAdvisory[3] = "          <NewVariable name> = <variable>) <operation>  <variable>\n\n";     
                strOperationValue = "+";
                alStr_Var_1_Data = dm.getAllTheColumns().get(var1Index).getTheCases_ArrayList();
                alStr_Var_2_Data = dm.getAllTheColumns().get(var2Index).getTheCases_ArrayList();
                cbVarChoice2.getSelectionModel().select(var2Index);
                break;
            
            case "unaryOperationOnVariable":
                controlTitle = "Unary Operation on a Variable";                
                controlWidth = 950; controlHeight = 300; widthFudgeFactor = 0.60;
                strAdvisory[0] = "\n    This control is used to perform unary operations on variables. Enter the new variable label,\n";
                strAdvisory[1] = "    and choose the unary operation from the drop down menus.\n\n"  ;
                strAdvisory[2] = "      The format for the choices is: \n\n";
                strAdvisory[3] = "          <NewVariable name> = <operation>  <variable>\n\n";  
                chosenProcedure = "z-score";
                alStr_Var_1_Data = dm.getAllTheColumns().get(var1Index).getTheCases_ArrayList();
                break;
            
            default: 
                String switchFailure = "Switch failure: Transformations_GUI 700 " + forThisMethod;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        
        textToResize = new String();
        maxStrSize = 0;
        
        for (String adviceStr : strAdvisory) {
            textToResize += adviceStr;            
            if (adviceStr.length() > maxStrSize) {
                maxStrSize = adviceStr.length();
            }         
        }
        
        txtResizableText = new Text(textToResize);
    }
    
    public void setUpAlphaAndBeta(TextField tfAlpha, TextField tfBeta) {
        tfAlpha.setPrefColumnCount(5);        
        tfAlpha.textProperty().set(strAlphaValue);
        tfAlpha.textProperty().addListener((observable, oldValue, newValue) -> {
            strAlphaValue = newValue;
        });
        
        tfBeta.setPrefColumnCount(5);
        tfBeta.textProperty().set(strBetaValue);
        tfBeta.textProperty().addListener((observable, oldValue, newValue) -> {
            strBetaValue = newValue;
        });     
    }
    
    public void showTheControlAndWait(HBox hBoxChoices) {
        root = new VBox();
        root.getChildren().addAll(txtResizableText, hBoxChoices, hBoxOkCancel);
        
        fontSize.bind(root.widthProperty().add(root.heightProperty()).divide(widthFudgeFactor * maxStrSize));
        root.styleProperty().bind(Bindings.concat("-fx-font-family: Serif; ",
                                                  "-fx-font-size: ", fontSize.asString(), ";"
                                                 ,"-fx-base: rgb(200, 255, 255,",color.asString(),");")); 

        controlStage = new Stage();
        scene = new Scene(root, controlWidth, controlHeight);        
        controlStage.setTitle(controlTitle);
        controlStage.setScene(scene);
        controlStage.showAndWait();   
        controlStage.close();
    }

    
    public void setUpTransformation() {
        strTransformedData = new String[nOriginalDataPoints];
        alphaValue = Double.parseDouble(strAlphaValue);
        betaValue = Double.parseDouble(strBetaValue);        
    }
    
    public double getVar1(int ithPoint) {       
        return Double.parseDouble(alStr_Var_1_Data.get(ithPoint));
    }
    
    public double getVar2(int ithPoint) {
        return Double.parseDouble(alStr_Var_2_Data.get(ithPoint));
    }
    
    private void addVarToStructure(TextField tfNewVariable) {
        int col;     
        ColumnOfData theNewColumn;
        // So that the new var is  not off-grid...
        tracker.setSneakingInANewColumn(true);
        col = tracker.getNVarsInStruct();
        dm.addToStructOneColumnWithNoData();  
        numVars = tracker.getNVarsInStruct();
        col++;
        dm.setVariableNameInStruct(col - 1, tfNewVariable.getText());
        dm.getAllTheColumns().get(col - 1).setDataType("Quantitative");
        theNewColumn = dm.getAllTheColumns().get(col - 1);
        int columnSize = theNewColumn.getColumnSize();
        
        for (int ithCase = 0; ithCase < columnSize; ithCase++) {
            dm.setDataInStruct("804 trans", 
            col - 1,
            ithCase,
            strTransformedData[ithCase]);
            //String tempString = dm.getFromDataStruct(col - 1, ithCase);
        }
        
        QuantitativeDataVariable ithQDV = new QuantitativeDataVariable("DummyA", "DummyB", dm.getAllTheColumns().get(col - 1).getTheCases_ArrayList());
        varContainsZero.add(ithQDV.getTheUCDO().getContainsAZero());
        varHasNegatives.add(ithQDV.getTheUCDO().getContainsANegative());
        varHasNonPositives.add(ithQDV.getTheUCDO().getContainsANonPositive());

        dm.sendDataStructToGrid(col - 1, 0);
        tracker.setSneakingInANewColumn(false);
    }
    
    public boolean getNumericVariableFound() { return numericVariableFound; }
    
    private void makeTheHBoxAndSpacers() {
        hBoxOkCancel = new HBox();
        hBoxOkCancel.setPadding(new Insets(10));
        spacer = new Region[3];
        spacer[0] = new Region();
        spacer[1] = new Region();
        spacer[2] = new Region();
        HBox.setHgrow(spacer[0], Priority.ALWAYS);
        HBox.setHgrow(spacer[1], Priority.ALWAYS);
        HBox.setHgrow(spacer[2], Priority.ALWAYS);        
    }
}
