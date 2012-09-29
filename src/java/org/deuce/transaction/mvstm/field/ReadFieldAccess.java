package org.deuce.transaction.mvstm.field;

import org.deuce.transform.ExcludeInternal;

/**
 * 
 * @author Ricardo Dias <ricardo.dias@campus.fct.unl.pt>
 */
@ExcludeInternal
public class ReadFieldAccess{
	public VBox field;
	public Version version;

	public ReadFieldAccess(){}
	
	public ReadFieldAccess(VBox field){
		init(field);
	}
	
	public void init(VBox field) {
		init(field, null);
	}
	
	public void init(VBox field, Version version){
		this.field = field;
		this.version = version;
	}

	@Override
	public boolean equals( Object obj){
		ReadFieldAccess other = (ReadFieldAccess)obj;
		return field == other.field;
	}

	@Override
	final public int hashCode(){
		return field.hashCode();
	}

	public void clear(){
		field = null;
		version = null;
	}
}