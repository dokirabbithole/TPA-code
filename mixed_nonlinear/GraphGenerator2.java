package mixed_nonlinear;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SplittableRandom;
import java.io.File;

import roulettewheel.AliasMethod;

public class GraphGenerator2 {
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
	double initAttract;
	int timeStamp = 0;
	// HashMap<Integer, ArrayList<Integer>> adj;
	// io
	PrintWriter writeEdge;
	
	// data structure
	ArrayList<TBucket> tBucketList;
	double sumTBucketWeight;
	// stochastic acceptance
	int[] deg;
	int[] fit;
	AliasMethod fitAlias;
	// ROLL-tree
	int[] rollNodeIndex;	// 
	int saThreshold;

	SplittableRandom random;
	
	// time
	long generationCost = 0;
	long initializationCost = 0;
	
	public GraphGenerator2(int numNodesFinal, String dType, double dExp, String tType, double tExp, String fType, double fExp, int fMax, int initAttract, int saThreshold)
	{
		// calculate initialization cost
		long t_init_0 = System.nanoTime();
		// initialize parameters
		this.numNodesFinal = numNodesFinal;
		this.dType = dType;
		this.dExp = dExp;
		this.tType = tType;
		this.tExp = tExp;
		this.fType = fType;
		this.fExp = fExp;
		this.fMax = fMax;
		this.initAttract = initAttract;

		this.saThreshold = saThreshold;
		
		timeStamp = 0;
		// initialize data structures
		// adj = new HashMap<Integer, ArrayList<Integer>>();
		deg = new int[this.numNodesFinal]; 
		for(int i = 0; i < this.numNodesFinal; i++)
		{
			deg[i] = 0;
			// adj.put(i, new ArrayList<Integer>());
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
		
		rollNodeIndex = new int[this.numNodesFinal];
		for(int i = 0; i < this.numNodesFinal; i++)
			rollNodeIndex[i] = -1;
		
		// calculate initialization cost
		initializationCost = System.nanoTime() - t_init_0;
		System.out.println("initialization cost = " + initializationCost);
	}
	public void createGraph(String edgeFile) throws IOException
	{
		// initialize data structures
		tBucketList = new ArrayList<TBucket>(2 * (int)(Math.log(this.numNodesFinal) / Math.log(2) + 1)); // upper bound
		sumTBucketWeight = 0;
		writeEdge = new PrintWriter(new OutputStreamWriter(new FileOutputStream(edgeFile)));
		random = new SplittableRandom(System.nanoTime());
		// initialize the graph with (node0, node1)
		TBucket tBucket0 = new SA_TBucket(0, this);
		tBucketList.add(0, tBucket0);
		TBucket tBucket1 = new SA_TBucket(1, this);
		tBucketList.add(1, tBucket1);
		//adj.get(0).add(1);
		//adj.get(1).add(0);
		tBucket0.addNodeDegreeByOne(0, -1, -1, -1);
		tBucket1.addNodeDegreeByOne(1, -1, -1, -1);
		timeStamp = 1;
		
		// calculate generation cost
		long t = System.nanoTime();
		int tempCurNumOfNodes = 0;
		for(int i = 2; i < numNodesFinal; i++)
		{
			addInternalLinks(i);
			// print current progress
			if(i % (numNodesFinal / 100) == 0 && i != tempCurNumOfNodes){
				System.out.println(i);	
				tempCurNumOfNodes = i;
			}
			mergeTBuckets();
			timeStamp++;
		}
		writeEdge.flush();
		writeEdge.close();
		// calculate generation cost
		generationCost += System.nanoTime() - t;
		
		graphInfo();
	}
	private void addInternalLinks(int numNodesCurrent)
	{
		ArrayList<UnorderedPair> newEdgeList = new ArrayList<UnorderedPair>();
		while(true)
		{
			TBucket fromTBucket, toTBucket;
			int fromNode, toNode;
			int fromNodeIndex, toNodeIndex;
			UnorderedPair newEdge;
			//do
			//{
				ArrayList returnList = selectOneEdge(numNodesCurrent);
				fromNode = (Integer)(returnList.get(0));
				fromNodeIndex = (Integer)(returnList.get(1));
				fromTBucket = (TBucket)(returnList.get(2));
				toNode = (Integer)(returnList.get(3));
				toNodeIndex = (Integer)(returnList.get(4));
				toTBucket = (TBucket)(returnList.get(5));
				newEdge = new UnorderedPair(fromNode, toNode); 
			//}while(newEdgeList.contains(newEdge)); // || adj.get(newEdge.from).contains(newEdge.to));
			newEdgeList.add(newEdge);
			
			if(fromNode != numNodesCurrent && toNode != numNodesCurrent)
			{
				// fromTBucket.addNodeDegreeByOne(fromNode, fromNodeIndex, deg[fromNode], deg[fromNode] + fit[fromNode]); // fromNodeIndex is not accurate
				fromTBucket.addNodeDegreeByOne(fromNode, rollNodeIndex[fromNode], deg[fromNode], deg[fromNode] + fit[fromNode]);
				// toTBucket.addNodeDegreeByOne(toNode, toNodeIndex, deg[toNode], deg[toNode] + fit[toNode]); // toNodeIndex is not accurate
				toTBucket.addNodeDegreeByOne(toNode, rollNodeIndex[toNode], deg[toNode], deg[toNode] + fit[toNode]);
			}
			else
			{
				if(fromNode == numNodesCurrent)
				{
					// toTBucket.addNodeDegreeByOne(toNode, toNodeIndex, deg[toNode], deg[toNode] + 1); // toNodeIndex is not accurate
					toTBucket.addNodeDegreeByOne(toNode, rollNodeIndex[toNode], deg[toNode], deg[toNode] + fit[toNode]);
					TBucket tBucketNew = new SA_TBucket(fromNode, this);
					tBucketList.add(0, tBucketNew);
					tBucketNew.addNodeDegreeByOne(fromNode, -1, -1, -1);
				}
				else //toNode == numNodeCurrent
				{
					// fromTBucket.addNodeDegreeByOne(fromNode, fromNodeIndex, deg[fromNode], deg[fromNode] + 1); fromNodeIndex is not accurate
					fromTBucket.addNodeDegreeByOne(fromNode, rollNodeIndex[fromNode], deg[fromNode], deg[fromNode] + fit[fromNode]);
					TBucket tBucketNew = new SA_TBucket(toNode, this);
					tBucketList.add(0, tBucketNew);
					tBucketNew.addNodeDegreeByOne(toNode, -1, -1, -1);
				}
				break;
			}
		}
		// persistence
		for(int i = 0; i < newEdgeList.size(); i++)
		{
			int fromNode = newEdgeList.get(i).from;
			int toNode = newEdgeList.get(i).to;
//			adj.get(fromNode).add(toNode);
//			adj.get(toNode).add(fromNode);
			writeEdge.println(fromNode + " " + toNode + " " + timeStamp);
			writeEdge.println(toNode + " " + fromNode + " " + timeStamp);
		}
	}
	private void mergeTBuckets()
	{
		for(int index = 0; index < tBucketList.size() - 2; index ++)
		{
			TBucket currentBucket = tBucketList.get(index);
			TBucket nextBucket = tBucketList.get(index + 1);
			TBucket nextNextBucket = tBucketList.get(index + 2);
			if(currentBucket.size() == nextBucket.size() && nextBucket.size() == nextNextBucket.size())
			{
				nextNextBucket.mergeTBucket(nextBucket);
				tBucketList.remove(index + 1);
			} 
		}
	}
	private ArrayList selectOneEdge(int numNodesCurrent)
	{
		TBucket fromTBucket, toTBucket;
		int fromNode, toNode;
		int fromNodeIndex, toNodeIndex;
		ArrayList fromReturnList = selectOneEndPoint(numNodesCurrent, true);
		fromNode = (Integer)(fromReturnList.get(0));
		fromNodeIndex = (Integer)(fromReturnList.get(1));
		fromTBucket = (TBucket)(fromReturnList.get(2));
		do
		{
			ArrayList toReturnList = selectOneEndPoint(numNodesCurrent, true);
			toNode = (Integer)(toReturnList.get(0));
			toNodeIndex = (Integer)(toReturnList.get(1));
			toTBucket = (TBucket)(toReturnList.get(2));
		}while(toNode == fromNode);
		ArrayList returnList = new ArrayList(4);
		returnList.add(fromNode);
		returnList.add(fromNodeIndex); // not used
		returnList.add(fromTBucket);
		returnList.add(toNode);
		returnList.add(toNodeIndex); // not used
		returnList.add(toTBucket);
		return returnList;
	}
	private ArrayList selectOneEndPoint(int numNodesCurrent, boolean hasVirtualNode)
	{
		int endPoint;
		int nodeIndex = -1; // no use
		TBucket tBucket = null;
		int indicator = 1;
		if(!hasVirtualNode)
			indicator = 0;
		double rand = random.nextDouble() * (initAttract * indicator + sumTBucketWeight);
		double accumulation = initAttract * indicator;
		if(accumulation >= rand)
		{
			endPoint = numNodesCurrent;
		}
		else
		{
			for(int i = tBucketList.size() - 1; i > 0; i--)
//			for(int i = 0; i < tBucketList.size(); i++)
			{
				tBucket = tBucketList.get(i);
				if(accumulation + tBucket.tBucketWeight >= rand)
					break;
				accumulation += tBucket.tBucketWeight;
			}
			int[] returnArray = tBucket.selectNodeInTBucket();
			endPoint = returnArray[0];
			nodeIndex = returnArray[1];
		}
		ArrayList returnList = new ArrayList(3);
		returnList.add((Integer)endPoint);
		returnList.add((Integer)nodeIndex);
		returnList.add(tBucket);
		return returnList;
	}
	private void graphInfo()
	{
		System.out.println("numNodes = " + numNodesFinal);
		long numEdges = 0;
		for(int i = 0; i < numNodesFinal; i++)
			numEdges += deg[i];
		System.out.println("numEdges = " + numEdges/2);
		// generation time info
		System.out.println("initialization cost = " + initializationCost + "\t" + initializationCost / 1_000_000_000.0);
		System.out.println("generation cost = " + generationCost + "\t" + generationCost / 1_000_000_000.0);
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
	private double factorial(int k){
		double res = 1.0;
		for(int i = k; i >= 1; i--)
			res *= i;
		return res;
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int numNodesFinal = 1000000; // Integer.parseInt(args[0]);
		String dType = "power-law";
		double dExp = 1.0;
		String tType = "power-law"; // args[1]; 
		double tExp = 0.8; // Double.parseDouble(args[2]);
		String fType = "poisson"; // args[3];
		double fExp = 5; // Double.parseDouble(args[4]);;
		int fMax = 30;
		int initAttract = 1000; // Integer.parseInt(args[5]);
		
		int saThreshold = 10000;
		System.out.println("saThreshold = " + saThreshold);
		
		GraphGenerator2 tpaUGraphGen = new GraphGenerator2(numNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract, saThreshold);
		String outputBase = "graph/";
		File outputDir = new File(outputBase);
		if(!outputDir.exists())
			outputDir.mkdir();
		String edgeFile = outputBase + "mix-nl-" + numNodesFinal + "-" + dType + "-" + tpaUGraphGen.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
		tpaUGraphGen.createGraph(edgeFile);
		
		for(int i = 0; i < tpaUGraphGen.tBucketList.size(); i++)
		{
			System.out.println("index = " + i + " , tBucketWeight = " + tpaUGraphGen.tBucketList.get(i).tBucketWeight);
		}
	}

}
