package jstamp.KMeans;

/* =============================================================================
 *
 * common.java
 *
 * =============================================================================
 *
 * For the license of bayes/sort.h and bayes/sort.c, please see the header
 * of the files.
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of kmeans, please see kmeans/LICENSE.kmeans
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of ssca2, please see ssca2/COPYRIGHT
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of lib/mt19937ar.c and lib/mt19937ar.h, please see the
 * header of the files.
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of lib/rbtree.h and lib/rbtree.c, please see
 * lib/LEGALNOTICE.rbtree and lib/LICENSE.rbtree
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

public class Common {

  public Common() {
  }


  /* =============================================================================
   * common_euclidDist2
   * -- multi-dimensional spatial Euclid distance square
   * =============================================================================
   */
   public static float
    common_euclidDist2 (float[] pt1, float[] pt2, int numdims)
    {
      int i;
      float ans = 0.0f;

      for (i = 0; i < numdims; i++) {
        ans += (pt1[i] - pt2[i]) * (pt1[i] - pt2[i]);
      }

      return ans;
    }


  /* =============================================================================
   * common_findNearestPoint
   * =============================================================================
   */
  public static int
    common_findNearestPoint (float[]  pt,        /* [nfeatures] */
        int     nfeatures,
        float[][] pts,       /* [npts][nfeatures] */
        int     npts)
    {
      int index = -1;
      int i;
      //double max_dist = FLT_MAX;
      float max_dist = (float)3.40282347e+38f;
      float limit = (float) 0.99999;

      /* Find the cluster center id with min distance to pt */
      for (i = 0; i < npts; i++) {
        float dist = common_euclidDist2(pt, pts[i], nfeatures);  /* no need square root */
        if ((dist / max_dist) < limit) {
          max_dist = dist;
          index = i;
          if (max_dist == 0) {
            break;
          }
        }
      }

      return index;
    }
}


/* =============================================================================
 *
 * End of common.java
 *
 * =============================================================================
 */
