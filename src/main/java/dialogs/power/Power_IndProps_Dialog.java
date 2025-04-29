/************************************************************
 *                   Power_IndProps_Dialog                  *
 *                          01/15/25                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs.power;

import utilityClasses.DataUtilities;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import smarttextfield.*;
import javafx.stage.WindowEvent;
import utilityClasses.MyAlerts;

public class Power_IndProps_Dialog extends Stage { 
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean okToContinue, showValuesLeftBlankAlert;
    boolean bool_Prop1Good, bool_Prop2Good, valuesLeftBlank,
            bool_N1Good, bool_N2Good, bool_EffectSizeGood;
    boolean allFieldsGood, showNotAllFieldsGoodAlert;
    
    int  n1, n2, alphaIndex, ciIndex, 
         valuesLeftBlankIndex, notAllFieldsGoodIndex; ;

    double alphaLevel, daNullDiff;
    double nullMeanDiff, nullDiffRequested, effectSize;
    double prop_1, prop_2;
    double[] theAlphaLevs;
    
    String str_HypNE, str_HypLT, str_HypGT, str_HypNull, str_NullAndAlt, str_AltHypChosen,  
           str_Group1_Title, str_Group1_SumInfo, str_Prop_1, str_Group1_N,
           str_Group2_Title, str_Group2_SumInfo, str_Prop_2, str_Group2_N,
           str_EffectSize;
    
    String strMean1, strMean2, strN1, strN2, returnStatus;
    final String toBlank = "";
    
    final String wtfString = "What!?!?  My hypothesized null difference of zero is not good"
                                    + "\nenough for you? It is pretty unusual that a non-zero difference"
                                    + "\nwould be hypothesized, but I'm willing to believe you know what"
                                    + "\nyou're doing. No skin off MY nose...";

    Alert notAllFieldsGoodAlert, valuesLeftBlankAlert, 
          altHypPropIncreaseMismatchAlert, altHypPropDecreaseMismatchAlert;
    
    ObservableList<String> strCILevels, strAlphaLevels;
    ListView<String> ciView, alphaView;
    
    // My classes
    SmartTextFieldsController stf_Controller;
    SmartTextFieldDoublyLinkedSTF al_STF;
    
    // JavaFX POJOs
    Button btnChangeNull, btnOK, btnCancel, btnReset;
    RadioButton radBtnHypNE, radBtnHypLT, radBtnHypGT;

    Label lblNullAndAlt, lblTitle, lblCILabel, lblAlphaLabel;
    HBox hBoxMiddlePanel, hBoxBottomPanel, hBox_Grp_1_Prop_Row, hBox_Grp_2_Prop_Row,
         hBoxAlphaAndCI, hBoxCurrDiff;

    VBox root, vBoxNullsPanel, vBoxNumValsPanel, vBox_Grp_Prop_1, vBox_Grp_Prop_2,
         vBoxCIBox, vBoxAlpha, vBoxInfChoicesPanel, vBoxEffectSize; 
    
    final Text txtCurrNullDiff = new Text("Current null diff: (p\u2081 - p\u2082) = ");
    TextField tf_HypDiff;
    TextInputDialog txtInputDialog;
  
    Scene scene;
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndBottom,
              sep_Prop1_and_Prop2, sep_Alpha, sep;  
    
    Text txt_Group1_Title, txt_Group1_SumInfo, txt_Prop_1, txt_Group1_N,
         txt_Group2_Title, txt_Group2_SumInfo, txt_Prop_2, txt_Group2_N,
         txt_EffectSize;
    
    public Power_IndProps_Dialog() {
        if (printTheStuff == true) {
            System.out.println("96 *** Power_IndProps_Dialog, Constructing");
        }
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        returnStatus = "OK";

        root = new VBox();
        root.setAlignment(Pos.CENTER);

        stf_Controller = new SmartTextFieldsController();
        //*    // stf_Controller is empty until size is set                          *
        stf_Controller.setSize(5);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        
        
        lblTitle = new Label("Power for two independent proportions");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        sep_NullsFromInf = new Separator();
        sep_NullsFromInf.setOrientation(Orientation.VERTICAL);
        sep_InfFromNumbers = new Separator();
        sep_InfFromNumbers.setOrientation(Orientation.VERTICAL);
        sep_MiddleAndBottom = new Separator();
        sep_Prop1_and_Prop2 = new Separator();
        sep_Alpha = new Separator();
        sep_Alpha.setMinHeight(10);

        makeInfDecisionsPanel();
        makeNullsPanel();
        makeNumericValuesPanel();
        makeBottomPanel();
        
        hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setSpacing(30);        
        hBoxMiddlePanel.getChildren().addAll(vBoxNullsPanel, sep_NullsFromInf,
                                         vBoxInfChoicesPanel,sep_InfFromNumbers,
                                         vBoxNumValsPanel);
        hBoxMiddlePanel.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(lblTitle, 
                                  sep_NullsFromInf,
                                  hBoxMiddlePanel,
                                  sep_MiddleAndBottom,
                                  hBoxBottomPanel);        
        
        scene = new Scene (root, 750, 400);
        setTitle("Power for a difference in Proportions");
        
        setOnCloseRequest((WindowEvent event) -> {
            returnStatus = "Cancel";
            close();
        });
        
        setScene(scene);
    }  
    
private void makeNullsPanel() {       
        nullMeanDiff = 0.0;
        nullDiffRequested = 0.0;
        str_AltHypChosen = "NotEqual";
        btnChangeNull = new Button("Change null difference");
        str_NullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(str_NullAndAlt);
        
        str_HypNull = "p\u2081 - p\u2082 = 0";
        str_HypNE = "p\u2081 - p\u2082 \u2260 0";
        str_HypLT = "p\u2081 - p\u2082 < 0";
        str_HypGT = "p\u2081 - p\u2082 > 0";
        
        radBtnHypNE = new RadioButton(str_HypNull + "\n" + str_HypNE);
        radBtnHypLT = new RadioButton(str_HypNull + "\n" + str_HypLT);
        radBtnHypGT = new RadioButton(str_HypNull + "\n" + str_HypGT);
        
        // top, right, bottom, left
        radBtnHypNE.setPadding(new Insets(10, 10, 10, 10));
        radBtnHypLT.setPadding(new Insets(10, 10, 10, 10));
        radBtnHypGT.setPadding(new Insets(10, 10, 10, 10));
        
        radBtnHypNE.setSelected(true);
        radBtnHypLT.setSelected(false);
        radBtnHypGT.setSelected(false);

        tf_HypDiff = new TextField("0.0");
        tf_HypDiff.setMinWidth(75);
        tf_HypDiff.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(txtCurrNullDiff, tf_HypDiff);

        vBoxNullsPanel = new VBox();        
        vBoxNullsPanel.getChildren()
                  .addAll(lblNullAndAlt, radBtnHypNE, radBtnHypLT, radBtnHypGT, 
                         btnChangeNull, hBoxCurrDiff);
        
        radBtnHypNE.setOnAction(e->{
            radBtnHypNE.setSelected(true);
            radBtnHypLT.setSelected(false);
            radBtnHypGT.setSelected(false);
            str_AltHypChosen = "NotEqual";

        });
            
        radBtnHypLT.setOnAction(e->{
            radBtnHypNE.setSelected(false);
            radBtnHypLT.setSelected(true);
            radBtnHypGT.setSelected(false);
            str_AltHypChosen = "LessThan";  //  Increase:  p1 < p2
            check_LTGT_Consistency();
        });
            
        radBtnHypGT.setOnAction(e->{
            radBtnHypNE.setSelected(false);
            radBtnHypLT.setSelected(false);
            radBtnHypGT.setSelected(true);
            str_AltHypChosen = "GreaterThan";   //  Decrease: p1 > p2
            check_LTGT_Consistency();
        });
        
        btnChangeNull.setOnAction((ActionEvent event) -> {
            String strResult = "";
            okToContinue = false;
            while (!okToContinue) {
                okToContinue = true;
                txtInputDialog = new TextInputDialog("");
                txtInputDialog.setTitle("Null hypothesis change");
                txtInputDialog.setHeaderText(wtfString);                
                txtInputDialog.setContentText("What difference between means would you like to test? ");                     

                Optional<String> result = txtInputDialog.showAndWait();
                
                if (result.isPresent()) {
                    strResult = result.get();                        
                }
                if (result.isPresent()) {
                    okToContinue = true;
                    try {
                        nullDiffRequested = Double.parseDouble(strResult);
                    }
                    catch (NumberFormatException ex ){ 
                        okToContinue = false;
                        MyAlerts.showGenericBadNumberAlert("a real #");
                        txtInputDialog.setContentText("");
                        okToContinue = false;
                        nullDiffRequested = 0.0;
                    }
                }
                else {
                    nullDiffRequested = 0.0;    // Null returns to 0.0 if Cancel
                }
            }
            nullMeanDiff = nullDiffRequested;
            tf_HypDiff.setText(String.valueOf(nullMeanDiff));
        });
        
    }
 
    private void makeNumericValuesPanel() {
        vBoxNumValsPanel = new VBox();
        vBox_Grp_Prop_1 = new VBox();
        vBox_Grp_Prop_1.setAlignment(Pos.CENTER);
        vBox_Grp_Prop_1.setPadding(new Insets(5, 5, 5, 5));
        str_Group1_Title = "Treatment / Population #1";
        txt_Group1_Title = new Text(str_Group1_Title);
    
        str_Group1_SumInfo = "   Summary Information";
        txt_Group1_SumInfo = new Text(str_Group1_SumInfo);
        
        str_Prop_1 = "  Prop #1";
        txt_Prop_1 = new Text(str_Prop_1);  
        
        str_EffectSize = "Min Effect size: ";
        txt_EffectSize = new Text(str_EffectSize);
        
        hBox_Grp_1_Prop_Row = new HBox();
        
        //stf_Prop1 = new SmartTextField(diffPropsHandler, 4, 1);
        al_STF.get(0).getTextField().setPrefColumnCount(12);    // Prop1
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("Prop1");
        al_STF.get(0).setSmartTextField_MB_REAL(true);
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            bool_Prop1Good = DataUtilities.strIsAProb(al_STF.get(0).getText());
            
            if (bool_Prop1Good) {
                strMean1 = al_STF.get(0).getText();
                prop_1 = Double.parseDouble(strMean1);
                al_STF.get(0).setText(String.valueOf(prop_1));
                check_LTGT_Consistency();
            }
        });
       
        hBox_Grp_1_Prop_Row.setAlignment(Pos.CENTER);
        hBox_Grp_1_Prop_Row.getChildren()
                           .add(al_STF.get(0).getTextField());
        hBox_Grp_1_Prop_Row.setSpacing(25);
        str_Group1_N = "   Group / Sample Size #1";        
        txt_Group1_N = new Text(str_Group1_N);   
        
        //stf_N1 = new SmartTextField(diffPropsHandler, 0, 2);
        al_STF.get(1).getTextField().setPrefColumnCount(12);    // stf_N1
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(1).getTextField().setText(toBlank);
        al_STF.get(1).getTextField().setId("SampleSize1");
        al_STF.get(1).setSmartTextField_MB_POSITIVEINTEGER(true);
        //al_stfForEntry.add(stf_N1);

        // ??????  Why am I setting the text again?  Vestigial from prop?
        al_STF.get(1).getTextField().setOnAction(e -> {
            bool_N1Good = DataUtilities.txtFieldHasPosInt(al_STF.get(1).getTextField());
            
            if (bool_N1Good) {
                n1 = Integer.parseInt(al_STF.get(1).getText());
                strN1 = String.valueOf(al_STF.get(1).getText());
                al_STF.get(1).setText(strN1);
                bool_N1Good = true;
            }
        });

        vBox_Grp_Prop_1.getChildren().addAll(txt_Group1_Title,
                                         txt_Group1_SumInfo,
                                         txt_Prop_1,
                                         hBox_Grp_1_Prop_Row,
                                         txt_Group1_N,
                                         al_STF.get(1).getTextField());
        
        vBox_Grp_Prop_2 = new VBox();
        vBox_Grp_Prop_2.setAlignment(Pos.CENTER);
        vBox_Grp_Prop_2.setPadding(new Insets(5, 5, 5, 5));
        str_Group2_Title = "Treatment / Population #2";
        txt_Group2_Title = new Text(str_Group2_Title);
        str_Group2_SumInfo = "   Summary Information";
        txt_Group2_SumInfo = new Text(str_Group2_SumInfo);        
        str_Prop_2 = "  Prop #2";
        txt_Prop_2 = new Text(str_Prop_2);
        hBox_Grp_2_Prop_Row = new HBox();
        
        //stf_Prop2 = new SmartTextField(diffPropsHandler, 1, 3);
        al_STF.get(2).getTextField().setPrefColumnCount(12);
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(2).getTextField().setText(toBlank);
        al_STF.get(2).getTextField().setId("Prop2"); 
        al_STF.get(2).setSmartTextField_MB_REAL(true);
        //al_stfForEntry.add(stf_Prop2);
        
        al_STF.get(2).getTextField().setOnAction(e -> {
            bool_Prop2Good = DataUtilities.strIsAProb(al_STF.get(2).getText());
            if (bool_Prop2Good) {
                strMean2 = al_STF.get(2).getText();
                prop_2 = Double.parseDouble(strMean2);
                al_STF.get(2).setText(String.valueOf(prop_2));
                check_LTGT_Consistency();
            }
        });
        
        hBox_Grp_2_Prop_Row.setAlignment(Pos.CENTER);
        hBox_Grp_2_Prop_Row.getChildren()
                             .addAll(al_STF.get(2).getTextField(),
                                     txt_Prop_2);
        hBox_Grp_2_Prop_Row.setSpacing(25);
        str_Group2_N = "   Group / Sample Size #2";
        txt_Group2_N = new Text(str_Group2_N);  
        
        //stf_N2 = new SmartTextField(diffPropsHandler, 2, 4);
        al_STF.get(3).getTextField().setPrefColumnCount(8);
        al_STF.get(3).getTextField().setMaxWidth(50);
        al_STF.get(3).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(3).getTextField().setText(toBlank);    
        al_STF.get(3).getTextField().setId("SampleSize2");
        al_STF.get(3).setSmartTextField_MB_POSITIVEINTEGER(true);
        
        al_STF.get(3).getTextField().setOnAction(e -> {
            bool_N2Good = DataUtilities.txtFieldHasPosInt(al_STF.get(3).getTextField());
            
            if (bool_N2Good) {
                n2 = Integer.parseInt(al_STF.get(3).getText());
                strN2 = String.valueOf(al_STF.get(3).getText());
                al_STF.get(3).setText(strN2);
                bool_N2Good = true;
            }
        });    

        vBox_Grp_Prop_2.getChildren().addAll(txt_Group2_Title,
                                         txt_Group2_SumInfo,
                                         txt_Prop_2,
                                         hBox_Grp_2_Prop_Row,
                                         txt_Group2_N,
                                         al_STF.get(3).getTextField());    

        //stf_EffectSize = new SmartTextField(diffPropsHandler, 3, 0);
        al_STF.get(4).getTextField().setPrefColumnCount(12);
        al_STF.get(4).getTextField().setMinWidth(65);
        al_STF.get(4).getTextField().setMaxWidth(65);
        al_STF.get(4).getTextField().setText(toBlank); 
        al_STF.get(4).getTextField().setId("StDev");
        al_STF.get(4).setSmartTextField_MB_POSITIVE(true);
        
        al_STF.get(4).getTextField().setOnAction(e -> {
            bool_EffectSizeGood = DataUtilities.strIsAPosDouble(al_STF.get(4).getTextField().getText());
            
            if (bool_EffectSizeGood) {
                effectSize = Double.parseDouble(al_STF.get(4).getText());
                str_EffectSize = String.valueOf(effectSize);
                al_STF.get(4).setText(str_EffectSize);
                bool_EffectSizeGood = true;
            }
        }); 
        
        vBoxEffectSize = new VBox();
        vBoxEffectSize.setPadding(new Insets(5, 10, 5, 5));
        vBoxEffectSize.getChildren().addAll(txt_EffectSize, al_STF.get(4).getTextField() );
        
        vBoxNumValsPanel.getChildren()
                  .addAll(vBox_Grp_Prop_1, 
                          sep_Prop1_and_Prop2,
                          vBox_Grp_Prop_2,
                          vBoxEffectSize);
        
        al_STF.get(0).getTextField().requestFocus();
    }
    
    private void makeInfDecisionsPanel() {
        nullMeanDiff = 0.;
        daNullDiff = 0.0;
       
        lblCILabel = new Label("   Select conf level");
        lblCILabel.setMaxWidth(120);
        lblCILabel.setMinWidth(120);
        strCILevels = FXCollections.<String>observableArrayList("          90%", "          95%", "          99%");
        ciView = new ListView<>(strCILevels);
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
 
        lblAlphaLabel = new Label("   Select alpha level");
        lblAlphaLabel.setMaxWidth(120);
        lblAlphaLabel.setMinWidth(120);
        strAlphaLevels = FXCollections.<String>observableArrayList("          0.10", "          0.05", "          0.01");
        alphaView = new ListView<>(strAlphaLevels);
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
        vBoxCIBox = new VBox();
        
        vBoxCIBox.getChildren().addAll(lblCILabel, ciView);
        vBoxAlpha = new VBox();
        vBoxAlpha.getChildren().addAll(lblAlphaLabel, alphaView);

        hBoxAlphaAndCI = new HBox();
        hBoxAlphaAndCI.getChildren().addAll(vBoxAlpha, sep, vBoxCIBox);

        hBoxAlphaAndCI = new HBox();
        hBoxAlphaAndCI.setPadding(new Insets(10, 5, 5, 5));
        hBoxAlphaAndCI.getChildren().addAll(vBoxAlpha, sep, vBoxCIBox);  
        
        vBoxInfChoicesPanel = new VBox();
        vBoxInfChoicesPanel.setAlignment(Pos.CENTER);
        vBoxInfChoicesPanel.getChildren().add(hBoxAlphaAndCI);           
    }
    
    private void makeBottomPanel() { 
        hBoxBottomPanel = new HBox(10);
        hBoxBottomPanel.setAlignment(Pos.CENTER);
        hBoxBottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        btnOK = new Button("Compute");
        btnCancel = new Button("Cancel");
        btnReset = new Button("Reset");
        
        btnOK.setOnAction((ActionEvent event) -> { 
            
        returnStatus = "OK";
        if (al_STF.get(0).isEmpty()
            || al_STF.get(1).isEmpty() || al_STF.get(2).isEmpty()    
            || al_STF.get(3).isEmpty() || al_STF.get(4).isEmpty()) 
        {
            valuesLeftBlank = true;
        }
        
        if (valuesLeftBlank) {
            valuesLeftBlankIndex++;
            
            if (valuesLeftBlankIndex == 1) {
                showValuesLeftBlankAlert = true;
                showValuesLeftBlankAlert();
            }
            else {
               valuesLeftBlankIndex = 0; 
            }    
            returnStatus = "Cancel";
            close();   
        }
            
        bool_Prop1Good = DataUtilities.strIsAProb(al_STF.get(0).getText());
        bool_Prop2Good = DataUtilities.strIsAProb(al_STF.get(2).getText());
        bool_N1Good = DataUtilities.txtFieldHasPosInt(al_STF.get(1).getTextField());
        bool_N2Good = DataUtilities.txtFieldHasPosInt(al_STF.get(3).getTextField());
        bool_EffectSizeGood = DataUtilities.txtFieldHasDouble(al_STF.get(4).getTextField());
        allFieldsGood = bool_Prop1Good  && bool_Prop2Good
                        && bool_N1Good && bool_N2Good && bool_EffectSizeGood;
        
        if (!allFieldsGood) {
            notAllFieldsGoodIndex++;
            
            if (notAllFieldsGoodIndex == 1) {
                showNotAllFieldsGoodAlert = true;
                showNotAllFieldsGoodAlert();
            }
            else {
               notAllFieldsGoodIndex = 0; 
            }    
            returnStatus = "Cancel";
            close();
        }

        if (allFieldsGood) {
            returnStatus = "OK";
            close();
        } 
    });
        
        setOnCloseRequest((WindowEvent t) -> {
            returnStatus = "Cancel";
            close();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            returnStatus = "Cancel";
            close();
        });

        btnReset.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(toBlank); al_STF.get(1).setText(toBlank);
            al_STF.get(2).setText(toBlank); al_STF.get(3).setText(toBlank);
            
            bool_Prop1Good = false; bool_Prop2Good = false; 
            bool_N1Good = false; bool_N2Good = false;
        });
        
        hBoxBottomPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
    }
    
    public boolean check_LTGT_Consistency() {
        if ((al_STF.get(0).isEmpty() || al_STF.get(2).isEmpty())) {
            return false;
        }  

        bool_Prop1Good = DataUtilities.strIsAProb(al_STF.get(0).getText());
        bool_Prop2Good = DataUtilities.strIsAProb(al_STF.get(2).getText());        
        
        double prop1 = Double.parseDouble(al_STF.get(0).getText());
        double prop2 = Double.parseDouble(al_STF.get(2).getText()); 
        
        if (!bool_Prop1Good || !bool_Prop1Good) { return false; }
        
        if (str_AltHypChosen.equals("LessThan") && (prop1 - prop2 > 0.0)) {
            showAltHyp_Prop_Increase_Mismatch_Alert();            
            return false;
        }
        
        if (str_AltHypChosen.equals("GreaterThan") && (prop1 - prop2 < 0.0)) {
            showAltHyp_Prop_Decrease_Mismatch_Alert();            
            return false;
        }
        
        return true;
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = ciView.getSelectionModel().getSelectedIndex();
        alphaView.getSelectionModel().select(ciIndex);
        alphaLevel = theAlphaLevs[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = alphaView.getSelectionModel().getSelectedIndex();
        ciView.getSelectionModel().select(alphaIndex);
        alphaLevel = theAlphaLevs[alphaIndex];   
    }
    
    public void showValuesLeftBlankAlert() { 
        if (showValuesLeftBlankAlert  == true) {
            valuesLeftBlankAlert = new Alert(Alert.AlertType.WARNING);
            valuesLeftBlankAlert.setTitle("Warning!  Some fields left blank!!");
            valuesLeftBlankAlert.setHeaderText("A slight Tabula Rasa here...");
            valuesLeftBlankAlert.setContentText("Ok, so here's the deal.  I don't care how tired you are,"
                                             + "\nyou gotta fill in all the stuff.  If you don't, I really am"
                                             + "\nnot authorized to make up numbers like SOME statisticians do."
                                             + "\nLet's try this numeric entry thing again.");        
            valuesLeftBlankAlert.showAndWait(); 
            showValuesLeftBlankAlert  = false;
        }
    }
    
    public void showNotAllFieldsGoodAlert() { 
        if (showNotAllFieldsGoodAlert == true) {
            notAllFieldsGoodAlert = new Alert(Alert.AlertType.WARNING);
            notAllFieldsGoodAlert.setTitle("Warning!  Bad fields detected!!");
            notAllFieldsGoodAlert.setHeaderText("And it ain't corn or sorghum fields, Bucko!...");
            notAllFieldsGoodAlert.setContentText("Ok, so here's the deal.  Proportions are real numbers,"
                                             + "\nand sample sizes must be natural numbers.  I, SPLAT, do not"
                                             + "\nmake up the mathy rules, I only enforce the mathy rules."
                                             + "\nLet's try this numeric entry thing again.");        
            notAllFieldsGoodAlert.showAndWait(); 
            showNotAllFieldsGoodAlert  = false;
        }
    }
    
    public void showAltHyp_Prop_Increase_Mismatch_Alert() { 
            altHypPropIncreaseMismatchAlert = new Alert(Alert.AlertType.WARNING);
            altHypPropIncreaseMismatchAlert.setTitle("Warning! Decrease detected!!");
            altHypPropIncreaseMismatchAlert.setHeaderText("And it ain't corn or sorghum fields, Bucko!...");
            altHypPropIncreaseMismatchAlert.setContentText("Ok, so here's the deal.  Your alternate hypothesis is "
                                             + "\nthat p1 < p2.  And you are telling me that your expected"
                                             + "\nproportions are differenty ordered?  How many fewer than one"
                                             + "\nclue are you of which in possession????   Let's try that again.");        
            altHypPropIncreaseMismatchAlert.showAndWait(); 
            al_STF.get(0).setText(toBlank);
            al_STF.get(2).setText(toBlank);
    }
    
    public void showAltHyp_Prop_Decrease_Mismatch_Alert() { 
            altHypPropDecreaseMismatchAlert = new Alert(Alert.AlertType.WARNING);
            altHypPropDecreaseMismatchAlert.setTitle("Warning!  Increase detected!!");
            altHypPropDecreaseMismatchAlert.setHeaderText("And it ain't corn or sorghum fields, Bucko!...");
            altHypPropDecreaseMismatchAlert.setContentText("Ok, so here's the deal.  Your alternate hypothesis is "
                                             + "\nthat p1 > p2.  And you are telling me that your expected"
                                             + "\nproportions are differenty ordered?  How many fewer than one"
                                             + "\nclue are you of which in possession????   Let's try that again.");         
            altHypPropDecreaseMismatchAlert.showAndWait(); 
            al_STF.get(0).setText(toBlank);
            al_STF.get(2).setText(toBlank);
    }
    
    public void printTheLot() {
        System.out.println("Prop1Good = " + bool_Prop1Good);
        System.out.println("Prop2Good = " + bool_Prop2Good);
        System.out.println("   N1Good = " + bool_N1Good);
        System.out.println("   N2Good = " + bool_N2Good); 
        System.out.println("  EffSize = " + bool_EffectSizeGood);
        
        System.out.println("\nProp1 = " + al_STF.get(0).getText());
        System.out.println("Prop2 = " + al_STF.get(2).getText());
        System.out.println("   n1 = " + al_STF.get(1).getText());
        System.out.println("   n2 = " + al_STF.get(3).getText()); 
    }
    
    public double getAlpha() {  return alphaLevel;  }    
    public double getEffectSize() { return effectSize; }    
    public double getLevelOfSignificance() { return alphaLevel; } 
    public String getAltHypothesis() { return str_AltHypChosen; }
    public double getAltDiff() { return nullMeanDiff; }   
    public int getN1() { return n1; }
    public int getN2() { return n2; }    
    public double getProp_1() { return prop_1; }
    public double getProp_2() { return prop_2; }    
    public double getTheNullDiff() { return daNullDiff; }    
    public String getReturnStatus() { return returnStatus; }
}


