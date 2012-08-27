package jstamp.ssca2;
/* =============================================================================
 *
 * createPartition.java
 *
 * =============================================================================
 * 
 * For the license of ssca2, please see ssca2/COPYRIGHT
 * 
 * ------------------------------------------------------------------------
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

public class CreatePartition {
  public CreatePartition() {
  }

  /* =============================================================================
   * createPartition
   * =============================================================================
   */
  public static void
    createPartition (int min, int max, int id, int n, LocalStartStop lss)
    {
      int range = max - min;
      int chunk = MAX(1, ((range + n/2) / n)); /* rounded */
      int start = min + chunk * id;
      int stop;
      if (id == (n-1)) {
        stop = max;
      } else {
        stop = MIN(max, (start + chunk));
      }

      lss.i_start = start;
      lss.i_stop = stop;
    }

  public static int MAX(int a, int b) {
    int val = (a > b) ? (a) : (b);
    return val;
  }

  public static int MIN(int a, int b) {
    int val = (a < b) ? (a) : (b); 
    return val;
  }
}
/* =============================================================================
 *
 * End of createPartition.java
 *
 * =============================================================================
 */
