/****************************************************************************
 *                        SmartTextField                                    * 
 *                           01/14/25                                       *
 *                            09:00                                         *
 ***************************************************************************/
/****************************************************************************
*       Sample set up of text field & must-be, with label.                  *
*                                                                           *
*       grProbA = new Label("P(A) = ");                                     *
*       GridPane.setHalignment(grProbA, HPos.RIGHT);                        *
*       grProbA.setFont(Font.font("Arial", FontWeight.BOLD, 16));           *
*       al_STF.get(0).setSmartTextField_MB_PROBABILITY(true);               *
*       al_STF.get(0).setPrefColumnCount(4);                                *
****************************************************************************/
package smarttextfield;

import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SmartTextField {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
        
    boolean showingAnAlert = false, ignoreFocusChanges = false;
    boolean mb_Negative = false, mb_NonPositive = false, mb_NonZero = false, 
            mb_NonNegative = false, mb_Positive = false, mb_Integer = false, 
            mb_Real = false, mb_Probability = false, mb_PositiveInteger = false; 
    
    boolean comingFromEnter;
    
    int previousInAL, meInAL, nextInAL;

    int intIfInt, shiftTabTo_TF, tabTo_TF;
    
    double doubleIfDouble;
    
    // My classes
    SmartTextFieldChecker stf_Checker;
    SmartTextFieldsController stf_Controller;
    SmartTextFieldHandler stf_Handler;
    DoublyLinkedSTF dlSTF;
    
    // POJOs / FX
    TextField lessThanSmart_TF;
    
    public SmartTextField() {
        if (printTheStuff) {
            System.out.println("\n55 *** SmartTextField, Constructing");
        }
        lessThanSmart_TF = new TextField(); // Just a wrapper
        setIsEditable(true);
    }
    
    public SmartTextField(String tfString) {
        if (printTheStuff) {
            System.out.println("\n62 *** SmartTextField, Constructing from tString");
        }
        lessThanSmart_TF = new TextField(tfString); // Just an initialized wrapper
        setIsEditable(true);
    }
    
    public SmartTextField(SmartTextFieldsController stf_Controller)  { 
        if (printTheStuff) {
            System.out.println("\n70 *** SmartTextField, Constructing from stf_Controller");
        }
        this.stf_Controller = stf_Controller;
        stf_Checker = new SmartTextFieldChecker(stf_Controller, this);  
        lessThanSmart_TF = new TextField();
        setIsEditable(true);
    }
    
    /**********************************************************************
    *  It is necessary to complete initializing the SmartTextField b/c    *
    *  of a chicken and egg problem among the SmartTextField classes.     *
    **********************************************************************/
    public void finishInitializations() {
        if (printTheStuff) {
            System.out.println("85 --- SmartTextField, finishInitializations");
        }
        stf_Handler = stf_Controller.getSTFHandler();
        dlSTF = stf_Controller.getLinkedSTF();
        stf_Handler.setShowingAnAlert(false);
        lessThanSmart_TF.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                                                                         Boolean newPropertyValue) {
                showingAnAlert = stf_Handler.getShowingAnAlert();        
                int tempChangesToIgnore = stf_Handler.getNChangesToIgnore();
                ignoreFocusChanges = ((showingAnAlert == true) || (tempChangesToIgnore > 0));
                
                if ((showingAnAlert == false) && (tempChangesToIgnore == 0) && (comingFromEnter == false)) {
                    
                    if (newPropertyValue) {  // This TF is coming on focus
                        int tempCurr = getArrayListPosition(lessThanSmart_TF);
                        stf_Handler.setCurrentAccessed_AL_Index(tempCurr);
                    }
                    else {  // This TF is going off focus

                        String daText = lessThanSmart_TF.getText();
                        int tempCurr = getArrayListPosition(lessThanSmart_TF);
                        stf_Handler.setLastAccessed_AL_Index(tempCurr);
                        
                        if (!daText.isEmpty()) {
                            lessThanSmart_TF.setText(daText);
                            lessThanSmart_TF.commitValue();

                            boolean restrictionsCheck = stf_Checker.checkAllRestrictions(daText);
                            if (restrictionsCheck == true) { // i.e. okToContinue
                                setText(getTextField().getText());
                                lessThanSmart_TF.fireEvent(new ActionEvent(this, null));
                            }
                            else {
                                int tempPrev = stf_Handler.getLastAccessed_AL_Index();
                                dlSTF.get(tempPrev).getTextField().setText("");
                                dlSTF.get(tempPrev).getTextField().requestFocus();                              
                            }                          
                        }
                    } 
                }   //  End if ignore
                else
                {
                    // System.out.println("Showing an alert");
                }
            }   //  End change
        });
        
        lessThanSmart_TF.setOnKeyTyped(stfKeyEventHandler);
        lessThanSmart_TF.setOnKeyPressed(stfKeyEventHandler);
        lessThanSmart_TF.setOnKeyReleased(stfKeyEventHandler);
        lessThanSmart_TF.addEventHandler(MouseEvent.MOUSE_CLICKED, e-> handleMouseClick(e));            
    }
 
    public void handleMouseClick(MouseEvent e) {
        stf_Handler.setLastAccessed_AL_Index(meInAL);
    }
    
    public int getArrayListPosition( TextField ofThisTextField)  {
        return meInAL;
    }        
    
    private EventHandler<KeyEvent> stfKeyEventHandler = new EventHandler<KeyEvent>()  {
        public void handle(KeyEvent ke) {      
            comingFromEnter = false;
            ignoreFocusChanges = true;
            KeyCode keyCode = ke.getCode();  
            
            if (ke.getEventType() == KeyEvent.KEY_TYPED) { 
            }   // end KEY_TYPED
            
            else
            if (ke.getEventType() == KeyEvent.KEY_PRESSED) { 
                if ((keyCode == KeyCode.TAB) && (ke.isShiftDown())) {
                    doShiftTabKey();
                    ke.consume();
                }   
                else
                if ((keyCode == KeyCode.TAB) && (!ke.isShiftDown())) {
                    doEnterKey(); 
                    ke.consume();
                }                     
            }   //  end KEY_PRESSED
            else
            if (ke.getEventType() == KeyEvent.KEY_RELEASED) {   
                if (keyCode == KeyCode.ENTER) {
                   comingFromEnter = true;
                   doEnterKey();   
                }
                else {  }                                   
            }   
            ignoreFocusChanges = false;
        }   //  end HandleKeyEvent
    };    
    
    public void doEnterKey() {  //  and TAB
        if (printTheStuff) {
            System.out.println("182 --- SmartTextField, doEnterKey()");
        }
        showingAnAlert = false;
        
        if (!showingAnAlert)  {  
            boolean restrictionsCheck = stf_Checker.checkAllRestrictions(getTextField().getText());
            
            if (restrictionsCheck) {    //  i.e. okToContinue
                setText(getTextField().getText());
                lessThanSmart_TF.fireEvent(new ActionEvent(this, null));
                dlSTF.get(nextInAL).getTextField().requestFocus();
            }
            else {
                setText("");
                lessThanSmart_TF.requestFocus();
            }
        
        } else { 
            System.out.println("--------> Showing alert"); 
        }
    }   //  end DoEnterKey
    
    public void doShiftTabKey() {  
        dlSTF.get(previousInAL).getTextField().requestFocus();    
    }     
    
    public String getText() {return lessThanSmart_TF.getText(); }
    public void setText(String theText) { 
        lessThanSmart_TF.setText(theText); 
    }
    
    public int  getPrefColumnCount() { return lessThanSmart_TF.getPrefColumnCount(); }
    public void setPrefColumnCount(int pref) { lessThanSmart_TF.setPrefColumnCount(pref);}
    
    public boolean getIsEditable() {return lessThanSmart_TF.isEditable(); }
    public void setIsEditable(boolean eddyWeddy) { lessThanSmart_TF.setEditable(eddyWeddy); }
    
    public TextField getTextField() {return lessThanSmart_TF;} 
    public SmartTextField getSmartTextField() {return this; }
    
    public int getSmartTextInteger() { return intIfInt; }
    public void setSmartTextInteger( int toThis) { intIfInt = toThis; }
    
    public double getSmartTextDouble() { return doubleIfDouble; }
    public void setSmartTextDouble( double toThis) { doubleIfDouble = toThis; }
    
    public void setPre_Me_AndPostSmartTF (int prev, int me, int next) {
        previousInAL = prev; meInAL = me; nextInAL = next;        
    }
    
    public boolean getSmartTextField_MB_NEGATIVE() { 
        return mb_Negative; 
    }
    
    public void setSmartTextField_MB_NEGATIVE(boolean mustBe) { 
        mb_Negative = mustBe;
    }
 
    public boolean getSmartTextField_MB_NONPOSITIVE() { 
        return mb_NonPositive; 
    }
        
    public void setSmartTextField_MB_NONPOSITIVE(boolean mustBe) { 
        mb_NonPositive = mustBe; 
    }
    
    public boolean getSmartTextField_MB_NONZERO() { 
        return mb_NonZero; 
    }

    public void setSmartTextField_MB_NONZERO(boolean mustBe) { 
        mb_NonZero = mustBe; 
    }
    
    public boolean getSmartTextField_MB_NONNEGATIVE() { 
        return mb_NonNegative; 
    }

    public void setSmartTextField_MB_NONNEGATIVE(boolean mustBe) { 
        mb_NonNegative = mustBe;
    }
    
    public boolean getSmartTextField_MB_POSITIVE() { 
        return mb_Positive; 
    }
           
    public void setSmartTextField_MB_POSITIVE(boolean mustBe) { 
        mb_Positive = mustBe; 
    }

    public boolean getSmartTextField_MB_INTEGER() { 
        return mb_Integer; 
    }
    
    public void setSmartTextField_MB_INTEGER(boolean mustBe) { 
        mb_Integer = mustBe; 
    }
    
    public boolean getSmartTextField_MB_POSITIVEINTEGER() { 
        return mb_PositiveInteger;
    }
        
    public void setSmartTextField_MB_POSITIVEINTEGER(boolean mustBe) { 
        mb_PositiveInteger = mustBe;
    }

    public boolean getSmartTextField_MB_REAL() { 
        return mb_Real; 
    }    
    
    public void setSmartTextField_MB_REAL(boolean mustBe) { 
        mb_Real = mustBe;
    }
    
    public boolean getSmartTextField_MB_PROBABILITY() { 
        return mb_Probability; 
    } 
    
    public void setSmartTextField_MB_PROBABILITY(boolean mustBe) { 
        mb_Probability = mustBe;
    } 
    
    public int getPrevSmartTF () {
        return shiftTabTo_TF;        
    }
    
    public void setPrevSmartTF (int newPrev) {  //  No idea whatFor
        shiftTabTo_TF = newPrev;        
    }
        
    public int getNextSmartTF () {
        return tabTo_TF;        
    }
    
    public void setNextSmartTF (int newNext) {     //  No idea whatFor
        tabTo_TF = newNext;        
    }
    
    public boolean getComingFromEnter() { return comingFromEnter; }
    
    /*
    public boolean get_ThisSTF_IsEmpty() {
        return lessThanSmart_TF.getText().isEmpty();
    }  
    */
    
    public void setPreviousInAL(int toThis) {
        previousInAL =  toThis;
            //int previousInAL, meInAL, nextInAL;
    }
    
    public void setNextInAL(int toThis) {
        nextInAL =  toThis;
    }
    
    public boolean isEmpty() {
        return lessThanSmart_TF.getText().isEmpty();
    }
    
    public boolean isBlank() {
        return lessThanSmart_TF.getText().isBlank();
    }
    
    public String toString() {
        System.out.print("stf.toString... ");
        //     int previousInAL, meInAL, nextInAL;
        String textString = this.getText();
        String outString = textString + ", " + previousInAL + ", " + meInAL + ", " + nextInAL;
        return outString;
    }
}
