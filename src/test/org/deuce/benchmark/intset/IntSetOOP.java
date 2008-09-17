package org.deuce.benchmark.intset;

import org.deuce.*;

/**
 * @author Pascal Felber
 * @since 0.1
 */
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
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		if (result = (v != value)) {
			previous.setNext(new Node(value, next));
		}

		return result;
	}

	@Atomic
	public boolean remove(int value) {
		boolean result = false;

		Node previous = m_first;
		Node next = previous.getNext();
		int v;
		while ((v = next.getValue()) < value) {
			previous = next;
			next = previous.getNext();
		}
		if (result = (v == value)) {
			previous.setNext(next.getNext());
		}

		return result;
	}

	@Atomic
	public boolean contains(int value) {
		boolean result = false;

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
