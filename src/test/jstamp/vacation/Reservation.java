package jstamp.vacation;

/* =============================================================================
 *
 * reservation.c
 * -- Representation of car, flight, and hotel relations
 *
 * =============================================================================
 *
 * Copyright (C) Stanford University, 2006.  All Rights Reserved.
 * Author: Chi Cao Minh
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

public class Reservation {
  public Reservation(int id, int numTotal, int price) {
    this.id=id;
    this.numUsed=0;
    this.numFree=numTotal;
    this.numTotal=numTotal;
    this.price=price;
    checkReservation();
  }

  int id;
  int numUsed;
  int numFree;
  int numTotal;
  int price;

/* =============================================================================
 * checkReservation
 * -- Check if consistent
 * =============================================================================
 */
  public void checkReservation() {
    int numUsed = this.numUsed;
    int numFree = this.numFree;
    int numTotal = this.numTotal;
    int price = this.price;
  }
  
  
  /* =============================================================================
   * reservation_addToTotal
   * -- Adds if 'num' > 0, removes if 'num' < 0;
   * -- Returns TRUE on success, else FALSE
   * =============================================================================
   */
  boolean reservation_addToTotal (int num) {
    if (numFree + num < 0) {
      return false;
    }
    
    numFree+=num;
    numTotal+=num;
    checkReservation();
    return true;
  }
  
  
  /* =============================================================================
   * reservation_make
   * -- Returns TRUE on success, else FALSE
   * =============================================================================
   */
  public boolean reservation_make() {
    if (numFree < 1) {
      return false;
    }
    numUsed++;
    numFree--;
    checkReservation();
    return true;
  }
  

/* =============================================================================
 * reservation_cancel
 * -- Returns TRUE on success, else FALSE
 * =============================================================================
 */
  boolean reservation_cancel() {
    if (numUsed < 1) {
      return false;
    }
    numUsed--;
    numFree++;
    checkReservation();
    return true;
  }

  
/* =============================================================================
 * reservation_updatePrice
 * -- Failure if 'price' < 0
 * -- Returns TRUE on success, else FALSE
 * =============================================================================
 */
  boolean reservation_updatePrice (int newPrice) {
    if (newPrice < 0) {
      return false;
    }
    
    this.price=newPrice;
    checkReservation();
    return true;
  }


/* =============================================================================
 * reservation_compare
 * -- Returns -1 if A < B, 0 if A = B, 1 if A > B
 * =============================================================================
 */
  int reservation_compare (Reservation aPtr, Reservation bPtr) {
    return aPtr.id-bPtr.id;
  }


/* =============================================================================
 * reservation_hash
 * =============================================================================
 */
  int reservation_hash() {
    return id;
  }
  
}
