/**************************************************
 *              Colors_and_CSS_Strings            *
 *                    11/01/23                    *
 *                     12:00                      *
 *************************************************/
package utilityClasses;

import javafx.scene.paint.Color;

public class Colors_and_CSS_Strings {
    
    static String cssLabel_01, cssLabel_02, cssLabel_03, cssLabel_04;
    
    static Color[] graphColors_01 = { Color.BROWN, Color.OLIVE, Color.TEAL,  Color.NAVY, 
                            Color.RED, Color.BLACK, Color.ORANGE,Color.MAROON, 
                            Color.GREEN, Color.CYAN, Color.BLUE, Color.PURPLE, 
                            Color.LINEN, Color.MAGENTA };   
    
    static Color[] graphColors_02 = { Color.RED, Color.BLUE, Color.TEAL,  Color.NAVY, 
                            Color.BROWN, Color.BLACK, Color.ORANGE,Color.MAROON, 
                            Color.GREEN, Color.CYAN, Color.OLIVE, Color.PURPLE, 
                            Color.LINEN, Color.MAGENTA };  

    
     static Color[] graphColors_03 = { Color.NAVY, Color.RED, Color.BLACK, Color.ORANGE,
                            Color.MAGENTA, Color.BROWN, Color.OLIVE, Color.TEAL, 
                            Color.MAROON, Color.GREEN, 
                            Color.CYAN, Color.BLUE, Color.PURPLE, Color.LINEN};  

    
    static Color[] graphColors_04 = { Color.ORANGE, Color.BROWN, Color.OLIVE, Color.TEAL, 
                            Color.NAVY, Color.BLUE, Color.BLACK, Color.MAGENTA,
                            Color.MAROON, Color.GREEN, 
                            Color.CYAN, Color.RED, Color.PURPLE, Color.LINEN};   

    
    static Color[] graphColors_05 = { Color.RED, Color.BLUE, Color.TEAL, Color.NAVY, 
                            Color.BROWN, Color.BLACK, Color.ORANGE,Color.MAROON, 
                            Color.GREEN, Color.CYAN, Color.OLIVE, Color.PURPLE, 
                            Color.LINEN, Color.MAGENTA };    
    
    static Color[] graphColors_06 = {  Color.BLUE, Color.RED, Color.OLIVE, Color.TEAL, 
                            Color.NAVY, Color.GREEN, Color.BLACK, Color.ORANGE,
                            Color.MAROON, Color.PURPLE, Color.MAGENTA, Color.BROWN,
                            Color.CYAN, Color.LINEN};   

    static Color[] graphColors_07 = {  Color.BLUE, Color.RED, Color.BROWN, Color.TEAL, 
                            Color.NAVY, Color.GREEN, Color.BLACK, Color.ORANGE,
                            Color.MAROON, Color.PURPLE, Color.MAGENTA, Color.OLIVE,
                            Color.CYAN, Color.LINEN};      

    
    public Colors_and_CSS_Strings() { 
        
        cssLabel_01 = "-fx-background-color: white;" 
                    + "-fx-text-fill: black;" 
                    + "-fx-pref-width: 75px;"
                    + "-fx-pref-height: 25px;"
                    + "-fx-weight: bold;"
                    + "-fx-font-family: Times New Roman;"
                    + "-fx-font-size: 14px;"
                    + "-fx-alignment: center;"
                    + "-fx-text-fill: black;"
                    + "-fx-background-color: yellow;"
                    + "-fx-border-color: transparent gray transparent gray;"
                    + "-fx-border-width: 1px;";
        
        cssLabel_02 = "-fx-background-color: white;" 
                    + "-fx-text-fill: black;"  
                    + "-fx-pref-width: 100px;"
                    + "-fx-pref-height: 25px;"
                    + "-fx-weight: bold;"
                    + "-fx-font-family: Times New Roman;"
                    + "-fx-font-size: 14px;"
                    + "-fx-alignment: center;"
                    + "-fx-text-fill: black;"
                    + "-fx-background-color: yellow;"
                    + "-fx-border-color: transparent gray transparent gray;"
                    + "-fx-border-width: 1px;";
        
        cssLabel_03 = "-fx-background-color: white;" 
                    + "-fx-text-fill: black;" 
                    + "-fx-pref-width: 125px;"
                    + "-fx-pref-height: 25px;"
                    + "-fx-weight: bold;"
                    + "-fx-font-family: Times New Roman;"
                    + "-fx-font-size: 14px;"
                    + "-fx-alignment: center;"
                    + "-fx-text-fill: black;"
                    + "-fx-background-color: yellow;"
                    + "-fx-border-color: transparent gray transparent gray;"
                    + "-fx-border-width: 1px;";
        
        cssLabel_04 = "-fx-background-color: white;" 
                    + "-fx-text-fill: red;" 
                    + "-fx-pref-width: 300px;"
                    + "-fx-pref-height: 25px;"
                    + "-fx-weight: bold;"
                    + "-fx-font-family: Times New Roman;"
                    + "-fx-font-size: 14px;"
                    + "-fx-alignment: center;"
                    + "-fx-background-color: yellow;"
                    + "-fx-border-color: transparent gray transparent gray;"
                    + "-fx-border-width: 1px;";  
    }
    
    public static Color[] getGraphColors_01() {return graphColors_01; }
    public static Color[] getGraphColors_02() {return graphColors_02; }
    public static Color[] getGraphColors_03() {return graphColors_03; }
    public static Color[] getGraphColors_04() {return graphColors_04; }
    public static Color[] getGraphColors_05() {return graphColors_05; }
    public static Color[] getGraphColors_06() {return graphColors_06; }
    public static Color[] getGraphColors_07() {return graphColors_07; }   
    
    public static String get_cssLabel_01() { return cssLabel_01; }
    public static String get_cssLabel_02() { return cssLabel_02; }
    public static String get_cssLabel_03() { return cssLabel_03; }
    public static String get_cssLabel_04() { return cssLabel_04; }
}
