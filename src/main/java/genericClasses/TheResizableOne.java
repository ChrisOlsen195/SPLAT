/******************************************************************
 *                    TheResizableOne                             *
 *                       10/15/23                                 *
 *                        18:00                                   *
 *****************************************************************/
package genericClasses;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.WritableImage;

public class TheResizableOne {
    static double initx;
    static double inity;
    static int height;
    static int width;
    // This variable controls the size of the initial Image window
    int fiveHundred;
    public static String path;
    static Scene /*initialScene,*/ View;
    Stage s;    
    static Node hint;
    static WritableImage source;
    static double offSetX, offSetY, zoomlvl;

    public TheResizableOne(int fiveHundred, Node daNode) {
        this.fiveHundred = fiveHundred;
        s = new Stage();
        // Can I bind the stage to something???
        s.setResizable(true);
        GridPane grid = new GridPane();
        grid.setHgap(20);grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        
        hint = daNode;

        grid.add(hint, 0, 0);

        //initialScene = new Scene(grid,600,100);
        //s.setScene(initialScene);
        
        initView();
      
        s.setScene(View);
        s.show();
    }

    void initView(){
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Title label title");
        
        source = hint.snapshot(null, null);
        ImageView image = new ImageView(source);
        double ratio = source.getWidth()/source.getHeight();

        if(fiveHundred/ratio < fiveHundred) {
            width=fiveHundred;
            height=(int) (fiveHundred/ratio);
        }else if(fiveHundred*ratio < fiveHundred){
            height=fiveHundred;
            width=(int) (fiveHundred*ratio);
        }else {
            height=fiveHundred;
            width=fiveHundred;
        }
        
        image.setPreserveRatio(false);
        image.setFitWidth(width);
        image.setFitHeight(height);
        height = (int) source.getHeight();
        width = (int) source.getWidth();
        HBox zoom = new HBox(10);
        zoom.setAlignment(Pos.CENTER);

        Slider zoomLvl = new Slider();
        zoomLvl.setMax(4);
        // zoomLvl.setMin(1);
        zoomLvl.setMin(0.1);
        zoomLvl.setMaxWidth(200);
        zoomLvl.setMinWidth(200);
        hint = new Label("Zoom Level");
        Label value = new Label("1.0");

        offSetX = width/2;
        offSetY = height/2;

        zoom.getChildren().addAll(hint,zoomLvl,value);

        Slider Hscroll = new Slider();
        Hscroll.setMin(0);
        Hscroll.setMax(width);
        Hscroll.setMaxWidth(image.getFitWidth());
        Hscroll.setMinWidth(image.getFitWidth());
        Hscroll.setTranslateY(-20);
        Slider Vscroll = new Slider();
        Vscroll.setMin(0);
        Vscroll.setMax(height);
        Vscroll.setMaxHeight(image.getFitHeight());
        Vscroll.setMinHeight(image.getFitHeight());
        Vscroll.setOrientation(Orientation.VERTICAL);
        Vscroll.setTranslateX(-20);

        BorderPane imageView = new BorderPane();
        BorderPane.setAlignment(Hscroll, Pos.CENTER);
        BorderPane.setAlignment(Vscroll, Pos.CENTER_LEFT);
        
        Hscroll.valueProperty().addListener(e->{
            offSetX = Hscroll.getValue();
            zoomlvl = zoomLvl.getValue();
            double newValue = (double)((int)(zoomlvl*10))/10;
            value.setText(newValue+"");
            
            if(offSetX<(width/newValue)/2) { offSetX = (width/newValue)/2; }
            
            if(offSetX>width-((width/newValue)/2)) {
                offSetX = width-((width/newValue)/2);
            }

            image.setViewport(new Rectangle2D(offSetX-((width/newValue)/2), offSetY-((height/newValue)/2), width/newValue, height/newValue));
        });
        
        Vscroll.valueProperty().addListener(e->{
            offSetY = height-Vscroll.getValue();
            zoomlvl = zoomLvl.getValue();
            double newValue = (double)((int)(zoomlvl*10))/10;
            value.setText(newValue+"");
            
            if(offSetY<(height/newValue)/2) { offSetY = (height/newValue)/2; }
            
            if(offSetY>height-((height/newValue)/2)) {
                offSetY = height-((height/newValue)/2);
            }
            
            image.setViewport(new Rectangle2D(offSetX-((width/newValue)/2), offSetY-((height/newValue)/2), width/newValue, height/newValue));
        });
        
        imageView.setCenter(image);
        imageView.setTop(Hscroll);
        imageView.setRight(Vscroll);
        
        zoomLvl.valueProperty().addListener(e->{
            zoomlvl = zoomLvl.getValue();
            double newValue = (double)((int)(zoomlvl*10))/10;
            value.setText(newValue+"");
            
            if(offSetX<(width/newValue)/2) { offSetX = (width/newValue)/2; }
            
            if(offSetX>width-((width/newValue)/2)) {
                offSetX = width-((width/newValue)/2);
            }
            
            if(offSetY<(height/newValue)/2) {
                offSetY = (height/newValue)/2;
            }
            
            if(offSetY>height-((height/newValue)/2)) {
                offSetY = height-((height/newValue)/2);
            }
            
            Hscroll.setValue(offSetX);
            Vscroll.setValue(height-offSetY);
            image.setViewport(new Rectangle2D(offSetX-((width/newValue)/2), offSetY-((height/newValue)/2), width/newValue, height/newValue));
        });
        
        imageView.setCursor(Cursor.OPEN_HAND);
        
        image.setOnMousePressed(e->{
            initx = e.getSceneX();
            inity = e.getSceneY();
            imageView.setCursor(Cursor.CLOSED_HAND);
        });
        
        image.setOnMouseReleased(e->{
            imageView.setCursor(Cursor.OPEN_HAND);
        });
        
        image.setOnMouseDragged(e->{
            Hscroll.setValue(Hscroll.getValue()+(initx - e.getSceneX()));
            Vscroll.setValue(Vscroll.getValue()-(inity - e.getSceneY()));
            initx = e.getSceneX();
            inity = e.getSceneY();
        });
        
        root.getChildren().addAll(title,imageView,zoom);

        View = new Scene(root,(image.getFitWidth())+70,(image.getFitHeight())+150);
    }
}    

