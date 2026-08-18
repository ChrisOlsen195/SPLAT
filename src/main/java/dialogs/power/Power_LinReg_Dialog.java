/************************************************************
 *                   Power_LinReg_Dialog                    *
 *                        06/13/26                          *
 *                         15:00                            *
 ***********************************************************/
/************************************************************
*       Checked for error messages 6/10/26                  *
************************************************************/
package dialogs.power;

import utilityClasses.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import power_LinReg.LinReg_Power_Controller;
import simpleRegression.Inf_Regr_Dashboard;

public class Power_LinReg_Dialog extends Power_Dialog { 
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean valuesLeftBlank;
    boolean boolEffectSizeGood, boolSampleSizeGood, boolAllFieldsGood;
    
    int  maxSampleSize, alphaIndex, ciIndex;            
    double nullSlope, sigma, effectSize, alpha; 
    double[] theAlphaLevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, 
           strAltHypChosen, str_Group_Title, str_Group_SumInfo, str_Mean, 
           strHypChosen, str_Sigma, str_MaxSampleSize, strSlope, strSigma,
           str_EffectSize;
    
    final String strToBlank = "";
    
    ObservableList<String> strCILevels, strAlphaLevels;
    ListView<String> strCIView, strAlphaView;
    
    // My classes
    SmartTextFieldsController stf_Controller;
    SmartTextFieldDoublyLinkedSTF al_STF;
    
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
    
    // My classes
    LinReg_Power_Controller linReg_Power_Controller;
    Inf_Regr_Dashboard inf_Regr_Dashboard;

    public Power_LinReg_Dialog(LinReg_Power_Controller linReg_Power_Controller) {
        super("Quantitative");
        if (printTheStuff) {
            System.out.println("86 --- Power_LinReg_Dialog, Constructing");
            linReg_Power_Controller.get_Inf_Regr_Dashboard();
        }
        this.linReg_Power_Controller = linReg_Power_Controller;
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
        setTitle("Inference for simple regression");
        
        /************************************************************
         *    If this object is closed, it needs to do some stuff   *
         *    in the Dashboard, but not close the dashboard.        *
         ***********************************************************/
        addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                inf_Regr_Dashboard = get_Inf_Regr_Dashboard();
                inf_Regr_Dashboard.restoreNumberSeven();
                close();
            }
        });

        setScene(scene);
    }  
    
private void makeNullsPanel() {
        if (printTheStuff) {
            System.out.println("158 --- Power_LinReg_Dialog, makeNullsPanel()");
        }        
        strHypChosen = "NotEqual";
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        strHypNull = "\u03B2 = k";
        strHypNE = "\u03B2 \u2260 k";
        strHypLT = "\u03B2  < k";
        strHypGT = "\u03B2  > k";
        
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
        if (printTheStuff) {
            System.out.println("222 --- Power_LinReg_Dialog, makeNumericValuesPanel()");
        } 

        str_EffectSize = "Min Effect size: ";
        str_MaxSampleSize = "Max Sample size: ";
               
        lbl_EffectSize = new Label(str_EffectSize);         
        lbl_SampleSize = new Label(str_MaxSampleSize);         
        
        int widthy = 100;
        lbl_EffectSize.setMinWidth(widthy);
        lbl_EffectSize.setMaxWidth(widthy);
        lbl_SampleSize.setMinWidth(widthy);
        lbl_SampleSize.setMaxWidth(widthy);

        lbl_EffectSize.setTextAlignment(TextAlignment.RIGHT);
        lbl_EffectSize.setAlignment(Pos.CENTER_RIGHT);
        lbl_SampleSize.setTextAlignment(TextAlignment.RIGHT);
        lbl_SampleSize.setAlignment(Pos.CENTER_RIGHT);
        
        vBoxNumValsPanel = new VBox();
        vBoxGroup = new VBox();
        vBoxGroup.setAlignment(Pos.CENTER);
        vBoxGroup.setPadding(new Insets(5, 5, 5, 5));
        str_Group_Title = " Please supply these values,   ";
        lbl_Group_Title = new Label(str_Group_Title);
    
        str_Group_SumInfo = "needed to calculate power.";
        lbl_Group_SumInfo = new Label(str_Group_SumInfo);
        
        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMinWidth(65);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setText(strToBlank); 
        al_STF.get(0).getTextField().setId("StDev");
        al_STF.get(0).setSmartTextField_MB_POSITIVE(true);

        al_STF.get(0).getTextField().setOnAction(e -> {
            boolEffectSizeGood = DataUtilities.strIsAPosDouble(al_STF.get(0).getTextField().getText());
            if (boolEffectSizeGood == true) {
                effectSize = Double.parseDouble(al_STF.get(0).getText());
                str_EffectSize = String.valueOf(effectSize);
                al_STF.get(0).setText(str_EffectSize);
                boolEffectSizeGood = true;
            }
        });   

        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMinWidth(65);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(strToBlank); 
        al_STF.get(1).getTextField().setId("StDev");
        al_STF.get(1).setSmartTextField_MB_POSITIVE(true);

        al_STF.get(1).getTextField().setOnAction(e -> {
            boolSampleSizeGood = DataUtilities.strIsANonNegInt(al_STF.get(1).getTextField().getText());
            if (boolSampleSizeGood == true) {
                maxSampleSize = Integer.parseInt(al_STF.get(1).getText());
                str_MaxSampleSize = String.valueOf(maxSampleSize);
                al_STF.get(1).setText(str_MaxSampleSize);
                boolSampleSizeGood = true;
            }
        });   

        gridPanePowerOption = new GridPane();
        gridPanePowerOption.add(lbl_EffectSize, 0, 0); gridPanePowerOption.add(al_STF.get(0).getTextField(),1,0);
        gridPanePowerOption.add(lbl_SampleSize, 0, 1); gridPanePowerOption.add(al_STF.get(1).getTextField(),1,1);
        
        vBoxGroup.getChildren().addAll(lbl_Group_Title,
                                       lbl_Group_SumInfo,
                                       gridPanePowerOption);
        
        al_STF.get(0).getTextField().requestFocus();
        
        vBoxNumValsPanel.getChildren().add(vBoxGroup);
    }
    
    private void makeInfDecisionsPanel() {
        if (printTheStuff) {
            System.out.println("301 --- Power_LinReg_Dialog, makeInfDecisionsPanel()");
        }
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
        if (printTheStuff) {
            System.out.println("359 --- Power_LinReg_Dialog, makeBottomPanel()");
        }
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
        
        btnCancel.setOnAction((ActionEvent event) -> {
            System.out.println("385 --- Power_LinReg_Dialog, Cancel");
            strReturnStatus = "Cancel";
            close();
        });

        btnReset.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(strToBlank); 
            al_STF.get(1).setText(strToBlank); 
            al_STF.get(2).setText(strToBlank);
            al_STF.get(3).setText(strToBlank);     
        });
        
        hBoxBottomPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
    }
    
    // The evaluations here will be specific to the dialog
    private void doMissingAndOrWrong() {
        valuesLeftBlank = al_STF.get(0).isEmpty() || al_STF.get(1).isEmpty();       
           
        boolEffectSizeGood = DataUtilities.strIsAPosDouble(al_STF.get(0).getTextField().getText()); 
        boolSampleSizeGood = DataUtilities.txtFieldHasPosInt(al_STF.get(1).getTextField());

        boolAllFieldsGood = boolEffectSizeGood && boolSampleSizeGood;
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = strCIView.getSelectionModel().getSelectedIndex();
        strAlphaView.getSelectionModel().select(ciIndex);
        alpha = theAlphaLevs[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = strAlphaView.getSelectionModel().getSelectedIndex();
        strCIView.getSelectionModel().select(alphaIndex);
        alpha = theAlphaLevs[alphaIndex];    
    }
    
    public Inf_Regr_Dashboard get_Inf_Regr_Dashboard() {
        return linReg_Power_Controller.get_Inf_Regr_Dashboard();
    }
    public double getAlpha() {  return alpha; }
    public LinReg_Power_Controller get_LinReg_Power_Controller() {
        return linReg_Power_Controller; 
    }
    public Power_LinReg_Dialog get_Power_LinReg_Dialog() { return this; }
    public String getHypotheses() { return strHypChosen; }  
    public double getLevelOfSignificance() { return alpha; }
    public String getAltParam() { return strAltHypChosen; }
    public double getSigma() {return sigma; }
    public double getEffectSize() { return effectSize; }
    public double getNullParam() { return nullSlope; }
    public int getMaxSampleSize() { return maxSampleSize; }
    public String getRejectionCriterion() { return strHypChosen; }
    @Override
    public String getDescriptionOfVariable() { return tfExplanVar.getText(); }
}


