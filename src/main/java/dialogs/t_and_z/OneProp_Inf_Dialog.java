/************************************************************
 *                     OneProp_Inf_Dialog                   *
 *                          12/13/25                        *
 *                            18:00                         *
 ***********************************************************/
package dialogs.t_and_z;

import dialogs.Splat_Dialog;
import utilityClasses.MyAlerts;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import smarttextfield.*;
import the_z_procedures.OneProp_Inf_Model;

public class OneProp_Inf_Dialog extends Splat_Dialog { 
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean dlg_okToContinue;
    boolean bool_PropGood, bool_SuccGood, bool_NGood;
    boolean allFieldsGood;
    
    int succ, n, alphaIndex, ciIndex;
    double prop, significanceLevel;
    double hypothesizedProp, daNewNullProp;
    double[] theAlphaLevs;
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, strAltHypChosen,  
           str_Group_Title, str_Group1_SumInfo, str_OROne, str_Group_N,
           resultAsString, strProp1, strN;
    final String toBlank = "";
    ListView<String> ciView, alphaView;
    ObservableList<String> ciLevels, alphaLevels;
    final Text currNullProp = new Text("   Current null hypothesis: p\u2080 = ");

    // My classes
    OneProp_Inf_Model oneProp_Inf_Model;
    SmartTextFieldDoublyLinkedSTF al_STF;
    SmartTextFieldsController stf_Controller;

    // JavaFX POJOs
    Button btn_ChangeNull, btn_Reset;

    GridPane gridChoicesMade;
    HBox hBox_MiddlePanel, hBox_BottomPanel, hBox_SuccessRow,
         hBox_AlphaAndCI, hBox_CurrProp, hBox_YesNoHypoth;

    Label lblNullAndAlt, lbl_Title, ciLabel, alphaLabel, lbl_GraphTitle, 
          lbl_GraphProp;

    RadioButton rb_HypNE, rb_HypLT, rb_HypGT, rb_HypothYes, rb_HypothNo;
    Region spacer;
    Text txt_Group1_Title, txt_Group1_SumInfo, txt_OROne, txt_Group_N;
    TextField tf_HypProp, tf_GraphTitle, tf_GraphProp;
    TextInputDialog txtDialog;
    
    Scene scene;
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndVars,
              sep_Alpha, sep, sep_VarsAndBottom;  

    VBox root, vBox_NullsPanel, vBox_NumValsPanel, vBox_Group,
         vBox_CI, vBox_Alpha, vBox_InfChoicesPanel, vBox_VarsPanel; 

    public OneProp_Inf_Dialog(OneProp_Inf_Model oneProp_Inf_Model) {
        if (printTheStuff) {
            System.out.println("*** 93 OneProp_Inference_Dialog, Constructing");
        }
        this.oneProp_Inf_Model = oneProp_Inf_Model;
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        strReturnStatus = "OK";
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);

        root = new VBox();
        root.setAlignment(Pos.CENTER);

        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set
        stf_Controller.setSize(3);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        
        lbl_Title = new Label("Inference for a proportion");
        lbl_Title.setPadding(new Insets(10, 5, 10, 10));        
        lbl_Title.getStyleClass().add("dialogTitle");
        
        lbl_Title.setPadding(new Insets(10, 5, 10, 10));
        sep_NullsFromInf = new Separator();
        sep_NullsFromInf.setOrientation(Orientation.VERTICAL);
        sep_InfFromNumbers = new Separator();
        sep_InfFromNumbers.setOrientation(Orientation.VERTICAL);
        sep_MiddleAndVars = new Separator();
        sep_VarsAndBottom = new Separator();
        sep_Alpha = new Separator();
        sep_Alpha.setMinHeight(10);

        makeInfDecisionsPanel();
        makeNullsPanel();
        makeNumericValuesPanel();
        makeVariableDefPanel();
        makeBottomPanel();
        boolGoodToGo = true;
        strReturnStatus = "OK";
        
        hBox_MiddlePanel = new HBox();
        hBox_MiddlePanel.setSpacing(25);
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
        
        scene = new Scene (root, 800, 500);
        setTitle("Inference for a proportion");
        setScene(scene);
    }  

    private void makeNullsPanel() { 
        if (printTheStuff) {
            System.out.println("*** 155 OneProp_Inference_Dialog, makeNullsPanel()");
        }
        vBox_NullsPanel = new VBox();
        hBox_YesNoHypoth = new HBox();
        hBox_YesNoHypoth.setMinSize(150, 40);
        hBox_YesNoHypoth.setMaxSize(150, 40);
        Label hypothQuery = new Label("I have a hypothesis to test");
        hypothQuery.setPadding(new Insets(5, 5, 5, 10));
        rb_HypothYes = new RadioButton("Yes");
        rb_HypothYes.setSelected(false);
        rb_HypothNo = new RadioButton("No");   
        rb_HypothYes.setSelected(false);
        rb_HypothNo.setSelected(true);
        spacer = new Region();
        hBox_YesNoHypoth.getChildren().addAll(rb_HypothYes, spacer, rb_HypothNo);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        hBox_YesNoHypoth.setPadding(new Insets(5, 5, 5, 10));
        
        rb_HypothYes.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            rb_HypothYes.setSelected(true);
            rb_HypothNo.setSelected(false);
        });
            
        rb_HypothNo.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            rb_HypothYes.setSelected(false);
            rb_HypothNo.setSelected(true);
            rb_HypNE.setSelected(false);
            rb_HypLT.setSelected(false);
            rb_HypGT.setSelected(false);
        });        

        strAltHypChosen = "NotEqual";
        btn_ChangeNull = new Button("Change null hypothesis");
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        lblNullAndAlt.setMinHeight(55.);
        
        //             Props
        strHypNull = "   p  = p\u2080";
        strHypNE = "   p  \u2260 p\u2080";
        strHypLT = "   p  < p\u2080";
        strHypGT = "   p  > p\u2080";
        
        rb_HypNE = new RadioButton(strHypNull + "\n" + strHypNE);
        rb_HypNE.setMinHeight(55.);
        rb_HypLT = new RadioButton(strHypNull + "\n" + strHypLT);
        rb_HypLT.setMinHeight(55.);
        rb_HypGT = new RadioButton(strHypNull + "\n" + strHypGT);
        rb_HypGT.setMinHeight(55.);
        
        // top, right, bottom, left
        rb_HypNE.setPadding(new Insets(10, 5, 10, 10));
        rb_HypLT.setPadding(new Insets(10, 5, 10, 10));
        rb_HypGT.setPadding(new Insets(10, 5, 10, 10));
        
        rb_HypNE.setSelected(true);
        rb_HypLT.setSelected(false);
        rb_HypGT.setSelected(false);
        
        hypothesizedProp = 0.5; 
        tf_HypProp = new TextField("0.5");
        tf_HypProp.setMinWidth(75);
        tf_HypProp.setMaxWidth(75);
        hBox_CurrProp = new HBox();
        hBox_CurrProp.getChildren().addAll(currNullProp, tf_HypProp);        
        
        vBox_NullsPanel.getChildren().addAll(hypothQuery, hBox_YesNoHypoth,
                                       lblNullAndAlt, 
                                       rb_HypNE, 
                                       rb_HypLT, 
                                       rb_HypGT,
                                       btn_ChangeNull, hBox_CurrProp);
        
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
            if (rb_HypothYes.isSelected()) {
                    dlg_okToContinue = false;
                    
                    while (!dlg_okToContinue) {
                        dlg_okToContinue = true;
                        txtDialog = new TextInputDialog("");
                        txtDialog.setContentText("What proportion would you like to test? ");                     
                        Optional<String> result = txtDialog.showAndWait();
                        if (result.isPresent()) {
                            resultAsString = result.get();                        
                        }
                        if (result.isPresent()) {
                            dlg_okToContinue = true;
                            try {
                                daNewNullProp = Double.parseDouble(resultAsString);
                            }
                            catch (NumberFormatException ex ){ 
                                dlg_okToContinue = false;
                                MyAlerts.showGenericBadNumberAlert(" real ");
                                txtDialog.setContentText("");
                                dlg_okToContinue = false;
                                daNewNullProp = 0.5;
                            }

                            if ((daNewNullProp <= 0.0) || (daNewNullProp >= 1.0)) {
                                dlg_okToContinue = false;
                                MyAlerts.showGenericBadNumberAlert("proportion");
                                txtDialog.setContentText("");
                                dlg_okToContinue = false;
                                daNewNullProp = 0.5;                        
                            }
                        }
                        else {
                            daNewNullProp = 0.5; // Null returns to 0.5 if Cancel
                        }
                    }
                    hypothesizedProp = daNewNullProp;
                    tf_HypProp.setText(String.valueOf(hypothesizedProp));
                } else {
                // Warn that hyp to test button not seletced
                    Alert hypCheck = new Alert(Alert.AlertType.INFORMATION);
                    hypCheck.setTitle("You want to change the null hypothesis?");
                    hypCheck.setHeaderText("Well, you haven't informed me of that yet.");
                    hypCheck.setContentText("Ok, so here's the deal.  If you want to test a hypothesis about"
                                + "\na population proportion, you were supposed to tell me that"
                                + "\nat the top so I could make some preparations.  No big deal,"
                                + "\nyou can still do that now... ");
                    hypCheck.showAndWait();     
                    }
                }
        );
    }
 
    private void makeNumericValuesPanel() {
        if (printTheStuff) {
            System.out.println("*** 321 OneProp_Inference_Dialog, makeNumericValuesPanel()");
        }
        vBox_NumValsPanel = new VBox();
        vBox_Group = new VBox();
        vBox_Group.setAlignment(Pos.CENTER);
        vBox_Group.setPadding(new Insets(5, 5, 5, 10));
        str_Group_Title = "Treatment / Population #1";
        txt_Group1_Title = new Text(str_Group_Title);
    
        str_Group1_SumInfo = "   Summary Information";
        txt_Group1_SumInfo = new Text(str_Group1_SumInfo);
        
        str_OROne = "  Prop       OR   Count   ";
        txt_OROne = new Text(str_OROne);    
        
        hBox_SuccessRow = new HBox();
        
        //stf_Prop = new SmartTextField(propNStuffHandler, 2, 2);
        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("Prop");
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            checkAndHandlelProportionEntered(al_STF.get(0));
        });
       
        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(toBlank); 
        al_STF.get(1).getTextField().setId("Successes");

        al_STF.get(1).getTextField().setOnAction(e -> {
            checkAndHandleSuccessesEntered(al_STF.get(1));
        });
        hBox_SuccessRow.setAlignment(Pos.CENTER);
        hBox_SuccessRow.getChildren()
                             .addAll(al_STF.get(0).getTextField(),
                                     al_STF.get(1).getTextField());
        hBox_SuccessRow.setSpacing(25);

        str_Group_N = "   Group / Sample Size  ";        
        txt_Group_N = new Text(str_Group_N);   

        al_STF.get(2).getTextField().setPrefColumnCount(12);
        al_STF.get(2).getTextField().setMaxWidth(65);
        al_STF.get(2).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(2).getTextField().setText(toBlank);
        al_STF.get(2).getTextField().setId("SampleSize");

        al_STF.get(2).getTextField().setOnAction(e -> {
            checkAndHandleSampleSizeEntered(al_STF.get(2));
        });
        
        vBox_Group.getChildren().addAll(txt_Group1_Title,
                                         txt_Group1_SumInfo,
                                         txt_OROne,
                                         hBox_SuccessRow,
                                         txt_Group_N,
                                         al_STF.get(2).getTextField());
      
        vBox_NumValsPanel.getChildren()
                         .add(vBox_Group);
    }
    
    private void makeInfDecisionsPanel() {
        if (printTheStuff) {
            System.out.println("*** 389 OneProp_Inference_Dialog, makeInfDecisionsPanel()");
        }
        hypothesizedProp = 0.5;       
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
        if (printTheStuff) {
            System.out.println("*** 448 OneProp_Inference_Dialog, makeVariableDefPanel()");
        }
        lbl_GraphProp = new Label(" Prop 1 Label: ");
        lbl_GraphTitle  = new Label("        Title: ");
        
        tf_GraphProp = new TextField("First prop");
        tf_GraphTitle  = new TextField("Title ");        
        tf_GraphProp.setPrefColumnCount(15);
        tf_GraphTitle.setPrefColumnCount(15);        
        tf_GraphProp.textProperty().addListener(this::changeProp_1_Description);
        tf_GraphTitle.textProperty().addListener(this::changeTitle_Description);

        gridChoicesMade = new GridPane();
        gridChoicesMade.setHgap(10);
        gridChoicesMade.setVgap(15);                
        gridChoicesMade.add(lbl_GraphProp, 0, 0);
        gridChoicesMade.add(tf_GraphProp, 1, 0);        
        gridChoicesMade.add(lbl_GraphTitle, 0, 1);
        gridChoicesMade.add(tf_GraphTitle, 1, 1);        
        GridPane.setValignment(tf_GraphProp, VPos.BOTTOM);
        gridChoicesMade.setPadding(new Insets(0, 10, 0, 0));
        
        vBox_VarsPanel = new VBox(10);
        vBox_VarsPanel.setAlignment(Pos.CENTER_LEFT);
        vBox_VarsPanel.setPadding(new Insets(0, 25, 0, 10));
        vBox_VarsPanel.getChildren().add(gridChoicesMade);            
    }
    
    private void makeBottomPanel() { 
        if (printTheStuff) {
            System.out.println("*** 478 OneProp_Inference_Dialog, makeBottomPanel()");
        }
        hBox_BottomPanel = new HBox(10);
        hBox_BottomPanel.setAlignment(Pos.CENTER);
        hBox_BottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        btnOK = new Button("Compute");
        btnCancel = new Button("Cancel");
        btn_Reset = new Button("Reset");
        
        bool_PropGood = false;
        bool_SuccGood = false;
        bool_NGood = false;
        
        btnOK.setOnAction((ActionEvent event) -> { 
        strReturnStatus = "OK";
        
        // Check for values left blank
        //valuesLeftBlank = false;
        if (al_STF.get(0).isEmpty()|| al_STF.get(1).isEmpty()|| al_STF.get(2).isEmpty()) {
            MyAlerts.showValuesLeftBlankAlert();  
            boolGoodToGo = false;
            strReturnStatus = "ValuesLeftBlank"; 
        }
        
        if (boolGoodToGo) {
            allFieldsGood = (bool_PropGood || bool_SuccGood) && bool_NGood;
            if (!allFieldsGood) {
                MyAlerts.showNotAllFieldsGoodAlert();
                boolGoodToGo = false;
                strReturnStatus = "PropFieldsBlank"; 
            } else {
                boolGoodToGo = true;
                close();
            }
        }
    });
        
        setOnCloseRequest((WindowEvent t) -> {
            boolGoodToGo = false;
            oneProp_Inf_Model.setReturnStatus("CloseWindow");
            close();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            boolGoodToGo = false;
            oneProp_Inf_Model.setReturnStatus("Cancel");
            close();
        });

        btn_Reset.setOnAction((ActionEvent event) -> {
            al_STF.get(0).setText(toBlank); 
            al_STF.get(1).setText(toBlank); 
            al_STF.get(2).setText(toBlank);
            
            bool_PropGood = false; 
            bool_SuccGood = false; 
            bool_NGood = false;             
        });
        
        hBox_BottomPanel.getChildren().addAll(btnOK, btnCancel, btn_Reset);
    }
    
    /*************************************************************************
    *    Check a proportion entry for legal (0 < p < 1) and update all other *
    *    values.  If both sample size and nSuccesses are given, change the   *
    *    number of successes.                                                *
    *************************************************************************/
    
    public boolean checkAndHandlelProportionEntered(SmartTextField theSTF) {
        boolean propIsGood = false; 
        double goodProp;        
        propIsGood = DataUtilities.txtFieldHasProp(theSTF.getTextField());

        if (propIsGood == false) {
             MyAlerts.showGenericBadNumberAlert("fraction or decimal ");
             theSTF.setText("");
        }    
        else {
            goodProp = Double.parseDouble(theSTF.getText());
                prop = goodProp;
                bool_PropGood = true;
                bool_NGood = DataUtilities.strIsAPosInt(al_STF.get(2).getText());
                bool_SuccGood = DataUtilities.strIsAPosInt(al_STF.get(1).getText());
                
                if (bool_NGood) {
                    strProp1 = al_STF.get(0).getText();
                    succ = (int)Math.floor(prop * n + 0.5);
                    al_STF.get(1).setText(String.valueOf(succ));
                } else if (bool_SuccGood) { //  and N1 not
                    strProp1 = al_STF.get(0).getText();
                    n = (int)Math.floor(succ / prop + 0.5);
                    al_STF.get(2).setText(String.valueOf(n));                    
                }
        }
        return propIsGood;
    }

    /*************************************************************************
    *    Check a success entry for legal (0 < N) and update all other values *
    *    values.  If both proportion and sample size are given, change the   *
    *    sample size.                                                        *
    *************************************************************************/
    
    public boolean checkAndHandleSuccessesEntered(SmartTextField theSTF) {
        boolean succIsGood = true;
        int goodSucc;
        
        if (theSTF.getText().isEmpty()) { return false; }
        if (!DataUtilities.strIsAPosInt(theSTF.getText())) {
            MyAlerts.showGenericBadNumberAlert("positive integer");
            theSTF.setText(toBlank);
            return false;
        }
        goodSucc = Integer.parseInt(theSTF.getText());
            succ = goodSucc;
            bool_SuccGood = true;
            bool_NGood = DataUtilities.strIsAPosInt(al_STF.get(2).getText());
            bool_PropGood = DataUtilities.strIsAProb(al_STF.get(0).getText());  

            if (bool_NGood) {
                
                if (succ >= n) {
                    MyAlerts.showPropOopsAlert();
                    al_STF.get(1).setText(toBlank);
                    al_STF.get(0).setText(toBlank);
                    al_STF.get(2).setText(toBlank);
                    return false;
                }
                prop = (double)succ / (double)n;
                al_STF.get(0).setText(String.valueOf(prop));
                return true;
            } else if (bool_PropGood) {    //  and N1 not
                n = (int)Math.floor(succ / prop + 0.5);
                al_STF.get(2).setText(String.valueOf(n));  
                return true;
            }
            return false;
        } 
    
    /*************************************************************************
    *    Check a sample size entry for legal (0 < N) and update all other    *
    *    values.  If both proportion and Successes are given, change the     *
    *    proportion.                                                         *
    *************************************************************************/
        
    public boolean checkAndHandleSampleSizeEntered(SmartTextField theSTF) {
        int goodN;
        
        if (theSTF.getText().isEmpty()) { return false; }
        
        if (!DataUtilities.strIsAPosInt(theSTF.getText())) {
            MyAlerts.showGenericBadNumberAlert(" a positive integer ");
            theSTF.setText(toBlank);
            return false;
        }
        goodN = Integer.parseInt(theSTF.getText());
            n = goodN;
            bool_NGood = true;
            bool_SuccGood = DataUtilities.strIsAPosInt(al_STF.get(1).getText());
            bool_PropGood = DataUtilities.strIsAProb(al_STF.get(0).getText());  

            if (bool_SuccGood) {
                
                if (succ >= n) {
                    MyAlerts.showPropOopsAlert();
                    al_STF.get(1).setText(toBlank);
                    al_STF.get(0).setText(toBlank);
                    al_STF.get(2).setText(toBlank);
                    return false;
                }
                prop = (double)succ / (double)n;
                al_STF.get(0).setText(String.valueOf(prop));
                return true;
            } else if (bool_PropGood) {    //  and Succ1 not
                succ = (int)Math.floor(n * prop + 0.5);
                al_STF.get(1).setText(String.valueOf(succ));  
                return true;
            }
            return false;
    }   
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = ciView.getSelectionModel().getSelectedIndex();
        alphaView.getSelectionModel().select(ciIndex);
        significanceLevel = theAlphaLevs[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = alphaView.getSelectionModel().getSelectedIndex();
        ciView.getSelectionModel().select(alphaIndex);
        significanceLevel = theAlphaLevs[alphaIndex];   
    }
    
    public void changeProp_1_Description(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tf_GraphProp.setText(newValue); 
    }
    
    public void changeTitle_Description(ObservableValue<? extends String> title,
        String oldValue,
        String newValue) {
        tf_GraphTitle.setText(newValue); 
    }
    
    public boolean getGoodToGo() { return boolGoodToGo; } 
    public double getLevelOfSignificance() { return significanceLevel; }
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getHypothesizedProp() { return hypothesizedProp; }
    public int getN1() { return n; }
    public double getPHat() {return prop; }
    public int getX1() { return succ; }
    public double getTheNullProp() { return hypothesizedProp; }
    public boolean getHypothesisTestDesired() { return rb_HypothYes.isSelected(); }
    public String get_GraphProp() { return tf_GraphProp.getText(); }
    public String get_GraphTitle() { return tf_GraphTitle.getText(); }
}

