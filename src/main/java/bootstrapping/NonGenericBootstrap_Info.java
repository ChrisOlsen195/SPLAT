/**********************************************************************
 *                      NonGenericBootStrap_Info                      *
 *                             01/08/25                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import genericClasses.Point_2D;
import splat.Data_Manager;

public class NonGenericBootstrap_Info {
    
    // POJOs
    int nReplications;
    double originalXLower, originalXUpper, alpha;
    String whichBoot, theBootingStat, strBootedStatText,
           strTitle0, strTitle1;
    
    // MyClasses
    Boot_Histo_DistrView original_Histo_DistrView,
                         shifted_Histo_DistrView;
    Boot_DotPlot_DistrView original_DotPlot_DistrView,
                           shifted_DotPlot_DistrView;
    Point_2D binLimits;
    Boot_DistrModel originalDistrModel,
                    shiftedDistrModel;
 
    Bootstrap_Dashboard bootstrap_Dashboard;
    Data_Manager dm;
    QuantitativeDataVariable qdv_theOriginalSample, qdv_theBootStrappedSample;
    
    public NonGenericBootstrap_Info(String whichBoot) {
        this.whichBoot = whichBoot;
    } 
    
    public Data_Manager getTheDataManager() { return dm; }
    
    public void setTheDataManager(Data_Manager dm) { this.dm = dm; }
    
    public Bootstrap_Dashboard getTheDashboard() { return bootstrap_Dashboard; }
    public void setTheDashboard (Bootstrap_Dashboard bootstrap_Dashboard) {
        this.bootstrap_Dashboard = bootstrap_Dashboard;
    }            
    
    public String getWhichBoot() { return whichBoot; }
    
    public QuantitativeDataVariable getTheOriginalSample() {
        return qdv_theOriginalSample;
    };
    
    public void setTheOriginalSample(QuantitativeDataVariable qdv) {
        qdv_theOriginalSample = qdv;
    }
    
    public String getTheBootingStat() { return theBootingStat; }
    
    public void setTheBootingStat(String toThis) {
        theBootingStat = toThis;
    }
    
    public QuantitativeDataVariable getTheBootStrappedSample() {
        return qdv_theBootStrappedSample;
    };
    public void setTheBootStrappedSample(QuantitativeDataVariable qdv) {
        qdv_theBootStrappedSample = qdv;
    }
    
    public Boot_DistrModel getOriginalDistrModel() { return originalDistrModel; }
    public void setOriginalDistrModel(Boot_DistrModel originalDistrModel) { 
        this.originalDistrModel = originalDistrModel; 
    } 
    
    public Boot_DistrModel getShiftedDistrModel() { return shiftedDistrModel; }
    public void setShiftedDistrModel(Boot_DistrModel shiftedDistrModel) { 
        this.shiftedDistrModel = shiftedDistrModel; 
    }  
    
    public Boot_Histo_DistrView get_Original_Histo_DistrView () {
        return original_Histo_DistrView;
    } 
    public void set_Original_Histo_DistrView (Boot_Histo_DistrView original_Histo_DistrView) {
        this.original_Histo_DistrView = original_Histo_DistrView;
    }
    
    public Boot_Histo_DistrView get_Shifted_Histo_DistrView () {
        return shifted_Histo_DistrView;
    } 
    public void set_Shifted_Histo_DistrView (Boot_Histo_DistrView original_Shifted_Histo_DistrView) {
        this.shifted_Histo_DistrView = original_Shifted_Histo_DistrView;
    }  

    public Boot_DotPlot_DistrView get_Original_DotPlot_DistrView () {
        return original_DotPlot_DistrView;
    } 
    public void set_Original_DotPlot_DistrView (Boot_DotPlot_DistrView original_DotPlot_DistrView) {
        this.original_DotPlot_DistrView = original_DotPlot_DistrView;
    }
    
    public Boot_DotPlot_DistrView get_Shifted_DotPlot_DistrView () {
        return shifted_DotPlot_DistrView;
    } 
    public void set_Shifted_DotPlot_DistrView (Boot_DotPlot_DistrView shifted_DotPlot_DistrView) {
        this.shifted_DotPlot_DistrView = shifted_DotPlot_DistrView;
    }  
    
    public double getAlpha() { return alpha; }
    public void setAlpha(double alpha) {this.alpha = alpha; }
    
    public String getBootedStatText() { return strBootedStatText; }
    public void setBootedStatText(String toThis) {strBootedStatText = toThis; }
      
    public int getNReplications() { return nReplications; }
    public void setNReplications(int nReplications) {
        this.nReplications = nReplications;
    }
    
    public Point_2D getBinLimits() { return binLimits; }
    public void setBinLimits(Point_2D binLimits) {
        this.binLimits = binLimits;
    }
    
    public String[] getTheTitles() { 
        String[] theTitles = new String[2];
        theTitles[0] = strTitle0;
        theTitles[1] = strTitle1;
        
        return theTitles; 
    }
    public void setTheTitles(String title0, String title1) { 
        strTitle0 = title0;
        strTitle1 = title1;   
    }    
    
    public double getOriginalXLower() { return originalXLower; }
    public void setOriginalXLower(double originalXLower) { this.originalXLower = originalXLower; }

    public double getOriginalXUpper() { return originalXUpper; }
    public void setOriginalXUpper(double originalXUpper) { this.originalXUpper = originalXUpper; }
    
    public double getAdjustedXLower() { return originalXLower; }
    public void setAdjustedXLower(double originalXLower) { this.originalXLower = originalXLower; }

    public double getAdjustedXUpper() { return originalXUpper; }
    public void setAdjustedXUpper(double originalXUpper) { this.originalXUpper = originalXUpper; }
    
    public String toString() {
        System.out.println("148 NonGenericBootstrap_Info - to String");
        System.out.println("149 NonGenericBootstrap_Info - whichBoot = " + whichBoot);
        return "Done";
    }
}
