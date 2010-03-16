package org.deuce.transaction.lsacm;

import java.util.HashMap;

import org.deuce.transaction.lsacm.field.Field.Type;
import org.deuce.transaction.lsacm.field.WriteFieldAccess;
import org.deuce.transaction.lsacm.LockTable;
import org.deuce.transform.Exclude;
import org.deuce.trove.THashMap;

/**
 * @author Pascal Felber
 */
@Exclude
public class WriteSet {

	private static final int DEFAULT_CAPACITY = 16;

	final private THashMap<Integer, WriteFieldAccess> entries;

	public WriteSet(int initialCapacity) {
		entries = new THashMap<Integer, WriteFieldAccess>(initialCapacity);
	}

	public WriteSet() {
		this(DEFAULT_CAPACITY);
	}

	public void clear() {
		entries.clear();
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public WriteFieldAccess get(int hash, Object obj, long field) {
		// Return value from existing entry
		WriteFieldAccess w = entries.get(hash);
		while (w != null) {
			// Check if we have already written that field
			if (w.getReference() == obj && w.getField() == field)
				return w;
			w = w.getNext();
		}
		return null;
	}

	public void appendWrite(int hash, Object obj, long field, Object value, Type type) {
		// Append to existing entry (write mode)
		WriteFieldAccess w = entries.get(hash);
		while (w != null) {
			// Check if we have already written that field
			if (w.getReference() == obj && w.getField() == field) {
				// Update written value
				w.update(type, value);
				return;
			}
			WriteFieldAccess next = w.getNext();
			if (next == null) {
				// We did not write this field (we must add it to write set)
				w.setNext(new WriteFieldAccess(obj, field, type, value, hash, 0));
				return;
			}
			w = next;
		}
	}

	public void addWrite(int hash, Object obj, long field, Object value, Type type, long timestamp) {
		// Add new entry (write mode)
		entries.put(hash, new WriteFieldAccess(obj, field, type, value, hash, timestamp));
	}

	public void addRead(int hash, Object obj, long field, long timestamp) {
		// Add new entry (read mode)
		entries.put(hash, new WriteFieldAccess(obj, field, hash, timestamp));
	}

	public void commit(long timestamp) {
		// Write values and release locks
		for (WriteFieldAccess w : entries.values()) {
			int hash = w.getHash();
			assert w.getLock() >= 0;
			do {
				w.writeField();
				w = w.getNext();
			} while (w != null);
			LockTable.setAndReleaseLock(hash, timestamp);
		}
	}

	public void rollback() {
		// Release locks
		for (WriteFieldAccess w : entries.values()) {
			assert w.getLock() >= 0;
			LockTable.setAndReleaseLock(w.getHash(), w.getLock());
		}
	}
}
