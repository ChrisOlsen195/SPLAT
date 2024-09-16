/**************************************************
 *                  DataUtilities                 *
 *                    09/07/24                    *
 *                      12:00                     *
 *************************************************/
package utilityClasses;

import java.util.ArrayList;
import javafx.scene.control.TextField;
import dataObjects.*;
import genericClasses.Point_2D;

public class DataUtilities {
    // POJOs
    
    static double tempDouble;
    static Double[] arrayOfDoubles;
    static ArrayList<Double> arrayListOfDoubles;
    
    static ArrayList<String> offendingStrings;

    public DataUtilities ()  { }
    
    public static boolean strIsNumeric(String allegedNumeric) {
        if (strIsADouble(allegedNumeric) || strIsAnInteger(allegedNumeric)) {
            return true;
        }
        else
            return false;
    }
    
    public static boolean strIsADouble(String allegedDouble) {
        //System.out.println("25 DataUtils, allegedDouble = " + allegedDouble);
        if (allegedDouble.isEmpty()) { return false;}
        try {
            //System.out.println("28 DataUtils, Try block");
            tempDouble = Double.parseDouble(allegedDouble);
            return true;
        } catch (NumberFormatException e) {
            //System.out.println("32 DataUtils, Catch block");
            return false;
        }
    }
    
    public static boolean strIsAPosDouble(String allegedDouble) {
        //System.out.println("38 DataUtils, allegedPosDouble = " + allegedDouble);
        if (!strIsADouble(allegedDouble)) { return false; }
        try {
            tempDouble = Double.parseDouble(allegedDouble);            
            if (tempDouble <= 0) {
                return false;
            }            
            return true;        
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /************************************************************************
     *   .0 is added somewhere to integer x in the SmartText code, perhaps? *
     *    Thus, integer checks must worry about this.                       *
     ***********************************************************************/
    public static boolean strIsAnInteger(String allegedInteger) {
        //System.out.println("56 DataUtils, allegedInteger = " + allegedInteger);
        if (allegedInteger.isEmpty()) { return false; }        
        // Strip the .0 if it is there
        if (allegedInteger.length() > 2) {
            String rightMostTwo = getRightmostNChars(2, allegedInteger);            
            if (rightMostTwo.equals(".0")) {
                allegedInteger = allegedInteger.substring(0, allegedInteger.length() - 2);
            }
        }
        try {
            int tempInt = Integer.parseInt(allegedInteger);
            return true;
        }
        catch (NumberFormatException e) { return false; }
    }
    
    public static boolean strIsANonNegInt(String allegedInteger) { 
        //System.out.println("73 DataUtils, allegedNonNegInteger = " + allegedInteger);
        if (!strIsAnInteger (allegedInteger)) { return false; }        
        if (Integer.parseInt(allegedInteger) < 0) { return false; }        
        return true;
    }
    
    public static boolean strIsAPosInt(String allegedInteger) {
        //System.out.println("80 DataUtils, allegedPosNegInteger = " + allegedInteger);
        if (!strIsAnInteger (allegedInteger)) { return false; }
        if (Integer.parseInt(allegedInteger) <= 0) { return false; }
        return true;
    }
    
    public static boolean strIsAProb(String allegedDouble) {
        //System.out.println("87 DataUtils, allegedProb = " + allegedDouble);
        if (!strIsAPosDouble(allegedDouble)) {return false; }
        tempDouble = Double.parseDouble(allegedDouble);
        if ((tempDouble >= 1.0) || (tempDouble <= 0.0)) { return false; }
        return true;
    }
    
public static double roundDoubleToNDigits(double theDouble, int nDigits) {
        String frmt = "%." + Integer.toString(nDigits) + "f";
        String str_toSigDigs = String.format(frmt, theDouble);
        double dbl_toSigDigs = Double.parseDouble(str_toSigDigs);
        return dbl_toSigDigs;
}     
    
    public static boolean txtFieldHasDouble(TextField theTF) {
        return strIsADouble(theTF.getText());        
    }
    
    public static boolean txtFieldHasPosDouble(TextField theTF) {
        return strIsAPosDouble(theTF.getText());        
    }

    public static boolean txtFieldHasPosInt(TextField theTF) {
        return strIsAPosInt(theTF.getText());        
    }
    
    public static boolean txtFieldHasProp(TextField theTF) {
        return strIsAProp(theTF.getText());        
    }
    
    public static boolean strIsAProp(String theString) {
        if (!strIsADouble(theString)) { return false; }
        tempDouble = Double.parseDouble(theString);
        return !(tempDouble <= 0.0 || tempDouble >= 1.0);
    }    
       
    public static boolean convertArrayListOfStringsToArrayListOfDoubles (ArrayList<String> arrayListOfStrings) {
        String tempString;
        boolean allStringsAreDoubles = true;
        offendingStrings = new ArrayList<>();
        int nDataPoints = arrayListOfStrings.size();
        arrayOfDoubles = new Double[nDataPoints];
        
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = arrayListOfStrings.get(ith);            
            if (strIsADouble(tempString)) {
                arrayListOfDoubles.set(ith, Double.valueOf(tempString));
            } else {
                offendingStrings.add(tempString);
                allStringsAreDoubles = false;
            }       
        }
        
        if (!allStringsAreDoubles) {
            MyAlerts.showNonDoubleInArrayListAlert();
        }
        return allStringsAreDoubles;
    }
   
    public static Double convertStringToDouble( String fromThis) {
        Double toThis = Double.valueOf(fromThis);
        return toThis;
    }
    
    public static boolean checkForVariabilityInQDV(QuantitativeDataVariable qdv) {
        if (!qdv.checkForVariability()) { 
            MyAlerts.showNoVariabilityInQDVAlert(qdv); 
            return false;
        }
        return true;
    }
    
    public static Point_2D makeAScaleIntervalFor(double thisMin, double thisMax) {
        //System.out.println("160 DataUtilities, makeAScaleIntervalFor(double thisMin, double thisMax)");
        double xLow, xHigh, log10_OrdLow, log10_OrdHigh, logOrdMag, floor_Log10, 
               bigTickInterval, pre_preTick_Low, 
               pre_preTick_High,preTick_Low, preTick_High, theLowTick,
               theHighTick; 
        
        double newLowTick, newHighTick, possibleNewLowTick, possibleNewHighTick,
               dbl_ithThousandth, thousandthInterval;               
               
        xLow = thisMin;
        xHigh = thisMax;

        // Never trust alligators or 0's!!
        if (xLow == 0.0) {xLow = -0.00001; }
        if (xHigh == 0.0){xHigh = 0.00001; }

        //System.out.println("176 DataUtilities, xLow / High = " + xLow + " / " + xHigh);

        log10_OrdLow = Math.log10(Math.abs(xLow));
        log10_OrdHigh = Math.log10(Math.abs(xHigh));
        //System.out.println("180 DataUtilities, Log10_OrdLow / High = " + log10_OrdLow  + " / " + log10_OrdHigh);

        logOrdMag = Math.max(log10_OrdLow, log10_OrdHigh);
        //System.out.println("183 DataUtilities, logOrdMag = " + logOrdMag);

        floor_Log10 = Math.floor(logOrdMag);
        //System.out.println("186 DataUtilities, floor_Log10 = " + floor_Log10);

        bigTickInterval = Math.pow(10., floor_Log10);
        //System.out.println("189 DataUtilities, bickTickInterval = " + bigTickInterval);

        pre_preTick_Low = xLow / bigTickInterval;
        pre_preTick_High = xHigh / bigTickInterval;
        //System.out.println("193 DataUtilities, pre_preTickL/H = " + pre_preTick_Low  + " / " + pre_preTick_High);

        preTick_Low = Math.floor(pre_preTick_Low);
        preTick_High = Math.floor(pre_preTick_High);
        //System.out.println("197 DataUtilities, preTickL/H = " + preTick_Low  + " / " + preTick_High);

        theLowTick = preTick_Low * bigTickInterval;
        theHighTick = (preTick_High + 1.0) * bigTickInterval;
        //System.out.println("201 DataUtilities, orig tickL/H = " + theLowTick + " / " + theHighTick);     
        
        newLowTick = theLowTick;    // Initialize to existing
        thousandthInterval = (theHighTick - theLowTick) / 1000.0;
        
        for (int ithThousandth = 0; ithThousandth < 1000; ithThousandth++) {
            dbl_ithThousandth = ithThousandth;
            possibleNewLowTick = theLowTick + dbl_ithThousandth * thousandthInterval;
            if ( possibleNewLowTick < xLow) {
                newLowTick = possibleNewLowTick;
            }
        }
        theLowTick = newLowTick;        
        newHighTick = theHighTick;
        
        for (int ithThousandth = 0; ithThousandth < 1000; ithThousandth++) {
            dbl_ithThousandth = ithThousandth;
            possibleNewHighTick = theHighTick - dbl_ithThousandth * thousandthInterval;
            if ( possibleNewHighTick > xHigh) {
                newHighTick = possibleNewHighTick;
            }
        }
        theHighTick = newHighTick;   // Initialize to existing

        Point_2D theInterval = new Point_2D(theLowTick, theHighTick);
        return theInterval;
    }
    
    private static String getRightmostNChars(int nChars, String ofThisString) {
        return ofThisString.substring(ofThisString.length() - nChars);
    }
    
    public static void printArrayOfIntegers(String intDescr, int[] inArray) {
        int nInArray = inArray.length;
        if (nInArray == 0) {
            System.out.println(" integerArray is Empty");
        }
        else {
            System.out.println("intDescr = " + intDescr);
            for (int ithInteger = 0; ithInteger < nInArray; ithInteger++) {
                System.out.println("--> " + ithInteger + " / " + inArray[ithInteger]);
            }
        }
    }
    
    public static void printArrayOfDoubles(String dblDescr, double[] inArray) {
        int nInArray = inArray.length;
        if (nInArray == 0) {
            System.out.println(" doubleArray is Empty");
        }
        else {
            System.out.println("dblDescr = " + dblDescr);
            for (int ithDouble = 0; ithDouble < nInArray; ithDouble++) {
                System.out.println("--> " + ithDouble + " / " + inArray[ithDouble]);
            }
        }
    }
    
    public ArrayList<String> getOffendingStrings() { return offendingStrings; }
    public Double[] getArrayOfDoubles() { return arrayOfDoubles; }
    public ArrayList<Double> getArrayListOfDoubles() { return arrayListOfDoubles; }
}   

