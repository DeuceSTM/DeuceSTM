package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeTM;

@ExcludeTM
public class TxArrLongField
extends      TxField
{
  final static private int ARR_BASE  = 
      AddressUtil.arrayBaseOffset(long[].class);
  final static private int ARR_SCALE = 
      AddressUtil.arrayIndexScale(long[].class);
  
  public long[] array;
  public int    index;
  
  public TxArrLongField(long[] arr, int idx) {
    super(arr, ARR_BASE + ARR_SCALE * idx);
    array = arr;
    index = idx;
  }
  
  public final long read() {
    return array[index];
  }

  public final void write(long value) {
    array[index] = value;
  }
}
