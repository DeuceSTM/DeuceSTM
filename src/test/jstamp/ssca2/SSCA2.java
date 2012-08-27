package jstamp.ssca2;


/* =============================================================================
*
* ssca2.java
*
* =============================================================================
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

public class SSCA2 extends Thread {
 /*
  * Tuple for Scalable Data Generation
  * stores startVertex, endVertex, long weight and other info
  */
 GraphSDG SDGdata;

 /**
  * The graph data structure for this benchmark - see defs.h
  **/
 Graph G;

 /**
  *
  */
 ComputeGraph computeGraphArgs;

 /**
  * thread id
  **/
 int threadid;

 /**
  * Total number of threads
  **/
 int numThread;

 /**
  * Global Arguments 
  **/
 Globals glb;


 /**
  *  Gen scalable data
  **/
 GenScalData gsd;

 /**
  **
  **/
 GetStartLists getStartListsArg;


 Alg_Radix_Smp radixsort;

 public SSCA2(int myId, int numThread, Globals glb, ComputeGraph computeGraphArgs, 
     GenScalData gsd, GetStartLists getStartListsArg, Alg_Radix_Smp radixsort) {
   this.threadid = myId;
   this.numThread = numThread;
   this.glb = glb;
   this.computeGraphArgs = computeGraphArgs;
   this.G = computeGraphArgs.GPtr;
   this.SDGdata = computeGraphArgs.SDGdataPtr;
   this.gsd = gsd;
   this. getStartListsArg = getStartListsArg;
   this.radixsort = radixsort;
 }

 public void run() {
	 if (Kernel.USE_PARALLEL_DATA_GENERATION()) {
   /* Generate Scaldata */
   Barrier.enterBarrier();
   GenScalData.genScalData(threadid, numThread, glb, SDGdata, gsd, radixsort);
   Barrier.enterBarrier();
	 }

	 if (Kernel.ENABLE_KERNEL1()) {
   /* Kernel 1 */
   Barrier.enterBarrier();
   ComputeGraph.computeGraph(threadid, numThread, glb, computeGraphArgs);
   Barrier.enterBarrier();
	 }
	 
	 if (Kernel.ENABLE_KERNEL2()) {
   /* Kernel 2 */
   Barrier.enterBarrier();
   GetStartLists.getStartLists(threadid, numThread, glb, getStartListsArg);
   Barrier.enterBarrier();
	 }
 }   

 /* =============================================================================
  * main
  * =============================================================================
  */
 public static void main(String[] args) {
   /*
    * Tuple for Scalable Data Generation
    * stores startVertex, endVertex, long weight and other info
    */
   GraphSDG SDGdata = new GraphSDG();

   /*
    * The graph data structure for this benchmark - see defs.h
    */
   Graph G = new Graph();

   /*
    * The Global arguments
    */
   ComputeGraph computeGraphArgs = new ComputeGraph();
   long starttime, total_starttime;
   long stoptime, total_stoptime;

   computeGraphArgs.GPtr       = G;
   computeGraphArgs.SDGdataPtr = SDGdata;
   /* -------------------------------------------------------------------------
    * Preamble
    * -------------------------------------------------------------------------
    */

   /*
    * User Interface: Configurable parameters, and global program control
    */

   System.out.println("\nHPCS SSCA #2 Graph Analysis Executable Specification:");
   System.out.println("\nRunning...\n\n");

   Globals glb = new Globals();

   GetUserParameters gup = new GetUserParameters(glb);
   gup.getUserParameters(args, glb);

   System.out.println("Number of processors:       " + glb.THREADS);
   System.out.println("Problem Scale:              " + glb.SCALE);
   System.out.println("Max parallel edges:         " + glb.MAX_PARAL_EDGES);
   System.out.println("Percent int weights:        " + glb.PERC_INT_WEIGHTS);
   System.out.println("Probability unidirectional: " + glb.PROB_UNIDIRECTIONAL);
   System.out.println("Probability inter-clique:   " + glb.PROB_INTERCL_EDGES);
   System.out.println("Subgraph edge length:       " + glb.SUBGR_EDGE_LENGTH);
   System.out.println("Kernel 3 data structure:    " + glb.K3_DS);

   /* Initiate Barriers */
   Barrier.setBarrier(glb.THREADS);

   SSCA2[] ssca = new SSCA2[glb.THREADS];
   int nthreads = glb.THREADS;

   GenScalData gsd = new GenScalData();

   Alg_Radix_Smp radixsort = new Alg_Radix_Smp();

   GetStartLists getStartListsArg = new GetStartLists();
   getStartListsArg.GPtr                = G;

   /* Create and Start Threads */
   for(int i = 1; i<nthreads; i++) {
     ssca[i] = new SSCA2(i, nthreads, glb, computeGraphArgs, gsd, getStartListsArg, radixsort);
   }

   for(int i = 1; i<nthreads; i++) {
     ssca[i].start();
   }
   System.out.println("\nScalable Data Generator - genScalData() beginning execution...\n");
   total_starttime=System.currentTimeMillis();
   starttime=System.currentTimeMillis();

if (Kernel.USE_PARALLEL_DATA_GENERATION()) {

   /*
    * Scalable Data Generator
    */
   parallel_work_genScalData(nthreads, glb, SDGdata, gsd, radixsort);

} else {

   GenScalData.genScalData_seq(glb, SDGdata, gsd, radixsort);

}

   stoptime=System.currentTimeMillis();
   System.out.println("\n\tgenScalData() completed execution.");
   System.out.println("Time="+(stoptime-starttime));

if (Kernel.ENABLE_KERNEL1()) {

   /* -------------------------------------------------------------------------
    * Kernel 1 - Graph Construction
    *
    * From the input edges, construct the graph 'G'
    * -------------------------------------------------------------------------
    */
   System.out.println("\nKernel 1 - computeGraph() beginning execution...");
   starttime=System.currentTimeMillis();
   parallel_work_computeGraph(nthreads, glb, computeGraphArgs);
   stoptime=System.currentTimeMillis();
   System.out.println("\n\tcomputeGraph() completed execution.\n");
   System.out.println("TIME="+(stoptime-starttime));
}

if (Kernel.ENABLE_KERNEL2()) {

   /* -------------------------------------------------------------------------
    * Kernel 2 - Find Max weight and sought string
    * -------------------------------------------------------------------------
    */

   getStartListsArg.GPtr                = G;
   getStartListsArg.maxIntWtListPtr     = null;
   getStartListsArg.maxIntWtListSize    = 0;
   getStartListsArg.soughtStrWtListPtr  = null;
   getStartListsArg.soughtStrWtListSize = 0;

   System.out.println("\nKernel 2 - getStartLists() beginning execution...\n");
   parallel_work_getStartLists(nthreads, glb, getStartListsArg);
   System.out.println("\n\tgetStartLists() completed execution.\n");

} // ENABLE_KERNEL2 

total_stoptime=System.currentTimeMillis();
System.out.println("TOTAL TIME="+(total_stoptime-total_starttime));


if (Kernel.ENABLE_KERNEL3()) {
	if (!Kernel.ENABLE_KERNEL2()) {
		throw new RuntimeException("KERNEL3 requires KERNEL2");
	}
}

if (Kernel.ENABLE_KERNEL3()) {

   /* -------------------------------------------------------------------------
    * Kernel 3 - Graph Extraction
    * -------------------------------------------------------------------------
    */
   VList[] intWtVList = null;
   VList[] strWtVList = null;

   System.out.println("\nKernel 3 - FindSubGraphs() beginning execution...\n");

   if (glb.K3_DS == 0) {
     /* TODO Add files for KERNEL 3
     intWtVList = new VList[G.numVertices * getStartListsArg.maxIntWtListSize];
     strWtVList = new VList[G.numVertices * getStartListsArg.soughtStrWtListSize];

     FindSubGraphs0_arg_t findSubGraphs0Arg;
     findSubGraphs0Arg.GPtr                = G;
     findSubGraphs0Arg.intWtVList          = intWtVList;
     findSubGraphs0Arg.strWtVList          = strWtVList;
     findSubGraphs0Arg.maxIntWtList        = getStartListsArg.maxIntWtList;
     findSubGraphs0Arg.maxIntWtListSize    = getStartListsArg.maxIntWtListSize;
     findSubGraphs0Arg.soughtStrWtList     = getStartListsArg.soughtStrWtList;
     findSubGraphs0Arg.soughtStrWtListSize = getStartListsArg.soughtStrWtListSize;

     parallel_work_FindSubGraphs0(findSubGraphs0Arg);
     */

   } else if (glb.K3_DS == 1) {

     /* TODO Add files for KERNEL 3
     intWtVList = new VL[getStartListsArg.maxIntWtListSize];
     strWtVList = new VL[getStartListsArg.soughtStrWtListSize];

     FindSubGraphs1_arg_t findSubGraphs1Arg;
     findSubGraphs1Arg.GPtr                = G;
     findSubGraphs1Arg.intWtVLList         = intWtVLList;
     findSubGraphs1Arg.strWtVLList         = strWtVLList;
     findSubGraphs1Arg.maxIntWtList        = maxIntWtList;
     findSubGraphs1Arg.maxIntWtListSize    = maxIntWtListSize;
     findSubGraphs1Arg.soughtStrWtList     = soughtStrWtList;
     findSubGraphs1Arg.soughtStrWtListSize = soughtStrWtListSize;

     parallel_work_FindSubGraphs1(findSubGraphs1Arg);
     */

   } else if (glb.K3_DS == 2) {

     /* TODO Add files for KERNEL 3
     intWtVList = new VL[getStartListsArg.maxIntWtListSize];
     strWtVList = new VL[getStartListsArg.soughtStrWtListSize];

     FindSubGraphs2_arg_t findSubGraphs2Arg;
     findSubGraphs2Arg.GPtr                = G;
     findSubGraphs2Arg.intWtVDList         = intWtVDList;
     findSubGraphs2Arg.strWtVDList         = strWtVDList;
     findSubGraphs2Arg.maxIntWtList        = maxIntWtList;
     findSubGraphs2Arg.maxIntWtListSize    = maxIntWtListSize;
     findSubGraphs2Arg.soughtStrWtList     = soughtStrWtList;
     findSubGraphs2Arg.soughtStrWtListSize = soughtStrWtListSize;

     parallel_work_FindSubGraphs2(findSubGraphs2Arg);
     */

   } else {
     ;
   }

   System.out.println("\n\tFindSubGraphs() completed execution.\n");

} /* ENABLE_KERNEL3 */

if (Kernel.ENABLE_KERNEL4()) {

   /* -------------------------------------------------------------------------
    * Kernel 4 - Graph Clustering
    * -------------------------------------------------------------------------
    */

   System.out.println("\nKernel 4 - cutClusters() beginning execution...\n");
   parallel_work_cutClusters(G);
   System.out.println("\n\tcutClusters() completed execution.\n");

} /* ENABLE_KERNEL4 */

//   System.exit(0);
 }

 /**
  * Work done by primary thread in parallel with other threads
  **/

//if (Kernel.USE_PARALLEL_DATA_GENERATION()()) {

 public static void parallel_work_genScalData(int numThread, Globals glb, GraphSDG SDGdata, GenScalData gsd, Alg_Radix_Smp radixsort) {
   Barrier.enterBarrier();
   GenScalData.genScalData(0, numThread, glb, SDGdata, gsd, radixsort); // threadId = 0 because primary thread
   Barrier.enterBarrier();
 }

//}

//if (Kernel.ENABLE_KERNEL1) {

 public static void parallel_work_computeGraph(int numThread, Globals glb, ComputeGraph computeGraphArgs) {
   Barrier.enterBarrier();
   ComputeGraph.computeGraph(0, numThread, glb, computeGraphArgs);
   Barrier.enterBarrier();
 }

//}

//if (Kernel.ENABLE_KERNEL2) {

 public static void parallel_work_getStartLists(int numThread, Globals glb, GetStartLists getStartListsArg) {
   Barrier.enterBarrier();
   GetStartLists.getStartLists(0, numThread, glb, getStartListsArg);
   Barrier.enterBarrier();
 }

//}

//if (Kernel.ENABLE_KERNEL3) {

 /*TODO add files for KERNEL 3
 public static void parallel_work_FindSubGraphs0(FindSubGraphs0_arg_t findSubGraphs0Arg) {
   Barrier.enterBarrier();
   Barrier.enterBarrier();
 }

 public static void parallel_work_FindSubGraphs1(FindSubGraphs1_arg_t findSubGraphs1Arg) {
   Barrier.enterBarrier();
   Barrier.enterBarrier();
 }

 public static void parallel_work_FindSubGraphs2(FindSubGraphs2_arg_t findSubGraphs2Arg) {
   Barrier.enterBarrier();
   Barrier.enterBarrier();
 }
 */

//}

//if (Kernel.ENABLE_KERNEL4) {

 public static void parallel_work_cutClusters(Graph G) {
   Barrier.enterBarrier();
   Barrier.enterBarrier();
 }

//}

}

/* =============================================================================
*
* End of ssca2.java
*
* =============================================================================
*/
