/****************************************************************************
 *                       ProbText_FullMonte                                 *
 *                            01/07/23                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import smarttextfield.DoublyLinkedSTF;
import smarttextfield.SmartTextFieldsController;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;


public class ProbText_FullMonte {
    
    // POJOs
boolean bThreeProbsExist, allProbsOK;
    int int_Event1, int_ProbOp, int_Event2, last_of_2or3, theCombo;
    
    double probA, probB, probAandB, probAorB, probNotAorNotB;
    double probNotA, probNotB, probAandNotB, probAGivenNotB, probBGivenA, 
           probBGivenNotA, probNotAandNotB, probNotAGivenB, probNotAGivenNotB,
           probNotBGivenA, probNotBGivenNotA,probAorNotB, probNotAorB;   
    double probNotAandB, probAGivenB;
    
     String probToCalc;
    
    // My Classes
    DoublyLinkedSTF al_STF;
    SmartTextFieldsController stf_Controller; 
    ProbText_View probText_View;
    
    // FX Classes
    Button resetProbabilities;
    GridPane gridOfProbs;
    HBox hBoxProbs, paneAndPickers;
    Text txtProbText_Title;
    Pane paneForCombos, paneForGrid;

    Text descrOfProb, equals2, equals3;
    
    Label grProbA, grProbB, grProbAorB, grProbAandB;
    VBox root, colorPickersAndGrid;
    
    public ProbText_FullMonte() {
        root = new VBox();       
        //probA = 0.3;  Dummy default for initialization   
        probB = 0.4;
        probAandB = 0.2;
        probAorB = 0.5;
        probToCalc = "A";   //  To pass the 3-probabilities test
        doSomeInitializations();
        allProbsOK = doTheProbs();
  
        createNewViews();
        
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
        
        paneForCombos = new Pane();
        paneForCombos.getChildren().add(txtProbText_Title);
           
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
        
        // set probabilities to blanks
        resetProbabilities = new Button("Reset Probabilities");
        resetProbabilities.setPadding(new Insets(15, 15, 15, 15));
        resetProbabilities.setOnAction(e -> {
            resetTheProbStrings();     
        });

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
        paneForGrid.getChildren().addAll(gridOfProbs);
        
        colorPickersAndGrid = new VBox(10);
        colorPickersAndGrid.getChildren().add(gridOfProbs);
        
        gridOfProbs.getTransforms().add(new Translate(25, 75));
        paneAndPickers = new HBox();
        removeThePaneAndPickersChoices();
        addThePaneAndPickersChoices();
        
        colorPickersAndGrid.getTransforms().add(new Translate(1, 100));
        
        root.getChildren().addAll(paneAndPickers);  
    }  
    
    
    private boolean doTheProbs() {
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
                String switchFailure = "Switch failure: 260 ProbText_FullMonte " + probToCalc;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);       
        }

        //  All 4 should be known at this point
        probNotA = 1.0 - probA;
        probNotB = 1.0 - probB;          
        probAandNotB = probA - probAandB;
        probNotAandB = probB - probAandB;
        
        probAorNotB = probA + probNotB - probAandNotB;
        probNotAorB = probNotA + probB - probNotAandB;
        probAGivenB = probAandB / probB;
        probAGivenNotB = probAandNotB / probNotB;        
        probBGivenA = probAandB / probA;
        probBGivenNotA = probNotAandB / probNotA;
        probNotAGivenB = probNotAandB / probB;

        probNotBGivenA = probAandNotB / probA;
        probNotAorNotB = 1.0 - probAandB;
        probNotAandNotB = 1.0 - probAorB;
        probNotAGivenNotB = probNotAandNotB / probNotB;
        probNotBGivenNotA = probNotAandNotB / probNotA; 

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
        if (!isAProb(probAGivenNotB)) { return false; }
        if (!isAProb(probBGivenNotA)) { return false; }
        if (!isAProb(probNotAGivenB)) { return false; }
        if (!isAProb(probNotAGivenNotB)) { return false; }
        if (!isAProb(probNotBGivenA)) { return false; }
        if (!isAProb(probNotBGivenNotA )) { return false; }
        if (!isAProb(probNotAorNotB)) { return false; }
        return true;
    }
    
    public boolean isAProb(double iHope) {
        return ((0 < iHope) && (iHope < 1)); 
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
        
    public void doSomeInitializations() {
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

        descrOfProb = new Text();
        descrOfProb.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        hBoxProbs = new HBox();
        
        txtProbText_Title = new Text(150., 55., "Probability Calculation Results");
        txtProbText_Title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        txtProbText_Title.setStroke(Color.BLACK);
        txtProbText_Title.setFill(Color.BLACK); 

        hBoxProbs.setLayoutX(50);
        hBoxProbs.setLayoutY(50); 

        equals2 = new Text(" = ");
        equals2.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        equals3 = new Text(" = ");
        equals3.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    }
    
    private void resetTheProbStrings() {
        al_STF.get(0).setText(" ");
        al_STF.get(1).setText(" ");
        al_STF.get(2).setText(" ");
        al_STF.get(3).setText(" ");
    }
    
    // Needed by the views
    public double getProbA() { return probA; }
    public double getProbNotA() { return probNotA; }
    public double getProbB() { return probB; }
    public double getProbNotB() { return probNotB; }
    public double getProbAandB() { return probAandB; }
    public double getProbAorB() { return probAorB; }
    public double getProbAandNotB() { return probAandNotB; }
    public double getProbAorNotB() { return probAorNotB; }
    public double getProbNotAandB() { return probNotAandB; }
    public double getProbNotAorB() { return probNotAorB; }
    public double getProbAGivenB() { return probAGivenB; }
    public double getProbBGivenA() { return probBGivenA; }
    public double getProbNotAGivenB() { return probNotAGivenB; }
    public double getProbNotBGivenA() { return probNotBGivenA; }
    public double getProbNotAGivenNotB() { return probNotAGivenNotB; }
    public double getProbNotBGivenNotA() { return probNotBGivenNotA; } 
    public double getProbNotAorNotB() { return probNotAorNotB; }
    public double getProbNotAandNotB() { return probNotAandNotB; }
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
     
    private void makeNewGraphs() {
        removeThePaneAndPickersChoices();
        createNewViews();
        addThePaneAndPickersChoices();
    }
    
    private void createNewViews() {
        probText_View = null; ;  
        probText_View = new ProbText_View(this);       
    }
    
    private void removeThePaneAndPickersChoices() {
        paneAndPickers.getChildren().removeAll(probText_View.getPane(), colorPickersAndGrid);
    }
    
    private void addThePaneAndPickersChoices() {
        paneAndPickers.getChildren().addAll(probText_View.getPane(), colorPickersAndGrid);    
    }
    
    public int getLastComboPressed() { return last_of_2or3; }
    
    public VBox getTheRoot() { return root; }
}
