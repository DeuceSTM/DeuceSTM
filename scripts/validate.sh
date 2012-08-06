#!/bin/bash

echo "-------------------- JSTAMP Genome------------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context jstamp.genome.Genome -g 256 -s 16 -n 16384 -t 1

echo "------------------ JSTAMP Intruder -----------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context jstamp.intruder.Intruder

echo "----------------- JSTAMP Vacation -------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context jstamp.vacation.Vacation

echo "----------------- JSTAMP SSCA2 ---------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context jstamp.ssca2.SSCA2 -s 13 -i 1 -u 1 -l 3 -p 3 -t 4

echo "----------------- JSTAMP Labyrinth ---------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context jstamp.Labyrinth3D.Labyrinth -i src/test/jstamp/Labyrinth3D/inputs/random-x32-y32-z3-n96.txt

echo "----------------- JSTAMP KMeans ---------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context jstamp.KMeans.KMeans -nthreads 1 -m 40 -n 40 -t 0.05 -i src/test/jstamp/KMeans/inputs/random-n2048-d16-c16.txt

echo "----------------- LinkedList ---------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context org.deuce.benchmark.Driver -n 2 -d 2000 -w 500 org.deuce.benchmark.intset.Benchmark LinkedList -r 262144 -i 4096 -w 20

echo "------------------ SkipList-----------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context org.deuce.benchmark.Driver -n 2 -d 2000 -w 500 org.deuce.benchmark.intset.Benchmark SkipList -r 262144 -i 4096 -w 20

echo "------------------ RBTree -----------------"
java -Xmx4g -Xms4g -cp bin/tests -javaagent:bin/deuceAgent.jar -Dorg.deuce.transaction.contextClass=org.deuce.transaction.$1.Context org.deuce.benchmark.Driver -n 2 -d 2000 -w 500 org.deuce.benchmark.intset.Benchmark RBTree -r 262144 -i 4096 -w 20

