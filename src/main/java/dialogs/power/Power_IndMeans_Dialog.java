/************************************************************
 *                   Power_IndMeans_Dialog                  *
 *                          12/31/25                        *
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

public class Power_IndMeans_Dialog extends Stage { 
    
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean okToContinue, valuesLeftBlank, 
            bool_N1Good, bool_N2Good, bool_Sigma1Good,
            bool_Sigma2Good, bool_EffectSizeGood;
    boolean allFieldsGood; 
    
    int  n1, n2, alphaIndex, ciIndex; //, numbersLeftBlankIndex,

    double sigma1, sigma2, alphaLevel, daNullDiff, nullMeanDiff, 
           nullDiffRequested, minEffectSize, mean1, mean2;
    double[] theAlphaLevels;
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, 
           strAltHypChosen, str_Group1_Title, str_Group1_SumInfo, 
           str_MeanAndSigma_1, str_Group1_N, str_Group2_Title, 
           str_Group2_SumInfo, str_MeanAndSigma_2, str_Group2_N,
           resultAsString, strHypChosen, str_EffectSize, strSigma1, 
           strSigma2, strN1, strN2, strReturnStatus;
    
    final String toBlank = "";
    
    final String strBadDouble = "Ok, so here's the deal.  There are numbers, and there are other"
                        + "\nthan numbers, like words and punctuation.  What you must enter in"
                        + "\nthis field are numbers, specifically numbers of the Arabic persuation."
                        + "\nThe Decline and Fall of the Roman Empire included the Decline and"
                        + "\nFall of Roman Numerals.  Now, let's try this number thing again...";

    
    final String wtfString = "What!?!?  My hypothesized null difference of zero is not good"
                                    + "\nenough for you? It is pretty unusual that a non-zero difference"
                                    + "\nwould be hypothesized, but I'm willing to believe you know what"
                                    + "\nyou're doing. No skin off MY nose...";
    
    ObservableList<String> ciLevels, alphaLevels;
    ListView<String> ciView, alphaView;
    
    // My classes
    SmartTextFieldsController stf_Controller;
    SmartTextFieldDoublyLinkedSTF al_STF; 
 
    // JavaFX POJOs
    Button changeNull, okButton, cancelButton, resetButton;;

    HBox middlePanel, bottomPanel, hBox_GPOne_Mean_Sigma_Row, hBox_Group2_Mean_Sigma_Row,
         alphaAndCI, hBoxCurrDiff;

    Label lblNullAndAlt, lbl_Title, ciLabel, alphaLabel; 
    
    RadioButton hypNE, hypLT, hypGT;

    VBox root, nullsPanel, vBox_NumValsPanel, group_Mean_1, group_Mean_2,
         ciBox, alphaBox, infChoicesPanel, vBoxEffectSize; 
    
    final Text currNullDiff = new Text("Current null diff: (\u03BC\u2081 - \u03BC\u2082) = ");
    TextField tf_HypDiff;
    TextInputDialog txtDialog;
  
    Scene scene;
    
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndBottom,
              sep_Mean1_and_Mean2, sep_Alpha, sep;  
    
    Text txt_Group1_Title, txt_Group1_SumInfo, txt_MeanAndSigma_1, txt_Group1_N,
         txt_Group2_Title, txt_Group2_SumInfo, txt_MeanAndSigma_2, txt_Group2_N,
         txt_EffectSize;
    
    public Power_IndMeans_Dialog() {
        if (printTheStuff) {
            System.out.println("*** 103 Power_IndMeans_Dialog, Constructing");
        }
        theAlphaLevels = new double[] { 0.10, 0.05, 0.01};

        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        strReturnStatus = "Ok";

        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set                          *
        stf_Controller.setSize(5);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();        
        
        lbl_Title = new Label("Power for two independent means");
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
        makeBottomPanel();
        
        middlePanel = new HBox();
        middlePanel.setSpacing(30);       
        middlePanel.getChildren().addAll(nullsPanel, sep_NullsFromInf,
                                         infChoicesPanel,sep_InfFromNumbers,
                                         vBox_NumValsPanel);
        middlePanel.setAlignment(Pos.CENTER);        
        root.getChildren().addAll(lbl_Title, 
                                  sep_NullsFromInf,
                                  middlePanel,
                                  sep_MiddleAndBottom,
                                  bottomPanel);        
        
        scene = new Scene (root, 750, 400);
        setTitle("Power for a difference in means");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        setScene(scene);
    }  
    
private void makeNullsPanel() { 
        if (printTheStuff) {
            System.out.println("*** 164 Power_IndMeans_Dialog, makeNullsPanel()");
        }
        nullMeanDiff = 0.0;
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
                        Alert badValue = new Alert(Alert.AlertType.ERROR);
                        badValue.setTitle("Warning! Must be a real number");
                        badValue.setHeaderText("You have entered something other than a number.");
                        badValue.setContentText(strBadDouble);
                        badValue.showAndWait();
                        txtDialog.setContentText("");
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
        if (printTheStuff) {
            System.out.println("*** 277 Power_IndMeans_Dialog,makeNumericValuesPanel()");
        }
        vBox_NumValsPanel = new VBox();
        group_Mean_1 = new VBox();
        group_Mean_1.setAlignment(Pos.CENTER);
        group_Mean_1.setPadding(new Insets(5, 5, 5, 5));
        str_Group1_Title = "Treatment / Population #1";
        txt_Group1_Title = new Text(str_Group1_Title);
    
        str_Group1_SumInfo = "   Summary Information";
        txt_Group1_SumInfo = new Text(str_Group1_SumInfo);
        
        str_MeanAndSigma_1 = "  N#1                StDev #1";
        txt_MeanAndSigma_1 = new Text(str_MeanAndSigma_1);  
        
        str_EffectSize = "Min Effect size: ";
        txt_EffectSize = new Text(str_EffectSize);
        
        hBox_GPOne_Mean_Sigma_Row = new HBox();
        
        al_STF.get(0).setPrefColumnCount(12);   // stf_N1
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("Mean1");
        al_STF.get(0).setSmartTextField_MB_POSITIVEINTEGER(true);
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            bool_N1Good = DataUtilities.strIsAPosInt(al_STF.get(0).getText());
            
            if (bool_N1Good) {
                strN1 = al_STF.get(0).getText();
                n1 = Integer.parseInt(strN1);
                al_STF.get(0).setText(String.valueOf(n1));
            }
        });
       
        al_STF.get(1).getTextField().setPrefColumnCount(12);   // stf_Sigma1
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(toBlank); 
        al_STF.get(1).getTextField().setId("Sigma1");
        al_STF.get(1).setSmartTextField_MB_POSITIVE(true);
        
        al_STF.get(1).getTextField().setOnAction(e -> {
            bool_Sigma1Good = DataUtilities.strIsAPosDouble(al_STF.get(1).getText());
            
            if (bool_Sigma1Good) {
                sigma1 = Double.parseDouble(al_STF.get(1).getText());
                strSigma1 = String.valueOf(sigma1);
                al_STF.get(1).setText(strSigma1);
            }
        });
        
        hBox_GPOne_Mean_Sigma_Row.setAlignment(Pos.CENTER);
        hBox_GPOne_Mean_Sigma_Row.getChildren()
                                 .addAll(al_STF.get(0).getTextField(),
                                     al_STF.get(1).getTextField());
        hBox_GPOne_Mean_Sigma_Row.setSpacing(25);

        str_Group1_N = "   Group / Sample Size #1";        
        txt_Group1_N = new Text(str_Group1_N);   
        
        group_Mean_1.getChildren().addAll(txt_Group1_Title,
                                          txt_Group1_SumInfo,
                                          txt_MeanAndSigma_1,
                                          hBox_GPOne_Mean_Sigma_Row);
        
        group_Mean_2 = new VBox();
        group_Mean_2.setAlignment(Pos.CENTER);
        group_Mean_2.setPadding(new Insets(5, 5, 5, 5));
        str_Group2_Title = "Treatment / Population #2";
        txt_Group2_Title = new Text(str_Group2_Title);
        str_Group2_SumInfo = "   Summary Information";
        txt_Group2_SumInfo = new Text(str_Group2_SumInfo);        
        str_MeanAndSigma_2 = "    N#2                StDev #2";
        txt_MeanAndSigma_2 = new Text(str_MeanAndSigma_2);
        hBox_Group2_Mean_Sigma_Row = new HBox();
        
        al_STF.get(2).getTextField().setPrefColumnCount(12);    //  stf_N2
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(2).getTextField().setText(toBlank);
        al_STF.get(2).getTextField().setId("Mean2"); 
        al_STF.get(2).setSmartTextField_MB_POSITIVEINTEGER(true);
        
        al_STF.get(2).getTextField().setOnAction(e -> {
            bool_N2Good = DataUtilities.strIsAPosInt(al_STF.get(2).getText());
            
            if (bool_N2Good) {
                strN2 = al_STF.get(2).getText();
                n2 = Integer.parseInt(strN2);
                al_STF.get(2).setText(String.valueOf(n2));
            }
        });

        al_STF.get(3).getTextField().setPrefColumnCount(12);    //  stf_Sigma2
        al_STF.get(3).getTextField().setMaxWidth(50);
        al_STF.get(3).getTextField().setText(toBlank);    
        al_STF.get(3).getTextField().setId("Sigma2");
        al_STF.get(3).setSmartTextField_MB_POSITIVE(true);
        
        al_STF.get(3).getTextField().setOnAction(e -> {
            bool_Sigma2Good = DataUtilities.strIsAPosDouble(al_STF.get(3).getText());
            
            if (bool_Sigma2Good) {
                sigma2 = Double.parseDouble(al_STF.get(3).getText());
                strSigma2 = String.valueOf(sigma2);
                al_STF.get(3).setText(strSigma2);
            }
        });
        
        hBox_Group2_Mean_Sigma_Row.setAlignment(Pos.CENTER);
        hBox_Group2_Mean_Sigma_Row.getChildren()
                                  .addAll(al_STF.get(2).getTextField(),
                                     txt_MeanAndSigma_2,
                                     al_STF.get(3).getTextField());
        hBox_Group2_Mean_Sigma_Row.setSpacing(25);
        str_Group2_N = "   Group / Sample Size #2";
        txt_Group2_N = new Text(str_Group2_N);          
        group_Mean_2.getChildren().addAll(txt_Group2_Title,
                                         txt_Group2_SumInfo,
                                         txt_MeanAndSigma_2,
                                         hBox_Group2_Mean_Sigma_Row);       

        al_STF.get(4).getTextField().setPrefColumnCount(12);    //  Effect size
        al_STF.get(4).getTextField().setMinWidth(65);
        al_STF.get(4).getTextField().setMaxWidth(65);
        al_STF.get(4).getTextField().setText(toBlank); 
        al_STF.get(4).getTextField().setId("EffectSize");
        al_STF.get(4).setSmartTextField_MB_REAL(true);
        
        al_STF.get(4).getTextField().setOnAction(e -> {
            bool_EffectSizeGood = DataUtilities.strIsADouble(al_STF.get(4).getText());
            if (bool_EffectSizeGood) {
                minEffectSize = Double.parseDouble(al_STF.get(4).getText());
                str_EffectSize = String.valueOf(minEffectSize);
                al_STF.get(4).setText(str_EffectSize);
            }
        }); 
        
        vBoxEffectSize = new VBox();
        vBoxEffectSize.setPadding(new Insets(5, 10, 5, 5));
        vBoxEffectSize.getChildren().addAll(txt_EffectSize, al_STF.get(4).getTextField() );
        
        vBox_NumValsPanel.getChildren()
                  .addAll(group_Mean_1, 
                          sep_Mean1_and_Mean2,
                          group_Mean_2,
                          vBoxEffectSize);
        
        al_STF.get(0).getTextField().requestFocus();
    }
    
    private void makeInfDecisionsPanel() {
        if (printTheStuff) {
            System.out.println("*** 432 Power_IndMeans_Dialog, makeInfDecisionsPanel()");
        }
        nullMeanDiff = 0.;
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
    
    private void makeBottomPanel() { 
        if (printTheStuff) {
            System.out.println("*** 493 Power_IndMeans_Dialog, makeBottomPanel()");
        }
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
                strReturnStatus = "OK";
                close();
            }  
        });
        
        setOnCloseRequest((WindowEvent t) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        cancelButton.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });

        resetButton.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(toBlank); 
            al_STF.get(1).setText(toBlank);
            al_STF.get(2).setText(toBlank); 
            al_STF.get(3).setText(toBlank);
            al_STF.get(4).setText(toBlank);
            
            bool_N1Good = false; 
            bool_N2Good = false; 
            bool_Sigma1Good = false; 
            bool_Sigma2Good = false;             
        });
        
        bottomPanel.getChildren().addAll(okButton, cancelButton, resetButton);
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = ciView.getSelectionModel().getSelectedIndex();
        alphaView.getSelectionModel().select(ciIndex);
        alphaLevel = theAlphaLevels[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = alphaView.getSelectionModel().getSelectedIndex();
        ciView.getSelectionModel().select(alphaIndex);
        alphaLevel = theAlphaLevels[alphaIndex];  
    }
    
    private void doMissingAndOrWrong() {
        valuesLeftBlank = al_STF.get(0).isEmpty() || al_STF.get(1).isEmpty()   
                       || al_STF.get(2).isEmpty() || al_STF.get(3).isEmpty();               
        bool_N1Good = DataUtilities.strIsAPosInt(al_STF.get(0).getText());
        bool_N2Good = DataUtilities.strIsAPosInt(al_STF.get(2).getText());
        bool_Sigma1Good = DataUtilities.strIsAPosDouble(al_STF.get(1).getText());
        bool_Sigma2Good = DataUtilities.strIsAPosDouble(al_STF.get(3).getText());
        bool_EffectSizeGood = DataUtilities.strIsADouble(al_STF.get(4).getText());      
        allFieldsGood = bool_N1Good && bool_Sigma1Good  
                        && bool_N2Good && bool_Sigma2Good 
                        && bool_EffectSizeGood;
    }
    
    public void printTheLot() {
        System.out.println("     N1Good = " + bool_N1Good);
        System.out.println("     N2Good = " + bool_N2Good);
        System.out.println("Sigma1Good  = " + bool_Sigma1Good );
        System.out.println("Sigma2Good  = " + bool_Sigma2Good );
        System.out.println("  EffSize = " + bool_EffectSizeGood);
        
        System.out.println("\n          N1 = " + al_STF.get(0).getText());
        System.out.println("          N2 = " + al_STF.get(2).getText());
        System.out.println("     sigma1  = " + al_STF.get(1).getText());
        System.out.println("     sigma2  = " + al_STF.get(3).getText());
        System.out.println("Effect size  = " + al_STF.get(4).getText());
    }
    
    public double getAlpha() { return alphaLevel; }    
    public String getRejectionCriterion() { return strHypChosen; }    
    public double getLevelOfSignificance() { return alphaLevel; } 
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getAltDiff() { return nullMeanDiff; }   
    public int getN1() { return n1; }
    public int getN2() { return n2; }    
    public double getStDev1() {return sigma1; }
    public double getStDev2() {return sigma2; }    
    public double getMean_1() { return mean1; }
    public double getMean_2() { return mean2; }    
    public double getTheNullDiff() { return daNullDiff; }    
    public double getMinEffectSize() { return minEffectSize; }    
    public String getReturnStatus() { return strReturnStatus; }
}