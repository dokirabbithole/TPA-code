package roulettewheel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

public class RouletteWheelModel {
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
	// record generation time
	long genTime = 0; 
	long initTime = 0;
	
	public RouletteWheelModel(int numNodesFinal, String dType, double dExp, String tType, double tExp, String fType, double fExp, int fMax, int initAttract)
	{
		long t_init_0 = System.nanoTime();
		
		this.numNodesFinal = numNodesFinal;
		this.dType = dType;
		this.dExp = dExp;
		this.tType = tType; 
		this.tExp = tExp;
		this.fType = fType;
		this.fExp = fExp;
		this.fMax = fMax;
		this.initAttract = initAttract;
		
		deg = new int[this.numNodesFinal];
//		adjs = new HashMap<Integer, ArrayList<Integer>>();
		for(int i = 0; i < this.numNodesFinal; i++)
		{
			deg[i] = 0;
//			adjs.put(i, new ArrayList<Integer>());
		}
		double[] p = new double[fMax];
		if(fType.equals("poisson"))
		{
			for(int i = 0; i < fMax; i++)
				p[i] = Math.pow(fExp, i+1) * Math.pow(Math.E, -fExp) / factorial(i+1);             
			fitAlias = new AliasMethod(p);
		}
		else if(fType.equals("power-law"))
		{
			for(int i = 0; i < fMax; i++)
				p[i] = 1.0 / Math.pow(i+1, fExp);             
			fitAlias = new AliasMethod(p);
		}
		else if(fType.equals("exp"))
		{
			for(int i = 0; i < fMax; i++)
				p[i] = 1.0 / Math.pow(Math.E, fExp * (i+1));             
			fitAlias = new AliasMethod(p);
		}
		else
		{
			System.err.println("unknown fType");
			for(int i = 1; i < fMax; i++)
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
		writeEdge = new PrintWriter(new OutputStreamWriter(new FileOutputStream(edgeFile)));
		//calculate genTime
		long t_gen_0 = System.nanoTime();
		int initialGraphSize = 10;
		for(int i = 0; i < initialGraphSize; i++){
			for(int j = i+1; j < initialGraphSize; j++){
				if(Math.random() < 0.3){ // a random ER graph
					deg[i]++;
					deg[j]++;
					writeEdge.println(i + " " + j + " " + timeStamp);
//					adjs.get(i).add(j);
					writeEdge.println(j + " " + i + " " + timeStamp);
//					adjs.get(j).add(i);
				}
			}
		}
		timeStamp = 1;
				
		int tempCurNumOfNodes = 0;
		for(int i = initialGraphSize; i < numNodesFinal; i++)
		{
			addEdges(i);

			// print current progress
			if(i % (numNodesFinal / 100) == 0 && i != tempCurNumOfNodes){
				System.out.println(i);	
				tempCurNumOfNodes = i;
			}
			timeStamp ++;
		}
		//calculate genTime
		genTime = System.nanoTime() - t_gen_0;
		
		writeEdge.flush();
		writeEdge.close();
		graphInfo();
	}
	
	private void addEdges(int numNodesCurrent)
	{
		while(true)
		{
			UnorderedPair newEdge = addInternalEdge(numNodesCurrent);
			deg[newEdge.from] ++;
			deg[newEdge.to] ++;
			writeEdge.println(newEdge.from + " " + newEdge.to + " " + timeStamp);
			writeEdge.println(newEdge.to + " " + newEdge.from + " " + timeStamp);
			if(newEdge.from == numNodesCurrent || newEdge.to == numNodesCurrent)
				break;
		}
	}
	
	private UnorderedPair addInternalEdge(int numNodesCurrent)
	{
		double sumWeight = 0;
		for(int node = 0; node <= numNodesCurrent; node++)
			sumWeight += tpaScore(node, numNodesCurrent);
		
		int fromNode = selectNodeByTPA(numNodesCurrent, sumWeight);
		int toNode;
		do
		{
			toNode = selectNodeByTPA(numNodesCurrent, sumWeight);
		}while(toNode == fromNode);
		return new UnorderedPair(fromNode, toNode);
	}
	
	private int selectNodeByTPA(int numNodesCurrent, double sumWeight)
	{
		int nodeId = 0;
		double random = Math.random() * sumWeight;
		double accumulation = 0;
		while(true)
		{
			double tpaScore = tpaScore(nodeId, numNodesCurrent);
			if(accumulation + tpaScore < random)
			{
				accumulation += tpaScore;
				nodeId ++;
			}
			else
				break;
		}
		return nodeId;
	}
	
	private double tpaScore(int nodeId, int numNodesCurrent)
	{
		if(nodeId < numNodesCurrent){
			double tpa_score = fit[nodeId];
			tpa_score *= Math.pow(deg[nodeId], dExp);
			// "power-law" ; "log-normal" ; "exp"
			if(tType.equals("exp"))
				tpa_score /= Math.pow(Math.E, Math.max(numNodesCurrent - nodeId, 1) * tExp);
			else if(tType.equals("log-normal"))
				tpa_score /= Math.pow(Math.E, Math.log(numNodesCurrent - nodeId + 1) * Math.log(numNodesCurrent - nodeId + 1) * tExp);
			else // "power-law"
				tpa_score /= Math.pow(Math.max(numNodesCurrent - nodeId, 1), tExp);
			return tpa_score;
		}
		else if(nodeId == numNodesCurrent)
			return initAttract;
		else
		{
			error("nodeId > numNodesCurrent " + nodeId + " , " + numNodesCurrent);
			return 0; // unreachable
		}
	}
	
	private void error(String errInfo)
	{
		System.err.println(errInfo);
		System.exit(0);
	}
	public void graphInfo()
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
			UnorderedPair up = (UnorderedPair)o;
			return from == up.from && to == up.to;
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
		
		int numNodesFinal = 10000; // Integer.parseInt(args[0]);
		String dType = "power-law";
		double dExp = 1.0;
		String tType = "power-law"; // args[1]; 
		double tExp = 0.8; // Double.parseDouble(args[2]);
		String fType = "poisson"; // args[3];
		double fExp = 5; // Double.parseDouble(args[4]);;
		int fMax = 30;
		int initAttract = 1000; // Integer.parseInt(args[5]);
				
		RouletteWheelModel rwModel = new RouletteWheelModel(numNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
		String outputBase = "graph/";
		File outputDir = new File(outputBase);
		if(!outputDir.exists())
			outputDir.mkdir();
		String edgeFile = outputBase + "rw-" + numNodesFinal + "-" + dType + "-" + rwModel.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
		rwModel.createGraph(edgeFile);
	}
}

