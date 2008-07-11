package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Opcodes;

public class DoubleTypeCodeResolver implements TypeCodeResolver {

    public int loadCode() {
	return Opcodes.DLOAD;
    }

    public int returnCode() {
	return Opcodes.DRETURN;
    }

    public int storeCode() {
	return Opcodes.DSTORE;
    }
    
    public int nullValueCode() {
	return Opcodes.DCONST_0;
    }

    public int extendLocals() {
	return 1;
    }
}
