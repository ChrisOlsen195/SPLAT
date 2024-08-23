/****************************************************************************
 *                            MyCircle                                     *
 *                            03/26/22                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.shape.Line;
import genericClasses.Point_2D;

public class MyCircle {
        boolean circleIsDrawn;
        double px_xCenter, px_yCenter;
        double px_TopAt_xpx, px_BottomAt_xpx;
        double px_Radius, pxVertRange, arcFactor, sizeFactor;
        double px_RadiusSquared;
        double relative_Radius;
        
        Line[] circleSegments;
        Point_2D px_03OClock, px_06OClock, px_09OClock, px_12OClock,
                 leftCircleTitleCoords, rightCircleTitleCoords;
        
        public MyCircle(Venn_View venn_View, double px_xCenter, double px_yCenter, double probability) { 
            this.px_xCenter = px_xCenter;
            this.px_yCenter = px_yCenter;
            sizeFactor = venn_View.get_sizeFactor();
            arcFactor = venn_View.get_arcFactor();
            this.sizeFactor = venn_View.get_sizeFactor();
            relative_Radius = Math.sqrt(probability/Math.PI);
            px_Radius = sizeFactor * relative_Radius;
            px_RadiusSquared = px_Radius * px_Radius;
            circleIsDrawn = false;
            circleSegments = new Line[1000];
            
            px_03OClock = new Point_2D(px_xCenter + px_Radius, px_yCenter);
            px_06OClock = new Point_2D(px_xCenter, px_yCenter + px_Radius);            
            px_09OClock = new Point_2D(px_xCenter - px_Radius, px_yCenter);
            px_12OClock = new Point_2D(px_xCenter, px_yCenter - px_Radius);  
            
            makeCircleLines();
        }
        
        private void makeCircleLines() {
            for (int ithLine = 0; ithLine < 999; ithLine++) {
                double theta1 = (double)ithLine * arcFactor;
                double theta2 = (double)(ithLine + 1.) * arcFactor;
                circleSegments[ithLine] = new Line(px_xCenter + px_Radius * Math.cos(theta1),
                                          px_yCenter + px_Radius * Math.sin(theta1),
                                          px_xCenter + px_Radius * Math.cos(theta2),
                                          px_yCenter + px_Radius * Math.sin(theta2));
            }   
            circleIsDrawn = true;
        }
        
        public void calculateTopAndBottomAt(double this_px) {
            double pxAbsDevFromCenter_x = Math.abs(this_px - px_xCenter);
            double pxAbsDevFromCenter_y = Math.sqrt(px_RadiusSquared - pxAbsDevFromCenter_x * pxAbsDevFromCenter_x);
            px_BottomAt_xpx = px_yCenter + pxAbsDevFromCenter_y;
            px_TopAt_xpx = px_yCenter - pxAbsDevFromCenter_y;
        }
        
        public Point_2D getCenter() {
            Point_2D center = new Point_2D(px_xCenter, px_yCenter);
            return center;
        }
        
        public double get_pxRadius() { return px_Radius; }
        public double getRelativeRadius() { return relative_Radius; }
        
        // radiusSquared is needed by the lens object
        public double getRadiusSquared() { return px_RadiusSquared; }
        public double getVertRange() { return pxVertRange; }
        
        public Point_2D get_03_OClock() { return px_03OClock; }
        public Point_2D get_06_OClock() { return px_06OClock; }
        public Point_2D get_09_OClock() { return px_09OClock; }
        public Point_2D get_12_OClock() { return px_12OClock; }
        
        public double getUpperY_At_xpx() { return px_TopAt_xpx; }
        public double getLowerY_At_xpx() { return px_BottomAt_xpx; }
        
        public Line[] getCircleSegments() {return circleSegments; }
        
        public boolean getCircleIsDrawn() { return circleIsDrawn; }
        
        public Point_2D getLeftCircleTitleCoords() { 
            double temp_xUL = px_xCenter + 0.9 * px_Radius * Math.cos(Math.toRadians(225));
            double temp_yUL = px_yCenter + 0.9 * px_Radius * Math.sin(Math.toRadians(225));
            leftCircleTitleCoords = new Point_2D(temp_xUL, temp_yUL);
            return leftCircleTitleCoords; 
        }
        public Point_2D getRightCircleTitleCoords() { 
            double temp_xUR = px_xCenter + 0.85 * px_Radius * Math.cos(Math.toRadians(315));
            double temp_yUR = px_yCenter + 0.85 * px_Radius * Math.sin(Math.toRadians(315));
            rightCircleTitleCoords = new Point_2D(temp_xUR, temp_yUR);
            return rightCircleTitleCoords; }
        
    }   //  End class MyCircle
