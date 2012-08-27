package jstamp.ssca2;

import org.deuce.Atomic;
/* =============================================================================
 *
 * computeGraph.java
 *
 * =============================================================================
 *
 * For the license of ssca2, please see ssca2/COPYRIGHT
 * 
 * ------------------------------------------------------------------------
 * 
 * Unless otherwise noted, the following license applies to STAMP files:
 * 
 * Copyright (c) 2007, Stanford University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 * 
 *     * Neither the name of Stanford University nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY STANFORD UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL STANFORD UNIVERSITY BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * =============================================================================
 */

public class ComputeGraph {
  public Graph GPtr;
  public GraphSDG SDGdataPtr;

  public int[] global_p;
  public int global_maxNumVertices;
  public int global_outVertexListSize;
  public int[][] global_impliedEdgeList;
  public int[][] global_auxArr;

  public ComputeGraph() {
    global_p                 = null;
    global_maxNumVertices    = 0;
    global_outVertexListSize = 0;
    global_impliedEdgeList   = null;
    global_auxArr            = null;
  }

  public int NOSHARE(int x) {
    x = x << 7;
    return x;
  }

  /* =============================================================================
   * prefix_sums
   * =============================================================================
   */
  public void
    prefix_sums (int myId, int numThread, int[] result, int[] input, int arraySize)
    {
      int[]  p;
      if (myId == 0) {
        p = new int[NOSHARE(numThread)];
        global_p = p;
      }

      Barrier.enterBarrier();

      p = global_p;

      int start;
      int end;

      int r = arraySize / numThread;
      start = myId * r + 1;
      end = (myId + 1) * r;
      if (myId == (numThread - 1)) {
        end = arraySize;
      }

      for (int j =  start; j <  end; j++) {
        result[j] = input[j-1] + result[j-1];
      }

      p[NOSHARE(myId)] = result[end-1];

      Barrier.enterBarrier();

      if (myId == 0) {
        for (int j = 1; j < numThread; j++) {
          p[NOSHARE(j)] += p[NOSHARE(j-1)];
        }
      }

      Barrier.enterBarrier();

      if (myId > 0) {
        int add_value = p[NOSHARE(myId-1)];
        for (int j = start-1; j < end; j++) {
          result[j] += add_value;
        }
      }

      Barrier.enterBarrier();
    }

  public void
    prefix_sumsin (int myId, int numThread, int[] result, int[] input, int arraySize)
    {
      int[]  p;
      if (myId == 0) {
        p = new int[NOSHARE(numThread)];
        global_p = p;
      }

      Barrier.enterBarrier();

      p = global_p;

      int start;
      int end;

      int r = arraySize / numThread;
      start = myId * r + 1;
      end = (myId + 1) * r;
      if (myId == (numThread - 1)) {
        end = arraySize;
      }

      for (int j =  start; j <  end; j++) {
        result[j] = input[j-1] + result[j-1];
      }

      p[NOSHARE(myId)] = result[end-1];

      Barrier.enterBarrier();

      if (myId == 0) {
        for (int j = 1; j < numThread; j++) {
          p[NOSHARE(j)] += p[NOSHARE(j-1)];
        }
      }

      Barrier.enterBarrier();

      if (myId > 0) {
        int add_value = p[NOSHARE(myId-1)];
        for (int j = start-1; j < end; j++) {
          result[j] += add_value;
        }
      }

      Barrier.enterBarrier();
    }

  /* =============================================================================
   * computeGraph
   * =============================================================================
   */
  public static void
    computeGraph (int myId, int numThread, Globals glb, ComputeGraph computeGraphArgs) {
    
      int maxNumVertices = 0;
      int numEdgesPlaced = computeGraphArgs.SDGdataPtr.numEdgesPlaced;
      Graph GPtr=computeGraphArgs.GPtr;
      GraphSDG SDGdataPtr=computeGraphArgs.SDGdataPtr;
      int MAX_CLUSTER_SIZE=glb.MAX_CLUSTER_SIZE;

      /*
       * First determine the number of vertices by scanning the tuple
       * startVertex list
       */
      LocalStartStop lss = new LocalStartStop();
      CreatePartition.createPartition(0, numEdgesPlaced, myId, numThread, lss);
      for (int i = lss.i_start; i < lss.i_stop; i++) {
        if (SDGdataPtr.startVertex[i] > maxNumVertices) {
          maxNumVertices = SDGdataPtr.startVertex[i];
        }
      }
      
        atomicMethodOne(computeGraphArgs, maxNumVertices);

      Barrier.enterBarrier();

      maxNumVertices = computeGraphArgs.global_maxNumVertices;

      if (myId == 0) {
        //FIXME temp fix for array sizes equal to pow(2, glb.SCALE)
        {
        int realMaxNumVertices = (int) (Math.pow(2, glb.SCALE));
        if(maxNumVertices == realMaxNumVertices);
          maxNumVertices++;
        }
	GPtr.numVertices = maxNumVertices;
	GPtr.numEdges    = numEdgesPlaced;
	GPtr.intWeight   = SDGdataPtr.intWeight;
	GPtr.strWeight   = SDGdataPtr.strWeight;

        for (int i = 0; i < numEdgesPlaced; i++) {
          if (GPtr.intWeight[numEdgesPlaced-i-1] < 0) {
	    GPtr.numStrEdges = -(GPtr.intWeight[numEdgesPlaced-i-1]) + 1;
	    GPtr.numIntEdges = numEdgesPlaced - GPtr.numStrEdges;
            break;
          }
        }

	GPtr.outDegree = new int[GPtr.numVertices];
	GPtr.outVertexIndex = new int[GPtr.numVertices];
      }

      Barrier.enterBarrier();

      CreatePartition.createPartition(0, GPtr.numVertices, myId, numThread, lss);

      for (int i = lss.i_start; i < lss.i_stop; i++) {
	GPtr.outDegree[i] = 0;
	GPtr.outVertexIndex[i] = 0;
      }

      int outVertexListSize = 0;

      Barrier.enterBarrier();

      int i0 = -1;
      for (int i = lss.i_start; i < lss.i_stop; i++) {
        int k = i;
        if ((outVertexListSize == 0) && (k != 0)) {
          while (i0 == -1) {
            for (int j = 0; j < numEdgesPlaced; j++) {
              if (k == SDGdataPtr.startVertex[j]) {
                i0 = j;
                break;
              }

            }
            k--;
          }
        }

        if ((outVertexListSize == 0) && (k == 0)) {
          i0 = 0;
        }

        for (int j = i0; j < numEdgesPlaced; j++) {
          if (i == GPtr.numVertices-1) {
            break;
          }
          if ((i != SDGdataPtr.startVertex[j])) {
            if ((j > 0) && (i == SDGdataPtr.startVertex[j-1])) {
              if (j-i0 >= 1) {
                outVertexListSize++;
		GPtr.outDegree[i]++;
                for (int t = (i0+1); t < j; t++) {
                  if (SDGdataPtr.endVertex[t] !=
		      SDGdataPtr.endVertex[t-1])
                  {
                    outVertexListSize++;
		    GPtr.outDegree[i] = GPtr.outDegree[i]+1;
                  }
                }
              }
            }
            i0 = j;
            break;
          }
        }

        if (i == GPtr.numVertices-1) {
          if (numEdgesPlaced-i0 >= 0) {
            outVertexListSize++;
	    GPtr.outDegree[i]++;
            for (int t =  (i0+1); t < numEdgesPlaced; t++) {
              if (SDGdataPtr.endVertex[t] != SDGdataPtr.endVertex[t-1]) {
                outVertexListSize++;
		GPtr.outDegree[i]++;
              }
            }
          }
        }

      } /* for i */

      Barrier.enterBarrier();

      computeGraphArgs.prefix_sums(myId, numThread, GPtr.outVertexIndex, GPtr.outDegree, GPtr.numVertices);

      Barrier.enterBarrier();

      
        atomicMethodTwo(computeGraphArgs, outVertexListSize);
      

      Barrier.enterBarrier();


      outVertexListSize = computeGraphArgs.global_outVertexListSize;


      if (myId == 0) {
	GPtr.numDirectedEdges = outVertexListSize;
	GPtr.outVertexList = new int[outVertexListSize];
	GPtr.paralEdgeIndex = new int[outVertexListSize];
	GPtr.outVertexList[0] = SDGdataPtr.endVertex[0];
      }

      Barrier.enterBarrier();

      /*
       * Evaluate outVertexList
       */

      i0 = -1;
      for (int i = lss.i_start; i < lss.i_stop; i++) {

        int k =  i;
        while ((i0 == -1) && (k != 0)) {
          for (int j = 0; j < numEdgesPlaced; j++) {
            if (k == SDGdataPtr.startVertex[j]) {
              i0 = j;
              break;
            }
          }
          k--;
        }

        if ((i0 == -1) && (k == 0)) {
          i0 = 0;
        }

        for (int j = i0; j < numEdgesPlaced; j++) {
          if (i == GPtr.numVertices-1) {
            break;
          }
          if (i != SDGdataPtr.startVertex[j]) {
            if ((j > 0) && (i == SDGdataPtr.startVertex[j-1])) {
              if (j-i0 >= 1) {
                int ii =  (GPtr.outVertexIndex[i]);
                int r = 0;
		GPtr.paralEdgeIndex[ii] = i0;
		GPtr.outVertexList[ii] = SDGdataPtr.endVertex[i0];
                r++;
                for (int t =  (i0+1); t < j; t++) {
                  if (SDGdataPtr.endVertex[t] !=
		      SDGdataPtr.endVertex[t-1])
                  {
		    GPtr.paralEdgeIndex[ii+r] = t;
		    GPtr.outVertexList[ii+r] = SDGdataPtr.endVertex[t];
                    r++;
                  }
                }

              }
            }
            i0 = j;
            break;
          }
        } /* for j */

        if (i == GPtr.numVertices-1) {
          int r = 0;
          if (numEdgesPlaced-i0 >= 0) {
            int ii = GPtr.outVertexIndex[i];
	    GPtr.paralEdgeIndex[ii+r] = i0;
	    GPtr.outVertexList[ii+r] = SDGdataPtr.endVertex[i0];
            r++;
            for (int t = i0+1; t < numEdgesPlaced; t++) {
              if (SDGdataPtr.endVertex[t] != SDGdataPtr.endVertex[t-1]) {
		GPtr.paralEdgeIndex[ii+r] = t;
		GPtr.outVertexList[ii+r] = SDGdataPtr.endVertex[t];
                r++;
              }
            }
          }
        }

      } /* for i */


      Barrier.enterBarrier();

      if (myId == 0) {
	SDGdataPtr.startVertex = null;
	SDGdataPtr.endVertex = null;
	GPtr.inDegree = new int[GPtr.numVertices];
	GPtr.inVertexIndex = new int[GPtr.numVertices];
      }

      Barrier.enterBarrier();

      //No need to zero memory, this is Java
      //for (int i = lss.i_start; i < lss.i_stop; i++) {
      //	GPtr.inDegree[i] = 0;
      //	GPtr.inVertexIndex[i] = 0;
      //}

      /* A temp. array to store the inplied edges */
      int[][] impliedEdgeList;
      
      if (myId == 0) {
        impliedEdgeList = new int[GPtr.numVertices][MAX_CLUSTER_SIZE];
        computeGraphArgs.global_impliedEdgeList = impliedEdgeList;
      }

      Barrier.enterBarrier();

      impliedEdgeList = computeGraphArgs.global_impliedEdgeList;

      CreatePartition.createPartition(0,
          GPtr.numVertices,
          myId,
          numThread,
          lss);
      
      //No need to zero memory, this is Java!
      //      for (int i = lss.i_start; i < lss.i_stop; i++) {
      //	for (int j=0;j<MAX_CLUSTER_SIZE;j++) {
      //	  impliedEdgeList[i][j] = 0;
      //	}
      //}

      CreatePartition.createPartition(0,
          (GPtr.numVertices * MAX_CLUSTER_SIZE),
          myId,
          numThread,
          lss);

      /*
       * An auxiliary array to store implied edges, in case we overshoot
       * MAX_CLUSTER_SIZE
       */

      int[][] auxArr;
      if (myId == 0) {
        auxArr = new int[GPtr.numVertices][];
        computeGraphArgs.global_auxArr = auxArr;
      }

      Barrier.enterBarrier();

      auxArr = computeGraphArgs.global_auxArr;

      CreatePartition.createPartition(0, GPtr.numVertices, myId, numThread, lss);
      for (int i = lss.i_start; i < lss.i_stop; i++) {
        /* Inspect adjacency list of vertex i */
	int jend=GPtr.outVertexIndex[i] + GPtr.outDegree[i];
        for (int j = GPtr.outVertexIndex[i];j < jend;j++) {
          int v =  (GPtr.outVertexList[j]);
          int k;
	  int kend=GPtr.outVertexIndex[v]+GPtr.outDegree[v];

          for (k = GPtr.outVertexIndex[v];k < kend;k++) {
            if (GPtr.outVertexList[k] == i) {
              break;
            }
          }
          if (k == kend) {
	    
              atomicMethodThree(GPtr, MAX_CLUSTER_SIZE, impliedEdgeList, auxArr,
					i, v);
	    
          }
        }
      } /* for i */
      Barrier.enterBarrier();

      computeGraphArgs.prefix_sumsin(myId, numThread, GPtr.inVertexIndex, GPtr.inDegree, GPtr.numVertices);

      if (myId == 0) {
	GPtr.numUndirectedEdges = GPtr.inVertexIndex[GPtr.numVertices-1]
          + GPtr.inDegree[GPtr.numVertices-1];
	GPtr.inVertexList = new int[GPtr.numUndirectedEdges];
      }

      Barrier.enterBarrier();

      /*
       * Create the inVertex List
       */
      for (int i = lss.i_start; i < lss.i_stop; i++) {
        for (int j = GPtr.inVertexIndex[i];
            j < (GPtr.inVertexIndex[i] + GPtr.inDegree[i]);
            j++)
        {
          if ((j - GPtr.inVertexIndex[i]) < MAX_CLUSTER_SIZE) {
            GPtr.inVertexList[j] =
              impliedEdgeList[i][j-GPtr.inVertexIndex[i]];
          } else {
	    GPtr.inVertexList[j] =
	      (auxArr[i])[(j-GPtr.inVertexIndex[i]) % MAX_CLUSTER_SIZE];
          }
        }
      }

      Barrier.enterBarrier();

      for (int i = lss.i_start; i < lss.i_stop; i++) {
        if (GPtr.inDegree[i] > MAX_CLUSTER_SIZE) {
          auxArr[i] = null;
        }
      }

      Barrier.enterBarrier();

    }

  @Atomic
private static void atomicMethodThree(Graph GPtr, int MAX_CLUSTER_SIZE,
		int[][] impliedEdgeList, int[][] auxArr, int i, int v) {
	/* Add i to the impliedEdgeList of v */
	  int inDegree = GPtr.inDegree[v];
     GPtr.inDegree[v] =  (inDegree + 1);
	  if ( inDegree < MAX_CLUSTER_SIZE) {
	    impliedEdgeList[v][inDegree] = i;
	  } else {
	    /* Use auxiliary array to store the implied edge */
	    /* Create an array if it's not present already */
int a[];
if ((inDegree % MAX_CLUSTER_SIZE) == 0) {
  a = new int[MAX_CLUSTER_SIZE];
  auxArr[v] = a;
	    } else {
  a = auxArr[v];
	    }
	    a[inDegree % MAX_CLUSTER_SIZE] = i;
	  }
}

  @Atomic
private static void atomicMethodTwo(ComputeGraph computeGraphArgs,
		int outVertexListSize) {
	computeGraphArgs.global_outVertexListSize = computeGraphArgs.global_outVertexListSize + outVertexListSize;
}

  @Atomic
private static void atomicMethodOne(ComputeGraph computeGraphArgs,
		int maxNumVertices) {
	int tmp_maxNumVertices = computeGraphArgs.global_maxNumVertices;
	int new_maxNumVertices = ((CreatePartition.MAX(tmp_maxNumVertices, maxNumVertices)) + 1);
	computeGraphArgs.global_maxNumVertices = new_maxNumVertices;
}
}

/* =============================================================================
 *
 * End of computeGraph.java
 *
 * =============================================================================
 */
