/**
 * JVSTM Implementation
 * 
 * JVSTM is a multiversion STM for Java developed by Joao Cachopo 
 * from INESC-ID.
 * 
 * @author Ricardo Dias
 */
package org.deuce.transaction.jvstminplace.field;


import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxSBody implements VBoxBody {

	public volatile int version;
	public VBoxSBody next;
	public volatile short value;

	public VBoxSBody(short value, int version, VBoxSBody next) {
		this.version = version;
		this.next = next;
		this.value = value;
	}

	public VBoxSBody getBody(int maxVersion) {
//		return ((version > maxVersion) ? next.getBody(maxVersion) : this);
		VBoxSBody res = this;
		while(res.version > maxVersion) { res = res.next; }
		return res;
	}

	public void clearPrevious() {
		this.next = null;
	}
}
