/****************************************************************************
 *                      Venn_OutsideTheAandB                                *
 *                            03/26/22                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Venn_OutsideTheAandB {
    
        boolean universeIsDrawn;
        
        double px_Chord_x, pxLeftEndOfUniverse, pxRightEndOfUniverse, 
               pxTopOfUniverse, pxBottomOfUniverse, lc_09, rc_03,  
               px_lc_Bottom, px_lc_Top, px_rc_Bottom, px_rc_Top,   
               pxLeftOfCirclesRange, pxRightOfCirclesRange,
               pxInLeftCircleRange, pxInRightCircleRange;
        
        Venn_View venn_View;
        MyCircle lc, rc;

        // FX classes
        Color colorOutsideAandB;
        Line[] leftOfCircles, rightOfCircles, lc_Upper, rc_Upper, lc_Lower,
               rc_Lower;
    
    public Venn_OutsideTheAandB(Venn_View venn_View, MyCircle lc, MyCircle rc) {
        
        this.lc = lc;
        this.rc = rc;
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

        fillTheCircleUniverse();
    }
    
        
    private void fillTheCircleUniverse() {
        universeIsDrawn = true;
        
        //  Left of circles
        pxLeftOfCirclesRange = lc.get_09_OClock().getFirstValue() - pxLeftEndOfUniverse;
        for (int ithLine = 0; ithLine < 500; ithLine++) {
            double leftFrac = (double)ithLine / 500.0;
            double px_FracAcross = pxLeftEndOfUniverse + leftFrac * pxLeftOfCirclesRange;
            leftOfCircles[ithLine] = new Line(px_FracAcross,
                                      pxTopOfUniverse,
                                      px_FracAcross,
                                      pxBottomOfUniverse);
            leftOfCircles[ithLine].setStroke(colorOutsideAandB);
        }   //  End Left of Circles 
        
        // Right of circles
        pxRightOfCirclesRange = pxRightEndOfUniverse - rc.get_03_OClock().getFirstValue();
        for (int ithLine = 0; ithLine < 500; ithLine++) {
            double leftFrac = (double)ithLine / 500.0;
            double px_FracAcross = rc.get_03_OClock().getFirstValue() + leftFrac * pxRightOfCirclesRange;
            rightOfCircles[ithLine] = new Line(px_FracAcross,
                                      pxTopOfUniverse,
                                      px_FracAcross,
                                      pxBottomOfUniverse);
            rightOfCircles[ithLine].setStroke(colorOutsideAandB);
        }   //  End Right of Circles 
        
        
        // Above and below left Circle
            pxInLeftCircleRange = px_Chord_x - lc.get_09_OClock().getFirstValue();
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                double lensFrac = (double)ithLine / 500.0;
                double px_FracAcrossTheLens = lc.get_09_OClock().getFirstValue() + lensFrac * pxInLeftCircleRange;
                lc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                double lc_CircleBottom = lc.getLowerY_At_xpx();
                double lc_CircleTop = lc.getUpperY_At_xpx();
                lc_Upper[ithLine] = new Line(px_FracAcrossTheLens,
                                          pxTopOfUniverse,
                                          px_FracAcrossTheLens,
                                          lc_CircleTop);
                lc_Upper[ithLine].setStroke(colorOutsideAandB);
                
                lc_Lower[ithLine] = new Line(px_FracAcrossTheLens,
                                          lc_CircleBottom,
                                          px_FracAcrossTheLens,
                                          pxBottomOfUniverse);
                lc_Lower[ithLine].setStroke(colorOutsideAandB);
            }   //  End ith line 
            
        // Above and below right Circle
            pxInRightCircleRange = rc.get_03_OClock().getFirstValue() - px_Chord_x;
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                double lensFrac = (double)ithLine / 500.0;
                double px_FracAcrossTheLens = px_Chord_x + lensFrac * pxInRightCircleRange;
                rc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                double rc_CircleBottom = rc.getLowerY_At_xpx();
                double rc_CircleTop = rc.getUpperY_At_xpx();
                rc_Upper[ithLine] = new Line(px_FracAcrossTheLens,
                                          pxTopOfUniverse,
                                          px_FracAcrossTheLens,
                                          rc_CircleTop);
                rc_Upper[ithLine].setStroke(colorOutsideAandB);
                
                rc_Lower[ithLine] = new Line(px_FracAcrossTheLens,
                                          rc_CircleBottom,
                                          px_FracAcrossTheLens,
                                          pxBottomOfUniverse);
                rc_Lower[ithLine].setStroke(colorOutsideAandB);
            }   //  End ith line
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
