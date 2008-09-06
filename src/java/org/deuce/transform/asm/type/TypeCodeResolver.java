package org.deuce.transform.asm.type;

public abstract class TypeCodeResolver {
	abstract public int loadCode();
	abstract public int storeCode();
	abstract public int returnCode();
	abstract public int nullValueCode();
	/** How much extends the locals over the IntTypeCodeResolver */
	public int extendLocals(){
		return 0; //  no extend, 32 bit 
	}
}
