/*
 * JVSTM: a Java library for Software Transactional Memory
 * Copyright (C) 2005 INESC-ID Software Engineering Group
 * http://www.esw.inesc-id.pt
 *
 * Author's contact:
 * INESC-ID Software Engineering Group
 * Rua Alves Redol 9
 * 1000 - 029 Lisboa
 * Portugal
 */
package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public interface VBox {
	boolean validate(VBoxBody body);

	VBoxBody commit(Value value, int newClock);

	void write(VBoxBody body);

	VBoxBody getBody(int clock);

	public VBoxBody getTop();
}
