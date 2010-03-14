package jstamp.genome;

public class List {
  ListNode head;
  int size;

  public List () {
    head = new ListNode();
    head.dataPtr = null;
    head.nextPtr = null;
    size = 0;
  }

  Pair find (Pair dataPtr) {
    ListNode nodePtr;
    ListNode prevPtr = findPrevious(dataPtr);

    nodePtr = prevPtr.nextPtr;

    if ((nodePtr == null) || nodePtr.dataPtr.firstPtr.compareTo(dataPtr.firstPtr) !=0 ) {
      return null;
    }

    return nodePtr.dataPtr;
  }

  ListNode findPrevious (Pair dataPtr) {
    ListNode prevPtr = head;
    ListNode nodePtr;
    nodePtr = prevPtr.nextPtr;

    for (; nodePtr != null; nodePtr = nodePtr.nextPtr) {
      if (nodePtr.dataPtr.firstPtr.compareTo(dataPtr.firstPtr) >= 0) {
        return prevPtr;
      }
      prevPtr = nodePtr;
    }

    return prevPtr;
  }

  boolean insert (Pair dataPtr) {
    ListNode prevPtr;
    ListNode nodePtr;
    ListNode currPtr;

    prevPtr = findPrevious(dataPtr);
    currPtr = prevPtr.nextPtr;

    if ((currPtr != null) && (currPtr.dataPtr.firstPtr.compareTo(dataPtr.firstPtr)==0)) {
      return false;
    }

    nodePtr = new ListNode(dataPtr);

    nodePtr.nextPtr = currPtr;
    prevPtr.nextPtr = nodePtr;
    size++;

    return true;
  }
}
