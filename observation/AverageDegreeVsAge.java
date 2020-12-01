package observation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

public class AverageDegreeVsAge {
	public static void avgd_vs_t_by_slot(int numOfNodesFinal, String edgeFile, String fileLabel, int numSlot, String logDir) throws IOException{
		int[] deg = new int[numOfNodesFinal]; // allow multi-edges
		for(int i = 0; i < numOfNodesFinal; i++)
			deg[i] = 0;
		HashMap<Integer, HashSet<Integer>> adjs = new HashMap<Integer, HashSet<Integer>>(); // simple graph
		BufferedReader graphReader = new BufferedReader(new FileReader(new File(edgeFile)));
		String edge = graphReader.readLine();
		while(edge != null){
			int from = Integer.parseInt(edge.split(" ")[0]);
			int to = Integer.parseInt(edge.split(" ")[1]);
			// undirected graph;
			deg[from]++;
			if(!adjs.containsKey(from))
				adjs.put(from, new HashSet<Integer>());
			HashSet<Integer> adj = adjs.get(from);
			if(!adj.contains(to))
				adj.add(to);
			
			edge = graphReader.readLine();
		}
		graphReader.close();
		
		double[] avgd_multi = new double[numSlot];
		double[] avgd_simple = new double[numSlot];
		for(int i = 0; i < numSlot; i++){
			avgd_multi[i] = 0;
			avgd_simple[i] = 0;
		}
		
		int numNodesPerSlot = numOfNodesFinal / numSlot;
		for(int slotNum = 0; slotNum < numSlot; slotNum++){
			int beginNode = slotNum * numNodesPerSlot;
			int endNode = (slotNum + 1) * numNodesPerSlot;
			for(int node = beginNode; node < endNode; node++){
				avgd_multi[slotNum] += deg[node];
				if(adjs.get(node) != null)
					avgd_simple[slotNum] += adjs.get(node).size();
			}
		}
		
		PrintWriter logWriterMulti = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logDir + fileLabel + "-avgd-vs-t-per-slot-multiple.txt")));
		PrintWriter logWriterSimple = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logDir + fileLabel + "-avgd-vs-t-per-slot-simple.txt")));
		for(int i = 0; i < numSlot; i++){
			logWriterMulti.println(i + " " + avgd_multi[i] / numNodesPerSlot);
			logWriterSimple.println(i + " " + avgd_simple[i] / numNodesPerSlot);
		}
		logWriterMulti.flush();
		logWriterMulti.close();
		logWriterSimple.flush();
		logWriterSimple.close();
	}
}
