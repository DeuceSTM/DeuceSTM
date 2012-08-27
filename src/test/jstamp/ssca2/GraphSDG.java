package jstamp.ssca2;
public class GraphSDG {
  public int[] startVertex;
  public int[] endVertex;
  public int[] intWeight;

  /* The idea is to store the index of the string weights (as a negative value)
   * in the int Weight array. A negative value because we need to sort on
   * the intWeights in Kernel 2. Hence the int int
   */
  public byte[] strWeight;
  public int numEdgesPlaced;

  public GraphSDG() {

  }
}
