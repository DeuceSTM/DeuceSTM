package org.deuce.transaction.jvstminnogc;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.InPlaceMetadata;
import org.deuce.transaction.TransactionException;
import org.deuce.transaction.jvstminnogc.field.BooleanWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.ByteWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.CharWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.DoubleWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.FloatWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.IntWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.LongWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.ObjectWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.ReadFieldAccess;
import org.deuce.transaction.jvstminnogc.field.ShortWriteFieldAccess;
import org.deuce.transaction.jvstminnogc.field.VBox;
import org.deuce.transaction.jvstminnogc.field.VersionB;
import org.deuce.transaction.jvstminnogc.field.VersionC;
import org.deuce.transaction.jvstminnogc.field.VersionD;
import org.deuce.transaction.jvstminnogc.field.VersionF;
import org.deuce.transaction.jvstminnogc.field.VersionI;
import org.deuce.transaction.jvstminnogc.field.VersionL;
import org.deuce.transaction.jvstminnogc.field.VersionO;
import org.deuce.transaction.jvstminnogc.field.VersionS;
import org.deuce.transaction.jvstminnogc.field.VersionZ;
import org.deuce.transaction.jvstminnogc.field.WriteFieldAccess;
import org.deuce.transaction.jvstminnogc.pool.Pool;
import org.deuce.transaction.jvstminnogc.pool.ResourceFactory;
import org.deuce.transaction.util.BooleanArrayList;
import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxField;
import org.deuce.trove.TObjectProcedure;

/**
 * Versioned STM with bounded number of versions based in the JVSTM algorithm.
 * This version also uses lock per write-set entry during commit instead of
 * the global lock in the original JVSTM algorithm. 
 * 
 * JVSTM is a multiversion STM for Java developed by Jo‹o Cachopo 
 * from INESC-ID.
 * 
 * @author Ricardo Dias
 * @version Oct 11, 2010 15:00:19 PM
 *
 */
@ExcludeInternal
@InPlaceMetadata(
		fieldObjectClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldO",
		fieldIntClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldI",
		fieldShortClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldS",
		fieldCharClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldC",
		fieldByteClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldB",
		fieldBooleanClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldZ",
		fieldFloatClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldF",
		fieldLongClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldL",
		fieldDoubleClass="org.deuce.transaction.jvstminnogc.field.VBoxFieldD",
		
		arrayObjectClass="org.deuce.transaction.jvstminnogc.field.VBoxArrO",
		arrayIntClass="org.deuce.transaction.jvstminnogc.field.VBoxArrI",
		arrayShortClass="org.deuce.transaction.jvstminnogc.field.VBoxArrS",
		arrayCharClass="org.deuce.transaction.jvstminnogc.field.VBoxArrC",
		arrayByteClass="org.deuce.transaction.jvstminnogc.field.VBoxArrB",
		arrayBooleanClass="org.deuce.transaction.jvstminnogc.field.VBoxArrZ",
		arrayFloatClass="org.deuce.transaction.jvstminnogc.field.VBoxArrF",
		arrayLongClass="org.deuce.transaction.jvstminnogc.field.VBoxArrL",
		arrayDoubleClass="org.deuce.transaction.jvstminnogc.field.VBoxArrD"
			)
public final class Context implements org.deuce.transaction.ContextMetadata {

	private static final TransactionException READ_ONLY_FAILURE_EXCEPTION =
		new TransactionException("Fail on write (read-only hint was set).");
	
	public static final TransactionException VERSION_UNAVAILABLE_EXCEPTION =
		new TransactionException("Fail on retrieveing an older and unexistent version.");
	
	public static int MAX_VERSIONS = 16;
	static {
		Integer mm = Integer.getInteger("org.deuce.transaction.jvstminnogc.versions");
		if (mm != null) {
			MAX_VERSIONS = mm;
		}
	}
	
	final private static AtomicInteger threadID = new AtomicInteger(0);
	
	private final int id;
	
	
	private static final AtomicInteger clock = new AtomicInteger(0);
	
	private int localClock;
	
	private ReadSet readSet = new ReadSet();
	private WriteSet writeSet = new WriteSet();
	
	// Keep per-thread read-only hints (uses more memory but faster)
	private final BooleanArrayList readWriteMarkers = new BooleanArrayList();
	private boolean readWriteHint = true;
	private int atomicBlockId;
	
	private final LockProcedure lockProcedure = new LockProcedure();
	
	
	public Context() {
		this.id = threadID.incrementAndGet();
		lockProcedure.owner = id;
	}
	
    
	public void init(int blockId, String metainf) {
        atomicBlockId = blockId;
		readWriteHint = readWriteMarkers.get(atomicBlockId);
		
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
		
		lockProcedure.clear();
			
	}
	
	TObjectProcedure<WriteFieldAccess> putProcedure = 
		new TObjectProcedure<WriteFieldAccess>() {
		
		public boolean execute(WriteFieldAccess wfa) {
			wfa.put(localClock);	
			return true;
		}
	};
	

	public boolean commit() {
		
		if (readWriteHint) { 
			if (!writeSet.isEmpty()) {
				try {
					writeSet.forEach(lockProcedure);
					if (readSet.validate(id)) {
						localClock = clock.get()+1;
						writeSet.forEach(putProcedure);
						clock.incrementAndGet();
					}
					else {
						return false;
					}
				}
				catch (TransactionException e) {
					return false;
				}
				finally {
					writeSet.forEach(lockProcedure.unlockProcedure);
				}
			}
		}
		return true;
	}
	
	
	public void rollback() {
	}

	
	
	public void beforeReadAccess(TxField field) {
		

	}
	
	private ReadFieldAccess dummy = new ReadFieldAccess();
	
	private WriteFieldAccess readLocal(VBox vbox) {
		dummy.init(vbox);		
        return writeSet.contains(dummy);
	}
	
	
	public Object onReadAccess(Object value, TxField field) {
		VBox box = (VBox)field;
		VersionO ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((ObjectWriteFieldAccess)res).value;
			}
			ver = (VersionO)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionO)box.get(localClock);
		}
		
		return ver.value;
	}

	public boolean onReadAccess(boolean value, TxField field) {
		VBox box = (VBox)field;
		
		VersionZ ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((BooleanWriteFieldAccess)res).value;
			}
			ver = (VersionZ)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionZ)box.get(localClock);
		}
		
		
		return ver.value;
	}

	public byte onReadAccess(byte value, TxField field) {
		VBox box = (VBox)field;
		VersionB ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((ByteWriteFieldAccess)res).value;
			}
			ver = (VersionB)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionB)box.get(localClock);
		}
		
		
		return ver.value;
	}

	public char onReadAccess(char value, TxField field) {
		VBox box = (VBox)field;
		VersionC ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((CharWriteFieldAccess)res).value;
			}
			ver = (VersionC)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionC)box.get(localClock);
		}
		
		return ver.value;
	}

	public short onReadAccess(short value, TxField field) {
		VBox box = (VBox)field;
		VersionS ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((ShortWriteFieldAccess)res).value;
			}
			ver = (VersionS)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionS)box.get(localClock);
		}
		
		return ver.value;
	}

	public int onReadAccess(int value, TxField field) {
		VBox box = (VBox)field;
		VersionI ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((IntWriteFieldAccess)res).value;
			}
			ver = (VersionI)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionI)box.get(localClock);
		}
		
		return ver.value;
	}

	public long onReadAccess(long value, TxField field) {
		VBox box = (VBox)field;
		VersionL ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((LongWriteFieldAccess)res).value;
			}
			ver = (VersionL)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionL)box.get(localClock);
		}
		
		return ver.value;
	}

	public float onReadAccess(float value, TxField field) {
		VBox box = (VBox)field;
		VersionF ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((FloatWriteFieldAccess)res).value;
			}
			ver = (VersionF)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionF)box.get(localClock);
		}
		
		return ver.value;
	}

	public double onReadAccess(double value, TxField field) {
		VBox box = (VBox)field;
		VersionD ver = null;
		
		if (readWriteHint) {
			WriteFieldAccess res = readLocal(box);
			if (res != null) {
				return ((DoubleWriteFieldAccess)res).value;
			}
			ver = (VersionD)box.get(localClock);
			readSet.getNext().init(box, ver);
		}
		else {
			ver = (VersionD)box.get(localClock);
		}
		
		return ver.value;
	}

	public void onWriteAccess(Object value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		ObjectWriteFieldAccess write = objectPool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(boolean value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		BooleanWriteFieldAccess write = booleanPool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(byte value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		ByteWriteFieldAccess write = bytePool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(char value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		CharWriteFieldAccess write = charPool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(short value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		ShortWriteFieldAccess write = shortPool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(int value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		IntWriteFieldAccess write = intPool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(long value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		LongWriteFieldAccess write = longPool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(float value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		FloatWriteFieldAccess write = floatPool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	public void onWriteAccess(double value, TxField field) {
		if (!readWriteHint) {
			readWriteMarkers.insert(atomicBlockId, true);
			throw READ_ONLY_FAILURE_EXCEPTION;
		}
		
		DoubleWriteFieldAccess write = doublePool.getNext();
		write.init((VBox)field);
		write.value = value;
		writeSet.put(write);
	}

	
	private static class ObjectResourceFactory implements ResourceFactory<ObjectWriteFieldAccess>{
		@Override
		public ObjectWriteFieldAccess newInstance() {
			return new ObjectWriteFieldAccess();
		}
	}
	final private Pool<ObjectWriteFieldAccess> objectPool = new Pool<ObjectWriteFieldAccess>(new ObjectResourceFactory());

	private static class BooleanResourceFactory implements ResourceFactory<BooleanWriteFieldAccess>{
		@Override
		public BooleanWriteFieldAccess newInstance() {
			return new BooleanWriteFieldAccess();
		}
	}
	final private Pool<BooleanWriteFieldAccess> booleanPool = new Pool<BooleanWriteFieldAccess>(new BooleanResourceFactory());

	private static class ByteResourceFactory implements ResourceFactory<ByteWriteFieldAccess>{
		@Override
		public ByteWriteFieldAccess newInstance() {
			return new ByteWriteFieldAccess();
		}
	}
	final private Pool<ByteWriteFieldAccess> bytePool = new Pool<ByteWriteFieldAccess>( new ByteResourceFactory());

	private static class CharResourceFactory implements ResourceFactory<CharWriteFieldAccess>{
		@Override
		public CharWriteFieldAccess newInstance() {
			return new CharWriteFieldAccess();
		}
	}
	final private Pool<CharWriteFieldAccess> charPool = new Pool<CharWriteFieldAccess>(new CharResourceFactory());

	private static class ShortResourceFactory implements ResourceFactory<ShortWriteFieldAccess>{
		@Override
		public ShortWriteFieldAccess newInstance() {
			return new ShortWriteFieldAccess();
		}
	}
	final private Pool<ShortWriteFieldAccess> shortPool = new Pool<ShortWriteFieldAccess>( new ShortResourceFactory());

	private static class IntResourceFactory implements ResourceFactory<IntWriteFieldAccess>{
		@Override
		public IntWriteFieldAccess newInstance() {
			return new IntWriteFieldAccess();
		}
	}
	final private Pool<IntWriteFieldAccess> intPool = new Pool<IntWriteFieldAccess>( new IntResourceFactory());

	private static class LongResourceFactory implements ResourceFactory<LongWriteFieldAccess>{
		@Override
		public LongWriteFieldAccess newInstance() {
			return new LongWriteFieldAccess();
		}
	}
	final private Pool<LongWriteFieldAccess> longPool = new Pool<LongWriteFieldAccess>( new LongResourceFactory());
	
	private static class FloatResourceFactory implements ResourceFactory<FloatWriteFieldAccess>{
		@Override
		public FloatWriteFieldAccess newInstance() {
			return new FloatWriteFieldAccess();
		}
	}
	final private Pool<FloatWriteFieldAccess> floatPool = new Pool<FloatWriteFieldAccess>( new FloatResourceFactory());
	
	private static class DoubleResourceFactory implements ResourceFactory<DoubleWriteFieldAccess>{
		@Override
		public DoubleWriteFieldAccess newInstance() {
			return new DoubleWriteFieldAccess();
		}
	}
	final private Pool<DoubleWriteFieldAccess> doublePool = new Pool<DoubleWriteFieldAccess>( new DoubleResourceFactory());


	@Override
	public void onIrrevocableAccess() {
		
	}
	

	

}
