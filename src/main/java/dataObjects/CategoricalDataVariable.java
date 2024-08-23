/**************************************************
 *              CategoricalDataVariable           *
 *                    05/24/24                    *
 *                      15:00                     *
 *************************************************/
package dataObjects;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoricalDataVariable {
    // POJOs
    private int nDataPoints, nLegalDataPoints, nLevels;
    private String varLabel;
    private String[] str_Data, str_LegalData, str_SortedLegalData;
    private ArrayList<String> str_al_Levels, str_al_LegalDataStrings;
    private ArrayList<Integer> al_Frequencies;
    
    // My classes
    public CategoricalDataVariable() { 
        //System.out.println("21 CatagoricalDataVariable, constructing: Default constructor");
    }
    
    //  Used by ANOVA2_Model and RCB_Model
    public CategoricalDataVariable (String dataLabel, int nDataPoints) {
        //System.out.println("26 CatagoricalDataVariable, constructing: Label & int points");
        this.varLabel = dataLabel;
        this.nDataPoints = nDataPoints;
        str_Data = new String[nDataPoints];
        nLevels = 0;
    }

   //    Used by ANOVA2 Controller
    public CategoricalDataVariable (String inLabel, String[] inDataStrings)  {
        //System.out.println("35 CatagoricalDataVariable, constructing: Label & String points");
        varLabel = inLabel;
        nDataPoints = inDataStrings.length;
        str_Data = new String[nDataPoints];
        System.arraycopy(inDataStrings, 0, str_Data, 0, nDataPoints);   
        nLevels = 0;
    }
    
   //    Exploring data, categorical
    public CategoricalDataVariable (String varLabel, ColumnOfData colOfData)  {
        //System.out.println("45 CatagoricalDataVariable, constructing: Label & Column");
        this.varLabel = varLabel;
        nDataPoints = colOfData.getNCasesInColumn();
        str_Data = new String[nDataPoints];

        str_al_LegalDataStrings = new ArrayList<>();
        
        for (int ithCase = 0; ithCase < nDataPoints; ithCase++) {
            String strTemp = colOfData.getStringInIthRow(ithCase);
            str_Data[ithCase] = strTemp;
            
            if (!strTemp.equals("*")) {
                str_al_LegalDataStrings.add(strTemp);
            }
        }
        nLegalDataPoints = str_al_LegalDataStrings.size();
        str_LegalData = new String[nLegalDataPoints];
        str_SortedLegalData = new String[nLegalDataPoints];
        str_LegalData = str_al_LegalDataStrings.toArray(str_LegalData);
        System.arraycopy(str_LegalData, 0, str_SortedLegalData, 0, nLegalDataPoints);    
        nLevels = 0;
    }

    public void analyzeLevels() {
        String[] sortedArray = new String[nDataPoints];
        System.arraycopy(str_Data, 0, sortedArray, 0, nDataPoints);  
        Arrays.sort(sortedArray);
        
        nLevels = 1;
        str_al_Levels = new ArrayList<>();
        str_al_Levels.add(sortedArray[0]);
        
        for (int ith = 1; ith < nDataPoints; ith++) {
            if (!sortedArray[ith].equals(sortedArray[ith - 1]))  {
                nLevels++;
                str_al_Levels.add(sortedArray[ith]);
            } 
        }
    }
 
    public void createFrequencyTable() {
        int lowIndex;
        String[] sortedArray = new String[nDataPoints];
        System.arraycopy(str_LegalData, 0, sortedArray, 0, nDataPoints);  
        Arrays.sort(sortedArray);
        nLevels = 1;
        lowIndex = 0;
        str_al_Levels = new ArrayList<>();
        al_Frequencies = new ArrayList<>();
        str_al_Levels.add(sortedArray[0]);
        
        for (int ith = 1; ith < nDataPoints; ith++) {
            
            if (!sortedArray[ith].equals(sortedArray[ith - 1]))  {
                nLevels++;
                str_al_Levels.add(sortedArray[ith]);
                int tempInt = (ith - 1) - lowIndex + 1;
                al_Frequencies.add(tempInt);
                lowIndex = ith;
            } 
        }       
    }
    
    
    public int get_N () {return nDataPoints;}   
    
    public int getNumberOfLevels() { 
        if (nLevels == 0) {analyzeLevels(); }
        return nLevels; 
    }   
    
    public ArrayList<String> getListOfLevels() { 
        if (nLevels == 0) { analyzeLevels(); }        
        return str_al_Levels; 
    }
    
    public String getIthDataPtAsString(int ith) {return str_Data[ith]; }    
    public void setIthDataPtAsString(int ith, String ithString) {str_Data[ith] = ithString; }       
    public String[] getDataAsStrings() {return str_Data; }    
    public String getTheDataLabel() {return varLabel; }
    public void setDataLabel( String daLabel) {varLabel = daLabel; }    
    public CategoricalDataVariable getDataVariable() { return this; }
    
    public String toString() {
        String theTo = "\nCatDataVar.toString " + varLabel;
        
        for (int ithDataPoint = 0; ithDataPoint < nDataPoints; ithDataPoint++)  {
            theTo += "\n";
            theTo += str_Data[ithDataPoint];
        }
        return theTo;
    }
}
