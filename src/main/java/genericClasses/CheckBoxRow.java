/**************************************************
 *                 CheckBoxRow                    *
 *                   10/15/23                     *
 *                     18:00                      *
 *************************************************/
package genericClasses;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CheckBoxRow  {
    // POJOs
    protected Boolean checkBoxSettings[];
    
    // Max # of sixteenths = 8 at present;
    
    protected int nCheckBoxes, nSpacers;
    protected double backGroundHeight, dashWidth, dashHeight,
                     upperLeftX, upperLeftY, lowerRightX, lowerRightY;
    protected final double CHECKBOXHEIGHT = 100.0;

    protected String returnStatus;
    
    protected final String cbStyle = "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" ;

    public String[] checkBoxDescr;
    
    // POJOs / FX
    protected CheckBox[] checkBoxes;
    protected HBox theHBox;
    protected VBox root;
    protected Pane backGround;
   // final protected Rectangle2D visualBounds; 
    protected Region[] spacer;

    public CheckBoxRow() {
        nCheckBoxes = 0;
        theHBox = new HBox();
        theHBox.setMinHeight(50);
        theHBox.setAlignment(Pos.CENTER);  
        theHBox.getChildren().add(new Region());
    }
    
    public CheckBoxRow(String[] cbDescriptions) { 
        nCheckBoxes = cbDescriptions.length;
        nSpacers = nCheckBoxes + 1;
        checkBoxDescr = new String[nCheckBoxes];
        System.arraycopy(cbDescriptions, 0, checkBoxDescr, 0, cbDescriptions.length);
        checkBoxSettings = new Boolean[nCheckBoxes];    
        checkBoxes = new CheckBox[nCheckBoxes];  
        spacer = new Region[nSpacers];
        
        for (int i = 0; i < nCheckBoxes; i++) {
            checkBoxSettings[i] = false;
        }
 
        // Left spacer min width apparently must be set to have space on the left end
        spacer[0] = new Region();
        spacer[0].setMinWidth(50);
        HBox.setHgrow(spacer[0], Priority.ALWAYS);
        
        for (int iSpacer = 1; iSpacer < nSpacers; iSpacer++) {
            spacer[iSpacer] = new Region();
            HBox.setHgrow(spacer[iSpacer], Priority.ALWAYS);
        }
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxes[ithCheckBox] = new CheckBox();
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setTextFill(Color.RED);
            checkBoxes[ithCheckBox].setStyle(cbStyle);
            HBox.setHgrow(checkBoxes[ithCheckBox], Priority.ALWAYS);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);

            //  Set Checkbox Action
            checkBoxes[ithCheckBox].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                System.out.println("101 CBRow, checkValue = " + checkValue);
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);

                for (int daCase = 0; daCase < nCheckBoxes; daCase++) {
                    if (daID.equals(checkBoxDescr[daCase])) {
                        checkBoxSettings[daCase] = (checkValue == true); 
                    }
                }
            });   
        }
        
        theHBox = new HBox();
        theHBox.setMinHeight(50);
        theHBox.setAlignment(Pos.CENTER);

        for (int ithCB = 0; ithCB < nCheckBoxes; ithCB++) {
            theHBox.getChildren().add(spacer[ithCB]);
            theHBox.getChildren().add(checkBoxes[ithCB]);
        }
        theHBox.getChildren().add(spacer[nCheckBoxes]);
    }
    
    public HBox getTheCBRow() { return theHBox; }
    
    public CheckBox getIthCheckBox(int ith) { return checkBoxes[ith]; }
        
    public String toString() {
        String toReturn = "toString from CheckBoxRow...";
        System.out.println("*************************nCheckBoxes = " + nCheckBoxes);
        
        for (int ithBox = 0; ithBox < nCheckBoxes; ithBox++) {
            System.out.println("137 CB_Row, is selected = " + checkBoxes[ithBox].isSelected());
            System.out.println("138 CB_Row, text = " + checkBoxes[ithBox].getText());
        }
        return toReturn;
    }         
}



