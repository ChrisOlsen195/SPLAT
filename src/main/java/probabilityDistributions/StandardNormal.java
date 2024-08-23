/**************************************************
 *                  StandardNormal                *
 *                    11/27/22                    *
 *                     21:00                      *
 *************************************************/
/*******************************************************
 *   This is a wrapper class for theRProbDists.Normal  *
 ******************************************************/
package probabilityDistributions;

import java.util.Random; 
import theRProbDists.*;


public class StandardNormal 
{                                                                                   
    private static double zValue;
    
    static Random randomNumberGenerator;
    
    //RVUtilities rvUtilities;
    Normal standardNormal;

    public StandardNormal() {
        randomNumberGenerator = new Random();
        //rvUtilities = new RVUtilities();
        standardNormal = new Normal();
    }
    
    public double getLeftTailArea(double z_Value) {
        return standardNormal.getLeftArea(z_Value);
    }

    public double getInvLeftTailArea(double p) {
        return standardNormal.getInvLeftArea(p);
    }

     public double getRightTailArea(double z_Value)
     {
         return standardNormal.getRightArea(z_Value);
     }
     
     public double getInvRightTailArea(double p) {
         return standardNormal.getInvRightArea(p);
     }

    public double getDensity( double z_Value) {
        return standardNormal.density(z_Value, false);
    }

    public double getMiddleArea(double zLow, double zHigh) {
        return standardNormal.getAreaBetweenZs(zLow, zHigh);
    }
    
    public double[] getInverseMiddleArea(double middleArea) {   // for graphing & crit values
        double[] middleInterval = new double[2];
        double tailArea = (1.0 - middleArea) / 2.0;
        middleInterval[0] = standardNormal.getInvLeftArea(tailArea);
        middleInterval[1] = standardNormal.getInvRightArea(tailArea);
        return middleInterval;
    }
 
    public double generateRandom() {
        double randomNormal = randomNumberGenerator.nextGaussian();
        return randomNormal;
    }
}