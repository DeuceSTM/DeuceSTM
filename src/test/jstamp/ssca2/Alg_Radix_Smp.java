package jstamp.ssca2;
/* =============================================================================
 *
 * alg_radix_smp.java
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

public class Alg_Radix_Smp {
  public int[] global_myHisto;
  public int[] global_psHisto;
  public int[] global_lTemp;
  public int[] global_lTemp2;

  public Alg_Radix_Smp() {
    global_myHisto = null;
    global_psHisto = null;
    global_lTemp   = null;
    global_lTemp2  = null;
  }

  public static int BITS(int x, int k, int j) {
    return  ((x>>k) & ~(~0<<j));
  }

  /* =============================================================================
   * all_countsort_node
   *
   * R (range)      must be a multiple of NODES
   * q (elems/proc) must be a multiple of NODES
   * =============================================================================
   */
  /*
  public void
    all_countsort_node (
        int q,
        int[] lKey,
        int[] lSorted,
        int R,
        int bitOff,
        int m)
    {
      int[] myHisto = null;
      int[] psHisto = null;

      if (myId == 0) {
        myHisto = new int[numThread*R];
        global_myHisto = myHisto;
        psHisto = new int[numThread*R];
        global_psHisto = psHisto;
      }

      Barrier.enterBarrier();

      myHisto = global_myHisto;
      psHisto = global_psHisto;

      int index = myId * R;

      for (int k =  index; k < index+R; k++) {
        myHisto[k] = 0;
      }

      LocalStartStop lss = new LocalStartStop();
      CreatePartition.createPartition(0, q, myId, numThread, lss);

      for (int k = lss.i_start; k < lss.i_stop; k++) {
        myHisto[(myId * R) + BITS(lKey[k],bitOff,m)]++;
      }

      Barrier.enterBarrier();

      CreatePartition.createPartition(0, R, myId, numThread, lss);

      int last;
      for (int k = lss.i_start; k < lss.i_stop; k++) {
        last = psHisto[k] = myHisto[k];
        for (int j = 1; j < numThread; j++) {
          int temp = psHisto[(j*R) + k] = last + myHisto[(j*R) + k];
          last = temp;
        }
      }

      Barrier.enterBarrier();

      int offset = 0;

      for (int k = 0; k < R; k++) {
        myHisto[(myId * R) + k] = (psHisto[(myId * R) + k] - myHisto[(myId * R) + k]) + offset;
        offset += psHisto[((numThread - 1) * R) + k];
      }

      Barrier.enterBarrier();

      CreatePartition.createPartition(0, q, myId, numThread, lss);

      for (int k = lss.i_start; k < lss.i_stop; k++) {
        int j = BITS(lKey[k],bitOff,m);
        lSorted[myHisto[(myId * R) + j]] = lKey[k];
        myHisto[(myId * R) + j]++;
      }

      Barrier.enterBarrier();

      if (myId == 0) {
        psHisto = null;
        myHisto = null;
      }
    }
*/


  /* =============================================================================
   * all_countsort_node_aux_seq
   *
   * R (range)      must be a multiple of NODES
   * q (elems/proc) must be a multiple of NODES
   * =============================================================================
   */
  public void
    all_countsort_node_aux_seq (int q,
        int[] lKey,
        int[] lSorted,
        int[] auxKey,
        int[] auxSorted,
        int R,
        int bitOff,
        int m)
    {
      int[] myHisto = new int[ R];
      int[] psHisto = new int[ R];
      
      for (int k = 0; k < R; k++) {
        myHisto[k] = 0;
      }

      for (int k = 0; k < q; k++) {
        myHisto[BITS(lKey[k],bitOff,m)]++;
      }

      int last;
      for (int k = 0; k < R; k++) {
        last = psHisto[k] = myHisto[k];
      }

      int offset = 0;

      for (int k = 0; k < R; k++) {
        myHisto[k] = (psHisto[k] - myHisto[k]) + offset;
        offset += psHisto[k];
      }

      for (int k = 0; k <  q; k++) {
        int j = BITS(lKey[k], bitOff, m);
        lSorted[myHisto[j]] = lKey[k];
        auxSorted[myHisto[j]] = auxKey[k];
        myHisto[j]++;

        //
        //lSorted[mhp[j]] = lKey[k];
        //auxSorted[mhp[j]] = auxKey[k];
        //mhp[j]++;
        
      }
    }


  /* =============================================================================
   * all_countsort_node_aux
   *
   * R (range)      must be a multiple of NODES
   * q (elems/proc) must be a multiple of NODES
   * =============================================================================
   */
  public void
    all_countsort_node_aux (int myId,
        int numThread,
        int q,
        int[] lKey,
        int[] lSorted,
        int[] auxKey,
        int[] auxSorted,
        int R,
        int bitOff,
        int m,
        Alg_Radix_Smp rdxsort)
    {
      int[] myHisto = null;
      int[] psHisto = null;

      if (myId == 0) {
        myHisto = new int[numThread * R];
        rdxsort.global_myHisto = myHisto;
        psHisto = new int[numThread * R];
        rdxsort.global_psHisto = psHisto;
      }

      Barrier.enterBarrier();

      myHisto = rdxsort.global_myHisto;
      psHisto = rdxsort.global_psHisto;

      for (int k = 0; k <  R; k++) {
        myHisto[((myId*R) + k)] = 0;
      }

      LocalStartStop lss = new LocalStartStop();
      CreatePartition.createPartition(0, q, myId, numThread, lss);

      for (int k = lss.i_start; k < lss.i_stop; k++) {
        myHisto[(myId*R) + BITS(lKey[k],bitOff,m)]++;
      }

      Barrier.enterBarrier();

      CreatePartition.createPartition(0, R, myId, numThread, lss);

      int last;
      for (int k = lss.i_start; k < lss.i_stop; k++) {
        last = psHisto[k] = myHisto[k];
        for (int j = 1; j < numThread; j++) {
          int temp = psHisto[(j*R + k)] = last + myHisto[ (j*R + k)];
          last = temp;
        }
      }

      Barrier.enterBarrier();

      int offset = 0;

      for (int k = 0; k < R; k++) {
        myHisto[(myId*R)+k] = (psHisto[(myId*R) + k] - myHisto[(myId*R) +k]) + offset;
        offset += psHisto[((numThread -1) * R) + k];
      }

      Barrier.enterBarrier();

      CreatePartition.createPartition(0, q, myId, numThread, lss);

      for (int k = lss.i_start; k < lss.i_stop; k++) {
        int j = BITS(lKey[k], bitOff, m);
        lSorted[myHisto[(myId*R) +j]] = lKey[k];
        auxSorted[myHisto[(myId*R) +j]] = auxKey[k];
        myHisto[(myId*R) +j]++;
      }

      Barrier.enterBarrier();

      if (myId == 0) {
        psHisto = null;
        myHisto = null;
      }
    }


  /* =============================================================================
   * all_radixsort_node_s3
   *
   * q (elems/proc) must be a multiple of NODES
   * =============================================================================
   */
  /*
  public void
    all_radixsort_node_s3 (int q,
        int[] lKeys,
        int[] lSorted)
    {

      int[] lTemp = null;

      if (myId == 0) {
        lTemp = new int[ q];
        global_lTemp = lTemp;
      }

      Barrier.enterBarrier();

      lTemp = global_lTemp;

      all_countsort_node(q, lKeys,   lSorted, (1<<11),  0, 11);
      all_countsort_node(q, lSorted, lTemp,   (1<<11), 11, 11);
      all_countsort_node(q, lTemp,   lSorted, (1<<10), 22, 10);

      Barrier.enterBarrier();

      if (myId == 0) {
        lTemp = null;
      }
    }
    */


  /* =============================================================================
   * all_radixsort_node_s2
   *
   * q (elems/proc) must be a multiple of NODES
   * =============================================================================
   */
  /*
  public void
    all_radixsort_node_s2 (int q,
        int[] lKeys,
        int[] lSorted)
    {

      int[] lTemp = null;

      if (myId == 0) {
        lTemp = new int[ q];
        global_lTemp = lTemp;
      }

      Barrier.enterBarrier();

      lTemp = global_lTemp;

      all_countsort_node(q, lKeys, lTemp,   (1<<16),  0, 16);
      all_countsort_node(q, lTemp, lSorted, (1<<16), 16, 16);

      Barrier.enterBarrier();

      if (myId == 0) {
        lTemp = null;
      }
    }
    */


  /* =============================================================================
   * all_radixsort_node_aux_s3_seq
   *
   * q (elems/proc) must be a multiple of NODES
   * =============================================================================
   */
  public void
    all_radixsort_node_aux_s3_seq (int q,
        int[] lKeys,
        int[] lSorted,
        int[] auxKey,
        int[] auxSorted)
    {
      int[] lTemp  = new int[q];
      int[] lTemp2 = new int[q];

      all_countsort_node_aux_seq(q, lKeys, lSorted, auxKey, auxSorted, (1<<11),  0, 11);
      all_countsort_node_aux_seq(q, lSorted, lTemp, auxSorted, lTemp2, (1<<11), 11, 11);
      all_countsort_node_aux_seq(q, lTemp, lSorted, lTemp2, auxSorted, (1<<10), 22, 10);
    }


  /* =============================================================================
   * all_radixsort_node_aux_s3
   *
   * q (elems/proc) must be a multiple of NODES
   * =============================================================================
   */
  public static void
    all_radixsort_node_aux_s3 (int myId,
        int numThread,
        int q,
        int[] lKeys,
        int[] lSorted,
        int[] auxKey,
        int[] auxSorted,
        Alg_Radix_Smp rdxsort)
    {
      int[] lTemp  = null;
      int[] lTemp2 = null;

      if (myId == 0) {
        lTemp = new int[ q];
        rdxsort.global_lTemp = lTemp;
        lTemp2 = new int[ q];
        rdxsort.global_lTemp2 = lTemp2;
      }

      Barrier.enterBarrier();

      lTemp  = rdxsort.global_lTemp;
      lTemp2 = rdxsort.global_lTemp2;

      rdxsort.all_countsort_node_aux(myId, numThread, q, lKeys, lSorted, auxKey, auxSorted, (1<<11),  0, 11, rdxsort);
      rdxsort.all_countsort_node_aux(myId, numThread, q, lSorted, lTemp, auxSorted, lTemp2, (1<<11), 11, 11, rdxsort);
      rdxsort.all_countsort_node_aux(myId, numThread, q, lTemp, lSorted, lTemp2, auxSorted, (1<<10), 22, 10, rdxsort);

      Barrier.enterBarrier();

    }
}

/* =============================================================================
 *
 * End of alg_radix_smp.java
 *
 * =============================================================================
 */

