/************************************************************
 *                     ProbCalc_Controller                  *
 *                          11/27/23                        *
 *                            00:00                         *
 ***********************************************************/
package probabilityCalculators;

public class ProbCalc_Controller {
    // POJOs
    String returnStatus;
    
    // My classes
    ProbCalc_Dashboard probCalc_Dashboard;  
    // POJOs / FX
    
    public ProbCalc_Controller() { 
        //System.out.println("17 ProbCalc_Controller, constructing");
    } 
        
    public String doTheProcedure() {
        // Not sure this is needed!
        try {
            probCalc_Dashboard = new ProbCalc_Dashboard(this);
            probCalc_Dashboard.populateTheBackGround();
            probCalc_Dashboard.putEmAllUp();
            probCalc_Dashboard.showAndWait();
            returnStatus = probCalc_Dashboard.getReturnStatus();
            return returnStatus;  
        }
        catch(Exception ex) {
            // ex.printStackTrace();  ?? p466 Liang
            System.out.println("\n" + ex.getMessage());
            System.out.println("\n" + ex.toString());
            System.out.println("\nTrace Info Obtained from getStackTrace");
            StackTraceElement[] traceElements = ex.getStackTrace();
            for (int i = 0; i < traceElements.length; i++) {
                System.out.print("method " + traceElements[i].getMethodName());
                System.out.print("(" + traceElements[i].getClassName() + ":");
                System.out.print(traceElements[i].getLineNumber() + ")\n");
            }             
        }
        return returnStatus;
    }
}

