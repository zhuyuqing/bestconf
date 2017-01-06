package cn.ict.zyq.bestConf.bestConf;

import java.util.Map;

import weka.core.Attribute;
import weka.core.Instances;

public interface ClusterManager {
	public void shutdown();
	public void test(int timeToTest);
	
	//only run those instance without the performance attribute
	public Instances runExp(Instances samplePoints, String perfAttName);
	public double setOptimal(Map<Attribute,Double> attributeToVal);
	
	/**collect the performances for part of samplePoints*/
	//fill the performance attribute of samplePoints
	public Instances collectPerfs(Instances samplePoints, String perfAttName);
}
