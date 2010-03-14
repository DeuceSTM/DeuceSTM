MAINCLASS=SSCA2
SRC=tmp${MAINCLASS}.java \
	Alg_Radix_Smp.java \
	ComputeGraph.java \
    CreatePartition.java \
    Edge.java \
    GenScalData.java \
	GetStartLists.java \
	GetUserParameters.java \
	Globals.java \
	Graph.java \
	GraphSDG.java \
	LocalStartStop.java \
	VList.java \
	../common/Random.java \
    ../../../ClassLibrary/JavaSTM/Barrier.java

include ../common/Makefile.flags

include ../common/Makefile.builds

prep:
#	cpp -DUSE_PARALLEL_DATA_GENERATION ${MAINCLASS}.java > tmp1${MAINCLASS}.java
#	./extractLines
#	../../../buildscript ${FLAGS} -o ${MAINCLASS} ${SRC}
	cpp -DENABLE_KERNEL1 ${MAINCLASS}.java > tmp1${MAINCLASS}.java
	./extractLines


clean:
	rm tmp1SSCA2.java
	rm tmpSSCA2.java
	rm -rf tmpbuilddirectory
	rm *.bin
