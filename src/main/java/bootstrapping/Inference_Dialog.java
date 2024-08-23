/************************************************************
 *                      Inference_Dialog                    *
 *                          04/17/24                        *
 *                            00:00                         *
 ***********************************************************/
package bootstrapping;

import dialogs.One_Variable_Dialog;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
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

public class Inference_Dialog extends One_Variable_Dialog { 
    int alphaIndex, ciIndex, confidenceLevel;
    int[] confLevels; 
    boolean okToContinue;
    double hypothesizedMean, alpha;
    Double daNewNullMean;      
    double[] alphaLevels; 
    
    Button changeNull;
    RadioButton altHypNE, altHypLT, altHypGT;
    String strAltHypNE, strAltHypLT, strAltHypGT, strNullHyp, strNullAndAlt, 
           strHypChosen;
    String resultAsString;
    
    String waldoFile = "Inference_Dialog";
    //String waldoFile = "";
    
    String[] hypothPair;
    Label lblNullAndAlt, ciLabel, alphaLabel;
    
    Separator sep;
    final Text currNullMean = new Text("Current null hypothesis: \u03B8 = ");
    TextField hypMean;
    TextInputDialog txtDialog;
    HBox alphaAndCI, hBoxCurrMean;
    VBox ciBox, alphaBox;
    
    ObservableList<String> list_ConfLevels, list_AlphaLevels;
    
    ListView<String> list_CIViews, list_AlphaViews; 

    public Inference_Dialog(Data_Manager dm, String variableType) {
        super(dm, "Quantitative");
        dm.whereIsWaldo(63, waldoFile, "\nConstructing");
        lbl_Title.setText("Inference for a single parameter");
        lblFirstVar.setText("Variable choice:");
        alphaLevels = new double[] { 0.10, 0.05, 0.01};
        confLevels = new int[] {90, 95, 99};
        makeHypotheses();
        makeAlphaAndCIPanel();
        rightPanel.getChildren().add(alphaAndCI);
        setTitle("Inference for a single parameter");
    }  

 private void makeHypotheses() {
        dm.whereIsWaldo(75, waldoFile, "makeHypotheses()");
        hypothesizedMean = 0.0;
        daNewNullMean = 0.0;
        strHypChosen = "NotEqual";
        changeNull = new Button("Change null hypothesis");
        strNullAndAlt = "  Choose from the null and \n  alternate hypotheses\n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        //  03B8 = theta
        strNullHyp = "\u03B8 = k";
        strAltHypNE = "\u03B8 \u2260 k";
        strAltHypLT = "\u03B8 < k";
        strAltHypGT = "\u03B8 > k";
        
        altHypNE = new RadioButton(strNullHyp + "\n" + strAltHypNE);
        altHypLT = new RadioButton(strNullHyp + "\n" + strAltHypLT);
        altHypGT = new RadioButton(strNullHyp + "\n" + strAltHypGT);
        
        // top, right, bottom, left
        altHypNE.setPadding(new Insets(10, 10, 10, 10));
        altHypLT.setPadding(new Insets(10, 10, 10, 10));
        altHypGT.setPadding(new Insets(10, 10, 10, 10));
        
        altHypNE.setSelected(true);
        altHypLT.setSelected(false);
        altHypGT.setSelected(false);
        
        hypothesizedMean = 0.0;
        hypMean = new TextField("0.0");
        hypMean.setEditable(false);
        hypMean.setMinWidth(75);
        hypMean.setMaxWidth(75);
        hBoxCurrMean = new HBox();
        hBoxCurrMean.getChildren().addAll(currNullMean, hypMean);
        
        leftPanel.getChildren()
                 .addAll(lblNullAndAlt, altHypNE, altHypLT, altHypGT, 
                         changeNull, hBoxCurrMean);
        
        altHypNE.setOnAction(e->{
            System.out.println("hypNE chosen");
            altHypNE.setSelected(true);
            altHypLT.setSelected(false);
            altHypGT.setSelected(false);
            strHypChosen = "NotEqual";
        });
            
        altHypLT.setOnAction(e->{
            System.out.println("hypLT chosen");
            altHypNE.setSelected(false);
            altHypLT.setSelected(true);
            altHypGT.setSelected(false);
            strHypChosen = "LessThan";

        });
            
        altHypGT.setOnAction(e->{
            System.out.println("hypGT chosen");
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
                txtDialog.setContentText("What value would you like to test? ");                     

                Optional<String> result = txtDialog.showAndWait();
                
                if (result.isPresent()) { resultAsString = result.get(); }
                
                if (result.isPresent() == true) {
                    okToContinue = true;
                    try {
                        daNewNullMean = Double.valueOf(resultAsString);
                    }
                    catch (NumberFormatException ex ){ 
                        MyAlerts.showGenericBadNumberAlert(" a real number ");
                        txtDialog.setContentText("");
                        okToContinue = false;
                        daNewNullMean = 0.0;
                    }
                }
                else {
                    daNewNullMean = 0.0;    // Null returns to 0.0 if Cancel
                }
            }
            hypothesizedMean = daNewNullMean;
            hypMean.setText(String.valueOf(hypothesizedMean));
        });
    }
 
private void makeAlphaAndCIPanel() {
        dm.whereIsWaldo(171, waldoFile, "makeAlphaAndCIPanel()");
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
        ciIndex = list_CIViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[ciIndex];
        list_AlphaViews.getSelectionModel().select(ciIndex);
        confidenceLevel = confLevels[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = list_AlphaViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[alphaIndex];
        list_CIViews.getSelectionModel().select(alphaIndex);
        confidenceLevel = confLevels[alphaIndex];        
    }
 
    public double getAlpha() { return alpha; }
    
    public String getChosenHypothesis() { return strHypChosen; }
    public double getHypothesizedMean() { return hypothesizedMean; }
    
    public int getConfidenceLevel() { return confidenceLevel; }
    
    public String[] getHypothesesToPrint() {
        hypothPair = new String[2];
        hypothPair[0] = StringUtilities.getStringOfNSpaces(20) + "Null hypothesis: \u03B8 = " + Double.toString(daNewNullMean);
        
        if (altHypNE.isSelected()) {
            hypothPair[1] =  strAltHypNE = StringUtilities.getStringOfNSpaces(20) + " Alt hypothesis: \u03B8 \u2260 " + Double.toString(daNewNullMean);
        }
        else if (altHypLT.isSelected()) {
            hypothPair[1] =  strAltHypLT = StringUtilities.getStringOfNSpaces(20) + " Alt hypothesis: \u03B8 < " + Double.toString(daNewNullMean);
        }
        else {
            hypothPair[1] =  strAltHypGT = StringUtilities.getStringOfNSpaces(20) + " Alt hypothesis: \u03B8 > " + Double.toString(daNewNullMean);
        }
        return hypothPair;
    }
}


