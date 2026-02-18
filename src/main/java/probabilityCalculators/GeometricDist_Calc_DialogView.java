/**************************************************
 *             GeometricDist_Calc_DialogView      *
 *                    11/01/25                    *
 *                     12:00                      *
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
import utilityClasses.MyAlerts;

public class GeometricDist_Calc_DialogView extends BivariateScale_W_CheckBoxes_View {
    
    //POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean okToGraph, graphFound;
    
    int geometric_N2Display;
    int probSelection;
    final int PROB_ROUND = 4;
    
    int moreThanOneSelectedIndex = 0;
    int badRangeOrderIndex = 0;
    
    int lowerShadeBound, upperShadeBound;
    
    double geometric_p;

    final String toBlank = "";

    // *******************************  Unicodes  ********************************
    String LT, EQ, GT, LE, GE, leftSingleProbParen, rightSingleProbParen, LT_AND_LT, LT_AND_LE, 
           LE_AND_LE, LE_AND_LT;
    
    String str_LeftParen_LT, str_LeftParen_LE, str_LeftParen_EQ,
           str_LeftParen_GE, str_LeftParen_GT;
    
    String str_Range_LeftParen, str_Range_LTLT, str_Range_LTLE, str_Range_LELT,
           str_Range_LELE;
    
    // FX Classes
    Button btn_ResetGeometric, btn_ResetParameters;
    
    Font probFont;
    
    GridPane allTheOptions;
    
    HBox hBox_LeftProbX_Is_LT,
         hBox_LeftProbX_Is_LE, hBox_LeftProbX_Is_EQ,
         hBox_LeftProbX_Is_GE, hBox_LeftProbX_Is_GT;

    HBox hBox_LeftProbX_Is_LTLT, hBox_LeftProbX_Is_LTLE, 
         hBox_LeftProbX_Is_LELT, hBox_LeftProbX_Is_LELE,
         hBox_N_Equals, hBox_p_Equals; 
    
    HBox hBox_4_Resets;
    
    Insets insets_1, insets_2;
    
    Label lbl_N2Display_Equals, lbl_P_Equals;
    Label[] probLabels;
    
    Text txtProbTitle;

    Pane theContainingPane;
    Region spacers[];
    
    // My classes
    GeometricDist_Calc_PDFView geometricDist_Calc_PDFView;  
    SmartTextFieldsController stf_ProbCalcs_Controller;
    SmartTextFieldDoublyLinkedSTF al_ProbCalcs_STF;     

    public GeometricDist_Calc_DialogView(ProbCalc_Dashboard probCalc_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);     
        if (printTheStuff) {
            System.out.println("91 *** GeometricDist_Calc_DialogView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        txtProbTitle = new Text(60, 25, "        Geometric Distribution -- Make your choices! ");
        txtProbTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,18));
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
       
        lbl_N2Display_Equals = new Label("n to display = ");
        lbl_P_Equals = new Label("p = ");

        lbl_N2Display_Equals.setFont(probFont);
        lbl_P_Equals.setFont(probFont);
       
        hBox_N_Equals.getChildren()
                     .addAll(spacers[0], lbl_N2Display_Equals, spacers[1],
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
        
        btn_ResetGeometric = new Button("Reset Geometric");
        btn_ResetGeometric.setOnAction(e -> resetGeometric());
        
        hBox_LeftProbX_Is_LTLT.setPadding(insets_1);
        
        btn_ResetParameters = new Button("Reset choice");
        btn_ResetParameters.setOnAction(e -> resetParameters());
        
        btn_ResetGeometric.setPadding(insets_2);
        btn_ResetParameters.setPadding(insets_2);

        hBox_4_Resets.getChildren().addAll(spacers[26], btn_ResetGeometric, spacers[27], btn_ResetParameters);
        
        allTheOptions = new GridPane();
        
        allTheOptions.add(txtProbTitle, 0, 0, 2, 1);
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
    
    private void resetGeometric() {
        if (printTheStuff) {
            System.out.println("229 *** GeometricDist_Calc_DialogView, resetGeometric()");
        }
        al_ProbCalcs_STF.get(0).setText(toBlank); 
        al_ProbCalcs_STF.get(1).setText(toBlank);
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
            System.out.println("247 *** GeometricDist_Calc_DialogView, setUpAnchorPane()");
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
            System.out.println("261 *** GeometricDist_Calc_DialogView, makeANewGraph()");
        }
        geometricDist_Calc_PDFView.respondToChanges();
        geometricDist_Calc_PDFView.doTheGraph(); 
    } 
    
    public String roundDoubleToProbString(double daDouble) {
        return StringUtilities.roundDoubleToNDigitString(daDouble, PROB_ROUND);
    }
  
    private void createHBoxes() {
        if (printTheStuff) {
            System.out.println("273 *** GeometricDist_Calc_DialogView, createHBoxes()");
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
            System.out.println("319 *** GeometricDist_Calc_DialogView, createSTFs()");
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
         *     7:  FirstRange_Prob_LELE                                  *
         *     8:  SecondRange_Prob_LELE                                 *
         *****************************************************************
         *     9:  FirstRange_Prob_LELT                                  *
         *    10:  SecondRange_Prob_LELT                                 *
         ***************************************************************** 
         *    11:  FirstRange_Prob_LTLE                                  *
         *    12:  SecondRange_Prob_LTLE                                 *
         *****************************************************************
         *    13:  FirstRange_Prob_LTLT                                  *
         *    14:  SecondRange_Prob_LTLT                                 *
         ****************************************************************/
        
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
            al_ProbCalcs_STF.get(ithSTF).setSmartTextField_MB_POSITIVEINTEGER(true);
        }      
        
        graphFound = false;
        for (int ithSTF = 0; ithSTF < 15; ithSTF++) {
            
            al_ProbCalcs_STF.get(ithSTF).getTextField().setOnAction(e -> {
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
            System.out.println("377 *** GeometricDist_Calc_DialogView, createTheLabels()");
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
        LT = " < "; EQ = " ="; GT = " > "; LE = " \u2266 "; GE = " \u2267 ";

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
        str_Range_LTLT = " < x < ";
        str_Range_LTLE = " < x " + LE; 
        str_Range_LELT = LE + " x < ";
        str_Range_LELE = LE + " x " + LE;        
        
        int nLabels = 25;
        probLabels = new Label[nLabels];

        probLabels[0] = new Label("This is a Geometric title");
        probLabels[1] = new Label("n to display= ");
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
            System.out.println("475 *** GeometricDist_Calc_DialogView, checkWhetherToGraph()");
        }
        okToGraph = false;
        // Check for geometric not yet defined        
        if (al_ProbCalcs_STF.get(0).getText().isEmpty() || al_ProbCalcs_STF.get(1).getText().isEmpty()) {
            geometricDist_Calc_PDFView.setInitializing(true);
            return false;
        }

        geometric_N2Display = Integer.parseInt(al_ProbCalcs_STF.get(0).getText());
        geometric_p = Double.parseDouble(al_ProbCalcs_STF.get(1).getText());
        geometricDist_Calc_PDFView.setInitializing(false);
        okToGraph = true;
        boolean moreThanOneSelected = moreThanOneIsSelected();
        
        if (moreThanOneSelected) {
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
            return false;
        }  
        
        // ---------------------------------------------------------------
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
            badRangeOrder = false;
            okToGraph = false;
            return false;
        }      
               
        // --------------------------------------------------------------
        
        // Check for X LT, LE, EQ, GE, GT
        if (!al_ProbCalcs_STF.get(2).getText().isEmpty()) {   //  LT N
            System.out.println("529 GeomCalcDial, LT N");
            lowerShadeBound = 0; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(2).getText()) - 1;
            probSelection = 1;
            okToGraph = true;
        }  
        else if (!al_ProbCalcs_STF.get(3).getText().isEmpty()) {  //  LE N
            System.out.println("536 GeomCalcDial, LE N");
            lowerShadeBound = 0; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(3).getText());
            probSelection = 2;
            okToGraph = true;
        }   
        else if (!al_ProbCalcs_STF.get(4).getText().isEmpty()) {  //  EQ N
            System.out.println("543 GeomCalcDial, EQ N");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(4).getText());  
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(4).getText()); 
            probSelection = 3;
            okToGraph = true;
        } 
        else if (!al_ProbCalcs_STF.get(5).getText().isEmpty()) {  //  GE N
            System.out.println("550 GeomCalcDial, GE N");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(5).getText()); 
            upperShadeBound = geometric_N2Display;
            probSelection = 4;
            okToGraph = true;
        } 
        else if (!al_ProbCalcs_STF.get(6).getText().isEmpty()) { //  GT N
            System.out.println("557 GeomCalcDial, GT N");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(6).getText()) + 1; 
            upperShadeBound = geometric_N2Display;
            probSelection = 5;
            okToGraph = true;
        } 
        
        //  ********************     Check the ranges     *********************
        
        //                      7  <  x  <  8
        else if (!al_ProbCalcs_STF.get(7).getText().isEmpty() && !al_ProbCalcs_STF.get(8).getText().isEmpty()) {
            System.out.println("568 GeomCalcDial, 7  <  x  <  8");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(7).getText()) + 1; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(8).getText()) - 1;
            probSelection = 6;
            okToGraph = true;
        }  
        //                        9 <  x <= 10
        else if (!al_ProbCalcs_STF.get(9).getText().isEmpty() && !al_ProbCalcs_STF.get(10).getText().isEmpty()) {
            System.out.println("576 GeomCalcDial, 9 <  x <= 10");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(9).getText()) + 1; 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(10).getText());
            probSelection = 7;
            okToGraph = true;
        }  
        //                      11  <=  x  <  12
        else if (!al_ProbCalcs_STF.get(11).getText().isEmpty() && !al_ProbCalcs_STF.get(12).getText().isEmpty()) {
            System.out.println("584 GeomCalcDial, 11  <=  x  <  12");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(11).getText()); 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(12).getText()) - 1;
            probSelection = 8;
            okToGraph = true;
        }  
        //                      13  <=  x  <=  14
        else if (!al_ProbCalcs_STF.get(13).getText().isEmpty() && !al_ProbCalcs_STF.get(14).getText().isEmpty()) {
            System.out.println("592 GeomCalcDial, 13  <=  x  <=  14");
            lowerShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(13).getText()); 
            upperShadeBound = Integer.parseInt(al_ProbCalcs_STF.get(14).getText());
            probSelection = 9;
            okToGraph = true;
        } 

        return okToGraph; 
    } 

    public void clearTheSTFs() {
        if (printTheStuff) {
            System.out.println("604 *** GeometricDist_Calc_DialogView, clearTheSTFs()");
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
            System.out.println("617 *** GeometricDist_Calc_DialogView, moreThanOneIsSelected()");
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
        
        if (nRightiesSelected > 1) {return true; }
        
        return false;
    }
    
            //    ************   Check for bad range  **********
    private boolean rangeOrderIsBad() {
        if (printTheStuff) {
            System.out.println("658 *** GeometricDist_Calc_DialogView, rangeOrderIsBad()");
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
        
        return badRange;
    }
    
    public boolean getOKToGraph() { return okToGraph; }
    
    public int getGeometric_nToDisplay() { return geometric_N2Display; }
    public double getGeometric_p() { return geometric_p; }
    public String getThisSTF(int thisSTF) { return al_ProbCalcs_STF.get(thisSTF).getText(); }
    
    @Override
    public Pane getTheContainingPane() { return theContainingPane; }

    public void set_Geometric_PDFView(GeometricDist_Calc_PDFView geometricDist_Calc_PDFView) {
        this.geometricDist_Calc_PDFView = geometricDist_Calc_PDFView;
    }
    
    public int getProbSelection() { return probSelection; }
    public int getLowerShadeBound() { return lowerShadeBound; }
    public int getUpperShadeBound() {return upperShadeBound; }    
}
