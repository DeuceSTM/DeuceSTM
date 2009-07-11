package org.deuce.benchmark.intset;

import org.deuce.*;

/**
 * @author Pascal Felber
 * @since 0.1
 */
public class IntSetLinkedList implements IntSet {

	public class Node {
		final private int m_value;
		private Node m_next;

		public Node(int value, Node next) {
			m_value = value;
			m_next = next;
		}

		public Node(int value) {
			this(value, null);
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

	final private Node m_first;

	public IntSetLinkedList() {
		Node min = new Node(Integer.MIN_VALUE);
		Node max = new Node(Integer.MAX_VALUE);
		min.setNext(max);
		m_first = min;
	}

	@Atomic
	public boolean add(int value) {
		boolean result;

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = v != value;
		if (result) {
			previous.setNext(new Node(value, next));
		}

		return result;
	}

	@Atomic
	public boolean remove(int value) {
		boolean result;

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = v == value;
		if (result) {
			previous.setNext(next.getNext());
		}

		return result;
	}

	@Atomic
	public boolean contains(int value) {
		boolean result;

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		result = (v == value);

		return result;
	}
}
