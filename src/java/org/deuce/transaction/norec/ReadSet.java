package org.deuce.transaction.norec;

import java.util.ArrayList;

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

/**
 * @author Pascal Felber
 */
@Exclude
public class ReadSet {

	private static final int DEFAULT_CAPACITY = 1024;

	private ArrayList<FieldAccess> entries;
	
	public ReadSet(int initialCapacity) {
		entries = new ArrayList<FieldAccess>(initialCapacity);
	}

	public ReadSet() {
		this(DEFAULT_CAPACITY);
	}

	public void clear() {
		entries.clear();
	}

	public void add(FieldAccess f) {
		entries.add(f);
	}

	public void add(Object obj, long field, Object value) {
		entries.add(new ObjectFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, boolean value) {
		entries.add(new BooleanFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, byte value) {
		entries.add(new ByteFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, char value) {
		entries.add(new CharFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, double value) {
		entries.add(new DoubleFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, float value) {
		entries.add(new FloatFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, int value) {
		entries.add(new IntFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, long value) {
		entries.add(new LongFieldAccess(obj, field, value));
	}

	public void add(Object obj, long field, short value) {
		entries.add(new ShortFieldAccess(obj, field, value));
	}

	public int getSize() {
		return entries.size();
	}

	public boolean validate() {
		for (FieldAccess r : entries) {
			if (!r.validate())
				return false;
		}
		return true;
	}
}
