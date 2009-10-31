#!/bin/sh

java="java -Xmx1g -Xms1g"
warmup=500
duration=5000
r=256
i=128
w=50
cm=Karma

rm logs/*

for b in RBTree; do #IntJavaHashSet LinkedList RBTree SkipList; do
for mapImpl in CHM COWAL RWL ARR HM; do
for t in 1 2 4 8 12 16 20 24 28 32; do
for iter in 1 2 3 4 5; do
log=logs/${iter}-intset-${b}-tl2cm-${mapImpl}-${cm}-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.tl2cm.Context \
  -Dorg.deuce.transaction.tl2cm.ContentionManager=Karma \
  -Dorg.deuce.transaction.tl2cm.ContextsMap=${mapImpl} \
  -Djava.util.logging.config.file=config/logging.properties \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r ${r} -i ${i} -w ${w} >> $log 2>&1
done
done
done
done