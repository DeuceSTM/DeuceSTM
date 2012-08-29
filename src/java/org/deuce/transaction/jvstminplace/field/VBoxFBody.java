package org.deuce.transaction.jvstminplace.field;


import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VBoxFBody implements VBoxBody {

	public volatile int version;
	public VBoxFBody next;
	public volatile float value;

	public VBoxFBody(float value, int version, VBoxFBody next) {
		this.version = version;
		this.next = next;
		this.value = value;
	}

	public VBoxFBody getBody(int maxVersion) {
//		return ((version > maxVersion) ? next.getBody(maxVersion) : this);
		VBoxFBody res = this;
		while(res.version > maxVersion) { res = res.next; }
		return res;
	}

	public void clearPrevious() {
		this.next = null;
	}
}
