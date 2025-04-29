/**************************************************
 *             UnivariateCategoricalDataObj       *
 *                    01/15/25                    *
 *                     12:00                      *
 *************************************************/
package dataObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UnivariateCategoricalDataObj {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nLegalValues, nCategories, nUniques;
    int[] observedCounts;
    
    String varLabel;
    String[] strLegalValues, strUniqueCategories;
    ArrayList<String> str_al_LegalValues;
    
    // My classes
    ArrayList<CatQuantPair> al_CatQuantPairs;
    
    public UnivariateCategoricalDataObj(ColumnOfData colOfData) {
        if (printTheStuff == true) {
            System.out.println("31 *** UnivariateCategoricalDataObj, Constructing");
        }
        int colSize = colOfData.getColumnSize();
        varLabel = colOfData.getVarLabel();
        str_al_LegalValues = new ArrayList<>();
        
        for (int ithDat = 0; ithDat < colSize; ithDat++) {
            String tempString = colOfData.getStringInIthRow(ithDat);
            
            if (!tempString.equals("*")) {
                str_al_LegalValues.add(tempString);
            }
        }
        
        nLegalValues = str_al_LegalValues.size();
        strLegalValues = new String[nLegalValues];
        
        for (int ithLegalDat = 0; ithLegalDat < nLegalValues; ithLegalDat++) {
            strLegalValues[ithLegalDat] = str_al_LegalValues.get(ithLegalDat);
        } 
        
        doObservedCounts();
        makeALCatQuantPairs();   
    }
    
    
    private void doObservedCounts() {
        if (printTheStuff == true) {
            System.out.println("59 --- UnivariateCategoricalDataObj, doObservedCounts()");
        }
        Map<String, Integer> mapOfStrings = new HashMap<>();
        for (int c = 0; c < nLegalValues; c++) {
            if (mapOfStrings.containsKey(strLegalValues[c])) {
                int value = mapOfStrings.get(strLegalValues[c]);
                mapOfStrings.put(strLegalValues[c], value + 1);
            } else {
                mapOfStrings.put(strLegalValues[c], 1);
            }
        }       
        nCategories = mapOfStrings.size();
        Set<Map.Entry<String, Integer>> entrySet = mapOfStrings.entrySet();
        strUniqueCategories = new String[nCategories];
        observedCounts = new int[nCategories];
        
        int index = 0;       
        
        for (Map.Entry<String, Integer> entry: entrySet) {
            strUniqueCategories[index] = entry.getKey();
            observedCounts[index] = entry.getValue();
            index++;
        }
        
        nUniques = mapOfStrings.size();
    }
    
    private void makeALCatQuantPairs() {
        al_CatQuantPairs = new ArrayList();
        
        for (int ithValue = 0; ithValue < nUniques; ithValue++) {
            al_CatQuantPairs.add(new CatQuantPair(strUniqueCategories[ithValue], observedCounts[ithValue]));
        }
    }
    
    @Override
    public String toString() {        
       String daReturnString = "finit UnivariateCategoricalDatObj";
       System.out.println("\nUnivariateCategoricalDatObj");
       System.out.println(" Var label = " + varLabel);
        System.out.println("  index    Unique         Count");
        for (int ithValue = 0; ithValue < nUniques; ithValue++) {
            System.out.println("    " + ithValue + "       " + strUniqueCategories[ithValue] + "       " + observedCounts[ithValue]);
        }
        System.out.println("END UnivariateCategoricalDatObj\n");
        return daReturnString;
    }
    
    public int getNUniques() { return nUniques; }    
    public String getIthValue(int ith) { return str_al_LegalValues.get(ith); }    
    public String[] getCategories() {return strUniqueCategories; }    
    public int[] getObservedCounts() { return observedCounts; }   
    public UnivariateCategoricalDataObj getUnivCatDataObj() { return this; }  
}
