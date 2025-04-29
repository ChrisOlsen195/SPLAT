/************************************************************
 *                 Power_SingleProp_Dialog                  *
 *                        01/15/25                          *
 *                         15:00                            *
 ***********************************************************/
package dialogs.power;

import utilityClasses.DataUtilities;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import smarttextfield.*;
import javafx.stage.WindowEvent;
import utilityClasses.MyAlerts;

public class Power_SingleProp_Dialog extends Power_Dialog { 
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean boolValuesLeftBlank, bool_NullPropGood, bool_EffectSizeGood, 
            bool_SampleSizeGood, allFieldsGood; 
    
    int  sampleSize, alphaIndex, ciIndex; 
    double nullProp, effectSize, alphaLevel;
    double[] theAlphaLevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, 
           strAltHypChosen, str_Group_Title, str_Group_SumInfo, str_Prop, 
           strHypChosen, str_SampleSize;
    
    String strProp, strEffectSize;
    
    final String toBlank = "";
    
    ObservableList<String> strCILevels, strAlphaLevels;
    ListView<String> listViewCI, listView_Alpha;
    
    // My classes
    SmartTextFieldsController stf_Controller;
    SmartTextFieldDoublyLinkedSTF al_STF;
    
    // JavaFX POJOs
    RadioButton radbtn_HypNE, radbtn_HypLT, radbtn_HypGT;
    
    GridPane powerOption;

    Label lblNullAndAlt, lblCILabel, lblAlphaLabel;
    HBox hBoxBottomPanel, hBoxAlphaAndCI;

    VBox root, vBoxNullsPanel, vBoxNumValsPanel, vBoxGroup, vBoxCIBox, 
         vBoxAlphaBox, vBoxInfChoicesPanel; 

    Scene scene;
    Separator sepNullsFromInf, sepInfFromNumbers, sepMiddleAndBottom,
              sepAlpha, sep;  
    
    Label lbl_Group_Title, lbl_Group_SumInfo, lblProp, lblEffectSize, 
          lblSampleSize; 

    public Power_SingleProp_Dialog() {
        super("Quantitative");  //  Doesn't really do anything (no data chosen)
        if (printTheStuff == true) {
            System.out.println("76 *** Power_SingleProp_Dialog, Constructing");
        }
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        
        strReturnStatus = "Cancel";

        root = new VBox();
        root.setAlignment(Pos.CENTER);
        stf_Controller = new SmartTextFieldsController();
        //    // stf_Controller is empty until size is set                          *
        stf_Controller.setSize(4);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        
        lblTitle = new Label("Inference for a single proportion");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        sepNullsFromInf = new Separator();
        sepNullsFromInf.setOrientation(Orientation.VERTICAL);
        sepInfFromNumbers = new Separator();
        sepInfFromNumbers.setOrientation(Orientation.VERTICAL);
        sepMiddleAndBottom = new Separator();
        sepAlpha = new Separator();
        sepAlpha.setMinHeight(10);

        makeInfDecisionsPanel();
        makeNullsPanel();
        makeNumericValuesPanel();
        makeBottomPanel();
        
        hBoxMiddlePanel = new HBox();
        hBoxMiddlePanel.setSpacing(30);       
        hBoxMiddlePanel.getChildren().addAll(vBoxNullsPanel, sepNullsFromInf,
                                         vBoxInfChoicesPanel,sepInfFromNumbers,
                                         vBoxNumValsPanel);
        hBoxMiddlePanel.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(lblTitle, 
                                  sepNullsFromInf,
                                  hBoxMiddlePanel,
                                  sepMiddleAndBottom,
                                  hBoxBottomPanel);        
        
        scene = new Scene (root, 725, 400);
        setTitle("Inference for a single proportion");

        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });        
        
        setScene(scene);
    }  
    
private void makeNullsPanel() {        
        strHypChosen = "NotEqual";
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        strHypNull = "p = k";
        strHypNE = "p \u2260 k";
        strHypLT = "p  < k";
        strHypGT = "p  > k";
        
        radbtn_HypNE = new RadioButton(strHypNull + "\n" + strHypNE);
        radbtn_HypLT = new RadioButton(strHypNull + "\n" + strHypLT);
        radbtn_HypGT = new RadioButton(strHypNull + "\n" + strHypGT);
        
        // top, right, bottom, left
        radbtn_HypNE.setPadding(new Insets(10, 10, 10, 10));
        radbtn_HypLT.setPadding(new Insets(10, 10, 10, 10));
        radbtn_HypGT.setPadding(new Insets(10, 10, 10, 10));
        
        radbtn_HypNE.setSelected(true);
        radbtn_HypLT.setSelected(false);
        radbtn_HypGT.setSelected(false);

        vBoxNullsPanel = new VBox();
        
        vBoxNullsPanel.getChildren()
                 .addAll(lblNullAndAlt, radbtn_HypNE, radbtn_HypLT, radbtn_HypGT);
        
        radbtn_HypNE.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            radbtn_HypNE.setSelected(true);
            radbtn_HypLT.setSelected(false);
            radbtn_HypGT.setSelected(false);
            strHypChosen = "NotEqual";

        });
            
        radbtn_HypLT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            radbtn_HypNE.setSelected(false);
            radbtn_HypLT.setSelected(true);
            radbtn_HypGT.setSelected(false);
            strHypChosen = "LessThan";

        });
            
        radbtn_HypGT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            radbtn_HypNE.setSelected(false);
            radbtn_HypLT.setSelected(false);
            radbtn_HypGT.setSelected(true);
            strHypChosen = "GreaterThan";
        });
            
    }
 
    private void makeNumericValuesPanel() {
        str_Prop = "Null prop: ";
        strEffectSize = "Min Effect size: ";
        str_SampleSize = "Sample size: ";
        
        lblProp = new Label(str_Prop);        
        lblEffectSize = new Label(strEffectSize);         
        lblSampleSize = new Label(str_SampleSize);         
        
        int widthy = 100;
        lblProp.setMinWidth(widthy);
        lblProp.setMaxWidth(widthy);
        lblEffectSize.setMinWidth(widthy);
        lblEffectSize.setMaxWidth(widthy);
        lblSampleSize.setMinWidth(widthy);
        lblSampleSize.setMaxWidth(widthy);
        
        lblProp.setTextAlignment(TextAlignment.RIGHT);
        lblProp.setAlignment(Pos.CENTER_RIGHT);
        lblEffectSize.setTextAlignment(TextAlignment.RIGHT);
        lblEffectSize.setAlignment(Pos.CENTER_RIGHT);
        lblSampleSize.setTextAlignment(TextAlignment.RIGHT);
        lblSampleSize.setAlignment(Pos.CENTER_RIGHT);
        
        vBoxNumValsPanel = new VBox();
        vBoxGroup = new VBox();
        vBoxGroup.setAlignment(Pos.CENTER);
        vBoxGroup.setPadding(new Insets(5, 5, 5, 5));
        str_Group_Title = "Treatment / Population   ";
        lbl_Group_Title = new Label(str_Group_Title);
    
        str_Group_SumInfo = "   Summary Information";
        lbl_Group_SumInfo = new Label(str_Group_SumInfo);

        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMinWidth(65);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("Prop");
        al_STF.get(0).setSmartTextField_MB_PROBABILITY(true);
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            bool_NullPropGood = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());
            
            if (bool_NullPropGood) {
                strProp = al_STF.get(0).getText();
                nullProp = Double.parseDouble(strProp);
                al_STF.get(0).setText(String.valueOf(nullProp));
            }
        });
        
        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMinWidth(65);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(toBlank); 
        al_STF.get(1).getTextField().setId("EffSize");
        al_STF.get(1).setSmartTextField_MB_PROBABILITY(true);
        al_STF.get(1).getTextField().setOnAction(e -> {
            bool_EffectSizeGood = DataUtilities.strIsAPosDouble(al_STF.get(1).getTextField().getText());
            
            if (bool_EffectSizeGood) {
                effectSize = Double.parseDouble(al_STF.get(1).getText());
                strEffectSize = String.valueOf(effectSize);
                al_STF.get(1).setText(strEffectSize);
                bool_EffectSizeGood = true;
            }
        });   

        al_STF.get(2).getTextField().setPrefColumnCount(12);
        al_STF.get(2).getTextField().setMinWidth(65);
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setText(toBlank); 
        al_STF.get(2).getTextField().setId("StDev");
        al_STF.get(2).setSmartTextField_MB_POSITIVEINTEGER(true);
        al_STF.get(2).getTextField().setOnAction(e -> {
            bool_SampleSizeGood = DataUtilities.strIsANonNegInt(al_STF.get(2).getTextField().getText());
            
            if (bool_SampleSizeGood) {
                sampleSize = Integer.parseInt(al_STF.get(2).getText());
                str_SampleSize = String.valueOf(sampleSize);
                al_STF.get(2).setText(str_SampleSize);
                bool_SampleSizeGood = true;
            }
        });   
        
        powerOption = new GridPane();
        powerOption.add(lblProp, 0, 0); powerOption.add(al_STF.get(0).getTextField(),1,0);
        powerOption.add(lblEffectSize, 0, 2); powerOption.add(al_STF.get(1).getTextField(),1,2);
        powerOption.add(lblSampleSize, 0, 3); powerOption.add(al_STF.get(2).getTextField(),1,3);
        vBoxGroup.getChildren().addAll(lbl_Group_Title,
                                         lbl_Group_SumInfo,
                                         powerOption);
        al_STF.get(0).getTextField().requestFocus();        
        vBoxNumValsPanel.getChildren().add(vBoxGroup);
    }
    
    private void makeInfDecisionsPanel() {
        lblCILabel = new Label("   Select conf level");
        lblCILabel.setMaxWidth(120);
        lblCILabel.setMinWidth(120);
        strCILevels = FXCollections.<String>observableArrayList("          90%", "          95%", "          99%");
        listViewCI = new ListView<>(strCILevels);
        listViewCI.setOrientation(Orientation.VERTICAL);
        listViewCI.setPrefSize(120, 100);
        
        listViewCI.getSelectionModel()
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
        listView_Alpha = new ListView<>(strAlphaLevels);
        listView_Alpha.setOrientation(Orientation.VERTICAL);
        listView_Alpha.setPrefSize(120, 100);        
        listView_Alpha.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       alphaChanged(ov, oldvalue, newvalue);
                  }
              }));        
        listView_Alpha.getSelectionModel().select(1);    //  Set at .05
        
        listViewCI.getSelectionModel().select(1);   //  Set at 95%
        vBoxCIBox = new VBox();
        
        vBoxCIBox.getChildren().addAll(lblCILabel, listViewCI);
        vBoxAlphaBox = new VBox();
        vBoxAlphaBox.getChildren().addAll(lblAlphaLabel, listView_Alpha);

        hBoxAlphaAndCI = new HBox();
        hBoxAlphaAndCI.getChildren().addAll(vBoxAlphaBox, sep, vBoxCIBox);

        hBoxAlphaAndCI = new HBox();
        hBoxAlphaAndCI.setPadding(new Insets(10, 5, 5, 5));
        hBoxAlphaAndCI.getChildren().addAll(vBoxAlphaBox, sep, vBoxCIBox);  
        
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
            doMissingAndOrWrong();
            
            if (boolValuesLeftBlank) {
                MyAlerts.showMustBeNonBlankAlert();  
            }
            else 
            if (!allFieldsGood) {
                MyAlerts.showNotAllFieldsGoodAlert();
            }
            else 
            if (nullProp <= effectSize || nullProp + effectSize >= 1.) {
                MyAlerts.showEffectSizeAlert();
            }
            else {
                strReturnStatus = "OK";
                close();
            }  
        });
        
        setOnCloseRequest((WindowEvent t) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        btnReset.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(toBlank); 
            al_STF.get(1).setText(toBlank);
            al_STF.get(2).setText(toBlank);
            bool_NullPropGood = false;  
        });
        
        hBoxBottomPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = listViewCI.getSelectionModel().getSelectedIndex();
        listView_Alpha.getSelectionModel().select(ciIndex);
        alphaLevel = theAlphaLevs[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = listView_Alpha.getSelectionModel().getSelectedIndex();
        listViewCI.getSelectionModel().select(alphaIndex);
        alphaLevel = theAlphaLevs[alphaIndex];   
    }
    
    // The evaluations here will be specific to the dialog
    private void doMissingAndOrWrong() {
        boolValuesLeftBlank = al_STF.get(0).isEmpty()
                          || al_STF.get(1).isEmpty()   
                          || al_STF.get(2).isEmpty();               
        bool_NullPropGood = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());         
        bool_EffectSizeGood = DataUtilities.strIsAPosDouble(al_STF.get(1).getTextField().getText()); 
        bool_SampleSizeGood = DataUtilities.txtFieldHasPosInt(al_STF.get(2).getTextField());
        allFieldsGood = bool_NullPropGood && bool_EffectSizeGood && bool_SampleSizeGood;
    }    
    
    public double getAlpha() { return alphaLevel; } 
    public String getHypotheses() { return strHypChosen; }
    public double getLevelOfSignificance() { return alphaLevel; }
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getEffectSize() { return effectSize; }
    public double getNullProp() { return nullProp; }
    public int getSampleSize() { return sampleSize; }
    public String getRejectionCriterion() { return strHypChosen; }
    public String getDescriptionOfVariable() { return tfExplanVar.getText(); }
}



