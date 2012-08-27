/* =============================================================================
 *
 * Pair.java
 *
 * =============================================================================
 *
 * Copyright (C) Stanford University, 2006.  All Rights Reserved.
 * Author: Chi Cao Minh
 *
 * Ported to Java
 *  
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

package jstamp.Labyrinth3D;

import java.util.*;


public class Pair {
    public Object first;
    public Object second;

    public Pair() {
        first = null;
        second = null;
    }


/* =============================================================================
 * 
 * pair constructor
 * 
 * pair_t* pair_alloc(void* firstPtr, void* secondPtr);
 * =============================================================================
 */
    public static Pair alloc(Object first,Object second)
    {
        Pair ptr= new Pair();
        ptr.first = first;
        ptr.second = second;

        return ptr;
    }



/* =============================================================================
 * Ppair_alloc
 *
 * -- Returns NULL if failure
 * =============================================================================
 */
  public static Pair Ppair_alloc (Object firstPtr, Object secondPtr) {
    Pair pairPtr = new Pair();       
    pairPtr.first = firstPtr;
    pairPtr.second = secondPtr;
    return pairPtr;
  }


/* =============================================================================
 * pair_free
 * =============================================================================
 *
 *  void pair_free (pair_t* pairPtr);
 *
 */
    public static void free(Pair pairPtr)
    {
        pairPtr = null;
    }


/* =============================================================================
 * Ppair_free
 * =============================================================================
 *
void Ppair_free (pair_t* pairPtr);
*/

/* =============================================================================
 * pair_swap
 * -- Exchange 'firstPtr' and 'secondPtr'
 * =============================================================================
 * void pair_swap (pair_t* pairPtr);
*/
    public static void swap(Pair pairPtr)
    {
        Object tmpPtr = pairPtr.first;

        pairPtr.first = pairPtr.second;
        pairPtr.second = tmpPtr;
    }

}    

/* =============================================================================
 *
 * End of pair.java
 *
 * =============================================================================
 */
