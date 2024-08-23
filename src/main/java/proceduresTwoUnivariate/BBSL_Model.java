/**************************************************
 *                   BBSL_Model                   *
 *                    02/07/24                    *
 *                     18:00                      *
 *************************************************/
package proceduresTwoUnivariate;

import proceduresOneUnivariate.StemNLeaf_Model;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import splat.Data_Manager;
import the_t_procedures.Indep_t_PrepStructs;
import utilityClasses.MyAlerts;

public final class BBSL_Model {
    // POJOs
    boolean witchesWarned;
    int maxLeftSize, maxRightSize, maxBbslLineSize,
        bbslFirstNonZeroColumn, bbslFirstNonConstantColumn;
    
    int maxCharsInLine, maxLineInBBSL;
    
    int maxBbslLineSize_01, maxBbslLineSize_02, maxBbslLineSize_05;
    int nStemsNeeded_1, nStemsNeeded_2, nStemsNeeded_5;
    double maxValue;

    String blanx, subTitle_And,
           leftDataLabel, rightDataLabel,
           leftLeafString, rightLeafString, 
           tempLeftString, tempRightString,
           firstVarDescription, secondVarDescription,
           firstAndSecondDescription;
    
    // Make empty if no-print
    //String waldoFile = "BBL_Model";
    String waldoFile = "";
    
    ArrayList<String> oneLineStemPlotLeft, twoLineStemPlotLeft, fiveLineStemPlotLeft,
                      oneLineStemPlotRight, twoLineStemPlotRight, fiveLineStemPlotRight,
                      oneLineBBSL, twoLineBBSL, fiveLineBBSL;
    
    // My classes
    QuantitativeDataVariable pooledQDV;
    ArrayList<QuantitativeDataVariable> bbslAllTheQDVs;  
    Data_Manager dm;
        
    // POJOs / FX

    TextArea textArea1, textArea2, textArea5;

    public BBSL_Model(Explore_2Ind_Controller explore_2Ind_Controller, 
            QuantitativeDataVariable pooledQDV,
            ArrayList<QuantitativeDataVariable> allTheQDVs) {
        dm = explore_2Ind_Controller.getDataManager();
        dm.whereIsWaldo(54, waldoFile, "Constructing from explore_2_Ind_Controller");
        subTitle_And = explore_2Ind_Controller.getSubTitleAnd();
        bbslAllTheQDVs = new ArrayList<>();
        this.pooledQDV = pooledQDV;
        bbslAllTheQDVs.add(allTheQDVs.get(0));  // Left
        bbslAllTheQDVs.add(allTheQDVs.get(1));  // Right
        makeTheBBSL();
    }
    
    public BBSL_Model(Indep_t_PrepStructs exp_2Ind_Structs, 
            QuantitativeDataVariable pooledQDV,
            ArrayList<QuantitativeDataVariable> allTheQDVs) {
        dm = exp_2Ind_Structs.getDataManager();
        dm.whereIsWaldo(65, waldoFile, "Constructing from exp_2Ind_Structs");
        firstVarDescription = exp_2Ind_Structs.getFirstVarDescription();
        secondVarDescription = exp_2Ind_Structs.getSecondVarDescription();
        firstAndSecondDescription = firstVarDescription + " vs. " + secondVarDescription;
        bbslAllTheQDVs = new ArrayList<>();
        this.pooledQDV = pooledQDV;
        bbslAllTheQDVs.add(allTheQDVs.get(0));  // Left
        bbslAllTheQDVs.add(allTheQDVs.get(1));  // Right
        makeTheBBSL();
    }
    
    private void makeTheBBSL() {
        maxCharsInLine = 100;
        maxLineInBBSL = 60;
        witchesWarned = false;
        textArea1 = new TextArea();
        textArea1.setFont(Font.font("Courier New"));    
        textArea2 = new TextArea();
        textArea2.setFont(Font.font("Courier New"));        
        textArea5 = new TextArea();
        textArea5.setFont(Font.font("Courier New"));
                        
        oneLineStemPlotLeft = new ArrayList<>();
        twoLineStemPlotLeft = new ArrayList<>();
        fiveLineStemPlotLeft = new ArrayList<>();

        oneLineStemPlotRight = new ArrayList<>();
        twoLineStemPlotRight = new ArrayList<>();
        fiveLineStemPlotRight = new ArrayList<>();
        
        oneLineBBSL = new ArrayList<>();
        twoLineBBSL = new ArrayList<>();
        fiveLineBBSL = new ArrayList<>();
        
        maxValue = (int)pooledQDV.getMaxValue();
        leftDataLabel = bbslAllTheQDVs.get(0).getTheVarLabel();
        rightDataLabel = bbslAllTheQDVs.get(1).getTheVarLabel();

        StemNLeaf_Model sandLAll = new StemNLeaf_Model("Null", pooledQDV, false, 0, 0, 0);
        int orderOfMagnitude = sandLAll.getOrderOfMagnitude();  // for 1|0 in SL
        
        // Needed by left and right to get proper columns;
        bbslFirstNonZeroColumn = sandLAll.getFirstNonZeroColumn();
        bbslFirstNonConstantColumn = sandLAll.getFirstNonConstantColumn();
        
        StemNLeaf_Model sandLLeft = new StemNLeaf_Model("Null", bbslAllTheQDVs.get(0), true, orderOfMagnitude,
                                                        bbslFirstNonZeroColumn, bbslFirstNonConstantColumn);
        StemNLeaf_Model sandLRight = new StemNLeaf_Model("Null", bbslAllTheQDVs.get(1), true, orderOfMagnitude,
                                                         bbslFirstNonZeroColumn, bbslFirstNonConstantColumn);
      
        ArrayList<String> theAllOneLiners = new ArrayList();
        ArrayList<String> theAllTwoLiners = new ArrayList();
        ArrayList<String> theAllFiveLiners = new ArrayList();
        ArrayList<String> theLeftOneLiners = new ArrayList();
        ArrayList<String> theRightOneLiners = new ArrayList();
        ArrayList<String> theLeftTwoLiners = new ArrayList();
        ArrayList<String> theRightTwoLiners = new ArrayList();
        ArrayList<String> theLeftFiveLiners = new ArrayList();
        ArrayList<String> theRightFiveLiners = new ArrayList();
       
        theAllOneLiners = sandLAll.get_1_LineSL(); 
        theAllTwoLiners = sandLAll.get_2_LineSL(); 
        theAllFiveLiners = sandLAll.get_5_LineSL(); 
        
        theLeftOneLiners = sandLLeft.get_1_LineSL();
        theRightOneLiners = sandLRight.get_1_LineSL();
        theLeftTwoLiners = sandLLeft.get_2_LineSL();
        theRightTwoLiners = sandLRight.get_2_LineSL();
        theLeftFiveLiners = sandLLeft.get_5_LineSL();
        theRightFiveLiners = sandLRight.get_5_LineSL();
        
        int nAllOneLiners = theAllOneLiners.size();
        int nAllTwoLiners = theAllTwoLiners.size();
        int nAllFiveLiners = theAllFiveLiners.size();
        
        int nLeftOneLiners = theLeftOneLiners.size();
        int nRightOneLiners = theRightOneLiners.size();
        int nLeftTwoLiners = theLeftTwoLiners.size();
        int nRightTwoLiners = theRightTwoLiners.size();
        int nLeftFiveLiners = theLeftFiveLiners.size();
        int nRightFiveLiners = theRightFiveLiners.size();
        
        int vertBarPosOne = theAllOneLiners.get(0).indexOf("|");
        int vertBarPosTwo = theAllTwoLiners.get(0).indexOf("|");
        int vertBarPosFive = theAllFiveLiners.get(0).indexOf("|");
        
/****************************************************************************
 *                        Start Code for one liners                         *
 ***************************************************************************/        
/****************************************************************************
 *  Need to know stem lengths to (a) position the bbsl, and (b) set up the  * 
 *  size of the text boxes                                                  *
 ***************************************************************************/

        maxLeftSize = 0; maxRightSize = 0; maxBbslLineSize = 0;
        
        for (int jthLeftLiner = 0; jthLeftLiner < nLeftOneLiners; jthLeftLiner++) {
            maxLeftSize = Math.max(maxLeftSize, theLeftOneLiners.get(jthLeftLiner).length());
        }

        for (int jthRightLiner = 0; jthRightLiner < nRightOneLiners; jthRightLiner++) {
            maxRightSize = Math.max(maxRightSize, theRightOneLiners.get(jthRightLiner).length());
        }  

        maxBbslLineSize = maxLeftSize + maxRightSize;
        maxBbslLineSize_01 = maxBbslLineSize;
        
        if (maxBbslLineSize > maxCharsInLine) {
            witchesWarned = true;
            MyAlerts.showStemAndLeafAlert();
        }
        
        for (int ithOneLiner = 0; ithOneLiner < nAllOneLiners; ithOneLiner++) {       
            initStrings();
            String tempAllString = theAllOneLiners.get(ithOneLiner);
            String daAllStem = tempAllString.substring(0, vertBarPosOne + 1);
            
            for (int jthLeftLiner = 0; jthLeftLiner < nLeftOneLiners; jthLeftLiner++) {
                tempLeftString = theLeftOneLiners.get(jthLeftLiner);
                String daLeftStem = tempLeftString.substring(0, vertBarPosOne + 1);
                
                if (daAllStem.equals(daLeftStem)) {
                    StringBuilder daLeftLeafs = new StringBuilder(tempLeftString);
                    daLeftLeafs.delete(0, vertBarPosOne + 1);
                    daLeftLeafs.reverse();
                    leftLeafString = new String(daLeftLeafs);
                    break;
                }
            }
            
            for (int kthRightLiner = 0; kthRightLiner < nRightOneLiners; kthRightLiner++) {
                tempRightString = theRightOneLiners.get(kthRightLiner);
                String daRightStem = tempRightString.substring(0, vertBarPosOne + 1);
                
                if (daAllStem.equals(daRightStem)) {
                    StringBuilder daRightLeafs = new StringBuilder(tempRightString);
                    daRightLeafs.delete(0, vertBarPosOne + 1);
                    rightLeafString = new String(daRightLeafs);
                    break;
                }
            }  
            
            String daLine = leftLeafString + "|" + daAllStem + rightLeafString;
            blanx = generateNBlanx(maxLeftSize + 5 - leftLeafString.length());
            String printLine = blanx + daLine;
            oneLineBBSL.add(printLine);
        }
        
        nStemsNeeded_1 = oneLineBBSL.size();
       
/****************************************************************************
 *                        Start Code for two liners                         *
 ***************************************************************************/               
        maxLeftSize = 0; maxRightSize = 0; maxBbslLineSize = 0;    
        
        // Need to know stem lengths to set up text boxes
        for (int jthLeftLiner = 0; jthLeftLiner < nLeftTwoLiners; jthLeftLiner++) {
            maxLeftSize = Math.max(maxLeftSize, theLeftTwoLiners.get(jthLeftLiner).length());
        }

        for (int jthRightLiner = 0; jthRightLiner < nRightTwoLiners; jthRightLiner++) {
            maxRightSize = Math.max(maxRightSize, theRightTwoLiners.get(jthRightLiner).length());
        } 

        maxBbslLineSize = maxLeftSize + maxRightSize;
        maxBbslLineSize_02 = maxBbslLineSize;
        
        for (int ithTwoLiner = 0; ithTwoLiner < nAllTwoLiners; ithTwoLiner++) {
            initStrings();
  
            String tempAllString = theAllTwoLiners.get(ithTwoLiner);
            String daAllStem = tempAllString.substring(0, vertBarPosTwo + 1);
            
            for (int jthLeftLiner = 0; jthLeftLiner < nLeftTwoLiners; jthLeftLiner++) {
                tempLeftString = theLeftTwoLiners.get(jthLeftLiner);
                String daLeftStem = tempLeftString.substring(0, vertBarPosTwo + 1);
                if (daAllStem.equals(daLeftStem)) {
                    StringBuilder daLeftLeafs = new StringBuilder(tempLeftString);
                    daLeftLeafs.delete(0, vertBarPosTwo + 1);
                    daLeftLeafs.reverse();
                    leftLeafString = new String(daLeftLeafs);
                    break;
                }
            }
            
            for (int kthRightLiner = 0; kthRightLiner < nRightTwoLiners; kthRightLiner++) {
                tempRightString = theRightTwoLiners.get(kthRightLiner);
                String daRightStem = tempRightString.substring(0, vertBarPosTwo + 1);
                
                if (daAllStem.equals(daRightStem)) {
                    StringBuilder daRightLeafs = new StringBuilder(tempRightString);
                    daRightLeafs.delete(0, vertBarPosTwo + 1);
                    rightLeafString = new String(daRightLeafs);
                    break;
                }
            }    

            String daLine = leftLeafString + "|" + daAllStem + rightLeafString;
            blanx = generateNBlanx(maxLeftSize + 5 - leftLeafString.length());
            String printLine = blanx + daLine;
            twoLineBBSL.add(printLine);
        }
        
        nStemsNeeded_2 = twoLineBBSL.size();

/****************************************************************************
 *                        Start Code for five liners                         *
 ***************************************************************************/                
        maxLeftSize = 0; maxRightSize = 0; maxBbslLineSize = 0;    
        
        // Need to know stem lengths to set up text boxes
        for (int jthLeftLiner = 0; jthLeftLiner < nLeftFiveLiners; jthLeftLiner++) {
            maxLeftSize = Math.max(maxLeftSize, theLeftFiveLiners.get(jthLeftLiner).length());
        }

        for (int jthRightLiner = 0; jthRightLiner < nRightFiveLiners; jthRightLiner++) {
            maxRightSize = Math.max(maxRightSize, theRightFiveLiners.get(jthRightLiner).length());
        }

        maxBbslLineSize = maxLeftSize + maxRightSize;
        maxBbslLineSize_05 = maxBbslLineSize;
        
        for (int ithFiveLiner = 0; ithFiveLiner < nAllFiveLiners; ithFiveLiner++) {
            initStrings();  
            String tempAllString = theAllFiveLiners.get(ithFiveLiner);
            String daAllStem = tempAllString.substring(0, vertBarPosFive + 1);
            
            for (int jthLeftLiner = 0; jthLeftLiner < nLeftFiveLiners; jthLeftLiner++) {
                tempLeftString = theLeftFiveLiners.get(jthLeftLiner);
                String daLeftStem = tempLeftString.substring(0, vertBarPosFive + 1);
                
                if (daAllStem.equals(daLeftStem)) {
                    StringBuilder daLeftLeafs = new StringBuilder(tempLeftString);
                    daLeftLeafs.delete(0, vertBarPosFive + 1);
                    daLeftLeafs.reverse();
                    leftLeafString = new String(daLeftLeafs);
                    break;
                }
            }
            
            for (int kthRightLiner = 0; kthRightLiner < nRightFiveLiners; kthRightLiner++) {
                tempRightString = theRightFiveLiners.get(kthRightLiner);
                String daRightStem = tempRightString.substring(0, vertBarPosFive + 1);
                
                if (daAllStem.equals(daRightStem)) {
                    StringBuilder daRightLeafs = new StringBuilder(tempRightString);
                    daRightLeafs.delete(0, vertBarPosFive + 1);
                    rightLeafString = new String(daRightLeafs);
                    break;
                }
            }    

            String daLine = leftLeafString + "|" + daAllStem + rightLeafString;
            blanx = generateNBlanx(maxLeftSize + 5 - leftLeafString.length());
            String printLine = blanx + daLine;
            fiveLineBBSL.add(printLine);
        }
        
        nStemsNeeded_5 = fiveLineBBSL.size();
  
        if ((nStemsNeeded_5 > maxLineInBBSL) && !witchesWarned) {
            MyAlerts.showStemAndLeafAlert();
        } 
    }
    
    private void initStrings() {
        leftLeafString = ""; rightLeafString = "";
        tempLeftString = ""; tempRightString = "";
    }
    
    public ArrayList<String> get_1_LineBBSL() { return oneLineBBSL; }
    public ArrayList<String> get_2_LineBBSL() { return twoLineBBSL; }
    public ArrayList<String> get_5_LineBBSL() { return fiveLineBBSL; } 
    
    public String getFirstAndSecondDescription() { return firstAndSecondDescription; }
    
    public int getBBSLFirstNonZeroColumn() { return bbslFirstNonZeroColumn; }
    public int getBBSLFirstNonConstantColumn() { return bbslFirstNonConstantColumn; }

    public ArrayList<QuantitativeDataVariable> getAllUDMs() { return bbslAllTheQDVs; }

    public double getMax() { return maxValue; }
    public String getSubTitle_And() { return subTitle_And; }
    
    public String generateNBlanx(int nBlanks) {
        StringBuilder theBlanks = new StringBuilder();
        
        for (int iBlank = 0; iBlank < nBlanks; iBlank++) {
            theBlanks.append(" ");
        }
        return theBlanks.toString();
    }
}

