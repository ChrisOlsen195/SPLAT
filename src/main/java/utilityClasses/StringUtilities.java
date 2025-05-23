/****************************************************************************
 *                        StringUtilities                                   * 
 *                            09/14/24                                      *
 *                             21:00                                        *
 ***************************************************************************/
package utilityClasses;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import smarttextfield.SmartTextFieldDoublyLinkedSTF;

public class StringUtilities {
    
    public StringUtilities() { }

    public static String centerTextInString(String s, int fieldSize)  {
        if (s.length() >= fieldSize) {
            s = s.substring(0, fieldSize);
            return s;
        }
        char pad = ' ';
        s = s.trim();   // Eliminate leading and trailing spaces
        if (s == null || fieldSize <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(fieldSize);
        for (int i = 0; i < (fieldSize - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < fieldSize) {
            sb.append(pad);
        }
        return sb.toString();
    } 

    public static String roundDoubleToNDigitString(double theDouble, int nDigits) {
        String frmt = "%." + Integer.toString(nDigits) + "f";
        String toSigDigs = String.format(frmt, theDouble);
        return toSigDigs;
}    

    public static String getStringOfNSpaces(int nSpaces) {
        String tempString = "";
        for (int iSpaces = 0; iSpaces < nSpaces; iSpaces++) {
            tempString += " "; 
        }
        return tempString;
    }

    public static String getUnicodeLineThisLong(int thisLong) {
        String tempString = "";
        for (int iDashes = 0; iDashes < thisLong; iDashes++) 
            {tempString += "\u2501";}
        return tempString;
    }
    
    //  Needed for back-to-back stemplot
    public static String reverseStringCharacters( String toBeReversed) {
        String reversedString;  
        char[] stringAsChars = toBeReversed.toCharArray();
        Arrays.sort(stringAsChars);
        
        int leefsLength = stringAsChars.length;
        char[] revleefs = new char[leefsLength];

        for (int ithLeef = 0; ithLeef < leefsLength; ithLeef++) {
            revleefs[ithLeef] = stringAsChars[leefsLength - ithLeef - 1];
        }
        System.arraycopy(revleefs, 0, stringAsChars, 0, leefsLength);                    
        reversedString = String.valueOf(stringAsChars);     
        return reversedString;
    }

    public static String truncateString(String inpString, int maxLength) {        
        int len = inpString.length();
        if (len <= maxLength) {
            return inpString;
        }
        else {
        String temp = inpString.substring(0, maxLength);
            return temp;
        }
    }
   
    public static double[] convert_alStr_to_alDoubles (ArrayList<String> alString) {
        String tempString;
        int nDataPoints = alString.size();
        double[] alDoubles = new double[nDataPoints];        
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = alString.get(ith);            
            if (DataUtilities.strIsADouble(tempString)) {
                alDoubles[ith] = Double.parseDouble(tempString);
            } else {
                System.out.println("Unclean Data! Conversion error in 93 StringUtilities!!");
                alDoubles[ith] = Double.NaN;
            }       
        }
        return alDoubles;
    }

    public static double[] convert_arrayStr_to_arrayDoubles (String[] arrayOfStrings) {
        String tempString;
        int nDataPoints = arrayOfStrings.length;
        double[] alDoubles = new double[nDataPoints];        
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = arrayOfStrings[ith];            
            if (DataUtilities.strIsADouble(tempString)) {
                alDoubles[ith] = Double.parseDouble(tempString);
            } else {
                System.out.println("Unclean Data! Conversion error in 113 StringUtilities!!");
                alDoubles[ith] = Double.NaN;
            }       
        }
        return alDoubles;
    }
    
    public static Double convertStringToDouble( String fromThis) {
        return Double.valueOf(fromThis);
    }
    
    public static Integer convertStringToInteger( String fromThis) {
        return Integer.valueOf(fromThis);
    }
    
    public static int TextFieldToPrimitiveInt(TextField theTF) {
        String strTheText = theTF.getText();
        return Integer.parseInt(strTheText);  
    }    
    
    public static String getleftMostNChars(String original, int leftChars)
    {
       String longString = original + "                       ";
       String truncated = longString.substring(0, leftChars - 1);
       return truncated;
    }
   
    public static String getRightMostNChars(String original, int rightChars)
    {
       int len = original.length();
       String rightMost = original.substring(len - rightChars);
       return rightMost;
    }
   
    public static void addNLinesToArrayList(ArrayList<String> thisAL, int thisManyLines) {
        for (int ithBlank = 0; ithBlank < thisManyLines; ithBlank++) {
            thisAL.add("\n");
        }       
   }
   
    public static boolean check_TextField_4Blanks(TextField tf) {
        boolean hasBlanks = true;
        String temp = tf.getText();
        if (temp.trim().equals("")) 
            hasBlanks = false;
        return hasBlanks;
    } 
    
    public static String eliminateMultipleBlanks(String fromThisString) {
        String oldString, trimmedString, newString;
        StringBuilder soFar = new StringBuilder();
        oldString  = fromThisString;
        trimmedString = oldString.trim();
        int lenTrimmed = trimmedString.length();
        soFar.append(trimmedString.charAt(0));        
        for (int ithChar = 1; ithChar < lenTrimmed; ithChar++) {
            char prevChar = trimmedString.charAt(ithChar - 1);
            char thisChar = trimmedString.charAt(ithChar);
            if ((prevChar != ' ') || (thisChar != ' ')) {
                soFar.append(thisChar);
            }
        }
        
        newString = soFar.toString();
        return newString;
    }
    
    public static void printArrayOfStrings(String strDescr, String[] inArray) {
        int nInArray = inArray.length;
        if (nInArray == 0) {
            System.out.println(" StringArray is Empty");
        }
        else {
            System.out.println("strArrayDescr = " + strDescr);
            for (int ithString = 0; ithString < nInArray; ithString++) {
                System.out.println("--> " + ithString + " / " + inArray[ithString]);
            }
        }
    }

    public static boolean stringIsEmpty (String str) {
        boolean isEmpty = (str == null || str.trim().isEmpty());
        return isEmpty;
    }
    
    public static boolean checkForUniqueStrings(String[] arrayOfStrings) {
        int nCategories = arrayOfStrings.length;       
        for (int ithString = 0; ithString < nCategories - 1; ithString++) {
            String temp1 = arrayOfStrings[ithString];   
            for (int jthString = ithString + 1; jthString < nCategories; jthString++) {
                String temp2 = arrayOfStrings[jthString];
                if (temp1.equals(temp2)) {
                    MyAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }
        //System.out.println("415  *** X2GOF_DataByHand, ALMOST END checkForUniqueCategories()");
        return true;        
    }
    
    public static boolean checkForUniqueStrings(SmartTextFieldDoublyLinkedSTF dlSTF) {
        int nCategories = dlSTF.getSize();       
        for (int ithString = 0; ithString < nCategories - 1; ithString++) {
            String temp1 = dlSTF.get(ithString).getText(); 
            for (int jthString = ithString + 1; jthString < nCategories; jthString++) {
                String temp2 = dlSTF.get(jthString).getText();
                if (temp1.equals(temp2)) {
                    MyAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }
        //System.out.println("415  *** X2GOF_DataByHand, ALMOST END checkForUniqueCategories()");
        return true;        
    }
    
    public static boolean checkForUniqueStrings(Text[] arrayOfTXT) {
        int nCategories = arrayOfTXT.length;       
        for (int ithText = 0; ithText < nCategories - 1; ithText++) {
            String temp1 = arrayOfTXT[ithText].getText(); 
            for (int jthText = ithText + 1; jthText < nCategories; jthText++) {
                String temp2 = arrayOfTXT[jthText].getText();;
                if (temp1.equals(temp2)) {
                    MyAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }
        return true;        
    }
}