package org.deuce.test.queue;

import org.deuce.Atomic;

public class PriorityQueue{
	
	private Node head;
	private Node tail;
	
	public PriorityQueue(){
		this.tail = new Node(Integer.MAX_VALUE, null);
		this.head = new Node(Integer.MIN_VALUE, tail);
	}
	/**
	 * Inserts element into priority queue
	 * @param value element to be inserted into priority queue
	 * @throws IllegalStateException when for some reason element can't be inserted
	 */
	@Atomic
	public void insert(int value){
		Node pred = head;
		Node curr = pred.next;
		while(curr.value < value){
			pred = curr;
			curr = curr.next;
		}
		if(value == curr.value){
			return;
		}else{
			Node newNode = new Node(value, curr);
			pred.next = newNode;
			return;
		}
	}
	
	/**
	 * Removes element from priority queue and returns it's value
	 * @return removed element
	 * @throws IllegalStateException when for some reason element can't be deleted
	 */
	@Atomic
	public int deleteMin(){

		if(head.next == tail){
			throw new EmptyException();
		}
		Node removedNode = head.next;
		head.next = removedNode.next;
		return removedNode.value;
	}

}
