/**************************************************
 *                 TableFormatter                 *
 *                    11/01/23                    *
 *                     09:00                      *
 *************************************************/
package utilityClasses;

import genericClasses.Point_2D;
import matrixProcedures.Matrix;

public class TableFormatter {
    
    int availablePrecision;
 
    public TableFormatter() { }
    
    public String doTheFormatting(String daFormatSpecification) {
        StringBuilder sb = new StringBuilder();
        String[] splitzie = daFormatSpecification.split("\\|");
        int nSplits = splitzie.length;

        for (int ithSplit = 0; ithSplit < nSplits; ithSplit++ ) {
            String daSplitzie = splitzie[ithSplit];
            int splitLen = daSplitzie.length();
            String firstChar = daSplitzie.substring(0, 1);
            
            if (firstChar.equals("F")) {
                String daDoubleFound = doubleFound(daSplitzie.substring(1, splitLen));
                sb.append(daDoubleFound);
            }
            
            if (firstChar.equals("X")) {
                String daSpacesFound = spacesFound(daSplitzie.substring(1, splitLen));
                
                sb.append(daSpacesFound);
            }
            
            if (firstChar.equals("S")) {
                //String daStringFound = stringFound(daSplitzie.substring(1, splitLen));
                sb.append(stringFound(daSplitzie.substring(1, splitLen)));
            }
            
            if (firstChar.equals("D")) {
                String daIntegerFound = intFound(daSplitzie.substring(1, splitLen));
                sb.append(daIntegerFound);
            }            
        }

        return sb.toString();           
    }
    
    private String spacesFound(String spaceFormat) { // "?X"
        StringBuilder sbInString = new StringBuilder(spaceFormat);
        sbInString.delete(0,0);
        int nSpaces = Integer.parseInt(sbInString.toString());
        StringBuilder sbOutString = new StringBuilder();
        for (int ithSpace = 0; ithSpace < nSpaces; ithSpace++) {
            sbOutString.append(" ");
        }
        String outString = sbOutString.toString();        
        return outString;
    }
    
    private String stringFound(String stringFormat) {    // "?S
        StringBuilder sbInString = new StringBuilder(stringFormat);
        sbInString.delete(0,0);
        String outString = "%" + sbInString.toString() + "s";        
        return outString;       
    }
    
    private String intFound(String intFormat) {   //  "?D"
        StringBuilder sbInString = new StringBuilder(intFormat);
        sbInString.delete(0,0);
        String outString = "%" + sbInString.toString() + "d";
        return outString; 
    }
    
    private String doubleFound(String doubleFormat) {    //  "?F"
        StringBuilder sbInDouble = new StringBuilder(doubleFormat);
        sbInDouble.delete(0, 0);
        doubleFormat = sbInDouble.toString();
        String[] splitzie = doubleFormat.split("\\.");
        String fieldWidth = splitzie[0];
        String precision = splitzie[1];
        String outString = "%" + fieldWidth + "." + precision + "f";
        return outString;  
    }
    
    // The helpWithDoubleField method returns a format with the double centered in the field
    // The user supplies all "|" except the ones generated here for doubles
    public String helpWithDoublesField(int fieldWidth, double logSmallest, double logLargest, int minPrecision, int maxPrecision) {
        /*
        System.out.println("\n97 TF, helpWithDoublesField(fieldWidth, logSmallest, logLargest, minPrecision, maxPrecision");
        System.out.println("97 TF, " + fieldWidth + " / " 
                                         + logSmallest + " / " 
                                         + logLargest + " / " 
                                         + minPrecision+ " / " 
                                         + maxPrecision);
        */
        StringBuilder sb = new StringBuilder();
        int ordMagLargest = (int)logLargest + 1;
        int ordMagSmallest = (int)logSmallest + 1;
        //                  Left of decimal     Right of decimal
        int spacesRequired = ordMagLargest + 1 + minPrecision;
        if (ordMagSmallest < 0) {
            //  Add zeros after decimal before precision
            spacesRequired = spacesRequired - ordMagSmallest; 
        }
        
        /*
        System.out.println("113 TF, ordMagLargest, ordMagSmallest, minPrecision, spacesRequired, fieldWidth");
        System.out.println("114 TF, ... " + ordMagLargest + " / " 
                                               + ordMagSmallest + " / " 
                                               + minPrecision + " / " 
                                               + spacesRequired + " / " 
                                               + fieldWidth);
        */
        
        if (spacesRequired <  fieldWidth) {
            //  Make more space in the field
            fieldWidth = spacesRequired;
        } else {    //  spaces left over
            availablePrecision = spacesRequired - fieldWidth;
            maxPrecision = Math.min(maxPrecision, availablePrecision);
        }
        if (spacesRequired >= fieldWidth) {
            sb.append("F").append(String.valueOf(spacesRequired + 1)).append(".").append(String.valueOf(minPrecision));    // theDouble
        } else
        {
            sb.append("F").append(String.valueOf(spacesRequired + 1)).append(".").append(String.valueOf(minPrecision));    // theDouble )
        }
        String outString = sb.toString();
        return outString;
    }
    
    // Get the range of orders of magnitute of values of double that will
    // appear in a column in the table.
   public Point_2D getMinAndMaxOfArray(double[] ofTheseValues) {
       double signumMin, signumMax, logMin, logMax;
        // Get rid of troublesome values
        int nValues = ofTheseValues.length;
        // Cannot handle values <= 1
        signumMin = Math.signum(ofTheseValues[0]);
        signumMax = Math.signum(ofTheseValues[0]);
        logMin = signumMin * Math.log10(Math.abs(ofTheseValues[0]));
        logMax = signumMax * Math.log10(Math.abs(ofTheseValues[0]));
        
        for (int ithValue = 1; ithValue < nValues; ithValue++) {
            signumMin = Math.signum(ofTheseValues[ithValue]);
            signumMax = Math.signum(ofTheseValues[ithValue]);            
            logMin = Math.min(logMin, signumMin * Math.log10(Math.abs(ofTheseValues[ithValue])));
            logMax = Math.max(logMax, signumMax * Math.log10(Math.abs(ofTheseValues[ithValue])));
        }
        
        Point_2D theRange = new Point_2D(logMin,logMax);
        return theRange;
    }

   public Point_2D[] getMinAndMaxOfMatrix(Matrix ofTheseValues) {
       double signumMin, signumMax, logMin, logMax;
       Point_2D[] theRange;
       int nRows = ofTheseValues.getRowDimension();
       int nCols = ofTheseValues.getColumnDimension();
       theRange = new Point_2D[nCols];
       for (int ithCol = 1; ithCol < nCols; ithCol++) {
        // Cannot handle values <= 1
        signumMin = Math.signum(ofTheseValues.get(0, ithCol));
        signumMax = Math.signum(ofTheseValues.get(0, ithCol));
        logMin = signumMin * Math.log10(Math.abs(ofTheseValues.get(0, ithCol)));
        logMax = signumMax * Math.log10(Math.abs(ofTheseValues.get(0, ithCol)));
        
        for (int ithRow = 1; ithRow < nRows; ithRow++) {
            signumMin = Math.signum(ofTheseValues.get(ithRow, ithCol));
            signumMax = Math.signum(ofTheseValues.get(ithRow, ithCol));            
            logMin = Math.min(logMin, signumMin * Math.log10(Math.abs(ofTheseValues.get(ithRow, ithCol))));
            logMax = Math.max(logMax, signumMax * Math.log10(Math.abs(ofTheseValues.get(ithRow, ithCol))));
           }

            theRange[ithCol] = new Point_2D(logMin, logMax);
           
            //System.out.println("179 tabForm, ithCol/logMin/Max = " + ithCol + " / " 
            //                                                       + logMin + " / " 
            //                                                       + logMax);
        
        theRange[ithCol] = new Point_2D(logMin, logMax);
        }
        return theRange;
    }
}
