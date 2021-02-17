package baseline;

import roulettewheel.AliasMethod;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;

public class DPAVaryInitDegree {
	int numOfNodesFinal;
	//int numOfEdgesPerNode;
	String initdType; // "power-law";"poisson"
	double initdExp;
	int initdMax;
	AliasMethod initdAlias;
	
	// time decay 
	String tType; // "power-law"; "log-normal"; "exp"
	double tExp;
	// fitness
	String fType; // "poisson"; "power-law"; "exp"
	double fExp;
	int fMax;
	
	int[] deg;
	int[] t;
	int[] fit;
	
	double[] pref;

	AliasMethod fitAlias; 
	int timeStamp = 0;
	// io
	PrintWriter graphWriter;
	
	public DPAVaryInitDegree(int numOfNodesFinal, String initdType, double initdExp, int initdMax, String tType, double tExp, String fType, double fExp, int fMax)
	{
		this.numOfNodesFinal = numOfNodesFinal;

		this.initdType = initdType; // "power-law";"poisson"
		this.initdExp = initdExp;
		this.initdMax = initdMax;
		// time decay 
		this.tType = tType; // "power-law"; "log-normal"; "exp"
		this.tExp = tExp;
		// fitness
		this.fType = fType; // "poisson"; "power-law"; "exp"
		this.fExp = fExp;
		this.fMax = fMax;
		
		deg = new int[this.numOfNodesFinal];
		t = new int[this.numOfNodesFinal];
		pref = new double[this.numOfNodesFinal];
		for(int i = 0; i < this.numOfNodesFinal; i++)
		{
			deg[i] = 0;
		}
		
		// sample init degree
		double[] pInitD = new double[initdMax];
		if(initdType.equals("power-law"))
		{
			for(int i = 0; i < initdMax; i++)
				pInitD[i] = 1.0 / Math.pow(i+1, initdExp);
			initdAlias = new AliasMethod(pInitD);
		}
		else if(initdType.equals("poisson"))
		{
			for(int i = 0; i < initdMax; i++)
				pInitD[i] = Math.pow(initdExp, i+1) * Math.pow(Math.E, -initdExp) / factorial(i+1);  
			initdAlias = new AliasMethod(pInitD);
		}
		else
		{
			System.err.println("invalid initdType: " + initdType);
			System.exit(0);
		}
		// sample fitness
		double[] pFit = new double[fMax];
		if(fType.equals("poisson"))
		{
			for(int i = 0; i < fMax; i++)
				pFit[i] = Math.pow(fExp, i+1) * Math.pow(Math.E, -fExp) / factorial(i+1);             
			fitAlias = new AliasMethod(pFit);
		}
		else if(fType.equals("power-law"))
		{
			for(int i = 0; i < fMax; i++)
				pFit[i] = 1.0 / Math.pow(i+1, fExp);             
			fitAlias = new AliasMethod(pFit);
		}
		else if(fType.equals("exp"))
		{
			for(int i = 0; i < fMax; i++)
				pFit[i] = 1.0 / Math.pow(Math.E, fExp * (i+1));             
			fitAlias = new AliasMethod(pFit);
		}
		else
		{
			System.err.println("unknown fType");
			for(int i = 1; i < fMax; i++)
				pFit[i] = 0;
			pFit[0] = 1;
			fitAlias = new AliasMethod(pFit);
		}
		fit = new int[this.numOfNodesFinal];
		for(int i = 0; i < fit.length; i++)
			fit[i] = fitAlias.sampleIndex() + 1;
			
		timeStamp = 0;
	}
	public void generateGraph() throws IOException{
		String outputBase = "graph/";
		File outputDir = new File(outputBase);
		if(!outputDir.exists())
			outputDir.mkdir();
		String outputPath = outputBase + "dpa-vary-" + numOfNodesFinal + "-" + initdType + "-" + initdExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + ".txt";
		graphWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
		// construct an initial graph
		int initialGraphSize = 10;
		System.out.println("initialGraphSize = " + initialGraphSize);
		for(int i = 0; i < initialGraphSize; i++){
			deg[i] = initialGraphSize - 1;
			t[i] = timeStamp;
			for(int j = 0; j < initialGraphSize; j++){
				if(j != i){
					graphWriter.println(i + " " + j + " " + timeStamp);
				}
			}
		}
		timeStamp++;
		// generate the remaining graph
		for(int nodeId = initialGraphSize; nodeId < numOfNodesFinal; nodeId++){
			addNodeWithEdges(nodeId); // allow duplicated edges, but no self-loops
			if(nodeId % (numOfNodesFinal / 100) == 0)
				System.out.println("nodeId = " + nodeId + " , " + nodeId / (numOfNodesFinal / 100) + "%");
		}
			
		// close
		graphWriter.flush();
		graphWriter.close();
		System.out.println("graph generated");
		System.out.println("numOfNodesFinal = " + numOfNodesFinal);
		long numOfEdges = 0;
		for(int i = 0; i < numOfNodesFinal; i++)
			numOfEdges += deg[i];
		System.out.println("numOfEdges = " + numOfEdges/2);
	}
	private void addNodeWithEdges(int nodeId){
		t[nodeId] = timeStamp;
		int numEdges = initdAlias.sampleIndex() + 1;
		for(int i = 0; i < numEdges; i++){
			int attachNode = chooseNodeToAttach(nodeId); // note that attachNode != nodeId
			graphWriter.println(attachNode + " " + nodeId + " " + timeStamp);
			graphWriter.println(nodeId + " " + attachNode + " " + timeStamp);
			deg[attachNode]++;
		}
		deg[nodeId] = numEdges;
		timeStamp++;
	}
	private int chooseNodeToAttach(int nodeId){
		double sumPref = 0;
		for(int i = 0; i < nodeId; i++){
			pref[i] = deg[i] * fit[i];
			
			if(tType.equals("power-law"))
				pref[i] /= Math.pow(Math.max(1, timeStamp - t[i]), tExp);
			else if(tType.equals("exp"))
				pref[i] /= Math.pow(Math.E, tExp * (timeStamp - t[i]));
			else if(tType.equals("log-normal"))
				pref[i] /= Math.pow(Math.E, tExp * Math.log(timeStamp - t[i] + 1) * Math.log(timeStamp - t[i] + 1));
				
			sumPref += pref[i];
		}
		double rand = Math.random() * sumPref;
		int attachNode = 0;
		double accumulation = 0;
		while(accumulation + pref[attachNode] < rand){
			accumulation += pref[attachNode];
			attachNode++;
		}
		// check 
		if(attachNode >= nodeId){
			System.err.println("attachNode >= nodeId : attachNode = " + attachNode + " , nodeId = " + nodeId);
			System.exit(0);
		}
		return attachNode;
	}
	private double factorial(int k){
		double res = 1.0;
		for(int i = k; i >= 1; i--)
			res *= i;
		return res;
	}
	public static void main(String[] args) throws IOException {
		int numOfNodesFinal = 10000;
		
		String initdType = "power-law";
		double initdExp = 1.0;
		int initdMax = 50;
		
		String tType = "power-law"; // args[1]; 
		double tExp = 0.8; // Double.parseDouble(args[2]);
		String fType = "poisson"; // args[3];
		double fExp = 5; // Double.parseDouble(args[4]);;
		int fMax = 30;
		
		DPAVaryInitDegree dpaGraph = new DPAVaryInitDegree(numOfNodesFinal, initdType, initdExp, initdMax, tType, tExp, fType, fExp, fMax);
		dpaGraph.generateGraph();
	}
}
