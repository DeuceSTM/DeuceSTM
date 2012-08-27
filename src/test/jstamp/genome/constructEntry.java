package jstamp.genome;

public class constructEntry {
    boolean isStart;
    ByteString segment;
    int endHash;
    constructEntry startPtr;
    constructEntry nextPtr;
    constructEntry endPtr;
    int overlap;
    int length;
      
    constructEntry(ByteString mySegment, boolean myStart, int myEndHash, constructEntry myStartPtr, constructEntry myNextPtr, constructEntry myEndPtr, int myOverlap, int myLength) {
      segment = mySegment;
      isStart = myStart;
      endHash = myEndHash;
      startPtr = this;
      nextPtr = myNextPtr;
      endPtr = this;
      overlap = myOverlap;
      length = myLength;
    }
    
    boolean equals(constructEntry copy) {
      return ((segment.compareTo(copy.segment) == 0) && (isStart == copy.isStart) && (endHash == copy.endHash) && (startPtr == copy.startPtr) && (nextPtr == copy.nextPtr) && (endPtr == copy.endPtr) && (overlap == copy.overlap) && (length == copy.length));
    }
}
