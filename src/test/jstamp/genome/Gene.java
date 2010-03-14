package jstamp.genome;

public class Gene {
  public int length;
  public ByteString contents;
  public Bitmap startBitmapPtr; /* used for creating segments */
  
  Gene(int myLength) {
    length = myLength;
    startBitmapPtr = new Bitmap(length);
  }


/* =============================================================================
 * gene_create
 * -- Populate contents with random gene
 * =============================================================================
 */
  void create(Random randomObj) {
    int i;
    byte[] nucleotides = new byte[4];
    byte[] arrayContents = new byte[length];
    nucleotides[0] = (byte) 'a';
    nucleotides[1] = (byte) 'c';
    nucleotides[2] = (byte) 'g';
    nucleotides[3] = (byte) 't';

    for (i = 0; i < length; i++) {
      arrayContents[i] = nucleotides[(int)(randomObj.random_generate() % 4)];
    }
    
    contents = new ByteString(arrayContents);
  }  
}
