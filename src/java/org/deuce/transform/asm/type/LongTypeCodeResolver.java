package org.deuce.transform.asm.type;

import org.deuce.objectweb.asm.Opcodes;

public class LongTypeCodeResolver extends TypeCodeResolver {

	@Override
	public int loadCode() {
		return Opcodes.LLOAD;
	}

	@Override
	public int returnCode() {
		return Opcodes.LRETURN;
	}

	@Override
	public int storeCode() {
		return Opcodes.LSTORE;
	}

	@Override
	public int nullValueCode() {
		return Opcodes.LCONST_0;
	}

	@Override
	public int localSize() {
		return 2; // 64 bit
	}
}
