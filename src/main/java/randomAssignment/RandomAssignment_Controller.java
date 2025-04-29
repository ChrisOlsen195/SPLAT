/************************************************************
 *                 RandomAssignment_Controller              *
 *                          02/14/24                        *
 *                            03:00                         *
 ***********************************************************/
package randomAssignment;

import dialogs.Define_Treatments_Dialog;
import dataObjects.CategoricalDataVariable;
import dataObjects.ColumnOfData;
import dialogs.RandomAssignment_CRD_Dialog;
import dialogs.RandomAssignment_RBD_Dialog;
import java.util.ArrayList;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import utilityClasses.PrintExceptionInfo;

public class RandomAssignment_Controller {
    // POJOs
    int nTreats, nSubjects;
    
    private String returnStatus, theDesign;
    private String[] theTreats;
    
    // Make empty if no-print
    //String waldoFile = "RandomAssignment_Controller";
    String waldoFile = "";
    
    // My classes
    ArrayList<ColumnOfData> data;
    private Data_Manager dm;
    Define_Treatments_Dialog defineTreatments_Dialog;
    RandomAssignment_CRD_Dialog randAssign_CRD_Dialog;
    RandomAssignment_RBD_Dialog randAssign_RBD_Dialog;
    RandAssgn randAssign;
    
    // POJOs / FX
    
    public RandomAssignment_Controller(Data_Manager dm, String design) {
        this.dm = dm;
        this.theDesign = design;
        dm.whereIsWaldo(42, waldoFile, "Constructing");
        data = new ArrayList<>();
        returnStatus = "OK"; //  So far...
        nSubjects = dm.getNCasesInStruct();
        
        if (nSubjects == 0) {
            MyAlerts.showAintGotNoDataAlert();
            returnStatus = "Cancel";
        }
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(54, waldoFile, "doTheProcedure()");
        try {            
            switch (theDesign) {
                case "CRD":
                    randAssign_CRD_Dialog = new RandomAssignment_CRD_Dialog(dm);
                    dm.whereIsWaldo(56, waldoFile, "randAssign_CRD");
                    randAssign_CRD_Dialog.showAndWait();
                    returnStatus = randAssign_CRD_Dialog.getReturnStatus();
                    
                    if (!returnStatus.equals("OK")) {  return returnStatus; }
                    
                    data = randAssign_CRD_Dialog.getData();
                    dm.whereIsWaldo(65, waldoFile, "randAssign_CRD");
                    
                    if (!returnStatus.equals("OK")) { return returnStatus; }                
                    break;
                
                case "RBD":
                    randAssign_RBD_Dialog = new RandomAssignment_RBD_Dialog(dm);
                    dm.whereIsWaldo(71, waldoFile, "randAssign_RBD");
                    randAssign_RBD_Dialog.showAndWait();
                    returnStatus = randAssign_RBD_Dialog.getReturnStatus();
                    if (!returnStatus.equals("OK")) {
                        return returnStatus;
                    }
                    data = randAssign_RBD_Dialog.getData();
                    dm.whereIsWaldo(78, waldoFile, "randAssign_RBD");
                    if (!returnStatus.equals("OK")) {
                        return returnStatus;
                    }             
                    break;           
                
                default:
                    String switchFailure = "Switch failure: RandomAssignment_Controller 87 " + theDesign;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                break; 
            }

            defineTreatments_Dialog = new Define_Treatments_Dialog(this);
            defineTreatments_Dialog.constructDialogGuts();
            defineTreatments_Dialog.showAndWait();
            returnStatus = defineTreatments_Dialog.getReturnStatus();
            
            if (!returnStatus.equals("OK")) { return "Cancel"; }
            
            theTreats = new String[nTreats];
            theTreats = defineTreatments_Dialog.getTreatments();
            randAssign = new RandAssgn(theTreats, data, theDesign);

            if (randAssign.getSubj_X_Treats() == 0) { return "Cancel"; }
            
            randAssign.assignTheTreatments();
            nTreats = randAssign.getNTreats();
            nSubjects = randAssign.getNSubjects();
            dm.setDataAreClean(false);

            String theTreatmentVariable = defineTreatments_Dialog.getTreatmentVariable();
            CategoricalDataVariable cdv = new CategoricalDataVariable(theTreatmentVariable, randAssign.getTheTreatments());
            dm.addToStructOneColumnWithExistingCatData(cdv);
        }
        catch (Exception ex) { // Constructs stack trace?
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "RandomAssignment_Controller()");
        }
        return returnStatus;
    }
    
    public String getTheDesign() { return theDesign; }
    public int getNTreatments() { return nTreats; }
    public int getNSubjects() { return nSubjects; }
    public String getReturnStatus() { return returnStatus; }
    
    public void setReturnStatusTo (String returnStatus) {
        if (!returnStatus.equals("OK")) {
            //randAssign_CRD_Dialog.close();
        }
    }
}

