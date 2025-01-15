/**************************************************
 *              Matched-t-DiffModel               *
 *                    11/01/23                    *
 *                     18:00                      *
 *************************************************/
package the_t_procedures;

import dataObjects.BivariateContinDataObj;
import splat.Data_Manager;

public class Matched_t_DiffModel {
    
    int nLegalDataPoints;
    
    double minPre, maxPre, minPost, maxPost, minScale, maxScale;
    double[] preData, postData;
    
    String preLabel, postLabel, descrOfDifference;
    
            // Make empty if no-print
    //String waldoFile = "Matched_t_DiffModel";
    String waldoFile = "";
    
    BivariateContinDataObj bivContin;
    Data_Manager dm;
    
    public Matched_t_DiffModel(Matched_t_Controller mt_Controller) {  
        dm = mt_Controller.getDataManager();
        dm.whereIsWaldo(29, waldoFile, "Constructing");
        bivContin = mt_Controller.getBivContin();
        preLabel = bivContin.getXLabel();
        postLabel = bivContin.getYLabel();
        descrOfDifference = mt_Controller.getDescriptionOfDifference();
        nLegalDataPoints = bivContin.getNLegalDataPoints();
        preData = new double[nLegalDataPoints];
        preData = bivContin.getXAs_arrayOfDoubles();
        postData = new double[nLegalDataPoints];
        postData = bivContin.getYAs_arrayOfDoubles();

        //  Calculate mins and maxes for DiffView scales;
        minPre = Double.MAX_VALUE;
        minPost = Double.MAX_VALUE;
        maxPre = -Double.MAX_VALUE;
        maxPost = -Double.MAX_VALUE;

        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            minPre = Math.min(minPre, preData[ith]);
            minPost = Math.min(minPost, postData[ith]);
            maxPre = Math.max(maxPre, preData[ith]);
            maxPost = Math.max(maxPost, postData[ith]);
        }
        
        minScale = Math.min(minPre, minPost);
        maxScale = Math.max(maxPre, maxPost);
    }
    
    public double getMinScale() { return minScale; }
    public double getMaxScale() { return maxScale; } 
    
    public double[] getThePreData() { return preData; }
    public double[] getThePostData() { return postData; }
    
    public String getDescrOfDiff() { return descrOfDifference; }
    
    public String getPreLabel() { return preLabel; };
    public String getPostLabel() { return postLabel; }
    
    public Data_Manager getDataManager() { return dm; };
    
    public int getNLegalDataPoints() {return nLegalDataPoints; }
}
