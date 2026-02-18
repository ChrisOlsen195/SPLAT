/************************************************************
 *                    One_Variable_Dialog                   *
 *                          12/18/25                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import dataObjects.ColumnOfData;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.WindowEvent;
import splat.Data_Manager;
import splat.Var_List;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class One_Variable_Dialog extends Splat_Dialog {
    // POJOs

    boolean dmIsPresent;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public int minVars, maxVars, varIndex, nCheckBoxes, minSampleSize;
    
    String strDataType, strVarLabel, strVarDescription, subTitle, 
           strReturnStatus;

    // My classes
    public ColumnOfData columnOfData;
    Var_List listOfVars;    
    
    // POJOs / FX
    public Button btnReset, selectVariable;
    public CheckBox[] dashBoardOptions;
    public GridPane gridChoicesMade;
    public HBox middlePanel, dataDescriptions;
    public VBox mainPanel, leftPanel, rightPanel, vBoxVars2ChooseFrom, 
                vBoxVarChoices;
    public Label lbl_Title, lbl_VarsInData, lblFirstVar, lblExplanVar; 
    public Label lbl_QuantReal, lbl_QuantInt, lbl_Categorical,
                 selectionDirections;
    
    public Rectangle quantReal, quantInt, categorical;
    public TextField tf_labelOfVarSelected, tf_DescriptionOfVarSelected;
    
    public One_Variable_Dialog(String strDataType) {
        super();
        if (printTheStuff) {
            System.out.println("*** 64 One_Variable_Dialog, Constructing");
        }
        strReturnStatus = "OK";

        this.strDataType = strDataType;
        dmIsPresent = false;
        proceed();
    }

    public One_Variable_Dialog(Data_Manager dm, String strDataType) {
        super();
        if (printTheStuff) {
            System.out.println("*** 76 One_Variable_Dialog, Constructing");
        }
        strReturnStatus = "OK";

        this.dm = dm;
        dmIsPresent = true;
        this.strDataType = strDataType;
        strReturnStatus = proceed();
    }
    
    private String proceed() {
        if (printTheStuff) {
            System.out.println("*** 88 One_Variable_Dialog, proceed()");
        }
        strReturnStatus = "OK";
        boolGoodToGo = true;
        lbl_Title = new Label("One-variable dialog");
        lbl_Title.getStyleClass().add("dialogTitle");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));

        if (dmIsPresent) {
            if (printTheStuff) {
                System.out.println("--- 98 One_Variable_Dialog, proceed()");
            }
            selectionDirections = new Label("Please select the variable of interest below...");
            selectionDirections.setPadding(new Insets(25, 0, 0, 0));      
            vBoxVars2ChooseFrom = new VBox();
            vBoxVars2ChooseFrom.setAlignment(Pos.TOP_LEFT);
            lbl_VarsInData = new Label("Variables in Data:");
            lbl_VarsInData.setPadding(new Insets(0, 0, 5, 0));
            listOfVars = new Var_List(dm, null, null);
            vBoxVars2ChooseFrom.getChildren().add(lbl_VarsInData);
            vBoxVars2ChooseFrom.getChildren().add(listOfVars.getPane());
            vBoxVars2ChooseFrom.setPadding(new Insets(0, 10, 0, 10));

            selectVariable = new Button("===>");

            vBoxVarChoices = new VBox();
            vBoxVarChoices.setAlignment(Pos.TOP_LEFT);
            lblFirstVar = new Label();
            lblFirstVar.setPadding(new Insets(0, 0, 5, 0));
            tf_labelOfVarSelected = new TextField("");
            tf_labelOfVarSelected.setPrefWidth(125.0);
            vBoxVarChoices.getChildren().addAll(lblFirstVar, tf_labelOfVarSelected);
        } 
        
        lblExplanVar =   new Label(" Description of Variable: ");
        tf_DescriptionOfVarSelected = new TextField("");
        
        tf_DescriptionOfVarSelected.setPrefColumnCount(15);
        tf_DescriptionOfVarSelected.textProperty().addListener(this::changeExplanVar);     
        if (printTheStuff) {
            System.out.println("--- 128 One_Variable_Dialog, proceed()");
        }
        if(dmIsPresent) {
            gridChoicesMade = new GridPane();
            gridChoicesMade.setHgap(10);
            gridChoicesMade.setVgap(15);
            gridChoicesMade.add(selectVariable, 0, 0);
            gridChoicesMade.add(vBoxVarChoices, 1, 0);

            gridChoicesMade.add(lblExplanVar, 0, 3);
            gridChoicesMade.add(tf_DescriptionOfVarSelected, 1, 3);      

            GridPane.setValignment(selectVariable, VPos.BOTTOM);
            gridChoicesMade.setPadding(new Insets(0, 10, 0, 0));
        }
        
        leftPanel = new VBox(10);
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPadding(new Insets(0, 25, 0, 10));
        
        if (dmIsPresent) {
            rightPanel = new VBox(10);
            rightPanel.setAlignment(Pos.CENTER_LEFT);
            rightPanel.setPadding(new Insets(0, 25, 0, 10));
            rightPanel.getChildren().add(gridChoicesMade);
        }
       
        middlePanel = new HBox();
        middlePanel.setAlignment(Pos.CENTER);
        middlePanel.getChildren().add(leftPanel);    
        if (printTheStuff) {
            System.out.println("--- 159 One_Variable_Dialog, proceed()");
        }
        if (dmIsPresent) {
            middlePanel.getChildren().add(vBoxVars2ChooseFrom);
            middlePanel.getChildren().add(rightPanel);
            middlePanel.setPadding(new Insets(10, 0, 10, 0));
        }
        
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
        
        mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);    
        Separator sepTitle = new Separator();
        
        if (dmIsPresent) {
            mainPanel.getChildren().addAll(lbl_Title, sepTitle, selectionDirections);   
        }
        
        mainPanel.getChildren().add(middlePanel);
        Separator sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);    
        mainPanel.getChildren().add(buttonPanel);
        
        Scene myScene = new Scene(mainPanel);
        myScene.getStylesheets().add(strCSS);
        setScene(myScene);
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            if (printTheStuff == true) {
                System.out.println("--- 195 One_Variable_Dialog, setOnCloseRequest");
            }
            hide();
        });
        
        btnReset.setOnAction((ActionEvent event) -> {
            listOfVars.resetList();
            tf_labelOfVarSelected.setText("");
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            hide();
        });        
        if (printTheStuff) {
            System.out.println("--- 210 One_Variable_Dialog, btnCancel.setOnAction");
        }
        if (dmIsPresent) {
            selectVariable.setOnAction((ActionEvent event) -> {                
                if (listOfVars.getNamesSelected().size() == 1) {
                    tf_labelOfVarSelected.setText(listOfVars.getNamesSelected().get(0));
                    listOfVars.delVarName(listOfVars.getNamesSelected());
                    strVarLabel = tf_labelOfVarSelected.getText();
                    varIndex = dm.getVariableIndex(strVarLabel);
                }
            });
        }

        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;
            strVarLabel = tf_labelOfVarSelected.getText();
            varIndex = dm.getVariableIndex(strVarLabel);
            
             if (varIndex == -1) {
                MyAlerts.showBlankVariableAlert();
                boolGoodToGo = false;
                btnReset.fire();
            }
             
            if (boolGoodToGo) {
                boolGoodToGo = checkVarForCorrectType(strDataType);
                
                if (!boolGoodToGo) { btnReset.fire(); }
            }
            
            if (boolGoodToGo) {
                varIndex = dm.getVariableIndex(strVarLabel);
                columnOfData = new ColumnOfData(dm.getSpreadsheetColumn(varIndex));
                strVarDescription = tf_DescriptionOfVarSelected.getText();
                
                if (StringUtilities.isEmpty(strVarDescription) || StringUtilities.isEmpty(strVarDescription))  {
                    strVarDescription = strVarLabel;
                }
                
                subTitle = "Variable: " + strVarDescription; 
                strReturnStatus = "OK";
                hide();
            } 
        });   

        return strReturnStatus;
    }
    
    public void changeExplanVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tf_DescriptionOfVarSelected.setText(newValue); 
    }
    
    //        Type is "Quantitative" or "Categorical"
    public boolean checkVarForCorrectType(String daCorrectType) {
        boolean isCorrectType = true;
        strVarLabel = tf_labelOfVarSelected.getText();
        varIndex = dm.getVariableIndex(strVarLabel);   

        boolean varIsNumeric = dm.getAllTheColumns().get(varIndex).getDataType().equals("Quantitative");
        if (daCorrectType.equals("Quantitative") && !varIsNumeric) {
            isCorrectType = false;
            MyAlerts.showInappropriateNonNumericVariableAlert();
        }
        
        if (daCorrectType.equals("Categorical") && varIsNumeric) {
            isCorrectType = false;
            MyAlerts.showInappropriateNumericVariableAlert();
        }  
         
        if (daCorrectType.equals("Either")) {
            isCorrectType = true;
        }  

        return isCorrectType;
    }
    
    public String getStrReturnStatus() { return strReturnStatus; }
    public String getLabelOfVariable() { return strVarLabel; }
    public String getDescriptionOfVariable() { return strVarDescription; }
    public String getSubTitle() {  return subTitle; }   
    
    public ColumnOfData getData() {  return columnOfData;  }
    
    public CheckBox[] getCheckBoxes() { return dashBoardOptions; }    
    public int getVarIndex() { return varIndex; }    
    // Used by regression -- change to ReturnStatus
    public boolean getOK() { return boolGoodToGo; }    
}
