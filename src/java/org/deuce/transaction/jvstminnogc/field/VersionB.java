package org.deuce.transaction.jvstminnogc.field;


import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class VersionB extends Version {
	public byte value;

	public VersionB(int version, byte value, Version next) {
		super(version, next);
		this.value = value;
	}
	
}
