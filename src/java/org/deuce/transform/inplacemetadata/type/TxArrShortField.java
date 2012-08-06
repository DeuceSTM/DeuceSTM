package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeTM;

@ExcludeTM
public class TxArrShortField
extends      TxField
{
  final static private int ARR_BASE  = 
      AddressUtil.arrayBaseOffset(short[].class);
  final static private int ARR_SCALE = 
      AddressUtil.arrayIndexScale(short[].class);
  
  public short[] array;
  public int     index;
  
  public TxArrShortField(short[] arr, int idx) {
    super(arr, ARR_BASE + ARR_SCALE * idx);
    array = arr;
    index = idx;
  }
  
  public final short read() {
    return array[index];
  }

  public final void write(short value) {
    array[index] = value;
  }
}
