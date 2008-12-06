package org.deuce.transaction.tl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

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
final public class Context implements org.deuce.transaction.Context
{
	final private static AtomicInteger clock = new AtomicInteger( 0);
	final private static Logger logger = Logger.getLogger("org.deuce.transaction.tl2");

	final private ArrayList<ReadFieldAccess> readSet = new ArrayList<ReadFieldAccess>( 50);
	final private HashMap<WriteFieldAccess,WriteFieldAccess> writeSet = new HashMap<WriteFieldAccess,WriteFieldAccess>( 50);
	final private BloomFilter bloomFilter = new BloomFilter();
	
	private int localClock;
	private ReadFieldAccess lastRead = null;
	private int lastReadLock;

	public Context(){
		this.localClock = clock.get();
	}

	
	public void init(String method){
		logger.fine("Init transaction.");
		this.bloomFilter.clear();
		this.readSet.clear(); // TODO reuse the same read set objects 
		this.writeSet.clear();
		this.localClock = clock.get();
	}

	
	public boolean commit(){
		logger.fine("Start to commit.");

        if (writeSet.isEmpty()) // if the writeSet is empty no need to lock a thing. 
        	return true;
        		
		int lockedCounter = 0;//used to count how many fields where locked if unlock is needed 
		try
		{
			for( WriteFieldAccess writeField : writeSet.keySet()){
				LockTable.lock( writeField.hashCode());
				++lockedCounter;
			}
			for( ReadFieldAccess readField : readSet){
				LockTable.checkLock( readField.hashCode(), localClock);
			}
		}
		catch( TransactionException exception){
			for( WriteFieldAccess writeField : writeSet.keySet()){
				if( lockedCounter-- == 0)
					break;
				LockTable.unLock( writeField.hashCode());
			}
			logger.fine("Fail on commit.");
			return false;
		}

		final int newClock = clock.incrementAndGet();

		for( Map.Entry<WriteFieldAccess,WriteFieldAccess> writeEntry : writeSet.entrySet()){
			
			// Use the value and not the key since the key might hold hold key.
			WriteFieldAccess writeField = writeEntry.getValue(); 
			writeField.put(); // commit value to field
			LockTable.setAndReleaseLock( writeField.hashCode(), newClock);
			
		}
		logger.fine("Commit successed.");
		return true;
	}

	
	public void rollback(){
		logger.fine("Start to rollback.");
		//	init(); // TODO maybe clean the sets
	}

	private WriteFieldAccess addReadAccess0( Object obj, long field){

		logger.finest("Read access.");

		int hash = lastRead.hashCode();

		// Check the read is still valid
		LockTable.checkLock(hash, localClock, lastReadLock);

		// Save to read set
		readSet.add( lastRead);
		
		// Check if it is already included in the write set
		return bloomFilter.contains(hash) ? writeSet.get( lastRead): null;
	}

	private void addWriteAccess0( WriteFieldAccess write){

		logger.finer("Write access.");

		// Add to bloom filter
		bloomFilter.add( write.hashCode());

		// Add to write set
		writeSet.put( write, write);
	}
	
	public void beforeReadAccess(Object obj, long field) {
		
		logger.finest("Before read access.");

		lastRead = new ReadFieldAccess( obj, field);
		int hash = lastRead.hashCode();

		// Check the read is still valid
		lastReadLock = LockTable.checkLock(hash, localClock);
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
