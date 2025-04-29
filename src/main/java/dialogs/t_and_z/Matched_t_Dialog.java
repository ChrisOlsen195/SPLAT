/************************************************************
 *                     Matched_t_Dialog                     *
 *                         02/09/25                         *
 *                          12:00                           *
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
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class Matched_t_Dialog extends Two_Variables_Dialog{ 
    
    int alphaIndex, ciIndex, confidenceLevel;
    int[] confLevels; 
    boolean okToContinue;
    double hypothesizedDifference, alpha;
    Double daNewNullDiff;  
    double[] alphaLevels; 
    
    Button changeNull;
    RadioButton altHypNE, altHypLT, altHypGT;
    String strAltHypNE, strAltHypLT, strAltHypGT, strHypNull, strNullAndAlt, 
            strHypChosen, resultAsString, differenceDescription;
    
    String[] hypothPair;
    
    // Make empty if no-print
    //String waldoFile = "MatchedPairs_Dialog";
    String waldoFile = "";
    
    // POJOs / FX
    
    Label lblNullAndAlt, ciLabel, alphaLabel; 
    
    Separator sep;
    final Text currNullDiff = new Text("Current null diff: (Mean difference = ");
    TextField hypDiff;
    TextInputDialog txtDialog;
    HBox alphaAndCI, hBoxCurrDiff;
    VBox ciBox, alphaBox;
    
    ObservableList<String> list_ConfLevels, list_AlphaLevels;
    
    ListView<String> list_CIViews, list_AlphaViews; 
   
    public Matched_t_Dialog(Data_Manager dm, String variableType) {
        super(dm, "MatchedPairs_Dialog", "None");
        dm.whereIsWaldo(69, waldoFile, "\nConstructing");        
        lblTitle.setText("Matched pairs t inference");
        lblExplanVar.setText("Variable #1:");   //  Not really explan
        lblResponseVar.setText("Variable #2:"); //  Not really resp
        tf_Var_1_Pref.setText(" ");
        tf_Var_2_Pref.setText(" ");
        alphaLevels = new double[] { 0.10, 0.05, 0.01};
        confLevels = new int[] {90, 95, 99};
        makeHypotheses();
        makeAlphaAndCIPanel();
        vBoxRightPanel.getChildren().add(alphaAndCI);
        setTitle("Matched pairs t inference");
    }  

    protected void defineTheCheckBoxes() {
        dm.whereIsWaldo(84, waldoFile, "defineTheCheckBoxes()");
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
        dm.whereIsWaldo(98, waldoFile, "makeHypotheses()");        
        hypothesizedDifference = 0.0;
        daNewNullDiff = 0.0;
        strHypChosen = "NotEqual";
        changeNull = new Button("Change null difference");
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        strHypNull = "Mean difference = k";
        strAltHypNE = "Mean difference \u2260 k";
        strAltHypLT = "Mean difference < k";
        strAltHypGT = "Mean difference > k";
        
        altHypNE = new RadioButton(strHypNull + "\n" + strAltHypNE);
        altHypLT = new RadioButton(strHypNull + "\n" + strAltHypLT);
        altHypGT = new RadioButton(strHypNull + "\n" + strAltHypGT);
        
        // top, right, bottom, left
        altHypNE.setPadding(new Insets(10, 10, 10, 10));
        altHypLT.setPadding(new Insets(10, 10, 10, 10));
        altHypGT.setPadding(new Insets(10, 10, 10, 10));
        
        altHypNE.setSelected(true);
        altHypLT.setSelected(false);
        altHypGT.setSelected(false);
        
        hypothesizedDifference = 0.0;
        //daNullDiff = 0.0; 
        hypDiff = new TextField("0.0");
        hypDiff.setMinWidth(75);
        hypDiff.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(currNullDiff, hypDiff);
        
        vBoxLeftPanel.getChildren()
                      .addAll(lblNullAndAlt, altHypNE, altHypLT, altHypGT, 
                         changeNull, hBoxCurrDiff);
        
        altHypNE.setOnAction(e->{
            altHypNE.setSelected(true);
            altHypLT.setSelected(false);
            altHypGT.setSelected(false);
            strHypChosen = "NotEqual";

        });
            
        altHypLT.setOnAction(e->{
            altHypNE.setSelected(false);
            altHypLT.setSelected(true);
            altHypGT.setSelected(false);
            strHypChosen = "LessThan";
        });
            
        altHypGT.setOnAction(e->{
            altHypNE.setSelected(false);
            altHypLT.setSelected(false);
            altHypGT.setSelected(true);
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
    }
 
 private void makeAlphaAndCIPanel() {
        dm.whereIsWaldo(194, waldoFile, "makeAlphaAndCIPanel()");
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
        dm.whereIsWaldo(245, waldoFile, "ciChanged()");
        ciIndex = list_CIViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[ciIndex];
        list_AlphaViews.getSelectionModel().select(ciIndex);
        confidenceLevel = confLevels[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        dm.whereIsWaldo(255, waldoFile, "alphaChanged()");
        alphaIndex = list_AlphaViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[alphaIndex];
        list_CIViews.getSelectionModel().select(alphaIndex);
        confidenceLevel = confLevels[alphaIndex];        
    }
    
public String[] getHypothesesToPrint() {
        hypothPair = new String[2];
        hypothPair[0] = StringUtilities.getStringOfNSpaces(20) + "Null hypothesis: \u03BC = " + Double.toString(daNewNullDiff);
        
        if (altHypNE.isSelected()) {
            hypothPair[1] =  strAltHypNE = StringUtilities.getStringOfNSpaces(20) + " Alt hypothesis: \u03BC \u2260 " + Double.toString(daNewNullDiff);
        }
        else if (altHypLT.isSelected()) {
            hypothPair[1] =  strAltHypLT = StringUtilities.getStringOfNSpaces(20) + " Alt hypothesis: \u03BC < " + Double.toString(daNewNullDiff);
            System.out.println(hypothPair[1]);
        }
        else {
            hypothPair[1] =  strAltHypGT = StringUtilities.getStringOfNSpaces(20) + " Alt hypothesis: \u03BC > " + Double.toString(daNewNullDiff);
            System.out.println(hypothPair[1]);
        }
        return hypothPair;
    }

    public String getDifferenceDescription() { return differenceDescription; } 
    public double getAlpha() {  return alpha; }
    public int getConfidenceLevel() { return confidenceLevel; }
    public String getChosenHypothesis() { return strHypChosen; }
    public double getHypothesizedDiff() { return hypothesizedDifference; }
}


