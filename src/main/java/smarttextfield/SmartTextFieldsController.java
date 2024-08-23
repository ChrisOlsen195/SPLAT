/****************************************************************************
 *                   SmartTextFieldsController                              * 
 *                           10/15/23                                       *
 *                            12:00                                         *
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
    
    // My classes
    SmartTextFieldHandler stf_Handler;
    DoublyLinkedSTF al_STF;
    
    public SmartTextFieldsController() {
       stf_Handler = new SmartTextFieldHandler(this);
    }
    
    public void setSize( int size) { 
        al_STF = new DoublyLinkedSTF(this, size);
    }
    
    public void finish_TF_Initializations() {
        
        for (int ithSTF = 0; ithSTF < al_STF.size; ithSTF++) {
            al_STF.get(ithSTF).finishInitializations();
        }
    }
    
    
    public DoublyLinkedSTF getLinkedSTF() { return al_STF; }
        
    public SmartTextFieldHandler getSTFHandler() {return stf_Handler; }   
}
