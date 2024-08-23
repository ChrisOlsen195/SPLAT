/*******************************************************************************
 *                       RandAssgn                                             *
 *                        0214/24                                             *
 *                         03:00                                               *
 ******************************************************************************/
package randomAssignment;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import java.util.Random;

public class RandAssgn {
    // POJOs
    boolean blockVarIsNumeric;
    
    int tempInt, nSub_X_nTreats, nSubjects, nTreatments;
    int[] index_Number;
    
    double tempDouble, TempDblJ, tempDbleK;
    double[] randomNumber;
    
    Random rand = new Random();
    
    String tempString, tempStrJ, tempStrK, design;
    String[] strSubjects, strAssignedTreat, strAvailableTreatments, strBlocks;
    
    // My classes
    ColumnOfData column_BlockingVariable;
    
    public RandAssgn(String[] theTreats, ArrayList<ColumnOfData> data, String design) {
        //System.out.println("30 RandAssgn, constructing");
        nTreatments = theTreats.length;
        nSubjects = data.get(0).getColumnSize();
        nSub_X_nTreats = nSubjects * nTreatments;
        
        if (nSubjects * nTreatments > 0) {            
            this.design = design;            
            if (design.equals("RBD")) {
                column_BlockingVariable = new ColumnOfData(data.get(1));
                blockVarIsNumeric = data.get(1).getIsNumeric();
                //System.out.println("40 RandAssign, blockVarIsNumeric = " + blockVarIsNumeric);
            }
            
            strBlocks = new String[nSubjects];
            index_Number = new int[nSubjects];
            strSubjects = new String[nSubjects];
            strAssignedTreat = new String[nSubjects];
            randomNumber = new double[nSubjects];
            strAvailableTreatments = new String[nTreatments];
            System.arraycopy(theTreats, 0, strAvailableTreatments, 0, nTreatments);
            System.arraycopy(data.get(0).getTheCases_asStrings(), 0, 
                             strSubjects, 0, nSubjects);
            
            if (design.equals("RBD")) {
            System.arraycopy(data.get(1).getTheCases_asStrings(), 0, 
                             strBlocks, 0, nSubjects);            
            }
            initializeArrays();   
        }
    }   
    
    private void initializeArrays()  {
        //System.out.println("61 RandAssgn, initializeArrays()");
        for (int indxNumber = 0; indxNumber < nSubjects; indxNumber++) {
            index_Number[indxNumber] = indxNumber;
            strAssignedTreat[indxNumber] = "Initial";
            randomNumber[indxNumber] = rand.nextDouble();   
        }
    }
 
    public void assignTheTreatments() {
        //System.out.println("70 RandAssgn, assignTheTreatments()");
        if (design.equals("CRD")) {
            bubbleSortByRandNum(); 
            for (int i = 0; i < nSubjects; i++) {    
                strAssignedTreat[i] = strAvailableTreatments[i % nTreatments];
            }
        } else {
            if (blockVarIsNumeric) {
                bubbleSortByBlockByValue();
                for (int i = 0; i < nSubjects; i++) {    
                   strAssignedTreat[i] = strAvailableTreatments[i % nTreatments];
                }
            } else {    // blockVar is NOT numeric
                bubbleSortByRandNum();
                bubbleSortByBlockByString();
                for (int i = 0; i < nSubjects; i++) {    
                    strAssignedTreat[i] = strAvailableTreatments[i % nTreatments];
                }
            }
        }
         bubbleSortByIndexNumber();
         //System.out.println("92 RandAssgn, assignTheTreatments() -- exit");
}
    // For debugging
    private void printTheArrays() {
        System.out.println("\n\n");  
        System.out.println("95 RandAssgn, printTheArrays()");
        if (!design.equals("CRD")) {
            for (int i = 0; i < nSubjects; i++) {
                System.out.println(index_Number[i]
                                     + " / " 
                                     + strSubjects[i]
                                     + " / " 
                                     + strBlocks[i]
                                     + " / " 
                                     + strAssignedTreat[i]
                );
            }
        }
        else {
            for (int i = 0; i < nSubjects; i++) {
                System.out.println(index_Number[i]
                                     + " / " 
                                     + strAssignedTreat[i]
                );
            }            
        }
    }
    
    private void bubbleSortByRandNum() {
        for (int i = 0; i < nSubjects - 1; i++) {   
            for (int j = 0; j < nSubjects - i - 1; j++) {
                if (randomNumber[j] > randomNumber[j+1]) {
                    swappy(j, j + 1); 
                }
            }
        }
    }
    
    
    private void bubbleSortByBlockByValue() {
        
        for (int i = 0; i < nSubjects - 1; i++) {               
            for (int j = 0; j < nSubjects - i - 1; j++) {                
                tempStrJ = column_BlockingVariable.getStringInIthRow(j);
                tempStrK = column_BlockingVariable.getStringInIthRow(j + 1);
                TempDblJ = Double.parseDouble(tempStrJ);
                tempDbleK = Double.parseDouble(tempStrK);
                
                if (TempDblJ > tempDbleK) {  swappy(j, j + 1); }
            }
        }        
    }
    
    private void bubbleSortByBlockByString() {        
        for (int i = 0; i < nSubjects - 1; i++) {               
            for (int j = 0; j < nSubjects - i - 1; j++) {
                tempStrJ = strBlocks[j];
                tempStrK = strBlocks[j+1];
                tempInt = tempStrJ.compareTo(tempStrK);
                if (tempInt > 0) {
                    swappy(j, j + 1); 
                }
            }
        }        
    }
 
    public void bubbleSortByIndexNumber() {
        
        for (int i = 0; i < nSubjects - 1; i++) {               
            for (int j = 0; j < nSubjects - i - 1; j++) {
                if (index_Number[j] > index_Number[j+1]) {
                    swappy(j, j + 1); 
                }
            }
        }
    }
    
    public void swappy(int thisOne, int thatOne) {        
        // Index #
        tempInt = index_Number[thisOne];
        index_Number[thisOne] = index_Number[thatOne];
        index_Number[thatOne] = tempInt;
        
        // subjects
        tempString = strSubjects[thisOne];
        strSubjects[thisOne] = strSubjects[thatOne];
        strSubjects[thatOne] = tempString;
        
        // treatments
        tempString = strAssignedTreat[thisOne];
        strAssignedTreat[thisOne] = strAssignedTreat[thatOne];
        strAssignedTreat[thatOne] = tempString;


        // randomNumber
        tempDouble = randomNumber[thisOne];
        randomNumber[thisOne] = randomNumber[thatOne];
        randomNumber[thatOne] = tempDouble;      
        
        // Block variable.  Block variable is a ColumnOfData
        // This is only a switch, not a compare and switch
        if (design.equals("RBD")) {
            tempString = strBlocks[thisOne];
            strBlocks[thisOne] = strBlocks[thatOne];
            strBlocks[thatOne] = tempString;
        }
    }
    
    public String[] getTheTreatments() {return strAssignedTreat; }
    public int getNTreats() { return nTreatments; }
    public int getNSubjects() { return nSubjects; }
    public int getSubj_X_Treats() { return nSub_X_nTreats; }
 }

