package org.deuce.transform.asm.type;

public interface TypeCodeResolver {
    int loadCode();
    int storeCode();
    int returnCode();
    int nullValueCode();
    /** How much extends the locals over the IntTypeCodeResolver */
    int extendLocals();
}
