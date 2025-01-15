/**************************************************
 *              BootedStat_DialogView             *
 *                    01/08/25                    *
 *                     15:00                      *
 *************************************************/
package bootstrapping;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import smarttextfield.DoublyLinkedSTF;

public class BootedStat_DialogView extends Super_BootedStat_DialogView {

    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double alpha;
    
    String strBootedStatText;
    // FX classes

    // My classes
    Boot_DistrModel bootedStat_OriginalDistrModel;
    Boot_DistrModel bootedStat_ShiftedDistrModel;
    Boot_Histo_DistrView bootedStat_OriginalDistrView,
                         bootedStat_ShiftedDistrView;  
    Boot_DotPlot_DistrView bootedStat_DotPlot_OriginalDistrView,
                           bootedStat_DotPlot_ShiftedDistrView;
    Bootstrap_Dashboard bootstrap_Dashboard;
    NonGenericBootstrap_Info nonGen;

    public BootedStat_DialogView(NonGenericBootstrap_Info nonGen,
            Bootstrap_Dashboard bootstrap_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super(nonGen, bootstrap_Dashboard, placeHoriz, placeVert, withThisWidth, withThisHeight); 

        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.bootstrap_Dashboard = bootstrap_Dashboard;
        this.nonGen = nonGen;
        strBootedStatText = nonGen.getBootedStatText();
        if (printTheStuff) {
            System.out.println("\n50 *** BootedStat_DialogView, Constructing");
            System.out.println("\n51 --- nonGen.toString = " + nonGen.toString());
        }
    }    
        
    public void continueConstruction() {  
        if (printTheStuff) {
            System.out.println("57 --- BootedStat_DialogView, continueConstruction()");
        }
        bootedStat_OriginalDistrView = bootstrap_Dashboard.get_Boot_OriginalHisto_DistrView();
        bootedStat_DotPlot_OriginalDistrView = bootstrap_Dashboard.get_Boot_OriginalDotPlot_DistrView();
        bootedStat_OriginalDistrModel = nonGen.getOriginalDistrModel();

        bootedStat_ShiftedDistrView = bootstrap_Dashboard.get_Boot_ShiftedHisto_DistrView();
        bootedStat_DotPlot_ShiftedDistrView = bootstrap_Dashboard.get_Boot_ShiftedDotPlot_DistrView();
        bootedStat_ShiftedDistrModel = nonGen.getShiftedDistrModel();

        /*********************************************************
        *     Reset the linking to eliminate the unneeded df     *
        *     from the traversal.  ----->                        *
        *********************************************************/

        al_STF.get(1).setPre_Me_AndPostSmartTF(0, 1, 3);
        al_STF.get(3).setPre_Me_AndPostSmartTF(1, 3, 4);
        
        /*********************************************************
        *    <----- Reset the linking to eliminate the f         *
        *     unneeded d from the traversal.                     *
        *********************************************************/
        
        al_STF.get(3).getTextField().setMinWidth(65);
        al_STF.get(3).getTextField().setMaxWidth(65);
        al_STF.get(3).getTextField().setPrefColumnCount(14);
        al_STF.get(3).getTextField().setText(toBlank);
        al_STF.get(3).getTextField().setId("LeftProb");
        al_STF.get(3).setSmartTextField_MB_PROBABILITY(true);
        al_STF.get(3).setIsEditable(true);

        al_STF.get(4).getTextField().setMinWidth(65);
        al_STF.get(4).getTextField().setMaxWidth(65);
        al_STF.get(4).getTextField().setPrefColumnCount(14);
        al_STF.get(4).getTextField().setText(toBlank);
        al_STF.get(4).getTextField().setId("MidProb");
        al_STF.get(4).setIsEditable(true);

        al_STF.get(5).getTextField().setMinWidth(65);
        al_STF.get(5).getTextField().setMaxWidth(65);
        al_STF.get(5).getTextField().setPrefColumnCount(14);
        al_STF.get(5).getTextField().setText(toBlank);
        al_STF.get(5).getTextField().setId("RightProb");
        al_STF.get(5).setSmartTextField_MB_PROBABILITY(true);
        al_STF.get(5).setIsEditable(true);
        
        /*********************************************************************
        *   These control changes to left/right prob/stats                   *
        *********************************************************************/
        al_STF.get(3).getTextField().setOnAction(e -> { doLeftProbability(); });
        al_STF.get(4).getTextField().setOnAction(e -> { doMiddleProbability();});
        al_STF.get(5).getTextField().setOnAction(e -> { doRightProbability(); });   

        /*********************************************************************
        *   These control changes to left/right prob/stats                   *
        *********************************************************************/
               
        chBoxLeftTail = new CheckBox("Left tail");
        chBoxLeftTail.setPadding(new Insets(10, 20, 5, 20));
        chBoxTwoTail = new CheckBox("Two equal tails");
        chBoxTwoTail.setPadding(new Insets(10, 15, 5, 15));
        chBoxRightTail = new CheckBox("Right tail");
        chBoxRightTail.setPadding(new Insets(10, 15, 5, 15));
        
        /*********************************************************************
        *            These control changes to the graphing                   *
        *********************************************************************/
        chBoxLeftTail.selectedProperty().addListener(this::chBoxLeftTailChanged);
        chBoxTwoTail.selectedProperty().addListener(this::chBoxTwoTailChanged);
        chBoxRightTail.selectedProperty().addListener(this::chBoxRightTailChanged);        
        /*********************************************************************
        *            These control changes to the graphing                   *
        *********************************************************************/
        
        chBoxLeftTail.setSelected(false);
        chBoxTwoTail.setSelected(false);
        chBoxRightTail.setSelected(false);
              
        resetBtn = new Button("Reset values");
        resetBtn.setPadding(new Insets(10, 5, 10, 5));
        resetBtn.setOnAction(e -> resetKandK());

        probLabels = new HBox(); probLabels.setPadding(new Insets(5, 10, 5, 10));
        probFields = new HBox(); probFields.setPadding(new Insets(5, 10, 15, 10));
        statLabels = new HBox(); statLabels.setPadding(new Insets(5, 10, 5, 10));
        statFields = new HBox(); statFields.setPadding(new Insets(5, 10, 5, 10));

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
        probFields.getChildren().addAll(spacers[3], al_STF.get(3).getTextField(), 
                                        spacers[4], al_STF.get(4).getTextField(), 
                                        spacers[5], al_STF.get(5).getTextField());

        theHBoxes = new VBox();
        theHBoxes.setPadding(new Insets(5, 5, 5, 5));
        theHBoxes.setAlignment(Pos.CENTER);

        theHBoxes.getChildren().addAll(chBoxBox,
                                       txt_ProbTitle,
                                       probLabels, probFields,
                                       resetBtn);
        
        al_STF.get(3).setText(toBlank);
        al_STF.get(5).setText(toBlank);
        al_STF.get(4).setText(toBlank);

        makeItHappen();
    }    
    
    public void makeANewGraph() {
        if (printTheStuff) {
            System.out.println("197 --- BootedStat_DialogView, makeANewGraph()");
        }
        bootedStat_OriginalDistrView.respondToChanges();
        bootedStat_OriginalDistrView.doTheGraph();  
        
        bootedStat_DotPlot_OriginalDistrView.respondToChanges();
        bootedStat_DotPlot_OriginalDistrView.doTheGraph(); 
        
        bootedStat_ShiftedDistrView.respondToChanges();
        bootedStat_ShiftedDistrView.doTheGraph();  
        
        bootedStat_DotPlot_ShiftedDistrView.respondToChanges();
        bootedStat_DotPlot_ShiftedDistrView.doTheGraph(); 
    } 
    
    @Override
    public void makeAllTheSTFs() { }  //    This is apparently vestigial
    
    @Override
    public void constructGraphStatus() {
        if (printTheStuff) {
            System.out.println("218 --- BootedStat_DialogView, constructGraphStatus()");
        }
        shadeLeft = bootedStat_OriginalDistrModel.get_ShadeLeft();
        shadeRight = bootedStat_OriginalDistrModel.get_ShadeRight(); 
        
        shadeLeft = bootedStat_ShiftedDistrModel.get_ShadeLeft();
        shadeRight = bootedStat_ShiftedDistrModel.get_ShadeRight(); 
        
        if (twoTail_IsChecked) {
            shadeLeft = str_Mid_Prob != null 
                           && !str_Mid_Prob.isEmpty();
            
            shadeRight = str_Mid_Prob !=  null 
                           && !str_Mid_Prob.isEmpty(); 
        }
    }
    
    @Override
    public Pane getTheContainingPane() { return theContainingPane; }
    // ***************************************************************
    // dbl_Left_Prob, dbl_Mid_Prob, rightProb, dbl_Left_Middle_Boundary, rightStat  *
    // ***************************************************************
    public DoublyLinkedSTF getAllTheSTFs() { return al_STF; }
    
    public BootedStat_DialogView getBootstrapOneStat_DialogView () {
        return this; 
    }
    
    public Bootstrap_Dashboard getTheDashboard() {return chooseStats_Dashboard; }
    
    public boolean getLeftTailChecked() { return chBoxLeftTail.isSelected(); }
    public boolean getTwoTailChecked() { return chBoxTwoTail.isSelected(); }
    public boolean getRightTailChecked() { return chBoxRightTail.isSelected(); }
}