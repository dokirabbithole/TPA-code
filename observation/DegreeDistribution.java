package observation;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class DegreeDistribution {
	public static void degreeDistribution(int numOfNodesFinal, String edgeFile, String fileLabel, String logPath) throws IOException
	{
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
		
		// allow multi-edges
		HashMap<Integer, Integer> degFreqMulti = new HashMap<Integer, Integer>();
		for(int i = 0; i < numOfNodesFinal; i++){
			if(deg[i] > 0){
				if(!degFreqMulti.containsKey(deg[i]))
					degFreqMulti.put(deg[i], 1);
				else
					degFreqMulti.put(deg[i], degFreqMulti.get(deg[i]) + 1);
			}
		}
		String multiDistFile = logPath + fileLabel + "-dist=multi" + ".txt";
		PrintWriter multiWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(multiDistFile)));
		Iterator<Integer> degFreqMultiItor = degFreqMulti.keySet().iterator();
		while(degFreqMultiItor.hasNext()){
			int d = degFreqMultiItor.next();
			multiWriter.println(d + " " + degFreqMulti.get(d));
		}
		multiWriter.flush();
		multiWriter.close();
		
		// simple graph
		HashMap<Integer, Integer> degFreqSimple = new HashMap<Integer, Integer>();
		Iterator<Integer> adjItor = adjs.keySet().iterator();
		while(adjItor.hasNext()){
			int degree = adjs.get(adjItor.next()).size();
			if(degree > 0){
				if(!degFreqSimple.containsKey(degree))
					degFreqSimple.put(degree, 1);
				else
					degFreqSimple.put(degree, degFreqSimple.get(degree) + 1);
			}
		}			
		String simpleDistFile = logPath + fileLabel + "-dist=simple" + ".txt";
		PrintWriter simpleWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(simpleDistFile)));		
		Iterator<Integer> degFreqSimpleItor = degFreqSimple.keySet().iterator();
		while(degFreqSimpleItor.hasNext()){
			int d = degFreqSimpleItor.next();
			simpleWriter.println(d + " " + degFreqSimple.get(d));
		}
		simpleWriter.flush();
		simpleWriter.close();
	}
}
