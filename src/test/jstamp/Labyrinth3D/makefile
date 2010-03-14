MAINCLASS=Labyrinth
SRC=ttttmp${MAINCLASS}.java \
	../common/Pair.java \
	ttttmpQueue_t.java \
	ttttmpQueue_Int.java \
	Vector_t.java \
	List_t.java \
	List_Node.java \
	List_Iter.java \
	Coordinate.java \
	ttttmpGrid.java \
	ttttmpMaze.java \
	ttttmpRouter.java \
	Point.java \
	Solve_arg.java \
	../../../ClassLibrary/JavaSTM/Barrier.java 

include ../common/Makefile.flags

include ../common/Makefile.builds

prep:
	cpp -P ${MAINCLASS}.java > tmp${MAINCLASS}.java
	cpp -P Grid.java	> tmpGrid.java
	cpp -P Router.java > tmpRouter.java
	cpp -P Maze.java > tmpMaze.java
	cpp -P Queue_t.java > tmpQueue_t.java
	cpp -P Queue_Int.java > tmpQueue_Int.java
	./extractLines tmp${MAINCLASS}.java
	./extractLines tmpGrid.java
	./extractLines tmpRouter.java
	./extractLines tmpMaze.java
	./extractLines tmpQueue_t.java
	./extractLines tmpQueue_Int.java


clean:
	rm -rf tmpbuilddirectory
	rm *.bin

test:
	cpp ${MAINCLASS}.java > tmp${MAINCLASS}.java
	cpp Grid.java	> tmpGrid.java
	cpp Router.java > tmpRouter.java
	cpp Maze.java > tmpMaze.java
	cpp Queue_t.java > tmpQueue_t.java
	cpp Queue_Int.java > tmpQueue_Int.java
	./extractLines tmp${MAINCLASS}.java
	./extractLines tmpGrid.java
	./extractLines tmpRouter.java
	./extractLines tmpMaze.java
	./extractLines tmpQueue_t.java
	./extractLines tmpQueue_Int.java
	../../../buildscript ${FLAGS} -o ${MAINCLASS} ${SRC}
	rm ttt*.java tmp*.java
	./Labyrinth.bin -i inputs/random-x32-y32-z3-n64.txt
