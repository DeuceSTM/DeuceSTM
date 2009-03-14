package org.deuce.benchmark.intset;

import java.util.*;

import org.deuce.*;

/**
 * @author Pascal Felber
 * @since 0.3
 */
public class IntSetSkipList implements IntSet {

	public class Node {
		final private int m_value;
		final private Node[] m_forward;

		public Node(int level, int value) {
			m_value = value;
			m_forward = new Node[level + 1];
		}

		public int getValue() {
			return m_value;
		}

		public int getLevel() {
			return m_forward.length - 1;
		}

		public void setForward(int level, Node next) {
			m_forward[level] = next;
		}

		public Node getForward(int level) {
			return m_forward[level];
		}

		public String toString() {
			String result = "";
			result += "<l=" + getLevel() + ",v=" + m_value + ">:";
			for (int i = 0; i <= getLevel(); i++) {
				result += " @[" + i + "]=";
				if (m_forward[i] != null)
					result += m_forward[i].getValue();
				else
					result += "null";
			}
			return result;
		}
	}

	// Probability to increase level
	final private double m_probability;
	// Upper bound on the number of levels
	final private int m_maxLevel;
	// Highest level so far
	private int m_level;
	// First element of the list
	final private Node m_head;
	// Thread-private PRNG
	final private static ThreadLocal<Random> s_random = new ThreadLocal<Random>() {
		protected synchronized Random initialValue() {
			return new Random();
		}
	};

	public IntSetSkipList(int maxLevel, double probability) {
		m_maxLevel = maxLevel;
		m_probability = probability;
		m_level = 0;
		m_head = new Node(m_maxLevel, Integer.MIN_VALUE);
		Node tail = new Node(m_maxLevel, Integer.MAX_VALUE);
		for (int i = 0; i <= m_maxLevel; i++)
			m_head.setForward(i, tail);
	}

	public IntSetSkipList() {
		this(32, 0.25);
	}

	protected int randomLevel() {
		int l = 0;
		while (l < m_maxLevel && s_random.get().nextDouble() < m_probability)
			l++;
		return l;
	}

	@Atomic
	public boolean add(int value) {
		boolean result;

		Node[] update = new Node[m_maxLevel + 1];
		Node node = m_head;

		for (int i = m_level; i >= 0; i--) {
			Node next = node.getForward(i);
			while (next.getValue() < value) {
				node = next;
				next = node.getForward(i);
			}
			update[i] = node;
		}
		node = node.getForward(0);

		if (node.getValue() == value) {
			result = false;
		} else {
			int level = randomLevel();
			if (level > m_level) {
				for (int i = m_level + 1; i <= level; i++)
					update[i] = m_head;
				m_level = level;
			}
			node = new Node(level, value);
			for (int i = 0; i <= level; i++) {
				node.setForward(i, update[i].getForward(i));
				update[i].setForward(i, node);
			}
			result = true;
		}

		return result;
	}

	@Atomic
	public boolean remove(int value) {
		boolean result;

		Node[] update = new Node[m_maxLevel + 1];
		Node node = m_head;

		for (int i = m_level; i >= 0; i--) {
			Node next = node.getForward(i);
			while (next.getValue() < value) {
				node = next;
				next = node.getForward(i);
			}
			update[i] = node;
		}
		node = node.getForward(0);

		if (node.getValue() != value) {
			result = false;
		} else {
			for (int i = 0; i <= m_level; i++) {
				if (update[i].getForward(i) == node)
					update[i].setForward(i, node.getForward(i));
			}
			while (m_level > 0 && m_head.getForward(m_level).getForward(0) == null)
				m_level--;
			result = true;
		}

		return result;
	}

	@Atomic
	public boolean contains(int value) {
		boolean result;

		Node node = m_head;

		for (int i = m_level; i >= 0; i--) {
			Node next = node.getForward(i);
			while (next.getValue() < value) {
				node = next;
				next = node.getForward(i);
			}
		}
		node = node.getForward(0);

		result = (node.getValue() == value);

		return result;
	}

	public String toString() {
		String result = "";

		result += "Skip list:\n";
		result += "  Level=" + m_level + "\n";
		result += "  Max_level=" + m_maxLevel + "\n";
		result += "  Probability=" + m_probability + "\n";

		result += "Elements:\n";
		int[] countLevel = new int[m_maxLevel + 1];
		Node element = m_head.getForward(0);
		while (element.getValue() < Integer.MAX_VALUE) {
			countLevel[element.getLevel()]++;
			result += "  " + element.toString() + "\n";
			element = element.getForward(0);
		}

		result += "Level distribution:\n";
		for (int i = 0; i <= m_maxLevel; i++)
			result += "  #[" + i + "]=" + countLevel[i] + "\n";

		return result;
	}
}
