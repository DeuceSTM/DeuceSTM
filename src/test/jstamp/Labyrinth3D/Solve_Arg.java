package jstamp.Labyrinth3D;

    public class Solve_Arg {
        Router routerPtr;
        Maze mazePtr;
        List_t pathVectorListPtr;

        public Solve_Arg(Router r,Maze m,List_t l)
        {
            routerPtr = r;
            mazePtr = m;
            pathVectorListPtr = l;
        }
    }

