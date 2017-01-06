package cn.ict.zyq.bestConf.bestConf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import cn.ict.zyq.bestConf.data.DataIOFile;
import cn.ict.zyq.bestConf.util.LHSInitializer;
import cn.ict.zyq.bestConf.util.TxtFileOperation;

public class RRSPoolOptimization implements Optimization {

	private BestConf bestconf;
	
	private int InitialSampleSetSize;
	
	private int RRSMaxRounds;
	
	public RRSPoolOptimization(BestConf bestconf, int InitialSampleSetSize, int RRSMaxRounds){
		this.bestconf = bestconf;
		this.InitialSampleSetSize = InitialSampleSetSize; 
		this.RRSMaxRounds = RRSMaxRounds;
	}

	@Override
	public void optimize(String preLoadDatasetPath) {
		double currentBest = -Double.MAX_VALUE, tempBest;
		Instance currentIns =null;
		int currentround = 0, subround = 0;
		Instances samplePoints = null, trainingSet = null, poolOfPoints = null;
		ArrayList<Attribute> props = bestconf.getAttributes();
		
		poolOfPoints = LHSInitializer.getMultiDimContinuous(props, InitialSampleSetSize * (RRSMaxRounds + 1), false);
		while(true){
			//is it a global search
			if(samplePoints == null){
				props = bestconf.getAttributes();
			}
			
			//the first time
			if(currentround==0 && subround==0){
				if(preLoadDatasetPath==null){
					//let's do the sampling
					samplePoints = extractFromSet(poolOfPoints, InitialSampleSetSize);
					samplePoints.add(0, bestconf.defltSettings.firstInstance());
					//traverse the set and initiate the experiments
					trainingSet = bestconf.runExp(samplePoints, currentround, "RRS"+String.valueOf(subround), false);
				}else{
					try {
						bestconf.allInstances = DataIOFile.loadDataFromArffFile(preLoadDatasetPath);
						bestconf.allInstances.setClassIndex(bestconf.allInstances.numAttributes()-1);
						samplePoints = trainingSet = new Instances(bestconf.allInstances);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{//(currentround!=0 || subround!=0)
				//let's do the sampling
				samplePoints = samplePoints==null?
									extractFromSet(poolOfPoints, InitialSampleSetSize)://a global search
												LHSInitializer.getMultiDimContinuous(props, InitialSampleSetSize, false);
				
				//traverse the set and initiate the experiments
				trainingSet = bestconf.runExp(samplePoints, currentround, "RRS"+String.valueOf(subround), false);
			}
			
			//get the point with the best performance
			Instance tempIns = BestConf.findBestPerf(trainingSet);
			tempBest = tempIns.value(trainingSet.numAttributes()-1);
			if(tempBest>currentBest){
				System.err.println("Previous best is "+currentBest+"; Current best is "+tempBest);
				
				currentBest = tempBest;
				currentIns = tempIns;
				
				try {
					Instances bestInstances = new Instances(samplePoints,1);
					bestInstances.add(currentIns);
					DataIOFile.saveDataToArffFile("data/trainingBestConf_RRS_"+currentround+"_"+subround+"_"+currentBest+".arff", bestInstances);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//let's search locally
				props = BestConf.scaleDownDetour(trainingSet, tempIns);
				subround++;
			}else{//let's do the restart
				samplePoints = null;
				
				currentround++; subround = 0;
				
				System.err.println("Entering into round "+currentround);
				
				if(currentround>=RRSMaxRounds)
					break;
			}
		}//RRS search
		
		//output the best
		Map<Attribute,Double> attsmap = BestConf.instanceToMap(currentIns);
		System.out.println(attsmap.toString());
		
		//set the best configuration to the cluster
		System.err.println("The best performance is : "+currentBest);
		
		System.out.println("=========================================");
		TxtFileOperation.writeToFile("bestConfOutput_RRS",attsmap.toString()+"\n");
		
		System.out.println("=========================================");
		
		//output the whole trainings dataset
		try {
			DataIOFile.saveDataToArffFile("data/trainingAllRSS.arff", bestconf.allInstances);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Random rand = new Random();
	private Instances extractFromSet(Instances poolOfPoints, int num){
		Instances retval = new Instances(poolOfPoints, num);
		for(int i=0;i<num;i++)
			retval.add(poolOfPoints.remove(rand.nextInt(poolOfPoints.numInstances())));
		return retval;
	}
}
