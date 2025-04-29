/**************************************************
 *           BivariateCategoricalDataObj          *
 *                    03/22/25                    *
 *                     21:00                      *
 *************************************************/
package dataObjects;

import java.util.ArrayList;
import splat.Data_Manager;

public class BivariateCategoricalDataObj {
    // POJOs
    int nOriginalDataPoints, nLegalDataPoints, nDataPointsMissing;
    String xLabel, yLabel; //, xDescription, yDescription;
    ArrayList<String> al_legalXVariable, al_legalYVariable;
    
    // Make empty if no-print
    String waldoFile = "BivariateCategoricalDataObj";
    //String waldoFile = "";
    
    // My classes
    ArrayList<ColumnOfData> bivCatDataOut;
    //Data_Manager dm;
    
    //  Called by X2Assoc_Controller 75, and and BivCatController 72 and 124
    public BivariateCategoricalDataObj(Data_Manager dm, String xDescription, 
                                       String yDescription, 
                                       ArrayList<ColumnOfData> inBivDat) {
        dm.whereIsWaldo(29, waldoFile, "Constructing, x/y Description = " + xDescription + " / " + yDescription);
        xLabel = xDescription;
        yLabel = yDescription;
        al_legalXVariable = new ArrayList<>();
        al_legalYVariable = new ArrayList<>();

        nOriginalDataPoints = inBivDat.get(0).getColumnSize();
        
        for (int ithPoint = 0; ithPoint < nOriginalDataPoints; ithPoint++) {
            
            String xTemp = inBivDat.get(0).getTheCases_ArrayList().get(ithPoint);
            String yTemp = inBivDat.get(1).getTheCases_ArrayList().get(ithPoint);
            
            if (!xTemp.equals("*") && !yTemp.equals("*")) {
                nLegalDataPoints++;
                al_legalXVariable.add(xTemp); al_legalYVariable.add(yTemp);
            }
        }
        
        nLegalDataPoints = al_legalXVariable.size();        
        nDataPointsMissing = nOriginalDataPoints - nLegalDataPoints;  
        
        bivCatDataOut = new ArrayList();
        bivCatDataOut.add(new ColumnOfData(dm, xLabel, "BivCatDataObj", al_legalXVariable));
        bivCatDataOut.add(new ColumnOfData(dm, yLabel, "BivCatDataObj", al_legalYVariable));
    }    
    
    public BivariateCategoricalDataObj(Data_Manager dm, ColumnOfData colA, ColumnOfData colB) {
        //this.dm = dm;
        dm.whereIsWaldo(58, waldoFile, "Constructing with two columns");
        xLabel = colA.getVarLabel();
        yLabel = colB.getVarLabel();
        al_legalXVariable = new ArrayList<>();
        al_legalYVariable = new ArrayList<>();

        nOriginalDataPoints = colA.getColumnSize();
        
        for (int ithPoint = 0; ithPoint < nOriginalDataPoints; ithPoint++) {
            String xTemp = colA.getTheCases_ArrayList().get(ithPoint);
            String yTemp = colB.getTheCases_ArrayList().get(ithPoint);
            
            if (!xTemp.equals("*") && !yTemp.equals("*")) {
                nLegalDataPoints++;
                al_legalXVariable.add(xTemp); al_legalYVariable.add(yTemp);
            }
        }
        
        nLegalDataPoints = al_legalXVariable.size();        
        nDataPointsMissing = nOriginalDataPoints - nLegalDataPoints;  
        
        bivCatDataOut = new ArrayList();
        bivCatDataOut.add(new ColumnOfData(dm, xLabel, "BivCatDataObj", al_legalXVariable));
        bivCatDataOut.add(new ColumnOfData(dm, yLabel, "BivCatDataObj", al_legalYVariable));
    }     
    
    public ArrayList<ColumnOfData> getLegalColumns() { return bivCatDataOut; }
    public int getNLegalDataPoints() { return nLegalDataPoints; }
    public int getNMissingDataPoints() { return nDataPointsMissing; }
}
