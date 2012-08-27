package jstamp.ssca2;
/* =============================================================================
 *
 * getUserParameters.java
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
 * Port to Java version
 * Alokika Dash
 * University of California, Irvine
 *
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
public class GetUserParameters {

  public GetUserParameters(Globals glb) {
    /*
     * Scalable Data Generator parameters - defaults
     */
    glb.THREADS             = 1;
    glb.SCALE               = 20;              /* binary scaling heuristic */
    glb.MAX_PARAL_EDGES     = 3;               /* between vertices. */
    glb.PERC_INT_WEIGHTS    = (float) 0.6f;             /* % int (vs. string) edge weights */
    glb.PROB_UNIDIRECTIONAL = (float) 0.1f;
    glb.PROB_INTERCL_EDGES  = (float) 0.5f;             /* Init probability link between cliques */

    glb.SUBGR_EDGE_LENGTH   = 3;               /* Kernel 3: max. path length,       */
                                               /* measured by num edges in subgraph */
                                               /* generated from the end Vertex of  */
                                               /* SI and SC lists                   */
    /*
     * Some implementation-specific vars, nothing to do with the specs
     */

    glb.K3_DS               = 2;               /* 0 - Array         */
                                               /* 1 - Linked List   */
                                               /* 2 - Dynamic Array */
  }

  /* =============================================================================
   * displayUsage
   * =============================================================================
   */
  public static void
    displayUsage ()
    {
      System.out.println("Usage: ./SSCA.bin [options]");
      System.out.println("    i <float>    Probability [i]nter-clique      ");
      System.out.println("    k <int>   [k]ind: 0=array 1=list 2=vector ");
      System.out.println("    l <int>   Max path [l]ength               ");
      System.out.println("    p <int>   Max [p]arallel edges            ");
      System.out.println("    s <int>   Problem [s]cale                 ");
      System.out.println("    t <int>   Number of [t]hreads             ");
      System.out.println("    u <float>    Probability [u]nidirectional    ");
      System.out.println("    w <float>    Fraction integer [w]eights      ");
      System.exit(-1);
    }


  /* =============================================================================
   * parseArgs
   * =============================================================================
   */
  public void
    parseArgs(String[] args, Globals glb)
    {
      int i = 0;
      String arg;
      while(i < args.length && args[i].startsWith("-")) {
        arg = args[i++];
        //check options
        if(arg.equals("-i")) {
          if(i < args.length) {
            glb.PROB_INTERCL_EDGES = new Integer(args[i++]).floatValue();
          }
        } else if(arg.equals("-k")) {
          if(i < args.length) {
            glb.K3_DS = new Integer(args[i++]).intValue();
          }
          if(!(glb.K3_DS >=0 && glb.K3_DS <=2)) {
            System.out.println("Input a valid number for -k option between >=0 and <= 2");
            System.exit(0);
          }
        } else if(arg.equals("-l")) {
          if(i < args.length) {
            glb.SUBGR_EDGE_LENGTH = new Integer(args[i++]).intValue();
          }
        } else if(arg.equals("-p")) {
          if(i < args.length) {
            glb.MAX_PARAL_EDGES = new Integer(args[i++]).intValue();
          }
        } else if(arg.equals("-s")) {
          if(i < args.length) {
            glb.SCALE = new Integer(args[i++]).intValue();
          }
        } else if(arg.equals("-t")) {
          if(i < args.length) {
            glb.THREADS = new Integer(args[i++]).intValue();
          }
        } else if(arg.equals("-u")) {
          if(i < args.length) {
            glb.PROB_UNIDIRECTIONAL = new Integer(args[i++]).floatValue();
          }
        } else if(arg.equals("-w")) {
          if(i < args.length) {
            glb.PERC_INT_WEIGHTS = new Integer(args[i++]).floatValue();
          }
        } else if(arg.equals("-h")) {
          displayUsage();
        }
      }

      if(glb.THREADS == 0) {
        System.out.println("Num processors cannot be Zero\n");
        displayUsage();
      }

      if((glb.THREADS  & (glb.THREADS -1)) != 0) {
        System.out.println("Number of [t]hreads must be power of 2\n");
        displayUsage();
      }
    }

  /* =============================================================================
   * getUserParameters
   * =============================================================================
   */
  public void
    getUserParameters (String[] argv, Globals glb)
    {
      /*
       * Scalable Data Generator parameters - defaults
       */

      glb.THREADS             = 2;
      glb.SCALE               = 20;              /* binary scaling heuristic */
      glb.MAX_PARAL_EDGES     = 3;               /* between vertices. */
      glb.PERC_INT_WEIGHTS    = (float)0.6;             /* % int (vs. string) edge weights */
      glb.PROB_UNIDIRECTIONAL = (float)1.0;//0.1;
      glb.PROB_INTERCL_EDGES  = (float)1.0;//0.5;             /* Init probability link between cliques */

      glb.SUBGR_EDGE_LENGTH   = 3;               /* Kernel 3: max. path length,       */
      /* measured by num edges in subgraph */
      /* generated from the end Vertex of  */
      /* SI and SC lists                   */

      /*
       * Some implementation-specific vars, nothing to do with the specs
       */

      glb.K3_DS               = 2;               /* 0 - Array         */
      /* 1 - Linked List   */
      /* 2 - Dynamic Array */

      parseArgs(argv, glb); /* overrides default values set above */


      glb.TOT_VERTICES        = (1<<glb.SCALE);
      glb.MAX_CLIQUE_SIZE     = (1<<(glb.SCALE/3));
      glb.MAX_INT_WEIGHT      = (1<<glb.SCALE);      /* Max int value in edge weight */
      glb.MAX_STRLEN          = glb.SCALE;

      glb.SOUGHT_STRING       = new byte[1];              /* Kernel 2: Character string sought:  */
      /* specify here, else it is picked     */
      /* picked from randomly selected entry */
      /* in genScalData.c                    */

      glb.MAX_CLUSTER_SIZE    = glb.MAX_CLIQUE_SIZE; /* Kernel 4: Clustering search box size */
    }
}
/* =============================================================================
 *
 * End of getUserParameters.java
 *
 * =============================================================================
 */
