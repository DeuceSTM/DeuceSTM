package jstamp.KMeans;

public class Random {
  int[] mt; 
  int mti;
  int RANDOM_DEFAULT_SEED;
  /* period parameter */
  int N;
  int M;
  int MATRIX_A;
  int UPPER_MASK;
  int LOWER_MASK;
  int[] mag01;

  public Random() {
    RANDOM_DEFAULT_SEED = 0;
    N = 624;
    M = 397;
    mt = new int[N];
    mti = N;
    MATRIX_A = 0x9908b0df;   /* constant vector a */
    UPPER_MASK = 0x80000000; /* most significant w-r bits */
    LOWER_MASK = 0x7fffffff; /* least significant r bits */
    mag01 = new int[2];
    mag01[0] = 0x0;
    mag01[1] = MATRIX_A;

  }

  public void random_alloc() {
    init_genrand(this.RANDOM_DEFAULT_SEED);
  }

  /* initializes mt[N] with a seed */
  public void init_genrand(int s) {
    mt[0]= s & 0xFFFFFFFF;
    for (int mti=1; mti<N; mti++) {
     mt[mti] = (1812433253 * (mt[mti-1] ^ (mt[mti-1] >> 30)) + mti);
      /* See Knuth TAOCP Vol2. 3rd Ed. P.106 for multiplier. */
      /* In the previous versions, MSBs of the seed affect   */
      /* only MSBs of the array mt[].                        */
      /* 2002/01/09 modified by Makoto Matsumoto             */
      mt[mti] &= 0xFFFFFFFF;
      /* for >32 bit machines */
    }
    this.mti=mti;
  }

  public void random_seed(int seed) {
    init_genrand(seed);
  }

  public int random_generate() {
    return genrand_int32();
  }

  public int posrandom_generate() {
    int r=genrand_int32();
    if (r>0)
      return r;
    else 
      return -r;
  }

  public int genrand_int32() {
    int y;
    int mti = this.mti;

    /* mag01[x] = x * MATRIX_A  for x=0,1 */

    if (mti >= 624) { /* generate N words at one time */
      int kk;
      int[] mt = this.mt;

      if (mti == 624+1)   /* if init_genrand() has not been called, */
        init_genrand(5489); /* a default initial seed is used */

      for (kk=0;kk<(624-397);kk++) {
        y = (mt[kk]&0x80000000)|(mt[kk+1]&0x7fffffff);
        mt[kk] = mt[kk+397] ^ (y >> 1) ^ ((y & 0x1)==0 ? 0:0x9908b0df);
      }
      for (;kk<(624-1);kk++) {
        y = (mt[kk]&0x80000000)|(mt[kk+1]&0x7fffffff);
        mt[kk] = mt[kk+(397-624)] ^ (y >> 1) ^ ((y & 0x1)==0 ? 0:0x9908b0df);
      }
      y = (mt[624-1]&0x80000000)|(mt[0]&0x7fffffff);
      mt[624-1] = mt[397-1] ^ (y >> 1) ^ ((y & 0x1)==0 ? 0:0x9908b0df);

      mti = 0;
    }

    y = mt[mti++];

    /* Tempering */
    y ^= (y >> 11);
    y ^= (y << 7) & 0x9d2c5680;
    y ^= (y << 15) & 0xefc60000;
    y ^= (y >> 18);

    this.mti = mti;

    return y;
  }
}
