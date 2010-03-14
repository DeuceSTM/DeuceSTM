package jstamp.genome;

public class ByteString {
  byte value[];
  int count;
  int offset;
  private int cachedHashcode;

  private ByteString() {
  }

  public ByteString(byte str[]) {
    this.value=str;
    this.count=str.length;
    this.offset=0;
  }

  public int compareTo(ByteString s) {
    int smallerlength=count<s.count?count:s.count;

    int off=offset;
    int soff=s.offset;
    for( int i = 0; i < smallerlength; i++) {
      int valDiff = this.value[i+offset] - s.value[i+soff];
      if( valDiff != 0 ) {
        return valDiff;
      }
    }
    return count-s.count;
  }

  public boolean endsWith(ByteString suffix) {
    return regionMatches(count - suffix.count, suffix, 0, suffix.count);
  }

  public ByteString substring(int beginIndex) {
    return substring(beginIndex, this.count);
  }

  public ByteString subString(int beginIndex, int endIndex) {
    return substring(beginIndex, endIndex);
  }

  public ByteString substring(int beginIndex, int endIndex) {
    ByteString str=new ByteString();
    if (beginIndex>this.count||endIndex>this.count||beginIndex>endIndex) {
      // FIXME
      System.out.println("Index error: "+beginIndex+" "+endIndex+" "+count+"\n"+this);
    }
    str.value=this.value;
    str.count=endIndex-beginIndex;
    str.offset=this.offset+beginIndex;
    return str;
  }

  public ByteString subString(int beginIndex) {
    return this.subString(beginIndex, this.count);
  }

  public int lastindexOf(int ch) {
    return this.lastindexOf(ch, count - 1);
  }

  public ByteString concat(ByteString str) {
    ByteString newstr=new ByteString();
    newstr.count=this.count+str.count;
    byte charstr[]=new byte[newstr.count];
    newstr.value=charstr;
    newstr.offset=0;
    for(int i=0; i<count; i++) {
      charstr[i]=value[i+offset];
    }
    int stroffset=str.offset;
    for(int i=0; i<str.count; i++) {
      charstr[i+count]=str.value[stroffset];
    }
    return newstr;
  }

  public int lastindexOf(int ch, int fromIndex) {
    int off=offset;
    for(int i=fromIndex; i>0; i--)
      if (this.value[i+offset]==ch)
	return i;
    return -1;
  }

  public int indexOf(int ch) {
    return this.indexOf(ch, 0);
  }

  public int indexOf(int ch, int fromIndex) {
    int off=offset;
    for(int i=fromIndex; i<count; i++)
      if (this.value[i+off]==ch)
	return i;
    return -1;
  }

  public int indexOf(ByteString str) {
    return this.indexOf(str, 0);
  }

  public int indexOf(ByteString str, int fromIndex) {
    if (fromIndex<0)
      fromIndex=0;
    for(int i=fromIndex; i<=(count-str.count); i++)
      if (regionMatches(i, str, 0, str.count))
	return i;
    return -1;
  }

  public int lastIndexOf(ByteString str, int fromIndex) {
    int k=count-str.count;
    if (k>fromIndex)
      k=fromIndex;
    for(; k>=0; k--) {
      if (regionMatches(k, str, 0, str.count))
	return k;
    }
    return -1;
  }

  public int lastIndexOf(ByteString str) {
    return lastIndexOf(str, count-str.count);
  }

  public boolean startsWith(ByteString str) {
    return regionMatches(0, str, 0, str.count);
  }

  public boolean startsWith(ByteString str, int toffset) {
    return regionMatches(toffset, str, 0, str.count);
  }

  public boolean regionMatches(int toffset, ByteString other, int ooffset, int len) {
    if (toffset<0 || ooffset <0 || (toffset+len)>count || (ooffset+len)>other.count)
      return false;
    for(int i=0; i<len; i++)
      if (other.value[i+other.offset+ooffset]!=
          this.value[i+this.offset+toffset])
	return false;
    return true;
  }

  public byte[] getBytes() {
    byte str[]=new byte[count];
    for(int i=0; i<count; i++)
      str[i]=(byte)value[i+offset];
    return str;
  }

  public int length() {
    return count;
  }

  public byte byteAt(int i) {
    return value[i+offset];
  }

  public int hashCode() {
    if (cachedHashcode!=0)
      return cachedHashcode;
    int hash=0;
    int off=offset;
    for(int index = 0; index < count; index++) {
      byte c = value[index+off];
      hash = c + (hash << 6) + (hash << 16) - hash;
    }
    if (hash<0)
      hash=-hash;
    cachedHashcode=hash;
    return hash;
  }

  public boolean equals(Object o) {
    if (o.getClass()!=getClass())
      return false;
    ByteString s=(ByteString)o;
    if (s.count!=count)
      return false;
    for(int i=0; i<count; i++) {
      if (s.value[i+s.offset]!=value[i+offset])
	return false;
    }
    return true;
  }
}
