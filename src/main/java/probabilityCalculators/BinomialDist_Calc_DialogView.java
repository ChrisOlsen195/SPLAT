/**************************************************
 *             BinomialDist_Calc_DialogView       *
 *                    02/04/26                    *
 *                     00:00                      *
 *************************************************/
package probabilityCalculators;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import smarttextfield.*;
import utilityClasses.StringUtilities;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import superClasses.*;
import static utilityClasses.DataUtilities.strIsANonNegInt;

import utilityClasses.MyAlerts;

public class BinomialDist_Calc_DialogView extends BivariateScale_W_CheckBoxes_View {
    
    //POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean okToGraph, graphFound; 
    
    int binomial_n, probSelection, lowerShadeBound, upperShadeBound;
    final int PROB_ROUND = 4;
    
    int moreThanOneSelectedIndex = 0;
    int badRangeOrderIndex = 0;
    
    double binomial_p;
    
    final String toBlank = "";
    
        // *******************************  Unicodes  ********************************
    String LT, EQ, GT, LE, GE, leftSingleProbParen, rightSingleProbParen,
           LE_AND_LE, LE_AND_LT, LT_AND_LT, LT_AND_LE,
           str_LeftParen_LT, str_LeftParen_LE, str_LeftParen_EQ,
           str_LeftParen_GE, str_LeftParen_GT,
           str_Range_LeftParen, str_Range_LTLT, str_Range_LTLE, str_Range_LELT,
           str_Range_LELE;
    
    // FX Classes
    Button btn_ResetBinomial, btn_ResetParameters;
    
    Font probFont;
    
    GridPane allTheOptions;
    
    HBox hBox_LeftProbX_Is_LT, 
         hBox_LeftProbX_Is_LE, hBox_LeftProbX_Is_EQ,
         hBox_LeftProbX_Is_GE, hBox_LeftProbX_Is_GT,

         hBox_LeftProbX_Is_LTLT, hBox_LeftProbX_Is_LTLE, 
         hBox_LeftProbX_Is_LELT, hBox_LeftProbX_Is_LELE,
         hBox_N_Equals, hBox_p_Equals, hBox_4_Resets; 
    
    Insets insets_1, insets_2;
    
    Label lbl_N_Equals, lbl_P_Equals;
    Label[] probLabels;

    Pane theContainingPane;
    Region spacers[];
    Text txt_ProbTitle;
    
    // My classes
    BinomialDist_Calc_PDFView binomialDist_Calc_PDFView;  
    SmartTextFieldsController stf_ProbCalcs_Controller;
    SmartTextFieldDoublyLinkedSTF al_ProbCalcs_STF; 

    public BinomialDist_Calc_DialogView(ProbCalc_Dashboard probCalc_Dashboard, 
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);     
        if (printTheStuff) {
            System.out.println("*** 86 BinomialDist_Calc_DialogView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        txt_ProbTitle = new Text(60, 25, "         Binomial Distribution -- Make your choices! ");
        txt_ProbTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,18));
        probFont = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15.);

        stf_ProbCalcs_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set                          *
        stf_ProbCalcs_Controller.setSize(15);
        stf_ProbCalcs_Controller.finish_TF_Initializations();
        al_ProbCalcs_STF = stf_ProbCalcs_Controller.getLinkedSTF();
        al_ProbCalcs_STF.makeCircular();          

        createTheLabels(); createHBoxes(); createSTFs();
        
        insets_1 = new Insets(5.0f, 5.0f, 5.0f, 5.0f);
        insets_2 = new Insets(5.0f, 5.0f, 5.0f, 15.0f);

        lbl_N_Equals = new Label("n = ");
        lbl_P_Equals = new Label("p = ");

        lbl_N_Equals.setFont(probFont);
        lbl_P_Equals.setFont(probFont);
       
        hBox_N_Equals.getChildren()
                     .addAll(spacers[0], lbl_N_Equals, spacers[1],
                             al_ProbCalcs_STF.get(0).getTextField());
        
        hBox_p_Equals.getChildren()
                     .addAll(spacers[2], lbl_P_Equals, spacers[3],
                             al_ProbCalcs_STF.get(1).getTextField());     
        
        hBox_LeftProbX_Is_LT.getChildren()
                            .addAll(spacers[4], probLabels[3], spacers[5],
                                    al_ProbCalcs_STF.get(2).getTextField(),
                                    probLabels[8]);
        
        hBox_LeftProbX_Is_LE.getChildren()
                            .addAll(spacers[6],probLabels[4], spacers[7],
                                    al_ProbCalcs_STF.get(3).getTextField(),
                                    probLabels[9]);   
        
        hBox_LeftProbX_Is_EQ.getChildren()
                            .addAll(spacers[8], probLabels[5], spacers[9],
                                    al_ProbCalcs_STF.get(4).getTextField(),
                                    probLabels[10]); 

        hBox_LeftProbX_Is_GE.getChildren()
                            .addAll(spacers[10], probLabels[6], spacers[11],
                                    al_ProbCalcs_STF.get(5).getTextField(),
                                    probLabels[11]);  

        hBox_LeftProbX_Is_GT.getChildren()
                            .addAll(spacers[12], probLabels[7], spacers[13],
                                    al_ProbCalcs_STF.get(6).getTextField(),
                                    probLabels[12]); 

        hBox_LeftProbX_Is_LTLT.getChildren()
                      .addAll(spacers[23], probLabels[13], spacers[24],
                            al_ProbCalcs_STF.get(7).getTextField(),
                            probLabels[17], spacers[25],
                            al_ProbCalcs_STF.get(8).getTextField(),
                            probLabels[21]);
        
        hBox_LeftProbX_Is_LTLE.getChildren()
                              .addAll(spacers[20], probLabels[14], spacers[21],
                                    al_ProbCalcs_STF.get(9).getTextField(),
                                    probLabels[18], spacers[22],
                                    al_ProbCalcs_STF.get(10).getTextField(),
                                    probLabels[22]);  
        
        hBox_LeftProbX_Is_LELT.getChildren()
                              .addAll(spacers[17], probLabels[16], spacers[18],
                                    al_ProbCalcs_STF.get(11).getTextField(),
                                    probLabels[20], spacers[19],
                                    al_ProbCalcs_STF.get(12).getTextField(),
                                    probLabels[24]); 
                
        hBox_LeftProbX_Is_LELE.getChildren()
                              .addAll(spacers[14], probLabels[15], spacers[15],
                                    al_ProbCalcs_STF.get(13).getTextField(),
                                    probLabels[19], spacers[16],
                                    al_ProbCalcs_STF.get(14).getTextField(),
                                    probLabels[23]);
        
        hBox_N_Equals.setPadding(insets_1);
        hBox_p_Equals.setPadding(insets_1);
        
        hBox_LeftProbX_Is_LT.setPadding(insets_1);
        hBox_LeftProbX_Is_LE.setPadding(insets_1);
        hBox_LeftProbX_Is_EQ.setPadding(insets_1);
        hBox_LeftProbX_Is_GE.setPadding(insets_1);
        hBox_LeftProbX_Is_GT.setPadding(insets_1);
        
        hBox_LeftProbX_Is_LELE.setPadding(insets_1);
        hBox_LeftProbX_Is_LELT.setPadding(insets_1);
        hBox_LeftProbX_Is_LTLE.setPadding(insets_1);
        hBox_LeftProbX_Is_LTLT.setPadding(insets_1);
        
        btn_ResetBinomial = new Button("Reset binomial");
        btn_ResetBinomial.setOnAction(e -> resetBinomial());
        
        hBox_LeftProbX_Is_LTLT.setPadding(insets_1);
        
        btn_ResetParameters = new Button("Reset choice");
        btn_ResetParameters.setOnAction(e -> resetParameters());
        
        btn_ResetBinomial.setPadding(insets_2);
        btn_ResetParameters.setPadding(insets_2);

        hBox_4_Resets.getChildren().addAll(spacers[26], btn_ResetBinomial, spacers[27], btn_ResetParameters);
        
        allTheOptions = new GridPane();
        
        allTheOptions.add(txt_ProbTitle, 0, 0, 2, 1);
        allTheOptions.add(hBox_N_Equals, 0, 1);
        allTheOptions.add(hBox_LeftProbX_Is_LT, 0, 2);
        allTheOptions.add(hBox_LeftProbX_Is_LE, 0, 3);
        allTheOptions.add(hBox_LeftProbX_Is_EQ, 0, 4);
        allTheOptions.add(hBox_LeftProbX_Is_GE, 0, 5);
        allTheOptions.add(hBox_LeftProbX_Is_GT, 0, 6);
        allTheOptions.add(hBox_4_Resets, 0, 7);
        
        allTheOptions.add(hBox_p_Equals, 1, 1);        
        allTheOptions.add(hBox_LeftProbX_Is_LTLT, 1, 2);
        allTheOptions.add(hBox_LeftProbX_Is_LTLE, 1, 3);
        allTheOptions.add(hBox_LeftProbX_Is_LELT, 1, 4);
        allTheOptions.add(hBox_LeftProbX_Is_LELE, 1, 5);

        okToGraph = false;
        makeItHappen();
    }
    
    private void resetBinomial() {
        if (printTheStuff) {
            System.out.println("--- 224 BinomialDist_Calc_DialogView, resetBinomial()");
        }
        al_ProbCalcs_STF.get(0).setText(toBlank); 
        al_ProbCalcs_STF.get(1).setText(toBlank);
        okToGraph = false;
        resetParameters();
    }
    
    private void resetParameters() { clearTheSTFs(); }
    
    private void makeItHappen() { theContainingPane = new Pane(); }
    
    public void completeTheDeal() {  
        setUpAnchorPane();
        theContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
    
    public void setUpAnchorPane() {
        if (printTheStuff) {
            System.out.println("--- 243 BinomialDist_Calc_DialogView, setUpAnchorPane()");
        }
        dragableAnchorPane = new DragableAnchorPane();  
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                          .getChildren()
                          .add(allTheOptions);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void makeANewGraph() {
        if (printTheStuff) {
            System.out.println("--- 259 BinomialDist_Calc_DialogView, makeANewGraph()");
        }
        binomialDist_Calc_PDFView.respondToChanges();
        binomialDist_Calc_PDFView.doTheGraph(); 
    } 
    
    public String roundDoubleToProbString(double daDouble) {
        return StringUtilities.roundDoubleToNDigitString(daDouble, PROB_ROUND);
    }
  
    private void createHBoxes() {
        if (printTheStuff) {
            System.out.println("--- 271 BinomialDist_Calc_DialogView, createHBoxes()");
        }
        hBox_LeftProbX_Is_LT = new HBox(); hBox_LeftProbX_Is_LE = new HBox();
        hBox_LeftProbX_Is_EQ = new HBox(); hBox_LeftProbX_Is_GE = new HBox();
        hBox_LeftProbX_Is_GT = new HBox(); hBox_LeftProbX_Is_LTLT = new HBox();
        hBox_LeftProbX_Is_LTLE = new HBox(); hBox_LeftProbX_Is_LELE = new HBox();
        hBox_LeftProbX_Is_LELT = new HBox(); hBox_N_Equals = new HBox();
        hBox_p_Equals = new HBox(); hBox_4_Resets = new HBox();
        
        spacers = new Region[28];
        for (int ithSpacer = 0; ithSpacer < 28; ithSpacer++) {
            spacers[ithSpacer] = new Region();
        }
 
        spacers[0].setMinWidth(55); spacers[0].setMaxWidth(55);
        spacers[1].setMinWidth(5); spacers[1].setMaxWidth(5);
        spacers[2].setMinWidth(90); spacers[2].setMaxWidth(90);
        spacers[3].setMinWidth(5); spacers[3].setMaxWidth(5);
        spacers[4].setMinWidth(50); spacers[4].setMaxWidth(50);
        spacers[5].setMinWidth(7); spacers[5].setMaxWidth(7);
        spacers[6].setMinWidth(50); spacers[6].setMaxWidth(50);
        spacers[7].setMinWidth(1); spacers[7].setMaxWidth(1);
        spacers[8].setMinWidth(50); spacers[8].setMaxWidth(50);
        spacers[9].setMinWidth(7); spacers[9].setMaxWidth(7);
        spacers[10].setMinWidth(50); spacers[10].setMaxWidth(50);
        spacers[11].setMinWidth(1); spacers[11].setMaxWidth(1);
        spacers[12].setMinWidth(50); spacers[12].setMaxWidth(50);
        spacers[13].setMinWidth(7); spacers[13].setMaxWidth(7);
        spacers[14].setMinWidth(75); spacers[14].setMaxWidth(75);
        spacers[15].setMinWidth(5); spacers[15].setMaxWidth(5);
        spacers[16].setMinWidth(4); spacers[16].setMaxWidth(4);
        spacers[17].setMinWidth(75); spacers[17].setMaxWidth(75);
        spacers[18].setMinWidth(5); spacers[18].setMaxWidth(5);
        spacers[19].setMinWidth(8); spacers[19].setMaxWidth(8);
        spacers[20].setMinWidth(75); spacers[20].setMaxWidth(75);
        spacers[21].setMinWidth(5); spacers[21].setMaxWidth(5);
        spacers[22].setMinWidth(8); spacers[22].setMaxWidth(8);
        spacers[23].setMinWidth(75); spacers[23].setMaxWidth(75);
        spacers[24].setMinWidth(5); spacers[24].setMaxWidth(5);
        spacers[25].setMinWidth(12); spacers[25].setMaxWidth(12);
        spacers[26].setMinWidth(20); spacers[26].setMaxWidth(20);
        spacers[27].setMinWidth(20); spacers[27].setMaxWidth(20);
    }
    
    
    private void createSTFs() { 
        if (printTheStuff) {
            //System.out.println("--- 318 BinomialDist_Calc_DialogView, createSTFs()");
        }
        /*****************************************************************
         * The STFs:                                                     *
         *     0:  n                                                     *
         *     1:  p                                                     *
         *     2:  X_Is_LT_N                                             *
         *     3:  X_Is_LE_N                                             *
         *     4:  X_Is_EQ_N                                             *
         *     5:  X_Is_GE_N                                             *
         *     6:  X_Is_GT_N                                             *
         *****************************************************************                                                  
         *     7:  FirstRange_Prob_LTLT                                  *
         *     8:  SecondRange_Prob_LTLT                                 *
         *****************************************************************
         *     9:  FirstRange_Prob_LTLE                                  *
         *    10:  SecondRange_Prob_LTLE                                 *
         ***************************************************************** 
         *    11:  FirstRange_Prob_LELT                                  *
         *    12:  SecondRange_Prob_LELT                                 *
         *****************************************************************
         *    13:  FirstRange_Prob_LELE                                  *
         *    14:  SecondRange_Prob_LELE                                 *
         ****************************************************************/
        
        for (int ithSTF = 0; ithSTF < 15; ithSTF++) {
            al_ProbCalcs_STF.get(ithSTF).setText(toBlank);
        }
        
        for (int ithSTF = 0; ithSTF < 15; ithSTF++) {
            al_ProbCalcs_STF.get(ithSTF).getTextField().setMinWidth(40);
            al_ProbCalcs_STF.get(ithSTF).getTextField().setMaxWidth(40);
            al_ProbCalcs_STF.get(ithSTF).getTextField().setPrefColumnCount(10);
            al_ProbCalcs_STF.get(ithSTF).getTextField().setText(toBlank);
            al_ProbCalcs_STF.get(ithSTF).getTextField().setId(String.valueOf(ithSTF));
        }
        
        al_ProbCalcs_STF.get(0).setSmartTextField_MB_POSITIVEINTEGER(true);
        al_ProbCalcs_STF.get(1).setSmartTextField_MB_PROBABILITY(true);
        
        for (int ithSTF = 2; ithSTF < 15; ithSTF++) {
            al_ProbCalcs_STF.get(ithSTF).setSmartTextField_MB_NONNEGATIVE(true);
        }   
        
        /****************************************************************
         *          Special cases, where 0 is allowed                   *
         ***************************************************************/
              //     *****  2, 3, 4  *****
        for (int ithSTF = 2; ithSTF < 4; ithSTF++) {
            al_ProbCalcs_STF.get(ithSTF).setSmartTextField_MB_NONNEGATIVE(false);
            al_ProbCalcs_STF.get(ithSTF).setSmartTextField_MB_POSITIVEINTEGER(true);

        }
            //     *****  8, 10, 12, 14   *****
        for (int ithSTF = 0; ithSTF < 4; ithSTF++) {
            al_ProbCalcs_STF.get(8 + 2 * ithSTF).setSmartTextField_MB_NONNEGATIVE(false);
            al_ProbCalcs_STF.get(8 + 2 * ithSTF).setSmartTextField_MB_POSITIVEINTEGER(true);

        } 

        graphFound = false;
        for (int ithSTF = 0; ithSTF < 15; ithSTF++) {
            
            al_ProbCalcs_STF.get(ithSTF).getTextField().setOnAction(e -> 
            {
                okToGraph = checkWhetherToGraph(); 
                if (okToGraph) {
                    graphFound = true;
                    if (graphFound) {
                        makeANewGraph();
                    }
                }   
            });
        }
    }
    
    
    private void createTheLabels() {
        if (printTheStuff) {
            System.out.println("--- 397 BinomialDist_Calc_DialogView, createTheLabels()");
        }
        /****************************************************************
         * The Labels:                                                   *
         *     0:  lbl_Title                                             *
         *     1:  lbl_N_Equals                                          *
         *     2:  lbl_P_Equals                                          *
         *     3:  lbl_LeftParen_LT                                      *
         *     4:  lbl_LeftParen_LE                                      *
         *     5:  lbl_LeftParen_EQ                                      *
         *     6:  lbl_LeftParen_GE                                      *
         *     7:  lbl_LeftParen_GT                                      *
         *****************************************************************                                                  
         *     8:  lbl_LT_RightParen                                     *
         *     9:  lbl_LE_RightParen                                     *
         *    10:  lbl_EQ_RightParen                                     *
         *    11:  lbl_GE_RightParen                                     *
         *    12:  lbl_GT_RightParen                                     *
         *****************************************************************
         *    13:  lbl_LTLT_Left_Paren                                   *
         *    14:  lbl_LTLE_Left_Paren                                   *
         *    15:  lbl_LELE_Left_Paren                                   *
         *    16:  lbl_LELT_Left_Paren                                   *
         *****************************************************************
         *    17:  lbl_RangeIs_LTLT                                      *
         *    18:  lbl_RangeIs_LTLE                                      *
         *    19:  lbl_RangeIs_LELE                                      *
         *    20:  lbl_RangeIs_LELT                                      *
         *****************************************************************
         *    21:  lbl_RangeIs_LTLT_RightParen                           *
         *    22:  lbl_RangeIs_LTLE_RightParen                           *
         *    23:  lbl_RangeIs_LELE_RightParen                           *
         *    24:  lbl_RangeIs_LELT_RightParen                           *
         ****************************************************************/
        
    // *******************************  Unicodes  ********************************
        LT = " < "; EQ = " = "; GT = " > "; LE = " \u2266 "; GE = " \u2267 ";

        leftSingleProbParen = "P(x"; rightSingleProbParen = ")";

        LT_AND_LT = LT + " x " + LT; LT_AND_LE = LT + " x " + LE;
        LE_AND_LE = LE + " x " + LE; LE_AND_LT = LE + " x " + LT;
        
        str_LeftParen_LT = leftSingleProbParen + LT;            // "P(x<"        
        str_LeftParen_LE = leftSingleProbParen + LE;        
        str_LeftParen_EQ = leftSingleProbParen + EQ;
        str_LeftParen_GE = leftSingleProbParen + GE;        
        str_LeftParen_GT = leftSingleProbParen + GT; 

        // ----------------  Probabilities of ranges  -------------------------      
        str_Range_LeftParen = "P("; 
        str_Range_LTLT = " < x <";
        str_Range_LTLE = " < x " + LE; 
        str_Range_LELT = LE + " x <";
        str_Range_LELE = LE + " x " + LE;        
        
         int nLabels = 25;
         probLabels = new Label[nLabels];
         
         probLabels[0] = new Label("This is a binomial title");
         probLabels[1] = new Label("n = ");
         probLabels[2] = new Label("p = ");

         probLabels[3] = new Label(str_LeftParen_LT); 
         probLabels[4] = new Label(str_LeftParen_LE);
         probLabels[5] = new Label(str_LeftParen_EQ);
         probLabels[6] = new Label(str_LeftParen_GE);
         probLabels[7] = new Label(str_LeftParen_GT);

         probLabels[8] = new Label(rightSingleProbParen);
         probLabels[9] = new Label(rightSingleProbParen);
         probLabels[10] = new Label(rightSingleProbParen);
         probLabels[11] = new Label(rightSingleProbParen);        
         probLabels[12] = new Label(rightSingleProbParen);   

         probLabels[13] = new Label(str_Range_LeftParen);
         probLabels[14] = new Label(str_Range_LeftParen);
         probLabels[15] = new Label(str_Range_LeftParen);       
         probLabels[16] = new Label(str_Range_LeftParen);   
         
         probLabels[17] = new Label(str_Range_LTLT);
         probLabels[18] = new Label(str_Range_LTLE);
         probLabels[19] = new Label(str_Range_LELE);       
         probLabels[20] = new Label(str_Range_LELT);    
         
         probLabels[21] = new Label(rightSingleProbParen);
         probLabels[22] = new Label(rightSingleProbParen);
         
         probLabels[23] = new Label(rightSingleProbParen);       
         probLabels[24] = new Label(rightSingleProbParen); 

         for (int ithLabel = 0; ithLabel < nLabels; ithLabel++) {
             probLabels[ithLabel].setFont(probFont);
         }
    }
    
    private boolean checkWhetherToGraph() { 
        if (printTheStuff) {
            System.out.println("--- 495 BinomialDist_Calc_DialogView, checkWhetherToGraph()");
        }
        // Check for binomial not yet defined
        
        if (al_ProbCalcs_STF.get(0).getText().isEmpty() || al_ProbCalcs_STF.get(1).getText().isEmpty()) {
            if (printTheStuff) {
                System.out.println("... 501 BinomialDist_Calc_DialogView");
            }
            binomialDist_Calc_PDFView.setInitializing(true);
            okToGraph = false;
            return okToGraph;
        }

        binomial_n = Integer.parseInt(al_ProbCalcs_STF.get(0).getText());
        binomial_p = Double.parseDouble(al_ProbCalcs_STF.get(1).getText());
        binomialDist_Calc_PDFView.setInitializing(false);
        okToGraph = true;
        //    ************   Check for out of bounds values  **********
        for (int ithSTF = 2; ithSTF < 15; ithSTF++) {
            if (!(al_ProbCalcs_STF.get(ithSTF).getText().isEmpty())) {
                String strTemp = al_ProbCalcs_STF.get(ithSTF).getText();   
                if (strIsANonNegInt(strTemp)) {
                    int tempInt = Integer.parseInt(strTemp);
                    if (tempInt > binomial_n) {
                        MyAlerts.showBinomialDaredevilAlert();
                        resetBinomial();
                        okToGraph = false;
                        return okToGraph;
                    }
                }
            }
            
        }
                
        //    ************   Check for more than one selected  **********
        boolean moreThanOneSelected = moreThanOneIsSelected();
        
        if (moreThanOneSelected) {
            if (printTheStuff) {
                System.out.println("... 517 BinomialDist_Calc_DialogView");
            }
            moreThanOneSelectedIndex++;
            
            if (moreThanOneSelectedIndex == 1) {
                MyAlerts.showMoreThanOneSelectionAlert();
                resetParameters();
            }
            else {
               moreThanOneSelectedIndex = 0; 
            }
            moreThanOneSelected = false;

            okToGraph = false;
            if (printTheStuff) {
                System.out.println("... 532 BinomialDist_Calc_DialogView, moreThanOneSelected = false");
                System.out.println("... 533 BinomialDist_Calc_DialogView, okToGraph = false");
            }
            return okToGraph;
        }  
        
        //    ************   Check for bad range order  **********
        boolean badRangeOrder = rangeOrderIsBad();
        
        if (badRangeOrder) {
            badRangeOrderIndex++;
            if (badRangeOrderIndex == 1) {
                MyAlerts.showBadRangeAlert();
                resetParameters();
            }
            else {
               badRangeOrderIndex = 0; 
            }
            //badRangeOrder = false;
            okToGraph = false;
            if (printTheStuff) {
                System.out.println("... 553 BinomialDist_Calc_DialogView,  badRangeOrderIndex = " + badRangeOrderIndex);
                System.out.println("... 554 BinomialDist_Calc_DialogView, okToGraph = false");
            }
            return okToGraph;
        }      
        
        /***************************************************************
         *   ProbSelection = 0 indicates that there will be no shading *
         *   The effect of probSelection = 0 will be that the mean and *
         *   standard deviation of the binomial can be printed without *
         *   any shading.                                              *
         **************************************************************/
        probSelection = 0;

        // Check for X LT, LE, EQ, GE, GT
        if (!al_ProbCalcs_STF.get(2).getText().isEmpty()) {   //  LT N
            //System.out.println("... 569 BinomCalcDial, 2 not empty");
            lowerShadeBound = 0; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(2).getText()) - 1;
            probSelection = 1;
            okToGraph = true;
        }  
        else if (!al_ProbCalcs_STF.get(3).getText().isEmpty()) {  //  LE N
            //System.out.println("... 576 BinomCalcDial, 3 not empty");
            lowerShadeBound = 0; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(3).getText());
            probSelection = 2;
            okToGraph = true;
        }   
        else if (!al_ProbCalcs_STF.get(4).getText().isEmpty()) {  //  EQ N
            //System.out.println("... 583 BinomCalcDial, 4 not empty");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(4).getText());  
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(4).getText()); 
            probSelection = 3;
            okToGraph = true;
        } 
        else if (!al_ProbCalcs_STF.get(5).getText().isEmpty()) {  //  GE N
            //System.out.println("... 590 BinomCalcDial, 5 not empty");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(5).getText()); 
            upperShadeBound = binomial_n;
            probSelection = 4;
            okToGraph = true;
        } 
        else if (!al_ProbCalcs_STF.get(6).getText().isEmpty()) { //  GT N
            //System.out.println("... 597 BinomCalcDial, 6 not empty");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(6).getText()) + 1; 
            upperShadeBound = binomial_n;
            probSelection = 5;
            okToGraph = true;
        } 
        
        //  ********************     Check the ranges     *********************
        
        //                      7  <  x  <  8
        else if (!al_ProbCalcs_STF.get(7).getText().isEmpty() && !al_ProbCalcs_STF.get(8).getText().isEmpty()) {
            //System.out.println("... 608 BinomCalcDial, 7, 8 not empty");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(7).getText()) + 1; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(8).getText()) - 1;
            System.out.println("... 604 BinomCalcDial, lower/upperShadeBounds = " + lowerShadeBound + " / " + upperShadeBound);
            probSelection = 6;
            okToGraph = true;
        }  
        //                        9 <  x  <= 10
        else if (!al_ProbCalcs_STF.get(9).getText().isEmpty() && !al_ProbCalcs_STF.get(10).getText().isEmpty()) {
            //System.out.println("... 617 BinomCalcDial, 9, 10 not empty");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(9).getText()) + 1; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(10).getText());
            //System.out.println("... 620 BinomCalcDial, lower/upperShadeBounds = " + lowerShadeBound + " / " + upperShadeBound);
            probSelection = 7;
            okToGraph = true;
        }  
        //                      11  <=  x  <  12
        else if (!al_ProbCalcs_STF.get(11).getText().isEmpty() && !al_ProbCalcs_STF.get(12).getText().isEmpty()) {
            //System.out.println("... 626 BinomCalcDial, 11, 12 not empty");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(11).getText()); 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(12).getText()) - 1;
            //System.out.println("... 629 BinomCalcDial, lower/upperShadeBounds = " + lowerShadeBound + " / " + upperShadeBound);
            probSelection = 8;
            okToGraph = true;
        }  
        //                      13  <=  x  <=  14
        else if (!al_ProbCalcs_STF.get(13).getText().isEmpty() && !al_ProbCalcs_STF.get(14).getText().isEmpty()) {
            //System.out.println("... 635 BinomCalcDial, 13, 14 not empty");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(13).getText()); 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(14).getText());
            //System.out.println("... 638 BinomCalcDial, lower/upperShadeBounds = " + lowerShadeBound + " / " + upperShadeBound);
            probSelection = 9;
            okToGraph = true;
        } 
        if (printTheStuff) {
            System.out.println("... 643 BinomialDist_Calc_DialogView, END checkWhetherToGraph()");
            System.out.println("... 644 BinomialDist_Calc_DialogView, lower/upper = " + lowerShadeBound + " / " + upperShadeBound);
            System.out.println("... 645 BinomialDist_Calc_DialogView, okToGraph = " + okToGraph);
        }    
        
        if ((lowerShadeBound > binomial_n) || (upperShadeBound > binomial_n)) {
            MyAlerts.showBinomialDaredevilAlert();
            okToGraph = false;
        }
        return okToGraph;
    } 
    
    public void clearTheSTFs() {
        if (printTheStuff) {
            System.out.println("--- 657 BinomialDist_Calc_DialogView, clearTheSTFs()");
        }
        for (int ithSTF = 2; ithSTF < 15; ithSTF++) {
            al_ProbCalcs_STF.get(ithSTF).setText(toBlank);
        }
    }
    
    public void setSTF(int thisOne, String toThisValue) {
        al_ProbCalcs_STF.get(thisOne).setText(toThisValue);
    }
    
    private boolean moreThanOneIsSelected() {
        if (printTheStuff) {
            System.out.println("--- 670 BinomialDist_Calc_DialogView, check for moreThanOneIsSelected()");
        }
        // Check the singles
        boolean moreThanOne = false;
        int nSelected = 0;

        for (int ithSTF = 2; ithSTF < 7; ithSTF++) {
            if (!al_ProbCalcs_STF.get(ithSTF).getText().isEmpty()) {
                nSelected++;
            }
        }
        
        if (nSelected > 1) {return true; }
        
        // Check the lefties
        int nLeftiesSelected = 0;
        int nRightiesSelected = 0;
        
        for (int ithSTF = 7; ithSTF <= 13; ithSTF = ithSTF + 2) {
            if (!al_ProbCalcs_STF.get(ithSTF).getText().isEmpty() || !al_ProbCalcs_STF.get(ithSTF).getText().isEmpty()) {
                nLeftiesSelected++;
            }
        }  
        if (nLeftiesSelected > 1) {return true; }
        
        // Check the righties
        for (int ithSTF = 8; ithSTF <= 14; ithSTF = ithSTF + 2) {
            if (!al_ProbCalcs_STF.get(ithSTF).getText().isEmpty() || !al_ProbCalcs_STF.get(ithSTF).getText().isEmpty()) {
                nRightiesSelected++;
            }
        }        
        return false;
    }
    
            //    ************   Check for bad range  **********
    private boolean rangeOrderIsBad() {
        if (printTheStuff) {
            System.out.println("--- 707 BinomialDist_Calc_DialogView, check for rangeOrderIsBad()");
        }
        boolean badRange = false;
        
        for (int ithSTF = 7; ithSTF <= 13; ithSTF = ithSTF + 2) {
            if (!al_ProbCalcs_STF.get(ithSTF).getText().isEmpty() && !al_ProbCalcs_STF.get(ithSTF + 1).getText().isEmpty()) {
                int leftEnd = Integer.parseInt(al_ProbCalcs_STF.get(ithSTF).getText());
                int rightEnd = Integer.parseInt(al_ProbCalcs_STF.get(ithSTF + 1).getText());
                if (rightEnd <= leftEnd) {
                    badRange = true;
                }
            }
        }         
        if (printTheStuff) {
            System.out.println("... 721 BinomialDist_Calc_DialogView, END check for rangeOrderIsBad()");
            System.out.println("... 722 BinomialDist_Calc_DialogView, badRange = " + badRange);
        }        
        return badRange;
    }
    
    public boolean getOKToGraph() { return okToGraph; }
    
    public int getBinomial_n() { return binomial_n; }
    public double getBinomial_p() { return binomial_p; }
    public String getThisSTF(int thisSTF) { return al_ProbCalcs_STF.get(thisSTF).getText(); }
    
    @Override
    public Pane getTheContainingPane() { return theContainingPane; }

    public void set_Binomial_PDFView(BinomialDist_Calc_PDFView binomialDist_Calc_PDFView) {
        this.binomialDist_Calc_PDFView = binomialDist_Calc_PDFView;
    }
    
    public int getProbSelection() { return probSelection; }
    public int getLowerShadeBound() { return lowerShadeBound; }
    public int getUpperShadeBound() {return upperShadeBound; }    
}
