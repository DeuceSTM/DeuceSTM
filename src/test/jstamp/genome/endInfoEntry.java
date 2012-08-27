package jstamp.genome;

  public class endInfoEntry {
      boolean isEnd;
      int jumpToNext;
      
      public endInfoEntry() {
        isEnd = false;
        jumpToNext = 0;
      }
      public endInfoEntry(boolean myEnd, int myNext) {
        isEnd = myEnd;
        jumpToNext = myNext;
      }
  }
