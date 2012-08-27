package jstamp.ssca2;

import org.deuce.Atomic;
/* =============================================================================
 *
 * getStartLists.java
 *
 * =============================================================================
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

public class GetStartLists {
  Graph GPtr;
  Edge[] maxIntWtListPtr;
  int  maxIntWtListSize;
  Edge[] soughtStrWtListPtr;
  int soughtStrWtListSize;

  int global_maxWeight; 
  int[] global_i_edgeStartCounter;
  int[] global_i_edgeEndCounter;
  Edge[] global_maxIntWtList;
  Edge[] global_soughtStrWtList;

  public GetStartLists() {
    global_maxWeight = 0;
    global_i_edgeStartCounter = null;
    global_i_edgeEndCounter   = null;
    global_maxIntWtList       = null;
    global_soughtStrWtList    = null;
    maxIntWtListSize          = 0;
    soughtStrWtListSize       = 0;
    maxIntWtListPtr           = null;
    soughtStrWtListPtr        = null;
  }

  /* =============================================================================
   * getStartLists
   * =============================================================================
   */
  public static void
    getStartLists (int myId, int numThread, Globals glb, GetStartLists gsl)
    {
      //
      // Find Max Wt on each thread
      //
      int maxWeight = 0;

      int i;

      LocalStartStop lss = new LocalStartStop();


      for (i = lss.i_start; i < lss.i_stop; i++) {
        if (gsl.GPtr.intWeight[i] > maxWeight) {
          maxWeight = gsl.GPtr.intWeight[i];
        }
      }

      
        atomicMethodSeven(gsl, maxWeight);
      

      Barrier.enterBarrier();

      maxWeight = gsl.global_maxWeight;

      //
      // Create partial lists
      //

      //
      // Allocate mem. for temp edge list for each thread
      //
      int numTmpEdge = ( 5 + (int) (Math.ceil(1.5*(gsl.GPtr.numIntEdges)/glb.MAX_INT_WEIGHT)));
      Edge[] tmpEdgeList = new Edge[numTmpEdge];

      int i_edgeCounter = 0;

      for (i = lss.i_start; i < lss.i_stop; i++) {

        if (gsl.GPtr.intWeight[i] == maxWeight) {

          // Find the corresponding endVertex 
          int j;
          for (j = 0; j < gsl.GPtr.numDirectedEdges; j++) {
            if (gsl.GPtr.paralEdgeIndex[j] > i) {
              break;
            }
          }
          tmpEdgeList[i_edgeCounter].endVertex = gsl.GPtr.outVertexList[j-1];
          tmpEdgeList[i_edgeCounter].edgeNum = j-1;

          int t;
          for (t = 0; t < gsl.GPtr.numVertices; t++) {
            if (gsl.GPtr.outVertexIndex[t] > j-1) {
              break;
            }
          }
          tmpEdgeList[i_edgeCounter].startVertex = t-1;

          i_edgeCounter++;

        }
      }

      //
      // Merge partial edge lists
      //

      int[] i_edgeStartCounter;
      int[] i_edgeEndCounter;

      if (myId == 0) {
        i_edgeStartCounter = new int[numThread];
        gsl.global_i_edgeStartCounter = i_edgeStartCounter;
        i_edgeEndCounter = new int[numThread];
        gsl.global_i_edgeEndCounter = i_edgeEndCounter;

        gsl.maxIntWtListSize = 0;

      }

      Barrier.enterBarrier();

      i_edgeStartCounter = gsl.global_i_edgeStartCounter;
      i_edgeEndCounter = gsl.global_i_edgeEndCounter;

      i_edgeEndCounter[myId] = i_edgeCounter;
      i_edgeStartCounter[myId] = 0;

      Barrier.enterBarrier();

      if (myId == 0) {
        for (i = 1; i < numThread; i++) {
          i_edgeEndCounter[i] = i_edgeEndCounter[i-1] + i_edgeEndCounter[i];
          i_edgeStartCounter[i] = i_edgeEndCounter[i-1];
        }
      }

      gsl.maxIntWtListSize += i_edgeCounter; //FIXME should be inside an atomic block

      Barrier.enterBarrier();

      Edge[] maxIntWtList;

      if (myId == 0) {
        gsl.maxIntWtListPtr = null;
        maxIntWtList = new Edge[gsl.maxIntWtListSize];
        gsl.global_maxIntWtList = maxIntWtList;
      }

      Barrier.enterBarrier();

      maxIntWtList = gsl.global_maxIntWtList;

      for (i = i_edgeStartCounter[myId]; i<i_edgeEndCounter[myId]; i++) {
        (maxIntWtList[i]).startVertex = tmpEdgeList[i-i_edgeStartCounter[myId]].startVertex;
        (maxIntWtList[i]).endVertex = tmpEdgeList[i-i_edgeStartCounter[myId]].endVertex;
        (maxIntWtList[i]).edgeNum = tmpEdgeList[i-i_edgeStartCounter[myId]].edgeNum;
      }

      if (myId == 0) {
        gsl.maxIntWtListPtr = maxIntWtList;
      }

      i_edgeCounter = 0;

      CreatePartition.createPartition(0, gsl.GPtr.numStrEdges, myId, numThread, lss);

      for (i = lss.i_start; i < lss.i_stop; i++) {

        //TODO java equivalent of strncmp
/*
        if (strncmp(gsl.GPtr.strWeight+i*glb.MAX_STRLEN,
              SOUGHT_STRING,
              glb.MAX_STRLEN) == 0)
        {
          //
          // Find the corresponding endVertex
          //

          int t;
          for (t = 0; t < gsl.GPtr.numEdges; t++) {
            if (gsl.GPtr.intWeight[t] == -i) {
              break;
            }
          }

          int j;
          for (j = 0; j < gsl.GPtr.numDirectedEdges; j++) {
            if (gsl.GPtr.paralEdgeIndex[j] > t) {
              break;
            }
          }
          tmpEdgeList[i_edgeCounter].endVertex = gsl.GPtr.outVertexList[j-1];
          tmpEdgeList[i_edgeCounter].edgeNum = j-1;

          for (t = 0; t < gsl.GPtr.numVertices; t++) {
            if (gsl.GPtr.outVertexIndex[t] > j-1) {
              break;
            }
          }
          tmpEdgeList[i_edgeCounter].startVertex = t-1;
          i_edgeCounter++;
        }
        */

      }

      Barrier.enterBarrier();

      i_edgeEndCounter[myId] = i_edgeCounter;
      i_edgeStartCounter[myId] = 0;

      if (myId == 0) {
        gsl.soughtStrWtListSize = 0;
      }

      Barrier.enterBarrier();

      if (myId == 0) {
        for (i = 1; i < numThread; i++) {
          i_edgeEndCounter[i] = i_edgeEndCounter[i-1] + i_edgeEndCounter[i];
          i_edgeStartCounter[i] = i_edgeEndCounter[i-1];
        }
      }

      gsl.soughtStrWtListSize += i_edgeCounter; 

      Barrier.enterBarrier();

      Edge[]soughtStrWtList;

      if (myId == 0) {
        gsl.soughtStrWtListPtr = null;
        soughtStrWtList = new Edge[gsl.soughtStrWtListSize];
        gsl.global_soughtStrWtList = soughtStrWtList;
      }

      Barrier.enterBarrier();

      soughtStrWtList = gsl.global_soughtStrWtList;

      for (i = i_edgeStartCounter[myId]; i < i_edgeEndCounter[myId]; i++) {
        (soughtStrWtList[i]).startVertex =
          tmpEdgeList[i-i_edgeStartCounter[myId]].startVertex;
        (soughtStrWtList[i]).endVertex =
          tmpEdgeList[i-i_edgeStartCounter[myId]].endVertex;
        (soughtStrWtList[i]).edgeNum =
          tmpEdgeList[i-i_edgeStartCounter[myId]].edgeNum;
      }

      Barrier.enterBarrier();

      if (myId == 0) {
        gsl.soughtStrWtListPtr = soughtStrWtList;
        i_edgeStartCounter = null;
      }

      tmpEdgeList = null;

    }

  @Atomic
private static void atomicMethodSeven(GetStartLists gsl, int maxWeight) {
	int tmp_maxWeight = gsl.global_maxWeight;
	if (maxWeight > tmp_maxWeight) {
	  gsl.global_maxWeight = maxWeight;
	}
}
}

/* =============================================================================
 *
 * End of getStartLists.java
 *
 * =============================================================================
 */
