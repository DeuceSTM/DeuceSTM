package jstamp.Labyrinth3D;

public class List_Iter {
    List_Node itPtr;

    /* =============================================================================
     * list_iter_reset
     * =============================================================================
       void list_iter_reset (list_iter_t* itPtr, list_t* listPtr);
    */
     public List_Iter() {
         itPtr = null;
     }
        
     public void reset(List_t listPtr) 
     {
         itPtr = listPtr.head;
     }

    /* =============================================================================
     * list_iter_hasNext
     * =============================================================================
     * bool_t list_iter_hasNext (list_iter_t* itPtr, list_t* listPtr);
     */
     public boolean hasNext(List_t listPtr) {
         return (itPtr.nextPtr != null)? true : false;
     }

    /* =============================================================================
     * list_iter_next
     * =============================================================================
     * void* list_iter_next (list_iter_t* itPtr, list_t* listPtr);
     */
     public Object next(List_t listPtr) 
     {
         itPtr = itPtr.nextPtr;
         return itPtr.dataPtr; 
     }
}

