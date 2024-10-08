/************************************************************
 *                       MLR_Controller                     *
 *                          10/15/23                        *
 *                            15:00                         *
 ***********************************************************/
/******************************************************************
 *   It is not obvious in the code what is happening here.  This   *
 *   procedure transforms the raw data into summary form for the   *
 *   Logistic_Model as per page 426 in:                            *
 *          Montgomery, D. C., Peck, E. A, & Vining,G. G.          *
 *          Introduction to Linear Regression Analysis (4th)       *
 *          Wiley Series in Probability and Statistics             *
 * ****************************************************************/

package multipleLogisticRegression;

import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import dialogs.regression.MLR_Dialog;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import matrixProcedures.Matrix;
import splat.Data_Manager;
import utilityClasses.*;

public class MLR_Controller {
    // POJOs
    private int nPoints, nGroups, totalNObservations, totalNSuccesses;    
    private int[] /*nFailures, nTotal,*/ nObservations, nSuccesses;
    //private ArrayList<Integer> countUniques;
    
    //private double[] dbl_xValue, props_OF_Xs, logits;
    //private double[][] rawData, initialUniques;    
    private String returnStatus, explanVar, respVar, respVsExplanVar;
    private String[] dataXYLabels, unique_Xs;
    
    //String waldoFile = "MLR_Controller";
    String waldoFile = "";
    
    ArrayList<String> xStrings, yStrings;

    // My classes
    BivariateContinDataObj bivContinDataObj;
    //private Logistic_View logRegView;
    private MLR_Model logRegModel;
    private MLR_Dashboard logRegDashboard;
    Matrix X, Y;
    QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    Data_Manager dm;
    
    public MLR_Controller(Data_Manager dm) {
        this.dm = dm;  
        dm.whereIsWaldo(56, waldoFile, "Constructing");
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(60, waldoFile, "doTheProcedure()");
        try {
            MLR_Dialog mlr_Dialog = new MLR_Dialog(dm);
            mlr_Dialog.showAndWait();
            returnStatus = mlr_Dialog.getReturnStatus();
            System.out.println("60 MLR Controller, returnStatus = " + returnStatus);
            if (!returnStatus.equals("OK")) {
                return returnStatus;
            }
            
            respVsExplanVar = mlr_Dialog.getSubTitle();
            ArrayList<ColumnOfData> data = mlr_Dialog.getData();
            qdv_XVariable = new QuantitativeDataVariable("mlr-Contr72", "mlr-Contr72", data.get(0));
            qdv_YVariable = new QuantitativeDataVariable("mlr-Contr72", "mlr-Contr72", data.get(1)); 

            // Transfer to previously coded variables
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
            doObservedCounts();

            logRegModel = new MLR_Model(this);
            logRegModel.doAllThatMathStuff();

            logRegDashboard = new MLR_Dashboard(this, logRegModel);
            logRegDashboard.populateTheBackGround();
            logRegDashboard.putEmAllUp();
            logRegDashboard.showAndWait();
            returnStatus = logRegDashboard.getReturnStatus();
            returnStatus = mlr_Dialog.getReturnStatus();

            return returnStatus;
        }
        catch (Exception ex) {
            PrintExceptionInfo pei = new PrintExceptionInfo( ex, "Logistic Procedure");
        }
        return returnStatus;
    }
    
    // Use system sort here?   Check it out.
    private void sortOriginalStrings() {   
        dm.whereIsWaldo(118, waldoFile, "sortOriginalStrings()");
        
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
    
    private void doObservedCounts() {
        dm.whereIsWaldo(140, waldoFile, "doObservedCounts()");
        Map<String, Integer> mapOfStrings = new HashMap<String, Integer>();
        
        for (int c = 0; c < nPoints; c++) {
            
            if (mapOfStrings.containsKey(xStrings.get(c))) {
                int value = mapOfStrings.get(xStrings.get(c));
                mapOfStrings.put(xStrings.get(c), value + 1);
            } else {
                mapOfStrings.put(xStrings.get(c), 1);
            }
        }
        
        nGroups = mapOfStrings.size();

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
    
    public int getTotalNFailures() {
        int totalNFailures = totalNObservations - totalNSuccesses;
        return totalNFailures;
    }
    
    //public QuantitativeDataVariable getQdvXVariable() { return qdv_XVariable; }
    
    public Data_Manager getDataManager() { return dm; }
}
