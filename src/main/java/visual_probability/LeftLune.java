/****************************************************************************
 *                           Left_Lune                                      *
 *                            03/26/22                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;
import javafx.scene.shape.Line;

 public class LeftLune {
        
        boolean leftLuneIsDrawn;
        double px_Chord_x, leftEndOfLune, leftLuneRange_01, leftLuneRange_02;
        
        double px_lc_lensBottom,px_lc_lensTop, px_rc_lensBottom,px_rc_lensTop;
        double px_BottomOfUpper, px_BottomOfLower, px_TopOfUpper, px_TopOfLower;   
        
        Venn_View venn_View;
        MyCircle lc, rc;

        Line[] leftLune_Lines_01, leftLune_Lines_02Upper, leftLune_Lines_02Lower;
        
        public LeftLune(Venn_View venn_View, MyCircle lc, MyCircle rc) { 
            //System.out.println("Constructing left lune...");
            
            this.venn_View = venn_View;
            this.lc = lc;
            this.rc = rc;
            px_Chord_x = venn_View.get_pxChord_x();
            leftLune_Lines_01 = new Line[500];
            leftLune_Lines_02Upper = new Line[500];
            leftLune_Lines_02Lower = new Line[500];
            fillTheLune();
        }
        
        private void fillTheLune() {
            //System.out.println("Filling left lune...");
            leftLuneIsDrawn = true;
            leftLuneRange_01 = rc.get_09_OClock().getFirstValue() - lc.get_09_OClock().getFirstValue();
            leftLuneRange_02 = px_Chord_x - rc.get_09_OClock().getFirstValue();
            //System.out.println("40 leftlune, px_Chord_x = " + px_Chord_x);
            // One circle -- left lune
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                double lensFrac = (double)ithLine / 500.0;
                double px_FracAcrossTheLens = lc.get_09_OClock().getFirstValue() + lensFrac * leftLuneRange_01;
                lc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                rc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                double lc_lensBottom = lc.getLowerY_At_xpx();
                double lc_lensTop = lc.getUpperY_At_xpx();
                leftLune_Lines_01[ithLine] = new Line(px_FracAcrossTheLens,
                                          lc_lensBottom,
                                          px_FracAcrossTheLens,
                                          lc_lensTop);
            }   //  End ith line   
            // Circle upper and lower  -- left lune
            //System.out.println("55 leftlune, leftLuneRange_02 = " + leftLuneRange_02);
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                double lensFrac = (double)ithLine / 500.0;
                double px_FracAcrossTheLens = rc.get_09_OClock().getFirstValue() + lensFrac * leftLuneRange_02;
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
                
                leftLune_Lines_02Upper[ithLine] = new Line(px_FracAcrossTheLens,
                                          px_BottomOfUpper,
                                          px_FracAcrossTheLens,
                                          px_TopOfUpper);
                leftLune_Lines_02Lower[ithLine] = new Line(px_FracAcrossTheLens,
                                          px_BottomOfLower,
                                          px_FracAcrossTheLens,
                                          px_TopOfLower);
            }   //  End ith line
        }
           
        
        public Line[] get_Lines_01() { return leftLune_Lines_01; } 
        public Line[] get_Lines_02Upper() { return leftLune_Lines_02Upper; }
        public Line[] get_Lines_02Lower() { return leftLune_Lines_02Lower; }       
    }
