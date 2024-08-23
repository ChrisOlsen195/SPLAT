/****************************************************************************
 *                              Lens                                        *
 *                            03/26/22                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.shape.Line;

public class Lens {
        
        boolean lensIsDrawn;
        double chordX, chordRange;
        
        Line[] lensLines;
        
        MyCircle lc, rc;
        Venn_View vennView;
        
        public Lens(Venn_View vennView, MyCircle lc, MyCircle rc) {
            //System.out.println("Constructing a lens...");
            this.vennView = vennView;
            this.lc = lc;
            this.rc = rc;
            lensLines = new Line[500];
            chordRange =  lc.get_03_OClock().getFirstValue() - rc.get_09_OClock().getFirstValue();
            fillTheLens();
        }
        
        private void fillTheLens() {
            //System.out.println("Filling the lens...");
            lensIsDrawn = true;
            for (int ithLine = 0; ithLine < 500; ithLine++) {
                double lensFrac = (double)ithLine / 500.0;
                double px_FracAcrossTheLens = rc.get_09_OClock().getFirstValue() + lensFrac * chordRange;
                lc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                rc.calculateTopAndBottomAt(px_FracAcrossTheLens);
                double lc_lensBottom = lc.getLowerY_At_xpx();
                double lc_lensTop = lc.getUpperY_At_xpx();
                double rc_lensBottom = rc.getLowerY_At_xpx();
                double rc_lensTop = rc.getUpperY_At_xpx();
                double px_LensTop = Math.max(lc_lensTop, rc_lensTop);
                double px_LensBottom = Math.min(lc_lensBottom, rc_lensBottom);
                
                lensLines[ithLine] = new Line(px_FracAcrossTheLens,
                                          px_LensTop,
                                          px_FracAcrossTheLens,
                                          px_LensBottom);
            }              
        }
    
    public Line[] getLensLines() { return lensLines; }
        
    }   //  End class theLens
