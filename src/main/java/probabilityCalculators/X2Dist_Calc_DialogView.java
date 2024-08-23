/**************************************************
 *              X2Dist_Calc_DialogView            *
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
import utilityClasses.StringUtilities;
import javafx.scene.control.CheckBox;
import probabilityDistributions.ChiSquareDistribution;
import smarttextfield.DoublyLinkedSTF;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

    /****************************************
    *   STF's in Dist_Calc_PDFView          *
    *   stf_Mu              [0]             *
    *   stf_Sigma           [1]             *
    *   stf_df              [2]             *
    *   stf_Left_Prob       [3]             *
    *   stf_Mid_Prob        [4]             *
    *   stf_Right_Prob      [5]             *
    *   stf_Left_Stat       [6]             *
    *   stf_Right_Stat      [7]             *
    ****************************************/

public class X2Dist_Calc_DialogView extends ProbCalc_DialogView{
    
    // POJOs
    
    boolean dfExists, shadeLeft, shadeRight;
    int init_df = 3;
    int df;
    
    double tempInvLeft_x2, tempInvRight_x2;
    
    // FX Classes

    // My classes
    ChiSquareDistribution x2Distr;  
    X2Dist_Calc_PDFView x2_Calc_PDFView;
  
    public X2Dist_Calc_DialogView(ProbCalc_Dashboard probCalc_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        //System.out.println("55 X2Dist_Calc_DialogView, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.probCalc_Dashboard = probCalc_Dashboard;
        
        goodToGo = false;
        df = init_df;   //  Initially set to 5 to get things rolling
        x2Distr = new ChiSquareDistribution(df);
        
        lbl_df_Equals = new Label("Degrees of freedom = ");
        lbl_df_Equals.setStyle(cssLabel_01);
        lbl_df_Equals.setPadding(new Insets(5, 5, 5, 5));      
        
        /*********************************************************
        *     Reset the linking to eliminate the unneeded mean   *
        *     and standard deviation from the traversal.         *
        *********************************************************/
        int size = al_ProbCalcs_STF.getSize();
        al_ProbCalcs_STF.get(2).setPre_Me_AndPostSmartTF(size - 1, 2, 3);         
        al_ProbCalcs_STF.get(size - 1).setPre_Me_AndPostSmartTF(size - 2, size - 1, 2);   
        
        //stf_df = new SmartTextField();
        al_ProbCalcs_STF.get(2).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(2).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(2).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(2).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(2).getTextField().setId("df");
        al_ProbCalcs_STF.get(2).setSmartTextField_MB_POSITIVEINTEGER(true);
        //allTheSTFs.add(stf_df);
        
        //stf_Left_Prob = new SmartTextField();
        al_ProbCalcs_STF.get(3).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(3).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(3).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(3).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(3).getTextField().setId("LeftProb");
        al_ProbCalcs_STF.get(3).setSmartTextField_MB_PROBABILITY(true);
        //allTheSTFs.add(stf_Left_Prob);

        //stf_Mid_Prob = new SmartTextField();
        al_ProbCalcs_STF.get(4).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(4).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(4).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(4).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(4).getTextField().setId("MidProb");
        al_ProbCalcs_STF.get(4).setSmartTextField_MB_PROBABILITY(true);
        //allTheSTFs.add(stf_Mid_Prob);    

        // stf_Right_Prob = new SmartTextField();
        al_ProbCalcs_STF.get(5).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(5).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(5).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(5).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(5).getTextField().setId("RightProb");
        al_ProbCalcs_STF.get(5).setSmartTextField_MB_PROBABILITY(true);
        // allTheSTFs.add(stf_Right_Prob);     

        //stf_Left_Stat = new SmartTextField();
        al_ProbCalcs_STF.get(6).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(6).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(6).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(6).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(6).getTextField().setId("LeftStat");
        al_ProbCalcs_STF.get(6).setSmartTextField_MB_POSITIVE(true);
        //allTheSTFs.add(stf_Left_Stat);       

        //stf_Right_Stat = new SmartTextField();
        al_ProbCalcs_STF.get(7).getTextField().setMinWidth(65);
        al_ProbCalcs_STF.get(7).getTextField().setMaxWidth(65);
        al_ProbCalcs_STF.get(7).getTextField().setPrefColumnCount(14);
        al_ProbCalcs_STF.get(7).getTextField().setText(toBlank);
        al_ProbCalcs_STF.get(7).getTextField().setId("RightStat");
        al_ProbCalcs_STF.get(7).setSmartTextField_MB_POSITIVE(true);
        //allTheSTFs.add(stf_Right_Stat);  
        
       
        al_ProbCalcs_STF.get(2).getTextField().setOnAction(e -> doDF()); 
        al_ProbCalcs_STF.get(3).getTextField().setOnAction(e -> { doLeftProbability(); });
        al_ProbCalcs_STF.get(4).getTextField().setOnAction(e -> doMiddleProbability());
        al_ProbCalcs_STF.get(5).getTextField().setOnAction(e -> { doRightProbability(); });   
        al_ProbCalcs_STF.get(6).getTextField().setOnAction(e -> { doLeftStatistic(); });     
        //  There is no stf_Mid_Stat
        al_ProbCalcs_STF.get(7).getTextField().setOnAction(e -> { doRightStatistic(); });
        
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
        
        paramStuff.getChildren().addAll(spacers[11], lbl_df_Equals,
                                        spacers[12], al_ProbCalcs_STF.get(2).getTextField());

        theHBoxes = new VBox();
        theHBoxes.setPadding(new Insets(5, 5, 5, 5));
        theHBoxes.setAlignment(Pos.CENTER);

        theHBoxes.getChildren().addAll(chBoxBox,
                                       paramDescr, paramStuff, txt_ProbTitle,
                                       probLabels, probFields, txt_StatTitle,
                                       statLabels, statFields,
                                       resetBtn);
        
        al_ProbCalcs_STF.get(2).setText(toBlank);
        al_ProbCalcs_STF.get(3).setText(toBlank);
        al_ProbCalcs_STF.get(5).setText(toBlank);
        al_ProbCalcs_STF.get(4).setText(toBlank);
        al_ProbCalcs_STF.get(6).setText(toBlank);
        al_ProbCalcs_STF.get(7).setText(toBlank);

        makeItHappen();
    }   //  End constructor
    
    void doDF() {
        strTempString = al_ProbCalcs_STF.get(2).getText();
        okToGraph = false;
        
        if (!strTempString.isEmpty()) {
            boolean dfPosInt = DataUtilities.strIsAPosInt(strTempString);
            okToGraph = dfPosInt;
            
            if (dfPosInt) {
                df = StringUtilities.TextFieldToPrimitiveInt(al_ProbCalcs_STF.get(2).getTextField());
                resetProbsAndStats();
                x2Distr = new ChiSquareDistribution(df);
                dbl_df = df;
                
                if (chBoxLeftTail.isSelected()) {
                    double temp_dbl = getInverseAreaToTheLeftOf(0.05);
                    String temp_str = String.valueOf(temp_dbl);
                    al_ProbCalcs_STF.get(6).setText(temp_str);
                } else if (chBoxTwoTail.isSelected()) {
                    double temp_dbl = getInverseAreaToTheLeftOf(0.025);
                    String temp_str = String.valueOf(temp_dbl);
                    al_ProbCalcs_STF.get(6).setText(temp_str);
                    temp_dbl = getInverseAreaToTheRightOf(0.025);
                    temp_str = String.valueOf(temp_dbl);
                    al_ProbCalcs_STF.get(7).setText(temp_str);
                } else if (chBoxRightTail.isSelected()) {
                    double temp_dbl = getInverseAreaToTheRightOf(0.05);
                    String temp_str = String.valueOf(temp_dbl);
                    al_ProbCalcs_STF.get(7).setText(temp_str);
                } else {
                    //System.out.println("No boxes checked!!!!!!!!!!!!!!!!!!");
                }
                resetProbsAndStats();
                makeAllTheSTFs();
                makeANewGraph();
            } 
        }
    }
    
    public void fromLeftProbDoLeftStat() {
        tempInvLeft_x2 = getInverseAreaToTheLeftOf(dbl_Left_Prob);
        dbl_Left_Stat = tempInvLeft_x2;
        al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob));
        al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
    }
    
    public void fromLeftStatDoLeftProb() {
        dbl_Left_Prob = getAreaToTheLeftOf(dbl_Left_Stat);
        al_ProbCalcs_STF.get(3).setText(roundDoubleToProbString(dbl_Left_Prob)); 
        al_ProbCalcs_STF.get(6).setText(roundDoubleToProbString(dbl_Left_Stat));
    }
    
    public void fromRightProbDoRightStat() {
        tempInvRight_x2 = getInverseAreaToTheRightOf(dbl_Right_Prob);
        dbl_Right_Stat = tempInvRight_x2;
        al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
        al_ProbCalcs_STF.get(7).setText(roundDoubleToProbString(dbl_Right_Stat));
    }
    
    public void fromRightStatDoRightProb() {
        dbl_Right_Prob = getAreaToTheRightOf(dbl_Right_Stat);
        al_ProbCalcs_STF.get(5).setText(roundDoubleToProbString(dbl_Right_Prob));
        al_ProbCalcs_STF.get(7).setText(roundDoubleToProbString(dbl_Right_Stat));
    }
    
    public void makeANewGraph() {
        x2_Calc_PDFView.respondToChanges();
        x2_Calc_PDFView.doTheGraph();        
    }
    
    public Pane getTheContainingPane() { return theContainingPane; }
    
    // public String getTheTailChoice() { return tailChoice; }
    
    // ***************************************************************
    //     The allTheSTFs are what are called by the PDF class       *
    //     df, LeftProb, MidProb, rightProb, LeftStat, rightStat     *
    // ***************************************************************

    public DoublyLinkedSTF getAllTheSTFs() { 
        makeAllTheSTFs();
        return al_ProbCalcs_STF;
    }
    
    public void makeAllTheSTFs() {
        al_ProbCalcs_STF.get(2).setText(al_ProbCalcs_STF.get(2).getText());
        al_ProbCalcs_STF.get(3).setText(al_ProbCalcs_STF.get(3).getText());
        al_ProbCalcs_STF.get(4).setText(al_ProbCalcs_STF.get(4).getText());
        al_ProbCalcs_STF.get(5).setText(al_ProbCalcs_STF.get(5).getText());
        al_ProbCalcs_STF.get(6).setText(al_ProbCalcs_STF.get(6).getText());
        al_ProbCalcs_STF.get(7).setText(al_ProbCalcs_STF.get(7).getText());
    }
    
    public boolean distrIsDefined() {
        constructGraphStatus();
        
        if (!distributionIsDefined) {
            MyAlerts.showUndefinedProbDistAlert("Chi Square");
            resetProbsAndStats();
            return false;
        }
        return true;
    }
    
    public void constructGraphStatus() {
        // Mu and Sigma are defined
        dfExists = !al_ProbCalcs_STF.get(2).getText().isEmpty();
        okToGraph = dfExists;
        distributionIsDefined = dfExists;
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
    
    public ChiSquareDistribution getTheX2Distribution() { return x2Distr; }
    public int getDegreesOfFreedom() { return df; }
    public double getAreaToTheLeftOf(double thisValue) { return x2Distr.getLeftTailArea(thisValue);}
    public double getAreaToTheRightOf(double thisValue) { return x2Distr.getRightTailArea(thisValue);}    
    public double getInverseAreaToTheLeftOf(double thisValue) { return x2Distr.getInvLeftTailArea(thisValue);}
    public double getInverseAreaToTheRightOf(double thisValue) { return x2Distr.getInvRightTailArea(thisValue);} 
    public double getDensity(double atThisValue) { return x2Distr.getDensity(atThisValue); }
    public boolean getDistributionIsDefined() { return distributionIsDefined; }
    boolean getOKToGraph() { return okToGraph; }
    boolean getShadeLeft() { return shadeLeft; }
    boolean getShadeRight() { return shadeRight; }
    
    public void set_X2_PDFView(X2Dist_Calc_PDFView x2_Calc_PDFView) {
        this.x2_Calc_PDFView = x2_Calc_PDFView;
    }
}
