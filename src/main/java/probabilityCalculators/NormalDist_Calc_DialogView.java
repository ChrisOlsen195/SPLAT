/**************************************************
 *             NormalDist_Calc_DialogView         *
 *                    11/27/23                    *
 *                     00:00                      *
 *************************************************/
package probabilityCalculators;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import probabilityDistributions.StandardNormal;
import javafx.scene.control.CheckBox;
import smarttextfield.DoublyLinkedSTF;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

/***************************************************************
 *     STFs and dbl_STFs are:                                  *
 *     [0] mu                                                  *
 *     [1] sigma                                               *
       [2] left probability                                    *
 *     [3] middle probability                                  *
 *     [4] right probability                                   *
 *     [5] left-mid boundary                                   *
 *     [6] mid-right boundary                                  *
 *                                                             *
 ***************************************************************/

public class NormalDist_Calc_DialogView extends ProbCalc_DialogView {
    
    boolean muExists, sigmaExists, shadeLeft, shadeRight; 
            //leftBoundaryExists, rightBoundaryExists;

    // FX classes
    Label lbl_uEquals, lbl_SigmaEquals;

    // My classes
    BootstrapDist_Calc_PDFView normal_Calc_PDFView;    
    
    StandardNormal zDistr;

    public NormalDist_Calc_DialogView(ProbCalc_Dashboard probCalc_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);     
        //printAlert(51, "Normal --------------- NormalDist_Calc_DialogView -- constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.probCalc_Dashboard = probCalc_Dashboard;
        zDistr = new StandardNormal();
        
        lbl_uEquals = new Label("\u03BC = ");
        lbl_uEquals.setStyle(cssLabel_01);
        lbl_uEquals.setPadding(new Insets(5, 5, 5, 5));        
        lbl_SigmaEquals = new Label ("\u03C3 = ");
        lbl_SigmaEquals.setStyle(cssLabel_01);
        lbl_SigmaEquals.setPadding(new Insets(5, 5, 5, 20)); 
        
        /*********************************************************
        *     Reset the linking to eliminate the unneeded df     *
        *     from the traversal.  ----->                        *
        *********************************************************/

        al_ProbCalcs_STF.get(1).setPre_Me_AndPostSmartTF(0, 1, 3);
        al_ProbCalcs_STF.get(3).setPre_Me_AndPostSmartTF(1, 3, 4);
        
        /*********************************************************
        *    <----- Reset the linking to eliminate the f         *
        *     unneeded d from the traversal.                     *
        *********************************************************/

        al_ProbCalcs_STF.get(0).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(0).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(0).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(0).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(0).getTextField().setId("Mu");
        al_ProbCalcs_STF.get(0).setSmartTextField_MB_REAL(true);
        al_ProbCalcs_STF.get(0).setIsEditable(true);

        al_ProbCalcs_STF.get(1).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(1).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(1).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(1).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(1).getTextField().setId("Sigma");
        al_ProbCalcs_STF.get(1).setSmartTextField_MB_POSITIVE(true);
        al_ProbCalcs_STF.get(1).setIsEditable(true);

        al_ProbCalcs_STF.get(3).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(3).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(3).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(3).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(3).getTextField().setId("LeftProb");
        al_ProbCalcs_STF.get(3).setSmartTextField_MB_PROBABILITY(true);
        al_ProbCalcs_STF.get(3).setIsEditable(true);

        al_ProbCalcs_STF.get(4).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(4).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(4).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(4).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(4).getTextField().setId("MidProb");
        al_ProbCalcs_STF.get(4).setIsEditable(true);

        al_ProbCalcs_STF.get(5).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(5).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(5).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(5).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(5).getTextField().setId("RightProb");
        al_ProbCalcs_STF.get(5).setSmartTextField_MB_PROBABILITY(true);
        al_ProbCalcs_STF.get(5).setIsEditable(true);

        al_ProbCalcs_STF.get(6).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(6).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(6).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(6).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(6).getTextField().setId("LeftStat");
        al_ProbCalcs_STF.get(6).setSmartTextField_MB_REAL(true);
        al_ProbCalcs_STF.get(6).setIsEditable(true);

        al_ProbCalcs_STF.get(7).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(7).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(7).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(7).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(7).getTextField().setId("RightStat");
        al_ProbCalcs_STF.get(7).setSmartTextField_MB_REAL(true);
        al_ProbCalcs_STF.get(7).setIsEditable(true);
        //allTheSTFs.add(stf_Right_Stat);  

        al_ProbCalcs_STF.get(0).getTextField().setOnAction(e -> doMu());
        al_ProbCalcs_STF.get(1).getTextField().setOnAction(e -> doSigma());
        al_ProbCalcs_STF.get(3).getTextField().setOnAction(e -> { doLeftProbability(); });
        al_ProbCalcs_STF.get(4).getTextField().setOnAction(e -> doMiddleProbability());
        al_ProbCalcs_STF.get(5).getTextField().setOnAction(e -> { doRightProbability(); });   
        al_ProbCalcs_STF.get(6).getTextField().setOnAction(e -> { doLeftStatistic(); });
        al_ProbCalcs_STF.get(7).getTextField().setOnAction(e ->  { doRightStatistic(); });
        
        chBoxLeftTail = new CheckBox("Left tail");
        chBoxLeftTail.setPadding(new Insets(10, 20, 5, 20));
        chBoxTwoTail = new CheckBox("Two equal tails");
        chBoxTwoTail.setPadding(new Insets(10, 15, 5, 15));
        chBoxRightTail = new CheckBox("Right tail");
        chBoxRightTail.setPadding(new Insets(10, 15, 5, 15));
        
        chBoxLeftTail.selectedProperty().addListener(this::chBoxLeftTailChanged);
        chBoxTwoTail.selectedProperty().addListener(this::chBoxTwoTailChanged);
        chBoxRightTail.selectedProperty().addListener(this::chBoxRightTailChanged);
        
        chBoxLeftTail.setSelected(false);
        chBoxTwoTail.setSelected(false);
        chBoxRightTail.setSelected(false);
              
        resetBtn = new Button("Reset values");
        resetBtn.setPadding(new Insets(10, 5, 10, 5));
        resetBtn.setOnAction(e -> resetProbsAndStats());

        probLabels = new HBox(); probLabels.setPadding(new Insets(5, 10, 5, 10));
        probFields = new HBox(); probFields.setPadding(new Insets(5, 10, 15, 10));
        statLabels = new HBox(); statLabels.setPadding(new Insets(5, 10, 5, 10));
        statFields = new HBox(); statFields.setPadding(new Insets(5, 10, 5, 10));
        paramDescr = new HBox(); paramDescr.setPadding(new Insets(5, 10, 5, 80));
        paramStuff = new HBox(); paramStuff.setPadding(new Insets(5, 10, 15, 10));

        spacers = new Region[18];
        for (int ithSpacer = 0; ithSpacer < 18; ithSpacer++) {
            spacers[ithSpacer] = new Region();
        }
 
        spacers[0].setMinWidth(25); spacers[0].setMaxWidth(25);
        spacers[1].setMinWidth(60); spacers[1].setMaxWidth(60);
        spacers[2].setMinWidth(65); spacers[2].setMaxWidth(65);
        spacers[3].setMinWidth(40); spacers[3].setMaxWidth(40);
        spacers[4].setMinWidth(80); spacers[4].setMaxWidth(80);
        spacers[5].setMinWidth(80); spacers[5].setMaxWidth(80);
        spacers[6].setMinWidth(45); spacers[6].setMaxWidth(45);
        spacers[7].setMinWidth(220); spacers[7].setMaxWidth(220);
        spacers[8].setMinWidth(45); spacers[8].setMaxWidth(45);
        spacers[9].setMinWidth(220); spacers[9].setMaxWidth(220);
        spacers[10].setMinWidth(35); spacers[10].setMaxWidth(35);
        spacers[11].setMinWidth(25); spacers[11].setMaxWidth(25);
        spacers[12].setMinWidth(5); spacers[12].setMaxWidth(5);
        spacers[13].setMinWidth(10); spacers[13].setMaxWidth(10);
        spacers[14].setMinWidth(10); spacers[14].setMaxWidth(10);

        spacers[15].setMinWidth(35); spacers[15].setMaxWidth(35);
        spacers[16].setMinWidth(10); spacers[16].setMaxWidth(10);
        spacers[17].setMinWidth(10); spacers[17].setMaxWidth(10);
        
        chBoxBox = new HBox(spacers[15], chBoxLeftTail, spacers[16], chBoxTwoTail, spacers[17], chBoxRightTail);
        chBoxBox.setSpacing(10);

        probLabels.getChildren().addAll(spacers[0], lbl_Left_Prob, 
                                        spacers[1], lbl_Mid_Prob, 
                                        spacers[2], lbl_Right_Prob);
        probFields.getChildren().addAll(spacers[3], al_ProbCalcs_STF.get(3).getTextField(), 
                                        spacers[4], al_ProbCalcs_STF.get(4).getTextField(), 
                                        spacers[5], al_ProbCalcs_STF.get(5).getTextField());

        statLabels.getChildren().addAll(spacers[6], lbl_Left_Stat, 
                                        spacers[7], lbl_Right_Stat);
        statFields.getChildren().addAll(spacers[8], al_ProbCalcs_STF.get(6).getTextField(),  
                                        spacers[9], al_ProbCalcs_STF.get(7).getTextField());
        
        paramDescr.getChildren().addAll(spacers[10],txtDistParams);
        
        paramStuff.getChildren().addAll(spacers[11], lbl_uEquals,
                                        spacers[12], al_ProbCalcs_STF.get(0).getTextField(),
                                        spacers[13], lbl_SigmaEquals, 
                                        spacers[14], al_ProbCalcs_STF.get(1).getTextField());

        theHBoxes = new VBox();
        theHBoxes.setPadding(new Insets(5, 5, 5, 5));
        theHBoxes.setAlignment(Pos.CENTER);

        theHBoxes.getChildren().addAll(chBoxBox,
                                       paramDescr, paramStuff, txt_ProbTitle,
                                       probLabels, probFields, txt_StatTitle,
                                       statLabels, statFields,
                                       resetBtn);
        
        mu = 0.0; sigma = 1.0;  //  Need for default axis
        al_ProbCalcs_STF.get(0).setText(toBlank); al_ProbCalcs_STF.get(1).setText(toBlank);
        al_ProbCalcs_STF.get(3).setText(toBlank);
        al_ProbCalcs_STF.get(5).setText(toBlank);
        al_ProbCalcs_STF.get(4).setText(toBlank);
        al_ProbCalcs_STF.get(6).setText(toBlank);
        al_ProbCalcs_STF.get(7).setText(toBlank);
        makeItHappen();
    }
    
    private void doMu() {
        //printAlert(235, "Normal --------------- doMu()");
        strTempString = al_ProbCalcs_STF.get(0).getTextField().getText();
        mu = StringUtilities.convertStringToDouble(strTempString);
        resetProbsAndStats();
        constructGraphStatus();
        if (okToGraph) {
            makeANewGraph();
        }
    }
  
    private void doSigma() {
        //printAlert(246, "Normal --------------- doSigma()");
        strTempString = al_ProbCalcs_STF.get(1).getTextField().getText();
        sigma = StringUtilities.convertStringToDouble(strTempString);
        resetProbsAndStats(); 
        constructGraphStatus();
        if (okToGraph) {
            makeANewGraph();
        }
    }
        
    public void fromLeftProbDoLeftStat() {
        //printAlert(257, "Normal --------------- fromLeftProbDoLeftStat()");
        dbl_Left_Stat = getInverseAreaToTheLeftOf(dbl_Left_Prob);
        // Un-standardize
        dbl_Left_Stat = sigma * dbl_Left_Stat + mu;
        al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
        al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
    }
    
    public void fromRightProbDoRightStat() {
        //printAlert(266, "Normal --------------- fromRightProbDoRightStat()");
        dbl_Right_Stat = getInverseAreaToTheRightOf(dbl_Right_Prob);
        // Un-standardize
        dbl_Right_Stat = sigma * dbl_Right_Stat + mu;
        al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
        al_ProbCalcs_STF.get(7).setText(roundDoubleToProbString(dbl_Right_Stat));
    }
    
    public void fromLeftStatDoLeftProb() {
        //printAlert(275, "Normal --------------- fromLeftStatDoLeftProb()");
        double z = (dbl_Left_Stat - mu) / sigma;
        dbl_Left_Prob = getAreaToTheLeftOf(z);
        al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob)); 
        al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
    }

    public void fromRightStatDoRightProb() {
        //printAlert(283, "Normal --------------- fromRightStatDoRightProb()");
        double z = (dbl_Right_Stat - mu) / sigma;
        dbl_Right_Prob = getAreaToTheRightOf(z);
        al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
        al_ProbCalcs_STF.get(7).setText(roundDoubleToProbString(dbl_Right_Stat));
    }
    
    public void makeANewGraph() {
        //printAlert(291, "Normal --------------- makeANewGraph()");
        normal_Calc_PDFView.respondToChanges();
        normal_Calc_PDFView.doTheGraph();        
    } 
    
    public void makeAllTheSTFs() {
        //printAlert(298, "Normal --------------- makeAllTheSTFs()");
        al_ProbCalcs_STF.get(0).setText(al_ProbCalcs_STF.get(0).getText());
        al_ProbCalcs_STF.get(1).setText(al_ProbCalcs_STF.get(1).getText());
        al_ProbCalcs_STF.get(3).setText(al_ProbCalcs_STF.get(3).getText());
        al_ProbCalcs_STF.get(4).setText(al_ProbCalcs_STF.get(4).getText());
        al_ProbCalcs_STF.get(5).setText(al_ProbCalcs_STF.get(5).getText());
        al_ProbCalcs_STF.get(6).setText(al_ProbCalcs_STF.get(6).getText());
        al_ProbCalcs_STF.get(7).setText(al_ProbCalcs_STF.get(7).getText());
    }
    
    public boolean distrIsDefined() {
        constructGraphStatus();
        if (!distributionIsDefined) {
            MyAlerts.showUndefinedProbDistAlert("normal");
            resetProbsAndStats();
            return false;
        }
        return true;
    }
    
    public void constructGraphStatus() {
        // Mu and Sigma are defined
        muExists = !al_ProbCalcs_STF.get(0).getText().isEmpty();
        sigmaExists = !al_ProbCalcs_STF.get(1).getText().isEmpty();
        okToGraph = muExists && sigmaExists;
        distributionIsDefined = muExists && sigmaExists;
        shadeLeft = leftTailChecked && str_Left_Stat != null 
                                    && !str_Left_Stat.isEmpty() 
                                    && !(str_Left_Stat == null)
                                    && okToGraph;
        shadeRight = rightTailChecked && str_Right_Stat !=  null 
                                      && !str_Right_Stat.isEmpty() 
                                      && !(str_Right_Stat == null)
                                      && okToGraph;  
        
        if (midTailChecked) {
            shadeLeft = str_Mid_Prob != null 
                           && !str_Mid_Prob.isEmpty() 
                           && okToGraph;
            shadeRight = str_Mid_Prob !=  null 
                           && !str_Mid_Prob.isEmpty() 
                           && okToGraph; 
        }
    }
  
    public double getMu() { return mu; }
    public double getSigma() { return sigma; }
    
    public double getAreaToTheLeftOf(double thisValue) { return zDistr.getLeftTailArea(thisValue);}
    public double getAreaToTheRightOf(double thisValue) { return zDistr.getRightTailArea(thisValue);}    
    public double getInverseAreaToTheLeftOf(double thisValue) { return zDistr.getInvLeftTailArea(thisValue);}
    public double getInverseAreaToTheRightOf(double thisValue) { return zDistr.getInvRightTailArea(thisValue);} 
    public double getDensity(double atThisValue) { return zDistr.getDensity(atThisValue); }
    public boolean getDistributionIsDefined() { return distributionIsDefined; }
    boolean getOKToGraph() { return okToGraph; }
    boolean getShadeLeft() { return shadeLeft; }
    boolean getShadeRight() { return shadeRight; }
    
    @Override
    public Pane getTheContainingPane() { return theContainingPane; }

    // ***************************************************************
    // mu, sigma, dbl_Left_Prob, dbl_Mid_Prob, rightProb, dbl_Left_Middle_Boundary, rightStat  *
    // ***************************************************************
    public DoublyLinkedSTF getAllTheSTFs() { return al_ProbCalcs_STF; }
    
    public void setNormal_PDFView(BootstrapDist_Calc_PDFView normal_Calc_PDFView) {
        this.normal_Calc_PDFView = normal_Calc_PDFView;
    }
     
        /***********************************************************
        *                 For debugging                           *
        **********************************************************/
    
    public void diagnose() {
        //System.out.println("Diagnose Normal, Left Stat = " + dbl_Left_Stat);
        //System.out.println("Diagnose Normal, Right Stat = " + dbl_Right_Stat);
        //System.out.println("Diagnose Normal, Left Prob = " + dbl_Left_Prob);
        //System.out.println("Diagnose Normal, Mid Prob = " + dbl_Mid_Prob);
        //System.out.println("Diagnose Normal, Right Prob = " + dbl_Right_Prob);
    }
}