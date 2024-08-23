/**************************************************
 *              Matched_t_Controller              *
 *                    11/01/23                    *
 *                     15:00                      *
 *************************************************/
package the_t_procedures;

import dialogs.t_and_z.MatchedPairs_Dialog;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.*;
import dataObjects.BivariateContinDataObj;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresOneUnivariate.NormProb_DiffModel;
import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.StemNLeaf_Model;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class Matched_t_Controller {
    // POJOs  
    int confidenceLevel;
    
    ArrayList<double[]> theMatched;
    String returnStatus, diffLabel, diffDescription;
    // Make empty if no-print
    //String waldoFile = "Matched_t_Controller";
    String waldoFile = "";
        
    // My classes
    ArrayList<ColumnOfData> indivColsOfData;
    BivariateContinDataObj bivContin;
    Data_Manager dm;
    HorizontalBoxPlot_Model hBox_Model;
    VerticalBoxPlot_Model vBox_Model;
    NormProb_Model normProb_Model;
    NormProb_DiffModel normProb_DiffModel;
    MatchedPairs_Dialog matchedPairs_Dialog;
    Matched_t_Dashboard matched_t_Dashboard;
    Matched_t_Model matched_t_Model; 
    Matched_t_DiffModel matched_t_DiffModel;
    QuantitativeDataVariable theQDV;
    StemNLeaf_Model stemNLeaf_Model;

    // ******  Constructor called from Main Menu  ******
    public Matched_t_Controller(Data_Manager dm) {
        this.dm = dm; 
        dm.whereIsWaldo(50, waldoFile, "Constructing");
        indivColsOfData = new ArrayList();
    }

    // ******                 Called from Main Menu                 ******        
    public String prepColumnsFromNonStacked() {
        dm.whereIsWaldo(56, waldoFile, "constructing");
        returnStatus = "OK";
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        matchedPairs_Dialog = new MatchedPairs_Dialog(dm, "QUANTITATIVE");
        matchedPairs_Dialog.showAndWait();
        returnStatus = matchedPairs_Dialog.getReturnStatus();
        
        if (returnStatus.equals("Cancel")) { return returnStatus; }
        
        indivColsOfData = matchedPairs_Dialog.getData();
        confidenceLevel = matchedPairs_Dialog.getConfidenceLevel();
        bivContin = new BivariateContinDataObj(dm, indivColsOfData);
        int nPairs = bivContin.getNLegalDataPoints();
        bivContin.continueConstruction();
        matched_t_DiffModel = new Matched_t_DiffModel(this);
        String xVarLabel = matchedPairs_Dialog.getFirstVarLabel_InFile();
        String yVarLabel = matchedPairs_Dialog.getSecondVarLabel_InFile();
        String xVarDescr = matchedPairs_Dialog.getPreferredFirstVarDescription();
        String yVarDescr = matchedPairs_Dialog.getPreferredSecondVarDescription();
        diffLabel = StringUtilities.truncateString(xVarLabel, 12) + " - " + StringUtilities.truncateString(yVarLabel, 12);  
        diffDescription = StringUtilities.truncateString(xVarDescr, 20) + " - " + StringUtilities.truncateString(yVarDescr, 20);       
         
        theMatched = new ArrayList(2);
        theMatched = bivContin.getBivDataAsDoubles();

        double[] theDiffs = new double[nPairs];
        for (int ithPair = 0; ithPair < nPairs; ithPair++) {
            double firstVal = theMatched.get(0)[ithPair];
            double secondVal = theMatched.get(1)[ithPair];
            theDiffs[ithPair] = firstVal - secondVal;
        }
        theQDV = new QuantitativeDataVariable(diffLabel, diffDescription, theDiffs);
        returnStatus = doTheProcedure();
        return returnStatus;
    } 
    
    private String doTheProcedure() {
        dm.whereIsWaldo(99, waldoFile, "doTheProcedure()");
        hBox_Model = new HorizontalBoxPlot_Model(diffDescription, theQDV);
        vBox_Model = new VerticalBoxPlot_Model(diffDescription, theQDV);
        
        normProb_Model = new NormProb_Model(diffDescription, theQDV);
        normProb_DiffModel = new NormProb_DiffModel(diffDescription, theQDV);
        // ****************************************************************
        // *  The stemNLeaf_Model parameters are also supporting a back-  *
        // *  to-back stem and leaf plot.                                 *
        // ****************************************************************
        stemNLeaf_Model = new StemNLeaf_Model(diffDescription, theQDV, false, 0, 0, 0);
        matched_t_Model = new Matched_t_Model(this, theQDV);
        matched_t_Model.doMatched_TAnalysis();
        matched_t_Dashboard = new Matched_t_Dashboard(this, theQDV);
        returnStatus = showTheDashboard();
        return returnStatus;        
    }
    
    public String showTheDashboard() {
        dm.whereIsWaldo(118, waldoFile, "showTheDashboard()");
        returnStatus = "OK";
        matched_t_Dashboard.populateTheBackGround();
        matched_t_Dashboard.putEmAllUp();
        matched_t_Dashboard.showAndWait();
        returnStatus = matched_t_Dashboard.getReturnStatus();
        return returnStatus;           
    }
    
    public HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    public VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    public StemNLeaf_Model getStemNLeaf_Model() { return stemNLeaf_Model; }    
    public Matched_t_Model getMatchedTModel() { return matched_t_Model; }
    
    public NormProb_Model getNormProbModel() { return normProb_Model; }
    
    public NormProb_DiffModel getNormProb_DiffModel() { 
        return normProb_DiffModel; 
    }
    
    public double getAlpha() { return matchedPairs_Dialog.getAlpha(); }
    public int getConfidenceLevel() { return confidenceLevel; }
    
    public String getAltHypothesis() { 
        return matchedPairs_Dialog.getChosenHypothesis(); 
    }
    
    public double getHypothesizedDiff() { 
        return matchedPairs_Dialog.getHypothesizedDiff();
    }
    
    public String[] getHypothPair() {
        return matchedPairs_Dialog.getHypothesesToPrint();
    }
    
    public String getDescriptionOfDifference() { return diffDescription; }
    public Data_Manager getDataManager() { return dm; }
    public BivariateContinDataObj getBivContin() { return bivContin; }
    public MatchedPairs_Dialog getTDialog() { return matchedPairs_Dialog; }
    public Matched_t_DiffModel getDiffModel() { return matched_t_DiffModel; };
}
