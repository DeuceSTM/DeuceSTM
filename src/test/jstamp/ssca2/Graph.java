package jstamp.ssca2;
/* Graph data structure*/
public class Graph {
  public int numVertices;
  public int numEdges;

  public int numDirectedEdges;
  public int numUndirectedEdges;

  public int numIntEdges;
  public int numStrEdges;

  public int[] outDegree;
  public int[] outVertexIndex;
  public int[] outVertexList;
  public int[] paralEdgeIndex;

  public int[] inDegree;
  public int[] inVertexIndex;
  public int[] inVertexList;

  public int[]  intWeight;
  public byte[] strWeight;

  public Graph() {

  }
}
