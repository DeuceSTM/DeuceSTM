package org.deuce.transaction.norec;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
	
	//Global lock used to allow only one irrevocable transaction solely. 
	final private static ReentrantReadWriteLock irrevocableAccessLock = new ReentrantReadWriteLock();
	private boolean irrevocableState = false;

	private int timeStamp;

	public Context() {
	}

	@Override
	public void init(int blockId, String metainf) {
		readSet.clear();
		writeSet.clear();
		
		//Lock according to the transaction irrevocable state
		if(irrevocableState)
			irrevocableAccessLock.writeLock().lock();
		else
			irrevocableAccessLock.readLock().lock();
		
		do {
			timeStamp = clock.get();
		} while((timeStamp & LOCK) != 0);
	}

	@Override
	public boolean commit() {
		try{
			if (writeSet.isEmpty())
				return true;

			// Acquire global lock (make clock odd)
			while (!clock.compareAndSet(timeStamp, timeStamp | LOCK)) {
				timeStamp = validate();
				if (timeStamp < 0)
					return false;
			}
			// Write values
			writeSet.commit();
			// Release global lock (make clock even)
			clock.set(timeStamp + 2);

			return true;
		}
		finally{
			if(irrevocableState){
				irrevocableState = false;
				irrevocableAccessLock.writeLock().unlock();
			}
			else{
				irrevocableAccessLock.readLock().unlock();
			}

		}
	}

	@Override
	public void rollback() {
		irrevocableAccessLock.readLock().unlock();
	}

	@Override
	public void beforeReadAccess(Object obj, long field) {
	}

	private int validate() {
		int lastClock = clock.get();
		while(true) {
			while((lastClock & LOCK) != 0)
				lastClock = clock.get();
			
			if (!readSet.validate())
				return -1;
			
			int tempClock = clock.get();
			if (lastClock == tempClock)
				break;
			lastClock = tempClock;
		}
		return lastClock;
	}

	private FieldAccess onReadAccess(Object obj, long field, Type type) {
		// Did we already write this field?
		FieldAccess f = writeSet.get(obj, field);
		if (f == null) {
			while (timeStamp != clock.get()) {
				timeStamp = validate();
				if (timeStamp < 0)
					throw VALIDATE_FAILURE_EXCEPTION;
				f = Field.newFieldAccess(obj, field, type);
			}
		}
		return f;
	}

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
	public void onWriteAccess(Object obj, Object value, long field) {
		writeSet.add(obj, field, value);
	}

	@Override
	public void onWriteAccess(Object obj, boolean value, long field) {
		writeSet.add(obj, field, value);
	}

	@Override
	public void onWriteAccess(Object obj, byte value, long field) {
		writeSet.add(obj, field, value);
	}

	@Override
	public void onWriteAccess(Object obj, char value, long field) {
		writeSet.add(obj, field, value);
	}

	@Override
	public void onWriteAccess(Object obj, short value, long field) {
		writeSet.add(obj, field, value);
	}

	@Override
	public void onWriteAccess(Object obj, int value, long field) {
		writeSet.add(obj, field, value);
	}

	@Override
	public void onWriteAccess(Object obj, long value, long field) {
		writeSet.add(obj, field, value);
	}

	@Override
	public void onWriteAccess(Object obj, float value, long field) {
		writeSet.add(obj, field, value);
	}
	
	@Override
	public void onWriteAccess(Object obj, double value, long field) {
		writeSet.add(obj, field, value);
	}
	
	@Override
	public void onIrrevocableAccess() {
		if(irrevocableState) // already in irrevocable state so no need to restart transaction.
			return;

		irrevocableState = true;
		throw TransactionException.STATIC_TRANSACTION;
	}
}
