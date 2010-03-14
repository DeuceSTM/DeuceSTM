package jstamp.Labyrinth3D;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/*=============================================================================
 *
 * Maze.java
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


public class Maze {
	public static int GRID_POINT_FULL = -2;
	public static int GRID_POINT_EMPTY = -1;

	
	Grid gridPtr;
    Queue_t workQueuePtr;
    Vector_t wallVectorPtr; /* contains source/destination pairs to route */
    Vector_t srcVectorPtr;  /* obstacles */
    Vector_t dstVectorPtr;  /* destinations */

    public Maze() {}


/* =============================================================================
 * maze_alloc
 * =============================================================================
 maze_t* maze_alloc ();
 */
   public static Maze alloc() 
   {
       Maze mazePtr = new Maze();

       mazePtr.gridPtr = null;
       mazePtr.workQueuePtr = Queue_t.queue_alloc(1024);
       mazePtr.wallVectorPtr = Vector_t.vector_alloc(1);
       mazePtr.srcVectorPtr = Vector_t.vector_alloc(1);
       mazePtr.dstVectorPtr = Vector_t.vector_alloc(1);


       return mazePtr;
   }

/* =============================================================================
 * maze_free
 * =============================================================================
 void maze_free (maze_t* mazePtr);
 */
    public static void free(Maze m)
    {
        m = null;
    }    

/* =============================================================================
 * addToGrid
 * =============================================================================
 */
    private void addToGrid(Grid gridPtr,Vector_t vectorPtr,String type)
    {
        int i;
        int n = vectorPtr.vector_getSize();

        for(i = 0; i < n; i++) {
            Coordinate coordinatePtr = (Coordinate)vectorPtr.vector_at(i);
            if(!gridPtr.isPointValid(coordinatePtr.x,coordinatePtr.y,coordinatePtr.z))
            {
                System.out.println("Error: " + type + " (" + coordinatePtr.x + 
                                                      ", " + coordinatePtr.y + 
                                                      ", " + coordinatePtr.z);
                System.exit(1);
            }
        }
        gridPtr.addPath(vectorPtr);
    }
/* =============================================================================
 * maze_read
 * -- Return number of path to route
 * =============================================================================
 long maze_read (maze_t* mazePtr, char* inputFileName);
 */
    public int readMaze(String inputFileName) throws NumberFormatException, IOException
    {
            FileInputStream in = new FileInputStream(inputFileName);    
            /*
             * Parse input file
             */
            int lineNumber = 0;
            int height = -1;
            int width = -1;
            int depth = -1;
            boolean isParseError = false;
            List_t workListPtr = List_t.alloc(1); // List.alloc(Coordinate.comparePair);
            String line;

            while((line = new DataInputStream(in).readLine()) != null) {
                
                String code;
                int[] xy = new int[6];  // equivalent to x1,y1,z1,x2,y2,z2
                int numToken = 0;
                
                StringTokenizer tok = new StringTokenizer(line);

                if((numToken = tok.countTokens()) < 1 ) {
                    continue;
                }

                code = tok.nextToken();

                if(code.equals("#")) {
                    /* comment line */
                    continue;
                }
                for(int i=0;i<numToken-1;i++) {
                    xy[i] = Integer.parseInt(tok.nextToken());
                }

                if(code.equals("d")) {
                      /* dimensions (format: d x y z) */
                     if(numToken != 4) {
                        isParseError = true;
                     }
                     else {
                        width = xy[0];
                        height = xy[1];
                        depth = xy[2];
                        if(width < 1 || height < 1 || depth <1)
                            isParseError = true;
                     }
                 }else if(code.equals("p")) { /* paths (format: p x1 y1 z1 x2 y2 z2) */
                    if(numToken != 7) {
                        isParseError = true;
                    }
                    else {
                        Coordinate srcPtr = Coordinate.alloc(xy[0],xy[1],xy[2]);
                        Coordinate dstPtr = Coordinate.alloc(xy[3],xy[4],xy[5]);

                        if(Coordinate.isEqual(srcPtr,dstPtr)) {
                            isParseError = true;
                        }
                        else { 
                            Pair coordinatePairPtr = Pair.alloc(srcPtr,dstPtr);
                            boolean status = workListPtr.insert(coordinatePairPtr);
                            srcVectorPtr.vector_pushBack(srcPtr);
                            dstVectorPtr.vector_pushBack(dstPtr);
                            
                        }
                    }
                }else if(code.equals("w")) {
                         /* walls (format: w x y z) */
                        if(numToken != 4) {
                            isParseError = true;
                        } else {
                            Coordinate wallPtr = Coordinate.alloc(xy[0],xy[1],xy[2]);
                            wallVectorPtr.vector_pushBack(wallPtr);
                        }
                }else { /* error */
                       isParseError = true;
                }
                
                if(isParseError)  {/* Error */
                    System.out.println("Error: line " + lineNumber + " of " + inputFileName + "invalid");
                    System.exit(1);
                }
            }
            /* iterate over lines in put file */
          
            in.close();
            /* 
             * Initialize grid contents
             */
            if(width < 1 || height < 1 || depth < 1) {
                System.out.println("Error : Invalid dimensions ( " + width + ", " + height + ", "+ depth + ")");
                System.exit(1);
            }

            Grid gridPtr = Grid.alloc(width,height,depth);
            this.gridPtr = gridPtr;
            addToGrid(gridPtr,wallVectorPtr,"wall");
            addToGrid(gridPtr,srcVectorPtr, "source");
            addToGrid(gridPtr,dstVectorPtr, "destination");
            System.out.println("Maze dimensions = " + width + " x " + height + " x " + depth);
            System.out.println("Paths to route  = " + workListPtr.getSize());

            /*
             * Initialize work queue
             */
            List_Iter it = new List_Iter();
            it.reset(workListPtr);
            while(it.hasNext(workListPtr)) {
                Pair coordinatePairPtr = (Pair)it.next(workListPtr);
                workQueuePtr.queue_push(coordinatePairPtr);
            }

            List_t.free(workListPtr);

            return srcVectorPtr.vector_getSize();
    }
    

/* =============================================================================
 * maze_checkPaths
 * =============================================================================
 bool_t maze_checkPaths (maze_t* mazePtr, list_t* pathListPtr, bool_t doPrintPaths);
 */
    public boolean checkPaths(List_t pathVectorListPtr,boolean doPrintPaths)
    {
        int i;
       
        /* Mark walls */
        Grid testGridPtr = Grid.alloc(gridPtr.width,gridPtr.height,gridPtr.depth);
        testGridPtr.addPath(wallVectorPtr);

        /* Mark sources */
        int numSrc = srcVectorPtr.vector_getSize();
//        System.out.println("numSrc = " +numSrc);
//        System.exit(1);
        for(i = 0; i < numSrc; i++) {
            Coordinate srcPtr = (Coordinate)srcVectorPtr.vector_at(i);
            testGridPtr.setPoint(srcPtr.x,srcPtr.y,srcPtr.z,0);
        }

        /* Mark destinations */
        int numdst = dstVectorPtr.vector_getSize();
        for(i = 0; i < numdst; i++) {
            Coordinate dstPtr = (Coordinate)dstVectorPtr.vector_at(i);
            testGridPtr.setPoint(dstPtr.x,dstPtr.y,dstPtr.z,0);
        }

//        testGridPtr.print();

        /* Make sure path is contiguous and does not overlap */
        int id = 0;
        List_Iter it = new List_Iter();
        it.reset(pathVectorListPtr);

        int height = gridPtr.height;
        int width = gridPtr.width;
        int area = height * width;

        while(it.hasNext(pathVectorListPtr)) {
            Vector_t pathVectorPtr = (Vector_t)it.next(pathVectorListPtr);
            int numPath = pathVectorPtr.vector_getSize();
          
            for(i = 0; i < numPath; i++) {
                id++;
                Vector_t pointVectorPtr = (Vector_t)pathVectorPtr.vector_at(i);
                /* Check start */
                int prevGridPointIndex = ((Integer)pointVectorPtr.vector_at(0)).intValue();

		int z=prevGridPointIndex/area;
		int index2d=prevGridPointIndex%area;
		int y=index2d/width;
		int x=index2d%width;

                if(testGridPtr.getPoint(x,y,z) != 0) {
                    return false;
                }

                Coordinate prevCoordinate = new Coordinate();
                prevCoordinate.x = x;
                prevCoordinate.y = y;
                prevCoordinate.z = z;

                
                int numPoint = pointVectorPtr.vector_getSize();
                int j;

                for(j = 1; j< (numPoint - 1) ;j++) { /* no need to check endpoints */
                    int currGridPointIndex = ((Integer)pointVectorPtr.vector_at(j)).intValue();
                    Coordinate currCoordinate = new Coordinate();

		    z=currGridPointIndex/area;
		    index2d=currGridPointIndex%area;
		    y=index2d/width;
		    x=index2d%width;

                    currCoordinate.x = x;
                    currCoordinate.y = y;
                    currCoordinate.z = z;

                    if(!Coordinate.areAdjacent(currCoordinate,prevCoordinate)) {
                        System.out.println("you there?");
                        return false;
                    }

                    prevCoordinate = currCoordinate;
                    int xx = currCoordinate.x;
                    int yy = currCoordinate.y;
                    int zz = currCoordinate.z;
                    if(testGridPtr.getPoint(xx,yy,zz) != GRID_POINT_EMPTY) {
                        return false;
                    } else {
                        testGridPtr.setPoint(xx,yy,zz,id);
                    }
                }
                /* Check end */
                int lastGridPointIndex = ((Integer)pointVectorPtr.vector_at(j)).intValue();
		z=lastGridPointIndex/area;
		index2d=lastGridPointIndex%area;
		y=index2d/width;
		x=index2d%width;
                if(testGridPtr.getPoint(x,y,z) != 0) {
                    return false;
                }
            } /* iterate over pathVector */
        } /* iterate over pathVectorList */

        if(doPrintPaths) {
            System.out.println("\nRouted Maze:");
	    testGridPtr.print();
        }


        return true;
    }
                    
 }
/* =============================================================================
 *
 * End of maze.h
 *
 * =============================================================================
 */
