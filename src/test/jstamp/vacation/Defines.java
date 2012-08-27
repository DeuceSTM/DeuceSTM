package jstamp.vacation;

public class Defines {

public final static int ACTION_MAKE_RESERVATION =0;
public final static int ACTION_DELETE_CUSTOMER= 1;
public final static int ACTION_UPDATE_TABLES= 2;
public final static long NUM_ACTION =2;
public final static long PARAM_CLIENTS= 'c';
public final static long PARAM_NUMBER ='n';
public final static long PARAM_QUERIES ='q';
public final static long PARAM_RELATIONS ='r';
public final static long PARAM_TRANSACTIONS ='t';
public final static long PARAM_USER= 'u';
public final static int PARAM_DEFAULT_CLIENTS    =  (1);
public final static int PARAM_DEFAULT_NUMBER     =  (10);
public final static int PARAM_DEFAULT_QUERIES    =  (90);
public final static int PARAM_DEFAULT_RELATIONS  =  (1 << 16);
public final static int PARAM_DEFAULT_TRANSACTIONS= (1 << 19); //(1 << 26);
public final static int PARAM_DEFAULT_USER        = (80);
public final static int RESERVATION_CAR =0;
public final static int RESERVATION_FLIGHT= 1;
public final static int RESERVATION_ROOM =2;
public final static int NUM_RESERVATION_TYPE =3;
public final static long OPERATION_MAKE_RESERVATION =0L;
public final static long OPERATION_DELETE_CUSTOMER =1L;
public final static long OPERATION_UPDATE_TABLE =2L;
public final static long NUM_OPERATION =3;

public final static int RED =0;
public final static int BLACK =1;

}