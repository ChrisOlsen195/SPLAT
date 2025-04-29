/************************************************************
 *                  TwoProp_Inference_Dialog                *
 *                          01/15/25                        *
 *                            18:00                         *
 ***********************************************************/
package dialogs.t_and_z;

import dialogs.Splat_Dialog;
import utilityClasses.MyAlerts;
import utilityClasses.DataUtilities;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import smarttextfield.*;

public class TwoProp_Inference_Dialog extends Splat_Dialog { 
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean valuesLeftBlank;
    boolean bool_Prop1Good, bool_Prop2Good, bool_Succ1Good, bool_Succ2Good, 
            bool_N1Good, bool_N2Good; //, bool_AlphaGood;
    boolean allFieldsGood;
    
    int succ1, succ2, n1, n2, alphaIndex, ciIndex;
    
    double prop1, prop2, significanceLevel, /*confidenceLevel,*/ daNullDiff;
    double hypothesizedDifference;
    double[] theAlphaLevs; //, theCILevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, strAltHypChosen,  
           str_Group1_Title, str_Group1_SumInfo, str_OROne, str_Group1_N,
           str_Group2_Title, str_Group2_SumInfo, str_ORTwo, str_Group2_N;
    
    final String toBlank = "";
    
    // My classes
    SmartTextFieldsController stf_Controller;
    SmartTextFieldDoublyLinkedSTF al_STF;
    
    // JavaFX POJOs
    Button btn_ChangeNull, btn_Reset;
    
    GridPane gridChoicesMade;
    HBox hBox_MiddlePanel, hBox_BottomPanel, hBox_GPOne_SuccessRow, hBox_Group2_SuccessRow,
         hBox_AlphaAndCI;
    VBox root, vBox_NullsPanel, vBox_NumValsPanel, vBox_Group_1, vBox_Group_Prop_2,
         vBox_CI, vBox_Alpha, vBox_InfChoicesPanel, vBox_VarsPanel; 

    Label lblNullAndAlt, lbl_Title, ciLabel, alphaLabel, lblProp_1, 
          lblProp_2, lblTitle;
    RadioButton rb_HypNE, rb_HypLT, rb_HypGT; // , rb_HypNull;
    Scene scene;
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndVars,
              sep_Prop1_and_Prop2, sep_Alpha, sep, sep_VarsAndBottom;  
    
    Text txt_Group1_Title, txt_Group1_SumInfo, txt_OROne, txt_Group1_N,
         txt_Group2_Title, txt_Group2_SumInfo, txt_ORTwo, txt_Group2_N;
    
    TextField tf_Prop_1, tf_Prop_2, tf_Title;

    ObservableList<String> ciLevels, alphaLevels;
    ListView<String> ciView, alphaView;

    public TwoProp_Inference_Dialog() {
        if (printTheStuff == true) {
            System.out.println("88 *** TwoProp_Inference_Dialog, Constructing");
        }
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        // theCILevs = new double[] {0.90, 0.95, 0.99};
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);

        root = new VBox();
        root.setAlignment(Pos.CENTER);

        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set
        stf_Controller.setSize(6);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        
        lbl_Title = new Label("Inference for two independent proportions");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));        
        lbl_Title.getStyleClass().add("dialogTitle");
        
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));
        sep_NullsFromInf = new Separator();
        sep_NullsFromInf.setOrientation(Orientation.VERTICAL);
        sep_InfFromNumbers = new Separator();
        sep_InfFromNumbers.setOrientation(Orientation.VERTICAL);
        sep_MiddleAndVars = new Separator();
        sep_Prop1_and_Prop2 = new Separator();
        sep_VarsAndBottom = new Separator();
        sep_Alpha = new Separator();
        sep_Alpha.setMinHeight(10);

        makeInfDecisionsPanel();
        makeNullsPanel();
        makeNumericValuesPanel();
        makeVariableDefPanel();
        makeBottomPanel();
        
        hBox_MiddlePanel = new HBox();
        hBox_MiddlePanel.setSpacing(30);
        hBox_MiddlePanel.getChildren().addAll(vBox_NullsPanel, sep_NullsFromInf,
                                         vBox_InfChoicesPanel,sep_InfFromNumbers,
                                         vBox_NumValsPanel);
        hBox_MiddlePanel.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(lbl_Title, 
                                  sep_NullsFromInf,
                                  hBox_MiddlePanel,
                                  sep_MiddleAndVars,
                                  vBox_VarsPanel,
                                  sep_VarsAndBottom,
                                  hBox_BottomPanel);        
        
        scene = new Scene (root, 725, 475);
        setTitle("Inference for two independent proportions");
        setScene(scene);
    }  
    
    private void makeNullsPanel() { 
        //System.out.println("142, TwoPropInfDialog, makeNullsPanel()");
        vBox_NullsPanel = new VBox();

        strAltHypChosen = "NotEqual";
        btn_ChangeNull = new Button("Change null difference");
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        //             Props
        strHypNull = "p\u2081 - p\u2082 = 0";
        strHypNE = "p\u2081 - p\u2082 \u2260 0";
        strHypLT = "p\u2081 - p\u2082 < 0";
        strHypGT = "p\u2081 - p\u2082 > 0";
        
        rb_HypNE = new RadioButton(strHypNull + "\n" + strHypNE);
        rb_HypLT = new RadioButton(strHypNull + "\n" + strHypLT);
        rb_HypGT = new RadioButton(strHypNull + "\n" + strHypGT);
        
        // top, right, bottom, left
        rb_HypNE.setPadding(new Insets(10, 10, 10, 10));
        rb_HypLT.setPadding(new Insets(10, 10, 10, 10));
        rb_HypGT.setPadding(new Insets(10, 10, 10, 10));
        
        rb_HypNE.setSelected(true);
        rb_HypLT.setSelected(false);
        rb_HypGT.setSelected(false);
 
        vBox_NullsPanel.getChildren().addAll(lblNullAndAlt, 
                                       rb_HypNE, 
                                       rb_HypLT, 
                                       rb_HypGT);
        
        rb_HypNE.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            rb_HypNE.setSelected(true);
            rb_HypLT.setSelected(false);
            rb_HypGT.setSelected(false);
            strAltHypChosen = "NotEqual";
        });
            
        rb_HypLT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            rb_HypNE.setSelected(false);
            rb_HypLT.setSelected(true);
            rb_HypGT.setSelected(false);
            strAltHypChosen = "LessThan";
        });
            
        rb_HypGT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            rb_HypNE.setSelected(false);
            rb_HypLT.setSelected(false);
            rb_HypGT.setSelected(true);
            strAltHypChosen = "GreaterThan";
        });
        
        btn_ChangeNull.setOnAction((ActionEvent event) -> {
            Alert cantDoNull = new Alert(Alert.AlertType.INFORMATION);
            cantDoNull.setTitle("Just so you know...");
            cantDoNull.setHeaderText("I can't actually 'test' this hypothesis");
            cantDoNull.setContentText("Ok, so here's the deal.  Given the usual information about two "
                        + "\nproportions, the standard error of the sampling distribution is"
                        + "\nnot uniquely determined.  The best that can be done -- which, of"
                        + "\ncourse what I, SPLAT, will do -- is find a confidence interval for"
                        + "\nthe difference in proportions.");
            cantDoNull.showAndWait();
        });
    }
 
    private void makeNumericValuesPanel() {
        //System.out.println("218, TwoPropInfDialog, makeNumericValuesPanel()");
        vBox_NumValsPanel = new VBox();
        vBox_Group_1 = new VBox();
        vBox_Group_1.setAlignment(Pos.CENTER);
        vBox_Group_1.setPadding(new Insets(5, 5, 5, 5));
        str_Group1_Title = "Treatment / Population #1";
        txt_Group1_Title = new Text(str_Group1_Title);
    
        str_Group1_SumInfo = "   Summary Information";
        txt_Group1_SumInfo = new Text(str_Group1_SumInfo);
        
        str_OROne = "  Prop #1    OR   Count #1";
        txt_OROne = new Text(str_OROne);    
        
        hBox_GPOne_SuccessRow = new HBox();
        
        //stf_Prop1 = new SmartTextField(propNStuffHandler, 6, 2);
        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("Prop1");
        
        al_STF.get(0).getTextField().setOnAction(e -> { 
            bool_Prop1Good = checkAndHandlelProportionEntered(1, al_STF.get(0));
        });
       
        //stf_Succ1 = new SmartTextField(propNStuffHandler, 0, 2);
        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(toBlank); 
        al_STF.get(1).getTextField().setId("Successes1");
        al_STF.get(1).getTextField().setOnAction(e -> {
            checkAndHandleSuccessesEntered(1, al_STF.get(1));
        });
        hBox_GPOne_SuccessRow.setAlignment(Pos.CENTER);
        hBox_GPOne_SuccessRow.getChildren()
                             .addAll(al_STF.get(0).getTextField(),
                                     al_STF.get(1).getTextField());
        hBox_GPOne_SuccessRow.setSpacing(25);

        str_Group1_N = "   Group / Sample Size #1";
        txt_Group1_N = new Text(str_Group1_N);   
        
        //stf_N1 = new SmartTextField(propNStuffHandler, 1, 3);
        al_STF.get(2).getTextField().setPrefColumnCount(12);
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(2).getTextField().setText(toBlank);
        al_STF.get(2).getTextField().setId("SampleSize1");

        al_STF.get(2).getTextField().setOnAction(e -> {
            bool_N1Good = checkAndHandleSampleSizeEntered(1, al_STF.get(2));
        });
        
        vBox_Group_1.getChildren().addAll(txt_Group1_Title,
                                         txt_Group1_SumInfo,
                                         txt_OROne,
                                         hBox_GPOne_SuccessRow,
                                         txt_Group1_N,
                                         al_STF.get(2).getTextField());
        
        vBox_Group_Prop_2 = new VBox();
        vBox_Group_Prop_2.setAlignment(Pos.CENTER);
        vBox_Group_Prop_2.setPadding(new Insets(5, 5, 5, 5));
        str_Group2_Title = "Treatment / Population #2";
        txt_Group2_Title = new Text(str_Group2_Title);

        str_Group2_SumInfo = "   Summary Information";
        txt_Group2_SumInfo = new Text(str_Group2_SumInfo);
        
        str_ORTwo = "  Prop #2    OR   Count #2";
        txt_ORTwo = new Text(str_ORTwo);

        hBox_Group2_SuccessRow = new HBox();

        al_STF.get(3).getTextField().setPrefColumnCount(12);
        al_STF.get(3).getTextField().setMaxWidth(65);
        al_STF.get(3).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(3).getTextField().setText(toBlank);
        al_STF.get(3).getTextField().setId("Prop2"); 
        
        al_STF.get(3).getTextField().setOnAction(e -> {
            bool_Prop2Good = checkAndHandlelProportionEntered(2, al_STF.get(3));
        });

        al_STF.get(4).getTextField().setPrefColumnCount(8);
        al_STF.get(4).getTextField().setMaxWidth(50);
        al_STF.get(4).getTextField().setText(toBlank);    
        al_STF.get(4).getTextField().setId("Successes2");
        
        al_STF.get(4).getTextField().setOnAction(e -> {
            bool_Succ2Good = checkAndHandleSuccessesEntered(2, al_STF.get(4));
        });
        
        hBox_Group2_SuccessRow.setAlignment(Pos.CENTER);
        hBox_Group2_SuccessRow.getChildren()
                             .addAll(al_STF.get(3).getTextField(),
                                     txt_ORTwo,
                                     al_STF.get(4).getTextField());
        hBox_Group2_SuccessRow.setSpacing(25);
        str_Group2_N = "   Group / Sample Size #2";
        txt_Group2_N = new Text(str_Group2_N);  
        
        al_STF.get(5).getTextField().setPrefColumnCount(8);
        al_STF.get(5).getTextField().setMaxWidth(50);
        al_STF.get(5).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(5).getTextField().setText(toBlank);    
        al_STF.get(5).getTextField().setId("SampleSize2");
        
        al_STF.get(5).getTextField().setOnAction(e -> {
            bool_N2Good = checkAndHandleSampleSizeEntered(2, al_STF.get(5));
        });    
        
        al_STF.get(0).getTextField().requestFocus();
        
        vBox_Group_Prop_2.getChildren().addAll(txt_Group2_Title,
                                         txt_Group2_SumInfo,
                                         txt_ORTwo,
                                         hBox_Group2_SuccessRow,
                                         txt_Group2_N,
                                         al_STF.get(5).getTextField());      
        
        vBox_NumValsPanel.getChildren()
                  .addAll(vBox_Group_1, 
                          sep_Prop1_and_Prop2,
                          vBox_Group_Prop_2);
    }
    
    private void makeInfDecisionsPanel() {
        //System.out.println("348, TwoPropInfDialog, makeInfDecisionsPanel()");
        hypothesizedDifference = 0.;
        daNullDiff = 0.0;
       
        ciLabel = new Label("   Select conf level");
        ciLabel.setMaxWidth(120);
        ciLabel.setMinWidth(120);
        ciLevels = FXCollections.<String>observableArrayList("          90%", "          95%", "          99%");
        ciView = new ListView<>(ciLevels);
        ciView.setOrientation(Orientation.VERTICAL);
        ciView.setPrefSize(120, 100);
        
        ciView.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       ciChanged(ov, oldvalue, newvalue);
                  }
              }));
 
        alphaLabel = new Label("   Select alpha level");
        alphaLabel.setMaxWidth(120);
        alphaLabel.setMinWidth(120);
        alphaLevels = FXCollections.<String>observableArrayList("          0.10", "          0.05", "          0.01");
        alphaView = new ListView<>(alphaLevels);
        alphaView.setOrientation(Orientation.VERTICAL);
        alphaView.setPrefSize(120, 100);        
        alphaView.getSelectionModel()
                 .selectedItemProperty()
                 .addListener((new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                    final String oldvalue, final String newvalue) {
                       alphaChanged(ov, oldvalue, newvalue);
                  }
            }));
        
        alphaView.getSelectionModel().select(1);    //  Set at .05
        ciView.getSelectionModel().select(1);   //  Set at 95%
        vBox_CI = new VBox();
        
        vBox_CI.getChildren().addAll(ciLabel, ciView);
        vBox_Alpha = new VBox();
        vBox_Alpha.getChildren().addAll(alphaLabel, alphaView);

        hBox_AlphaAndCI = new HBox();
        hBox_AlphaAndCI.getChildren().addAll(vBox_Alpha, sep, vBox_CI);

        hBox_AlphaAndCI = new HBox();
        hBox_AlphaAndCI.setPadding(new Insets(10, 5, 5, 5));
        hBox_AlphaAndCI.getChildren().addAll(vBox_Alpha, sep, vBox_CI);  
        
        vBox_InfChoicesPanel = new VBox();
        vBox_InfChoicesPanel.setAlignment(Pos.CENTER);
        vBox_InfChoicesPanel.getChildren().add(hBox_AlphaAndCI);           
    }
    
    private void makeVariableDefPanel() {
        //System.out.println("406, TwoPropInfDialog, makeVariableDefPanel()");
        lblProp_1 = new Label(" Prop 1 Label: ");
        lblProp_2 = new Label(" Prop 2 Label: ");
        lblTitle  = new Label("        Title: ");

        tf_Prop_1 = new TextField("First prop");
        tf_Prop_2 = new TextField("Second prop");
        tf_Title  = new TextField("Title ");
        
        tf_Prop_1.setPrefColumnCount(15);
        tf_Prop_2.setPrefColumnCount(15);
        tf_Title.setPrefColumnCount(15);
        
        tf_Prop_1.textProperty().addListener(this::changeProp_1_Description);
        tf_Prop_2.textProperty().addListener(this::changeProp_2_Description);
        tf_Title.textProperty().addListener(this::changeTitle_Description);

        gridChoicesMade = new GridPane();
        gridChoicesMade.setHgap(10);
        gridChoicesMade.setVgap(15);        
        gridChoicesMade.add(lblProp_1, 0, 0);
        gridChoicesMade.add(lblProp_2, 0, 1);        
        gridChoicesMade.add(tf_Prop_1, 1, 0);
        gridChoicesMade.add(tf_Prop_2, 1, 1);        
        gridChoicesMade.add(lblTitle, 0, 2);
        gridChoicesMade.add(tf_Title, 1, 2);
        
        GridPane.setValignment(tf_Prop_1, VPos.BOTTOM);
        GridPane.setValignment(tf_Prop_2, VPos.BOTTOM);
        gridChoicesMade.setPadding(new Insets(0, 10, 0, 0));
        
        vBox_VarsPanel = new VBox(10);
        vBox_VarsPanel.setAlignment(Pos.CENTER_LEFT);
        vBox_VarsPanel.setPadding(new Insets(0, 25, 0, 10));
        vBox_VarsPanel.getChildren().add(gridChoicesMade);            
    }
    
    private void makeBottomPanel() { 
        //System.out.println("444, TwoPropInfDialog, makeBottomPanel()");
        hBox_BottomPanel = new HBox(10);
        hBox_BottomPanel.setAlignment(Pos.CENTER);
        hBox_BottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        btnOK = new Button("Compute");
        btnCancel = new Button("Cancel");
        btn_Reset = new Button("Reset");
        
        btnOK.setOnAction((ActionEvent event) -> { 
        boolGoodToGo = checkForMissing();
        
        if (boolGoodToGo) { boolGoodToGo = checkLegal(); }

        if (boolGoodToGo) {
            strReturnStatus = "OK";
            close();
        } 
    });
        
        setOnCloseRequest((WindowEvent t) -> {
            strReturnStatus = "WindowClosed";
            close();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });

        btn_Reset.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(toBlank); al_STF.get(3).setText(toBlank);
            al_STF.get(1).setText(toBlank); al_STF.get(4).setText(toBlank);
            al_STF.get(3).setText(toBlank); al_STF.get(5).setText(toBlank);
            
            bool_Prop1Good = false; bool_Prop2Good = false; 
            bool_Succ1Good = false; bool_Succ2Good = false; 
            bool_N1Good = false; bool_N2Good = false;
        });
        
        hBox_BottomPanel.getChildren().addAll(btnOK, btnCancel, btn_Reset);
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = ciView.getSelectionModel().getSelectedIndex();
        alphaView.getSelectionModel().select(ciIndex);
        significanceLevel = theAlphaLevs[ciIndex];
        //confidenceLevel = theCILevs[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = alphaView.getSelectionModel().getSelectedIndex();
        ciView.getSelectionModel().select(alphaIndex);
        significanceLevel = theAlphaLevs[alphaIndex];
        //confidenceLevel = theCILevs[alphaIndex];    
    }
    
    public void changeProp_1_Description(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tf_Prop_1.setText(newValue); 
        //System.out.println("509, TwoPropInfDialog");
    }

    public void changeProp_2_Description(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tf_Prop_2.setText(newValue); 
        //System.out.println("516, TwoPropInfDialog");
    }  
    
    public void changeTitle_Description(ObservableValue<? extends String> title,
        String oldValue,
        String newValue) {
        tf_Title.setText(newValue); 
        //System.out.println("523, TwoPropInfDialog");
    } 
    
    
    // The evaluations here will be specific to the dialog
    private boolean checkForMissing() {
        //System.out.println("529, TwoPropInfDialog, checkForMissing()");
        boolGoodToGo = true;
        valuesLeftBlank = false;
        
        for (int ithSTF = 0; ithSTF < 6; ithSTF++) {
            if (al_STF.get(ithSTF).isEmpty()) {
                valuesLeftBlank = true;
            }
        }  
        
        if (valuesLeftBlank) {
           MyAlerts.showMissingDataAlert();
           boolGoodToGo = false;
        }
        return boolGoodToGo;
    }
        
    private boolean checkLegal() {
        //System.out.println("547, TwoPropInfDialog, checkLegal()");
        boolGoodToGo = true;
        bool_Prop1Good = DataUtilities.txtFieldHasProp(al_STF.get(0).getTextField());
        bool_Prop2Good = DataUtilities.txtFieldHasProp(al_STF.get(3).getTextField());
        
        if ((!bool_Prop1Good) || (!bool_Prop2Good)) {
            MyAlerts.showGenericBadNumberAlert(" a legal proportion");
            boolGoodToGo = false;
            strReturnStatus = "BadProportion";
        }
        
        //System.out.println("558, TwoPropInfDialog, checkLegal()");
        
        if (boolGoodToGo) {  
            boolean choices_1_good = (bool_Succ1Good || bool_Prop1Good) && bool_N1Good;
            boolean choices_2_good = (bool_Succ2Good || bool_Prop2Good) && bool_N2Good;
            allFieldsGood = choices_1_good && choices_2_good;
            
            if (!allFieldsGood) {
                //System.out.println("566, TwoPropInfDialog, checkLegal()");
                MyAlerts.showGenericBadNumberAlert(" a positive integer ");
                boolGoodToGo = false;
                strReturnStatus = "BadCountField";
            }
        }
        //System.out.println("572, TwoPropInfDialog, checkLegal()");
        return boolGoodToGo;
    }

    /*************************************************************************
    *    Check a proportion entry for legal (0 < p < 1) and update all other *
    *    values.  If both sample size and nSuccesses are given, change the   *
    *    number of successes.                                                *
    *************************************************************************/
    
    public boolean checkAndHandlelProportionEntered(int theProp, SmartTextField theSTF) {
        boolean propIsGood = false; 
        int whichProp = theProp;
        double goodProp;
        
        propIsGood = DataUtilities.txtFieldHasProp(theSTF.getTextField());
        //System.out.println("588 TwoProp_Inf_Dialog, propIsGood = " + propIsGood);
        //  If it really was a number, check for Prop
        if (!propIsGood) {
             MyAlerts.showGenericBadNumberAlert(" a bad fraction or decimal ");
             theSTF.setText("");
        }    
        else {
            //goodProp = Double.valueOf(theSTF.getText());
            goodProp = Double.parseDouble(theSTF.getText());
            //System.out.println("597 TwoProp_Inf_Dialog, goodProp = " + goodProp);
            
            if (whichProp == 1) {
                prop1 = goodProp;
                bool_Prop1Good = true;
                bool_N1Good = DataUtilities.strIsAPosInt(al_STF.get(2).getText());
                bool_Succ1Good = DataUtilities.strIsAPosInt(al_STF.get(1).getText());
                
                if (bool_N1Good) {
                    succ1 = (int)Math.floor(prop1 * n1 + 0.5);
                    al_STF.get(1).setText(String.valueOf(succ1));
                } else if (bool_Succ1Good) {    //  and N1 not
                    n1 = (int)Math.floor(succ1 / prop1 + 0.5);
                    al_STF.get(2).setText(String.valueOf(n1));                    
                }
            } else if (whichProp == 2) {
                prop2 = goodProp;
                //System.out.println("614 TwoProp_Inf_Dialog, goodProp = " + goodProp);
                bool_Prop2Good = true;
                bool_N2Good = DataUtilities.strIsAPosInt(al_STF.get(5).getText());
                bool_Succ2Good = DataUtilities.strIsAPosInt(al_STF.get(4).getText());
                
                if (bool_N2Good) {
                    succ2 = (int)Math.floor(prop2 * n2 + 0.5);
                    al_STF.get(4).setText(String.valueOf(succ2));
                } else if (bool_Succ2Good) {    //  and N2 not
                    n2 = (int)Math.floor(succ2 / prop2 + 0.5);
                    al_STF.get(5).setText(String.valueOf(n2));                    
                }
            }
        }
        return propIsGood;
    }

    /*************************************************************************
    *    Check a success entry for legal (0 < N) and update all other values *
    *    values.  If both proportion and sample size are given, change the   *
    *    sample size.                                                        *
    *************************************************************************/
    
    public boolean checkAndHandleSuccessesEntered(int theSucc, SmartTextField theSTF) {
        // boolean succIsGood = true;
        int goodSucc;
        int whichSucc = theSucc;
        if (theSTF.getText().isEmpty()) {
            return false;
        }
        if (!DataUtilities.strIsAPosInt(theSTF.getText())) {
            MyAlerts.showGenericBadNumberAlert(" a positive integer ");
            theSTF.setText(toBlank);
            return false;
        }

        goodSucc = Integer.parseInt(theSTF.getText());
        if (whichSucc == 1) {
            succ1 = goodSucc;
            bool_Succ1Good = true;
            bool_N1Good = DataUtilities.strIsAPosInt(al_STF.get(2).getText());
            bool_Prop1Good = DataUtilities.strIsAProb(al_STF.get(0).getText());  
            
            if (bool_N1Good) {
                
                if (succ1 >= n1) {
                    MyAlerts.showPropOopsAlert();
                    al_STF.get(0).setText(toBlank);
                    al_STF.get(1).setText(toBlank);
                    al_STF.get(2).setText(toBlank);
                    return false;
                }
                prop1 = succ1 / (double)n1;
                al_STF.get(0).setText(String.valueOf(prop1));
                return true;
            } else if (bool_Prop1Good) {    //  and N1 not
                n1 = (int)Math.floor(succ1 / prop1 + 0.5);
                al_STF.get(2).setText(String.valueOf(n1));  
                return true;
            }
            return false;
        } else {    // whichSucc = 2
            succ2 = goodSucc;
            bool_Succ2Good = true;
            bool_N2Good = DataUtilities.strIsAPosInt(al_STF.get(5).getText());
            bool_Prop2Good = DataUtilities.strIsAProb(al_STF.get(3).getText());  
            
            if (bool_N2Good) {
                
                if (succ2 > n2) {
                    MyAlerts.showPropOopsAlert();
                    al_STF.get(3).setText(toBlank);
                    al_STF.get(4).setText(toBlank);
                    al_STF.get(5).setText(toBlank);
                    return false;
                }
                prop2 = succ2 / (double)n2;
                al_STF.get(3).setText(String.valueOf(prop2));
                return true;
            } else if (bool_Prop2Good) {    //  and N2 not
                n2 = (int)Math.floor(succ2 / prop2 + 0.5);
                al_STF.get(5).setText(String.valueOf(n2));  
                return true;
            }
            return false;            
        }
    }  
    
    /*************************************************************************
    *    Check a sample size entry for legal (0 < N) and update all other    *
    *    values.  If both proportion and Successes are given, change the     *
    *    proportion.                                                         *
    *************************************************************************/
        
    public boolean checkAndHandleSampleSizeEntered(int theSS, SmartTextField theSTF) {
        //boolean nIsGood = true;
        int goodN;
        int whichSS = theSS;
        if (theSTF.getText().isEmpty()) { return false; }
        if (!DataUtilities.strIsAPosInt(theSTF.getText())) {
            MyAlerts.showGenericBadNumberAlert(" a positive integer ");
            theSTF.setText(toBlank);
            return false;
        }

        goodN = Integer.parseInt(theSTF.getText());
        
        if (whichSS == 1) {
            n1 = goodN;
            bool_N1Good = true;
            bool_Succ1Good = DataUtilities.strIsAPosInt(al_STF.get(1).getText());
            bool_Prop1Good = DataUtilities.strIsAProb(al_STF.get(0).getText()); 
            
            if (bool_Succ1Good) {
                if (succ1 >= n1) {
                    MyAlerts.showPropOopsAlert();
                    al_STF.get(0).setText(toBlank);
                    al_STF.get(1).setText(toBlank);
                    al_STF.get(2).setText(toBlank);
                    return false;
                }
                prop1 = succ1 / (double)n1;
                al_STF.get(0).setText(String.valueOf(prop1));
                return true;
            } else if (bool_Prop1Good) {    //  and Succ1 not
                succ1 = (int)Math.floor(n1 * prop1 + 0.5);
                al_STF.get(1).setText(String.valueOf(succ1));  
                return true;
            }
            return false;
        } else {    // whichSucc = 2
            n2 = goodN;
            bool_N2Good = true;
            bool_Succ2Good = DataUtilities.strIsAPosInt(al_STF.get(4).getText());
            bool_Prop2Good = DataUtilities.strIsAProb(al_STF.get(3).getText()); 
            
            if (bool_Succ2Good) {
                
                if (succ2 > n2) {
                    MyAlerts.showPropOopsAlert();
                    al_STF.get(3).setText(toBlank);
                    al_STF.get(4).setText(toBlank);
                    al_STF.get(5).setText(toBlank);
                    return false;
                }
                prop2 = succ2 / (double)n2;
                al_STF.get(3).setText(String.valueOf(prop2));
                return true;
            } else if (bool_Prop2Good) {    //  and Succ2 not
                succ2 = (int)Math.floor(n2 * prop2 + 0.5);
                al_STF.get(4).setText(String.valueOf(succ2));  
                return true;
            }
            return false;            
        }
    }   
    
    public double getLevelOfSignificance() { return significanceLevel; }
    public String getAltHypothesis() { return strAltHypChosen; }    
    public double getHypothesizedDiff() { return hypothesizedDifference; }   
    public int getN1() { return n1; }    
    public int getN2() { return n2; }    
    public double getP1() {return prop1; }
    public double getP2() {return prop2; }    
    public String getP1Label() { return tf_Prop_1.getText(); }
    public String getP2Label() { return tf_Prop_2.getText(); }
    public String getTheTitle() { return tf_Title.getText(); }    
    public int getX1() { return succ1; }   
    public int getX2() { return succ2; }    
    public double getTheNullDiff() { return daNullDiff; }
}

