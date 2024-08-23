/****************************************************************************
 *                     Table_OutsideTheAandB                                *
 *                            03/19/22                                      *
 *                             21:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Table_OutsideTheAandB {
    
        boolean universeIsDrawn;
        
        double px_Chord_x, pxLeftEndOfUniverse, pxRightEndOfUniverse, 
               pxTopOfUniverse, pxBottomOfUniverse, lc_09, rc_03,  
               px_lc_Bottom, px_lc_Top, px_rc_Bottom, px_rc_Top;
        

        // FX classes
        Color colorOutsideAandB;
        Line[] leftOfCircles, rightOfCircles, lc_Upper, rc_Upper, lc_Lower,
               rc_Lower;
     
    public Table_OutsideTheAandB(Table_View table_View) {
        fillTheBoxUniverse();
    }
    
    private void fillTheBoxUniverse() { }
    
    private void fillTheTreeUniverse() { }
    
    public void setColor(Color toThis) { colorOutsideAandB = toThis; }
}
