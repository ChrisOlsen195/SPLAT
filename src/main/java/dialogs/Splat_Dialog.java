/************************************************************
 *                        Splat_Dialog                      *
 *                          08/20/24                        *
 *                            12:00                         *
 ***********************************************************/

package dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import splat.Data_Manager;

public class Splat_Dialog extends Stage {
    public boolean boolGoodToGo;
    public Button btnOK, btnCancel;
    public String strCSS, strReturnStatus;
    
    // Make empty if no-print
    //String waldoFile = "Splat_Dialog";
    public String waldoFile = "";
    
    public Data_Manager dm;
    
    public Splat_Dialog() { initialize(); }
    
    public Splat_Dialog(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(31, waldoFile, "Constructing");       
        initialize();
    }
    
    public Splat_Dialog(String messageOfSomeSort) { initialize(); }
    
    private void initialize() {
        boolGoodToGo = true;
        strReturnStatus = "OK";    //  Initialize to OK
        this.setTitle("SPLAT: StatisticsPackageForLearningAndTeaching"); 
        this.getIcons().add(new Image(getClass().getResource("/SplatJPG.jpg").toExternalForm())); 
        strCSS = getClass().getClassLoader().getResource("StatDialogs.css").toExternalForm();
        
        btnOK = new Button("");
        btnOK.setOnAction((ActionEvent event) -> {
            boolGoodToGo = true;
            strReturnStatus = "OK";
            close();
        });
        
        btnCancel = new Button("");
        btnCancel.setOnAction((ActionEvent event) -> {
            boolGoodToGo = false;
            strReturnStatus = "Cancel";
            close();
        });
        
        setOnCloseRequest((WindowEvent we) -> {
            boolGoodToGo = false;
            strReturnStatus = "Cancel";
            close();
        });
        
    }
    
    public boolean getGoodToGO() { return boolGoodToGo; }
    public String getReturnStatus() { return strReturnStatus; }  
}
