package cn.ict.zyq.bestConf.cluster.InterfaceImpl;

import cn.ict.zyq.bestConf.cluster.Interface.Performance;

public class SUTPerformance implements Performance {

	private double throughPut;
	private double latency;
	
	public SUTPerformance(){
		
	}
	@Override
	public void initial(double throughPut, double latency) {
		this.throughPut = throughPut;
		this.latency = latency;
	}
	
	@Override
	public double getPerformanceOfThroughput() {
		return throughPut;
	}
	@Override
	public double getPerformanceOfLatency() {
		return latency;
	}
}
