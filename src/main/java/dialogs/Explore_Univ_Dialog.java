/************************************************************
 *                      ExploreUniv_Dialog                  *
 *                          12/12/25                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class Explore_Univ_Dialog extends One_Variable_Dialog {
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    public Explore_Univ_Dialog(Data_Manager dm, String variableType) {
        super(dm, variableType);
        if (printTheStuff) {
            System.out.println("*** 16 Explore_Univ_Dialog, Constructing");
        }
        setTitle("Univariate Data Exploration");
    }   
}

