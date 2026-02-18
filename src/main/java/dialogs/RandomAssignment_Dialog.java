/************************************************************
 *                    RandomAssignment_Dialog               *
 *                          12/12/25                        *
 *                            12:00                         *
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
import splat.Data_Manager;
import splat.Var_List;
import utilityClasses.MyAlerts;

public class RandomAssignment_Dialog extends Splat_Dialog {
    // POJOs
    private boolean quantLabelCheckedAlready; 
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private int varIndex, varIndexForX, varIndexForY, variableNowChecking; 
    protected int nCheckBoxes;
    
    private String /*callingProc,*/ strSelected;
    private String subTitle;
    
    public String wtf_NullChangeQuery;
    private ArrayList<String> strVarLabels;

    // My classes
    private ArrayList<ColumnOfData> al_OfColumns;
    protected Var_List listOfVars;    
    
    // POJOs / FX
    private final Button selectXVariable, selectYVariable;
    protected Button resetButton;
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
        if (printTheStuff) {
            System.out.println("*** 68 RandomAssignment_Dialog, Constructing");
        }
        strReturnStatus = "OK";
        quantLabelCheckedAlready = false;
        boolGoodToGo = true;
        al_OfColumns = new ArrayList<>();   
        strVarLabels = new ArrayList<>();
        lbl_Title = new Label("RandomAssignment_Dialog");
        lbl_Title.getStyleClass().add("dialogTitle");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));

        quantLabelCheckedAlready = false;
        
        vBoxVars2ChooseFrom = new VBox();
        vBoxVars2ChooseFrom.setAlignment(Pos.TOP_LEFT);
        lbl_VarsInData = new Label("Variables in File:");
        lbl_VarsInData.setPadding(new Insets(0, 0, 5, 0));
        listOfVars = new Var_List(dm, null, null);
        vBoxVars2ChooseFrom.getChildren().add(lbl_VarsInData);
        vBoxVars2ChooseFrom.getChildren().add(listOfVars.getPane());
        vBoxVars2ChooseFrom.setPadding(new Insets(0, 10, 0, 10));
        
        selectXVariable = new Button("===>");
        selectYVariable = new Button("===>");

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
        
        if (!callingProc.equals("RandAssign_CRD")) {
            vBoxYVarChoices.getChildren().addAll(lblSecondVar, tf_SecondVarLabel);
        }

        gridChoicesMade = new GridPane();
        gridChoicesMade.setHgap(10);
        gridChoicesMade.setVgap(15);
        gridChoicesMade.add(selectXVariable, 0, 0);
        gridChoicesMade.add(vBoxXVarChoices, 1, 0);
        
        if (!callingProc.equals("RandAssign_CRD")) {
            gridChoicesMade.add(selectYVariable, 0, 1);
        }
        
        gridChoicesMade.add(vBoxYVarChoices, 1, 1);
        
        GridPane.setValignment(selectXVariable, VPos.BOTTOM);
        GridPane.setValignment(selectYVariable, VPos.BOTTOM);
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
        resetButton = new Button("Reset");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, resetButton);
        
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

        resetButton.setOnAction((ActionEvent event) -> {
            listOfVars.resetList();
            tf_FirstVarLabel.setText("");
            tf_SecondVarLabel.setText("");
        });

        selectXVariable.setOnAction((ActionEvent event) -> {
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

        selectYVariable.setOnAction((ActionEvent event) -> {
            variableNowChecking = 2;
            
            if (listOfVars.getNamesSelected().size() == 1) {
                String tempIndicator = listOfVars.getNamesSelected().get(0);
                tf_SecondVarLabel.setText(tempIndicator);
                listOfVars.delVarName(listOfVars.getNamesSelected());
                
                //boolean yVarType_Ok = true;
                strSelected = tf_SecondVarLabel.getText();
                varIndexForY = dm.getVariableIndex(strSelected);                
                //yVarType_Ok = checkVarForCorrectType("Categorical");
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;
            strSelected = tf_FirstVarLabel.getText();
            varIndexForX = dm.getVariableIndex(strSelected);

            //  Check that both variables have been selected
            if (varIndexForX == -1) {
                System.out.println("207 RandAssignDialog");
                MyAlerts.showNoSubjectsChosenAlert();
                //resetButton.fire();
                boolGoodToGo = false;
            }
            
            if (boolGoodToGo == true) {
                strSelected = tf_SecondVarLabel.getText();
                varIndexForY = dm.getVariableIndex(strSelected);

                if ((varIndexForY == -1) && !callingProc.equals("RandAssign_CRD")){
                   System.out.println("217 RandAssignDialog");
                   MyAlerts.showNoBlockingVariableChosenAlert();
                   resetButton.fire();
                   boolGoodToGo = false;
                }
            }

            if (boolGoodToGo) {
                //if (strDataType_1.equals("Categorical")) {
                    ColumnOfData col_x = dm.getAllTheColumns().get(varIndexForX);
                    col_x.cleanTheColumn(dm, varIndexForX);
                //}

                if (!callingProc.equals("RandAssign_CRD")) {
                    ColumnOfData col_y = dm.getAllTheColumns().get(varIndexForY);
                    col_y.cleanTheColumn(dm, varIndexForY);
                }  
            } 
            
            if ((varIndexForX > -1 && varIndexForY > -1) 
               || (varIndexForX > -1 && callingProc.equals("RandAssign_CRD"))){
                strVarLabels.add(dm.getVariableName(varIndexForX));
                al_OfColumns.add(dm.getSpreadsheetColumn(varIndexForX));
                
                if (!callingProc.equals("RandAssign_CRD")) {
                    strVarLabels.add(dm.getVariableName(varIndexForY));
                    al_OfColumns.add(dm.getSpreadsheetColumn(varIndexForY));
                }
            }
            else { boolGoodToGo = false; }
            
            if (!boolGoodToGo) { strReturnStatus = "Cancel";
            } else { strReturnStatus = "OK"; }

            if(boolGoodToGo) {
                subTitle = "SubTitle";
                strReturnStatus = "OK";
                close();
            }
        });       
    }
    
    public boolean checkVarForCorrectType(String daCorrectType) {
        boolean isCorrectType = true;
        strReturnStatus = "OK";
        
        switch (variableNowChecking) {
            case 1:
                strSelected = tf_FirstVarLabel.getText();
                break;
                
            case 2:
                strSelected = tf_SecondVarLabel.getText();
                break;
                
            default:
                String switchFailure = "Switch failure: TwovarsDial 276 " + variableNowChecking;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        
        varIndex = dm.getVariableIndex(strSelected);
        
        switch (daCorrectType) {
            case "Quantitative":
                
                if(dm.getAllTheColumns().get(varIndex).getDataType().equals("Quantitative")) {
                    isCorrectType = true;
                }
                else {
                    isCorrectType = false;
                    strReturnStatus = "NonNumericValueDetected";
                }   
                break;
                
            case "Categorical":
                if(!dm.getAllTheColumns().get(varIndex).getDataType().equals("Quantitative")) {
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
                        strReturnStatus = "NumericValueDetected";
                    }
                }   quantLabelCheckedAlready = true;
                break;
                
            default:
                String switchFailure = "Switch failure: 319 RandomAssignment_Dialog " + daCorrectType;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }
        return isCorrectType;
    }
    
    public ArrayList<ColumnOfData> getData() { return al_OfColumns; }   
    public String getSubTitle() { return subTitle; }    
    public CheckBox[] getCheckBoxes() { return dashBoardOptions; }
}
