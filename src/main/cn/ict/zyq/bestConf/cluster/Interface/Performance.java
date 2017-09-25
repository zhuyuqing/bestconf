package cn.ict.zyq.bestConf.cluster.Interface;

public interface Performance {
	void initial(double throughPut, double latency);
	double getPerformanceOfThroughput();
	double getPerformanceOfLatency();
}
