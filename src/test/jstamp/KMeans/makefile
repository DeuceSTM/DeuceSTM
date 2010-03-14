MAINCLASS=KMeans
SRC=${MAINCLASS}.java \
	Random.java \
	Cluster.java \
    Normal.java \
    Common.java \
    GlobalArgs.java \
    ../../../ClassLibrary/JavaSTM/Barrier.java

include ../common/Makefile.flags

include ../common/Makefile.builds

FLAGSSTATS=-mainclass ${MAINCLASS} -singleTM -optimize -debug -joptimize -32bit -abcclose -dcopts -transstats -arraypad -stmstats

stmlock:
	../../../buildscript ${FLAGSSTATS} -abrt 0 -o STATS${MAINCLASS} ${SRC}

prep:

clean:
	rm -rf tmpbuilddirectory
	rm *.bin
