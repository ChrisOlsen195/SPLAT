/****************************************************************************
 *                        StringUtilities                                   * 
 *                            01/11/25                                      *
 *                             18:00                                        *
 ***************************************************************************/
package utilityClasses;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import smarttextfield.DoublyLinkedSTF;

public class StringUtilities {
    
    //static boolean printTheStuff = true;
    static boolean printTheStuff = false;
    
    public StringUtilities() { }

    public static String centerTextInString(String s, int fieldSize)  {
        if (printTheStuff) {
            System.out.println("23 --- StringUtilities, centerTextInString");
        }
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
        if (printTheStuff) {
            System.out.println("47 --- StringUtilities, roundDoubleToNDigitString");
        }
        String frmt = "%." + Integer.toString(nDigits) + "f";
        String toSigDigs = String.format(frmt, theDouble);
        return toSigDigs;
}    

    public static String getStringOfNSpaces(int nSpaces) {
        if (printTheStuff) {
            //System.out.println("56 --- StringUtilities, getStringOfNSpaces");
        }
        String tempString = "";
        for (int iSpaces = 0; iSpaces < nSpaces; iSpaces++) {
            tempString += " "; 
        }
        return tempString;
    }

    public static String getUnicodeLineThisLong(int thisLong) {
        if (printTheStuff) {
            System.out.println("67 --- StringUtilities, getUnicodeLineThisLong");
        }
        String tempString = "";
        for (int iDashes = 0; iDashes < thisLong; iDashes++) 
            {tempString += "\u2501";}
        return tempString;
    }
    
    //  Needed for back-to-back stemplot
    public static String reverseStringCharacters( String toBeReversed) {
        if (printTheStuff) {
            System.out.println("78 --- StringUtilities, reverseStringCharacters");
        }
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
        if (printTheStuff) {
            System.out.println("97 --- StringUtilities, truncateString");
        }
        int len = inpString.length();
        if (len <= maxLength) {
            return inpString;
        }
        else {
        String temp = inpString.substring(0, maxLength);
            return temp;
        }
    }
   
    public static double[] convert_alStr_to_alDoubles(ArrayList<String> alString) {
        if (printTheStuff) {
            System.out.println("111 --- StringUtilities, convert_alStr_to_alDoubles");
        }
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
        if (printTheStuff) {
            System.out.println("130 --- StringUtilities, convert_arrayStr_to_arrayDoubles");
        }
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
        if (printTheStuff) {
            System.out.println("149 --- StringUtilities, convertStringToDouble");
        }
        return Double.valueOf(fromThis);
    }
    
    public static Integer convertStringToInteger( String fromThis) {
        if (printTheStuff) {
            System.out.println("156 --- StringUtilities, convertStringToInteger");
        }
        return Integer.valueOf(fromThis);
    }
    
    public static int TextFieldToPrimitiveInt(TextField theTF) {
        if (printTheStuff) {
            System.out.println("163 --- StringUtilities, TextFieldToPrimitiveInt");
        }
        String strTheText = theTF.getText();
        return Integer.parseInt(strTheText);  
    }    
    
    public static String getleftMostNChars(String original, int nLeftChars) {
        if (printTheStuff) {
            System.out.println("171 --- StringUtilities, getleftMostNChars");
            System.out.println(" original = " + original);
            System.out.println(" nLeftChars = " + nLeftChars);
        }
       String longString = original + "                                   ";
       String truncated = longString.substring(0, nLeftChars - 1);
       return truncated;
    }
   
    // Used in DataGrid
    public static String getRightMostNChars(String original, int nRightChars) {
        if (printTheStuff) {
            System.out.println("182 --- StringUtilities, getRightMostNChars");
        }
       int len = original.length();
       String rightMost = original.substring(len - nRightChars);
       return rightMost;
    }
    
    public static void addNLinesToArrayList(ArrayList<String> thisAL, int thisManyLines) {
        if (printTheStuff) {
            //System.out.println("191 --- StringUtilities, addNLinesToArrayList");
        }
        for (int ithBlank = 0; ithBlank < thisManyLines; ithBlank++) {
            thisAL.add("\n");
        }       
   }
   
    public static boolean check_TextField_4Blanks(TextField tf) {
        if (printTheStuff) {
            System.out.println("200 --- StringUtilities, check_TextField_4Blanks");
        }
        boolean hasBlanks = true;
        String temp = tf.getText();
        if (temp.trim().equals("")) 
            hasBlanks = false;
        return hasBlanks;
    } 
    
    public static String eliminateMultipleBlanks(String fromThisString) {
        if (printTheStuff) {
            System.out.println("211 --- StringUtilities, eliminateMultipleBlanks");
        }
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
        if (printTheStuff) {
            System.out.println("232 --- StringUtilities, printArrayOfStrings");
        }
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

    public static boolean stringIsEmpty( String str) {
        if (printTheStuff) {
            System.out.println("249 --- StringUtilities, stringIsEmpty");
        }
        boolean isEmpty = (str == null || str.trim().isEmpty());
        return isEmpty;
    }
    
    public static boolean checkForUniqueStrings(String[] arrayOfStrings) {
        if (printTheStuff) {
            System.out.println("257 --- StringUtilities, checkForUniqueStrings");
        }
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
        return true;        
    }
    
    public static boolean checkForUniqueStrings(DoublyLinkedSTF dlSTF) {
        if (printTheStuff) {
            System.out.println("275 --- StringUtilities, checkForUniqueStrings");
        }
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
        return true;        
    }
    
    public static boolean checkForUniqueStrings(Text[] arrayOfTXT) {
        if (printTheStuff) {
            System.out.println("293 --- StringUtilities, checkForUniqueStrings");
        }
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