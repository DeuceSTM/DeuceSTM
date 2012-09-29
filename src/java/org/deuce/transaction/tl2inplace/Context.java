package org.deuce.transaction.tl2inplace;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.deuce.InPlaceMetadata;
import org.deuce.transaction.TransactionException;
import org.deuce.transaction.tl2inplace.field.BooleanWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.ByteWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.CharWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.DoubleWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.FloatWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.IntWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.LongWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.ObjectWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.ReadFieldAccess;
import org.deuce.transaction.tl2inplace.field.ShortWriteFieldAccess;
import org.deuce.transaction.tl2inplace.field.WriteFieldAccess;
import org.deuce.transaction.tl2.pool.Pool;
import org.deuce.transaction.tl2.pool.ResourceFactory;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;
import org.deuce.trove.TObjectProcedure;

/**
 * TL2 with in-place metadata.
 * 
 * @author Ricardo Dias, Tiago Vale <{ricardo.dias,t.vale}@campus.fct.unl.pt}>
 */
@ExcludeInternal
@InPlaceMetadata(
		fieldObjectClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldIntClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldShortClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldCharClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldByteClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldBooleanClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldFloatClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldLongClass = "org.deuce.transaction.tl2inplace.TL2Field", 
		fieldDoubleClass = "org.deuce.transaction.tl2inplace.TL2Field",

		arrayObjectClass = "org.deuce.transaction.tl2inplace.TL2ArrObjectField", 
		arrayIntClass = "org.deuce.transaction.tl2inplace.TL2ArrIntField", 
		arrayByteClass = "org.deuce.transaction.tl2inplace.TL2ArrByteField", 
		arrayShortClass = "org.deuce.transaction.tl2inplace.TL2ArrShortField", 
		arrayCharClass = "org.deuce.transaction.tl2inplace.TL2ArrCharField", 
		arrayBooleanClass = "org.deuce.transaction.tl2inplace.TL2ArrBoolField", 
		arrayFloatClass = "org.deuce.transaction.tl2inplace.TL2ArrFloatField", 
		arrayLongClass = "org.deuce.transaction.tl2inplace.TL2ArrLongField", 
		arrayDoubleClass = "org.deuce.transaction.tl2inplace.TL2ArrDoubleField")

final public class Context implements org.deuce.transaction.ContextMetadata {

	private static final boolean TX_LOAD_OPT = Boolean.getBoolean("org.deuce.transaction.tl2inplace.txload.opt");

	final static AtomicInteger clock = new AtomicInteger(0);
	
	final private ReadSet readSet = new ReadSet();
	final private WriteSet writeSet = new WriteSet();

	private ReadFieldAccess currentReadFieldAccess = null;

	// Marked on beforeRead, used for the double lock check
	private int localClock;
	private int lastReadLock;

	// Global lock used to allow only one irrevocable transaction solely.
	final private static ReentrantReadWriteLock irrevocableAccessLock = new ReentrantReadWriteLock();
	private boolean irrevocableState = false;
		
	final private LockProcedure lockProcedure = new LockProcedure(this);

	final private TObjectProcedure<WriteFieldAccess> putProcedure = new TObjectProcedure<WriteFieldAccess>() {

		public boolean execute(WriteFieldAccess writeField) {
			writeField.put();
			return true;
		}

	};

	public Context() {
		this.localClock = clock.get();
	}

	public void init(int atomicBlockId, String metainf) {
		this.currentReadFieldAccess = null;
		this.readSet.clear();
		this.writeSet.clear();
		this.localClock = clock.get();
		this.objectPool.clear();
		this.booleanPool.clear();
		this.bytePool.clear();
		this.charPool.clear();
		this.shortPool.clear();
		this.intPool.clear();
		this.longPool.clear();
		this.floatPool.clear();
		this.doublePool.clear();
		
		// Lock according to the transaction irrevocable state
		if (irrevocableState)
			irrevocableAccessLock.writeLock().lock();
		else
			irrevocableAccessLock.readLock().lock();
	}

	public boolean commit() {
		try {
			if (writeSet.isEmpty()) { // if the writeSet is empty no need to lock a thing.
				return true;
			}

			try {
				// pre commit validation phase
				writeSet.forEach(lockProcedure);
				readSet.checkClock(localClock, this);
			} catch (TransactionException exception) {
				writeSet.forEach(lockProcedure.unlockProcedure);
				return false;
			}

			// commit new values and release locks
			writeSet.forEach(putProcedure);

			lockProcedure.setAndUnlockProcedure.retrieveNewClock();
			writeSet.forEach(lockProcedure.setAndUnlockProcedure);

			lockProcedure.clear();

			return true;
		} finally {
			if (irrevocableState) {
				irrevocableState = false;
				irrevocableAccessLock.writeLock().unlock();
			} else {
				irrevocableAccessLock.readLock().unlock();
			}
		}
	}

	public void rollback() {
		irrevocableAccessLock.readLock().unlock();
	}

	private WriteFieldAccess onReadAccess0(TxField field) {
		if (!TX_LOAD_OPT) {
			ReadFieldAccess current = currentReadFieldAccess;

			// Check the read is still valid
			((InPlaceLock) field).checkLock(localClock, lastReadLock);

			// Check if it is already included in the write set
			return writeSet.contains(current);
		} else {
			ReadFieldAccess current = readSet.getNext();
			current.init(field);
			((InPlaceLock) field).checkLock2(localClock);

			return writeSet.contains(current);
		}
	}

	private void addWriteAccess0(WriteFieldAccess write) {
		// Add to write set
		writeSet.put(write);
	}

	public void beforeReadAccess(TxField field) {
		if (!TX_LOAD_OPT) {
			ReadFieldAccess next = readSet.getNext();
			currentReadFieldAccess = next;
			next.init(field);

			// Check the read is still valid
			lastReadLock = ((InPlaceLock) field).checkLock(localClock);
		}
	}

	public Object onReadAccess(Object value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		Object r = ((ObjectWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public boolean onReadAccess(boolean value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		boolean r = ((BooleanWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public byte onReadAccess(byte value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		byte r = ((ByteWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public char onReadAccess(char value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		char r = ((CharWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public short onReadAccess(short value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		short r = ((ShortWriteFieldAccess) writeAccess).getValue();

		return r;

	}

	public int onReadAccess(int value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		int r = ((IntWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public long onReadAccess(long value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		long r = ((LongWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public float onReadAccess(float value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		float r = ((FloatWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public double onReadAccess(double value, TxField field) {
		WriteFieldAccess writeAccess = onReadAccess0(field);
		if (writeAccess == null)
			return value;

		double r = ((DoubleWriteFieldAccess) writeAccess).getValue();

		return r;
	}

	public void onWriteAccess(Object value, TxField field) {

		ObjectWriteFieldAccess next = objectPool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(boolean value, TxField field) {

		BooleanWriteFieldAccess next = booleanPool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(byte value, TxField field) {

		ByteWriteFieldAccess next = bytePool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(char value, TxField field) {

		CharWriteFieldAccess next = charPool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(short value, TxField field) {

		ShortWriteFieldAccess next = shortPool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(int value, TxField field) {

		IntWriteFieldAccess next = intPool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(long value, TxField field) {

		LongWriteFieldAccess next = longPool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(float value, TxField field) {

		FloatWriteFieldAccess next = floatPool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	public void onWriteAccess(double value, TxField field) {

		DoubleWriteFieldAccess next = doublePool.getNext();
		next.set(value, field);
		addWriteAccess0(next);

	}

	private static class ObjectResourceFactory implements ResourceFactory<ObjectWriteFieldAccess> {
		public ObjectWriteFieldAccess newInstance() {
			return new ObjectWriteFieldAccess();
		}
	}

	final private Pool<ObjectWriteFieldAccess> objectPool = new Pool<ObjectWriteFieldAccess>(
			new ObjectResourceFactory());

	private static class BooleanResourceFactory implements ResourceFactory<BooleanWriteFieldAccess> {
		public BooleanWriteFieldAccess newInstance() {
			return new BooleanWriteFieldAccess();
		}
	}

	final private Pool<BooleanWriteFieldAccess> booleanPool = new Pool<BooleanWriteFieldAccess>(
			new BooleanResourceFactory());

	private static class ByteResourceFactory implements ResourceFactory<ByteWriteFieldAccess> {
		public ByteWriteFieldAccess newInstance() {
			return new ByteWriteFieldAccess();
		}
	}

	final private Pool<ByteWriteFieldAccess> bytePool = new Pool<ByteWriteFieldAccess>(new ByteResourceFactory());

	private static class CharResourceFactory implements ResourceFactory<CharWriteFieldAccess> {
		public CharWriteFieldAccess newInstance() {
			return new CharWriteFieldAccess();
		}
	}

	final private Pool<CharWriteFieldAccess> charPool = new Pool<CharWriteFieldAccess>(new CharResourceFactory());

	private static class ShortResourceFactory implements ResourceFactory<ShortWriteFieldAccess> {
		public ShortWriteFieldAccess newInstance() {
			return new ShortWriteFieldAccess();
		}
	}

	final private Pool<ShortWriteFieldAccess> shortPool = new Pool<ShortWriteFieldAccess>(new ShortResourceFactory());

	private static class IntResourceFactory implements ResourceFactory<IntWriteFieldAccess> {
		public IntWriteFieldAccess newInstance() {
			return new IntWriteFieldAccess();
		}
	}

	final private Pool<IntWriteFieldAccess> intPool = new Pool<IntWriteFieldAccess>(new IntResourceFactory());

	private static class LongResourceFactory implements ResourceFactory<LongWriteFieldAccess> {
		public LongWriteFieldAccess newInstance() {
			return new LongWriteFieldAccess();
		}
	}

	final private Pool<LongWriteFieldAccess> longPool = new Pool<LongWriteFieldAccess>(new LongResourceFactory());

	private static class FloatResourceFactory implements ResourceFactory<FloatWriteFieldAccess> {
		public FloatWriteFieldAccess newInstance() {
			return new FloatWriteFieldAccess();
		}
	}

	final private Pool<FloatWriteFieldAccess> floatPool = new Pool<FloatWriteFieldAccess>(new FloatResourceFactory());

	private static class DoubleResourceFactory implements ResourceFactory<DoubleWriteFieldAccess> {
		public DoubleWriteFieldAccess newInstance() {
			return new DoubleWriteFieldAccess();
		}
	}

	final private Pool<DoubleWriteFieldAccess> doublePool = new Pool<DoubleWriteFieldAccess>(
			new DoubleResourceFactory());

	@Override
	public void onIrrevocableAccess() {
		if (irrevocableState) // already in irrevocable state so no need to restart
			return;
		
		irrevocableState = true;
		throw TransactionException.STATIC_TRANSACTION;
	}
}
