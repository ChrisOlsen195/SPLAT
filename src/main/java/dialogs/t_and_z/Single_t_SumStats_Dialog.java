/************************************************************
 *                   Single_t_SumStats_Dialog               *
 *                          10/15/23                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs.t_and_z;

import dialogs.One_Variable_Dialog;
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
import smarttextfield.*;
import javafx.stage.WindowEvent;
import utilityClasses.MyAlerts;

public class Single_t_SumStats_Dialog extends One_Variable_Dialog { 
    
    // POJOs
    boolean okToContinue, /*runAnalysis, ok, caughtYa,*/ dataPresent, 
            valuesLeftBlank, bool_MeanGood, bool_SigmaGood, bool_NGood, 
            /*bool_AlphaGood,*/ allFieldsGood; // , showValuesLeftBlankAlert, 
            //showNotAllFieldsGoodAlert;
    
    int  n, alphaIndex, ciIndex; //, valuesLeftBlankIndex, notAllFieldsGoodIndex;
    
    //Integer suspectedCount;
    
    double sigma1, alphaLevel, /*ciLevel, nullHypothesis,*/ hypothesizedMean, 
           nullDiffRequested, mean;
    double[] theAlphaLevs; //, theCILevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, 
           strAltHypChosen, str_Group_Title, str_Group_SumInfo, str_OROne, 
           str_Group1_N, resultAsString, strHypChosen, strMean1, strSigma1;
    
    final String toBlank = "";

    ObservableList<String> ciLevels, alphaLevels;
    ListView<String> ciView, alphaView;
    
    // My classes
    //SmartTextField stf_Mean, stf_Sigma, stf_N;
    //SmartTextFieldHandler meansHandler;
    //ArrayList<SmartTextField> al_stfForEntry; 
    SmartTextFieldsController stf_Controller;
    DoublyLinkedSTF al_STF;
    
    // JavaFX POJOs
    //Alert valuesLeftBlankAlert, notAllFieldsGoodAlert;
    Button changeNull;

    HBox bottomPanel, hBox_GPOne_SuccessRow, //hBox_GPTwo_SuccessRow,
         alphaAndCI, /*nullDiffInfo,*/ hBoxCurrDiff;
    
    Label lblNullAndAlt, /*lblSigLevel,*/ ciLabel, alphaLabel; //, lbl_nullDiffInfo;
    
    RadioButton hypNE, hypLT, hypGT; //, hypNull;
    
    Scene scene;
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndBottom,
              /*sep_Prop1_and_Prop2,*/ sep_Alpha, sep; 
    
    VBox root, nullsPanel, numValsPanel, group, explanVarStuff,
         ciBox, alphaBox, infChoicesPanel; 
    
    final Text currNullMean = new Text(" Current null: \u03BC = ");
    TextField tf_HypMean;
    TextInputDialog txtDialog;
    Text txt_Group_Title, txt_Group_SumInfo, txt_OROne, txt_Group_N;

    public Single_t_SumStats_Dialog() {
        super("Quantitative");
        //System.out.println("86 Single_t_SumStats_Dialog, constructing");
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        //theCILevs = new double[] {0.90, 0.95, 0.99};
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        
        strReturnStatus = "OK";

        root = new VBox();
        dataPresent = false;
        root.setAlignment(Pos.CENTER);

        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set
        stf_Controller.setSize(3);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        
        lbl_Title = new Label("Inference for a single mean");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));        
        lbl_Title.getStyleClass().add("dialogTitle");
        
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));
        sep_NullsFromInf = new Separator();
        sep_NullsFromInf.setOrientation(Orientation.VERTICAL);
        sep_InfFromNumbers = new Separator();
        sep_InfFromNumbers.setOrientation(Orientation.VERTICAL);
        sep_MiddleAndBottom = new Separator();
        // sep_Prop1_and_Prop2 = new Separator();
        sep_Alpha = new Separator();
        sep_Alpha.setMinHeight(10);

        makeInfDecisionsPanel();
        makeNullsPanel();
        makeNumericValuesPanel();
        makeBottomPanel();
        
        middlePanel = new HBox();
        middlePanel.setSpacing(30);
        
        middlePanel.getChildren().addAll(nullsPanel, sep_NullsFromInf,
                                         infChoicesPanel,sep_InfFromNumbers,
                                         numValsPanel);
        middlePanel.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(lbl_Title, 
                                  sep_NullsFromInf,
                                  middlePanel,
                                  sep_MiddleAndBottom,
                                  bottomPanel);        
        
        scene = new Scene (root, 725, 400);
        setTitle("Inference for a single mean");

        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });        
        
        setScene(scene);
        showAndWait();
    }  
    
private void makeNullsPanel() {
        hypothesizedMean = 0.0;
        nullDiffRequested = 0.0;
        strHypChosen = "NotEqual";
        changeNull = new Button("Change null hypothesis");
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        strHypNull = "\u03BC = k";
        strHypNE = "\u03BC \u2260 k";
        strHypLT = "\u03BC  < k";
        strHypGT = "\u03BC  > k";
        
        hypNE = new RadioButton(strHypNull + "\n" + strHypNE);
        hypLT = new RadioButton(strHypNull + "\n" + strHypLT);
        hypGT = new RadioButton(strHypNull + "\n" + strHypGT);
        
        // top, right, bottom, left
        hypNE.setPadding(new Insets(10, 10, 10, 10));
        hypLT.setPadding(new Insets(10, 10, 10, 10));
        hypGT.setPadding(new Insets(10, 10, 10, 10));
        
        hypNE.setSelected(true);
        hypLT.setSelected(false);
        hypGT.setSelected(false);
        
        tf_HypMean = new TextField("0.0");
        tf_HypMean.setMinWidth(75);
        tf_HypMean.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(currNullMean, tf_HypMean);
        
        nullsPanel = new VBox();
        
        nullsPanel.getChildren()
                 .addAll(lblNullAndAlt, hypNE, hypLT, hypGT, 
                         changeNull, hBoxCurrDiff);
        
        hypNE.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypNE chosen");
            hypNE.setSelected(true);
            hypLT.setSelected(false);
            hypGT.setSelected(false);
            strHypChosen = "NotEqual";
        });
            
        hypLT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());

            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypLT chosen");
            hypNE.setSelected(false);
            hypLT.setSelected(true);
            hypGT.setSelected(false);
            strHypChosen = "LessThan";
        });
            
        hypGT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypGT chosen");
            hypNE.setSelected(false);
            hypLT.setSelected(false);
            hypGT.setSelected(true);
            strHypChosen = "GreaterThan";
        });
            
        changeNull.setOnAction((ActionEvent event) -> {
            okToContinue = false;
            while (okToContinue == false) {
                okToContinue = true;
                txtDialog = new TextInputDialog("");
                txtDialog.setTitle("Null hypothesis change");
                txtDialog.setHeaderText(toBlank);                
                txtDialog.setContentText("What is your hypothesized mean? ");                     

                Optional<String> result = txtDialog.showAndWait();
                
                if (result.isPresent()) { resultAsString = result.get(); }
                
                if (result.isPresent() == true) {
                    okToContinue = true;
                    try {
                        nullDiffRequested = Double.valueOf(resultAsString);
                    }
                    catch (NumberFormatException ex ){ 
                        MyAlerts.showGenericBadNumberAlert(" a real number ");
                        txtDialog.setContentText("");
                        okToContinue = false;
                        nullDiffRequested = 0.0;
                    }
                }
                else {
                    nullDiffRequested = 0.0;    // Null returns to 0.0 if Cancel
                }
            }
            hypothesizedMean = nullDiffRequested;
            tf_HypMean.setText(String.valueOf(hypothesizedMean));
        });
    }
 
    private void makeNumericValuesPanel() {
        numValsPanel = new VBox();
        group = new VBox();
        group.setAlignment(Pos.CENTER);
        group.setPadding(new Insets(5, 5, 5, 5));
        str_Group_Title = "Treatment / Population   ";
        txt_Group_Title = new Text(str_Group_Title);
    
        str_Group_SumInfo = "   Summary Information";
        txt_Group_SumInfo = new Text(str_Group_SumInfo);
        
        str_OROne = "  Mean            StDev   ";
        txt_OROne = new Text(str_OROne);    
        
        hBox_GPOne_SuccessRow = new HBox();
        
        //stf_Mean = new SmartTextField(meansHandler, 2, 1);
        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("Mean");
        al_STF.get(0).setSmartTextField_MB_REAL(true);
        //al_stfForEntry.add(stf_Mean);
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            bool_MeanGood = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());
            if (bool_MeanGood == true) {
                strMean1 = al_STF.get(0).getText();
                mean = Double.parseDouble(strMean1);
                al_STF.get(0).setText(String.valueOf(mean));
            }
        });
       
        //stf_Sigma = new SmartTextField(meansHandler, 0, 2);
        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(toBlank); 
        al_STF.get(1).getTextField().setId("StDev");
        al_STF.get(1).setSmartTextField_MB_POSITIVE(true);
        //al_stfForEntry.add(stf_Sigma);
               
        al_STF.get(1).getTextField().setOnAction(e -> {
            bool_SigmaGood = DataUtilities.strIsAPosDouble(al_STF.get(1).getTextField().getText());
            
            if (bool_SigmaGood) {
                sigma1 = Double.parseDouble(al_STF.get(1).getText());
                strSigma1 = String.valueOf(sigma1);
                al_STF.get(1).setText(strSigma1);
                bool_SigmaGood = true;
            }
        });

        
        hBox_GPOne_SuccessRow.setAlignment(Pos.CENTER);
        hBox_GPOne_SuccessRow.getChildren()
                             .addAll(al_STF.get(0).getTextField(),
                                     al_STF.get(1).getTextField());
        hBox_GPOne_SuccessRow.setSpacing(25);

        str_Group1_N = "   Group / Sample Size   ";
        txt_Group_N = new Text(str_Group1_N);   
        
        //stf_N = new SmartTextField(meansHandler, 1, 0);
        al_STF.get(2).getTextField().setPrefColumnCount(12);
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(2).getTextField().setText(toBlank);
        al_STF.get(2).getTextField().setId("SampleSize");
        al_STF.get(2).setSmartTextField_MB_POSITIVEINTEGER(true);
        //al_stfForEntry.add(stf_N);

        // ??????  Why am I setting the text again?  Vestigial from prop?

        al_STF.get(2).getTextField().setOnAction(e -> {
            bool_NGood = DataUtilities.strIsAPosInt(al_STF.get(2).getTextField().getText());
            
            if (bool_NGood) {
                n = Integer.parseInt(al_STF.get(2).getText());
                al_STF.get(2).setText(String.valueOf(al_STF.get(2).getText()));
                bool_NGood = true;
            }
        });
        
        explanVarStuff = new VBox();    
        explanVarStuff.getChildren().addAll(lblExplanVar, tf_DescriptionOfVarSelected);
        
        group.getChildren().addAll(txt_Group_Title,
                                         txt_Group_SumInfo,
                                         txt_OROne,
                                         hBox_GPOne_SuccessRow,
                                         txt_Group_N,
                                         al_STF.get(2).getTextField(),
                                         explanVarStuff);
        
        al_STF.get(0).getTextField().requestFocus();
        
        numValsPanel.getChildren()
                    .add(group);
        
    }
    
    private void makeInfDecisionsPanel() {
        hypothesizedMean = 0.;
        //nullHypothesis = 0.0;
       
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
        
        ciBox = new VBox();        
        ciBox.getChildren().addAll(ciLabel, ciView);
        alphaBox = new VBox();
        alphaBox.getChildren().addAll(alphaLabel, alphaView);

        //alphaAndCI = new HBox();
        //alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);

        alphaAndCI = new HBox();
        alphaAndCI.setPadding(new Insets(10, 5, 5, 5));
        alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);  
        
        infChoicesPanel = new VBox();
        infChoicesPanel.setAlignment(Pos.CENTER);
        infChoicesPanel.getChildren().add(alphaAndCI);           
    }
    
    private void makeBottomPanel() {        
        bottomPanel = new HBox(10);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        btnOK = new Button("Compute");
        btnCancel = new Button("Cancel");
        resetButton = new Button("Reset");
        
        btnOK.setOnAction((ActionEvent event) -> { 
            
        doMissingAndOrWrong();
        
        if (valuesLeftBlank) { MyAlerts.showMustBeNonBlankAlert(); }
        else 
        if (!allFieldsGood) { MyAlerts.showNotAllFieldsGoodAlert(); }
        else{
            strReturnStatus = "OK";
            close();
        }
    });
        
        setOnCloseRequest((WindowEvent t) -> {
            dataPresent = false;
            close();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            dataPresent = false;
            close();
        });

        resetButton.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(toBlank); 
            al_STF.get(1).setText(toBlank); 
            al_STF.get(2).setText(toBlank);
            
            bool_MeanGood = false; 
            bool_SigmaGood = false;  
            bool_NGood = false; 
            
        });
        
        bottomPanel.getChildren().addAll(btnOK, btnCancel, resetButton);
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = ciView.getSelectionModel().getSelectedIndex();
        alphaView.getSelectionModel().select(ciIndex);
        alphaLevel = theAlphaLevs[ciIndex];
        //ciLevel = theCILevs[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = alphaView.getSelectionModel().getSelectedIndex();
        ciView.getSelectionModel().select(alphaIndex);
        alphaLevel = theAlphaLevs[alphaIndex];
        //ciLevel = theCILevs[alphaIndex];    
    }
    
    // The evaluations here will be specific to the dialog
    private void doMissingAndOrWrong() {
        valuesLeftBlank = al_STF.get(0).isEmpty() || al_STF.get(1).isEmpty() || al_STF.get(2).isEmpty();               
        bool_MeanGood = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());      
        bool_SigmaGood = DataUtilities.strIsAPosDouble(al_STF.get(1).getTextField().getText());
        bool_NGood = DataUtilities.txtFieldHasPosInt(al_STF.get(2).getTextField());
        allFieldsGood = bool_MeanGood && bool_SigmaGood && bool_NGood;
    }
    
    public double getAlpha() { return alphaLevel; }    
    public String getHypotheses() { return strHypChosen; }
    public boolean getDataPresent() { return dataPresent; }
    public double getLevelOfSignificance() { return alphaLevel; }
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getHypothesizedMean() { return hypothesizedMean; }
    public int getN() { return n; }
    public double getStDev() {return sigma1; }
    public double getXBar() { return mean; }
    //public String getReturnStatus() { return strReturnStatus; }
    //public String getDescriptionOfVariable() { return tf_DescriptionOfVarSelected.getText(); }
}


