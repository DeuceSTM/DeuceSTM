/**
 * JVSTM Implementation
 * 
 * JVSTM is a multiversion STM for Java developed by Joao Cachopo 
 * from INESC-ID.
 * 
 * @author Ricardo Dias
 */
package org.deuce.transaction.jvstm.field;


import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxZBody implements VBoxBody {

	public volatile int version;
	public VBoxZBody next;
	public volatile boolean value;

	public VBoxZBody(boolean value, int version, VBoxZBody next) {
		this.version = version;
		this.next = next;
		this.value = value;
	}

	public VBoxZBody getBody(int maxVersion) {
//		return ((version > maxVersion) ? next.getBody(maxVersion) : this);
		VBoxZBody res = this;
		while(res.version > maxVersion) { res = res.next; }
		return res;
	}

	public void clearPrevious() {
		this.next = null;
	}
}
