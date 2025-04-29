/****************************************************************************
 *                      StemNLeaf_View                                      * 
 *                         01/16/25                                         *
 *                          12:00                                           *
 ***************************************************************************/
package proceduresOneUnivariate;

import the_t_procedures.Matched_t_Dashboard;
import the_t_procedures.Single_t_Dashboard;
import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import java.util.Formatter;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import utilityClasses.MyAlerts;

public class StemNLeaf_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean[] radioButtonSettings;    
    
    int nSpacers, ithIDChosen;  
    int n_OneLineToPrint, n_TwoLinesToPrint, n_FiveLinesToPrint;
    int nRadioButtons;
    
    double initHoriz, initVert, initWidth, initHeight;   

    String sl_Title1, sl_Title2_1, sl_Title2_2, sl_Title2_5, slTitleLines;
    String tempString, strThisLine;
    String[] strRadioButtonDescriptions;
    ArrayList<String> oneLineSL, twoLineSL, fiveLineSL; 
    ArrayList<String> preStringsOneLine, preStringsTwoLine, preStringsFiveLine; 
    StemNLeaf_View snl_View;
    
    // POJOs / FX
    HBox radioButtonRow;
    RadioButton[] sl_RadioButtons;   
    Region[] spacers;
    
    Pane slpvContainingPane;
    String containingPaneStyle;
    // My objects
    DragableAnchorPane dragableAnchorPane;
    Exploration_Dashboard explore_Dashboard; 
    StemNLeaf_Model stemNLeaf_Model;

    // FX Objects
    Pane containingPane;   
    TextArea txtArea, txtArea_SL_1, txtArea_SL_2, txtArea_SL_5;
    Text titleText_x, thisText;
    AnchorPane theSLAnchorPane;
   
    public StemNLeaf_View(StemNLeaf_Model stemLeaf_Model, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("70 *** StemNLeaf_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        snl_View = this;
        this.stemNLeaf_Model = stemLeaf_Model;
        ithIDChosen = 0;
        doFurtherInitializations();
    }
    
    public StemNLeaf_View(StemNLeaf_Model stemLeaf_Model, Single_t_Dashboard single_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("84 *** StemNLeaf_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        this.stemNLeaf_Model = stemLeaf_Model;
        ithIDChosen = 0;
        doFurtherInitializations();
    }
    
    public StemNLeaf_View(StemNLeaf_Model stemLeaf_Model, Matched_t_Dashboard matched_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("98 *** StemNLeaf_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.stemNLeaf_Model = stemLeaf_Model;
        ithIDChosen = 0;
        doFurtherInitializations();
    }
    
    private void doFurtherInitializations() {
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

        oneLineSL = new ArrayList<>();
        twoLineSL = new ArrayList<>();
        fiveLineSL = new ArrayList<>();
        
        preStringsOneLine = new ArrayList<>();
        preStringsTwoLine = new ArrayList<>();
        preStringsFiveLine = new ArrayList<>();
        
        oneLineSL = stemNLeaf_Model.get_1_LineSL();
        twoLineSL = stemNLeaf_Model.get_2_LineSL();
        fiveLineSL = stemNLeaf_Model.get_5_LineSL();  
             
        n_OneLineToPrint = oneLineSL.size();
        n_TwoLinesToPrint = twoLineSL.size();
        n_FiveLinesToPrint = fiveLineSL.size();      
    }
    
    public void completeTheDeal() {
        setUpUI();  
        makeTheButtonBox();
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane();
        doTheGraph();
    }
    
    private void setUpUI() {
        int ordMag = stemNLeaf_Model.getOrderOfMagnitude();
        
        Formatter fmt = new Formatter();
        String daFormat = "%-10." + String.valueOf(Math.abs(ordMag)) + "f"; 
        fmt.format(daFormat, Math.pow(10., ordMag));
        String strOrdMag = "1|0 = " + fmt;      
        
        txtArea = new TextArea();
        txtArea.heightProperty().addListener(ov-> {doTheGraph();});
        txtArea.widthProperty().addListener(ov-> {doTheGraph();});
        
        sl_Title1 = "Stem & Leaf Plot\n";
        sl_Title2_1 = strRadioButtonDescriptions[0];
        sl_Title2_2 = strRadioButtonDescriptions[1];
        sl_Title2_5 = strRadioButtonDescriptions[2];
        
        String slVarName = stemNLeaf_Model.getDescriptionOfVariable() + "\n";
        int slNSize = stemNLeaf_Model.getTheQDV().getLegalN();
        String strSLNSize = "N = " + String.valueOf(slNSize) + "    ";
        slTitleLines = sl_Title1 + slVarName + strSLNSize + strOrdMag + "\n\n";
        
        preStringsOneLine.add(sl_Title1);
        preStringsOneLine.add(slVarName);
        preStringsOneLine.add(strSLNSize);
        preStringsOneLine.add(strOrdMag + "\n\n");
        
        preStringsTwoLine.add(sl_Title1);
        preStringsTwoLine.add(slVarName);
        preStringsTwoLine.add(strSLNSize);
        preStringsTwoLine.add(strOrdMag + "\n\n");
        
        preStringsFiveLine.add(sl_Title1);
        preStringsFiveLine.add(slVarName);
        preStringsFiveLine.add(strSLNSize);
        preStringsFiveLine.add(strOrdMag + "\n\n");
        //  One line per stem is the default
        doOneLiners();  
        txtArea = txtArea_SL_1; 
        containingPane = new Pane();
    }
    
    public void doOneLiners() {
        txtArea_SL_1 = new TextArea(); 
        txtArea_SL_1.setWrapText(false);
        txtArea_SL_1.setEditable(false);
        txtArea_SL_1.setPrefColumnCount(50);

        // Title area
        titleText_x = new Text(20, 20, sl_Title1 + sl_Title2_1);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
        // Stem & Leaf proper        
        txtArea_SL_1.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        txtArea_SL_1.setOnMouseClicked(daRightClickMouseHandler);
        strThisLine = slTitleLines;
        txtArea_SL_1.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);
        
        for (int printLines = 0; printLines < n_OneLineToPrint; printLines++) {
            tempString = oneLineSL.get(n_OneLineToPrint - printLines - 1);
            strThisLine = tempString + "\n";
            preStringsOneLine.add(strThisLine);
            thisText = new Text(20, 19 * (printLines + 3) + 40, strThisLine);
            txtArea_SL_1.appendText(strThisLine);
        }  
    }
    
    public void doTwoLiners() {
        txtArea_SL_2 = new TextArea();
        txtArea_SL_2.setWrapText(false);
        txtArea_SL_2.setEditable(false);
        txtArea_SL_2.setPrefColumnCount(50);

        // Title area
        titleText_x = new Text(20, 20, sl_Title1 + sl_Title2_2);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));   

        // Stem & Leaf proper        
        txtArea_SL_2.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        txtArea_SL_2.setOnMouseClicked(daRightClickMouseHandler);
        strThisLine = slTitleLines;
        txtArea_SL_2.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);      

        for (int printLines = 0; printLines < n_TwoLinesToPrint; printLines++) {
            tempString = twoLineSL.get(n_TwoLinesToPrint - printLines - 1);
            strThisLine = tempString + "\n";
            preStringsTwoLine.add(strThisLine);
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            txtArea_SL_2.appendText(strThisLine);
        }  
    }
    
    
    public void doFiveLiners() {
        txtArea_SL_5 = new TextArea(); 
        txtArea_SL_5.setWrapText(false);
        txtArea_SL_5.setEditable(false);
        txtArea_SL_5.setPrefColumnCount(50);

        // Title area
        titleText_x = new Text(20, 20, sl_Title1 + sl_Title2_5);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));  

        // Stem & Leaf proper        
        txtArea_SL_5.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        txtArea_SL_5.setOnMouseClicked(daRightClickMouseHandler);
        strThisLine = slTitleLines;
        txtArea_SL_5.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);      

        for (int printLines = 0; printLines < n_FiveLinesToPrint; printLines++) {
            tempString = fiveLineSL.get(n_FiveLinesToPrint - printLines - 1);
            strThisLine = tempString + "\n";
            preStringsFiveLine.add(strThisLine);
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            txtArea_SL_5.appendText(strThisLine);
        }   
    }
    
    private void setUpAnchorPane() {        
        dragableAnchorPane = new DragableAnchorPane();    
        theSLAnchorPane = dragableAnchorPane.getTheAP();         
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(radioButtonRow, titleText_x, txtArea);        
        dragableAnchorPane.makeDragable();  
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);        
        doTheGraph();
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
    
    public void makeTheButtonBox() { 
        // Determine which graphs are initially shown
        // Here none are shown, by design; let the user choose
        radioButtonRow = new HBox(10);
        
        spacers = new Region[nSpacers];
        radioButtonSettings = new boolean[nRadioButtons];
        sl_RadioButtons = new RadioButton[nRadioButtons];
        
        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < nRadioButtons; i++) {
            sl_RadioButtons[i] = new RadioButton(strRadioButtonDescriptions[i]);
            group.getToggles().add(sl_RadioButtons[i]);
            sl_RadioButtons[i].setMaxWidth(Double.MAX_VALUE);
            sl_RadioButtons[i].setId(strRadioButtonDescriptions[i]);
            sl_RadioButtons[i].setSelected(radioButtonSettings[i]);
            sl_RadioButtons[i].setTextFill(Color.BLUE);
            sl_RadioButtons[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            sl_RadioButtons[i].setOnAction(e->{
                RadioButton tb = ((RadioButton) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                for (int ithID = 0; ithID < nRadioButtons; ithID++) {
                    
                    if (daID.equals(strRadioButtonDescriptions[ithID])) {
                        radioButtonSettings[ithID] = (checkValue == true);
                        ithIDChosen = ithID;
                        
                        switch(ithIDChosen) {
                            case 0:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doOneLiners();
                                txtArea = txtArea_SL_1;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea); 
                            break;
                            
                            case 1:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doTwoLiners();
                                txtArea = txtArea_SL_2;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea);                                
                            break;
                            
                            case 2:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doFiveLiners();
                                txtArea = txtArea_SL_5;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea); 
                            break;
                            
                            default:
                                String switchFailure = "Switch failure: StemNLeaf_View 362 " + ithIDChosen;
                                MyAlerts.showUnexpectedErrorAlert(switchFailure);
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
            HBox.setMargin(sl_RadioButtons[ithBtn], new Insets(10));
        }
        
        for (int ithPair = 0; ithPair < nSpacers; ithPair++) {
            radioButtonRow.getChildren().addAll(sl_RadioButtons[ithPair], spacers[ithPair]);
        }
        
        radioButtonRow.getChildren().add(sl_RadioButtons[nRadioButtons - 1]);            
        sl_RadioButtons[0].setSelected(true);
    }
    
    public ArrayList<String> getTheDesiredSL() { 
        switch (ithIDChosen) {
            case 0:
                return preStringsOneLine;
            //break;

            case 1:
               return preStringsTwoLine;
            //break;
            
            case 2:
                return preStringsFiveLine;
            //break;
            
            default:
                String switchFailure = "Switch failure: StemNLeaf_View 406 " + ithIDChosen;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
            break;
        } 
        
        //  Happy compiler, happy programmer -- should never get here!!
        oneLineSL = new ArrayList<>();
        oneLineSL.add("Unknown fault condition: StemNLeaf_View 413 ");
        return oneLineSL;
    } 
     
    public EventHandler<MouseEvent> daRightClickMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            SL_PrintText_View slpv = 
                    new SL_PrintText_View(snl_View, explore_Dashboard,
                                     initHoriz, initVert,
                                     initWidth, initHeight);
                    slpv.completeTheDeal();
                    slpvContainingPane = slpv.getTheContainingPane(); 
                    slpvContainingPane.setStyle(containingPaneStyle);
                    slpvContainingPane.setVisible(true);
                    slpv.triggerTheZoomieThing();
        }
    }; 
    
    public Pane getTheContainingPane() { return containingPane; }
}


