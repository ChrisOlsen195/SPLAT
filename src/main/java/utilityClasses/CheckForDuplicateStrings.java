/************************************************************
 *                  CheckForDuplicateStrings                *
 *                          11/04/23                        *
 *                            12:00                         *
 ***********************************************************/
package utilityClasses;

import java.util.ArrayList;
import javafx.collections.ObservableList;

public class CheckForDuplicateStrings {

    int nStrings;
    ArrayList<String> al_TheStrings;
    
    public CheckForDuplicateStrings(ObservableList<String> theStrings) {
        al_TheStrings = new ArrayList<>();
        for (int ithString = 0; ithString < theStrings.size(); ithString++) {
            al_TheStrings.add(theStrings.get(ithString));
            //System.out.println("20 CheckForDups " +  al_TheStrings.get(ithString));
        }
        nStrings = al_TheStrings.size();
        
    }
    
    public CheckForDuplicateStrings(ObservableList<String> theStrings, String possibleDuplicate) {
        al_TheStrings = new ArrayList<>();
        
        for (int ithString = 0; ithString < theStrings.size(); ithString++) {
            al_TheStrings.add(theStrings.get(ithString));
        }
        
        al_TheStrings.add(possibleDuplicate);
        nStrings = al_TheStrings.size() + 1;
    }
    
    public String CheckTheStrings() {
        String dupStatus = "OK";        
        for (int ithString = 0; ithString < nStrings - 2; ithString++) {            
            for (int jthString = ithString + 1; jthString < nStrings - 1; jthString++) {
                if (al_TheStrings.get(ithString).equals(al_TheStrings.get(jthString)))  {
                    dupStatus = "DupFound";
                }
            }            
        }
        return dupStatus;        
    }
}
