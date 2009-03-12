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

	private WriteFieldAccess addReadAccess0( Object obj, long field){

		int hash = readSet.getCurrent().hashCode();

		// Check the read is still valid
		LockTable.checkLock(hash, localClock, lastReadLock);

		// Check if it is already included in the write set
		return writeSet.contains( readSet.getCurrent());
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
	
	public Object addReadAccess( Object obj, Object value, long field){
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ObjectWriteFieldAccess)writeAccess).getValue();  
	}
		
	public boolean addReadAccess(Object obj, boolean value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((BooleanWriteFieldAccess)writeAccess).getValue();  
	}
	
	public byte addReadAccess(Object obj, byte value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ByteWriteFieldAccess)writeAccess).getValue();  
	}
	
	public char addReadAccess(Object obj, char value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((CharWriteFieldAccess)writeAccess).getValue();  
	}
	
	public short addReadAccess(Object obj, short value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ShortWriteFieldAccess)writeAccess).getValue();  

	}
	
	public int addReadAccess(Object obj, int value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((IntWriteFieldAccess)writeAccess).getValue();  
	}
	
	public long addReadAccess(Object obj, long value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((LongWriteFieldAccess)writeAccess).getValue();  
	}
	
	public float addReadAccess(Object obj, float value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((FloatWriteFieldAccess)writeAccess).getValue();  
	}
	
	public double addReadAccess(Object obj, double value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((DoubleWriteFieldAccess)writeAccess).getValue();  
	}
	
	public void addWriteAccess( Object obj, Object value, long field){
		addWriteAccess0( new ObjectWriteFieldAccess( value, obj, field));
	}
	
	public void addWriteAccess(Object obj, boolean value, long field) {
		addWriteAccess0( new BooleanWriteFieldAccess( value, obj, field));
	}
	
	public void addWriteAccess(Object obj, byte value, long field) {
		addWriteAccess0( new ByteWriteFieldAccess( value, obj, field));
	}
	
	public void addWriteAccess(Object obj, char value, long field) {
		addWriteAccess0( new CharWriteFieldAccess( value, obj, field));
	}
	
	public void addWriteAccess(Object obj, short value, long field) {
		addWriteAccess0( new ShortWriteFieldAccess( value, obj, field));
	}
	
	public void addWriteAccess(Object obj, int value, long field) {
		addWriteAccess0( new IntWriteFieldAccess( value, obj, field));
	}
	
	public void addWriteAccess(Object obj, long value, long field) {
		addWriteAccess0( new LongWriteFieldAccess( value, obj, field));
	}

	public void addWriteAccess(Object obj, float value, long field) {
		addWriteAccess0( new FloatWriteFieldAccess( value, obj, field));
	}

	
	public void addWriteAccess(Object obj, double value, long field) {
		addWriteAccess0( new DoubleWriteFieldAccess( value, obj, field));	
	}
}
