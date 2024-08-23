/****************************************************************************
 *                            Universe                                      *
 *                            12/31/21                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.shape.Line;

public class Tree_Universe {
    
        boolean universeIsDrawn;
        
        double px_Chord_x, pxLeftEndOfUniverse, pxRightEndOfUniverse, 
               pxTopOfUniverse, pxBottomOfUniverse, lc_09, rc_03,  
               px_lc_Bottom, px_lc_Top, px_rc_Bottom, px_rc_Top,   
               pxLeftOfCirclesRange, pxRightOfCirclesRange,
               pxInLeftCircleRange, pxInRightCircleRange;
      
    public Tree_Universe(Tree_View tree_View) {
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
     
}
