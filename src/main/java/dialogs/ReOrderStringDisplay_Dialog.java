/**************************************************
 *              ReOrderStrings_Dialog             *
 *                    12/12/25                    *
 *                     12:00                      *
 *************************************************/
package dialogs;

import anova1.categorical.ANOVA1_Cat_Controller;
import anova1.quantitative.ANOVA1_Quant_Controller;
import chiSquare.GOF.X2GOF_Model;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import proceduresManyUnivariate.MultUni_Controller;
import proceduresTwoUnivariate.Explore_2Ind_Controller;
import the_t_procedures.Indep_t_Controller;
import utilityClasses.MyAlerts;

public class ReOrderStringDisplay_Dialog extends Splat_Dialog{
	
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nVariables;
    int[] stringOrder;
    String strDirections, daModel;
    String[] strVariableNames;

    // FX
    AnchorPane root;
    Button draggingButton, btnCompute, btnNoChange;
    static DataFormat buttonFormat;
    Font fntDirections;
    private GridPane gpStackPanes, gpDraggableButtons, gpPositions;
    private HBox hBoxForButtons;
    Insets spacerInsets;
    Label[] varPositions;
    Region[] spacer;
    Scene scene;
    StackPane[] rowOfSPs;
    Text txtDirections;
    MultUni_Controller multUni_Controller;
    ANOVA1_Cat_Controller anova1_Cat_Controller;
    ANOVA1_Quant_Controller anova1_Quant_Controller;
    Indep_t_Controller indep_t_Controller;
    Explore_2Ind_Controller explore_2Ind_Controller;
    X2GOF_Model x2GOF_Model;

public ReOrderStringDisplay_Dialog(MultUni_Controller multUni_Controller, String[] theOriginalOrder) {
    if (printTheStuff) {
        System.out.println("*** 71 ReOrderStringDisplay_Dialog, Constructing");
    }
    this.multUni_Controller = multUni_Controller;
    if (buttonFormat == null) {
        buttonFormat = new DataFormat("MyButton");
    }
    daModel = "MULT_UNI";
    nVariables = theOriginalOrder.length;
    strVariableNames = new String[nVariables];
    System.arraycopy(theOriginalOrder, 0, strVariableNames, 0, nVariables);
    doTheDialog();
}

public ReOrderStringDisplay_Dialog(ANOVA1_Cat_Controller anova1_Cat_Controller, String[] theOriginalOrder) {
    if (printTheStuff) {
        System.out.println("*** 86 ReOrderStringDisplay_Dialog, Constructing");
    }
    this.anova1_Cat_Controller = anova1_Cat_Controller;
    if (buttonFormat == null) {
        buttonFormat = new DataFormat("MyButton");
    }
    daModel = "ANOVA1_CAT";
    nVariables = theOriginalOrder.length;
    strVariableNames = new String[nVariables];
    System.arraycopy(theOriginalOrder, 0, strVariableNames, 0, nVariables);
    doTheDialog();
}

public ReOrderStringDisplay_Dialog(ANOVA1_Quant_Controller anova1_Quant_Controller, String[] theOriginalOrder) {
    if (printTheStuff) {
        System.out.println("*** 101 ReOrderStringDisplay_Dialog, Constructing");
    }
    this.anova1_Quant_Controller = anova1_Quant_Controller;
    if (buttonFormat == null) {
        buttonFormat = new DataFormat("MyButton");
    }
    daModel = "ANOVA1_QUANT";
    nVariables = theOriginalOrder.length;
    strVariableNames = new String[nVariables];
    System.arraycopy(theOriginalOrder, 0, strVariableNames, 0, nVariables);
    doTheDialog();
}

public ReOrderStringDisplay_Dialog(Explore_2Ind_Controller explore_2Ind_Controller, String[] theOriginalOrder) {
    if (printTheStuff) {
        System.out.println("*** 116 ReOrderStringDisplay_Dialog, Constructing");
    }
    this.explore_2Ind_Controller = explore_2Ind_Controller;
    if (buttonFormat == null) {
        buttonFormat = new DataFormat("MyButton");
    }
    daModel = "EXPLORE_2IND";
    nVariables = theOriginalOrder.length;
    strVariableNames = new String[nVariables];
    System.arraycopy(theOriginalOrder, 0, strVariableNames, 0, nVariables);
    doTheDialog();
}

public ReOrderStringDisplay_Dialog(Indep_t_Controller indep_t_Controller, String[] theOriginalOrder) {
    if (printTheStuff) {
        System.out.println("*** 131 ReOrderStringDisplay_Dialog, Constructing");
    }
    this.indep_t_Controller = indep_t_Controller;
    if (buttonFormat == null) {
        buttonFormat = new DataFormat("MyButton");
    }
    daModel = "INDEP_T";
    nVariables = theOriginalOrder.length;
    strVariableNames = new String[nVariables];
    System.arraycopy(theOriginalOrder, 0, strVariableNames, 0, nVariables);
    doTheDialog();
}

//  *******************************  Cancel OK  ************************
public ReOrderStringDisplay_Dialog(X2GOF_Model x2GOF_Model, String[] theOriginalOrder) {
    if (printTheStuff) {
        System.out.println("*** 147 ReOrderStringDisplay_Dialog, Constructing");
    }
    this.x2GOF_Model = x2GOF_Model;
    if (buttonFormat == null) {
        buttonFormat = new DataFormat("MyButton");
    }
    daModel = "X2_GOF";
    nVariables = x2GOF_Model.getNCategories();
    strVariableNames = new String[nVariables];
    System.arraycopy(theOriginalOrder, 0, strVariableNames, 0, nVariables);
    doTheDialog();
}

private void doTheDialog() {
        doInitialInits();
        doTheLoops();
        doButtons();

        hBoxForButtons.getChildren().addAll(spacer[0], btnCancel, 
                                               spacer[1], btnCompute, spacer[2], 
                                               btnNoChange, spacer[3]);

        for (int ithButton = 0; ithButton < nVariables; ithButton++) {
                                        Button b = new Button(strVariableNames[ithButton]);
                                        b.setId(String.valueOf(ithButton));
                                        b.setPrefHeight(35);
                                        b.setPrefWidth(100);
                                        gpDraggableButtons.add(b, ithButton,0);
                                        dragButton(b);
        }                                

        root.getChildren().addAll(txtDirections,
                                   gpStackPanes, 
                                   gpPositions, 
                                   gpDraggableButtons,
                                   hBoxForButtons);

        AnchorPane.setTopAnchor(txtDirections, 25.);
        AnchorPane.setTopAnchor(gpDraggableButtons, 225.);
        AnchorPane.setTopAnchor(gpStackPanes, 325.);
        AnchorPane.setTopAnchor(gpPositions, 365.);
        AnchorPane.setTopAnchor(gpPositions, 365.);
        AnchorPane.setTopAnchor(hBoxForButtons, 425.);

        AnchorPane.setLeftAnchor(txtDirections, 25.);
        AnchorPane.setLeftAnchor(gpDraggableButtons, 25.);
        AnchorPane.setLeftAnchor(gpStackPanes, 25.);
        AnchorPane.setLeftAnchor(hBoxForButtons, 75.);

        for (int ithSB = 0; ithSB < nVariables; ithSB++) {
            addDropHandling(rowOfSPs[ithSB]);
        }

        setScene(scene);
        //show();
    }

    private void doInitialInits() {
        root = new AnchorPane();
        gpStackPanes = new GridPane();
        gpPositions = new GridPane();
        gpDraggableButtons = new GridPane();
        spacer = new Region[4];
        spacerInsets = new Insets(0, 5, 0, 5);
        //  Width, Height
        scene = new Scene(root, 800, 500);
        rowOfSPs = new StackPane[nVariables];
        stringOrder = new int[nVariables];
        // default
        for (int ithVar = 0; ithVar < nVariables; ithVar++) {
            stringOrder[ithVar] = ithVar;
        }        
        
        strDirections = "                                       A non-Kantian Categorical Imperative!!!"
                           + "\n\nOK, so here's the deal.  I, SPLAT, am going to"
                           + " give you, USER, an opportunity to choose the"
                           + "\norder to display the distributions of data."
                           + " Human judgement being what it is, I doth tremble"
                           + "\nin anticipation.  Please, User, try to put these in an order"
                           + " that makes some semblance of sense. \n\nOr, you could just"
                           +" leave these in their current order. Your call..."; 

        txtDirections = new Text(strDirections);
        fntDirections = Font.font("Times New Roman", 20);
        txtDirections.setFont(fntDirections); 
        varPositions = new Label[nVariables]; 
        
        for (int ithSpacer = 0; ithSpacer < 4; ithSpacer++) {
            spacer[ithSpacer] = new Region(); 
            spacer[ithSpacer].setPrefWidth(25);
            HBox.setHgrow(spacer[ithSpacer], Priority.ALWAYS); 
        }
        
        hBoxForButtons = new HBox();
    }

    private void doTheLoops() {
        for (int ithIndex = 0; ithIndex < nVariables; ithIndex++) {
            varPositions[ithIndex] = new Label("         Pos   " + String.valueOf(ithIndex + 1) + " ");
            varPositions[ithIndex].setStyle("-fx-font-size: 18");   

            rowOfSPs[ithIndex] = new StackPane();
            rowOfSPs[ithIndex].setPrefHeight(35);
            rowOfSPs[ithIndex].setPrefWidth(100);
            rowOfSPs[ithIndex].setId(String.valueOf(ithIndex));

            rowOfSPs[ithIndex].setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, spacerInsets)));

            gpStackPanes.add(rowOfSPs[ithIndex], ithIndex, 0);
            gpPositions.add(varPositions[ithIndex], ithIndex, 0);        
        }
    }

    private void doButtons() {
        btnCompute = new Button("Compute");
        btnCompute.setStyle("-fx-text-fill: red;");
        btnCompute.setOnAction(e -> {
            if (printTheStuff == true) {
                System.out.println("266 --- ReOrderStringDisplay_Dialog, doButtons() daModel = " + daModel);
             }
            switch (daModel) {
                case "MULT_UNI":
                    multUni_Controller.copyTheReOrderDialog(stringOrder);
                    break;
                    
                case "ANOVA1_CAT":
                    anova1_Cat_Controller.copyTheReOrder(stringOrder);
                    break;
                    
                case "ANOVA1_QUANT":
                    anova1_Quant_Controller.copyTheReOrder(stringOrder);
                    break;
                    
                case "X2_GOF":
                    x2GOF_Model.closeTheReOrderDialog(stringOrder);
                    break;
                    
                case "EXPLORE_2IND":
                    explore_2Ind_Controller.copyTheReOrder(stringOrder);
                    break;
                    
                case "INDEP_T":
                    indep_t_Controller.copyTheReOrder(stringOrder);
                    break;
               
                default:
                    String switchFailure = "Switch failure: ReOrderStringDisplay_Dialog doButtons() 293 " + daModel;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                    break;
            }
                
        });

        btnNoChange = new Button("No Change");
        btnNoChange.setStyle("-fx-text-fill: red;");
        btnNoChange.setOnAction(e -> {    
            btnCompute.fire();
        });    
    }


    private void dragButton(Button b) {
        b.setOnDragDetected(e -> {
            Dragboard db = b.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(b.snapshot(null, null)); 
            ClipboardContent cc = new ClipboardContent();
            cc.put(buttonFormat, " "); 
            db.setContent(cc); 
            draggingButton = b;	
        });
     }

     private void addDropHandling(StackPane pane) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(buttonFormat) && draggingButton != null) {
                e.acceptTransferModes(TransferMode.MOVE);

            }
        });

        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();

            if (db.hasContent(buttonFormat)) {
                int ithDraggingButton = Integer.parseInt(draggingButton.getId());
                ((Pane)draggingButton.getParent()).getChildren().remove(draggingButton);
                pane.getChildren().add(draggingButton);
                // Only one child in the StackPane
                int targetOrder = Integer.parseInt(pane.getChildren().get(0).getParent().getId());
                stringOrder[targetOrder] = ithDraggingButton;
                e.setDropCompleted(true);
                draggingButton = null;
            }           
        });
    }
     
     public String getStrReturnStatus() { return strReturnStatus; }
}