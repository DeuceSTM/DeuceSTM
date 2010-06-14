package org.deuce.transaction.norec;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.norec.field.BooleanFieldAccess;
import org.deuce.transaction.norec.field.ByteFieldAccess;
import org.deuce.transaction.norec.field.CharFieldAccess;
import org.deuce.transaction.norec.field.DoubleFieldAccess;
import org.deuce.transaction.norec.field.FieldAccess;
import org.deuce.transaction.norec.field.Field;
import org.deuce.transaction.norec.field.FloatFieldAccess;
import org.deuce.transaction.norec.field.IntFieldAccess;
import org.deuce.transaction.norec.field.LongFieldAccess;
import org.deuce.transaction.norec.field.ObjectFieldAccess;
import org.deuce.transaction.norec.field.ShortFieldAccess;
import org.deuce.transaction.norec.field.Field.Type;
import org.deuce.transaction.norec.ReadSet;
import org.deuce.transaction.norec.WriteSet;
import org.deuce.transform.Exclude;

/**
 * LSA implementation
 * 
 * @author Pascal Felber
 */
@Exclude
final public class Context implements org.deuce.transaction.Context {

	final private static TransactionException VALIDATE_FAILURE_EXCEPTION =
		new TransactionException("Fail on validate.");

	final private static int LOCK = 1;

	final private static AtomicInteger clock = new AtomicInteger(0);

	final private ReadSet readSet = new ReadSet(1024);
	final private WriteSet writeSet = new WriteSet(32);

	private int ts;

	public Context() {
	}

	public void init(int blockId, String metainf) {
		readSet.clear();
		writeSet.clear();
		do {
			ts = clock.get();
		} while((ts & LOCK) != 0);
	}

	public boolean commit() {
		if (!writeSet.isEmpty()) {
			// Acquire global lock (make clock odd)
			while (!clock.compareAndSet(ts, ts | LOCK)) {
				ts = validate();
				if (ts < 0)
					return false;
			}
			// Write values
			writeSet.commit();
			// Release global lock (make clock even)
			clock.set(ts + 2);
		}
		return true;
	}

	public void rollback() {
		// Nothing to be done
	}

	public void beforeReadAccess(Object obj, long field) {
	}

	private int validate() {
		int c = clock.get();
		while(true) {
			while((c & LOCK) != 0)
				c = clock.get();
			if (!readSet.validate())
				return -1;
			int t = clock.get();
			if (c == t)
				break;
			c = t;
		}
		return c;
	}

	private FieldAccess onReadAccess(Object obj, long field, Type type) {
		// Did we already write this field?
		FieldAccess f = writeSet.get(obj, field);
		if (f == null) {
			while (ts != clock.get()) {
				ts = validate();
				if (ts < 0)
					throw VALIDATE_FAILURE_EXCEPTION;
				f = Field.newFieldAccess(obj, field, type);
			}
		}
		return f;
	}

	public Object onReadAccess(Object obj, Object value, long field) {
		ObjectFieldAccess f = (ObjectFieldAccess) onReadAccess(obj, field, Type.OBJECT);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public boolean onReadAccess(Object obj, boolean value, long field) {
		BooleanFieldAccess f = (BooleanFieldAccess) onReadAccess(obj, field, Type.BOOLEAN);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public byte onReadAccess(Object obj, byte value, long field) {
		ByteFieldAccess f = (ByteFieldAccess) onReadAccess(obj, field, Type.BYTE);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public char onReadAccess(Object obj, char value, long field) {
		CharFieldAccess f = (CharFieldAccess) onReadAccess(obj, field, Type.CHAR);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public short onReadAccess(Object obj, short value, long field) {
		ShortFieldAccess f = (ShortFieldAccess) onReadAccess(obj, field, Type.SHORT);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public int onReadAccess(Object obj, int value, long field) {
		IntFieldAccess f = (IntFieldAccess) onReadAccess(obj, field, Type.INT);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public long onReadAccess(Object obj, long value, long field) {
		LongFieldAccess f = (LongFieldAccess) onReadAccess(obj, field, Type.LONG);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public float onReadAccess(Object obj, float value, long field) {
		FloatFieldAccess f = (FloatFieldAccess) onReadAccess(obj, field, Type.FLOAT);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public double onReadAccess(Object obj, double value, long field) {
		DoubleFieldAccess f = (DoubleFieldAccess) onReadAccess(obj, field, Type.DOUBLE);
		if (f != null) {
			readSet.add(f);
			return f.getValue();
		} else {
			readSet.add(obj, field, value);
			return value;
		}
	}

	public void onWriteAccess(Object obj, Object value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, boolean value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, byte value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, char value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, short value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, int value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, long value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, float value, long field) {
		writeSet.add(obj, field, value);
	}

	public void onWriteAccess(Object obj, double value, long field) {
		writeSet.add(obj, field, value);
	}
}
