package org.deuce.benchmark.intset;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class Node {
	private int m_value;
	private Node m_next;

	public Node(int value, Node next) {
		m_value = value;
		m_next = next;
	}

	public Node(int value) {
		this(value, null);
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
