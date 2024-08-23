/****************************************************************************
 *                          MyRectangle                                     *
 *                            01/03/22                                      *
 *                             03:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class MyRectangle {
    
    boolean rectIsDrawn;
    double upperLeftX, upperLeftY, lowerRightX, lowerRightY, vertRange;
    
    final int FOUR;
    
    Paint rectColor;
    Line top, bottom, left, right;
    Line[] border, fill;
    
    public MyRectangle(double upperLeftX, double upperLeftY, 
                       double lowerRightX, double lowerRightY) {
        this.upperLeftX = upperLeftX;
        this.upperLeftY = upperLeftY;
        this.lowerRightX = lowerRightX;
        this.lowerRightY = lowerRightY;
        top = new Line(upperLeftX, upperLeftY, lowerRightX, upperLeftY);
        bottom = new Line(upperLeftX, lowerRightY, lowerRightX, lowerRightY);
        left = new Line(upperLeftX, upperLeftY, upperLeftX, lowerRightY);
        right = new Line(lowerRightX, upperLeftY, lowerRightX, lowerRightY);   
        FOUR = 4;
        border = new Line[FOUR];
        border[0] = top;
        border[1] = right;
        border[2] = bottom;
        border[3] = left;
        fill = new Line[500];
        rectColor = Color.BLACK;
        vertRange = lowerRightY - upperLeftY;
        for (int ithLine = 0; ithLine < 500; ithLine++) {
            fill[ithLine] = new Line(upperLeftX, 
                                      upperLeftY + (double)ithLine / 500. * vertRange,
                                      lowerRightX,
                                      upperLeftY + (double)ithLine / 500. * vertRange);
            fill[ithLine].setStroke(rectColor);
        }        

    }
    
    public void resizeTheRectangleTo(double upperLeftX, double upperLeftY, 
                       double lowerRightX, double lowerRightY) {
        this.upperLeftX = upperLeftX;
        this.upperLeftY = upperLeftY;
        this.lowerRightX = lowerRightX;
        this.lowerRightY = lowerRightY;
        top = new Line(upperLeftX, upperLeftY, lowerRightX, upperLeftY);
        bottom = new Line(upperLeftX, lowerRightY, lowerRightX, lowerRightY);
        left = new Line(upperLeftX, upperLeftY, upperLeftX, lowerRightY);
        right = new Line(lowerRightX, upperLeftY, lowerRightX, lowerRightY);   
        border[0] = top;
        border[1] = right;
        border[2] = bottom;
        border[3] = left;
        vertRange = lowerRightY - upperLeftY;
        for (int ithLine = 0; ithLine < 500; ithLine++) {
            fill[ithLine] = new Line(upperLeftX, 
                                      upperLeftY + (double)ithLine / 500. * vertRange,
                                      lowerRightX,
                                      upperLeftY + (double)ithLine / 500. * vertRange);
            fill[ithLine].setStroke(rectColor);
        }
    }
    
    public void setStrokeWidth(double toThis) {
        for (int ithSide = 0; ithSide < FOUR; ithSide++) {
            border[ithSide].setStrokeWidth(toThis);
        }
    }
    
    //  Set the color of the line
    public void setStroke(Paint toThisColor) {
        rectColor = toThisColor;
        for (int ithSide = 0; ithSide < FOUR; ithSide++) {
            border[ithSide].setStroke(toThisColor);
        }
        for (int ithLine = 0; ithLine < 500; ithLine++) {
            fill[ithLine].setStroke(rectColor);
        }  
    }
    
    /*
    private void createRectFill() {
        //System.out.println("70 MyRectangle, createRectFill()");
        double vertRange = lowerRightY - upperLeftY;
        for (int ithLine = 0; ithLine < 500; ithLine++) {

            fill[ithLine] = new Line(upperLeftX, 
                                      upperLeftY + (double)ithLine / 500. * vertRange,
                                      lowerRightX,
                                      upperLeftY + (double)ithLine / 500. * vertRange);
            fill[ithLine].setStroke(rectColor);
        }   
    }
    */
    
    public Line[] getRectFill() { return fill; }
    
    public Line[] getTheRectBorder() {
        return border;
    }
    
    
}
