/**************************************************
 *             ANOVA2_UnbalancedView              *
 *                   05/24/24                     *
 *                     12:00                      *
 *************************************************/
package anova2;

import genericClasses.JustAnAxis;
import matrixProcedures.Matrix;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ANOVA2_UnbalancedView extends ANOVA2_Views_Super {
    // POJOs

    int lineToDraw; 
    int NONE = 0;
    int BESTFIT = 1;
    int Y_EQUALS_0 = 2;
    
    double slope, intercept, xMin, xMax, xRange;    
    double dataArray[][];
    
    // My classes
    JustAnAxis xAxis;
    Matrix X, Y;

    // FX Classes
    Canvas regressionCanvas;
    GraphicsContext gc;
    GridPane gridPane;
    Label xScalePad, titlePad, title;
    Scene scene;    
    Stage stage;

    ANOVA2_UnbalancedView(ANOVA2_UnbalancedRegression anova2_Unbalanced_Regression, 
                   String[] Labels, 
                   Matrix X, Matrix Y, 
                   int lineToDraw) {
        this.lineToDraw = lineToDraw; 
        
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        
        title = new Label("Title goes here"); 
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));        
        title.getStyleClass().add("titleLabel");              
        GridPane.setHalignment(title, HPos.CENTER); 
        
        xScalePad = new Label(" ");
        titlePad  = new Label(" ");

        this.X = X; this.Y = Y;
        stage = new Stage();
        
        if (lineToDraw == BESTFIT) {
            slope = anova2_Unbalanced_Regression.getSimpleRegSlope();
            intercept = anova2_Unbalanced_Regression.getSimpleRegIntercept();
        }
        else if (lineToDraw == Y_EQUALS_0) { slope = intercept = 0.0; }  
        
        // Position the Stage/Window on the screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getWidth() - stage.getWidth()/4);
        stage.setY(screenBounds.getHeight() - stage.getHeight()/4);
        stage.setResizable(true);
        stage.setTitle("Stage Title");
        
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: white;");
        
        scene = new Scene(gridPane, 800, 800);
        scene.getStylesheets().add(graphsCSS);
        
        // Initial values apparently needed for Canvas at construction ?
        regressionCanvas = new Canvas(600, 600);
        gc = regressionCanvas.getGraphicsContext2D();
        
        scene.heightProperty().addListener(ov-> {doThePlot();});
        scene.widthProperty().addListener(ov-> {doThePlot();});

        constructDataArray();
        
        // These constants control the rate of change when dragging        
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;

        //  **************   X Axis  **********************
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);

        //  **************   Y Axis  **********************
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(100, gridPane.getHeight() - 50);        
     
        // Insets: Top, right, bottom, left
        gridPane.setPadding(new Insets(10, 10, 5, 10));

        titlePad.setMinHeight(60); // To give the X Label some room       
        xScalePad.setMinHeight(60); // To give the X scale some room 
        
        gridPane.add(xScalePad, 0, 2);
        gridPane.add(titlePad, 0, 0);
        gridPane.add(yAxis, 0, 1);
        gridPane.add(xAxis, 1, 2);   

        //  **************   Y Labels & Titles  **********************
        gridPane.add(title, 1, 0); 

        setHandlers();
       
        gridPane.prefHeightProperty().bind(scene.heightProperty());
        gridPane.prefWidthProperty().bind(scene.widthProperty());
        gridPane.add(regressionCanvas, 1, 1);     
        
        stage.setResizable(true);
        stage.setScene(scene);   
        stage.sizeToScene();
        
        regressionCanvas.heightProperty().bind(scene.heightProperty().subtract(150));
        regressionCanvas.widthProperty().bind(scene.widthProperty().subtract(150));
        
        stage.show();
        doThePlot();   
    }
    
    public void doThePlot() {    
        gc.clearRect(0, 0 , regressionCanvas.getWidth(), regressionCanvas.getHeight());

        for (int i = 0; i < nDataPoints; i++) {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            gc.fillOval(xx - 4, yy - 4, 8, 8); //  0.5*radius to get dot to center
        }
        
        if (lineToDraw != NONE) {
            line = new Line();
            double x1 = xAxis.getDisplayPosition(xMin);
            double y1 = yAxis.getDisplayPosition(slope * xMin + intercept);
            double x2 = xAxis.getDisplayPosition(xMax);
            double y2 = yAxis.getDisplayPosition(slope * xMax + intercept);
            gc.setLineWidth(4);
            gc.setStroke(Color.TOMATO);
            gc.strokeLine(x1, y1, x2, y2);           
        }
        
        theContainingPane.requestFocus();
        theContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = theContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));        
    }
    
    private void constructDataArray() {
        nDataPoints = X.getRowDimension();
        dataArray = new double[nDataPoints][2];
        
        xMin = xMax = X.get(0, 0);
        yMin = yMax = Y.get(0, 0);
        
        for (int iRow = 0; iRow < nDataPoints; iRow++) {
            double tempDoubleX = X.get(iRow, 0);
            double tempDoubleY = Y.get(iRow, 0);
            
            dataArray[iRow][0] = tempDoubleX;
            dataArray[iRow][1] = tempDoubleY;
   
            if (tempDoubleX < xMin) xMin = tempDoubleX;
            if (tempDoubleY < yMin) yMin = tempDoubleY;
            if (tempDoubleX > xMax) xMax = tempDoubleX;
            if (tempDoubleY > yMax) yMax = tempDoubleY;
        } 
        
        xRange = xMax - xMin;
        yRange = yMax - yMin;
        
        //  Make room for the circles
        xMin = xMin - .02 * xRange; xMax = xMax + .02 * xRange;
        yMin = yMin - .02 * yRange; yMax = yMax + .02 * yRange;                 
    }
    
    private void setHandlers() {
        xAxis.setOnMouseDragged(xAxisMouseHandler); 
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler);  
        yAxis.setOnMouseDragged(yAxisMouseHandler);  
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
    
    EventHandler<MouseEvent> xAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent)  {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)  { 
                xPix_InitialPress = mouseEvent.getX();  
                xPix_MostRecentDragPoint = mouseEvent.getX();
                dragging = false;   
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED)  {                
                if (dragging) {
                    xAxis.setLowerBound(newX_Lower ); 
                    xAxis.setUpperBound(newX_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double xPix_Dragging = mouseEvent.getX();
                newX_Lower = xAxis.getLowerBound();
                newX_Upper = xAxis.getUpperBound(); 

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());

                double frac = mouseEvent.getX() / dispUpperBound;
                
                // Still dragging right
                if((xPix_Dragging > xPix_InitialPress) && (xPix_Dragging > xPix_MostRecentDragPoint)) {    
                    // Which half of scale?
                    if (frac > 0.5) {//  Right of center -- OK
                        newX_Upper = xAxis.getUpperBound() - deltaX;
                    }
                    else  // Left of Center
                    {
                        newX_Lower = xAxis.getLowerBound() - deltaX;
                    }
                }
                else 
                    if ((xPix_Dragging < xPix_InitialPress) && (xPix_Dragging < xPix_MostRecentDragPoint)) {   
                        if (frac < 0.5) { // Left of center
                            newX_Lower = xAxis.getLowerBound() + deltaX;
                        }
                        else {   // Right of center -- OK
                            newX_Upper = xAxis.getUpperBound() + deltaX;
                        }
                    }    

                xAxis.setLowerBound(newX_Lower ); 
                xAxis.setUpperBound(newX_Upper );

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());
                xPix_MostRecentDragPoint = mouseEvent.getX();
                
                doThePlot();
            }
        }
    };  
}

