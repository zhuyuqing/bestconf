package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ict.zyq.bestConf.cluster.Interface.Test;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SUTTest implements Test {

	private Connection connection;
	private String username;
	private String password;
	private String shellofstartTest;
	private String shellofterminateTest;
	private String shellofgetTestResult;
	private String shellofisFinished;
	private String server;
	private static final String filePath = "data";
	private long defaultTesttime;
	private boolean isFirsttest = true;
	private long TestTimeMaxInMilli;
	private int maxRoundConnection;
	private int sshReconnectWatingtime;
	private String defaultTestfile = "data/defaultTestTime";
	private String defaultTestfilepath = defaultTestfile + "/defaultTesttime.txt";
	
	private static final String resultFolder = filePath +"/TestResults";
	
	public SUTTest(){
	}
    public SUTTest(long testDurationTimeOutInSec){
    	this.TestTimeMaxInMilli = testDurationTimeOutInSec * 1000;
    }
	@Override
	public void initial(String server, String username, String password, String targetTestpath, int maxRoundConnection, int sshReconnectWatingtime) {
		this.server = server;
		this.username = username;
		this.password = password;
		this.maxRoundConnection = maxRoundConnection;
		this.sshReconnectWatingtime = sshReconnectWatingtime;
		
		shellofstartTest = "cd " + targetTestpath + ";./startTest.sh";
		shellofterminateTest = "cd " + targetTestpath + ";./terminateTest.sh";
		shellofgetTestResult = "cd " + targetTestpath + ";./getTestResult.sh";
		shellofisFinished = "cd " + targetTestpath + ";./isFinished.sh";
		
		File file = new File(resultFolder);
		if(!file.exists())
			file.mkdirs();
	}
	
	public Connection getConnection(){
		int round = 0;
    	while(round<maxRoundConnection){
    		try{
    			connection = new Connection(server);
    			connection.connect();
    			boolean isAuthenticated = connection.authenticateWithPassword(username, password);
    			if (isAuthenticated == false) {
    				throw new IOException("Authentication failed...");
    			}
    			break;
    		}catch (Exception e) {
    			e.printStackTrace();
    			connection.close();
    			connection = null;
    			System.err.println("================= connection is null in round "+round);
    			try {
					Thread.sleep(sshReconnectWatingtime*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
    		}
    		round++;
    	}
		return connection;
	}
    
    public void closeConnection() {
    	try {
			if (connection!=null && connection.connect() != null) {
				connection.close();
			}
		} catch (IOException e) {
			/*e.printStackTrace();*/
		} finally {
			if(connection != null)
				connection.close();
		}
	}
    
    public static void test() throws IOException{
    	SUTTest test = new SUTTest();
    	//test.initial("172.16.48.41", "root", "ljx123", "/opt/HiBench-master");
    	//test.startTest();
    	//System.out.println("runtime is : " + test.getRuntime("/opt/HiBench-master/report"));
        test.writeDefaultTesttimeTofile("data/defaultTestTime/defaultTesttime.txt", 34343);
    }
    
    public static void main(String[] args){
    	//SUTTest.test();
		SUTTest test = new SUTTest();
		test.initial("172.16.48.41", "root", "ljx123", "/opt/HiBench-master", 1,1);
		//test.getResultofTest(2, true);
        //test.writeDefaultTesttimeTofile("data/defaultTestTime/defaultTesttime.txt", 34343);

    }
 
	@Override
	public void startTest(){
		Session session=null;
		try {
			getConnection();
			if(connection==null)
				throw new IOException("Unable to connect the server!");
		    session = connection.openSession();
			session.execCommand(shellofstartTest);
			System.out.println("Here is some information about the remote host:");
			InputStream stderr = new StreamGobbler(session.getStderr());
			BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
			InputStream stdout = new StreamGobbler(session.getStdout());
			BufferedReader stdbr = new BufferedReader(new InputStreamReader(stdout));
//			String line;
//			while((line = stdbr.readLine()) != null){
//				System.out.println(line);
//			}
			System.out.println("²âÊÔÒÑ¾­Æô¶¯£¡");
		    /*if(session != null)
		    	session.close();
			closeConnection();*/
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
	    	   if(session != null)
	    		   session.close();
	    	   closeConnection();	
	     	}
	}

	public boolean isFinished(){
		BufferedReader br = null;
	    InputStream stdout;
	    boolean flag = false;
	    Session session = null;
		try{
			getConnection();
			try {
				if(connection==null)
					throw new IOException("Unable to connect the server!");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			session = connection.openSession();
			session.execCommand(shellofisFinished);
			stdout = new StreamGobbler(session.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout));
	        String line = null;
	       
	        while ((line = br.readLine()) != null){
	        	if(line.equals("ok")){
	        		flag = true;
	        		break;
	        	}
			}
		}catch(IOException e){
            e.printStackTrace();
        }finally{
    	   if(session != null)
    		   session.close();
    	   closeConnection();	
     	}
	    return flag;
	}

	@Override
	public void terminateTest() {
		BufferedReader br = null;
	    InputStream stdout;
	    Session session = null;
		try{
			getConnection();
			try {
				if(connection==null)
					throw new IOException("Unable to connect the server!");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			session = connection.openSession();
			session.execCommand(shellofterminateTest);
			stdout = new StreamGobbler(session.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout));
	        String line = null;
	       
	        while ((line = br.readLine()) != null){
	        	if(line.equals("ok")){
	        		break;
	        	}
			}
		}catch(IOException e){
            e.printStackTrace();
        }finally{
    	   if(session != null)
    		   session.close();
    	   closeConnection();	
     	}
	}

	@Override
	public double getResultofTest(int num, boolean isInterrupt) {
		
	    InputStream stdout;
	    boolean flag = false;
		Session session = null;
		double performance = -1;//a very important initialization
    	long waitingTime;
		long begin = 0,end;
    	if(num == 0 && isFirsttest){
    		waitingTime = System.currentTimeMillis()+TestTimeMaxInMilli;
    		begin = System.currentTimeMillis();
    	}else if(isInterrupt){
		         defaultTesttime = getDefaultTesttimeFromfile(defaultTestfilepath);
		         isFirsttest = false;
		         waitingTime = System.currentTimeMillis() + (long)(defaultTesttime * 1.9);
	          }else	
    		     waitingTime = System.currentTimeMillis() + (long)(defaultTesttime * 1.9);
    	
    	boolean testEnd = false;
    	System.out.print("waiting.");
    	while (System.currentTimeMillis()<waitingTime) {
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
    		System.out.print(".");
			if(isFinished()){
				try {
					getConnection();
					try {
						if(connection==null)
							throw new IOException("Unable to connect the server!");
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					testEnd = true;
					session = connection.openSession();
					session.execCommand(shellofgetTestResult);
					stdout = new StreamGobbler(session.getStdout());
					BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
					String lineofres = null;
					while(true){
						lineofres = br.readLine();
						if(lineofres != null && !lineofres.equals("error") && !lineofres.equals("not exist")){
							performance = Double.parseDouble(lineofres.trim());
							break;
						}
						if(lineofres == null)
							break;
					}
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally{
			    	   if(session != null)
			    		   session.close();
			    	   closeConnection();	
			     }
			}
		}
    	System.out.println();
    	if(!testEnd){ 
    		terminateTest();
    		performance = -1;
    	}

    	if(num == 0 && isFirsttest){
    		end = System.currentTimeMillis();
    		defaultTesttime = end - begin;
    		isFirsttest = false;
    		writeDefaultTesttimeTofile(defaultTestfilepath, defaultTesttime);
    		System.out.println("defaultTesttime is : " + defaultTesttime);
    	}
		return performance;
	}
	private void writeDefaultTesttimeTofile(String filepath, long defaulttime){
    	File file = new File(filepath);
    	
    	BufferedWriter writer;
			try {
				if(!file.exists()){
					file.getParentFile().mkdir();
					file.createNewFile();
				}
				writer = new BufferedWriter(new FileWriter(file));
				writer.write(defaulttime+"\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	private long getDefaultTesttimeFromfile(String filepath){
		long result = -1;
		File res = new File(filepath);
		
		try {
			if(!res.exists()){
				res.getParentFile().mkdir();
				res.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader(res));
			String readline = null;
			while ((readline = reader.readLine()) != null) {
				result = Long.parseLong(readline);
				break;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			result = (long)((double)TestTimeMaxInMilli/1.9);
		}
		return result;
	}
}
