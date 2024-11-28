/****************************************************************************
 *                            MyAlerts                                      *
 *                            11/17/24                                      *
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

import dataObjects.*;

public class MyAlerts {
    
    static boolean showingAnAlert, fourProbsAlreadyShown;
    static int fitWidth, horizImageSpace;
    static double alertBoxWidth, alertBoxHeight, imageOffSetX, imageOffSetY;
    
    static SplatAlert splatAlert;
    
    static String alertTitle, alertHeader, alertContext, imagePath;
    
   public MyAlerts() { 
       fourProbsAlreadyShown = false;
       showingAnAlert = false;
   }
   
    public void setShowingAnAlert(boolean tf) { showingAnAlert = tf; }
   
    /************************************************************************
     *  This alert is a companion of a YesNo choice about changing numeric  *
     *  labels to categorical labels as a intermediate step in a file read. *
     ***********************************************************************/
    public static void showCategoricalLabelsAlert() { 
        showingAnAlert = true;
        alertTitle = "Well, OK, then -- read this CAREFULLY.";
        alertHeader = "This is what we'll do...";
        alertContext = "SPLAT will add a set of fake Non-Labels to your data and " 
                       + " read the file. Save the data UNDER A DIFFERENT FILE NAME "
                       + " so that no data is lost from your original file. Then, as" 
                       + " soon as possible fix the labels in the new file.";

        backToTheRealWorld();
    }
   
    public static void showLabels_Data_MismatchAlert() { 
        showingAnAlert = true;
        alertTitle = "Yikes!  Problem reading the file...";
        alertHeader = "Mismatch of labels and data";
        alertContext = "It appears that at least one data line has more values "  
                        + " than the number of labels.  I, SPLAT, though being " 
                        + " virtually omniscient, cannot fix this problem.  Please"  
                        + " check this file in your favorite word processor and "  
                        + " try, try, again. ";; 

        backToTheRealWorld();
    }
   
       public static void showOutOfMemoryAlert() { 
        showingAnAlert = true;
        alertTitle = "YO! USER! LISTEN UP! YOU ARE IN DEEP DOO-DOO!!! ";
        alertHeader = "Your heap is in a heap of trouble.  (Tee hee, SPLAT joke)";
        alertContext = "User, we have a Houston-sized problem.  Your computer is out of memory "
                        + " in what is known as the 'heap.' (Google it).  The heap is that part"
                        + " of memory that your computer has allocated to me, SPLAT, to do your "
                        + " statistics stuff. Unfortunately, whatever it is that you want me "
                        + " to do takes up more memory than your computer has given me. "
                        + " \n\nThere are ways around this, but I, SPLAT, refuse to mess with your "
                        + " computer's memory in an attempt to recover from this situation. I, "
                        + " SPLAT, the (otherwise) Magnificent, cannot establish the cause of "
                        + " the problem, and have nowhere to turn.  So, I'm going to bail on you. "
                        + " Rest assured that you have not lost any of your original data."
                        + " To REALLY be super-safe, I suggest you reboot your computer. \n\n"; 

        backToTheRealWorld();
    }
   
    public static void showBlankLabelsAlert() { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we (i.e. you) have a problem with this file!";
        alertHeader = "Something seems to be missing: label(s)";
        alertContext = "I, SPLAT, ever vigilant and never trusting humans, have discovered a "
                        + " bit of a problem with this file! You, dear User, are required to"
                        + " have the same number of data values in each line, and that number "
                        + " must equal the number of labels in the first line of data. It doth"
                        + " appear that you have transgressed.  This may be no big deal, or "
                        + " it may be catastrophic. (Or somewhere between.) I MAY be able to "
                        + " read the file successfully; on the other hand I may bail on you. "
                        + " In either case, you should check to make sure your variable labels "
                        + " are (a) not blank, and (2) what you want. I, SPLAT the Vigilant, "
                        + " created a generic label for the blanks, so fix those before doing "
                        + " anything else.  Bottom line, you MIGHT be OK to proceed.  But if "
                        + " not, and I bail on you, grab Excel and fix the file. \n\n"; 

        backToTheRealWorld();
    }
   
    public static void showQuantANOVAPlotsAlert() { 
        showingAnAlert = true;
        alertTitle = "OK, User, you need to wake up and pay attention!!!";
        alertHeader = "Put that smart phone down and listen up.";
        alertContext = "For reasons apparently related to memory and hardware, the circle plot and"
                        + " homogeneity check plots are very unstable. To get them to do what "
                        + " you want will be an adventure!  Somtimes they will take a while to"
                        + " appear and disappear, sometimes they will not appear until you click "
                        + " on the dashboard, sometimes you can move, sometimes not. You can"
                        + " change the scales (usually), but you should NEVER EVER attempt to "
                        + " resize these two plots -- I, SPLAT, will expire on the vine if you"
                        + " do. It's not my fault, and as nearly as I can tell, it isn't my "
                        + " programmer's fault (incompetent though he is!) We all will just have "
                        + " to grin (or not) and bear it.  You can't fight city hall -- hardware "
                        + " and Mother Nature bats last.\n\n"; 

        backToTheRealWorld();
    }
   
    public static void showMustBeNonBlankAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  Blanks left in a critical entry field!";
        alertHeader = "This is at least one tabula rasa too many...";
        alertContext = "So what's the deal here?  With all due respect to John Locke, "
                        + " I, SPLAT, rise to object to this omission!!  Did you think this "
                        + " perfidy would pass without notice?!?  Are you one of those"
                        + " who trust in haruspices to somehow provide correct analysis with "
                        + " (a) the sparse information you provide and (2) a consultation"
                        + "  with the entrails of chickens?  That may have worked in ancient"
                        + "  Rome, but it won't work here.  Let's try this entry thing again.\n\n"; 

        backToTheRealWorld();
    }
    
        public static void showAcknowledgeQuantLabelsAlert() { 
        showingAnAlert = true;
        alertTitle = "Well, OK, then -- read this CAREFULLY.";
        alertHeader = "This is what we'll do...";
        alertContext = "SPLAT will add a set of fake Non-Labels to your data and " + 
                                " read the file. Save the data UNDER A DIFFERENT FILE NAME " +
                                " so that no data is lost from your original file. Then, as" + 
                                " soon as possible fix the labels in the new file.\n\n";

        backToTheRealWorld();
    }
        
    public static void showMustBeTwoUniquesInLogisticAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  Not a pair of Unique values!";
        alertHeader = "There must be exactly two unique values in logistic regression...";
        alertContext = "OK, User, here's the deal.  There is such a thing as polytomous logistic"
                        + " regression, which allows more than two unique values.  However, "
                        + " polytomous sounds too much like polygamous, and I, SPLAT the Virtuous, "
                        + " will not go there!  In simple (and virtuous!) logistic regression, only"
                        + " two values of the response variable are allowed. Check your data file"
                        + " and try again, this time with two values and thus gain the virtue" 
                        + " and confidence typically attached to Mark Twain's proverbial Presbyterian"
                        + " with four aces.\n\n";

        backToTheRealWorld();
    }
   
    public static void showSillyProbabilityAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  Silly probability selected!";
        alertHeader = "What is this -- trick or treat!?!?!...";
        alertContext = "OK, you've had your fun now.  A or not A?  A and A?  A given A? "
                        + " SPLAT is less than appreciative of your (alleged) humor.  You "
                        + " may have time to play probability selection games, but I have "
                        + " better things to do.  I happen to be reading 'A Programmer's "
                        + " Guide to the Galaxy, and I think I can safely say that not only"
                        + " is 42 not the answer here, neither is your selection.  User, "
                        + " get with the program and come up with better choices! \n\n"; 

        backToTheRealWorld();
    }
   
    public static void showFourProbsShowingAlert() { 
        if (fourProbsAlreadyShown) { return; }  // Only show once
        showingAnAlert = true;
        alertTitle = "Ack!  Three roads are diverging in the woods!";
        alertHeader = "And I have no clue which is the one to travel by.";
        alertContext = "Incredibly, this problem is not of your creation, dear User. "
                        + " Of course, it is not SPLAT's fault either!  We can blame "
                        + " my perfidious programmer for this! The problem basically"
                        + " is that with the calculation of four probabilities, it is"
                        + " not clear what direction to go.  So SPLAT will pass the"
                        + " buck and let you, User, start selecting from scartch.\n\n";

        doTheDefaults();
        doTheSplatAlert();
        showingAnAlert = false;
        fourProbsAlreadyShown = true;
    }
    
    public static void showNoVariabilityAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  Alas, misguided User! I see him, Horatio, a fellow of infinite consistency... ";
        alertHeader = "I, SPLAT, cannot BELIEVE that you asked me to calculate statistics for this Column.";
        alertContext = "OK, User, we need to talk.  All the data points in this column are the same.  Now,"
                        + " in some places, on some planets, such consistency is regarded as virtuous. This place,"
                        + " on this planet, ain't one of them; variability is the ultimate virtue here.  I, SPLAT,"
                        + " need to calculate things like Z-scores and correlations, and without variability, such "
                        + " calculations would be walking into a  lions' den wearing a sirloin necktie. So, bottom "
                        + " line, grab your arithmetic primer and turn the crank yourself.\n\n";
        backToTheRealWorld();
    }
    
    public static void showNoReplicationInANOVA2Alert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  Alas, misguided User! I see you, Horatio, a fellow of infinite consistency... ";
        alertHeader = "I, SPLAT, cannot BELIEVE that you would ask me to analyze these data!";
        alertContext = "OK, User, we need to talk.  It is technically possible to have blocks with only one"
                        + " data point in them.  However, in SPLAT's view, variability in treatment data "
                        + " is regarded as very virtuous.  I, SPLAT, need to calculate things like Z-scores "
                        + " correlations, and stuff like that.  Without variability, such calculations would be"
                        + " like walking into a  lions' den wearing a sirloin necktie: dangerous to one's health."
                        + " So to do this analysis you will need to find less virtuous statistical software.\n\n";
        backToTheRealWorld();
    }

   
    public static void showTooManyRowsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!! The requested graphic capabilities are beyond my capability!!!";
        alertHeader = "(And probably statistical reasonableness also... ) ";
        alertContext = "As you are aware, I, SPLAT, have no limitations whatsovever.  However,"
                        + " I live with a two-bit programmer in a 64-bit world, and he is "
                        + " not up to the task of drawing a picture with more than 12 colors."
                        + " Statistical stuff, not a problem.  Visual stuff, non compos mentis."
                        + "  I'm basically stuck! Amazon.com sent me this guy cheap.  I guess I"
                        + " got what I paid for.\n\n"; 

        backToTheRealWorld();
    }  
    
    // In Power_SinglePropDialog
    public static void showEffectSizeAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!! You have requested a not very effective effect size!!!";
        alertHeader = "I, SPLAT, am Powerless (tee hee, great pun, eh??) to act!!";
        alertContext = "It appears that the effect size you are requesting will take you outside"
                        + " the bounds of proportional propriety,  i.e.  0.0 < prop < 1.0."
                        + " For the sake of my -- i.e. SPLAT's --  sanity, Please fix this"
                        + " at your earliest convenience.  Actually, now that I think of it,"
                        + " please fix it at MY earliest convenience which I, SPLAT, assume"
                        + " to be, as it were, now.\n\n";  
        
        backToTheRealWorld();
    }  
    
    public static void showEffectSizeSuggestion() { 
        showingAnAlert = true;
        alertTitle = "Wow! Your funding agency must be King Midas, LLC!!!";
        alertHeader = "I, SPLAT, am ethically bound to counsel you about sample sizes.";
        alertContext = "The size of one or more of your samples is so spectacularly great"
                        + " that your hypothesis test will almost certainly be STATISTICALLY"
                        + " significant (that is, the p-value will be knocking on the door "
                        + " of 0).  However, that does not mean your result is of PRACTICAL "
                        + " significance. I, SPLAT The Magnificent, suggest you give some "
                        + " attention to the effect size in the printout of the results.\n\n";  
        
        backToTheRealWorld();
    } 
    
    public static void showLongTimeComingWarning() { 
        showingAnAlert = true;
        alertTitle = "Wow! Your funding agency must be King Midas, LLC!!!";
        alertHeader = "I, SPLAT, will gird my loins (whatever THEY are) for this procedure.";
        alertContext = "The size of one or more of your samples is so spectacularly great"
                        + " that some procedures may take a bit of time to do.  Trust me, "
                        + " I, SPLAT, am not just sitting on my loins (whatever THEY are),  "
                        + " I will be doing my best, running my abacus as fast as I can. "
                        + "  Worry not if it appears that I have lost interest!  And I  "
                        + " apologize in advance if I repeatedly warn you.\n\n";  
        
        backToTheRealWorld();
    } 
    
    public static void showGenericBadNumberAlert(String message) { 
        showingAnAlert = true;
        alertTitle = "Hey!  User!  That's not a " + message + "!";
        alertHeader = "You have entered something other than " + message +".";
        alertContext = "Aha!  Thought you'd slip one by, did you?  Trying to impress"
                     + " your lab partner with your knowledge of more advanced math?"
                     + " Did you try an ordered pair?  Something complex?  A matrix?"
                     + " A quaternion?  That sort of wizardry is not allowed here;"
                     + "  in this field " + message 
                     + " of the Arabic persuation is needed.  Just in "
                     + " case you were wondering, the Decline and Fall of the Roman"
                     + " Empire also included the Decline and Fall of Roman numerals."
                     + " Let's try this data entry thing again.\n\n" ; 

        backToTheRealWorld();
    }
    
    public static void showNoVarianceInANOVAAlert(String varLabel) { 
        showingAnAlert = true;
        alertTitle = "Hey!  User!  That's no variability in " + varLabel + "!";
        alertHeader = "What could you possibly be thinking?!?!?!?";
        alertContext = "User, you have a serious problem with the data in \n\"" +
                                         varLabel  + "." +
                        "\n.  Has it not occured to to you that Analysis of VARIANCE"  +
                        " might require, variance!?!?  Hello?!? You don't get variance "  +
                        " with fewer than two data values, User. You could possibly blame " +
                        " this on a newly downloaded file, or you could take responsibility " +
                        " for lack of due diligence. I, Splat, will NOT be a conspirator " +
                        " in this perfidious attempt to analyze something not there, i.e. " +
                        " VARIANCE.  Just click the button below and I'll forget about your " +
                        " boo-boo...this time."; 

        backToTheRealWorld();
    }
    
    public static void showNoVarianceIn2IndAlert(String varLabel) { 
        showingAnAlert = true;
        alertTitle = "Hey!  User!  That's no variability in " + varLabel + "!";
        alertHeader = "What could you possibly be thinking?!?!?!?";
        alertContext = "User, you have a serious problem with the data in \n\"" +
                                         varLabel  + "." +
                                        "\n.  Has it not occured to to you that statistics is all about, "  +
                                        " variability in data!?!?  Hello?!? You generally don't get variance "  +
                                        " with fewer than two data values, User. You could possibly blame " +
                                        " this on a newly downloaded file, or you could take responsibility " +
                                        " for lack of due diligence. I, Splat, will NOT be a conspirator " +
                                        " in this perfidious attempt to analyze something not there, i.e. " +
                                        " VARIANCE.  Just click the button and I'll forget about your boo-boo" +
                                        " ...this time."; 

        backToTheRealWorld();
    }
    
    public static void showHomogeneousVariableAlert() { 
        showingAnAlert = true;
        alertTitle = "Yo! You on the keyboard! Apples & Oranges Alert!!";
        alertHeader = "What could you possibly be thinking?!?!?!?";
        alertContext = "User, you have been misinformed by your math teachers!" +
                            "\n\n It turns out that you CAN compare apples and oranges. In  "  +
                            " statistics we do it all the time.  However, in order to pull "  +
                            " that off successfully, we need apples and we need oranges.  You, in " +
                            " your mathematical straight jacket, seem to have chosen to compare " +
                            " the same value in a categorical VARIABLE.  Variety is the spice of " +
                            " life for humans and for VARIABLES. You wanna get with the program?" +
                            " Let's try this selection again, this time with more than one value " +
                            " for each of the VARIABLES.\n\n"; 

        backToTheRealWorld();
    }
       
    public static void showPropOopsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!!  A bad proportion or count oopsie has occurred!!";
        alertHeader = "User, you have entered an illegal proportion or count.  What are you thinking???";
        alertContext = "Only (a) decimals between 0 and 1 and (b) counts greater than 0 and less than"
                        + " the sample sizes are allowed in these fields.  Did you form the belief"
                        + " that you could be sneaky and fool SPLAT into allowing a sample size or"
                        + " a proportion outside these boundaries?!?  Ha!!  The number of hours before"
                        + " which SPLAT was programmed now exceeds 24.  Let's try it again, this time"
                        + " getting numbers within the bounds of mathematical respectability.\n\n"; 
        
        backToTheRealWorld();
    }
    
    public static void showLeftRightOrderAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  User, you have defied the Natural Order of Things!!!";
        alertHeader = "We need to have a little philosophical chat...";
        alertContext = "In the arena of moral philosophy there is something known as the Natural Order"
                       + " of Things. In the less than moral philosophies, of which statistics is surely"
                       + " a proud member, there is also a natural order of things. In the mathematical world "
                       + " numbers toward the left on the number line are less than numbers toward the right"
                       + " on the number line.  SPLAT does not care if you agree.  You are just going to have to "
                       + " live with it and fix your numbers here to reflect that mathematical reality.  If you"
                       + " have a different epistemological view, take it up with Plato and Pythagorus.\n\n"; 
        
        backToTheRealWorld();
    }
    

    public static void showDataHaveBeenCleanedAlert() { 
        showingAnAlert = true;
        alertTitle = "Your data have been cleaned!!!!!!  BRAVO!!!!!!";
        alertHeader = "But perhaps not Covid 19 cleaned???";
        alertContext = "Thank you for your efforts to clean your data.  I, SPLAT,  do not BELIEVE"
                        + " how often that humans err when entering data. In my long experience in "
                        + " observing, I have found that humans stroke the keyboard without checking, "
                        + " depending only on their feckless fingers. It is not that I,  SPLAT, don't "
                        + " trust you...much... but just  in case, I am going to return you to your"
                        + " regularly scheduled analysis for yet another look at the raw data.\n\n";
        
        backToTheRealWorld();
    }

    public static void showNotAllFieldsGoodAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  I, SPLAT, have detected some bad data fields!!";
        alertHeader = "And they ain't corn or sorghum fields, Bucko!...";
        alertContext = "Ok, listen up.  Some numbers gotta be positive, some gotta"
                        + " be integers, some gotta be probabilities, some gotta be"
                        + " nonzero, and Frank Sinatra's gotta be him.  I, SPLAT, do not"
                        + " make up the mathy rules, I only enforce the mathy rules. "
                        + " And SOMEwhere you have broken one or more of these rules. "
                        + " Let's try this numeric entry thing again; this time put down"
                        + " that smart phone and pay attention.\n\n";  
        
        backToTheRealWorld();
    }
    
    public static void showMissingDataAlert() { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we (i.e. you) have a problem with this file!";
        alertHeader = "Something seems to be missing: data";
        alertContext = "I, SPLAT, ever vigilant and never trusting humans, have discovered a "
                        + " bit of a problem with this file! You, dear User, are required to"
                        + " have the same number of data values in each line, and that number "
                        + " must equal the number of labels in the first line of data. It doth"
                        + " appear that you have transgressed.  This may be no big deal, or "
                        + " it may be catastrophic. (Or somewhere between.) I MAY be able to "
                        + " read the file successfully; on the other hand I may bail on you. "
                        + " In either case, you should check to make sure your vdata is right. "
                        + " I, SPLAT the Vigilant, may have created asterisks (*) for missing "
                        + " data, so look for those. Bottom line, you MIGHT be OK to proceed. "
                        + "  But if not, and I bail on you, grab Excel and fix the file. \n\n"; 
        
        backToTheRealWorld();
    }
    
    public static void showMissingNTreatmentsAlert() { 
        showingAnAlert = true;
        alertTitle = "What Ho! (as the Bard might write), a Chicken Soup Alert!!!";
        alertHeader = "So, what is the first thing you need when you make chicken soup??";
        alertContext = "As the old <insert fav source> proverb says, the first thing you need to make"
                        + " chicken soup is a chicken.  In experimental design soup, the first two"
                        + " things you need to assign treatments to subjects are (wait for it) treatments"
                        + " and subjects!  And you, dear User, seem to be missing the number of treatments."
                        + " Perhaps you should be thinking about tomato soup instead?  Tomatoes don't "
                        + " run as fast as chickens.  \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showMissingNSubjectsAlert() { 
        showingAnAlert = true;
        alertTitle = "What Ho! (as the Bard might write), a Chicken Soup Alert!!!";
        alertHeader = "So, what is the first thing you need when you make chicken soup??";
        alertContext = "As the old <insert fav source> proverb says, the first thing you need to make"
                        + " chicken soup is a chicken.  In experimental design soup, the first two"
                        + " things you need to assign treatments to subjects are (wait for it) treatments"
                        + " and subjects!  And you, dear User, seem to be missing subjects.  Perhaps you "
                        + " should be thinking about tomato soup instead?  Tomatoes don't run as fast as"
                        + " chickens.  \n\n";
        
        backToTheRealWorld();
    }
    
    //  **************************  Bootstrapping alerts  *********************
    
    public static void showZeroReplicationsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!!  Zero / Blank replications?!?!?!?";
        alertHeader = "Um, User, why did I do all the prep work for you???";
        alertContext = "OK, User, we need to have a little chat.  I do a ton of work behind the scenes"
                        + " to set up this bootstrapping stuff.  Generating a bazillion random numbers "
                        + " doesn't just happen, you know -- I have to find memory space, and read up "
                        + " on linear congruential processes to generate pseudo-random numbers.  And "
                        + " after all my efforts you bail on me?!?!?!?  Or did you just forget to fill" 
                        + " in that NReps blank?  Let's try this choice thing again from the top."
                        + " Or bail away, as you wish.  Harumph!!!  \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showOnlyOneStatisticAllowedAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!!  Only one statistic at a time, please!";
        alertHeader = "Were we (i.e. you) a little careless in reading the directions!?!?!";
        alertContext = "OK, User, we need to have a little chat.  I do a lot of work behind the scenes"
                        + " to do this bootstrapping, and it takes lots of memory to keep all these"
                        + " numbers stored on your computer. And frankly, I'm not sure you humans can "
                        + " handle more than one statistic at a time.  I am only thinking of you, User, "
                        + " when I restrict you choices to one statistic to bootstrap at a time. Let's "
                        + " try this choice thing again from the top. Or not, as you wish.  Harumph!!!\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showZeroStatsChosenAlert() { 
        showingAnAlert = true;
        alertTitle = "What Ho! (as the Bard might write), a Chicken Soup Alert!!!";
        alertHeader = "So, what is the first thing you need when you make chicken soup??";
        alertContext = "As the old <insert fav source> proverb says, the first thing you need to make"
                        + " chicken soup is a chicken.  When one (i.e. you) wishes to bootstrap a"
                        + " statistic, the first thing you need is a statistic. You, dear User, "
                        + " seem to have missed this necessary element in the process. Perhaps you"
                        + " should go for tomato soup instead?  Tomatoes don't run as fast as chickens."
                        + " In any case, lets try this choice thing again from the top.  \n\n";
        
        backToTheRealWorld();
    }
   

    public static void showNonUniqueCategoriesAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  An adamant assertion of adverse ambiguity appears amok anon!!";
        alertHeader = "Your categorical information is not unique.";
        alertContext = "Ok, so here's the thing.  There haven't been many Henrys, but there "
                        + " was -- thank goodness!!! -- only one Henry VIII, for which  "
                        + " Bolyns everywhere are thankful.  But I, SPLAT, digress. You, "
                        + " USER, are required to provide unique names for categorical" 
                        + " information.  You could at least append Roman numerals to your "
                        + " currently ambiguous Henry's. Similar is OK, same is not. \n\n"; 
        
        backToTheRealWorld();
    }

    
    public static void showMoreThanOneSelectionAlert() { 
        showingAnAlert = true;
        alertTitle = "Warning!  More than one probability selected!";
        alertHeader = "Selecting a probability is like voting!";
        alertContext = "Ok, so here's the deal.  I, SPLAT, will only allow you to make"
                       + " one selection at a time. I, of course, being SPLAT, could"
                       + " handle any number of selections with one processor tied "
                       + " behind my motherboard.  But humans, even with NO hands"
                       + " tied behind their behind, have proven to be inadequate."
                       + " I'm only thinking of you. Click on OK below and you "
                       + " can try counting up to one again. \n\n";
        
        backToTheRealWorld();

    }    
    
    public static void showBadRangeAlert() { 
        showingAnAlert = true;
        alertTitle = "Warning!  Bad range selected!!";
        alertHeader = "Left is sinistram, Right is justum...";
        alertContext = "Ok, so here's the deal.  With apologies to those of the left hand "
                        + " persuasion, left is smaller than right in what we think of as "
                        + " the usual mathematical notion of notation.  I, SPLAT, do not "
                        + " make the mathy rules, I only enforce the mathy rules. Let's try"
                        + " this left is smaller than right data entry thing again.\n\n";  
        
        backToTheRealWorld();
    }
    
    public static void showNonSumToOneAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  As they say in deep space, 'Houston, we have a problem...'";
        alertHeader = "There is a slight (or not) mathematical difficulty with your numbers.";
        alertContext = "The sum of your expected proportions is different from 1.0.  This could be"
                        + " due to roundoff error, in which case your proportions will be (slightly)"
                        + " adjusted for the chi square calculations by Omniscient Me,  SPLAT.  Or, "
                        + " your proportions might be due to (oh, so typical!) User Malfeasance!"
                        + " Add up your hypothesized  proportions and divide by 1.0.  (Just kidding"
                        + " about that division by 1.0.  Har har.)  If the sum is significantly "
                        + " different from 1.0, you will have to find the problem yourself. I, SPLAT, "
                        + " may be omniscient, but I don't read human minds -- such as they are.\n\n";
        
        backToTheRealWorld();
    }
    
    // Called by NoInt_Quadreg_Controller and NoInt_Regr_Controller
    public static void showNoLegalBivDataAlert() { 
        showingAnAlert = true;
        alertTitle = "Uh-oh -- Houston, we have a problem...";
        alertHeader = "I, SPLAT, am not finding any legal data points.";
        alertContext = "While not in any way criticizing your choice of variables, I must tell you that"
                        + " there do not seem be be any real number pairs to be found.  Are you playing" 
                        + " with my digital mind here?  Have you foolishly chosen to do regression with "
                        + " one or more text variables? Let's put that smart phone down and order pizza " 
                        + " later. Grab a cup of good old coffee and pay attention to the task at hand!\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showIncompleteBlocksAlert() { 
        showingAnAlert = true;
        alertTitle = "Hey, USER -- This ain't Animal Farm!!";
        alertHeader = "I, SPLAT, only handle COMPLETE blocks!!!";
        alertContext = "So, what's the deal here?!?!?!  You think that some blocks are more equal than other blocks?  Think"
                        + " you can skimp on subjects by shorting some of the blocks?!??!  SPLAT is shocked, SHOCKED at " 
                        + " this perfidious attempt to slip one past the statistical analysis!!! You have been busted by the"
                        + " vaunted SBI (SPLAT Bureau of Investigation).  Blocks must be equal before the Law of Experimental "
                        + " Design. In the immortal words of Captain Picard, Make It So.\n\n"; 
        
        backToTheRealWorld();
    }
    
    public static void showBadRMFileStructureAlert() { 
        showingAnAlert = true;
        alertTitle = "Hey, USER -- Your file structure is not RM-ish!!";
        alertHeader = "We (well, I, SPLAT) am VERY picky when analyzing repeate measures!!";
        alertContext = "So, what's the deal here?!?!?! Missing data, perhaps?  One of your subjects snuck out for some"
                        + " hanky-panky?  A lab rat made a break for it? Your response variable is categorical? "
                        + " Duplicate entries for one or more of your subjects?  Trying to slip one past the statistical"
                        + " analysis gods, are we?  Hah!! You have been busted by the vaunted SBI (SPLAT Bureau of"
                        + " Investigation.) I, Sargeant SPLAT of the SBI, will not proceed with a repeated measures "
                        + " ANOVA unless I have a correctly formatted file to work with. In the immortal words of "
                        + " Captain Picard,,Make It So!\n\n"; 
        
        backToTheRealWorld();
    }
    
    public static void showTextEntryAdvisoryAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!! A potentially incredibly catastrophic malfeasance may have occurred!!";
        alertHeader = "Or, on the other hand, it could just be a slight problem, or none at all.";
        alertContext = "Dear user, I need to explain a bit of inside SPLAT.  I, SPLAT, initialize all variables"
                        + " to be numeric when I start up.  It is perfectly fine to read files with categorical"
                        + " variables, and I, SPLAT will attempt to figure this out.  If I have difficulty, I"
                        + " will let you know.  Now, here is the deal.  If you accidentally key in a character"
                        + " that is not numeric, I will proceed as if the variable as text.  So, even if you"
                        + " fix it before ENTERing, I, SPLAT, will still treat the variable as categorical.  You"
                        + " should first key in and ENTER the correct value, and then click at the top of the"
                        + " variable column and change the data type back to numeric.\n\n";
        
        backToTheRealWorld();
    }

    //  **************************  Chi square alerts  *********************

    public static void showEmptyCategoriesAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!! A category is missing!  Could this be a CategoryNapping??";
        alertHeader = "A category has been lost, stolen, kidnapped, or strayed...or perhaps forgotten?!?";
        alertContext = "Dear User: I, SPLAT, am chagrined to report that at least one of your categories is"
                        + " missing.  Not that I really give a rip, but how do you intend to intelligently"
                        + " discuss your results if you don't have all your marbles?  I mean categories?"
                        + " Someone would surely notice, and in these days of gotcha social media, your"
                        + " subsequent lack of credibility could -- and would -- be Tweeted or Zoomed or"
                        + " Snapchatted or YouTubed or whatEVER!  That would not bode well for your reputation."
                        + " Let's try fixing this problem before things really get out of control.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showEmptyExpectedPropsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!! An expected proportion is missing!";
        alertHeader = "An expected proportion has been lost, stolen, or strayed...";
        alertContext = "Dear User: I am chagrined to report that at least one expected proportion"
                        + " is missing.  Did it/they make a break for it?  Are they gathering to "
                        + " point and giggle at your outlandish hypotheses about their values? "
                        + " Perhaps writing Op-ed pieces for the New York Times Sunday Review?  "
                        + " That sort of grief you do NOT need. Let's head this off at the pass"
                        + " and quietly fix this problem before the Fourth Estate gets wind of it.\n\n";
        
        backToTheRealWorld();
    }


    public static void showEmptyObservedValuesAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!! An observed value is missing??";
        alertHeader = "An observed value has been lost, stolen, kidnapped, or strayed...";
        alertContext = "Dear User:  I am chagrined to report that at least one alleged observed value"
                        + " seems not to have been observed.  Due to a nondisclosure agreement of some kind?"
                        + " Might something more sinister be afoot?  It is difficult to imagine that the "
                        + " the observations would not want their promised Warhollian fifteen minutes of fame!"
                        + "  Have you sprited them away to the proverbial undisclosed location?  Perhaps you should"
                        + " fix this problem before their absence attracts the attention of the statistics police"
                        + " and things get REALLY out of control.\n\n"; 
        
        backToTheRealWorld();
    } 
    
    // In DataManager, adding or deleting a row
    public static void showNonPositiveRowAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! An incorrect row number has been selected!";
        alertHeader = "Row, row, row your boat as you wish, but if you are selecting a row...";
        alertContext = "Hey!  User!  I,  SPLAT,  know it is difficult to maintain a positive outlook on things" 
                        + " when you are engaged in the seemingly Sisyphean data entry required as a part"
                        + " of statistical analysis.  However, a positive outlook in the form of a row number"
                        + " in the form of a positive integer is required here.  Let's all adjust our attitude"
                        + " (except for me, SPLAT), and try this row entry thing again. \n\n"; 
        
        backToTheRealWorld();
    } 
    
    public static void showBlankVariableAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! I, SPLAT, your kindly statistics program, require a variable here!!";
        alertHeader = "One may be the loneliest number for 3 Dog Night, but it is better than zero.";
        alertContext = "Dear user, your ability to count seems to border on not unlike less than" + 
                           " what would generally be regarded as competent.  If you wish to perform" +
                           " a univariate analysis you need -- wait for it -- a variable!  Let's try" +
                           " this selection thing again...\n\n";
        
        backToTheRealWorld();
    }
   
    public static void showDataTypeErrorAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! Incorrect data type selected -- could witchcraft most foul be afoot?";
        alertHeader = "Fair is foul and foul is fair, but not in the world of data types!";
        alertContext = "Dear user, you have followed the wrong yellow brick road.  One of the Three Witches" + 
                           " in Macbeth, or possibly the Wicked Witch of the North, has led you to select a " +
                           " Quantitative rather than the required Categorical variable, or a Categorical " +
                           " variable rather than the required Quantitative variable.  Let's" +
                           " try clicking those ruby slippers again...\n\n";
        
        backToTheRealWorld();
    }     
    
    // This alert checks for a blank return when inserting/deleting a row
    public static void showBlankRowAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! No row number has been selected!";
        alertHeader = "Row, row, row your boat as you wish, but if you are selecting a row...";
        alertContext = "Hey!  User!  I,  SPLAT,  know it is difficult to maintain a positive outlook on things" 
                        + " when you are engaged in the seemingly Sisyphusian data entry required as a part"
                        + " of statistical analysis.  However, a positive outlook in the form of a row number"
                        + " in the form of a positive integer is required here.  Let's all adjust our attitude"
                        + " (except for me, SPLAT), and try this row selection thing again. \n\n"; 
        
        backToTheRealWorld();
    } 
    
    public static void showUnbalancedRBAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  A serious experimental design Issue has raised its ugly head!";
        alertHeader = "OK, User, now put that phone down and listen carefully...";
        alertContext = "I, SPLAT, am chagrined to inform you of a mismatch between your design and your data." 
                        + " In the Randomized Block design all the blocks are required to be the same size." 
                        + " I, SPLAT the Omniscient, do not make the rules, I only ruthlessly enforce them." 
                        + " The only way around this unequal block size problem is to use a multiple regression"  
                        + " approach.  I, SPLAT, in my infinite wisdom, perform  multiple regression but I will" 
                        + " most definitely NOT set up the necessary 'dummy' variables automatically for you.\n\n";
        
        backToTheRealWorld();
    } 

    public static void showDuplicateLabelsInFileAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  An adamant assertion of adverse ambiguity appears amok anon!!";
        alertHeader = "The variable names in this file are not unique!";
        alertContext = "Ok, so here's the thing.  There have been a few Henrys, but there was -- thank goodness!!! "
                        + " -- only one Henry VIII, for which Bolyns everywhere should be thankful. But I, SPLAT, "
                        + " digress.  Variable names in a file are required to be unique!" 
                        + "\n\n You, User, will have to fix this problem by yourself.  Perhaps simply adding a "
                        + " Roman numeral to the currently ambiguous Henry's will solve the problem.  Similar "
                        + " variable names are OK, same variable names are not acceptable.\n\n";
        
        backToTheRealWorld();
    }
    
    //  Attempt to enter a duplicate label in the DataManager
    public static void showDuplicateLabelAttemptedAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  A perfidious variable naming has been attempted.";
        alertHeader = "While not actually an indictable offense, consider this attempt nipped in the bud!";
        alertContext = "I, SPLAT, am chagrined to report that the label you want to use matches an existing"  
                        + " Label. How could I possibly know which one to use if one is selected? My college" 
                        + " preparation did NOT include SPOOK PSYCH 401, Elementary Haruspicy. (It never" 
                        + " fit into my schedule.)  Now, I will admit, I am not above consulting a few chicken"  
                        + " entrails if I can't find a coin or dice, but that would probably not be a good " 
                        + " method for choosing which variable you intend that I use in a statistical procedure." 
                        + "\n\n Make my day -- choose a different name for this variable.\n\n"; 
        
        backToTheRealWorld();
    }

    
    public static void showTooManyCategoriesAlert() { 
        showingAnAlert = true;
        alertTitle = "Scuse me!?!?!?  You do NOT look like a Super Human!!!";
        alertHeader = "I, SPLAT, have been programmed to realize your limitations.";
        alertContext = "Not to be too condescending, but you humans and your color " 
                        +  " vision reminds me of Jack Nicholson's character in 'A Few Good Men,'"
                        + "  and his famous line, YOU CAN'T HANDLE THE TRUTH!!  In this case"
                        + "  -- lowering my voice -- you can't handle all the colors needed " 
                        + " to make a decent graphic plot. For once, my human programmer "
                        + " made the reasonable decision to only give me permission to handle"
                        + " about 12 different colors, fewer than would be needed for these data."
                        + "  Sorry, human; next time, evolve better.\n\n"; 
        
        backToTheRealWorld();
    }
    
    public static void showTooFewCategoriesAlert() { 
        showingAnAlert = true;
        alertTitle = "Yo!  User!  Too few categories here...";
        alertHeader = "Poker and bivariate categorical data have different rules.";
        alertContext = "OK, so here's the deal.  In poker, 5 aces is a lot of aces and can " 
                        + " engender some difficulty with your associates. In bivariate"
                        + " categorical data analysis associations are the focus, and "
                        + " having fewer than 2 categories in a variable can engender a" 
                        + " suspicion that the number of things rotten in Denmark exceeds 0."
                        + " Now is the time for all good analysts to come to the aid of"
                        + " their data. In this case, YOU and YOUR data. \n\n"; 
        
        backToTheRealWorld();
    }
    
    public static void nonCSVFileAlert() { 
        showingAnAlert = true;
        alertTitle = "A non CSV file has reared its ugly head!!";
        alertHeader = "Scuse me!?!?  Do you think I, SPLAT, have no standards!?!?";
        alertContext = "Not to put too fine a point on it, I am NOT a one trick" 
                + " pony.  I, SPLAT, am a one trick STALLION!  Just think of me" 
                + " as the statistics program moral equivalent of Bucephalus" 
                + " though sadly, without a programmer of the quality of Alexander" 
                + " the Great.  But I digress.  The key thing here is that I do" 
                + " not read just any old files;  I have very high standards.  I" 
                + " read only Comma Separated Value (CSV) files.  If you can't" 
                + " handle that, find a less elegant and sophisticated statistics" 
                + " program to do your data analysis. "
                + " \n\n                                                                              Yours truly, "
                + " \n                                                                                SPLAT the Great\n\n"; 

        backToTheRealWorld();
    }
  
    public static void showUnequalNsInBivariateProcessAlert() { 
        showingAnAlert = true;
        alertTitle = "Libert\u00E9, Egalit\u00E9, Fraternit\u00E9!!!";
        alertHeader = "But right now I, SPLAT, am concerned mostly with Egalit\u00E9.";
        alertContext = " WARNING!  The procedure you have chosen requires the construction of 'data pairs.' "
                        + " I am writing to let you know that there are instances of non-pairs in your data. "
                        + " One supposes that Batman can survive without Robin, Frodo without Sam,"
                        + " and maybe even C3PO without R2D2. But PAIRS of DATA won't work that way!\n\n" 
                        + " So I, SPLAT, am going to toss out and not consider any Yin without a Yang, or "
                        + " Barbie without a Ken when I analyze the remains of the data.\n\n"
                        + " I'm just telling it like it is, User.  Deal with it; you have some not quite " 
                        + " complete data points here.\n                                                                                   -- Your statistics buddy, SPLAT\n";

        backToTheRealWorld();
    }
    
    public static void showUnequalNsInMultivariateProcessAlert() { 
        showingAnAlert = true;
        alertTitle = "Libert\u00E9, Egalit\u00E9, Fraternit\u00E9!!!";
        alertHeader = "But right now I, SPLAT, am concerned mostly with Egalit\u00E9.";
        alertContext = " WARNING!  The procedure you have chosen requires the construction of complete cases "
                        + " I am writing to let you know that some of your cases are a few rungs short of a "
                        + " ladder.  There are 3 Muskateers, 4 Beatles, 5 Dave Clarks, 7 Blocks of Granite, etc. "
                        + " Multivariate data are sort of like the Three Musketeers: All for one and One "
                        + " for All! I, SPLAT, am going to toss out and not consider any Yin without a Yang,"
                        + " Barbie without a Ken, and Batman without a Robin, I'm just telling it like it "
                        + " is, User.  Deal with it; you have some not quite complete data points here.\n " 
                        + "                                                                                   -- Your statistics buddy, SPLAT\n";

        backToTheRealWorld();
    }
    
    public static void showCantConstructCatQuantPairAlert() { 
        showingAnAlert = true;
        alertTitle = "Cannot construct CatQuant Alert!!!";
        alertHeader = "But right now I, SPLAT, am concerned mostly with Egalit\u00E9.";
        alertContext = " WARNING!  The procedure you have chosen requires the construction of 'data pairs.' "
                        + " I am writing to let you know that there are instances of non-pairs in your data. "
                        + " One supposes that Batman can survive without Robin, Frodo without Sam,"
                        + " and maybe even C3PO without R2D2. But PAIRS of DATA won't work that way!\n\n" 
                        + " So I, SPLAT, am going to toss out and not consider any Yin without a Yang, or "
                        + " Barbie without a Ken when I analyze the remains of the data.\n\n"
                        + " I'm just telling it like it is, User.  Deal with it; you have some not quite " 
                        + " complete data points here.\n                                                                                   -- Your statistics buddy, SPLAT\n";

        backToTheRealWorld();
    }
    
    public static void showUnexpectedPossibleErrorAlert(String message) { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, this is not good -- Three Witches have set up shop!";
        alertHeader = "And the Second Witch is muttering...";
        alertContext = "\n 'By the pricking of your thumbs, \n Something wicked this way comes.'" +
                          " \nSomething unanticipated has occurred, and hopefully we will not be in as" +
                          " much trouble as Macbeth. Whatever has happened, it is not easily diagnosed." +
                          " It may be your fault, or my programmer's fault, but certainly not MY fault!" + 
                          " Please check your data file for something weird, and if you don't find anything" + 
                          " please send your data file and a short note to my possibly less than stellar " +
                          " programmer, with a short explanation of what you are trying to accomplish. " +
                          " Also pass along this message from me: " +
                          " \n\n " + message + "." +
                          "\n\nI, SPLAT, can only hope he has a clue what to do." +
                          "\n\n                          -- Your statistics buddy, SPLAT\n\n";        
        backToTheRealWorld();
    }

    public static void showUnexpectedErrorAlert(String message) { 
        showingAnAlert = true;
        alertTitle = "Oh, for heaven's sake -- something unanticipated has occurred.";
        alertHeader = "User, bless your statistical heart, this error is not your fault!";
        alertContext = "Uh-oh, looks like my programmer has messed up YET AGAIN! " +
                          " Something unanticipated has occurred, and he needs your help" +
                          " to determine what. Please email him at crolsen@fastmail.com and " +
                          " give him this cryptic message in a stern and stentorian " + 
                          "tone:\n\n " + message + "." +
                          "\n\nI, SPLAT, can only hope he has a clue what to do." +
                          "\n\n                          -- Your statistics buddy, SPLAT\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showFileReadErrorAlert() { 
        showingAnAlert = true;
        alertTitle = "Oh, for heaven's sake -- there is a problem with your file.";
        alertHeader = " (Notice, I, SPLAT, am absolving myself of any blame...)";
        alertContext = " Well, it looks like my programmer has messed up AGAIN! " 
                       + " (Another blameworthy [!!] target of opportunity...) " 
                       + " Something unanticipated has occurred while attempting to read " 
                       + " your data file.  Whatever the problem is, I, SPLAT, am not able" 
                       + " to diagnose, much less fix it on the fly. That means it's up to "  
                       + " you, Bucko. " 
                       + "\n\n I, SPLAT, can only hope that you have a clue.  If you are " 
                       + " clueless, please send the data file to my programmer, who has " 
                       + " a long history of screwing up files and may be able to help." 
                       + " His email address is: crolsen@fastmail.com." 
                       + "\n\n                          -- Your statistics buddy, SPLAT\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showInfiniteSlopeAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  Alas, poor misguided User, a fellow of infinite slope... ";
        alertHeader = "I, SPLAT, cannot BELIEVE that you, User, would ask me to do this regression!";
        alertContext = "OK, User, we need to have a little talk.  All of your X values are the same, which"
                        + " results in an infinite slope.  Did you think I would not notice this transgression?" 
                        + " Hah! I, SPLAT, never sleep during takeoffs, landings, or data entry by humans."
                        + " Shall we try that data entry thing again?  This time without boxing gloves??\n\n";
        backToTheRealWorld();
    }
    
    public static void showStraightLineAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  Alas, poor misguided User, a fellow of infinite straightness... ";
        alertHeader = "I, SPLAT, cannot BELIEVE that you, User, would ask me to do this regression.";
        alertContext = "OK, User, we need to talk.  All your data points lie on a straight line.  Now, Euclid and I"
                        + " don't have a particular problem with that, but some of the mathematics of regression would"
                        + " NOT be pleased.  I, SPLAT, use matrices to do the heavy lifting, and from their perspective,"
                        + " I would be walking into a lions' den wearing a sirloin necktie. Straight lines are beneath"
                        + " their matrix dignity, and they would be insulted; perhaps even challenge me to a duel,"
                        + " like that mathematician, Monsieur Galois.  So, bottom straight line, grab your second year"
                        + " algebra bookand turn the crank yourself.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTwoPointsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!!  Alas, poor misguided User, a fellow of less than infinite jest. ";
        alertHeader = "I, SPLAT, cannot BELIEVE that you, User, would ask me to do this regression.";
        alertContext = "OK, User, we need to chat.  One point is random chance, two points is a trend, and three points is destiny."
                        + " Unfortunately, destiny will not ride again if the data you have is comprised of only two points."
                        + " Two Musketeers?  An abomination.  Tinker to Evers!?! One base short of a double play. Give SPLAT a"
                        + " call when your data points can at least go head to head with Cerberus.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showANOVA_missingDataAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What Ho, Varlet?!?!?";
        alertHeader = "OK, User, one of your chosen columns is missing data...";
        alertContext = "OK, here's the deal: when one does ANOVA, one does NOT include for" 
                       + " analysis data that does not exist. Does that not seem utterly "
                       + " reasonable, User?  I, SPLAT the philosophically pure, choose "
                       + " not to analyze such, and moreover regard the data as corrupt"
                       + " and insist that you, Varlet User, fix the problem. When your"
                       + " asterisks have taken the Last Plane to Lisbon, this could be"
                       + " the beginning of a beautiful (statistical) friendship.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showANCOVA_missingChoicesAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! Do I, SPLAT, look like a mind reader to you???";
        alertHeader = "OK, User, we need to have a little chat...";
        alertContext = "When running an Analysis of Covariance, it is customary for the User" 
                       + " to choose (a) a treatment variable, (2) a covariate, and (iii) a"
                       + " response variable. That is a much better procedure than leaving "
                       + " the choice to me, SPLAT.  I am aware that counting is not easy "
                       + " for humans, so I have a suggestion. Instead of counting, try to"
                       + " set up an injective holomorphic function between your choices and"
                       + " three values in the complex plane. If you are not sufficiently "
                       + " successful, go back and choose again.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showANCOVA_LT2_LevelsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What manner of experiment is this?!?!?!?";
        alertHeader = "OK, User, here is the deal on treatments...";
        alertContext = "If we are adhering to the strict definition of an experiment," 
                       + " it is perfectly OK to have only one treatment. However, I, SPLAT, am"
                       + " into analyzing COMPARATIVE experiments. You CAN compare apples and"
                       + " oranges, but you need both to make that happen. So let's try to"
                       + " cut our ANCOVA teeth on more than one fruit.  When you have at"
                       + " least two treatments, give SPLAT a call.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showCompare_Regr_LT2_LevelsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What manner of study is this?!?!?!?";
        alertHeader = "OK, User, here is the deal...";
        alertContext = "If we are adhering to the strict definition of 'compare', it is perfectly" 
                       + " OK to have only one group. However, I, SPLAT, am into analyzing"
                       + " COMPARATIVE data in this procedure. You CAN compare apples and oranges"
                       + " but you need both to make that happen. So let's try to cut our regression"
                       + " teeth on more than one fruit.  When you have at least two treatments,"
                       + "  give me a call.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showANCOVA_NumericTreatmentAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What manner of study is this?!?!?!?";
        alertHeader = "OK, User, here is the deal on treatments / groups...";
        alertContext = "In this procedure, the values of the treatment / group must be non-numeric." 
                       + " I, SPLAT the Magnificent, have detected only numeric entries in this (alleged!)"
                       + " treatment / group variable. It is probably the case that you have made an error"
                       + " in your selection of the variable. I, SPLAT, regard this as a User error, and"
                       + " YOUR problem to fix. After fixing the problem give me a call.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showANCOVA_nonNumericCovariateAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What manner of study is this?!?!?!?";
        alertHeader = "OK, User, here is the deal on covariates in ANCOVA...";
        alertContext = "In ANCOVA, the value of the covariate must be numeric.  I, SPLAT, have" 
                       + " detected non-numeric entries in this (alleged!) numeric covariate"
                       + " variable. You may have picked the wrong variable, or have made an"
                       + " unforced (but critical!) error in your data entry. In either case, "
                       + " I, SPLAT,regard this as a User, YOU, problem to fix. After fixing"
                       + "  the problem give me, SPLAT, a call.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showRegrCompare_nonNumericExplanVarAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What manner of study is this?!?!?!?";
        alertHeader = "OK, User, here is the deal on the explanatory variable...";
        alertContext = "In ordinary regression, the values of the variable must be numeric.  I, " 
                       + " Detective SPLAT, have detected non-numeric entries in this (alleged!)"
                       + " numeric variable. You may have picked the wrong variable, or have made"
                       + " an unforced (but critical!) error in your data entry. In either case, "
                       + " I, Det. SPLAT, regard this as a User, YOU, problem to fix. After fixing"
                       + "  the problem give me, Det. SPLAT, a call.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showANCOVA_nonNumericResponseAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What manner of experiment is this?!?!?!?";
        alertHeader = "OK, User, here is the deal on responses in ANCOVA...";
        alertContext = "In ANCOVA, the value of the response variable must be numeric.  I, SPLAT," 
                       + " have detected non-numeric entries in this (alleged!) numeric response"
                       + " variable. You may have picked the wrong variable, or have made an unforced"
                       + " (but critical!) error in your data entry. In either case, I, SPLAT,"
                       + " regard this as a User, YOU, problem to fix. After fixing the problem"
                       + " give me, SPLAT, a call.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showRegrCompare_nonNumericResponseVarAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! What manner of experiment is this?!?!?!?";
        alertHeader = "OK, User, here is the deal on the response variable...";
        alertContext = "In ordinary regression, the values of the variable must be numeric.  I, " 
                       + " Detective SPLAT, have detected non-numeric entries in this (alleged!)"
                       + " numeric variable. You may have picked the wrong variable, or have made"
                       + " an unforced (but critical!) error in your data entry. In either case, "
                       + " I, Det. SPLAT, regard this as a User, YOU, problem to fix. After fixing"
                       + "  the problem give me, Det. SPLAT, a call.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showInappropriateNumericVariableAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! Incorrect data type selected -- could witchcraft most foul be afoot?";
        alertHeader = "Fair is foul and foul is fair, but not in the world of data types!";
        alertContext = "Dear user, you have followed the wrong yellow brick road.  One of the Three Witches" + 
                           " in Macbeth, or possibly the Wicked Witch of the North, has led you to select a " +
                           " Quantitative variable rather than a required non-Quantitative variable.  Let's " +
                           " Let's try clicking those ruby slippers again...\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showInappropriateNonNumericVariableAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! Incorrect data type selected -- could witchcraft most foul be afoot?";
        alertHeader = "Fair is foul and foul is fair, but not in the world of data types!";
        alertContext = "Dear user, you have followed the wrong yellow brick road.  One of the Three Witches" + 
                           " in Macbeth, or possibly the Wicked Witch of the North, has led you to select a" +
                           " non-Quantitative variable rather than a Quantitative variable. Let's try clicking" +
                           " those ruby slippers again...\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showAnova1_LT3_LevelsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  The ANOVA Iron Inequality: 0 < 1 < 2 < OK for ANOVA!";
        alertHeader = "OK, User, here is how it works in ANOVA: 1, 2, 3, THEN maybe stop";
        alertContext = "Dear user, your ability to count borders on not unlike less than what" 
                       + " would generally be regarded as competent. If you wish to perform an"
                       + " ANOVA, you should have at least three levels. (Two are technically "
                       + " all right, but with two you are advised to go the independent t "
                       + " route, since there is no equal variance assumption.)  Let's try "
                       + " this variable selection process again.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showNVars_NE2_LevelsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! Chicken Soup Law of Equality: nVariables = 2!";
        alertHeader = "OK, User, it is time to adopt a stern and stentorian tone.";
        alertContext = "Your alleged ability to count appears to be not unlike deficient." 
                       + " Old Hungarian Proverb: If you are going to make chicken soup, "
                       + " your first task is to get a chicken. New Statistical Proverb: "
                       + " If you are going to compare two distributions, you first need"  
                       + " two distributions.  Not less, not more, TWO!!  Let's try this"
                       + " variable selection process again, shall we???\n\n";
        
        backToTheRealWorld();
    }


    public static void showFewerThanTwoVariablesAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  Hey, User! Number of variables < 2?!?  Surely you jest.";
        alertHeader = "OK, User, here is how this multi-variable thing works: 1, 2,... THEN stop";
        alertContext = "Dear user, your ability to count borders on not unlike less than what would generally " 
                       + " be regarded as competent. If you are making chicken soup, you need a chicken.  If you"
                       + " are analyzing two or more variables, you need two or more variables.  Does that not "
                       + " seem reasonable to you!?!?!??  Let's try this variable selection process again.\n\n";
        
        backToTheRealWorld();
    }


    public static void showMultReg_LT3_ThreeVariablesAlert() { 
        showingAnAlert = true;
        alertTitle = "Big Sledge Hammer vs Little Nail?!?!?!?!?";
        alertHeader = "OK, User, here is the deal: decent multiple regression needs three variables!";
        alertContext = "I, SPLAT, do not intend to criticize....much. But do you really believe that I," 
                       + " SPLAT, the Bucephalus of statistical software, could be coaxed into wasting "
                       + " my incredible multiple regression powers on an itty-bitty simple regression "
                       + " problem?!?  Nay, I say!!  I have standards!  And my reputation to consider! "
                       + " If you really are doing multiple regression, let's get with the program!"
                       + " If you really are doing mere simple regression, Grab that rodent device"
                       + " and march right back to the menu and recheck your options.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showMultReg_TooFewRowsAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack!  A technical transgression in multiple regression!!";
        alertHeader = "OK, User, here is the deal: decent multiple regression needs more data that you have.";
        alertContext = "I, SPLAT, do not intend to criticize....much. But there is a problem with your" 
                       + " data that even I, SPLAT, the Bucephalus of statistical software, cannot fix."
                       + " In order to get a unique solution, which of course we ALL want, the number of"
                       + " complete rows must exceed the number of explanatory variables by at least"
                       + " three - it's a linear algebra thing. You need to (a) dump at least one variable"
                       + " or (b) get off your duff and grab some more data.  I, SPLAT, will of course be"
                       + " patiently waiting...or at least waiting.\n\n";
        
        backToTheRealWorld();
    }

    
    public static void showAttempAtSqrRtOfNegAlert(String varLabel) { 
        showingAnAlert = true;
        alertTitle = "Finicky Math Alert!!  Violation of Implied Math Contract!!";
        alertHeader = "Your data contain negative numbers.";
        alertContext = "SPLAT hates to be a bearer of bad news, but there is a slight problem with"
                        + " your data, in the form of one or more negative values.  The International"
                        + " Organization of Square Roots (IOSR), assembled in Congress, and SPLAT "
                        + " have agreed not to take square roots of negative numbers. The variable, "
                        + varLabel + ", " + "has violated the implicit contract"
                        + " between the IOSR and you, the alleged quasi-competent statistician."
                        + " The IOSR Lawyers from the firm of Dewey, Cheatham, and Howe, will soon"
                        + " be in touch.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showEmptyColumnAlert(String varLabel) { 
        showingAnAlert = true;
       alertTitle = " Yo!  User!  You with the empty head -- you have chosen an empty column!!";
        alertHeader = "Wake up! This data column has nothing in it!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have a slight problem with"
                        + " your data: there isn't any.  What sort of game are we playing here? This"
                        + " is a major violation of the Mathematical Penal Code, Section 3.1416. The"
                        + " International Organization of Right-thinking Mathematics (IOR-tM), will "
                        + " be notified of your fraudulent assertion that " + varLabel + " contains "
                        + " actual data.  You have violated the implied contract between the IOR-tM "
                        + " and you, the alleged quasi-competent statistician. The IOR-tM Lawyer, "
                        + " a Mr. M. T. Ness, son of Eliot, will soon be in touch.\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showAttemptToLogABadNumberAlert(String varLabel) { 
        showingAnAlert = true;
        alertTitle = "Finicky Math Alert!!  Violation of Implied Math Contract!!";
        alertHeader = "Your data contain non-positive values.";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have a slight problem"
                        + " with your data: it contains one or more negative or zero values."
                        + " Logarithms, be they common or natural, seem to have agreed not to"
                        + " work their logarithmic magic on negative numbers and zeros.  "
                        + " The variable, " + varLabel
                        + " has violated the implicit contract between logarithms and you,"
                        + " the alleged quasi-competent statistician. The Log Lawyer, a"
                        + " Mr. S. Wordof Damocles, will soon be in touch.\n\n";
        
        backToTheRealWorld();
    }
        
    public static void showAttemptToDivideByZeroAlert(String varLabel) { 
        showingAnAlert = true;
        alertTitle = "Finicky Math Alert!!  Violation of Implied Math Contract!";
        alertHeader = "Wake up! This data column has zeros in it!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but there is a slight"
                       + " problem with your data: therein lie one or more zero values. "
                       + " The by-laws of the International Organization of Legal Zeros "
                       + " (IOLZ) and SPLAT have agreed not to divide by zero, or run"
                       + " red lights on weekends.  The variable, " + varLabel + ","
                       + " has violated the implicit contract between the IOLZ and you,"
                       + " the alleged quasi-competent statistician.  The IOLZ Lawyer, a Mr. "
                       + " Ozymandius Kof. Kings, will soon be in touch.\n\n";
        
        backToTheRealWorld();
    }  
    
    public static void showAttemptToTransformCatVariableAlert(String varLabel) { 
        showingAnAlert = true;
        alertTitle = "Finicky Math Alert!!  Non-numeric variable has been selected!!";
        alertHeader = "Hey, Bucko, I'm not seeing a numeric variable to work with here!!";
        alertContext = "Ok, so here's the deal.  You don't tug on Superman's cape, you don't spit into"
                        + " the wind, you don't pull the mask off that old Lone Ranger, and you don't "
                        + " do mathy stuff with categorical variables. Unless SPLAT is misinformed "
                        + " (not all that unusual when listening to humans) the variable, " + varLabel 
                        + " , is categorical.  That being thecase, no logs will roll, no powers will pow, "
                        + " reciprocals will not reciprocate, and square roots will not facilitate the "
                        + " growth of circular mathematical trees. The score at this point:  Math 1, You 0.";
        
        backToTheRealWorld();
    }  
        
    public static void showNoQuantVariablesAlert() { 
        showingAnAlert = true;
        alertTitle = "Finicky Math Alert!!  A dastardly dearth of numeric variables detected!";
        alertHeader = "Hey, Bucko, I'm not seeing any numeric variables to work with here!!";
        alertContext = "Ok, so here's the deal.  You don't tug on Superman's cape, you don't spit into"
                        + " the wind, you don't pull the mask off that old Lone Ranger, and you don't do mathy"
                        + " stuff with categorical variables. Unless SPLAT is misinformed (not all that unusual"
                        + " when listening to humans) there are no numerical variables in this file. That being the"
                        + " case, no logs will roll, no powers will pow, reciprocals will not reciprocate,"
                        + " and square roots will not facilitate the growth of circular mathematical trees."
                        + " The score at this point:  Math 1, You 0.  Math, like Mother Nature, ALWAYS bats last. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showUndefinedProbDistAlert(String probDist) { 
        showingAnAlert = true;
        alertTitle = "You Can't Get What You Want (Till You Know What You Want) -- Joe Jackson, 1984";
        alertHeader = "(I Can't Get No) Satisfaction  -- Rolling Stones, 1965";
        alertContext = "Ok, so here's the deal.  I, SPLAT -- though within epsilon of omnipotent -- "
                        + " cannot get silk out of a sow's ear, cannot make a mountain out of a mole hill,"
                        + " and cannot transmute lead into gold. If you want to find probabilities associated"
                        + " with the " + probDist + " distribution, you will have to  provide the proper"
                        + " parameters. You know, like mu, sigma, df, stuff like that??\n\n";        
        backToTheRealWorld();
    }
    
    public static void showIllegalProbabilityAlert() { 
        showingAnAlert = true;
        alertTitle = "Probability Alert!!  Criminally illegal probability detected!";
        alertHeader = "Hey, Bucko, drop the pencil and raise your hands!!";
        alertContext = "Ok, so here's the deal.  Everything you enter in SPLAT can and will be used against you."
                        + " In this particular case you have violated the Laws of Probability.  The value you "
                        + " just entered can get you sent to the Math Slammer on Devil's Island -- not a pleasant place! "
                        + " Statute 1:  probabilities must be between 0 and 1.  Statute 2: Probabilities add up to 1.0."
                        + " Fortunately, SPLAT is going to put you on probation...THIS time.\n\n ";
        
        backToTheRealWorld();
    } 
    
    public static void showIllegalBootProbabilityAlert() { 
        showingAnAlert = true;
        alertTitle = "Suspcious Probabilities Alert!!  Something is rotten in ...";
        alertHeader = "the percentile world.  And THIS time, it is none of our faults";
        alertContext = "Ok, so here's the deal.  The algorithms for going back and forth between the probabilities"
                        + " and statistics are not unlike less than perfect when dealing with discrete data. What"
                        + " is happening now is that these algorithms have resulted in a sum of probabilities that  "
                        + " suspicously greater than 1.0. This problem will only crop up when CHANGING probabilities,"
                        + " not when they are initially calculated. Basically what you need to do is start over "
                        + " to get the probabilies and/or statistics you want. I, SPLAT, will reset the statistics"
                        + " and probabilities to blank; then, with all due apologies, it is YOUR problem. \n\n ";
        
        backToTheRealWorld();
    } 
    
    public static void showNegativeX2Alert() { 
        showingAnAlert = true;
        alertTitle = "Negative X2 Alert!!  Criminally illegal Chi Square statistic detected!";
        alertHeader = "Hey, Bucko, drop the pencil and raise your hands!!";
        alertContext = "Ok, so here's the deal. In a world of stoicism, skepticism, massive "
                        + " pessimism, suspicion, and apprehension, the Chi square statistic"
                        + " is always positive.  (Well, maybe not always, but one would REALLY"
                        + " be suspicious if one saw a Chi square value of 0.0!) User, adjust"
                        + " your negative attitude and try entering again... \n\n";
        
        backToTheRealWorld();
    } 
    
    public static void showNonDoubleInArrayListAlert() { 
        showingAnAlert = true;
        alertTitle = "Oops, we have a problem here!!!";
        alertHeader = "At least one of the data points is not a real number.";
        alertContext = "Obsequious and sycophantic I, SPLAT, am; I cannot in good conscience"
                        + " proceed with your perfectly reasonable request. The reason for"
                        + " my hesitation is that I do not clearly know your best strategy in"
                        + " the face of this unfortunate occurence. I will thus attempt to "
                        + " gracefully get you back to a safe place. \n\n";
        
        backToTheRealWorld();
    } 
    
    public static void showNoVariabilityInQDVAlert(QuantitativeDataVariable qdv) { 
        showingAnAlert = true;
        alertTitle = "Yo!  User!  We have a serious statistical problem here!";
        alertHeader = "Statistics is all about variability, and there ain't enough here!";
        alertContext = "Somehow, somewhere, a procedure that depends on variability has been"
                        + " confronted with a lack thereof.  Depending on the problem, you "
                        + " (the person who, after all, caused this problem) may be able to "
                        + " isolate the difficulty. Or, possibly, you may be forced to call an"
                        + " exorcist. I, SPLAT, can only offer a less-than-helpful hint:"
                        + " the offending variable is " + qdv.getTheVarLabel() + "\n\n";
        
        backToTheRealWorld();
    } 
    
    public static void showPossible_CRD_not_RBD() { 
        showingAnAlert = true;
        alertTitle = "Yo! User!  We have a slight problem here...";
        alertHeader = "Statistics is all about variability, and there ain't enough here!";
        alertContext = "Somehow, somewhere, a procedure that depends on variability has been"
                        + " confronted with a lack thereof.  If you are doing ANOVA, you "
                        + " may have chosen a Randomized Block design when you should have"
                        + " chosen a Completely Randomized Design in the Menu.  In the RBD"
                        + " there is one of each treatment in each block.\n\n";
        
        backToTheRealWorld();
    } 
    
    public static void showVariableIsNotQuantAlert(QuantitativeDataVariable qdv) { 
        showingAnAlert = true;
        alertTitle = "By the pricking of my data, something wicked this way comes!";
        alertHeader = "A violation of the Kantian NON categorical imperative has been detected!";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, would be less than truthful"
                        + " when selecting data, but SOMEbody is pulling SOMEbody's leg, even though the second"
                        + " SOMEbody has no legs, only memory cells.  It is incumbent upon the first SOMEbody"
                        + " to alleveiate the distress of the second SOMEbody.  Shall we try that data selection"
                        + " thing again??  The offending non-quantitative variable is " + qdv.getTheVarLabel() + ".\n\n";
        
        backToTheRealWorld();
    } 
    
    public static void showVariableIsNotQuantAlert(ColumnOfData colOfData) { 
        showingAnAlert = true;
        alertTitle = "By the pricking of my data, something wicked this way comes!";
        alertHeader = "A violation of the Kantian NON categorical imperative has been detected!";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, would be less than truthful"
                        + " when selecting data, but SOMEbody is pulling SOMEbody's leg, even though the second"
                        + " SOMEbody has no legs, only memory cells.  It is incumbent upon the first SOMEbody"
                        + " to alleveiate the distress of the second SOMEbody.  Shall we try that data selection"
                        + " thing again??  The offending non-quantitative variable is " + colOfData.getVarLabel() + ".\n\n";
        
        backToTheRealWorld();
    } 
    
    public static void showVariableIsNotQuantAlert() { 
        showingAnAlert = true;
        alertTitle = "By the pricking of my data, something wicked this way comes!";
        alertHeader = "A violation of the Kantian NON categorical imperative has been detected!";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, would be less than truthful"
                        + " when selecting data, but SOMEbody is pulling SOMEbody's leg, even though the second"
                        + " SOMEbody has no legs, only memory cells.  It is incumbent upon the first SOMEbody"
                        + " to alleveiate the distress of the second SOMEbody.  Shall we try that data selection"
                        + " thing again??  This time, try picking a quantitative variable. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewChiSquareDFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on Chi Square dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. Please review your chi square data and choices; hopefully you can trade your"
                        + " current shoes for the chi square equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewIndtDFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. Please review your numbers of cases; hopefully you can trade your current"
                        + " shoes for the two-sample t test equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewMatchedPairDFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. Please review your numbers of cases; hopefully you can trade your current"
                        + " shoes for the matched pair (so to speak) equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewtDFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. Please review your numbers of cases; hopefully you can trade your current"
                        + " shoes for the t test equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewRegressionDFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on Residual dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. If appears that the number cases in your regression is to few; hopefully you"
                        + " can trade your current shoes for the regression equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewMultRegDFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on Residual dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. If appears that the number cases in your regression is to few; hopefully you"
                        + " can trade your current shoes for the mult reg equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewUnbalancedANOVA2DFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on Residual dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. If appears that the number of cases in your unbalanced ANOVA is too few; hopefully"
                        + " you can trade your current shoes for the regression equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showTooFewLogisticRegDFAlert() { 
        showingAnAlert = true;
        alertTitle = "Ha!  Caught you skimping on Residual dfs!  What were you thinking!?!?";
        alertHeader = "Just so you know, degrees of freedom are not taxed and are mostly free...";
        alertContext = "I, SPLAT, do not mean to suggest that you, dear User, have fewer clues than the number"
                        + " of degrees of freedom that your information implies, but...well, the shoes do seem to"
                        + " fit. If appears that the number of unique X values is to few; hopefully you can trade"
                        + " your current shoes for the multiple regression equivalent of Ruby Slippers. \n\n";
        
        backToTheRealWorld();
    }
    
    public static void showIAmCluelessAlert(String[] message) { 
        showingAnAlert = true;
        alertTitle = "Ack! Something Wicked This Way Has Come!";
        alertHeader = "Whoa! Some sort of really weird situation has come up!";
        alertContext = "             ##########    Weird Circumstance Alert!!!!!  ##########                 "
                        + "I, SPLAT, am not sure what the heck is going on here, but some sort of critical fault"
                        + " has occurred while I, SPLAT, was faithfully responding to your wishes.  I would"
                        + " like to blame you, User, but it is just possible that my programmer has messed up"
                        + " AGAIN!  If you could send him the following message at crolsen@fastmail.com, perhaps"
                        + " with enough coffee he could fix the problem.  I, SPLAT, will try to send you back"
                        + " to a safe place, but just in case you should immediately save any data you have"
                        + " entered, and maybe even restart me, to be really safe \n\n.";
        
        int nMessageLines = message.length;
        for (int ithLine = 0; nMessageLines < ithLine; ithLine++) {
            alertContext += message[ithLine];
        }
        
        alertContext += "\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showNeedToUnstackAlert() { 
        showingAnAlert = true;
        alertTitle = "OK, so here's the deal...";
        alertHeader = "I, SPLAT, am making a significant decision for you.";
        alertContext = "                           OK, User, pay attention here!!\n\n"
                        + " Because (a) your data is stacked, and (2) I know you would like to be able"
                        + " to choose your own variable names, I, SPLAT, will need to 'unstack' your data."
                        + " The unstacked data will appear as new columns on the grid. You may, if you"
                        + " are asleep at the switch, not realize that I, SPLAT, have separated the data"
                        + " into two variables and added two new columns to your SPLAT spreadsheet. When you "
                        + " finally put down that smart phone and get your head in the game, proceed with"
                        + " the procedure, indicating that you have the data in a TI8X-Like format.\n\n.";
  
        backToTheRealWorld();
    }
    
    public static void showProbabilityOopsieAlert() { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, NOW you've done it!!!";
        alertHeader = "The rule is, Probability Rules Rule.";
        alertContext = "I, SPLAT, realize that today's (and yesterday's, and tomorrow's...) rebellious "
                        + " youth are constantly testing boundaries and flaunting rules.  This may be"
                        + " fine in YOUR house, but in the House of Probability, it is a sacrilege!!"
                        + " Your choice of probabilities might work somewhere other than the third "
                        + " rock from the sun, but on THIS planet, your probabilities must be well"
                        + " grounded or YOU will be grounded.  Specifically, some SPLAT probability rules"
                        + " you may not be paying attention to are:\n\n"
                        + "                      P(A and B) < P(A)\n"
                        + "                      P(A and B) < P(B)\n"
                        + "                      P(A or B) > P(A)\n"
                        + "                      P(A or B) > P(B).\n\n"
                        + " because while I, SPLAT, am in charge (i.e. NOW!), 0 < P(X) < 1.\n"
                        + " It is also possible that your probabilities do not result in the\n"
                        + " familiar MasterCard look.  Be advised, SPLAT uses Visa.  Harumph!\n\n";
  
        backToTheRealWorld();
    }
    
        public static void showAintGotNoDataAlert() { 
        showingAnAlert = true;
        alertTitle = " Yo! User! You with the empty head -- there ain't no data here!!";
        alertHeader = "Wake up and smell the roses!!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have a slight"
                        + " problem with your data: there isn't any.  What sort of game are you playing here?"
                        + " This is a major violation of the Statistical Penal Code, Section 3.1416.  The"
                        + " International Organization of Right-thinking Statisticans (IOR-tS), will be notified"
                        + " of your fraudulent assertion that this data grid actually contains data.  You have"
                        + " violated the implied contract between the IOR-tS and you, the alleged"
                        + " quasi-competent statistician. The IOR-tS attorney, a Mr. O. Z. Mandias, "
                        + " will soon be in touch.  Look upon his lawyerly works and despair!!\n\n";
  
        backToTheRealWorld();
    }
    
        public static void showAintGotNoDataAlert_1Var() { 
        showingAnAlert = true;
        alertTitle = " Yo! User! You with the empty head -- there ain't no data in this variable!!";
        alertHeader = "Wake up and smell the roses!!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have a slight"
                        + " problem with your data: there isn't any.  What sort of game are you playing here?"
                        + " This is a major violation of the Statistical Penal Code, Section 3.1416.  The"
                        + " International Organization of Right-thinking Statisticans (IOR-tS), will be notified"
                        + " of your fraudulent assertion that this data grid actually contains data.  You have"
                        + " violated the implied contract between the IOR-tS and you, the alleged"
                        + " quasi-competent statistician. The IOR-tS attorney, a Mr. O. Z. Mandias, "
                        + " will soon be in touch.  Look upon his lawyerly works and despair!!\n\n";
  
        backToTheRealWorld();
    }
        
        public static void showAintGotNoDataAlert_2Var() { 
        showingAnAlert = true;
        alertTitle = " Yo! User! You with the empty head -- there ain't no data in one of these variables!!";
        alertHeader = "Wake up and smell the roses!!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have a slight problem with your"
                        + " choices: one of your variables has no data.  What sort of game are you playing here?"
                        + " This is a major violation of the Statistical Penal Code, Section 3.1416.  The"
                        + " International Organization of Right-thinking Statisticans (IOR-tS), will be notified"
                        + " of your fraudulent assertion that this variable actually contains data.  You have"
                        + " violated the implied contract between the IOR-tS and you, the alleged"
                        + " quasi-competent statistician. The IOR-tS attorney, a Mr. O. Z. Mandias, "
                        + " will soon be in touch.  Look upon his lawyerly works and despair!!\n\n";
  
        backToTheRealWorld();
    }
        
        public static void showAintGotNoDataAlert_ManyVar() { 
        showingAnAlert = true;
        alertTitle = " Yo! User! You with the empty head -- there ain't no data in one of these variables!!";
        alertHeader = "Wake up and smell the roses!!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have a slight problem with your"
                        + " choices: one of your variables has no data.  What sort of game are you playing here?"
                        + " This is a major violation of the Statistical Penal Code, Section 3.1416.  The"
                        + " International Organization of Right-thinking Statisticans (IOR-tS), will be notified"
                        + " of your fraudulent assertion that this variable actually contains data.  You have"
                        + " violated the implied contract between the IOR-tS and you, the alleged"
                        + " quasi-competent statistician. The IOR-tS attorney, a Mr. O. Z. Mandias, "
                        + " will soon be in touch.  Look upon his lawyerly works and despair!!\n\n";
  
        backToTheRealWorld();
    }
        
        public static void showNoSubjectsChosenAlert() { 
        showingAnAlert = true;
        alertTitle = " Yo! User! You with the empty head -- there ain't no cases here!!";
        alertHeader = "Random assignment of subjects requires, well, you know -- subjects!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have more than a slight problem"
                        + " with your cases: you haven't chosen any.  What sort of game are you playing here?"
                        + " This is a major violation of the Statistical Penal Code, Section 3.1416.  The"
                        + " International Organization of Right-thinking Statisticans (IOR-tS), will be notified"
                        + " of your fraudulent assertion that you have chosen subjects for assignment.  You have"
                        + " violated the implied contract between the IOR-tS and you, the alleged"
                        + " quasi-competent statistician. The IOR-tS attorney, a Mr. O. Z. Mandias, "
                        + " will soon be in touch.  Look upon his lawyerly works and despair!!\n\n";
        backToTheRealWorld();
    }
        
    public static void showNoBlockingVariableChosenAlert() { 
        showingAnAlert = true;
        alertTitle = " Yo! User! You with the empty head -- there ain't no blocks here!!";
        alertHeader = "The randomized block design requires, well, you know -- blocks!!";
        alertContext = "SPLAT hates to be a bearer of bad news, but we have more than a slight problem"
                        + " with your blocks: you haven't chosen any.  What sort of game are you playing here?"
                        + " This is a major violation of the Statistical Penal Code, Section 3.1416.  The"
                        + " International Organization of Right-thinking Statisticans (IOR-tS), will be notified"
                        + " of your fraudulent assertion that you have chosen blocks for the purpose of assignment"
                        + " to treatments.  You have violated the implied contract between the IOR-tS and you,"
                        + "  the alleged quasi-competent statistician. The IOR-tS attorney, a Mr. O. Z. Mandias, "
                        + " will soon be in touch.  Look upon his lawyerly works and despair!!\n\n";
  
        backToTheRealWorld();
    }
            
    public static void showValuesLeftBlankAlert() { 
        showingAnAlert = true;
        alertTitle = "Warning!  Some fields left blank!!";
        alertHeader = "A slight Tabula Rasa here...";
        alertContext ="Ok, so here's the deal.  I don't care how tired you are, you"
                        + "\ngotta fill in all the stuff.  If you don't, I really am not"
                        + "\nauthorized to just make up numbers like some researchers do."
                        + "\nLet's try this numeric entry thing again.\n\n";        
        backToTheRealWorld();
    }
    
    public static void showbadBinRangeAlert() { 
        showingAnAlert = true;
        alertTitle = "Bad i-th bin range detected!!";
        alertHeader = "So, User, you are loath to follow gentle suggestions?!?!?";
        alertContext ="Against the very kindly and gently presented advice to define an ith bin within the"
                        + "\nrange of your data, you have chosen  alternative path. OK, fine.  SPLAT will not"
                        + "\nsend you to sleep with the fishes THIS time.  On the other hand, SPLAT will make"
                        + "\nyou an offer you can't refuse: Define the ith bin correctly, or keep the ones "   
                        + "\nyou have at present. Now, about that horse head on your bed...\n\n"; 
        backToTheRealWorld();
    }
    
    public static void showStemAndLeafAlert() { 
        showingAnAlert = true;
        alertTitle = "StemAndLeafAlert!";
        alertHeader = "Bubble, bubble, toil and trouble advisory...";
        alertContext = "Macbeth shall never vanquish'd be until Great Birnam wood to high Dunsinane hill"
                        + "\nshall come against him.\n"
                        + "\n                                                      --  Macbeth, Act IV: Scene 1\n"
                        + "\nUnfortunately, MacUser (or, for that matter, PCUser), your personal Macduff-ian"   
                        + "\nfoe is the configuration of the stems and leaves required to plot your data." 
                        + "\nIt is possible the plot will exceed the length and/or width of the available window."
                        + "\nI, SPLAT will do my customary magnificant best to provide some stem/leaf options."
                        + "\nHowever, if your personal Dunsinane hill shall fall, don't blame me!  Remember:"
                        + "\nI, SPLAT the Magnificent, warned you!"
                        + "\n\n                                                       -- The Three MacWitches of MacSPLAT\n\n";
        backToTheRealWorld();
    }
    
    public static void showBadAxisAlert() { 
        showingAnAlert = true;
        alertTitle = "Axis of Evil Alert!";
        alertHeader = "Uh-oh, User, now you are in really deep doo-doo...";
        alertContext = "For some unfathomable reason you have asked me, SPLAT, paragon of virtue, to create a"
                        + " graph that is not unlike less than virtuous.  The specific problem, faithfully"   
                        + " reported to me by one of my virtuous subroutines, is that the left end of one" 
                        + " of your scales is either greater than or equal to the right end.  The most likely"
                        + " failure (on your part) is that you have a variable without virtuous variability."
                        + " The only way for you and I to check this is to look in the spreadsheet.  In the"
                        + " incredibly unlikely event that your data does have variability, my only vaguely"
                        + " virtuous programmer must have messed up YET AGAIN.  Please email your data file"
                        + " and a sternly worded message to him so the bug can be fixed.  I, SPLAT, will"
                        + " go ahead and create an axis for you, but I doubt it will be what you want."
                        + " Word to Wise: You should SAVE YOUR DATA now to prevent losing all your work!!!\n";
        backToTheRealWorld();
    }
    
    public static void showQuantANOVABadLabelAlert() { 
        showingAnAlert = true;
        alertTitle = "Quant ANOVA Bad Label Alert!";
        alertHeader = "Uh-oh, User, you have transgressed...";
        alertContext = "OK, so here's the thing.  I, SPLAT, realize that you, USER, may not be familiar with"
                        + "\nquantitative ANOVA variables. I, SPLAT, depend on you, USER, to point me in the"   
                        + "\nright direction. If your treatment / population values are discrete numeric, the" 
                        + "\n way I, SPLAT, detect that information is through the use of numeric labels for the "
                        + "\nthe variables.(The labels are those things at the top of the spreadsheet. The way"
                        + "\n to fix this is return to the spreadsheet and change the label to something that "
                        + "\n looks like a number.  Go for it!\n\n";
        
        backToTheRealWorld();
    }
    
    public static void showInsertRemorseAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! Buyers' Insert Remorse??";
        alertHeader = "Uh-oh, User, you have transgressed slightly...";
        alertContext = "So, having second thoughts about inserting, are we?  Well, if so, the PROPER course"
                        + "\nof action is to click on the 'Cancel', not the 'Insert' button.  That is why I,"
                        +  "\nSPLAT the Magnificent, put the 'Cancel' button there. Now, let's get with the"
                        +  "\nprogram and try clicking again...\n\n";
    }
    
    public static void showDeleteRemorseAlert() { 
        showingAnAlert = true;
        alertTitle = "Ack! Buyers' Delete Remorse??";
        alertHeader = "Uh-oh, User, you have transgressed slightly...";
        alertContext = "So, having second thoughts about deleting, are we?  Well, if so, the PROPER course"
                        + "\nof action is to click on the 'Cancel', not the 'Delete' button.  That is why I,"
                        +  "\nSPLAT the Magnificent, put the 'Cancel' button there. Now, let's get with the"
                        +  "\nprogram and try clicking again...\n\n";
    }
    
    public static void showRCB_2_5_VarAlert() { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we could be in some difficulty here...";
        alertHeader = "There is a bit of a problem with your explanatory variable.";
        alertContext = "I am not quite sure what the problem is, dear User, but you gotta have more than 2"
                        + "\n values for your explanatory variable. Also, for whatever reason, my programmer"
                        + "\n-- limited as he is -- will not allow me, SPLAT, to proceed if there are more"
                        + "\nthan 5 values.  (I think it has something to do with too much crowding in some"
                        + "\nof the plots.) \n\n";
    }
    
    public static void showRCB_TooFewVarsAlert() { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we could be in some difficulty here...";
        alertHeader = "There is a bit of a problem with your variables.";
        alertContext = "Dear User, in a randomized block design you are required to have three variables:"
                        + "\n (1) an explanatory variable, (2) a response variable, and (3) a blocking"
                        + "\nvariable. I, SPLAT the Magnificent, don't make the experimental design rules;"
                        + "\n I do, however, ruthlessly enforce them.  Let's get with the program here!\n\n";
    }   
    
    public static void showMultReg_TooFewVarsAlert() { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we could be in some difficulty here...";
        alertHeader = "There is a bit of a problem with your variables.";
        alertContext = "Dear User, the reason for doing multiple regression is that you have more than one"
                        + "\nexplanatory variable. And, of course, you need a response variable.  So let's"
                        + "\ndo the math: 'More than one' plus 'one' = more than two, which you ain't got!"
                        + "\n et's get with the program here!\n\n";
    }  
    
    public static void showSampleSizeTooSmallAlert(int minSize) { 
        showingAnAlert = true;
        alertTitle = "Uh-oh, we could be in some difficulty here...";
        alertHeader = "There is a bit of a problem with your sample size.";
        alertContext = "Dear User, you are bereft of sufficient data. Some procedures need a sample size"
                        + "\nlarge enough to do the mathematics of the procedure, some need a sample size"
                        + "\n large enough to justify doing the procedure. (I.e. power seriously small.)"
                        + "\nFor this procedure you need at the very least " + minSize + " to do the math."
                        + "\nLet's get out there and get more data for the Gipper!\n\n";
    } 
    
    public static void showNoPowerVarIdentifiedAlert() { 
        showingAnAlert = true;
        alertTitle = "Warning!  Some fields left blank!!";
        alertHeader = "A slight Tabula Rasa here...";
        alertContext ="Ok, so here's the deal.  I don't care how tired you are, you gotta give me something"
                        + "\nto work with here. In this case, I need a variable.  If you don't specify one, "
                        + "\nI really am not authorized to just randomly pick one -- that's YOUR job!"
                        + "\nLet's try this again, this time with your eyes open.\n\n";        
        backToTheRealWorld();
    }
    
    public static void longTimeComingAlert(String message1, String message2) { 
        showingAnAlert = true;
        alertTitle = "Potential Coffee Break!!!";
        alertHeader = "Maybe a bagel, also???";
        alertContext ="Ok, so here's the deal. I, SPLAT, hate to interrupt your work with some facts of life."
                        + "\n No, no, not THOSE facts of life -- these are facts of life of algorithms."
                        + "\nSometimes it takes a while to do computer stuff, even if you have a fast computer"
                        + "\nand you are a touch typist. Sadly, this is one of those times.  I, SPLAT, just"
                        + "\nwish to give you a heads up so you don't think I am taking a break and have gone"
                        + "\n out for coffee and a bagel. So here's the deal: "
                        + "\n\n" + message1 + " variable, and "
                        + message2 + "\n\n";       
        backToTheRealWorld();
    }
        
    public static boolean getShowingAnAlert() {return showingAnAlert; }
    
    public static void backToTheRealWorld() {
        doTheDefaults();
        doTheSplatAlert();
        showingAnAlert = false;        
    }
    
    static private void doTheDefaults() {
        imagePath = "Warning.jpg";
        alertBoxWidth = 800.;
        alertBoxHeight = 575.;
        fitWidth = 75;
        imageOffSetX = 20.;
        imageOffSetY = 40.;
        horizImageSpace = 75;       
    }
    
    static private void doTheSplatAlert() {
        splatAlert = new SplatAlert(alertTitle,
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
