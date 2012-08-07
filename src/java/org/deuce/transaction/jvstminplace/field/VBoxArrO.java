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
import org.deuce.transform.inplacemetadata.type.TxArrObjectField;

@ExcludeInternal
public class VBoxArrO extends TxArrObjectField implements VBox {
	public VBoxOBody body = null;

	public VBoxArrO(Object[] arr, int idx) {
		super(arr, idx);
		body = new VBoxOBody(read(), 0, null);
	}
	
	public VBoxArrO(Object[] arr, int idx, Object dummy) {
          super(arr, idx, dummy);
          body = new VBoxOBody(read(), 0, null);
        }

	public boolean validate(VBoxBody body) {
		return this.body == body;
	}

	public VBoxBody commit(Value newValue, int txNumber) {
		VBoxOBody newBody = makeNewBody(newValue, txNumber, this.body);
//		this.body.value = read();

		this.body = newBody;
//		write(newBody);
//		this.body.version = txNumber;

		return newBody;
	}

	public static VBoxOBody makeNewBody(Value value, int version, VBoxOBody next) {
		return new VBoxOBody(((ObjectValue) value).value, version, next);
	}

	public void write(VBoxBody body) {
		super.write(((VBoxOBody) body).value);
	}

	public VBoxBody getBody(int clock) {
		return body.getBody(clock);
	}

	public VBoxBody getTop() {
		return body;
	}
}
