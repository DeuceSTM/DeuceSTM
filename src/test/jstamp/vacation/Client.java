package jstamp.vacation;

import org.deuce.Atomic;

/* =============================================================================
 *
 * client.c
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


public class Client extends Thread {
  int id;
  Manager managerPtr;
  Random randomPtr;
  int numOperation;
  int numQueryPerTransaction;
  int queryRange;
  int percentUser;

  public Client() {}

/* =============================================================================
 * client_alloc
 * -- Returns NULL on failure
 * =============================================================================
 */
  public Client(int id,
		Manager managerPtr,
		int numOperation,
		int numQueryPerTransaction,
		int queryRange,
		int percentUser) {
    this.randomPtr = new Random();
    this.randomPtr.random_alloc();
    this.id=id;
    this.managerPtr = managerPtr;
    randomPtr.random_seed(id);
    this.numOperation = numOperation;
    this.numQueryPerTransaction = numQueryPerTransaction;
    this.queryRange = queryRange;
    this.percentUser = percentUser;
  }


/* =============================================================================
 * selectAction
 * =============================================================================
 */
  public int selectAction (int r, int percentUser) {
    if (r < percentUser) {
      return Defines.ACTION_MAKE_RESERVATION;
    } else if ((r & 1)==1) {
      return Defines.ACTION_DELETE_CUSTOMER;
    } else {
      return Defines.ACTION_UPDATE_TABLES;
    }
  }


/* =============================================================================
 * client_run
 * -- Execute list operations on the database
 * =============================================================================
 */
  public void run() {
	  int myId = id;

	  Manager managerPtr = this.managerPtr;
	  Random randomPtr  = this.randomPtr;

	  int numOperation           = this.numOperation;
	  int numQueryPerTransaction = this.numQueryPerTransaction;
	  int queryRange             = this.queryRange;
	  int percentUser            = this.percentUser;

	  int types[]  = new int[numQueryPerTransaction];
	  int ids[]    = new int[numQueryPerTransaction];
	  int ops[]    = new int[numQueryPerTransaction];
	  int prices[] = new int[numQueryPerTransaction];

	  
	  Barrier.enterBarrier();  
	  for (int i = 0; i < numOperation; i++) {
		  int r = randomPtr.posrandom_generate() % 100;
		  int action = selectAction(r, percentUser);

		  if(action==Defines.ACTION_MAKE_RESERVATION) {
			  int maxPrices[]=new int[Defines.NUM_RESERVATION_TYPE];
			  int maxIds[]=new int[Defines.NUM_RESERVATION_TYPE];
			  maxPrices[0]=-1;
			  maxPrices[1]=-1;
			  maxPrices[2]=-1;
			  maxIds[0]=-1;
			  maxIds[1]=-1;
			  maxIds[2]=-1;
			  int n;
			  int numQuery = randomPtr.posrandom_generate() % numQueryPerTransaction + 1;
			  int customerId = randomPtr.posrandom_generate() % queryRange + 1;
			  for (n = 0; n < numQuery; n++) {
				  types[n] = randomPtr.random_generate() % Defines.NUM_RESERVATION_TYPE;
				  ids[n] = (randomPtr.random_generate() % queryRange) + 1;
			  }
			  boolean isFound = false;

			  n = atomicMethodOne(managerPtr, types, ids, maxPrices, maxIds, numQuery,
					  customerId, isFound);

		  } else if (action==Defines.ACTION_DELETE_CUSTOMER) {
			  int customerId = randomPtr.posrandom_generate() % queryRange + 1;
			  atomicMethodTwo(managerPtr, customerId);
		  } else if (action==Defines.ACTION_UPDATE_TABLES) {
			  int numUpdate = randomPtr.posrandom_generate() % numQueryPerTransaction + 1;
			  int n;
			  for (n = 0; n < numUpdate; n++) {
				  types[n] = randomPtr.posrandom_generate() % Defines.NUM_RESERVATION_TYPE;
				  ids[n] = (randomPtr.posrandom_generate() % queryRange) + 1;
				  ops[n] = randomPtr.posrandom_generate() % 2;
				  if (ops[n]==1) {
					  prices[n] = ((randomPtr.posrandom_generate() % 5) * 10) + 50;
				  }
			  }
			  n = atomicMethodThree(managerPtr, types, ids, ops, prices, numUpdate);
		  }
	  } /* for i */
	  Barrier.enterBarrier();
  }

  @Atomic
private int atomicMethodThree(Manager managerPtr, int[] types, int[] ids,
		int[] ops, int[] prices, int numUpdate) {
	int n;
	for (n = 0; n < numUpdate; n++) {
	    int t = types[n];
	    int id = ids[n];
	    int doAdd = ops[n];
	    if (doAdd==1) {
	      int newPrice = prices[n];
	      if (t==Defines.RESERVATION_CAR) {
		managerPtr.manager_addCar(id, 100, newPrice);
	      } else if (t==Defines.RESERVATION_FLIGHT) {
		managerPtr.manager_addFlight(id, 100, newPrice);
	      } else if (t==Defines.RESERVATION_ROOM) {
		managerPtr.manager_addRoom(id, 100, newPrice);
	      }
	    } else { /* do delete */
	      if (t==Defines.RESERVATION_CAR) {
		managerPtr.manager_deleteCar(id, 100);
	      } else if (t==Defines.RESERVATION_FLIGHT) {
		managerPtr.manager_deleteFlight(id);
	      } else if (t==Defines.RESERVATION_ROOM) {
		managerPtr.manager_deleteRoom(id, 100);
	      }
	    }
	  }
	return n;
}

  @Atomic
private void atomicMethodTwo(Manager managerPtr, int customerId) {
	int bill = managerPtr.manager_queryCustomerBill(customerId);
	  if (bill >= 0) {
	    managerPtr.manager_deleteCustomer(customerId);
	  }
}

  @Atomic
private int atomicMethodOne(Manager managerPtr, int[] types, int[] ids,
		int[] maxPrices, int[] maxIds, int numQuery, int customerId,
		boolean isFound) {
	int n;
	for (n = 0; n < numQuery; n++) {
	    int t = types[n];
	    int id = ids[n];
	    int price = -1;
	    if (t==Defines.RESERVATION_CAR) {
	      if (managerPtr.manager_queryCar(id) >= 0) {
		price = managerPtr.manager_queryCarPrice(id);
	      }
	    } else if (t==Defines.RESERVATION_FLIGHT) {
	      if (managerPtr.manager_queryFlight(id) >= 0) {
		price = managerPtr.manager_queryFlightPrice(id);
	      }
	    } else if (t==Defines.RESERVATION_ROOM) {
	      if (managerPtr.manager_queryRoom(id) >= 0) {
		price = managerPtr.manager_queryRoomPrice(id);
	      }
	    }
	    if (price > maxPrices[t]) {
	      maxPrices[t] = price;
	      maxIds[t] = id;
	      isFound = true;
	    }
	  } /* for n */
	  if (isFound) {
	    managerPtr.manager_addCustomer(customerId);
	  }
	  if (maxIds[Defines.RESERVATION_CAR] > 0) {
	    managerPtr.manager_reserveCar(customerId, maxIds[Defines.RESERVATION_CAR]);
	  }
	  if (maxIds[Defines.RESERVATION_FLIGHT] > 0) {
	    managerPtr.manager_reserveFlight(customerId, maxIds[Defines.RESERVATION_FLIGHT]);
	  }
	  if (maxIds[Defines.RESERVATION_ROOM] > 0) {
	    managerPtr.manager_reserveRoom(customerId, maxIds[Defines.RESERVATION_ROOM]);
	  }
	return n;
}
}

/* =============================================================================
 *
 * End of client.c
 *
 * =============================================================================
 */




