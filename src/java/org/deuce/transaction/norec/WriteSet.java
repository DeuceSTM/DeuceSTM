package org.deuce.transaction.norec;

import org.deuce.transaction.norec.field.BooleanFieldAccess;
import org.deuce.transaction.norec.field.ByteFieldAccess;
import org.deuce.transaction.norec.field.CharFieldAccess;
import org.deuce.transaction.norec.field.DoubleFieldAccess;
import org.deuce.transaction.norec.field.FieldAccess;
import org.deuce.transaction.norec.field.FloatFieldAccess;
import org.deuce.transaction.norec.field.IntFieldAccess;
import org.deuce.transaction.norec.field.LongFieldAccess;
import org.deuce.transaction.norec.field.ObjectFieldAccess;
import org.deuce.transaction.norec.field.ShortFieldAccess;
import org.deuce.transform.Exclude;
import org.deuce.trove.THashSet;

/**
 * @author Pascal Felber
 */
@Exclude
public class WriteSet {

	private static final int DEFAULT_CAPACITY = 16;

	final private THashSet<FieldAccess> entries;
	final private FieldAccess tempFieldAccess = new FieldAccess();

	public WriteSet(int initialCapacity) {
		entries = new THashSet<FieldAccess>(initialCapacity);
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

	public FieldAccess get(Object obj, long field) {
		tempFieldAccess.init(obj, field);
		return entries.get(tempFieldAccess);
	}

	public void add(Object obj, long field, Object value) {
		FieldAccess w = new ObjectFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, boolean value) {
		FieldAccess w = new BooleanFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, byte value) {
		FieldAccess w = new ByteFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, char value) {
		FieldAccess w = new CharFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, double value) {
		FieldAccess w = new DoubleFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, float value) {
		FieldAccess w = new FloatFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, int value) {
		FieldAccess w = new IntFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, long value) {
		FieldAccess w = new LongFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void add(Object obj, long field, short value) {
		FieldAccess w = new ShortFieldAccess(obj, field, value);
		if(!entries.add(w))
			entries.replace(w);
	}

	public void commit() {
		// Write values
		for (FieldAccess w : entries) {
			w.writeField();
		}
	}
}
