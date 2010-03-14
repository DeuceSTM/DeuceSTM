package jstamp.KMeans;

/* ==============================================================================
 *
 * GlobalArgs.java
 * -- Class that holds all the global parameters used by each thread
 *    during parallel execution
 *
 * =============================================================================
 * Author:
 *
 * Alokika Dash
 * University of California, Irvine
 * email adash@uci.edu
 *
 * =============================================================================
 */

public class GlobalArgs {

  public GlobalArgs() {

  }

  /**
   * Number of threads
   **/
  public int nthreads;

  /**
   * List of attributes
   **/
  public float[][] feature;

  /**
   * Number of attributes per Object
   **/
  public int nfeatures;

  /**
   * Number of Objects
   **/
  public int npoints;


  /**
   * Iteration id between min_nclusters to max_nclusters 
   **/
  public int nclusters;


  /**
   * Array that holds change index of cluster center per thread 
   **/
  public int[] membership;

  /**
   *
   **/
  public float[][] clusters;


  /**
   * Number of points in each cluster [nclusters]
   **/
  public int[] new_centers_len;

  /**
   * New centers of the clusters [nclusters][nfeatures]
   **/
  public float[][] new_centers;

  /**
    *
  **/
  public int global_i;

  public float global_delta;

  long global_time;

}
