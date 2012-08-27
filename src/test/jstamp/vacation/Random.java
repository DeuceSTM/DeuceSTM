package jstamp.vacation;

public class Random {
  long[] mt; 
  int mti;
  int RANDOM_DEFAULT_SEED;
  /* period parameter */


  public Random() {
    RANDOM_DEFAULT_SEED = 0;
    mt = new long[624];
  }

  public void random_alloc() {
    init_genrand(this.RANDOM_DEFAULT_SEED);
  }

  /* initializes mt[N] with a seed */
  public void init_genrand(int s) {
    mt[0]= ((long)s) & 0xFFFFFFFFL;
    for (int mti=1; mti<624; mti++) {
      mt[mti] = (1812433253L * (mt[mti-1] ^ (mt[mti-1] >> 30)) + ((long)mti));
      /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
      /* In the previous versions, MSBs of the seed affect   */
      /* only MSBs of the array mt[].                        */
      /* 2002/01/09 modified by Makoto Matsumoto             */
      mt[mti] &= 0xFFFFFFFFL;
      /* for >32 bit machines */
    }
    this.mti=624;
  }

  public void random_seed(int seed) {
    init_genrand(seed);
  }

  public int random_generate() {
    long genrandInt32 = genrand_int32();
	long x= genrandInt32&0xFFFFFFFFL;
    int x2 = (int) x;
    if (genrandInt32 < 0)
    	if (x2 <0)
    		return x2;
    	else 
    		return -x2;
    if (genrandInt32 > 0)
    	if (x2 >0)
    		return x2;
    	else
    		return -x2;
	return x2;
  }

  public int posrandom_generate() {
    int r=(int) genrand_int32();
    if (r>0)
      return r;
    else 
      return -r;
  }

  public long genrand_int32() {
    long y;
    int mti = this.mti;
    long[] mt = this.mt;

    if (mti >= 624) { /* generate N words at one time */
      int kk;

      if (mti == 624+1) {  /* if init_genrand() has not been called, */
        init_genrand(5489); /* a default initial seed is used */
	mti=this.mti;
      }
      for (kk=0;kk<(624-397);kk++) {
        y = (mt[kk]&0x80000000L)|(mt[kk+1]&0x7fffffffL);
        mt[kk] = mt[kk+397] ^ (y >> 1) ^ ((y & 0x1)==0 ? 0L:0x9908b0dfL);
      }
      for (;kk<(624-1);kk++) {
        y = (mt[kk]&0x80000000L)|(mt[kk+1]&0x7fffffffL);
        mt[kk] = mt[kk+(397-624)] ^ (y >> 1) ^ ((y & 0x1)==0 ? 0L:0x9908b0dfL);
      }
      y = (mt[624-1]&0x80000000L)|(mt[0]&0x7fffffffL);
      mt[624-1] = mt[397-1] ^ (y >> 1) ^ ((y & 0x1)==0 ? 0L:0x9908b0dfL);

      mti = 0;
    }

    y = mt[mti++];

    /* Tempering */
    y ^= (y >> 11);
    y ^= (y << 7) & 0x9d2c5680L;
    y ^= (y << 15) & 0xefc60000L;
    y ^= (y >> 18);

    this.mti = mti;

    return y;
  }
}
