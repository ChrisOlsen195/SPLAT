/**************************************************
 *                ANOVA1_Controller               *
 *                    02/14/25                    *
 *                     15:00                      *
 *************************************************/
package superClasses;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;

import splat.*;

public class ANOVA1_Controller {
    //  POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    public boolean goodToGo;
    public boolean checkForLegalChoices;
    public int n_QDVs; 
    public String strReturnStatus, daProcedure;
    public String explVarDescr, respVarDescr, subTitle;
    public ArrayList<String> allTheLabels;
 
    // My classes
    public ArrayList<ColumnOfData> anova1_ColsOfData;
    public ArrayList<String> varLabel;
    public CatQuantDataVariable cqdv;
    public QuantitativeDataVariable tempQDV;
    public ArrayList<QuantitativeDataVariable> incomingQDVs, allTheQDVs;
    public Data_Manager dm;
    
    // POJOs / FX

    public ANOVA1_Controller(Data_Manager dm) {
        this.dm = dm;
        if (printTheStuff == true) {
            System.out.println("41 *** ANOVA1_Controller (Super), Constructing");
        }
        anova1_ColsOfData = new ArrayList();
        varLabel = new ArrayList();
        strReturnStatus = "OK";
    }

    protected String doTheANOVA() { return "OK"; }
    
    public String getExplanatoryVariable() {  return explVarDescr; }
    public String getResponseVariable() { return respVarDescr; }
    public String getSubTitle() { 
        subTitle = respVarDescr + " vs. " + explVarDescr; 
        return subTitle;
    }
    
    public boolean getGoodToGo() { return goodToGo; }
    public String getReturnStatus() { return strReturnStatus; }
}