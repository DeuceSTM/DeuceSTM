/*
 * @(#)Context.java   05/01/2008
 *
 * Copyright 2008 GigaSpaces Technologies Inc.
 */

package org.deuce.transaction;

import org.deuce.transform.ExcludeTM;

/**
 * All the STM implementations should implement this interface.
 * Using the -Dorg.deuce.transaction.contextClass property one can
 * switch between the different implementations. 
 *
 * @author	Guy Korland
 * @since	1.0
 */
@ExcludeTM
public interface Context extends IContext
{

	/* Methods called on Read/Write event */
	void beforeReadAccess( Object obj, long field);
	Object onReadAccess( Object obj, Object value, long field);
	boolean onReadAccess( Object obj, boolean value, long field);
	byte onReadAccess( Object obj, byte value, long field);
	char onReadAccess( Object obj, char value, long field);
	short onReadAccess( Object obj, short value, long field);
	int onReadAccess( Object obj, int value, long field);
	long onReadAccess( Object obj, long value, long field);
	float onReadAccess( Object obj, float value, long field);
	double onReadAccess( Object obj, double value, long field);

	void onWriteAccess( Object obj, Object value, long field);
	void onWriteAccess( Object obj, boolean value, long field);
	void onWriteAccess( Object obj, byte value, long field);
	void onWriteAccess( Object obj, char value, long field);
	void onWriteAccess( Object obj, short value, long field);
	void onWriteAccess( Object obj, int value, long field);
	void onWriteAccess( Object obj, long value, long field);
	void onWriteAccess( Object obj, float value, long field);
	void onWriteAccess( Object obj, double value, long field);
	
}
