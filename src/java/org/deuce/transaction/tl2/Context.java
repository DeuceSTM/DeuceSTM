package org.deuce.transaction.tl2;

import java.util.concurrent.atomic.AtomicInteger;

import org.deuce.transaction.TransactionException;
import org.deuce.transaction.tl2.field.BooleanWriteFieldAccess;
import org.deuce.transaction.tl2.field.ByteWriteFieldAccess;
import org.deuce.transaction.tl2.field.CharWriteFieldAccess;
import org.deuce.transaction.tl2.field.DoubleWriteFieldAccess;
import org.deuce.transaction.tl2.field.FloatWriteFieldAccess;
import org.deuce.transaction.tl2.field.IntWriteFieldAccess;
import org.deuce.transaction.tl2.field.LongWriteFieldAccess;
import org.deuce.transaction.tl2.field.ObjectWriteFieldAccess;
import org.deuce.transaction.tl2.field.ReadFieldAccess;
import org.deuce.transaction.tl2.field.ShortWriteFieldAccess;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transaction.tl2.pool.Pool;
import org.deuce.transaction.tl2.pool.ResourceFactory;
import org.deuce.transform.Exclude;

/**
 * TL2 implementation
 *
 * @author	Guy Korland
 * @since	1.0
 */
@Exclude
final public class Context implements org.deuce.transaction.Context{
	
	final private static AtomicInteger clock = new AtomicInteger( 0);

	final private ReadSet readSet = new ReadSet();
	final private WriteSet writeSet = new WriteSet();
		
	//Used by the thread to mark locks it holds.
	final private byte[] locksMarker = new byte[LockTable.LOCKS_SIZE /8 + 1];
	
	//Marked on beforeRead, used for the double lock check
	private int localClock;
	private int lastReadLock;
	
	public Context(){
		this.localClock = clock.get();
	}
	
	public void init(int atomicBlockId){
		
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
	}
	
	public boolean commit(){
        if (writeSet.isEmpty()) // if the writeSet is empty no need to lock a thing. 
        	return true;
        		
		int lockedCounter = 0;//used to count how many fields where locked if unlock is needed 
		try
		{
			for( WriteFieldAccess writeField : writeSet){
				LockTable.lock( writeField.hashCode(), locksMarker);
				++lockedCounter;
			}
			readSet.checkClock( localClock);
		}
		catch( TransactionException exception){
			for( WriteFieldAccess writeField : writeSet){
				if( lockedCounter-- == 0)
					break;
				LockTable.unLock( writeField.hashCode(),locksMarker);
			}
			return false;
		}

		final int newClock = clock.incrementAndGet();

		for( WriteFieldAccess writeField : writeSet){
			writeField.put(); // commit value to field
			LockTable.setAndReleaseLock( writeField.hashCode(), newClock, locksMarker);
		}
		return true;
	}
	
	public void rollback(){
	}

	private WriteFieldAccess onReadAccess0( Object obj, long field){

		ReadFieldAccess current = readSet.getCurrent();
		int hash = current.hashCode();

		// Check the read is still valid
		LockTable.checkLock(hash, localClock, lastReadLock);

		// Check if it is already included in the write set
		return writeSet.contains( current);
	}

	private void addWriteAccess0( WriteFieldAccess write){

		// Add to write set
		writeSet.put( write);
	}
	
	public void beforeReadAccess(Object obj, long field) {
		
		ReadFieldAccess next = readSet.getNext();
		next.init(obj, field);

		// Check the read is still valid
		lastReadLock = LockTable.checkLock(next.hashCode(), localClock);
	}
	
	public Object onReadAccess( Object obj, Object value, long field){
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ObjectWriteFieldAccess)writeAccess).getValue();  
	}
		
	public boolean onReadAccess(Object obj, boolean value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((BooleanWriteFieldAccess)writeAccess).getValue();  
	}
	
	public byte onReadAccess(Object obj, byte value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ByteWriteFieldAccess)writeAccess).getValue();  
	}
	
	public char onReadAccess(Object obj, char value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((CharWriteFieldAccess)writeAccess).getValue();  
	}
	
	public short onReadAccess(Object obj, short value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ShortWriteFieldAccess)writeAccess).getValue();  

	}
	
	public int onReadAccess(Object obj, int value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((IntWriteFieldAccess)writeAccess).getValue();  
	}
	
	public long onReadAccess(Object obj, long value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((LongWriteFieldAccess)writeAccess).getValue();  
	}
	
	public float onReadAccess(Object obj, float value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((FloatWriteFieldAccess)writeAccess).getValue();  
	}
	
	public double onReadAccess(Object obj, double value, long field) {
		WriteFieldAccess writeAccess = onReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((DoubleWriteFieldAccess)writeAccess).getValue();  
	}
	
	public void onWriteAccess( Object obj, Object value, long field){
		ObjectWriteFieldAccess next = objectPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, boolean value, long field) {
		
		BooleanWriteFieldAccess next = booleanPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, byte value, long field) {
		
		ByteWriteFieldAccess next = bytePool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, char value, long field) {
		
		CharWriteFieldAccess next = charPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, short value, long field) {
		
		ShortWriteFieldAccess next = shortPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, int value, long field) {
		
		IntWriteFieldAccess next = intPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}
	
	public void onWriteAccess(Object obj, long value, long field) {
		
		LongWriteFieldAccess next = longPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	public void onWriteAccess(Object obj, float value, long field) {
		
		FloatWriteFieldAccess next = floatPool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}

	
	public void onWriteAccess(Object obj, double value, long field) {
		
		DoubleWriteFieldAccess next = doublePool.getNext();
		next.set(value, obj, field);
		addWriteAccess0(next);
	}
	
	private Pool<ObjectWriteFieldAccess> objectPool = new Pool<ObjectWriteFieldAccess>( new ResourceFactory<ObjectWriteFieldAccess>(){
		public ObjectWriteFieldAccess newInstance() {
			return new ObjectWriteFieldAccess();
		}
	});
	
	private Pool<BooleanWriteFieldAccess> booleanPool = new Pool<BooleanWriteFieldAccess>( new ResourceFactory<BooleanWriteFieldAccess>(){
		public BooleanWriteFieldAccess newInstance() {
			return new BooleanWriteFieldAccess();
		}
	});
	
	private Pool<ByteWriteFieldAccess> bytePool = new Pool<ByteWriteFieldAccess>( new ResourceFactory<ByteWriteFieldAccess>(){
		public ByteWriteFieldAccess newInstance() {
			return new ByteWriteFieldAccess();
		}
	});
	
	private Pool<CharWriteFieldAccess> charPool = new Pool<CharWriteFieldAccess>( new ResourceFactory<CharWriteFieldAccess>(){
		public CharWriteFieldAccess newInstance() {
			return new CharWriteFieldAccess();
		}
	});
	
	private Pool<ShortWriteFieldAccess> shortPool = new Pool<ShortWriteFieldAccess>( new ResourceFactory<ShortWriteFieldAccess>(){
		public ShortWriteFieldAccess newInstance() {
			return new ShortWriteFieldAccess();
		}
	});
	
	private Pool<IntWriteFieldAccess> intPool = new Pool<IntWriteFieldAccess>( new ResourceFactory<IntWriteFieldAccess>(){
		public IntWriteFieldAccess newInstance() {
			return new IntWriteFieldAccess();
		}
	});
	
	private Pool<LongWriteFieldAccess> longPool = new Pool<LongWriteFieldAccess>( new ResourceFactory<LongWriteFieldAccess>(){
		public LongWriteFieldAccess newInstance() {
			return new LongWriteFieldAccess();
		}
	});
	
	private Pool<FloatWriteFieldAccess> floatPool = new Pool<FloatWriteFieldAccess>( new ResourceFactory<FloatWriteFieldAccess>(){
		public FloatWriteFieldAccess newInstance() {
			return new FloatWriteFieldAccess();
		}
	});
	
	private Pool<DoubleWriteFieldAccess> doublePool = new Pool<DoubleWriteFieldAccess>( new ResourceFactory<DoubleWriteFieldAccess>(){
		public DoubleWriteFieldAccess newInstance() {
			return new DoubleWriteFieldAccess();
		}
	});
}
