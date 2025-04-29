/****************************************************************************
 *                          MyYesNoAlerts                                      *
 *                            02/16/25                                      *
 *                             00:00                                        *
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
    
    boolean showingAnAlert; //, fourProbsAlreadyShown;
    int fitWidth, horizImageSpace;
    double alertBoxWidth, alertBoxHeight, imageOffSetX, imageOffSetY;
    double diffAlertBoxWidth, diffAlertBoxHeight;
    YesNoAlert yesNoAlert;
    
    String alertTitle, alertHeader, alertContext, imagePath;
    String theYes, theNo;
    
   public MyYesNoAlerts() { 
       //System.out.println("29 MyYesNoAlerts, *** Constructing");
       showingAnAlert = false;
       diffAlertBoxWidth = 0.;
       diffAlertBoxHeight = 0.;
       theYes = "You betcha!";
       theNo = "Not hardly!";
   }
   
   public void setShowingAnAlert(boolean tf) { showingAnAlert = tf; }

    public void showAvoidRepetitiousClicksAlert() { 
        showingAnAlert = true;
        alertTitle = "Boring, repetitive homework?";
        alertHeader = "I, SPLAT, has an option for you...";
        alertContext ="Ok, so here's the deal.  If you are doing the same kind of problem over and over, "
                        + "\nI, SPLAT the Magnificent can save you a bit of time.  If you have more of the "
                        + "\nsame kind of problem to do, just let me know.  On the other hand, if you enjoy"
                        + "\npressing that rodent, let me know that. Do you want to do another problem but"
                        + "\nskip the extra clicks?\n\n"; 
        
        diffAlertBoxHeight = 350.;
        backToTheRealWorld();
    }   

    public void showUnsavedDataAlert() { 
        showingAnAlert = true;
        alertTitle = "Whoa, there, Bucko!";
        alertHeader = "Are you sure about this???";
        alertContext ="I, SPLAT the Magnificent, never cease to wonder about the frailty of human "
                        + " judgement.  Normally I just roll my eyes and shrug my shoulders, but I"
                        + " just can't ignore this one.  After entering data you now want to just "
                        + " trash it?!? Are you SURE you want to leave your data in the dustbin of "
                        + " history?\n\n"; 
        diffAlertBoxHeight = 300.;
        backToTheRealWorld();
    } 


    public void showFirstLineContainsNumbersAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!?!?!  Your first line contains numerics!";
        alertHeader = "Pardon this interruption, but a clarification is needed...";
        alertContext = "Usually a number in the first line indicates an absence of predetermined"
                       + "\nlabels for the variables. In some cases, such as dosage level variables, "
                       + "\nvariable labels might actually be intended to be numeric.  Is it your "
                       + "\nintention that the numeric labels in the first line should be treated as"
                       + "\nquantitatve labels for the variables?\n\n";
        diffAlertBoxHeight = 350.;
        backToTheRealWorld();
    } 

    public void showAmbiguousColumnAlert(String message) { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we have a problem with the " + message + " variable!";
        alertHeader = "The 'type' of " + message +" appears to be ambiguous.";
        alertContext = "OK, so here's the deal.  I, SPLAT, can do all sorts of statistical"
                     + " stuff with categorical data, and ditto for quantitative data."
                     + " However, there seems to be a mixture of both (or some blanks)"
                     + " in this file. You may have downloaded a file from somewhere and"
                     + " it has values or blanks to indicate missing data. I, SPLAT,"
                     + " use asterisks for the purpose of indicating missing data."
                     + " \n\nDo you, User, want me, Splat, to convert these non-numerical "
                     + " values into asterisks, thus indicating missing values? \n\n" ; 

        backToTheRealWorld();
    }
    
    public void showRawDataOrSummaryAlert() { 
        showingAnAlert = true;
        alertTitle = "A quick question for you...";
        alertHeader = "I need some information before proceeding...";
        alertContext = "Do you have raw data to analyze, or do you have already summarized "
                        + " means, standard deviations, and sample sizes? \n\n"; 

        backToTheRealWorld();
    }
    
    public void showTidyOrTI8xAlert() { 
        showingAnAlert = true;
        alertTitle = "I, SPLAT, need to check about your data format...";
        alertHeader = "(The price you pay for the SPLAT versatility!)";
        alertContext = "In order to maximize the flexibility of your data entry, "
                        + " SPLAT allows two possible strategies.  One strategy is similar "
                        + " to how data is entered in the TI-8x calculators.  Another is to"
                        + " enter group / treatment information in one column and the values "
                        + " in another column (Tidy data).  Please indicate which format"
                        + " you usedf for this file.  \n\nThank you in advance!\n\n"; 

        backToTheRealWorld();
    }
    
    public boolean getShowingAnAlert() {return showingAnAlert; }
    
    public void setTheYes(String toThis) { theYes = toThis; }
    public void setTheNo(String toThis) { theNo = toThis; }
    
    public String getYesOrNo() { 
        String strTemp = yesNoAlert.getYesOrNo();
        if (strTemp == null) { strTemp = "Cancel"; }
        return strTemp; }
    
    public void backToTheRealWorld() {
        doTheDefaults();
        if (diffAlertBoxWidth != 0.) { alertBoxWidth = diffAlertBoxWidth; }
        if (diffAlertBoxHeight != 0.) { alertBoxHeight = diffAlertBoxHeight; }
        doTheSplatAlert();
        showingAnAlert = false;        
    }
    
    private void doTheDefaults() {
        imagePath = "Warning.jpg";
        alertBoxWidth = 750.;
        alertBoxHeight = 575.;
        fitWidth = 75;
        imageOffSetX = 20.;
        imageOffSetY = 40.;
        horizImageSpace = 75;       
    }
    
    private void doTheSplatAlert() {
        yesNoAlert = new YesNoAlert(theYes, theNo,
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
