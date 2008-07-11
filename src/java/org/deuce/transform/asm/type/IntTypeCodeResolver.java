package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Opcodes;

public class IntTypeCodeResolver implements TypeCodeResolver {

    public int loadCode() {
	return Opcodes.ILOAD;
    }

    public int returnCode() {
	return Opcodes.IRETURN;
    }

    public int storeCode() {
	return Opcodes.ISTORE;
    }
    
    public int nullValueCode() {
	return Opcodes.ICONST_0;
    }
    
    public int extendLocals() {
	return 0;
    }
}
