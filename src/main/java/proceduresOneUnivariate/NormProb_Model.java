/**************************************************
 *                 NormProb_Model                 *
 *                    06/20/26                    *
 *                      12:00                     *
 *************************************************/
package proceduresOneUnivariate;

import genericClasses.Transformations_Calculations;
import dataObjects.QuantitativeDataVariable;

public class NormProb_Model {  
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nLegalDataPoints;
    
    double[] theLegalRawData;
    
    String varLabel, varDescription, subTitle;    
    String[] strNormalScores;

    // My classes
    //NormProb_View normProb_View;
    QuantitativeDataVariable qdv_Data, qdvNormalScores;
    Transformations_Calculations transCalc;
   
    public NormProb_Model()  { }
        
    public NormProb_Model(String subTitle, QuantitativeDataVariable qdv_Data) { 
        if (printTheStuff) {
            System.out.println("32 *** NormProb_Model, Constructing");
        }
        this.subTitle = subTitle;
        this.qdv_Data = qdv_Data;
        varLabel = qdv_Data.getTheVarLabel();
        varDescription = qdv_Data.getTheVarDescription();
        transCalc = new Transformations_Calculations();
        theLegalRawData = qdv_Data.getLegalDataAsDoubles();
        
        nLegalDataPoints = theLegalRawData.length;
        strNormalScores = new String[nLegalDataPoints];   // Sorted in views     
        //qdv_Data = new QuantitativeDataVariable(varLabel, varDescription, theLegalRawData);
        strNormalScores = transCalc.unaryOpsOfVars(theLegalRawData, "rankits");  // Sorted in views
     
        String nrml_Label = "Normal" + varLabel;
        String nrml_Description = "Normal" + varDescription;
        qdvNormalScores = new QuantitativeDataVariable(nrml_Label, nrml_Description, strNormalScores);
    }
    
    
    public String getSubTitle() { return subTitle; }
    //public NormProb_View getNormProb_View() { return normProb_View; }
    public QuantitativeDataVariable getData() { return qdv_Data; }
    public QuantitativeDataVariable getNormalScores() { return qdvNormalScores; }    
    public int getNDataPoints() { return nLegalDataPoints; }
    public String getNormProbLabel() { return varLabel; }
    public String getNormProbUnits() { return varDescription; }
    
    public String toString() {
        String daString = "NormProb_DiffModel toString()";
        return daString;
    }

}

