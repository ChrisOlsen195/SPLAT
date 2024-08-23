/****************************************************************************
 *                           Tree_View                                      *
 *                            01/07/23                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/*****************************************************************
                                                  px_y21         *
                          px_y11                                 *
                                                  px_y22         *
              px_y0                                              *
                                                  px_y23         *
                          px_y12                                 *
                                                  px_y24         *
 ****************************************************************/

public class Tree_View {
    
    // POJOs
    boolean txtProbAIsDrawn, txtProbNotAIsDrawn, 
            txtProbBIsDrawn, txtProbNotBIsDrawn,
            txtProbBGivenAIsDrawn, 
            txtProbNotBGivenAIsDrawn, txtProbBGivenNotAIsDrawn, 
            txtProbNotBGivenNotAIsDrawn;
    boolean[] bUniverse, bSuccess;   
    
    int theCombo;
    
    double px_x_UL_Universe, px_y_UL_Universe, px_x_LR_Universe, px_y_LR_Universe;
    double px_UniverseWidth, px_UniverseHeight;
    double px_UBT;  //  OutsideTheAandB band thickness
    
    double universeHalfWidth, successHalfWidth;
    
    double px_MaxHorizontalOfProbs;
    
    public double probA, probB, probAandB, probAorB;
    public double probNotA, probNotB, probAandNotB, probBandNotA, probNotAandB;
    public double probAGivenB, probAGivenNotB, probBGivenA, probBGivenNotA, probNotAandNotB,
                  probNotAGivenB, probNotAGivenNotB, probNotBGivenA, probNotBGivenNotA,
                  probAorNotB, probNotAorB, probNotAorNotB; 
    
    double  px_x0, px_x11, px_x12, px_x21, px_x22, px_x23, px_x24,
            px_y0, px_y11, px_y12, px_y21, px_y22, px_y23, px_y24, 
            probToPixels;

    double px_ProbA, px_ProbB, px_ProbNotA, px_ProbNotB, px_ProbBGivenA,
           px_ProbNotBGivenA, px_ProbBGivenNotA, px_ProbNotBGivenNotA;
  
    double angleAlpha, cosAlpha, sinAlpha, angleBeta, cosBeta, sinBeta;

    // My classes
    MyRectangle rect_Universe;
    Tree_FullMonte tree_FullMonte;
    
    //  FX Classes
    Color clr_Universe, clr_Text, clr_Yes;
    Pane pane;
    Polygon[] polyUniverse, polySuccess;
    Text txtProbA, txtProbNotA, txtProbB, txtProbNotB, txtProbBGivenA, 
         txtProbNotBGivenA,
         txtProbBGivenNotA, txtProbNotBGivenNotA;
            

    public Tree_View(Tree_FullMonte tree_FullMonte) {
        this.tree_FullMonte = tree_FullMonte;
        pane = new Pane();
        
        clr_Universe =  tree_FullMonte.getUniverseColor();
        clr_Text = tree_FullMonte.getTextColor();
        clr_Yes = tree_FullMonte.getYesColor();

        probA = tree_FullMonte.getProbA();
        probNotA = tree_FullMonte.getProbNotA();
        probB = tree_FullMonte.getProbB();
        probNotB = tree_FullMonte.getProbNotB();
        probAandB = tree_FullMonte.getProbAandB();
        probAorB = tree_FullMonte.getProbAorB();
        probAandNotB = tree_FullMonte.getProbAandNotB();
        probAorNotB = tree_FullMonte.getProbAorNotB();
        probNotAandB = tree_FullMonte.getProbNotAandB();
        probNotAorB = tree_FullMonte.getProbNotAorB();
        probAGivenB = tree_FullMonte.getProbAGivenB();
        probBGivenA = tree_FullMonte.getProbBGivenA();
        probNotAGivenB = tree_FullMonte.getProbNotAGivenB();
        probNotBGivenA = tree_FullMonte.getProbNotBGivenA();
        probNotAGivenNotB = tree_FullMonte.getProbNotAGivenNotB();
        probNotBGivenNotA = tree_FullMonte.getProbNotBGivenNotA();

        angleAlpha = 45.0;
        angleBeta = 25.0;
        double radianAlpha = angleAlpha * Math.PI / 180.;
        double radianBeta = angleBeta * Math.PI / 180.;
        cosAlpha = Math.cos(radianAlpha);
        sinAlpha = Math.sin(radianAlpha);
        cosBeta = Math.cos(radianBeta);
        sinBeta = Math.sin(radianBeta);
        
        probToPixels = 350.0;
        
        bUniverse = new boolean[6];
        bSuccess = new boolean[6];
        polyUniverse = new Polygon[6];
        polySuccess = new Polygon[6];   
        
        universeHalfWidth = 3;
        successHalfWidth = 1;

        theCombo = tree_FullMonte.getTheCombo();
        initializeStuff();
        constructTheUniverse();
        makeTheTriangles();
        makeThePolynomials();
    }
    
    private void makeThePolynomials() {
        polySuccess[0] = makeAPolygon(px_x0, px_y0, px_x11, px_y11, successHalfWidth, clr_Yes);
        polySuccess[1] = makeAPolygon(px_x0, px_y0, px_x12, px_y12, successHalfWidth, clr_Yes);
        polySuccess[2] = makeAPolygon(px_x11, px_y11, px_x21, px_y21, successHalfWidth, clr_Yes);
        polySuccess[3] = makeAPolygon(px_x11, px_y11, px_x22, px_y22, successHalfWidth, clr_Yes);
        polySuccess[4] = makeAPolygon(px_x12, px_y12, px_x23, px_y23, successHalfWidth, clr_Yes);
        polySuccess[5] = makeAPolygon(px_x12, px_y12, px_x24, px_y24, successHalfWidth, clr_Yes);
        
        polyUniverse[0] = makeAPolygon(px_x0, px_y0, px_x11, px_y11, universeHalfWidth, clr_Universe);
        polyUniverse[1] = makeAPolygon(px_x0, px_y0, px_x12, px_y12, universeHalfWidth, clr_Universe);
        polyUniverse[2] = makeAPolygon(px_x11, px_y11, px_x21, px_y21, universeHalfWidth, clr_Universe);
        polyUniverse[3] = makeAPolygon(px_x11, px_y11, px_x22, px_y22, universeHalfWidth, clr_Universe);
        polyUniverse[4] = makeAPolygon(px_x12, px_y12, px_x23, px_y23, universeHalfWidth, clr_Universe);
        polyUniverse[5] = makeAPolygon(px_x12, px_y12, px_x24, px_y24, universeHalfWidth, clr_Universe);
    }
    
    public void doTheDeed() {
        theCombo = tree_FullMonte.getTheCombo();
        
        clr_Universe =  tree_FullMonte.getUniverseColor();
        clr_Text = tree_FullMonte.getTextColor();
        clr_Yes = tree_FullMonte.getYesColor();
        
        switch (theCombo) {
            case   2: drawProb_AandB(); break;
            case 200: drawProb_AandB(); break;
            case   3: drawProb_AandNotB(); break;
            case 300: drawProb_AandNotB(); break;
            case  13: drawProb_AorNotB(); break;
            case 102: drawProb_NotAandB(); break;
            case 122: drawProb_NotAGivenB(); break;
            case 123: drawProb_NotAGivenNotB(); break;
            case 201: drawProb_NotAandB(); break;
            case  22: drawProb_AGivenB(); break;
            case  23: drawProb_AGivenNotB(); break;
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
                drawSillyProb();
                //  Silly message
        }
    }

    private void initializeStuff() {
        // px => in pixels
        px_x_UL_Universe = 25.0;
        px_y_UL_Universe = 25.0;
        px_x_LR_Universe = 635.;
        px_y_LR_Universe = 550.;
        px_UBT = 5;    // Inset of table into universe
  
        rect_Universe = new MyRectangle(px_x_UL_Universe, px_y_UL_Universe, 
                                       px_x_LR_Universe, px_y_LR_Universe); 
        
        px_UniverseWidth = px_x_LR_Universe - px_x_UL_Universe;
        px_UniverseHeight = px_y_LR_Universe - px_y_UL_Universe;
    }
    
    private void constructTheUniverse() {
        rect_Universe.setStrokeWidth(2);
        rect_Universe.setStroke(Color.BLUE);
        pane.getChildren().addAll(rect_Universe.getTheRectBorder());    
    }  
    
    private void makeTheTriangles() {
        px_ProbA = probToPixels * probA;
        px_ProbB = probToPixels * probB;
        px_ProbNotA = probToPixels * probNotA;
        px_ProbNotB = probToPixels * probNotB;
        px_ProbBGivenA = probToPixels * probBGivenA;
        px_ProbBGivenNotA = probToPixels * probBGivenNotA;
        px_ProbNotBGivenA = probToPixels * probNotBGivenA;
        px_ProbNotBGivenNotA = probToPixels * probBGivenNotA;
        px_ProbNotBGivenNotA = probToPixels * probNotBGivenNotA;        
        
        px_x0 = px_x_UL_Universe + 0.025 * px_UniverseWidth; 
        px_y0 = px_y_UL_Universe + 0.5 * px_UniverseHeight;
        
        px_x11 = px_x0 + px_ProbA * cosAlpha;
        px_y11 = px_y0 - px_ProbA * sinAlpha;
        
        px_x12 = px_x0 + px_ProbNotA * cosAlpha;
        px_y12 = px_y0 + px_ProbNotA * sinAlpha;

        px_x21 = px_x0 + px_x11 + px_ProbBGivenA * cosBeta;
        px_y21 = px_y11 - px_ProbBGivenA * sinBeta;
        
        px_x22 = px_x0 + px_x11 + px_ProbNotBGivenA * cosBeta;
        px_y22 = px_y11 + px_ProbNotBGivenA * sinBeta;
        
        px_x23 = px_x0 + + px_x12 + px_ProbBGivenNotA * cosBeta;
        px_y23 = px_y12 - px_ProbBGivenNotA * sinBeta;
        
        px_x24 = px_x0 + px_x12 + px_ProbNotBGivenNotA * cosBeta;
        px_y24 = px_y12 + px_ProbNotBGivenNotA * sinBeta;
        
        px_MaxHorizontalOfProbs = px_x21;
        px_MaxHorizontalOfProbs = Double.max(px_MaxHorizontalOfProbs, px_x22);
        px_MaxHorizontalOfProbs = Double.max(px_MaxHorizontalOfProbs, px_x23);
        px_MaxHorizontalOfProbs = Double.max(px_MaxHorizontalOfProbs, px_x24);
    }    
    
    private Polygon makeAPolygon(double x_a, double y_a, 
                                      double x_b, double y_b,
                                      double daHalfWidth,
                                      Color daColor) {
        
        Polygon polly = new Polygon(x_a, y_a - daHalfWidth,
                                    x_a, y_a + daHalfWidth,
                                    x_b, y_b + daHalfWidth,
                                    x_b, y_b - daHalfWidth);
        polly.setFill(daColor);
        polly.setStroke(daColor);
        return polly;  
    }
    
    /*************************************************
    *          Universe and Success Lines            *
    *              Line 0:  ProbA                    *
    *              Line 1:  ProbNotA                 *
    *              Line 2:  ProbBGivenA              *
    *              Line 3:  ProbNotBGivenA           *
    *              Line 4:  ProbBGivenNotA           *
    *              Line 5:  ProbNotBGivenNotA        *
    *************************************************/  
      
    private void drawProb_AandB() { //  Checked
        tree_FullMonte.setTextDescription("A and B");
        startOver();
        int[] theSuccesses = {0, 2};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse);
    }

    private void drawProb_AorB() {  //  Checked
        tree_FullMonte.setTextDescription("A or B");   //  Checked
        startOver();
        int[] theSuccesses = {0, 1, 2, 3, 4};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse);    
    }
    
    private void drawProb_NotAorB() { 
        tree_FullMonte.setTextDescription("not A or B");
        startOver();
        
        int[] theSuccesses = {0, 1, 2, 4, 5};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse); 
    }
    
    private void drawProb_AandNotB() { 
        tree_FullMonte.setTextDescription("A and not B");
        startOver();
        
        int[] theSuccesses = {0, 3};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse); 
    }
    
    private void drawProb_NotAandB() { 
        tree_FullMonte.setTextDescription("not A and B");
        startOver();
        int[] theSuccesses = {1, 4};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse);        
    }
    
    private void drawProb_NotAandNotB() {   //  Checked
        tree_FullMonte.setTextDescription("not A and not B");
        startOver();
        
        int[] theSuccesses = {1, 5};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse); 
    }
    
    private void drawProb_AorNotB() {  
        tree_FullMonte.setTextDescription("A or not B");
        startOver();
        
        int[] theSuccesses = {0, 1, 2, 3, 5};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse); 
    }
    
    private void drawProb_NotAorNotB() { 
        tree_FullMonte.setTextDescription("not A or not B");
        startOver();
        
        int[] theSuccesses = {1, 3, 4, 5};
        int[] theUniverse = {0, 1, 2, 3, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse); 
    }
        
    private void drawProb_AGivenB() { 
        tree_FullMonte.setTextDescription("A given B");
        startOver();
        
        int[] theSuccesses = {0, 2};
        int[] theUniverse = {0, 1, 2, 4};
        constructSuccAndUnivLines(theSuccesses, theUniverse); 
    }
    
    private void drawProb_BGivenA() { 
        tree_FullMonte.setTextDescription("B given A");
        startOver();
        
        int[] theSuccesses = {0, 2};
        int[] theUniverse = {0, 2, 3};
        constructSuccAndUnivLines(theSuccesses, theUniverse);
    }
    
    private void drawProb_AGivenNotB() { 
        tree_FullMonte.setTextDescription("A given not B");
        startOver();
        
        int[] theSuccesses = {0, 3};
        int[] theUniverse = {0, 1, 3, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse);
    }
    
    private void drawProb_BGivenNotA() { 
        tree_FullMonte.setTextDescription("B given not A");
        startOver();
        
        int[] theSuccesses = {1, 4};
        int[] theUniverse = {1, 4, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse);
    }
    
    private void drawProb_NotAGivenB() { 
        tree_FullMonte.setTextDescription("not A Given B");
        startOver();
        
        int[] theSuccesses = {1, 4};
        int[] theUniverse = {0, 1, 2, 4};
        constructSuccAndUnivLines(theSuccesses, theUniverse);
    }
    
    private void drawProb_NotAGivenNotB() { 
        tree_FullMonte.setTextDescription("not A Given not B");
        startOver();
        
        int[] theSuccesses = {1, 5};
        int[] theUniverse = {0, 1, 3, 5};
        constructSuccAndUnivLines(theSuccesses, theUniverse);
    }
    
    private void constructSuccAndUnivLines(int[] theSuccesses, int[] theUniverse) {
        for (int ithSucc = 0; ithSucc < theSuccesses.length; ithSucc++) {
            bSuccess[theSuccesses[ithSucc]] = true;
        }
        for (int ithUniv = 0; ithUniv < theUniverse.length; ithUniv++) {
            bUniverse[theUniverse[ithUniv]] = true;
        }
        
        drawTheUniverse();
        drawTheSuccesses();
    }
    
    private void drawSillyProb() { 
        tree_FullMonte.setTextDescription("drawSillyProb");
        startOver(); 
        drawTheUniverse();
        drawTheSuccesses();
    }

    
    private void drawTheSuccesses() {
        double px_xm, px_ym, arcTan;
        // First remove all the successes
        if (txtProbAIsDrawn) {
            pane.getChildren().remove(txtProbA); txtProbAIsDrawn = false;
        }
        
        if (txtProbNotAIsDrawn) {
            pane.getChildren().remove(txtProbNotA); txtProbNotAIsDrawn = false;
        }
        
        if (txtProbBIsDrawn) {
            pane.getChildren().remove(txtProbB); txtProbBIsDrawn = false;
        }
        
        if (txtProbNotBIsDrawn) {
            pane.getChildren().remove(txtProbNotB); txtProbNotBIsDrawn = false;
        }
        
        if (txtProbBGivenAIsDrawn) {
            pane.getChildren().remove(txtProbBGivenA); txtProbBGivenAIsDrawn = false;
        }
        
        if (txtProbNotBGivenAIsDrawn) {
            pane.getChildren().remove(txtProbNotBGivenA); txtProbNotBGivenAIsDrawn = false;
        }
        
        if (txtProbBGivenNotAIsDrawn) {
            pane.getChildren().remove(txtProbBGivenNotA); txtProbBGivenNotAIsDrawn = false;
        }
        
        if (txtProbNotBGivenNotAIsDrawn) {
            pane.getChildren().remove(txtProbNotBGivenNotA); txtProbNotBGivenNotAIsDrawn = false;
        }
           
        // Now draw the successes
        if (bSuccess[0] == true) {
            pane.getChildren().add(polySuccess[0]);
            px_xm = 0.5 * (px_x0 + px_x11);
            px_ym = 0.5 * (px_y0 + px_y11) - 30;
            arcTan = Math.atan((px_y0 - px_y11) / (px_x11 - px_x0));
            txtProbA = new Text("A");
            txtProbA.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            txtProbA.getTransforms().addAll(new Translate(px_xm, px_ym), new Rotate(arcTan));
            pane.getChildren().add(txtProbA);
            txtProbAIsDrawn = true;
        }
        if (bSuccess[1] == true) {
            pane.getChildren().add(polySuccess[1]);
            px_xm = 0.5 * (px_x0 + px_x12);
            px_ym = 0.5 * (px_y0 + px_y12) - 20;
            arcTan = Math.atan((px_y11 - px_y21) / (px_x12 - px_x0));
            txtProbNotA = new Text("not A");
            txtProbNotA.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            txtProbNotA.getTransforms().addAll(new Translate(px_xm, px_ym), new Rotate(arcTan));
            pane.getChildren().add(txtProbNotA);
            txtProbNotAIsDrawn = true;
        }
        if (bSuccess[2] == true) {
            pane.getChildren().add(polySuccess[2]);
            px_xm = 0.5 * (px_x11 + px_x21) - 10;
            px_ym = 0.5 * (px_y11 + px_y21) - 20;
            arcTan = Math.atan((px_y0 - px_y11) / (px_x21 - px_x11));
            txtProbBGivenA = new Text("B|A");
            txtProbBGivenA.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            txtProbBGivenA.getTransforms().addAll(new Translate(px_xm, px_ym), new Rotate(arcTan));
            pane.getChildren().add(txtProbBGivenA);
            txtProbBGivenAIsDrawn = true;
        }
        if (bSuccess[3] == true) {
            pane.getChildren().add(polySuccess[3]);
            px_xm = 0.5 * (px_x11 + px_x22);
            px_ym = 0.5 * (px_y11 + px_y22) - 20;
            arcTan = Math.atan((px_y0 - px_y11) / (px_x22 - px_x11));
            txtProbNotBGivenA = new Text("not B | A");
            txtProbNotBGivenA.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            txtProbNotBGivenA.getTransforms().addAll(new Translate(px_xm, px_ym), new Rotate(arcTan));
            pane.getChildren().add(txtProbNotBGivenA);
            txtProbNotBGivenAIsDrawn = true;
        }
        if (bSuccess[4] == true) {
            pane.getChildren().add(polySuccess[4]);
            px_xm = 0.5 * (px_x12 + px_x23) - 40;
            px_ym = 0.5 * (px_y12 + px_y23) - 20;
            arcTan = Math.atan((px_y12 - px_y23) / (px_x23 - px_x12));
            txtProbBGivenNotA = new Text("B | not A");
            txtProbBGivenNotA.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            txtProbBGivenNotA.getTransforms().addAll(new Translate(px_xm, px_ym), new Rotate(arcTan));
            pane.getChildren().add(txtProbBGivenNotA);
            txtProbBGivenNotAIsDrawn = true;
        }
        if (bSuccess[5] == true) {
            polySuccess[5] = makeAPolygon(px_x12, px_y12, px_x24, px_y24, successHalfWidth, clr_Yes);
            pane.getChildren().add(polySuccess[5]);
            px_xm = 0.5 * (px_x12 + px_x24) - 40;
            px_ym = 0.5 * (px_y12 + px_y24) - 20;
            arcTan = Math.atan((px_y12 - px_y24) / (px_x24 - px_x12));
            txtProbNotBGivenNotA = new Text("not B | not A");
            txtProbNotBGivenNotA.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            txtProbNotBGivenNotA.getTransforms().addAll(new Translate(px_xm, px_ym), new Rotate(arcTan));
            pane.getChildren().add(txtProbNotBGivenNotA);
            txtProbNotBGivenNotAIsDrawn = true;
        }
    }
    
    private void drawTheUniverse () {
        for (int univ = 0; univ < 6; univ++) {
            if (bUniverse[univ] == true) {
                pane.getChildren().add(polyUniverse[univ]);
            }            
        } 
    }
    
    public void startOver() {
        for (int ithPoly = 0; ithPoly < 6; ithPoly++) {

            if (bUniverse[ithPoly]) {
                pane.getChildren().remove(polyUniverse[ithPoly]);
                bUniverse[ithPoly] = false;
            }
            if (bSuccess[ithPoly]) {
                pane.getChildren().remove(polySuccess[ithPoly]);
                bSuccess[ithPoly] = false;
            }
            bUniverse[ithPoly] = false;
            bSuccess[ithPoly] = false;
        }  
    }  
    
    private void makeAllUniversesTrue() {
        for(int univ = 0; univ < 6; univ++) {bUniverse[univ] = true; }
    }
    
    public Pane getPane() { return pane; }
    
    private void printTheValues() {
        System.out.println("probA = " + probA);
        System.out.println("probB = " + probB);
        
        System.out.println("\nprobAandB = " + probAandB);
        System.out.println("probAandNotB = " + probAandNotB);
        System.out.println("probNotAandB = " + probNotAandB);
        System.out.println("probNotAandNotB = " + probNotAandNotB);
        
        System.out.println("\nprobAGivenB = " + probAGivenB);
        System.out.println("probNotAGivenB = " + probNotAGivenB);
        System.out.println("probAGivenNotB = " + probAGivenNotB);        
        System.out.println("probNotAGivenNotB = " + probNotAGivenNotB);
        
        System.out.println("\nprobBGivenA = " + probBGivenA);
        System.out.println("probNotBGivenA = " + probNotBGivenA);
        System.out.println("probBGivenNotA = " + probBGivenNotA);        
        System.out.println("probNotBGivenNotA = " + probNotBGivenNotA);
        
        System.out.println("\npx_ProbA = " + px_ProbA);
        System.out.println("px_ProbNotA = " + px_ProbNotA);
        System.out.println("px_ProbB = " + px_ProbB);
        System.out.println("px_ProbnotB = " + px_ProbNotB);

        System.out.println("\npx_ProbBGivenA = " + px_ProbBGivenA);
        System.out.println("px_ProbNotBGivenA = " + px_ProbNotBGivenA);
        System.out.println("px_ProbBGivenNotA = " + px_ProbBGivenNotA);        
        System.out.println("px_ProbNotBGivenNotA = " + px_ProbNotBGivenNotA);
    }
}
