#!/bin/sh

java="/usr/lib/jvm/java-6-sun-1.6.0.13/jre/bin/java -Xmx1g -Xms1g"
#java="java -Xmx1g -Xms1g"
warmup=500
duration=5000

#cmList="KarmaLockStealerBW"
cmList="Suicide KarmaLockStealerBW"
bList="RBTree"
iterList="1 2 3 4 5"
#wList="20"
wList="5 20 50"
#rList="1024"
rList="16384 65536 524288"
#tList="16"
tList="1 4 8 16 32 64"

# clear previous experiments
rm logs/*

# run the experiment
for iter in $iterList; do
	for w in $wList; do
		for r in $rList; do
			let i=$r
			for b in $bList; do
				for t in $tList; do
					for cm in $cmList; do
						log=logs/${iter}-intset-${b}-tl2cm-${cm}-w${w}-r${r}-t${t}.log
						echo "Launching $log"  
						CLASSPATH=bin/classes \
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
	done
done

