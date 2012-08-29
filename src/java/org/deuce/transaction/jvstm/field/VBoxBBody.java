package org.deuce.transaction.jvstm.field;


import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxBBody implements VBoxBody {

	public volatile int version;
	public VBoxBBody next;
	public volatile byte value;

	public VBoxBBody(byte value, int version, VBoxBBody next) {
		this.version = version;
		this.next = next;
		this.value = value;
	}

	public VBoxBBody getBody(int maxVersion) {
//		return ((version > maxVersion) ? next.getBody(maxVersion) : this);
		VBoxBBody res = this;
		while(res.version > maxVersion) { res = res.next; }
		return res;
	}

	public void clearPrevious() {
		this.next = null;
	}
}
