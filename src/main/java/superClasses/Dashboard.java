/**************************************************
 *                   Dashboard                    *
 *                   01/16/25                     *
 *                     18:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package superClasses;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import splat.Data_Manager;

public abstract class Dashboard extends Stage {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    protected Boolean checkBoxSettings[];
        
    protected int nCheckBoxes, nSpacers;
    
    final protected double W_ONE_16TH, H_ONE_16TH;
    protected double backGroundHeight, dashWidth, dashHeight,
                     upperLeftX, upperLeftY, lowerRightX, lowerRightY;
    protected final double CHECKBOXHEIGHT = 100.0;
    protected double[] sixteenths_across, sixteenths_down;
    
    protected double[] initWidth, initHeight; 
    
    protected String returnStatus;
    protected final String cbStyle = "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" ;

    public String containingPaneStyle = "-fx-background-color: white;" +
                                       "-fx-border-color: blue, blue;" + 
                                       "-fx-border-width: 4, 4;" +
                                       "-fx-border-radius: 0, 0;" +
                                       "-fx-border-insets: 0, 0;" +
                                       "-fx-border-style: solid centered, solid centered;";
    
    protected String strJustClickedOn;
    
    protected String[] checkBoxDescr;
    
    public Data_Manager dm;
    
    // POJOs / FX
    protected CheckBox[] checkBoxes;
    protected HBox checkBoxRow;
    protected VBox root;
    protected Pane backGround;
    final protected Rectangle2D visualBounds; 
    protected Region[] spacer;
    final protected Scene scene;
    final protected Screen primaryScreen;
                
    public Dashboard(int numberOfCheckBoxes) { 
        if (printTheStuff == true) {
            System.out.println("77 *** Dashboard (Super), Constructing");
        }
        strJustClickedOn = "xxx";   // Initialize
        primaryScreen = Screen.getPrimary();
        visualBounds = primaryScreen.getVisualBounds();
        upperLeftX = 0.025 * visualBounds.getMaxX();
        upperLeftY = 0.025 * visualBounds.getMaxY();
        lowerRightX = 0.975 * visualBounds.getMaxX();
        lowerRightY = 0.975 * visualBounds.getMaxY(); 
        
        nCheckBoxes = numberOfCheckBoxes;
        nSpacers = numberOfCheckBoxes + 1;
        
        initWidth = new double[nCheckBoxes];
        initHeight = new double[nCheckBoxes];

        checkBoxSettings = new Boolean[nCheckBoxes];    
        checkBoxes = new CheckBox[nCheckBoxes];  
        spacer = new Region[nSpacers];
        
        // These are initial values and may be altered in the subclasses
        for (int i = 0; i < nCheckBoxes; i++) {
            initWidth[i] = 400; 
            initHeight[i] = 350;
            checkBoxSettings[i] = false;
        }

        dashWidth = lowerRightX - upperLeftX;
        dashHeight = lowerRightY - upperLeftY;
        W_ONE_16TH = 0.03125 * dashWidth;        
        H_ONE_16TH = 0.03125 * dashHeight;    
        setX(25); setY(25);
        setWidth(dashWidth);
        setHeight(dashHeight);

        sixteenths_across = new double[nCheckBoxes];
        sixteenths_down = new double[nCheckBoxes];
        
        for (int ith_16th = 0; ith_16th < nCheckBoxes; ith_16th++) {
           sixteenths_across[ith_16th] = upperLeftX + (ith_16th + 1) * W_ONE_16TH;
           sixteenths_down[ith_16th] = upperLeftY + (ith_16th + 1) * H_ONE_16TH;
        }

        // Left spacer min width apparently must be set to have space on the left end
        spacer[0] = new Region();
        spacer[0].setMinWidth(25);
        HBox.setHgrow(spacer[0], Priority.ALWAYS);
        
        for (int iSpacer = 1; iSpacer < nSpacers; iSpacer++) {
            spacer[iSpacer] = new Region();
            spacer[iSpacer].setMinWidth(25);
            HBox.setHgrow(spacer[iSpacer], Priority.ALWAYS);
        }
        
        for (int i = 0; i < nCheckBoxes; i++) {
            checkBoxes[i] = new CheckBox();
            checkBoxes[i].setText("");
            checkBoxes[i].setPrefWidth(400);
            checkBoxes[i].setId("");
            checkBoxes[i].setSelected(false);
            checkBoxes[i].setStyle(cbStyle);
            
            if (checkBoxes[i].isSelected() == true) {
                checkBoxes[i].setTextFill(Color.GREEN);
            }
            else {
                checkBoxes[i].setTextFill(Color.RED);
            }
            
            //  Set Checkbox Action
            checkBoxes[i].setOnAction(e->{
                //System.out.println("140 Dashboard, checkbox action");
                CheckBox tb = ((CheckBox) e.getTarget());            
                String daID = tb.getId();
                strJustClickedOn = daID.trim();
                //System.out.println("147 Dashboard, justClickedOn, daID = " + strJustClickedOn);
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true) {
                    tb.setTextFill(Color.GREEN);
                    ;
                    
                }
                else 
                    tb.setTextFill(Color.RED);

                for (int daCase = 0; daCase < nCheckBoxes; daCase++) {
                    if (daID.equals(checkBoxDescr[daCase])) {
                        checkBoxSettings[daCase] = (checkValue == true); 
                    }
                }
                putEmAllUp();
            });   
        }
        
        checkBoxRow = new HBox();
        checkBoxRow.setMinHeight(50);
        checkBoxRow.setAlignment(Pos.CENTER);
        
        // Sourround the checkBoxes with spacers
        for (int ithCB = 0; ithCB < nCheckBoxes; ithCB++) {
            checkBoxRow.getChildren().add(spacer[ithCB]);
            checkBoxRow.getChildren().add(checkBoxes[ithCB]);
        }
        
        setOnCloseRequest((WindowEvent event) -> {
            returnStatus = "OK";
            close();
        });
        
        backGround = new Pane();
        backGround.setStyle("-fx-background-color: lightblue;");
        backGroundHeight = dashHeight /* - titleTextHeight - checkBoxHeight */;
        backGround.setPrefSize(dashWidth, backGroundHeight);  

        root = new VBox();
        
        Text titleText = new Text("");
        
        root.getChildren().addAll(titleText, checkBoxRow, backGround);
        scene = new Scene(root, dashWidth, dashHeight);
        
        setScene(scene);        
    }
    
    public Data_Manager getDataManager() { return dm; }
        
    public String getReturnStatus() { return returnStatus; }
        
    protected abstract void populateTheBackGround();
    protected abstract void putEmAllUp();  
}

