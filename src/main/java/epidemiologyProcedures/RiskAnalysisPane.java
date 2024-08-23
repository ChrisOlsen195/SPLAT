/******************************************************************************
 *                       RiskAnalysisPane                                     *
 *                          08/19/24                                          *
 *                            00:00                                           *
 * ***************************************************************************/

/******************************************************************************
 *           Constructed by Epidemiology_View                                 *
 *****************************************************************************/
package epidemiologyProcedures;

import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class RiskAnalysisPane extends Pane {
    int a, b, c, d;
    int[][] observed;
    
    double left_Col_1, left_Col_2, left_Col_3;
    double top_Row_1, top_Row_2, top_Row_3;
    double width_Col_1, width_Col_2, width_Col_3;
    double height_Row_1, height_Row_2, height_Row_3;
    
    Rectangle title_YVar, title_XVar, xVar_YesNo_Title, xTotals_Title, 
              titleY_YesNo, dataTable, yTotals, xTotalsTitle, xTotals, total;
    
    String strXVar, strYVar, strXVarTotalsTitle, strYVarTotalsTitle, strBigTitle;
    String[] strXValues, strYValues, strTableIntegers;

    Text txtXVar, txtYVar, txtBigTitle, txtXVarTotalsTitle, txtYVarTotalsTitle;
    
    Text[] txtXValues, txtYValues, txtEpiStrings, txtTableIntegers;
    
    TwoByTwo_Calculations twoByTwo_Calculations;
    
    ArrayList<String> epiReport;

    public RiskAnalysisPane() {
        //System.out.println("\n45 RiskAnalysisPane, Constructing");
        doSomeInitializations();
        
        strYValues[0] = "No";
        strYValues[1] = "Yes";      
        strBigTitle = "Risk Analysis";   
        strXVar = "MI Incidence over 3 years";
        strYVar = "OC-use group";        
        strXValues[0] = "Yes";        
        strYVar = "OC-use group";
        strXValues[0] = "Yes";
        strXValues[1] = "No"; 
        strYValues[0] = "Never-OC users";    
        strYValues[1] = "Current OC users";
        strXVarTotalsTitle = "Total";
        strYVarTotalsTitle = "Total";       
        
        observed = new int[2][2];
        a = observed[0][0] = 1;
        b = observed[1][0] = 1;
        c = observed[0][1] = 1;
        d = observed[1][1] = 1;    
    }
    
    public RiskAnalysisPane(ArrayList<String> fromBivCatModel) {
        //System.out.println("\n70 RiskAnalysisPane fromBivCatModel, Constructing");
        doSomeInitializations();
        a = observed[0][0] = Integer.parseInt(fromBivCatModel.get(6));
        b = observed[1][0] = Integer.parseInt(fromBivCatModel.get(8));
        c = observed[0][1] = Integer.parseInt(fromBivCatModel.get(7));
        d = observed[1][1] = Integer.parseInt(fromBivCatModel.get(9));
    
       twoByTwo_Calculations = new TwoByTwo_Calculations(observed);
       twoByTwo_Calculations.doRelRiskAndOddsRatio();
       
        width_Col_1 = 175;
        width_Col_2 = 325;
        width_Col_3 = 100;
        
        left_Col_1 = 5;
        left_Col_2 = left_Col_1 + width_Col_1;
        left_Col_3 = left_Col_2 + width_Col_2;
        
        height_Row_1 = 150;
        height_Row_2 = 125;
        height_Row_3 = 50;
        
        top_Row_1 = 75;
        top_Row_2 = top_Row_1 + height_Row_1;
        top_Row_3 = top_Row_2 + height_Row_2;
        
        Font fntBigTitle = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.REGULAR,24);
        Font fntNormal = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.REGULAR,16);
        Font fntEpiNormal = Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,14);
        Font fntBoldNumbers = Font.font("Times New Roman", FontWeight.NORMAL, FontPosture.REGULAR,18);

        strXValues = new String[2];
        strYValues = new String[2];
        txtXValues = new Text[2];
        txtYValues = new Text[2];

        strYValues[0] = "No";
        strYValues[1] = "Yes";     
        
        strBigTitle = "Risk Analysis";
        txtBigTitle = new Text(strBigTitle);
        txtBigTitle.setX(left_Col_2 + 0.15 * width_Col_2);
        txtBigTitle.setY(top_Row_1 - 25.);
        txtBigTitle.setFont(fntBigTitle);
        
        // Outcome variable
        strXVar = fromBivCatModel.get(3);
        txtXVar = new Text(strXVar);
        txtXVar.setX(left_Col_2 + 0.50 * width_Col_2 - 6.0 * strXVar.length());
        txtXVar.setY(top_Row_1 + 0.25 * height_Row_1);
        txtXVar.setFont(fntBoldNumbers);
        
        // Exposure variable
        strYVar = fromBivCatModel.get(0);
        txtYVar = new Text(strYVar);
        txtYVar.setX(0.5 *(left_Col_1 + left_Col_2) - 10.0 * strYVar.length());
        txtYVar.setY(top_Row_1 + 0.75 * height_Row_1);
        txtYVar.setFont(fntBoldNumbers);
        
        // Outcome Yes
        strXValues[0] = fromBivCatModel.get(4);       
        txtXValues[0] = new Text(strXValues[0]);
        txtXValues[0].setX(left_Col_2 + 0.25 * width_Col_2 - 2.0 * strYVar.length());
        txtXValues[0].setY(top_Row_1 + 0.75 * height_Row_1);
        txtXValues[0].setFont(fntBoldNumbers);   
        
        // Outcome No
        strXValues[1] = fromBivCatModel.get(5);     
        txtXValues[1] = new Text(strXValues[1]);
        txtXValues[1].setX(left_Col_2 + 0.75 * width_Col_2 - 2.0 * strXValues[1].length());
        txtXValues[1].setY(top_Row_1 + 0.75 * height_Row_1);
        txtXValues[1].setFont(fntBoldNumbers); 
        
        // Exposure No
        strYValues[0] = fromBivCatModel.get(2);       
        txtYValues[0] = new Text(strYValues[0]);
        txtYValues[0].setX(left_Col_1 + 25);
        txtYValues[0].setY(top_Row_2 + 0.67 * height_Row_2);
        txtYValues[0].setFont(fntBoldNumbers); 
        
        // Exposure Yes
        strYValues[1] = fromBivCatModel.get(1);    
        txtYValues[1] = new Text(strYValues[1]);
        txtYValues[1].setX(left_Col_1 + 25);
        txtYValues[1].setY(top_Row_2 + 0.33 * height_Row_2);
        txtYValues[1].setFont(fntBoldNumbers);    
        
        strXVarTotalsTitle = "Total";       
        txtXVarTotalsTitle = new Text(strXVarTotalsTitle);
        txtXVarTotalsTitle.setX(left_Col_1 + 25);
        txtXVarTotalsTitle.setY(top_Row_3 + 0.50 * height_Row_3);
        txtXVarTotalsTitle.setFont(fntBoldNumbers); 
        
        strYVarTotalsTitle = "Total";       
        txtYVarTotalsTitle = new Text(strYVarTotalsTitle);
        txtYVarTotalsTitle.setX(left_Col_3 + 0.33 * width_Col_3);
        txtYVarTotalsTitle.setY(top_Row_1 + 0.75 * height_Row_1);
        txtYVarTotalsTitle.setFont(fntBoldNumbers);           

        /****************************************************************
        *                       First Row                               * 
        ****************************************************************/

        // ******************  Label of the Y Variable  *****************
        title_YVar = new Rectangle(left_Col_1, top_Row_1, width_Col_1, height_Row_1);
        title_YVar.setFill(Color.WHITE);
        title_YVar.setStroke(Color.BLACK);

        //  ************  Label of the X Variable  ************************        
        title_XVar = new Rectangle(left_Col_2, top_Row_1, width_Col_2, 0.50 * height_Row_1);
        title_XVar.setFill(Color.WHITE);
        title_XVar.setStroke(Color.BLACK);      

        //  ************   Yes/No Box for X values  ************************   
        double startTopAt = top_Row_1 + 0.50 * height_Row_1;
        xVar_YesNo_Title = new Rectangle(left_Col_2, startTopAt, width_Col_2, 0.50 * height_Row_1);
        xVar_YesNo_Title.setFill(Color.WHITE);
        xVar_YesNo_Title.setStroke(Color.BLACK);           

        //  ************   Totals Title for over X values  ********************        
        xTotals_Title = new Rectangle(left_Col_3, top_Row_1, width_Col_3, height_Row_1);
        xTotals_Title.setFill(Color.WHITE);
        xTotals_Title.setStroke(Color.BLACK);      
                
        /****************************************************************
        *                       Second Row                              * 
        ****************************************************************/
        
        //  ************   Yes/No Title Box for Y values  ************************        
        titleY_YesNo = new Rectangle(left_Col_1, top_Row_2, width_Col_1, height_Row_2);
        titleY_YesNo.setFill(Color.WHITE);
        titleY_YesNo.setStroke(Color.BLACK);         
        
        //  ************   data table  ************************        
        dataTable = new Rectangle(left_Col_2, top_Row_2, width_Col_2, height_Row_2);
        dataTable.setFill(Color.WHITE);
        dataTable.setStroke(Color.BLACK); 
            
        //  ************   Y Totals  ************************        
        yTotals = new Rectangle(left_Col_3, top_Row_2, width_Col_3, height_Row_2);
        yTotals.setFill(Color.WHITE);
        yTotals.setStroke(Color.BLACK); 
              
        /****************************************************************
        *                       Third Row                              * 
        ****************************************************************/
        
        //  ************   X Totals Title  ************************        
        xTotalsTitle = new Rectangle(left_Col_1, top_Row_3, width_Col_1, height_Row_3);
        xTotalsTitle.setFill(Color.WHITE);
        xTotalsTitle.setStroke(Color.BLACK); 
        
        //  ************   X Totals  ************************        
        xTotals = new Rectangle(left_Col_2, top_Row_3, width_Col_2, height_Row_3);
        xTotals.setFill(Color.WHITE);
        xTotals.setStroke(Color.BLACK);     
 
        //  ************   Total Totals  ************************        
        total = new Rectangle(left_Col_3, top_Row_3, width_Col_3, height_Row_3);
        total.setFill(Color.WHITE);
        total.setStroke(Color.BLACK);  
        
        //  ************   The counts to Text ************************

        strTableIntegers[0] = String.format("%5d", a);
        strTableIntegers[1] = String.format("%5d", b);
        strTableIntegers[2] = String.format("%5d", c);
        strTableIntegers[3] = String.format("%5d", d);
 
        strTableIntegers[4] = String.format("%5d", twoByTwo_Calculations.getXCountNos());
        strTableIntegers[5] = String.format("%5d", twoByTwo_Calculations.getXCountYesses());
        strTableIntegers[6] = String.format("%5d", twoByTwo_Calculations.getYCountNos());
        strTableIntegers[7] = String.format("%5d", twoByTwo_Calculations.getYCountYesses());
        strTableIntegers[8] = String.format("%5d", twoByTwo_Calculations.getTotal());
        
        for (int i = 0; i <= 8; i++) {
            txtTableIntegers[i] = new Text(strTableIntegers[i]);
            txtTableIntegers[i].setStroke(Color.BLACK);
            txtTableIntegers[i].setFont(fntNormal);
        }
        
        txtTableIntegers[0].setX(left_Col_2 + 0.25 * width_Col_2);  //  a
        txtTableIntegers[0].setY(top_Row_2 + 0.33 * height_Row_2);
        
        txtTableIntegers[1].setX(left_Col_2 + 0.25 * width_Col_2);  //  b
        txtTableIntegers[1].setY(top_Row_2 + 0.67 * height_Row_2);

        txtTableIntegers[2].setX(left_Col_2 + 0.75 * width_Col_2);  // c
        txtTableIntegers[2].setY(top_Row_2 + 0.33 * height_Row_2);

        txtTableIntegers[3].setX(left_Col_2 + 0.75 * width_Col_2);  // d
        txtTableIntegers[3].setY(top_Row_2 + 0.67 * height_Row_2);

        txtTableIntegers[4].setX(left_Col_2 + 0.25 * width_Col_2);    //  x Nos
        txtTableIntegers[4].setY(top_Row_3 + 0.50 * height_Row_3);    

        txtTableIntegers[5].setX(left_Col_2 + 0.75 * width_Col_2);  //  x Yesses
        txtTableIntegers[5].setY(top_Row_3 + 0.50 * height_Row_3);

        txtTableIntegers[6].setX(left_Col_3 + 0.50 * width_Col_3);  //  y Nos
        txtTableIntegers[6].setY(top_Row_2 + 0.67 * height_Row_2);

        txtTableIntegers[7].setX(left_Col_3 + 0.50 * width_Col_3);  // y Yesses
        txtTableIntegers[7].setY(top_Row_2 + 0.33 * height_Row_2);
        
        txtTableIntegers[8].setX(left_Col_3 + 0.5 * width_Col_3);  // totals
        txtTableIntegers[8].setY(top_Row_3 + 0.50 * height_Row_3);
        
        epiReport = new ArrayList();
        epiReport  =  twoByTwo_Calculations.getEpiReport();
        int nEpiStrings = epiReport.size();
        txtEpiStrings = new Text[nEpiStrings];
        
        for (int ithEpiString = 0; ithEpiString < nEpiStrings; ithEpiString++) {
            String epiString = epiReport.get(ithEpiString);
            txtEpiStrings[ithEpiString] = new Text(epiString);
            txtEpiStrings[ithEpiString].setX(10.);
            txtEpiStrings[ithEpiString].setY(450. + 10.0 * ithEpiString);
            txtEpiStrings[ithEpiString].setFont(fntEpiNormal);
        }

        this.setPrefSize(800, 250);
        getChildren().add(txtBigTitle);
        getChildren().addAll(title_YVar, title_XVar, xVar_YesNo_Title, xTotals_Title);
        getChildren().addAll(titleY_YesNo, dataTable, yTotals);
        getChildren().addAll(xTotalsTitle, xTotals, total);
        getChildren().addAll(txtXVar, txtYVar, txtXVarTotalsTitle, txtYVarTotalsTitle);
        getChildren().addAll(txtXValues);
        getChildren().addAll(txtYValues);
        getChildren().addAll(txtTableIntegers);
        getChildren().addAll(txtEpiStrings);        
    }
    
    private void doSomeInitializations() {
        strXValues = new String[2];
        strYValues = new String[2];
        observed = new int[2][2];
        
        strTableIntegers = new String[9];
        txtTableIntegers = new Text[9];
    }
    
    public RiskAnalysisPane getRiskAnalysisPane() { return this; }
}
