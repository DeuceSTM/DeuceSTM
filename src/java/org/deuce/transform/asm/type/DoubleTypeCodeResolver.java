package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Opcodes;

public class DoubleTypeCodeResolver extends TypeCodeResolver {

	@Override
	public int loadCode() {
		return Opcodes.DLOAD;
	}

	@Override
	public int returnCode() {
		return Opcodes.DRETURN;
	}

	@Override
	public int storeCode() {
		return Opcodes.DSTORE;
	}

	@Override
	public int nullValueCode() {
		return Opcodes.DCONST_0;
	}

	@Override
	public int extendLocals() {
		return 1; // 64 bit
	}
}
