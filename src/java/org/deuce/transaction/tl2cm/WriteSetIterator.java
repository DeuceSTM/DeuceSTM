package org.deuce.transaction.tl2cm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.deuce.transaction.tl2.WriteSet;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transaction.tl2cm.WriteSetIterator.WriteSetIteratorElement.State;
import org.deuce.transform.Exclude;

@Exclude
public class WriteSetIterator {

	private LinkedList<WriteSetIteratorElement> pending = new LinkedList<WriteSetIteratorElement>();
	private LinkedList<WriteSetIteratorElement> acquired = new LinkedList<WriteSetIteratorElement>();

	public WriteSetIterator(WriteSet writeSet) {
		Iterator<WriteFieldAccess> iter = writeSet.iterator();
		while (iter.hasNext()) {
			WriteSetIteratorElement n = new WriteSetIteratorElement(iter.next());
			pending.add(n);
		}
	}
	
	public boolean isEmpty() {
		return pending.isEmpty();
	}
	
	public WriteFieldAccess getLock() {
		return pending.peek().writeField;
	}
	
	public void lockAcquired(boolean byForce) {
		WriteSetIteratorElement n = pending.poll();
		n.state = byForce ? State.FORCED : State.ACQUIRED;
		acquired.offer(n);
	}
	
	public void skipLock() {
		WriteSetIteratorElement n = pending.poll();
		n.lockAttempts++;
		pending.offer(n);
	}
	
	public List<WriteSetIteratorElement> getAcquiredLocks() {
		return acquired;
	}
	
	public static class WriteSetIteratorElement {
	
		enum State {PENDING, ACQUIRED, FORCED}
		
		private WriteFieldAccess writeField;
		private State state;
		private int lockAttempts;
		
		WriteSetIteratorElement(WriteFieldAccess writeField) {
			this.writeField = writeField;
			this.state = State.PENDING;
			this.lockAttempts = 0;
		}
		
		public WriteFieldAccess getField() {
			return writeField;
		}
		
		public State getState() {
			return state;
		}
		
	}
	
}
