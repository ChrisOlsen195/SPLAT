/**************************************************
 *            Regr_Compare_Controller             *
 *                  11/27/24                      *
 *                   12:00                        *
 *************************************************/
/**************************************************
 *    Tested against Tamhane p101  02/16/24       *
 *    Tested against Huitema p140  06/29/24       * 
 *    Tested against Montgomery p656 02/16/24     *
 *************************************************/
package simpleRegression;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dataObjects.Regr_Compare_Object;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proceduresOneUnivariate.NormProb_Model;
import splat.Data_Manager;

public class Regr_Compare_Controller {
    
    // POJOs
    boolean dataAreBalanced, replicatesExist;//, dataAreMissing;
    
    int nReplications, nLevels;
            
    String returnStatus, treatment_Name, covariate_Name, response_Name;
    
    ObservableList<String> categoryLabels;
    ArrayList<String> originalLevels;
    CatQuantDataVariable cqdv;
    ObservableList<String> transformedLevels;
    
    // String waldoFile = "Regr_Compare_Controller";
    String waldoFile = "";

    // My classes
    Regr_Compare_Dashboard ancova_Dashboard;
    Regr_Compare_Model ancova_Model;
    Regr_Compare_Object ancova_Object;
    Regr_Compare_Dialog ancova_Dialog;
    Data_Manager dm;
    NormProb_Model normProb_Model;
    QuantitativeDataVariable qdv_Residuals;
            
    public Regr_Compare_Controller (Data_Manager dm) {
        this.dm = dm; 
        dm.whereIsWaldo(51, waldoFile, "Constructing");
        ancova_Dialog = new Regr_Compare_Dialog(dm);
        ancova_Dialog.doTheDialog();
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.add(ancova_Dialog.getCovariate_Name());
        categoryLabels.add(ancova_Dialog.getResponse_Name());
        categoryLabels.add(ancova_Dialog.getTreatment_Name());
    }
        
public String doTheRegr_Compare() {
        dm.whereIsWaldo(61, waldoFile, "doTheANCOVA()");
        returnStatus = "OK";

        if (ancova_Dialog.getReturnStatus().equals("Cancel")) {
            returnStatus = "Cancel";
            return returnStatus;
        }
        
        originalLevels = new ArrayList();
        originalLevels = ancova_Dialog.getOriginalLevels();
          
        treatment_Name = ancova_Dialog.getTreatment_Name();
        covariate_Name = ancova_Dialog.getCovariate_Name();   
        response_Name = ancova_Dialog.getResponse_Name(); 
        
        // data is a row x col ArrayList of values;
        ArrayList <ColumnOfData> data = ancova_Dialog.getData();
        
        ancova_Object = new Regr_Compare_Object(data);
        ancova_Model = new Regr_Compare_Model(this);

        nLevels = originalLevels.size();
        transformedLevels = ancova_Model.getTransformedLabels();
        originalLevels = ancova_Dialog.getOriginalLevels();
        
        cqdv = new CatQuantDataVariable(dm, 
                                        originalLevels,
                                        transformedLevels, 
                                        "ANCOVA_Controller");
        
        
        double[] residuals;
        residuals = ancova_Model.getStudentizedResiduals();
        qdv_Residuals = new QuantitativeDataVariable("Residuals", "Residuals", residuals);

        normProb_Model = new NormProb_Model("Studentized residuals", qdv_Residuals);
        ancova_Dashboard = new Regr_Compare_Dashboard(this, ancova_Model);
        ancova_Dashboard.populateTheBackGround();
        ancova_Dashboard.putEmAllUp();
        ancova_Dashboard.showAndWait();

        return returnStatus;
    }    
    
    public int getNLevels() { return nLevels; }
    public String get_TreatmentName() { return treatment_Name; }
    public String get_ResponseName() { return response_Name; }
    public String get_CovariateName() { return covariate_Name; }
    public Regr_Compare_Object get_RegrCompareObject() { return ancova_Object; }
    public ArrayList<String> get_OriginalLevels() { return originalLevels; }
    public ObservableList<String> getCategoryLabels() { return categoryLabels; }
    public CatQuantDataVariable get_CatQuant_DV() { return cqdv; }
    public boolean getDataAreBalanced() { return dataAreBalanced; }
    public boolean getReplicatesExist() { return replicatesExist; }
    public int getNReplications() {return nReplications; }
    public String getReturnStatus() { return returnStatus; }
    public Regr_Compare_Model getANCOVA_Model() { return ancova_Model; }
    public QuantitativeDataVariable getQDV_StudResids() { return qdv_Residuals; }
    public NormProb_Model getNormProb_Model() {return normProb_Model; }
    public Data_Manager getDataManager() { return dm; }
}
