/****************************************************************************
 *                     X2GOF_DataDialogObj                                  * 
 *                           01/15/25                                       *
 *                            12:00                                         *
 ***************************************************************************/
/****************************************************************************
 *  This object is passed back to the X2GOF_DataByHand_Dialog                      * 
 ***************************************************************************/
package chiSquare.GOF;

public class X2GOF_DataDialogObj {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false; 
    
    int nCategories;
    int[] gofObs;
    
    double[] gofExpProps;

    String strGOFVariable;
    String[] strGOFCats;
    
    public X2GOF_DataDialogObj() { 
        if (printTheStuff == true) {
            System.out.println("27 *** X2GOF_DataDialogObj, Constructing");
        }
        nCategories = 0;
    }
    
    public X2GOF_DataDialogObj(int nCategories) { 
        if (printTheStuff == true) {
            System.out.println("34 *** X2GOF_DataDialogObj, Constructing");
        }
        this.nCategories = nCategories; 
        gofExpProps = new double[nCategories];
        gofObs = new int[nCategories];
        strGOFCats = new String[nCategories];    
    }
    
    public int getNCategories() { return nCategories;  }
    public void setNCategories(int toThis) {nCategories = toThis; } 
    public String[] getTheGOFCategories() {return strGOFCats; }    
    public void setTheGOFCategories( String[] theGOFCats) {
        System.arraycopy(theGOFCats, 0, strGOFCats, 0, nCategories);   
    }    
    public String getGOFVariable() { return strGOFVariable; }
    public void setGOFVariable( String theVar) { strGOFVariable = theVar; } 
    public double[] getExpectedProps() {return gofExpProps; }   
    public void setExpectedProps (double[] expProps) {                
        System.arraycopy(expProps, 0, gofExpProps, 0, nCategories);
    }   
    public int[] getObservedValues() {return gofObs; }    
    public void setObservedValues (int[] obsVals) {
        System.arraycopy(obsVals, 0, gofObs, 0, nCategories);
    }  
    
    public String toString() {
        System.out.println(" X2GOF_DataDialogObj.toString()...");
        System.out.println("   Category     Observed       Expected");
        for (int ithCat = 0; ithCat < nCategories; ithCat++) {
            System.out.println("   " + strGOFCats[ithCat] + "   " + gofObs[ithCat] + "   " + gofExpProps[ithCat]);
        }
        return "Endeth DataDialogObj";
    }
}

