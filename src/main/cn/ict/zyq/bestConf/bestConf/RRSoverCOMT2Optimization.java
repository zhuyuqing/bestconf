package cn.ict.zyq.bestConf.bestConf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import cn.ict.zyq.bestConf.COMT2.COMT2;
import cn.ict.zyq.bestConf.data.DataIOFile;
import cn.ict.zyq.bestConf.util.LHSInitializer;

public class RRSoverCOMT2Optimization implements Optimization {

	private BestConf bestconf;
	
	private int InitialSampleSetSize;
	
	private int RRSMaxRounds;
	
	private int COMT2Iteration;
	
	public RRSoverCOMT2Optimization(BestConf bestconf, int InitialSampleSetSize, int RRSMaxRounds, int COMT2Iteration){
		this.bestconf = bestconf;
		this.InitialSampleSetSize = InitialSampleSetSize; 
		this.COMT2Iteration = COMT2Iteration;
		this.RRSMaxRounds = RRSMaxRounds;
	}
	
	@Override
	public void optimize(String preLoadDatasetPath) {
		Instances samplePoints = null;
		COMT2 comt = null;
		try {
			if(preLoadDatasetPath!=null){
				bestconf.allInstances = DataIOFile.loadDataFromArffFile(preLoadDatasetPath);
				bestconf.allInstances.setClassIndex(bestconf.allInstances.numAttributes()-1);
			}else{
				// let's do the sampling
				samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), InitialSampleSetSize, false);
				// traverse the set and initiate the experiments
				samplePoints.add(0, bestconf.defltSettings.firstInstance());
				bestconf.runExp(samplePoints, 0, "train_RRSoverCOMT2", false);
			}
			
			// let's do the sampling
			samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), bestconf.allInstances.size()*COMT2Iteration, false);
			samplePoints.insertAttributeAt(bestconf.allInstances.classAttribute(), samplePoints.numAttributes());
			samplePoints.setClassIndex(samplePoints.numAttributes()-1);
			comt = new COMT2(samplePoints, COMT2Iteration);
			
			comt.buildClassifier(bestconf.allInstances);
			
			Instance best = comt.getInstanceWithPossibleMaxY(samplePoints.firstInstance());
			Instances bestInstances = new Instances(bestconf.allInstances,1);
			bestInstances.add(best);
			DataIOFile.saveDataToArffFile("data/bestConfOutput_RRSoverCOMT2.arff", bestInstances);
			
			//set the best configuration to the cluster
			Map<Attribute,Double> attsmap = new HashMap<Attribute,Double>();
			for(int i=0;i<best.numAttributes()-1;i++){
				attsmap.put(best.attribute(i), best.value(i));
			}
			
			double bestPerf = bestconf.setOptimal(attsmap, "COMT2");
			bestInstances.firstInstance().setClassValue(bestPerf);
			DataIOFile.saveDataToArffFile("data/bestConfOutput_RRSoverCOMT2_ac.arff", bestInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//now we use RRS
		ArrayList<Attribute> props = bestconf.getAttributes();
		double currentBest = -Double.MAX_VALUE, tempBest;
		Instance currentIns =null;
		int currentround = 0, subround = 0;
		samplePoints = null;
		while(true){
			//is it a global search
			if(samplePoints == null){
				props = bestconf.getAttributes();
			}
			
			//let's do the sampling
			samplePoints = LHSInitializer.getMultiDimContinuous(props, InitialSampleSetSize, false);
			samplePoints.insertAttributeAt(bestconf.allInstances.classAttribute(), samplePoints.numAttributes());
			samplePoints.setClassIndex(samplePoints.numAttributes()-1);
			
			try {
				samplePoints = emulateWithModel(samplePoints, comt);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			//get the point with the best performance
			Instance tempIns = BestConf.findBestPerf(samplePoints);
			tempBest = tempIns.value(samplePoints.numAttributes()-1);
			if(tempBest>currentBest){
				System.err.println("Previous best is "+currentBest+"; Current best is "+tempBest);
				
				currentBest = tempBest;
				currentIns = tempIns;
				
				try {
					Instances bestInstances = new Instances(samplePoints,1);
					bestInstances.add(currentIns);
					DataIOFile.saveDataToArffFile("data/emulatingBestRRSoverCOMT2"+currentround+"_"+subround+"_"+currentBest+".arff", bestInstances);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//let's search locally
				props = BestConf.scaleDownDetour(samplePoints, tempIns);
				subround++;
			}else{//let's do the restart
				samplePoints = null;
				currentround++; subround = 0;
				System.err.println("Entering into round "+currentround);
				if(currentround>=RRSMaxRounds)
					break;
			}
		}
		
		try {
			Instances bestInstances = new Instances(bestconf.allInstances,1);
			bestInstances.add(currentIns);
			DataIOFile.saveDataToArffFile("data/bestConfOutput_RRSoverCOMT2"+currentround+"_"+subround+"_"+currentBest+".arff", bestInstances);
			
			//set the best configuration to the cluster
			Map<Attribute,Double> attsmap = BestConf.instanceToMap(currentIns);
			double bestPerf = bestconf.setOptimal(attsmap, "COMT2");
			bestInstances.firstInstance().setClassValue(bestPerf);
			DataIOFile.saveDataToArffFile("data/bestConfOutput_RRSoverCOMT2"+currentround+"_"+subround+"_"+currentBest+"_ac.arff", bestInstances);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static Instances emulateWithModel(Instances trainingSet, Classifier model) throws Exception{
		Enumeration<Instance> enu = trainingSet.enumerateInstances();
		while(enu.hasMoreElements()){
			Instance ins = enu.nextElement();
			double y = model.classifyInstance(ins);
			ins.setClassValue(y);
		}
		return trainingSet;
	}

}
