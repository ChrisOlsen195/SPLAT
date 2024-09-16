/**************************************************
 *                 MLR_Reg_Dialog                 *
 *                   09/13/24                     *
 *                     00:00                      *
 *************************************************/
package dialogs.regression;

import dataObjects.ColumnOfData;
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

public class MLR_Dialog extends Splat_Dialog {
    // POJOs
    private boolean ok = true; 
    private int numSelected;  
    private int dvSelected = -1;    
    private int minSampleSize;
    private ArrayList<Integer> ivSelected; 

    private String DVText; 
    private ArrayList<String> strY, varLabel;    
    private ArrayList<String>[] strX;    
    
    // My classes
    ColumnOfData tempCol;
    Label titleLabel; 
    ArrayList<ColumnOfData> mlr_ColOfData;
    
    // POJOs / FX

    public MLR_Dialog(Data_Manager dm) {
        super(dm);
        // waldoFile = "MLR_Dialog";
        waldoFile = "";
        ivSelected = new ArrayList();
        varLabel = new ArrayList();

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);

        titleLabel = new Label("Multiple Logistic Regression");
        titleLabel.getStyleClass().add("dialogTitle");
        titleLabel.setPadding(new Insets(10, 10, 10, 10));
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(titleLabel, sepTitle);

        HBox hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setAlignment(Pos.CENTER);

        VBox vBoxList_1 = new VBox();
        vBoxList_1.setAlignment(Pos.TOP_LEFT);
        Label lblLabel_1 = new Label("Variables in Data:");
        lblLabel_1.setPadding(new Insets(0, 0, 5, 0));
        Var_List var_List_1 = new Var_List(dm, null, null);
        vBoxList_1.getChildren().add(lblLabel_1);
        vBoxList_1.getChildren().add(var_List_1.getPane());
        vBoxList_1.setPadding(new Insets(0, 10, 0, 10));
        hBoxMiddlePanel.getChildren().add(vBoxList_1);

        Button btnSelect_DV = new Button("===>");
        Button btnSelect_IV = new Button("===>");

        VBox vBoxList_2 = new VBox();
        vBoxList_2.setAlignment(Pos.TOP_LEFT);
        Label lblLabel_2 = new Label("Predictor Variable(s)");
        lblLabel_2.setPadding(new Insets(0, 0, 5, 0));
        Var_List var_List_2 = new Var_List(dm, 125.0, 125.0);
        var_List_2.clearList();
        vBoxList_2.getChildren().add(lblLabel_2);
        vBoxList_2.getChildren().add(var_List_2.getPane());

        VBox vBoxList_3 = new VBox();
        vBoxList_3.setAlignment(Pos.TOP_LEFT);
        Label lblLabel_3 = new Label("0/1 Outcome Variable");
        lblLabel_3.setPadding(new Insets(0, 0, 5, 0));
        TextField tf_IVText = new TextField("");
        tf_IVText.setPrefWidth(125.0);
        vBoxList_3.getChildren().addAll(lblLabel_3, tf_IVText);

        GridPane gridPaneRightPanel = new GridPane();
        gridPaneRightPanel.setHgap(10);
        gridPaneRightPanel.setVgap(15);
        gridPaneRightPanel.add(btnSelect_DV, 0, 0);
        gridPaneRightPanel.add(vBoxList_3, 1, 0);
        gridPaneRightPanel.add(btnSelect_IV, 0, 1);
        gridPaneRightPanel.add(vBoxList_2, 1, 1);
        GridPane.setValignment(btnSelect_DV, VPos.BOTTOM);
        GridPane.setValignment(btnSelect_IV, VPos.CENTER);
        gridPaneRightPanel.setPadding(new Insets(0, 10, 0, 0));

        hBoxMiddlePanel.getChildren().add(gridPaneRightPanel);
        hBoxMiddlePanel.setPadding(new Insets(10, 0, 10, 0));

        mainPanel.getChildren().add(hBoxMiddlePanel);
        Separator sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);

        HBox hBoxButtonPanel = new HBox(10);
        hBoxButtonPanel.setAlignment(Pos.CENTER);
        hBoxButtonPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("Compute");
        btnCancel.setText("Cancel");
        Button btnResetButton = new Button("Reset");
        hBoxButtonPanel.getChildren().addAll(btnOK, btnCancel, btnResetButton);

        mainPanel.getChildren().add(hBoxButtonPanel);
        Scene myScene = new Scene(mainPanel);
        myScene.getStylesheets().add(strCSS);

        setScene(myScene);

        btnResetButton.setOnAction((ActionEvent event) -> {
            var_List_1.resetList();
            var_List_2.clearList();
            tf_IVText.setText("");
            ivSelected.clear();
            numSelected = 0;
        });

        btnSelect_IV.setOnAction((ActionEvent event) -> {
            ArrayList<String> selected = var_List_1.getNamesSelected();
            ok = true;
            
            for (String tmpVar : selected) {
                
                if (!dm.getVariableIsNumeric(dm.getVariableIndex(tmpVar))) {
                    MyAlerts.showDataTypeErrorAlert();
                    btnResetButton.fire();
                    ok = false;
                }
            }

            int tempNum = var_List_2.getVarIndices().size();

            if (ok) {
                var_List_2.addVarName(selected);
                var_List_1.delVarName(selected);
            }
        });

        btnSelect_DV.setOnAction((ActionEvent event) -> {
            ok = true;
            
            if (var_List_1.getNamesSelected().size() == 1) {
                DVText = var_List_1.getNamesSelected().get(0);
                dvSelected = dm.getVariableIndex(DVText);
                tf_IVText.setText(DVText);
                var_List_1.delVarName(var_List_1.getNamesSelected());
            }
            
            if (!dm.getVariableIsNumeric(dvSelected)) {
                MyAlerts.showDataTypeErrorAlert();
                btnResetButton.fire();
                ok = false;
            }
        });

        btnOK.setOnAction((ActionEvent event) -> {
            ok = true;
            ivSelected = var_List_2.getVarIndices();
            numSelected = ivSelected.size();
            int dvPresent = 0;
            
            if (dvSelected > -1) {
                dvPresent = 1;
            }

            if ((numSelected < 2) || (dvPresent == 0)) {
                MyAlerts.showMultReg_TooFewVarsAlert();
                ok = false;
            }
            
            mlr_ColOfData = new ArrayList<>();

            if (ok) {
                strX = new ArrayList[numSelected];
                strY = new ArrayList();
                strY = dm.getSpreadsheetColumnAsStrings(dvSelected, -1, null);
                varLabel.add(dm.getVariableName(dvSelected));
                
                // The label for the variable and the ArrayList of strings
                tempCol = new ColumnOfData(dm, dm.getVariableName(dvSelected), "MLR_Dial", strY);
                // Column 0 contains the strY variable
                mlr_ColOfData.add(tempCol);

                minSampleSize = 5 * numSelected;
                for (int j = 0; j < numSelected; j++) {
                    varLabel.add(dm.getVariableName(ivSelected.get(j)));
                    strX[j] = new ArrayList();
                    strX[j] = dm.getSpreadsheetColumnAsStrings(ivSelected.get(j), -1, null);
                    tempCol = new ColumnOfData(dm, dm.getVariableName(ivSelected.get(j)), "MLRDial",  strX[j]);
                    mlr_ColOfData.add(tempCol);
                    
                    // Five legal values for this variable?
                    if (strX[j].size() < minSampleSize) { 
                        ok = false;
                    }
                }
              
                // Five legal values for the response variable?
                if (strY.size() < minSampleSize) {
                    ok = false;
                }
                
                if (!ok) {
                    MyAlerts.showSampleSizeTooSmallAlert(minSampleSize);
                } else {
                    strReturnStatus = "OK";
                    close();
                }
            }
        });

        setTitle("Multiple Logistic Regression");
    } // twoSampDialog
    
    public void setTitleLabel (String toThis) { titleLabel.setText(toThis);}   
    public String getSubTitle() { return "This is a subtitle"; }    
    public ArrayList<ColumnOfData> getData() { return mlr_ColOfData; }
    public ArrayList<String> getVarLabels() { return varLabel; }
    public ArrayList<String>[] getXMatrix() { return strX; }
    public ArrayList<String> getYMatrix() { return strY; }
    public int getNumVars() { return numSelected; }
} // class


