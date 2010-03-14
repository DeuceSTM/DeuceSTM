/* =============================================================================
 *
 * queue.java
 *
 * =============================================================================
 *
 * Copyright (C) Stanford University, 2006.  All Rights Reserved.
 * Author: Chi Cao Minh
 *
 * Ported to Java
 * Author:Alokika Dash
 * University of California, Irvine
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

package jstamp.intruder;

public class Queue_t {
	int QUEUE_GROWTH_FACTOR = 2;
	
  int pop; /* points before element to pop */
  int push;
  int capacity;
  Object[] elements;

  /* =============================================================================
   * queue_alloc
   * =============================================================================
   */
  public Queue_t(int initCapacity) {
    int capacity = ((initCapacity < 2) ? 2 : initCapacity);
    elements = new Object[capacity];
    pop      = capacity - 1;
    push     = 0;
    this.capacity = capacity;
  }

  /* =============================================================================
   * queue_free
   * =============================================================================
   */
  public void
    queue_free (Queue_t queuePtr)
    {
      queuePtr.elements = null;
    }



  /* =============================================================================
   * TMqueue_free
   * =============================================================================
   *
  public void
    TMqueue_free (TM_ARGDECL  Queue* queuePtr)
    {
      queuePtr.elements = null;
      queuePtr = null;
    }

*/
  /* =============================================================================
   * queue_isEmpty
   * =============================================================================
   */
  public boolean
    queue_isEmpty ()
    {
      //int pop      = queuePtr.pop;
      //int push     = queuePtr.push;
      //int capacity = queuePtr.capacity;

      return (((pop + 1) % capacity == push) ? true : false);
    }


  /* =============================================================================
   * queue_clear
   * =============================================================================
   */
  public void
    queue_clear ()
    {
      pop  = capacity - 1;
      push = 0;
    }


  /* =============================================================================
   * TMqueue_isEmpty
   * =============================================================================
   */



  /* =============================================================================
   * queue_push
   * =============================================================================
   */
  public boolean queue_push (Object dataPtr) {
    if(pop == push) {
      System.out.println("push == pop in Queue.java");
      return false;
    }

    /* Need to resize */
    int newPush = (push + 1) % capacity;
    if (newPush == pop) {
      int newCapacity = capacity * QUEUE_GROWTH_FACTOR;
      Object[] newElements = new Object[newCapacity];
      
      int dst = 0;
      Object[] tmpelements = elements;
      if (pop < push) {
	int src;
	for (src = (pop + 1); src < push; src++, dst++) {
	  newElements[dst] = elements[src];
	}
      } else {
	int src;
	for (src = (pop + 1); src < capacity; src++, dst++) {
	  newElements[dst] = elements[src];
	}
	for (src = 0; src < push; src++, dst++) {
	  newElements[dst] = elements[src];
	}
      }
      
      //elements = null;
      elements = newElements;
      pop      = newCapacity - 1;
      capacity = newCapacity;
      push = dst;
      newPush = push + 1; /* no need modulo */
    }
    
    elements[push] = dataPtr;
    push = newPush;
    
    return true;
  }


  /* =============================================================================
   * Pqueue_push
   * =============================================================================
   */
  public boolean
    Pqueue_push (Queue_t queuePtr, Object dataPtr)
    {
      int pop      = queuePtr.pop;
      int push     = queuePtr.push;
      int capacity = queuePtr.capacity;

      if(pop == push) {
        System.out.println("push == pop in Queue.java");
        return false;
      }

      /* Need to resize */
      int newPush = (push + 1) % capacity;
      if (newPush == pop) {

        int newCapacity = capacity * QUEUE_GROWTH_FACTOR;
        Object[] newElements = new Object[newCapacity];

        int dst = 0;
        Object[] elements = queuePtr.elements;
        if (pop < push) {
          int src;
          for (src = (pop + 1); src < push; src++, dst++) {
            newElements[dst] = elements[src];
          }
        } else {
          int src;
          for (src = (pop + 1); src < capacity; src++, dst++) {
            newElements[dst] = elements[src];
          }
          for (src = 0; src < push; src++, dst++) {
            newElements[dst] = elements[src];
          }
        }

        elements = null;
        queuePtr.elements = newElements;
        queuePtr.pop      = newCapacity - 1;
        queuePtr.capacity = newCapacity;
        push = dst;
        newPush = push + 1; /* no need modulo */

      }

      queuePtr.elements[push] = dataPtr;
      queuePtr.push = newPush;

      return true;
    }

  /* =============================================================================
   * queue_pop
   * =============================================================================
   */
  public Object queue_pop () {
    int newPop = (pop + 1) % capacity;
    if (newPop == push) {
      return null;
    }
    
    Object dataPtr = elements[newPop];
    pop = newPop;
    
    return dataPtr;
  }

  public void queue_shuffle (Random randomPtr)
    {
      int numElement;
      if (pop < push) {
        numElement = push - (pop + 1);
      } else {
        numElement = capacity - (pop - push + 1);
      }

      int i;
      int base = pop + 1;
      for (i = 0; i < numElement; i++) {
        int r1 = (int) (randomPtr.random_generate() % numElement);
        int r2 = (int) (randomPtr.random_generate() % numElement);
        int i1 = (base + r1) % capacity;
        int i2 = (base + r2) % capacity;
        Object tmp = elements[i1];
        elements[i1] = elements[i2];
        elements[i2] = tmp;
      }
    }

}
/* =============================================================================
 *
 * End of queue.java
 *
 * =============================================================================
 */
