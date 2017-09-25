package cn.ict.zyq.bestConf.cluster.Interface;

public interface Test {
	void initial(String server, String username, String password, String targetTestPath, int maxRoundConnection, int sshReconnectWatingtime);
	void startTest();
	boolean isFinished();
	void terminateTest();
	double getResultofTest(int num, boolean isInterrupt);
	
}
