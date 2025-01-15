/**************************************************
 *               Indep_t_PrepareStructs           *
 *                    12/23/23                    *
 *                      18:00                     *
 *************************************************/
package the_t_procedures;

import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import proceduresTwoUnivariate.*;
import splat.Data_Manager;

public class Indep_t_PrepStructs  
{ 
    // POJOs
    String strSubtitle, strFirstVarDescription, strSecondDescription;
    //String waldoFile = "Indep_t_PrepStructs";
    String waldoFile = "";
    
    // My classes
    BBSL_Model bbsl_Model;
    Data_Manager dm;
    private final Indep_t_Dashboard indep_t_Dashboard;
    HorizontalBoxPlot_Model hBox_Model;
    QuantitativeDataVariable pooledQDV;
    QQPlot_Model qqPlot_Model;
    VerticalBoxPlot_Model vBox_Model;
    Indep_t_Model indep_t_Model;

    public Indep_t_PrepStructs(Indep_t_Controller indep_t_Controller, 
            QuantitativeDataVariable pooledQDV,
            ArrayList<QuantitativeDataVariable> allTheQDVs) { 
        dm = indep_t_Controller.getDataManager();
        dm.whereIsWaldo(33, waldoFile, "Constructing");
        strFirstVarDescription = indep_t_Controller.getFirstVarDescription();
        strSecondDescription = indep_t_Controller.getSecondVarDescription();           
        strSubtitle = strFirstVarDescription  + " & " + strSecondDescription; 
        hBox_Model = new HorizontalBoxPlot_Model(this, strSubtitle, allTheQDVs);
        vBox_Model = new VerticalBoxPlot_Model(this, strSubtitle, allTheQDVs);
        qqPlot_Model = new QQPlot_Model(this, strSubtitle, allTheQDVs);
        bbsl_Model = new BBSL_Model(this, pooledQDV, allTheQDVs);
        indep_t_Model = new Indep_t_Model(indep_t_Controller, allTheQDVs);
        indep_t_Model.doIndepTAnalysis();
        indep_t_Dashboard = new Indep_t_Dashboard(this, pooledQDV, allTheQDVs);
    }  
    
    public String showTheDashboard() {
        String returnStatus;
        indep_t_Dashboard.populateTheBackGround();
        indep_t_Dashboard.putEmAllUp();
        indep_t_Dashboard.showAndWait();
        returnStatus = indep_t_Dashboard.getReturnStatus();
        //if (returnStatus.equals("Ok"))
        //    return returnStatus; 
        // ************************************************************
        return returnStatus;           
    }
    
    public String getFirstVarDescription() { return strFirstVarDescription; }
    public String getSecondVarDescription() { return strSecondDescription; }
    public String getSubTitle() { return strSubtitle; }
    HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    QQPlot_Model getQQ_Model() { return qqPlot_Model; }
    BBSL_Model getBBSL_Model() { return bbsl_Model; }    
    Indep_t_Model getIndepTModel() { return indep_t_Model; }
    
    public Data_Manager getDataManager() { return dm; }
}
