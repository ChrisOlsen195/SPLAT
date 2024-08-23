/****************************************************************************
 *                    SmartTextFieldChecker                                 * 
 *                           10/15/23                                       *
 *                            12:00                                         *
 ***************************************************************************/
/****************************************************************************
 *  Checks: positive & negative fractions & decimals  01/03                 * 
 *                  -0.25 a problem in checking probs                       *
 *  Note to self: When Alerts are presented, these constitute changes in    *
 *                focus.  Is there a specific focusChangeListener that      *
 *                could distinguish Alerts, ENTERS, and mouse clicks???     *
 ***************************************************************************/
package smarttextfield;

import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class SmartTextFieldChecker {
    // POJOs
    boolean mb_Negative, mb_NonPositive, mb_NonZero, mb_NonNegative, 
            mb_Positive, mb_Integer, mb_Real, mb_NonBlank, //mb_Blank,
            mb_Probability, mb_PositiveInteger, okToContinue, comingFromEnter, 
            showingAnAlert, checkingForProbability;

    int intIfInt;//, nChangesToIgnore;
    int startNToIgnore = 4;
    
    double doubleIfDouble, tempDouble;

    String stringToCheck;
    
    // My classes
    //SmartTextFieldsController stf_Controller;
    SmartTextFieldHandler stfHandler;
    SmartTextField theSTF;
    
    public SmartTextFieldChecker(SmartTextFieldsController stf_Controller, SmartTextField stf) { 
        //this.stf_Controller = stf_Controller;
        theSTF = stf;
        stfHandler = stf_Controller.getSTFHandler();
    } 

    /**************************************************************
    *    These restriction tests return FALSE if:                 *
    *         The MB restriction is asked for AND                 *
    *         the restriction asked for is not fulfilled.         *
     * @return 
    **************************************************************/
    
    //  Called from 196 STF
public boolean checkAllRestrictions( String forThisString) {
    
    stringToCheck = forThisString;
         
    /**************************************************************
    *  If we are checkingAllRestrictions after a click on ENTER
    *  nChangesToIgnore is set to 2 b/c the changeListener in     *
    *  the SmartTextField needs to consume two changes: losing    *
    *  focus and the click on the Alert.                          *
    **************************************************************/
    //System.out.println("62 stfChecker, checkAllRestrictions()");
        okToContinue = true;
        checkingForProbability = false; // Need to initialize here to avoid
                                        // false Bad Fraction alert.
                                        
        comingFromEnter = theSTF.getComingFromEnter();

        mb_Integer = theSTF.getSmartTextField_MB_INTEGER();
        mb_Negative = theSTF.getSmartTextField_MB_NEGATIVE();
        mb_NonNegative = theSTF.getSmartTextField_MB_NONNEGATIVE();
        mb_NonPositive = theSTF.getSmartTextField_MB_NONPOSITIVE();
        mb_NonZero = theSTF.getSmartTextField_MB_NONZERO();
        mb_Real = theSTF.getSmartTextField_MB_REAL();
        mb_Positive = theSTF.getSmartTextField_MB_POSITIVE();
        mb_PositiveInteger = theSTF.getSmartTextField_MB_POSITIVEINTEGER();
        mb_Probability = theSTF.getSmartTextField_MB_PROBABILITY();
        
        if ( (mb_Integer) || (mb_Negative) || (mb_NonNegative) || (mb_NonPositive) ||
             (mb_NonZero) || (mb_Real) || (mb_Positive) || (mb_PositiveInteger) ||
             (mb_Probability) || (mb_Real)) {
            mb_NonBlank = true;  
        }
       
        if ((mb_NonBlank == true) && (checkSmartTextField_MB_NONBLANK() == false)) {
            okToContinue = false;
            return okToContinue;
        }
               
        if ((mb_Probability == true) && (checkSmartTextField_MB_PROBABILITY() == false)) {
            okToContinue = false;
            return okToContinue;
        }      
        
        if ((mb_Positive == true) && (checkSmartTextField_MB_POSITIVE() == false)) {
            okToContinue = false;
            return okToContinue;
        }    
        
        if ((mb_Real == true) && (checkSmartTextField_MB_REAL() == false)) {
            okToContinue = false;
            return okToContinue;
        }
        
        if ((mb_PositiveInteger == true) && (checkSmartTextField_MB_POSITIVEINTEGER() == false)) {
            okToContinue = false;
            return okToContinue;
        }
        
        return okToContinue;
    }

    private boolean checkSmartTextField_MB_NEGATIVE() {
        mb_Real = true;
        if (checkSmartTextField_MB_REAL() == false) { return false; } 
        return (Double.parseDouble(stringToCheck) < 0.00);      
    }    

    private boolean checkSmartTextField_MB_NONPOSITIVE() { 
        mb_Real = true;  
        if (checkSmartTextField_MB_REAL() == false) { return false; }
        return (Double.parseDouble(stringToCheck) <= 0.00);        
    } 
    
    private boolean checkSmartTextField_MB_NONZERO() {
        mb_Real = true; 
        if (checkSmartTextField_MB_REAL() == false) { return false; }
        return (Double.valueOf(stringToCheck) == 0.00);          
    } 
    
    private boolean checkSmartTextField_MB_NONNEGATIVE() {
        mb_Real = true;
        if (checkSmartTextField_MB_REAL() == false) { return false; }
        return (Double.parseDouble(stringToCheck) >= 0.00);        
    } 

    private boolean checkSmartTextField_MB_POSITIVE() {
        okToContinue = true;
        boolean isAReal = false;
        boolean isPositive = false;
        isAReal = DataUtilities.strIsADouble(stringToCheck);
        
        if (isAReal) { 
            double theReal = Double.parseDouble(stringToCheck);
            isPositive = (theReal > 0.0);
        }
        
        if ((mb_Positive) && (!isPositive)) { 
            
            if (comingFromEnter) {
                stfHandler.setNChangesToIgnore(startNToIgnore);
            }
            
            showingAnAlert = true;
            MyAlerts.showGenericBadNumberAlert(" a positive number ");
            showingAnAlert = false;
            okToContinue = false; 
        }
        return okToContinue;         
    }
    
    private boolean checkSmartTextField_MB_POSITIVEINTEGER() {
        okToContinue = true;
        
        if (!checkSmartTextField_MB_REAL()) { return false; }

        if (!mb_PositiveInteger) {
            return okToContinue;    //  No need to check
        }
        
        if (!DataUtilities.strIsAnInteger(stringToCheck)) { 
            
            if (comingFromEnter) {
                stfHandler.setNChangesToIgnore(startNToIgnore);
            }
            
            showingAnAlert = true;
            MyAlerts.showGenericBadNumberAlert(" a positive integer ");;
            showingAnAlert = false;
            okToContinue = false; 
            return okToContinue;
        }
        else
        {
            intIfInt = Integer.parseInt(stringToCheck);  
            
            if (intIfInt < 1) {
                
                if (comingFromEnter) {
                    stfHandler.setNChangesToIgnore(startNToIgnore);
                }

                showingAnAlert = true;
                MyAlerts.showGenericBadNumberAlert(" a positive integer ");
                showingAnAlert = false;
                okToContinue = false;
            }
            else {
                theSTF.setSmartTextInteger(intIfInt); 
            }
        }        
        return okToContinue;        
    } 

    private boolean checkSmartTextField_MB_REAL() {
        okToContinue = true;
        
        if ((mb_Real) && (!DataUtilities.strIsADouble(stringToCheck))) { 
            
            if (comingFromEnter) {
                stfHandler.setNChangesToIgnore(startNToIgnore);
            }
            
            showingAnAlert = true;
            MyAlerts.showGenericBadNumberAlert(" a real number ");
            showingAnAlert = false;
            okToContinue = false; 
        }
        else
        {   // Store this a a legal real if must be
            if (mb_Real) {
                doubleIfDouble = Double.parseDouble(stringToCheck);
                theSTF.setSmartTextDouble(doubleIfDouble);   
            }   
        }    
        return okToContinue;        
    } 

    // public so that a range of fields can be checked from the handler
    public boolean checkSmartTextField_MB_NONBLANK() {
        okToContinue = true;
        
        if ((mb_NonBlank) && (stringToCheck.length() == 0)) {
            
            if (comingFromEnter) {
                stfHandler.setNChangesToIgnore(startNToIgnore);
            }
            
            showingAnAlert = true;
            MyAlerts.showMustBeNonBlankAlert();
            showingAnAlert = false;
            okToContinue = false; 
        }
        return okToContinue;        
    } 

    private boolean checkSmartTextField_MB_PROBABILITY() { 
        checkingForProbability = true;
        //  Check for legal decimal, and if true check for legal prob
        
        if (DataUtilities.strIsADouble(stringToCheck)) {
            
            tempDouble = Double.parseDouble(stringToCheck);
            
            if ((0 < tempDouble) && (tempDouble < 1.0)) {
                doubleIfDouble = tempDouble;   
                theSTF.setSmartTextDouble(doubleIfDouble); 
                okToContinue = true;
                return okToContinue;
            }
        }    
        
        //  Check for legal fraction, and if true check for legal prob
        if (checkForLegalFraction()) {  // ,it is now in decimal form

            tempDouble = Double.parseDouble(stringToCheck);
            
            if ((0 < tempDouble) && (tempDouble < 1.0)) {
                doubleIfDouble = tempDouble;   
                theSTF.setSmartTextDouble(doubleIfDouble); 
                okToContinue = true;
                return okToContinue;
            }
            else {
                showingAnAlert = true;   
                System.out.println("259 SmartTextFieldChecker, Illegal prob");
                MyAlerts.showIllegalProbabilityAlert();
                showingAnAlert = false;
                return okToContinue;            
            }
        }    
        okToContinue = false;
        
        if (comingFromEnter) {
            stfHandler.setNChangesToIgnore(startNToIgnore);
        }
        
        showingAnAlert = true;   
        MyAlerts.showIllegalProbabilityAlert();
        showingAnAlert = false;
        return okToContinue;
    }

    private boolean checkForLegalFraction() { 
        okToContinue = true;
        int numerator = 0;
        int denominator = 0;
        String[] tokens = stringToCheck.split("/");
        
        if (tokens.length != 2) {
            okToContinue = false;
            
            if (comingFromEnter) {
                stfHandler.setNChangesToIgnore(startNToIgnore);
            }
            
            showingAnAlert = true;
            
            if (!checkingForProbability) {
                MyAlerts.showGenericBadNumberAlert(" a legal fraction ");
            }
            
            showingAnAlert = false;
            return okToContinue;
        }
        
        if ((!DataUtilities.strIsAnInteger(tokens[0])) || 
                (!DataUtilities.strIsAnInteger(tokens[1]))){
            
                    if (comingFromEnter) {
                        stfHandler.setNChangesToIgnore(startNToIgnore);
                    }
                    
                    showingAnAlert = true;
                    MyAlerts.showGenericBadNumberAlert(" a legal fraction ");
                    showingAnAlert = false;
                    okToContinue = false;
                    return okToContinue;   
        }
        else {  //  Is a legal fraction, convert to decimal and update stringToCheck
            numerator = Integer.parseInt(tokens[0]);
            denominator = Integer.parseInt(tokens[1]);           
            doubleIfDouble = (double)numerator / (double) denominator;
            theSTF.setSmartTextDouble(doubleIfDouble);
            stringToCheck = String.valueOf(doubleIfDouble);
            theSTF.setSmartTextDouble(doubleIfDouble);
            theSTF.setText(stringToCheck);
            return okToContinue;
        }
    }    
}
