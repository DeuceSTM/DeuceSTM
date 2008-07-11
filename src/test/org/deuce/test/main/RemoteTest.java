package org.deuce.test.main;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

// Referenced classes of package org.eclipse.jdt.internal.junit.runner:
//            DefaultClassifier, FirstRunExecutionListener, ITestIdentifier, ITestLoader, 
//            ITestReference, IVisitsTestTrees, JUnitMessages, MessageSender, 
//            RerunExecutionListener, TestExecution, TestIdMap, IClassifiesThrowables

public class RemoteTest
{
    private class ReaderThread extends Thread
    {

        public void run()
        {
            try
            {
                while(true) 
                {
                    String message;
                    for(message = null; (message = fReader.readLine()) == null;);
                    if(message.startsWith(">STOP   "))
                    {
                        fStopped = true;
                        stop();
                        synchronized(RemoteTest.this)
                        {
                            notifyAll();
                        }
                        break;
                    }
                    if(message.startsWith(">RERUN  "))
                    {
                        String arg = message.substring(8);
                        int c0 = arg.indexOf(' ');
                        int c1 = arg.indexOf(' ', c0 + 1);
                        String s = arg.substring(0, c0);
                        int testId = Integer.parseInt(s);
                        String className = arg.substring(c0 + 1, c1);
                        String testName = arg.substring(c1 + 1, arg.length());
                        synchronized(RemoteTest.this)
                        {
                            fRerunRequests.add(new RerunRequest(testId, className, testName));
                            notifyAll();
                        }
                    }
                }
            }
            catch(Exception _ex)
            {
                stop();
            }
        }

        public ReaderThread()
        {
            super("ReaderThread");
        }
    }

    private static class RerunRequest
    {

        String fRerunClassName;
        String fRerunTestName;
        int fRerunTestId;

        public RerunRequest(int testId, String className, String testName)
        {
            fRerunTestId = testId;
            fRerunClassName = className;
            fRerunTestName = testName;
        }
    }


    public RemoteTest()
    {
        fVersion = "";
        fHost = "";
        fPort = -1;
        fDebugMode = false;
        fKeepAlive = false;
        fStopped = false;
        fRerunRequests = new Vector(10);
        fConsoleMode = false;
    }


    public static void main(String args[])
    {
        try
        {
            RemoteTest testRunServer = new RemoteTest();
            testRunServer.init(args);
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

    protected void init(String args[])
    {
        defaultInit(args);
    }

    protected ClassLoader getTestClassLoader()
    {
        return getClass().getClassLoader();
    }

    protected final void defaultInit(String args[])
    {
        for(int i = 0; i < args.length; i++)
            if(args[i].toLowerCase().equals("-classnames") || args[i].toLowerCase().equals("-classname"))
            {
                Vector list = new Vector();
                for(int j = i + 1; j < args.length; j++)
                {
                    if(args[j].startsWith("-"))
                        break;
                    list.add(args[j]);
                }

                fTestClassNames = (String[])list.toArray(new String[list.size()]);
            } else
            if(args[i].toLowerCase().equals("-test"))
            {
                String testName = args[i + 1];
                int p = testName.indexOf(':');
                if(p == -1)
                    throw new IllegalArgumentException("Testname not separated by '%'");
                fTestName = testName.substring(p + 1);
                fTestClassNames = (new String[] {
                    testName.substring(0, p)
                });
                i++;
            } else
            if(args[i].toLowerCase().equals("-testnamefile"))
            {
                String testNameFile = args[i + 1];
                try
                {
                    readTestNames(testNameFile);
                }
                catch(IOException _ex)
                {
                    throw new IllegalArgumentException("Cannot read testname file.");
                }
                i++;
            } else
            if(args[i].toLowerCase().equals("-testfailures"))
            {
                String testFailuresFile = args[i + 1];
                try
                {
                    readFailureNames(testFailuresFile);
                }
                catch(IOException _ex)
                {
                    throw new IllegalArgumentException("Cannot read testfailures file.");
                }
                i++;
            } else
            if(args[i].toLowerCase().equals("-port"))
            {
                fPort = Integer.parseInt(args[i + 1]);
                i++;
            } else
            if(args[i].toLowerCase().equals("-host"))
            {
                fHost = args[i + 1];
                i++;
            } else
            if(args[i].toLowerCase().equals("-rerun"))
            {
                fRerunTest = args[i + 1];
                i++;
            } else
            if(args[i].toLowerCase().equals("-keepalive"))
                fKeepAlive = true;
            else
            if(args[i].toLowerCase().equals("-debugging") || args[i].toLowerCase().equals("-debug"))
                fDebugMode = true;
            else
            if(args[i].toLowerCase().equals("-version"))
            {
                fVersion = args[i + 1];
                i++;
            } else
            if(args[i].toLowerCase().equals("-junitconsole"))
                fConsoleMode = true;
            else
            if(args[i].toLowerCase().equals("-testloaderclass"))
            {
                String className = args[i + 1];
                i++;
            }

            System.out.println("keepalive " + fKeepAlive);
    }


    protected Class loadTestLoaderClass(String className)
        throws ClassNotFoundException
    {
        return Class.forName(className);
    }

    private void readTestNames(String testNameFile)
        throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(new File(testNameFile)));
        Vector list = new Vector();
        String line;
        while((line = br.readLine()) != null) 
            list.add(line);
        fTestClassNames = (String[])list.toArray(new String[list.size()]);
        br.close();
        if(fDebugMode)
        {
            System.out.println("Tests:");
            for(int i = 0; i < fTestClassNames.length; i++)
                System.out.println("    " + fTestClassNames[i]);

        }
        return;
    }

    private void readFailureNames(String testFailureFile)
        throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(new File(testFailureFile)));
        Vector list = new Vector();
        String line;
        while((line = br.readLine()) != null) 
            list.add(line);
        fFailureNames = (String[])list.toArray(new String[list.size()]);
        br.close();
        br.close();
        if(fDebugMode)
        {
            System.out.println("Failures:");
            for(int i = 0; i < fFailureNames.length; i++)
                System.out.println("    " + fFailureNames[i]);

        }
        return;
    }


    public void runFailed(String message, Exception exception)
    {
        System.err.println(message);
        if(exception != null)
            exception.printStackTrace(System.err);
    }



    private String escapeComma(String s)
    {
        if(s.indexOf(',') < 0 && s.indexOf('\\') < 0)
            return s;
        StringBuffer sb = new StringBuffer(s.length() + 10);
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c == ',')
                sb.append("\\,");
            else
            if(c == '\\')
                sb.append("\\\\");
            else
                sb.append(c);
        }

        return sb.toString();
    }


    private void shutDown()
    {
        if(fWriter != null)
        {
            fWriter.close();
            fWriter = null;
        }
        try
        {
            if(fReaderThread != null)
                fReaderThread.interrupt();
            if(fReader != null)
            {
                fReader.close();
                fReader = null;
            }
        }
        catch(IOException e)
        {
            if(fDebugMode)
                e.printStackTrace();
        }
        try
        {
            if(fClientSocket != null)
            {
                fClientSocket.close();
                fClientSocket = null;
            }
        }
        catch(IOException e)
        {
            if(fDebugMode)
                e.printStackTrace();
        }
    }

    public void sendMessage(String msg)
    {
        if(fWriter == null)
        {
            return;
        } else
        {
            fWriter.println(msg);
            return;
        }
    }


    public void flush()
    {
        fWriter.flush();
    }


    public static final String RERAN_FAILURE = "FAILURE";
    public static final String RERAN_ERROR = "ERROR";
    public static final String RERAN_OK = "OK";
    private String fTestClassNames[];
    private String fTestName;
    private String fVersion;
    private Socket fClientSocket;
    private PrintWriter fWriter;
    private BufferedReader fReader;
    private String fHost;
    private int fPort;
    private boolean fDebugMode;
    private boolean fKeepAlive;
    private boolean fStopped;
    private Vector fRerunRequests;
    private ReaderThread fReaderThread;
    private String fRerunTest;
    private String fFailureNames[];
    private boolean fConsoleMode;



}

