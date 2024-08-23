/**************************************************
 *                  ANCOVA_Object                 *
 *                    05/24/24                    *
 *                      15:00                     *
 *************************************************/
package dataObjects;

import java.util.ArrayList;

public class ANCOVA_Object {
    
    int nLevels;
    int nOriginalCases; //, nLegalCases;
    
    BivariateContinDataObj bivContinDataObject;
    ArrayList<BivariateContinDataObj> al_BivContinWithin;
    CategoricalDataVariable cdvTreatments;
    ColumnOfData col_Covariate, col_Response, col_Treatment;
    ArrayList <ColumnOfData> col_al_Data;
    ArrayList<String>[] str_al_Covariates_Within, str_al_Responses_Within;
    ArrayList<ColumnOfData> col_al_ForBiv;
    ArrayList<String> str_al_Covariate, str_al_Response, str_al_Treatment;
    ArrayList<String> strTreatmentLabels; 
    
    public ANCOVA_Object(ArrayList <ColumnOfData> data) {
        //System.out.println("26 ANCOVA_Object, constructing");

        col_Covariate = new ColumnOfData(data.get(0));
        col_Response = new ColumnOfData(data.get(1));
        col_Treatment = new ColumnOfData(data.get(2));
        
        nOriginalCases = col_Covariate.getNCasesInColumn();
        cdvTreatments = new CategoricalDataVariable("Treatments", col_Treatment);
        strTreatmentLabels = new ArrayList();
        strTreatmentLabels = cdvTreatments.getListOfLevels();
        nLevels = strTreatmentLabels.size();
        
        // Convert categorical treatments to 0, 1, ...
        for (int ithCase = 0; ithCase < nOriginalCases; ithCase++) {
            for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
                String strIthCase = col_Treatment.getIthCase(ithCase);
                String strIthTreat = strTreatmentLabels.get(ithLevel);
                if (strIthCase.equals(strIthTreat)) {
                    String quantTreat = Double.toString(ithLevel);
                    col_Treatment.setStringInIthRow(ithCase, quantTreat);
                }
            }
        }
        
        str_al_Covariate = new ArrayList();
        str_al_Response = new ArrayList();
        str_al_Treatment = new ArrayList();

        str_al_Covariates_Within = new ArrayList[nLevels];
        str_al_Responses_Within = new ArrayList[nLevels];
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            str_al_Covariates_Within[ithLevel] = new ArrayList();
            str_al_Responses_Within[ithLevel] = new ArrayList();
        }
        
        int nData = col_Covariate.getNCasesInColumn();
        for (int ithCase = 0; ithCase < nData; ithCase++) {
            if(!col_Covariate.getIthCase(ithCase).equals("*")
              && !col_Response.getIthCase(ithCase).equals("*")
              && !col_Treatment.getIthCase(ithCase).equals("*")) {  
                
                // Fill the stuff for All
                str_al_Covariate.add(col_Covariate.getIthCase(ithCase));
                str_al_Response.add(col_Response.getIthCase(ithCase));
                str_al_Treatment.add(col_Treatment.getIthCase(ithCase)); 
                
                int ithTreatment = (int)Double.parseDouble(col_Treatment.getIthCase(ithCase));
                str_al_Covariates_Within[ithTreatment].add(col_Covariate.getIthCase(ithCase));
                str_al_Responses_Within[ithTreatment].add(col_Response.getIthCase(ithCase));
            }
        }
        
        // At this point we have all the legal col_al_Data -- now re-use the cols
        col_Covariate = new ColumnOfData("Covariate", "Covariate", str_al_Covariate);
        col_Response = new ColumnOfData("Response", "Response", str_al_Response);
        col_Treatment = new ColumnOfData("Treatment", "Treatment", str_al_Treatment);
       
        //nLegalCases = col_Covariate.getNCasesInColumn();

        /*
        for (int ithCase = 0; ithCase < nLegalCases; ithCase++) {
            String colCase = col_Covariate.getStringInIthRow(ithCase);
            String colResponse = col_Response.getStringInIthRow(ithCase);
            String colTreatment = col_Treatment.getStringInIthRow(ithCase);
        }
        */
        
        col_al_ForBiv = new ArrayList();
        col_al_ForBiv.add(col_Covariate);
        col_al_ForBiv.add(col_Response);    

        bivContinDataObject = new BivariateContinDataObj(col_al_ForBiv);
        bivContinDataObject.continueConstruction();
        
        al_BivContinWithin = new ArrayList();
        
        for (int ithBCW = 0; ithBCW < nLevels; ithBCW++) {
            String tempLabel_1 = "cov" + String.valueOf(ithBCW);
            ColumnOfData tempCov = new ColumnOfData(tempLabel_1, tempLabel_1, str_al_Covariates_Within[ithBCW]);
            String tempLabel_2 = "resp" + String.valueOf(ithBCW);
            ColumnOfData tempResp = new ColumnOfData(tempLabel_2, tempLabel_2, str_al_Responses_Within[ithBCW]); 
            ArrayList<ColumnOfData> temp_alColOfData = new ArrayList();
            temp_alColOfData.add(tempCov);
            temp_alColOfData.add(tempResp);
            BivariateContinDataObj tempBCDO = new BivariateContinDataObj(temp_alColOfData);
            tempBCDO.continueConstruction();
            al_BivContinWithin.add(tempBCDO);            
        }       
    }
    
    public ArrayList <ColumnOfData> getData() { return col_al_Data; }
    
    public ColumnOfData getColCovariate() { return col_Covariate; }
    public ColumnOfData getColResponse() { return col_Response; }
    public ColumnOfData getColTreatment() { return col_Treatment; }
    
    public double getMeanCov() { return bivContinDataObject.getMeanX(); }
    public double getMeanResp() { return bivContinDataObject.getMeanY(); }
    
    public double getMeanCovWithin(int ithWithin) { return al_BivContinWithin.get(ithWithin).getMeanX(); }
    public double getMeanRespWithin(int ithWithin) { return al_BivContinWithin.get(ithWithin).getMeanY(); }
    
    public double getStDevCov() { return bivContinDataObject.getStDevX(); }
    public double getStDevResp() { return bivContinDataObject.getStDevY(); }
    
    public double getStDevCovWithin(int ithWithin) { return al_BivContinWithin.get(ithWithin).getStDevX(); }
    public double getStDevRespWithin(int ithWithin) { return al_BivContinWithin.get(ithWithin).getStDevY(); }
    
    public double getSumOfSquaresCov() {
        double s_xx = bivContinDataObject.getSumOfSquares_XX();
        return s_xx;
    }
    
    public double getSumOfProductsCovResponse() {
        double s_xy = bivContinDataObject.getSumOfSquares_XY();
        return s_xy;
    }
    
    public double getSumOfSquaresResponse() {
        double s_yy = bivContinDataObject.getSumOfSquares_YY();
        return s_yy;
    }
    
    public double getSumOfSquaresCovWithin(int ithWithin) {
        double s_xx = al_BivContinWithin.get(ithWithin).getSumOfSquares_XX();
        return s_xx;
    }
    
    public double getSumOfProductsCovResponseWithin(int ithWithin) {
        double s_xy = al_BivContinWithin.get(ithWithin).getSumOfSquares_XY();
        return s_xy;
    }
    
    public double getSumOfSquaresResponseWithin(int ithWithin) {
        double s_yy = al_BivContinWithin.get(ithWithin).getSumOfSquares_YY();
        return s_yy;
    }
    
    public double getCorrelation() {
        double r = bivContinDataObject.getCorrelation();
        return r;
    }
    
    public double getCorrelationWithin(int ithWithin) {
        double r = al_BivContinWithin.get(ithWithin).getCorrelation();
        return r;
    }
    
    public double getSlope() {
        double r = bivContinDataObject.getSlope();
        return r;
    }
    
    public double getSlopeWithin(int ithWithin) {
        double r = al_BivContinWithin.get(ithWithin).getSlope();;
        return r;
    }
    
    public double getIntercept() {
        double r = bivContinDataObject.getIntercept();
        return r;
    }
    
    public double getInterceptWithin(int ithWithin) {
        double r = al_BivContinWithin.get(ithWithin).getIntercept();
        return r;
    }
    
    public String toString() {
        /*
        System.out.println("ANCOVA_Object toString()");;
        System.out.println("xMean = " + bivContinDataObject.getMeanX());
        System.out.println("yMean = " + bivContinDataObject.getMeanY());
        System.out.println("corr = " + bivContinDataObject.getCorrelation());
        System.out.println("slope = " + bivContinDataObject.getSlope());
        System.out.println("intercept = " + bivContinDataObject.getIntercept());
*/
        return "ANCOVA_Object.toString printed \n\n";
    }
    
}
