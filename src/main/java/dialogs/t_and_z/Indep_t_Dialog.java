/************************************************************
 *                       Indep_t_Dialog                     *
 *                          12/25/25                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs.t_and_z;

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
import splat.Data_Manager;
import utilityClasses.*;

public class Indep_t_Dialog extends Two_Variables_Dialog{ 
    // POJOs
    int alphaIndex, ciIndex;
    boolean okToContinue;
    double hypothesizedDifference, currentSigLevel, currentConfLevel, alpha;
    double[] alphaLevels, confLevels; 
    Double daNewNullDiff;  

    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, 
            strAltHypothesis, resultAsString;    
    
    //String waldoFile = "Indep_t_Dialog";
    String waldoFile = "";
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    // FX POJOs
    Button changeNull;
    HBox alphaAndCI, hBoxCurrDiff;    
    Label lblNullAndAlt, ciLabel, alphaLabel;     
    RadioButton hypNE, hypLT, hypGT;
    Separator sep;
    final Text currNullDiff = new Text("Current null diff: (\u03BC\u2081 - \u03BC\u2082) = ");
    TextField hypDiff;
    TextInputDialog txtDialog;

    VBox ciBox, alphaBox;
    
    ObservableList<String> list_ConfLevels, list_AlphaLevels;
    
    ListView<String> list_CIViews, list_AlphaViews; 
   
    public Indep_t_Dialog(Data_Manager dm, String callingProcedure) {
        super(dm, callingProcedure, "None");
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 68 Indep_t_Dialog, Constructing");
        }
        dm.whereIsWaldo(65, waldoFile, "CallingProc = " + callingProcedure);
        lblTitle.setText("Independent t inference");
        lblExplanVar.setText("Variable #1:");   //  Not really explan
        lblResponseVar.setText("Variable #2:"); //  Not really resp
        
        if (callingProcedure.equals("Indep_t_tidy")) {
            dm.whereIsWaldo(71, waldoFile, "CallingProc = " + callingProcedure);
            lblExplanVar.setText("Group/Treat var: ");
            lblResponseVar.setText("   Response var: ");
            tf_Var_1_Pref.setText("Group/Treat var: ");
            tf_Var_2_Pref.setText("   Response var: ");
        }
        alphaLevels = new double[] { 0.10, 0.05, 0.01};
        confLevels = new double[] {0.90, 0.95, 0.99};
        makeHypotheses();
        makeAlphaAndCIPanel();
        vBoxRightPanel.getChildren().add(alphaAndCI);
        setTitle("Independent t inference");
    }  
 
    protected void defineTheCheckBoxes() {
        if (printTheStuff) {
            System.out.println("*** 92 Indep_t_Dialog, defineTheCheckBoxes()");
        }
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
        if (printTheStuff) {
            System.out.println("*** 108 Indep_t_Dialog, makeHypotheses()");
        }
        hypothesizedDifference = 0.0;
        daNewNullDiff = 0.0;
        strAltHypothesis = "NotEqual";
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
        hypDiff = new TextField("0.0");
        hypDiff.setMinWidth(75);
        hypDiff.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(currNullDiff, hypDiff);
        
        vBoxLeftPanel.getChildren()
                 .addAll(lblNullAndAlt, hypNE, hypLT, hypGT, 
                         changeNull, hBoxCurrDiff);
        
        hypNE.setOnAction(e->{
            dm.whereIsWaldo(137, waldoFile, " --- hypeNE chosen");
            hypNE.setSelected(true);
            hypLT.setSelected(false);
            hypGT.setSelected(false);
            strAltHypothesis = "NotEqual";
        });
            
        hypLT.setOnAction(e->{
            dm.whereIsWaldo(145, waldoFile, " --- hypLT chosen");
            hypNE.setSelected(false);
            hypLT.setSelected(true);
            hypGT.setSelected(false);
            strAltHypothesis = "LessThan";
        });
            
        hypGT.setOnAction(e->{
            dm.whereIsWaldo(153, waldoFile, " --- hypGT chosen");
            hypNE.setSelected(false);
            hypLT.setSelected(false);
            hypGT.setSelected(true);
            strAltHypothesis = "GreaterThan";
        });
            
        changeNull.setOnAction((ActionEvent event) -> {
            okToContinue = false;
            dm.whereIsWaldo(162, waldoFile, " --- changeNull chosen");
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
            hypDiff.setText(String.valueOf(hypothesizedDifference));
        });
        dm.whereIsWaldo(194, waldoFile, " --- END makeHypotheses()");
    }
 
 private void makeAlphaAndCIPanel() {
        if (printTheStuff) {
            System.out.println("*** 208 Indep_t_Dialog, makeAlphaAndCIPanel()");
        }    
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
        dm.whereIsWaldo(244, waldoFile, " --- END makeAlphaAndCIPanel()"); 
 }
 
     public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = list_CIViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[ciIndex];
        list_AlphaViews.getSelectionModel().select(ciIndex);
        currentSigLevel = alphaLevels[ciIndex];
        currentConfLevel = confLevels[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = list_AlphaViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[alphaIndex];
        list_CIViews.getSelectionModel().select(alphaIndex);
        currentSigLevel = alphaLevels[alphaIndex];
        currentConfLevel = confLevels[alphaIndex];        
    }
 
    public double getInd_t_Alpha() {  return alpha; }
    public String getAltHypothesis() { return strAltHypothesis; }
    public double getHypothesizedDiffInMeans() { return hypothesizedDifference; }
    
    public String toString() { return "Indep_t_Dialog.toString() called"; }
}

