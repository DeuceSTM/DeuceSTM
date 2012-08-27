package jstamp.Labyrinth3D;

import org.deuce.Atomic;

public class SmallTest {
  public SmallTest() {}

  public static void main(String qweqwe[]) {
      atomicMethodMain();
  }

  @Atomic
private static void atomicMethodMain() {
	Vector_t pointVectorPtr = Vector_t.vector_alloc(1);
      int i=3;
      while(true) {
	pointVectorPtr.vector_pushBack(new Integer(i));
	if (i==5) {
	  break;
	}
      }
      int n = pointVectorPtr.vector_getSize();
      
      for(i = 0; i < n; i++) {
	int gridPointIndex = ((Integer)(pointVectorPtr.vector_at(i))).intValue();
      }
}
}
