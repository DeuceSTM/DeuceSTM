package org.deuce.transaction.tl2cm;

import java.util.Iterator;

import org.deuce.transaction.tl2cm.field.ReadFieldAccess;
import org.deuce.transaction.tl2cm.field.WriteFieldAccess;
import org.deuce.transform.Exclude;
import org.deuce.trove.THashMap;
import org.deuce.trove.TObjectProcedure;

/**
 * Represents the transaction write set. Based on Guy Korland's work on <code>org.deuce.transaction.tl2.*</code>
 * 
 * @author Yoav Cohen, yoav.cohen@cs.tau.ac.il
 */
@Exclude
public class WriteSet implements Iterable<WriteFieldAccess>{
	
	final private THashMap<WriteFieldAccess,WriteFieldAccess> writeSet = 
		new THashMap<WriteFieldAccess,WriteFieldAccess>( 16);
	
	public void clear() {
		writeSet.clear();
	}

	public boolean isEmpty() {
		return writeSet.isEmpty();
	}

	public Iterator<WriteFieldAccess> iterator() {
		// Use the value and not the key since the key might hold old key.
		// Might happen if the same field was update more than once.
		return writeSet.values().iterator();
	}

	public boolean forEach(TObjectProcedure<WriteFieldAccess> procedure){
		return writeSet.forEach(procedure);
	}
	
	public void put(WriteFieldAccess write) {
		// Add to write set
		writeSet.put( write, write);
	}
	
	public WriteFieldAccess contains(ReadFieldAccess read) {
		// Check if it is already included in the write set
		return writeSet.get( read);
	}
	
	public int size() {
		return writeSet.size();
	}
	
}
