/************************************************************
 *                    Indep_t_SumStats_Dialog               *
 *                          06/15/24                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs.t_and_z;

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
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import smarttextfield.*;
import javafx.stage.WindowEvent;
import utilityClasses.MyAlerts;

public class Indep_t_SumStats_Dialog extends Stage { 
    
    // POJOs
    boolean okToContinue, valuesLeftBlank;
    boolean bool_Mean1Good, bool_Mean2Good, bool_Sigma1Good, bool_Sigma2Good, 
            bool_N1Good, bool_N2Good;
    boolean allFieldsGood;
    
    int  n1, n2, alphaIndex, ciIndex;
    double sigma1, sigma2, alphaLevel, /*ciLevel,*/ daNullDiff;
    double hypothesizedDifference, nullDiffRequested;
    double mean1, mean2;
    double[] theAlphaLevs; //, theCILevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, strAltHypChosen,  
           str_Group1_Title, str_Group1_SumInfo, str_OROne, str_Group1_N,
           str_Group2_Title, str_Group2_SumInfo, str_ORTwo, str_Group2_N,
           resultAsString, strHypChosen;
    
    String strMean1, strMean2, strSigma1, strSigma2, strN1, strN2, returnStatus;
    final String toBlank = "";
    
    final String wtfString = "What!?!?  My hypothesized null difference of zero is not good"
                                    + "\nenough for you? It is pretty unusual that a non-zero difference"
                                    + "\nwould be hypothesized, but I'm willing to believe you know what"
                                    + "\nyou're doing. No skin off MY nose...";
    
    ObservableList<String> ciLevels, alphaLevels;
    ListView<String> ciView, alphaView;
    
    // My classes
    DoublyLinkedSTF al_STF;
    TextField tf_Mean_1, tf_Mean_2, tf_Title;
    
    SmartTextFieldsController stf_Controller;
    
    // JavaFX POJOs
    Button changeNull, okButton, cancelButton, resetButton;;
    RadioButton hypNE, hypLT, hypGT;
    
    GridPane gridChoicesMade;

    Label lblNullAndAlt, lbl_Title, ciLabel, alphaLabel, 
          lblMean_1, lblMean_2, lblTitle;
    HBox middlePanel, bottomPanel, hBox_GPOne_SuccessRow, hBox_Group2_SuccessRow,
         alphaAndCI, hBoxCurrDiff;

    VBox root, nullsPanel, numValsPanel, group_1, group_2,
         ciBox, alphaBox, infChoicesPanel, vBox_VarsPanel; 
    
    final Text currNullDiff = new Text("Current null diff: (\u03BC\u2081 - \u03BC\u2082) = ");
    TextField tf_HypDiff;
    TextInputDialog txtDialog;
  
    Scene scene;
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndBottom,
              sep_Mean1_and_Mean2, sep_Alpha, sep;  
    
    Text txt_Group1_Title, txt_Group1_SumInfo, txt_OROne, txt_Group1_N,
         txt_Group2_Title, txt_Group2_SumInfo, txt_ORTwo, txt_Group2_N;
    
    public Indep_t_SumStats_Dialog() {;
        //System.out.println("93 Ind_t_SumStats_Dialog, constructing");
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        // theCILevs = new double[] {0.90, 0.95, 0.99};
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        returnStatus = "Ok";

        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        //meansHandler = new SmartTextFieldHandler();
        //al_stfForEntry = new ArrayList<>();
        //meansHandler.setHandlerArrayList(al_stfForEntry);
        //meansHandler.setHandlerTransversal(true);
        //meansHandler.setHandlerTransversalIndex(0);
        
        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set                          *
        stf_Controller.setSize(6);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
         
        lbl_Title = new Label("Inference for two independent means");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));        
        lbl_Title.getStyleClass().add("dialogTitle");
        
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));
        sep_NullsFromInf = new Separator();
        sep_NullsFromInf.setOrientation(Orientation.VERTICAL);
        sep_InfFromNumbers = new Separator();
        sep_InfFromNumbers.setOrientation(Orientation.VERTICAL);
        sep_MiddleAndBottom = new Separator();
        sep_Mean1_and_Mean2 = new Separator();
        sep_Alpha = new Separator();
        sep_Alpha.setMinHeight(10);

        makeInfDecisionsPanel();
        makeNullsPanel();
        makeNumericValuesPanel();
        makeVariableDefPanel();
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
                                  vBox_VarsPanel,
                                  bottomPanel);        
        
        // width, height
        scene = new Scene (root, 800, 500);
        setTitle("Inference for a difference in means");
        
        setOnCloseRequest((WindowEvent event) -> {
            returnStatus = "Cancel";
            close();
        });
        setScene(scene);
        
        showAndWait();
    }  
    
private void makeNullsPanel() {       
        hypothesizedDifference = 0.0;
        nullDiffRequested = 0.0;
        strHypChosen = "NotEqual";
        changeNull = new Button("Change null difference");
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        strHypNull = "\u03BC\u2081 - \u03BC\u2082 = k";
        strHypNE = "\u03BC\u2081 - \u03BC\u2082 \u2260 k";
        strHypLT = "\u03BC\u2081 - \u03BC\u2082 < k";
        strHypGT = "\u03BC\u2081 - \u03BC\u2082 > k";
        
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
        
        tf_HypDiff = new TextField("0.0");
        tf_HypDiff.setMinWidth(75);
        tf_HypDiff.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(currNullDiff, tf_HypDiff);
        
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
            while (!okToContinue) {
                okToContinue = true;
                txtDialog = new TextInputDialog("");
                txtDialog.setTitle("Null hypothesis change");
                txtDialog.setHeaderText(wtfString);
                
                txtDialog.setContentText("What difference between means would you like to test? ");                     

                Optional<String> result = txtDialog.showAndWait();
                
                if (result.isPresent()) {
                    resultAsString = result.get();                        
                }
                if (result.isPresent() == true) {
                    okToContinue = true;
                    try {
                        nullDiffRequested = Double.parseDouble(resultAsString);
                    }
                    catch (NumberFormatException ex ){ 
                        okToContinue = false;
                        MyAlerts.showGenericBadNumberAlert(" a real number ");
                        okToContinue = false;
                        nullDiffRequested = 0.0;
                    }
                }
                else {
                    nullDiffRequested = 0.0;    // Null returns to 0.0 if Cancel
                }
            }
            hypothesizedDifference = nullDiffRequested;
            tf_HypDiff.setText(String.valueOf(hypothesizedDifference));
        });
    }
 
    private void makeNumericValuesPanel() {
        numValsPanel = new VBox();
        group_1 = new VBox();
        group_1.setAlignment(Pos.CENTER);
        group_1.setPadding(new Insets(5, 5, 5, 5));
        str_Group1_Title = "Treatment / Population #1";
        txt_Group1_Title = new Text(str_Group1_Title);
    
        str_Group1_SumInfo = "   Summary Information";
        txt_Group1_SumInfo = new Text(str_Group1_SumInfo);
        
        str_OROne = "  Mean #1         StDev #1";
        txt_OROne = new Text(str_OROne);    
        
        hBox_GPOne_SuccessRow = new HBox();
        
        //stf_Mean1 = new SmartTextField(meansHandler, 6, 1);
        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("Mean1");
        al_STF.get(0).setSmartTextField_MB_REAL(true);
        //al_stfForEntry.add(stf_Mean1);
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            bool_Mean1Good = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());
            
            if (bool_Mean1Good) {
                strMean1 = al_STF.get(0).getText();
                mean1 = Double.parseDouble(strMean1);
                al_STF.get(0).setText(String.valueOf(mean1));
            }
        });
       
        //stf_Sigma1 = new SmartTextField(meansHandler, 0, 2);
        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(toBlank); 
        al_STF.get(1).getTextField().setId("Sigma1");
        al_STF.get(1).setSmartTextField_MB_POSITIVE(true);
        //al_stfForEntry.add(stf_Sigma1);
        
        al_STF.get(1).getTextField().setOnAction(e -> {
            bool_Sigma1Good = DataUtilities.strIsAPosDouble(al_STF.get(1).getTextField().getText());
            if (bool_Sigma1Good) {
                sigma1 = Double.parseDouble(al_STF.get(1).getText());
                strSigma1 = String.valueOf(sigma1);
                al_STF.get(1).setText(strSigma1);
                bool_Sigma1Good = true;
            }
        });
        hBox_GPOne_SuccessRow.setAlignment(Pos.CENTER);
        hBox_GPOne_SuccessRow.getChildren()
                             .addAll(al_STF.get(0).getTextField(),
                                     al_STF.get(1).getTextField());
        hBox_GPOne_SuccessRow.setSpacing(25);

        str_Group1_N = "   Group / Sample Size #1";       
        txt_Group1_N = new Text(str_Group1_N);   
        
        //stf_N1 = new SmartTextField(meansHandler, 1, 3);
        al_STF.get(2).getTextField().setPrefColumnCount(12);
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(2).getTextField().setText(toBlank);
        al_STF.get(2).getTextField().setId("SampleSize1");
        al_STF.get(2).setSmartTextField_MB_POSITIVEINTEGER(true);
        //al_stfForEntry.add(stf_N1);

        // ??????  Why am I setting the text again?  Vestigial from prop?
        al_STF.get(2).getTextField().setOnAction(e -> {
            bool_N1Good = DataUtilities.txtFieldHasPosInt(al_STF.get(2).getTextField());
            
            if (bool_N1Good) {
                n1 = Integer.parseInt(al_STF.get(2).getText());
                strN1 = String.valueOf(al_STF.get(2).getText());
                al_STF.get(2).setText(strN1);
                bool_N1Good = true;
            }
        });

        group_1.getChildren().addAll(txt_Group1_Title,
                                         txt_Group1_SumInfo,
                                         txt_OROne,
                                         hBox_GPOne_SuccessRow,
                                         txt_Group1_N,
                                         al_STF.get(2).getTextField());
        
        group_2 = new VBox();
        group_2.setAlignment(Pos.CENTER);
        group_2.setPadding(new Insets(5, 5, 5, 5));
        str_Group2_Title = "Treatment / Population #2";
        txt_Group2_Title = new Text(str_Group2_Title);

        str_Group2_SumInfo = "   Summary Information";
        txt_Group2_SumInfo = new Text(str_Group2_SumInfo);
        
        str_ORTwo = "  Mean #2         StDev #2";
        txt_ORTwo = new Text(str_ORTwo);

        hBox_Group2_SuccessRow = new HBox();
        
        //stf_Mean2 = new SmartTextField(meansHandler, 2, 4);
        al_STF.get(3).getTextField().setPrefColumnCount(12);
        al_STF.get(3).getTextField().setMaxWidth(65);
        al_STF.get(3).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(3).getTextField().setText(toBlank);
        al_STF.get(3).getTextField().setId("Mean2"); 
        al_STF.get(3).setSmartTextField_MB_REAL(true);
        //al_stfForEntry.add(stf_Mean2);
        

        al_STF.get(3).getTextField().setOnAction(e -> {
            bool_Mean2Good = DataUtilities.txtFieldHasDouble(al_STF.get(3).getTextField());
            
            if (bool_Mean2Good) {
                strMean2 = al_STF.get(3).getText();
                mean2 = Double.parseDouble(strMean2);
                al_STF.get(3).setText(String.valueOf(mean2));
            }
        });

        //stf_Sigma2 = new SmartTextField(meansHandler, 3, 5);
        al_STF.get(4).getTextField().setPrefColumnCount(8);
        al_STF.get(4).getTextField().setMaxWidth(50);
        al_STF.get(4).getTextField().setText(toBlank);    
        al_STF.get(4).getTextField().setId("Sigma2");
        al_STF.get(4).setSmartTextField_MB_POSITIVE(true);
        //al_stfForEntry.add(stf_Sigma2);
        
        al_STF.get(4).getTextField().setOnAction(e -> {
            bool_Sigma2Good = DataUtilities.strIsAPosDouble(al_STF.get(4).getTextField().getText());
            
            if (bool_Sigma2Good) {
                sigma2 = Double.parseDouble(al_STF.get(4).getText());
                strSigma2 = String.valueOf(sigma2);
                al_STF.get(4).setText(strSigma2);
                bool_Sigma2Good = true;
            }
        });
        hBox_Group2_SuccessRow.setAlignment(Pos.CENTER);
        hBox_Group2_SuccessRow.getChildren()
                             .addAll(al_STF.get(3).getTextField(),
                                     txt_ORTwo,
                                     al_STF.get(4).getTextField());
        hBox_Group2_SuccessRow.setSpacing(25);
        str_Group2_N = "   Group / Sample Size #2";
        txt_Group2_N = new Text(str_Group2_N);  
        
        //stf_N2 = new SmartTextField(meansHandler, 4, 0);
        al_STF.get(5).getTextField().setPrefColumnCount(8);
        al_STF.get(5).getTextField().setMaxWidth(50);
        al_STF.get(5).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(5).getTextField().setText(toBlank);    
        al_STF.get(5).getTextField().setId("SampleSize2");
        al_STF.get(5).setSmartTextField_MB_POSITIVEINTEGER(true);
        //al_stfForEntry.add(stf_N2);
        
        // ??????  Why am I setting the text again?  Vestigial from prop?

        al_STF.get(5).getTextField().setOnAction(e -> {
            bool_N2Good = DataUtilities.txtFieldHasPosInt(al_STF.get(5).getTextField());
            
            if (bool_N2Good) {
                n2 = Integer.parseInt(al_STF.get(5).getText());
                strN2 = String.valueOf(al_STF.get(5).getText());
                al_STF.get(5).setText(strN2);
                bool_N2Good = true;
            }
        });    

        al_STF.get(0).getTextField().requestFocus();
        
        group_2.getChildren().addAll(txt_Group2_Title,
                                         txt_Group2_SumInfo,
                                         txt_ORTwo,
                                         hBox_Group2_SuccessRow,
                                         txt_Group2_N,
                                         al_STF.get(5).getTextField());      
        
        numValsPanel.getChildren()
                  .addAll(group_1, 
                          sep_Mean1_and_Mean2,
                          group_2);
    }
    
    private void makeInfDecisionsPanel() {
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
        ciBox = new VBox();
        
        ciBox.getChildren().addAll(ciLabel, ciView);
        alphaBox = new VBox();
        alphaBox.getChildren().addAll(alphaLabel, alphaView);

        alphaAndCI = new HBox();
        alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);

        alphaAndCI = new HBox();
        alphaAndCI.setPadding(new Insets(10, 5, 5, 5));
        alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);  
        
        infChoicesPanel = new VBox();
        infChoicesPanel.setAlignment(Pos.CENTER);
        infChoicesPanel.getChildren().add(alphaAndCI);           
    }
    
    private void makeVariableDefPanel() {
        lblMean_1 = new Label(" Mean 1 Label: ");
        lblMean_2 = new Label(" Mean 2 Label: ");
        lblTitle  = new Label("        Title: ");

        tf_Mean_1 = new TextField("First mean");
        tf_Mean_2 = new TextField("Second mean");
        tf_Title  = new TextField("Title ");
        
        tf_Mean_1.setPrefColumnCount(15);
        tf_Mean_2.setPrefColumnCount(15);
        tf_Title.setPrefColumnCount(15);
        
        tf_Mean_1.textProperty().addListener(this::changeProp_1_Description);
        tf_Mean_2.textProperty().addListener(this::changeProp_2_Description);
        tf_Title.textProperty().addListener(this::changeTitle_Description);

        gridChoicesMade = new GridPane();
        gridChoicesMade.setHgap(10);
        gridChoicesMade.setVgap(15);
        
        gridChoicesMade.add(lblMean_1, 0, 0);
        gridChoicesMade.add(lblMean_2, 0, 1);
        
        gridChoicesMade.add(tf_Mean_1, 1, 0);
        gridChoicesMade.add(tf_Mean_2, 1, 1);
        
        gridChoicesMade.add(lblTitle, 0, 2);
        gridChoicesMade.add(tf_Title, 1, 2);
        
        GridPane.setValignment(tf_Mean_1, VPos.BOTTOM);
        GridPane.setValignment(tf_Mean_2, VPos.BOTTOM);
        gridChoicesMade.setPadding(new Insets(0, 10, 0, 0));
        
        vBox_VarsPanel = new VBox(10);
        vBox_VarsPanel.setAlignment(Pos.CENTER_LEFT);
        vBox_VarsPanel.setPadding(new Insets(0, 25, 0, 10));
        vBox_VarsPanel.getChildren().add(gridChoicesMade);            
    }    
    
    private void makeBottomPanel() { 
        bottomPanel = new HBox(10);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        okButton = new Button("Compute");
        cancelButton = new Button("Cancel");
        resetButton = new Button("Reset");
        
        okButton.setOnAction((ActionEvent event) -> {            
        doMissingAndOrWrong();
        
        if (valuesLeftBlank) {
            MyAlerts.showMustBeNonBlankAlert();  
        }
        else 
        if (!allFieldsGood) {
            MyAlerts.showNotAllFieldsGoodAlert();
        }
        else{
            returnStatus = "OK";
            close();
        }  
    });
        
        setOnCloseRequest((WindowEvent t) -> {
            returnStatus = "Cancel";
            close();
        });
        
        cancelButton.setOnAction((ActionEvent event) -> {
            returnStatus = "Cancel";
            close();
        });

        resetButton.setOnAction((ActionEvent event) -> {
            
            for (int ithSTF = 0; ithSTF < 6; ithSTF++) {
                al_STF.get(ithSTF).setText(toBlank); 
            }
            
            bool_Mean1Good = false; 
            bool_Mean2Good = false; 
            bool_Sigma1Good = false; 
            bool_Sigma2Good = false; 
            bool_N1Good = false; 
            bool_N2Good = false;
            
        });
        
        bottomPanel.getChildren().addAll(okButton, cancelButton, resetButton);
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
    
    public void changeProp_1_Description(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tf_Mean_1.setText(newValue); 
        //System.out.println("631, Ind_t_SumStats_Dialog,");
    }


    public void changeProp_2_Description(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tf_Mean_2.setText(newValue); 
        //System.out.println("639, Ind_t_SumStats_Dialog,");
    }  
    
    public void changeTitle_Description(ObservableValue<? extends String> title,
        String oldValue,
        String newValue) {
        tf_Title.setText(newValue); 
        //System.out.println("646, Ind_t_SumStats_Dialog,");
    }      
    
    // The evaluations here will be specific to the dialog
    private void doMissingAndOrWrong() {
        valuesLeftBlank = false;
        
        for (int ithSTF = 0; ithSTF < 6; ithSTF++) {
            if (al_STF.get(ithSTF).isEmpty()) 
                valuesLeftBlank = true;
        }
             
        bool_Mean1Good = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());
        bool_Mean2Good = DataUtilities.txtFieldHasDouble(al_STF.get(3).getTextField());
        bool_Sigma1Good = DataUtilities.txtFieldHasPosDouble(al_STF.get(1).getTextField());
        bool_Sigma2Good = DataUtilities.txtFieldHasPosDouble(al_STF.get(4).getTextField());
        bool_N1Good = DataUtilities.txtFieldHasPosInt(al_STF.get(2).getTextField());
        bool_N2Good = DataUtilities.txtFieldHasPosInt(al_STF.get(5).getTextField());

        allFieldsGood = bool_Mean1Good && bool_Sigma1Good  
                        && bool_Mean2Good && bool_Sigma2Good 
                        && bool_N1Good && bool_N2Good;
    }
    
    public void printTheLot() {
        System.out.println("Mean1Good = " + bool_Mean1Good);
        System.out.println("Mean2Good = " + bool_Mean2Good);
        System.out.println("Sigma1Good  = " + bool_Sigma1Good );
        System.out.println("Sigma2Good  = " + bool_Sigma2Good );
        System.out.println("N1Good = " + bool_N1Good);
        System.out.println("N2Good = " + bool_N2Good);     
        
        System.out.println("\nmean1 = " + al_STF.get(0).getText());
        System.out.println("mean2 = " + al_STF.get(3).getText());
        System.out.println("sigma1  = " + al_STF.get(1).getText());
        System.out.println("sigma2  = " + al_STF.get(4).getText());
        System.out.println("n1 = " + al_STF.get(2).getText());
        System.out.println("n2 = " + al_STF.get(5).getText()); 
    }
    
    public double getAlpha() {  return alphaLevel; }
     
    public String getMean_1_Description() { return tf_Mean_1.getText();}
    public String getMean_2_Description() { return tf_Mean_2.getText();}        
    public String getHypotheses() { return strHypChosen; }    
    public double getLevelOfSignificance() { return alphaLevel; } 
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getHypothesizedDiff() { return hypothesizedDifference; }   
    public int getN1() { return n1; }
    public int getN2() { return n2; }    
    public double getStDev1() {return sigma1; }
    public double getStDev2() {return sigma2; }    
    public double getVariance1() {return sigma1 * sigma1; }
    public double getVariance2() {return sigma2 * sigma2; }    
    public double getXBar1() { return mean1; }
    public double getXBar2() { return mean2; }   
    public double getTheNullDiff() { return daNullDiff; }    
    public String getReturnStatus() { return returnStatus; }
}

