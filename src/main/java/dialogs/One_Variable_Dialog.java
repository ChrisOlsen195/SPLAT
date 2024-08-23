/************************************************************
 *                    One_Variable_Dialog                   *
 *                          03/04/24                        *
 *                            09:00                         *
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
import splat.Data_Manager;
import splat.Var_List;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class One_Variable_Dialog extends Splat_Dialog {
    // POJOs

    boolean dmIsPresent;
    
    public int minVars, maxVars, varIndex, nCheckBoxes, minSampleSize;
    
    String strDataType, strVarLabel, strVarDescription, subTitle;

    // My classes
    public ColumnOfData columnOfData;
    Var_List listOfVars;    
    
    // POJOs / FX
    public Button resetButton, selectVariable;
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
        //System.out.println("58 One_Var_Dialog, constructing, dm not present");
        this.strDataType = strDataType;
        dmIsPresent = false;
        proceed();
    }

    public One_Variable_Dialog(Data_Manager dm, String strDataType) {
        super();
        this.dm = dm;
        dmIsPresent = true;
        this.strDataType = strDataType;
        proceed();
    }
    
    private String proceed() {
        //System.out.println("73 One_Var_Dialog, proceed()");
        strReturnStatus = "OK";
        boolGoodToGo = true;
        lbl_Title = new Label("One-variable dialog");
        lbl_Title.getStyleClass().add("dialogTitle");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));

        if (dmIsPresent) {
            //System.out.println("81 One_Var_Dialog, proceed() with dm present");
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
        
        if (dmIsPresent) {
            //System.out.println("140 One_Var_Dialog, proceed() with dm present");
            middlePanel.getChildren().add(vBoxVars2ChooseFrom);
            middlePanel.getChildren().add(rightPanel);
            middlePanel.setPadding(new Insets(10, 0, 10, 0));
        }
        
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        resetButton = new Button("Reset");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, resetButton);
        
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
        
        resetButton.setOnAction((ActionEvent event) -> {
            listOfVars.resetList();
            tf_labelOfVarSelected.setText("");
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });        
        
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
                resetButton.fire();
            }
             
            if (boolGoodToGo) {
                boolGoodToGo = checkVarForCorrectType(strDataType);
                
                if (!boolGoodToGo) { resetButton.fire(); }
            }
            
            if (boolGoodToGo) {
                varIndex = dm.getVariableIndex(strVarLabel);
                columnOfData = new ColumnOfData(dm.getSpreadsheetColumn(varIndex));
                strVarDescription = tf_DescriptionOfVarSelected.getText();
                
                if (StringUtilities.stringIsEmpty(strVarDescription) || StringUtilities.stringIsEmpty(strVarDescription))  {
                    strVarDescription = strVarLabel;
                }
                
                subTitle = "Variable: " + strVarDescription; 
                strReturnStatus = "OK";
                close();
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

        boolean varIsNumeric = dm.getAllTheColumns().get(varIndex).getIsNumeric();
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
    
    public String getLabelOfVariable() { return strVarLabel; }
    public String getDescriptionOfVariable() { return strVarDescription; }
    public String getSubTitle() {  return subTitle; }   
    
    public ColumnOfData getData() {  return columnOfData;  }
    
    public CheckBox[] getCheckBoxes() { return dashBoardOptions; }    
    public int getVarIndex() { return varIndex; }    
    // Used by regression -- change to ReturnStatus
    public boolean getOK() { return boolGoodToGo; }    
}
