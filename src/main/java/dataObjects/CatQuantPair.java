 /*************************************************
 *                  CatQuantPair                  *
 *                    05/24/24                    *
 *                      15:00                     *
 *************************************************/
package dataObjects;

public class CatQuantPair {
    // POJOs
    private int theQuantValueInt;
    private double theQuantValueDouble;
    private String strTheCatValue;
    
    public CatQuantPair(String theInCatValue, double quantValueDouble)  {
        strTheCatValue = theInCatValue;
        theQuantValueDouble = quantValueDouble;     
    }
    
    public CatQuantPair(String theInCatValue, int quantValueInt)  {
        strTheCatValue = theInCatValue;
        theQuantValueInt = quantValueInt;     
    }
    
    public CatQuantPair(String theInCatValue, String theInQuantValue)  {
        strTheCatValue = theInCatValue;
        theQuantValueDouble = Double.parseDouble(theInQuantValue);     
    }

    public CatQuantPair getCQP() {return this; }

    public String getCatValue() { return strTheCatValue;}
    public double getQuantValueInt() { return theQuantValueInt;}
    public double getQuantValueDouble() { return theQuantValueDouble;}
    public String getQuantValueAsString() { return String.valueOf(theQuantValueDouble);}

    public void setCatValue(String newCatValue) { strTheCatValue = newCatValue;}
    public void setQuantValueInt(int newQuantValue) { theQuantValueInt = newQuantValue;} 
    public void setQuantValueDouble(double newQuantValue) { theQuantValueDouble = newQuantValue;} 
    
    public String toString() {
        System.out.println("41 CatQuantPair, catValue = " + strTheCatValue);
        System.out.println("42 CatQuantPair, quanValue = " + theQuantValueDouble);
        return "ta daaaaa!  A CatQuantPair";
    }
}
