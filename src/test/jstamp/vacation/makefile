MAINCLASS=Vacation
SRC=tmpVacation.java tmpClient.java tmpManager.java tmpRBTree.java	\
../../../ClassLibrary/JavaSTM/Barrier.java ../common/Random.java

include ../common/Makefile.flags

include ../common/Makefile.builds

prep:
	cat defines Client.java > Client.j
	cat defines Manager.java > Manager.j
	cat defines Vacation.java > Vacation.j
	cpp -P -CC Client.j > tmpClient.java
	cpp -P -CC Manager.j > tmpManager.java
	cpp -P -CC Vacation.j > tmpVacation.java
	cpp -P -CC RBTree.java > tmpRBTree.java

clean:
	rm -rf tmpbuilddirectory
	rm *.bin *.j tmp*.java

test:

