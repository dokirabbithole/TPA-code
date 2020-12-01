package observation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EvolvingGraph {
	public int numNodes;
	public long numEdges;
	public int currentNumNodes;
	public long currentNumEdges;
	
	// graphs are undirected
	public int[][] adj; // allow duplicated edges
	public int[] d;
	
	String timeStampType; // "timeStamp" or "node"
	public long[] time;
	long currentTime;
	static final long NULLTIME = Long.MIN_VALUE;
	
	// cursor
	public BufferedReader readEdge = null;
	public String line = null;
	public EvolvingGraph(String edgeFile, String timeStampType) throws IOException
	{
		this.timeStampType = timeStampType;
		// 1st scan
		numNodes = 0;
		numEdges = 0;
		currentNumNodes = 0;
		currentNumEdges = 0;
		readEdge = new BufferedReader(new FileReader(new File(edgeFile)));
		line = readEdge.readLine();
		while(line != null)
		{
			int fromNode = Integer.parseInt(line.split(" ")[0]);
			int toNode = Integer.parseInt(line.split(" ")[1]);
			// may exist node of degree 0
			numNodes = Math.max(numNodes, fromNode + 1);	
			numNodes = Math.max(numNodes, toNode + 1);
			numEdges++;
			line = readEdge.readLine();
		}
		readEdge.close();
		System.out.println("numNodes = " + numNodes);
		System.out.println("numEdges = " + numEdges);
		// 2nd scan
		readEdge = new BufferedReader(new FileReader(new File(edgeFile)));
		d = new int[numNodes];
		time = new long[numNodes];
		for(int i = 0; i < numNodes; i++)
		{
			d[i] = 0;
			time[i] = NULLTIME;
		}
		line = readEdge.readLine();
		while(line != null)
		{
			int fromNode = Integer.parseInt(line.split(" ")[0]);
			int toNode = Integer.parseInt(line.split(" ")[1]);
			d[fromNode]++; // if there exists (fromNode, toNode), it also exists (toNode, fromNode)
			line = readEdge.readLine();
		}
		readEdge.close();
		// 3rd scan, evolving
		adj = new int[numNodes][];
		for(int i = 0; i < numNodes; i++)
		{
			adj[i] = new int[d[i]];
			d[i] = 0;
		}
		readEdge = new BufferedReader(new FileReader(new File(edgeFile)));
		// invariant: line != null
		line = readEdge.readLine();
		if(this.timeStampType.equals("timeStamp")) // natural graph
		{
			currentTime = Long.parseLong(line.split(" ")[2]);
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			System.out.println("evolving... start time: " + dateFormat.format(new Date(currentTime)));
		}
		else // timeStampType.equals("node"); synthetic graph
		{
			currentTime = (long)(Integer.parseInt(line.split(" ")[2]));
		}
	}
	public boolean evolveByTime(String timeGranularity, int value) throws IOException
	{
		if(value <= 0)
		{
			System.err.println("value <= 0: " + value); System.exit(0); return true;
		}
		
		if(timeGranularity.equals("day"))
		{
			long timeInterval = (long)1000 * 60 * 60 * 24 * value;
			return evolveByTime(timeInterval);
		}
		else if(timeGranularity.equals("month"))
		{
			long timeInterval = (long)1000 * 60 * 60 * 24 * 30 * value;
			return evolveByTime(timeInterval);
		}
		else
		{
			System.err.println("timeGranularity = " + timeGranularity); System.exit(0); return true;
		}
	}
	private boolean evolveByTime(long timeInterval) throws IOException
	{
		int numNewNodes = 0, numNewEdges = 0;
		
		String[] split;
		int fromNode;
		int toNode;
		long timeStamp = NULLTIME;
		while(line != null)
		{
			split = line.split(" ");
			fromNode = Integer.parseInt(split[0]);
			toNode = Integer.parseInt(split[1]);
			timeStamp = Long.parseLong(split[2]);
			
			if(timeStamp >= currentTime + timeInterval)
				break;	
			
			numNewEdges ++;
			currentNumEdges ++;
			if(time[fromNode] == NULLTIME)
			{
				numNewNodes ++;
				currentNumNodes ++;
				
				time[fromNode] = timeStamp;
			}
			if(time[toNode] == NULLTIME)
			{
				numNewNodes ++;
				currentNumNodes ++;
				
				time[toNode] = timeStamp;
			}
			
			adj[fromNode][d[fromNode]] = toNode;
			d[fromNode] ++;
			
			line = readEdge.readLine();
		}
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println(dateFormat.format(new Date(currentTime)) + " -> " + dateFormat.format(new Date(timeStamp)));
		System.out.println("numNewNodes = " + numNewNodes + " , numNewEdges = " + numNewEdges);
		System.out.println("currentNumNodes = " + currentNumNodes + " , currentNumEdges = " + currentNumEdges);
		// update currentDate
		currentTime = timeStamp;		
		if(line != null)
			return true;
		else
		{
			readEdge.close();
			return false;
		}
	}
	public boolean evolveByEdge(long numEdgeToEvolve) throws IOException
	{
		int numNewNodes = 0, numNewEdges = 0;
		
		String[] split;
		int fromNode;
		int toNode;
		long timeStamp = NULLTIME;
		while(line != null)
		{
			split = line.split(" ");
			fromNode = Integer.parseInt(split[0]);
			toNode = Integer.parseInt(split[1]);
			timeStamp = (long)Integer.parseInt(split[2]);
			
			if(numNewEdges >= numEdgeToEvolve)
				break;	
			
			numNewEdges ++;
			currentNumEdges ++;
			if(time[fromNode] == NULLTIME)
			{
				numNewNodes ++;
				currentNumNodes ++;
				
				time[fromNode] = timeStamp;
			}
			if(time[toNode] == NULLTIME)
			{
				numNewNodes ++;
				currentNumNodes ++;
				
				time[toNode] = timeStamp;
			}
						
			adj[fromNode][d[fromNode]] = toNode;
			d[fromNode]++;
			
			line = readEdge.readLine();
		}
		System.out.println("numNewNodes = " + numNewNodes + " , numNewEdges = " + numNewEdges + " , numEdgeToEvolve = " + numEdgeToEvolve);
		System.out.println("currentNumNodes = " + currentNumNodes + " , currentNumEdges = " + currentNumEdges);
		// update currentDate 
		currentTime = timeStamp;		
		if(line != null)
			return true;
		else
		{
			readEdge.close();
			return false;
		}
	}
	public boolean evolveByNode(long numNodeToEvolve) throws IOException
	{
		int numNewNodes = 0, numNewEdges = 0;
		
		String[] split;
		int fromNode;
		int toNode;
		long timeStamp = NULLTIME;
		while(line != null)
		{
			split = line.split(" ");
			fromNode = Integer.parseInt(split[0]);
			toNode = Integer.parseInt(split[1]);
			timeStamp = (long)Integer.parseInt(split[2]);
			
			if(numNewNodes >= numNodeToEvolve)
				break;	
			
			numNewEdges ++;
			currentNumEdges ++;
			if(time[fromNode] == NULLTIME)
			{
				numNewNodes ++;
				currentNumNodes ++;
				
				time[fromNode] = timeStamp;
			}
			if(time[toNode] == NULLTIME)
			{
				numNewNodes ++;
				currentNumNodes ++;
				
				time[toNode] = timeStamp;
			}
			
			adj[fromNode][d[fromNode]] = toNode;
			d[fromNode] ++;
						
			line = readEdge.readLine();
		}
		System.out.println("numNewNodes = " + numNewNodes + " , numNewEdges = " + numNewEdges + " , numNodeToEvolve = " + numNodeToEvolve);
		System.out.println("currentNumNodes = " + currentNumNodes + " , currentNumEdges = " + currentNumEdges);
		// update currentDate
		currentTime = timeStamp;
		if(line != null)
			return true;
		else
		{
			readEdge.close();
			return false;
		}
	}
	public static void main(String[] args) throws IOException {
		// natural graph: Wiki-Conflicts ; Youtube
		// synthetic graphs
		
		String edgeFile = "E:/Dataset/TPA/KONECT-Format/wikiconflict.txt";
		String timeStampType = "timeStamp";
		
		EvolvingGraph eg = new EvolvingGraph(edgeFile, timeStampType);
		boolean canEvolve = true;
		while(canEvolve)
		{
			canEvolve = eg.evolveByTime("month", 1);
			//canEvolve = eg.evolveByEdge((long)(Math.ceil(eg.numEdges/10.0)));
		}
	}
}
