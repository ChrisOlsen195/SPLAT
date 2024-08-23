/****************************************************************************
 *                           Right_Lune                                      *
 *                            03/26/22                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.shape.Line;

public class RightLune {
        
        boolean rightLuneIsDrawn;
        double rightEndOfLune, rightLuneRange_01, rightLuneRange_02, px_Chord_x;
        
        double px_lc_lensBottom,px_lc_lensTop, px_rc_lensBottom,px_rc_lensTop;
        double px_BottomOfUpper, px_BottomOfLower, px_TopOfUpper, px_TopOfLower;        

        Line[] rightLune_Lines_01, rightLune_Lines_02Upper, rightLune_Lines_02Lower;
        
        Venn_View venn_View;
        
        MyCircle lc, rc;
        
        public RightLune(Venn_View venn_View, MyCircle lc, MyCircle rc) {
            //System.out.println("Constructing right lune...");
            this.lc = lc;
            this.rc = rc;
            px_Chord_x = venn_View.get_pxChord_x();
            rightLune_Lines_01 = new Line[500];
            rightLune_Lines_02Upper = new Line[500];
            rightLune_Lines_02Lower = new Line[500];
            fillTheLune();
        }
        
        public void fillTheLune() {
            //System.out.println("Filling right lune...");
            rightLuneIsDrawn = true;
            rightLuneRange_01 = rc.get_03_OClock().getFirstValue() - lc.get_03_OClock().getFirstValue();
            rightLuneRange_02 = lc.get_03_OClock().getFirstValue() - px_Chord_x;
            
            // One circle

            for (int ithLine = 0; ithLine < 500; ithLine++) {
                double lensFrac = (double)ithLine / 500.0;
                double px_FracAcrossTheLens = lc.get_03_OClock().getFirstValue() + lensFrac * rightLuneRange_01;
                lc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                rc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                double rc_lensBottom = rc.getLowerY_At_xpx();
                double rc_lensTop = rc.getUpperY_At_xpx();
                rightLune_Lines_01[ithLine] = new Line(px_FracAcrossTheLens,
                                          rc_lensBottom,
                                          px_FracAcrossTheLens,
                                          rc_lensTop);
            }   //  End ith line   

            // Circle upper and lower  -- right lune

            for (int ithLine = 0; ithLine < 500; ithLine++) {
                double lensFrac = (double)ithLine / 500.0;
                double px_FracAcrossTheLens = px_Chord_x + lensFrac * rightLuneRange_02;
                lc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                rc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                
                px_lc_lensBottom = lc.getLowerY_At_xpx();
                px_lc_lensTop = lc.getUpperY_At_xpx();
                px_rc_lensBottom = rc.getLowerY_At_xpx();
                px_rc_lensTop = rc.getUpperY_At_xpx();
                
                px_BottomOfUpper  = Math.max(px_lc_lensTop, px_rc_lensTop);
                px_TopOfUpper  = Math.min(px_lc_lensTop, px_rc_lensTop);
                
                px_BottomOfLower  = Math.max(px_lc_lensBottom, px_rc_lensBottom);
                px_TopOfLower  = Math.min(px_lc_lensBottom, px_rc_lensBottom);
                
                //printTheLooneyInfo();
                
                rightLune_Lines_02Upper[ithLine] = new Line(px_FracAcrossTheLens,
                                          px_TopOfUpper,
                                          px_FracAcrossTheLens,
                                          px_BottomOfUpper);
                rightLune_Lines_02Lower[ithLine] = new Line(px_FracAcrossTheLens,
                                          px_TopOfLower,
                                          px_FracAcrossTheLens,
                                          px_BottomOfLower);
                
            }   //  End ith line
        } 
        
        public Line[] get_Lines_01() { return rightLune_Lines_01; } 
        public Line[] get_Lines_02Upper() { return rightLune_Lines_02Upper; }
        public Line[] get_Lines_02Lower() { return rightLune_Lines_02Lower; }
        
    }   // End right lune
