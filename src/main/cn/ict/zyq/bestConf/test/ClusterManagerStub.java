package cn.ict.zyq.bestConf.test;

import java.util.Map;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import cn.ict.zyq.bestConf.bestConf.ClusterManager;

public class ClusterManagerStub implements ClusterManager {

	@Override
	public void shutdown() {
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>We are shutting down");
	}

	@Override
	public void test(int timeToTest) {
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>We are testing");
	}
	
	@Override
	public Instances collectPerfs(Instances samplePoints, String perfAttName) {
		if(samplePoints.attribute(perfAttName) == null){
			Attribute performance = new Attribute(perfAttName);
			samplePoints.insertAttributeAt(performance, samplePoints.numAttributes());
		}
		return samplePoints;
	}

	private Random rand = new Random();
	@Override
	public Instances runExp(Instances samplePoints, String perfAttName) {
		Instances retVal = null;
		if(samplePoints.attribute(perfAttName) == null){
			Attribute performance = new Attribute(perfAttName);
			samplePoints.insertAttributeAt(performance, samplePoints.numAttributes());
		}
		
		int pos = samplePoints.numInstances();
		for(int i = 0; i < pos; i++){
			Instance ins = samplePoints.get(i);
			ins.setValue(samplePoints.numAttributes()-1, rand.nextDouble()*1000);
		}
		retVal = samplePoints;
		retVal.setClassIndex(retVal.numAttributes()-1);
		
		return retVal;
	}

	@Override
	public double setOptimal(Map<Attribute, Double> attributeToVal) {
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>We are setting Optimal");
		return -1;
	}

}
