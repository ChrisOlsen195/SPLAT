/****************************************************************************
 *                        PropAndProb                                       * 
 *                         11/01/23                                         *
 *                           09:00                                          *
 ***************************************************************************/
package the_z_procedures;

public class PropAndProb {
    int numerator;  //  of the proportion
    double proportion, mass, cdfProp;
    
    public PropAndProb()  {
        numerator = 0; proportion = 0.; mass = 0.0; cdfProp = 0.0;
        System.out.println("14 PropAndProb, constructing");
    }
    
    public PropAndProb(int daNumerator, double daProp, double daMass)  
    {
        numerator = daNumerator; proportion = daProp; mass = daMass; cdfProp = 0.0;
        System.out.println("20 PropAndProb, constructing");
    }   
    
    public int getNumerator() { return numerator; }
    public double getProportion() { return proportion; }
    public double getMass() { return mass; }
    public double getCDF()  { return cdfProp; }
    
    public void setNumerator(int toThisNumerator) { numerator = toThisNumerator; }
    public void setProportion(double toThisProp) { proportion = toThisProp; }
    public void setMAss(double toThisMass) { mass = toThisMass; }   
    public void setCDF(double toThisCDF) {cdfProp = toThisCDF; }
}
