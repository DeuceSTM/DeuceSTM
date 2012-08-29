/**
 * JVSTM Implementation
 * 
 * JVSTM is a multiversion STM for Java developed by Joao Cachopo 
 * from INESC-ID.
 * 
 * @author Ricardo Dias
 */
package org.deuce.transaction.jvstminplace.field;

import org.deuce.transform.ExcludeInternal;

@ExcludeInternal
public class ByteValue implements Value {
	public byte value;
}
