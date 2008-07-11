package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Opcodes;

public class FloatTypeCodeResolver implements TypeCodeResolver {

    public int loadCode() {
	return Opcodes.FLOAD;
    }

    public int returnCode() {
	return Opcodes.FRETURN;
    }

    public int storeCode() {
	return Opcodes.FSTORE;
    }
    
    public int nullValueCode() {
	return Opcodes.FCONST_0;
    }
    
    public int extendLocals() {
	return 0;
    }
}
