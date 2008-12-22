#!/bin/sh

for b in LinkedList SkipList; do
for i in 256 4096 16384; do
for w in 0 20 50; do

j=0
for c in lock tl2.Context lsa.Context lsa.Context.ro lsa64.Context lsa64.Context.ro; do
    data=logs/intset-${b}-${c}-i${i}-w${w}.data
    rm -f ${data}
    for t in 1 2 4 8 12 16; do
        log=logs/intset-${b}-${c}-i${i}-w${w}-t${t}.log
        iterations=`grep "Nb iterations" ${log} | awk '{ s += $4; nb++ } END { printf "%f", s/nb }'`
        duration=`grep "Test duration" ${log} | awk '{ s += $5; nb++ } END { printf "%f", s/nb/1000 }'`
        echo "${t} ${iterations} ${duration}" >> ${data}
    done
    j=`expr ${j} \+ 1`
done

g=logs/graph-intset-${b}-i${i}-w${w}.gp
eps=logs/graph-intset-${b}-i${i}-w${w}.eps

echo "set term postscript eps enhanced color 22" > ${g}
echo "set output \"${eps}\"" >> ${g}
echo "set title \"IntSet ${b}, size=${i}, update=${w}%\"" >> ${g}
echo "set key bottom" >> ${g}
echo "set key right" >> ${g}
echo "set xtics 1" >> ${g}
echo "set xlabel \"Number of threads\"" >> ${g}
echo "set ylabel \"Throughput (transactions/s)\"" >> ${g}
echo -n "plot " >> ${g}

for c in lock tl2.Context lsa.Context lsa.Context.ro lsa64.Context lsa64.Context.ro; do
    data=logs/intset-${b}-${c}-i${i}-w${w}.data
    echo -n "\"${data}\" using 1:(\$2/\$3) title \"${c}\" with lines" >> ${g}
    j=`expr ${j} \- 1`
    if [ ${j} -gt 0 ]; then
        echo ", \\" >> ${g}
        echo -n "    " >> ${g}
    else
        echo "" >> ${g}
    fi
done

gnuplot ${g}
epstopdf ${eps}

done
done
done
