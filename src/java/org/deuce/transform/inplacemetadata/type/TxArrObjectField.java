package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.reflection.UnsafeHolder;
import org.deuce.transform.ExcludeInternal;

/**
 * This class is used both for arrays of objects and for multiarrays.
 * With unidimensional arrays, it is similar to all the other TxArrField 
 * classes. But with multiarrays, the dimensions are chained with the nextDim 
 * field.
 * 
 * Consider this example:
 *  int[][][] cube = new int[1][2][3];
 *  
 *  We could visualize that structure roughly like this:

        +cube
        |
        v
        +-+
        | |
        +++
         |
         v
         +-++-+
         | || |
         ++++++
          |  |
          v  |
  +-++-++-+  |
  |i||i||i|  |
  +-++-++-+  |
             v
     +-++-++-+
     |i||i||i|
     +-++-++-+
     
 *
 *  With the TxArrObjectField multiarrays, we get this:

  TxAOF: TxArrObjectField
  TxAIF: TxArrIntField
  
  +cube
  |
  v
  +-------+                                                 +--------->+-+
  | TxAOF |                                                 |          | |
  |-------|                                                 |          +++
  | array +-------------------------------------------------+           |
  |nextDim+->+-------+                                                  v
  +-------+  | TxAOF |                                          +------>+-++-+
             |-------|                                          |       | || |
             | array +---+--------------------------------------+       ++++++
       +-----+nextDim|   |                                               |  |
       |     +-------+   |                                               v  |
       |     +-------+   |   +--------------------+              +-++-++-+  |
       |     | TxAOF |   |   |                    |              |i||i||i|  |
       |     |-------|   |   |                    v              +-++-++-+  |
       |     | array +---+   |                    +-------+      ^          v
       |     |nextDim+-------+                    | TxAIF |      |  +-++-++-+
       |     +-------+        +-->+-------+       |-------|      |  |i||i||i|
       |                      |   | TxAIF |       | array-+--+   |  +-++-++-+
       |                      |   |-------|       +-------+  |   |          ^
       +----------------------+   | array +---+   +-------+  |   |          |
                                  +-------+   |   | TxAIF |  |   |          |
                                  +-------+   |   |-------|  |   |          |
                                  | TxAIF |   |   | array-+--+---+          |
                                  |-------|   |   +-------+  |              |
                                  | array +---+   +-------+  |              |
                                  +-------+   |   | TxAIF |  |              |
                                  +-------+   |   |-------|  |              |
                                  | TxAIF |   |   | array-+--+              |
                                  |-------|   |   +-------+                 |
                                  | array +---+-----------------------------+
                                  +-------+

 *
 * The original array is not lost, but only the metadata structure can actually
 * access it.
 * 
 * Having said this, the ref and address fields inherited from TxField have 
 * different values if the context is array or multiarray.
 * 
 *  Array context:
 *    - ref points to the array field;
 *    - address is ARR_BASE + ARR_SCALE * idx.
 *  
 *  Multiarray context:
 *    - ref points to this object;
 *    - address is the offset of the nextDim field.
 *    
 * @author tvale
 */
@ExcludeInternal
public class TxArrObjectField extends TxField {
	final static private int ARR_BASE = AddressUtil.arrayBaseOffset(Object[].class);
	final static private int ARR_SCALE = AddressUtil.arrayIndexScale(Object[].class);

	static public long __ADDRESS__;
	static {
		try {
			__ADDRESS__ = AddressUtil.getAddress(TxArrObjectField.class.getDeclaredField("nextDim"));
		} catch (Exception e) {
			__ADDRESS__ = -1L;
		}
	}

	public Object[] array;
	public int index;
	public Object nextDim;

	/**
	 * Main constructor, used in a "normal" unidimensional context.
	 * 
	 * @param arr
	 * @param idx
	 */
	public TxArrObjectField(Object[] arr, int idx) {
		super(arr, ARR_BASE + ARR_SCALE * idx);
		array = arr;
		index = idx;
	}

	/**
	 * Constructor to be used in a multidimensional array context. The last,
	 * dummy, parameter's purpose is only needed because constructors cannot
	 * have the same signature.
	 * 
	 * @param arr
	 * @param idx
	 * @param dummy
	 */
	public TxArrObjectField(Object[] arr, int idx, Object dummy) {
		super(null, __ADDRESS__);
		this.ref = this;
		array = arr;
		index = idx;
	}

	public final Object read() {
		return UnsafeHolder.getUnsafe().getObject(ref, address);
	}

	public final void write(Object value) {
		UnsafeHolder.getUnsafe().putObject(ref, address, value);
	}
}
