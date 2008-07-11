package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Opcodes;

public class LongTypeCodeResolver implements TypeCodeResolver {

    public int loadCode() {
	return Opcodes.LLOAD;
    }

    public int returnCode() {
	return Opcodes.LRETURN;
    }

    public int storeCode() {
	return Opcodes.LSTORE;
    }
    
    public int nullValueCode() {
	return Opcodes.LCONST_0;
    }
    
    public int extendLocals() {
	return 1;
    }
}
