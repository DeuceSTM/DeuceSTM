package org.deuce.benchmark.examples.intset;

import org.deuce.*;

public class IntSetOOP implements IntSet {

	private Node m_first;

	public IntSetOOP() {
		Node min = new Node(Integer.MIN_VALUE);
		Node max = new Node(Integer.MAX_VALUE);
		min.setNext(max);
		m_first = min;
	}

	@Atomic
	public boolean add(int value) {
		boolean result = false;

		Node previous = m_first;
		Node next = previous.getNext();
		while (next.getValue() < value) {
			previous = next;
			next = previous.getNext();
		}
		if (result = (next.getValue() != value)) {
			Node n = new Node(value);
			n.setNext(previous.getNext());
			previous.setNext(n);
		}

		return result;
	}

	@Atomic
	public boolean remove(int value) {
		boolean result = false;

		Node previous = m_first;
		Node next = previous.getNext();
		while (next.getValue() < value) {
			previous = next;
			next = previous.getNext();
		}
		if (result = (next.getValue() == value)) {
			previous.setNext(next.getNext());
		}

		return result;
	}

	@Atomic
	public boolean contains(int value) {
		boolean result = false;

		Node previous = m_first;
		Node next = previous.getNext();
		while (next.getValue() < value) {
			previous = next;
			next = previous.getNext();
		}
		result = (next.getValue() == value);

		return result;
	}
}
