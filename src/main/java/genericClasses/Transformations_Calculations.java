/************************************************************
 *                  Transformations_Calculations            *
 *                          12/09/23                        *
 *                            12:00                         *
 ***********************************************************/
/************************************************************
 *  Note: Not all calculations below take string arrays.    *
 *        Will add these as sloth diminishes.  New methods  *
 *        should be able to mimic unaryOpsOfVars            *
 *        Current functions that take string arrays:        *
 *           -- unaryOpsOfVars                              *  
 ***********************************************************/
package genericClasses;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import utilityClasses.MyAlerts;

public class Transformations_Calculations {
    // POJOs
    int nOriginalDataPoints;
    int nLegalDoubles;
    double tempDouble;
    double[] ns;
    double[] dblLegalCases;
    double[] dblSortedLegalCases;
    ArrayList<Double> alDouble_AllTheData;
    final String missingData;
    String[] strTransformedData;
    ArrayList<String> tempAlString, theLegalData;
    
    // My classes
    NormalScores normScores;
    QuantitativeDataVariable qdv;
    
   public Transformations_Calculations() { 
       //System.out.println("41 TramsCalc, constructing");
        missingData = "*";
   }
   
   public String[] linearTransformation(ArrayList<String> var_1_Data, double alphaValue, double betaValue) {
       //System.out.println("46 TramsCalc, constructing, linearTransformation");
        nOriginalDataPoints =  var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint = var_1_Data.get(dataPoint);
            
            if (strDataPoint.equals(missingData)) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint = Double.valueOf(strDataPoint);
                double reScaled = alphaValue + betaValue * dblDataPoint;
                strTransformedData[dataPoint] = String.valueOf(reScaled );
            }   
        }
        return strTransformedData; 
   }
   
    public String[] linTransWithFunc(ArrayList<String> alStr_Var_1_Data, 
                                     String chosenProcedure,
                                     double alphaValue, double betaValue) {
        //System.out.println("67 TramsCalc, constructing, linTransWithFunc");
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint = alStr_Var_1_Data.get(dataPoint);
            
            if (strDataPoint.equals(missingData)) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint = Double.valueOf(strDataPoint);
                if(chosenProcedure.equals("ln") && (dblDataPoint > 0.0))  {
                    tempDouble = alphaValue + betaValue * Math.log(dblDataPoint);
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(chosenProcedure.equals("log10") && (dblDataPoint > 0.0))  {
                    tempDouble = alphaValue + betaValue * Math.log10(dblDataPoint);                
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(chosenProcedure.equals("sqrt") && (dblDataPoint >= 0.0))  {
                    tempDouble = alphaValue + betaValue * Math.sqrt(dblDataPoint);                
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(chosenProcedure.equals("recip") && (dblDataPoint != 0.0))  {
                        tempDouble = alphaValue + betaValue / dblDataPoint;                
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }   
                else if(chosenProcedure.equals("10^x")) {
                        tempDouble = alphaValue + betaValue * Math.pow(10.0, dblDataPoint);
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }    
                else if(chosenProcedure.equals("e^x"))  {
                        tempDouble = alphaValue + betaValue * Math.exp(dblDataPoint);
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else 
                    strTransformedData[dataPoint] = "*";                
            }             
        }
        return strTransformedData;
    }
    
    public String[] unaryOpsOfVars(double[]  double_1_Data,
                                   String chosenProcedure)    {
        //System.out.println("111 TramsCalc, constructing, linTransWithFunc");
        tempAlString = convertArrayOfDouble_To_alStrArrayList(double_1_Data);
        return unaryOpsOfVars(tempAlString, chosenProcedure);   
    } 
    
   public String[] unaryOpsOfVars(String[]  strVar_1_Data,
                                   String chosenProcedure)    {
       //System.out.println("118 TramsCalc, constructing, unaryOpsOfVars");
       tempAlString = convertStrArray_To_alStrArrayList(strVar_1_Data);
       return unaryOpsOfVars(tempAlString, chosenProcedure);   
   } 

   public String[] unaryOpsOfVars(ArrayList<String>  alStr_Var_1_Data,
                                   String uOpProcedure) {
        //System.out.println("125 TramsCalc, constructing, unaryOpsOfVars");
        alStr_Var_1_Data.toString();
        String strDataPoint;
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        
        // First two parameters are dummy variables.  qdv wants a label and
        // a description of the variablel
        
        qdv = new QuantitativeDataVariable("xxx", "yyy", alStr_Var_1_Data);
        //System.out.println("135 TransCalc, uOpProcedure = " + uOpProcedure);
        
        switch (uOpProcedure) {
            case "percentile rank":
                //System.out.println("139 TramsCalc, case percentile");
                
                for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {    
                    strDataPoint = alStr_Var_1_Data.get(dataPoint);       
                    if (strDataPoint.equals(missingData)) {
                        strTransformedData[dataPoint] = missingData;
                    } else { 
                        Double dblDataPoint = Double.valueOf(strDataPoint);
                        strTransformedData[dataPoint] = String.valueOf(qdv.getIthPercentileRank(dblDataPoint));
                    }                   
                }
                qdv.setTheVarLabel("Percentile rank");
                qdv.setTheVarDescription("Percentile rank");
                break;
                
            case "z-score":
                //System.out.println("155 TramsCalc, case z-score");
                double mean, stDev, zScore;
                mean = qdv.getTheMean();
                stDev = qdv.getTheStandDev();
                
                for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
                   strDataPoint = alStr_Var_1_Data.get(dataPoint);
                   
                    if (strDataPoint.equals(missingData)) {
                        strTransformedData[dataPoint] = missingData;
                    } else {
                    Double dblDataPoint = Double.valueOf(strDataPoint);
                    tempDouble = dblDataPoint;   //  Unbox
                }
                    zScore = (tempDouble - mean) / stDev;
                    strTransformedData[dataPoint] = String.valueOf(zScore);
                }
                qdv.setTheVarLabel("z-score");
                qdv.setTheVarDescription("z-score");
                break;
                
            case "rank":
                //System.out.println("177 TransCalc, case rank");
                boolean endOfStory;
                int startOfTie, endOfTie;
                Double rank, daRank;
                // var1_Data is an arrayList of Strings;
                //sizeofColumn = alStr_Var_1_Data.size();
                nLegalDoubles = qdv.get_nDataPointsLegal();

                alDouble_AllTheData = new ArrayList<>();
                alDouble_AllTheData = qdv.getLegalCases_AsALDoubles();
                theLegalData = qdv.getLegalCases_AsALStrings();
                Collections.sort(alDouble_AllTheData);
                Map<String, Double> mapStringsToDoubles = new HashMap<>();
                Map<Double, Double> mapDoublesToRanks = new HashMap<>();
                
                for (int ithOrigData = 0; ithOrigData < nLegalDoubles; ithOrigData++ ) {
                    mapStringsToDoubles.put(theLegalData.get(ithOrigData), alDouble_AllTheData.get(ithOrigData));
                }   
                
                startOfTie = 0;    //  Start process at first number;
                endOfTie = 0;      // subscript is as in ArrayList
                endOfStory = false;

                do {
                    for (int askIfTie = startOfTie; askIfTie < nLegalDoubles; askIfTie++) {
                        if (alDouble_AllTheData.get(askIfTie) <= alDouble_AllTheData.get(startOfTie)) {
                            endOfTie = askIfTie;
                        }
                    }

                    rank = (startOfTie + 1 + endOfTie + 1) / 2.0;
                    
                    for (int daTies = startOfTie; daTies <= endOfTie; daTies++) {
                        mapDoublesToRanks.put(alDouble_AllTheData.get(daTies), rank);
                    }
                    
                    startOfTie = endOfTie + 1;
                    endOfTie = startOfTie;
                    if (endOfTie == nLegalDoubles)
                        endOfStory = true;      
                }   while (endOfStory == false);
                
            for (int ithRanked = 0; ithRanked < nOriginalDataPoints; ithRanked ++) {
                String tempString = qdv.getIthDataPtAsString(ithRanked);
                
                if (mapStringsToDoubles.containsKey(tempString)) {
                    tempDouble = Double.parseDouble(tempString);
                    daRank = mapDoublesToRanks.get(tempDouble);
                    String daString = String.valueOf(daRank);
                    strTransformedData[ithRanked] = String.valueOf(daString);
                } else {
                strTransformedData[ithRanked] = "*";
                }
            }
            qdv.setTheVarLabel("rank");
            qdv.setTheVarDescription("rank");
                break;
                
            case "rankits":    //  For normal probability plot; 
                               //  Using rankits (used in qqnorm in R)
                // System.out.println("240 T_C, doing Rankits");
                // ?????????  Can this be simplified  ???????????????
                //System.out.println("244 TransCalc, case rankits");
                theLegalData = new ArrayList<>();
                theLegalData = qdv.getLegalCases_AsALStrings();
                nLegalDoubles = qdv.get_nDataPointsLegal();
                dblLegalCases = new double[nLegalDoubles];
                dblSortedLegalCases = new double[nLegalDoubles];
                ns = new double[nLegalDoubles];
                
                for (int ith = 0; ith < nLegalDoubles; ith++) {
                    dblLegalCases[ith] = Double.parseDouble(theLegalData.get(ith));
                }
                
                System.arraycopy(dblLegalCases, 0, dblSortedLegalCases, 0, dblLegalCases.length);
                Arrays.sort(dblSortedLegalCases);                
                normScores = new NormalScores();
                ns = normScores.getNormalScores(nLegalDoubles);
                
                for (int ithOriginalPoint = 0; ithOriginalPoint < nOriginalDataPoints; ithOriginalPoint ++) {
                    String tempString = qdv.getIthDataPtAsString(ithOriginalPoint);
                    
                    if (tempString.equals(missingData)) {
                        strTransformedData[ithOriginalPoint] = "*";
                    } else {
                        tempDouble = qdv.getIthDataPtAsDouble(ithOriginalPoint);
                        
                        for (int jth = 0; jth < nLegalDoubles; jth++) {        
                            if (tempDouble == dblSortedLegalCases[jth]) {
                                strTransformedData[ithOriginalPoint] = String.valueOf(ns[jth]);
                                break;
                            }
                        }
                    }
                } 
                qdv.setTheVarLabel("rankits");
                qdv.setTheVarDescription("rankits");
                break;                
                
            default:
                String switchFailure = "Switch failure: Transformations_Calculations 277 " + uOpProcedure;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        return strTransformedData; 
   }
    
   public String[] binaryOpsOfVars(ArrayList<String>  alStr_Var_1_Data,
                                   String binaryOperation,
                                   ArrayList<String>  alStr_Var_2_Data) {
        //System.out.println("286 TramsCalc, constructing, binaryOpsOfVars");
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint_1 = alStr_Var_1_Data.get(dataPoint);
            String strDataPoint_2 = alStr_Var_2_Data.get(dataPoint);
            
            if (strDataPoint_1.equals(missingData)  
                    || (strDataPoint_2.equals(missingData))) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint_1 = Double.valueOf(strDataPoint_1);
                Double dblDataPoint_2 = Double.valueOf(strDataPoint_2);
                if(binaryOperation.equals("+"))  {
                    tempDouble = dblDataPoint_1 + dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(binaryOperation.equals("-"))  {
                    tempDouble = dblDataPoint_1 - dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(binaryOperation.equals("*"))  {
                    tempDouble = dblDataPoint_1 * dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if ((binaryOperation.equals("/") && (dblDataPoint_2 != 0.0)))  {
                    tempDouble = dblDataPoint_1 / dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else
                    strTransformedData[dataPoint] = "*";
            }   
        }
        return strTransformedData; 
   }
   
   public String[] linearCombinationOfVars(ArrayList<String>  alStr_Var_1_Data, 
                                     ArrayList<String>  alStr_Var_2_Data, 
                                     double alphaValue, 
                                     double betaValue) {
        //System.out.println("327 TramsCalc, constructing, binaryOpsOfVars");
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint_1 = alStr_Var_1_Data.get(dataPoint);
            String strDataPoint_2 = alStr_Var_2_Data.get(dataPoint);
            
            if (strDataPoint_1.equals(missingData)  
                    || (strDataPoint_2.equals(missingData))) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint_1 = Double.valueOf(strDataPoint_1);
                Double dblDataPoint_2 = Double.valueOf(strDataPoint_2);
                double reScaled = alphaValue * dblDataPoint_1 
                                    + betaValue * dblDataPoint_2;
                strTransformedData[dataPoint] = String.valueOf(reScaled );
            }   
        }
        return strTransformedData; 
   }
   
   private ArrayList<String> convertArrayOfDouble_To_alStrArrayList(double[] arrayOfDoubles) {
        //System.out.println("350 TramsCalc, constructing, convertArrayOfDouble_To_alStrArrayList");
        ArrayList<String> alOfStrs = new ArrayList<>();
        
        for (int ith = 0; ith < arrayOfDoubles.length; ith++) {
            alOfStrs.add(String.valueOf(arrayOfDoubles[ith]));
        }
        return alOfStrs;
   }
   
   private ArrayList<String> convertStrArray_To_alStrArrayList(String[] arrayOfStrings) {
        //System.out.println("360 TramsCalc, constructing, convertArrayOfDouble_To_alStrArrayList");
        ArrayList<String> alOfStrs = new ArrayList<>();
        alOfStrs.addAll(Arrays.asList(arrayOfStrings));
        return alOfStrs;
   }   
}
