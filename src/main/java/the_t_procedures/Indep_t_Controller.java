/**************************************************
 *               Indep_t_Controller               *
 *                   12/03/25                     *
 *                     12:00                      *
 *************************************************/
package the_t_procedures;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.ReOrderStringDisplay_Dialog;
import dialogs.t_and_z.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresTwoUnivariate.BBSL_Model;
import proceduresTwoUnivariate.DotPlot_2Ind_Model;
import proceduresTwoUnivariate.QQPlot_Model;
import splat.*;
import utilityClasses.*;

public class Indep_t_Controller {
    //  POJOs
    public boolean goodToGo, checkForLegalChoices;
    boolean[] theStrIsNumeric;
    public int n_QDVs, TWO, nColumnsOfData; 
    public int[] theNewOrder;
    String strThisVarLabel, strThisVarDescr, strSubTitle_And, strSubTitle_Vs,
           tidyOrTI8x, rawDataOrSummary, strFirstVarDescr, strSecondVarDescr;
    String strIndepDlgReturnStatusX, strIndepDlgReturnStatusY, strReturnStatus;
    String[] incomingLabels;
    public ArrayList<String> allTheLabels, alStr_AllTheLabels;
    
    // Make empty if no-print
    //String waldoFile = "Indep_t_Controller";
    String waldoFile = "";

    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
    public ArrayList<ColumnOfData> indep_t_ColsOfData;
    public CatQuantDataVariable catQuantVar;
    public ObservableList<String> categoryLabels;
    public QuantitativeDataVariable qdv_Pooled;
    public ArrayList<QuantitativeDataVariable> allTheQDVs;
    public Data_Manager dm;

    public Indep_t_Dialog indep_t_Dialog;
    private Indep_t_Model indep_t_Model;
    private Indep_t_SumStats_Model indep_t_SumStats_Model;
    public ReOrderStringDisplay_Dialog reorderStringDisplay_Dialog;
    public Indep_t_Dashboard indep_t_Dashboard;
    public Indep_t_Dialog Indep_t_Dialog;
    private Indep_t_SumStats_Dashboard indep_t_SumStats_Dashboard;
    private Indep_t_SumStats_Dialog ind_t_SumStats_Dialog;   
    private Indep_t_Tidy_Dialog indep_t_Tidy_Dialog; // Choose Tidy/TI8x/Quant

    BBSL_Model bbsl_Model;
    ArrayList<ColumnOfData> alTwoVariables;
    DotPlot_2Ind_Model dotPlot_Indep_t_Model;
    HorizontalBoxPlot_Model hBox_Model;
    QQPlot_Model qqPlot_Model;
    VerticalBoxPlot_Model vBox_Model;

    // My classes
    public CatQuantDataVariable cqdv;
    public QuantitativeDataVariable tempQDV;
    public ArrayList<QuantitativeDataVariable> incomingQDVs;  
    public MyYesNoAlerts myYesNoAlerts;
    
    public Indep_t_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(76, waldoFile, " *** Constructing");
        indep_t_ColsOfData = new ArrayList();
        TWO = 2;
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();
        strReturnStatus = "";
        strIndepDlgReturnStatusX = "";
        strIndepDlgReturnStatusY = "";
    }
    
    // Called from Main Menu
    public String doTidyOrNot() {
        dm.whereIsWaldo(88, waldoFile, " --- doTidyOrNot()");
        rawDataOrSummary = dm.getRawOrSummary();
        dm.whereIsWaldo(90, waldoFile, " --- doTidyOrNot(), rawOrSummary = " + rawDataOrSummary);
        if (rawDataOrSummary.equals("NULL")) {
        if (printTheStuff) {
            System.out.println("--- 93 Indep_t_Controller, rawDataOrSummary.equals(NULL");
        }
            myYesNoAlerts.setTheYes("Raw Data");
            myYesNoAlerts.setTheNo("Data Summary");
            myYesNoAlerts.showRawDataOrSummaryAlert();
            rawDataOrSummary = myYesNoAlerts.getYesOrNo();
            if (rawDataOrSummary == null) {
                if (printTheStuff) {
                    System.out.println("--- 101 Indep_t_Controller, rawDataOrSummary == null");
                }
                return "Cancel";
            }
        }
        dm.whereIsWaldo(106, waldoFile, " --- doTidyOrNot(), rawDataOrSummary = " + rawDataOrSummary);

        if (rawDataOrSummary.equals("No")) { // Summary statistics already calculated
            if (printTheStuff) {
                System.out.println("--- 110 Indep_t_Controller, rawDataOrSummary.equals(No)");
            }
            dm.setRawOrSummary("Summary");
            doTheSumStatsProcedure();
        } else {
            if (printTheStuff) {
                System.out.println("--- 116 Indep_t_Controller, rawDataOrSummary = " + rawDataOrSummary);
            }
            dm.setRawOrSummary("Raw");
            //  Check for existing value ( = not NULL)
            tidyOrTI8x = dm.getTIorTIDY();
            if (printTheStuff) {
                System.out.println("--- 122 Indep_t_Controller, tidyOrTI8x = " + tidyOrTI8x);
            }
            if (tidyOrTI8x.equals("NULL")) {
                myYesNoAlerts.setTheYes("Tidy");
                myYesNoAlerts.setTheNo("TI8x");
                myYesNoAlerts.showTidyOrTI8xAlert();
                // Get the Alert Yes/No = 'Yes' or 'No' and re-cast tidyOrTI8x
                tidyOrTI8x = myYesNoAlerts.getYesOrNo();
            if (printTheStuff) {
                System.out.println("--- 131 Indep_t_Controller, tidyOrTI8x = " + tidyOrTI8x);
            }
                if (tidyOrTI8x == null) { return "Cancel"; }
            }

            //              First time through                 Repeat
            if (printTheStuff) {
                System.out.println("--- 138 Indep_t_Controller, tidyOrTI8x = " + tidyOrTI8x);
            }
            if (tidyOrTI8x.equals("Yes") || tidyOrTI8x.equals("Tidy")) { // = Tidy
                tidyOrTI8x = "Tidy";
                dm.setTIorTIDY("Tidy");
                strReturnStatus = doTidy(); 
                if (printTheStuff) {
                    System.out.println("--- 145 Indep_t_Controller, strReturnStatus = " + strReturnStatus);
                }
                if (!strIndepDlgReturnStatusX.equals("OK") || !strIndepDlgReturnStatusX.equals("OK")) {
                    if (printTheStuff) {
                        System.out.println("--- 149 Indep_t_Controller, strReturnStatus = " + strReturnStatus);
                    }
                    strReturnStatus = "Cancel"; 
                    return strReturnStatus;
                }
            } else {    // No = TI8x
                tidyOrTI8x = "TI8x";
                dm.setTIorTIDY("TI8x");
                strReturnStatus =  doTI8x();
                if (printTheStuff) {
                    System.out.println("--- 159 Indep_t_Controller, strReturnStatus = " + strReturnStatus);
                }
                if (strReturnStatus.equals("Cancel")) { 
                    dm.setRawOrSummary("NULL");
                    return "Cancel"; }
                if (!strIndepDlgReturnStatusX.equals("OK") || !strIndepDlgReturnStatusX.equals("OK")) {
                    dm.setRawOrSummary("NULL");
                    strReturnStatus = "Cancel"; }
            }  
        }
        if (printTheStuff) {
            System.out.println("--- 170 Indep_t_Controller, strReturnStatus = " + strReturnStatus);
        }
        return strReturnStatus;
    }
    
    protected String doTI8x() {
        dm.whereIsWaldo(176, waldoFile, " --- doTI8x()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }

        indep_t_Dialog = new Indep_t_Dialog(dm, "Indep_t_ti8x");
        indep_t_Dialog.showAndWait();
        
        strReturnStatus = indep_t_Dialog.getStrReturnStatus();
        strIndepDlgReturnStatusX = indep_t_Dialog.getReturnStatusX();
        strIndepDlgReturnStatusY = indep_t_Dialog.getReturnStatusY();
        if (printTheStuff) {
            System.out.println("--- 193 Indep_t_Controller, strReturnStatus = " + strReturnStatus);
            System.out.println("--- 194 Indep_t_Controller, strIndepDlgReturnStatusX = " + strIndepDlgReturnStatusX);
            System.out.println("--- 195 Indep_t_Controller, strIndepDlgReturnStatusY = " + strIndepDlgReturnStatusY);
        }
        
        if (strReturnStatus == null) { strReturnStatus = "Cancel"; }
        if (strIndepDlgReturnStatusX == null) { strIndepDlgReturnStatusX = "Cancel"; }
        if (strIndepDlgReturnStatusY == null) { strIndepDlgReturnStatusY = "Cancel"; }

        if (!strReturnStatus.equals("OK") 
                || !strIndepDlgReturnStatusX.equals("OK")
                || ! strIndepDlgReturnStatusY.equals("OK")) {
            return "Cancel";
        }
       
        strThisVarLabel = "All";
        strThisVarDescr = "All";
        
        ArrayList<String> alStrPooledData = new ArrayList<>();
        alTwoVariables = new ArrayList<>();
        alTwoVariables = indep_t_Dialog.getData();
        
        strFirstVarDescr = alTwoVariables.get(0).getVarLabel();
        strSecondVarDescr = alTwoVariables.get(1).getVarLabel();
        strSubTitle_And = strFirstVarDescr  + " & " + strSecondVarDescr;  
        strSubTitle_Vs = strFirstVarDescr  + " vs " + strSecondVarDescr;
        
        nColumnsOfData = alTwoVariables.size();
        
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            ColumnOfData tempCol = alTwoVariables.get(ith);
            int nColSize = tempCol.getColumnSize();
            
            for (int jth = 0; jth < nColSize; jth++) {
                alStrPooledData.add(tempCol.getTheCases_ArrayList().get(jth));
            }
        }

        ColumnOfData tempCOfD = new ColumnOfData(dm, strThisVarLabel, strThisVarDescr, alStrPooledData);
        qdv_Pooled = new QuantitativeDataVariable(strThisVarLabel, strThisVarDescr,tempCOfD);    

        goodToGo = strReturnStatus.equals("OK"); 
        if (!goodToGo) { return "Cancel"; }
        // else...

        if (nColumnsOfData == 0) { 
            goodToGo = false;            
            return "Cancel"; 
        }
 
        if (nColumnsOfData != TWO) {
            MyAlerts.showExplore2Ind_NE2_LevelsAlert();
            goodToGo = false;
            return "Cancel";
        }

        // else...
        indep_t_ColsOfData = indep_t_Dialog.getData();
        incomingQDVs = new ArrayList();

        for (int ith = 0; ith < nColumnsOfData; ith++) {
            strThisVarLabel = indep_t_ColsOfData.get(ith).getVarLabel();
            strThisVarDescr = indep_t_ColsOfData.get(ith).getVarDescription();
            tempQDV = new QuantitativeDataVariable(strThisVarLabel, strThisVarDescr, indep_t_ColsOfData.get(ith));  
            incomingQDVs.add(tempQDV);                 
        } 
        n_QDVs = incomingQDVs.size();
        strReturnStatus = askAboutReOrdering();
        if (strReturnStatus.equals("Cancel")) {return "Cancel"; }
        doTheIndep_t();

        dm.whereIsWaldo(264, waldoFile, " --- END doTI8x()");
        dm.setRawOrSummary("NULL"); //-------------------------------------
        return strReturnStatus;
    }
    
    protected String doTidy() {
        dm.whereIsWaldo(270, waldoFile, " --- doTidy()");
        strReturnStatus = "OK";
        strIndepDlgReturnStatusX = "OK";
        strIndepDlgReturnStatusX = "OK";
        dm.whereIsWaldo(274, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            dm.whereIsWaldo(281, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
            indep_t_Dialog = new Indep_t_Dialog(dm, "Indep_t_tidy");
            indep_t_Dialog.showAndWait();

            strReturnStatus = indep_t_Dialog.getStrReturnStatus();
            strIndepDlgReturnStatusX = indep_t_Dialog.getReturnStatusX();
            strIndepDlgReturnStatusY = indep_t_Dialog.getReturnStatusY();
            
            if (strReturnStatus == null) { strReturnStatus = "Cancel"; }
            if (strIndepDlgReturnStatusX == null) { strIndepDlgReturnStatusX = "Cancel"; }
            if (strIndepDlgReturnStatusY == null) { strIndepDlgReturnStatusY = "Cancel"; }

            if (!strReturnStatus.equals("OK") 
                    || !strIndepDlgReturnStatusX.equals("OK")
                    || ! strIndepDlgReturnStatusY.equals("OK")) {
                return "Cancel";
            }
            
            indep_t_ColsOfData = indep_t_Dialog.getData();
            int nLevels = indep_t_ColsOfData.get(0).getNumberOfDistinctValues();
            if (nLevels != TWO) {
                MyAlerts.showExplore2Ind_NE2_LevelsAlert();
                goodToGo = false;
                return "Cancel";
            }
            dm.whereIsWaldo(306, waldoFile, " --- doTidy()");
            dm.whereIsWaldo(307, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
            checkForLegalChoices = validateTidyChoices();
        } while (!checkForLegalChoices);

        //                                Categorical,             Quantitative            return All and individuals
        dm.whereIsWaldo(312, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
        cqdv = new CatQuantDataVariable(dm, indep_t_ColsOfData.get(0), indep_t_ColsOfData.get(1), true, "ANOVA1_Cat_Controller"); 
        dm.whereIsWaldo(314, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
        strReturnStatus = cqdv.finishConstructingTidy();

        if(strReturnStatus.equals("OK")) { 
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            /******************************************************
             *  The qdv_Pooled is a qdv of the pooled allTheQDVs  *
             *****************************************************/
            qdv_Pooled = allTheQDVs.get(0);
            allTheQDVs.remove(0);   // Dump the All qdv
            n_QDVs = allTheQDVs.size();
            
            strFirstVarDescr = allTheQDVs.get(0).getTheVarLabel();
            strSecondVarDescr = allTheQDVs.get(1).getTheVarLabel();
            strSubTitle_And = strFirstVarDescr  + " & " + strSecondVarDescr;  
            strSubTitle_Vs = strFirstVarDescr  + " vs " + strSecondVarDescr;            
            collectAllTheLabels();
            doTheIndep_t();
            dm.whereIsWaldo(333, waldoFile, " --- END doTidy()");
            dm.setRawOrSummary("NULL"); //-------------------------------------
            return "OK";
        }
        dm.whereIsWaldo(337, waldoFile, " --- END doTidy()");
        dm.setRawOrSummary("NULL"); //-------------------------------------
        return "Cancel";
    }
    
    public String doTheSumStatsProcedure() {
        dm.whereIsWaldo(343, waldoFile, " --- doTheSumStatsProcedure()");        
        String doItAgain = "Yes";
        while (doItAgain.equals("Yes")) {
            strReturnStatus = "Cancel";
            ind_t_SumStats_Dialog = new Indep_t_SumStats_Dialog();
            strReturnStatus = ind_t_SumStats_Dialog.getStrReturnStatus();
            if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
            indep_t_SumStats_Model = new Indep_t_SumStats_Model(this, ind_t_SumStats_Dialog);
            indep_t_SumStats_Model.doIndepTAnalysis();
            indep_t_SumStats_Dashboard = new Indep_t_SumStats_Dashboard(this);
            showTheSumStatsDashboard();
            myYesNoAlerts.setTheYes("Sure!");
            myYesNoAlerts.setTheNo("Thank you, no");
            myYesNoAlerts.showAvoidRepetitiousClicksAlert();
            doItAgain = myYesNoAlerts.getYesOrNo();
        }
        dm.setRawOrSummary("NULL"); //-------------------------------------
        return strReturnStatus;
    } 
    
     public String showTheSumStatsDashboard() {
        dm.whereIsWaldo(364, waldoFile, " --- showTheSumStatsDashboard()"); 
        indep_t_SumStats_Dashboard.populateTheBackGround();
        indep_t_SumStats_Dashboard.putEmAllUp();
        indep_t_SumStats_Dashboard.showAndWait();
        strReturnStatus = indep_t_SumStats_Dashboard.getStrReturnStatus();
        strReturnStatus = "Ok";
        return strReturnStatus;           
    } 
    
    protected boolean doTheIndep_t() {
        dm.whereIsWaldo(374, waldoFile, " --- doTheIndep_t()");
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        indep_t_Model = new Indep_t_Model(this, strFirstVarDescr, strSecondVarDescr, allTheQDVs);
        String indepTOK = indep_t_Model.doIndepTAnalysis();
        if (!indepTOK.equals("OK")) { return false;  }
        
        /***************************************************************
        * allTheQDVs here are original all with pooled separated out   *
        ***************************************************************/
        hBox_Model = new HorizontalBoxPlot_Model(this, strSubTitle_And, allTheQDVs);
        vBox_Model = new VerticalBoxPlot_Model(this, strSubTitle_And, allTheQDVs);
        qqPlot_Model = new QQPlot_Model(this, strSubTitle_And, allTheQDVs);
        bbsl_Model = new BBSL_Model(this, qdv_Pooled, allTheQDVs);
        dotPlot_Indep_t_Model = new DotPlot_2Ind_Model(this, strSubTitle_And, allTheQDVs);      
        dm.whereIsWaldo(385, waldoFile, " --- doTheIndep_t()");
  
        indep_t_Dashboard = new Indep_t_Dashboard(this, indep_t_Model);
        indep_t_Dashboard.populateTheBackGround();
        indep_t_Dashboard.putEmAllUp();
        indep_t_Dashboard.showAndWait();
        strReturnStatus = indep_t_Dashboard.getStrReturnStatus();
        strReturnStatus = "OK";
        dm.whereIsWaldo(400, waldoFile, " --- END doTheIndep_t()");
        return true;        
    } 
       
    private String askAboutReOrdering() {
        dm.whereIsWaldo(405, waldoFile, "  --- askAboutReOrdering()");
        n_QDVs = incomingQDVs.size();
        theNewOrder = new int[n_QDVs];
        // Default
        for (int ithQDV= 0; ithQDV < n_QDVs; ithQDV++) {
            theNewOrder[ithQDV] = ithQDV;
        }
        incomingLabels = new String[n_QDVs];
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            incomingLabels[iVars] = incomingQDVs.get(iVars).getTheVarLabel();
        }        
        
        reorderStringDisplay_Dialog = new ReOrderStringDisplay_Dialog(this, incomingLabels);
        reorderStringDisplay_Dialog.showAndWait();
        strReturnStatus = reorderStringDisplay_Dialog.getStrReturnStatus();
        if (strReturnStatus.equals("Cancel")) { return "Cancel"; }

        allTheQDVs = new ArrayList<>();
        for (int ithQDV = 0; ithQDV < n_QDVs; ithQDV++) {
            allTheQDVs.add(incomingQDVs.get(theNewOrder[ithQDV]));
        }

        collectAllTheLabels(); 
        return strReturnStatus;
    }
    
    private void collectAllTheLabels() {
        dm.whereIsWaldo(432, waldoFile, "  --- collectAllTheLabels()");
        categoryLabels = FXCollections.observableArrayList();         
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            categoryLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
    }
    
    public void copyTheReOrder(int[] returnedOrder) {
        dm.whereIsWaldo(440, waldoFile, "  --- copyTheReOrder()");
        System.arraycopy(returnedOrder, 0, theNewOrder, 0, n_QDVs);
        reorderStringDisplay_Dialog.close();
    } 
    
    private boolean validateTidyChoices() {
        dm.whereIsWaldo(446, waldoFile, " --- validateTidyChoices()");
        theStrIsNumeric = new boolean[TWO];        
        for (int ithCol = 0; ithCol < TWO; ithCol++){
            theStrIsNumeric[ithCol] = indep_t_ColsOfData.get(ithCol).getDataType().equals("Quantitative");  
        }
        return true;
    }
    
    public void setTidyOrTI8x(String toThis) { tidyOrTI8x = toThis; }
    public String getSubTitleAnd() { return strSubTitle_And; }    
    public String getSubTitleVs() { return strSubTitle_Vs; }   
    public String getFirstVarDescr() { return strFirstVarDescr; }
    public String getSecondVarDescr() { return strSecondVarDescr; }
    
    public Indep_t_Model getIndepTModel() {  return indep_t_Model; } 
    
    public Indep_t_Dialog getIndepTDialog() { return indep_t_Dialog; }
    public HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    public DotPlot_2Ind_Model get_2Ind_Dot_Model() { return dotPlot_Indep_t_Model; }
    public QQPlot_Model getQQ_Model() { return qqPlot_Model; }
    public VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    public BBSL_Model getBBSL_Model() { return bbsl_Model; }
    public Indep_t_SumStats_Model getIndep_t_SumStats_Model() {
        return indep_t_SumStats_Model;
    }
    public ObservableList <String> getCategoryLabels() {
        return categoryLabels; 
    }
    public Data_Manager getDataManager() {return dm; }
}
