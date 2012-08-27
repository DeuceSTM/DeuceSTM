package jstamp.Labyrinth3D;

  public class Point {
        int x;
        int y;
        int z;
        int value;
        int momentum;

        public Point() {
            x = -1;
            y = -1;
            z = -1;
            value = -1;
            momentum = -1;
        }
        
        public Point(int x,int y, int z,int value, int m) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.value = value;
            momentum = m;
        }
    }

