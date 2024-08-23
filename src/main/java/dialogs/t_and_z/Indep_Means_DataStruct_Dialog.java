/************************************************************
 *                  Indep_Means_DataStruct_Dialog           *
 *                          10/15/23                        *
 *                            09:00                         *
 *                                                          *
 *   Called by Ind_t_Controller                             *
 ***********************************************************/
package dialogs.t_and_z;

import dialogs.Splat_Dialog;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Indep_Means_DataStruct_Dialog extends Splat_Dialog {
    
    //String theChoice;
    String selectedLabel;
    Label msg;
    RadioButton sepColumns, stacked, summarized;
    ToggleGroup group;
    VBox buttonBox;

    public Indep_Means_DataStruct_Dialog() {
        System.out.println("33 Indep_Means_DataStruct_Dialog, constructing");
        sepColumns = new RadioButton("Data are in separate columns");
        selectedLabel = "Data are in separate columns";
        stacked = new RadioButton("Data are stacked");
        summarized = new RadioButton("Data are summarized");
        group = new ToggleGroup();
        group.getToggles().addAll(sepColumns, stacked, summarized);
        group.selectedToggleProperty().addListener(this::changed);
        sepColumns.setSelected(true);
  
        msg = new Label("How is your data structured?");
        buttonBox = new VBox(sepColumns, stacked, summarized);
        buttonBox.setSpacing(10);
        
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));

        btnOK.setText("OK!");
        btnCancel.setText("Cancel");
        buttonPanel.getChildren().addAll(btnOK, btnCancel);
        
        VBox root = new VBox(msg, buttonBox, buttonPanel);
        root.setMinWidth(300);
        root.setMaxWidth(300);
        root.setMinHeight(200);
        root.setMaxHeight(200);
        root.setSpacing(10);
        
        root.setStyle("-fx-padding: 10;" +
                      "-fx-border-style: solid inside;" +
                      "-fx-border-width: 2;" +
                      "-fx-border-insets: 5;" +
                      "-fx-border-radius: 5;" +
                      "-fx-border-color: blue;");
        
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Data structure inquiry...");
        // showAndWait();
    }
    
    // A change listener to track the selection in the group
    public void changed(ObservableValue<? extends Toggle> observable,
                        Toggle oldBtn,
                        Toggle newBtn) {
        if (newBtn != null) {
            selectedLabel = ((Labeled)newBtn).getText();
        }
    }
    
    public String getTheChoice() { return selectedLabel; }
}
