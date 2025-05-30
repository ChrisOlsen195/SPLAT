/****************************************************************************
 *                    X2Assoc_Summary_Dialog                                * 
 *                          01/22/25                                        *
 *                            00:00                                         *
 ***************************************************************************/
package dialogs.chisquare;

import chiSquare_Assoc.X2Assoc_Model;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import smarttextfield.*;
import chiSquare.*;
import dialogs.Splat_Dialog;
import utilityClasses.*;
        
/****************************************************************************
 *        GridPane is (column index, row index)                             *
 *   Var1 categories go DOWN the left column from row 1 to row nCategories  *
 *   Var2 categories go ACROSS the top row from col 1 to col nCategories    *
 ***************************************************************************/

public class X2Assoc_SummaryDialog extends Splat_Dialog {
    //  POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    int nRowCategories, nColCategories, nTotalCategories, nCols, nRows;
    int[][] observedValues;
    
    String switchFailure;
    
    //  These strings are for keeping track of which control is 'up.'
    final String strX2Chosen = "X2CHOSEN";
    final String strCategoriesGrid = "CATEGORIES";
    final String strObserved = "OBSERVED";  
    
    String strCurControl, strMiddle_Independence,
           strTop_Independence, strTop_Homogeneity, strTop_Experiment,
           strMiddle_Homogeneity, strMiddle_Experiment, strAssocType;
    String[] strRowCats, strColCats, strXValues, strYValues;
    
    // My classes
    SmartTextFieldsController stf_RowCol_Controller, stf_VarCat_Controller;
    SmartTextFieldDoublyLinkedSTF al_RowCol_STF, al_VarCat_STF;
    
    X2Assoc_Model x2Assoc_Model;
    X2_Grid x2Grid;
    
    // POJOs / FX
    BorderPane borderPane_ObsValGrid;
    Button btnGoBack, btnClearControl, btnContinue;    
    GridPane gridPaneRowCol, gridPaneVarCat;
    HBox hBoxWhereToNext;
    Label lbl_NColCats, lbl_NRowCats, lbl_variable1,
          lbl_RowVar, lbl_ColVar, lbl_variable2;
    Scene sceneAssocDialog;
    Text txt_Top, txt_Middle, txt_Bottom; 
    VBox vBoxVisControl;    

    public X2Assoc_SummaryDialog(X2Assoc_Model x2Assoc_Model) {   //  Constructor
        super();
        if (printTheStuff == true) {
            System.out.println("78 *** X2Assoc_SummaryDialog, Constructing");
        }
        this.x2Assoc_Model = x2Assoc_Model;
        strAssocType = x2Assoc_Model.getAssociationType();
        stf_RowCol_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set
        stf_RowCol_Controller.setSize(4);
        stf_RowCol_Controller.finish_TF_Initializations();
        al_RowCol_STF = stf_RowCol_Controller.getLinkedSTF();
        al_RowCol_STF.makeCircular();

        initializeUIComponents();   
        doX2ChosenPanel();
        
        setResizable(true);
        setWidth(640);
        setHeight(325);
    
        sceneAssocDialog = new Scene(vBoxVisControl);
        setScene(sceneAssocDialog);
    }
    
/****************************************************************************
 *                       Independence/Homogeneity                           * 
 ***************************************************************************/
    private void doX2ChosenPanel() {
        if (printTheStuff == true) {
            System.out.println("105 --- X2Assoc_SummaryDialog, doX2ChosenPanel()");
        }
        strCurControl = strX2Chosen;
        
        lbl_NRowCats.setText("nRow categories:");
        lbl_NRowCats.setPrefWidth(135);

        lbl_NColCats.setText("nCol categories:");
        lbl_NColCats.setPrefWidth(135);  
        
        gridPaneRowCol = new GridPane();
        gridPaneRowCol.setGridLinesVisible(false);   
        gridPaneRowCol.setPadding(new Insets(10, 10, 10, 10));
        gridPaneRowCol.setVgap(10);
        gridPaneRowCol.setHgap(10);    
   
        gridPaneRowCol.add(lbl_RowVar, 0, 1);
        al_RowCol_STF.get(0).getSmartTextField().setText("");
        gridPaneRowCol.add(al_RowCol_STF.get(0).getSmartTextField().getTextField(), 1, 1);       
        gridPaneRowCol.add(lbl_NRowCats, 2, 1);
        al_RowCol_STF.get(1).getSmartTextField().getTextField().setPrefWidth(30);
        al_RowCol_STF.get(1).getSmartTextField().getTextField().setText("");
        gridPaneRowCol.add(al_RowCol_STF.get(1).getSmartTextField().getTextField(), 3, 1);        
        gridPaneRowCol.add(lbl_ColVar, 0, 3);  
        al_RowCol_STF.get(2).getSmartTextField().setText("");
        gridPaneRowCol.add(al_RowCol_STF.get(2).getSmartTextField().getTextField(), 1, 3);
        gridPaneRowCol.add(lbl_NColCats, 2, 3);  
        al_RowCol_STF.get(3).getSmartTextField().getTextField().setPrefWidth(30);
        al_RowCol_STF.get(3).getSmartTextField().setText("");       
        gridPaneRowCol.add(al_RowCol_STF.get(3).getSmartTextField().getTextField(), 3, 3);
        if (printTheStuff == true) {
            System.out.println("136 --- X2Assoc_SummaryDialog, END doX2ChosenPanel()");
        }
        
        armDirectionsButtons();
        al_RowCol_STF.get(0).getSmartTextField().getTextField().requestFocus();
        vBoxVisControl.getChildren().addAll(txt_Top, gridPaneRowCol, hBoxWhereToNext);

        if (printTheStuff == true) {
            System.out.println("144 X2Assoc_SummaryDlg, al_RowCol_STF = ...");
            al_RowCol_STF.toString();
        }

        if (printTheStuff == true) {
            System.out.println("149 --- X2Assoc_SummaryDialog, END doX2ChosenPanel()");
        }
    }
    
    public void constructObservedValuesPanel() {
        if (printTheStuff == true) {
            System.out.println("155 --- X2Assoc_SummaryDialog, constructObservedValuesPanel()");
        }
        strCurControl = strObserved;
        
        setWidth(200 + 175 * nColCategories);
        if (nColCategories < 4)
            setWidth(625);
        
        setHeight(175 + 75 * nRowCategories);       

        x2Grid = new X2_Grid(nRowCategories + 1, nColCategories + 1);
        
        // Style for left colum and top row
        String theStyle = "-fxpadding: 10;" +
                          "-fx-border-style: solid inside;" + 
                          "-fx-font-weight: bold;" +
                          "-fx-border-width: 2;" +
                          "-fx-border-color: lightgrey;" +
                          "-fx-background-color: lightgrey;" +
                          "-fx-text-fill: black;";
        
        x2Grid.getTF_col_row(0, 0).setStyle(theStyle);
        x2Grid.getTF_col_row(0, 0).setEditable(false);
        x2Grid.getTF_col_row(0, 0).setText("Categories");

        x2Grid.getTF_col_row(0, 0).setAlignment(Pos.CENTER);
        
        for (int ithRowCategory = 0; ithRowCategory < nRowCategories; ithRowCategory++) {
            x2Grid.getTF_col_row(0, ithRowCategory+1).setText(strRowCats[ithRowCategory]);
            x2Grid.getTF_col_row(0, ithRowCategory + 1).setStyle(theStyle);
            x2Grid.getTF_col_row(0, ithRowCategory + 1).setEditable(false);
            x2Grid.getTF_col_row(0, ithRowCategory + 1).setAlignment(Pos.CENTER);
        }
               
        for (int ithColumnCategory = 0; ithColumnCategory < nColCategories; ithColumnCategory++) {  
            x2Grid.getTF_col_row(ithColumnCategory + 1, 0).setText(strColCats[ithColumnCategory]);
            x2Grid.getTF_col_row(ithColumnCategory + 1, 0).setStyle(theStyle);
            x2Grid.getTF_col_row(ithColumnCategory + 1, 0).setEditable(false);
            x2Grid.getTF_col_row(ithColumnCategory + 1, 0).setAlignment(Pos.CENTER);
        }

        borderPane_ObsValGrid = x2Grid.getGridPane();
        borderPane_ObsValGrid.setPadding(new Insets(0, 0, 0, 15));
        
        armDirectionsButtons();
        vBoxVisControl.getChildren().addAll(txt_Bottom, borderPane_ObsValGrid, hBoxWhereToNext);
        if (printTheStuff == true) {
            System.out.println("202 --- X2Assoc_SummaryDialog, END constructObservedValuesPanel()");
        }
    }
  
    private void initializeUIComponents() {   
        if (printTheStuff == true) {
            System.out.println("208 --- X2Assoc_SummaryDialog, initializeUIComponents()");
        }
    // **********************   Buttons  ***********************************
        btnCancel.setText("Return to Menu");
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                strReturnStatus = "Cancel";
                x2Assoc_Model.closeTheAssocDialog(false);
            }
        });
        
        btnClearControl = new Button("Clear Entries");
        btnClearControl.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {   
                if (printTheStuff == true) {
                    System.out.println("223 --- X2Assoc_SummaryDialog, btnClearControl pressed");
                }
                switch (strCurControl) {
                    case strX2Chosen: 
                        al_RowCol_STF.get(0).getSmartTextField().setText(""); 
                        al_RowCol_STF.get(1).getSmartTextField().setText("");
                        al_RowCol_STF.get(2).getSmartTextField().setText(""); 
                        al_RowCol_STF.get(3).getSmartTextField().setText("");
                        break;   

                    case strCategoriesGrid:
                        for (int ithCategory = 0; ithCategory < nTotalCategories; ithCategory++) {
                            al_VarCat_STF.get(ithCategory).getSmartTextField().setText("");                        
                        }
                        break;

                    case strObserved:
                        for (int i = 0; i < nRowCategories; i++) {
                            for (int j = 0; j < nColCategories; j++) {
                                x2Grid.getTF_col_row(j + 1, i + 1).setText("");
                            } 
                        } 
                        break;

                    default:
                        switchFailure = "Switch failure: X2Assoc_Summary_Dialog 246 " + strCurControl;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);  
                    }   
            }
        });
           
        btnContinue = new Button("Continue");
        btnContinue.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
        if (printTheStuff == true) {
            System.out.println("258 --- X2Assoc_SummaryDialog, Continue pressed");
        }
                switch (strCurControl) {
                    case strX2Chosen:                        
                        if (checkOKChosen()) {                          
                            vBoxVisControl.getChildren().removeAll(txt_Top, gridPaneRowCol, hBoxWhereToNext);  
                            constructCategoriesPanel();  
                        }
                        else {/* System.out.println("x2Chosen known false"); */}
                        break;   

                    case strCategoriesGrid:                        
                        if (checkOKCategoriesGrid()) {                        
                            vBoxVisControl.getChildren().removeAll(txt_Middle, gridPaneVarCat, hBoxWhereToNext);                
                            constructObservedValuesPanel();
                        }
                        else {/* System.out.println("Categories Grid known false"); */}
                        break;

                    case strObserved:   
                        /**************************************************
                         *  Check for all #s non-blank -- false = blank   *
                         *************************************************/
                        for (int i = 1; i <= nRowCategories; i++) {
                            for (int j = 1; j <= nColCategories; j++) {
                                if (!StringUtilities.check_TextField_4Blanks(x2Grid.getTF_col_row(j, i) )) {
                                    MyAlerts.showMissingDataAlert();
                                    return;
                                }
                            } 
                        }  
                        
                    default:
                        /* Ignore */

        /***************************************************************************
         *        Formerly, construct the object and send it to Parent             *
         **************************************************************************/
                        doFormerDialogObj();
                        strReturnStatus = "OK";
                        x2Assoc_Model.closeTheAssocDialog( true);
                    } 
                }
        });         
        
        btnGoBack = new Button("goBack");
        btnGoBack.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) { 
        if (printTheStuff == true) {
            System.out.println("307 --- X2Assoc_SummaryDialog, btnGoBack pressed");
        }               
                switch (strCurControl) {
                    case strX2Chosen: 
                        btnCancel.fire();
                        break;  // back to menu 

                    case strCategoriesGrid:
                        vBoxVisControl.getChildren().removeAll(txt_Middle, gridPaneVarCat, hBoxWhereToNext);              
                        doX2ChosenPanel();
                        break;

                    case strObserved:
                        vBoxVisControl.getChildren().removeAll(txt_Bottom, borderPane_ObsValGrid, hBoxWhereToNext); 
                        constructCategoriesPanel();
                        break;

                    default:
                        switchFailure = "Switch failure: X2Assoc_SummDial 326 " + strCurControl;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);   
                } 
            }
        });       
        
    // **********************   Strings and Text  ***********************************
    
        strTop_Independence = "\n\n         *****   Chi square test of independence   *****" +
                               "\n\n     In the fields below, indicate the two variables under study," +                
                               "\n     and also the number of categories for each variable. ";

        strTop_Homogeneity = "\n\n          *****   Chi square test of homogeneity   *****" +
                              "\n\n     In the fields below, generally describe the overall population" +                
                              "\n     under study, and the number of categories in the variable under " +
                              "\n     study.";     

        strTop_Experiment = "\n\n                   *****        Experimental study        *****" +
                             "\n\n     In the fields below, globally define the experimental variable," +                
                             "\n     the response variable, the number of treatments, and the number of" +
                             "\n     categorical response categories.";  


        strMiddle_Independence = "\n\n     *****   Chi square test of independence   *****" +
                                  "\n\n     In the fields below, indicate the specific values of the " +                
                                  "\n     two variables under study.";

        strMiddle_Homogeneity = "\n\n      *****   Chi square test of homogeneity   *****" +
                                 "\n\n     In the fields below, indicate the sub-populations and the" +                
                                 "\n     values of the categorical variable under study.";     

        strMiddle_Experiment = "\n\n                 *****       Experimental study            *****" +
                                "\n\n         In the fields below, indicate the specific treatments and " +                
                                "\n         categorical values of the response variable under study."; 

        txt_Bottom = new Text("\n\n      ******  In the fields below, enter the observed values.  *****\n\n");

    // **********************   Labels  *********************************** 
        lbl_RowVar = new Label("");              
        lbl_NRowCats = new Label(""); 
        lbl_ColVar = new Label("");
        lbl_NColCats = new Label("");
        lbl_variable1 = new Label("");
        lbl_variable2 = new Label("");        

        switch (strAssocType) {
            case "EXPERIMENT": 
                txt_Top = new Text(strTop_Experiment); 
                txt_Middle = new Text(strMiddle_Experiment);  
                lbl_RowVar.setText("  Responses: "); 
                lbl_ColVar.setText(" Treatments: "); 
                break;   

            case "HOMOGENEITY": 
                txt_Top = new Text(strTop_Homogeneity); 
                txt_Middle = new Text(strMiddle_Homogeneity);
                lbl_RowVar.setText("  Responses: "); 
                lbl_ColVar.setText("Populations: "); 
                break;  

            case "INDEPENDENCE": 
                txt_Top = new Text(strTop_Independence); 
                txt_Middle = new Text(strMiddle_Independence);
                lbl_RowVar.setText("Row variable: "); 
                lbl_ColVar.setText("Col variable: "); 
                break;

            default:
                switchFailure = "Switch failure: X2Assoc_SummaryDialog 393, strAssocType = " + strAssocType;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);             
        }

    // **********************   TextFields  ***********************************         
        al_RowCol_STF.get(1).setSmartTextField_MB_POSITIVEINTEGER(true);    
        al_RowCol_STF.get(3).setSmartTextField_MB_POSITIVEINTEGER(true); 
        al_RowCol_STF.get(0).getTextField().requestFocus();

     // **********************   HBoxes, VBoxes *******************************   
        vBoxVisControl = new VBox(); 
        hBoxWhereToNext = new HBox();
        hBoxWhereToNext.setAlignment(Pos.CENTER);
        // Insets top, right, bottom, left
        Insets margin = new Insets(5, 10, 0, 10);
        HBox.setMargin(btnCancel, margin);
        HBox.setMargin(btnGoBack, margin);
        HBox.setMargin(btnClearControl, margin);
        HBox.setMargin(btnContinue, margin);
        hBoxWhereToNext.getChildren().addAll(btnCancel, btnGoBack, btnClearControl, btnContinue);

         // *************************   Misc  ***********************************      
        Font CourierNew_14 = Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14);
        txt_Top.setFont(CourierNew_14);
        txt_Middle.setFont(CourierNew_14);
        txt_Bottom.setFont(CourierNew_14);

        lbl_RowVar.setFont(CourierNew_14);
        lbl_ColVar.setFont(CourierNew_14);
        lbl_NRowCats.setFont(CourierNew_14);
        lbl_NColCats.setFont(CourierNew_14);
        lbl_variable1.setFont(CourierNew_14); 
        lbl_variable2.setFont(CourierNew_14); 
        if (printTheStuff == true) {
            System.out.println("427 --- X2Assoc_SummaryDialog, END initializeUIComponents()");
        }
    }    
    
    public void setCurrentFocusOn(int thisListArrayElement) {
        if (printTheStuff == true) {
            System.out.println("433 *** ColumnOfData, setCurrentFocusOn()");
        }
        switch (strCurControl) {
            case strX2Chosen: 
                al_VarCat_STF.get(thisListArrayElement).getSmartTextField().getTextField().requestFocus();
            break;   

            case strCategoriesGrid:
                al_VarCat_STF.get(thisListArrayElement).getSmartTextField().getTextField().requestFocus();
            break;

            case strObserved:
            break;
            
            default:
                switchFailure = "Switch failure: X2Assoc_SummaryDialog 448, strCurControl = " + strCurControl;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
            }
    }

    // 'Final' check on any data entry problems for the X2Chosen Panel
    private boolean checkOKChosen() {
        if (printTheStuff == true) {
            System.out.println("456 --- X2Assoc_SummaryDialog, checkOKChosen()");
        }
        boolean goForIt = true;
        boolean[] okToContinue = new boolean[4];    //  Yes, hard-coded
        okToContinue[0] = !al_RowCol_STF.get(0).isEmpty();
        okToContinue[1] = !al_RowCol_STF.get(2).isEmpty();
        okToContinue[2] = !al_RowCol_STF.get(1).isEmpty();
        okToContinue[3] = !al_RowCol_STF.get(3).isEmpty();
        
        for (int ithBoolean = 0; ithBoolean < 4; ithBoolean++) {            
            if (okToContinue[ithBoolean]  == false) {
                goForIt  = false;
            }
        }

        if (printTheStuff == true) {
            System.out.println("472 --- X2Assoc_SummaryDialog, END checkOKChosen()");
        }        
        if (!goForIt) {
            MyAlerts.showMustBeNonBlankAlert();
            return false;
        }        
        return true;
    }
    
    public void constructCategoriesPanel()  {  
        if (printTheStuff == true) {
            System.out.println("483 --- X2Assoc_SummaryDialog, constructCategoriesPanel()");
        }
        strCurControl = strCategoriesGrid;
        gridPaneVarCat = new GridPane();
        gridPaneVarCat.setPadding(new Insets(10, 10, 10, 10));
        gridPaneVarCat.setVgap(5);
        gridPaneVarCat.setHgap(5);
        lbl_variable1.setText(al_RowCol_STF.get(0).getSmartTextField().getText());
        lbl_variable2.setText(al_RowCol_STF.get(2).getSmartTextField().getText());
              
        gridPaneVarCat.add(lbl_variable1, 0, 5); 
        gridPaneVarCat.add(lbl_variable2, 3, 4); 

        GridPane.setHalignment(lbl_NRowCats, HPos.LEFT);  
        GridPane.setHalignment(lbl_NColCats, HPos.LEFT);
        
        nRowCategories = al_RowCol_STF.get(1).getSmartTextInteger();
        nColCategories = al_RowCol_STF.get(3).getSmartTextInteger();
        nTotalCategories =  nRowCategories + nColCategories;   
        
        stf_VarCat_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set
        stf_VarCat_Controller.setSize(nTotalCategories);
        stf_VarCat_Controller.finish_TF_Initializations();
        al_VarCat_STF = stf_VarCat_Controller.getLinkedSTF();
        al_VarCat_STF.makeCircular();

        setWidth(275 + 100 * nColCategories);
        if (nColCategories < 3)
            setWidth(600);
        
        setHeight(225 + 120 * nRowCategories);
        
        for (int ithRowCategory = 0; ithRowCategory < nRowCategories; ithRowCategory++) {  
            gridPaneVarCat.add(al_VarCat_STF.get(ithRowCategory).getSmartTextField().getTextField(), 0, ithRowCategory + 6); 
        }
        
        for (int ithColumnCategory = 0; ithColumnCategory < nColCategories; ithColumnCategory++) {
            gridPaneVarCat.add(al_VarCat_STF.get(nRowCategories + ithColumnCategory).getSmartTextField().getTextField(), ithColumnCategory + 2, 5); 
        }
        
        al_VarCat_STF.get(0).getTextField().requestFocus();

        armDirectionsButtons();
        vBoxVisControl.getChildren().addAll(txt_Middle, gridPaneVarCat, hBoxWhereToNext);  
        al_VarCat_STF.get(0).getSmartTextField().getTextField().requestFocus();

        if (printTheStuff == true) {
            System.out.println("531 X2Assoc_SummaryDlg, al_VarCat_STF = ...");
            al_VarCat_STF.toString();
        }

        if (printTheStuff == true) {
            System.out.println("536 --- X2Assoc_SummaryDialog, END constructCategoriesPanel()");
        }
    }   
    
    /***********************************************************************
     *        Formerly this code was in the X2Assoc_SummaryDialog_Obj      *
     **********************************************************************/
    private void doFormerDialogObj() {
        if (printTheStuff == true) {
            System.out.println("545 --- X2Assoc_SummaryDialog, doFormerDialogObj()");
        }
        nRows = al_RowCol_STF.get(1).getSmartTextInteger();
        nCols = al_RowCol_STF.get(3).getSmartTextInteger();

        strYValues = new String[nRows];
        strXValues =  new String[nCols];
        
        observedValues = new int[nRows][nCols];
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            strYValues[ithRow] = al_VarCat_STF.get(ithRow).getText();
        }
        
        for (int ithCol = 0; ithCol < nCols; ithCol++) {  
            strXValues[ithCol] = al_VarCat_STF.get(nRows + ithCol).getText();
        }
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {            
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                observedValues[ithRow][jthCol] =
                        x2Grid.getGriddyWiddy_IJ(ithRow, jthCol);
            }
        } 
        if (printTheStuff == true) {
            System.out.println("570 --- X2Assoc_SummaryDialog, END doFormerDialogObj()");
        }
    }

    private boolean checkOKCategoriesGrid() {
        if (printTheStuff == true) {
            System.out.println("576 --- X2Assoc_SummaryDialog, checkOKCategoriesGrid()");
        }
        boolean okToContinue = true;
        
        for (int ithSTF = 0; ithSTF < al_VarCat_STF.getSize(); ithSTF++) {            
            if (al_VarCat_STF.get(ithSTF).isEmpty()) {
                okToContinue = false;
            }
        }

        if (!okToContinue) {
            MyAlerts.showMustBeNonBlankAlert();
            return false;
        }
        
        if (printTheStuff == true) {
            System.out.println("592 --- X2Assoc_SummaryDialog, nRowCategories = " + nRowCategories);
            System.out.println("593 --- X2Assoc_SummaryDialog, nColCategories = " + nColCategories);
        }
        
        strRowCats = new String[nRowCategories];
        strColCats = new String[nColCategories];
        for (int ithRowCategory = 0; ithRowCategory < nRowCategories; ithRowCategory++) {
            strRowCats[ithRowCategory] = al_VarCat_STF.get(ithRowCategory).getSmartTextField().getText();
            if (printTheStuff == true) {
                System.out.println("601 --- X2Assoc_SummaryDlg, strRowCats[ithRowCategory] = " + strRowCats[ithRowCategory]);
            }
        }
            
        for (int ithColumnCategory = 0; ithColumnCategory < nColCategories; ithColumnCategory++) {  
            strColCats[ithColumnCategory] = al_VarCat_STF.get(nRowCategories + ithColumnCategory).getSmartTextField().getText();
            if (printTheStuff == true) {
                System.out.println("608 --- X2Assoc_SummaryDlg, strColCats[ithColCategory] = " + strColCats[ithColumnCategory]);
            }
        }
        
        okToContinue = StringUtilities.checkForUniqueStrings(strRowCats);
        
        if (printTheStuff == true) {
            System.out.println("615 --- X2Assoc_SummaryDialog, END checkOKCategoriesGrid()");
        }
        if (!okToContinue) {
            return false;
        }
        return StringUtilities.checkForUniqueStrings(strColCats);
    }
   
    public void armDirectionsButtons() {
        btnCancel.arm(); btnGoBack.arm(); 
        btnClearControl.arm(); btnContinue.arm(); 
    }
       
    public int getNRows() { return nRows; }
    public int getNCols() { return nCols; }    
    public String getTopLabel() { return al_RowCol_STF.get(2).getText(); }
    public String getLeftLabel() { return al_RowCol_STF.get(0).getText(); }    
    public String[] getYValues() { return strYValues; }
    public String[] getXValues() { return strXValues; }   
    
   public int getObsVal_IJ(int ithRow, int jthCol) {
        return observedValues[ithRow][jthCol];
    }   
}
