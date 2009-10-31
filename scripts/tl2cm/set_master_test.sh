#!/bin/sh

java="java -Xmx1g -Xms1g"
warmup=500
duration=5000
r=256
i=128
w=50

rm logs/*

for iter in 1; do # 2 3 4 5; do
for b in RBTree; do #IntJavaHashSet LinkedList RBTree SkipList; do
for t in 1 2 4 8 12 16 20 24 28 32; do
# Control Group 1 - TL2
log=logs/${iter}-intset-${b}-tl2-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.tl2.Context \
  -Djava.util.logging.config.file=config/logging.properties \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r ${r} -i ${i} -w ${w} >> $log 2>&1

# Control Group 2 - LSACM
for cm in Suicide Aggressive Timestamp; do
log=logs/${iter}-intset-${b}-lsacm-${cm}-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.lsacm.Context \
  -Dorg.deuce.transaction.lsacm.cm=org.deuce.transaction.lsacm.cm.${cm} \
  -Djava.util.logging.config.file=config/logging.properties \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r ${r} -i ${i} -w ${w} >> $log 2>&1
done

# Control Group 3 - Lock
log=logs/${iter}-intset-${b}-lock-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.global=true \
  -Djava.util.logging.config.file=config/logging.properties \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r ${r} -i ${i} -w ${w} >> $log 2>&1

# Test Groug
for cm in Suicide Aggressive Polite Karma Polka; do 
log=logs/${iter}-intset-${b}-tl2cm-${cm}-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.tl2cm.Context \
  -Dorg.deuce.transaction.tl2cm.ContentionManager=${cm} \
  -Djava.util.logging.config.file=config/logging.properties \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r ${r} -i ${i} -w ${w} >> $log 2>&1
done

done
done
done
