/**************************************************
 *                CatQuantDataVariable            *
 *                    05/24/24                    *
 *                      15:00                     *
 *************************************************/
package dataObjects;

import splat.*;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import utilityClasses.*;

public class CatQuantDataVariable {
    // POJOs
    
    boolean returnAll;
    int nLegalCQPairs, tempCatN, tempQuantN;
    
    double[] arCatDoubles;
    String strReturnStatus, strCallingProc;
    ArrayList<String> str_al_CategoryLabels;
    
    //ArrayList<String> str_al_OriginalLabels;
    //ObservableList<String> str_List_TransformedLabels;
    
    // Make empty if no-print
    //String waldoFile = "CatQuantDataVariable";
    String waldoFile = "";
    
    // My classes
    ArrayList<CatQuantPair> al_CatQuantPairs;
    ArrayList<String> str_al_Levels;
    CategoricalDataVariable cdv;
    ColumnOfData col_OriginalCatData, col_OriginalQuantData;
    QuantitativeDataVariable qdv_Temp;
    ArrayList<QuantitativeDataVariable> al_QDVs;
    Data_Manager dm;
    
    public CatQuantDataVariable(Data_Manager dm, 
                                ColumnOfData catColumn, 
                                ColumnOfData quantColumn, 
                                boolean returnAll,
                                String callingProc) {
        this.dm = dm;
        this.returnAll = returnAll;
        this.strCallingProc = callingProc;
        dm.whereIsWaldo(47, waldoFile, "Constructing"); 
        String catLabel = catColumn.getVarLabel();
        cdv = new CategoricalDataVariable(catLabel,catColumn);
        str_al_Levels = cdv.getListOfLevels(); // Unique values in catColumn
        col_OriginalCatData = new ColumnOfData(catColumn);
        col_OriginalQuantData = new ColumnOfData(quantColumn);  
    }
    
    public CatQuantDataVariable(Data_Manager dm, 
                                ArrayList<String> originalLabels,
                                ObservableList<String> transformedLabels, 
                                String callingProc) {
        this.dm = dm;
        this.strCallingProc = callingProc;
        dm.whereIsWaldo(61, waldoFile, "Constructing"); 
        //this.str_al_OriginalLabels = originalLabels;
        //this.str_List_TransformedLabels = transformedLabels;
        al_CatQuantPairs = new ArrayList();
        for (int ithPair = 0; ithPair < originalLabels.size(); ithPair++) {
            String pairString = originalLabels.get(ithPair);
            double pairDouble = Double.parseDouble(transformedLabels.get(ithPair));
            al_CatQuantPairs.add(new CatQuantPair(pairString, pairDouble));
        }
    }
    
    public String finishConstructingStacked() {
        dm.whereIsWaldo(73, waldoFile, "finishConstructingStacked()");
        strReturnStatus = "OK";
        tempCatN = col_OriginalCatData.getColumnSize();
        tempQuantN = col_OriginalQuantData.getColumnSize();
        
        if (tempCatN != tempQuantN) {
            dm.whereIsWaldo(79, waldoFile, "finishConstructingStacked()");
            //System.out.println("80 CatQuantDataVariable, callingProc = " + strCallingProc);
            switch(strCallingProc) {
                case "ANOVA2_RCB_PrelimANOVA1":
                    MyAlerts.showIncompleteBlocksAlert();
                    break;
                    
                default: MyAlerts.showUnequalNsInBivariateProcessAlert();
            }
            
            dm.whereIsWaldo(89, waldoFile, "finishConstructingStacked()");
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }
        
        // Create a list of pairs where neither pair value is missing ( = "*")
        al_CatQuantPairs = new ArrayList<>();
        
        for (int ith = 0; ith < tempCatN; ith++) {
            String tempCatString = col_OriginalCatData.getStringInIthRow(ith);
            String tempQuantString = col_OriginalQuantData.getStringInIthRow(ith);            
            if ((!tempCatString.equals("*")) &&(!tempQuantString.equals("*"))) {
                al_CatQuantPairs.add(new CatQuantPair(tempCatString, Double.parseDouble(tempQuantString)));
            }
        }

        nLegalCQPairs = al_CatQuantPairs.size();   
        
        arCatDoubles = new double[nLegalCQPairs];
        for (int dbl = 0; dbl < nLegalCQPairs; dbl++) {
            arCatDoubles[dbl] = al_CatQuantPairs.get(dbl).getQuantValueDouble();
        }
        
        unstackCatQuantPairs();
        dm.whereIsWaldo(113, waldoFile, "finishConstructingStacked()");
        return strReturnStatus;
    }
    
    /*********************************************************************
    *  If returnAll is true, a QDV with all (stacked) values is returned *
    *  with the unstacked QDVs.  Generally ALL is needed for graphs, but *
    *  only the unstacked QDVs are needed for the independent t.  The    *
    *  unstacked QDVs are returned for analysis as unstacked QDVs.       *
    *  The default sort here is by Cat values for ANOVA purposes.        *
    *  MultVar dot plots need to be explicitly sorted by Quant value by  *
    *  the calling class.                                                *
    *********************************************************************/
    
    private void unstackCatQuantPairs() {
        boolean endOfStory;
        int startOfTie, endOfTie, cqpCompare;
        dm.whereIsWaldo(130, waldoFile, "unstackCatQuantPairs()"); 
        // Sort the data points by first dataVariable
        sortByCatValue();
        // Separate the data by value of the categorical variable
        al_QDVs = new ArrayList<>();
        str_al_CategoryLabels = new ArrayList<>();

        if (returnAll == true) {
            qdv_Temp = createNewQDV("All", "All", 0, nLegalCQPairs - 1);
            al_QDVs.add(qdv_Temp);
        }

        startOfTie = 0;    //  Start process at first number;
        endOfTie = 0;      // subscript is as in ArrayList
        endOfStory = false;
        
        do {            
            for (int ithPair = startOfTie; ithPair < nLegalCQPairs; ithPair++) {
                cqpCompare = (al_CatQuantPairs.get(ithPair).getCatValue()).compareTo(al_CatQuantPairs.get(startOfTie).getCatValue());                
                if ( cqpCompare <= 0) {
                    endOfTie = ithPair;
                }
            }  
            
            String newLabel = al_CatQuantPairs.get(startOfTie).getCatValue(); 
            String newDescr = newLabel;
            qdv_Temp = createNewQDV(newLabel, newDescr, startOfTie, endOfTie);

            al_QDVs.add(qdv_Temp);      
            startOfTie = endOfTie + 1;
            endOfTie = startOfTie;
        
            if (endOfTie == nLegalCQPairs) { endOfStory = true; }
            
        }  while (endOfStory == false);  //    End do
        dm.whereIsWaldo(165, waldoFile, "finish unstackCatQuantPairs()");
    }   //  unstackCatQuantPairs()
    
    public void sortByCatValue() {
        int cqpCompare;
        for (int k = 1; k < nLegalCQPairs; k++) {            
            for (int i = 0; i < nLegalCQPairs - k; i++)  {                
                cqpCompare = (al_CatQuantPairs.get(i).getCatValue()).compareTo(al_CatQuantPairs.get(i + 1).getCatValue());                
                if (cqpCompare > 0)  {
                    String tempCatValue = al_CatQuantPairs.get(i).getCatValue();
                    double tempQuantValue = al_CatQuantPairs.get(i).getQuantValueDouble();
                    
                    al_CatQuantPairs.get(i).setCatValue(al_CatQuantPairs.get(i + 1).getCatValue());
                    al_CatQuantPairs.get(i).setQuantValueDouble(al_CatQuantPairs.get(i + 1).getQuantValueDouble());
                    
                    al_CatQuantPairs.get(i + 1).setCatValue(tempCatValue);
                    al_CatQuantPairs.get(i + 1).setQuantValueDouble(tempQuantValue);   
                }
            }
        }        
    }
    
    public void sortByQuantValue() {
        for (int k = 1; k < nLegalCQPairs; k++) {            
            for (int i = 0; i < nLegalCQPairs - k; i++)  {                
                double ithQuant = al_CatQuantPairs.get(i).getQuantValueDouble();
                double ithPlusOneQuant = al_CatQuantPairs.get(i + 1).getQuantValueDouble();
                if (ithQuant > ithPlusOneQuant)  {
                    String tempCatValue = al_CatQuantPairs.get(i).getCatValue();
                    double tempQuantValue = al_CatQuantPairs.get(i).getQuantValueDouble();
                    
                    al_CatQuantPairs.get(i).setCatValue(al_CatQuantPairs.get(i + 1).getCatValue());
                    al_CatQuantPairs.get(i).setQuantValueDouble(al_CatQuantPairs.get(i + 1).getQuantValueDouble());
                    
                    al_CatQuantPairs.get(i + 1).setCatValue(tempCatValue);
                    al_CatQuantPairs.get(i + 1).setQuantValueDouble(tempQuantValue);   
                }
            }
        }         
    }
    
    private QuantitativeDataVariable createNewQDV(String qdvLabel, String qdvDescr, int fromHere, int toThere) {
        dm.whereIsWaldo(207, waldoFile, "createNewQDV(String qdvLabel, String qdvDescr, int fromHere, int toThere)");
        str_al_CategoryLabels.add(qdvLabel);
        QuantitativeDataVariable theNewQDV;
        int thisMany = toThere - fromHere + 1;
        double[] theQuants = new double[thisMany];
        
        for (int ith = 0; ith < thisMany; ith++) {
            theQuants[ith] = al_CatQuantPairs.get(ith + fromHere).getQuantValueDouble();
        }
        
        theNewQDV = new QuantitativeDataVariable(qdvLabel, qdvDescr, theQuants);
        dm.whereIsWaldo(218, waldoFile, "finish createNewQDV(String qdvLabel, String qdvDescr, int fromHere, int toThere)");
        return theNewQDV;
    }
    
    public QuantitativeDataVariable get_IthQDV(int ithQDV) { return al_QDVs.get(ithQDV);}
    
    public int get_nQDVs() { return al_QDVs.size(); }
    public ArrayList<QuantitativeDataVariable> getAllQDVs() { return al_QDVs; }
    
    public void unstackToDataStruct() { 
        dm.addToStructNColumnsWithExistingData(al_QDVs);
    }
    
    public int getNLegalCQPairs() { return nLegalCQPairs; }
    
    public int getCountOfLabels() { return str_al_CategoryLabels.size(); }
    
    public ArrayList<String> getCategoryLevels() { 
        //for (int ithLevel = 0; ithLevel < str_al_Levels.size(); ithLevel++) {
            //System.out.println(str_al_Levels.get(ithLevel));
        //}
        return str_al_Levels;
    }
    
    public ArrayList<CatQuantPair> getCatQuantPairs() {return al_CatQuantPairs; }
    
    public String getIthCatQuantPair_Cat(int ith) { 
        return al_CatQuantPairs.get(ith).getCatValue(); 
    }
    public double getIthCatQuantPair_Double(int ith) {
        return al_CatQuantPairs.get(ith).getQuantValueDouble();
    }
    
    public CatQuantPair getIthCatQuantPair(int ith) { return al_CatQuantPairs.get(ith); }
    
    public double[] get_arCatDoubles() { return arCatDoubles; }
    
    public String toString() {
        nLegalCQPairs = al_CatQuantPairs.size();
        for (int ithPair = 0; ithPair < nLegalCQPairs; ithPair++) {
            String theCat = al_CatQuantPairs.get(ithPair).getCatValue();
            double theDouble = al_CatQuantPairs.get(ithPair).getQuantValueDouble();
            System.out.println("260 cqdv, cat/quant = " + theCat + " / " + theDouble);            
        }        
        return "cqdv done.";
    }
}
