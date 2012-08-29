package org.deuce.transaction.jvstminplace;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.deuce.InPlaceMetadata;
import org.deuce.transaction.TransactionException;
import org.deuce.transaction.jvstminplace.field.BoolValue;
import org.deuce.transaction.jvstminplace.field.ByteValue;
import org.deuce.transaction.jvstminplace.field.CharValue;
import org.deuce.transaction.jvstminplace.field.DoubleValue;
import org.deuce.transaction.jvstminplace.field.FloatValue;
import org.deuce.transaction.jvstminplace.field.IntValue;
import org.deuce.transaction.jvstminplace.field.LongValue;
import org.deuce.transaction.jvstminplace.field.ObjectValue;
import org.deuce.transaction.jvstminplace.field.ShortValue;
import org.deuce.transaction.jvstminplace.field.VBox;
import org.deuce.transaction.jvstminplace.field.VBoxBBody;
import org.deuce.transaction.jvstminplace.field.VBoxBody;
import org.deuce.transaction.jvstminplace.field.VBoxCBody;
import org.deuce.transaction.jvstminplace.field.VBoxDBody;
import org.deuce.transaction.jvstminplace.field.VBoxFBody;
import org.deuce.transaction.jvstminplace.field.VBoxIBody;
import org.deuce.transaction.jvstminplace.field.VBoxLBody;
import org.deuce.transaction.jvstminplace.field.VBoxOBody;
import org.deuce.transaction.jvstminplace.field.VBoxSBody;
import org.deuce.transaction.jvstminplace.field.VBoxZBody;
import org.deuce.transaction.jvstminplace.field.Value;
import org.deuce.transaction.jvstminplace.pool.Pool;
import org.deuce.transaction.jvstminplace.pool.ResourceFactory;
import org.deuce.transaction.util.BooleanArrayList;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;
import org.deuce.trove.THashMap;
import org.deuce.trove.TObjectObjectProcedure;

/**
 * JVSTM Implementation
 * 
 * JVSTM is a multiversion STM for Java developed by Joao Cachopo 
 * from INESC-ID.
 * 
 * @author Ricardo Dias
 * @version Oct 05, 2010 22:48:19 PM
 *
 */
@ExcludeInternal
@InPlaceMetadata(
		fieldObjectClass="org.deuce.transaction.jvstminplace.field.VBoxO",
		fieldIntClass="org.deuce.transaction.jvstminplace.field.VBoxI",
		fieldShortClass="org.deuce.transaction.jvstminplace.field.VBoxS",
		fieldCharClass="org.deuce.transaction.jvstminplace.field.VBoxC",
		fieldByteClass="org.deuce.transaction.jvstminplace.field.VBoxB",
		fieldBooleanClass="org.deuce.transaction.jvstminplace.field.VBoxZ",
		fieldFloatClass="org.deuce.transaction.jvstminplace.field.VBoxF",
		fieldLongClass="org.deuce.transaction.jvstminplace.field.VBoxL",
		fieldDoubleClass="org.deuce.transaction.jvstminplace.field.VBoxD",
		
		arrayObjectClass="org.deuce.transaction.jvstminplace.field.VBoxArrO",
		arrayIntClass="org.deuce.transaction.jvstminplace.field.VBoxArrI",
		arrayShortClass="org.deuce.transaction.jvstminplace.field.VBoxArrS",
		arrayCharClass="org.deuce.transaction.jvstminplace.field.VBoxArrC",
		arrayByteClass="org.deuce.transaction.jvstminplace.field.VBoxArrB",
		arrayBooleanClass="org.deuce.transaction.jvstminplace.field.VBoxArrZ",
		arrayFloatClass="org.deuce.transaction.jvstminplace.field.VBoxArrF",
		arrayLongClass="org.deuce.transaction.jvstminplace.field.VBoxArrL",
		arrayDoubleClass="org.deuce.transaction.jvstminplace.field.VBoxArrD"
			)
public final class Context implements org.deuce.transaction.ContextMetadata {

	private static final TransactionException READ_ONLY_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read-only hint was set).");
	
	private static final boolean USE_SPIN_LOCK = true;
	
	private static final ReentrantLock COMMIT_LOCK = new ReentrantLock(true);
	
	private static volatile ActiveTransactionsRecord mostRecentRecord = new ActiveTransactionsRecord(0, null);
	
	private int localClock;
	private ActiveTransactionsRecord activeTxRecord;
	
	private Cons<Pair<VBox,VBoxBody>> bodiesRead = Cons.empty();
	private Map<VBox,Value> boxesWritten = new THashMap<VBox, Value>();
	
	// Keep per-thread read-only hints (uses more memory but faster)
	private final BooleanArrayList readWriteMarkers = new BooleanArrayList();
	private boolean readWriteHint = true;
	private int atomicBlockId;
	
	private Cons<VBoxBody> newBodies = Cons.empty();
	
	
	private static int getMostRecentCommitedNumber() {
        return mostRecentRecord.transactionNumber;
    }
	
	// this method is called during the commit of a write transaction
    // the commits are already synchronized, so this method doesn't need to be
    private static void setMostRecentActiveRecord(ActiveTransactionsRecord record) {
        mostRecentRecord.setNext(record);
        mostRecentRecord = record;
    }
    
	public void init(int blockId, String metainf) {
		activeTxRecord = mostRecentRecord.getRecordForNewTransaction();
		localClock = activeTxRecord.transactionNumber;
		
		bodiesRead = null;
		bodiesRead = Cons.empty();
		
		boxesWritten.clear();
        newBodies = Cons.empty();
        
        atomicBlockId = blockId;
		readWriteHint = readWriteMarkers.get(atomicBlockId);
		
		this.objectPool.clear();
		this.booleanPool.clear();
		this.bytePool.clear();
		this.charPool.clear();
		this.shortPool.clear();
		this.intPool.clear();
		this.longPool.clear();
		this.floatPool.clear();
		this.doublePool.clear();
			
	}
	
	private static final AtomicInteger lock = new AtomicInteger(0);
	
	private static final void lock() {
		int l;
		do {
			while (((l = lock.get()) & LockTable.LOCK) != 0) {}
		} while(!lock.compareAndSet(l, l | LockTable.LOCK));
	}
	
	private static final void unlock() {
		lock.set(lock.get() & LockTable.UNLOCK);
	}
	

	public boolean commit() {
		if (readWriteHint) { 
		
			if (!boxesWritten.isEmpty()) {
			
				if (!USE_SPIN_LOCK) {
					COMMIT_LOCK.lock();
				}
				else {
					lock();
				}
				
				try {
					
					if (validate()) {
						apply();
					}
					else {
						activeTxRecord.decrementRunning();
						return false;
					}
					
				
				}
				finally {
					if (!USE_SPIN_LOCK) {
						COMMIT_LOCK.unlock();
					}
					else {
						unlock();
					}
				}
			
			}
		
		}
		
		activeTxRecord.decrementRunning();
		
		
		return true;
	}
	
	
	public void rollback() {
		activeTxRecord.decrementRunning();
	}

	private final boolean validate() {
		for (Pair<VBox,VBoxBody> entry : bodiesRead) {
			if (!entry.first.validate(entry.second)) {
				return false;
			}
		}
		return true;
	}

	
	
	
	TObjectObjectProcedure<VBox, Value> putProcedure = 
		new TObjectObjectProcedure<VBox, Value>() {
		
		public boolean execute(VBox vbox, Value newValue) {
			VBoxBody newBody = vbox.commit(newValue, localClock);			
			
			newBodies = newBodies.cons(newBody);
			
			return true;
		}
	};
	
	private final void apply() {
		int newTxNumber = getMostRecentCommitedNumber() + 1;
		localClock = newTxNumber;

		
		((THashMap<VBox, Value>)boxesWritten).forEachEntry(putProcedure);
		
		
        
        ActiveTransactionsRecord newRecord = new ActiveTransactionsRecord(newTxNumber, newBodies);
        setMostRecentActiveRecord(newRecord);
        newRecord.incrementRunning();
        activeTxRecord.decrementRunning();
        activeTxRecord = newRecord;
	}
	
	
	
	
	
	public void beforeReadAccess(TxField field) {
		

	}
	
	
	private Value readLocal(VBox vbox) {
		Value value = null;
		
        value = boxesWritten.get(vbox);
        
        return value;
	}
	
	
	public Object onReadAccess(Object value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((ObjectValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxOBody)body).value;
		return ((VBoxOBody)body).value;
	}

	public boolean onReadAccess(boolean value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((BoolValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxZBody)body).value;
		return ((VBoxZBody)body).value;
	}

	public byte onReadAccess(byte value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((ByteValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxBBody)body).value;
		return ((VBoxBBody)body).value;
	}

	public char onReadAccess(char value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((CharValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxCBody)body).value;
		return ((VBoxCBody)body).value;
	}

	public short onReadAccess(short value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((ShortValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxSBody)body).value;
		return ((VBoxSBody)body).value;
	}

	public int onReadAccess(int value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((IntValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
		
//		return body == box.getTop() ? value : ((VBoxIBody)body).value;
		return ((VBoxIBody)body).value;
	}

	public long onReadAccess(long value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((LongValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxLBody)body).value;
		return ((VBoxLBody)body).value;
	}

	public float onReadAccess(float value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((FloatValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxFBody)body).value;
		return ((VBoxFBody)body).value;
	}

	public double onReadAccess(double value, TxField field) {
		VBox box = (VBox)field;
		
		if (readWriteHint) {
			Value res = readLocal(box);
			if (res != null) {
				return ((DoubleValue)res).value;
			}
		}
		
		VBoxBody body = box.getBody(localClock); 
		
		if (readWriteHint) {
			bodiesRead = bodiesRead.cons(new Pair<VBox,VBoxBody>(box, body));
		}
		
//		return body == box.getTop() ? value : ((VBoxDBody)body).value;
		return ((VBoxDBody)body).value;
	}

	public void onWriteAccess(Object value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		ObjectValue v = objectPool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(boolean value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		BoolValue v = booleanPool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(byte value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		ByteValue v = bytePool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(char value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		CharValue v = charPool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(short value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		ShortValue v = shortPool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(int value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		IntValue v = intPool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(long value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		LongValue v = longPool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(float value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		FloatValue v = floatPool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	public void onWriteAccess(double value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		DoubleValue v = doublePool.getNext();
		v.value = value;
		boxesWritten.put((VBox)field, v);
	}

	
	private static class ObjectResourceFactory implements ResourceFactory<ObjectValue>{
		@Override
		public ObjectValue newInstance() {
			return new ObjectValue();
		}
	}
	final private Pool<ObjectValue> objectPool = new Pool<ObjectValue>(new ObjectResourceFactory());

	private static class BooleanResourceFactory implements ResourceFactory<BoolValue>{
		@Override
		public BoolValue newInstance() {
			return new BoolValue();
		}
	}
	final private Pool<BoolValue> booleanPool = new Pool<BoolValue>(new BooleanResourceFactory());

	private static class ByteResourceFactory implements ResourceFactory<ByteValue>{
		@Override
		public ByteValue newInstance() {
			return new ByteValue();
		}
	}
	final private Pool<ByteValue> bytePool = new Pool<ByteValue>( new ByteResourceFactory());

	private static class CharResourceFactory implements ResourceFactory<CharValue>{
		@Override
		public CharValue newInstance() {
			return new CharValue();
		}
	}
	final private Pool<CharValue> charPool = new Pool<CharValue>(new CharResourceFactory());

	private static class ShortResourceFactory implements ResourceFactory<ShortValue>{
		@Override
		public ShortValue newInstance() {
			return new ShortValue();
		}
	}
	final private Pool<ShortValue> shortPool = new Pool<ShortValue>( new ShortResourceFactory());

	private static class IntResourceFactory implements ResourceFactory<IntValue>{
		@Override
		public IntValue newInstance() {
			return new IntValue();
		}
	}
	final private Pool<IntValue> intPool = new Pool<IntValue>( new IntResourceFactory());

	private static class LongResourceFactory implements ResourceFactory<LongValue>{
		@Override
		public LongValue newInstance() {
			return new LongValue();
		}
	}
	final private Pool<LongValue> longPool = new Pool<LongValue>( new LongResourceFactory());
	
	private static class FloatResourceFactory implements ResourceFactory<FloatValue>{
		@Override
		public FloatValue newInstance() {
			return new FloatValue();
		}
	}
	final private Pool<FloatValue> floatPool = new Pool<FloatValue>( new FloatResourceFactory());
	
	private static class DoubleResourceFactory implements ResourceFactory<DoubleValue>{
		@Override
		public DoubleValue newInstance() {
			return new DoubleValue();
		}
	}
	final private Pool<DoubleValue> doublePool = new Pool<DoubleValue>( new DoubleResourceFactory());


	@Override
	public void onIrrevocableAccess() {
		
		
	}
	

	

}
