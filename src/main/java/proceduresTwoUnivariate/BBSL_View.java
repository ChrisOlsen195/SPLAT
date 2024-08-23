/****************************************************************************
 *                        BBSL_View                                         * 
 *                        12/06/23                                          *
 *                          00:00                                           *
 ***************************************************************************/
package proceduresTwoUnivariate;

import the_t_procedures.Indep_t_Dashboard;
import genericClasses.DragableAnchorPane;
import utilityClasses.StringUtilities;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import utilityClasses.MyAlerts;

public class BBSL_View {
    // POJOs
    
    boolean[] radioButtonSettings; 
    
    int titleLeftNSize, titleRightNSize, n_OneLineToPrint, n_TwoLinesToPrint, 
        n_FiveLinesToPrint, nRadioButtons, nSpacers, linePerStemChoice, 
        ithIDChosen, charsInToVertChar_1, charsInToVertChar_2, 
        charsInToVertChar_5;
    
    double initHoriz, initVert, initWidth, initHeight;  
    
    String strBBSL_Title_1, strBBSL_Title_2, strBBSL_Title_5,
           strBBSL_VarName_1,  strBBSL_VarName_2,  strBBSL_VarName_5,           
           strBBSL_NSize_1, strBBSL_NSize_2, strBBSL_NSize_5,         
           strBBSL_OrdMag_1, strBBSL_OrdMag_2, strBBSL_OrdMag_5,       
           tempString, strThisLine, strSLNSize, strOrdMag,
           strVarName, bbslTitle2, bbsl_Title1, bbsl_Title2_1, bbsl_Title2_2, 
           bbsl_Title2_5, bbslTitleLines;    
    String bbsl_Title = "Back to Back Stem & Leaf Plot";      
    String[] strRadioButtonDescriptions;    
    static ArrayList<String> oneLineBBSL, twoLineBBSL, fiveLineBBSL; 
    ArrayList<String> oneLineStrings2Print, twoLinesStrings2Print, 
                      fiveLinesStrings2Print;
    
    // My classes
    BBSL_Model bbsl_Model;
    BBSL_View bbsl_View;
    Indep_t_Dashboard independent_t_Dashboard;
    DragableAnchorPane dragableAnchorPane; 

    // FX Objects
    AnchorPane theSLAnchorPane;
    Pane containingPane;   
    Pane bbslpvContainingPane;
    String containingPaneStyle;

    Text titleText, thisText, titleText_x;
    TextArea txtArea, txtArea_BBSL_1, txtArea_BBSL_2, txtArea_BBSL_5;

    HBox radioButtonRow;
    Region[] spacers;
    RadioButton[] bbsl_RadioButtons;  
   
    public BBSL_View(BBSL_Model bbsl_Model, Explore_2Ind_Dashboard compare2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        //System.out.println("74 BBSL_View, Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        bbsl_View = this;
        bbslTitle2 = bbsl_Model.getSubTitle_And();
        this.bbsl_Model = bbsl_Model;
        someFurtherInitializations();
    }
    
    public BBSL_View(BBSL_Model bbsl_Model, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        //System.out.println("86 BBSL_View, Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;    
        this.bbsl_Model = bbsl_Model;
        bbslTitle2 = bbsl_Model.getFirstAndSecondDescription();
        someFurtherInitializations();
    }
    
    private void someFurtherInitializations() {
        containingPaneStyle = "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        nRadioButtons = 3;
        nSpacers = 2;
        strRadioButtonDescriptions = new String[3];
        strRadioButtonDescriptions[0] = " One line / stem ";
        strRadioButtonDescriptions[1] = " Two lines / stem ";
        strRadioButtonDescriptions[2] = " Five lines / stem "; 
        
        oneLineBBSL = new ArrayList<>();
        twoLineBBSL = new ArrayList<>();
        fiveLineBBSL = new ArrayList<>();
        
        oneLineStrings2Print = new ArrayList<>();
        twoLinesStrings2Print = new ArrayList<>();
        fiveLinesStrings2Print = new ArrayList<>();
        
        oneLineBBSL = bbsl_Model.get_1_LineBBSL();
        twoLineBBSL = bbsl_Model.get_2_LineBBSL();
        fiveLineBBSL = bbsl_Model.get_5_LineBBSL();  
         
        n_OneLineToPrint = oneLineBBSL.size();
        n_TwoLinesToPrint = twoLineBBSL.size();
        n_FiveLinesToPrint = fiveLineBBSL.size();       
    }
    
    public void completeTheDeal() {
        setUpUI(); 
        makeTheButtonBox();
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane(); 
    }
    
    private void setUpUI() {  
        // Construct the magnitude
        double max = (int)bbsl_Model.getMax();
        int ordMag = (int)Math.floor(Math.log10(max));
        strOrdMag = "1|0 = " + Math.pow(10, ordMag) + "\n\n";

        txtArea = new TextArea();
        txtArea.heightProperty().addListener(ov-> {doTheGraph();});
        txtArea.widthProperty().addListener(ov-> {doTheGraph();});
        
        bbsl_Title1 = "Back to Back Stem & Leaf Plot\n";
        bbsl_Title2_1 = strRadioButtonDescriptions[0];
        bbsl_Title2_2 = strRadioButtonDescriptions[1];
        bbsl_Title2_5 = strRadioButtonDescriptions[2];
        
        formatTheTitleInfo();        
        doOneLiners();
        txtArea = txtArea_BBSL_1;         
        containingPane = new Pane();
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();    
        theSLAnchorPane = dragableAnchorPane.getTheAP(); 
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(radioButtonRow, titleText_x, txtArea);        
        dragableAnchorPane.makeDragable();  
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);        
        doTheGraph();
    }
    
    public void makeTheButtonBox() { 
        // Determine which graphs are initially shown
        // Here none are shown, by design; let the user choose
        radioButtonRow = new HBox(10);
        spacers = new Region[nSpacers];
        radioButtonSettings = new boolean[nRadioButtons];
        bbsl_RadioButtons = new RadioButton[nRadioButtons];
    
        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < nRadioButtons; i++) {
            bbsl_RadioButtons[i] = new RadioButton(strRadioButtonDescriptions[i]);
            group.getToggles().add(bbsl_RadioButtons[i]);
            bbsl_RadioButtons[i].setMaxWidth(Double.MAX_VALUE);
            bbsl_RadioButtons[i].setId(strRadioButtonDescriptions[i]);
            bbsl_RadioButtons[i].setSelected(radioButtonSettings[i]);
            bbsl_RadioButtons[i].setTextFill(Color.BLUE);
            bbsl_RadioButtons[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            bbsl_RadioButtons[i].setOnAction(e->{
                RadioButton tb = ((RadioButton) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                for (int ithID = 0; ithID < nRadioButtons; ithID++) {
                    
                    if (daID.equals(strRadioButtonDescriptions[ithID])) {
                        radioButtonSettings[ithID] = (checkValue == true);
                        ithIDChosen = ithID;
                        linePerStemChoice = ithID;
                        switch(linePerStemChoice) {
                            case 0:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doOneLiners();
                                txtArea = txtArea_BBSL_1;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea); 
                            break;
                            
                            case 1:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doTwoLiners();
                                txtArea = txtArea_BBSL_2;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea);                                
                            break;
                            
                            case 2:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doFiveLiners();
                                txtArea = txtArea_BBSL_5;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea); 
                            break;
                            
                            default:
                                String switchFailure = "Switch failure: BBSL_View 234 " + linePerStemChoice;
                                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
                                return;
                        }
                        doTheGraph();
                    }
                }
            }); //  end setOnAction
        }  
        
        for (int ithSpacer = 0; ithSpacer < nSpacers; ithSpacer++) {
            spacers[ithSpacer] = new Region();
            spacers[ithSpacer].setPrefSize(2, 2);
            HBox.setHgrow(spacers[ithSpacer], Priority.ALWAYS);
        }    
            
        for (int ithBtn = 0; ithBtn < nRadioButtons; ithBtn++) {
            radioButtonSettings[ithBtn] = false;
            HBox.setMargin(bbsl_RadioButtons[ithBtn], new Insets(10));
        }
        
        for (int ithPair = 0; ithPair < nSpacers; ithPair++) {
            radioButtonRow.getChildren().addAll(bbsl_RadioButtons[ithPair], spacers[ithPair]);
        }
        
        radioButtonRow.getChildren().add(bbsl_RadioButtons[nRadioButtons - 1]);            
        bbsl_RadioButtons[0].setSelected(true);
    }
    
    public void doTheGraph() {
        double paneWidth = initWidth;
        double titleWidth = titleText_x.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(radioButtonRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(radioButtonRow, 0.05 * tempWidth);
        AnchorPane.setRightAnchor(radioButtonRow, 0.05 * tempWidth);
        AnchorPane.setBottomAnchor(radioButtonRow, 0.925 * tempHeight);
        
        AnchorPane.setTopAnchor(titleText_x, 0.075 * initHeight);        
        AnchorPane.setLeftAnchor(titleText_x, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(titleText_x, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(titleText_x, 0.8 * initHeight);
        
        AnchorPane.setTopAnchor(txtArea, 0.20 * initHeight);
        AnchorPane.setLeftAnchor(txtArea, 0.0 * initWidth);
        AnchorPane.setRightAnchor(txtArea, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(txtArea,0.0 * initHeight);    
    }
    
    private void doOneLiners() {
        // ****************************   OneLiners   ********************************************         
        txtArea_BBSL_1 = new TextArea();  // Where text will be drawn
        txtArea_BBSL_1.setWrapText(false);
        txtArea_BBSL_1.setEditable(false);
        txtArea_BBSL_1.setPrefColumnCount(50);
        txtArea_BBSL_1.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        
        // Title area
        titleText_x = new Text(20, 20, bbsl_Title1 + bbsl_Title2_1);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
        bbslTitleLines = strBBSL_Title_1 + "\n" +
                         strBBSL_VarName_1 + "\n" +
                         strBBSL_NSize_1 + "\n" +
                         strBBSL_OrdMag_1;
        
        txtArea_BBSL_1.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        txtArea_BBSL_1.setOnMouseClicked(daRightClickMouseHandler);
        strThisLine = bbslTitleLines;
        txtArea_BBSL_1.appendText(strThisLine);
        oneLineStrings2Print.add(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);

        for (int printLines = 0; printLines < n_OneLineToPrint; printLines++) {
            tempString = oneLineBBSL.get(n_OneLineToPrint - printLines - 1);
            strThisLine = tempString + "\n";
            oneLineStrings2Print.add(strThisLine);
            txtArea_BBSL_1.appendText(strThisLine);
        }  
    }
    
    private void doTwoLiners() {
            // ****************************   TwoLiners   ********************************************
        txtArea_BBSL_2 = new TextArea();  // Where text will be drawn
        txtArea_BBSL_2.setWrapText(false);
        txtArea_BBSL_2.setEditable(false);
        txtArea_BBSL_2.setPrefColumnCount(50);
        txtArea_BBSL_2.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 

        // Title area
        titleText_x = new Text(20, 20, bbsl_Title1 + bbsl_Title2_2);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
          
        bbslTitleLines = strBBSL_Title_2 + "\n" +
                         strBBSL_VarName_2 + "\n" +
                         strBBSL_NSize_2 + "\n" +
                         strBBSL_OrdMag_2;
        
        txtArea_BBSL_2.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        txtArea_BBSL_2.setOnMouseClicked(daRightClickMouseHandler);
        strThisLine = bbslTitleLines;
        txtArea_BBSL_2.appendText(strThisLine);
        twoLinesStrings2Print.add(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);

        for (int printLines = 0; printLines < n_TwoLinesToPrint; printLines++) {
            tempString = twoLineBBSL.get(n_TwoLinesToPrint - printLines - 1);
            strThisLine = tempString + "\n";
            twoLinesStrings2Print.add(strThisLine);
            txtArea_BBSL_2.appendText(strThisLine);
        }      
    }
    
    private void doFiveLiners() {
        // ****************************   FiveLiners   ********************************************
        txtArea_BBSL_5 = new TextArea();  // Where text will be drawn
        txtArea_BBSL_5.setWrapText(false);
        txtArea_BBSL_5.setEditable(false);
        txtArea_BBSL_5.setPrefColumnCount(50);
        txtArea_BBSL_5.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12));
        
        // Title area
        titleText_x = new Text(20, 20, bbsl_Title1 + bbsl_Title2_5);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));       
        
        bbslTitleLines = strBBSL_Title_5 + "\n" +
                         strBBSL_VarName_5 + "\n" +
                         strBBSL_NSize_5 + "\n" +
                         strBBSL_OrdMag_5;
        
        txtArea_BBSL_5.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        txtArea_BBSL_5.setOnMouseClicked(daRightClickMouseHandler);
        strThisLine = bbslTitleLines;
        txtArea_BBSL_5.appendText(strThisLine);
        fiveLinesStrings2Print.add(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);

        for (int printLines = 0; printLines < n_FiveLinesToPrint; printLines++) {
            tempString = fiveLineBBSL.get(n_FiveLinesToPrint - printLines - 1);
            strThisLine = tempString + "\n";
            fiveLinesStrings2Print.add(strThisLine);
            txtArea_BBSL_5.appendText(strThisLine);
        }      
    }
    
    private void formatTheTitleInfo() {
        // The big title at the top?
        titleText = new Text(10, 20, bbsl_Title);
        titleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,16)); 
        strVarName = bbslTitle2;
        
        titleLeftNSize = bbsl_Model.getAllUDMs().get(0).getLegalN();
        titleRightNSize = bbsl_Model.getAllUDMs().get(1).getLegalN();
        strSLNSize = "N1 = " + String.valueOf(titleLeftNSize) + "    "
                          + "N2 = " + String.valueOf(titleRightNSize);

        // Determine how far into the strings is the first vertical
        charsInToVertChar_1 = oneLineBBSL.get(0).indexOf('|');
        charsInToVertChar_2 = twoLineBBSL.get(0).indexOf('|');
        charsInToVertChar_5 = fiveLineBBSL.get(0).indexOf('|');
        
        strBBSL_Title_1 = prePendBlanks(charsInToVertChar_1, bbsl_Title);
        strBBSL_Title_2 = prePendBlanks(charsInToVertChar_2, bbsl_Title);
        strBBSL_Title_5 = prePendBlanks(charsInToVertChar_5, bbsl_Title);

        strBBSL_VarName_1 = prePendBlanks(charsInToVertChar_1, strVarName);
        strBBSL_VarName_2 = prePendBlanks(charsInToVertChar_2, strVarName);
        strBBSL_VarName_5 = prePendBlanks(charsInToVertChar_5, strVarName);
        
        strBBSL_NSize_1 = prePendBlanks(charsInToVertChar_1, strSLNSize);
        strBBSL_NSize_2 = prePendBlanks(charsInToVertChar_2, strSLNSize);        
        strBBSL_NSize_5 = prePendBlanks(charsInToVertChar_5, strSLNSize);
        
        strBBSL_OrdMag_1 = prePendBlanks(charsInToVertChar_1, strOrdMag);
        strBBSL_OrdMag_2 = prePendBlanks(charsInToVertChar_2, strOrdMag);
        strBBSL_OrdMag_5 = prePendBlanks(charsInToVertChar_5, strOrdMag);        
    }
    
    public String getSLNSize() { return strSLNSize; }
    
    private String prePendBlanks(int positionOfVertBar, String strToPrePendTo) {
        String prePendedString;
        int strLen = strToPrePendTo.length();
        int halfLen = strLen / 2;
        // +3 is a fudge term (kinda like a fudge factor...)
        int nBlanksToAdd = positionOfVertBar - halfLen + 3;
        
        if (nBlanksToAdd > 0) {
            prePendedString = StringUtilities.getStringOfNSpaces(nBlanksToAdd) + strToPrePendTo;
        } else {
            prePendedString = strToPrePendTo;
        }
        return prePendedString;
    }
    
    public ArrayList<String> getTheDesiredBBSL() { 
        switch (ithIDChosen) {
            case 0:
                return oneLineStrings2Print;
            //break;

            case 1:
               return twoLinesStrings2Print;
            //break;
            
            case 2:
                return fiveLinesStrings2Print;
            //break;
            
            default:
                String switchFailure = "Switch failure: BBSL_View 447 " + ithIDChosen;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
        }
        return oneLineStrings2Print; 
    } //  Happy compiler, happy programmer
    
    public EventHandler<MouseEvent> daRightClickMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent)  {
            /*
            BBSL_PrintText_View bbslpv = 
                    new BBSL_PrintText_View(bbsl_View, independent_t_Dashboard,
                                     initHoriz, initVert,
                                     initWidth, initHeight);
                    bbslpv.completeTheDeal();
                    bbslpvContainingPane = bbslpv.getTheContainingPane(); 
                    bbslpvContainingPane.setStyle(containingPaneStyle);
                    bbslpvContainingPane.setVisible(true);
                    bbslpv.triggerTheZoomieThing();
            */
        }
    }; 
    
    public Pane getTheContainingPane() { return containingPane; }
}


 

