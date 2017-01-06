package cn.ict.zyq.bestConf.bestConf;

import weka.core.Instance;
import weka.core.Instances;
import cn.ict.zyq.bestConf.COMT2.COMT2;
import cn.ict.zyq.bestConf.data.DataIOFile;
import cn.ict.zyq.bestConf.util.LHSInitializer;

public class COMT2MultiOptimization implements Optimization {

	private BestConf bestconf;
	
	private int InitialSampleSetSize;
	
	private int COMT2MultiIteration;
	private int COMT2Iteration;
	
	public COMT2MultiOptimization(BestConf bestconf, int InitialSampleSetSize, int COMT2Iteration, int COMT2MultiIteration){
		this.bestconf = bestconf;
		this.InitialSampleSetSize = InitialSampleSetSize; 
		this.COMT2Iteration = COMT2Iteration;
		this.COMT2MultiIteration = COMT2MultiIteration;
	}
	
	@Override
	public void optimize(String preLoadDatasetPath) {
		Instances samplePoints;
		int round = 0;
		Instance prevBest = null;
		try {
			if(preLoadDatasetPath!=null){
				bestconf.allInstances = DataIOFile.loadDataFromArffFile(preLoadDatasetPath);
				bestconf.allInstances.setClassIndex(bestconf.allInstances.numAttributes()-1);
			}else{
				// let's do the sampling
				samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), InitialSampleSetSize, false);
				// traverse the set and initiate the experiments
				samplePoints.add(0, bestconf.defltSettings.firstInstance());
				bestconf.runExp(samplePoints, 0, "pretrain_COMT2Multi", false);
			}
			
			while(round<COMT2MultiIteration){
				// now we run COMT to find out the best point
				samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), InitialSampleSetSize*COMT2Iteration, false);
				samplePoints.insertAttributeAt(bestconf.allInstances.classAttribute(), samplePoints.numAttributes());
				samplePoints.setClassIndex(samplePoints.numAttributes()-1);
				COMT2 comt = new COMT2(samplePoints, COMT2Iteration);
				comt.buildClassifier(bestconf.allInstances);
				
				Instance best = comt.getInstanceWithPossibleMaxY(samplePoints.firstInstance());
				Instances bestInstances = new Instances(bestconf.allInstances,1);
				bestInstances.add(best);
				DataIOFile.saveDataToArffFile("data/bestConfOutput_COMT2Multi_"+round+"_.arff", bestInstances);
				
				if(best.equals(prevBest))
					break;//the best instance wins in two rounds--must be the final winner
				
				prevBest = best;
				bestconf.runExp(bestInstances, round, "train_COMT2Multi", false);
				round++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
