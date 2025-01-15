/****************************************************************************
 *                     SmartTextFieldHandler                                * 
 *                           01/13/25                                      *
 *                             09:00                                        *
 ***************************************************************************/
package smarttextfield;

import utilityClasses.MyAlerts;

public class SmartTextFieldHandler {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    /*
    boolean mb_Negative = false, mb_NonPositive = false, mb_NonZero = false, 
            mb_NonNegative = false, mb_Positive = false, mb_Integer = false, 
            mb_Real = false, mb_Probability = false, mb_PositiveInteger = false;
    
    boolean hasARestriction, isTraversed, legalFraction, legalDecimal,
            comingFromEnter;
    
    double doubleIfDouble;
    */
    
    int intIfInt, lastAccessed_AL_Index, nChangesToIgnore; 
    static int current_AL_Index, prevSmartTF_Number, nextSmartTF_Number;

    //String stringToCheck;

    // My classes
    MyAlerts myAlerts;
    SmartTextFieldChecker stfChecker;
    //SmartTextFieldsController stf_Controller;
    DoublyLinkedSTF al_STF;
    
    public SmartTextFieldHandler(SmartTextFieldsController stf_Controller) { 
        if (printTheStuff) {
            System.out.println("\n39 *** SmartTextFieldHandler, Constructing");
        }
        myAlerts = new MyAlerts();
        al_STF = stf_Controller.getLinkedSTF();   
    }
     
    public void setFocusRequest(int focusHere) {
        al_STF.get(intIfInt).getTextField().requestFocus();
    }
    
    /**************************************************************
    *  If we are checkingAllRestrictions after a click on ENTER   *
    *  nChangesToIgnore is set to 2 b/c the changeListener in     *
    *  the SmartTextField needs to consume two changes: losing    *
    *  focus and the click on the Alert.                          *
     * @return 
    **************************************************************/

    public int getNChangesToIgnore() {
        if (nChangesToIgnore > 0)
            nChangesToIgnore--;
        return nChangesToIgnore;
    }
    
    public void setNChangesToIgnore(int toThisManyChanges) {
        nChangesToIgnore = toThisManyChanges;
    }

    // Note: Only the alert class can set ShowingAnAlert
    public boolean getShowingAnAlert() {
        return myAlerts.getShowingAnAlert();
    }
    
    // Only used at SmartTextField initialization
    public void setShowingAnAlert(boolean showingAnAlert) {
        myAlerts.setShowingAnAlert(showingAnAlert);
    }

    /*
    public boolean getComingFromEnter() {return comingFromEnter; }
    
    public void setComingFromEnter(boolean toThis) {
        comingFromEnter = toThis;
    }
    */
    
    public int getCurrentAccessed_AL_Index() {return current_AL_Index;}
    
    public void setCurrentAccessed_AL_Index( int toThis_AL_Index) {
        current_AL_Index = toThis_AL_Index;
    }
    
    public int getLastAccessed_AL_Index() {return lastAccessed_AL_Index;}
    
    public void setLastAccessed_AL_Index( int toThis_AL_Index) {
        lastAccessed_AL_Index = toThis_AL_Index;
    }
    
    /*
    public boolean finalCheckForBlanksInArray(int startHere, int endHere) {
        boolean continueCheck = true;   //  To molify compiler
        boolean mustBeNonBlank = true;  //  To molify compiler
        boolean isBlank = true;         //  To molify compiler
        for (int smartFieldIndex = startHere; smartFieldIndex <= endHere; smartFieldIndex++) {
            if (continueCheck == true) {
                SmartTextField tempSTF = new SmartTextField();
                tempSTF = handlerArrayList.get(smartFieldIndex).getSmartTextField();
                stfChecker.setSTF(tempSTF);
                mustBeNonBlank = handlerArrayList.get(smartFieldIndex).getSmartTextField_MB_NONBLANK();
                String tempString = tempSTF.getText();
                isBlank = tempString.trim().isEmpty();
                continueCheck = ((mustBeNonBlank == false) || (isBlank == false));
            }
        }
        return continueCheck;
    }
    */
    
    /*
    public boolean finalCheck4IndividualSTFBlank(SmartTextField singleSTF) {
        SmartTextField tempSTF = new SmartTextField();
        tempSTF = singleSTF;
        stfChecker.setSTF(tempSTF);
        boolean mustBeNonBlank = tempSTF.getSmartTextField_MB_NONBLANK();
        String tempString = tempSTF.getText();
        boolean isBlank = tempString.trim().isEmpty();
        boolean okToContinue = ((mustBeNonBlank == false) || (isBlank == false));
        return okToContinue;
    }
    */
 
    public void setPreAndPostSmartTF (int shiftTab, int unshiftTab) {
        prevSmartTF_Number = shiftTab; nextSmartTF_Number = unshiftTab;        
    }
    
    public SmartTextFieldChecker getSmartTextFieldChecker() {return stfChecker; }
}
