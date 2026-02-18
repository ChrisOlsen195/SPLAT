/****************************************************************************
 *                   SmartTextFieldsController                              * 
 *                           01/01/26                                       *
 *                            09:00                                         *
 ***************************************************************************/
/***************************************************************************      
*    This is the typical garden-variety sequence to set up the STFs        *
*                                                                          * 
*    stf_Controller = new SmartTextFieldsController();                     *
*    // stf_Controller is empty until size is set                          *
*    stf_Controller.setSize(4);                                            *
*    stf_Controller.finish_TF_Initializations();                           *
*    al_STF = stf_Controller.getLinkedSTF();                               *
*    al_STF.makeCircular();                                                *
*                                                                          *
****************************************************************************/
package smarttextfield;

public class SmartTextFieldsController {
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
    SmartTextFieldHandler stf_Handler;
    SmartTextFieldDoublyLinkedSTF al_STF;
    
    public SmartTextFieldsController() {
        if (printTheStuff) {
            System.out.println("31 *** SmartTextFieldsController, Constructing");
        }
       stf_Handler = new SmartTextFieldHandler(this);
    }
    
    public void setSize( int size) { 
        al_STF = new SmartTextFieldDoublyLinkedSTF(this, size);
    }
    
    public void finish_TF_Initializations() {
        
        for (int ithSTF = 0; ithSTF < al_STF.size; ithSTF++) {
            al_STF.get(ithSTF).finishInitializations();
        }
    }
    
    public SmartTextFieldDoublyLinkedSTF getLinkedSTF() { return al_STF; }
        
    public SmartTextFieldHandler getSTFHandler() {return stf_Handler; }   
}
