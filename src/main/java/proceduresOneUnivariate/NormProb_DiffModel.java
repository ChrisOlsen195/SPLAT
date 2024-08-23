/**************************************************
 *              NormProb-DiffModel                *
 *                    11/01/23                    *
 *                     21:00                      *
 *************************************************/
package proceduresOneUnivariate;

import dataObjects.QuantitativeDataVariable;
import genericClasses.Transformations_Calculations;

public class NormProb_DiffModel {
    
    // POJOs
    int nLegalDataPoints;
    
    double rawMean, rawStDev;
    double[] theLegalRawData;
    
    String rawDataLabel, rawDataDescription, subTitle;    
    String[] strNormalScores;
    
    NormProb_View normProb_View;
    QuantitativeDataVariable qdv, qdvData;
    QuantitativeDataVariable qdvNSs;
    Transformations_Calculations transCalc;
    
    public NormProb_DiffModel() { }
    
    public NormProb_DiffModel(String subTitle, QuantitativeDataVariable qdv_Data) {
        //System.out.println("30 NormProb_DiffModel, constructing");
        this.subTitle = subTitle;
        qdv = qdv_Data;
        rawDataLabel = qdv.getTheVarLabel();
        rawDataDescription = qdv.getTheVarDescription();
        transCalc = new Transformations_Calculations();
        theLegalRawData = qdv_Data.getLegalDataAsDoubles();
        
        rawMean = qdv_Data.getTheMean();
        rawStDev = qdv_Data.getTheStandDev();
        
        nLegalDataPoints = theLegalRawData.length;
        strNormalScores = new String[nLegalDataPoints];   // Sorted in views     
        qdvData = new QuantitativeDataVariable(rawDataLabel, rawDataDescription, theLegalRawData);
        strNormalScores = transCalc.unaryOpsOfVars(theLegalRawData, "rankits");  // Sorted in views
     
        String nrml_Label = "Normal" + rawDataLabel;
        String nrml_Description = "Normal" + rawDataDescription;
        qdvNSs = new QuantitativeDataVariable(nrml_Label, nrml_Description, strNormalScores); 
    }
    
    public String getSubTitle() { return subTitle; }
    public NormProb_View getNormProb_View() { return normProb_View; }
    public QuantitativeDataVariable getData() { return qdvData; }
    public QuantitativeDataVariable getNormalScores() { return qdvNSs; }    
    public int getNLegalDataPoints() { return nLegalDataPoints; }
    public String getRawDataLabel() { return rawDataLabel; }
    public String getRawDataDescription() { return rawDataDescription; }
  
    public double[] getTheRawData () { return theLegalRawData; }
    public double[] getTheNormalScores() { return qdvNSs.getLegalDataAsDoubles(); }
    
    public double getTheMean() { return rawMean; }
    public double getTheStDev() { return rawStDev; }

    public String toString() {
        String daString = "NormProb_Model toString()";
        return daString;
    }
}
