package org.deuce.transaction.mvstm.field;

import org.deuce.transaction.mvstm.Context;
import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class VersionList<T extends Version> {
	
	public Version[] versions = new Version[Context.MAX_VERSIONS];
	public int curr = 0;
	
	public VersionList(int version, Object value) {
		set(version, value);
	}
	
	public VersionList(int version, int value) {
		set(version, value);
	}
	
	public VersionList(int version, short value) {
		set(version, value);
	}
	
	public VersionList(int version, char value) {
		set(version, value);
	}
	
	public VersionList(int version, byte value) {
		set(version, value);
	}
	
	public VersionList(int version, boolean value) {
		set(version, value);
	}
	
	public VersionList(int version, float value) {
		set(version, value);
	}
	
	public VersionList(int version, long value) {
		set(version, value);
	}
	
	public VersionList(int version, double value) {
		set(version, value);
	}
	
	public VersionO set(int version, Object value) {
		int v = curr;
		VersionO ver = new VersionO(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionI set(int version, int value) {
		int v = curr;
		VersionI ver = new VersionI(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionB set(int version, byte value) {
		int v = curr;
		VersionB ver = new VersionB(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionZ set(int version, boolean value) {
		int v = curr;
		VersionZ ver = new VersionZ(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionC set(int version, char value) {
		int v = curr;
		VersionC ver = new VersionC(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionF set(int version, float value) {
		int v = curr;
		VersionF ver = new VersionF(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionL set(int version, long value) {
		int v = curr;
		VersionL ver = new VersionL(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionD set(int version, double value) {
		int v = curr;
		VersionD ver = new VersionD(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	public VersionS set(int version, short value) {
		int v = curr;
		VersionS ver = new VersionS(version, value, null);
		versions[v] = ver;
		
		curr = (curr+1)&(Context.MAX_VERSIONS-1);
		return ver;
	}
	
	
	public Version get(int version) {
		
//		// fast-path: only works with curr as an AtomicInteger
//		VersionO old = versions[curr];
//		if (old != null && old.version > version) {
//			throw new TransactionException();
//		}
		
		int v = (curr-1)&(Context.MAX_VERSIONS-1);
		do {
			Version ver = versions[v]; 
			if (ver.version <= version) {
				return ver; 
			}
			v = (v-1)&(Context.MAX_VERSIONS-1);
		} while(v != curr);
		throw Context.VERSION_UNAVAILABLE_EXCEPTION;
	}
	
	public Version getLast() {
		return versions[(curr-1)&(Context.MAX_VERSIONS-1)];
	}
	
	public boolean isLast(int version) {
		return versions[(curr-1)&(Context.MAX_VERSIONS-1)].version == version;
	}
	
	public boolean isLast(T ver) {
		return versions[(curr-1)&(Context.MAX_VERSIONS-1)] == ver;
	}
	
}
