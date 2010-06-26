package org.deuce.transaction.tl2cm;

import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transform.Exclude;

/**
 * Represents the transaction read set. Based on Guy Korland's work on <code>org.deuce.transaction.tl2.*</code>
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
public class ReadSet{
	
	private static final int DEFAULT_CAPACITY = 1024;
	private ReadFieldAccess[] readSet = new ReadFieldAccess[DEFAULT_CAPACITY];
	private int nextAvaliable = 0;
	private ReadFieldAccess currentReadFieldAccess = null;
	
	
	public ReadSet(){
		fillArray( 0);
	}
	
	public void clear() {
		nextAvaliable = 0;
	}

	private void fillArray( int offset){
		for( int i=offset ; i < readSet.length ; ++i){
			readSet[i] = new ReadFieldAccess();
		}
	}

	public ReadFieldAccess getNext() {
		if( nextAvaliable >= readSet.length){
			int orignLength = readSet.length;
			ReadFieldAccess[] tmpReadSet = new ReadFieldAccess[ 2*orignLength];
			System.arraycopy(readSet, 0, tmpReadSet, 0, orignLength);
			readSet = tmpReadSet;
			fillArray( orignLength);
		}
		currentReadFieldAccess = readSet[ nextAvaliable++];
		return currentReadFieldAccess;
	}
	
	public ReadFieldAccess getCurrent(){
		return currentReadFieldAccess;
	}
	
    public boolean validate(int version) {
        for (int i = 0; i < nextAvaliable; i++) {
        	ReadFieldAccess field = readSet[i];
			int hash = field.hashCode();
        	long lock = LockTable.getLock(hash);
        	int lockVersion = LockTable.getVersion(lock);
        	if (lockVersion > version) {
        		return false;
        	}
        }
        return true;
    }
    
    public int size() {
    	return nextAvaliable;
    }
    
    public interface ReadSetListener{
    	void execute( ReadFieldAccess read);
    }
	
}
