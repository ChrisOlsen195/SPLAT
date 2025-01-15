/**************************************************
 *                  DataUtilities                 *
 *                    09/07/24                    *
 *                      12:00                     *
 *************************************************/
package utilityClasses;
import dataObjects.QuantitativeDataVariable;
import genericClasses.Point_2D;
import java.util.ArrayList;

/**************************************************
 *              Point_2D_Array_Utilities          *
 *                    09/07/24                    *
 *                      12:00                     *
 *************************************************/
public class Point_2D_ArrayList_Utilities {
    
    int n;
    ArrayList<Point_2D> al_Point_2Ds;
    double[] arrayOfDoubles;
    String varLabel_0, varDescr_0, varLabel_1, varDescr_1;
    
    Point_2D_ArrayList_Utilities() {
        
    }
    

    Point_2D_ArrayList_Utilities(double[] x, double[] y) {
        varLabel_0 = "Null";
        varDescr_0  = "Null";
        varLabel_1 = "Null";
        varDescr_1  = "Null";
        n = x.length;
        al_Point_2Ds = new ArrayList();
        for (int ith = 0; ith < n; ith++) {
            Point_2D temp2D = new Point_2D(x[ith], y[ith]);
            al_Point_2Ds.add(temp2D);
        }
    }
    
    Point_2D_ArrayList_Utilities(String varLabel_0, String varDescr_0, 
                                 String varLabel_1, String varDescr_1,
            double[] x, double[] y) {
        this.varLabel_0 = varLabel_0;
        this.varDescr_0  = varDescr_0;
        this.varLabel_1 = varLabel_1;
        this.varDescr_1  = varDescr_1;
        n = x.length;
        al_Point_2Ds = new ArrayList();
        for (int ith = 0; ith < n; ith++) {
            Point_2D temp2D = new Point_2D(x[ith], y[ith]);
            al_Point_2Ds.add(temp2D);
        }
    }
    
    public ArrayList<Point_2D> returnAs_ALOfStrings() { return al_Point_2Ds; }
    
    public double[] returnXsAsArrayOfDoubles() { 
        arrayOfDoubles = new double[n];
        for (int ith = 0; ith < n; ith++) { 
            arrayOfDoubles[ith] = al_Point_2Ds.get(ith).getFirstValue();
        }
        return arrayOfDoubles;
    }
    
    public double[] returnYsAsArrayOfDoubles() { 
        arrayOfDoubles = new double[n];
        for (int ith = 0; ith < n; ith++) { 
            arrayOfDoubles[ith] = al_Point_2Ds.get(ith).getSecondValue();
        }
        return arrayOfDoubles;
    }
    
    /*
    public double[] returnXsAsArrayOfStrings() { 
        arrayOfDoubles = new double[n];
        for (int ith = 0; ith < n; ith++) { 
            arrayOfDoubles[ith] = al_Point_2Ds.get(ith).getFirstValue();
        }
        return arrayOfDoubles;
    }
    
    public double[] returnYsAsArrayOStrings() { 
        arrayOfDoubles = new double[n];
        for (int ith = 0; ith < n; ith++) { 
            arrayOfDoubles[ith] = al_Point_2Ds.get(ith).getSecondValue();
        }
        return arrayOfDoubles;
    }
    */
    
    public QuantitativeDataVariable returnXsAsQDV() { 
        arrayOfDoubles = new double[n];
        for (int ith = 0; ith < n; ith++) { 
            arrayOfDoubles[ith] = al_Point_2Ds.get(ith).getFirstValue();
        }
        QuantitativeDataVariable qdv = new QuantitativeDataVariable(varLabel_0,
                                                                    varDescr_0,
                                                                    arrayOfDoubles);
        return qdv;
    }
    
    public QuantitativeDataVariable returnYsAsQDV() { 
        arrayOfDoubles = new double[n];
        for (int ith = 0; ith < n; ith++) { 
            arrayOfDoubles[ith] = al_Point_2Ds.get(ith).getSecondValue();
        }
        QuantitativeDataVariable qdv = new QuantitativeDataVariable(varLabel_0,
                                                                    varDescr_0,
                                                                    arrayOfDoubles);
        return qdv;
    }
}
