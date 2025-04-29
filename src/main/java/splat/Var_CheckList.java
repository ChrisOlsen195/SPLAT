/************************************************************
 *                     Splat_VarCheckList                   *
 *                          01/20/25                        *
 *                            18:00                         *
 ***********************************************************/
package splat;

import java.util.ArrayList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class Var_CheckList {
    // POJOs
    int numVars;
    
    Double stdWidth = 125.0;
    Double stdHeight = 200.0;
    
    // POJOs / FX
    ArrayList<CheckBox> box;
    ScrollPane sPanel;
    Data_Manager dm;
    PositionTracker tracker;
    VBox vPanel;

    public Var_CheckList (Data_Manager dm, Boolean numericOnly, Double width, Double height) {                
        this.dm = dm;
        System.out.println("29 Var_CheckList, constructing");
        tracker = dm.getPositionTracker();
        numVars = tracker.getNVarsInStruct();

        if (width != null) { stdWidth = width; }        
        if (height != null) { stdHeight = height; }
        
        vPanel = new VBox(5);
        box = new ArrayList();
        
        for (int i = 0; i < numVars; i++) {
            box.add(new CheckBox());
            box.get(i).setPrefWidth(stdWidth);
            box.get(i).setText(this.dm.getVariableName(i));
            vPanel.getChildren().add(box.get(i));            
            if ((numericOnly) & (!this.dm.getDataType(i).equals("Quantitative"))) {
                box.get(i).setDisable(true);
            }
        }
        sPanel = new ScrollPane(vPanel);
    } 
    
    public ScrollPane getPane () {        
        sPanel.setMinHeight(stdHeight);
        sPanel.setMaxHeight(stdHeight);
        sPanel.setMinWidth(stdWidth);
        sPanel.setMaxWidth(stdWidth);
        sPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return sPanel;
        
    } 
    
    public void setDisabled (boolean setMe) {
        for (int i = 0; i < numVars; i++) { box.get(i).setDisable(setMe); }
    }
    
    public ArrayList<Integer> getSelected () {        
        ArrayList<Integer> selected = new ArrayList();        
        for (int i = 0; i < tracker.getNVarsInStruct(); i++) {            
            if (box.get(i).isSelected()) {
                selected.add(i);
            }
        }        
        return selected;        
    }   
} 