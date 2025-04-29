/************************************************************
 *                          VarList                         *
 *                          01/16/25                        *
 *                            12:00                         *
 ***********************************************************/
package splat;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class Var_List {
    //POJO
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int numVars;

    Double stdWidth = 125.0;
    Double stdHeight = 200.0;
    
    ObservableList<String> varNames;
    
    // My classes
     Data_Manager dm;
     PositionTracker tracker;
     
    // POJOs / FX
    ListView vPanel;

    public Var_List(Data_Manager dm, Double width, Double height) {
        this.dm = dm;
        if (printTheStuff == true) {
            System.out.println("36 *** Var_List, Constructing");
        }
        tracker = new PositionTracker();
        tracker = dm.getPositionTracker();
        numVars = tracker.getNVarsInStruct();
        
        if (width != null) { stdWidth = width;  }        
        if (height != null) { stdHeight = height; }

        vPanel = new ListView();
        varNames = FXCollections.observableArrayList();
        varNames = dm.getVariableNames();
        vPanel.setItems(varNames);
        vPanel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    } // constructor

    public ListView getPane() {
        vPanel.setMinHeight(stdHeight);
        vPanel.setMaxHeight(stdHeight);
        vPanel.setMinWidth(stdWidth);
        vPanel.setMaxWidth(stdWidth);
        return vPanel;
    }
    
    public void clearList() { varNames.remove(0, varNames.size()); }

    public void resetList() {
        clearList();        
        for (int i = 0; i < numVars; i++) {
            varNames.add(dm.getVariableName(i));
        }
    }

    public void delVarName(ArrayList<String> thisOne) {
        varNames.removeAll(thisOne);
    }

    public void addVarName(ArrayList<String> addThis) {
        varNames.addAll(addThis);
    }

    public int getNumSelected() {
        return vPanel.getSelectionModel().getSelectedItems().size();
    }

    public ArrayList<String> getNamesSelected() {
        ArrayList<String> selectedNames = new ArrayList();
        selectedNames.addAll(vPanel.getSelectionModel().getSelectedItems());
        return selectedNames;
    } 

    public ArrayList<Integer> getVarIndices() {
        ArrayList<String> allNames = new ArrayList();
        ArrayList<Integer> selectedVars = new ArrayList();
        allNames.addAll(varNames);        
        for(String eachVar : allNames) {
            selectedVars.add(dm.getVariableIndex(eachVar));
        }
        return selectedVars;
    } 
    
    public ObservableList<String> getVarList() { return varNames; }
    
    public int getVarListSize() { return varNames.size(); }
    
    public String toString() {
        System.out.println("\n\n\"OK, here's the VarList");
        System.out.println("98 varList.toString, numVars = " + numVars);
        System.out.println("99 varList, varNames.getsize = " + varNames.size());
        
        for (int iVars = 0; iVars < varNames.size(); iVars++) {
            System.out.println(varNames.get(iVars));
        }
        return "OK, that's the VarList";
    }
} 
