/**************************************************
 *               NormProb_DiffModel               *
 *                    10/15/23                    *
 *                      18:00                     *
 *************************************************/
package proceduresOneUnivariate;

import genericClasses.Transformations_Calculations;
import dataObjects.QuantitativeDataVariable;

public class NormProb_Model {  
    // POJOs
    int nDataPoints;
    
    double[] theRawData;
    
    String normProbVarLabel, normProbVarDescription, subTitle;    
    String[] strNormalScores;

    // My classes
    NormProb_View normProb_View;
    QuantitativeDataVariable qdv, qdvData;
    QuantitativeDataVariable qdvNSs;
    Transformations_Calculations transCalc;
   
    public NormProb_Model()  { }
        
    public NormProb_Model(String subTitle, QuantitativeDataVariable qdv_Data) { 
        this.subTitle = subTitle;
        qdv = qdv_Data;
        normProbVarLabel = qdv.getTheVarLabel();
        normProbVarDescription = qdv.getTheVarDescription();
        transCalc = new Transformations_Calculations();
        theRawData = qdv_Data.getLegalDataAsDoubles();
        
        nDataPoints = theRawData.length;
        strNormalScores = new String[nDataPoints];   // Sorted in views     
        qdvData = new QuantitativeDataVariable(normProbVarLabel, normProbVarDescription, theRawData);
        strNormalScores = transCalc.unaryOpsOfVars(theRawData, "rankits");  // Sorted in views
     
        String nrml_Label = "Normal" + normProbVarLabel;
        String nrml_Description = "Normal" + normProbVarDescription;
        qdvNSs = new QuantitativeDataVariable(nrml_Label, nrml_Description, strNormalScores);
    }
    
    
    public String getSubTitle() { return subTitle; }
    public NormProb_View getNormProb_View() { return normProb_View; }
    public QuantitativeDataVariable getData() { return qdvData; }
    public QuantitativeDataVariable getNormalScores() { return qdvNSs; }    
    public int getNDataPoints() { return nDataPoints; }
    public String getNormProbLabel() { return normProbVarLabel; }
    public String getNormProbUnits() { return normProbVarDescription; }
    
    public String toString() {
        String daString = "NormProb_DiffModel toString()";
        return daString;
    }

}

