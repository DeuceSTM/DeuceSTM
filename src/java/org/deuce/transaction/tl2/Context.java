package org.deuce.transaction.tl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.deuce.transaction.AbstractContext;
import org.deuce.transaction.TransactionException;
import org.deuce.transaction.tl2.field.ReadFieldAccess;
import org.deuce.transaction.tl2.field.WriteFieldAccess;
import org.deuce.transaction.tl2.field.WriteFieldAccess.Type;
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
	final private HashMap<WriteFieldAccess,Object> writeSet = new HashMap<WriteFieldAccess,Object>( 50);
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
			int unLockedCounter = 0;
			for( WriteFieldAccess writeField : writeSet.keySet()){
				if( unLockedCounter++ >= lockedCounter)
					break;
				LockTable.unLock( writeField.hashCode());
			}
			logger.fine("Fail on commit.");
			return false;
		}

		int newClock = clock.incrementAndGet();

		for( Map.Entry<WriteFieldAccess,Object> writeEntry : writeSet.entrySet()){
			WriteFieldAccess writeField = writeEntry.getKey();
			writeField.put( writeEntry.getValue());
			
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

	@Override
	public Object addReadAccess( Object obj, Object value, long field){

		logger.finest("Read access.");

		ReadFieldAccess read = new ReadFieldAccess( obj, field);
		int hash = read.hashCode();

		// Check the read is still valid
		LockTable.checkLock(hash, localClock);

		// Save to read set
		readSet.add( read);

		// Check if it is already included in the write set
		if( bloomFilter.contains(hash)){
			Object newValue = writeSet.get( read);
			if( newValue != null)
				return newValue;
		}

		return value;
	}

	private void addWriteAccess0( Object obj, long field, Object value, Type type){

		logger.finer("Write access.");

		WriteFieldAccess write = new WriteFieldAccess( obj, field, type);

		// Add to bloom filter
		bloomFilter.add( write.hashCode());

		// Add to write set
		writeSet.put( write, value);
	}

	@Override
	public boolean addReadAccess(Object obj, boolean value, long field) {
		return (Boolean)addReadAccess(obj, (Object)value, field);
	}

	@Override
	public byte addReadAccess(Object obj, byte value, long field) {
		return ((Number)addReadAccess(obj, (Object)value, field)).byteValue();
	}

	@Override
	public char addReadAccess(Object obj, char value, long field) {
		return (Character)addReadAccess(obj, (Object)value, field);
	}

	@Override
	public short addReadAccess(Object obj, short value, long field) {
		return ((Number)addReadAccess(obj, (Object)value, field)).shortValue();
	}

	@Override
	public int addReadAccess(Object obj, int value, long field) {
		return ((Number)addReadAccess(obj, (Object)value, field)).intValue();
	}

	@Override
	public long addReadAccess(Object obj, long value, long field) {
		return ((Number)addReadAccess(obj, (Object)value, field)).longValue();
	}

	@Override
	public float addReadAccess(Object obj, float value, long field) {
		return ((Number)addReadAccess(obj, (Object)value, field)).floatValue();
	}

	@Override
	public double addReadAccess(Object obj, double value, long field) {
		return ((Number)addReadAccess(obj, (Object)value, field)).doubleValue();
	}

	@Override
	public void addWriteAccess( Object obj, Object value, long field){
		addWriteAccess0( obj, field, value, Type.OBJECT);
	}

	@Override
	public void addWriteAccess(Object obj, boolean value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.BOOLEAN);
	}

	@Override
	public void addWriteAccess(Object obj, byte value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.BYTE);
	}

	@Override
	public void addWriteAccess(Object obj, char value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.CHAR);
	}

	@Override
	public void addWriteAccess(Object obj, short value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.SHORT);
	}

	@Override
	public void addWriteAccess(Object obj, int value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.INT);
	}

	@Override
	public void addWriteAccess(Object obj, long value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.LONG);
	}

	@Override
	public void addWriteAccess(Object obj, float value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.FLOAT);
	}

	@Override
	public void addWriteAccess(Object obj, double value, long field) {
		addWriteAccess0(obj, field, (Object)value, Type.DOUBLE);	
	}
}
