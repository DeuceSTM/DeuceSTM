package org.deuce.transaction.capmem;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.deuce.objectweb.asm.Type;
import org.deuce.transaction.Context;
import org.deuce.transform.Exclude;


/**
 * The instances of this class encapsulate an array 
 * and keep track its captured state.  
 *  
 * @author fmcarvalho <mcarvalho@cc.isel.ip.pt>
 */
@Exclude
public class CapturedStateObjectArray extends CapturedStateArrayBase{
	public Object [] elements;
	public int nrOfDimensions;
	public int[] dimLengths;
	public Class componentClass;

	@Override
	public int arrayLength() {
		return elements.length;
	}

	@Override
	public void arraycopy(int srcPos, Object dest, int destPos, int length) {
		System.arraycopy(this.elements, srcPos, ((CapturedStateObjectArray)dest).elements, destPos, length);
	}


	/**
	 * Invoked from regular methods (non-transactional) when they
	 * need to update array fields with cap mem.
	 */
	public CapturedStateObjectArray(Object [] elements) {
		super();
		if(elements != null && elements.getClass().getName().lastIndexOf('[') > 0)
			processMultiArray(elements, null);
		else 
			this.elements = elements;
	}

	/**
	 * Invoked from transactional methods.
	 */
	public CapturedStateObjectArray(int length, String type, Context ctx) {
		super(ctx);

		if(type.charAt(0) == '['){
			this.elements = new Object[length];
		}
		else{
			try {
				Class<?> componentType = Class.forName(Type.getObjectType(type).getClassName());
				this.elements = (Object[]) Array.newInstance(componentType, length);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * This constructor is for multi arrays
	 */
	public CapturedStateObjectArray(Object [] elements, Context ctx) {
		super(ctx);
		processMultiArray(elements, ctx);
	}

	public void processMultiArray(Object [] elements, Context ctx) {
		Class firstDimElemClass = elements.getClass().getComponentType(); // = int[] if elements is int[][]
		Class scondDimElemClass  = firstDimElemClass.getComponentType(); // = int if elements is int[][]

		if(scondDimElemClass == null){
			this.elements = elements;
			return;
		}

		nrOfDimensions = 0;
		componentClass = elements.getClass();
		while ( componentClass.isArray() ) {
			nrOfDimensions++;
			componentClass = componentClass.getComponentType();
		}
		dimLengths = new int[nrOfDimensions];
		Object arr = elements;
		for (int i = 0; i < dimLengths.length; i++) {
			dimLengths[i] = arr == null? 0 : Array.getLength(arr);
			arr = ((arr == null) || (dimLengths[i] == 0)) ? null : Array.get(arr, 0);
		}

		Object [] aux = new Object[elements.length];
		for (int i = 0; i < aux.length; i++) {
			if(scondDimElemClass.isArray()){ 
				// if elements is e.g. int[][][]
				if(ctx == null)
					aux[i] = new CapturedStateObjectArray((Object[]) elements[i]);
				else
					aux[i] = new CapturedStateObjectArray((Object[]) elements[i], ctx); //then call recursive passing int[][]
			}else{
				// if elements is e.g. int[][]
				aux[i] = newWrapper(firstDimElemClass, scondDimElemClass, elements[i], ctx); // then create a CapturedStateIntArray 
			}
		}
		this.elements = aux;
	}

	private static Object newWrapper(Class firstDimElemClass, Class scondDimElemClass, Object innerArray, Context ctx){
		if(!scondDimElemClass.isPrimitive()){
			if(ctx == null)
				return new CapturedStateObjectArray((Object[]) innerArray);
			else
				return new CapturedStateObjectArray((Object[]) innerArray, ctx);
		}else{
			// e.g. firstDimElemClass = int[] and scondDimElemClass = int
			String primName = scondDimElemClass.getName(); // = int
			primName = Character.toUpperCase(primName.charAt(0)) + primName.substring(1); // = Int
			try {
				String capArrayName = CapturedStateArrayBase.class.getName().replace("ArrayBase", primName + "Array");
				Class wrapper = Class.forName(capArrayName); // = CapturedStateIntArray
				if(ctx == null){
					Constructor ctor = wrapper.getConstructor(firstDimElemClass); // = ctor(int[], Context)
					return ctor.newInstance(innerArray);
				}else{
					Constructor ctor = wrapper.getConstructor(firstDimElemClass, Context.class); // = ctor(int[], Context)
					return ctor.newInstance(innerArray, ctx);
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} 
		}
	}
	/*
	 * This method is for when regular methods (non-transactional) access arrays in captured memory.
	 * Yet this operations has a huge overhead and in this case we prefer to throw an exception 
	 * alerting the user programmer to access the owner class from a transactional method, instead 
	 * of accessing it from a non-transactional method, which in turn requires the encapsulated array 
	 * to be unwrapped. 
	 */
	public Object unwrapp() throws Exception{
		// this method will fail if it unwrapps unideimensional
		Object [] aux = (Object []) Array.newInstance(componentClass, dimLengths);
		unwrapp(aux);
		return aux;
	}

	private void unwrapp(Object[] newElems) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if(elements.length == 0) 
			return;

		if(!(elements[0] instanceof CapturedStateArrayBase)){
			for (int i = 0; i < elements.length; i++) {
				newElems[i] = elements[i];  
			}
		}
		else if(elements[0] instanceof CapturedStateObjectArray){
			for (int i = 0; i < elements.length; i++) {
				((CapturedStateObjectArray)elements[i]).unwrapp((Object[]) newElems[i]);
			}
		}else{
			for (int i = 0; i < elements.length; i++) {
				Field elems = elements[i].getClass().getDeclaredField("elements");
				newElems[i] = elems.get(elements[i]);
			}
		}

	}    
}
