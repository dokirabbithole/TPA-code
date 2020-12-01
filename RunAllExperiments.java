import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import observation.AverageDegreeVsAge;
import observation.DegreeDistribution;
import observation.ShrinkingDiameter;
import baseline.BA;
import baseline.DPA;
import baseline.DPAVaryInitDegree;
import mixed.GraphGenerator;
import roulettewheel.RouletteWheelModel;
import sa.SAGenerator;


public class RunAllExperiments {
	public void runExp11a() throws IOException
	{
		// average degree = 10
		// the following parameters are fixed
		String dType = "power-law";
		double dExp = 1.0;
		String tType = "power-law"; 
		double tExp = 0.8; 
		String fType = "poisson"; 
		double fExp = 5; 
		int fMax = 30;
		// and vary the parameters below
		// RW
		ArrayList<Integer> rwNodeSizeList = new ArrayList<Integer>(Arrays.asList(1000, 5000, 10000));
		ArrayList<Integer> rwAttractList = new ArrayList<Integer>(Arrays.asList(37, 56, 68));
		for(int i = 0; i < rwNodeSizeList.size(); i++)
		{
			int numOfNodesFinal = rwNodeSizeList.get(i);
			int initAttract = rwAttractList.get(i);
			RouletteWheelModel rwModel = new RouletteWheelModel(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
			String edgeFile = "graph/" + "rw-" + numOfNodesFinal + "-" + dType + "-" + rwModel.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
			rwModel.createGraph(edgeFile);
		}
		// SA
		ArrayList<Integer> saNodeSizeList = new ArrayList<Integer>(Arrays.asList(1000, 5000, 10000, 50000, 100*1000, 500*1000, 1000*1000, 5*1000*1000, 10*1000*1000, 50*1000*1000));
		int saInitAttract = 10000; 
		for(int i = 0; i < saNodeSizeList.size(); i++)
		{
			int numOfNodesFinal = saNodeSizeList.get(i);
			SAGenerator saGen = new SAGenerator(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, saInitAttract);
			String edgeFile = "graph/" + "sa-" + numOfNodesFinal + "-" + dType + "-" + saGen.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + saInitAttract + ".txt";
			saGen.createGraph(edgeFile);
		}
		// Hybrid
		int saThreshold = 100000; // fixed
		System.out.println("saThreshold = " + saThreshold);
		ArrayList<Integer> mixedNodeSizeList = new ArrayList<Integer>(Arrays.asList(1000, 5000, 10000, 50000, 100*1000, 500*1000, 1000*1000, 5*1000*1000, 10*1000*1000, 50*1000*1000, 100*1000*1000));
		ArrayList<Integer> mixedInitAttract = new ArrayList<Integer>(Arrays.asList(10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 100000, 100000, 100000));
		for(int i = 0; i < mixedNodeSizeList.size(); i++)
		{
			int numOfNodesFinal = mixedNodeSizeList.get(i);
			int initAttract = mixedInitAttract.get(i);
			GraphGenerator tpaUGraphGen = new GraphGenerator(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract, saThreshold);
			String edgeFile = "graph/" + "mix-" + numOfNodesFinal + dType + "-" + tpaUGraphGen.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
			tpaUGraphGen.createGraph(edgeFile);
		}
	}
	public void runExp11b() throws IOException
	{
		// average degree = 20
		// the following parameters are fixed
		String dType = "power-law";
		double dExp = 1.0;
		String tType = "power-law"; 
		double tExp = 0.8; 
		String fType = "poisson"; 
		double fExp = 5; 
		int fMax = 30;
		// and vary the parameters below
		// RW
		ArrayList<Integer> rwNodeSizeList = new ArrayList<Integer>(Arrays.asList(1000, 5000, 10000));
		ArrayList<Integer> rwAttractList = new ArrayList<Integer>(Arrays.asList(31, 44, 53));
		for(int i = 0; i < rwNodeSizeList.size(); i++)
		{
			int numOfNodesFinal = rwNodeSizeList.get(i);
			int initAttract = rwAttractList.get(i);
			RouletteWheelModel rwModel = new RouletteWheelModel(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
			String edgeFile = "graph/" + "rw-" + numOfNodesFinal + "-" + dType + "-" + rwModel.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
			rwModel.createGraph(edgeFile);
		}
		// SA
		// 1000, 5000, 10000, 50000, 100*1000, 500*1000, 1000*1000, 5*1000*1000, 10*1000*1000
		// 150,  200,  280,   400,   500,      670,      800,       1000,        1300
		ArrayList<Integer> saNodeSizeList = new ArrayList<Integer>(Arrays.asList(1000, 5000, 10000, 50000, 100*1000, 500*1000, 1000*1000, 5*1000*1000, 10*1000*1000));
		ArrayList<Integer> saInitAttractList = new ArrayList<Integer>(Arrays.asList(150,  200,  280,   400,   500,      670,      800,       1000,        1300));
		for(int i = 0; i < saNodeSizeList.size(); i++)
		{
			int numOfNodesFinal = saNodeSizeList.get(i);
			int saInitAttract = saInitAttractList.get(i);
			SAGenerator saGen = new SAGenerator(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, saInitAttract);
			String edgeFile = "graph/" + "sa-" + numOfNodesFinal + "-" + dType + "-" + saGen.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + saInitAttract + ".txt";
			saGen.createGraph(edgeFile);
		}
		// Hybrid
		int saThreshold = 100000; // fixed
		System.out.println("saThreshold = " + saThreshold);
		// 1000, 5000, 10000, 50000, 100*1000, 500*1000, 1000*1000, 5*1000*1000, 10*1000*1000, 50*1000*1000, 100*1000*1000
		// 150,  200,  280,   400,   500,      670,      800,       2000,        3000,		   12000,        23000
		ArrayList<Integer> mixedNodeSizeList = new ArrayList<Integer>(Arrays.asList(1000, 5000, 10000, 50000, 100*1000, 500*1000, 1000*1000, 5*1000*1000, 10*1000*1000, 50*1000*1000, 100*1000*1000));
		ArrayList<Integer> mixedInitAttract = new ArrayList<Integer>(Arrays.asList(150,  200,  280,   400,   500,      670,      800,       2000,        3000,		   12000,        23000));
		for(int i = 0; i < mixedNodeSizeList.size(); i++)
		{
			int numOfNodesFinal = mixedNodeSizeList.get(i);
			int initAttract = mixedInitAttract.get(i);
			GraphGenerator tpaUGraphGen = new GraphGenerator(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract, saThreshold);
			String edgeFile = "graph/" + "mix-" + numOfNodesFinal + dType + "-" + tpaUGraphGen.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
			tpaUGraphGen.createGraph(edgeFile);
		}
	}
	
	public void runExp3() throws IOException
	{
		BA baGraph = new BA(10000, 5);
//		baGraph.generateGraph();
		String baFile = "graph/" + "ba-10000-5.txt";
		
		DPA dpaGraph = new DPA(10000, 5, "power-law", 0.8, "poisson", 5, 30);
//		dpaGraph.generateGraph();
		String dpaFile = "graph/" + "dpa-10000-5-power-law-0.8-poisson-5.0.txt";
		
		DPAVaryInitDegree dpaGraph2 = new DPAVaryInitDegree(10000, "power-law", 1.1, 50, "power-law", 0.8, "poisson", 5, 30);
//		dpaGraph2.generateGraph();
		String dpaFile2 = "graph/" + "dpa-vary-10000-power-law-1.1-power-law-0.8-poisson-5.0.txt";
		
		RouletteWheelModel rwModel = new RouletteWheelModel(10000, "power-law", 1, "power-law", 0.8, "poisson", 5, 30, 68);
		String rwFile = "graph/" + "rw-" + 10000 + "-" + "power-law" + "-" + 1 + "-" + "power-law" + "-" + 0.8 + "-" + "poisson" + "-" + 5 + "-" + 68 + ".txt";
//		rwModel.createGraph(rwFile);
		
		// degree distribution
		DegreeDistribution degDist = new DegreeDistribution();
		degDist.degreeDistribution(10000, baFile, "ba-10000-5", "properties/");
		degDist.degreeDistribution(10000, dpaFile, "dpa-10000-5-power-law-0.8-poisson-5", "properties/");
		degDist.degreeDistribution(10000, dpaFile2, "dpa-vary-10000-power-law-1.1-power-law-0.8-poisson-5.0", "properties/");
		degDist.degreeDistribution(10000, rwFile, "rw-10000-power-law-1-power-law-0.8-poisson-5-68", "properties/");
		
		// shrinking diameter
		ShrinkingDiameter sd_ba = new ShrinkingDiameter(baFile, "node");
		sd_ba.effectiveDiameterByNode(1000, 100, "properties/" + "ba-10000-5-effd.txt");
		ShrinkingDiameter sd_dpa = new ShrinkingDiameter(dpaFile, "node");
		sd_dpa.effectiveDiameterByNode(1000, 100, "properties/" + "dpa-10000-5-power-law-0.8-poisson-5-effd.txt");
		ShrinkingDiameter sd_dpa2 = new ShrinkingDiameter(dpaFile2, "node");
		sd_dpa2.effectiveDiameterByNode(1000, 100, "properties/" + "dpa-vary-10000-power-law-1.1-power-law-0.8-poisson-5.0-effd.txt");
		ShrinkingDiameter sd_rw = new ShrinkingDiameter(rwFile, "node");
		sd_rw.effectiveDiameterByNode(1000, 100, "properties/" + "rw-10000-power-law-1-power-law-0.8-poisson-5-68-effd.txt");
		
		// avg degree vs. time
		AverageDegreeVsAge.avgd_vs_t_by_slot(10000, baFile, "ba-10000-5", 100, "properties/");
		AverageDegreeVsAge.avgd_vs_t_by_slot(10000, dpaFile, "dpa-10000-5-power-law-0.8-poisson-5", 100, "properties/");
		AverageDegreeVsAge.avgd_vs_t_by_slot(10000, dpaFile2, "dpa-vary-10000-power-law-1.1-power-law-0.8-poisson-5.0", 100, "properties/");
		AverageDegreeVsAge.avgd_vs_t_by_slot(10000, rwFile, "rw-10000-power-law-1-power-law-0.8-poisson-5-68", 100, "properties/");
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		RunAllExperiments runAllExp = new RunAllExperiments();
		
		// Fig. 11
//		runAllExp.runExp11a();
//		runAllExp.runExp11b();
		
		// Fig. 3
		runAllExp.runExp3();
	}

}
