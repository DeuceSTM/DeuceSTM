package org.deuce.transaction.jvstm.field;


import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxDBody implements VBoxBody {

	public volatile int version;
	public VBoxDBody next;
	public volatile double value;

	public VBoxDBody(double value, int version, VBoxDBody next) {
		this.version = version;
		this.next = next;
		this.value = value;
	}

	public VBoxDBody getBody(int maxVersion) {
//		return ((version > maxVersion) ? next.getBody(maxVersion) : this);
		VBoxDBody res = this;
		while(res.version > maxVersion) { res = res.next; }
		return res;
	}

	public void clearPrevious() {
		this.next = null;
	}
}
