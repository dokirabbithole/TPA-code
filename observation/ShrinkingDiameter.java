package observation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class ShrinkingDiameter {
	EvolvingGraph eg;
	public ShrinkingDiameter(String edgeFile, String timeStampType) throws IOException
	{
		if(timeStampType.equals("node"))
			eg = new EvolvingGraph(edgeFile, timeStampType);
		else
		{
			System.err.println("timeStampType must be \"node\"");
			System.exit(0);
		}
	}
	public void effectiveDiameterByNode(long numNewNodes, int sampleSize, String effdFile) throws IOException
	{
		boolean canEvolve = true;
		int evolveCount = 0;
		PrintWriter writeEffd = new PrintWriter(new OutputStreamWriter(new FileOutputStream(effdFile)));
		while(canEvolve)
		{
			canEvolve = eg.evolveByNode(numNewNodes);
			evolveCount ++;
			double effd = computeEffDiameter(0.9, sampleSize, eg.adj, eg.d);
			writeEffd.println(evolveCount + " " + effd);
		}
		writeEffd.flush(); writeEffd.close();
	}
	private double computeEffDiameter(double reachRatio, int sampleSize, int[][] adj, int[] d)
	{
		double effd = 0;
		HashMap<Integer, Long> distFreq = new HashMap<Integer, Long>();
		int[] srcNodeList = new int[sampleSize];
		for(int i = 0; i < sampleSize; i++)
			srcNodeList[i] = i;
		for(int i = sampleSize; i < eg.numNodes; i++){
			if(Math.random() < (double)(sampleSize) / (i + 1)){
				int pos = (int)(Math.random() * sampleSize);
				srcNodeList[pos] = i;
			}
		}		
		for(int i = 0; i < sampleSize; i++)
		{
			SSSP(srcNodeList[i], adj, d, distFreq);
		}
		long countTotalDist = 0;
		ArrayList<Integer> sortDist = new ArrayList<Integer>();
		Iterator<Integer> itorDist = distFreq.keySet().iterator();
		while(itorDist.hasNext())
			sortDist.add(itorDist.next());
		Collections.sort(sortDist);
		System.out.println("***********************");
		for(int i = 0; i < sortDist.size(); i++)
		{
			System.out.println(sortDist.get(i) + "\t" + distFreq.get(sortDist.get(i)));
			countTotalDist += distFreq.get(sortDist.get(i));
		}
		System.out.println("***********************");
		
		long accumCount = 0;
		for(int i = 0; i < sortDist.size(); i++)
		{
			if(accumCount <= reachRatio * countTotalDist && ( accumCount + distFreq.get(sortDist.get(i))) > reachRatio * countTotalDist)
			{
				if(i == 0)
					effd = reachRatio * countTotalDist / distFreq.get(sortDist.get(i)) * sortDist.get(i);
				else
					effd = sortDist.get(i - 1) + (reachRatio * countTotalDist - accumCount) / distFreq.get(sortDist.get(i)) * (sortDist.get(i) - sortDist.get(i - 1));
				
				break;
			}
			accumCount += distFreq.get(sortDist.get(i));
		}
		System.out.println("reachRatio = " + reachRatio + " , effd = " + effd);
		return effd;
	}
	private void SSSP(int source, int[][] adj, int[] d, HashMap<Integer, Long> globalDistFreq)
	{
		// note that d[i] != adj[i].length, since the graph is evolving
		int numReach = 0; // num nodes reached by source
		HashMap<Integer, Integer> localDistFreq = new HashMap<Integer, Integer>();
		
		FibonacciHeap heap = new FibonacciHeap();
		FibonacciHeap.Entry[] nodeEntry = new FibonacciHeap.Entry[d.length];
		for(int i = 0; i < nodeEntry.length; i++)
			if(eg.time[i] != EvolvingGraph.NULLTIME)
				nodeEntry[i] = new FibonacciHeap.Entry(Integer.MAX_VALUE, i);	//key=infinity, id=i
		nodeEntry[source] = new FibonacciHeap.Entry(0, source);
		heap.insert(nodeEntry[source]);
		while(true)
		{
			FibonacciHeap.Entry currentVertex = heap.extractMin();
			if(currentVertex == null)
				break;
			if(currentVertex.getKey() == Integer.MAX_VALUE)
				break;	//Since all remain vertex can not reachable from source, no need to proceed
			else
			{
				int current_v = (Integer)(currentVertex.getObject());
				int current_dist = currentVertex.getKey();
				numReach++;
				if(!localDistFreq.containsKey(current_dist))
					localDistFreq.put(current_dist, 1);
				else
					localDistFreq.put(current_dist, localDistFreq.get(current_dist) + 1);
					
				if(d[current_v] > 0)
				{
					for(int j = 0; j < d[current_v]; j++)
					{
						int next = adj[current_v][j];
						if(nodeEntry[next].getKey() == Integer.MAX_VALUE)
							heap.insert(nodeEntry[next]);
						if(nodeEntry[next].getKey() > current_dist + 1)
							heap.decreaseKey(nodeEntry[next], current_dist + 1);
					}
				}
			}
		}
		System.out.println("source = " + source + " , numReach = " + numReach);
		if(numReach > 100) // if numReach is too small, do not consider it
		{
			Iterator<Integer> localItor = localDistFreq.keySet().iterator();
			while(localItor.hasNext())
			{
				int nextKey = localItor.next();
				if(!globalDistFreq.containsKey(nextKey))
					globalDistFreq.put(nextKey, (long)(localDistFreq.get(nextKey)));
				else
					globalDistFreq.put(nextKey, globalDistFreq.get(nextKey) + localDistFreq.get(nextKey));
			}
		}
	}
}
