package org.deuce.benchmark.examples.intset;

public class Node {
	private int m_value;
	private Node m_next;

	public Node(int value) {
		m_value = value;
	}

	public void setValue(int value) {
		m_value = value;
	}

	public int getValue() {
		return m_value;
	}

	public void setNext(Node next) {
		m_next = next;
	}

	public Node getNext() {
		return m_next;
	}
}
