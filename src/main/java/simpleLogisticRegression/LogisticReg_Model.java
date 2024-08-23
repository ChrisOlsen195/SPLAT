/**************************************************
 *               LogisticReg_Model                *
 *                    11/14/23                    *
 *                     12:00                      *
 *************************************************/
package simpleLogisticRegression;

import genericClasses.Point_2D;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import matrixProcedures.Matrix;
import probabilityDistributions.*;
import splat.Data_Manager;
import utilityClasses.TableFormatter;
import utilityClasses.*;

/***************************************************************************
* The "fleiss_" variable names are from Fleiss, J.L., et al. (2003).       *
* Statistical Methods for Rates and Proportions (3rd). Wiley Inter-        *
* science.                                                                 *
*                                                                          *
*The Newton-Raphson Iterative Reweighted Least Squares algorithm is from   *
* Hardin, J. W., & Hilbe, J. M.  (2018).  Generalized Linear Models and    *
* Extensions (4th). Note: An error appears in their Listing 9.1, which     *
* has been silently corrected here.                                        *     
*                                                                          *
* The "hh" variable names are from there also.                             *
***************************************************************************/

public class LogisticReg_Model {         
    int nGroups, df, totalNSuccesses, totalNObservations,
        totalNFailures;
    
    final int ONE = 1;
    final int NVARPLUS1 = 2;

    int[] successes_IthGroup, failures_IthGroup, totals_IthGroup, sortKey;
    
    double zBeta0, zBeta1, oddsRatio, ciLower, ciUpper, pValuePearson,
           ssError, pearsonChiSquare, beta0, beta1, seBeta0, 
           seBeta1, covBetas, deviance,
    
           llrm_1, llrm_2, llrm_3, lrts_1, lrts_2, logLikeReduced, 
           logLikeFull, G_Statistic, LRTS_PValue, ithXValue;
    
    double [] estPi, fleissPHatX, fleissWi, originalProps, 
              logitProps, expectedSuccesses, theXValues,
              expectedProps, dbl_nTotals, sortedXValues; 
       
    String respVsExplanVar, xAxisLabel, format_01, format_02, format_03,
           firstVarDescr;
    String[] unique_Xs, grpResponseTable, strUniques;
    String[] logisticEquation;
    
    // Make empty if no-print
    //String waldoFile = "LogisticReg_Model";
    String waldoFile = "";
    
    // My classes
    StandardNormal standNorm;
    ChiSquareDistribution chiSqDist_PandD, chiSqLRTS;
    Data_Manager dm;
    
    Matrix hh_XTranspose, hh_XTrans_W_X, hh_XTrans_W_X_Inverse, hh_XTrans_W_Z,
           hh_Eta, hh_Z, mat_X, mat_XTranspose, mat_Beta,
           mat_OrigProps, mat_LogitProps, devianceResiduals, pearsonResiduals, 
           standPearsonResiduals, estProbs, hh_W, mat_V;    
    Matrix V_oneHalf, V_oneHalf_X, XTrans_V_X, XTrans_V_X_Inverse,
           XTrans_V_oneHalf, hatMatrix;
    
    QuantitativeDataVariable qdv_DevResids;     
    ArrayList<String> logisticReport, logisticDiagnostics; 
    Logistic_Controller logReg_Controller;    
    TableFormatter tf;

    public LogisticReg_Model(Logistic_Controller logReg_Controller){
        this.logReg_Controller = logReg_Controller;
        dm = logReg_Controller.getDataManager();
        dm.whereIsWaldo(77, waldoFile, "Constructing");
        nGroups = logReg_Controller.getNUniqueXs();
        originalProps = new double[nGroups];
        logitProps = new double[nGroups];
        expectedProps = new double[nGroups];
        expectedSuccesses = new double[nGroups];
        estProbs = new Matrix(nGroups, 1);
        estPi = new double [nGroups];
        sortKey = new int [nGroups];

        fleissPHatX = new double[nGroups];
        fleissWi = new double [nGroups];

        devianceResiduals = new Matrix(nGroups, ONE);
        pearsonResiduals = new Matrix(nGroups, ONE);
        standPearsonResiduals = new Matrix(nGroups, ONE);
        theXValues = new double[nGroups];
        successes_IthGroup = new int[nGroups];
        failures_IthGroup = new int[nGroups];
        dbl_nTotals = new double[nGroups];
        sortedXValues = new double[nGroups];
        respVsExplanVar = logReg_Controller.getRespVsExplSubtitle();
        unique_Xs = new String[nGroups]; 
        totals_IthGroup = new int[nGroups];
        logisticReport = new ArrayList<>();
        logisticDiagnostics = new ArrayList<>();
        strUniques = logReg_Controller.getUniques();
        
        mat_OrigProps = new Matrix (nGroups, ONE);
        mat_LogitProps = new Matrix (nGroups, ONE);
        mat_Beta = new Matrix(NVARPLUS1, ONE);
        mat_X = new Matrix (nGroups, NVARPLUS1);
        mat_XTranspose = new Matrix (NVARPLUS1, nGroups);
        mat_V = Matrix.identity(nGroups, nGroups);
        firstVarDescr = logReg_Controller.getFirstVarDescription();
        tf = new TableFormatter();
    }
        
    public void doAllThatMathStuff() {        
        doMoreIntitializations();        
        mat_Beta = mat_X.solve(mat_OrigProps);
        beta0 = mat_Beta.get(0,0);
        beta1 = mat_Beta.get(1,0);
                    
        iterationCycle();
        
        ssError = 0.0;        
        for (int i = 0; i < nGroups; i++) {
           ssError += (originalProps[i] - estPi[i]) * (originalProps[i] - estPi[i]);
        }

        beta0 = mat_Beta.get(0,0);
        beta1 = mat_Beta.get(1,0);   
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            double temp = beta0 + beta1 * mat_X.get(ithGroup, 1);
            estPi[ithGroup] = Math.exp(temp)/ (1.0 + Math.exp(temp));
        }
        
        double fleiss_SumWiXi = 0.0;
        double fleiss_SumWi = 0.0;
        
        for (int ithGroup = 0;ithGroup < nGroups; ithGroup++) {
            double x_ithGroup = theXValues[ithGroup];
            double temp1 = Math.exp(beta0 + beta1 * x_ithGroup);
            fleissPHatX[ithGroup] = temp1 / (1.0 + temp1);          
            // Weighted least squares -- binomial variance.  (Not obvious in Fleiss)
            fleissWi[ithGroup] = dbl_nTotals[ithGroup] * fleissPHatX[ithGroup] * (1.0 - fleissPHatX[ithGroup]);
            fleiss_SumWiXi += fleissWi[ithGroup] * x_ithGroup;
            fleiss_SumWi += fleissWi[ithGroup];
        }

        double fleiss_xBarW = fleiss_SumWiXi / fleiss_SumWi;
        double fleiss_SSw = 0.0;

        for (int ith_Fleiss = 0;ith_Fleiss < nGroups; ith_Fleiss++) {
           double tempX = theXValues[ith_Fleiss];
           fleiss_SSw += (fleissWi[ith_Fleiss] * (tempX - fleiss_xBarW) * (tempX - fleiss_xBarW));
        }

        seBeta0 = Math.sqrt(1.0 / fleiss_SumWi + fleiss_xBarW * fleiss_xBarW / fleiss_SSw);
        seBeta1 = 1.0 / Math.sqrt(fleiss_SSw);
        covBetas = - fleiss_xBarW / fleiss_SSw;

        zBeta0 = beta0 / seBeta0;
        zBeta1 = beta1 / seBeta1;

        oddsRatio = Math.exp(beta1);
        ciLower = Math.exp(beta1 - 1.96 * seBeta1);
        ciUpper = Math.exp(beta1 + 1.96 * seBeta1);
      
        nGroups = logReg_Controller.getNUniqueXs();
      
        pearsonChiSquare = 0;
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            expectedProps[ithGroup] = 1.0 / (1.0 + Math.exp(-(beta0 + beta1 * theXValues[ithGroup])));
            expectedSuccesses[ithGroup] = dbl_nTotals[ithGroup] * expectedProps[ithGroup];
            double x2Num = successes_IthGroup[ithGroup] - dbl_nTotals[ithGroup] * expectedProps[ithGroup];
            double x2Den = dbl_nTotals[ithGroup] * expectedProps[ithGroup] * (1.0 - expectedProps[ithGroup]);
            pearsonChiSquare = pearsonChiSquare + x2Num * x2Num / x2Den; 
        }
       
        //   ***************       MPV, Chapter 13         ***************       
        //   ***************   Log liklihood, full model   ***************
        logLikeFull = 0.0;
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            int xSucc, xFail;
            double xPi;
            xSucc = successes_IthGroup[ithGroup];
            xFail = failures_IthGroup[ithGroup];
            xPi = expectedProps[ithGroup];
            logLikeFull += xSucc * Math.log(xPi) + xFail * Math.log(1.0 - xPi);
        }
        
        //   *************** Log liklihood, reduced model *************** 
        llrm_1 = (double)totalNSuccesses * Math.log((double)totalNSuccesses);
        llrm_2 = (double)totalNFailures * Math.log((double)totalNFailures);
        llrm_3 = (double)totalNObservations * Math.log((double)totalNObservations);
        logLikeReduced = llrm_1 + llrm_2 - llrm_3;
        
        //   ************  Likelihood ratio test statistic  *************
        lrts_1 = lrts_2 = 0.0;
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            lrts_1 += successes_IthGroup[ithGroup] * Math.log(expectedProps[ithGroup]);
            lrts_2 += failures_IthGroup[ithGroup] * Math.log(1.0 - expectedProps[ithGroup]);   
        }      
        
        G_Statistic  = 2.0 * (lrts_1 + lrts_2 - logLikeReduced);
        chiSqLRTS = new ChiSquareDistribution(1);
       
        LRTS_PValue = chiSqLRTS.getRightTailArea(G_Statistic);

        calculateDeviance();        
        calculateResiduals(); 
        printStatistics(); 
        printDiagnostics();     
   }  // end doAllThatMathStuff
    
   private void doMoreIntitializations() {
        unique_Xs = logReg_Controller.getUniqueXs();
        successes_IthGroup = logReg_Controller.getNSuccesses();
        totals_IthGroup = logReg_Controller.getNTotals();

        totalNSuccesses = logReg_Controller.getTotalNSuccesses();
        totalNObservations = logReg_Controller.getTotalNObservations();
        totalNFailures = logReg_Controller.getTotalNFailures();
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            dbl_nTotals[ithGroup] = totals_IthGroup[ithGroup];
            theXValues[ithGroup] = Double.parseDouble(unique_Xs[ithGroup]);
            failures_IthGroup[ithGroup] = totals_IthGroup[ithGroup] - successes_IthGroup[ithGroup];
            
            /*******************************************************************
            *  See Hosmer, D. W., et al., p146.  Add + .5 to nSuccesses,       * 
            *                                        + .5 to nFailures         *
            *  separation problem, but is not the most satisfying solution.    *
            *  NOT IMPLEMENTED HERE                                            *
            *******************************************************************/
            if ((failures_IthGroup[ithGroup] == 0) || (successes_IthGroup[ithGroup] == 0)) {
                originalProps[ithGroup] = ((double)successes_IthGroup[ithGroup]/* + 0.5*/)/ ((double)totals_IthGroup[ithGroup]/* + 1.0*/);
            }            
            else {
                originalProps[ithGroup] = (double)successes_IthGroup[ithGroup] / (double)totals_IthGroup[ithGroup];
            }

            double temp_OP = originalProps[ithGroup]; 
            logitProps[ithGroup] = Math.log(temp_OP / (1.0 - temp_OP)); 
        }

        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            mat_X.set(ithGroup, 0, 1.0);
            mat_X.set(ithGroup, 1, theXValues[ithGroup]);
            mat_OrigProps.set(ithGroup, 0, originalProps[ithGroup]);
            mat_LogitProps.set(ithGroup, 0, logitProps[ithGroup]);
        }       
        mat_XTranspose = mat_X.transpose();
   }
    
   private void iterationCycle() {
        double tolerance = .00001;
        double hh_Dev = 0.0;
        double hh_Old_Dev;
        double hh_Delta_Dev;
        double[] hh_Mu = new double[nGroups];
        double[] hh_k = new double[nGroups];
        double[] hh_y = new double [nGroups];

        hh_W = new Matrix(nGroups, nGroups);
        hh_W = Matrix.identity(nGroups, nGroups);
        hh_XTranspose = new Matrix(2, nGroups);   
        hh_XTrans_W_X = new Matrix(nGroups, nGroups);
        hh_XTrans_W_X_Inverse = new Matrix(nGroups, nGroups);
        hh_XTrans_W_Z = new Matrix(nGroups, nGroups);
        hh_Z = new Matrix(nGroups, 1);
        hh_Eta = new Matrix(nGroups, 1);
        
       for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            hh_y[ithGroup] = successes_IthGroup[ithGroup];
            hh_k[ithGroup] = totals_IthGroup[ithGroup];
            hh_Mu[ithGroup] = hh_k[ithGroup] * (hh_y[ithGroup] + 0.5) / (hh_k[ithGroup] + 1.0);
            hh_Eta.set(ithGroup, 0, Math.log(hh_Mu[ithGroup] / (hh_k[ithGroup] - hh_Mu[ithGroup])));
       }
        
        do { 
            for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
                double hh_kk = hh_k[ithGroup]; 
                double hh_yy = hh_y[ithGroup]; 
                double hh_mu = hh_Mu[ithGroup]; 
                double temp_hh_1 = hh_mu * (1.0  - hh_mu / hh_kk);
                hh_W.set(ithGroup, ithGroup, temp_hh_1);
                double temp_hh_2 = hh_yy  - hh_mu;
                double temp_hh_3 = hh_mu * (1.0 - hh_mu / hh_kk);
                hh_Z.set(ithGroup, 0, hh_Eta.get(ithGroup, 0) + temp_hh_2 / temp_hh_3);
            }  

            hh_XTranspose = mat_X.transpose();                      //
            hh_XTrans_W_X = hh_XTranspose.times(hh_W).times(mat_X); //  Calculate Beta
            hh_XTrans_W_X_Inverse = hh_XTrans_W_X.inverse();        //  and Eta
            hh_XTrans_W_Z = hh_XTranspose.times(hh_W).times(hh_Z);  //           
            mat_Beta = hh_XTrans_W_X_Inverse.times(hh_XTrans_W_Z);  //
            hh_Eta = mat_X.times(mat_Beta);                         //   
            
            for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
                hh_Mu[ithGroup] = hh_k[ithGroup] / (1.0 + Math.exp(-hh_Eta.get(ithGroup, 0)));
            }

            hh_Old_Dev = hh_Dev;
            
            double devSum = 0.0;
            
            for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {                
                if (hh_y[ithGroup] == 0.0) {
                    double temp_hh_4 = hh_k[ithGroup] / (hh_k[ithGroup] - hh_Mu[ithGroup]);
                    devSum += 2.0 * hh_k[ithGroup] * Math.log(temp_hh_4);
                }
                else if (hh_y[ithGroup] == hh_k[ithGroup]) {
                   devSum += 2.0 * hh_k[ithGroup] * Math.log(hh_k[ithGroup] / hh_Mu[ithGroup]); 
                }
                else {
                   double temp_hh_5 = 2.0 * hh_y[ithGroup] * Math.log(hh_y[ithGroup] / hh_Mu[ithGroup]);
                   double temp_hh_6 = (hh_k[ithGroup] - hh_y[ithGroup]) / (hh_k[ithGroup] - hh_Mu[ithGroup]); 
                   double temp_hh_7 = 2.0 * (hh_k[ithGroup] - hh_y[ithGroup]) * Math.log(temp_hh_6);
                   devSum += (temp_hh_5 + temp_hh_7);
                }
            }
            hh_Dev = 2 * devSum;
            hh_Delta_Dev = Math.abs(hh_Dev - hh_Old_Dev);
       } while (hh_Delta_Dev > tolerance);
   }
   
   private void calculateDeviance() {
        //  ******************    Deviance   ***************************
        //  ******************    MPV, p432  ***************************
        double tempLeft, tempRight;        
        double preDevianceSigma = 0.0;
        
        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            double piHat = estPi[ithGroup];
            double nSucc = successes_IthGroup[ithGroup];
            double nObs = totals_IthGroup[ithGroup];
            double nFail = failures_IthGroup[ithGroup];
            
            if (nSucc == 0) { tempLeft = 0; }
            else { tempLeft = nSucc * Math.log(nSucc / (nObs * piHat)); }
            
            if (nSucc == nObs) { tempRight = 0; }
            else {
                tempRight = nFail * Math.log(nFail / (nObs * (1.0 - piHat)));
            }
            
            preDevianceSigma += (tempLeft + tempRight);
        }
        
        deviance = 2.0 * preDevianceSigma;  
   }

    private void calculateResiduals() {
        double ithResid, /*ithObsProb,*/ ithEstProb, ithDevianceResid, signOfResid, 
               ithPearsonResid, ySubi, nSubi, nSubiMinusySubi, piHatSubi,
                oneMinuspiHatSubi, nSubiypiHatSubi, nSubiyOneMinuspiHatSubi;
        
        devianceResiduals = new Matrix(nGroups, 1); 
        
        for (int ith = 0; ith < nGroups; ith++) {
            ySubi = (double)successes_IthGroup[ith];
            nSubiMinusySubi = (double)failures_IthGroup[ith];
            nSubi = dbl_nTotals[ith];
            piHatSubi = expectedProps[ith];
            oneMinuspiHatSubi = 1.0 - piHatSubi;
            nSubiypiHatSubi = nSubi * piHatSubi;
            nSubiyOneMinuspiHatSubi = nSubi * (1.0 - piHatSubi);
            ithEstProb = piHatSubi;
            estProbs.set(ith, 0, ithEstProb);
            ithResid = ySubi - nSubi * piHatSubi;
            signOfResid = Math.signum(ithResid);
            double term1 = ySubi * Math.log(ySubi / (nSubiypiHatSubi));
            double term2 = nSubiMinusySubi * Math.log(nSubiMinusySubi / nSubiyOneMinuspiHatSubi);
            double preSqr = 2.0 * (term1 + term2); 
            
            if (ySubi == 0.) {
                ithDevianceResid = -Math.sqrt(-2.0 * nSubi * Math.log(oneMinuspiHatSubi));
            } else 
                if (ySubi == nSubi) {
                ithDevianceResid = Math.sqrt(-2.0 * nSubi * Math.log(piHatSubi));
            }
            else {
                ithDevianceResid = signOfResid * Math.sqrt(preSqr);
            }
            
            devianceResiduals.set(ith, 0, ithDevianceResid);
            ithPearsonResid = ithResid / Math.sqrt(nSubi * piHatSubi * oneMinuspiHatSubi);
            pearsonResiduals.set(ith, 0, ithPearsonResid);
            mat_V.set(ith, ith, nSubi * piHatSubi * oneMinuspiHatSubi);
        }   
        
        doHatStuff();   // In order to get standardized Pearson Residuals
            
        for (int ith = 0; ith < nGroups; ith++) {
            nSubi = dbl_nTotals[ith];
            ySubi = (double)successes_IthGroup[ith];
            piHatSubi = expectedProps[ith];
            oneMinuspiHatSubi = 1.0 - piHatSubi;
            ithResid = ySubi - nSubi * piHatSubi;
            double denom_1= 1.0 - hatMatrix.get(ith, ith);
            double denom_2 = nSubi * piHatSubi * oneMinuspiHatSubi;  
            standPearsonResiduals.set(ith, 0, ithResid / Math.sqrt(denom_1 * denom_2));
        }
        
        qdv_DevResids = new QuantitativeDataVariable(dm, "Residuals", "Residuals", devianceResiduals);     
}
    
    private void doHatStuff() {
        V_oneHalf = Matrix.identity(nGroups, nGroups);
        
        for (int ithGrp = 0; ithGrp < nGroups; ithGrp++) {
            V_oneHalf.set(ithGrp, ithGrp, Math.sqrt(mat_V.get(ithGrp, ithGrp)));
        }
        
        V_oneHalf_X = V_oneHalf.times(mat_X);
        XTrans_V_X = new Matrix(NVARPLUS1, NVARPLUS1);
        XTrans_V_X = mat_XTranspose.times(mat_V).times(mat_X);
        XTrans_V_X_Inverse = new Matrix(nGroups, nGroups);
        XTrans_V_X_Inverse = XTrans_V_X.inverse();
        XTrans_V_oneHalf = new Matrix(nGroups, nGroups);
        XTrans_V_oneHalf = mat_XTranspose.times(V_oneHalf);
        hatMatrix = new Matrix(nGroups, nGroups);
        hatMatrix = V_oneHalf_X.times(XTrans_V_X_Inverse).times(XTrans_V_oneHalf);           
    }
    
    private void printStatistics() {
        sortXsLowToHigh();
        grpResponseTable = new String[5];
        //grpResponseTable[0] = " X values";
        grpResponseTable[0] = (StringUtilities.getleftMostNChars(firstVarDescr, 9)).trim();
        grpResponseTable[1] = "N Successes "; 
        grpResponseTable[2] = "exp Successes ";
        grpResponseTable[3] = "N values"; 
        grpResponseTable[4] = "Prop Success";   
        logisticReport = new ArrayList<>();
        addNBlankLinesToLogisticReport(2);
        
        /*
        String responseTableTitle = "***************     Response information     **********\n";
        logisticReport.add(String.format("             %25s \n", responseTableTitle));
        
        // If values of 0/1 are not 0/1
        if (!strUniques[0].equals("0") && !strUniques[0].equals("1")
              ||  !strUniques[1].equals("0") && !strUniques[1].equals("1")) {
            logisticReport.add(String.format("                             0  =  %1s\n", strUniques[0]));
            logisticReport.add(String.format("                             1  =  %1s\n", strUniques[1]));
            addNBlankLinesToLogisticReport(1);      
        }
        
        logisticReport.add(String.format("%15s     %12s  %15s    %12s   %15s\n", 
                                          grpResponseTable[0], grpResponseTable[1], grpResponseTable[2], 
                                          grpResponseTable[3], grpResponseTable[4]));
        addNBlankLinesToDiagnosticReport(2);

        Point_2D xValuesRange = new Point_2D();
        xValuesRange = tf.getMinAndMaxOfArray(theXValues);
        
        Point_2D expSuccessRange = new Point_2D();
        expSuccessRange = tf.getMinAndMaxOfArray(expectedSuccesses);
        
        Point_2D origPropRange = new Point_2D();
        origPropRange = tf.getMinAndMaxOfArray(expectedSuccesses);
        
        String formatSpec_01 = "X8"                               + "|";  // first spaces
        formatSpec_01 += tf.helpWithDoublesField(9, xValuesRange.getFirstValue(), xValuesRange.getSecondValue(), 3, 4) + "|";  //  theXValues
        formatSpec_01 += "X3|D8|X10"                              + "|";  //  spaces, successes, spaces
        formatSpec_01 += tf.helpWithDoublesField(7, expSuccessRange.getFirstValue(), expSuccessRange.getSecondValue(), 3, 4)  + "|";  //  expected successes
        formatSpec_01 += "X16|D3|X9"                             + "|";  //  spaces, observed, spaces
        formatSpec_01 += tf.helpWithDoublesField(5, origPropRange.getFirstValue(), origPropRange.getSecondValue(), 6, 9);  //  original props
        format_01 = tf.doTheFormatting(formatSpec_01);
        format_01 += "\n";

        for (int ithGroup = 0; ithGroup < nGroups; ithGroup++) {
            logisticReport.add(String.format(format_01, theXValues[sortKey[ithGroup]],  
                                                         successes_IthGroup[sortKey[ithGroup]], 
                                                         expectedSuccesses[sortKey[ithGroup]],
                                                         totals_IthGroup[sortKey[ithGroup]], 
                                                         originalProps[sortKey[ithGroup]]));
        }
        */
        
        // -------------------------------------------------------------------
            String explanLabel2Print = firstVarDescr;
            String tempRegrEq = String.format("%9.5f %3s %9.5f %15s",
                                 beta0, "+",
                                 beta1, explanLabel2Print
                              );

            String deBlankedRegEq = "(" + StringUtilities.eliminateMultipleBlanks(tempRegrEq) + ")";
            logisticEquation = new String[5];
            logisticEquation[0] = "                     " + deBlankedRegEq;
            logisticEquation[1] = "                   e";
            logisticEquation[2] = " P(Success) =  ------------------------------------------------";   
            logisticEquation[3] = "                      " + deBlankedRegEq;
            logisticEquation[4] = "                1 + e";
            
            String lrTable0 = "        **********     Logistic Regression Equation     **********\n\n";
            logisticReport.add(String.format("\n\n          %10s", lrTable0));
            
            logisticReport.add(String.format("%45s \n", logisticEquation[0]));
            logisticReport.add(String.format("%20s \n", logisticEquation[1]));
            logisticReport.add(String.format("%20s \n", logisticEquation[2]));
            logisticReport.add(String.format("%45s \n", logisticEquation[3]));
            logisticReport.add(String.format("%20s \n", logisticEquation[4]));

        /*********************************************************************
         *   The confidence intervals below are for the Odds Ratios, not the *
         *   coefficient of the explanatory variable.                        *
         ********************************************************************/
        String lrTable1 = "          **********     Logistic Regression Table     **********\n\n";
        logisticReport.add(String.format("\n\n          %10s", lrTable1));
        String lrTable2 = "            Odds      *** 95% CI ***";
        logisticReport.add(String.format("                                                         %15s\n", lrTable2));
        String lrTable3 = "Predictor     Coefficient    StandErr       Z       P-value    Ratio     Lower    Upper\n";
        logisticReport.add(String.format("      %30s      \n", lrTable3));

        double[] betasCol = new double[NVARPLUS1];
        betasCol[0] = beta0;
        betasCol[1] = beta1;
        
        Point_2D betasRange = new Point_2D();
        betasRange = tf.getMinAndMaxOfArray(betasCol);
        
        double[] seBetasCol = new double[NVARPLUS1];
        seBetasCol[0] = seBeta0;
        seBetasCol[1] = seBeta1;
        Point_2D seBetasRange = new Point_2D();
        seBetasRange = tf.getMinAndMaxOfArray(seBetasCol);
        
        double[] zBetasCol = new double[NVARPLUS1];
        zBetasCol[0] = zBeta0;
        zBetasCol[1] = zBeta1;
        //Point_2D zBetasRange = new Point_2D();
        betasRange = tf.getMinAndMaxOfArray(betasCol);
        
        String formatSpec_02;
        formatSpec_02 = "S16|X6"                                                          + "|"; 
        formatSpec_02 += tf.helpWithDoublesField(6, betasRange.getFirstValue(), betasRange.getSecondValue(), 3, 3)     + "|";
        formatSpec_02 += "X8"                                     + "|";
        formatSpec_02 += tf.helpWithDoublesField(6, seBetasRange.getFirstValue(), seBetasRange.getSecondValue(), 3, 3) + "|";
        formatSpec_02 += "X5|F6.3|"                                                             + "|";
        format_02 = tf.doTheFormatting(formatSpec_02);
        format_02 += "\n";

        String lrTable4 = "Constant";
        logisticReport.add(String.format(format_02, lrTable4,
                                                     beta0,
                                                     seBeta0,
                                                     zBeta0));
        df = nGroups - 2;
        standNorm = new StandardNormal();
        chiSqDist_PandD = new ChiSquareDistribution(df);
        pValuePearson = 2.0 * standNorm.getRightTailArea(Math.abs(zBeta1));

        String formatSpec_03;
        formatSpec_03 = "S16|X6"                                  + "|"; 
        formatSpec_03 += tf.helpWithDoublesField(6, betasRange.getFirstValue(), betasRange.getSecondValue(), 3, 3)      + "|"; 
        formatSpec_03 += "X8"                                     + "|";  //  spaces
        formatSpec_03 += tf.helpWithDoublesField(6, seBetasRange.getFirstValue(), seBetasRange.getSecondValue(), 3, 3)  + "|"; 
        
        formatSpec_03 += "X5|F6.3|X6|F4.3|X4|F6.2|X4|F6.2|X3|F6.2|";
        
        format_03 = tf.doTheFormatting(formatSpec_03);
        
        logisticReport.add(String.format(format_03, grpResponseTable[0], 
                                                     beta1 , seBeta1,  
                                                     zBeta1, pValuePearson,
                                                     oddsRatio,
                                                     ciLower, ciUpper));

        double pearsonPValue = chiSqDist_PandD.getRightTailArea(pearsonChiSquare);
        double deviationPValue = chiSqDist_PandD.getRightTailArea(deviance);
        addNBlankLinesToLogisticReport(2);
        
        String lrTable5 = "     **********     Goodness-of-Fit Tests     **********\n";
        logisticReport.add(String.format("       %10s", lrTable5));
        
        String lrTable6 = "   Method          Chi-Square       df        p-value\n";
        logisticReport.add(String.format("\n        %10s", lrTable6));
          
        logisticReport.add(String.format("           Pearson        %9.3f        %3d        %6.3f", pearsonChiSquare,
                                                                df,  
                                                                pearsonPValue)); 
        logisticReport.add(String.format("\n           Deviance       %9.3f        %3d        %6.3f", deviance,
                                                                df,  
                                                                deviationPValue)); 
        
        addNBlankLinesToLogisticReport(2);
        logisticReport.add(String.format("       Log-Likelihood = %8.3f", logLikeFull)); 
        addNBlankLinesToLogisticReport(1);
        logisticReport.add(String.format("       Test for all parameters zero: G =%7.3f,   df = %3d,   P-value = %4.3f", G_Statistic,
                                                                df,  
                                                                LRTS_PValue)); 
        addNBlankLinesToLogisticReport(5);
    }

   private void printDiagnostics() {
       double /*ithResid,*/ ithObsProb, ithEstProb, ithDevianceResid, 
              ithPearsonResid, ithStandPearsonResid;    
       
       addNBlankLinesToDiagnosticReport(2);
       
        logisticDiagnostics.add("                               Logistic Regression Diagnostics");
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("                Observed        Estimated        Deviance        Pearson       Stand Pearson"));
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("Observation    Probability     Probability       Residual       Residual         Residual"));
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(1);

        for (int ith = 0; ith < nGroups; ith++) {
            ithXValue = sortedXValues[ith];
            ithObsProb =  originalProps[sortKey[ith]];
            ithEstProb = estPi[sortKey[ith]];
            ithDevianceResid = devianceResiduals.get(sortKey[ith], 0);
            
            ithPearsonResid = pearsonResiduals.get(sortKey[ith], 0);
            ithStandPearsonResid = standPearsonResiduals.get(sortKey[ith], 0);
            
            logisticDiagnostics.add(String.format("  %8.3f     %8.3f        %8.3f         %8.3f       %8.3f          %8.3f", 
                             ithXValue, ithObsProb, ithEstProb, ithDevianceResid, ithPearsonResid, ithStandPearsonResid));
            addNBlankLinesToDiagnosticReport(1);
        }
        
        qdv_DevResids = new QuantitativeDataVariable(dm, "Residuals", "Residuals", devianceResiduals);

        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(3);
   }
   
   /************************************************************************
    *   These need to be sorted b/c the Map in the Logistic_Controller     *
    *   does not necessarily get the X values in the low to high sequence. *
    *   These are sorted so that the printing gets it right.               *
    ***********************************************************************/
   private void sortXsLowToHigh() {
       int tempInt;
       double tempDouble;
       
       for (int ithKey = 0; ithKey < nGroups; ithKey++) {
           sortKey[ithKey] = ithKey;
           sortedXValues[ithKey] = theXValues[ithKey];
       }
       
       for (int k = 1; k < nGroups; k++) {           
           for (int i = 0; i < nGroups - k; i++) {               
               if (sortedXValues[i] > sortedXValues[i + 1]) {
                   tempInt = sortKey[i];
                   tempDouble = sortedXValues[i];
                   sortKey[i] = sortKey[i + 1];
                   sortedXValues[i] = sortedXValues[i + 1];
                   sortKey[i + 1] = tempInt;
                   sortedXValues[i + 1] = tempDouble;   
               }
           }
       }
   }
   
    private void addNBlankLinesToDiagnosticReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(logisticDiagnostics, thisMany);
    }

    private void addNBlankLinesToLogisticReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(logisticReport, thisMany);
    }
  
   public double[] getPropsOfXValues() { return originalProps; }
   
   public double getBeta0()  {return beta0;}
   public double getBeta1()  {return beta1;}
   public double getSEBeta0()  {return seBeta0;}
   public double getSEBeta1()  {return seBeta1;}
   public double getCovBetas() {return covBetas;}
   public String getXAxisLabel()  {return xAxisLabel;}
   public QuantitativeDataVariable getQDVResids() {return qdv_DevResids;}   
   public Logistic_Controller getLogisticRegController() {return logReg_Controller; }
   public ArrayList<String> getLogisticReport() { return logisticReport; }
   public ArrayList<String> getDiagnostics() { return logisticDiagnostics; }
   public Matrix getDevianceResids() { return devianceResiduals; }
   public Matrix getEstimatedProbs() { return estProbs; }
   public String[] getUniques() { return strUniques; }   
   public Data_Manager getDataManager() { return dm; }   
   public String getRespVsExplSubtitle() { return respVsExplanVar; }   
}