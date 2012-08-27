package jstamp.genome;

public class Genome extends Thread {
  int geneLength;
  int segmentLength;
  int minNumSegment;
  int numThread;

  int threadid;

  // add segments, random, etc to member variables
  // include in constructor
  // allows for passing in thread run function
  Random randomPtr;
  Gene genePtr;
  Segments segmentsPtr;
  Sequencer sequencerPtr;

  Genome(String x[]) {
    parseCmdLine(x);
    if(numThread == 0) {
      numThread = 1;
    }

    randomPtr = new Random();
    randomPtr.random_alloc();
    randomPtr.random_seed(0);

    genePtr = new Gene(geneLength);
    genePtr.create(randomPtr);

    segmentsPtr = new Segments(segmentLength, minNumSegment);
    segmentsPtr.create(genePtr, randomPtr);

    sequencerPtr = new Sequencer(geneLength, segmentLength, segmentsPtr);
  }

  Genome(int myThreadid, int myGeneLength, int mySegLength, int myMinNumSegs, int myNumThread, Random myRandomPtr, Gene myGenePtr, Segments mySegmentsPtr, Sequencer mySequencerPtr) {
    threadid = myThreadid;
    geneLength = myGeneLength;
    segmentLength = mySegLength;
    minNumSegment = myMinNumSegs;
    numThread = myNumThread;

    randomPtr = myRandomPtr;
    genePtr = myGenePtr;
    segmentsPtr = mySegmentsPtr;
    sequencerPtr = mySequencerPtr;
  }

  public void parseCmdLine(String args[]) {
    int i = 0;
    String arg;
    while (i < args.length && args[i].startsWith("-")) {
      arg = args[i++];
      //check options
      if(arg.equals("-g")) {
        if(i < args.length) {
          this.geneLength = new Integer(args[i++]).intValue();
        }
      } else if(arg.equals("-s")) {
        if(i < args.length) {
          this.segmentLength = new Integer(args[i++]).intValue();
        }
      } else if(arg.equals("-n")) {
        if(i < args.length) {
          this.minNumSegment = new Integer(args[i++]).intValue();
        }
      } else if(arg.equals("-t")) {
        if(i < args.length) {
          this.numThread = new Integer(args[i++]).intValue();
        }
      } 
    }

  }

  public void run() {
    Barrier.enterBarrier();
    Sequencer.run(threadid, numThread, randomPtr, sequencerPtr); 
    Barrier.enterBarrier();
  }

  public static void main(String x[]){

    System.out.print("Creating gene and segments... ");
    Genome g = new Genome(x);

    System.out.println("done.");
    System.out.println("Gene length     = " + g.genePtr.length);
    System.out.println("Segment length  = " + g.segmentsPtr.length);
    System.out.println("Number segments = " + g.segmentsPtr.contentsPtr.size());
    System.out.println("Number threads  = " + g.numThread);


    Barrier.setBarrier(g.numThread);

    /* Create and Start Threads */

    ByteString gene = g.genePtr.contents;
    Genome[] gn = new Genome[g.numThread];

    for(int i = 1; i<g.numThread; i++) {
      gn[i] = new Genome(i, g.geneLength, g.segmentLength, g.minNumSegment, g.numThread, g.randomPtr, g.genePtr, g.segmentsPtr, g.sequencerPtr);
    }

    System.out.print("Sequencing gene... ");    

    for(int i = 1; i<g.numThread; i++) {
      gn[i].start();
    }
    
    long start=System.currentTimeMillis();
    Barrier.enterBarrier();
    Sequencer.run(0, g.numThread, g.randomPtr, g.sequencerPtr); 
    Barrier.enterBarrier();
    long stop=System.currentTimeMillis();
    long diff=stop-start;
    System.out.println("TIME="+diff);

    System.out.println("done.");

    /* Check result */
    {
      ByteString sequence = g.sequencerPtr.sequence;
      boolean result = gene.compareTo(sequence) == 0;
      System.out.println("Sequence matches gene: " + (result ? "yes" : "no"));
      //DEBUG
      //if (result) {
      // System.out.println("gene     = " + gene);
      // System.out.println("sequence = " + sequence);
      //}
    }

  }
}
