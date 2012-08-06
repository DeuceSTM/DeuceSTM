package org.deuce.transaction;

import org.deuce.transform.ExcludeTM;
import org.deuce.transform.inplacemetadata.type.TxField;

/**
 * All the STM implementations that use inlocal metadata should 
 * implement this interface and annotate the subclass with LocalMetadata
 * annotation.
 * Using the -Dorg.deuce.transaction.contextClass property one can
 * switch between the different implementations. 
 *
 * @author	Ricardo Dias
 */
@ExcludeTM
public interface ContextMetadata extends IContext
{

	/* Methods called on Read/Write event */
	void beforeReadAccess(TxField field);
	Object onReadAccess(Object value, TxField field);
	boolean onReadAccess(boolean value, TxField field);
	byte onReadAccess(byte value, TxField field);
	char onReadAccess(char value, TxField field);
	short onReadAccess(short value, TxField field);
	int onReadAccess(int value, TxField field);
	long onReadAccess(long value, TxField field);
	float onReadAccess(float value, TxField field);
	double onReadAccess(double value, TxField field);

	void onWriteAccess(Object value, TxField field);
	void onWriteAccess(boolean value, TxField field);
	void onWriteAccess(byte value, TxField field);
	void onWriteAccess(char value, TxField field);
	void onWriteAccess(short value, TxField field);
	void onWriteAccess(int value, TxField field);
	void onWriteAccess(long value, TxField field);
	void onWriteAccess(float value, TxField field);
	void onWriteAccess(double value, TxField field);
	
}
