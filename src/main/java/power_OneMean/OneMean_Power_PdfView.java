/**************************************************
 *            OneMean_Power_PdfView               *
 *                   05/23/26                     *
 *                    18:00                       *
 *************************************************/
package power_OneMean;

import genericClasses.Point_2D;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import noncentrals.JDistr_Noncentrals.*;
import superClasses.*;
import utilityClasses.*;

public class OneMean_Power_PdfView extends OneParam_Power_PdfView  { 
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    final double MIDDLE_Z = 0.999;
    final double SQRT2_Over2 = Math.sqrt(2.0) / 2.0;

    // My classes
    OneMean_Power_Model oneMean_Power_Model;
    
    //  FX

    public OneMean_Power_PdfView(OneMean_Power_Model oneMean_Power_Model, 
                             OneMean_Power_Dashboard single_Z_Dash,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("37 *** OneMean_Power_PdfView, Constructing");
        }
        this.oneMean_Power_Model = oneMean_Power_Model;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
                
        oneMean_Power_Model.restoreNullValues();
        nullParam = oneMean_Power_Model.getNullParam();
        stErrNullParam = oneMean_Power_Model.getStErr_NullParam();        
        // Control the height of the normal curves
        densityFactor = 0.35 / Normal.density(nullParam, nullParam, stErrNullParam, false);
        alpha = oneMean_Power_Model.getAlpha();
        effectSize = oneMean_Power_Model.getEffectSize();
        lowerSliver =  (1.0 - MIDDLE_Z) / 2.0;
        upperSliver = 1 - lowerSliver;     
        nullParam = oneMean_Power_Model.getNullParam();
        strRejectionCriterion = oneMean_Power_Model.getRejectionCriterion();
        
        switch (strRejectionCriterion) {
            case "LessThan":
                altParam = nullParam - effectSize;
                break;
                
            case "NotEqual":
            case "GreaterThan":
                altParam = nullParam + effectSize;
                break;
                
            default:
                String switchFailure = "Switch failure: OneMean_Power_PdfView 68 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }        
  
        nonRejectionRegion = new Point_2D(oneMean_Power_Model.getNonRejectionRegion().getFirstValue(),
                                                oneMean_Power_Model.getNonRejectionRegion().getSecondValue());
    
        checkBoxHeight = 350.0;
        //graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes();
        makeItHappen(); 
    }  
    
    @Override
    protected void setUpUI() { 
        strTitle1 = " Power, single Mean";
        strTitle2 = oneMean_Power_Model.getPrintedNullHypothesis() + " vs. " + oneMean_Power_Model.getPrintedAltHypothesis();
        txtTitle1 = new Text(50, 25, strTitle1);
        txtTitle2 = new Text (60, 45, strTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
}
