/****************************************************************************
 *                           Venn_View                                      *
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
import genericClasses.Point_2D;

public class Venn_View {
    
    // POJOs
    
    int theCombo;
    
    boolean leftLuneIsDrawn, rightLuneIsDrawn, bothCirclesDrawn, lensIsDrawn, 
            leftCircleIsDrawn, rightCircleIsDrawn;  
    
    boolean boxIsDrawn, universeIsDrawn;
    
    double sizeFactor, lc_pxCenter_x, lc_pxCenter_y, rc_pxCenter_x, 
           rc_pxCenter_y, arcFactor, px_Chord_x,
           pxLeft, pxRight, pxTop, pxBottom, pxHoriz, pxVert,
           px_Separation, px_LuneRange;
    
    public double probA, probB, probAandB, probAorB;
    public double probNotA, probNotB, probAandNotB, probBandNotA, probNotAandB;
    public double probAGivenB, probAGivenNotB, probBGivenA, probBGivenNotA, probNotAandNotB,
                  probNotAGivenB, probNotAGivenNotB, probNotBGivenA, probNotBGivenNotA,
                  probAorNotB, probNotAorB, probNotAorNotB; 

    // My classes
    LeftLune l_Lune;
    Lens lens;

    MyCircle lc, rc;    //  left Circle, right Circle
    Point_2D lcCenter, rcCenter, boxDim, pxBoxDim;
    RightLune r_Lune;
    Text leftCircleTitle, rightCircleTitle;
    Venn_Universe universe;
    Venn_Model venn_Model;
    Venn_FullMonte venn_FullMonte;
    
    //  FX Classes
    Color clr_outsideCircles, clr_LeftLune, 
          clr_RightLune, clr_LeftCircle, clr_RightCircle, clr_Lens;
    Color clr_Universe, clr_Text, clr_Yes, clr_White;
    
    Line line_BoxTop, line_BoxLeft, line_BoxRight, line_BoxBottom;
    
    Line[] lines_LC, lines_RC, lines_Lens, leftLune_Lines_01, 
           leftLune_Lines_02Upper, leftLune_Lines_02Lower, rightLune_Lines_01, 
           rightLune_Lines_02Upper, rightLune_Lines_02Lower,   
           lines_LeftOfCircles, lines_RightOfCircles, lines_AboveLeftCircle,
           lines_BelowLeftCircle, lines_AboveRightCircle, lines_BelowRightCircle;
    
    Pane pane;

    public Venn_View(Venn_FullMonte venn_FullMonte) {
        this.venn_FullMonte = venn_FullMonte;
        pane = new Pane();
        probA = venn_FullMonte.getProbA();
        probNotA = venn_FullMonte.getProbNotA();
        probB = venn_FullMonte.getProbB();
        probNotB = venn_FullMonte.getProbNotB();
        probAandB = venn_FullMonte.getProbAandB();
        probAorB = venn_FullMonte.getProbAorB();
        probAandNotB = venn_FullMonte.getProbAandNotB();
        probAorNotB = venn_FullMonte.getProbAorNotB();
        probNotAandB = venn_FullMonte.getProbNotAandB();
        probNotAorB = venn_FullMonte.getProbNotAorB();
        probAGivenB = venn_FullMonte.getProbAGivenB();
        probBGivenA = venn_FullMonte.getProbBGivenA();
        probNotAGivenB = venn_FullMonte.getProbNotAGivenB();
        probNotBGivenA = venn_FullMonte.getProbNotBGivenA();
        probNotAGivenNotB = venn_FullMonte.getProbNotAGivenNotB();
        probNotBGivenNotA = venn_FullMonte.getProbNotBGivenNotA();
        initializeStuff();
        venn_Model= new Venn_Model(sizeFactor);
        venn_Model.setProbA(probA);
        venn_Model.setProbB(probB);
        venn_Model.setProbAandB(probAandB);
        px_Separation = venn_Model.calculate_pxCenterSeparation(); 
        arcFactor = 2.0 * Math.PI / 1000.0;
        px_Chord_x = lc_pxCenter_x + sizeFactor * venn_Model.get_chord_var_X();
    }
    
    public void doTheDeed() {
        theCombo = venn_FullMonte.getTheCombo();
        createTheMasterCard(); 
        
        clr_Universe =  venn_FullMonte.getUniverseColor();
        clr_Text = venn_FullMonte.getTextColor();
        clr_Yes = venn_FullMonte.getYesColor();
        
        px_LuneRange = Math.max(lc.getVertRange(), rc.getVertRange());
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
            case  22: drawProb_AGivenB(); break;
            case  23: drawProb_AGivenNotB(); break;
            case 220: drawProb_BGivenA(); break;
            case  12: drawProb_AorB(); break;
            case 210: drawProb_AorB(); break;
            case 112: drawProb_NotAorB(); break;
            case 211: drawProb_NotAorB(); break;
            case 221: drawProb_BGivenNotA(); break;
            case 103: drawProb_NotAandNotB(); break;
            case 301: drawProb_NotAandNotB(); break;
            case 113: drawProb_NotAorNotB(); break;
            case 311: drawProb_NotAorNotB(); break;
            case 321: drawProb_NotBGivenNotA(); break;

            //  Silly choices
            default: 
                startOver();
                //  Silly message
        }
    }
    
    
    private void initializeStuff() {
        lines_LC = new Line[1000];
        lines_RC = new Line[1000];
        lines_Lens = new Line[500];
        leftLune_Lines_01 = new Line[500];
        leftLune_Lines_02Upper = new Line[500];
        leftLune_Lines_02Lower = new Line[500];
        rightLune_Lines_01 = new Line[500];
        rightLune_Lines_02Upper = new Line[500];
        rightLune_Lines_02Lower = new Line[500];   
        lines_LeftOfCircles = new Line[500];  
        lines_RightOfCircles = new Line[500];
        lines_AboveLeftCircle= new Line[500];
        lines_BelowLeftCircle= new Line[500]; 
        lines_AboveRightCircle= new Line[500]; 
        lines_BelowRightCircle= new Line[500];

        /***********************************************
        *           Arbitrary constants mutable        *
        ***********************************************/
        sizeFactor = 400.0;
        lc_pxCenter_x = 250.0;   //  Left circle
        lc_pxCenter_y = 300.0;   //  Left circle

        lcCenter = new Point_2D(lc_pxCenter_x, lc_pxCenter_y);
        clr_outsideCircles = Color.AQUAMARINE;
        clr_LeftCircle = Color.BLACK;
        clr_RightCircle = Color.BLACK;
        clr_LeftLune = Color.BLUEVIOLET;
        clr_RightLune = Color.CADETBLUE;
        clr_Lens = Color.AZURE;
    }
    

    private void drawProb_AandB() { //  Checked
        venn_FullMonte.setTextDescription("A and B");
        startOver();
        setTheColors(clr_Universe, clr_Yes, clr_Universe, clr_Universe);
        doTheAdds();  
    }
    
    private void drawProb_AorB() {  //  Checked
        venn_FullMonte.setTextDescription("A or B");   //  Checked
        startOver();
        setTheColors(clr_Yes, clr_Yes, clr_Yes, clr_Universe);
        doTheAdds();
    }
    
    private void drawProb_NotAorB() {   //  Checked
        venn_FullMonte.setTextDescription("not A or B");
        startOver();
        setTheColors(clr_Universe, clr_Yes, clr_Yes, clr_Yes);
        doTheAdds();  
    }
    
    private void drawProb_AandNotB() {  //  Checked
        venn_FullMonte.setTextDescription("A and not B");
        startOver();
        setTheColors(clr_Yes, clr_Universe, clr_Universe, clr_Universe);
        doTheAdds();
    }
    
    private void drawProb_NotAandB() { 
        venn_FullMonte.setTextDescription("not A and B");
        startOver();
        setTheColors(clr_Universe, clr_Universe, clr_Yes, clr_Universe);
        doTheAdds();
    }
    
    private void drawProb_NotAandNotB() {   //  Checked
        venn_FullMonte.setTextDescription("not A and not B");
        startOver();
        setTheColors(clr_Universe, clr_Universe, clr_Universe, clr_Yes);
        doTheAdds();       
    }
    
    private void drawProb_AorNotB() {   //  Checked
        venn_FullMonte.setTextDescription("A or not B");
        startOver();
        setTheColors(clr_Yes, clr_Yes, clr_Universe, clr_Yes);
        doTheAdds();        
    }
    
    private void drawProb_NotAorNotB() {    //  Checked
        venn_FullMonte.setTextDescription("not A or not B");
        startOver();
        setTheColors(clr_Yes, clr_Universe, clr_Yes, clr_Yes);
        doTheAdds();        
    }

        
    private void drawProb_AGivenB() {   //  Checked
        venn_FullMonte.setTextDescription("A given B");
        startOver();
        setTheColors(clr_White, clr_Yes, clr_Universe, clr_White);
        doTheAdds();
    }
    
    private void drawProb_BGivenA() {   //  Checked
        venn_FullMonte.setTextDescription("B given A");
        startOver();
        setTheColors(clr_Universe, clr_Yes, clr_White, clr_White);
        doTheAdds();
    }
    
    private void drawProb_AGivenNotB() {   //  Checked
        venn_FullMonte.setTextDescription("A given not B");
        startOver();
        setTheColors(clr_Yes, clr_White, clr_White, clr_Universe);
        doTheAdds();
    }
    
    private void drawProb_BGivenNotA() {   //  Checked
        venn_FullMonte.setTextDescription("B given not A");
        startOver();
        setTheColors(clr_White, clr_White, clr_Yes, clr_Universe);
        doTheAdds();
    }
    
    private void drawProb_NotBGivenNotA() {   //  Checked
        venn_FullMonte.setTextDescription("not B given not A");
        startOver();
        setTheColors(clr_White, clr_White, clr_Universe, clr_Yes);
        doTheAdds();
    }

    private void drawProb_NotAGivenB() {    // ********************************
        venn_FullMonte.setTextDescription("not A given B");
        startOver();
        setTheColors(clr_White, clr_Universe, clr_Yes, clr_White);
        doTheAdds();

    }
    
    private void drawProb_NotAGivenNotB() { 
        venn_FullMonte.setTextDescription("not A Given not B");
        startOver();
        setTheColors(clr_Universe, clr_White, clr_White, clr_Yes);
        doTheAdds();
    }
    
    private void createTheMasterCard() {
        rc_pxCenter_x = lc_pxCenter_x + px_Separation;
        rc_pxCenter_y = lc_pxCenter_y;
        rcCenter = new Point_2D(rc_pxCenter_x, lc_pxCenter_y);  
        lc = new MyCircle(this, lc_pxCenter_x, lc_pxCenter_y, probA);
        rc = new MyCircle(this, rc_pxCenter_x, rc_pxCenter_y, probB);  
        lines_LC = lc.getCircleSegments();
        lines_RC = rc.getCircleSegments();
        lens = new Lens(this, lc, rc);
        l_Lune = new LeftLune(this, lc, rc);
        r_Lune = new RightLune(this, lc, rc);
        createTheUniverseBox();
        universe = new Venn_Universe(this, lc, rc);
    }
    
    private void drawBothCircles() {
        addTheLeftCircle();
        addTheRightCircle();
        bothCirclesDrawn = true;
    }
    
    private void addTheLeftCircle() {
        if (!leftCircleIsDrawn) {
            lines_LC = lc.getCircleSegments();
            for (int ithLine = 0; ithLine < 999; ithLine++) {
                lines_LC[ithLine].setStroke(clr_LeftCircle);
                pane.getChildren().add(lines_LC[ithLine]);
            }
        }

    Point_2D lcCoords = lc.getLeftCircleTitleCoords();
    leftCircleTitle = new Text(lcCoords.getFirstValue(), lcCoords.getSecondValue(), "A");
    leftCircleTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    leftCircleTitle.setStroke(venn_FullMonte.getTextColor());
    leftCircleTitle.setFill(venn_FullMonte.getTextColor());  
    pane.getChildren().add(leftCircleTitle);
        leftCircleIsDrawn = true;
    }
    
    private void addTheRightCircle() {
        if (!rightCircleIsDrawn) {
            lines_RC = rc.getCircleSegments();
            for (int ithLine = 0; ithLine < 999; ithLine++) {
                lines_RC[ithLine].setStroke(clr_RightCircle);
                pane.getChildren().add(lines_RC[ithLine]);
            } 
        }
        
    Point_2D lcCoords = rc.getRightCircleTitleCoords();
    rightCircleTitle = new Text(lcCoords.getFirstValue(), lcCoords.getSecondValue(), "B");
    rightCircleTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    rightCircleTitle.setStroke(venn_FullMonte.getTextColor());
    rightCircleTitle.setFill(venn_FullMonte.getTextColor());  
    pane.getChildren().add(rightCircleTitle);
        rightCircleIsDrawn = true;
    }
    
    private void removeTheLeftCircle() {
        if (leftCircleIsDrawn) {
            for (int ithLine = 0; ithLine < 999; ithLine++) {
                pane.getChildren().remove(lines_LC[ithLine]);
            }
        }
        pane.getChildren().remove(leftCircleTitle);
        leftCircleIsDrawn = false;
        bothCirclesDrawn = false;
    }
    
    private void removeTheRightCircle() {
        if (rightCircleIsDrawn) {
            for (int ithLine = 0; ithLine < 999; ithLine++) {
                pane.getChildren().remove(lines_RC[ithLine]);
            }  
        }
        pane.getChildren().remove(rightCircleTitle);
        rightCircleIsDrawn = false;
        bothCirclesDrawn = false;
    }
    
    private void addTheLeftLune() {
        if (!leftLuneIsDrawn) {
            leftLune_Lines_01 = l_Lune.get_Lines_01();
            leftLune_Lines_02Upper = l_Lune.get_Lines_02Upper();
            leftLune_Lines_02Lower = l_Lune.get_Lines_02Lower();
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                pane.getChildren().add(leftLune_Lines_01[ithLine]);
                pane.getChildren().add(leftLune_Lines_02Upper[ithLine]);
                pane.getChildren().add(leftLune_Lines_02Lower[ithLine]); 
                leftLune_Lines_01[ithLine].setStroke(clr_LeftLune);
                leftLune_Lines_02Upper[ithLine].setStroke(clr_LeftLune);
                leftLune_Lines_02Lower[ithLine].setStroke(clr_LeftLune);
            }
        }
        leftLuneIsDrawn = true;
    }
    
    private void removeTheLeftLune() {;
        if (leftLuneIsDrawn) {
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                pane.getChildren().remove(leftLune_Lines_01[ithLine]);
                pane.getChildren().remove(leftLune_Lines_02Upper[ithLine]);
                pane.getChildren().remove(leftLune_Lines_02Lower[ithLine]);
            }   
        }
        leftLuneIsDrawn = false;
    }
    
    private void addTheRightLune() {
        if (!rightLuneIsDrawn) {
            rightLune_Lines_01 = r_Lune.get_Lines_01();
            rightLune_Lines_02Upper = r_Lune.get_Lines_02Upper();
            rightLune_Lines_02Lower = r_Lune.get_Lines_02Lower();
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                pane.getChildren().add(rightLune_Lines_01[ithLine]);
                pane.getChildren().add(rightLune_Lines_02Upper[ithLine]);
                pane.getChildren().add(rightLune_Lines_02Lower[ithLine]);  
                rightLune_Lines_01[ithLine].setStroke(clr_RightLune);
                rightLune_Lines_02Upper[ithLine].setStroke(clr_RightLune);
                rightLune_Lines_02Lower[ithLine].setStroke(clr_RightLune);
            }
        }
        rightLuneIsDrawn = true;
    }
    
    private void removeTheRightLune() { 
        if (rightLuneIsDrawn) {
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                pane.getChildren().remove(rightLune_Lines_01[ithLine]);
                pane.getChildren().remove(rightLune_Lines_02Upper[ithLine]);
                pane.getChildren().remove(rightLune_Lines_02Lower[ithLine]);
            } 
        } 
        rightLuneIsDrawn = false;
    }
    
    private void addTheLens() { 
        if (!lensIsDrawn) {
            lens = new Lens(this, lc, rc);
            lines_Lens = lens.getLensLines();
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                lines_Lens[ithLine].setStroke(clr_Lens);
                pane.getChildren().add(lines_Lens[ithLine]);
            }   
        }
        lensIsDrawn = true;
    }
    
    private void removeTheLens() {
        if (lensIsDrawn) {
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                pane.getChildren().remove(lines_Lens[ithLine]);
            } 
        }
        lensIsDrawn = false;
    }  
    
    private void addTheUniverseBox() {
        if (!boxIsDrawn) {
            pane.getChildren().addAll(line_BoxTop, line_BoxLeft, line_BoxRight, line_BoxBottom); 
        }
        boxIsDrawn = true;
    }
    
    private void removeTheUniverseBox() {
        if (boxIsDrawn) {
            pane.getChildren().removeAll(line_BoxTop, line_BoxLeft, line_BoxRight, line_BoxBottom); 
        }
        boxIsDrawn = false;
    }
    
    private void createTheUniverseBox() {
        pxLeft = lc_pxCenter_x - venn_Model.get_pxRadiusLeft();
        pxRight = rc_pxCenter_x + venn_Model.get_pxRadiusRight();
        double pxDiffHoriz = pxRight - pxLeft;

        pxTop = lc_pxCenter_y - Math.max(venn_Model.get_pxRadiusLeft(), venn_Model.get_pxRadiusRight());
        pxBottom = lc_pxCenter_y + Math.max(venn_Model.get_pxRadiusLeft(), venn_Model.get_pxRadiusRight());
        double pxDiffVert = pxBottom - pxTop;

        pxLeft = pxLeft - .05*pxDiffHoriz;
        pxRight = pxRight + .05*pxDiffHoriz;
        pxTop = pxTop - .05 * pxDiffVert;
        pxBottom = pxBottom + .05 * pxDiffVert;

        line_BoxTop = new Line(pxLeft, pxTop, pxRight, pxTop);
        line_BoxTop.setStrokeWidth(2);
        line_BoxLeft = new Line(pxLeft, pxTop, pxLeft, pxBottom);
        line_BoxLeft.setStrokeWidth(2);
        line_BoxRight = new Line(pxRight, pxTop, pxRight, pxBottom);
        line_BoxRight.setStrokeWidth(2);
        line_BoxBottom = new Line(pxLeft, pxBottom, pxRight, pxBottom);
        line_BoxBottom.setStrokeWidth(2);
        boxIsDrawn = false;
    }

    private void addTheUniverse() {
        if (!universeIsDrawn) {
            lines_LeftOfCircles = universe.get_Lines_leftOfCircles(); 
            lines_RightOfCircles = universe.get_Lines_rightOfCircles();
            lines_BelowLeftCircle = universe.get_Lines_lc_Lower();
            lines_AboveLeftCircle = universe.get_Lines_lc_Upper();
            lines_BelowRightCircle = universe.get_Lines_rc_Lower();
            lines_AboveRightCircle = universe.get_Lines_rc_Upper();

            for (int ithLine = 0; ithLine < 500; ithLine++) {
                lines_LeftOfCircles[ithLine].setStroke(clr_outsideCircles);
                lines_RightOfCircles[ithLine].setStroke(clr_outsideCircles);
                lines_BelowLeftCircle[ithLine].setStroke(clr_outsideCircles);
                lines_AboveLeftCircle[ithLine].setStroke(clr_outsideCircles);
                lines_BelowRightCircle[ithLine].setStroke(clr_outsideCircles);
                lines_AboveRightCircle[ithLine].setStroke(clr_outsideCircles);

                pane.getChildren().add(lines_LeftOfCircles[ithLine]);
                pane.getChildren().add(lines_RightOfCircles[ithLine]);
                pane.getChildren().add(lines_BelowLeftCircle[ithLine]);
                pane.getChildren().add(lines_AboveLeftCircle[ithLine]);
                pane.getChildren().add(lines_BelowRightCircle[ithLine]);
                pane.getChildren().add(lines_AboveRightCircle[ithLine]);
            } 
        }
        universeIsDrawn = true;
    }
    
    private void removeTheUniverse() {
        if (universeIsDrawn) {
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                pane.getChildren().remove(lines_LeftOfCircles[ithLine]);
                pane.getChildren().remove(lines_RightOfCircles[ithLine]);
                pane.getChildren().remove(lines_BelowLeftCircle[ithLine]);
                pane.getChildren().remove(lines_AboveLeftCircle[ithLine]);
                pane.getChildren().remove(lines_BelowRightCircle[ithLine]);
                pane.getChildren().remove(lines_AboveRightCircle[ithLine]);
            } 
        }
        universeIsDrawn = false;
    }
    
    public void startOver() {
        removeTheLeftCircle();
        removeTheRightCircle();
        removeTheLeftLune();
        removeTheRightLune();
        removeTheLens();
        removeTheUniverseBox();
        removeTheUniverse();  
    }
    
    public void setTheColors(Color clr_leftLune, Color clr_Lens, Color clr_RightLune, Color clr_Universe) {
        clr_LeftLune = clr_leftLune;
        this.clr_Lens = clr_Lens;
        this.clr_RightLune = clr_RightLune;
        clr_outsideCircles = clr_Universe;        
    }
    
    public void doTheAdds() {
        addTheLens();
        addTheLeftLune();
        addTheRightLune();
        addTheUniverseBox();
        addTheUniverse();
        drawBothCircles();        
    }

    public void changeUniverseColorTo (Color thisColor) {
        clr_outsideCircles = thisColor;
    }
    
    public void changeTextColorTo (Color thisColor) {
        clr_Text = thisColor;
    }
    
    public void changeYesColorTo (Color thisColor) {
        clr_Yes = thisColor;
    }
    
    public double get_pxChord_x() { return px_Chord_x; }
    public double get_arcFactor() { return arcFactor; }
    public double get_sizeFactor() { return sizeFactor; }   
    
    public double get_pxLeft() { return pxLeft; }
    public double get_pxRight() { return pxRight; }    
    public double get_pxTop() { return pxTop; }
    public double get_pxBottom() { return pxBottom; }
    
    public double get_pxHoriz() { return pxHoriz; }   
    public double get_pxVert() { return pxVert; }  
    
    public Pane getPane() { return pane; }
}

