/****************************************************************************
 *                        Venn_FullMonte                                     *
 *                            01/07/23                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import smarttextfield.SmartTextFieldDoublyLinkedSTF;
import smarttextfield.SmartTextFieldsController;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class Venn_FullMonte {
    
    // POJOs
    boolean bThreeProbsExist, bSwitchDefault, allProbsOK;
    int int_Event1, int_ProbOp, int_Event2, theCombo;
    
    double probA, probB, probAandB, probAorB;
    double probNotA, probNotB, probAandNotB, probNotAandB;
    double probAGivenB, probAGivenNotB, probBGivenA, probBGivenNotA, probNotAandNotB,
           probNotAGivenB, probNotAGivenNotB, probNotBGivenA, probNotBGivenNotA,
           probAorNotB, probNotAorB, probNotAorNotB; 
    
    double numer, denom, daProb;
     
    String ev1A, ev1NotA, ev1B, ev1NotB, opAnd, opOr, opGiven, ev2A, ev2NotA, 
           ev2B, ev2NotB, daDescr, probToCalc;    
    
    // My Classes
    SmartTextFieldDoublyLinkedSTF al_STF;
    SmartTextFieldsController stf_Controller;
    
    Venn_View venn_View;
    
    // FX Classes
    Button resetProbabilities;
    Color clr_Universe, clr_Text, clr_Yes;
    ColorPicker clrPicker_Text, clrPicker_Universe, clrPicker_Yes;
    ComboBox cb_Event1, cb_ProbOp, cb_Event2;
    GridPane gridOfProbs;
    HBox hBoxProbs, hBoxCombos, hBoxProbFraction, paneAndPickers, 
         univRow, successRow, textRow;
    Label lbl_TextColor, lbl_UnivColor, lbl_YesColor;
    Label lblFirstEvent, lblProbOp, lblSecondEvent;
    Pane paneForCombos, paneForGrid;
    
    Rectangle rectColorSuccessFrac, rectColorUniverseFrac, colorVinculum, 
              numberVinculum, aboveCV, belowCV, aboveNV, belowNV;

    Text descrOfProb, equals2, equals3, txtNumerator_SuccessFrac, 
         txtDenominator_UniverseFrac, txtDaProb;
    
    Label grProbA, grProbB, grProbAorB, grProbAandB;
    VBox root, colorPickersAndGrid, colorFrac, numberFrac;
    
    public Venn_FullMonte() {
        root = new VBox();       
        probA = 0.6; probB = 0.4; probAandB = 0.2; probAorB = 0.8;
        numer = 0.2; denom = 1.0; daProb = 0.2;
        theCombo = 2;
        hBoxProbFraction = new HBox();
        probToCalc = "Initializing";
        doSomeInitializations();
        resetTheProbStrings(); //  to blanks
        doTheProbs();
        createNewViews();
        
        cb_Event1.getSelectionModel().selectedIndexProperty()
                                     .addListener(this::cb_Event1_Changed);
        
        cb_ProbOp.getSelectionModel().selectedIndexProperty()
                                     .addListener(this::cb_ProbOp_Changed); 
        
        cb_Event2.getSelectionModel().selectedIndexProperty()
                                     .addListener(this::cb_Event2_Changed);  
    
        /*********************************************************************
        *      Trap the ENTER to see if probabilities can be calculated.     *
        *********************************************************************/   
        
        //  Prob(A)
        al_STF.get(0).getTextField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if ((ke.getCode() == KeyCode.ENTER) || (ke.getCode() == KeyCode.TAB)){
                    String tempString = al_STF.get(0).getText();
                    if (!tempString.isBlank() && !tempString.isEmpty()) {
                        if (checkForLegalProbability(tempString)) {
                            probA = Double.parseDouble(tempString);
                            System.out.println("117 Venn_FullMonte, probA = " + probA);
                            bThreeProbsExist = checkForThreeProbabilities();
                            if (bThreeProbsExist) {
                                doTheProbs();
                                makeNewGraphs();
                            }
                        }
                        else {
                            al_STF.get(0).setText("");
                        }
                    }
                }
            }
        });       
        
        //  Prob(B)
        al_STF.get(1).getTextField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if ((ke.getCode() == KeyCode.ENTER) || (ke.getCode() == KeyCode.TAB)){
                    String tempString = al_STF.get(1).getText();
                    if (!tempString.isBlank() && !tempString.isEmpty()) {
                        if (checkForLegalProbability(tempString)) {
                            probB = Double.parseDouble(tempString);
                            bThreeProbsExist = checkForThreeProbabilities();
                            if (bThreeProbsExist) {
                                doTheProbs();
                                makeNewGraphs();
                            }
                        }
                        else {
                            al_STF.get(1).setText("");
                        }
                    }
                }
            }
        });  
        
        //  Prob(A or B)
        al_STF.get(2).getTextField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if ((ke.getCode() == KeyCode.ENTER) || (ke.getCode() == KeyCode.TAB)){
                    String tempString = al_STF.get(2).getText();
                    if (!tempString.isBlank() && !tempString.isEmpty()) {
                        if (checkForLegalProbability(tempString)) {
                            probAorB = Double.parseDouble(tempString);
                            bThreeProbsExist = checkForThreeProbabilities();
                            if (bThreeProbsExist) {
                                doTheProbs();
                                makeNewGraphs();
                            }
                        }
                        else {
                            al_STF.get(2).setText("");
                        }
                    }
                }
            }
        });        
        
        // Prob (A and B)
        al_STF.get(3).getTextField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if ((ke.getCode() == KeyCode.ENTER) || (ke.getCode() == KeyCode.TAB)){
                    String tempString = al_STF.get(3).getText();
                    if (!tempString.isBlank() && !tempString.isEmpty()) {
                        if (checkForLegalProbability(tempString)) {
                            probAandB = Double.parseDouble(tempString);
                            bThreeProbsExist = checkForThreeProbabilities();
                            if (bThreeProbsExist) {
                                doTheProbs();
                                makeNewGraphs();
                            }
                        }
                        else {
                            al_STF.get(3).setText("");
                        }
                    }
                }
            }
        });  

        cb_Event1.getItems().addAll(ev1A, ev1NotA, ev1B, ev1NotB);
        cb_Event1.getSelectionModel().select(0);
       
        cb_ProbOp.getItems().addAll(opAnd, opOr, opGiven);
        cb_ProbOp.getSelectionModel().select(0);
        
        cb_Event2.getItems().addAll(ev2A, ev2NotA, ev2B, ev2NotB);
        cb_Event2.getSelectionModel().select(2);
 
        clrPicker_Universe = new ColorPicker(Color.AQUAMARINE);
        clr_Universe = clrPicker_Universe.getValue();
        
        clrPicker_Universe.setOnAction(e1 -> {
            clr_Universe = clrPicker_Universe.getValue();
            changeUniverseColorTo(clr_Universe);
            doTheDeedChoices();
            
        });        

        clrPicker_Text = new ColorPicker(Color.BLACK);
        clr_Text = clrPicker_Text.getValue();
        
        clrPicker_Text.setOnAction(e2 -> {
            clr_Text = clrPicker_Text.getValue();
            changeTextColorTo(clr_Text);
            doTheDeedChoices();
        });   
            
        clrPicker_Yes = new ColorPicker(Color.RED);
        clr_Yes = clrPicker_Yes.getValue();
        
        clrPicker_Yes.setOnAction(e3 -> {
            clr_Yes = clrPicker_Yes.getValue();
            changeYesColorTo(clr_Yes);
            doTheDeedChoices();
        }); 
            
        paneForCombos = new Pane();
        hBoxCombos = new HBox();      
        hBoxCombos.getChildren().addAll(lblFirstEvent, cb_Event1, 
                              lblProbOp , cb_ProbOp, 
                              lblSecondEvent, cb_Event2); 
        hBoxCombos.setPadding(new Insets(50, 0, 0, 200));
        paneForCombos.getChildren().add(hBoxCombos);
        
        univRow = new HBox(); 
        successRow = new HBox();
        textRow = new HBox();
        
       //  stfProbA 
        paneForGrid = new VBox();
        
        grProbA = new Label("P(A) = ");
        GridPane.setHalignment(grProbA, HPos.RIGHT);
        grProbA.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        al_STF.get(0).setPrefColumnCount(4);  
        
        // stfProbB         
        grProbB = new Label("P(B) = ");
        GridPane.setHalignment(grProbB, HPos.RIGHT);
        grProbB.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        al_STF.get(1).setPrefColumnCount(4);
        
        // stfProbAorB          
        grProbAorB = new Label("P(A or B) = ");  
        GridPane.setHalignment(grProbAorB, HPos.RIGHT);
        grProbAorB.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        al_STF.get(2).setPrefColumnCount(4);
        
        // stfProbAandB        
        grProbAandB = new Label("P(A and B) = ");
        GridPane.setHalignment(grProbAandB, HPos.RIGHT);
        grProbAandB.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        al_STF.get(3).setPrefColumnCount(4);
        
        // -------------------------------------------------------------
        // set probabilities to blanks
        resetProbabilities = new Button("Reset Probabilities");
        resetProbabilities.setPadding(new Insets(15, 15, 15, 15));
        resetProbabilities.setOnAction(e -> {
            resetTheProbStrings();     
        });

        // -------------------------------------------------------------
        
        gridOfProbs = new GridPane();
        gridOfProbs.add(grProbA, 0, 0);
        gridOfProbs.add(al_STF.get(0).getTextField(), 1, 0);
        gridOfProbs.add(grProbB, 2, 0);
        gridOfProbs.add(al_STF.get(1).getTextField(), 3, 0); 
        
        gridOfProbs.add(grProbAorB, 0, 1);
        gridOfProbs.add(al_STF.get(2).getTextField(), 1, 1);
        gridOfProbs.add(grProbAandB, 2, 1);
        gridOfProbs.add(al_STF.get(3).getTextField(), 3, 1);   
        gridOfProbs.add(resetProbabilities, 2, 2); 
        gridOfProbs.setHgap(5);
        gridOfProbs.setVgap(10);
        paneForGrid.getChildren().addAll(gridOfProbs  /*, resetProbabilities */);
        
        univRow.getChildren().addAll(lbl_UnivColor, clrPicker_Universe);
        successRow.getChildren().addAll(lbl_YesColor, clrPicker_Yes);
        textRow.getChildren().addAll(lbl_TextColor, clrPicker_Text);
        
        colorPickersAndGrid = new VBox(10);
        colorPickersAndGrid.getChildren().addAll(univRow, successRow, textRow, gridOfProbs);
        
        gridOfProbs.getTransforms().add(new Translate(25, 75));
        paneAndPickers = new HBox();
        removeThePaneAndPickersChoices();
        addThePaneAndPickersChoices();
        
        colorPickersAndGrid.getTransforms().add(new Translate(1, 100));

        rectColorSuccessFrac = new Rectangle(100, 25); 
        rectColorSuccessFrac.setFill(clr_Yes);
        rectColorUniverseFrac = new Rectangle(100, 25);
        rectColorUniverseFrac.setFill(clr_Universe);      
        
        colorVinculum = new Rectangle(100, 3);
        colorVinculum.setFill(Color.BLACK);
        numberVinculum = new Rectangle(60, 3);
        numberVinculum.setFill(Color.BLACK);
        
        aboveCV = new Rectangle(100, 3);
        aboveCV.setFill(Color.WHITE);
        belowCV = new Rectangle(100, 3);
        belowCV.setFill(Color.WHITE);

        aboveNV = new Rectangle(60, 3);
        aboveNV.setFill(Color.WHITE);
        belowNV = new Rectangle(60, 3);
        belowNV.setFill(Color.WHITE);

        colorFrac = new VBox();
        colorFrac.getChildren().addAll(rectColorSuccessFrac, aboveCV, colorVinculum, belowCV, rectColorUniverseFrac);
        
        numberFrac = new VBox();
        numberFrac.getChildren().addAll(txtNumerator_SuccessFrac, aboveNV, numberVinculum, belowNV, txtDenominator_UniverseFrac);
        
        
        hBoxProbFraction.getChildren().addAll(descrOfProb, colorFrac, equals2, numberFrac, equals3, txtDaProb);
        hBoxProbFraction.setAlignment(Pos.CENTER);
        hBoxProbFraction.getTransforms().add(new Translate(-170, 40));
        changeUniverseColorTo(clr_Universe);
        changeTextColorTo(clr_Text);
        root.getChildren().addAll(hBoxCombos, hBoxProbs, paneAndPickers, hBoxProbFraction);  
        hBoxProbFraction.setVisible(true);
    }  
    
    private boolean doTheProbs() {
        hBoxProbFraction.setVisible(true);
        /*********************************************************************
         *            probToCalc is the remaining blank probability          *
         ********************************************************************/
        switch(probToCalc) {
            case "A":
                probA = probAorB - probB + probAandB;
                al_STF.get(0).setText(String.format("%5.4f", probA));
            break;
            
            case "B":
                probB = probAorB - probA + probAandB;
                al_STF.get(1).setText(String.format("%5.4f", probB));
            break;
            
            case "AorB":
                probAorB = probA + probB - probAandB;
                al_STF.get(2).setText(String.format("%5.4f", probAorB));
            break;
            
            case "AandB":
                probAandB = probA + probB - probAorB;
                al_STF.get(3).setText(String.format("%5.4f", probAandB));
            break;
            
            case "Initializing":
               // No op
            break;     
            
            default:
                String switchFailure = "Switch failure: 373 Venn_FullMonte " + probToCalc;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }

        //  All 4 should be known at this point
        probNotA = 1.0 - probA;
        probNotB = 1.0 - probB;          
        probAandNotB = probA - probAandB;
        probNotAandB = probB - probAandB;
        probNotAandNotB = 1.0 - probAandB - probAandNotB - probNotAandB;
        probAorNotB = probA + probNotB - probAandNotB;
        probNotAorB = probNotA + probB - probNotAandB;
        probAGivenB = probAandB / probB;
        probAGivenNotB = probAandNotB / probNotB;        
        probBGivenA = probAandB / probA;
        probBGivenNotA = probNotAandB / probNotA;
        probNotAGivenB = probNotAandB / probB;
        probNotAGivenNotB = probNotAandNotB / probNotB; 
        probNotBGivenA = probAandNotB / probA;
        probNotBGivenNotA = probNotAandNotB / probNotA;
        probNotAorNotB = probNotA + probNotB - probNotAandNotB;
        
        if (probAandB >= probA) { 
            MyAlerts.showProbabilityOopsieAlert();
            return false; 
        }

        if (probAandB >= probB) { 
            MyAlerts.showProbabilityOopsieAlert();
            return false; 
        }  

        if (probAorB >= probA + probB) { 
            MyAlerts.showProbabilityOopsieAlert();
            return false; 
        }     

        
        allProbsOK = checkTheProbs();

        if (!allProbsOK) { 
            MyAlerts.showProbabilityOopsieAlert();
            return false; 
        }
 
        theCombo = 100 * int_Event1 + 10 * int_ProbOp + int_Event2;

        bSwitchDefault = false;
        hBoxProbFraction.setVisible(true);
        switch (theCombo) {
            case 0:
                // No op -- not completely sure why this might appear
            break;
            
            case   2:   // A and B
            case 200: 
                numer = probAandB; denom = 1.0;
            break;
            
            case   3: 
            case 300: 
                numer = probAandNotB; denom = 1.0;
            break;
            
            case  12:   // A or B
            case 210: 
                numer = probAorB; denom = 1.0;
           break;
            
            case  13: 
            case 310: 
                numer = probAorNotB; denom = 1.0;
           break;
           
            case 102: 
            case 201:  
                numer = probNotAandB; denom = 1.0;
            break;          
            
            case 122: 
                numer = probNotAandB; denom = probB;
            break;
                
            case 123: 
                numer = probNotAandNotB; denom = probNotB;
            break;
                
            case 22: 
                numer = probAandB; denom = probB;
            break;
                
            case 23: 
                numer = probAandNotB; denom = probNotB;
            break;
                
            case 220: 
                numer = probAandB; denom = probA;
            break;

            case 211: 
                numer = probNotAorB; denom = 1.0;
            break;
                
            case 221: 
                numer = probNotAandB; denom = probNotA;
            break;
            
            case 112: 
                numer = probNotA + probB - probNotAandB; denom = 1.0;
            break;
            
            case 103: 
            case 301:   
                numer = probNotAandNotB; denom = 1.0;
            break;    
                
            case 113:
            case 311:
                numer = probNotA + probNotB - probNotAandNotB; denom = 1.0;
                break;
                
            case 321:
                numer = probNotAandNotB; denom = probNotA;
                break;
                
            //  Silly choices
            default: 
                //MyAlerts.showSillyProbabilityAlert();
                bSwitchDefault = true;
                hBoxProbFraction.setVisible(true);
        }        

        if (!bSwitchDefault) {
            daProb = numer / denom;
            if (!isAProb(daProb)) { return false; }
            updateTheProbStrings(); 
        }
        return true;
    }
    
    private boolean checkForThreeProbabilities() {
        boolean a_IsaProb = false;
        boolean b_IsaProb = false;
        boolean aOrb_IsaProb = false;
        boolean aAndb_IsaProb = false;
        
        /*******************************************************************
         *   The checkForBlank() method is getting the probabilities from  *
         *   the SmartTextField b/c default probabilities are initialized  *
         *   in the constructor.  The SmartTextFields contain the current  *
         *   probabilities.                                                *
         ******************************************************************/
        int nProbs = 0;
        // Prob A
        if (checkForBlank(0)) { nProbs++; a_IsaProb = true; }
        // Prob B
        if (checkForBlank(1)) { nProbs++; b_IsaProb = true; }
         // Prob A or B
        if (checkForBlank(2)) { nProbs++; aOrb_IsaProb = true; }
        // Prob A and B
        if (checkForBlank(3)) { nProbs++; aAndb_IsaProb = true; }     

        if (nProbs == 4) {
            MyAlerts.showFourProbsShowingAlert();
            resetTheProbStrings();
            return false;
        }

        if (nProbs < 3) {
            return false;
        }

        // Which is false?
        if (!a_IsaProb) { probToCalc = "A"; return true; }
        if (!b_IsaProb) { probToCalc = "B"; return true; }
        if (!aOrb_IsaProb) { probToCalc = "AorB"; return true; }
        if (!aAndb_IsaProb) { probToCalc = "AandB"; return true; }
        return false;
    }
    
    private boolean checkForBlank(int thisProb) {
        String strProb = al_STF.get(thisProb).getText();
        if (strProb.isBlank() || strProb.isEmpty()) {
            return false;
        }
        return true;  
    }

    
    public boolean checkTheProbs() {
        if (!isAProb(probA)) { return false; }
        if (!isAProb(probB)) { return false; }
        if (!isAProb(probNotA)) { return false; }
        if (!isAProb(probNotB)) { return false; }
        if (!isAProb(probAGivenB)) { return false; }
        if (!isAProb(probBGivenA)) { return false; }
        if (!isAProb(probAorB)) { return false; }
        if (!isAProb(probAandB)) { return false; }
        if (!isAProb(probAandNotB)) { return false; }
        if (!isAProb(probNotAandB)) { return false; }
        if (!isAProb(probNotAandNotB)) { return false; }
        if (!isAProb(probAorNotB)) { return false; }
        if (!isAProb(probNotAorB)) { return false; }
        if (!isAProb(probNotAorNotB)) { return false; }
        if (!isAProb(probAGivenNotB)) { return false; }
        if (!isAProb(probBGivenNotA)) { return false; }
        if (!isAProb(probNotAGivenB)) { return false; }
        if (!isAProb(probNotAGivenNotB)) { return false; }
        if (!isAProb(probNotBGivenA)) { return false; }
        if (!isAProb(probNotBGivenNotA )) { return false; }
        if (probAandB > probA) { return false; }
        if (probAandB > probB) { return false; }
        return true;
    }
    
    public boolean isAProb(double iHope) {
        return ((0 < iHope) && (iHope < 1)); 
    }
    
    public Color getUniverseColor() { return clr_Universe; }
    
    private void changeUniverseColorTo (Color thisColor) {
        clr_Universe = thisColor;
        rectColorUniverseFrac.setFill(clr_Universe);
    }
    
    public Color getTextColor() { return clr_Text; }
    
    private void changeTextColorTo (Color thisColor) {
        clr_Text = thisColor; 
    }
    
    public Color getYesColor() { return clr_Yes; } 
    
    public void changeYesColorTo(Color thisColor) {
        clr_Yes = thisColor;
        rectColorSuccessFrac.setFill(clr_Yes);
    } 

    public void setTextDescription(String daDescr) {
        descrOfProb.setText("Probability of " + daDescr + " = ");
    }
    
    public void cb_Event1_Changed(ObservableValue<? extends Number> observable,
                Number oldValue, Number newValue) {
        int_Event1 = (int) newValue;
        updateComboChoices();
        if (bThreeProbsExist) {
            allProbsOK = doTheProbs();
            updateTheProbStrings();
        }
        doTheDeedChoices();
        
    };
        
    public void cb_ProbOp_Changed(ObservableValue<? extends Number> observable,
                Number oldValue, Number newValue) {
        int_ProbOp = (int) newValue;
        updateComboChoices();
        if (bThreeProbsExist) {
            allProbsOK = doTheProbs();
            updateTheProbStrings();
        }
        doTheDeedChoices();
    };
        
        
    public void cb_Event2_Changed(ObservableValue<? extends Number> observable,
                Number oldValue, Number newValue) {
        int_Event2 = (int) newValue;
        updateComboChoices();
        if (bThreeProbsExist) {
            allProbsOK = doTheProbs();
            updateTheProbStrings();
        }
        doTheDeedChoices();
    };
              
    private void doSomeInitializations() {
        clr_Yes = Color.RED;  
        clr_Universe = Color.AQUAMARINE;

        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set
        stf_Controller.setSize(4);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        al_STF.get(0).setText(Double.toString(probA));
        al_STF.get(1).setText(Double.toString(probB));
        al_STF.get(2).setText(Double.toString(probAorB));
        al_STF.get(3).setText(Double.toString(probAandB));

        daDescr = "A and B";
        descrOfProb = new Text(daDescr);
        descrOfProb.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        txtNumerator_SuccessFrac = new Text(""); 
        txtDenominator_UniverseFrac = new Text("");
        txtDaProb = new Text("");
        hBoxProbs = new HBox();
        lbl_TextColor = new Label("Text color: ");
        lbl_TextColor.setMinWidth(220);
        lbl_TextColor.setMaxWidth(220);
        lbl_TextColor.setAlignment(Pos.CENTER_RIGHT);
        lbl_TextColor.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lbl_UnivColor = new Label("Universe color: ");
        lbl_UnivColor.setMinWidth(220);
        lbl_UnivColor.setMaxWidth(220);
        lbl_UnivColor.setAlignment(Pos.CENTER_RIGHT);
        lbl_UnivColor.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lbl_YesColor = new Label("Success color: ");
        lbl_YesColor.setMinWidth(220);
        lbl_YesColor.setMaxWidth(220);
        lbl_YesColor.setAlignment(Pos.CENTER_RIGHT);
        lbl_YesColor.setFont(Font.font("Arial", FontWeight.BOLD, 20));  
        lblFirstEvent = new Label("First Event: ");
        lblFirstEvent.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblProbOp = new Label("Prob Op: ");
        lblProbOp .setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblSecondEvent = new Label("Second Event: ");
        lblSecondEvent.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        hBoxProbs.setLayoutX(50);
        hBoxProbs.setLayoutY(50); 

        cb_Event1 = new ComboBox<>();
        cb_ProbOp = new ComboBox<>(); 
        cb_Event2 = new ComboBox<>();

        ev1A = "A"; ev1NotA = "not A"; ev1B = "B"; ev1NotB = "not B";
        opAnd = "And"; opOr = " Or"; opGiven = "given ";
        ev2A = "A"; ev2NotA = "not A"; ev2B = "B"; ev2NotB = "not B";

        equals2 = new Text(" = ");
        equals2.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        equals3 = new Text(" = ");
        equals3.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    }
    
    private void resetTheProbStrings() {
        String tempBlanks = " ";
        txtNumerator_SuccessFrac.setText(tempBlanks);
        txtNumerator_SuccessFrac.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        txtDenominator_UniverseFrac.setText(tempBlanks);
        txtDenominator_UniverseFrac.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        txtDaProb.setText(tempBlanks);
        txtDaProb.setFont(Font.font("Arial", FontWeight.BOLD, 20)); 
        al_STF.get(0).setText(" ");
        al_STF.get(1).setText(" ");
        al_STF.get(2).setText(" ");
        al_STF.get(3).setText(" ");
    }

    private void updateTheProbStrings() {
        txtNumerator_SuccessFrac.setText(String.format("%6.4f", numer));
        txtNumerator_SuccessFrac.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        txtDenominator_UniverseFrac.setText(String.format("%6.4f", denom));
        txtDenominator_UniverseFrac.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        txtDaProb.setText(String.format("%6.4f", daProb));
        txtDaProb.setFont(Font.font("Arial", FontWeight.BOLD, 20)); 
    }
    
    // Needed by the Views
    public double getProbA() { return probA; }
    public double getProbNotA() { return probNotA; }
    public double getProbB() { return probB; }
    public double getProbNotB() { return probNotB; }
    public double getProbAandB() { return probAandB; }
    public double getProbAorB() { return probAorB; }
    public double getProbAandNotB() { return probAandNotB; }
    public double getProbAorNotB() { return probAorNotB; }
    public double getProbNotAandB() { return probNotAandB; }
    public double getProbNotAandNotB() { return probNotAandNotB; }
    public double getProbNotAorB() { return probNotAorB; }
    public double getProbNotAorNotB() { return probNotAorNotB; }
    public double getProbAGivenB() { return probAGivenB; }
    public double getProbBGivenA() { return probBGivenA; }
    public double getProbNotAGivenB() { return probNotAGivenB; }
    public double getProbNotBGivenA() { return probNotBGivenA; }
    public double getProbNotAGivenNotB() { return probNotAGivenNotB; }
    public double getProbNotBGivenNotA() { return probNotBGivenNotA; } 
    public int getTheCombo() { return theCombo; }

    
    public void updateComboChoices() {  
        theCombo = 100 * int_Event1 + 10 * int_ProbOp + int_Event2;
    }
    
    public boolean checkForLegalProbability(String strThisOne) {
        if (!DataUtilities.strIsADouble(strThisOne)) {
            MyAlerts.showGenericBadNumberAlert("number");
            return false;
        }
        double daDouble = Double.parseDouble(strThisOne);
        if (daDouble <=0 || daDouble >= 1.0) {
            MyAlerts.showIllegalProbabilityAlert();
            return false;
        }        
        return true;
    }
    
    public void doTheDeedChoices() { venn_View.doTheDeed(); }
     
    private void makeNewGraphs() {
        removeThePaneAndPickersChoices();
        createNewViews();
        addThePaneAndPickersChoices();
    }
    
    private void createNewViews() {
        venn_View = null; 
        venn_View = new Venn_View(this); 
    }
    
    private void removeThePaneAndPickersChoices() {
        paneAndPickers.getChildren().removeAll(venn_View.getPane(), colorPickersAndGrid);     
    }
    
    private void addThePaneAndPickersChoices() {
        venn_View.doTheDeed();
        paneAndPickers.getChildren().addAll(venn_View.getPane(), colorPickersAndGrid);       
    }
    
    public VBox getTheRoot() { return root; }    //Called by Main Menu
}
