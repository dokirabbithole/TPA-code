package baseline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class BA {
	// parameters
	int numOfNodesFinal;
	int numOfEdgesPerNode;
	
	int[] deg;
	int[] t;
	int timeCounter = 0;

	PrintWriter graphWriter;
	
	public BA(int numOfNodesFinal, int numOfEdgesPerNode)
	{
		this.numOfNodesFinal = numOfNodesFinal;
		this.numOfEdgesPerNode = numOfEdgesPerNode;
		System.out.println("for the efficient generation of BA graphs, please refer to https://github.com/alihadian/ROLL");
	}
	public void generateGraph() throws IOException{
		deg = new int[numOfNodesFinal];
		t = new int[numOfNodesFinal];
		for(int i = 0; i < numOfNodesFinal; i++){
			deg[i] = 0;
		}
		String outputBase = "graph/";
		File outputDir = new File(outputBase);
		if(!outputDir.exists())
			outputDir.mkdir();
		String outputPath = outputBase + "ba-" + numOfNodesFinal + "-" + numOfEdgesPerNode + ".txt";
		graphWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
		// construct an initial graph
		int initialGraphSize = Math.max(10, numOfEdgesPerNode);
		System.out.println("initialGraphSize = " + initialGraphSize);
		for(int i = 0; i < initialGraphSize; i++){
			deg[i] = initialGraphSize - 1;
			t[i] = timeCounter;
			for(int j = 0; j < initialGraphSize; j++){
				if(j != i){
					graphWriter.println(i + " " + j + " " + timeCounter);
				}
			}
		}
		timeCounter++;
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
		t[nodeId] = timeCounter;
		for(int i = 0; i < numOfEdgesPerNode; i++){
			int attachNode = chooseNodeToAttach(nodeId); // note that attachNode != nodeId
			graphWriter.println(attachNode + " " + nodeId + " " + timeCounter);
			graphWriter.println(nodeId + " " + attachNode + " " + timeCounter);
			deg[attachNode]++;
		}
		deg[nodeId] = numOfEdgesPerNode;
		timeCounter++;
	}
	
	private int chooseNodeToAttach(int nodeId){
		double sumPref = 0;
		for(int i = 0; i < nodeId; i++){
			sumPref += deg[i];
		}
		double rand = Math.random() * sumPref;
		int attachNode = 0;
		double accumulation = 0;
		while(accumulation + deg[attachNode] < rand){
			accumulation += deg[attachNode];
			attachNode++;
		}
		// check 
		if(attachNode >= nodeId){
			System.err.println("attachNode >= nodeId : attachNode = " + attachNode + " , nodeId = " + nodeId);
			System.exit(0);
		}
		return attachNode;
	}

	public static void main(String[] args) throws IOException {
		int numOfEdgesPerNode = 10;
		int numOfNodesFinal = 10000;
		
		BA baGraph = new BA(numOfNodesFinal, numOfEdgesPerNode);
		baGraph.generateGraph();
	}
}
