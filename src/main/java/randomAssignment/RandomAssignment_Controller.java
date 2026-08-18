/************************************************************
 *                 RandomAssignment_Controller              *
 *                          08/15/26                        *
 *                            06:00                         *
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
    
    private String strReturnStatus, theDesign;
    private String[] theTreats;
    
    // Make empty if no-print
    //String waldoFile = "RandomAssignment_Controller";
    String waldoFile = "";
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
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
        dm.whereIsWaldo(45, waldoFile, "Constructing");
        data = new ArrayList<>();
        strReturnStatus = "OK"; //  So far...
        nSubjects = dm.getNCasesInStruct();
        
        if (nSubjects == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
        }
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(57, waldoFile, "doTheProcedure()");
        try {            
            switch (theDesign) {
                case "CRD":
                    dm.whereIsWaldo(61, waldoFile, "case CRD");
                    randAssign_CRD_Dialog = new RandomAssignment_CRD_Dialog(dm);
                    dm.whereIsWaldo(63, waldoFile, "randAssign_CRD");
                    randAssign_CRD_Dialog.showAndWait();
                    strReturnStatus = randAssign_CRD_Dialog.getStrReturnStatus();
                    dm.whereIsWaldo(66, waldoFile, "strReturnStatus = " + strReturnStatus);
                    if (!strReturnStatus.equals("OK")) { return strReturnStatus; }
                    
                    data = randAssign_CRD_Dialog.getData();
                    dm.whereIsWaldo(70, waldoFile, "randAssign_CRD");
                    
                    if (!strReturnStatus.equals("OK")) { return strReturnStatus; }                
                    break;
                
                case "RBD":
                    randAssign_RBD_Dialog = new RandomAssignment_RBD_Dialog(dm);
                    dm.whereIsWaldo(77, waldoFile, "randAssign_RBD");
                    randAssign_RBD_Dialog.showAndWait();
                    strReturnStatus = randAssign_RBD_Dialog.getStrReturnStatus();
                    if (!strReturnStatus.equals("OK")) {
                        return strReturnStatus;
                    }
                    data = randAssign_RBD_Dialog.getData();
                    dm.whereIsWaldo(84, waldoFile, "randAssign_RBD");
                    if (!strReturnStatus.equals("OK")) {
                        return strReturnStatus;
                    }             
                    break;           
                
                default:
                    String switchFailure = "Switch failure: RandomAssignment_Controller 91 " + theDesign;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                break; 
            }

            defineTreatments_Dialog = new Define_Treatments_Dialog(this);
            defineTreatments_Dialog.constructDialogGuts();
            defineTreatments_Dialog.showAndWait();
            strReturnStatus = defineTreatments_Dialog.getStrReturnStatus();
            
            if (!strReturnStatus.equals("OK")) { return "Cancel"; }
            
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
        return strReturnStatus;
    }
    
    public String getTheDesign() { return theDesign; }
    public int getNTreatments() { return nTreats; }
    public int getNSubjects() { return nSubjects; }
    
    public String getStrReturnStatus() { 
        if (printTheStuff) {
            System.out.println("... 130 RandomAssignment_Controller, getting StrReturnStatus: " + strReturnStatus);
        }    
        return strReturnStatus; 
    }  
    public void setStrReturnStatus(String toThis) { 
        if (printTheStuff) {
            System.out.println("... 136 RandomAssignment_Controller, settingStrReturnStatus to " + toThis);
        }    
        strReturnStatus = toThis; 
    }
}

