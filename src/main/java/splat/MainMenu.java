/************************************************************
 *                       Splat_MainMenu                     *
 *                          12/13/25                        *
 *                            15:00                         *
 ***********************************************************/
package splat;

import bivariateProcedures_Categorical.BivCat_Controller;
import power_OneMean.OneMean_Power_Controller;
import anova2.ANOVA2_RCB_Controller;
import anova1.quantitative.ANOVA1_Quant_Controller;
import anova1.categorical.ANOVA1_Cat_Controller;
import anova2.ANCOVA_Controller;
import anova2.ANOVA2_RM_Controller;
import simpleLogisticRegression.Logistic_Controller;
import proceduresManyUnivariate.MultUni_Controller;
import chiSquare_Assoc.X2Assoc_Controller;
import chiSquare.GOF.X2GOF_Controller;
import multipleRegression.MultReg_Controller;
import simpleRegression.*;
import genericClasses.Transformations_GUI;
import proceduresOneUnivariate.Univ_Quant_Controller;
import javafx.application.Application;
import javafx.event.*;
import proceduresTwoUnivariate.*;
import chiSquare.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import noInterceptRegression.NoIntercept_Regr_Controller;
import the_t_procedures.*;
import the_z_procedures.*;
import quadraticRegression.*;
import power_OneProp.OneProp_Power_Controller;
import probabilityCalculators.*;
import univariateProcedures_Categorical.UnivCat_Controller;
import utilityClasses.MyAlerts;
import power_twomeans.*;
import power_twoprops.IndepProps_Power_Controller;
import randomAssignment.RandomAssignment_Controller;
import utilityClasses.PrintExceptionInfo;
import visual_probability.ProbText_FullMonte;
import visual_probability.Table_FullMonte;
import visual_probability.Tree_FullMonte;
import visual_probability.Venn_FullMonte;
import utilityClasses.*;
import bootstrapping.*;
import epidemiologyProcedures.Epi_Controller;

public class MainMenu extends MenuBar {
    //  POJOs
    boolean dataExists;
    
    String procedure, strReturnStatus;
    final String ESCAPE = "ESCAPE";
    final String EXPERIMENT = "EXPERIMENT";
    final String GOF = "GOF";
    final String INDEPENDENCE = "INDEPENDENCE";
    final String HOMOGENEITY = "HOMOGENEITY";
    
        //  Make empty if no-print
    //String waldoFile = "MainMenu";
    String waldoFile = "";
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
    Data_Manager dm;
    File_Ops myFileOps;
    Edit_Ops myEditOps;
    Label fileLabel;

    public MainMenu(Application splat, Data_Manager dm, Label fileLabel) {
        dm.whereIsWaldo(79, waldoFile, " *** Constructing");
        this.fileLabel = fileLabel;
        this.dm = dm;
        dm.setMainMenu(this);
        myFileOps = new File_Ops(this.dm);
        myEditOps = new Edit_Ops(this.dm);
        Menu fileMenu = new Menu("File");
        MenuItem clearData = new MenuItem("Clear Data");
        MenuItem openFile = new MenuItem("Open File");
        MenuItem saveData = new MenuItem("Save Data");
        MenuItem saveDataAs = new MenuItem("Save Data As");
        MenuItem printFile = new MenuItem("Print file");
        MenuItem exitProgram = new MenuItem("Exit");
        fileMenu.getItems().addAll(clearData, 
                                   openFile, 
                                   saveData,
                                   saveDataAs, 
                                   printFile,
                                   exitProgram);

        Menu editMenu = new Menu("Edit Ops");
        MenuItem insertCol = new MenuItem("Insert a Column");
        MenuItem deleteColumn = new MenuItem("Delete a Column");
        MenuItem insertRow = new MenuItem("Insert a Row");
        MenuItem deleteRow = new MenuItem("Delete a Row");

        MenuItem cleanColData = new MenuItem("Clean Column data");

        editMenu.getItems().addAll(insertCol, 
                                   deleteColumn,
                                   insertRow, 
                                   deleteRow, 
                                   cleanColData);

        Menu dataMenu = new Menu("DataOps");
        MenuItem linTransOfVar = new MenuItem("Linear transformation");
        MenuItem nonlinTransOfVar = new MenuItem("Nonlinear transformations");
        MenuItem linearCombOfVars = new MenuItem("Linear combination");
        MenuItem unaryOperationsWithVars = new MenuItem("Unary operations");
        MenuItem binaryOperationsWithVars = new MenuItem("Binary operations");
        dataMenu.getItems().addAll(linTransOfVar,
                                   nonlinTransOfVar,
                                   linearCombOfVars,
                                   unaryOperationsWithVars, 
                                   binaryOperationsWithVars);
        
        Menu dataExplorationMenu = new Menu("Explore data");
        Menu exploreUnivariateData = new Menu("Univariate data");
        
        MenuItem aUnivQuantVariable = new MenuItem("A single quantitative variable");
        MenuItem aUnivCategoricalVariable = new MenuItem("A single categorical variable");
        MenuItem compareTwoDistributions = new MenuItem("Compare Exactly Two Quantitative distributions");
        MenuItem compareManyDistributions = new MenuItem("Compare Two or More Quantitative distributions"); // 394
        exploreUnivariateData.getItems().addAll(aUnivQuantVariable,
                                                aUnivCategoricalVariable,
                                                compareTwoDistributions,
                                                compareManyDistributions);  
        
        Menu exploreBivariateData = new Menu("Bivariate data");
        MenuItem noInf_Regression = new MenuItem("Linear regression");
        MenuItem regression_Compare = new MenuItem("Compare regressions");

        MenuItem quadraticRegression = new MenuItem("Quadratic regression");
        
        Menu bivariateCategorical = new Menu("Bivariate categorical data");
        MenuItem bivCatRawCounts = new MenuItem("I will enter m x n data in a table");
        MenuItem bivCatFileData = new MenuItem("The m x n data are in the current file");
        bivariateCategorical.getItems().addAll(bivCatRawCounts,
                                        bivCatFileData);
        
        Menu epidemiology_2x2 = new Menu("Epidemiology: 2 x 2 categories");
        MenuItem epidemiology_RawCounts = new MenuItem("I will enter 2 x 2 data in a table");
        MenuItem epidemiology_FileData = new MenuItem("The 2 x 2 data are in the current file");
        epidemiology_2x2.getItems().addAll(epidemiology_RawCounts,
                                        epidemiology_FileData);              
        
        exploreBivariateData.getItems().addAll(noInf_Regression,
                                               regression_Compare,

                                               quadraticRegression,
                                               bivariateCategorical,
                                               epidemiology_2x2);

        dataExplorationMenu.getItems().addAll(exploreUnivariateData, 
                                    exploreBivariateData);

        Menu planningAStudyMenu = new Menu("Planning");
        MenuItem randomAssignmentCRD = new MenuItem("Random Assign (CRD)");
        MenuItem randomAssignmentRBD = new MenuItem("Random Assign (RCBD");
        MenuItem powerSingleMean = new MenuItem("Power: single mean");
        MenuItem powerTwoMeans = new MenuItem("Power: ind means");
        MenuItem powerSingleProp = new MenuItem("Power: single prop");
        MenuItem powerTwoProps = new MenuItem("Power: two props");
        planningAStudyMenu.getItems().addAll(randomAssignmentCRD,
                                    randomAssignmentRBD,
                                    powerSingleMean, 
                                    powerTwoMeans,
                                    powerSingleProp,
                                    powerTwoProps);        

        Menu probabilityMenu = new Menu("Probability");
        MenuItem probDistCalculations = new MenuItem("Probability Distributions");
        MenuItem probVenn = new MenuItem("Venn diagram");
        MenuItem probTree = new MenuItem("Tree diagram");
        MenuItem probTable = new MenuItem("2 x 2 Table");
        MenuItem probCalculator = new MenuItem("ProbabilityCalculator");
        probabilityMenu.getItems().addAll(probDistCalculations,
                                          probVenn,
                                          probTree,
                                          probTable,
                                          probCalculator);
        
        Menu inference = new Menu("Inference");
        MenuItem singleProportion = new MenuItem("One proportion");
        MenuItem differenceInProportions = new MenuItem("Two proportions");
        MenuItem singleMean = new MenuItem("One mean");
        MenuItem pairedMean = new MenuItem("Paired mean");
        MenuItem independentMeans = new MenuItem("Independent means");
        MenuItem simpleLinearRegression = new MenuItem("Regression");        
        Menu chiSqrSubMenu = new Menu("Chi square");
        MenuItem chiSquareRawCounts = new MenuItem("I will enter data in a table");
        MenuItem chiSquareFileData = new MenuItem("The data are in the current file");
        chiSqrSubMenu.getItems().addAll(chiSquareRawCounts,
                                        chiSquareFileData);
        Menu bootstrap = new Menu("Bootstrapping");
        MenuItem bootOneVar = new MenuItem("Boot Generic one-var");
        MenuItem bootTwoVar = new MenuItem("Boot Generic two-var");
        MenuItem bootRegression = new MenuItem("Boot Regression");
        bootstrap.getItems().add(bootOneVar);
        bootstrap.getItems().add(bootTwoVar);
        bootstrap.getItems().add(bootRegression);
        inference.getItems().addAll(singleProportion, 
                                    differenceInProportions,
                                    singleMean,
                                    pairedMean,
                                    independentMeans,
                                    chiSqrSubMenu,
                                    simpleLinearRegression,
                                    bootstrap
        );         
        
        Menu baps = new Menu("BAPS");
        Menu anova = new Menu("Analysis of Variance");
        MenuItem anova_Cat_Data = new MenuItem("One-Factor ANOVA: "
                + "Categorical treatments");
        MenuItem anova_Quant_Data = new MenuItem("One-Factor ANOVA: "
                + "Quantitative treatments");
        MenuItem runRCBanova = new MenuItem("One-Factor ANOVA for "
                + "Randomized (Complete) Block Design");
        MenuItem runRManova = new MenuItem("One-Factor ANOVA for "
                + "Repeated Measures Design");
        MenuItem runCR2anova = new MenuItem("Two-Factor ANOVA for "
                + "Completely Randomized Design");
        anova.getItems().addAll(anova_Cat_Data, 
                                anova_Quant_Data, 
                                runRCBanova, 
                                runRManova,
                                runCR2anova);
        
        Menu advancedRegression = new Menu("Advanced regression");
        MenuItem multLinRegression = new MenuItem("Multiple Regression");
        MenuItem logisticRegression = new MenuItem("Logistic regression");
        MenuItem noInterceptSimpleRegression = new MenuItem("One parameter linear regression");
        advancedRegression.getItems().addAll(multLinRegression,
                                             logisticRegression,
                                             noInterceptSimpleRegression);  
        Menu analysisOfCovariance = new Menu("One-Factor Analysis of Covariance");
        baps.getItems().addAll(anova, advancedRegression, analysisOfCovariance);
        
        this.getMenus().addAll(fileMenu, 
                                editMenu, 
                                dataMenu, 
                                dataExplorationMenu,
                                planningAStudyMenu,
                                probabilityMenu,
                                inference,
                                baps
                                );

        clearData.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(257, waldoFile, "clearData.setOnAction((ActionEvent event)");
            dm.getDataGrid().goHome(); 
            myFileOps.ClearTable();
 
            fileLabel.setText("File: " + dm.getTheFile());
        });

        // **************************************************************
        // *                    File Menu                               *
        // **************************************************************        
        openFile.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(268, waldoFile, "openFile.setOnAction((ActionEvent event)");
            boolean yikesException = false;
            myFileOps.ClearTable();
            try {
                strReturnStatus = myFileOps.getDataFromFile(0);
            }
            catch(Exception ex) {
               dm.whereIsWaldo(275, waldoFile, "catch(Exception ex)");
               yikesException = true; 
               PrintExceptionInfo pei = new PrintExceptionInfo(ex, "FileMenu.openFile.setOnAction");
            } 
            if (yikesException) {
                dm.whereIsWaldo(280, waldoFile, "yikesException");
                fileLabel.setText("File: " + dm.getTheFile());
                dm.sendDataStructToGrid(0, 0);
                if (myFileOps.getDuplicateLabelsExist()) {
                    MyAlerts.showDuplicateLabelsInFileAlert();
                }
                dm.setDataAreClean(true);
            }
            dm.whereIsWaldo(288, waldoFile, "dm.getFileName() = " + dm.getTheFile());            
            fileLabel.setText("File: " + dm.getTheFile());
            CheckForDuplicateStrings check4DupLabels = new CheckForDuplicateStrings(dm.getVariableNames());
            String haveDups = check4DupLabels.CheckTheStrings();
            if (!haveDups.equals("OK")) { 
                MyAlerts.showDuplicateLabelsInFileAlert(); 
            }
        });
        
        saveData.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(298, waldoFile, "saveData.setOnAction((ActionEvent event)");
            myFileOps.SaveData(dm, false);
            dm.setDataAreClean(true);
            dm.whereIsWaldo(301, waldoFile, "dm.getFileName() = " + dm.getTheFile()); 
            fileLabel.setText("File: " + dm.getTheFile());
        });

        saveDataAs.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(306, waldoFile, "saveDataAs.setOnAction((ActionEvent event)");
            myFileOps.SaveData(dm, true);
            dm.setDataAreClean(true);
            fileLabel.setText("File: " + dm.getTheFile());
        });
        
        printFile.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(313, waldoFile, "saveDataAs.setOnAction((ActionEvent event)");
            myFileOps.PrintFile(dm);
        });

        exitProgram.setOnAction((ActionEvent event) -> {
            dm.whereIsWaldo(318, waldoFile, "exitProgram.setOnAction((ActionEvent event)");
            myFileOps.ExitProgram(dm);
        });

        // **************************************************************
        // *                    Edit Menu                               *
        // **************************************************************        
        insertRow.setOnAction((ActionEvent event) -> {
            if (checkForExistenceOfData())  {
                dm.whereIsWaldo(327, waldoFile, "insertRow.setOnAction((ActionEvent event)");
                myEditOps.insertRow();
            }
        });

        //  This is not implemented!!!
        deleteRow.setOnAction((ActionEvent event) -> {
            if (checkForExistenceOfData())  {
                dm.whereIsWaldo(335, waldoFile, "deleteRow.setOnAction((ActionEvent event)");
                myEditOps.deleteRow();
            }
        });

        insertCol.setOnAction((ActionEvent event) -> {
            if (checkForExistenceOfData())  {
                dm.whereIsWaldo(342, waldoFile, "insertCol.setOnAction((ActionEvent event)");
                myEditOps.insertColumn();
            }
        });

        
        deleteColumn.setOnAction((ActionEvent event) -> {
            if (checkForExistenceOfData())  {
                myEditOps.deleteColumn();
            }
        });
        
        cleanColData.setOnAction((ActionEvent event) -> {
            if (checkForExistenceOfData())  {
                myEditOps.cleanDataInColumn();
            }
        });
        
        // **************************************************************
        // *              Transformations & Operations                  *
        // **************************************************************        
        linTransOfVar.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            if (trans_GUI.getNumericVariableFound() == true) {
                trans_GUI.linTransVars();
            }
        });

        nonlinTransOfVar.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            if (trans_GUI.getNumericVariableFound() == true) {
                trans_GUI.linTransFuncs();
            }
        });
        
        linearCombOfVars.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            if (trans_GUI.getNumericVariableFound() == true) {
                trans_GUI.linearCombOfVariables();
            }
        });
        
        unaryOperationsWithVars.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            if (trans_GUI.getNumericVariableFound() == true) {
                trans_GUI.unaryOperationOnVar();
            }
        });

        binaryOperationsWithVars.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            if (trans_GUI.getNumericVariableFound() == true) {
                trans_GUI.binaryOpsWithVariables();
            }
        });

        // **************************************************************
        // *                  Data Exploration                          *
        // **************************************************************        
        aUnivQuantVariable.setOnAction((ActionEvent event) -> {
            Univ_Quant_Controller explorationController = new Univ_Quant_Controller(dm, "Quantitative");
            strReturnStatus = explorationController.doTheQuantitativeProcedure();
        });
        
        aUnivCategoricalVariable.setOnAction((ActionEvent event) -> {
            UnivCat_Controller univ_CatController = new UnivCat_Controller(dm);
            strReturnStatus = univ_CatController.doUnivCat_FromFileData(dm);
        });
        
        compareTwoDistributions.setOnAction((ActionEvent event) -> {
            Explore_2Ind_Controller twoInd_Controller = new Explore_2Ind_Controller(dm);
            twoInd_Controller.doTidyOrTI8x();
        });

        compareManyDistributions.setOnAction((ActionEvent event) -> {
            MultUni_Controller multUni_Controller = new MultUni_Controller(dm);
            multUni_Controller.doTidyOrNot();
        });
        
        noInf_Regression.setOnAction((ActionEvent event) -> {
                NoInf_Regr_Controller noInf_RegrController = new NoInf_Regr_Controller(dm);
                strReturnStatus = noInf_RegrController.doTheProcedure();
        });
        
        // **************************************************************
        // *              Planning a Study                              *
        // **************************************************************   
        randomAssignmentCRD.setOnAction((ActionEvent event) -> {
            RandomAssignment_Controller randomAssignmentController = new RandomAssignment_Controller(dm, "CRD");
            if (randomAssignmentController.getReturnStatus().equals("OK")) {
                randomAssignmentController.doTheProcedure();
            }
            strReturnStatus = randomAssignmentController.getReturnStatus();
        }); 

        randomAssignmentRBD.setOnAction((ActionEvent event) -> {
            RandomAssignment_Controller randomAssignmentController = new RandomAssignment_Controller(dm, "RBD");
            if (randomAssignmentController.getReturnStatus().equals("OK")) {
                randomAssignmentController.doTheProcedure();
            }
            strReturnStatus = randomAssignmentController.getReturnStatus();
        });         
        
        powerSingleMean.setOnAction((ActionEvent event) -> {
            OneMean_Power_Controller power_SingleMean_Controller = new OneMean_Power_Controller();
            strReturnStatus = power_SingleMean_Controller.ShowNWait();
        });     
        
        powerSingleProp.setOnAction((ActionEvent event) -> {
            OneProp_Power_Controller power_SingleProp_Controller = new OneProp_Power_Controller();
            strReturnStatus = power_SingleProp_Controller.ShowNWait();
        }); 
        
        powerTwoMeans.setOnAction((ActionEvent event) -> {
            IndepMeans_Power_Controller power_TwoMeans_Controller = new IndepMeans_Power_Controller();
            strReturnStatus = power_TwoMeans_Controller.ShowNWait();
        }); 
        
        powerTwoProps.setOnAction((ActionEvent event) -> {
            IndepProps_Power_Controller power_TwoMeans_Controller = new IndepProps_Power_Controller();
            strReturnStatus = power_TwoMeans_Controller.ShowNWait();
        }); 
        
        // **************************************************************
        // *                     Probability                            *
        // **************************************************************        
        probDistCalculations.setOnAction((ActionEvent event) -> {
            ProbCalc_Controller probCalc_Controller = new ProbCalc_Controller();
            strReturnStatus = probCalc_Controller.doTheProcedure();
        });    
         
        probVenn.setOnAction((ActionEvent event) -> {
        Venn_FullMonte venn_FullMonte = new Venn_FullMonte();
        VBox rootyWooty = venn_FullMonte.getTheRoot();
        Scene scene = new Scene(rootyWooty, 1000, 750); 
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show(); 
        }); 
         
        probTree.setOnAction((ActionEvent event) -> {
            Tree_FullMonte tree_FullMonte = new Tree_FullMonte();
            VBox rootyWooty = tree_FullMonte.getTheRoot();
            Scene scene = new Scene(rootyWooty, 1000, 750);  
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show(); 
        }); 
         
        probTable.setOnAction((ActionEvent event) -> {
            Table_FullMonte table_FullMonte = new Table_FullMonte();
            VBox rootyWooty = table_FullMonte.getTheRoot();
            Scene scene = new Scene(rootyWooty, 1000, 750);       
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }); 
         
        probCalculator.setOnAction((ActionEvent event) -> {
            ProbText_FullMonte probText_FullMonte = new ProbText_FullMonte();
            VBox rootyWooty = probText_FullMonte.getTheRoot();
            Scene scene = new Scene(rootyWooty, 1000, 750);   
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }); 
        
        // **************************************************************
        // *               Inference for means                          *
        // **************************************************************        
        singleMean.setOnAction((ActionEvent event) -> {
            Single_t_Controller singleT_Controller = new Single_t_Controller(dm);
            strReturnStatus = singleT_Controller.chooseDataOrSummary();
            if (strReturnStatus.equals("Cancel")) { dm.setRawOrSummary("NULL"); }
            if (printTheStuff) {
                System.out.println("--- 517 MainMenu, strReturnStatus = " + strReturnStatus);
            }
        });
        
        // ******          Independent t procedure          ******
        independentMeans.setOnAction((ActionEvent event) -> {
            Indep_t_Controller indep_t_Controller = new Indep_t_Controller(dm);
            strReturnStatus = indep_t_Controller.doTidyOrNot();
            if (strReturnStatus.equals("Cancel")) { dm.setRawOrSummary("NULL"); }
            if (printTheStuff) {
                System.out.println("--- 527 MainMenu, strReturnStatus = " + strReturnStatus);
            }
        });

        pairedMean.setOnAction((ActionEvent event) -> {
            Matched_t_Controller matchedT_Controller = new Matched_t_Controller(dm);
            strReturnStatus = matchedT_Controller.prepColumns();
            if (printTheStuff) {
                System.out.println("--- 537 MainMenu, strReturnStatus = " + strReturnStatus);
            }
            if (strReturnStatus.equals("Cancel")) { dm.setRawOrSummary("NULL"); }
        });
        
        // **************************************************************
        // *              One way Analysis of Variance                  *
        // **************************************************************
        anova_Cat_Data.setOnAction((ActionEvent event) -> {
            ANOVA1_Cat_Controller anova1_Cat_Controller = new ANOVA1_Cat_Controller(dm);
            anova1_Cat_Controller.doTidyOrTI8x();
            strReturnStatus = anova1_Cat_Controller.getReturnStatus(); 
        });
        
        anova_Quant_Data.setOnAction((ActionEvent event) -> {
            ANOVA1_Quant_Controller anova1_Quant_Controller = new ANOVA1_Quant_Controller(dm);
            anova1_Quant_Controller.doTidyOrTI8x();
            strReturnStatus = anova1_Quant_Controller.getReturnStatus(); 
        });
        
        // **************************************************************
        // *              Two way Analysis of Variance                  *
        // **************************************************************
        runCR2anova.setOnAction((ActionEvent event) -> {
            ANOVA2_RCB_Controller anova2_Controller = new ANOVA2_RCB_Controller(dm, "Factorial");
            strReturnStatus = anova2_Controller.doTheANOVA2();
        });

        //              Randomized Block
        runRCBanova.setOnAction((ActionEvent event) -> {
            ANOVA2_RCB_Controller anova2_Controller = new ANOVA2_RCB_Controller(dm, "RCB");
            strReturnStatus = anova2_Controller.doTheANOVA2();
        });
        
        //             Analysis of Covariance
        analysisOfCovariance.setOnAction((ActionEvent event) -> {
            ANCOVA_Controller ancova_Controller = new ANCOVA_Controller(dm);
            strReturnStatus = ancova_Controller.doTheANCOVA();
        });
        
        //              Repeated Measures
        runRManova.setOnAction((ActionEvent event) -> {
            ANOVA2_RM_Controller anova2_RM_Controller = new ANOVA2_RM_Controller(dm);
            boolean fileStructIsOK = anova2_RM_Controller.getFileStructureIsOK();
            if (fileStructIsOK) {
                strReturnStatus = anova2_RM_Controller.doTheANOVA2();
            }
        });
        
        // **************************************************************
        // *                   Regression                               *
        // **************************************************************       
        simpleLinearRegression.setOnAction((ActionEvent event) -> {
                Inf_Regr_Controller simpleRegr_Controller = new Inf_Regr_Controller(dm);
                strReturnStatus = simpleRegr_Controller.doTheProcedure();
        });
        
        regression_Compare.setOnAction((ActionEvent event) -> {
            if (getDataExists()) {
                Regr_Compare_Controller regr_Compare_Controller = new Regr_Compare_Controller(dm);
                strReturnStatus = regr_Compare_Controller.doTheRegr_Compare();
            }
        });
        
        noInterceptSimpleRegression.setOnAction((ActionEvent event) -> {
                NoIntercept_Regr_Controller noInt_RegrProc = new NoIntercept_Regr_Controller(dm);
                strReturnStatus = noInt_RegrProc.doTheProcedure();
        });
        
        quadraticRegression.setOnAction((ActionEvent event) -> {
                QuadReg_Controller quadRegrProc = new QuadReg_Controller (dm);
                strReturnStatus = quadRegrProc.doTheProcedure();
        });
        
        multLinRegression.setOnAction((ActionEvent event) -> {
            MultReg_Controller multRegProc = new MultReg_Controller(dm);
            strReturnStatus = multRegProc.doTheProcedure();
        });

        logisticRegression.setOnAction((ActionEvent event) -> {
            Logistic_Controller logisticController = new Logistic_Controller(dm);
            strReturnStatus = logisticController.doTheProcedure();
        });
                
        // **************************************************************
        // *                 Bivariate Categorical                      *
        // **************************************************************
        epidemiology_RawCounts.setOnAction((ActionEvent event) -> {

            Epi_Controller epi_Controller = new Epi_Controller(dm, "Epidemiology");
            epi_Controller.doEpidemiology_FromTable();
        });

        epidemiology_FileData.setOnAction((ActionEvent event) -> { 
            Epi_Controller epi_Controller = new Epi_Controller(dm, "Epidemiology");
            epi_Controller.doEpidemiology_FromFile(dm);
        });                
                
        bivCatRawCounts.setOnAction((ActionEvent event) -> {
            BivCat_Controller bivCat_Controller = new BivCat_Controller(dm, "Standard");
            bivCat_Controller.doAssoc_FromTable();
        });

        bivCatFileData.setOnAction((ActionEvent event) -> {
            BivCat_Controller bivCat_Controller = new BivCat_Controller(dm, "Standard");
            bivCat_Controller.doAssoc_FromFile(dm);
        });
        
        // **************************************************************
        // *                    Chi square                              *
        // **************************************************************
        chiSquareRawCounts.setOnAction((ActionEvent event) -> {
            X2_Menu x2Menu = new X2_Menu();
            x2Menu.chooseProcedure();
            procedure = x2Menu.getChosenProcedure();
            switch(procedure) {
                case GOF: 
                    X2GOF_Controller x2GOF_Controller = new X2GOF_Controller(); 
                    strReturnStatus = x2GOF_Controller.doGOF_ByHand();
                    break; 
 
                case EXPERIMENT: 
                case HOMOGENEITY: 
                case INDEPENDENCE: 
                    X2Assoc_Controller x2Assoc_Controller = new X2Assoc_Controller(dm, procedure);
                    x2Assoc_Controller.doAssoc_FromTable();
                    break;

                case ESCAPE: break;
                default:
                    String switchFailure = "Switch failure: MainMenu.chiSquareFileData, 662";
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }
        });

        chiSquareFileData.setOnAction((ActionEvent event) -> {
            X2_Menu x2Menu = new X2_Menu();
            x2Menu.chooseProcedure();
            procedure = x2Menu.getChosenProcedure();
            switch(procedure) {
                case GOF: 
                    X2GOF_Controller x2GOF_Controller = new X2GOF_Controller(); 
                    x2GOF_Controller.doGOF_FromFileData(dm);
                    break;     
     
                case EXPERIMENT: 
                case HOMOGENEITY: 
                case INDEPENDENCE: 
                    X2Assoc_Controller x2Assoc_Proc = new X2Assoc_Controller(dm, procedure);
                    x2Assoc_Proc.doAssoc_FromFile(dm);
                    break;
                case ESCAPE: break;
                default:
                    String switchFailure = "Switch failure: MainMenu.chiSquareFileData, 685";
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }
        });
        
        // **************************************************************
        // *                      Bootstrapping                         *
        // **************************************************************
        
        bootOneVar.setOnAction((ActionEvent event) -> {
            String whichBoot = "ChooseUnivStat";
            ChooseStats_Controller boot_Controller = new ChooseStats_Controller(dm, whichBoot);
            boot_Controller.doTheControllerThing();
            strReturnStatus = boot_Controller.getStrReturnStatus();  
        });
        
        bootTwoVar.setOnAction((ActionEvent event) -> {
            String whichBoot = "ChooseTwoStat";
            ChooseStats_Controller boot_Controller = new ChooseStats_Controller(dm, whichBoot);
            boot_Controller.doTheControllerThing();
            strReturnStatus = boot_Controller.getStrReturnStatus();  
        });
        
        bootRegression.setOnAction((ActionEvent event) -> {
            String whichBoot = "ChooseRegression";
            ChooseStats_Controller boot_Controller = new ChooseStats_Controller(dm, whichBoot);
            boot_Controller.doTheControllerThing();
            strReturnStatus = boot_Controller.getStrReturnStatus();  
        });
        
        // **************************************************************
        // *                Inference for proportions                   *
        // **************************************************************
        singleProportion.setOnAction((ActionEvent event) -> {
            OneProp_Inf_Controller oneProp_Inf_Controller = new OneProp_Inf_Controller(/* dm */);
            if (oneProp_Inf_Controller.getReturnStatus().equals("OK")) {
                oneProp_Inf_Controller.doTheControllerThing();
            }
        });

        differenceInProportions.setOnAction((ActionEvent event) -> {
            TwoProp_Inf_Controller twoProp_Inf_Controller = new TwoProp_Inf_Controller(this);
            if (twoProp_Inf_Controller.getReturnStatus().equals("OK")) {
                twoProp_Inf_Controller.doTheControllerThing();
            }
        });
    } // End constructor
    
    public Data_Manager getDataManager() { return dm; }
    
    public String getReturnStatus() { return strReturnStatus; }
    public void setReturnStatus(String toThis) { strReturnStatus = toThis; }
    
    public boolean checkForExistenceOfData() {
        int casesInStruct = dm.getNCasesInStruct();

        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return false;
        }
        return true;
    }
    
    public void setFileLabel(String toThis) { fileLabel.setText("File: " + toThis); }
    
    private boolean getDataExists() { 
        dataExists = dm.getDataExists();
        if (!dataExists) { MyAlerts.showAintGotNoDataAlert(); }
        return dataExists; 
    }
    
    public String toString() { // new Exception().printStackTrace();
        // new Exception().printStackTrace(); 
        return "MainMinu, not printing toString";
    }
} 
