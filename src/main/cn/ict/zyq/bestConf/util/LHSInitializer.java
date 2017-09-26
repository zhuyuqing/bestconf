/**
 * Copyright (c) 2017 Institute of Computing Technology, Chinese Academy of Sciences, 2017 
 * Institute of Computing Technology, Chinese Academy of Sciences contributors. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package cn.ict.zyq.bestConf.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.ProtectedProperties;

public class LHSInitializer {
	
	private static Random uniRand = new Random(System.nanoTime());
	
	/**
	 * Assumptions:(1)Numberic is continuous and has lower/upper bounds; (2) Nominals have domains permutable
	 * 
	 * @param useMid true if to use the middle point of a subdomain, false if to use a random point within a subdomain
	 */
	public static Instances getMultiDim(ArrayList<Attribute> atts, int sampleSetSize, boolean useMid){
		
		int L = Math.min(7, Math.max(sampleSetSize, atts.size()));//7 is chosen for no special reason
		double maxMinDist = 0, crntMinDist;//work as the threshold to select the sample set
		ArrayList<Integer>[] setWithMaxMinDist=null;
		//generate L sets of sampleSetSize points
		for(int i=0; i<L; i++){
			ArrayList<Integer>[] setPerm = generateOneSampleSet(sampleSetSize, atts.size());
			//compute the minimum distance minDist between any sample pair for each set
			crntMinDist = minDistForSet(setPerm);
			//select the set with the maximum minDist
			if(crntMinDist>maxMinDist){
				setWithMaxMinDist = setPerm;
				maxMinDist = crntMinDist;
			}
		}
		
		//generate and output the set with the maximum minDist as the result
		
		//first, divide the domain of each attribute into sampleSetSize equal subdomain
		double[][] bounds = new double[atts.size()][sampleSetSize+1];//sampleSetSize+1 to include the lower and upper bounds
		Iterator<Attribute> itr = atts.iterator();
		Attribute crntAttr;
		double pace;
		for(int i=0;i<bounds.length;i++){
			crntAttr = itr.next();
			
			if(crntAttr.isNumeric()){
				bounds[i][0] = crntAttr.getLowerNumericBound();
				bounds[i][sampleSetSize] = crntAttr.getUpperNumericBound();
				pace = (crntAttr.getUpperNumericBound() - crntAttr.getLowerNumericBound())/sampleSetSize;
				for(int j=1;j<sampleSetSize;j++){
					bounds[i][j] = bounds[i][j-1] + pace;
				}
			}else{//crntAttr.isNominal()
				if(crntAttr.numValues()>=sampleSetSize){
					//randomly select among the set
					for(int j=0;j<=sampleSetSize;j++)
						bounds[i][j] = uniRand.nextInt(crntAttr.numValues());//the position of one of the nominal values
				}else{
					//first round-robin
					int lastPart = sampleSetSize%crntAttr.numValues();
					for(int j=0;j<sampleSetSize-lastPart;j++)
						bounds[i][j] = j%crntAttr.numValues();
					//then randomly select
					for(int j=sampleSetSize-lastPart;j<=sampleSetSize;j++)
						bounds[i][j] = uniRand.nextInt(crntAttr.numValues());
				}
			}//nominal attribute
		}//get all subdomains
		
		//second, generate the set according to setWithMaxMinDist
		Instances data = new Instances("InitialSetByLHS", atts, sampleSetSize);
		for(int i=0;i<sampleSetSize;i++){
			double[] vals = new double[atts.size()];
			for(int j=0;j<vals.length;j++){
				if(atts.get(j).isNumeric()){
					vals[j] = useMid?
							(bounds[j][setWithMaxMinDist[j].get(i)]+bounds[j][setWithMaxMinDist[j].get(i)+1])/2:
								bounds[j][setWithMaxMinDist[j].get(i)]+
								(
									(bounds[j][setWithMaxMinDist[j].get(i)+1]-bounds[j][setWithMaxMinDist[j].get(i)])*uniRand.nextDouble()
								);
				}else{//isNominal()
					vals[j] = bounds[j][setWithMaxMinDist[j].get(i)];
				}
			}
			data.add(new DenseInstance(1.0, vals));
		}
		
		//third, return the generated points
		return data;
	}
	
	/**
	 * At current version, we assume all attributes are numeric attributes with bounds
	 * 
	 * Let PACE be log10(upper/lower)
	 * 
	 * @param useMid true if to use the middle point of a subdomain, false if to use a random point within a subdomain
	 */
	public static Instances getMultiDimContinuousLog(ArrayList<Attribute> atts, int sampleSetSize, boolean useMid){
		
		int L = Math.min(7, Math.max(sampleSetSize, atts.size()));//7 is chosen for no special reason
		double maxMinDist = 0, crntMinDist;//work as the threshold to select the sample set
		ArrayList<Integer>[] setWithMaxMinDist=null;
		//generate L sets of sampleSetSize points
		for(int i=0; i<L; i++){
			ArrayList<Integer>[] setPerm = generateOneSampleSet(sampleSetSize, atts.size());
			//compute the minimum distance minDist between any sample pair for each set
			crntMinDist = minDistForSet(setPerm);
			//select the set with the maximum minDist
			if(crntMinDist>maxMinDist){
				setWithMaxMinDist = setPerm;
				maxMinDist = crntMinDist;
			}
		}
		
		//generate and output the set with the maximum minDist as the result
		
		//first, divide the domain of each attribute into sampleSetSize equal subdomain
		double[][] bounds = new double[atts.size()][sampleSetSize+1];//sampleSetSize+1 to include the lower and upper bounds
		Iterator<Attribute> itr = atts.iterator();
		Attribute crntAttr;
		int step, crntStep;
		for(int i=0;i<bounds.length;i++){
			crntAttr = itr.next();
			
			bounds[i][0] = crntAttr.getLowerNumericBound();
			bounds[i][sampleSetSize] = crntAttr.getUpperNumericBound();
			crntStep = (int)Math.log10(bounds[i][sampleSetSize] - bounds[i][0]);
			step = sampleSetSize/crntStep;//num of points drawn after the multiplication of 10
			int left = sampleSetSize%crntStep;
			if(bounds[i][0]==0)
				bounds[i][0]=uniRand.nextInt(10);
			crntStep = 1;
			double theBound = bounds[i][sampleSetSize]/10;
			for(int j=1;j<sampleSetSize;j++){
				if(crntStep>=step && bounds[i][j-1]<=theBound)
					crntStep=0;
				
				if(crntStep==0)
					bounds[i][j] = bounds[i][j-step] * 10;
				else if(crntStep<step)
					bounds[i][j] = bounds[i][j-crntStep] * ((double)crntStep*10./((double)step+1.));
				else if(crntStep>=step)
					bounds[i][j] = bounds[i][j-crntStep] * ((double)crntStep*10./(double)(left+step+1));
				
				if(bounds[i][j]>=bounds[i][sampleSetSize])
					System.err.println("be careful!!!!");
				crntStep++;
			}
		}
		
		//second, generate the set according to setWithMaxMinDist
		Instances data = new Instances("InitialSetByLHS", atts, sampleSetSize);
		for(int i=0;i<sampleSetSize;i++){
			double[] vals = new double[atts.size()];
			for(int j=0;j<vals.length;j++){
				vals[j] = useMid?
						(bounds[j][setWithMaxMinDist[j].get(i)]+bounds[j][setWithMaxMinDist[j].get(i)+1])/2:
							bounds[j][setWithMaxMinDist[j].get(i)]+
							(
								(bounds[j][setWithMaxMinDist[j].get(i)+1]-bounds[j][setWithMaxMinDist[j].get(i)])*uniRand.nextDouble()
							);
			}
			data.add(new DenseInstance(1.0, vals));
		}
		
		//third, return the generated points
		return data;
	}
	
	/**
	 * At current version, we assume all attributes are numeric attributes with bounds
	 * 
	 * Let PACE be log10(upper/lower)
	 * 
	 * @param useMid true if to use the middle point of a subdomain, false if to use a random point within a subdomain
	 */
	public static Instances getMultiDimContinuous(ArrayList<Attribute> atts, int sampleSetSize, boolean useMid){
		
		int L = Math.min(7, Math.max(sampleSetSize, atts.size()));//7 is chosen for no special reason
		double maxMinDist = 0, crntMinDist;//work as the threshold to select the sample set
		ArrayList<Integer>[] setWithMaxMinDist=null;
		//generate L sets of sampleSetSize points
		for(int i=0; i<L; i++){
			ArrayList<Integer>[] setPerm = generateOneSampleSet(sampleSetSize, atts.size());
			//compute the minimum distance minDist between any sample pair for each set
			crntMinDist = minDistForSet(setPerm);
			//select the set with the maximum minDist
			if(crntMinDist>maxMinDist){
				setWithMaxMinDist = setPerm;
				maxMinDist = crntMinDist;
			}
		}
		
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
		Instances data = new Instances("InitialSetByLHS", atts, sampleSetSize);
		for(int i=0;i<sampleSetSize;i++){
			double[] vals = new double[atts.size()];
			for(int j=0;j<vals.length;j++){
				vals[j] = useMid?
						(bounds[j][setWithMaxMinDist[j].get(i)]+bounds[j][setWithMaxMinDist[j].get(i)+1])/2:
							bounds[j][setWithMaxMinDist[j].get(i)]+
							(
								(bounds[j][setWithMaxMinDist[j].get(i)+1]-bounds[j][setWithMaxMinDist[j].get(i)])*uniRand.nextDouble()
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
	
	private static final int BigStepPower = 2;
	private static void flexibleBoundsGeneration(double[] bounds, Attribute crntAttr, int sampleSetSize){
		int howGen = 0;//div
		int step, crntStep;
		double pace;
		
		bounds[0] = crntAttr.getLowerNumericBound();
		bounds[sampleSetSize] = crntAttr.getUpperNumericBound();

		pace = (bounds[sampleSetSize] - bounds[0])/sampleSetSize;
		crntStep = bounds[0]>1?(int)Math.log10(bounds[sampleSetSize] / bounds[0]):(int)Math.log10(bounds[sampleSetSize]);
		if(crntStep>0)
			step = sampleSetSize/crntStep;//num of points drawn after the multiplication of 10
		else
			step = 11;//anything larger than 10
		
		if(sampleSetSize<crntStep){
			howGen = 3;
		}else if(0<step && step <10)//each hierarchy has fewer than 10 points
			howGen = 1;
		else if((bounds[0]>1 && (int)Math.log10(pace/bounds[0])> BigStepPower) || 
				(bounds[0]<1 && (int)Math.log10(pace)> BigStepPower) )//a big first step
			howGen = 2;
		else
			howGen = 0;
		
		switch (howGen) {
			case 1://use log
				int left = sampleSetSize%crntStep;//æ?ä¸?½®çä¸ªæ?
				while(bounds[0]==0)
					bounds[0]=uniRand.nextInt(10);
				crntStep = 1;
				double theBound = bounds[sampleSetSize]/10;
				for(int j=1;j<sampleSetSize;j++){
					//stepæ¯æ¯è½®çä¸ªæ°
					if(crntStep>=step && bounds[j-1]<=theBound)
						crntStep=0;
					
					if(crntStep==0)
						bounds[j] = bounds[j-step] * 10;
					else if(crntStep<step)
						bounds[j] = bounds[j-crntStep] * ((double)crntStep*10./((double)step+1.));
					else//(crntStep>=step)
						bounds[j] = bounds[j-crntStep] * ((double)crntStep*10./(double)(left+step+1));
					
					if(bounds[j]>=bounds[sampleSetSize]){
						bounds[j] = bounds[sampleSetSize]-Math.random()*pace;
						System.err.println("============Be careful!!!!=============");
					}
					crntStep++;
				}
				break;
			case 2://first log, then pace
				//for smaller than pace
				int count = 0;
				while(bounds[count]<pace && count<sampleSetSize-1){
					count++;
					bounds[count] = bounds[count-1]*10;
				}
				//for larger than pace
				pace = (bounds[sampleSetSize] - bounds[count])/(sampleSetSize-count);
				for(int j=count;j<sampleSetSize;j++){
					bounds[j] = bounds[j-1] + pace;
				}
				break;
			case 3://randomly choices
				pace = bounds[sampleSetSize] - bounds[0];
				for(int j=1;j<sampleSetSize;j++){
					bounds[j] = bounds[0] + Math.random() * pace;
				}
				break;
			default:
				for(int j=1;j<sampleSetSize;j++){
					bounds[j] = bounds[j-1] + pace;
				}
				break;
		}
	}
	
	/**
	 * At current version, we assume all attributes are numeric attributes with bounds
	 * 
	 * Let PACE be upper-lower DIVided by the sampleSetSize
	 * 
	 * @param useMid true if to use the middle point of a subdomain, false if to use a random point within a subdomain
	 */
	public static Instances getMultiDimContinuousDiv(ArrayList<Attribute> atts, int sampleSetSize, boolean useMid){
		
		int L = Math.min(7, Math.max(sampleSetSize, atts.size()));//7 is chosen for no special reason
		double maxMinDist = 0, crntMinDist;//work as the threshold to select the sample set
		ArrayList<Integer>[] setWithMaxMinDist=null;
		//generate L sets of sampleSetSize points
		for(int i=0; i<L; i++){
			ArrayList<Integer>[] setPerm = generateOneSampleSet(sampleSetSize, atts.size());
			//compute the minimum distance minDist between any sample pair for each set
			crntMinDist = minDistForSet(setPerm);
			//select the set with the maximum minDist
			if(crntMinDist>maxMinDist){
				setWithMaxMinDist = setPerm;
				maxMinDist = crntMinDist;
			}
		}
		
		//generate and output the set with the maximum minDist as the result
		
		//first, divide the domain of each attribute into sampleSetSize equal subdomain
		double[][] bounds = new double[atts.size()][sampleSetSize+1];//sampleSetSize+1 to include the lower and upper bounds
		Iterator<Attribute> itr = atts.iterator();
		Attribute crntAttr;
		double pace;
		for(int i=0;i<bounds.length;i++){
			crntAttr = itr.next();
			
			bounds[i][0] = crntAttr.getLowerNumericBound();
			bounds[i][sampleSetSize] = crntAttr.getUpperNumericBound();
			pace = (bounds[i][sampleSetSize] - bounds[i][0])/sampleSetSize;
			for(int j=1;j<sampleSetSize;j++){
				bounds[i][j] = bounds[i][j-1] + pace;
			}
		}
		
		//second, generate the set according to setWithMaxMinDist
		Instances data = new Instances("InitialSetByLHS", atts, sampleSetSize);
		for(int i=0;i<sampleSetSize;i++){
			double[] vals = new double[atts.size()];
			for(int j=0;j<vals.length;j++){
				vals[j] = useMid?
						(bounds[j][setWithMaxMinDist[j].get(i)]+bounds[j][setWithMaxMinDist[j].get(i)+1])/2:
							bounds[j][setWithMaxMinDist[j].get(i)]+
							(
								(bounds[j][setWithMaxMinDist[j].get(i)+1]-bounds[j][setWithMaxMinDist[j].get(i)])*uniRand.nextDouble()
							);
			}
			data.add(new DenseInstance(1.0, vals));
		}
		
		//third, return the generated points
		return data;
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
		
		/*Properties p1 = new Properties();
		p1.setProperty("range", "[0,1]");
		ProtectedProperties prop1 = new ProtectedProperties(p1);*/
		
		Properties p2 = new Properties();
		p2.setProperty("range", "[321,1E9]");
		ProtectedProperties prop2 = new ProtectedProperties(p2);
		
		ArrayList<String> attVals = new ArrayList<String>();
		for (int i = 0; i < 5; i++)
		      attVals.add("val" + (i+1));
		
		//atts.add(new Attribute("att1", prop1));
		atts.add(new Attribute("att2", prop2));
		//atts.add(new Attribute("att3", attVals));
		//Instances data = LHSInitializer.getMultiDimContinuous(atts, 10, false);
		//Instances data = LHSInitializer.getMultiDim(atts, 10, false);
		Instances data = LHSInitializer.getMultiDimContinuous(atts, 1, false);
		
		System.out.println(data);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static int[] getOneDimInt(int minX, int maxX, int sampleSetSize){
		double[] randArr = new double[sampleSetSize];
		int[] strata = new int[sampleSetSize];
		
		generate(sampleSetSize, randArr, strata);
		
		//pick x according to randArr
		double len = maxX - minX;
		for(int i=0;i<sampleSetSize;i++)
			strata[i] = (int)(randArr[i]*len) + minX;
		
		return strata;
	}
	
	public static double[] getOneDimDouble(double minX, double maxX, int sampleSetSize){
		double[] retVal = new double[sampleSetSize];
		double[] randArr = new double[sampleSetSize];
		int[] strata = new int[sampleSetSize];
		
		generate(sampleSetSize, randArr, strata);
		
		//pick x according to randArr
		double len = maxX - minX;
		for(int i=0;i<sampleSetSize;i++)
			retVal[i] = randArr[i]*len + minX;
		
		return retVal;
	}
	
	/**
	 * generate the CDF
	 */
	private static void generate(int sampleNumber, double[] randArr, int[] strata){
		//get a list of uniform random numbers
		for(int i=0;i<sampleNumber;i++)
			randArr[i] = uniRand.nextDouble();
		
		//Get a sequence of integers, 1,2,3,... ,SampleNumber
		for(int i=0;i<sampleNumber;i++)
			strata[i] = i;
		
		//Re-distribute the random numbers using LHC
		scaleUFunction(randArr, strata, sampleNumber);
	}
	
	/**
	 * the LHS re-scaling function
	 */
	private static void scaleUFunction(double[] u, int[] i, int ss){
		for(int pos = 0;pos<ss;pos++)
			u[pos] = u[pos]*(1/ss) + ((i[pos]-1)/ss);
	}

}
