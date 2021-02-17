package sa_nonlinear;

import roulettewheel.AliasMethod;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SplittableRandom;
import java.io.File;


public class SAGeneratorNonLinear {
	// parameters
	int numNodesFinal;
	// degree-based PA
	String dType; // "power-law"
	public double dExp;
	// time decay
	String tType; // "power-law"; "log-normal"; "exp"
	double tExp;
	// fitness
	String fType; // "poisson"; "power-law"; "exp"
	double fExp;
	int fMax;
	// preference of virtual nodes
	int initAttract;
	// data structures
	int[] deg;
	int[] fit;
	// does not store t[] explicitly, since t[v]=v
	AliasMethod fitAlias; 	
//	HashMap<Integer, ArrayList<Integer>> adjs;	
	// time 
	int timeStamp = 0;
	// io
	PrintWriter writeEdge;
	// data structure
	ArrayList<TBucketSANonLinear> tBucketList;
	double sumTBucketWeight;
	SplittableRandom random;
	
	// record generation time
	long totalTime = 0;
	long ioTime = 0;
	long genTime = 0;
	long initTime = 0;
	public SAGeneratorNonLinear(int numNodesFinal, String dType, double dExp, String tType, double tExp, String fType, double fExp, int fMax, int initAttract)
	{
		long t_init_0 = System.nanoTime();
		
		this.numNodesFinal = numNodesFinal;
		this.dType = dType;
		this.dExp = dExp;
		this.tType = tType; 
		this.tExp = tExp;
		this.fType = fType;
		this.fExp = fExp;
		// this.fMax = fMax;
		this.initAttract = initAttract;
		
		deg = new int[this.numNodesFinal];
//		adjs = new HashMap<Integer, ArrayList<Integer>>();
		for(int i = 0; i < this.numNodesFinal; i++)
		{
			deg[i] = 0;
//			adjs.put(i, new ArrayList<Integer>());
		}
		this.fMax = (int)(Math.pow(fMax, 1.0 / this.dExp)) + 1;
		double[] p = new double[this.fMax];
		if(fType.equals("poisson"))
		{
			for(int i = 0; i < p.length; i++)
				p[i] = Math.pow(Math.pow(fExp, i+1) * Math.pow(Math.E, -fExp) / factorial(i+1), 1.0 / this.dExp);             
			fitAlias = new AliasMethod(p);
		}
		else if(fType.equals("power-law"))
		{
			for(int i = 0; i < p.length; i++)
				p[i] = Math.pow(1.0 / Math.pow(i+1, fExp), 1.0 / this.dExp);             
			fitAlias = new AliasMethod(p);
		}
		else if(fType.equals("exp"))
		{
			for(int i = 0; i < p.length; i++)
				p[i] = Math.pow(1.0 / Math.pow(Math.E, fExp * (i+1)), 1.0 / this.dExp);              
			fitAlias = new AliasMethod(p);
		}
		else
		{
			System.err.println("unknown fType");
			for(int i = 0; i < p.length; i++)
				p[i] = 0;
			p[0] = 1;
			fitAlias = new AliasMethod(p);
		}
		fit = new int[this.numNodesFinal];
		for(int i = 0; i < fit.length; i++)
			fit[i] = fitAlias.sampleIndex() + 1;
			
		timeStamp = 0;

		initTime = System.nanoTime() - t_init_0;
	}
	public void createGraph(String edgeFile) throws IOException
	{
		tBucketList = new ArrayList<TBucketSANonLinear>(2 * (int)(Math.log(this.numNodesFinal) / Math.log(2) + 1)); // upper bound
		sumTBucketWeight = 0;
		
		writeEdge = new PrintWriter(new OutputStreamWriter(new FileOutputStream(edgeFile)));
		random = new SplittableRandom(System.nanoTime());
		
		TBucketSANonLinear tBucket0 = new TBucketSANonLinear(0, this);
		tBucketList.add(0, tBucket0);
		TBucketSANonLinear tBucket1 = new TBucketSANonLinear(1, this);
		tBucketList.add(0, tBucket1);
		//adj.get(0).add(1);
		//adj.get(1).add(0);
		tBucket0.addNodeDegreeByOne(0);
		tBucket1.addNodeDegreeByOne(1);
		timeStamp = 1;
		
		//calculate genTime
		long t_gen_0 = System.nanoTime();
		int tempCurNumOfNodes = 0;
		for(int i = 2; i < numNodesFinal; i++)
		{
			addEdges(i);
			// print current progress
			if(i % (numNodesFinal / 100) == 0 && i != tempCurNumOfNodes){
				System.out.println(i);	
				tempCurNumOfNodes = i;
			}
			mergeTBuckets();
			timeStamp ++;
		}
		//calculate genTime
		genTime += System.nanoTime() - t_gen_0;
		
		writeEdge.flush();
		writeEdge.close();
		
		graphInfo();
	}
	
	private void mergeTBuckets()
	{
		for(int index = 0; index < tBucketList.size() - 2; index ++)
		{
			TBucketSANonLinear currentBucket = tBucketList.get(index);
			TBucketSANonLinear nextBucket = tBucketList.get(index + 1);
			TBucketSANonLinear nextNextBucket = tBucketList.get(index + 2);
			if(currentBucket.size() == nextBucket.size() && nextBucket.size() == nextNextBucket.size())
			{
				nextNextBucket.mergeTBucket(nextBucket);
				tBucketList.remove(index + 1);
			} 
		}
	}
	private void addEdges(int numNodesCurrent)
	{
		ArrayList<UnorderedPair> newEdgeList = new ArrayList<UnorderedPair>();
		while(true)
		{
			TBucketSANonLinear fromBucket, toBucket;
			int fromNode, toNode;
			UnorderedPair newEdge;
			//do
			//{
				ArrayList returnList = selectOneEdge(numNodesCurrent);
				fromNode = (Integer)(returnList.get(0));
				fromBucket = (TBucketSANonLinear)(returnList.get(1));
				toNode = (Integer)(returnList.get(2));
				toBucket = (TBucketSANonLinear)(returnList.get(3));
				newEdge = new UnorderedPair(fromNode, toNode);
			//}while(newEdgeList.contains(newEdge) || adj.get(newEdge.from).contains(newEdge.to));
			newEdgeList.add(newEdge);			
			if(fromNode != numNodesCurrent && toNode != numNodesCurrent)
			{
				fromBucket.addNodeDegreeByOne(fromNode);
				toBucket.addNodeDegreeByOne(toNode);
			}
			else
			{
				if(fromNode == numNodesCurrent)
				{
					toBucket.addNodeDegreeByOne(toNode);
					TBucketSANonLinear tBucketNew = new TBucketSANonLinear(fromNode, this);
					tBucketList.add(0, tBucketNew);
					tBucketNew.addNodeDegreeByOne(fromNode);
				}
				else //toNode == numNodeCurrent
				{
					fromBucket.addNodeDegreeByOne(fromNode);
					TBucketSANonLinear tBucketNew = new TBucketSANonLinear(toNode, this);
					tBucketList.add(0, tBucketNew);
					tBucketNew.addNodeDegreeByOne(toNode);
				}
				break;
			}
		}
		// store and write out
		for(int i = 0; i < newEdgeList.size(); i++)
		{
			//adj.get(newEdgeList.get(i).from).add(newEdgeList.get(i).to);
			//adj.get(newEdgeList.get(i).to).add(newEdgeList.get(i).from);
			writeEdge.println(newEdgeList.get(i).from + " " + newEdgeList.get(i).to + " " + timeStamp);
			writeEdge.println(newEdgeList.get(i).to + " " + newEdgeList.get(i).from + " " + timeStamp);
		}
	}
	
	private ArrayList selectOneEdge(int numNodesCurrent)
	{
		int fromNode, toNode;
		TBucketSANonLinear fromBucket = null, toBucket = null;
		ArrayList returnFrom = selectOneEndPoint(numNodesCurrent, true);
		fromNode = (Integer)(returnFrom.get(0));
		fromBucket = (TBucketSANonLinear)(returnFrom.get(1));
		do
		{
			ArrayList returnTo = selectOneEndPoint(numNodesCurrent, true);
			toNode = (Integer)(returnTo.get(0));
			toBucket = (TBucketSANonLinear)(returnTo.get(1));
		}while(toNode == fromNode);
		ArrayList returnList = new ArrayList();
		returnList.add(fromNode);
		returnList.add(fromBucket);
		returnList.add(toNode);
		returnList.add(toBucket);
		return returnList;
	}
	
	private ArrayList selectOneEndPoint(int numNodesCurrent, boolean withVirtualNode)
	{
		int endPoint;
		TBucketSANonLinear selectedBucket = null;
		int vNodeIndicator = 1;
		if(!withVirtualNode)
			vNodeIndicator = 0;
		double rand = random.nextDouble() * (initAttract * vNodeIndicator + sumTBucketWeight);
		double accumulation = initAttract * vNodeIndicator;
		if(accumulation >= rand)
		{
			endPoint = numNodesCurrent;
		}
		else
		{
			for(int i = tBucketList.size() - 1; i >= 0; i--)
			//for(int i = 0; i < tBucketList.size(); i++)
			{
				selectedBucket = tBucketList.get(i);
				if(accumulation + selectedBucket.tBucketWeight >= rand)
					break;
				accumulation += selectedBucket.tBucketWeight;
			}
			endPoint = selectedBucket.selectNodeBySA();
		}
		ArrayList returnList = new ArrayList(2);
		returnList.add((Integer)endPoint);
		returnList.add(selectedBucket);
		return returnList;
	}
	
	private class UnorderedPair
	{
		int from;
		int to;
		private UnorderedPair(int f, int t)
		{
			from = Math.min(f, t);
			to = Math.max(f, t);
		}
		@Override
		public boolean equals(Object o)
		{
			if(!(o instanceof UnorderedPair))
				return false;
			UnorderedPair p = (UnorderedPair)o;
			return from == p.from && to == p.to;
		}
	}
	private void graphInfo()
	{
		System.out.println("numNodes = " + numNodesFinal);
		long numEdges = 0;
		for(int i = 0; i < numNodesFinal; i++)
			numEdges += deg[i];
		System.out.println("numEdges = " + numEdges/2);
		// gen time info
		System.out.println("initTime = " + initTime + "\t" + initTime / 1_000_000_000.0);
		System.out.println("genTime = " + genTime + "\t" + genTime / 1_000_000_000.0);
	}
	private double factorial(int k){
		double res = 1.0;
		for(int i = k; i >= 1; i--)
			res *= i;
		return res;
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int numNodesFinal = 10000; // Integer.parseInt(args[0]);
		String dType = "power-law";
		double dExp = 1.2;
		String tType = "power-law"; // args[1]; 
		double tExp = 0.8; // Double.parseDouble(args[2]);
		String fType = "poisson"; // args[3];
		double fExp = 5; // Double.parseDouble(args[4]);;
		int fMax = 30;
		int initAttract = 1000; // Integer.parseInt(args[5]);
		
		SAGeneratorNonLinear saGenNonLinear = new SAGeneratorNonLinear(numNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
		String outputBase = "graph/";
		File outputDir = new File(outputBase);
		if(!outputDir.exists())
			outputDir.mkdir();
		String edgeFile = outputBase + "sa-nl-" + numNodesFinal + "-" + dType + "-" + saGenNonLinear.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
		saGenNonLinear.createGraph(edgeFile);
	}

}

