package cn.ict.zyq.bestConf.bestConf.sampler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.ProtectedProperties;

public class DDSSampler extends ConfigSampler{
	
	private static Random uniRand = new Random(System.nanoTime());
	
	private int rounds = 1;
	
	public DDSSampler(int totalRounds){
		rounds = totalRounds;
	}
	
	private static boolean inAlready(ArrayList<Integer>[][] sets, ArrayList<Integer>[] permNew){
		boolean noSame = true, notEqual = false;
		for(int i=0;i<sets.length;i++){
			if(sets[i]!=null){//compare the two sample
				notEqual = false;
				//compare from the second attribute
				for(int attR=1;attR<permNew.length;attR++){
					//compare the permutation of this attribute
					for(int p=0;p<permNew[attR].size();p++){
						if(sets[i][attR].get(p)!=permNew[attR].get(p)){
							notEqual = true;
							break;
						}
					}
					if(notEqual)
						break;//no need to compare other attributes
				}
				if(!notEqual){
					noSame=false;
					break;
				}
			}else//no more recursion
				break;
		}
		return !noSame;
	}
	
	private static void positionSwitch(ArrayList<Integer>[][] sets, long[] dists, int pos1, int pos2){
		ArrayList<Integer>[] tempSet = sets[pos1];
		sets[pos1] = sets[pos2];
		sets[pos2] = tempSet;
		
		long tempVal = dists[pos1];
		dists[pos1] = dists[pos2];
		dists[pos2] = tempVal;
	}
	
	long dists[] = null;
	ArrayList<Integer>[][] sets = null;
	private int sampleSetToGet = 0;
	public void setCurrentRound(int crntRound){
		if(sets!=null && crntRound<sets.length)
			sampleSetToGet = crntRound;
		sampleSetToGet = 0;
	}
	
	public void resetRound(){
		sets = null;
		sampleSetToGet = 0;
		dists = null;
		
		File f = new File("data/000SAMPLING_RESET_"+System.currentTimeMillis());
		try {
			System.out.println("creating file "+f.createNewFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * At current version, we assume all attributes are numeric attributes with bounds
	 * 
	 * @param useMid true if to use the middle point of a subdomain, false if to use a random point within a subdomain
	 */
	public Instances sampleMultiDimContinuous(ArrayList<Attribute> atts, int sampleSetSize, boolean useMid){
		
		ArrayList<Integer>[] crntSetPerm;
		//only initialize once
		if(sets==null){
			//possible number of sample sets will not exceed $sampleSetSize to the power of 2
			int L = (int) Math.min(rounds, 
					atts.size()>2?Math.pow(sampleSetSize,atts.size()-1):
						(atts.size()>1?sampleSetSize:1));
			
			//initialization
			dists = new long[L];
			sets = new ArrayList[L][];
			for(int i=0;i<L;i++){
				dists[i] = -1;
				sets[i] = null;
			}
		
			long maxMinDist = -1;
			int posWithMaxMinDist = -1;
			//generate L sets of sampleSetSize points
			for(int i=0; i<L; i++){
				ArrayList<Integer>[] setPerm = generateOneSampleSet(sampleSetSize, atts.size());
				while(inAlready(sets,setPerm))//continue the samples set generation till different samples are obtained
					setPerm = generateOneSampleSet(sampleSetSize, atts.size());
				sets[i] = setPerm;
				
				//compute the minimum distance minDist between any sample pair for each set
				dists[i] = minDistForSet(setPerm);
				//select the set with the maximum minDist
				if(dists[i]>maxMinDist){
					posWithMaxMinDist = i;
					maxMinDist = dists[i];
				}
			}
			//now let the first sample set be the one with the max mindist
			positionSwitch(sets, dists, 0, posWithMaxMinDist);
		}
		crntSetPerm = sets[sampleSetToGet];
		
		//generate and output the set with the maximum minDist as the result
		
		//first, divide the domain of each attribute into sampleSetSize equal subdomain
		double[][] bounds = new double[atts.size()][sampleSetSize+1];//sampleSetSize+1 to include the lower and upper bounds
		Iterator<Attribute> itr = atts.iterator();
		Attribute crntAttr;
		boolean[] roundToInt = new boolean[atts.size()];
		for(int i=0;i<bounds.length;i++){
			crntAttr = itr.next();
			uniBoundsGeneration(bounds[i], crntAttr, sampleSetSize);
			//flexibleBoundsGeneration(bounds[i], crntAttr, sampleSetSize);
			
			if(bounds[i][sampleSetSize]-bounds[i][0]>sampleSetSize)
				roundToInt[i]=true;
		}
		
		//second, generate the set according to setWithMaxMinDist
		Instances data = new Instances("SamplesByLHS", atts, sampleSetSize);
		for(int i=0;i<sampleSetSize;i++){
			double[] vals = new double[atts.size()];
			for(int j=0;j<vals.length;j++){
				vals[j] = useMid?
						(bounds[j][crntSetPerm[j].get(i)]+bounds[j][crntSetPerm[j].get(i)+1])/2:
							bounds[j][crntSetPerm[j].get(i)]+
							(
								(bounds[j][crntSetPerm[j].get(i)+1]-bounds[j][crntSetPerm[j].get(i)])*uniRand.nextDouble()
							);
				if(roundToInt[j])
					vals[j] = (int)vals[j];
			}
			data.add(new DenseInstance(1.0, vals));
		}
		
		//third, return the generated points
		return data;
	}
	
	private static void uniBoundsGeneration(double[] bounds, Attribute crntAttr, int sampleSetSize){
		bounds[0] = crntAttr.getLowerNumericBound();
		bounds[sampleSetSize] = crntAttr.getUpperNumericBound();
		double pace = (bounds[sampleSetSize] - bounds[0])/sampleSetSize;
		for(int j=1;j<sampleSetSize;j++){
			bounds[j] = bounds[j-1] + pace;
		}
	}
	
	/**
	 * generate one sample set based on the requirement of LHS sampling method
	 * @return	the generated sample set that specifies which subdomain to choose under each attributed for each sample
	 * 			each arraylist is a permutation of the subdomains for each attribute
	 */
	private static ArrayList<Integer>[] generateOneSampleSet(int sampleSetSize, int attrNum){
		ArrayList<Integer>[] setPerm = new ArrayList[attrNum];//sampleSetSize samples; each with atts.size() attributes
		int crntRand;
		//generate atts.size() permutations of sampleSetSize integers
		//		start from the second attribute, the first attribute always uses the natural order
		for(int i=1;i<attrNum;i++){
			setPerm[i] = new ArrayList<Integer>(sampleSetSize);
			
			//randomly generate a permutation for sampleSetSize integers
			for(int j=0;j<sampleSetSize;j++){
				crntRand = uniRand.nextInt(sampleSetSize);
				
				//for each set, each subdomain of any parameter has one and only one sample in it
				while(setPerm[i].contains(crntRand)){
					crntRand = uniRand.nextInt(sampleSetSize);
				}
				setPerm[i].add(crntRand);
			}
		}
		//the first attribute always uses the natural order
		setPerm[0] = new ArrayList<Integer>(sampleSetSize);
		for(int j=0;j<sampleSetSize;j++){
			setPerm[0].add(j);
			
		}
		return setPerm;
	}
	
	/**
	 * compute the minimum distance between any sample pair in the set of setPerm
	 */
	private static long minDistForSet(ArrayList<Integer>[] setPerm){
		long mindist = Long.MAX_VALUE, dist;
		int sampleSetSize = setPerm[0].size();
		int[] sampleA = new int[setPerm.length], sampleB = new int[setPerm.length];
		for(int i=0;i<sampleSetSize-1;i++){
			for(int j=0;j<sampleA.length;j++)
				sampleA[j] = setPerm[j].get(i);
			//enumerate all combinations
			for(int k=i+1;k<sampleSetSize;k++){
				for(int j=0;j<sampleB.length;j++)
					sampleB[j] = setPerm[j].get(k);
				
				dist = eucDistForPairs(sampleA, sampleB);
				mindist = mindist>dist?dist:mindist;
			}
		}
		
		return mindist;
	}
	
	/**
	 * compute the Euclidean distance between two points in a multi-dim integer space
	 */
	private static long eucDistForPairs(int[] sampleA, int[] sampleB){
		long dist = 0;
		for(int i=0;i<sampleA.length;i++)
			dist += (sampleA[i]-sampleB[i])*(sampleA[i]-sampleB[i]);
		return dist;
	}
	
	public static void main(String[] args){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		
		Properties p1 = new Properties();
		p1.setProperty("range", "[0,1]");
		ProtectedProperties prop1 = new ProtectedProperties(p1);
		
		Properties p2 = new Properties();
		p2.setProperty("range", "[321,1E9]");
		ProtectedProperties prop2 = new ProtectedProperties(p2);
		
		Properties p3 = new Properties();
		p3.setProperty("range", "[1,30]");
		ProtectedProperties prop3 = new ProtectedProperties(p3);
		
		ArrayList<String> attVals = new ArrayList<String>();
		for (int i = 0; i < 5; i++)
		      attVals.add("val" + (i+1));
		
		atts.add(new Attribute("att1", prop1));
		atts.add(new Attribute("att2", prop2));
		atts.add(new Attribute("att3", prop3));
		//atts.add(new Attribute("att4", attVals));
		//Instances data = LHSInitializer.getMultiDimContinuous(atts, 10, false);
		//Instances data = LHSInitializer.getMultiDim(atts, 10, false);
		DDSSampler sampler = new DDSSampler(3);
		
		sampler.setCurrentRound(0);
		Instances data = sampler.sampleMultiDimContinuous(atts, 2, false);
		System.out.println(data);
		
		sampler.setCurrentRound(01);
		data = sampler.sampleMultiDimContinuous(atts, 2, false);
		System.out.println(data);
		
		sampler.setCurrentRound(2);
		data = sampler.sampleMultiDimContinuous(atts, 2, false);
		System.out.println(data);
	}
	
}
