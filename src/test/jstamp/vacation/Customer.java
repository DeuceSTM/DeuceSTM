package jstamp.vacation;

/* =============================================================================
 *
 * customer.c
 * -- Representation of customer
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


public class Customer {

  
  /* =============================================================================
   * compareReservationInfo
   * =============================================================================
   */
  int id;
  List_t reservationInfoListPtr;

/* =============================================================================
 * customer_alloc
 * =============================================================================
 */
  public Customer(int id) {
    this.id=id;
    reservationInfoListPtr = new List_t();
  }
  

/* =============================================================================
 * customer_compare
 * -- Returns -1 if A < B, 0 if A = B, 1 if A > B
 * =============================================================================
 */
  int  customer_compare (Customer aPtr, Customer bPtr) {
    return (aPtr.id - bPtr.id);
  }


/* =============================================================================
 * customer_addReservationInfo
 * -- Returns true if success, else FALSE
 * =============================================================================
 */
  boolean customer_addReservationInfo (int type, int id, int price) {
    Reservation_Info reservationInfoPtr= new Reservation_Info(type, id, price);
    //    assert(reservationInfoPtr != NULL);
    
    return reservationInfoListPtr.insert(reservationInfoPtr);
  }


/* =============================================================================
 * customer_removeReservationInfo
 * -- Returns true if success, else FALSE
 * =============================================================================
 */
  boolean customer_removeReservationInfo (int type, int id) {
    Reservation_Info findReservationInfo =new Reservation_Info(type, id, 0);
    
    Reservation_Info reservationInfoPtr = (Reservation_Info)reservationInfoListPtr.find(findReservationInfo);
    
    if (reservationInfoPtr == null) {
      return false;
    }

    boolean status = reservationInfoListPtr.remove(findReservationInfo);
    return true;
  }


/* =============================================================================
 * customer_getBill
 * -- Returns total cost of reservations
 * =============================================================================
 */
  int customer_getBill () {
    int bill = 0;
    List_Node it;

    it=reservationInfoListPtr.head;
    while (it.nextPtr!=null) {
      it=it.nextPtr;
      Reservation_Info reservationInfoPtr =
	(Reservation_Info)it.dataPtr;
      bill += reservationInfoPtr.price;
    }
    
    return bill;
  }
}
