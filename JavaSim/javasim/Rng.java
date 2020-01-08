package org.javasim;

/**
 * @author ledelato This random number generator is a VBA translation of the C
 *         program found in Law, A. M. and Kelton, W. D., ``Simulation Modeling
 *         and Analysis'', Singapore: The McGraw-Hill Book Co, pp. 430--431.
 */
public class Rng {

  public final long MODLUS = 2147483647;
  public final long MULT1  = 24112;
  public final long MULT2  = 26143;

  private long[]    zrng;

  /**
   * Constructor initializes the random seed
   */

  public Rng() {
    initializeRNSeed();
  }

  /**
   * Initialize the random seed.
   */

  private void initializeRNSeed() {

    zrng = new long[100];

    zrng[0] = 1973272912;
    zrng[1] = 281629770;
    zrng[2] = 20006270;
    zrng[3] = 1280689831;
    zrng[4] = 2096730329;
    zrng[5] = 1933576050;
    zrng[6] = 913566091;
    zrng[7] = 246780520;
    zrng[8] = 1363774876;
    zrng[9] = 604901985;
    zrng[10] = 1511192140;
    zrng[11] = 1259851944;
    zrng[12] = 824064364;
    zrng[13] = 150493284;
    zrng[14] = 242708531;
    zrng[15] = 75253171;
    zrng[16] = 1964472944;
    zrng[17] = 1202299975;
    zrng[18] = 233217322;
    zrng[19] = 1911216000;
    zrng[20] = 726370533;
    zrng[21] = 403498145;
    zrng[22] = 993232223;
    zrng[23] = 1103205531;
    zrng[24] = 762430696;
    zrng[25] = 1922803170;
    zrng[26] = 1385516923;
    zrng[27] = 76271663;
    zrng[28] = 413682397;
    zrng[29] = 726466604;
    zrng[30] = 336157058;
    zrng[31] = 1432650381;
    zrng[32] = 1120463904;
    zrng[33] = 595778810;
    zrng[34] = 877722890;
    zrng[35] = 1046574445;
    zrng[36] = 68911991;
    zrng[37] = 2088367019;
    zrng[38] = 748545416;
    zrng[39] = 622401386;
    zrng[40] = 2122378830;
    zrng[41] = 640690903;
    zrng[42] = 1774806513;
    zrng[43] = 2132545692;
    zrng[44] = 2079249579;
    zrng[45] = 78130110;
    zrng[46] = 852776735;
    zrng[47] = 1187867272;
    zrng[48] = 1351423507;
    zrng[49] = 1645973084;
    zrng[50] = 1997049139;
    zrng[51] = 922510944;
    zrng[52] = 2045512870;
    zrng[53] = 898585771;
    zrng[54] = 243649545;
    zrng[55] = 1004818771;
    zrng[56] = 773686062;
    zrng[57] = 403188473;
    zrng[58] = 372279877;
    zrng[59] = 1901633463;
    zrng[60] = 498067494;
    zrng[61] = 2087759558;
    zrng[62] = 493157915;
    zrng[63] = 597104727;
    zrng[64] = 1530940798;
    zrng[65] = 1814496276;
    zrng[66] = 536444882;
    zrng[67] = 1663153658;
    zrng[68] = 855503735;
    zrng[69] = 67784357;
    zrng[70] = 1432404475;
    zrng[71] = 619691088;
    zrng[72] = 119025595;
    zrng[73] = 880802310;
    zrng[74] = 176192644;
    zrng[75] = 1116780070;
    zrng[76] = 277854671;
    zrng[77] = 1366580350;
    zrng[78] = 1142483975;
    zrng[79] = 2026948561;
    zrng[80] = 1053920743;
    zrng[81] = 786262391;
    zrng[82] = 1792203830;
    zrng[83] = 1494667770;
    zrng[84] = 1923011392;
    zrng[85] = 1433700034;
    zrng[86] = 1244184613;
    zrng[87] = 1147297105;
    zrng[88] = 539712780;
    zrng[89] = 1545929719;
    zrng[90] = 190641742;
    zrng[91] = 1645390429;
    zrng[92] = 264907697;
    zrng[93] = 620389253;
    zrng[94] = 1502074852;
    zrng[95] = 927711160;
    zrng[96] = 364849192;
    zrng[97] = 2049576050;
    zrng[98] = 638580085;
    zrng[99] = 547070247;
  }

  /**
   * 
   * @param stream
   *          The seed for the generator
   * @return A random number from a linear congruential generator
   */

  public double lcgrand(int stream) {
    long zi;
    long lowprd;
    long hi31;

    zi = zrng[stream];

    lowprd = (zi & 65535) * MULT1;
    hi31 = (zi / 65536) * MULT1 + lowprd / 65536;
    zi = ((lowprd & 65535) - MODLUS) + ((hi31 & 32767) * 65536) + (hi31 / 32768);

    if (zi < 0) {
      zi += MODLUS;
    }

    lowprd = (zi & 65535) * MULT2;
    hi31 = (zi / 65536) * MULT2 + (lowprd / 65536);

    zi = ((lowprd & 65535) - MODLUS) + ((hi31 & 32767) * 65536) + (hi31 / 32768);

    if (zi < 0) {
      zi = zi + MODLUS;
    }

    zrng[stream] = zi;

    return (zi / 128 | 1) / (double) 16777216;
  }

  /**
   * Set linear congruential generator random stream
   * 
   * @param zset
   *          The value to which position stream will be set.
   * @param stream
   *          The position in the stream to set.
   */

  public void lcgrandst(long zset, int stream) {
    zrng[stream] = zset;
  }

  /**
   * Get linear congruential generator random stream
   * 
   * @param stream
   *          position in stream from which to return value
   * @return Value of the random stream
   */

  public long lcgrandgt(int stream) {
    return zrng[stream];
  }

  /**
   * function to generate exponential variates with mean Mean via inverse cdf
   * 
   * @param mean
   *          Mean of the exponential distribution
   * @param seed
   *          seed for the random stream
   * @return exponential random variate with mean Mean
   */

  public double expon(double mean, int seed) {
    return -Math.log(1 - lcgrand(seed)) * mean;
  }

  /**
   * Function to generate U(Lower, Upper) variates via inverse cdf
   * 
   * @param lower
   *          lower bound of distribution
   * @param upper
   *          upper bound of distribution
   * @param seed
   *          seed for the random stream
   * @return U(lower,upper) random variate
   */

  public double uniform(double lower, double upper, int seed) {
    return lower + (upper - lower) * lcgrand(seed);
  }

  /**
   * Function to generate Erlang random variates with shape parameter m and mean
   * mean
   * 
   * @param m
   *          Shape parameter for the Erlang distribution
   * @param mean
   *          Mean for the Erlang distribution
   * @param stream
   *          Seed for the random stream
   * @return Erlang random variate with shape parameter m and mean mean
   */

  public double erlang(int m, double mean, int stream) {
    double meanExponential;
    double sum;

    meanExponential = mean / (double) m;
    sum = 0.0;
    for(int i = 0; i < m; i++) {
      sum += expon(meanExponential, stream);
    }

    return sum;
  }

  /**
   * function to generate normal variates via polar method
   * 
   * @param Mean
   *          mean of normal distribution
   * @param Variance
   *          variance of normal distribution
   * @param seed
   *          seed for the random stream
   * @return Normal random variate
   */

  public double normal(double mean, double variance, int seed) {
    double u1, u2, v1, v2, w, y;

    do {
      u1 = lcgrand(seed);
      u2 = lcgrand(seed);
      v1 = 2.0 * u1 - 1.0;
      v2 = 2.0 * u2 - 1.0;
      w = v1 * v1 + v2 * v2;
    } while(w > 1);

    y = Math.sqrt(-2.0 * Math.log(w) / w);

    double normal = v1 * y;
    return mean + Math.sqrt(variance) * normal;
  }

  /**
   * Function to generate a random integer with the distribution probDistrib
   * 
   * @param probDistrib
   *          probability distribution of the random integer
   * @param seed
   *          seed for the random stream
   * @return random integer
   */

  public double randomInteger(double probDistrib[], int seed) {
    double u = lcgrand(seed);
    // Generate a U(0,1) random variate.

    // Return a random integer in accordance with the (accumulative)
    // distribution
    // function prob_distrib.
    int randomInteger = 0;
    while(u >= probDistrib[randomInteger]) {
      randomInteger++;
    }

    return randomInteger;
  }

  /**
   * Function to generate lognormal variate by transforming a normal
   * 
   * @param meanPrime
   *          mean parameter (mean = exp(meanPrime + variancePrime^2/2)
   * @param variancePrime
   *          variance parameter (variance = (exp(variancePrime^2) -1) *
   *          exp(2*meanPrime + variancePrime^2))
   * @param seed
   *          seed for the random stream
   * @return
   */

  public double lognormal(double meanPrime, double variancePrime, int seed) {
    double mean = Math.log(meanPrime * meanPrime / Math.sqrt(meanPrime * meanPrime + variancePrime));
    double variance = Math.log(1.0 + variancePrime / (meanPrime * meanPrime));
    return Math.exp(normal(mean, variance, seed));
  }

  public double triangular(double a, double b, double c, int seed) {
    double standardC = (c - a) / (b - a);
    double u = lcgrand(seed);
    double triangular = 0.0;
    if (u <= standardC) {
      triangular = Math.sqrt(standardC * u);
    } else {
      triangular = 1.0 - Math.sqrt((1.0 - standardC) * (1.0 - u));
    }

    return a + (b - a) * triangular;
  }

}