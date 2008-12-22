#!/bin/sh

# java="java -client -Xmx1024m -Xms1024m"
# java="java"
java="java -verbose:gc -Xmx1g -Xms1g -XXaggressive"

warmup=2000
duration=10000

for r in 1 2 3; do
for b in LinkedList SkipList; do
for i in 256 4096 16384; do
for w in 0 20 50; do
for t in 1 2 4 8 12 16; do

# No RO hint
for c in lsa.Context lsa64.Context tl2.Context; do
log=logs/intset-${b}-${c}-i${i}-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.${c} \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r 262144 -i ${i} -w ${w} >> $log 2>&1
done

# RO hint (LSA)
for c in lsa.Context lsa64.Context; do
log=logs/intset-${b}-${c}.ro-i${i}-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.${c} \
  -Dorg.deuce.transaction.lsa.rohint=true \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r 262144 -i ${i} -w ${w} >> $log 2>&1
done

# RO hint (TL2)
for c in tl2.Context; do
log=logs/intset-${b}-${c}.ro-i${i}-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.contextClass=org.deuce.transaction.${c} \
  -Dorg.deuce.transaction.tl2.rohint=true \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r 262144 -i ${i} -w ${w} >> $log 2>&1
done

# Lock
for c in lock; do
log=logs/intset-${b}-${c}-i${i}-w${w}-t${t}.log
CLASSPATH=classes \
  ${java} -javaagent:bin/deuceAgent.jar \
  -Dorg.deuce.transaction.global=true \
  org.deuce.benchmark.Driver -n ${t} -d ${duration} -w ${warmup} \
  org.deuce.benchmark.intset.Benchmark ${b} -r 262144 -i ${i} -w ${w} >> $log 2>&1
done

done
done
done
done
done
