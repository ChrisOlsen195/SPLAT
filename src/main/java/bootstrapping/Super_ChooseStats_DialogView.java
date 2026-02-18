/**************************************************
 *            Super_ChooseStats_DialogView        *
 *                    08/19/25/25                    *
 *                     15:00                      *
 *************************************************/

/***************************************************************
 *     STFs and dbl_STFs are:                                  *
 *     [0] mu                                                  *
 *     [1] sigma                                               *
 *     [3] left probability                                    *
 *     [4] middle probability                                  *
 *     [5] right probability                                   *
 *     [6] left-mid boundary                                   *
 *     [7] mid-right boundary                                  *
 *                                                             *
 ***************************************************************/
package bootstrapping;

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
import smarttextfield.SmartTextFieldDoublyLinkedSTF;
import smarttextfield.SmartTextFieldsController;
import splat.Data_Manager;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.*;

public abstract class Super_ChooseStats_DialogView extends BivariateScale_W_CheckBoxes_View {
    
    boolean twoTail_IsChecked, leftTail_IsChecked, rightTail_IsChecked, 
            shadeLeft, shadeRight; 
    
    final int PROB_ROUND = 4;
    
    double dbl_Left_Prob, dbl_Mid_Prob, dbl_Left_Stat, dbl_Right_Prob, 
           dbl_Right_Stat_Original, leftPercentile_Original, rightPercentile_Original,
           dbl_Right_Stat_Shifted, leftPercentile_Shifted, rightPercentile_Shifted;
    
    String str_Left_Prob, str_Mid_Prob, str_Right_Prob, 
           str_Left_Stat, str_Right_Stat_Original, str_Right_Stat_Shifted;

    final String toBlank = "";
    
    // Make empty if no-print
    //String waldoFile = "Super_ChooseStats_DialogView";
    String waldoFile = "";
    
    // FX classes
    Button resetBtn;
    
    CheckBox chBoxLeftTail, chBoxTwoTail, chBoxRightTail;
    
    HBox probLabels, probFields, statLabels, statFields, chBoxBox;
    
    Label lbl_Left_Prob, lbl_Mid_Prob, lbl_Right_Prob, 
          lbl_Left_Stat, lbl_Right_Stat;
    
    Pane theContainingPane;
    Region spacers[];  
    Text txt_ProbTitle, txt_StatTitle; //, txtDistParams;
    VBox theHBoxes;  
    
    // My classes
    Data_Manager dm;
    SmartTextFieldDoublyLinkedSTF al_STF;
    ChooseStats_Dashboard chooseStats_Dashboard;
    ChooseStats_DistrModel originalDistrModel;
    ChooseStats_DistrModel shiftedDistrModel;
    ChooseStats_Controller chooseStats_Controller;
    SmartTextFieldsController stf_Controller;
 
    Super_ChooseStats_DialogView( ChooseStats_Dashboard chooseStats_Dashboard,
                                  double placeHoriz, double placeVert,
                                  double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        chooseStats_Controller = chooseStats_Dashboard.get_Boot_Controller();
        dm = chooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(86, waldoFile, "Constructing");
        originalDistrModel = chooseStats_Controller.get_Boot_OriginalDistrModel();
        shiftedDistrModel = chooseStats_Controller.get_Boot_ShiftedDistrModel();
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        stf_Controller = new SmartTextFieldsController();
        stf_Controller.setSize(8);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();       
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
        txt_ProbTitle = new Text("\n\n      *****  Probabilities  *****\n");
        txt_ProbTitle.setStyle(cssLabel_04);
        txt_StatTitle = new Text("        *****  Statistics  *****");
        txt_StatTitle.setStyle(cssLabel_04);      
    }
    
    void doLeftStatistic() { 
        dm.whereIsWaldo(120, waldoFile, "doLeftStatistic()");
        str_Left_Stat = al_STF.get(6).getText();
        if (str_Left_Stat.isEmpty()) { return; }
        
        if (!DataUtilities.strIsADouble(str_Left_Stat)) {
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }
 
        dbl_Left_Stat = Double.parseDouble(str_Left_Stat); 
        //fromLeftStatDoLeftProb(); 
 
        //  Is there a right prob?
        if (dbl_Right_Prob > 0.0) { 
            if(!leftRightOrderIsOK()) { return; } // after Error Message
            if (!probabilitiesAreLegal()) { return; }   // after Error Message
            
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;      
            al_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
        }
        
        makeANewGraph();       
    } 
        
    void doRightStatistic() { 
        dm.whereIsWaldo(145, waldoFile, "doRightStatistic()");
        if (!DataUtilities.strIsADouble(al_STF.get(7).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }

        str_Right_Stat_Original = al_STF.get(7).getText();
        if (str_Right_Stat_Original.isEmpty()) { return; }
        
        dbl_Right_Stat_Original = Double.parseDouble(str_Right_Stat_Original);
        
        if (dbl_Left_Prob > 0.0) { //  Is there a left prob?;  
            if (!leftRightOrderIsOK()) { return; } // after error message
            if (!probabilitiesAreLegal()) { return; }   // after Error Message
            
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
            al_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));  
        } 
        makeANewGraph();
    } 
    
    public void doLeftProbability() {
        dm.whereIsWaldo(167, waldoFile, "doLeftProbability()");
        if (!DataUtilities.strIsADouble(al_STF.get(3).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }

        if (!DataUtilities.strIsAProb(al_STF.get(3).getText())) {   //  Checks for empty also
            MyAlerts.showIllegalProbabilityAlert();
            resetKandK(); 
            return;
        }
        
        str_Left_Prob = al_STF.get(3).getText();       
        dbl_Left_Prob = Double.parseDouble(str_Left_Prob);
        al_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob)); 
        
        if (!al_STF.get(4).isEmpty() && al_STF.get(5).isEmpty()) {  
            dbl_Right_Prob = 1.0 - dbl_Left_Prob - dbl_Mid_Prob;
            str_Right_Prob = String.valueOf(dbl_Right_Prob);
            al_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
            //fromRightProbDoRightStat();
            return;
        }
        
        if (!al_STF.get(4).isEmpty()) {
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
            str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
            al_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
        }        

        //fromLeftProbDoLeftStat();
        makeANewGraph();
    }
    
    /************************************************************************
     *    Assuming two equal tails.  If the user wanted other, they should  *
     *    click on left and right tails separately.                         *
     ***********************************************************************/
    /*************************************************************************
    *         Probabilities are the same for Originals & Shifteds            *
    *************************************************************************/
    public void doMiddleProbability() {
        dm.whereIsWaldo(209, waldoFile, "doMiddleProbability()");
        if (!DataUtilities.strIsADouble(al_STF.get(4).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }
        
        if (!DataUtilities.strIsAProb(al_STF.get(4).getText())) {   //  Checks for empty also
            MyAlerts.showIllegalProbabilityAlert();
            resetKandK(); 
            return;
        }        

        //  Save midProb before reset
        double temp_MidProb = Double.parseDouble(al_STF.get(4).getText());
        boolean temp_TwoTail_OriginalChecked = originalDistrModel.get_TwoTail_IsChecked();
        boolean temp_TwoTail_ShiftedChecked = shiftedDistrModel.get_TwoTail_IsChecked();
        resetKandK();   
        originalDistrModel.set_TwoTail_IsChecked(temp_TwoTail_OriginalChecked);
        shiftedDistrModel.set_TwoTail_IsChecked(temp_TwoTail_ShiftedChecked);
        set_TwoTailCheckBox(temp_TwoTail_OriginalChecked);
        dbl_Mid_Prob = temp_MidProb;
        str_Mid_Prob = String.valueOf(dbl_Mid_Prob);
        al_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob)); 
        
        dbl_Left_Prob = (1.0 - dbl_Mid_Prob) / 2.0;
        str_Left_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Left_Prob, PROB_ROUND));
        al_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
        
        leftPercentile_Original = originalDistrModel.getTheUCDO().fromPercentileRank_toPercentile(dbl_Left_Prob);
        originalDistrModel.set_LeftPercentile(leftPercentile_Original); 
        
        leftPercentile_Shifted = shiftedDistrModel.getTheUCDO().fromPercentileRank_toPercentile(dbl_Left_Prob);
        shiftedDistrModel.set_LeftPercentile(leftPercentile_Shifted); 
        
      
        dbl_Left_Stat = originalDistrModel.get_LeftPercentile();
        str_Left_Stat = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Left_Stat, PROB_ROUND));        
        al_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
        
        dbl_Right_Prob = (1.0 - dbl_Mid_Prob) / 2.0;
        str_Right_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Right_Prob, PROB_ROUND));
        al_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
        
        rightPercentile_Original = originalDistrModel.getTheUCDO().fromPercentileRank_toPercentile(1.0 - dbl_Right_Prob);
        originalDistrModel.set_RightPercentile(rightPercentile_Original);
        
        rightPercentile_Shifted = shiftedDistrModel.getTheUCDO().fromPercentileRank_toPercentile(1.0 - dbl_Right_Prob);
        shiftedDistrModel.set_RightPercentile(rightPercentile_Shifted);
        
        dbl_Right_Stat_Original = originalDistrModel.get_RightPercentile();
        str_Right_Stat_Original = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Right_Stat_Original, PROB_ROUND));
        
        dbl_Right_Stat_Shifted = shiftedDistrModel.get_RightPercentile();
        str_Right_Stat_Shifted = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Right_Stat_Shifted, PROB_ROUND));
        
        al_STF.get(7).setText(roundDoubleToProbString(dbl_Right_Stat_Original));
        
        makeANewGraph();
    }
    
    void doRightProbability() {
        dm.whereIsWaldo(270, waldoFile, "doRightProbability()");
        if (!DataUtilities.strIsADouble(al_STF.get(5).getText())) {   //  Checks for empty also
            MyAlerts.showGenericBadNumberAlert("number");
            return;
        }
        
        if (!DataUtilities.strIsAProb(al_STF.get(5).getText())) {   //  Checks for empty also
            MyAlerts.showIllegalProbabilityAlert();
            resetKandK(); 
            al_STF.get(5).setText(toBlank);
            return;
        }       
       
        str_Right_Prob = al_STF.get(5).getText(); 
        dbl_Right_Prob = Double.parseDouble(str_Right_Prob);
        
        if (!al_STF.get(4).isEmpty() && al_STF.get(3).isEmpty()) {
            dbl_Left_Prob = 1.0 - dbl_Right_Prob - dbl_Mid_Prob;
            str_Left_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Left_Prob, PROB_ROUND));
            al_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
        }    
        
        if (!al_STF.get(3).isEmpty()) {
            if (!probabilitiesAreLegal()) { return; }   // after Error Message
            
            dbl_Mid_Prob = 1.0 - dbl_Left_Prob - dbl_Right_Prob;
            str_Mid_Prob = String.valueOf(DataUtilities.roundDoubleToNDigits(dbl_Mid_Prob, PROB_ROUND));
            al_STF.get(4).setText(roundDoubleToProbString(dbl_Mid_Prob));
        } 
        
        makeANewGraph();
    }
    
    /*************************************************************************
    *              Check boxes determine which parts are graphed             *
    *************************************************************************/
    public void chBoxLeftTailChanged(ObservableValue<? extends Boolean> observable,
        Boolean oldValue, Boolean newValue) {
        originalDistrModel.set_LeftTail_IsChecked(newValue);
        shiftedDistrModel.set_LeftTail_IsChecked(newValue);
        set_LeftTailCheckBox(newValue);
        if (originalDistrModel.get_LeftTail_IsChecked()) {  
            if (originalDistrModel.get_TwoTail_IsChecked()) { 
                resetMiddle();
                chBoxLeftTail.setSelected(true);    // Now get a prob or stat
            }
            else 
            {  //  midTail NotChecked but right tail might be
                if (dbl_Left_Prob > 0.0) {  //  Already a number there?
                    originalDistrModel.set_LeftTail_IsChecked(true);
                    shiftedDistrModel.set_LeftTail_IsChecked(true);
                    set_LeftTailCheckBox(true);
                }   // else just leave blank
            }
        }      
        else    // leftTailIsUnchecked
        { 
            originalDistrModel.set_LeftTail_IsChecked(false);
            shiftedDistrModel.set_LeftTail_IsChecked(false);
            set_LeftTailCheckBox(false);
        }
        makeANewGraph();        
    }            
    
    /*************************************************************************
    *              Check boxes determine which parts are graphed             *
    *  set_XXXTailCheckBox(newValue);             -- Changes in the View     *
    *  originalDistrModel.set_XXX_IsChecked(); -- Alerts the model           *
    *************************************************************************/ 
    public void chBoxTwoTailChanged(ObservableValue<? extends Boolean> observable,
        Boolean oldValue, Boolean newValue) {
        originalDistrModel.set_TwoTail_IsChecked(newValue);
        shiftedDistrModel.set_TwoTail_IsChecked(newValue);
        set_TwoTailCheckBox(newValue);
        if (originalDistrModel.get_TwoTail_IsChecked() && (!al_STF.get(4).getTextField().getText().isBlank())) {  
            str_Mid_Prob = al_STF.get(4).getText(); 
            dbl_Mid_Prob = Double.parseDouble(str_Mid_Prob);
            dbl_Left_Prob = (1.0 - dbl_Mid_Prob) / 2.0; 
            dbl_Right_Prob = (1.0 - dbl_Mid_Prob) / 2.0; 
            originalDistrModel.set_ShadeLeft(true);
            originalDistrModel.set_ShadeRight(true);  
            shiftedDistrModel.set_ShadeLeft(true);
            shiftedDistrModel.set_ShadeRight(true);
        }
        else {
            if (originalDistrModel.get_TwoTail_IsChecked() && (al_STF.get(4).getTextField().getText().isBlank())) {
                return; // do nothing
            } 
            else 
            {
                resetMiddle();
            }
        }
        makeANewGraph();
    }
    
    /*************************************************************************
    *              Check boxes determine which parts are graphed            *
    ************************************************************************/
    public void chBoxRightTailChanged(ObservableValue<? extends Boolean> observable,
        Boolean oldValue, Boolean newValue) {
        originalDistrModel.set_RightTail_IsChecked(newValue);
        shiftedDistrModel.set_RightTail_IsChecked(newValue);
        set_RightTailCheckBox(newValue);
        if (originalDistrModel.get_RightTail_IsChecked()) {  
            if (originalDistrModel.get_TwoTail_IsChecked()) { 
                resetMiddle();
                chBoxRightTail.setSelected(true);    // Now get a prob or stat
            }
            else 
            {  //  midTail NotChecked but right tail might be
                if (dbl_Right_Prob > 0.0) {  //  Already a number there?
                    originalDistrModel.set_RightTail_IsChecked(true);
                    shiftedDistrModel.set_RightTail_IsChecked(true);
                    set_RightTailCheckBox(true);
                }   // else just leave blank
            }
        }      
        else    // rightTailIsUnchecked
        { 
            originalDistrModel.set_RightTail_IsChecked(false);
            shiftedDistrModel.set_RightTail_IsChecked(false);
            set_RightTailCheckBox(false);
        }
        makeANewGraph();     
    } 
    
    public void resetKandK() {  // Kit and Kaboodle
        dm.whereIsWaldo(398, waldoFile, "resetKandK()");
        al_STF.get(3).getTextField().setText(toBlank);
        al_STF.get(6).getTextField().setText(toBlank);
        al_STF.get(4).getTextField().setText(toBlank);
        //  There is no stf_Mid_Stat
        al_STF.get(5).getTextField().setText(toBlank);
        al_STF.get(7).getTextField().setText(toBlank);
        dbl_Left_Prob = 0.0;
        dbl_Mid_Prob = 0.0;
        dbl_Right_Prob = 0.0;
        dbl_Left_Stat = 0.0;
        dbl_Right_Stat_Original = 0.0;
        chBoxLeftTail.setSelected(false); 
        chBoxTwoTail.setSelected(false);
        chBoxRightTail.setSelected(false); 
        originalDistrModel.set_ShadeLeft(false);
        originalDistrModel.set_ShadeRight(false);
        shiftedDistrModel.set_ShadeLeft(false);
        shiftedDistrModel.set_ShadeRight(false);
        makeANewGraph();
    }
    
    public void resetLeft() {
        al_STF.get(3).getTextField().setText(toBlank);
        al_STF.get(6).getTextField().setText(toBlank);  
        dbl_Left_Prob = 0.0;
    }
    
    public void resetMiddle() {
        al_STF.get(4).getTextField().setText(toBlank);    
        dbl_Mid_Prob = 0.0;
    }
    
    public void resetRight() {
        al_STF.get(5).getTextField().setText(toBlank);
        al_STF.get(7).getTextField().setText(toBlank);  
        dbl_Right_Prob = 0.0;
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
   
    public void set_LeftTailCheckBox(boolean toThis) {
        chBoxLeftTail.setSelected(toThis);  
        originalDistrModel.set_LeftTail_IsChecked(toThis);
        shiftedDistrModel.set_LeftTail_IsChecked(toThis);
    }
    
    public void set_TwoTailCheckBox(boolean toThis) {
        chBoxTwoTail.setSelected(toThis);
        originalDistrModel.set_TwoTail_IsChecked(toThis);  
        shiftedDistrModel.set_TwoTail_IsChecked(toThis);
    }
    
    public void set_RightTailCheckBox(boolean toThis) {
        chBoxRightTail.setSelected(toThis); 
        originalDistrModel.set_RightTail_IsChecked(toThis); 
        shiftedDistrModel.set_RightTail_IsChecked(toThis);  
    }
    
    /***********************************************************************
     *                       Error checks                                  *
     **********************************************************************/
    
    private boolean probabilitiesAreLegal() {
        boolean probsAreLegal = true;
        if (dbl_Left_Prob + dbl_Mid_Prob + dbl_Right_Prob > 1.01) {
            probsAreLegal = false;
            MyAlerts.showIllegalBootProbabilityAlert();
            resetKandK();  
        } 
        return probsAreLegal;
    }
    
    private boolean leftRightOrderIsOK() {
        boolean leftRightIsOK = true;
        if (dbl_Left_Stat >= dbl_Right_Stat_Original){
            leftRightIsOK = false;
            MyAlerts.showLeftRightOrderAlert();
            resetKandK(); 
            
        }        
        return leftRightIsOK;
    }
    
    /*******************************************************************
    *                      Abstract methods                            *                       
    *******************************************************************/    
    public abstract void makeAllTheSTFs();
    public abstract void makeANewGraph();
    public abstract void constructGraphStatus();
}
