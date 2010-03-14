/* =============================================================================
 *
 * coordinate.java 
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
 *      contributors may be used to endorse or promote products derived
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



import java.lang.Math;

public class Coordinate {

    public int x;
    public int y;
    public int z;

    public Coordinate() {}


    // coordiate_alloc will be constructor
    // coordinate_t* coordinate_alloc(long x, long y, long z)
    public Coordinate(int x,int y,int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Coordinate alloc(int x,int y,int z) {
        Coordinate c = new Coordinate(x,y,z);

        return c;
    }

    
    // deallocate memory
    // may not need
    //  coordinate_free

    /*========================================================
    // coordinate_isEqual
    ==========================================================*/
    public static boolean isEqual(Coordinate a,Coordinate b) 
    {
        if((a.x == b.x) && (a.y == b.y) && (a.z == b.z))
            return true;
        
        return false;
    }

    /*==========================================================
      *
      * getPairDistance
      *
      *========================================================*/   
    private static double getPairDistance(Pair p)
    {
        Coordinate a = (Coordinate)p.first;
        Coordinate b = (Coordinate)p.second;
        int dx = a.x - b.x;
        int dy = a.y - b.y;
        int dz = a.z - b.z;
        int dx2 = dx* dx;
        int dy2 = dy* dy;
        int dz2 = dz* dz;

        return Math.sqrt((double)(dx2+dy2+dz2));
    }


    /*================================================
    // coordinat_ comparePair
     * -- For sorting in list of source/destination pairs
     * -- Route longer paths first so they are more likely to suceed
     
    *================================================*/
    public static int comparePair(final Object a,final Object b) 
    {
        double aDistance = getPairDistance((Pair)a);
        double bDistance = getPairDistance((Pair)b);

        if(aDistance < bDistance) {
            return 1;
        } else if(aDistance > bDistance) {
            return -1;
        }

        return 0;
    }

    /*=======================================================
      * coordinate_areAdjacent
      *=======================================================*/

    public static boolean areAdjacent(Coordinate a,Coordinate b) 
    {
        int dx = a.x - b.x;
        int dy = a.y - b.y;
        int dz = a.z - b.z;
        int dx2 = dx * dx;
        int dy2 = dy * dy;
        int dz2 = dz * dz;

        return (((dx2 + dy2 + dz2) == 1) ? true : false);
    }
    }

    /*=====================================================
      * 
      * End of Coordinate
      *
      *====================================================*/
    
    
