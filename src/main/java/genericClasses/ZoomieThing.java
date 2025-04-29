/**************************************************
 *                  ZoomieThing                   *
 *                    11/03/23                    *
 *                     12:00                      *
 *************************************************/
package genericClasses;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class ZoomieThing {
    //Node nodyWody;
    Stage stage;
    Scene scene;
    String type;
    double scaleFactor;

  public ZoomieThing(Node nodyWody) {
    //this.nodyWody = nodyWody;
    final Group group = new Group(nodyWody);
    stage = new Stage();
    stage.setOnCloseRequest(event -> stage.close());
    Parent zoomPane = createZoomPane(group);

    VBox layout = new VBox();
    layout.getChildren().setAll(createMenuBar(stage, group), zoomPane);

    VBox.setVgrow(zoomPane, Priority.ALWAYS);

    scene = new Scene(layout);

    stage.setTitle("Zoomie!!!");
    stage.setScene(scene);

    EventHandler<KeyEvent> pressFilter = (KeyEvent e) -> {
        type = e.getEventType().getName();
        String kCode1 = e.getCode().toString();

        if(type.equals("KEY_PRESSED")) {
            //System.out.println("\n\n 142 dg, *******************  Key pressed  ************************");
            //System.out.println("143dg, kCode1 = " + kCode1);
            //System.out.println("144 dg, control-down = " + e.isControlDown());
            //System.out.println("144 dg, shift-down = " + e.isShiftDown());
            if (kCode1.equals("EQUALS") 
                && e.isControlDown()) {
                //System.out.println("Hallelujah +!!!");
                group.setScaleX(group.getScaleX() * scaleFactor);
                group.setScaleY(group.getScaleY() * scaleFactor);
            }
            else 
            if (kCode1.equals("MINUS")) {
                //System.out.println("Hallelujah -!!!");
                group.setScaleX(group.getScaleX() / scaleFactor);
                group.setScaleY(group.getScaleY() / scaleFactor);
            }
        }   // End Key pressed
    };  //  End Press Fileter Event handler
        
    scene.addEventFilter(KeyEvent.KEY_PRESSED, pressFilter);
    scene.addEventFilter(KeyEvent.KEY_RELEASED, pressFilter);
    scene.addEventFilter(KeyEvent.KEY_TYPED, pressFilter);   
    
    stage.showAndWait();
  }

  private Parent createZoomPane(final Group group) {
    final double SCALE_DELTA = 1.1;
    scaleFactor = SCALE_DELTA;
    final StackPane zoomPane = new StackPane();

    zoomPane.getChildren().add(group);

    final ScrollPane scroller = new ScrollPane();
    final Group scrollContent = new Group(zoomPane);
    scroller.setContent(scrollContent);

    scroller.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
      @Override
      public void changed(ObservableValue<? extends Bounds> observable,
          Bounds oldValue, Bounds newValue) {
        zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight());
      }
    });

    scroller.setPrefViewportWidth(256);
    scroller.setPrefViewportHeight(256);

    zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
      @Override
      public void handle(ScrollEvent event) {
        event.consume();

        if (event.getDeltaY() == 0) { return; }

        scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA
            : 1 / SCALE_DELTA;

        // amount of scrolling in each direction in scrollContent coordinate
        // units
        Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

        group.setScaleX(group.getScaleX() * scaleFactor);
        group.setScaleY(group.getScaleY() * scaleFactor);

        // move viewport so that old center remains in the center after the
        // scaling
        repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

      }
    });
    
    // Panning via drag....
    final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<Point2D>();
    
    scrollContent.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        lastMouseCoordinates.set(new Point2D(event.getX(), event.getY()));
      }
    });

    scrollContent.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        double deltaX = event.getX() - lastMouseCoordinates.get().getX();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
        double desiredH = scroller.getHvalue() - deltaH;
        scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));

        double deltaY = event.getY() - lastMouseCoordinates.get().getY();
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double deltaV = deltaY * (scroller.getHmax() - scroller.getHmin()) / extraHeight;
        double desiredV = scroller.getVvalue() - deltaV;
        scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
      }
    });

    return scroller;
  }

  private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
    double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
    double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
    double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
    double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
    double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
    double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
    return new Point2D(scrollXOffset, scrollYOffset);
  }
  
    private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        scroller.setHvalue(0);
        scroller.setVvalue(0);
    }
  
  private MenuBar createMenuBar(final Stage stage, final Group group) {
    Menu fileMenu = new Menu("_File");
    MenuItem exitMenuItem = new MenuItem("E_xit");
    exitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        stage.close();
      }
    });
    
    fileMenu.getItems().setAll(exitMenuItem);
    Menu zoomMenu = new Menu("_Zoom");
    MenuItem zoomResetMenuItem = new MenuItem("Zoom _Reset");
    zoomResetMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
    
    zoomResetMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        group.setScaleX(1);
        group.setScaleY(1);
      }
    });
    
    MenuItem zoomInMenuItem = new MenuItem("Zoom _In");
    zoomInMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.I));
    zoomInMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        group.setScaleX(group.getScaleX() * 1.5);
        group.setScaleY(group.getScaleY() * 1.5);
      }
    });
    
    MenuItem zoomOutMenuItem = new MenuItem("Zoom _Out");
    zoomOutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O));
    zoomOutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        group.setScaleX(group.getScaleX() * 1 / 1.5);
        group.setScaleY(group.getScaleY() * 1 / 1.5);
      }
    });
    
    zoomMenu.getItems().setAll(zoomResetMenuItem, zoomInMenuItem,
        zoomOutMenuItem);
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().setAll(fileMenu, zoomMenu);
    return menuBar;
  }
}


