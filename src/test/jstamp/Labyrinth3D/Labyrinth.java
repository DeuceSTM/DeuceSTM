/* =============================================================================
 *
 * Labyrinth.java
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

import java.io.IOException;

public class Labyrinth extends Thread{

    static String global_inputFile;
    static boolean global_doPrint;
    int numThread;
    int bendCost;
    int xCost;
    int yCost;
    int zCost;


    // For threads
    int threadID;
    Solve_Arg routerArg;

    private void setDefaultParams() {

        /* default values */
        global_inputFile = null;
        global_doPrint = false;
        bendCost = 1;
        xCost = 1;
        yCost = 1;
        zCost = 2;
        numThread = 1;
    }

    private void parseArg(String[] argv) {
        int i=0;
        String arg;
        boolean opterr = false;


        setDefaultParams();

        while (i < argv.length) {

            if(argv[i].charAt(0) == '-' ) {
                arg = argv[i++];
                // check options
                if(arg.equals("-b")) {
                    bendCost = Integer.parseInt(argv[i++]);
                }
                else if(arg.equals("-x")) {
                    xCost = Integer.parseInt(argv[i++]);
                    }
                else if(arg.equals("-y")) {
                    yCost = Integer.parseInt(argv[i++]);
                    }
                else if(arg.equals("-z")) {
                    zCost = Integer.parseInt(argv[i++]);
                    }
                else if(arg.equals("-t")) {
                        numThread = Integer.parseInt(argv[i++]);
                }
                else if(arg.equals("-i")) {
                    global_inputFile = argv[i++];
                    }
                else if(arg.equals("-p")) {
                        global_doPrint = true;
                }
                else {
                    System.out.println("Non-option argument: " + argv[i]);
                    opterr = true;
                }   
            
            }
        }
        if(opterr) {
            displayUsage();
            System.exit(1);
        }
    }

    public Labyrinth(String[] argv)
    {     
        parseArg(argv);
    }


    public Labyrinth(int myID,Solve_Arg rArg)
    {
        threadID = myID;
        routerArg = rArg;
    }

    public void run() {
            Barrier.enterBarrier();
            Router.solve(routerArg);
            Barrier.enterBarrier();
    }

    public void displayUsage() 
    {
        System.out.println("Usage: Labyrinth [options]");
        System.out.println("Options:");
        System.out.println("    b <INT>     bend cost");
        System.out.println("    i <FILE>    input file name");
        System.out.println("    p           print routed maze");
        System.out.println("    t <INT>     Number of threads");
        System.out.println("    x <INT>     x movement cost");
        System.out.println("    y <INT>     y movement cost");
        System.out.println("    z <INT>     z movement cost");
    }
        

    public static void main(String[] argv) 
    {
        /*
         * Initailization
         */

        Labyrinth labyrinth = new Labyrinth(argv);

        Barrier.setBarrier(labyrinth.numThread);

        Maze mazePtr = Maze.alloc();

        try {
			int numPathToRoute =  mazePtr.readMaze(labyrinth.global_inputFile);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Router routerPtr = Router.alloc(labyrinth.xCost,labyrinth.yCost,
                                        labyrinth.zCost,labyrinth.bendCost);

        List_t pathVectorListPtr = List_t.alloc(0);     // list_t.alloc(null)
        /*
         * Run transactions
         */
        Solve_Arg routerArg = new Solve_Arg(routerPtr,mazePtr,pathVectorListPtr);

        /* Create and start thread */

        Labyrinth[] lb = new Labyrinth[labyrinth.numThread];

        for(int i = 1; i<labyrinth.numThread;i++) {
            lb[i] = new Labyrinth(i,routerArg);
        }

        for(int i = 1; i<labyrinth.numThread;i++) {
            lb[i].start();
        }
        long start = System.currentTimeMillis();

        Barrier.enterBarrier();
        Router.solve(routerArg);        
        Barrier.enterBarrier();

        /* End of Solve */
        long finish = System.currentTimeMillis();
	long diff=finish-start;
	System.out.println("TIME="+diff);


        int numPathRouted = 0;
        List_Iter it = new List_Iter();

        it.reset(pathVectorListPtr);
        while(it.hasNext(pathVectorListPtr)) {
	  Vector_t pathVectorPtr = (Vector_t)it.next(pathVectorListPtr);
	  numPathRouted += pathVectorPtr.vector_getSize();
        }

        float elapsed = ((float)finish-(float)start)/1000;

        System.out.println("Paths routed    = " + numPathRouted);
        System.out.println("Elapsed time    = " + elapsed);

        boolean stats = mazePtr.checkPaths(pathVectorListPtr,labyrinth.global_doPrint);
        if(!stats)
        {
            System.out.println("Verification not passed");
            
        }
        else 
            System.out.println("Verification passed.");
        System.out.println("Finished");    
    }
}

/* =============================================================================
 *
 * End of labyrinth.c
 *
 * =============================================================================
 */
