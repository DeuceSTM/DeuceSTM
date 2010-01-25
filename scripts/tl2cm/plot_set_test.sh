#!/bin/sh

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

threadsForAbortsPatternList="8 32 64"

# delete old output files
rm logs/*.data
rm logs/*.gp
rm logs/*.eps
rm logs/*.pdf
rm logs/*.txt

for b in $bList; do
	for w in $wList; do
		for r in $rList; do
			let i=$r
			j=0
			for cm in $cmList; do
			    data=logs/intset-ta-${b}-tl2cm-${cm}-w${w}-r${r}.data
			    echo "Processing ${data}"
			    rm -f ${data}
			    for t in $tList; do
				    sumIterations=0
				    sumDuration=0
				    sumAborts=0
					sumAbortsDuringSpeculation=0
					sumAbortsDuringSpeculationReadVersion=0
					sumAbortsDuringSpeculationLocationLocked=0
				    sumAbortsDuringCommit=0
				    sumAbortsDuringCommitRSValid=0
				    sumAbortsDuringCommitWSLock=0
					for iter in $iterList; do
				        log=logs/${iter}-intset-${b}-tl2cm-${cm}-w${w}-r${r}-t${t}.log
				        # parsing log
				        iterations=`grep "Nb iterations" ${log} | awk '{ s += $4; nb++ } END { printf "%f", s/nb }'`
				        duration=`grep "Test duration" ${log} | awk '{ s += $5; nb++ } END { printf "%f", s/nb/1000 }'`
						aborts=`grep "Aborts (%)" ${log} | awk '{ s += $4; nb++ } END { printf "%f", s/nb }'`
						abortsDuringSpeculation=`grep "During speculation" ${log} | awk '{ s += $6; nb++ } END { printf "%f", s/nb }'`
						abortsDuringSpeculationReadVersion=`grep "Newer Read Version" ${log} | awk '{ s += $7; nb++ } END { printf "%f", s/nb }'`
						abortsDuringSpeculationLocationLocked=`grep "Location locked" ${log} | awk '{ s += $6; nb++ } END { printf "%f", s/nb }'`
						abortsDuringCommit=`grep "During commit" ${log} | awk '{ s += $6; nb++ } END { printf "%f", s/nb }'`
						abortsDuringCommitRSValid=`grep "Readset Validation" ${log} | awk '{ s += $6; nb++ } END { printf "%f", s/nb }'`
						abortsDuringCommitWSLock=`grep "Writeset Locking" ${log} | awk '{ s += $6; nb++ } END { printf "%f", s/nb }'`
						
						# summing up
						sumIterations=`echo "$sumIterations + $iterations" | bc`
						sumDuration=`echo "$sumDuration + $duration" | bc`
						sumAborts=`echo "$sumAborts + $aborts" | bc`
						sumAbortsDuringSpeculation=`echo "$sumAbortsDuringSpeculation + $abortsDuringSpeculation" | bc`
						sumAbortsDuringSpeculationReadVersion=`echo "$sumAbortsDuringSpeculationReadVersion + $abortsDuringSpeculationReadVersion" | bc`
						sumAbortsDuringSpeculationLocationLocked=`echo "$sumAbortsDuringSpeculationLocationLocked + $abortsDuringSpeculationLocationLocked" | bc`
						sumAbortsDuringCommit=`echo "$sumAbortsDuringCommit + $abortsDuringCommit" | bc`
						sumAbortsDuringCommitRSValid=`echo "$sumAbortsDuringCommitRSValid + $abortsDuringCommitRSValid" | bc`
						sumAbortsDuringCommitWSLock=`echo "$sumAbortsDuringCommitWSLock + $abortsDuringCommitWSLock" | bc`
				    done
				   
				    # finishing calculations
					sumIterations=`echo "tmp=$sumIterations; tmp /= 1; tmp" | bc`
					sumDuration=`echo "tmp=$sumDuration; tmp /= 1; tmp" | bc`
					throughput=`echo "tmp=($sumIterations/$sumDuration); tmp /= 1; tmp" | bc` 
					abortsPercentage=`echo "scale=2; $sumAborts/$iter;" | bc` 
					abortsDuringSpeculationPercentage=`echo "scale=2; $sumAbortsDuringSpeculation/$iter" | bc` 
					abortsDuringSpeculationReadVersionPercentage=`echo "scale=2; $sumAbortsDuringSpeculationReadVersion/$iter" | bc` 
					abortsDuringSpeculationLocationLockedPercentage=`echo "scale=2; $sumAbortsDuringSpeculationLocationLocked/$iter" | bc` 
					abortsDuringCommitPercentage=`echo "scale=2; $sumAbortsDuringCommit/$iter" | bc` 
					abortsDuringCommitRSValidPercentage=`echo "scale=2; $sumAbortsDuringCommitRSValid/$iter" | bc`
					abortsDuringCommitWSLockPercentage=`echo "scale=2; $sumAbortsDuringCommitWSLock/$iter" | bc`
					# output to the data file
					echo "t ${t} ${throughput} ${abortsPercentage} ${abortsDuringSpeculationPercentage} ${sumAbortsDuringSpeculationReadVersion} ${abortsDuringSpeculationLocationLocked} ${abortsDuringCommitPercentage} ${abortsDuringCommitWSLockPercentage} ${abortsDuringCommitRSValidPercentage}" >> ${data}
			    done
			    j=`expr ${j} \+ 1`
			done
		
			# process throughput graph
			echo " - Processing throughput graph"
			g=logs/graph-trpt-intset-${b}-r${r}-w${w}.gp
			eps=logs/graph-trpt-intset-${b}-r${r}-w${w}.eps	
			echo "set term postscript eps enhanced color 22" > ${g}
			echo "set output \"${eps}\"" >> ${g}
			echo "set size 1.5,1.5" >> ${g}
			echo "set title \"IntSet ${b}, keys rage=${r}, initial size=${i}, update=${w}%\"" >> ${g}
			echo "set key top" >> ${g}
			echo "set key right" >> ${g}
			#echo "set xtics 2" >> ${g}
			echo "set ytics nomirror" >> ${g}
			echo "set xlabel \"Number of threads\"" >> ${g}
			echo "set ylabel \"Throughput (transactions/s)\"" >> ${g}
			echo -n "plot " >> ${g}
			let j1=$j
			for cm in $cmList; do
				data=logs/intset-ta-${b}-tl2cm-${cm}-w${w}-r${r}.data
			    echo -n "\"${data}\" using 2:(\$3) title \"${cm}\" with lines" >> ${g}
			    j1=`expr ${j1} \- 1`
			    if [ ${j1} -gt 0 ]; then
			        echo ", \\" >> ${g}
			        echo -n "    " >> ${g}
			    else
			        echo "" >> ${g}
			    fi  
			done
			gnuplot ${g}
			epstopdf ${eps}
			
			# process summary table
			echo " - Processing summary table"
			for threadsForAbortsPattern in $threadsForAbortsPatternList; do
				table=logs/table-summ-intset-${b}-r${r}-w${w}-t${threadsForAbortsPattern}.txt
				rm -f ${table}
				echo "Summary table for benchmark: IntSet ${b}, keys rage=${r}, initial size=${i}, update=${w}%, $threadsForAbortsPattern threads" >> ${table}
				echo "" >> ${table}
				for cm in $cmList; do
					data=logs/intset-ta-${b}-tl2cm-${cm}-w${w}-r${r}.data					
					echo "Contention Manager: ${cm}" >> ${table}
					throughput=`grep "t ${threadsForAbortsPattern}" ${data} | awk '{ s += $3; nb++ } END { printf "%f", s/nb }'`
					throughput=`echo "tmp=($throughput); tmp /= 1; tmp" | bc`
					echo "  Throughput                                : ${throughput} tx/sec" >> ${table}
					
					totalAborts=`grep "t ${threadsForAbortsPattern}" ${data} | awk '{ s += $4; nb++ } END { printf "%f", s/nb }'`
					totalAborts=`echo "scale=2; tmp=($totalAborts); tmp /= 1; tmp" | bc`
					echo "  Total aborts                              : ${totalAborts}%" >> ${table}
					
					abortsDuringSpeculation=`grep "t ${threadsForAbortsPattern}" ${data} | awk '{ s += $5; nb++ } END { printf "%f", s/nb }'`
					abortsDuringSpeculation=`echo "scale=2; tmp=($abortsDuringSpeculation); tmp /= 1; tmp" | bc`
					echo "  Aborts during speculation                 : ${abortsDuringSpeculation}%" >> ${table}
					
					abortsDuringSpeculationReadVersion=`grep "t ${threadsForAbortsPattern}" ${data} | awk '{ s += $6; nb++ } END { printf "%f", s/nb }'`
					abortsDuringSpeculationReadVersion=`echo "scale=2; tmp=($abortsDuringSpeculationReadVersion); tmp /= 1; tmp" | bc`
					echo "  Aborts during speculation Read Version    : ${abortsDuringSpeculationReadVersion}%" >> ${table}
					
					abortsDuringSpeculationLocationLocked=`grep "t ${threadsForAbortsPattern}" ${data} | awk '{ s += $7; nb++ } END { printf "%f", s/nb }'`
					abortsDuringSpeculationLocationLocked=`echo "scale=2; tmp=($abortsDuringSpeculationLocationLocked); tmp /= 1; tmp" | bc`
					echo "  Aborts during speculation Location Locked : ${abortsDuringSpeculationLocationLocked}%" >> ${table}
					
					abortsDuringCommitWSLock=`grep "t ${threadsForAbortsPattern}" ${data} | awk '{ s += $9; nb++ } END { printf "%f", s/nb }'`
					abortsDuringCommitWSLock=`echo "scale=2; tmp=($abortsDuringCommitWSLock); tmp /= 1; tmp" | bc`
					echo "  Aborts during Write-Set Locking           : ${abortsDuringCommitWSLock}%" >> ${table}
					
					abortsDuringCommitRSValid=`grep "t ${threadsForAbortsPattern}" ${data} | awk '{ s += $10; nb++ } END { printf "%f", s/nb }'`
					abortsDuringCommitRSValid=`echo "scale=2; tmp=($abortsDuringCommitRSValid); tmp /= 1; tmp" | bc`
					echo "  Aborts during Read-Set Validation         : ${abortsDuringCommitRSValid}%" >> ${table}
					echo "" >> ${table}
				done
			done
		done
	done
done

#compress the results to a zip file
zip -r "logs_$(date '+%d-%m')" logs







