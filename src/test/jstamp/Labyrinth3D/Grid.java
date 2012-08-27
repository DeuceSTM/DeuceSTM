/* =============================================================================
 *
 * grid.java
 *
 * =============================================================================
 *
 * Copyright (C) Stanford University, 2006.  All Rights Reserved.
 * Author: Chi Cao Minh
 *
 * =============================================================================
 *
 * For the license of bayes/sort.h and bayes/sort.c, please see the header
 * of the files.
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of kmeans, please see kmeans/LICENSE.kmeans
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of ssca2, please see ssca2/COPYRIGHT
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of lib/mt19937ar.c and lib/mt19937ar.h, please see the
 * header of the files.
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of lib/rbtree.h and lib/rbtree.c, please see
 * lib/LEGALNOTICE.rbtree and lib/LICENSE.rbtree
 * 
 * ------------------------------------------------------------------------
 * 
 * Unless otherwise noted, the following license applies to STAMP files:
 * 
 * Copyright (c) 2007, Stanford University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 * 
 *     * Neither the name of Stanford University nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY STANFORD UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL STANFORD UNIVERSITY BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * =============================================================================
 */

package jstamp.Labyrinth3D;

import java.lang.Long;
import java.lang.reflect.Array;
import java.util.Arrays;


public class Grid {

	public static final int GRID_POINT_FULL = -2;
	public static final int GRID_POINT_EMPTY = -1;

	
	public int width;
    public int height;
    public int depth;
    public int[][][] points_unaligned;

    public Grid() {}

    
/* =============================================================================
 * grid_alloc
 * =============================================================================
    grid_t* grid_alloc (long width, long height, long depth);

    well... need to implement
    got stuck
*/
  public static Grid alloc(int width,int height,int depth) {
    Grid grid = new Grid();
    
    grid.width = width;
    grid.height = height;
    grid.depth = depth;
    
    int[][][] points_unaligned = new int[width][height][depth];
    
    
    for(int i=0;i<width;i++)
      for(int j=0;j<height;j++)
	for(int k=0;k<depth;k++)
	  points_unaligned[i][j][k]= GRID_POINT_EMPTY;
    
    grid.points_unaligned = points_unaligned;
    
    return grid;         
  }

  public static Grid scratchalloc(int width,int height,int depth) {
    Grid grid = /*scratch*/ new Grid();
    grid.width = width;
    grid.height = height;
    grid.depth = depth;
    int[][][] points_unaligned = /*scratch*/ new int[width][height][depth];
    grid.points_unaligned = points_unaligned;
    return grid;         
  }


  public static Object deepCopy(Object src)
  {
      int srcLength = Array.getLength(src);
      Class srcComponentType = src.getClass().getComponentType();

      Object dest = Array.newInstance(srcComponentType, srcLength);

      if (srcComponentType.isArray())
      {
          for (int i = 0; i < Array.getLength(src); i++)
              Array.set(dest, i, deepCopy(Array.get(src, i)));
      }
      else
      {
          System.arraycopy(src, 0, dest, 0, srcLength);
      }

      return dest;
  }

/* =============================================================================
 * grid_copy
 * =============================================================================
    void grid_copy (grid_t* dstGridPtr, grid_t* srcGridPtr);
 */
  public static void copy(Grid dstGridPtr,Grid srcGridPtr) {
    if((srcGridPtr.width == dstGridPtr.width) ||
       (srcGridPtr.height == dstGridPtr.height) ||
       (srcGridPtr.depth == dstGridPtr.depth)) {
      int width=srcGridPtr.width;
      int height=srcGridPtr.height;
      int depth=srcGridPtr.depth;
      Object deepCopyArr = deepCopy(srcGridPtr.points_unaligned);
      dstGridPtr.points_unaligned = (int[][][]) deepCopyArr;
      //System.deepArrayCopy(dstGridPtr.points_unaligned, srcGridPtr.points_unaligned);
    }      
  }

/* =============================================================================
 * grid_isPointValid
 * =============================================================================
 bool_t grid_isPointValid (grid_t* gridPtr, long x, long y, long z);
 */
  public boolean isPointValid(int x,int y,int z) {
    return x>=0 && x< width && y>=0 && y<height && z>=0 && z<depth;
  }


  public int getPoint(int x,int y,int z) {
    return this.points_unaligned[x][y][z];
  }


/* =============================================================================
 * grid_isPointEmpty
 * =============================================================================
 bool_t grid_isPointEmpty (grid_t* gridPtr, long x, long y, long z); {
 */

  public boolean isPointEmpty(int x,int y,int z) {
    return points_unaligned[x][y][z]==GRID_POINT_EMPTY;
  }



/* =============================================================================
 * grid_isPointFull
 * =============================================================================
 bool_t grid_isPointFull (grid_t* gridPtr, long x, long y, long z);
 */
  public boolean isPointFull(int x,int y,int z) {
    return points_unaligned[x][y][z]==GRID_POINT_FULL;
  }


/* =============================================================================
 * grid_setPoint
 * =============================================================================
 void grid_setPoint (grid_t* gridPtr, long x, long y, long z, long value);
 */
  public void setPoint(int x,int y,int z,int value) {
    points_unaligned[x][y][z] = value;
  }


/* =============================================================================
 * grid_addPath
 * =============================================================================
 
void grid_addPath (grid_t* gridPtr, vector_t* pointVectorPtr);
*/
  public void addPath(Vector_t pointVectorPtr) {
    int i;
    int n = pointVectorPtr.vector_getSize();
    
    for(i = 0; i < n; i++) {
      Coordinate coordinatePtr = (Coordinate)pointVectorPtr.vector_at(i);
      int x = coordinatePtr.x;
      int y = coordinatePtr.y;
      int z = coordinatePtr.z;

      points_unaligned[x][y][z]=GRID_POINT_FULL;
    }
  }

  public boolean TM_addPath(Vector_t pointVectorPtr) {
    int i;
    int n = pointVectorPtr.vector_getSize();

    int height = this.height;
    int width = this.width;
    int area = height * width;
    boolean dowrites=true;
    for(i = 1; i < (n-1); i++) {
      int gridPointIndex = ((Integer)(pointVectorPtr.vector_at(i))).intValue();
      int z = gridPointIndex / area;
      int index2d = gridPointIndex % area;
      int y = index2d / width;
      int x = index2d % width;        
      if (points_unaligned[x][y][z] != GRID_POINT_EMPTY) {
	dowrites=false;
      }
    }

    for(i = 1; i < (n-1); i++) {
      int gridPointIndex = ((Integer)(pointVectorPtr.vector_at(i))).intValue();
      int z = gridPointIndex / area;
      int index2d = gridPointIndex % area;
      int y = index2d / width;
      int x = index2d % width;
      int[] array=points_unaligned[x][y];
      if (dowrites) array[z] = GRID_POINT_FULL;
    }
    return !dowrites;
  }

  public int getPointIndex(int x,int y,int z) {
    return ((z * height) + y) * width + x;
  }

  
  public void print() {
    int width  = this.width;
    int height = this.height;
    int depth  = this.depth;

    for (int z = 0; z < depth; z++) {
      System.out.println("[z ="+z+"]");
      for (int x = 0; x < width; x++) {
	for (int y = 0; y < height; y++) {
	  String str=String.valueOf(points_unaligned[x][y][z]);
	  for(int sp=0; sp<(4-str.length());sp++)
	    System.out.print(" ");
	  System.out.print(str);
	}
	System.out.println("");
      }
      System.out.println("");
    }
  }
}


/* =============================================================================
 *
 * End of grid.c
 *
 * =============================================================================
 */
