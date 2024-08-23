/****************************************************************************
 *                     Tree_OutsideTheAandB                                 *
 *                            03/26/22                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Tree_OutsideTheAandB {
    
        boolean universeIsDrawn;
        
        double px_Chord_x, pxLeftEndOfUniverse, pxRightEndOfUniverse, 
               pxTopOfUniverse, pxBottomOfUniverse, lc_09, rc_03,  
               px_lc_Bottom, px_lc_Top, px_rc_Bottom, px_rc_Top,   
               pxLeftOfCirclesRange, pxRightOfCirclesRange,
               pxInLeftCircleRange, pxInRightCircleRange;

        // FX classes
        Color colorOutsideAandB;
        Line[] leftOfCircles, rightOfCircles, lc_Upper, rc_Upper, lc_Lower,
               rc_Lower;
    
    public Tree_OutsideTheAandB(Tree_View tree_View) {
        /*
        this.lc = lc;
        this.rc = rc;
        
        lc.printTheCircleInfo();
        rc.printTheCircleInfo();
        
        px_Chord_x = venn_View.get_pxChord_x();
        pxLeftEndOfUniverse = venn_View.get_pxLeft();
        pxRightEndOfUniverse = venn_View.get_pxRight();
        pxTopOfUniverse = venn_View.get_pxTop();
        pxBottomOfUniverse = venn_View.get_pxBottom();

        leftOfCircles = new Line[500];
        rightOfCircles = new Line[500];
        lc_Upper = new Line[500];
        lc_Lower = new Line[500];
        rc_Lower = new Line[500];
        rc_Upper = new Line[500];
        */
        fillTheTreeUniverse();
    }
    
    private void fillTheBoxUniverse() { }
    
    private void fillTheTreeUniverse() { }
    
    public void setColor(Color toThis) { colorOutsideAandB = toThis; }
    
    public Line[] get_Lines_leftOfCircles() { return leftOfCircles; } 
    public Line[] get_Lines_rightOfCircles() { return rightOfCircles; }
    public Line[] get_Lines_lc_Lower() { return lc_Lower; }
    public Line[] get_Lines_lc_Upper() { return lc_Upper; }
    public Line[] get_Lines_rc_Lower() { return rc_Lower; }
    public Line[] get_Lines_rc_Upper() { return rc_Upper; }  
}
