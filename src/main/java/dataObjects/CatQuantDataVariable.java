/**************************************************
 *                CatQuantDataVariable            *
 *                    11/17/24                    *
 *                      12:00                     *
 *************************************************/
package dataObjects;

import splat.*;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import utilityClasses.*;

public class CatQuantDataVariable {
    // POJOs
    
    boolean returnAll, catQuantSizesOK;
    int nLegalCQPairs, tempCatN, tempQuantN;
    
    double[] arCatDoubles;
    String strReturnStatus, strCallingProc;
    ArrayList<String> str_al_CategoryLabels;
    
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
    
    /**********************************************************
    *         Called by:   ANOVA2_PrelimANOVA1                *
    *                      ANOVA2_RCB_PrelimANOVA1            *
    *                      MultUni_Conttroller                *
    *                      Explore_2Ind_Controller            *
    *                      Indep_t_Controller                 *
    ***********************************************************/
    
    public CatQuantDataVariable(Data_Manager dm, 
                                ColumnOfData catColumn, 
                                ColumnOfData quantColumn, 
                                boolean returnAll,
                                String callingProc) {
        this.dm = dm;
        this.returnAll = returnAll;
        this.strCallingProc = callingProc;
        dm.whereIsWaldo(52, waldoFile, "Constructing"); 
        //System.out.println("53 CatQuantDataVariable, callingProc = " + callingProc);
        //System.out.println("54 CatQuantDataVariable, cat/quant size = " + catColumn.getNCasesInColumn() + " / " + quantColumn.getNCasesInColumn());
        al_CatQuantPairs = new ArrayList();
        tempCatN = catColumn.getNCasesInColumn();
        tempQuantN = quantColumn.getNCasesInColumn();
        catQuantSizesOK = true;
        if (tempCatN == tempQuantN) {   // Equal N's to begin with??
            for (int ith = 0; ith < tempCatN; ith++) {
                String tempCatString = catColumn.getStringInIthRow(ith);
                String tempQuantString = quantColumn.getStringInIthRow(ith); 
                if ((!tempCatString.equals("*")) && (!tempQuantString.equals("*"))) {
                    al_CatQuantPairs.add(new CatQuantPair(tempCatString, Double.parseDouble(tempQuantString)));
                }
            }
            int tempNPairs = al_CatQuantPairs.size();
            if (tempCatN != tempNPairs) {  // Incomplete pairs?
                MyAlerts.showUnequalNsInBivariateProcessAlert();
            }
            
        } else {  
            MyAlerts.showCantConstructCatQuantPairAlert();
            catQuantSizesOK = false;
        }        

        String catLabel = catColumn.getVarLabel();
        cdv = new CategoricalDataVariable(catLabel,catColumn);
        str_al_Levels = cdv.getListOfLevels(); // Unique values in catColumn  
    }
    
    /**********************************************************
    *         Called by:   ANOVA1_Cat_Controller              *
    *                      ANOVA1_QuantController             *
    *                      ANOVA2_PrelimANOVA1                *
    *                      ANOVA2_RCB_PrelimANOVA1            *
    *                      MultUni_Controller                 *
    *                      Explore_2Ind_Controller            *
    *                      Indep_t_Controller                 *
    **********************************************************/
    
    public CatQuantDataVariable(Data_Manager dm, 
                                ArrayList<String> al_CatVar,
                                ObservableList<String> al_QuantVar, 
                                String callingProc) {
        this.dm = dm;
        this.strCallingProc = callingProc;
        dm.whereIsWaldo(98, waldoFile, "Constructing"); 
        al_CatQuantPairs = new ArrayList();
        tempCatN = al_CatVar.size();
        tempQuantN = al_QuantVar.size();
        catQuantSizesOK = true;
        if (tempCatN == tempQuantN) {
            for (int ith = 0; ith < tempCatN; ith++) {
                String tempCatString = col_OriginalCatData.getStringInIthRow(ith);
                String tempQuantString = col_OriginalQuantData.getStringInIthRow(ith);            
                if ((!tempCatString.equals("*")) &&(!tempQuantString.equals("*"))) {
                    al_CatQuantPairs.add(new CatQuantPair(tempCatString, Double.parseDouble(tempQuantString)));
                }
            }
            int tempNPairs = al_CatQuantPairs.size();
            System.out.println("115 CatQuantDataVariable, tempNPairs = " + tempNPairs);
            
            if (tempCatN != al_CatQuantPairs.size()) {
                MyAlerts.showUnequalNsInBivariateProcessAlert(); 
            }
            
        } else {
            MyAlerts.showCantConstructCatQuantPairAlert();
            catQuantSizesOK = false;
        }
    }
    
    public String finishConstructingStacked() {
        dm.whereIsWaldo(125, waldoFile, "finishConstructingStacked()");
        
        if (!catQuantSizesOK) { return "Cancel"; }
        strReturnStatus = "OK";
        
        if (tempCatN != tempQuantN) {
            dm.whereIsWaldo(131, waldoFile, "finishConstructingStacked()");
            switch(strCallingProc) {
                case "ANOVA2_RCB_PrelimANOVA1":
                    MyAlerts.showIncompleteBlocksAlert();
                    break;
                    
                default: /*  No op  */
            }
            
            dm.whereIsWaldo(140, waldoFile, "finishConstructingStacked()");
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }

        nLegalCQPairs = al_CatQuantPairs.size();   
        
        arCatDoubles = new double[nLegalCQPairs];
        for (int dbl = 0; dbl < nLegalCQPairs; dbl++) {
            arCatDoubles[dbl] = al_CatQuantPairs.get(dbl).getQuantValueDouble();
        }
        
        unstackCatQuantPairs();
        dm.whereIsWaldo(153, waldoFile, "finishConstructingStacked()");
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
        dm.whereIsWaldo(170, waldoFile, "unstackCatQuantPairs()"); 
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
        dm.whereIsWaldo(205, waldoFile, "finish unstackCatQuantPairs()");
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
        dm.whereIsWaldo(247, waldoFile, "createNewQDV(String qdvLabel, String qdvDescr, int fromHere, int toThere)");
        str_al_CategoryLabels.add(qdvLabel);
        QuantitativeDataVariable theNewQDV;
        int thisMany = toThere - fromHere + 1;
        double[] theQuants = new double[thisMany];
        
        for (int ith = 0; ith < thisMany; ith++) {
            theQuants[ith] = al_CatQuantPairs.get(ith + fromHere).getQuantValueDouble();
        }
        
        theNewQDV = new QuantitativeDataVariable(qdvLabel, qdvDescr, theQuants);
        dm.whereIsWaldo(258, waldoFile, "finish createNewQDV(String qdvLabel, String qdvDescr, int fromHere, int toThere)");
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
    
    /**************************************************************
     *    Called by MultUni_DotPlotView                           *
     *************************************************************/
    public ArrayList<String> getCategoryLevels() { 
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
        System.out.println("\nCatQuantDataVariable.toString()");
        nLegalCQPairs = al_CatQuantPairs.size();
        for (int ithPair = 0; ithPair < nLegalCQPairs; ithPair++) {
            String theCat = al_CatQuantPairs.get(ithPair).getCatValue();
            double theDouble = al_CatQuantPairs.get(ithPair).getQuantValueDouble();
            System.out.println("cat/quant = " + theCat + " / " + theDouble);            
        } 
        System.out.println("end CatQuantDataVariable.toString()\n");
        return "cqdv done.";
    }
}
