/**************************************************************
 *          Point_2D   (the old awt.Point.2D)                 *
 *                       03/25/21                             *
 *                        18:00                               *
 *************************************************************/
package genericClasses;

public class Point_2D {
    
    private double x,y;
    
    public Point_2D() { x = 0.0; x = 0.0; }
    
    public Point_2D(double firstValue, double secondValue) {
        x = firstValue; y = secondValue;
    }

    public double getFirstValue() { return x; }    
    public void setFirstValue (double toThis) { x = toThis; }    
    public double getSecondValue() { return y; }    
    public void setSecondValue (double toThis) { y = toThis; }    
    public Point_2D getThePoint() { return this; }    
    public String toString() { 
        String daTo = "x = " + String.valueOf(x) + " y = " + String.valueOf(y);
        return daTo;
    }
}

