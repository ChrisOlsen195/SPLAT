/************************************************************
 *                        Power_Dialog                      *
 *                          05/29/24                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs.power;

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
import splat.Var_List;
import dialogs.*;

/*****************************************************************
 *                    This is a super class!                     *
 ****************************************************************/
public class Power_Dialog extends Splat_Dialog {
    // POJOs
    private boolean ok;  
    
    public int minVars, maxVars, varIndexForX, nCheckBoxes, minSampleSize;

    public String strVarLabel, strMessageOfSomeSort;
    String strVarDescr, strSelected;
    // My classes
    public ColumnOfData colData;
    Var_List var_List;    
    
    // POJOs / FX
    public Button btnReset, btnSelectVariable;
    public CheckBox[] chBoxDashBoardOptions;
    public GridPane gridPaneChoicesMade;
    public HBox hBoxMiddlePanel, hBoxDataDescriptions;
    public VBox vBoxMainPanel, vBoxLeftPanel, vBoxRightPanel, vBoxVars2ChooseFrom, 
                vBoxVarChoices;
    public Label lblTitle, lblVarsInData, lblFirstVar, lblExplanVar; 
    public Label lblQuantReal, lblQuantInt, lblCategorical,
                 lblSelectionDirections;
    
    public Rectangle quantReal, quantInt, categorical;
    public TextField tfFirstVar, tfExplanVar;
    
    //  ***************** Vestigial!  ******************************
    //public Data_Manager dm;

    public Power_Dialog(String strMessageOfSomeSort) {
        //System.out.println("\n60 Power_Dialog, Constructing");
        //System.out.println("61 Power_Dialog, message of some sort = " + strMessageOfSomeSort);
        this.strMessageOfSomeSort = strMessageOfSomeSort;
        strReturnStatus = "Ok";
        ok = true;

        lblTitle = new Label("One-variable dialog");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        if (dm != null) {
            lblSelectionDirections = new Label("Please select the variable of interest below...");
            lblSelectionDirections.setPadding(new Insets(25, 0, 0, 0));      
            vBoxVars2ChooseFrom = new VBox();
            vBoxVars2ChooseFrom.setAlignment(Pos.TOP_LEFT);
            lblVarsInData = new Label("Variables in Data:");
            lblVarsInData.setPadding(new Insets(0, 0, 5, 0));
            var_List = new Var_List(dm, null, null);
            vBoxVars2ChooseFrom.getChildren().add(lblVarsInData);
            vBoxVars2ChooseFrom.getChildren().add(var_List.getPane());
            vBoxVars2ChooseFrom.setPadding(new Insets(0, 10, 0, 10));

            btnSelectVariable = new Button("===>");

            vBoxVarChoices = new VBox();
            vBoxVarChoices.setAlignment(Pos.TOP_LEFT);
            lblFirstVar = new Label();
            lblFirstVar.setPadding(new Insets(0, 0, 5, 0));
            tfFirstVar = new TextField("");
            tfFirstVar.setPrefWidth(125.0);
            vBoxVarChoices.getChildren().addAll(lblFirstVar, tfFirstVar);
        }
        
        lblExplanVar =   new Label(" Description of Variable: ");
        tfExplanVar = new TextField("Description of Variable");
        
        tfExplanVar.setPrefColumnCount(15);
        tfExplanVar.textProperty().addListener(this::changeExplanVar);
                
        if( dm != null) {
            gridPaneChoicesMade = new GridPane();
            gridPaneChoicesMade.setHgap(10);
            gridPaneChoicesMade.setVgap(15);
            gridPaneChoicesMade.add(btnSelectVariable, 0, 0);
            gridPaneChoicesMade.add(vBoxVarChoices, 1, 0);

            gridPaneChoicesMade.add(lblExplanVar, 0, 3);
            gridPaneChoicesMade.add(tfExplanVar, 1, 3);      

            GridPane.setValignment(btnSelectVariable, VPos.BOTTOM);
            gridPaneChoicesMade.setPadding(new Insets(0, 10, 0, 0));
        }
        
        vBoxLeftPanel = new VBox(10);
        vBoxLeftPanel.setAlignment(Pos.CENTER_LEFT);
        vBoxLeftPanel.setPadding(new Insets(0, 25, 0, 10));
        
        if (dm != null) {
            vBoxRightPanel = new VBox(10);
            vBoxRightPanel.setAlignment(Pos.CENTER_LEFT);
            vBoxRightPanel.setPadding(new Insets(0, 25, 0, 10));
            vBoxRightPanel.getChildren().add(gridPaneChoicesMade);
        }
        
        hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setAlignment(Pos.CENTER);
        hBoxMiddlePanel.getChildren().add(vBoxLeftPanel);    
        
        if (dm != null) {
            hBoxMiddlePanel.getChildren().add(vBoxVars2ChooseFrom);
            hBoxMiddlePanel.getChildren().add(vBoxRightPanel);
            hBoxMiddlePanel.setPadding(new Insets(10, 0, 10, 0));
        }
        
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        btnReset = new Button("Reset");
        buttonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
        
        vBoxMainPanel = new VBox();
        vBoxMainPanel.setAlignment(Pos.CENTER);    
        Separator sepTitle = new Separator();

        if (dm != null) {
            vBoxMainPanel.getChildren().addAll(lblTitle, sepTitle, lblSelectionDirections);   
        }
        vBoxMainPanel.getChildren().add(hBoxMiddlePanel);
        Separator sepButtons = new Separator();
        vBoxMainPanel.getChildren().add(sepButtons);    
        vBoxMainPanel.getChildren().add(buttonPanel);
        
        Scene myScene = new Scene(vBoxMainPanel);
        myScene.getStylesheets().add(strCSS);
        setScene(myScene);

        btnReset.setOnAction((ActionEvent event) -> {
            var_List.resetList();
            tfFirstVar.setText("");
        });

        if (dm != null) {
            btnSelectVariable.setOnAction((ActionEvent event) -> {
                
                if (var_List.getNamesSelected().size() == 1) {
                    String tempIndicator = var_List.getNamesSelected().get(0);
                    tfFirstVar.setText(tempIndicator);
                    var_List.delVarName(var_List.getNamesSelected());
                }
            });
        }

        btnOK.setOnAction((ActionEvent event) -> {
            ok = true;
            strSelected = tfFirstVar.getText();
            varIndexForX = dm.getVariableIndex(strSelected);
            
            if (varIndexForX == -1) { ok = false; }
            
            if (ok) {

                varIndexForX = dm.getVariableIndex(strSelected);
                colData = new ColumnOfData(dm.getSpreadsheetColumn(varIndexForX));

                strVarDescr = tfExplanVar.getText();
                strReturnStatus = "OK";
                close();
            } else {
                MyDialogs msgDiag = new MyDialogs();
                String msgTitle = "Alas and Alack!  I am bereft of needed information!";
                String msg = "I am unable to do any computation until you identify" + 
                             "\n a variable.  You have to do that arrow thing to" +
                             "\nselect a variable for analysis.";
                msgDiag.NoChoiceMessage(2, msgTitle, msg);             
            }  
        });       
    }
    
    public void changeExplanVar(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tfExplanVar.setText(newValue); 
    }
        
    public String getDescriptionOfVariable() { return strVarDescr; }
    
    public ColumnOfData getData() { return colData; }

    public CheckBox[] getCheckBoxes() { return chBoxDashBoardOptions; }
}

