/****************************************************************************
 *                         ProbText_View                                    *
 *                            01/07/23                                      *
 *                             12:00                                        *
 ***************************************************************************/
package visual_probability;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import genericClasses.Point_2D;

public class ProbText_View {
    
    // POJOs
    
    //int theCombo;
    
    double px_x_UL_Universe, px_y_UL_Universe, px_x_LR_Universe, px_y_LR_Universe;
    
    public double probA, probB, probAandB, probAorB;
    public double probNotA, probNotB, probAandNotB, probBandNotA, probNotAandB;
    public double probAGivenB, probAGivenNotB, probBGivenA, probBGivenNotA, probNotAandNotB,
                  probNotAGivenB, probNotAGivenNotB, probNotBGivenA, probNotBGivenNotA,
                  probAorNotB, probNotAorB, probNotAorNotB;

    // My classes
    MyRectangle rect_Universe;
    
    Point_2D boxDim, pxBoxDim; 
    ProbText_FullMonte probText_FullMonte;   
    //  FX Classes
    //Color clr_Universe, clr_Text, clr_Yes, clr_White; 
    Font probFont;
    Pane pane;
    //Scene scene;
    //Stage stage;
    Text txtProbA, txtProbNotA, txtProbB, txtProbAandB, txtProbAorB,
         txtProbAandNotB, txtProbAorNotB, txtProbNotAandB, txtProbNotAorB,
         txtProbNotB, txtProbAGivenB, txtProbNotAGivenB, txtProbNotAGivenNotB,
         txtProbBGivenA, txtProbNotAandNotB, txtProbNotAorNotB,
         txtProbNotBGivenA,
         txtProbBGivenNotA, txtProbNotBGivenNotA;        

    public ProbText_View(ProbText_FullMonte probText_FullMonte) {
        this.probText_FullMonte = probText_FullMonte;
        pane = new Pane();

        probA = probText_FullMonte.getProbA();
        probNotA = probText_FullMonte.getProbNotA();
        probB = probText_FullMonte.getProbB();
        probNotB = probText_FullMonte.getProbNotB();
        probAandB = probText_FullMonte.getProbAandB();
        probAorB = probText_FullMonte.getProbAorB();
        probAandNotB = probText_FullMonte.getProbAandNotB();
        probAorNotB = probText_FullMonte.getProbAorNotB();
        probNotAandB = probText_FullMonte.getProbNotAandB();
        probNotAorB = probText_FullMonte.getProbNotAorB();
        probAGivenB = probText_FullMonte.getProbAGivenB();
        probBGivenA = probText_FullMonte.getProbBGivenA();
        probNotAGivenB = probText_FullMonte.getProbNotAGivenB();
        probNotBGivenA = probText_FullMonte.getProbNotBGivenA();
        probNotAGivenNotB = probText_FullMonte.getProbNotAGivenNotB();
        probNotBGivenNotA = probText_FullMonte.getProbNotBGivenNotA();
        probNotAandNotB = probText_FullMonte.getProbNotAandNotB();
        probNotAorNotB = probText_FullMonte.getProbNotAorNotB();
        
        probFont = Font.font("Arial", FontWeight.BOLD, 20);
        initializeStuff();
        constructTheUniverse();
        drawTheProbabilities();
    }
    

    private void initializeStuff() {
        // px => in pixels
        px_x_UL_Universe = 25.0;
        px_y_UL_Universe = 25.0;
        px_x_LR_Universe = 635.;
        px_y_LR_Universe = 550.;
  
        rect_Universe = new MyRectangle(px_x_UL_Universe, px_y_UL_Universe, 
                                       px_x_LR_Universe, px_y_LR_Universe); 
        
    }
    
    private void constructTheUniverse() {
        rect_Universe.setStrokeWidth(2);
        rect_Universe.setStroke(Color.BLUE);
        pane.getChildren().addAll(rect_Universe.getTheRectBorder());    
    }  

    
    private void drawTheProbabilities() {
        // Now draw the successes
        txtProbA = new Text("");
        txtProbA.setText("*****  The Kitchen Sink!!!  *****");
        txtProbA.setFont(probFont);
        txtProbA.getTransforms().add(new Translate(125., 60.));
        pane.getChildren().add(txtProbA);
        
        txtProbA = new Text("");
        txtProbA.setText(String.format("Probability of A = %5.3f", probA));
        txtProbA.setFont(probFont);
        txtProbA.getTransforms().add(new Translate(100., 100.));
        pane.getChildren().add(txtProbA);
        
        txtProbNotA = new Text("");
        txtProbNotA.setText(String.format("Probability of not-A = %5.3f", probNotA));
        txtProbNotA.setFont(probFont);
        txtProbNotA.getTransforms().add(new Translate(100., 125.));
        pane.getChildren().add(txtProbNotA);

        txtProbB = new Text("");
        txtProbB.setText(String.format("Probability of B = %5.3f", probB));
        txtProbB.setFont(probFont);
        txtProbB.getTransforms().add(new Translate(100., 150.));
        pane.getChildren().add(txtProbB);
        
        txtProbNotB = new Text("");
        txtProbNotB.setText(String.format("Probability of not-B = %5.3f", probNotB));
        txtProbNotB.setFont(probFont);
        txtProbNotB.getTransforms().add(new Translate(100., 175.));
        pane.getChildren().add(txtProbNotB);
        
        txtProbAandB = new Text("");
        txtProbAandB.setText(String.format("Probability of A and B = %5.3f", probAandB));
        txtProbAandB.setFont(probFont);
        txtProbAandB.getTransforms().add(new Translate(100., 200.));
        pane.getChildren().add(txtProbAandB);
        
        txtProbAorB = new Text("");
        txtProbAorB.setText(String.format("Probability of A or B = %5.3f", probAorB));
        txtProbAorB.setFont(probFont);
        txtProbAorB.getTransforms().add(new Translate(100., 225.));
        pane.getChildren().add(txtProbAorB);
        
        txtProbAandNotB = new Text("");
        txtProbAandNotB.setText(String.format("Probability of A and not-B = %5.3f", probAandNotB));
        txtProbAandNotB.setFont(probFont);
        txtProbAandNotB.getTransforms().add(new Translate(100., 250.));
        pane.getChildren().add(txtProbAandNotB);
        
        txtProbAorNotB = new Text("");
        txtProbAorNotB.setText(String.format("Probability of A or not-B = %5.3f", probAorNotB));
        txtProbAorNotB.setFont(probFont);
        txtProbAorNotB.getTransforms().add(new Translate(100., 275.));
        pane.getChildren().add(txtProbAorNotB);
        
        txtProbNotAandB = new Text("");
        txtProbNotAandB.setText(String.format("Probability of not-A and B = %5.3f", probNotAandB));
        txtProbNotAandB.setFont(probFont);
        txtProbNotAandB.getTransforms().add(new Translate(100., 300.));
        pane.getChildren().add(txtProbNotAandB);
        
        txtProbNotAorB = new Text("");
        txtProbNotAorB.setText(String.format("Probability of not-A or B = %5.3f", probNotAorB));
        txtProbNotAorB.setFont(probFont);
        txtProbNotAorB.getTransforms().add(new Translate(100., 325.));
        pane.getChildren().add(txtProbNotAorB);
        
        txtProbNotAandNotB = new Text("");
        txtProbNotAandNotB.setText(String.format("Probability of not-A and Not-B = %5.3f", probNotAandNotB));
        txtProbNotAandNotB.setFont(probFont);
        txtProbNotAandNotB.getTransforms().add(new Translate(100., 350.));
        pane.getChildren().add(txtProbNotAandNotB);
        
        txtProbNotAorNotB = new Text("");
        txtProbNotAorNotB.setText(String.format("Probability of not-A or Not-B = %5.3f", probNotAorNotB));
        txtProbNotAorNotB.setFont(probFont);
        txtProbNotAorNotB.getTransforms().add(new Translate(100., 375.));
        pane.getChildren().add(txtProbNotAorNotB);        
        
        txtProbAGivenB = new Text("");
        txtProbAGivenB.setText(String.format("Probability of A given B = %5.3f", probAGivenB));
        txtProbAGivenB.setFont(probFont);
        txtProbAGivenB.getTransforms().add(new Translate(100., 400.));
        pane.getChildren().add(txtProbAGivenB);
        
        txtProbBGivenA = new Text("");
        txtProbBGivenA.setText(String.format("Probability of B given A = %5.3f", probBGivenA));
        txtProbBGivenA.setFont(probFont);
        txtProbBGivenA.getTransforms().add(new Translate(100., 425.));
        pane.getChildren().add(txtProbBGivenA);
        
        txtProbNotAGivenB = new Text("");
        txtProbNotAGivenB.setText(String.format("Probability of not-A given B = %5.3f", probNotAGivenB));
        txtProbNotAGivenB.setFont(probFont);
        txtProbNotAGivenB.getTransforms().add(new Translate(100., 450.));
        pane.getChildren().add(txtProbNotAGivenB);
        
        txtProbNotBGivenA = new Text("");
        txtProbNotBGivenA.setText(String.format("Probability of not-B given A = %5.3f", probNotBGivenA));
        txtProbNotBGivenA.setFont(probFont);
        txtProbNotBGivenA.getTransforms().add(new Translate(100., 475.));
        pane.getChildren().add(txtProbNotBGivenA);
        
        txtProbNotAGivenNotB = new Text("");
        txtProbNotAGivenNotB.setText(String.format("Probability of not-A given not-B = %5.3f", probNotAGivenNotB));
        txtProbNotAGivenNotB.setFont(probFont);
        txtProbNotAGivenNotB.getTransforms().add(new Translate(100., 500.));
        pane.getChildren().add(txtProbNotAGivenNotB);
        
        txtProbNotBGivenNotA = new Text("");
        txtProbNotBGivenNotA.setText(String.format("Probability of not-B given not-A = %5.3f", probNotBGivenNotA));
        txtProbNotBGivenNotA.setFont(probFont);
        txtProbNotBGivenNotA.getTransforms().add(new Translate(100., 525.));
        pane.getChildren().add(txtProbNotBGivenNotA);
    }
    
    public Pane getPane() { return pane; }
}
