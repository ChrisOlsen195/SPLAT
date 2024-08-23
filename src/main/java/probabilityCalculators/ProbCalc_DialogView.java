/**************************************************
 *               ProbCalc_DialogView              *
 *                    11/27/23                    *
 *                     00:00                      *
 *************************************************/
package probabilityCalculators;

import genericClasses.DragableAnchorPane;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import smarttextfield.DoublyLinkedSTF;
import smarttextfield.SmartTextFieldsController;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.StringUtilities;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

    /****************************************
    *   stf_Mu              [0]             *
    *   stf_Sigma           [1]             *
    *   stf_df              [2]             *
    *   stf_Left_Prob       [3]             *
    *   stf_Mid_Prob        [4]             *
    *   stf_Right_Prob      [5]             *
    *   stf_Left_Stat       [6]             *
    *   stf_Right_Stat      [7]             *
    ****************************************/

public abstract class ProbCalc_DialogView extends BivariateScale_W_CheckBoxes_View {
    
    boolean okToGraph, leftTailChecked, midTailChecked, rightTailChecked, 
            distributionIsDefined, goodToGo;
    
    boolean printErrorAlerts;
    
    final int PROB_ROUND = 4;
    
    double dbl_Left_Prob, dbl_Mid_Prob, dbl_Left_Stat, dbl_Right_Prob, 
           dbl_Right_Stat, mu, sigma, dbl_df;
    
    String strTempString, str_Left_Prob, str_Mid_Prob, str_Right_Prob, 
           str_Left_Stat, str_Right_Stat;
    
    final String strDistParams = "Distribution Parameters";
    final String toBlank = "";
    
    // FX classes
    Button resetBtn;
    
    CheckBox chBoxLeftTail, chBoxTwoTail, chBoxRightTail;
    
    HBox probLabels, probFields, statLabels, statFields, paramDescr, paramStuff,
         chBoxBox;
    
    Label lbl_Left_Prob, lbl_Mid_Prob, lbl_Right_Prob, 
          lbl_Left_Stat, lbl_Right_Stat, lbl_df_Equals;
    
    Pane theContainingPane;
    Region spacers[];  
    Text txt_ProbTitle, txt_StatTitle, txtDistParams;
    VBox theHBoxes;  
    
    // My classes
    DoublyLinkedSTF al_ProbCalcs_STF;
    ProbCalc_Dashboard probCalc_Dashboard;
    SmartTextFieldsController stf_ProbCalcs_Controller;
 
    ProbCalc_DialogView(double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        //System.out.println("79 ProbCalc_DialogView, constructing");
        /***********************************************************
         *                 For debugging                           *
         **********************************************************/
        printErrorAlerts = false;
        /**********************************************************
        *                 For debugging                           *
        **********************************************************/        

        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        stf_ProbCalcs_Controller = new SmartTextFieldsController();
        stf_ProbCalcs_Controller.setSize(8);
        stf_ProbCalcs_Controller.finish_TF_Initializations();
        al_ProbCalcs_STF = stf_ProbCalcs_Controller.getLinkedSTF();
        al_ProbCalcs_STF.makeCircular();       
        lbl_Left_Prob = new Label("Left Tail");
        lbl_Left_Prob.setStyle(cssLabel_03);
        lbl_Left_Prob.setPadding(new Insets(5, 10, 5, 30));
        lbl_Mid_Prob = new Label("Middle");
        lbl_Mid_Prob.setStyle(cssLabel_03);
        lbl_Mid_Prob.setPadding(new Insets(5, 10, 5, 30));
        lbl_Right_Prob = new Label("Right Tail");
        lbl_Right_Prob.setStyle(cssLabel_03);
        lbl_Right_Prob.setPadding(new Insets(5, 10, 5, 25));
        lbl_Left_Stat = new Label("LeftStat");
        lbl_Left_Stat.setStyle(cssLabel_03);
        lbl_Left_Stat.setPadding(new Insets(5, 10, 5, 15));
        lbl_Left_Stat.setStyle(cssLabel_03);
        lbl_Left_Stat.setPadding(new Insets(5, 10, 5, 15));
        lbl_Right_Stat = new Label("RightStat"); 
        lbl_Right_Stat.setStyle(cssLabel_03);
        lbl_Right_Stat.setPadding(new Insets(5, 10, 5, 5));                
        txt_ProbTitle = new Text("      *****  Probabilities  *****");
        txt_ProbTitle.setStyle(cssLabel_04);
        txt_StatTitle = new Text("        *****  Statistics  *****");
        txt_StatTitle.setStyle(cssLabel_04);
        txtDistParams = new Text(strDistParams);        
    }
    
    void doLeftStatistic() { 
        //printAlert(120, "doLeftStatistic()");
        if (!distrIsDefined()) { 
            return; 
        }
        
        if (!DataUtilities.strIsADouble(al_ProbCalcs_STF.get(6).getText())) {   //  Checks for empty also
            //printAlert(126, "(!DataUtilities.strIsADouble(al_ProbCalcs_STF.get(6).getText()))");
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }

        str_Left_Stat = al_ProbCalcs_STF.get(6).getText();
        //System.out.println("At 132, str_Left_Stat = " + str_Left_Stat);
        if (str_Left_Stat.isEmpty()) { return; }
        
        dbl_Left_Stat = Double.parseDouble(str_Left_Stat); 

        if (dbl_Right_Prob > 0.0) { //  Is there a right prob?
            
            if (dbl_Left_Stat >= dbl_Right_Stat) {
                MyAlerts.showLeftRightOrderAlert();
                resetProbsAndStats(); 
                return;
            }
            
            if (dbl_Left_Prob + dbl_Right_Prob > 1.0) {
                MyAlerts.showIllegalProbabilityAlert();
                resetProbsAndStats();  
                return;
            }
            
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;      
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
        }
        
        fromLeftStatDoLeftProb(); 

        if (getMidTailChecked()) {
            if (dbl_Left_Prob > 0.5) {
                MyAlerts.showIllegalProbabilityAlert();
                resetProbsAndStats(); 
                return;
            }           
            dbl_Right_Prob = dbl_Left_Prob;
            str_Right_Prob = String.valueOf(dbl_Right_Prob);
            al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
            dbl_Right_Stat = mu + (mu - dbl_Left_Stat);
            str_Right_Stat = String.valueOf(dbl_Right_Stat);     
            al_ProbCalcs_STF.get(7).setText(roundDoubleToProbString(dbl_Right_Stat));
            dbl_Mid_Prob = (1.0 - 2.0 * dbl_Left_Prob);
            str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
            makeANewGraph(); 
            
        } else if (getLeftTailChecked() && getRightTailChecked()) { 
            dbl_Mid_Prob = (1.0 - dbl_Left_Prob - dbl_Right_Prob);
            str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
            
            if (!check4LegalSumOfProbabilities()) {
                MyAlerts.showIllegalProbabilityAlert();
                resetProbsAndStats(); 
                return;
            }
            makeANewGraph();
            
        }  else 
            if (getLeftTailChecked()) { 
            makeANewGraph();            
        }

        else {} 
    } 
        
    void doRightStatistic() { 
        if (!distrIsDefined()) { return; }
        
        if (!DataUtilities.strIsADouble(al_ProbCalcs_STF.get(7).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }

        str_Right_Stat = al_ProbCalcs_STF.get(7).getText();
        if (str_Right_Stat.isEmpty()) { return; }
        
        dbl_Right_Stat = Double.parseDouble(str_Right_Stat);
        
        if (dbl_Left_Prob > 0.0) { //  Is there a left prob?;            
            if (dbl_Left_Stat >= dbl_Right_Stat){
                MyAlerts.showLeftRightOrderAlert();
                resetProbsAndStats(); 
                return;
            }
            if (dbl_Left_Prob + dbl_Right_Prob > 1.0) {
                MyAlerts.showIllegalProbabilityAlert();
                resetProbsAndStats();  
                return;
            }
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));  
        } 

        fromRightStatDoRightProb(); 

        if (getMidTailChecked()) {            
            if (dbl_Right_Prob > 0.5) {
                MyAlerts.showIllegalProbabilityAlert();
                resetProbsAndStats(); 
                return;
            } 
   
            dbl_Left_Prob = dbl_Right_Prob;
            str_Left_Prob = String.valueOf(dbl_Left_Prob);
            al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
            dbl_Left_Stat = getInverseAreaToTheLeftOf(dbl_Left_Prob);  
            str_Left_Stat = String.valueOf(dbl_Left_Stat);     
            al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
            dbl_Mid_Prob = (1.0 - 2.0 * dbl_Right_Prob);
            str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
            makeANewGraph(); 
            
        } else 
            if (getLeftTailChecked() && getRightTailChecked()) { 
            dbl_Mid_Prob = (1.0 - dbl_Left_Prob - dbl_Right_Prob);
            str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
            if (!check4LegalSumOfProbabilities()) {
                //printAlert(266, "(!check4LegalSumOfProbabilities())");
                MyAlerts.showIllegalProbabilityAlert();
                return;
            }
            makeANewGraph();
            
        }  else 
            if (getRightTailChecked()) { 
                makeANewGraph();            
            }

        else {} 
    } 
    
    public void doLeftProbability() {
        if (!distrIsDefined()) { 
            //alertProblem = "DistrNotDefined";
            return; 
        }
        
        // ---------------------------------------------------------------------------------------------
        
        if (!DataUtilities.strIsADouble(al_ProbCalcs_STF.get(3).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }
        
        // ---------------------------------------------------------------------------------------------
        
        if (!DataUtilities.strIsAProb(al_ProbCalcs_STF.get(3).getText())) {   //  Checks for empty also
            MyAlerts.showIllegalProbabilityAlert();
            resetProbsAndStats(); 
            return;
        }
        
        str_Left_Prob = al_ProbCalcs_STF.get(3).getText();       
        dbl_Left_Prob = Double.parseDouble(str_Left_Prob);
        al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob)); 
        
        if (!al_ProbCalcs_STF.get(4).isEmpty() && al_ProbCalcs_STF.get(5).isEmpty()) {            
            dbl_Right_Prob = 1.0 - dbl_Left_Prob - dbl_Mid_Prob;
            str_Right_Prob = String.valueOf(dbl_Right_Prob);
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Right_Prob));
        }
        
        if (!al_ProbCalcs_STF.get(4).isEmpty()) {
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
            str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
        }        

        fromLeftProbDoLeftStat();
        if (getLeftTailChecked()) { 
            makeAllTheSTFs();
            makeANewGraph();                
        } 
        else {
            if (getMidTailChecked()) {
                fromLeftProbDoLeftStat();
                dbl_Right_Prob = dbl_Left_Prob;
                dbl_Right_Stat = getInverseAreaToTheRightOf(dbl_Right_Prob);
                al_ProbCalcs_STF.get(7).setText(roundDoubleToProbString(dbl_Right_Stat));
                dbl_Mid_Prob = (1.0 - 2.0 * dbl_Right_Prob);

                al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
                al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
                al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
                makeAllTheSTFs();                   
                makeANewGraph();
            }

            if (dbl_Right_Prob > 0.) {
                
                if (dbl_Left_Prob + dbl_Right_Prob > 1.0) {
                    MyAlerts.showIllegalProbabilityAlert();
                    resetProbsAndStats();  
                    return;
                }
                
                dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
                al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
            }
        }
    }
    
    void doMiddleProbability() {
        if (!distrIsDefined()) { return; }

        if (!DataUtilities.strIsADouble(al_ProbCalcs_STF.get(4).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }
        
        if (!DataUtilities.strIsAProb(al_ProbCalcs_STF.get(4).getText())) {   //  Checks for empty also
            MyAlerts.showIllegalProbabilityAlert();
            resetProbsAndStats(); 
            return;
        }        
        
        //  Save midProb before reset
        double temp_MidProb = Double.parseDouble(al_ProbCalcs_STF.get(4).getText());
        resetProbsAndStats();
        dbl_Mid_Prob = temp_MidProb;
        str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
        al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));    
        
        if (!al_ProbCalcs_STF.get(3).isEmpty() && al_ProbCalcs_STF.get(5).isEmpty()) {
            dbl_Right_Prob = 1.0 - dbl_Left_Prob - dbl_Mid_Prob;
            str_Right_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Right_Prob, PROB_ROUND));
            al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
        }        
        
        if (al_ProbCalcs_STF.get(3).isEmpty() && !al_ProbCalcs_STF.get(5).isEmpty()) {
            dbl_Left_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
            str_Left_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Left_Prob, PROB_ROUND));
            al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
        }    
        
        if (!check4LegalSumOfProbabilities()) {
            MyAlerts.showIllegalProbabilityAlert();
            return;
        }

        if (getMidTailChecked()) {
            chBoxLeftTail.setSelected(false);
            chBoxRightTail.setSelected(false);
            dbl_Mid_Prob = StringUtilities.convertStringToDouble(str_Mid_Prob);
            dbl_Left_Prob = (1.0 - dbl_Mid_Prob) / 2.0;
            dbl_Right_Prob = dbl_Left_Prob;
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
            al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
            fromLeftProbDoLeftStat();
            al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
            fromRightProbDoRightStat();
            makeAllTheSTFs();
            makeANewGraph();
        }
    }
    
    void doRightProbability() {
        if (!distrIsDefined()) { return; }

        if (!DataUtilities.strIsADouble(al_ProbCalcs_STF.get(5).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }
        
        if (!DataUtilities.strIsAProb(al_ProbCalcs_STF.get(5).getText())) {   //  Checks for empty also
            MyAlerts.showIllegalProbabilityAlert();
            resetProbsAndStats(); 
            al_ProbCalcs_STF.get(5).setText(toBlank);
            return;
        }       
       
        str_Right_Prob = al_ProbCalcs_STF.get(5).getText(); 
        dbl_Right_Prob = Double.parseDouble(str_Right_Prob);
        
        if (!al_ProbCalcs_STF.get(4).isEmpty() && al_ProbCalcs_STF.get(3).isEmpty()) {
            dbl_Left_Prob = 1.0 - dbl_Right_Prob - dbl_Mid_Prob;
            str_Left_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Left_Prob, PROB_ROUND));
            al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
        }    
        
        if (!al_ProbCalcs_STF.get(3).isEmpty()) {
            if (dbl_Left_Prob + dbl_Right_Prob > 1.0) {
                MyAlerts.showIllegalProbabilityAlert();
                resetProbsAndStats();  
                return;
            }
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
            str_Mid_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Mid_Prob, PROB_ROUND));
            al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
        } 
        //setOKToGraph(true);
        fromRightProbDoRightStat();
        
        if (getRightTailChecked()) { 
            makeAllTheSTFs();
            makeANewGraph();                
        } 
        else {
            if (getMidTailChecked()) {
                fromRightProbDoRightStat();
                dbl_Left_Prob = dbl_Right_Prob;
                dbl_Left_Stat = getInverseAreaToTheLeftOf(dbl_Left_Prob);
                dbl_Mid_Prob = (1.0 - 2.0 * dbl_Left_Prob);
                al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
                al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
                al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
                makeAllTheSTFs();                   
                makeANewGraph(); 
            }

            if (dbl_Left_Prob > 0.) {                
                if (dbl_Left_Prob + dbl_Right_Prob > 1.0) {
                    MyAlerts.showIllegalProbabilityAlert();
                    resetProbsAndStats();  
                    return;
                }
                dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
                al_ProbCalcs_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
            }
        } 
    }  
    
    public void chBoxLeftTailChanged(ObservableValue<? extends Boolean> observable,
        Boolean oldValue,
        Boolean newValue) {
        leftTailChecked = newValue;
        shadeLeftTail = leftTailChecked;
        
        if (leftTailChecked) {           
            if (midTailChecked) {
                chBoxTwoTail.setSelected(false); 
                midTailChecked = false;
                resetProbsAndStats();
            }
            else {  //  midTail NotChecked
               makeANewGraph(); 
            }
        }        
        else {  //  rightTail NotChecked
            shadeLeftTail = false;
            makeANewGraph(); 
        }   
    }         
    
    public void chBoxTwoTailChanged(ObservableValue<? extends Boolean> observable,
        Boolean oldValue,
        Boolean newValue) {
        resetProbsAndStats();
            midTailChecked = newValue;
            resetProbsAndStats();
    }
    
    public void chBoxRightTailChanged(ObservableValue<? extends Boolean> observable,
        Boolean oldValue,
        Boolean newValue) {
        rightTailChecked = newValue;
        shadeRightTail = rightTailChecked;
        
        if (rightTailChecked) {            
            if (midTailChecked) {
                chBoxTwoTail.setSelected(false); 
                midTailChecked = false;
                resetProbsAndStats();
            }
            else {  //  midTail NotChecked
               makeANewGraph(); 
            }
        }        
        else {  //  rightTail NotChecked
            shadeRightTail = false;
            makeANewGraph(); 
        }   
    } 
    
    public void resetProbsAndStats() {
        al_ProbCalcs_STF.get(3).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(6).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(4).getTextField().setText(toBlank);
        //  There is no stf_Mid_Stat
        al_ProbCalcs_STF.get(5).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(7).getTextField().setText(toBlank);
        dbl_Left_Prob = 0.0;
        dbl_Mid_Prob = 0.0;
        dbl_Right_Prob = 0.0;
        dbl_Left_Stat = 0.0;
        dbl_Right_Stat = 0.0;
        chBoxLeftTail.setSelected(false); 
        chBoxRightTail.setSelected(false); 
        shadeLeftTail = false;
        shadeRightTail = false;
        makeANewGraph();
    }
    
    public void makeItHappen() { theContainingPane = new Pane(); }
    
    public void completeTheDeal() {  
        setUpAnchorPane();
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();  
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .add(theHBoxes);        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public String roundDoubleToProbString(double daDouble) {
        return StringUtilities.roundDoubleToNDigitString(daDouble, PROB_ROUND);
    }
    
    public boolean check4LegalSumOfProbabilities() {
        double probSum = 0.0;
        
        if (DataUtilities.strIsAProb(al_ProbCalcs_STF.get(3).getText())) {
            probSum += dbl_Left_Prob;
        }
        
        if (DataUtilities.strIsAProb(al_ProbCalcs_STF.get(4).getText())) {
            probSum += dbl_Mid_Prob;
        }
        
        if (DataUtilities.strIsAProb(al_ProbCalcs_STF.get(5).getText())) {
            probSum += dbl_Right_Prob;
        }
        
        if (0.0 <= probSum && probSum <= 1.0) 
           return true;
        else {
           MyAlerts.showIllegalProbabilityAlert();
           return false;
        }
    }

    /***********************************************************
     *                 For debugging                           *
     **********************************************************/
/*        
    private void printAlert(int lineNumber, String message) {
        if (printErrorAlerts) {
            System.out.println("PrintAlert in ProbCalc_DialogView, called at line " + lineNumber);
            System.out.println("Message: " + message);
        }
    }
*/

    /**********************************************************
    *                 For debugging                           *
    **********************************************************/
      
    public boolean getLeftTailChecked() { return leftTailChecked; }
    public boolean getMidTailChecked() { return midTailChecked; }    
    public boolean getRightTailChecked() { return rightTailChecked; }
    
    /*******************************************************************
     *                      Abstract methods                           *                       
     ******************************************************************/
    
    public abstract boolean distrIsDefined();     
    public abstract double getAreaToTheLeftOf(double thisValue);
    public abstract double getAreaToTheRightOf(double thisValue);    
    public abstract double getInverseAreaToTheLeftOf(double thisValue);
    public abstract double getInverseAreaToTheRightOf(double thisValue);
    public abstract double getDensity(double atThisValue);   
    public abstract void fromLeftStatDoLeftProb();
    public abstract void fromRightStatDoRightProb(); 
    public abstract void fromLeftProbDoLeftStat();
    public abstract void fromRightProbDoRightStat();     
    public abstract void makeAllTheSTFs();
    public abstract void makeANewGraph();
    public abstract void constructGraphStatus();
}
