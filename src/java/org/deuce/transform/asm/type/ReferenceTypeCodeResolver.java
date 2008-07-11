package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Opcodes;

public class ReferenceTypeCodeResolver implements TypeCodeResolver {

    public int loadCode() {
	return Opcodes.ALOAD;
    }

    public int returnCode() {
	return Opcodes.ARETURN;
    }

    public int storeCode() {
	return Opcodes.ASTORE;
    }

    public int nullValueCode() {
	return Opcodes.ACONST_NULL;
    }
    
    public int extendLocals() {
	return 0;
    }
}
