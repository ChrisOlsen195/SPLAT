/******************************************************************
 *                     DataCleaner                                *
 *                       11/01/23                                 *
 *                        09:00                                   *
 *****************************************************************/
package utilityClasses;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import splat.*;

public class DataCleaner {
    // POJOs
    boolean inListToClean, doubleTrouble, dataAreClean;
    
    int nEdits, nDataToClean, currentlyEditing, nUniques, nStringsToClean,
        nNonMissingData;
    int[] categoryCount;
    
    String changeFrom, changeTo, returnStatus;    
    String[] str_FixedData, str_CleanedData, finalCategories, str_NonMissing;
    
    //String waldoFile = "DataCleaner";
    String waldoFile  = "";
    
    ArrayList<String> listView_From, listView_To, al_NonMissing;
    ListView<String> lv_Uniques, listView_PreChoice, listView_PostChoice;
    
    // POJOs / FX
    Button btn_Cancel, btn_OK;
    Data_Manager dm;
    HBox hBox_Buttons, lists;
    Stage stage;
    Text captions;
    Text directions;
    VBox root;
      
    public DataCleaner(Data_Manager dm, ColumnOfData columnToClean) {
        this.dm = dm; 
        dm.whereIsWaldo(58, waldoFile, "Constructing");
        nDataToClean = dm.getNCasesInStruct();
        str_FixedData = new String[nDataToClean];
        al_NonMissing = new ArrayList();  
        
        for (int ith = 0; ith < nDataToClean; ith++) {
            str_FixedData[ith] = columnToClean.getStringInIthRow(ith);
        }  
    }
    
    
    public boolean cleanAway() {
        dm.whereIsWaldo(70, waldoFile, "cleanAway()");
        stage = new Stage();
        dataAreClean = true;    //  Initialize
        
        for (int ith = 0; ith < nDataToClean; ith++) {         
            al_NonMissing.add(str_FixedData[ith]);
        }
        
        nStringsToClean = al_NonMissing.size();  
        
        str_NonMissing = new String[nStringsToClean];
        for (int ith = 0; ith < nStringsToClean; ith++) {
            str_NonMissing[ith] = al_NonMissing.get(ith);
        }
        
        nNonMissingData = str_NonMissing.length;
        
        if (nNonMissingData > 0) {
            str_CleanedData = new String[nNonMissingData];
            lv_Uniques = new ListView<>();
            listView_From = new ArrayList<>();
            listView_To = new ArrayList<>();
            lv_Uniques.setPrefSize(200, 150);
            lv_Uniques.setEditable(true);
            lv_Uniques.setCellFactory(TextFieldListCell.forListView());
            nEdits = 0;    

            // Add 0th item
            lv_Uniques.getItems().add(str_NonMissing[0]);
            
            // Add only different items
            for (int ith = 1; ith < nNonMissingData; ith++) {
                boolean differentFromEarlier = true;
                
                for (int jth = 0; jth < ith; jth++) {
                    if (str_NonMissing[jth].equals(str_NonMissing[ith]))
                        differentFromEarlier = false;
                }
                
                if (differentFromEarlier)
                     lv_Uniques.getItems().add(str_NonMissing[ith]);
            }

            listView_PreChoice = new ListView<>();
            listView_PreChoice.setPrefSize(200, 120);
            listView_PreChoice.setEditable(false);
            listView_PreChoice.setCellFactory(TextFieldListCell.forListView());

            listView_PostChoice = new ListView<>();
            listView_PostChoice.setPrefSize(200, 120);
            listView_PostChoice.setEditable(false);
            listView_PostChoice.setCellFactory(TextFieldListCell.forListView());

            // Add Edit-related event handlers
            lv_Uniques.setOnEditStart(this::editStart);
            lv_Uniques.setOnEditCommit(this::editCommit);

            dataAreClean = cleanTheStrings();   
        }
        
        else {
            System.out.println("131 dc, Data are clean!!!");
        }
        return dataAreClean;
    } 
    
    private boolean cleanTheStrings() {
        dm.whereIsWaldo(141, waldoFile, "cleanTheStrings()");
        root = new VBox();
        nStringsToClean = str_NonMissing.length;
        directions = new Text();
        directions.prefWidth(750);

        String daDirections = "\n      Double-click to edit incorrect values.  When the list on the left contains only correct values," +
                              "\n      press OK to continue.\n";

        directions.setText(daDirections);
        String captionText = "               Double click to edit                                         Old Value                                       Edited Value";

        directions.setFill(Color.RED);
        directions.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));
        captions = new Text(captionText);
        captions.setFill(Color.BLUE);
        captions.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));                
        lists = new HBox();
        lists.getChildren().addAll(lv_Uniques, listView_PreChoice, listView_PostChoice);
        hBox_Buttons = new HBox();

        btn_Cancel = new Button("Cancel");
        btn_Cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                dataAreClean = false;
                returnStatus = "Cancel";
                stage.close();
                return;
            }
        });

        btn_OK = new Button("OK");
        btn_OK.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {     
                // For the edits made
                // Make changes in the original list
                // Clean if needed, else just copy                
                for (int i = 0; i < nDataToClean; i++) {
                    for (int fromTos = 0; fromTos < nEdits; fromTos++) {
                        if (str_FixedData[i].equals(listView_From.get(fromTos))) {
                            dataAreClean = false;
                            str_FixedData[i] = listView_To.get(fromTos);
                            break;
                        }
                    }  
                }

                collectUniqueValues();
                stage.hide();
            }
        });                

        hBox_Buttons.getChildren().addAll(btn_Cancel, btn_OK);
        hBox_Buttons.setPadding(new Insets(5, 20, 5, 20));
        hBox_Buttons.setSpacing(20);

        lists.setSpacing(20);
        lists.setStyle("-fx-padding: 10;" + 
                      "-fx-border-style: solid inside;" + 
                      "-fx-border-width: 2;" +
                      "-fx-border-insets: 5;" + 
                      "-fx-border-radius: 5;" + 
                      "-fx-border-color: blue;");

        root.getChildren().addAll(directions, captions, lists, hBox_Buttons);

        Scene scene = new Scene(root);	    
        stage.setScene(scene);		
        stage.setTitle("Editing categorical list from DataManager");
        stage.showAndWait();
        return dataAreClean;
    }   //  end cleanTheseStrings
    
    public boolean isInListToClean(String thisOne) {
        inListToClean = false;
        
        for (int ith = 0; ith < nNonMissingData; ith++) {
            if (thisOne.equals(str_NonMissing[ith])) {
                inListToClean = true;
            }
        }
        
        return inListToClean;
    }
    
	
    public void editStart(ListView.EditEvent<String> e) {
        currentlyEditing = e.getIndex();
        changeFrom = lv_Uniques.getSelectionModel().getSelectedItem();
    } 

    public void editCommit(ListView.EditEvent<String> e) {
        boolean inOriginalList = false;        
        changeTo = e.getNewValue();

        for (int i = 0; i < nStringsToClean; i++) {
            if (changeTo.equals(str_CleanedData[i])) {
                inOriginalList = true;
            }
        }

        listView_PreChoice.getItems().add(changeFrom);   //  Add to the listView
        listView_PostChoice.getItems().add(changeTo);    //  Add to the listView
        listView_From.add(changeFrom);                //  Add to the ArrayList
        listView_To.add(changeTo);                    //  Add to the ArrayList
        nEdits++;
        lv_Uniques.getItems().remove(currentlyEditing);
    }  
        
    // Find and collect the unique values
    private void collectUniqueValues() {
        Map<String, Integer> mapOfStrings = new HashMap<>();        
        for (int c = 0; c < nDataToClean; c++) {            
            if (mapOfStrings.containsKey(str_FixedData[c])) {
                int value = mapOfStrings.get(str_FixedData[c]);
                mapOfStrings.put(str_FixedData[c], value + 1);
            } else {
                mapOfStrings.put(str_FixedData[c], 1);
            }
        }
        
        nUniques = mapOfStrings.size();
        Set<Map.Entry<String, Integer>> entrySet = mapOfStrings.entrySet();
        finalCategories = new String[nUniques];
        categoryCount = new int[nUniques];
        
        int index = 0;        
        for (Map.Entry<String, Integer> entry: entrySet) {
            finalCategories[index] = entry.getKey();
            categoryCount[index] = entry.getValue();
            index++;
        }
    }
    
    public String[] getFixedData() { return str_FixedData; }
    public boolean getGoodToGo() { return dataAreClean; }
    public String getReturnStatus() { return returnStatus; }
    public String[] getUniques() { return finalCategories; }
    public int getNUniques() { return nUniques; }
    public int[] getCategoryCount() { return categoryCount; }
    public String[] getFinalCategories() { return finalCategories; }
    public boolean getDoubleTrouble() { return doubleTrouble; }
    public int getNStringsToClean() { return nStringsToClean; }
}


