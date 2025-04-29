/****************************************************************************
 *                       DoublyLinkedSTF                                    * 
 *                           01/22/25                                       *
 *                            00:00                                         *
 ***************************************************************************/
package smarttextfield;

import java.util.ArrayList;

public class SmartTextFieldDoublyLinkedSTF {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int size;
    
    ArrayList<SmartTextField> al_STF;
    
    public SmartTextFieldDoublyLinkedSTF() { }
    
    public SmartTextFieldDoublyLinkedSTF (SmartTextFieldsController stf_Controller, int size) {
        if (printTheStuff == true) {
            System.out.println("23 *** DoublyLinkedSTF, Constructing, size = " + size);
        }
        this.size = size;
        al_STF = new ArrayList(size);        
        for (int ithSTF = 0; ithSTF < size; ithSTF++) {
            al_STF.add(new SmartTextField(stf_Controller));
        }        
        al_STF.get(0).setPre_Me_AndPostSmartTF(0, 0, 1);
        al_STF.get(size - 1).setPre_Me_AndPostSmartTF(size - 2, size - 1, size - 1);
        for (int ithSTF = 0; ithSTF < size - 1; ithSTF++) {
            al_STF.get(ithSTF).setPre_Me_AndPostSmartTF(ithSTF - 1, ithSTF, ithSTF + 1);
        }
    } 
    
    public void makeCircular() {
        if (printTheStuff == true) {
            System.out.println("39 --- DoublyLinkedSTF, makeCircular() ");
        }
        al_STF.get(0).setPre_Me_AndPostSmartTF(size - 1, 0, 1);
        al_STF.get(size - 1).setPre_Me_AndPostSmartTF(size - 2, size - 1, 0);        
    }
    
    public SmartTextField get(int thisOne) { return al_STF.get(thisOne); }    
    public ArrayList<SmartTextField> getTheDLArrayList() { return al_STF; }   
    public int getSize() { return al_STF.size(); }
    
    public String toString() {
        System.out.println("     *****  DoublyLinkedSTF   *****");
        System.out.println("STF size = " + size);
        System.out.println("         backTo   ThisOne   ForwardTo ");
        for (int ithSTF = 0; ithSTF < size; ithSTF++) {
            System.out.println(al_STF.get(ithSTF).toString());
        }
        return "EndethLinketh";
    }
}
