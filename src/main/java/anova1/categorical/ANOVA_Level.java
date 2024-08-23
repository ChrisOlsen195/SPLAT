/**************************************************
 *                   ANOVA_Level                  *
 *                    11/01/23                    *
 *                      21:00                     *
 *************************************************/
package anova1.categorical;

import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import dataObjects.CatQuantPair;
import java.util.ArrayList;

/**************************************************
 *  This class is intended to support the various *
 *  ANOVA programs in SPLAT.  It is ancient, and  *
 *  probably needs to be culled.                  *
 *************************************************/

public class ANOVA_Level  {
    // POJOs
    int nValuesInThisLevel = 0;
    double[] dataInThisLevel;    
    String levelVarLabel;
    
    // My classes
    ArrayList<CatQuantPair> al_CatQuantPairs;
    QuantitativeDataVariable levelQDV;
    
    public ANOVA_Level (String theLevelValue)  {
        System.out.println("30 ANOVA_Level, constructing");
        al_CatQuantPairs = new ArrayList<>();
        levelVarLabel = theLevelValue;
    }
    
    public ANOVA_Level (String theLevelValue, ArrayList<CatQuantPair> pre_alcqp)  {
        System.out.println("36 ANOVA_Level, constructing");
        al_CatQuantPairs = new ArrayList(pre_alcqp);
        levelVarLabel = theLevelValue;
        dataInThisLevel = new double[al_CatQuantPairs.size()];        
        for (int ith = 0; ith < al_CatQuantPairs.size(); ith++) {
            dataInThisLevel[ith] = al_CatQuantPairs.get(ith).getQuantValueDouble();
        }        
    }

    public double getIthValue(int ith)  { return dataInThisLevel[ith]; }   
    
    public void createQDV() {
        System.out.println("48 ANOVA_Level, createQDV()");
        levelQDV = new QuantitativeDataVariable(levelVarLabel, "ANOVA_Level", dataInThisLevel);
    }
    
    public QuantitativeDataVariable getQDV() { return levelQDV; }
    public UnivariateContinDataObj getUCDO () {return levelQDV.getTheUCDO(); }
    public String getLevelName() {return levelVarLabel; }
    public int getNValues() {return nValuesInThisLevel; } 
    public void setNValues(int numValues) {nValuesInThisLevel = numValues; }

    //**************************************************************
    // *   The next two methods return the data column label and    *
    // *   the data in string form, for the spreadsheet.            *
    // **************************************************************
    
    public String getLevelLabel() {return levelVarLabel; }   
    public double[] getLevelDataAsDoubles() {return dataInThisLevel; }

    public String toString() { 
        String theTo = levelVarLabel;
        theTo += "\n";
        for (int ithValue = 0; ithValue < nValuesInThisLevel; ithValue++) {
            theTo += "\n";
            theTo += getIthValue(ithValue);
        }
        return theTo;
    }
}  
