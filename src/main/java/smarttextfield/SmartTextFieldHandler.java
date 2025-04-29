/****************************************************************************
 *                     SmartTextFieldHandler                                * 
 *                           01/16/25                                      *
 *                             12:00                                        *
 ***************************************************************************/
package smarttextfield;

import utilityClasses.MyAlerts;

public class SmartTextFieldHandler {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int intIfInt, lastAccessed_AL_Index, nChangesToIgnore; 
    static int current_AL_Index, prevSmartTF_Number, nextSmartTF_Number;

    // My classes
    MyAlerts myAlerts;
    SmartTextFieldChecker stfChecker;
    SmartTextFieldDoublyLinkedSTF al_STF;
    
    public SmartTextFieldHandler(SmartTextFieldsController stf_Controller) { 
        if (printTheStuff == true) {
            System.out.println("26 *** SmartTextFieldHandler, Constructing");
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
    
    public int getCurrentAccessed_AL_Index() {return current_AL_Index;}
    
    public void setCurrentAccessed_AL_Index( int toThis_AL_Index) {
        current_AL_Index = toThis_AL_Index;
    }
    
    public int getLastAccessed_AL_Index() {return lastAccessed_AL_Index;}
    
    public void setLastAccessed_AL_Index( int toThis_AL_Index) {
        lastAccessed_AL_Index = toThis_AL_Index;
    }
 
    public void setPreAndPostSmartTF (int shiftTab, int unshiftTab) {
        prevSmartTF_Number = shiftTab; nextSmartTF_Number = unshiftTab;        
    }
    
    public SmartTextFieldChecker getSmartTextFieldChecker() {return stfChecker; }
}
