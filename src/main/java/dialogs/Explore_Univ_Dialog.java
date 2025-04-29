/************************************************************
 *                      ExploreUniv_Dialog                  *
 *                          11/01/23                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class Explore_Univ_Dialog extends One_Variable_Dialog {
    
    public Explore_Univ_Dialog(Data_Manager dm, String variableType) {
        super(dm, variableType);
        // Make empty if no-print
        waldoFile = "ExploreUniv_Dialog";
        //waldoFile = "";
        //dm.whereIsWaldo(17, waldoFile, "Constructing");
        setTitle("Univariate Data Exploration");
    }   
}

