/*
 * JVSTM: a Java library for Software Transactional Memory
 * Copyright (C) 2005 INESC-ID Software Engineering Group
 * http://www.esw.inesc-id.pt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author's contact:
 * INESC-ID Software Engineering Group
 * Rua Alves Redol 9
 * 1000 - 029 Lisboa
 * Portugal
 */
package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;
import org.deuce.transform.inplacemetadata.type.TxByteField;

@ExcludeInternal
public class VBoxB extends TxByteField implements VBox {
	public VBoxBBody body = null;

	public VBoxB(Object ref, long address) {
		super(ref, address);
		body = new VBoxBBody(read(), 0, null);
	}

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxBBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();
		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;

		return newBody;
	}

	private static VBoxBBody makeNewBody(Value value, int version,
			VBoxBBody next) {
		return new VBoxBBody(((ByteValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxBBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
