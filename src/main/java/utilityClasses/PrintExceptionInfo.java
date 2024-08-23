/************************************************************
 *                     PrintExceptionInfo                   *
 *                         11/01/23                         *
 *                           00:00                          *
 ***********************************************************/
package utilityClasses;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class PrintExceptionInfo {
    
    public PrintExceptionInfo(Exception ex, String otherInfo) {       
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("An Exception Dialog -- NOT Your fault!");
        alert.setHeaderText("Ack!!!! -- an Exception Dialog!  This is not good...");
        String pleaToSend = "Uh-oh, looks like my programmer has messed up yet AGAIN! " +
                          "\nSomething unanticipated has occured, and he needs your help to " +
                          "\nfind what that is. Please 'Show Details', copy the stacktrace," +
                          "\n to a Word doc and send it to him at crolsen@fastmail.com. If you can," +
                          "\nsend your data file and perhaps a short note about what you were " +
                          "\ndoing when this problem occurred.  Thank you in advance!" +
                          "\n\n                                     -- Your statistics buddy, SPLAT";

        alert.setContentText(pleaToSend);
        
        // Liang 12 p466 -- for me
        // ex.printStackTrace();
        System.out.println("\n" + ex.getMessage());
        System.out.println("\n" + ex.toString());

        System.out.println("\nTrace Info Obtained from getStackTrace");
        StackTraceElement[] traceElements = ex.getStackTrace();
        
        for (StackTraceElement traceElement : traceElements) {
            System.out.print("method " + traceElement.getMethodName());
            System.out.print("(" + traceElement.getClassName() + ":");
            System.out.println(traceElement.getLineNumber() + ")");
        }

        // Create expandable Exception -- for user.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace is:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();   
    } 
}
