/************************************************************
 *                    Change_Radius_Dialog                  *
 *                          08/04/24                        *
 *                            21:00                         *
 ***********************************************************/
package dialogs;

import bivariateProcedures_Categorical.BivCat_PieChartView;
import bootstrapping.*;
import chiSquare_Assoc.X2Assoc_PieChartView;
import epidemiologyProcedures.Epi_PieChartView;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import proceduresManyUnivariate.MultUni_DotPlotView;
import proceduresOneUnivariate.DotPlot_View;
import proceduresTwoUnivariate.DotPlot_View_For2Ind;
import utilityClasses.MyAlerts;

public class Change_Radius_Dialog extends Stage { 
    // POJOs
    double relRad;
    
    String strTitle, strReturnStatus, strDotOrPie;
    
    // My classes
    DotPlot_View dotPlot_View;
    MultUni_DotPlotView multUni_DotPlotView;
    Boot_DotPlot_DistrView chooseStats_DotPlot_DistrView; 
    X2Assoc_PieChartView x2Assoc_PieChart_View;
    BivCat_PieChartView bivCat_PieChart_View;
    Epi_PieChartView epi_PieChart_View;
    // JavaFX POJOs
    Button btnOK, btnCancel;
    Canvas canvasSlider;
    GraphicsContext gc;
    Label lblTitle;
    HBox hBoxButtonPanel;
    VBox root;
    Slider sliderRadius;
    Text txtTitle;
    Scene scene; 
    
    public Change_Radius_Dialog(X2Assoc_PieChartView x2Assoc_PieChart_View) {
        // System.out.println("58 Change_Radius_Dialog, Constructing");
        this.x2Assoc_PieChart_View = x2Assoc_PieChart_View;
        strDotOrPie = "PIE";
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        lblTitle = new Label("Set radius for pieChart / dotplot / etc");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        relRad = 1.0;   //  Default
        makeDirectionsPanel();
        makeButtonPanel();
        makeSliderPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvasSlider,
                                  sliderRadius,
                                  hBoxButtonPanel);        
        // width, height
        scene = new Scene (root, 350, 275);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        setScene(scene);
    } 
    
    public Change_Radius_Dialog(BivCat_PieChartView bivCat_PieChart_View) {
        //System.out.println("92 Change_Radius_Dialog, Constructing");
        this.bivCat_PieChart_View = bivCat_PieChart_View;
        strDotOrPie = "EPI";
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        lblTitle = new Label("Set radius for pieChart / dotplot / etc");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        relRad = 1.0;   //  Default
        makeDirectionsPanel();
        makeButtonPanel();
        makeSliderPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvasSlider,
                                  sliderRadius,
                                  hBoxButtonPanel);        
        // width, height
        scene = new Scene (root, 350, 275);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        setScene(scene);
    } 
    
    public Change_Radius_Dialog(Epi_PieChartView epi_PieChart_View) {
        //System.out.println("92 Change_Radius_Dialog, Constructing");
        this.epi_PieChart_View = epi_PieChart_View;
        strDotOrPie = "EPI";
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        lblTitle = new Label("Set radius for pieChart / dotplot / etc");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        relRad = 1.0;   //  Default
        makeDirectionsPanel();
        makeButtonPanel();
        makeSliderPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvasSlider,
                                  sliderRadius,
                                  hBoxButtonPanel);        
        // width, height
        scene = new Scene (root, 350, 275);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        setScene(scene);
    } 
    
    public Change_Radius_Dialog(DotPlot_View dotPlot_View) {
        this.dotPlot_View = dotPlot_View;
        strDotOrPie = "DOT";
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        lblTitle = new Label("Set radius for dotplot");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        relRad = 1.0;   //  Default
        makeDirectionsPanel();
        makeButtonPanel();
        makeSliderPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvasSlider,
                                  sliderRadius,
                                  hBoxButtonPanel);        
        // width, height
        scene = new Scene (root, 350, 275);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        setScene(scene);
    } 
    
    public Change_Radius_Dialog(Boot_DotPlot_DistrView boot_ChooseStats_DotPlot_DistrView) {
        this.chooseStats_DotPlot_DistrView = boot_ChooseStats_DotPlot_DistrView;
        strDotOrPie = "CHOOSEVAR";
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        lblTitle = new Label("Set radius for dotplot");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        relRad = 2.0;   //  Default
        makeDirectionsPanel();
        makeButtonPanel();
        makeSliderPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvasSlider,
                                  sliderRadius,
                                  hBoxButtonPanel);        
        // width, height
        scene = new Scene (root, 350, 275);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        setScene(scene);
    }
    
    public Change_Radius_Dialog(MultUni_DotPlotView multUni_DotPlotView) {
        this.multUni_DotPlotView = multUni_DotPlotView;
        strDotOrPie = "MULT_DOT";
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        lblTitle = new Label("Set radius for dotplot");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        relRad = 1.0;   //  Default
        makeDirectionsPanel();
        makeButtonPanel();
        makeSliderPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvasSlider,
                                  sliderRadius,
                                  hBoxButtonPanel);        
        // width, height
        scene = new Scene (root, 350, 275);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        setScene(scene);
    }
    
    public Change_Radius_Dialog(DotPlot_View_For2Ind dotPlot_View_For2Ind) {
        //this.dotPlot_View_For2Ind = dotPlot_View_For2Ind;
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        lblTitle = new Label("Set radius");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        
        relRad = 1.0;   //  Default
        makeDirectionsPanel();
        makeButtonPanel();
        makeSliderPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvasSlider,
                                  sliderRadius,
                                  hBoxButtonPanel);        
        // width, height
        scene = new Scene (root, 350, 275);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        setScene(scene);
    } 
    

    
    private void makeDirectionsPanel() {  
        canvasSlider = new Canvas(350, 50);
        gc = canvasSlider.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        strTitle = " Using the slider below, set the diameter of the dots "
                   + "\nin the dot plot, relative to the bin size.  As "
                   + "\nan example, a value of 0.5 woul give a diameter "
                   + "\nof the dots equal to 50% of the bin width.";
        txtTitle = new Text(50, 150, strTitle);
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BLACK, 14));
    }
 
    private void makeSliderPanel() {
        sliderRadius = new Slider();
        sliderRadius.setMin(0.0);
        sliderRadius.setMax(2.0);
        sliderRadius.setValue(0.9);        
        sliderRadius.setShowTickLabels(true);
        sliderRadius.setShowTickMarks(true);
        sliderRadius.setMajorTickUnit(0.25);
        sliderRadius.setMinorTickCount(5);
        sliderRadius.setSnapToTicks(true);
        // Top, Right, Bottom, Left
        sliderRadius.setPadding(new Insets(5, 10, 5, 10));
        sliderRadius.setStyle("-fx-font-size: 18");

        sliderRadius.valueProperty().addListener((ObservableValue<? extends Number> prop, Number oldVal,
              Number newVal) -> {
                relRad = (double)newVal;
                if (sliderRadius.isValueChanging()) {
                    switch (strDotOrPie) {
                        case "DOT":
                            dotPlot_View.setRelRad(relRad);
                            dotPlot_View.doTheGraph();
                            break;
                        case "CHOOSEVAR":
                            chooseStats_DotPlot_DistrView.setRelRad(relRad);
                            chooseStats_DotPlot_DistrView.doTheGraph();
                            break;
                        case "MULT_DOT":
                            multUni_DotPlotView.setRelRad(relRad);
                            multUni_DotPlotView.doTheGraph();
                            break;
                        case "PIE":
                            x2Assoc_PieChart_View.setRelRad(relRad);
                            x2Assoc_PieChart_View.doTheGraph();
                            break;
                        case "EPI":
                            bivCat_PieChart_View.setRelRad(relRad);
                            bivCat_PieChart_View.doTheGraph();
                            break;
                        default:
                            String switchFailure = "Switch failure: Change_Radius_Dialog 270 " + strDotOrPie;
                            MyAlerts.showUnexpectedErrorAlert(switchFailure);
                    }
                }
           });
    }
    
    private void makeButtonPanel() { 
        hBoxButtonPanel = new HBox(10);
        hBoxButtonPanel.setAlignment(Pos.CENTER);
        hBoxButtonPanel.setPadding(new Insets(5, 5, 5, 5));
        btnOK = new Button("OK");
        btnCancel = new Button("Cancel");
        
        btnOK.setOnAction((ActionEvent event) -> { 
            strReturnStatus = "OK";
            close();
        });
        
        setOnCloseRequest((WindowEvent t) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        hBoxButtonPanel.getChildren().addAll(btnOK, btnCancel);
    }     
          
    public void setRelativeRadius(double toThis) {relRad = toThis; }
    public String getReturnStatus() { return strReturnStatus; }
    public double getRelativeRadius() { return relRad; }
}


