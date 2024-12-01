/****************************************************************************
 *                    X2Assoc_Summary_Dialog                                * 
 *                          12/01/24                                        *
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
    DoublyLinkedSTF al_RowCol_STF, al_VarCat_STF;
    
    X2Assoc_Model x2Assoc_Model;
    X2Grid x2Grid;
    
    // POJOs / FX
    BorderPane borderPane_ObsValGrid;
    Button btnGoBack, btnClearControl, btnGoForward;    
    GridPane gridPaneRowCol, gridPaneVarCat;
    HBox hBoxWhereToNext;
    Label lbl_NColCats, lbl_NRowCats, lbl_variable1,
          lbl_RowVar, lbl_ColVar, lbl_variable2;
    Scene sceneAssocDialog;
    Text txt_Top, txt_Middle, txt_Bottom; 
    VBox vBoxVisControl;    

    public X2Assoc_SummaryDialog(X2Assoc_Model x2Assoc_Model) {   //  Constructor
        super();
        if (printTheStuff) {
            System.out.println("\n77 *** X2Assoc_SummaryDialog, constructing");
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
        if (printTheStuff) {
            System.out.println("104 --- X2Assoc_SummaryDialog, doX2ChosenPanel()");
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
        
        armDirectionsButtons();
        al_RowCol_STF.get(0).getSmartTextField().getTextField().requestFocus();
        vBoxVisControl.getChildren().addAll(txt_Top, gridPaneRowCol, hBoxWhereToNext);
        //System.out.println("138 X2Assoc_SummaryDialog, END doX2ChosenPanel()");
    }
    
    public void constructObservedValuesPanel() {
        if (printTheStuff) {
            System.out.println("147 --- X2Assoc_SummaryDialog, constructObservedValuesPanel()");
        }
        strCurControl = strObserved;
        
        setWidth(200 + 175 * nColCategories);
        if (nColCategories < 4)
            setWidth(625);
        
        setHeight(175 + 75 * nRowCategories);       

        x2Grid = new X2Grid(nRowCategories + 1, nColCategories + 1);
        
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
    }
  
    private void initializeUIComponents() {   
        if (printTheStuff) {
            System.out.println("197 --- X2Assoc_SummaryDialog, initializeUIComponents()");
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
                        switchFailure = "Switch failure: X2Assoc_Summary_Dialog 234 " + strCurControl;
                        MyAlerts.showUnexpectedErrorAlert(switchFailure);  
                    }   
            }
        });
           
        btnGoForward = new Button("Continue");
        btnGoForward.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
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
                        switchFailure = "Switch failure: X2Assoc_SummDial 305 " + strCurControl;
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
            switchFailure = "Switch failure: X2Assoc_SummaryDialog 374 " + strAssocType;
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
    HBox.setMargin(btnGoForward, margin);
    hBoxWhereToNext.getChildren().addAll(btnCancel, btnGoBack, btnClearControl, btnGoForward);

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
}    
    
    public void setCurrentFocusOn(int thisListArrayElement) {
        if (printTheStuff) {
            System.out.println("410 --- X2Assoc_SummaryDialog, setCurrentFocusOn()");
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
                switchFailure = "Switch failure: X2Assoc_SummaryDialog 425 " + strCurControl;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
            }
    }

    // 'Final' check on any data entry problems for the X2Chosen Panel
    private boolean checkOKChosen() {
        if (printTheStuff) {
            System.out.println("433 --- X2Assoc_SummaryDialog, checkOKChosen()");
        }
        boolean goForIt = true;
        boolean[] okToContinue = new boolean[4];    //  Yes, hard-coded
        okToContinue[0] = !al_RowCol_STF.get(0).isEmpty();
        okToContinue[1] = !al_RowCol_STF.get(2).isEmpty();
        okToContinue[2] = !al_RowCol_STF.get(1).isEmpty();
        okToContinue[3] = !al_RowCol_STF.get(3).isEmpty();
        
        for (int ithBoolean = 0; ithBoolean < 4; ithBoolean++) {            
            if (!okToContinue[ithBoolean]) {
                goForIt  = false;
            }
        }
        
        if (!goForIt) {
            MyAlerts.showMustBeNonBlankAlert();
            return false;
        }        
        return true;
    }
    
    public void constructCategoriesPanel()  {  
        if (printTheStuff) {
            System.out.println("457 --- X2Assoc_SummaryDialog, constructCategoriesPanel()");
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
    }   //  End constructCategoriesGridControl
    
    /***********************************************************************
     *        Formerly this code was in the X2Assoc_SummaryDialog_Obj      *
     **********************************************************************/
    private void doFormerDialogObj() {
        if (printTheStuff) {
            System.out.println("511 --- X2Assoc_SummaryDialog, doFormerDialogObj()");
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
    }

    private boolean checkOKCategoriesGrid() {
        if (printTheStuff) {
            System.out.println("539 --- X2Assoc_SummaryDialog, checkOKCategoriesGrid()");
        }
        //al_VarCat_STF.toString();
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
        
        strRowCats = new String[nRowCategories];
        strColCats = new String[nColCategories];
   
        for (int ithRowCategory = 0; ithRowCategory < nRowCategories; ithRowCategory++) {
            strRowCats[ithRowCategory] = al_VarCat_STF.get(ithRowCategory).getSmartTextField().getText();
        }
        
        for (int ithColumnCategory = 0; ithColumnCategory < nColCategories; ithColumnCategory++) { 
            strColCats[ithColumnCategory] = al_VarCat_STF.get(nRowCategories + ithColumnCategory).getSmartTextField().getText();
        }
        
        okToContinue = StringUtilities.checkForUniqueStrings(strRowCats);
        if (!okToContinue) {
            return false;
        }
        return StringUtilities.checkForUniqueStrings(strColCats);
    }
   
    public void armDirectionsButtons() {
        btnCancel.arm(); btnGoBack.arm(); 
        btnClearControl.arm(); btnGoForward.arm(); 
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
