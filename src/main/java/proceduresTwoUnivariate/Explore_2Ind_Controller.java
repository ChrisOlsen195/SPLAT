/**************************************************
 *             Explore_2Ind_Controller            *
 *                    03/05/25                    *
 *                     15:00                      *
 *************************************************/
package proceduresTwoUnivariate;

import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import dialogs.*;
import javafx.collections.FXCollections;
import splat.*;
import utilityClasses.MyAlerts;
import dialogs.ReOrderStringDisplay_Dialog;
import javafx.collections.ObservableList;
import utilityClasses.MyYesNoAlerts;

public class Explore_2Ind_Controller {
    //  POJOs
    public boolean goodToGo, checkForLegalChoices;
    boolean[] theStrIsNumeric;
    public int n_QDVs, TWO, nColumnsOfData; 
    public int[] theNewOrder;
    String thisVarLabel, thisVarDescr, strReturnStatus, subTitle_And, subTitle_Vs,
           tidyOrTI8x, firstVarDescr, secondVarDescr;
    String[] incomingLabels;
    public ArrayList<String> allTheLabels, alStr_AllTheLabels;
    
    // Make empty if no-print
    //String waldoFile = "Explore_2Ind_Controller";
    String waldoFile = "";
    
    // My classes
    public ArrayList<ColumnOfData> explore_2Ind_ColsOfData;
    public CatQuantDataVariable catQuantVar;
    public ObservableList<String> categoryLabels;
    public QuantitativeDataVariable qdv_Pooled;
    public ArrayList<QuantitativeDataVariable> allTheQDVs;
    public Data_Manager dm;

    private Explore_2Ind_Model explore_2Ind_Model;
    public ReOrderStringDisplay_Dialog reorderStringDisplay_Dialog;
    private Explore_2Ind_Dashboard explore_2Ind_Dashboard;
    Explore_2Ind_TI8x_Dialog explore_2Ind_TI8x_Dialog;
    Explore_2Ind_Tidy_Dialog explore_2Ind_Tidy_Dialog;
    
    BBSL_Model bbsl_Model;
    ArrayList<ColumnOfData> catQuantColumns;
    DotPlot_2Ind_Model dotPlot_2Ind_Model;
    HorizontalBoxPlot_Model hBox_Model;
    QQPlot_Model qqPlot_Model;
    VerticalBoxPlot_Model vBox_Model;

    // My classes
    public CatQuantDataVariable cqdv;
    public QuantitativeDataVariable tempQDV;
    public ArrayList<QuantitativeDataVariable> incomingQDVs;  
    public MyYesNoAlerts myYesNoAlerts;
    
    public Explore_2Ind_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(66, waldoFile, " *** Constructing");
        explore_2Ind_ColsOfData = new ArrayList();
        TWO = 2;
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();
    }
    
    // Called from MainMenu
    public String doTidyOrTI8x() {
        myYesNoAlerts.setTheYes("Tidy");
        myYesNoAlerts.setTheNo("TI8x");
        myYesNoAlerts.showTidyOrTI8xAlert();
        tidyOrTI8x = myYesNoAlerts.getYesOrNo();
        if (tidyOrTI8x == null) { return "Cancel"; }
        if (tidyOrTI8x.equals("Yes")) { 
            strReturnStatus = doTidy(); }
            dm.whereIsWaldo(82, waldoFile, " --- strReturnStatus = " + strReturnStatus);
            if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
        if (tidyOrTI8x.equals("No")) { 
            doTI8x(); } 
        return "OK";
    }
    
    protected String doTI8x() {
        dm.whereIsWaldo(90, waldoFile, " --- doTI8x()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        explore_2Ind_TI8x_Dialog = new Explore_2Ind_TI8x_Dialog( dm );
        strReturnStatus = explore_2Ind_TI8x_Dialog.getStrReturnStatus();
        dm.whereIsWaldo(101, waldoFile, " --- strReturnStatus = " + strReturnStatus);
        if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
        
        // Stack the columns into one column for the BBSL procedure
        // the 2nd and third colums are passed to the other procedures
        thisVarLabel = "All";
        thisVarDescr = "All";
        ArrayList<String> alStrPooledData = new ArrayList<>();
        nColumnsOfData = explore_2Ind_TI8x_Dialog.getNLevels();
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            ColumnOfData tempCol = explore_2Ind_TI8x_Dialog.getIthColumnOfData(ith);
            int nColSize = tempCol.getColumnSize();
            
            for (int jth = 0; jth < nColSize; jth++) {
                alStrPooledData.add(tempCol.getTheCases_ArrayList().get(jth));
            }
        }
        
        ColumnOfData tempCOfD = new ColumnOfData(dm, thisVarLabel, thisVarDescr, alStrPooledData);
        qdv_Pooled = new QuantitativeDataVariable(thisVarLabel, thisVarDescr,tempCOfD);    
        dm.whereIsWaldo(121, waldoFile, " --- strReturnStatus = " + strReturnStatus);
        if (!strReturnStatus.equals("OK")) { return "Cancel"; }
        // else...
        firstVarDescr = explore_2Ind_TI8x_Dialog.getFirstVariable();
        secondVarDescr = explore_2Ind_TI8x_Dialog.getSecondVariable();
        subTitle_And = firstVarDescr  + " & " + secondVarDescr;  
        subTitle_Vs = firstVarDescr  + " vs " + secondVarDescr;
            
        if (nColumnsOfData == 0) { 
            goodToGo = false;
            strReturnStatus = "Cancel";            
            return "Cancel"; 
        }
 
        if (nColumnsOfData != TWO) {
            MyAlerts.showExplore2Ind_NE2_LevelsAlert();
            goodToGo = false;
            return "Cancel";
        }
        dm.whereIsWaldo(140, waldoFile, " --- strReturnStatus = " + strReturnStatus);
        // else...
        explore_2Ind_ColsOfData = explore_2Ind_TI8x_Dialog.getData();
        incomingQDVs = new ArrayList();

        for (int ith = 0; ith < nColumnsOfData; ith++) {
            thisVarLabel = explore_2Ind_ColsOfData.get(ith).getVarLabel();
            thisVarDescr = explore_2Ind_ColsOfData.get(ith).getVarDescription();
            tempQDV = new QuantitativeDataVariable(thisVarLabel, thisVarDescr, explore_2Ind_ColsOfData.get(ith));  
            incomingQDVs.add(tempQDV);                 
        } 
        n_QDVs = incomingQDVs.size();
        strReturnStatus = askAboutReOrdering();
        if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
        doTheExplore2Ind();

        dm.whereIsWaldo(156, waldoFile, " --- END doTI8x()");
        return strReturnStatus;
    }
    
    protected String doTidy() {
        dm.whereIsWaldo(161, waldoFile, " --- doTidy()");
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            explore_2Ind_Tidy_Dialog = new Explore_2Ind_Tidy_Dialog( dm, "Categorical" );
            explore_2Ind_Tidy_Dialog.showAndWait();
            strReturnStatus = explore_2Ind_Tidy_Dialog.getStrReturnStatus();
            if (strReturnStatus.equals("Cancel")) {return "Cancel"; }
            goodToGo = explore_2Ind_Tidy_Dialog.getGoodToGO();
            
            if (!goodToGo) {
                strReturnStatus = explore_2Ind_Tidy_Dialog.getStrReturnStatus();
                return "Cancel";
            }
            explore_2Ind_ColsOfData = explore_2Ind_Tidy_Dialog.getData();
            
            int nLevels = explore_2Ind_ColsOfData.get(0).getNumberOfDistinctValues();
            if (nLevels != TWO) {
                MyAlerts.showExplore2Ind_NE2_LevelsAlert();
                goodToGo = false;
                return "Cancel";
            }
            
            checkForLegalChoices = validateTidyChoices();
        } while (!checkForLegalChoices);

        //                                Categorical,             Quantitative            return All and individuals
        cqdv = new CatQuantDataVariable(dm, explore_2Ind_ColsOfData.get(0), explore_2Ind_ColsOfData.get(1), true, "ANOVA1_Cat_Controller");   
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

            firstVarDescr = allTheQDVs.get(0).getTheVarLabel();
            secondVarDescr = allTheQDVs.get(1).getTheVarLabel();
            subTitle_And = firstVarDescr  + " & " + secondVarDescr;  
            subTitle_Vs = firstVarDescr  + " vs " + secondVarDescr;        

            collectAllTheLabels();
            doTheExplore2Ind();
            dm.whereIsWaldo(214, waldoFile, " --- END doTidy()");
            return "OK";
        }
        dm.whereIsWaldo(217, waldoFile, " --- END doTidy()");
        return "Cancel";
    }
    
    protected boolean doTheExplore2Ind() {
        dm.whereIsWaldo(222, waldoFile, " --- doTheExplore2Ind()");
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        explore_2Ind_Model = new Explore_2Ind_Model(this, firstVarDescr, secondVarDescr, allTheQDVs);
        String explore2IndOK = explore_2Ind_Model.continueInitializing();
        if (!explore2IndOK.equals("OK")) { return false;  }
        
        /***************************************************************
        * allTheQDVs here are original all with pooled separated out   *
        ***************************************************************/
        hBox_Model = new HorizontalBoxPlot_Model(this, subTitle_And, allTheQDVs);
        vBox_Model = new VerticalBoxPlot_Model(this, subTitle_And, allTheQDVs);
        qqPlot_Model = new QQPlot_Model(this, allTheQDVs);
        bbsl_Model = new BBSL_Model(this, qdv_Pooled, allTheQDVs);
        dotPlot_2Ind_Model = new DotPlot_2Ind_Model(this, subTitle_And, allTheQDVs);      
        
        explore_2Ind_Dashboard = new Explore_2Ind_Dashboard(this, explore_2Ind_Model);
        explore_2Ind_Dashboard.populateTheBackGround();
        explore_2Ind_Dashboard.putEmAllUp();
        explore_2Ind_Dashboard.showAndWait();
        strReturnStatus = explore_2Ind_Dashboard.getStrReturnStatus();
        strReturnStatus = "OK";
        dm.whereIsWaldo(247, waldoFile, " END doTheExplore2Ind()");
        return true;        
    } 
       
    private String askAboutReOrdering() {
        dm.whereIsWaldo(252, waldoFile, "  --- askAboutReOrdering()");
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
        return "OK";
    }
    
    private void collectAllTheLabels() {
        dm.whereIsWaldo(278, waldoFile, "  --- collectAllTheLabels()");
        categoryLabels = FXCollections.observableArrayList();         
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            String tempVarLabel = allTheQDVs.get(iVars).getTheVarLabel();
            categoryLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
    }
    
    public void copyTheReOrder(int[] returnedOrder) {
        System.arraycopy(returnedOrder, 0, theNewOrder, 0, n_QDVs);
        reorderStringDisplay_Dialog.close();
    } 
    
    private boolean validateTidyChoices() {
        dm.whereIsWaldo(292, waldoFile, " --- validateStackChoices()");
        theStrIsNumeric = new boolean[TWO];        
        for (int ithCol = 0; ithCol < TWO; ithCol++){
            theStrIsNumeric[ithCol] = explore_2Ind_ColsOfData.get(ithCol).getDataType().equals("Quantitative");  
        }
        return true;
    }
    
    public void setTidyOrTI8x(String toThis) { tidyOrTI8x = toThis; }
    public String getSubTitleAnd() { return subTitle_And; }    
    public String getSubTitleVs() { return subTitle_Vs; }   
    public String getFirstVarDescr() { return firstVarDescr; }
    public String getSecondVarDescr() { return secondVarDescr; }
    public Explore_2Ind_Model getThe2IndModel() { return explore_2Ind_Model; }    
    public HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    public DotPlot_2Ind_Model get_2Ind_Dot_Model() { return dotPlot_2Ind_Model; }
    public QQPlot_Model getQQ_Model() { return qqPlot_Model; }
    public VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    public BBSL_Model getBBSL_Model() { return bbsl_Model; }
    public ObservableList <String> getCategoryLabels() {return categoryLabels; }
    public Data_Manager getDataManager() {return dm; }
}