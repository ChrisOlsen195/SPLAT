/************************************************************
 *                     Logistic_Controller                  *
 *                          11/11/24                        *
 *                            12:00                         *
 ***********************************************************/
/******************************************************************
 *   It is not obvious in the code what is happening here.  This   *
 *   procedure transforms the raw data into summary form for the   *
 *   Logistic_Model as per page 426 in:                            *
 *          Montgomery, D. C., Peck, E. A, & Vining,G. G.          *
 *          Introduction to Linear Regression Analysis (4th)       *
 *          Wiley Series in Probability and Statistics             *
 * ****************************************************************/
package simpleLogisticRegression;

import dialogs.regression.Logistic_Dialog;
import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import matrixProcedures.Matrix;
import splat.Data_Manager;
import utilityClasses.*;

public class Logistic_Controller {
    // POJOs
    private int nPoints, nGroups, totalNObservations, totalNSuccesses;    
    private int[] nObservations, nSuccesses;
    
    private String returnStatus, explanVar, respVar, respVsExplanVar,
                   firstVarDescr;
    ArrayList<String> xStrings, yStrings, al_tempStr;
    
    // Make empty if no-print
    String waldoFile = "Logistic_Controller";
    //String waldoFile = "";
    
    private String[] dataXYLabels, unique_Xs, strUniques;

    // My classes
    BivariateContinDataObj bivContinDataObj;
    Data_Manager dm;
    private LogisticReg_Model logisticReg_Model;
    private Logistic_Dashboard logRegDashboard;
    Logistic_Dialog logistic_Dialog;
    Matrix X, Y;
    MyYesNoAlerts myYesNoAlerts;
    QuantitativeDataVariable qdv_XVariable, qdv_YVariable;

    public Logistic_Controller(Data_Manager dm) {
        this.dm = dm;      
        dm.whereIsWaldo(52, waldoFile, "Constructing");
        myYesNoAlerts = new MyYesNoAlerts();
    }  
        
    public String doTheProcedure() {    //  Called from Main Menu
        dm.whereIsWaldo(56, waldoFile, "doTheProcedure()");
        try {
            int casesInStruct = dm.getNCasesInStruct();
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert_1Var();
                return "Cancel";
            }
            dm.whereIsWaldo(68, waldoFile, "doTheProcedure()");
            logistic_Dialog = new Logistic_Dialog(dm, "QUANTITATIVE");
            logistic_Dialog.showAndWait();
            returnStatus = logistic_Dialog.getReturnStatus();
            
            if (!returnStatus.equals("OK")) { return returnStatus; }
            
            firstVarDescr = logistic_Dialog.getPreferredFirstVarDescription();;
            respVsExplanVar = logistic_Dialog.getSubTitle();
            ArrayList<ColumnOfData> originalData = logistic_Dialog.getData();
            
            /******************************************************************
             *  This is essentially a copy constructor. It is needed b/c      *
             *  this procedure would write over the original data if more     *
             *  than one logistic regression is called; the copy constructor  *
             *  in ColumnOfData returns a reference to the original data.     *
             *****************************************************************/
            ArrayList<ColumnOfData> data = new ArrayList();
            data.add(originalData.get(0));
            String tempLabel = originalData.get(1).getVarLabel();
            String tempDescr = originalData.get(1).getVarDescription();
            al_tempStr = new ArrayList();
            for (int ithElement = 0; ithElement < originalData.get(1).getNCasesInColumn(); ithElement++) {
                al_tempStr.add(originalData.get(1).getIthCase(ithElement));
            }
            data.add(new ColumnOfData(tempLabel, tempDescr, al_tempStr)); 
            /******************************************************************
             *  This is essentially a copy constructor. It is needed b/c      *
             *  this procedure would write over the original data if more     *
             *  than one logistic regression is called; the copy constructor  *
             *  in ColumnOfData returns a reference to the original data.     *
             *****************************************************************/
            
            DataCleaner dc = new DataCleaner(dm, data.get(1));
            dc.cleanAway();
            int nUniques = dc.getNUniques();
            strUniques = dc.getUniques();
            
            if (nUniques != 2) {
                MyAlerts.showMustBeTwoUniquesInLogisticAlert();
                return "NotOK";
            }
            
            ColumnOfData colOfData = new ColumnOfData(dm, "LogisticContr83", "LogisticRegContr83", dc.getFixedData());
            int colSize = colOfData.getNCasesInColumn();            
            
            if(!columnIsZeroOne()) { 
                dm.whereIsWaldo(89, waldoFile, "doTheProcedure()");
                String[] twoCategories = dc.getFinalCategories();    
                System.out.println("91 Logistic_Controller, strUniques[0] = " + strUniques[0]);
                System.out.println("92 Logistic_Controller, strUniques[1] = " + strUniques[1]);
                String theYes  = "Make " + strUniques[0] + " my failure value";
                String theNo  = "Make " + strUniques[1] + " my failure value";
                myYesNoAlerts.logReg_Choose_0_And_1_Alert(strUniques[0], 
                                                          strUniques[1], 
                                                          theYes, 
                                                          theNo);
                String logRegChoice = myYesNoAlerts.getYesOrNo();
                System.out.println("102 LogisticController, logRegChoice = " + logRegChoice);
                if (logRegChoice.equals("Yes")) {
                
                
                for (int ithCase = 0; ithCase < colSize; ithCase++) {
                    if ((colOfData.getStringInIthRow(ithCase).trim()).equals((twoCategories[0].trim()))) {                        
                        data.get(1).setStringInIthRow(ithCase, "0");
                    }
                    else  { data.get(1).setStringInIthRow(ithCase, "1"); }  
                }
                
                }  else  {
                    for (int ithCase = 0; ithCase < colSize; ithCase++) {
                        if ((colOfData.getStringInIthRow(ithCase).trim()).equals((twoCategories[0].trim()))) {                        
                            data.get(1).setStringInIthRow(ithCase, "1");
                        }
                        else  { data.get(1).setStringInIthRow(ithCase, "0"); }  
                    }
                }   
            }
            dm.whereIsWaldo(98, waldoFile, "doTheProcedure()");
            qdv_XVariable = new QuantitativeDataVariable("LogisticRegContr95", "LogisticRegContr95", data.get(0));
            qdv_YVariable = new QuantitativeDataVariable("LogisticRegContr96", "LogisticRegContr96", data.get(1)); 

            dataXYLabels = new String[2];
            explanVar = qdv_XVariable.getTheVarLabel();
            dataXYLabels[0] = explanVar;
            dataXYLabels[1] = "Probability of "; 

            bivContinDataObj = new BivariateContinDataObj(dm, data);

            xStrings = bivContinDataObj.getLegalXsAs_AL_OfStrings();
            yStrings = bivContinDataObj.getLegalYsAs_AL_OfStrings();

            nPoints = xStrings.size();
            X = new Matrix(nPoints, 1);
            Y = new Matrix(nPoints, 1);
            
            for (int ithPoint = 0; ithPoint < nPoints; ithPoint++) {
                X.set(ithPoint, 0, Double.parseDouble(xStrings.get(ithPoint)));
                Y.set(ithPoint, 0, Double.parseDouble(yStrings.get(ithPoint)));
            }
            
            sortOriginalStrings();
            returnStatus = doObservedCounts();
            
            if (!returnStatus.equals("OK")) { return returnStatus; }

            logisticReg_Model = new LogisticReg_Model(this);
            logisticReg_Model.doAllThatMathStuff();

            logRegDashboard = new Logistic_Dashboard(this, logisticReg_Model);
            logRegDashboard.populateTheBackGround();
            logRegDashboard.putEmAllUp();
            logRegDashboard.showAndWait();
            returnStatus = logRegDashboard.getReturnStatus();
            returnStatus = logistic_Dialog.getReturnStatus();

            return returnStatus;
        }
        catch (Exception ex) {
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "Logistic Procedure");
        }
        return returnStatus;
    }
    
    private boolean columnIsZeroOne() {
        return false;
    }
    
    // Use system sort here?   Check it out.
    private void sortOriginalStrings() {           
        for (int i = 0; i < nPoints - 1; i++) {            
            for (int j = i + 1; j < nPoints; j++) {
                String tempXI = xStrings.get(i);
                String tempXJ = xStrings.get(j);
                
                if (tempXI.compareTo(tempXJ) > 0) { 
                    
                    String strTempXVal = xStrings.get(i);
                    String strTempYVal = yStrings.get(i);
                    
                    xStrings.set(i, xStrings.get(j));
                    yStrings.set(i, yStrings.get(j));
                    
                    xStrings.set(j, strTempXVal);
                    yStrings.set(j, strTempYVal);                    
                }
            }
        }
    }
    
    private String doObservedCounts() {
        Map<String, Integer> mapOfStrings = new HashMap<String, Integer>();
        
        for (int c = 0; c < nPoints; c++) {            
            if (mapOfStrings.containsKey(xStrings.get(c))) {
                int value = mapOfStrings.get(xStrings.get(c));
                mapOfStrings.put(xStrings.get(c), value + 1);
            } else { mapOfStrings.put(xStrings.get(c), 1); }
        }
        
        nGroups = mapOfStrings.size();
        
        if (nGroups < 3) {
            MyAlerts.showTooFewLogisticRegDFAlert();  
            return "Cancel";
        }

        unique_Xs = new String[nGroups];
        nObservations = new int[nGroups];
        nSuccesses = new int[nGroups];

        Set<Map.Entry<String, Integer>> entrySet = mapOfStrings.entrySet();
        
        int index = 0;
        
        for (Map.Entry<String, Integer> entry: entrySet) {
            unique_Xs[index] = entry.getKey();
            nObservations[index] = entry.getValue();
            index++;
        }
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            nSuccesses[ithGroup] = 0;            
            for (int jthPoint = 0; jthPoint < nPoints; jthPoint++) {                
                if ((xStrings.get(jthPoint).equals(unique_Xs[ithGroup]))
                        && (Double.parseDouble(yStrings.get(jthPoint)) == 1.0)) {
                    nSuccesses[ithGroup]++;
                }
            }
        }
        
        totalNSuccesses = totalNObservations = 0;
        for (int ithUnique = 0; ithUnique < nGroups; ithUnique++) {            
            totalNSuccesses += nSuccesses[ithUnique];
            totalNObservations += nObservations[ithUnique];
        }
        return "OK";
    }
    
    public double[] getUniqueXValues() {
        double[] theXs = new double[nGroups];        
        for (int ithUnique = 0; ithUnique < nGroups; ithUnique++) {
            theXs[ithUnique] = Double.parseDouble(unique_Xs[ithUnique]);
        }
        return theXs;
    }
    
    public Matrix getMatrix_X() { return X; }
    public Matrix getMatrix_Y() { return Y; }
    public String getExplanVar() { return explanVar; }
    public String getResponseVar() { return respVar; }
    public String getRespVsExplSubtitle() { return respVsExplanVar; }
    public int getNUniqueXs() { return nGroups; }
    public String[] getUniqueXs() { return unique_Xs; }
    public int[] getNSuccesses() { return nSuccesses; }
    public int[] getNTotals() { return nObservations; }
    public int getTotalNSuccesses() { return totalNSuccesses; }
    public int getTotalNObservations() { return totalNObservations; }
    
    public String getFirstVarDescription() { return firstVarDescr; }
    
    public int getTotalNFailures() {
        int totalNFailures = totalNObservations - totalNSuccesses;
        return totalNFailures;
    }
    
    public Data_Manager getDataManager() { return dm; } 
    public String[] getUniques() { return strUniques; }
    public QuantitativeDataVariable getQdvXVariable() { return qdv_XVariable; }
}
