package org.deuce.transaction.tl2;

import org.deuce.transaction.tl2.field.ReadFieldAccess;

/**
 * Represents the transaction read set.
 * And acts as a recycle pool of the {@link ReadFieldAccess}.
 *  
 * @author Guy Korland
 * @since 0.7
 */
public class ReadSet{
	
	private static final int DEFAULT_CAPACITY = 1024;
	private ReadFieldAccess[] readSet = new ReadFieldAccess[DEFAULT_CAPACITY];
	private int nextAvaliable = 0;
	private ReadFieldAccess currentReadFieldAccess = null;
	
	public ReadSet(){
		fillArray( 0);
	}
	
	public void clear(){
		nextAvaliable = 0;
	}

	private void fillArray( int offset){
		for( int i=offset ; i < DEFAULT_CAPACITY + offset ; ++i){
			readSet[i] = new ReadFieldAccess();
		}
	}

	public ReadFieldAccess getNext(){
		if( nextAvaliable >= readSet.length){
			int orignLength = readSet.length;
			ReadFieldAccess[] tmpReadSet = new ReadFieldAccess[ orignLength + DEFAULT_CAPACITY];
			System.arraycopy(readSet, 0, tmpReadSet, 0, readSet.length);
			readSet = tmpReadSet;
			fillArray( orignLength);
			currentReadFieldAccess = new ReadFieldAccess();
		}
		currentReadFieldAccess = readSet[ nextAvaliable++];
		return currentReadFieldAccess;
	}
	
	public ReadFieldAccess getCurrent(){
		return currentReadFieldAccess;
	}
	
    public void checkClock(int clock) {
        for (int i = 0; i < nextAvaliable; i++) {
        	LockTable.checkLock( readSet[i].hashCode(), clock);
        }
    }
    
    public interface ReadSetListener{
    	void execute( ReadFieldAccess read);
    }
	
}
