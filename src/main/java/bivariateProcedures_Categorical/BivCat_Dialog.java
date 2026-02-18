/************************************************************
 *                       BivCat_Dialog                      *
 *                          10/11/25                        *
 *                            12:00                         *
 ***********************************************************/
package bivariateProcedures_Categorical;

import dialogs.Two_Variables_Dialog;
import splat.Data_Manager;

public class BivCat_Dialog extends Two_Variables_Dialog{ 
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public BivCat_Dialog(Data_Manager dm, String procCaller) {
        super(dm, "BivCatDialog", procCaller);
        this.dm = dm;
        if (printTheStuff) {
            System.out.println("*** 20 BivCatDialog, Constructing");
            System.out.println("--- 21 BivCatDialog, procCaller = " + procCaller);
        }
    }  
}

