package org.deuce.transaction.lsacm;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.lsacm.field.ReadFieldAccess;
import org.deuce.transaction.lsacm.LockTable;
import org.deuce.transaction.lsacm.Context;
import org.deuce.transform.Exclude;

/**
 * @author Pascal Felber
 */
@Exclude
public class ReadSet {

	private static final int DEFAULT_CAPACITY = 1024;

	private ReadFieldAccess[] entries;
	private int size;

	public ReadSet(int initialCapacity) {
		entries = new ReadFieldAccess[initialCapacity];
		size = 0;
		initArray(0);
	}

	public ReadSet() {
		this(DEFAULT_CAPACITY);
	}

	public void clear() {
		size = 0;
	}

	public void add(Object reference, long field, int hash, long lock) {
		if (size >= entries.length) {
			int l = entries.length;
			ReadFieldAccess[] e = new ReadFieldAccess[l << 1];
			System.arraycopy(entries, 0, e, 0, l);
			entries = e;
			initArray(l);
		}
		assert size < entries.length;
		ReadFieldAccess r = entries[size++];
		r.init(reference, field, hash, lock);
	}

	public int getSize() {
		return size;
	}

	public boolean validate(Context context, int id) {
		try {
			for (int i = 0; i < size; i++) {
				// Throws an exception if validation fails
				ReadFieldAccess r = entries[i];
				long lock = LockTable.checkLock(context, r.getHash(), id);
				if (lock >= 0 && lock != r.getLock()) {
					// Other version: cannot validate
					return false;
				}
			}
		} catch (TransactionException e) {
			return false;
		}
		return true;
	}

	public boolean contains(Object obj, long field) {
		for (int i = 0; i < size; i++) {
			ReadFieldAccess r = entries[i];
			if (r.getReference() == obj && r.getField() == field)
				return true;
		}
		return false;
	}

	private void initArray(int fromIndex) {
		for (int i = fromIndex; i < entries.length; i++)
			entries[i] = new ReadFieldAccess();
	}
}
