/****************************************************************************
 *                           Venn_Model                                     *
 *                            01/04/23                                      *
 *                             18:00                                        *
 ***************************************************************************/
package visual_probability;

import genericClasses.Point_2D;

public class Venn_Model {

    int nRoots;
    double radius_A, radius_B, radSum;    
    double c1, c2, xLeft, xRight, delta, thisRoot, pxCenterSeparation, 
           centerSeparation, targetProb, lensArea;
    double vbd_Length, vbd_Height; 
    double fOfX, fPrimeOfX, initialEstimate, thisEstimate, nextEstimate;
    double lowBoundary, highBoundary, sizeFactor;  
    
    boolean probAIsCalculated, probBIsCalculated, probAandBIsCalculated,
            probAorBIsCalculated, probAGivenBIsCalculated, probBGivenAIsCalculated,
            separationIsCalculated;
    
    double probOfA, probOfB, probOfAandB; 
    
    double var_r, var_R, var_d, var_x, var_y;
    
    Point_2D vennBoxDim, pxVennBoxDim;  //  length, width
    
    public Venn_Model (double sizeFactor) {
        //System.out.println("31 Venn_Model, constructing");
        probAIsCalculated = false;
        probBIsCalculated = false;
        probAandBIsCalculated = false;
        probAorBIsCalculated = false;
        probAGivenBIsCalculated = false;
        probBGivenAIsCalculated = false;
        separationIsCalculated = false;   
        this.sizeFactor = sizeFactor;
    }
    
    public void setProbA( double toThis) {
        //System.out.println("43 Venn_Model, setProbA( double toThis)");
        probOfA = toThis;
        radius_A = Math.sqrt(probOfA / Math.PI);
        //System.out.println("46 Venn_Model, radius_A = " + radius_A);
        if (probBIsCalculated && probAandBIsCalculated) { calculate_pxCenterSeparation(); }
    }
    
    public void setProbB( double toThis) {
        probOfB = toThis;
        radius_B = Math.sqrt(probOfB / Math.PI);
        //System.out.println("54  Venn_Model, radius_B = " + radius_B);
        if (probAIsCalculated && probAandBIsCalculated) { calculate_pxCenterSeparation(); }
    }
    
    public void setProbAandB( double toThis) {
        probOfAandB = toThis;
        if (probAIsCalculated && probBIsCalculated) { calculate_pxCenterSeparation(); }
    }
    
    /********************************************************************
     *         Calculate pxCenterSeparation of radii in the MasterCard          *
                  Given probs A, B, AandB                          *
   https://mathworld.wolfram.com/Lens.html                         *
   https://mathworld.wolfram.com/Circle-CircleIntersection.html    *
    ********************************************************************/
    
    public double calculate_pxCenterSeparation() {
        //System.out.println("72 Venn_Model, calculate_pxCenterSeparation()");
        if (Math.min(probOfA, probOfB) <  probOfAandB) {
            System.out.println("74 Venn Model, OOPSIE!!!");
            return 0.0;
        }
        targetProb = probOfAandB;
        //System.out.println("78 Venn_Model, targetProb = " + targetProb);
        /***************************************************************
         *       Find separation using bisection method                *
         **************************************************************/
        lowBoundary = Math.abs(radius_A - radius_B);
        highBoundary = radius_A + radius_B;
        centerSeparation = 0.5 * (lowBoundary + highBoundary);
        boolean closeEnough = false;
        while (!closeEnough) {
            lensArea = calculateLensArea(lowBoundary, highBoundary);
            centerSeparation = 0.5 * (lowBoundary + highBoundary);
            if (Math.abs(lensArea - targetProb) < .001) {
                closeEnough = true;
            }
            else if (lensArea < targetProb) {
                highBoundary = centerSeparation;
            }
            else if (lensArea > targetProb) {
                lowBoundary = centerSeparation;
            }
        }
        pxCenterSeparation = sizeFactor * centerSeparation;
        return pxCenterSeparation;
    }
    
    double calculateLensArea(double low, double high) {
        // System.out.println("104 Venn_Model, calculateLensArea(double low, double high)");
        var_r = radius_B;
        var_R = radius_A;
        var_d = 0.5 *(low + high);
        
        double temp_1_a = (var_d * var_d + var_r * var_r - var_R * var_R) / (2. * var_d * var_r);
        double temp_1_b = var_r * var_r * Math.acos(temp_1_a);
        
        double temp_2_a = (var_d * var_d + var_R * var_R - var_r * var_r) / (2. * var_d * var_R);
        double temp_2_b = var_R * var_R * Math.acos(temp_2_a);   
        
        double temp_3_a = -var_d + var_r + var_R;
        double temp_3_b = var_d + var_r - var_R;
        double temp_3_c = var_d - var_r + var_R;
        double temp_3_d = var_d + var_r + var_R;
        
        double temp_4 = temp_3_a * temp_3_b * temp_3_c * temp_3_d; 

        double area = temp_1_b + temp_2_b - 0.5 * Math.sqrt(temp_4);

        // Supplementary calculations for the chord
        var_x = (var_d * var_d - var_r * var_r + var_R * var_R) / (2.0 * var_d);
        double temp_y_1 = 4.0 * var_d * var_d * var_R * var_R;
        double temp_y_2a = var_d * var_d - var_r * var_r + var_R * var_R;
        double temp_y_2 = temp_y_2a * temp_y_2a;
        double temp_3 = 4.0 * var_d * var_d;
        var_y = Math.sqrt((temp_y_1 - temp_y_2) / temp_3);
        return area;
    }
    
    Point_2D getVennBoxDim() {
        //System.out.println("135 Venn_Model, getVennBoxDim()");
        vbd_Length = centerSeparation + radius_A + radius_B;
        vbd_Height = 2.0 * Math.max(radius_A, radius_B);
        //System.out.println("138 Venn_Model, centerSeparation = " + centerSeparation);
        vennBoxDim = new Point_2D(vbd_Length, vbd_Height);
        return vennBoxDim;
    }    
    Point_2D get_pxVennBoxDim() { 
        //System.out.println("143 Venn_Model, get_pxVennBoxDim()");
        double px_vbd_Length = vbd_Length * sizeFactor;
        double px_vbd_Height = vbd_Height * sizeFactor;
        pxVennBoxDim = new Point_2D(px_vbd_Length, px_vbd_Height);
        return pxVennBoxDim;
    }    
    
    double get_chord_var_X() { return var_x; }
    double get_chord_var_Y() { return var_y; }
    
    double get_pxRadiusLeft() { return var_R * sizeFactor; }
    double get_pxRadiusRight() { return var_r * sizeFactor; }
    
    double get_CenterSeparation() { return centerSeparation; }
    double get_pxCenterSeparation() { return pxCenterSeparation; }
}
