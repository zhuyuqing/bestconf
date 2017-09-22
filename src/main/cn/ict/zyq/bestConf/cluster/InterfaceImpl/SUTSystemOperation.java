package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.ict.zyq.bestConf.cluster.Interface.SystemOperation;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SUTSystemOperation implements SystemOperation {
    private Connection connection;
	private String server;
	private String username;
	private String password;
	public String shellofStart;
	public String cmdStatus; 
	public String checkStop;
	public String shellofStop;
	public String cmdStatusCreate;
	
	private String shellofTerminate;
	private String shellofisStarted;
	private String shellofisClosed;
	private int maxRoundConnection;
	private int sshReconnectWatingtime;
	private int DeducingErrorTime;
	public SUTSystemOperation(){
		
	};
	
	@Override
	public void initial(String server, String username, String password, String shellsPath, int timeout, int maxRoundConnection, int sshReconnectWatingtime) {
		this.server = server;
		this.username = username;
		this.password = password;
		DeducingErrorTime = timeout;
		this.maxRoundConnection = maxRoundConnection;
		this.sshReconnectWatingtime = sshReconnectWatingtime;
		//shellofStart = "cd " + shellsPath + ";./start.sh";
		shellofStart = shellsPath + "/start.sh";
		shellofStop = "cd " + shellsPath + ";./stop.sh";
		shellofisStarted = "cd " + shellsPath + ";./isStart.sh";
		shellofisClosed = "cd " + shellsPath + ";./isClosed.sh";
		shellofTerminate = "cd " + shellsPath + ";./terminateSystem.sh";
		
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
    
	@Override
	public void start() {
		Session session = null;
		try{
			getConnection();
			if(connection==null)
				throw new IOException("Unable to connect the server!");
			session = connection.openSession();
			session.execCommand(shellofStart);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BufferedReader br = null;
		    InputStream stdout;
		    boolean flag = false;
		    System.out.println("System is starting......");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}finally{
			if(session != null)
				session.close();
			closeConnection();
		}
	}
	
	public static void main(String[] args){
		SUTSystemOperation op = new SUTSystemOperation();
	    op.start();
	}
    
	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(30);//a very silly threadpool
	
	@Override
	public void shutdown() {
		fixedThreadPool.shutdownNow();
	}

	

	@Override
	public void stopSystem() {
	    Session session = null;
		try{
			getConnection();
			if(connection == null)
				throw new IOException("Unable to connect the server!");
			session = connection.openSession();
			session.execCommand(shellofStop);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("System is being closed.......");
		}catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }finally{
    	   if(session != null)
    		   session.close();
    	   closeConnection();	
     	}
	}
	@Override
	public boolean isClosed() {
		final AtomicBoolean hasEnded = new AtomicBoolean(false);
		final AtomicBoolean readStatusRetval = new AtomicBoolean(false);
		try {
			getConnection();
			if(connection==null)
				throw new IOException("Unable to connect the server!");
			final Session session = connection.openSession();
			Thread exeThread = new Thread() {
				public void run() {
					try{
						session.execCommand(shellofisClosed);
						System.out.println("Here is remote information:");
						InputStream stdout = new StreamGobbler(session.getStdout());
						BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
						String line = null;
						String targetLine = "ok";
						while(true){
							line = br.readLine();
							if(line != null && line.equals(targetLine)){
								System.err.println(line);
								readStatusRetval.set(true);
								hasEnded.set(true);
								return;
							}
							if(line == null)
								break;
						}
						session.close();
						closeConnection();
					}catch (IOException e) {
						e.printStackTrace();
					}
					readStatusRetval.set(false);
					hasEnded.set(true);
				}
			};
			fixedThreadPool.execute(exeThread);
			long waitingTime = 0;
			while(!hasEnded.get()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitingTime++;
				if(waitingTime>DeducingErrorTime)
					break;
			}
			//we need to break the remote session
			if(session != null)
				session.close();
			if(connection != null)
				closeConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		System.err.println(readStatusRetval.get());
		return readStatusRetval.get();
	}
	public boolean isStarted(){
		InputStream stdout;
		BufferedReader br;
		boolean result = false;
		boolean toContinue = true;
		int round = 0;
		Session session;
		getConnection();
		System.out.print("waiting for system start.");
		if(connection == null)
			try {
				throw new IOException("============Connection is null!");
			} catch (IOException e2) {
				System.exit(-1);
				e2.printStackTrace();
			}
	    while(toContinue){
	    	try {
				Thread.sleep(1000);
				System.out.print(".");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try{
				if(round>=DeducingErrorTime){
					System.err.println("================Max connection has reached");
					return false;
				}
				//toContinue = false;
				
				session = connection.openSession();
	            session.execCommand(shellofisStarted);
	            stdout = new StreamGobbler(session.getStdout());
	            br = new BufferedReader(new InputStreamReader(stdout));
	            boolean flag = false;
	            String line = null;
	            String targetFound = "ok";
	            //String targetNotFound = "ls";
	            //-----------------make sure to thoroughly kill process---------------------------
	            int tot = 0;
	            while(true){
		            line = br.readLine();
		            if(line != null && line.equals(targetFound)){
						System.err.println(line);
						System.err.println("启动成功了！");
						result = true;
						break;
					}
		            if(line == null)
		            	break;
	            }
	            if(session != null)
	            	session.close();
	            if(result) toContinue = false;
	            else round++;
	            	
	         }catch(IOException e){
	              e.printStackTrace();
	              toContinue = true;
	              round++;
	         }finally{
	        	 
	         }   
		}
	    System.out.println();
	    closeConnection();
		return result;
	}

	@Override
	public void terminateSystem() {
		Session session = null;
		try {
			getConnection();
			if (connection == null)
				throw new IOException("Unable to connect the server!");
			session = connection.openSession();
			session.execCommand(shellofTerminate);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("System is being terminated.......");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} finally {
			if (session != null)
				session.close();
			closeConnection();
		}
	}
}
