/************************************************************
 *                 Power_SingleMean_Dialog                  *
 *                          05/26/24                        *
 *                            21:00                         *
 ***********************************************************/
/************************************************************
*       Checked for error messages 3/25/20                  *
************************************************************/
package dialogs.power;

import utilityClasses.*;
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

public class Power_SingleMean_Dialog extends Power_Dialog { 
    
    // POJOs
    boolean valuesLeftBlank;
    boolean boolNullMeanGood, boolSigmaGood, 
            boolEffectSizeGood, boolSampleSizeGood, boolAllFieldsGood;
    
    int  sampleSize, alphaIndex, ciIndex;
    double nullMean, sigma, effectSize, alphaLevel; 
    double[] theAlphaLevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, 
           strAltHypChosen, str_Group_Title, str_Group_SumInfo, str_Mean, 
           strHypChosen, str_Sigma, str_SampleSize, strMean, strSigma,
           str_EffectSize;
    
    final String strToBlank = "";
    
    ObservableList<String> strCILevels, strAlphaLevels;
    ListView<String> strCIView, strAlphaView;
    
    // My classes
    SmartTextFieldsController stf_Controller;
    DoublyLinkedSTF al_STF;
    
    // JavaFX POJOs
    RadioButton hypNE, hypLT, hypGT;
    
    GridPane gridPanePowerOption;

    Label lblNullAndAlt, lblCILabel, lblAlphaLabel;
    HBox hBoxBottomPanel, hBoxAlphaAndCI;

    VBox root, vBoxNullsPanel, vBoxNumValsPanel, vBoxGroup,
         vBoxCIBox, vBoxAlphaBox, vBoxInfChoicesPanel; 
    
    TextInputDialog txtInputDialog;
  
    Scene scene;
    Separator sepNullsFromInf, sepInfFromNumbers, sepMiddleAndBottom,
              sepAlpha, sep;  
    
    Label lbl_Group_Title, lbl_Group_SumInfo, lbl_Mean, lbl_Sigma,
         lbl_EffectSize, lbl_SampleSize;

    public Power_SingleMean_Dialog() {
        super("Quantitative");
        System.out.println("\n76 Power_SingleMean_Dialog, Constructing");
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        
        strReturnStatus = "Cancel";

        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set                          *
        stf_Controller.setSize(4);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();       

        lblTitle = new Label("Power for a single mean");
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
        setTitle("Inference for a single mean");

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

        vBoxNullsPanel = new VBox();
        
        vBoxNullsPanel.getChildren()
                      .addAll(lblNullAndAlt, hypNE, hypLT, hypGT);
        
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
            hypNE.setSelected(false);
            hypLT.setSelected(false);
            hypGT.setSelected(true);
            strHypChosen = "GreaterThan";
        });         
    }
 
    private void makeNumericValuesPanel() {
        str_Mean = "Null mean: ";
        str_Sigma = "Sigma: ";
        str_EffectSize = "Min Effect size: ";
        str_SampleSize = "Sample size: ";
        
        lbl_Mean = new Label(str_Mean);  
        lbl_Sigma = new Label(str_Sigma);        
        lbl_EffectSize = new Label(str_EffectSize);         
        lbl_SampleSize = new Label(str_SampleSize);         
        
        int widthy = 100;
        lbl_Mean.setMinWidth(widthy);
        lbl_Mean.setMaxWidth(widthy);
        lbl_Sigma.setMinWidth(widthy);
        lbl_Sigma.setMaxWidth(widthy);
        lbl_EffectSize.setMinWidth(widthy);
        lbl_EffectSize.setMaxWidth(widthy);
        lbl_SampleSize.setMinWidth(widthy);
        lbl_SampleSize.setMaxWidth(widthy);
        
        lbl_Mean.setTextAlignment(TextAlignment.RIGHT);
        lbl_Mean.setAlignment(Pos.CENTER_RIGHT);
        lbl_Sigma.setTextAlignment(TextAlignment.RIGHT);
        lbl_Sigma.setAlignment(Pos.CENTER_RIGHT);
        lbl_EffectSize.setTextAlignment(TextAlignment.RIGHT);
        lbl_EffectSize.setAlignment(Pos.CENTER_RIGHT);
        lbl_SampleSize.setTextAlignment(TextAlignment.RIGHT);
        lbl_SampleSize.setAlignment(Pos.CENTER_RIGHT);
        
        vBoxNumValsPanel = new VBox();
        vBoxGroup = new VBox();
        vBoxGroup.setAlignment(Pos.CENTER);
        vBoxGroup.setPadding(new Insets(5, 5, 5, 5));
        str_Group_Title = "Treatment / Population   ";
        lbl_Group_Title = new Label(str_Group_Title);
    
        str_Group_SumInfo = "   Summary Information";
        lbl_Group_SumInfo = new Label(str_Group_SumInfo);

        //stf_Mean = new SmartTextField(meansHandler, 3, 1);
        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMinWidth(65);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(strToBlank);
        al_STF.get(0).getTextField().setId("Mean");
        al_STF.get(0).setSmartTextField_MB_REAL(true);
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            boolNullMeanGood = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());
            if (boolNullMeanGood == true) {
                strMean = al_STF.get(0).getText();
                nullMean = Double.parseDouble(strMean);
                al_STF.get(0).setText(String.valueOf(nullMean));
            }
        });

        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMinWidth(65);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(strToBlank); 
        al_STF.get(1).getTextField().setId("StDev");
        al_STF.get(1).setSmartTextField_MB_POSITIVE(true);
        al_STF.get(1).getTextField().setOnAction(e -> {
            boolSigmaGood = DataUtilities.strIsAPosDouble(al_STF.get(1).getTextField().getText());
            if (boolSigmaGood == true) {
                sigma = Double.parseDouble(al_STF.get(1).getText());
                strSigma = String.valueOf(sigma);
                al_STF.get(1).setText(strSigma);
                boolSigmaGood = true;
            }
        });
        
        al_STF.get(2).getTextField().setPrefColumnCount(12);
        al_STF.get(2).getTextField().setMinWidth(65);
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setText(strToBlank); 
        al_STF.get(2).getTextField().setId("StDev");
        al_STF.get(2).setSmartTextField_MB_POSITIVE(true);
        //al_stfForEntry.add(stf_EffectSize);
        al_STF.get(2).getTextField().setOnAction(e -> {
            boolEffectSizeGood = DataUtilities.strIsAPosDouble(al_STF.get(2).getTextField().getText());
            if (boolEffectSizeGood == true) {
                effectSize = Double.parseDouble(al_STF.get(2).getText());
                str_EffectSize = String.valueOf(effectSize);
                al_STF.get(2).setText(str_EffectSize);
                boolEffectSizeGood = true;
            }
        });   

        //stf_SampleSize = new SmartTextField(meansHandler, 2, 0);
        al_STF.get(3).getTextField().setPrefColumnCount(12);
        al_STF.get(3).getTextField().setMinWidth(65);
        al_STF.get(3).getTextField().setMaxWidth(65);
        al_STF.get(3).getTextField().setText(strToBlank); 
        al_STF.get(3).getTextField().setId("StDev");
        al_STF.get(3).setSmartTextField_MB_POSITIVE(true);
        //al_stfForEntry.add(stf_SampleSize);
        al_STF.get(3).getTextField().setOnAction(e -> {
            boolSampleSizeGood = DataUtilities.strIsANonNegInt(al_STF.get(3).getTextField().getText());
            if (boolSampleSizeGood == true) {
                sampleSize = Integer.parseInt(al_STF.get(3).getText());
                str_SampleSize = String.valueOf(sampleSize);
                al_STF.get(3).setText(str_SampleSize);
                boolSampleSizeGood = true;
            }
        });   

        gridPanePowerOption = new GridPane();
        gridPanePowerOption.add(lbl_Mean, 0, 0); gridPanePowerOption.add(al_STF.get(0).getTextField(),1,0);
        gridPanePowerOption.add(lbl_Sigma, 0, 1); gridPanePowerOption.add(al_STF.get(1).getTextField(),1,1);
        gridPanePowerOption.add(lbl_EffectSize, 0, 2); gridPanePowerOption.add(al_STF.get(2).getTextField(),1,2);
        gridPanePowerOption.add(lbl_SampleSize, 0, 3); gridPanePowerOption.add(al_STF.get(3).getTextField(),1,3);
        
        vBoxGroup.getChildren().addAll(lbl_Group_Title,
                                       lbl_Group_SumInfo,
                                       gridPanePowerOption);
        
        al_STF.get(0).getTextField().requestFocus();
        
        vBoxNumValsPanel.getChildren().add(vBoxGroup);
    }
    
    private void makeInfDecisionsPanel() {
        lblCILabel = new Label("   Select conf level");
        lblCILabel.setMaxWidth(120);
        lblCILabel.setMinWidth(120);
        strCILevels = FXCollections.<String>observableArrayList("          90%", "          95%", "          99%");
        strCIView = new ListView<>(strCILevels);
        strCIView.setOrientation(Orientation.VERTICAL);
        strCIView.setPrefSize(120, 100);
        
        strCIView.getSelectionModel()
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
        strAlphaView = new ListView<>(strAlphaLevels);
        strAlphaView.setOrientation(Orientation.VERTICAL);
        strAlphaView.setPrefSize(120, 100);
        
        strAlphaView.getSelectionModel()
                 .selectedItemProperty()
                 .addListener((new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                       final String oldvalue, final String newvalue) {
                         alphaChanged(ov, oldvalue, newvalue);
                    }
              }));
        
        strAlphaView.getSelectionModel().select(1);    //  Set at .05
        strCIView.getSelectionModel().select(1);   //  Set at 95%
        vBoxCIBox = new VBox();
        
        vBoxCIBox.getChildren().addAll(lblCILabel, strCIView);
        vBoxAlphaBox = new VBox();
        vBoxAlphaBox.getChildren().addAll(lblAlphaLabel, strAlphaView);

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
            if (valuesLeftBlank) {
                MyAlerts.showMustBeNonBlankAlert();  
            }
            else 
            if (!boolAllFieldsGood) {
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
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });

        btnReset.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(strToBlank); 
            al_STF.get(1).setText(strToBlank); 
            al_STF.get(2).setText(strToBlank);
            al_STF.get(3).setText(strToBlank);
            boolNullMeanGood = false; 
            boolSigmaGood = false;        
        });
        
        hBoxBottomPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
    }
    
    // The evaluations here will be specific to the dialog
    private void doMissingAndOrWrong() {
        valuesLeftBlank = al_STF.get(0).isEmpty()
                          || al_STF.get(1).isEmpty()       
                          || al_STF.get(2).isEmpty()
                          || al_STF.get(3).isEmpty();       
        
        boolNullMeanGood = DataUtilities.txtFieldHasDouble(al_STF.get(0).getTextField());      
        boolSigmaGood = DataUtilities.strIsAPosDouble(al_STF.get(1).getTextField().getText());    
        boolEffectSizeGood = DataUtilities.strIsAPosDouble(al_STF.get(2).getTextField().getText()); 
        boolSampleSizeGood = DataUtilities.txtFieldHasPosInt(al_STF.get(3).getTextField());

        boolAllFieldsGood = boolNullMeanGood && boolSigmaGood 
                     && boolEffectSizeGood && boolSampleSizeGood;
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = strCIView.getSelectionModel().getSelectedIndex();
        strAlphaView.getSelectionModel().select(ciIndex);
        alphaLevel = theAlphaLevs[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = strAlphaView.getSelectionModel().getSelectedIndex();
        strCIView.getSelectionModel().select(alphaIndex);
        alphaLevel = theAlphaLevs[alphaIndex];    
    }
    
    public double getAlpha() {  return alphaLevel; }
        
    public String getHypotheses() { return strHypChosen; }  
    public double getLevelOfSignificance() { return alphaLevel; }
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getSigma() {return sigma; }
    public double getEffectSize() { return effectSize; }
    public double getNullMean() { return nullMean; }
    public int getSampleSize() { return sampleSize; }
    public String getRejectionCriterion() { return strHypChosen; }
    public String getDescriptionOfVariable() { return tfExplanVar.getText(); }
}


