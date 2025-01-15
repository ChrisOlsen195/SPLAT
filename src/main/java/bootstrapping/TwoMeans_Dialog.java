/************************************************************
 *                      TwoMeans_Dialog                     *
 *                          01/08/25                        *
 *                            15:00                         *
 ***********************************************************/
package bootstrapping;

import dialogs.Two_Variables_Dialog;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import smarttextfield.SmartTextField;
import splat.Data_Manager;
import utilityClasses.*;

public class TwoMeans_Dialog extends Two_Variables_Dialog{ 
    // POJOs
    int alphaIndex, ciIndex, nReplications;
    boolean okToContinue;
    double hypothesizedDifference, currentSigLevel, currentConfLevel, alpha;
    double[] alphaLevels, confLevels; 
    Double daNewNullDiff;  

    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, 
            strHypChosen, resultAsString;    
    
    //String waldoFile = "TwoMeans_Dialog";
    String waldoFile = "";

    // FX POJOs
    Button changeNull;
    HBox alphaAndCI, hBoxCurrDiff, hBoxCurrReplications;    
    Label lblNullAndAlt, ciLabel, alphaLabel;     
    RadioButton hypNE, hypLT, hypGT;
    Separator sep;
    
    SmartTextField stfNReps;
    
    final Text currNullDiff = new Text("Current null diff: (\u03BC\u2081 - \u03BC\u2082) = ");
    final Text currNReplications = new Text("        Current replications = ");
    TextField tfHypDiff, tfCurrNReps;
    TextInputDialog txtDialog;

    VBox ciBox, alphaBox;
    
    ObservableList<String> list_ConfLevels, list_AlphaLevels;
    
    ListView<String> list_CIViews, list_AlphaViews; 
   
    public TwoMeans_Dialog(Data_Manager dm, String variableType) {
        super(dm, "TwoMeans_Dialog", "None");
        this.dm = dm;
        dm.whereIsWaldo(69, waldoFile, "\n *** Constructing");
        lblTitle.setText("Bootstrap TwoMeans");
        lblExplanVar.setText("Variable #1:");   //  Not really explan
        lblResponseVar.setText("Variable #2:"); //  Not really resp
        tf_PreferredFirstVarDescription.setText("Variable #1");
        tf_PreferredSecondVarDescription.setText("Variable #2");
        alphaLevels = new double[] { 0.10, 0.05, 0.01};
        confLevels = new double[] {0.90, 0.95, 0.99};
        makeHypotheses();
        makeAlphaAndCIPanel();
        vBox_RightPanel.getChildren().add(alphaAndCI);
        setTitle("Bootstrap Two Means");
    }  

 
    protected void defineTheCheckBoxes() {
        dm.whereIsWaldo(85, waldoFile, "\n --- defineTheCheckBoxes()");
        // Check box strings must match the order of dashboard strings
        // Perhaps pass them to dashboard in future?
        nCheckBoxes = 4;
        String[] chBoxStrings = { " Best fit line ", " Residuals ",
                                         " RegrReport ", " DiagReport "}; 
        chBoxDashBoardOptions = new CheckBox[nCheckBoxes];
        
        for (int ithCBx = 0; ithCBx < nCheckBoxes; ithCBx++) {
            chBoxDashBoardOptions[ithCBx] = new CheckBox(chBoxStrings[ithCBx]);
        }
    } 

    private void makeHypotheses() {
        dm.whereIsWaldo(99, waldoFile, "makeHypotheses()");
        hypothesizedDifference = 0.0;
        daNewNullDiff = 0.0;
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
        
        hypothesizedDifference = 0.0;
        tfHypDiff = new TextField("0.0");
        tfHypDiff.setMinWidth(75);
        tfHypDiff.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(currNullDiff, tfHypDiff);   

        stfNReps = new SmartTextField();
        nReplications = 0;
        stfNReps.getTextField().setText("0");
        stfNReps.getTextField().setMinWidth(75);
        stfNReps.getTextField().setMaxWidth(75);
        stfNReps.getTextField().setPrefColumnCount(10);
        stfNReps.getTextField().setId("nReps");
        stfNReps.setSmartTextField_MB_POSITIVEINTEGER(true);
        stfNReps.setIsEditable(true);
        
        hBoxCurrReplications = new HBox();
        hBoxCurrReplications.getChildren().addAll(currNReplications, stfNReps.getTextField());
        
        vBox_LeftPanel.getChildren()
                 .addAll(lblNullAndAlt, hypNE, hypLT, hypGT, 
                         changeNull, hBoxCurrDiff, hBoxCurrReplications);
        
        hypNE.setOnAction(e->{
            //RadioButton tb = ((RadioButton) e.getTarget());
            //String daID = tb.getId();
            //Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypNE chosen");
            hypNE.setSelected(true);
            hypLT.setSelected(false);
            hypGT.setSelected(false);
            strHypChosen = "NotEqual";
        });
            
        hypLT.setOnAction(e->{
            //RadioButton tb = ((RadioButton) e.getTarget());
            //String daID = tb.getId();
            //Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypLT chosen");
            hypNE.setSelected(false);
            hypLT.setSelected(true);
            hypGT.setSelected(false);
            strHypChosen = "LessThan";
        });
            
        hypGT.setOnAction(e->{
            //RadioButton tb = ((RadioButton) e.getTarget());
            //String daID = tb.getId();
            //Boolean checkValue = tb.selectedProperty().getValue();
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
                txtDialog.setHeaderText(strNullChangeQuery);
                txtDialog.setContentText("What difference between means would you like to test? ");                     

                Optional<String> result = txtDialog.showAndWait();
                if (result.isPresent()) {
                    resultAsString = result.get();                        
                }
                if (result.isPresent() == true) {
                    okToContinue = true;
                    try {
                        daNewNullDiff = Double.valueOf(resultAsString);
                    }
                    catch (NumberFormatException ex ){ 
                        okToContinue = false;
                        System.out.println("203 TwoMeans_Dialog -- gen #");
                        MyAlerts.showGenericBadNumberAlert(" a real number ");
                        txtDialog.setContentText("");
                        okToContinue = false;
                        daNewNullDiff = 0.0;
                    }
                }
                else {
                    daNewNullDiff = 0.0;    // Null returns to 0.0 if Cancel
                }
            }
            hypothesizedDifference = daNewNullDiff;
            tfHypDiff.setText(String.valueOf(hypothesizedDifference));
        });
    }
 
 private void makeAlphaAndCIPanel() {
        dm.whereIsWaldo(220, waldoFile, "makeAlphaAndCIPanel()");    
        ciLabel = new Label("   Select conf level");
        ciLabel.setMaxWidth(130);
        ciLabel.setMinWidth(130);
        list_ConfLevels = FXCollections.<String>observableArrayList("          90%", "          95%", "          99%");
        list_CIViews = new ListView<>(list_ConfLevels);
        list_CIViews.setOrientation(Orientation.VERTICAL);
        list_CIViews.setPrefSize(120, 100);
        
        list_CIViews.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       ciChanged(ov, oldvalue, newvalue);
                  }
              }));
 
        alphaLabel = new Label("   Select alpha level");
        alphaLabel.setMaxWidth(130);
        alphaLabel.setMinWidth(130);
        list_AlphaLevels = FXCollections.<String>observableArrayList("          0.10", "          0.05", "          0.01");
        list_AlphaViews = new ListView<>(list_AlphaLevels);
        list_AlphaViews.setOrientation(Orientation.VERTICAL);
        list_AlphaViews.setPrefSize(120, 100);
        
        list_AlphaViews.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       alphaChanged(ov, oldvalue, newvalue);
                  }
              }));
        
        list_AlphaViews.getSelectionModel().select(1);    //  Set at .05
        list_CIViews.getSelectionModel().select(1);   //  Set at 95%
        ciBox = new VBox();        
        ciBox.getChildren().addAll(ciLabel, list_CIViews);
        alphaBox = new VBox();
        alphaBox.getChildren().addAll(alphaLabel, list_AlphaViews);
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        alphaAndCI = new HBox(10);
        alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);  
 }
 
     public void ciChanged(ObservableValue<? extends String> observable,
                                                     String oldValue,
                                                     String newValue) {
        dm.whereIsWaldo(270, waldoFile, "--- ciChanged");
        ciIndex = list_CIViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[ciIndex];
        list_AlphaViews.getSelectionModel().select(ciIndex);
        currentSigLevel = alphaLevels[ciIndex];
        currentConfLevel = confLevels[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        dm.whereIsWaldo(281, waldoFile, "--- alphaChanged");
        alphaIndex = list_AlphaViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[alphaIndex];
        list_CIViews.getSelectionModel().select(alphaIndex);
        currentSigLevel = alphaLevels[alphaIndex];
        currentConfLevel = confLevels[alphaIndex];        
    }
 
    public double getAlpha() {  return alpha; }
    public String getHypotheses() { return strHypChosen; }
    public double getHypothesizedDiff() { return hypothesizedDifference; }
    public int getNReplications() {
        String strNReps = stfNReps.getTextField().getText();
        nReplications = Integer.parseInt(strNReps);
        return nReplications;
    }
}

