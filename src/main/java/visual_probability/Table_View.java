/****************************************************************************
 *                           Table_View                                     *
 *                            01/07/23                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Table_View {
    
    // POJOs
    boolean bool_AandNotB_IsDrawn, bool_AandB_IsDrawn, bool_NotAandB_IsDrawn,
            bool_NotAandNotB_IsDrawn;
    
    int theCombo;
    
    double px_x_UL_Universe, px_y_UL_Universe, 
           px_x_LR_Universe, px_y_LR_Universe, px_x_UL_Table, px_y_UL_Table, 
           px_x_LR_Table, px_y_LR_Table;
    double px_TableWidth, px_TableHeight;
    double px_UBT;  //  OutsideTheAandB band thickness
    
    public double probA, probB, probAandB, probAorB;
    public double probNotA, probNotB, probAandNotB, probBandNotA, probNotAandB;
    public double probAGivenB, probAGivenNotB, probBGivenA, probBGivenNotA, probNotAandNotB,
                  probNotAGivenB, probNotAGivenNotB, probNotBGivenA, probNotBGivenNotA,
                  probAorNotB, probNotAorB, probNotAorNotB; 
    
    String strMargin_A, strMargin_Not_A, strMargin_B, strMargin_Not_B;

    // My classes
    MyRectangle rectUniverse, rectTable, rectAandB, rectAandNotB,
                rectNotAandB, rectNotAandNotB, rectA, rectB, rectNotA,
                rectNotB;

    Table_OutsideTheAandB universe;
    Table_FullMonte table_FullMonte;
    //  FX Classes
    Color clr_Universe, clr_Text, clr_Yes, clr_White;    
    Line line_BorderAandNotA, line_BorderBandNotB;
    Pane pane;
    Text txtMargin_A, txtMargin_Not_A, txtMargin_B, txtMargin_Not_B;

    public Table_View(Table_FullMonte table_FullMonte) {
        this.table_FullMonte = table_FullMonte;
        pane = new Pane();
        
        clr_Universe =  table_FullMonte.getUniverseColor();
        clr_Text = table_FullMonte.getTextColor();
        clr_Yes = table_FullMonte.getYesColor();
        clr_White = Color.WHITE;

        probA = table_FullMonte.getProbA();
        probNotA = table_FullMonte.getProbNotA();
        probB = table_FullMonte.getProbB();
        probNotB = table_FullMonte.getProbNotB();
        probAandB = table_FullMonte.getProbAandB();
        probAorB = table_FullMonte.getProbAorB();
        probAandNotB = table_FullMonte.getProbAandNotB();
        probAorNotB = table_FullMonte.getProbAorNotB();
        probNotAandB = table_FullMonte.getProbNotAandB();
        probNotAorB = table_FullMonte.getProbNotAorB();
        probAGivenB = table_FullMonte.getProbAGivenB();
        probBGivenA = table_FullMonte.getProbBGivenA();
        probNotAGivenB = table_FullMonte.getProbNotAGivenB();
        probNotBGivenA = table_FullMonte.getProbNotBGivenA();
        probNotAGivenNotB = table_FullMonte.getProbNotAGivenNotB();
        probNotBGivenNotA = table_FullMonte.getProbNotBGivenNotA();

        initializeStuff();
        createTheTable();
        setTheTable();
        drawProb_AandB();
    }
    
    public void doTheDeed() {
        theCombo = table_FullMonte.getTheCombo();
        createTheTable(); 
        
        clr_Universe =  table_FullMonte.getUniverseColor();
        clr_Text = table_FullMonte.getTextColor();
        clr_Yes = table_FullMonte.getYesColor();
        
        switch (theCombo) {
            case   2: drawProb_AandB(); break;
            case 200: drawProb_AandB(); break;
            case   3: drawProb_AandNotB(); break;
            case 300: drawProb_AandNotB(); break;
            case  13: drawProb_AorNotB(); break;
            case 310: drawProb_AorNotB(); break;
            case 102: drawProb_NotAandB(); break;
            case 201: drawProb_NotAandB(); break;
            case 122: drawProb_NotAGivenB(); break;
            case 123: drawProb_NotAGivenNotB(); break;
            case 22: drawProb_AGivenB(); break;
            case 23: drawProb_AGivenNotB(); break;
            case 220: drawProb_BGivenA(); break;
            case  12: drawProb_AorB(); break;
            case 210: drawProb_AorB(); break;
            case 211: drawProb_NotAorB(); break;
            case 221: drawProb_BGivenNotA(); break;
            case 112: drawProb_NotAorB(); break;
            case 103: drawProb_NotAandNotB(); break;
            case 301: drawProb_NotAandNotB(); break;
            case 113: drawProb_NotAorNotB(); break;
            case 311: drawProb_NotAorNotB(); break;

            //  Silly choices
            default: 
                startOver();
                //  Silly message
        }
        
        
    }
    
    private void initializeStuff() {
        // px => in pixels
        px_x_UL_Universe = 25.0;
        px_y_UL_Universe = 25.0;
        px_x_LR_Universe = 635.;
        px_y_LR_Universe = 550.;
        
        px_UBT = 12;    // Inset of table into universe
        
        px_x_UL_Table = px_x_UL_Universe + 5 * px_UBT;
        px_y_UL_Table = px_y_UL_Universe + 5 * px_UBT;
        px_x_LR_Table = px_x_LR_Universe - 0.5 * px_UBT;
        px_y_LR_Table = px_y_LR_Universe - 0.5 * px_UBT;
        
        rectUniverse = new MyRectangle(px_x_UL_Universe, px_y_UL_Universe, 
                                       px_x_LR_Universe, px_y_LR_Universe);
        
        rectTable = new MyRectangle(px_x_UL_Table, px_y_UL_Table, 
                                    px_x_LR_Table, px_y_LR_Table);   
        
        px_TableWidth = px_x_LR_Table - px_x_UL_Table;
        px_TableHeight = px_y_LR_Table - px_y_UL_Table;
    }
    
    private void setTheTable() {
        // Outside rectangles
        rectUniverse.setStrokeWidth(2);
        rectUniverse.setStroke(Color.BLUE);
        rectTable.setStrokeWidth(2);
        rectTable.setStroke(Color.RED);

        pane.getChildren().addAll(rectUniverse.getTheRectBorder());
        pane.getChildren().addAll(rectTable.getTheRectBorder());    
    }
    
    private void drawProb_AandB() { //  Checked
        genericDrawProb("A and B", clr_Yes, clr_Universe,
                         clr_Universe, clr_Universe);
    }
    
    private void drawProb_AorB() {  //  Checked
        genericDrawProb("A or B", clr_Yes, clr_Yes,
                         clr_Yes, clr_Universe);
    }
    
    
    private void drawProb_NotAorB() { 
        genericDrawProb("not A or B", clr_Yes, clr_Universe,
                         clr_Yes, clr_Yes);
    }
    
    private void drawProb_AandNotB() { 
        genericDrawProb("A and not B", clr_Universe, clr_Yes,
                         clr_Universe, clr_Universe);
    }
    
    private void drawProb_NotAandB() { 
        genericDrawProb("not A and B", clr_Universe, clr_Universe,
                         clr_Yes, clr_Universe);
    }
    
    private void drawProb_NotAandNotB() {   //  Checked
        genericDrawProb("not A and not B", clr_Universe, clr_Universe,
                         clr_Universe, clr_Yes);
    }
    
    private void drawProb_AorNotB() {  
        genericDrawProb("A or not B", clr_Yes, clr_Yes,
                         clr_Universe, clr_Yes);
    }
    
    private void drawProb_NotAorNotB() { 
        genericDrawProb("not A or not B", clr_Universe, clr_Yes,
                         clr_Yes, clr_Yes);
    }
        
    private void drawProb_AGivenB() { 
        genericDrawProb("A given B", clr_Yes, clr_White,
                         clr_Universe, clr_White);
    }
    
    private void drawProb_BGivenA() { 
        genericDrawProb("B given A", clr_Yes, clr_Universe,
                         clr_White, clr_White);
    }
    
    private void drawProb_AGivenNotB() { 
        genericDrawProb("A given not B", clr_White, clr_Yes,
                         clr_White, clr_Universe);
    }
    
    private void drawProb_BGivenNotA() { 
        genericDrawProb("B given not A", clr_White, clr_White,
                         clr_Yes, clr_Universe);
    }
    
    private void drawProb_NotAGivenB() { 
        genericDrawProb("not A given B", clr_Universe, clr_White,
                         clr_Yes, clr_White);
    }
    
    private void drawProb_NotAGivenNotB() { 
        genericDrawProb("not A Given not B", clr_White, clr_Universe,
                         clr_White, clr_Yes);
    }
    
    private void genericDrawProb(String daProbString, 
                                 Color clr_rectAandB,
                                 Color clr_rectAandNotB,
                                 Color clr_rectNotAandB,
                                 Color clr_rectNotAandNotB) {
        table_FullMonte.setTextDescription(daProbString);
        startOver();
        rectAandB.setStroke(clr_rectAandB);        
        rectAandNotB.setStroke(clr_rectAandNotB);
        rectNotAandB.setStroke(clr_rectNotAandB);
        rectNotAandNotB.setStroke(clr_rectNotAandNotB);
        addTheRectangles();         
    }
    
    private void createTheTable() {
        // joint rectangles
        rectAandB = new MyRectangle(px_x_UL_Table, 
                                    px_y_UL_Table, 
                                    px_x_UL_Table + probB * px_TableWidth,
                                    px_y_UL_Table + probA * px_TableHeight);
        
        rectAandNotB = new MyRectangle(px_x_UL_Table + probB * px_TableWidth, 
                                       px_y_UL_Table, 
                                       px_x_LR_Table, 
                                       px_y_UL_Table + probA * px_TableHeight);
       
        rectNotAandB = new MyRectangle(px_x_UL_Table, 
                                       px_y_UL_Table + probA * px_TableHeight, 
                                       px_x_UL_Table + probB * px_TableWidth, 
                                       px_y_LR_Table);

        rectNotAandNotB = new MyRectangle(px_x_UL_Table + probB * px_TableWidth, 
                                       px_y_UL_Table + probA * px_TableHeight, 
                                       px_x_LR_Table, 
                                       px_y_LR_Table);
        
        line_BorderAandNotA = new Line(px_x_UL_Table, 
                                    px_y_UL_Table + probA * px_TableHeight, 
                                    px_x_LR_Table,
                                    px_y_UL_Table + probA * px_TableHeight);
        
        line_BorderAandNotA.setStroke(Color.BLACK);
        line_BorderAandNotA.setStrokeWidth(2);
        
        line_BorderBandNotB = new Line(px_x_UL_Table + probB * px_TableWidth, 
                                    px_y_UL_Table, 
                                    px_x_UL_Table + probB * px_TableWidth,
                                    px_y_LR_Table);
        
        line_BorderBandNotB.setStroke(Color.BLACK);
        line_BorderBandNotB.setStrokeWidth(2);
        
        universe = new Table_OutsideTheAandB(this);
    }
    
    private void addTheRectangles() {
        // Fill is 500 lines
        rectAandB.getRectFill();
        rectAandNotB.getRectFill();
        rectNotAandB.getRectFill();
        rectNotAandNotB.getRectFill();
        add_AandB();
        add_AandNotB();
        add_NotAandB();
        add_NotAandNotB();  
        pane.getChildren().addAll(line_BorderAandNotA, line_BorderBandNotB); 

        double yCoord_txtMargin_A = px_y_UL_Table + 0.5 * probA * px_TableHeight;
        txtMargin_A = new Text(px_x_UL_Universe + 10, yCoord_txtMargin_A, "A");
        txtMargin_A.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        txtMargin_A.setStroke(Color.BLACK);
        txtMargin_A.setFill(Color.BLACK);
        
        double yCoord_txtMargin_Not_A = 0.5 * (px_y_UL_Table + probA * px_TableHeight + px_y_LR_Table);
        txtMargin_Not_A = new Text(px_x_UL_Universe + 10, yCoord_txtMargin_Not_A, "not A");
        txtMargin_Not_A.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        txtMargin_Not_A.setStroke(Color.BLACK);
        txtMargin_Not_A.setFill(Color.BLACK);   
        
        double xCoord_txtMargin_B = px_x_UL_Table + 0.5 * probB * px_TableWidth;
        txtMargin_B = new Text(xCoord_txtMargin_B, px_y_UL_Universe + 25, "B");
        txtMargin_B.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        txtMargin_B.setStroke(Color.BLACK);
        txtMargin_B.setFill(Color.BLACK);
        
        double xCoord_txtMargin_Not_B = 0.5 * (px_x_UL_Table + 0.5 * probB * px_TableWidth + px_x_LR_Table);
        txtMargin_Not_B = new Text(xCoord_txtMargin_Not_B, px_y_UL_Universe + 25, "not B");
        txtMargin_Not_B.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        txtMargin_Not_B.setStroke(Color.BLACK);
        txtMargin_Not_B.setFill(Color.BLACK);       
        
        pane.getChildren().addAll(txtMargin_A, txtMargin_Not_A, txtMargin_B, txtMargin_Not_B);
    }
    
    private void add_AandNotB() {
        if (!bool_AandNotB_IsDrawn) {
            pane.getChildren().addAll(rectAandNotB.getRectFill());
        }
        bool_AandNotB_IsDrawn = true;
    }
    
    private void remove_AandNotB() {
        if (bool_AandNotB_IsDrawn) {
            pane.getChildren().removeAll(rectAandNotB.getRectFill());
        }
        bool_AandNotB_IsDrawn = false;
    }
    
    private void add_NotAandB() {
        if (!bool_NotAandB_IsDrawn) {
            pane.getChildren().addAll(rectNotAandB.getRectFill());
        }
        bool_NotAandB_IsDrawn = true;
    }
    
    private void remove_NotAandB() { 
        if (bool_NotAandB_IsDrawn) {
            pane.getChildren().removeAll(rectNotAandB.getRectFill());
        } 
        bool_NotAandB_IsDrawn = false;
    }
    
    private void add_AandB() { 
        if (!bool_AandB_IsDrawn) {
            pane.getChildren().addAll(rectAandB.getRectFill());
        }
        bool_AandB_IsDrawn = true;
    }
    
    private void remove_AandB() { 
        if (bool_AandB_IsDrawn) {
            pane.getChildren().removeAll(rectAandB.getRectFill());
        }
        bool_AandB_IsDrawn = false;
    }  
    
    private void add_NotAandNotB() { 
        if (!bool_NotAandNotB_IsDrawn) {
            pane.getChildren().addAll(rectNotAandNotB.getRectFill());
        }
        bool_AandB_IsDrawn = true;
    }
    
    private void remove_NotAandNotB() { 
        if (bool_NotAandNotB_IsDrawn) {
            pane.getChildren().removeAll(rectNotAandNotB.getRectFill());
        }
        bool_AandB_IsDrawn = false;
    }
    
    public void startOver() {
        remove_AandNotB();
        remove_NotAandB();
        remove_AandB();
        remove_NotAandNotB(); 
        pane.getChildren().remove(txtMargin_A);
    }
    
    public Pane getPane() { return pane; }
}
