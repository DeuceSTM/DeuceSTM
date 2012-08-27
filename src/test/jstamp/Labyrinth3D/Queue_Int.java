/* =============================================================================
 *
 * queue.java
 *
 * =============================================================================
 *
 * Copyright (C) Stanford University, 2006.  All Rights Reserved.
 * Author: Chi Cao Minh
 *
 * Ported to Java June 2009 Alokika Dash
 * adash@uci.edu
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

package jstamp.Labyrinth3D;


public class Queue_Int {
	
	public final int QUEUE_GROWTH_FACTOR = 2;
	
  int pop; /* points before element to pop */
  int push;
  int capacity;
  int[] elements;

  public Queue_Int() {
  }

  /* =============================================================================
   * queue_alloc
   * =============================================================================
   */
  public static Queue_Int queue_alloc (int initCapacity)
  {
    Queue_Int queuePtr = new Queue_Int();

    int capacity = ((initCapacity < 2) ? 2 : initCapacity);
    queuePtr.elements = new int[capacity];
    queuePtr.pop      = capacity - 1;
    queuePtr.push     = 0;
    queuePtr.capacity = capacity;

    return queuePtr;
  }


  /* =============================================================================
   * Pqueue_alloc
   * =============================================================================
   */
  public Queue_Int
    Pqueue_alloc (int initCapacity)
    {
      Queue_Int queuePtr = new Queue_Int();

      int capacity = ((initCapacity < 2) ? 2 : initCapacity);
      queuePtr.elements = new int[capacity];
      queuePtr.pop      = capacity - 1;
      queuePtr.push     = 0;
      queuePtr.capacity = capacity;

      return queuePtr;
    }


  /* =============================================================================
   * TMqueue_alloc
   * =============================================================================
   */
  public Queue_Int TMqueue_alloc (int initCapacity)
  {
    Queue_Int queuePtr = new Queue_Int();

    int capacity = ((initCapacity < 2) ? 2 : initCapacity);
    queuePtr.elements = new int[capacity];
    queuePtr.pop      = capacity - 1;
    queuePtr.push     = 0;
    queuePtr.capacity = capacity;

    return queuePtr;
  }


  /* =============================================================================
   * queue_free
   * =============================================================================
   */
  public void
    queue_free ()
    {
      elements = null;
    }


  /* =============================================================================
   * Pqueue_free
   * =============================================================================
   */
  public void
    Pqueue_free ()
    {
      elements = null;
    }


  /* =============================================================================
   * TMqueue_free
   * =============================================================================
   */
  public void
    TMqueue_free ()
    {
      elements = null;
    }


  /* =============================================================================
   * queue_isEmpty
   * =============================================================================
   */
  public boolean
    queue_isEmpty ()
    {
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
  public boolean
    TMqueue_isEmpty (Queue_Int queuePtr)
    {
      int pop      = queuePtr.pop;
      int push     = queuePtr.push;
      int capacity = queuePtr.capacity;

      return (((pop + 1) % capacity == push) ? true : false);
    }


  /* =============================================================================
   * queue_push
   * =============================================================================
   */
  public boolean
    queue_push (int dataPtr)
    {

      /* Need to resize */
      int newPush = (push + 1) % capacity;
      if (newPush == pop) {

        int newCapacity = capacity * QUEUE_GROWTH_FACTOR;
        int[] newElements = new int[newCapacity];

        int dst = 0;
        int[] tmpelements = elements;
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
    Pqueue_push (Queue_Int queuePtr, int dataPtr)
    {
      int pop      = queuePtr.pop;
      int push     = queuePtr.push;
      int capacity = queuePtr.capacity;


      /* Need to resize */
      int newPush = (push + 1) % capacity;
      if (newPush == pop) {

        int newCapacity = capacity * QUEUE_GROWTH_FACTOR;
        int[] newElements = new int[newCapacity];

        int dst = 0;
        int[] elements = queuePtr.elements;
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
   * TMqueue_push
   * =============================================================================
   */
  public boolean
    TMqueue_push (Queue_Int queuePtr, int dataPtr)
    {
      int pop      = (queuePtr.pop);
      int push     = (queuePtr.push);
      int capacity = (queuePtr.capacity);


      /* Need to resize */
      int newPush = (push + 1) % capacity;
      if (newPush == pop) {
        int newCapacity = capacity * QUEUE_GROWTH_FACTOR;
        int[] newElements = new int[newCapacity];

        int dst = 0;
        int[] elements = queuePtr.elements;
        if (pop < push) {
          int src;
          for (src = (pop + 1); src < push; src++, dst++) {
            newElements[dst] = (elements[src]);
          }
        } else {
          int src;
          for (src = (pop + 1); src < capacity; src++, dst++) {
            newElements[dst] = (elements[src]);
          }
          for (src = 0; src < push; src++, dst++) {
            newElements[dst] = (elements[src]);
          }
        }

        elements = null;
        queuePtr.elements = newElements;
        queuePtr.pop      = newCapacity - 1;
        queuePtr.capacity = newCapacity;
        push = dst;
        newPush = push + 1; /* no need modulo */

      }

      int[] elements = queuePtr.elements;
      elements[push] = dataPtr;
      queuePtr.push = newPush;

      return true;
    }


  /* =============================================================================
   * queue_pop
   * =============================================================================
   */
  public int
    queue_pop ()
    {
      int newPop = (pop + 1) % capacity;
      if (newPop == push) {
        return 0;
      }

      int dataPtr = elements[newPop];
      pop = newPop;

      return dataPtr;
    }


  /* =============================================================================
   * TMqueue_pop
   * =============================================================================
   */
  public int
    TMqueue_pop (Queue_Int queuePtr)
    {
      int pop      = queuePtr.pop;
      int push     = queuePtr.push;
      int capacity = queuePtr.capacity;

      int newPop = (pop + 1) % capacity;
      if (newPop == push) {
        return 0;
      }

      int[] elements = queuePtr.elements;
      int dataPtr = elements[newPop];
      queuePtr.pop = newPop;

      return dataPtr;
    }

  /****
   * main method for testing
   **/
  /*
     public static void main(String[] args) {
     testQueue queuePtr = testQueue.queue_alloc(-1);
     int numData = 4;
     if(queuePtr.queue_isEmpty())
     System.out.println("Queue is empty");

     for(int i = 0; i<numData; i++) {
     System.out.println("Inserting " + i);
     queuePtr.queue_push(i);
     }

     for(int i = 0; i<numData; i++) {
     int val = queuePtr.queue_pop();
     System.out.println("Removing " + val);
     }

     if(queuePtr.queue_isEmpty())
     System.out.println("Queue is empty");
     }
     */
}
/* =============================================================================
 *
 * End of queue.java
 *
 * =============================================================================
 */
