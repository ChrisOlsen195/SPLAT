/****************************************************************************
 *                    TwoByTwo_Calculations                                 * 
 *                          08/19/24                                        *
 *                            00:00                                         *
 ***************************************************************************/
package epidemiologyProcedures;

/***************************************************************************
 *                                                                         *
 *  Rosner, B.  Fundamentals of Biostatistics (8th ed)                     *
 *                                                                         *
 **************************************************************************/
/**************************************************************************
 *                                 Diseased
 * 
 *                          Yes      No
 * 
 *                     Yes    a       b      a + b
 *           Exposed
 *                      No    c       d      c + d
 *      
 *                          a + c   b + d      n
 * 
 **************************************************************************/

import java.util.ArrayList;
import probabilityDistributions.*;
import utilityClasses.*;

public class TwoByTwo_Calculations {
    // POJOs
    int a, b, c, d, n;
    
    int int_aPlusC, int_bPlusD, int_aPlusB, int_cPlusD;
    
    double dbl_a, dbl_b, dbl_c, dbl_d, p1, q1, p2, q2, dbl_n1, dbl_n2;
    double dbl_aPlusC, dbl_bPlusD, dbl_aPlusB, dbl_cPlusD;
    double risk_Ratio, risk_Difference;
    double logRR, var_logRR, stdev_logRR, ci_logRR_Low, ci_logRR_High, 
           ci_RR_Low, ci_RR_High, oddsRatio, logOR, var_logOR, stdev_logOR, 
           x2_df1_OR, ci_logOR_Low, ci_logOR_High, ci_OR_Low, ci_OR_High, 
           critical_Z, lowBound, hiBound, daParam, varRiskDiff, sdRiskDiff, 
           lowCI_RiskDiff, highCI_RiskDiff;
    
    ArrayList<String> epiReport;
    
    String parameter;
    
    final String bunchaBlanks = "                                ";
    
    public TwoByTwo_Calculations(int [][] observed) {
        //System.out.println("52 TwoByTwo_Calculations, Constructing");
        a = observed[0][0];
        c = observed[1][0];
        b = observed[0][1];
        d = observed[1][1];
        
        int_aPlusC = a + c;
        int_bPlusD = b + d;
        int_aPlusB = a + b;
        int_cPlusD = c + d;

        n = a + b + c + d;
 
        dbl_a = a;
        dbl_b = b;
        dbl_c = c;
        dbl_d = d; 
    }
   
    public void doRelRiskAndOddsRatio() {
        double alpha = .05;
        dbl_aPlusC = dbl_a + dbl_c;
        dbl_bPlusD = dbl_b + dbl_d;
        
        // Can't have logs of 0.
        if (a == 0) {
            dbl_a = 0.5;
            dbl_aPlusC += 0.5;
        }
        if (b == 0) {
            dbl_b = 0.5;
            dbl_bPlusD += 0.5;
        }        
        if (c == 0) {
            dbl_c = 0.5;
            dbl_aPlusC += 0.5;
        }        
            if (d == 0) {
            dbl_d = 0.5;
            dbl_bPlusD += 0.5;
        }     

        dbl_aPlusB = dbl_a + dbl_b;
        dbl_cPlusD = dbl_c + dbl_d;
        
        // Can't have logs of 0.
        if (a == 0) {
            dbl_a = 0.5;
            dbl_aPlusB += 0.5;
        }
        if (c == 0) {
            dbl_c = 0.5;
            dbl_cPlusD += 0.5;
        }   
        
        ChiSquareDistribution chi_2 = new ChiSquareDistribution(1);
        StandardNormal zDistr = new StandardNormal();
        critical_Z = zDistr.getInvRightTailArea(alpha / 2.0);
        
        dbl_n1 = dbl_aPlusB;
        dbl_n2 = dbl_cPlusD;
        p1 = dbl_a / dbl_aPlusB;
        p2 = dbl_c / (dbl_cPlusD);
        q1 = 1.0 - p1;
        q2 = 1.0 - p2;

        risk_Difference = p1 - p2;       
        varRiskDiff = p1*q1 / dbl_n1 + p2 * q2 / dbl_n2;
        sdRiskDiff = Math.sqrt(varRiskDiff);
        lowCI_RiskDiff = risk_Difference - critical_Z * sdRiskDiff;
        highCI_RiskDiff = risk_Difference + critical_Z * sdRiskDiff;

        risk_Ratio = p1 / p2;
        logRR = Math.log(risk_Ratio);
        var_logRR = dbl_b / (dbl_a * dbl_n1) + dbl_d / (dbl_c * dbl_n2); 
        stdev_logRR = Math.sqrt(var_logRR);

        ci_logRR_Low = logRR - critical_Z * stdev_logRR; 
        ci_logRR_High = logRR + critical_Z * stdev_logRR;
        ci_RR_Low = Math.exp(ci_logRR_Low);
        ci_RR_High = Math.exp(ci_logRR_High);

        oddsRatio = dbl_a * dbl_d / (dbl_b * dbl_c);
        logOR = Math.log(oddsRatio);
        var_logOR = 1.0 / dbl_a + 1.0 / dbl_b + 1.0 / dbl_c + 1.0 / dbl_d;
        stdev_logOR = Math.sqrt(var_logOR);
        x2_df1_OR = logOR * logOR / var_logOR;
        
        ci_logOR_Low = logOR - critical_Z * stdev_logOR; 
        ci_logOR_High = logOR + critical_Z * stdev_logOR;
        ci_OR_Low = Math.exp(ci_logOR_Low);
        ci_OR_High = Math.exp(ci_logOR_High);

        epiReport = new ArrayList();
        
        epiReport.add(String.format("%40s", "                           95% Confidence intervals"));
        addNBlankLinesToRegressionReport(1);
        epiReport.add(String.format("     Parameter            Lower bound        Estimate        Upper Bound"));
        addNBlankLinesToRegressionReport(2);
        
        //  Risk difference
        parameter = getLeftMostNChars("Risk Difference" + bunchaBlanks, 21);
        daParam = risk_Difference;
        lowBound = lowCI_RiskDiff;        
        hiBound = highCI_RiskDiff;
        epiReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);
        
        //  Risk Ratio
        parameter = getLeftMostNChars("Risk Ratio" + bunchaBlanks, 21);
        daParam = risk_Ratio;
        lowBound = ci_RR_Low;        
        hiBound = ci_RR_High;
        epiReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);    
        
        //  log Risk Ratio
        parameter = getLeftMostNChars("Log Risk Ratio" + bunchaBlanks, 21);
        daParam = logRR;
        lowBound = ci_logRR_Low;        
        hiBound = ci_logRR_High;
        epiReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1); 
        
        //  oddsRatio
        parameter = getLeftMostNChars("Odds Ratio" + bunchaBlanks, 21);
        daParam = oddsRatio;
        lowBound = ci_OR_Low;        
        hiBound = ci_OR_High;
        epiReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1); 
        
        //  Log oddsRatio
        parameter = getLeftMostNChars("Log Odds Ratio" + bunchaBlanks, 21);
        daParam = logOR;
        lowBound = ci_logOR_Low ;        
        hiBound = ci_logOR_High;
        epiReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);         
    }
    
    private void addNBlankLinesToRegressionReport(int thisMany) {
        for (int ithBlank = 0; ithBlank < thisMany; ithBlank++) {
            epiReport.add("\n");
        }
    }
    
   public String getLeftMostNChars(String original, int leftChars) {
       return StringUtilities.getleftMostNChars(original, leftChars);
   }
   
    public double getRR() { return risk_Ratio; }
    public double getCIRRLow() { return ci_RR_Low; }
    public double getCIRRHigh() { return ci_RR_High; }
    
    public double getOddsRatio() { return oddsRatio; }
    public double getCIOddsRatioLow() { return ci_OR_Low; }
    public double getCIOddsRatioHigh() { return ci_OR_High; }
    
    
    public double getLogOddsRatio() { return logOR; }
    public double getCILogOddsRatioLow() { return ci_logOR_Low; }
    public double getCILogOddsRatioHigh() { return ci_logOR_High; }    
    
    public int getYCountYesses() { return int_aPlusB; }
    public int getYCountNos() { return int_cPlusD; }
    
    public int getXCountYesses() { return int_bPlusD; }
    public int getXCountNos() { return int_aPlusC; }
    
    public ArrayList getEpiReport() { return epiReport; }
    
    public int getTotal() { return n; }
}
