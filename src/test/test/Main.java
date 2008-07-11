package test;

public class Main {
	
	 // Field descriptor #17 Ljava/lang/String;
	  public static  java.lang.String RERAN_FAILURE = "FAILURE";
	  
	  // Field descriptor #21 J
	  public static  long RERAN_FAILURE__ADDRESS__;
	  
	  // Field descriptor #17 Ljava/lang/String;
	  public static  java.lang.String RERAN_ERROR = "ERROR";
	  
	  // Field descriptor #21 J
	  public static  long RERAN_ERROR__ADDRESS__;
	  
	  // Field descriptor #17 Ljava/lang/String;
	  public static  java.lang.String RERAN_OK = "OK";
	  
	  // Field descriptor #21 J
	  public static  long RERAN_OK__ADDRESS__;
	  
	  // Field descriptor #31 [Ljava/lang/String;
	  private java.lang.String[] fTestClassNames;
	  
	  // Field descriptor #21 J
	  public static  long fTestClassNames__ADDRESS__;
	  
	  // Field descriptor #17 Ljava/lang/String;
	  private java.lang.String fTestName;
	  
	  // Field descriptor #21 J
	  public static  long fTestName__ADDRESS__;
	  
//	  // Field descriptor #36 Lorg/eclipse/jdt/internal/junit/runner/TestExecution;
//	  private org.eclipse.jdt.internal.junit.runner.TestExecution fExecution;
	  
	  // Field descriptor #21 J
	  public static  long fExecution__ADDRESS__;
	  
	  // Field descriptor #17 Ljava/lang/String;
	  private java.lang.String fVersion;
	  
	  // Field descriptor #21 J
	  public static  long fVersion__ADDRESS__;
	  
	  // Field descriptor #41 Ljava/net/Socket;
	  private java.net.Socket fClientSocket;
	  
	  // Field descriptor #21 J
	  public static  long fClientSocket__ADDRESS__;
	  
	  // Field descriptor #44 Ljava/io/PrintWriter;
	  private java.io.PrintWriter fWriter;
	  
	  // Field descriptor #21 J
	  public static  long fWriter__ADDRESS__;
	  
	  // Field descriptor #47 Ljava/io/BufferedReader;
	  private java.io.BufferedReader fReader;
	  
	  // Field descriptor #21 J
	  public static  long fReader__ADDRESS__;
	  
	  // Field descriptor #17 Ljava/lang/String;
	  private java.lang.String fHost;
	  
	  // Field descriptor #21 J
	  public static  long fHost__ADDRESS__;
	  
	  // Field descriptor #52 I
	  private int fPort;
	  
	  // Field descriptor #21 J
	  public static  long fPort__ADDRESS__;
	  
	  // Field descriptor #55 Z
	  private boolean fDebugMode;
	  
	  // Field descriptor #21 J
	  public static  long fDebugMode__ADDRESS__;
	  
	  // Field descriptor #55 Z
	  private boolean fKeepAlive;
	  
	  // Field descriptor #21 J
	  public static  long fKeepAlive__ADDRESS__;
	  
	  // Field descriptor #55 Z
	  private boolean fStopped;
	  
	  // Field descriptor #21 J
	  public static  long fStopped__ADDRESS__;
	  
	  // Field descriptor #62 Ljava/util/Vector;
	  private java.util.Vector fRerunRequests;
	  
	  // Field descriptor #21 J
	  public static  long fRerunRequests__ADDRESS__;
	  
//	  // Field descriptor #65 Lorg/eclipse/jdt/internal/junit/runner/RemoteTestRunner$ReaderThread;
//	  private org.eclipse.jdt.internal.junit.runner.RemoteTestRunner$ReaderThread fReaderThread;
	  
	  // Field descriptor #21 J
	  public static  long fReaderThread__ADDRESS__;
	  
	  // Field descriptor #17 Ljava/lang/String;
	  private java.lang.String fRerunTest;
	  
	  // Field descriptor #21 J
	  public static  long fRerunTest__ADDRESS__;
	  
//	  // Field descriptor #70 Lorg/eclipse/jdt/internal/junit/runner/TestIdMap;
//	  private  org.eclipse.jdt.internal.junit.runner.TestIdMap fIds;
	  
	  // Field descriptor #21 J
	  public static  long fIds__ADDRESS__;
	  
	  // Field descriptor #31 [Ljava/lang/String;
	  private java.lang.String[] fFailureNames;
	  
	  // Field descriptor #21 J
	  public static  long fFailureNames__ADDRESS__;
	  
//	  // Field descriptor #75 Lorg/eclipse/jdt/internal/junit/runner/ITestLoader;
//	  private org.eclipse.jdt.internal.junit.runner.ITestLoader fLoader;
	  
	  // Field descriptor #21 J
	  public static  long fLoader__ADDRESS__;
	  
//	  // Field descriptor #78 Lorg/eclipse/jdt/internal/junit/runner/MessageSender;
//	  private org.eclipse.jdt.internal.junit.runner.MessageSender fSender;
	  
	  // Field descriptor #21 J
	  public static  long fSender__ADDRESS__;
	  
	  // Field descriptor #55 Z
	  private boolean fConsoleMode;
	  
	  // Field descriptor #21 J
	  public static  long fConsoleMode__ADDRESS__;
	  
//	  // Field descriptor #83 Ljava/lang/Class;
//	  static synthetic java.lang.Class class$0;
	  
	  // Field descriptor #21 J
	  public static  long class$0__ADDRESS__;
	  
	  // Field descriptor #334 Ljava/lang/Object;
	  public static  java.lang.Object __CLASS_BASE__;

    /**
     * @param args
     * @throws Exception 
     * @throws SecurityException 
     */
    public static void main(String[] args) throws SecurityException, Exception {
    	
    	Main.class.getField("asd");
//	Unsafe unsafe = UnsafeHolder.getUnsafe();
//	Field f = AAA.class.getDeclaredField( "x");
//	long offset = unsafe.objectFieldOffset(f);
//	
//	AAA aaa = new AAA();
//	
//	int x = 0;
//	 
//	for( int i=0 ; i<5000000 ; ++i)
//	    x += f.getInt( aaa);
//
//	
//	long time1 = System.nanoTime();
//	for( int i=0 ; i<5000000 ; ++i)
//	    x += f.getInt( aaa);
//
//
//	System.out.println( System.nanoTime() - time1);
//
//	
//	for( int i=0 ; i<5000000 ; ++i)
//	    x += unsafe.getInt( aaa, offset);
//
//	
//	time1 = System.nanoTime();
//	for( int i=0 ; i<5000000 ; ++i)
//	    x += unsafe.getInt( aaa, offset);
//	
//	System.out.println( System.nanoTime() - time1);
////	unsafe.putInt( obj, address, 8);
//	
    }
    
    public static class AAA{
	public int x = 6;
    }

}
