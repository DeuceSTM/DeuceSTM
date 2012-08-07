package org.deuce.transaction.jvstminnogc.field;

import org.deuce.transaction.jvstminnogc.Context;
import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class Version {
	public volatile int version;	
	public Version next;
	public int size;
	
	public Version(int version, Version next) {
		this.version = version;
		this.next = next;
		this.size = next != null ? next.size+1 : 1;
		if (size == Context.MAX_VERSIONS) {
			cleanVersions();
			size = Context.MAX_VERSIONS >>> 1;
		}
	}
	
	private void cleanVersions() {
		int c = Context.MAX_VERSIONS >>> 1;
		Version v = this;
		while(c > 1) {
			v = v.next;
			c--;
		}
		v.next = null;
	}
	
	public Version get(int maxVersion) {
		Version res = this;
		while(res.version > maxVersion) { 
			res = res.next;
			if (res == null) {
				throw Context.VERSION_UNAVAILABLE_EXCEPTION;
			}
		}
		return res;
	}
}
