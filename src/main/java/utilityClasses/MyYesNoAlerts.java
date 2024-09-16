/****************************************************************************
 *                          MyYesNoAlerts                                      *
 *                            09/14/24                                      *
 *                             03:00                                        *
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
        alertContext ="I, SPLAT the Magnificent, never cease to wonder about the frailty of human (alleged) "
                        + "\njudgement.  Normally I just roll my eyes and shrug my shoulders, but I just"
                        + "\ncan't let this one go.  After entering data you now want to just trash it?!?"
                        + "\nAre you SURE you want to leave your data in the dustbin of history?\n\n"; 
        diffAlertBoxHeight = 300.;
        backToTheRealWorld();
    } 
    
    public boolean getShowingAnAlert() {return showingAnAlert; }
    
    public String getYesOrNo() { 
        String strTemp = yesNoAlert.getYesOrNo();
        return strTemp; }
    
    public static void backToTheRealWorld() {
        doTheDefaults();
        if (diffAlertBoxWidth != 0.) { alertBoxWidth = diffAlertBoxWidth; }
        if (diffAlertBoxHeight != 0.) { alertBoxHeight = diffAlertBoxHeight; }
        doTheSplatAlert();
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
    
    static private void doTheSplatAlert() {
        yesNoAlert = new YesNoAlert(alertTitle,
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
