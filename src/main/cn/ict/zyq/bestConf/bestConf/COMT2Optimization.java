package cn.ict.zyq.bestConf.bestConf;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import weka.classifiers.evaluation.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import cn.ict.zyq.bestConf.COMT2.COMT2;
import cn.ict.zyq.bestConf.data.DataIOFile;
import cn.ict.zyq.bestConf.util.LHSInitializer;

public class COMT2Optimization implements Optimization{
	
	private BestConf bestconf;
	
	private int InitialSampleSetSize;
	
	private int COMT2Iteration;
	
	public COMT2Optimization(BestConf bestconf, int InitialSampleSetSize, int COMT2Iteration){
		this.bestconf = bestconf;
		this.InitialSampleSetSize = InitialSampleSetSize; 
		this.COMT2Iteration = COMT2Iteration;
	}

	@Override
	public void optimize(String preLoadDatasetPath) {
		Instances samplePoints, trainingSet = null;
		try {
			if(preLoadDatasetPath!=null){
				bestconf.allInstances = DataIOFile.loadDataFromArffFile(preLoadDatasetPath);
				bestconf.allInstances.setClassIndex(bestconf.allInstances.numAttributes()-1);
				trainingSet = new Instances(bestconf.allInstances);
			}else{
				// let's do the sampling
				samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), InitialSampleSetSize, false);
				// traverse the set and initiate the experiments
				samplePoints.add(0, bestconf.defltSettings.firstInstance());
				trainingSet = bestconf.runExp(samplePoints, 0, "pretrain_COMT2", false);
			}
			
			// now we run COMT to find out the best point
			samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), InitialSampleSetSize*COMT2Iteration, false);
			samplePoints.insertAttributeAt(bestconf.allInstances.classAttribute(), samplePoints.numAttributes());
			samplePoints.setClassIndex(samplePoints.numAttributes()-1);
			COMT2 comt = new COMT2(samplePoints, COMT2Iteration);
			
			comt.buildClassifier(trainingSet);
			
			samplePoints = LHSInitializer.getMultiDimContinuous(bestconf.getAttributes(), InitialSampleSetSize, false);
			samplePoints = bestconf.runExp(samplePoints, 0, "eval_COMT2", false);
			Evaluation eval = new Evaluation(trainingSet);
			eval.evaluateModel(comt, samplePoints);
			System.err.println(eval.toSummaryString());
			
			//now we output the training set with the class value updated as the predicted value
			Instances output = new Instances(trainingSet, trainingSet.numInstances()+samplePoints.numInstances());
			Enumeration<Instance> enu = trainingSet.enumerateInstances();
			while(enu.hasMoreElements()){
				Instance ins = enu.nextElement();
				double[] values = ins.toDoubleArray();
				values[values.length-1] = comt.classifyInstance(ins);
				output.add(ins.copy(values));
			}
			enu = samplePoints.enumerateInstances();
			while(enu.hasMoreElements()){
				Instance ins = enu.nextElement();
				double[] values = ins.toDoubleArray();
				values[values.length-1] = comt.classifyInstance(ins);
				output.add(ins.copy(values));
			}
			DataIOFile.saveDataToArffFile("data/trainingBestConf_COMT2_trainingSet+evalSet_Predict.arff", output);
			
			//find out hte best configuration
			Instance best = comt.getInstanceWithPossibleMaxY(samplePoints.firstInstance());
			Instances bestInstances = new Instances(trainingSet,2);
			bestInstances.add(best);
			DataIOFile.saveDataToArffFile("data/bestConfOutput_COMT2.arff", bestInstances);
			
			//set the best configuration to the cluster
			Map<Attribute,Double> attsmap = new HashMap<Attribute,Double>();
			for(int i=0;i<best.numAttributes()-1;i++){
				attsmap.put(best.attribute(i), best.value(i));
			}
			
			double bestPerf = bestconf.setOptimal(attsmap, "COMT2");
			best.setClassValue(bestPerf);
			DataIOFile.saveDataToArffFile("data/bestConfOutput_COMT2_ac.arff", bestInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

}
