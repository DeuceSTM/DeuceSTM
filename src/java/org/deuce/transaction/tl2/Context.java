package org.deuce.transaction.tl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.deuce.transaction.AbstractContext;
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
final public class Context extends AbstractContext
{
	final private static AtomicInteger clock = new AtomicInteger( 0);
	private static final Logger logger = Logger.getLogger("org.deuce.transaction.tl2");

	final private ArrayList<ReadFieldAccess> readSet = new ArrayList<ReadFieldAccess>( 50);
	final private HashMap<WriteFieldAccess,WriteFieldAccess> writeSet = new HashMap<WriteFieldAccess,WriteFieldAccess>( 50);
	private int localClock;

	private BloomFilter bloomFilter = new BloomFilter();

	public Context(){
		this.localClock = clock.get();
	}

	@Override
	public void init(){
		logger.fine("Init transaction.");
		this.bloomFilter.clear();
		this.readSet.clear(); // TODO reuse the same read set objects 
		this.writeSet.clear();
		this.localClock = clock.get();
	}

	@Override
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

		int newClock = clock.incrementAndGet();

		for( Map.Entry<WriteFieldAccess,WriteFieldAccess> writeEntry : writeSet.entrySet()){
			WriteFieldAccess writeField = writeEntry.getKey();
			writeField.put(); // commit value to field
			LockTable.setAndReleaseLock( writeField.hashCode(), newClock);
			
		}
		logger.fine("Commit successed.");
		return true;
	}

	@Override
	public void rollback(){
		logger.fine("Start to rollback.");
		//	init(); // TODO maybe clean the sets
	}

	private WriteFieldAccess addReadAccess0( Object obj, long field){

		logger.finest("Read access.");

		ReadFieldAccess read = new ReadFieldAccess( obj, field);
		int hash = read.hashCode();

		// Check the read is still valid
		LockTable.checkLock(hash, localClock);

		// Save to read set
		readSet.add( read);
		
		// Check if it is already included in the write set
		return bloomFilter.contains(hash) ? writeSet.get( read): null;
	}

	private void addWriteAccess0( WriteFieldAccess write){

		logger.finer("Write access.");

		// Add to bloom filter
		bloomFilter.add( write.hashCode());

		// Add to write set
		writeSet.put( write, write);
	}
	
	@Override
	public Object addReadAccess( Object obj, Object value, long field){
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ObjectWriteFieldAccess)writeAccess).getValue();  
	}
		

	@Override
	public boolean addReadAccess(Object obj, boolean value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((BooleanWriteFieldAccess)writeAccess).getValue();  
	}

	@Override
	public byte addReadAccess(Object obj, byte value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ByteWriteFieldAccess)writeAccess).getValue();  
	}

	@Override
	public char addReadAccess(Object obj, char value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((CharWriteFieldAccess)writeAccess).getValue();  
	}

	@Override
	public short addReadAccess(Object obj, short value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((ShortWriteFieldAccess)writeAccess).getValue();  

	}

	@Override
	public int addReadAccess(Object obj, int value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((IntWriteFieldAccess)writeAccess).getValue();  
	}

	@Override
	public long addReadAccess(Object obj, long value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((LongWriteFieldAccess)writeAccess).getValue();  
	}

	@Override
	public float addReadAccess(Object obj, float value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((FloatWriteFieldAccess)writeAccess).getValue();  
	}

	@Override
	public double addReadAccess(Object obj, double value, long field) {
		WriteFieldAccess writeAccess = addReadAccess0(obj, field);
		if( writeAccess == null)
			return value;
		
		return ((DoubleWriteFieldAccess)writeAccess).getValue();  
	}

	@Override
	public void addWriteAccess( Object obj, Object value, long field){
		addWriteAccess0( new ObjectWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, boolean value, long field) {
		addWriteAccess0( new BooleanWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, byte value, long field) {
		addWriteAccess0( new ByteWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, char value, long field) {
		addWriteAccess0( new CharWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, short value, long field) {
		addWriteAccess0( new ShortWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, int value, long field) {
		addWriteAccess0( new IntWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, long value, long field) {
		addWriteAccess0( new LongWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, float value, long field) {
		addWriteAccess0( new FloatWriteFieldAccess( value, obj, field));
	}

	@Override
	public void addWriteAccess(Object obj, double value, long field) {
		addWriteAccess0( new DoubleWriteFieldAccess( value, obj, field));	
	}
}
