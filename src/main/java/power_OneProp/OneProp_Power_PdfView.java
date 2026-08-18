/**************************************************
 *            OneProp_Power_PdfView               *
 *                   05/23/26                     *
 *                    00:00                       *
 *************************************************/
package power_OneProp;

import genericClasses.Point_2D;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import noncentrals.JDistr_Noncentrals.*;
import superClasses.*;
import genericClasses.*;
import javafx.scene.control.CheckBox;

import utilityClasses.*;

public class OneProp_Power_PdfView extends OneParam_Power_PdfView { 
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean[] hBoxChBoxSettings;
    

    
    final double MIDDLE_Z = 0.999;
    final double SQRT2_OVER2 = Math.sqrt(2.0) / 2.0;
    
    /*
    String strRejectionCriterion, strTitle1, strtitle2;
    //String graphsCSS;
    String strSingleCritValueDescr = "Critical value = ";
    String strTwoCritValuesDescr = "Critical values = ";
    
    final String strRejectRegion = "Reject";
    final String strNonRejectRegion = "Fail to \nreject";   
    final String strGoodCall = "  Good  \n  Call!";
    final String strPrTypeII = "Oops! Type \n II error";
    final String str_AltSampDistDescr = "The alternate\ndistribution";
    final String strNullSampDistDescr = "  The null\ndistribution";
    
    String[] strHBoxCheckBoxDescr;
    */
    
    // My classes
    OneProp_Power_Model oneProp_Power_Model;
    
    //  FX

    public OneProp_Power_PdfView(OneProp_Power_Model oneProp_Power_Model, 
                             OneProp_Power_Dashboard oneProp_Power_Dashboard,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("*** 79 OneProp_Power_PdfView, Constructing");
        }
        this.oneProp_Power_Model = oneProp_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        this.oneProp_Power_Model.restoreNullValues();
        nullParam = this.oneProp_Power_Model.getNullParam();
        stErrNullParam = this.oneProp_Power_Model.getStErr_NullParam();
        stErrAltParam = this.oneProp_Power_Model.getStErr_AltParam();
        // Control the height of the normal curves
        densityFactor = 0.35 / Normal.density(nullParam, nullParam, stErrNullParam, false);
        alpha = this.oneProp_Power_Model.getAlpha();
        effectSize = this.oneProp_Power_Model.getEffectSize();
        lowerSliver =  (1.0 - MIDDLE_Z) / 2.0;
        upperSliver = 1 - lowerSliver;     
        nullParam = this.oneProp_Power_Model.getNullParam();
        if (printTheStuff) {
            System.out.println("98 --- OneProp_Power_PdfView, getting strRejRegion");
        }
        strRejectionCriterion = oneProp_Power_Model.getRejectionCriterion();
        if (printTheStuff) {
            System.out.println("102 --- OneProp_Power_PdfView, strRejectionCriterion = " + strRejectionCriterion);
        }
        
        switch (strRejectionCriterion) {
            case "LessThan":
                altParam = nullParam - effectSize;
                break;
                
            case "NotEqual":
            case "GreaterThan":
                altParam = nullParam + effectSize;
                break;
                
            default:
                String switchFailure = "Switch failure: OneProp_Power_PdfView 109 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }         
        nonRejectionRegion = new Point_2D(oneProp_Power_Model.getNonRejectionRegion().getFirstValue(),
                                                oneProp_Power_Model.getNonRejectionRegion().getSecondValue());

        makeTheCheckBoxes();
        makeItHappen();  
    } 

        @Override
    protected void setUpUI() { 
        strTitle1 = " Power, single Proportion Z";
        strTitle2 = oneProp_Power_Model.getPrintedNullHypothesis() + " vs. " + oneProp_Power_Model.getPrintedAltHypothesis();
        txtTitle1 = new Text(50, 25, strTitle1);        
        txtTitle2 = new Text (60, 45, strTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
}
