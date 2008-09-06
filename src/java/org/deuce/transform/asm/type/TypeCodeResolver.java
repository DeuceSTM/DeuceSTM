package org.deuce.transform.asm.type;

public abstract class TypeCodeResolver {
	abstract public int loadCode();
	abstract public int storeCode();
	abstract public int returnCode();
	abstract public int nullValueCode();
	/** Returns this type size in the Locals table */
	public int localSize(){
		return 1; //  no extend, 32 bit 
	}
}
