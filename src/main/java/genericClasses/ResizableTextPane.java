/******************************************************************
 *                  ResizableTextPane                             *
 *                       10/15/23                                 *
 *                        21:00                                   *
 *****************************************************************/
/*******************************************************************
 * Seems to work as intended, but not sure I like what it does.    *
 * I'm thinking it should always print the last x lines, not just  *
 * one line when the vertical slider is at max.                    *
 ******************************************************************/
package genericClasses;

import java.util.ArrayList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ResizableTextPane {
    int /* height, width,*/ startLine, startChar, /* endChar,*/ maxLineLen, nLines;
    int maxLinesToPrint; //, firstOfLast;
    
    double zoomSliderLevel, horizSliderLevel, vertSliderLevel,
            lastWidthStanding, lastHeightStanding;
    Stage stage;  
    Scene /*scene,*/ tfScene;   
    Region spacer1, spacer2;
    Label titleLabel, zoomLabel;
    TextFlow tf;
    VBox tfVBoxRoot;
    HBox zoomieBox, zoomStuff;
    Slider horizSlider, vertSlider, zoomSlider;
    StringBuilder sb;
    String title;
    String[] txtString, initTxtString;
    Text[] txt;
    ArrayList<String> theStrings;
    
    TextFlow preZoomie;
    
    //ZoomieThing zoomieThing;
        
    public ResizableTextPane(String title, ArrayList<String> theStrings) {
        this.title = title;
        this.theStrings = theStrings;
        nLines = theStrings.size();
        startLine = 0;
        startChar = 0;
        maxLinesToPrint = 20;
        lastWidthStanding = 200.0;
        lastHeightStanding = 400.0;
        txtString = new String[nLines];
        initTxtString = new String[nLines];
        txt = new Text[nLines];
        tf = new TextFlow();    
        spacer1 = new Region();
        spacer1.setMinWidth(5.0);
        spacer1.setMaxWidth(5.0);
        spacer2 = new Region(); 
        zoomieBox = new HBox(10);
        zoomieBox.setAlignment(Pos.CENTER_LEFT);

        for (int ithLine = 0; ithLine < nLines; ithLine++) {
            initTxtString[ithLine] = theStrings.get(ithLine);
        }

        int maxStringSize = 0;
        for (int ithLine = 0; ithLine < nLines; ithLine++) {
            int lenThisString = initTxtString[ithLine].length();
            if (lenThisString > maxStringSize) {
                maxStringSize = lenThisString;
            }
        }
        
        maxLineLen = maxStringSize;
        for (int ithLine = 0; ithLine < nLines; ithLine++) {
            StringBuilder sb = new StringBuilder("\n");
            int lenThisString = initTxtString[ithLine].length();
            if (lenThisString < maxStringSize) {
                int diffLength = maxStringSize - lenThisString;
                sb.append(initTxtString[ithLine]);
                for (int addBlank = 0; addBlank < diffLength; addBlank++) {
                    sb.append(" ");
                }
            }
            else {
                sb.append(initTxtString[ithLine]);
            }
            initTxtString[ithLine] = sb.toString();
        }

        for (int daLine = 0; daLine < nLines; daLine++) {
            txt[daLine] = new Text(theStrings.get(daLine) + "\n");
            txt[daLine].setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        }
        
        createInitialGUI();
        makeATextFlow();
        tf.getChildren().addAll(txt);
        
        preZoomie = new TextFlow(txt);
        
        makeAZoomie();
                
        preZoomie = new TextFlow(txt);         
        
        //zoomieThing = new ZoomieThing(preZoomie);
        // zoomieThing = new ZoomieThing(tf);
        // stage.showAndWait(); 
        // stage.toFront();
    }
    
    private void makeAZoomie() {
        txt = new Text[nLines + 1];
        txt[0] = new Text(title + "\n") ;
        for (int ithLine = 1; ithLine <= nLines; ithLine++) {
        txt[ithLine] = new Text(theStrings.get(ithLine - 1));
        }
    }
    

    
    private void createInitialGUI() {
        titleLabel = new Label("This is a title");
        zoomLabel = new Label("Zoom level");
        zoomSliderLevel = 1.0;
        horizSlider = new Slider();
        horizSlider.setMin(0.0);
        horizSlider.setMax(80.0);
        horizSlider.setValue(0.0);
        horizSlider.setMajorTickUnit(10.0);
        horizSlider.setShowTickLabels(true);
        horizSlider.setMinHeight(10.0);
        horizSlider.setMaxHeight(10.0);
        
        horizSlider.valueProperty().addListener(e->{
            horizSliderLevel = horizSlider.getValue();
            startChar = (int)horizSliderLevel;
            makeANewTextFlow();
        });
         
        vertSlider = new Slider();
        vertSlider.setMin(0);
        vertSlider.setMax(nLines - 1);
        vertSlider.setValue(nLines - 1);
        vertSlider.setOrientation(Orientation.VERTICAL);
        vertSlider.setMajorTickUnit(10.0);
        vertSlider.setShowTickLabels(true);
        vertSlider.setMinWidth(7.5);
        vertSlider.setMaxWidth(7.5);
        
        vertSlider.valueProperty().addListener(e->{
            vertSliderLevel = vertSlider.getValue();
            startLine = (int)(nLines - vertSliderLevel) - 1;
            if (startLine < nLines - maxLinesToPrint + 1) {
                makeANewTextFlow();
            }
        });
        
        zoomSlider = new Slider();
        zoomSlider.setMin(1.0);
        zoomSlider.setMax(3.0);
        zoomSlider.setValue(1.0);
        zoomSlider.setMajorTickUnit(0.5);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setPrefWidth(600);
        
        zoomSlider.valueProperty().addListener(e->{
            zoomSliderLevel = zoomSlider.getValue();
            for (int daLine = 0; daLine < nLines; daLine++) {
                txt[daLine].setFont(Font.font("Courier New", FontWeight.BOLD, 15 * zoomSliderLevel));
            }
        }); 
        
        stage = new Stage();

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
             lastWidthStanding = (double)newVal; 
             tf.setMinWidth(0.9 * (double)newVal);
             tf.setMaxWidth(0.9 * (double)newVal);
             horizSlider.setMinWidth(0.9 * (double)newVal);
             horizSlider.setMaxWidth(0.9 * (double)newVal);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            lastHeightStanding = (double)newVal;
            tf.setMinHeight(0.75 * (double)newVal);
            tf.setMaxHeight(0.75 * (double)newVal);
        });
        
        makeATextFlow();
        zoomieBox.getChildren().addAll(spacer1, tf, spacer2, vertSlider);        
        zoomStuff = new HBox(10);
        zoomStuff.getChildren().addAll(zoomLabel, zoomSlider);
        
        tfVBoxRoot = new VBox(10);
        tfVBoxRoot.getChildren().addAll(zoomStuff, titleLabel, horizSlider, zoomieBox);

        Pane root = new Pane();
        tfScene = new Scene(tfVBoxRoot, 800, 500);
        stage.setScene(tfScene);
    }
    
    private void makeANewTextFlow() {
        tf.getChildren().removeAll(txt);
        makeATextFlow();
        tf.getChildren().addAll(txt);
    }
    
    private void makeATextFlow() {
        int linesToDraw = Math.min(maxLinesToPrint, nLines);
        //firstOfLast = Math.max(0, nLines - maxLinesToPrint);
        
        for (int ithTxtString = 0; ithTxtString < nLines; ithTxtString++) {
            txtString[ithTxtString] = "";
            txt[ithTxtString] = new Text("");
        }
        
        for (int visLine = 0; visLine < linesToDraw; visLine++) {
            
            if (visLine + startLine >= nLines)  {  return;  }
            
            if (startChar == 0) {
                sb = new StringBuilder("");
            }
            else {
                sb = new StringBuilder("\n");
            }

            for (int ithChar = startChar; ithChar <= maxLineLen; ithChar++) {
                
                if (ithChar < initTxtString[visLine + startLine].length()) {
                    sb.append(initTxtString[visLine + startLine].charAt(ithChar));
                }
            }   // end ithChar
            
            txtString[visLine] = new String(sb);
            txt[visLine] = new Text(txtString[visLine]);
            txt[visLine].setFont(Font.font("Courier New", FontWeight.BOLD, 15 * zoomSliderLevel));
        }   // end visLine
        
        for (int notVis = linesToDraw; notVis < nLines; notVis++) {
            txt[notVis] = new Text("\n");
        }
        
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setMaxHeight(Double.MAX_VALUE);
        tf.setPrefSize(250, 10); 
    }  
}
