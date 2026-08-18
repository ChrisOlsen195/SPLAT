/************************************************************
 *                    RandomAssignment_Dialog               *
 *                          08/15/26                        *
 *                            00:00                         *
 ***********************************************************/
package dialogs;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import splat.Data_Manager;
import splat.Var_List;
import utilityClasses.MyAlerts;

public class RandomAssignment_Dialog extends Splat_Dialog {
    // POJOs

    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private int varIndex, varIndexForX, varIndexForY, variableNowChecking; 
    protected int nCheckBoxes;
    
    private String callingProc, strSelected, subTitle;
    
    public String wtf_NullChangeQuery;
    private ArrayList<String> strVarLabels;

    // My classes
    private ArrayList<ColumnOfData> al_OfColumns;
    protected Var_List listOfVars;    
    
    // POJOs / FX
    private final Button btnSelectX_Arrow, btnSelectY_Arrow;
    protected Button btnReset;
    protected CheckBox[] dashBoardOptions;
    protected GridPane gridChoicesMade;
    private final HBox middlePanel; //, dataDescriptions;
    private final VBox mainPanel, vBoxVars2ChooseFrom, 
                vBoxXVarChoices, vBoxYVarChoices;
    protected VBox leftPanel, rightPanel;
    protected Label lbl_Title, lblFirstVar, lblSecondVar; 
    private final Label lbl_VarsInData;
    protected TextField tf_FirstVarLabel, tf_SecondVarLabel;
    
    public RandomAssignment_Dialog(Data_Manager dm, String callingProc) {
        super(dm);
        this.dm = dm;
        this.callingProc = callingProc;
        if (printTheStuff) {
            System.out.println("*** 69 RandomAssignment_Dialog, Constructing");
            System.out.println("... 70 RandomAssignment_Dialog, callingProc = " + callingProc);
        }
        setStrReturnStatus("OK");
        
        boolGoodToGo = true;
        al_OfColumns = new ArrayList<>();   
        strVarLabels = new ArrayList<>();
        lbl_Title = new Label("RandomAssignment_Dialog");
        lbl_Title.getStyleClass().add("dialogTitle");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));
        
        vBoxVars2ChooseFrom = new VBox();
        vBoxVars2ChooseFrom.setAlignment(Pos.TOP_LEFT);
        lbl_VarsInData = new Label("Variables in File:");
        lbl_VarsInData.setPadding(new Insets(0, 0, 5, 0));
        listOfVars = new Var_List(dm, null, null);
        vBoxVars2ChooseFrom.getChildren().add(lbl_VarsInData);
        vBoxVars2ChooseFrom.getChildren().add(listOfVars.getPane());
        vBoxVars2ChooseFrom.setPadding(new Insets(0, 10, 0, 10));
        
        btnSelectX_Arrow = new Button("===>");
        btnSelectY_Arrow = new Button("===>");

        vBoxXVarChoices = new VBox();
        vBoxXVarChoices.setAlignment(Pos.TOP_LEFT);
        lblFirstVar = new Label();
        lblFirstVar.setPadding(new Insets(0, 0, 5, 0));
        tf_FirstVarLabel = new TextField("");
        tf_FirstVarLabel.setPrefWidth(125.0);
        vBoxXVarChoices.getChildren().addAll(lblFirstVar, tf_FirstVarLabel);

        vBoxYVarChoices = new VBox();
        vBoxYVarChoices.setAlignment(Pos.TOP_LEFT);
        lblSecondVar = new Label();
        lblSecondVar.setPadding(new Insets(0, 0, 5, 0));
        tf_SecondVarLabel = new TextField("");
        tf_SecondVarLabel.setPrefWidth(125.0);
        
        if (callingProc.equals("RandomAssign_RBD")) {
            vBoxYVarChoices.getChildren().addAll(lblSecondVar, tf_SecondVarLabel);
        }

        gridChoicesMade = new GridPane();
        gridChoicesMade.setHgap(10);
        gridChoicesMade.setVgap(15);
        gridChoicesMade.add(btnSelectX_Arrow, 0, 0);
        gridChoicesMade.add(vBoxXVarChoices, 1, 0);
        
        if (callingProc.equals("RandomAssign_RBD")) {
            gridChoicesMade.add(btnSelectY_Arrow, 0, 1);
        }
        
        gridChoicesMade.add(vBoxYVarChoices, 1, 1);
        
        GridPane.setValignment(btnSelectX_Arrow, VPos.BOTTOM);
        GridPane.setValignment(btnSelectY_Arrow, VPos.BOTTOM);
        gridChoicesMade.setPadding(new Insets(0, 10, 0, 0));

        leftPanel = new VBox(10);
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPadding(new Insets(0, 25, 0, 10));
        
        rightPanel = new VBox(10);
        rightPanel.setAlignment(Pos.CENTER_LEFT);
        rightPanel.setPadding(new Insets(0, 25, 0, 10));
        rightPanel.getChildren().add(gridChoicesMade);
        
        middlePanel = new HBox();
        middlePanel.setAlignment(Pos.CENTER);
        middlePanel.getChildren().add(leftPanel);     
        middlePanel.getChildren().add(vBoxVars2ChooseFrom);
        middlePanel.getChildren().add(rightPanel);
        middlePanel.setPadding(new Insets(10, 0, 10, 0));

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Assign");
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
        
        mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);    
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(lbl_Title, sepTitle);    
        mainPanel.getChildren().add(middlePanel);
        Separator sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);    
        mainPanel.getChildren().add(buttonPanel);
        
        Scene myScene = new Scene(mainPanel);
        myScene.getStylesheets().add(strCSS);
        setScene(myScene);
        
        setOnCloseRequest((WindowEvent we) -> {
            if (printTheStuff) {
                System.out.println("... 168 RandomAssignment_Dialog, setOnCloseRequest((WindowEvent we)");
            }
            btnCancel.fire();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            if (printTheStuff) {
                System.out.println("... 175 RandomAssignment_Dialog, btnCancel.setOnAction");
            }
            setStrReturnStatus("Cancel");
            hide();
        });

        btnReset.setOnAction((ActionEvent event) -> {
            if (printTheStuff) {
                System.out.println("... 183 RandomAssignment_Dialog, btnReset.setOnAction");
            }
            listOfVars.resetList();
            tf_FirstVarLabel.setText("");
            tf_SecondVarLabel.setText("");
        });

        btnSelectX_Arrow.setOnAction((ActionEvent event) -> {
        if (printTheStuff) {
            System.out.println("... 192 RandomAssignment_Dialog, btnSelectX_Arrow.setOnAction");
        }

            variableNowChecking = 1;
            
            if (listOfVars.getNamesSelected().size() == 1) {
                String tempIndicator = listOfVars.getNamesSelected().get(0);
                tf_FirstVarLabel.setText(tempIndicator);
                listOfVars.delVarName(listOfVars.getNamesSelected());
                
                //boolean xVarType_Ok = true;
                strSelected = tf_FirstVarLabel.getText();
                varIndexForX = dm.getVariableIndex(strSelected);
                //xVarType_Ok = checkVarForCorrectType("Categorical");
            }
        });

        btnSelectY_Arrow.setOnAction((ActionEvent event) -> {
        if (printTheStuff) {
            System.out.println("... 211 RandomAssignment_Dialog, btnSelectY_Arrow.setOnAction");
        }
            variableNowChecking = 2;
            
            if (listOfVars.getNamesSelected().size() == 1) {
                String tempIndicator = listOfVars.getNamesSelected().get(0);
                tf_SecondVarLabel.setText(tempIndicator);
                listOfVars.delVarName(listOfVars.getNamesSelected());
                strSelected = tf_SecondVarLabel.getText();
                varIndexForY = dm.getVariableIndex(strSelected);                
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;
            strSelected = tf_FirstVarLabel.getText();
            varIndexForX = dm.getVariableIndex(strSelected);

            //  Check that both variables have been selected
            if (varIndexForX == -1) {
                //System.out.println("230 RandAssignDialog");
                MyAlerts.showNoSubjectsChosenAlert();
                boolGoodToGo = false;
            }
            
            if (boolGoodToGo) {
                strSelected = tf_SecondVarLabel.getText();
                varIndexForY = dm.getVariableIndex(strSelected);

                if ((varIndexForY == -1) && callingProc.equals("RandomAssign_RBD")){
                   MyAlerts.showNoBlockingVariableChosenAlert();
                   btnReset.fire();
                   boolGoodToGo = false;
                }
            }

            if ((varIndexForX > -1 && varIndexForY > -1) 
               || (varIndexForX > -1 && callingProc.equals("RandAssign_CRD"))){
                strVarLabels.add(dm.getVariableName(varIndexForX));
                al_OfColumns.add(dm.getSpreadsheetColumn(varIndexForX));
                
                if (callingProc.equals("RandomAssign_RBD")) {
                    strVarLabels.add(dm.getVariableName(varIndexForY));
                    al_OfColumns.add(dm.getSpreadsheetColumn(varIndexForY));
                }
            }   // end from 289
            else { boolGoodToGo = false; 
            }            
            if (!boolGoodToGo) { 
                setStrReturnStatus("Cancel");              

            } else { 
                setStrReturnStatus("OK");   // ###############################
            }
            if(boolGoodToGo) {
                subTitle = "SubTitle";
                setStrReturnStatus("OK");   // ################################
                close();
            }
            if (printTheStuff) {
                System.out.println("... 275 RandomAssignment_Dialog, bailed!!! strReturnStatus = " + getStrReturnStatus());
            }
        });       
    }  
    
    public boolean checkVarForCorrectType(String daCorrectType) {
        if (printTheStuff) {
            System.out.println("--- 278 RandomAssignment_Dialog, checkVarForCorrectType");
        }
        boolean isCorrectType = true;
        setStrReturnStatus("OK");   // ################################
        switch (variableNowChecking) {
            case 1:
                strSelected = tf_FirstVarLabel.getText();
                break;
                
            case 2:
                strSelected = tf_SecondVarLabel.getText();
                break;
                
            default:
                String switchFailure = "Switch failure: TwovarsDial 292 " + variableNowChecking;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        
        varIndex = dm.getVariableIndex(strSelected);
        
        switch (daCorrectType) {
            case "Quantitative":
                
                if(dm.getAllTheColumns().get(varIndex).getStrDataType().equals("Quantitative")) {
                    isCorrectType = true;
                }
                else {
                    isCorrectType = false;
                    strReturnStatus = "NonNumericValueDetected";
                }   
                break;
                
            case "Categorical":
                if(!dm.getAllTheColumns().get(varIndex).getStrDataType().equals("Quantitative")) {
                    isCorrectType = true;
                }
                else {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("I, SPLAT, am just checking here...");
                    alert.setHeaderText("The chosen variable looks numeric...");
                    alert.setContentText("This may not actually be incorrect; sometimes treatments" +
                                        "\nsuch as dosage level have values that are quantitative." +
                                        "\nAnd, sometimes not! So, are you OK with the values being" +
                                        "\nquantitative in appearance here?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        isCorrectType = true;
                        
                    } else {
                        isCorrectType = false;
                        strReturnStatus = "NumericValueDetected";   // ################################
                    }
                }   
                break;
                
            default:
                String switchFailure = "Switch failure: 335 RandomAssignment_Dialog " + daCorrectType;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }
        return isCorrectType;
    }
    
    public ArrayList<ColumnOfData> getData() { return al_OfColumns; } 

    public String getSubTitle() { return subTitle; }    
    public CheckBox[] getCheckBoxes() { return dashBoardOptions; }
    
    /*
    public String getStrReturnStatus() { 
        if (printTheStuff) {
            System.out.println("... 359 RandomAssignment_Dialog, getting StrReturnStatus: " + strReturnStatus);
        }    
        return strReturnStatus; 
    }  
    public void setStrReturnStatus(String toThis) { 
        if (printTheStuff) {
            System.out.println("... 365 RandomAssignment_Dialog, settingStrReturnStatus to " + toThis);
        }    
        strReturnStatus = toThis; 
    }
    
    public boolean getBoolReturnStatus() { 
        if (printTheStuff) {
            System.out.println("... 372 RandomAssignment_Dialog, getting StrReturnStatus: " + boolReturnStatus);
        }     
        return boolReturnStatus; }  
    
    public void setBoolReturnStatus(boolean toThis) { 
        if (printTheStuff) {
            System.out.println("... 378 RandomAssignment_Dialog, settingBoolReturnStatus to " + toThis);
        }
        boolReturnStatus = toThis; 
    }
*/
}
