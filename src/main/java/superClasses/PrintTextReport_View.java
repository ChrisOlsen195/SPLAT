/****************************************************************************
 *                   PrintTextReport_View                                   * 
 *                         01/16/25                                         *
 *                          18:00                                           *
 ***************************************************************************/
package superClasses;

import genericClasses.DragableAnchorPane;
import genericClasses.ZoomieThing;
import java.util.ArrayList;
import javafx.event.EventHandler;
//import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import utilityClasses.StringUtilities;

public class PrintTextReport_View {
    // POJOs
     //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public double initHoriz, initVert, initWidth, initHeight; 
    
    public String strRTPTitle;
    public String sourceString, strTextPaneTitle, strTitleText, graphsCSS ;
    public ArrayList<String> stringsToPrint; 
    String[] arrayOfStrings;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    ZoomieThing zoomieThing;
    Text tempText;
    TextFlow textFlow;
    
    //Node daNode;
    
    // FX Objects
    public AnchorPane thePSAnchorPane;
    public Pane containingPane;   
    public TextArea txtArea4Strings; 
    public Text txtTitle, thisText;
    private String fnt_TimesNewRoman, fnt_CourierNew;
    
    public PrintTextReport_View(double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        if (printTheStuff == true) {
            System.out.println("55 *** PrintTextReport_View (Super), Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        stringsToPrint = new ArrayList<>();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
        fnt_TimesNewRoman = "Times New Roman";
        fnt_CourierNew = "Courier New";
    }
    
    public void setUpUI() {
        strRTPTitle = "Place holder";
        txtTitle = new Text(250, 20, strTitleText);       
        txtTitle.setFont(Font.font(fnt_TimesNewRoman, FontWeight.BOLD, FontPosture.REGULAR,20)); 
        
        txtArea4Strings = new TextArea();  // Where text will be drawn
        txtArea4Strings.setOnMouseClicked(txtArea4StringsMouseHandler);
        txtArea4Strings.setWrapText(false);
        txtArea4Strings.setEditable(false);
        txtArea4Strings.setPrefColumnCount(80);
        txtArea4Strings.setFont(Font.font(fnt_CourierNew, FontWeight.BOLD, FontPosture.REGULAR,12)); 
        arrayOfStrings = new String[stringsToPrint.size()];
        
        for (int printLines = 0; printLines < stringsToPrint.size(); printLines++) {
            String tempString = stringsToPrint.get(printLines);
            arrayOfStrings[printLines] = tempString;
            String strThisLine = tempString;
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            thisText.setFont(Font.font(fnt_CourierNew, FontWeight.BOLD, FontPosture.REGULAR, 15));
            txtArea4Strings.appendText(strThisLine);
        }

        containingPane = new Pane();
    }
    
    public void completeTheDeal() {
        constructPrintLines();
        setUpUI();                 
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane();   
        
        textFlow = new TextFlow();
        textFlow.setPrefSize(750, 750);
        tempText = new Text(strTitleText);
        tempText.setFont(Font.font(fnt_CourierNew, FontWeight.BOLD, 12));
        textFlow.getChildren().add(tempText);
        
        for (int printLines = 0; printLines < stringsToPrint.size(); printLines++) {
            tempText = new Text(arrayOfStrings[printLines]);
            textFlow.getChildren().add(tempText);
        }
    }    
        
    public void setUpAnchorPane() {       
        dragableAnchorPane = new DragableAnchorPane();
        // Construct the draggable
        thePSAnchorPane = dragableAnchorPane.getTheAP();
        // Finish the job -- make it dragable
        dragableAnchorPane.makeDragable();
        thePSAnchorPane.getChildren().addAll(txtTitle, txtArea4Strings);  
        
        double paneWidth = initWidth;
        double titleWidth = txtTitle.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        
        AnchorPane.setTopAnchor(txtTitle, 0.0 * initHeight);        
        AnchorPane.setLeftAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.075 * initHeight);
        
        AnchorPane.setTopAnchor(txtArea4Strings, 0.075 * initHeight);
        AnchorPane.setLeftAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setRightAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(txtArea4Strings,0.0 * initHeight);

        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void constructPrintLines() {}    //  Reports construct their lines
    
    public void addNBlankLines(int thisMany) {
        StringUtilities.addNLinesToArrayList(stringsToPrint, thisMany);
    }
    
    public void triggerTheZoomieThing() { 
        zoomieThing = new ZoomieThing(textFlow); 
    }
    
    public EventHandler<MouseEvent> txtArea4StringsMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent)  {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {         
                zoomieThing = new ZoomieThing(textFlow);
            }
        }
    }; 
    
    public Pane getTheContainingPane() { return containingPane; }    
}
