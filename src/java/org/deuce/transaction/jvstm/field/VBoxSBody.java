package org.deuce.transaction.jvstm.field;


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
