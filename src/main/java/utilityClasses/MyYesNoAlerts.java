/****************************************************************************
 *                          MyYesNoAlerts                                      *
 *                            11/11/24                                      *
 *                             09:00                                        *
 ***************************************************************************/
/****************************************************************************
 *   The showingAnAlert variable is to remind SPLAT to eat the additional   *
 *   carriage return that is apparently needed to make all this work.       *                                    *
 *                                                                          *
 *   The construction :                                                     *
 *           doTheDefaults();                                               *
 *           doTheSplatAlert();                                             *
 *   allows an override of the defaults before the Alert is called.         *
 ***************************************************************************/
package utilityClasses;

public class MyYesNoAlerts {
    
    static boolean showingAnAlert; //, fourProbsAlreadyShown;
    static int fitWidth, horizImageSpace;
    static double alertBoxWidth, alertBoxHeight, imageOffSetX, imageOffSetY;
    static double diffAlertBoxWidth, diffAlertBoxHeight;
    static YesNoAlert yesNoAlert;
    
    static String alertTitle, alertHeader, alertContext, imagePath;
    
    public MyYesNoAlerts() { 
       showingAnAlert = false;
       diffAlertBoxWidth = 0.;
       diffAlertBoxHeight = 0.;
   }
   
    public void setShowingAnAlert(boolean tf) { showingAnAlert = tf; }
   
    public void showAvoidRepetitiousClicksAlert(String theYes, String theNo) { 
        showingAnAlert = true;
        alertTitle = "Boring, repetitive homework?";
        alertHeader = "I, SPLAT, has an option for you...";
        alertContext ="Ok, so here's the deal.  If you are doing the same kind of problem over and over, "
                        + " I, SPLAT the Magnificent can save you a bit of time.  If you have more of the "
                        + " same kind of problem to do, just let me know.  On the other hand, if you enjoy"
                        + " pressing that rodent, let me know that. Do you want to do another problem but"
                        + " skip the extra clicks?\n\n"; 
        
        diffAlertBoxHeight = 350.;
        backToTheRealWorld(theYes, theNo);
    } 
    
    public void showUnsavedDataAlert(String theYes, String theNo) { 
        showingAnAlert = true;
        alertTitle = "Whoa, there, Bucko!";
        alertHeader = "Are you sure about this???";
        alertContext ="I, SPLAT the Magnificent, never cease to wonder about the frailty of human (alleged) "
                        + " judgement.  Normally I just roll my eyes and shrug my shoulders, but I just"
                        + " can't let this one go.  After entering data you now want to just trash it?!?"
                        + " Are you SURE you want to leave your data in the dustbin of history?\n\n"; 
        diffAlertBoxHeight = 300.;
        backToTheRealWorld(theYes, theNo);
    } 

    public void showFirstLineContainsNumbersAlert(String theYes, String theNo) { 
        showingAnAlert = true;
        alertTitle = "Ack!?!?!  Your first line contains numerics!";
        alertHeader = "Pardon this interruption, but a clarification is needed...";
        alertContext = "Usually a number in the first line indicates an absence of predetermined"
                       + "\nlabels for the variables. In some cases, such as dosage level variables, "
                       + "\nvariable labels might actually be intended to be numeric.  Is it your "
                       + "\nintention that the numeric labels in the first line should be treated as"
                       + "\nquantitatve labels for the variables?\n\n";
        diffAlertBoxHeight = 350.;
        backToTheRealWorld(theYes, theNo);
    } 
        
    public void showAmbiguousColumnAlert(String message, String theYes, String theNo) { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we have a problem with the '" + message + "' variable!";
        alertHeader = "The 'type' of " + message +" appears to be ambiguous.";
        alertContext = "OK, so here's the deal.  I, SPLAT, can do all sorts of statistical"
                     + " stuff with categorical data, and ditto for quantitative data."
                     + " However, there seems to be a mixture of both (or some blanks)"
                     + " in this file. You may have downloaded a file from somewhere and"
                     + " it has values or blanks to indicate missing data. I, SPLAT,"
                     + " use asterisks for the purpose of indicating missing data."
                     + " Do you, User, want me, Splat, to convert these non-numerical "
                     + " values into asterisks, thus indicating missing values? \n\n" ; 
        diffAlertBoxHeight = 500.;
        backToTheRealWorld(theYes, theNo);
    } 
    
    public void logReg_Choose_0_And_1_Alert(String strFirst, 
                                            String strSecond, 
                                            String theYes, 
                                            String theNo) { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we have a problem with the binary variable";
        alertHeader = "Which of " + strFirst + " and " + strSecond + " is 'failure'?";      
        alertContext = "As you, Astute User, knows, the response variable in logistic"
                     + " regression is binary.  One value indicates 'failure', the"
                     + " other indicates 'success'. I, SPLAT the Magnicent, draw "
                     + " the line at attempting to read the (quasi-) minds of humans"
                     + " so you, User, will have to give me a clue.  Which of the"
                     + " values indicates failure?\n\n" ; 
        diffAlertBoxHeight = 500.;
        backToTheRealWorld(theYes, theNo);
    } 
    
    public boolean getShowingAnAlert() {return showingAnAlert; }
    
    public String getYesOrNo() { 
        String strTemp = yesNoAlert.getYesOrNo();
        return strTemp; }
    
    public static void backToTheRealWorld(String theYes, String theNo) {
        doTheDefaults();
        if (diffAlertBoxWidth != 0.) { alertBoxWidth = diffAlertBoxWidth; }
        if (diffAlertBoxHeight != 0.) { alertBoxHeight = diffAlertBoxHeight; }
        doTheSplatAlert(theYes, theNo);
        showingAnAlert = false;        
    }
    
    static private void doTheDefaults() {
        imagePath = "Warning.jpg";
        alertBoxWidth = 750.;
        alertBoxHeight = 575.;
        fitWidth = 75;
        imageOffSetX = 20.;
        imageOffSetY = 40.;
        horizImageSpace = 75;       
    }
    
    static private void doTheSplatAlert(String theYes, String theNo) {
        yesNoAlert = new YesNoAlert(theYes, 
                                    theNo,
                                    alertTitle,
                                    alertHeader,
                                    alertContext,
                                    imagePath,
                                    alertBoxWidth,
                                    alertBoxHeight,
                                    imageOffSetX,
                                    imageOffSetY,
                                    horizImageSpace,
                                    fitWidth); 
        showingAnAlert = false;
    }
}
