package mixed;

import java.util.HashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class SA_TBucket extends TBucket{
	int minNodeId, maxNodeId;
	int maxDeg;
	// double tBucketWeight; // inherited
	double timeDecayFactor;
	// GraphGenerator generator; // inherited
	
	public SA_TBucket(int nodeId, GraphGenerator generator)
	{
		minNodeId = nodeId;
		maxNodeId = nodeId;
		maxDeg = 0;
		tBucketWeight = maxDeg;

		if(generator.tType.equals("power-law") || generator.tType.equals("log-normal"))
			timeDecayFactor = 1;
		else if(generator.tType.equals("exp"))
			timeDecayFactor = Math.pow(Math.E, generator.tExp);
		else{
			System.err.println("invalid generator.tType: " + generator.tType);
			System.exit(0);
		}
		
		this.generator = generator;
	}
	public void addNodeDegreeByOne(int nodeId, int nodeIndex, int degreeBefore, int degreeAfter)
	{
		// should not use nodeIndex, degreeBefore, degreeAfter
		// integrity check
		if(nodeId < minNodeId || nodeId > maxNodeId)
		{
			System.err.println("This SA_TBucket does not contain " + nodeId);
			System.exit(0);
		}
		
		generator.deg[nodeId] += generator.fit[nodeId];
		if(generator.deg[nodeId] > maxDeg)
			maxDeg = generator.deg[nodeId];
		
		double weightInc = generator.fit[nodeId] / timeDecayFactor; // dExp = 1
		tBucketWeight += weightInc;
		generator.sumTBucketWeight += weightInc;
	}
	public int[] selectNodeInTBucket()
	{
		int selectedNode;
		int selectedNodeIndex = -1; // no use
		while(true)
		{
			selectedNode = minNodeId + generator.random.nextInt(maxNodeId - minNodeId + 1);
			if(generator.random.nextDouble() < (double)generator.deg[selectedNode] / maxDeg)
			{
				int[] returnArray = new int[2];
				returnArray[0] = selectedNode;
				returnArray[1] = selectedNodeIndex;
				return returnArray;
			}
		}
	}
	public int size()
	{
		return maxNodeId - minNodeId + 1;
	}
	public void mergeTBucket(TBucket anotherTBucket)
	{
		// integrity check
		if(!(anotherTBucket instanceof SA_TBucket))
		{
			System.err.println("trying to merge a SA_TBucket and a ROLL_TBucket");
			System.exit(0);
		}
		if(size() != anotherTBucket.size())
		{
			System.err.println("trying to merge two SA_TBucket of different size: " + size() + " , " + anotherTBucket.size());
			System.exit(0);
		}
		SA_TBucket another_SA_TBucket = (SA_TBucket)anotherTBucket;
		
		if(size() * 2 <= generator.saThreshold) // merge two SA_TBucket into one SA_TBucket
		{
			// merge another_SA_TBucket into this tBucket
			minNodeId = Math.min(minNodeId, another_SA_TBucket.minNodeId);
			maxNodeId = Math.max(maxNodeId, another_SA_TBucket.maxNodeId);
			maxDeg = Math.max(maxDeg, another_SA_TBucket.maxDeg);
			
			generator.sumTBucketWeight -= tBucketWeight;
			generator.sumTBucketWeight -= another_SA_TBucket.tBucketWeight;			
			double temp = (tBucketWeight + another_SA_TBucket.tBucketWeight) * timeDecayFactor;
			
			if(generator.tType.equals("power-law")) 
				timeDecayFactor = Math.pow(size(), generator.tExp);
			else if(generator.tType.equals("log-normal"))
				timeDecayFactor = Math.pow(Math.E, generator.tExp * Math.log(size()) * Math.log(size()));
			else if(generator.tType.equals("exp"))
				timeDecayFactor = Math.pow(Math.E, generator.tExp * size());
			else{
				System.err.println("invalid generator.tType: " + generator.tType);
				System.exit(0);
			}			
			tBucketWeight = temp / timeDecayFactor;
			generator.sumTBucketWeight += tBucketWeight;
		}
		else // merge two SA_TBucket into one ROLL_TBucket
		{
			// integrity check
			if(size() > generator.saThreshold)
			{
				System.err.println("size() = " + size() + " , generator.saThreshold = " + generator.saThreshold + " , should not be an instance of SA_TBucket");
				System.exit(0);
			}
			
			HashMap<Integer, IntArrayList> bucketMap = new HashMap<Integer, IntArrayList>();
			int beginNodeId = Math.min(minNodeId, another_SA_TBucket.minNodeId);
			int endNodeId = Math.max(maxNodeId, another_SA_TBucket.maxNodeId);
			for(int node = beginNodeId; node <= endNodeId; node++)
			{
				if(!bucketMap.containsKey(generator.deg[node]))
					bucketMap.put(generator.deg[node], new IntArrayList());
				bucketMap.get(generator.deg[node]).add(node);
			}
			// insert a ROLL_TBucket and remove the SA_TBucket
			ROLL_TBucket mergedTBucket = new ROLL_TBucket(bucketMap, this.generator); // ROLL_TBucket should update its tBucketWeight, timeDecayFactor,... and sumTBucketWeight itself
			generator.sumTBucketWeight -= tBucketWeight;
			generator.sumTBucketWeight -= another_SA_TBucket.tBucketWeight;
			
			// replace this in tBucketList by mergedTBucket
			int tBucketIndex = generator.tBucketList.indexOf(this);
			//System.out.println("tBucketIndex = " + tBucketIndex);
			generator.tBucketList.set(tBucketIndex, mergedTBucket);
		}
	}
}
