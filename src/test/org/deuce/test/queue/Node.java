package org.deuce.test.queue;

public class Node {
	volatile int value;
	volatile Node next;
	public Node(int value, Node next){
		this.value = value;
		this.next = next;
	}
}
