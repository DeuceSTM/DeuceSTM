package org.deuce.transform.inplacemetadata.type;

import org.deuce.reflection.AddressUtil;
import org.deuce.transform.ExcludeTM;

@ExcludeTM
public class TxArrBoolField
extends      TxField
{
  final static private int ARR_BASE  = 
      AddressUtil.arrayBaseOffset(boolean[].class);
  final static private int ARR_SCALE = 
      AddressUtil.arrayIndexScale(boolean[].class);
  
  public boolean[] array;
  public int       index;
  
  public TxArrBoolField(boolean[] arr, int idx) {
    super(arr, ARR_BASE + ARR_SCALE * idx);
    array = arr;
    index = idx;   
  }
  
  public final boolean read() {
    return array[index];
  }

  public final void write(boolean value) {
    array[index] = value;
  }
}
