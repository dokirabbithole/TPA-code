package sa;

import roulettewheel.AliasMethod;

public class TBucketSA {
	int minNodeId, maxNodeId;
	int maxDeg; // in fact, max weight
	double tBucketWeight;
	double timeDecayFactor;
	SAGenerator saGenerator;
	
	public TBucketSA(int nodeId, SAGenerator saGenerator)
	{
		minNodeId = nodeId;
		maxNodeId = nodeId;
		maxDeg = 0;
		tBucketWeight = maxDeg;
		
		if(saGenerator.tType.equals("power-law") || saGenerator.tType.equals("log-normal"))
			timeDecayFactor = 1;
		else if(saGenerator.tType.equals("exp"))
			timeDecayFactor = Math.pow(Math.E, saGenerator.tExp);
		else{
			System.err.println("invalid saGenerator.tType: " + saGenerator.tType);
			System.exit(0);
		}
		
		this.saGenerator = saGenerator;
	}
	public void addNodeDegreeByOne(int nodeId)
	{
		if(nodeId < minNodeId || nodeId > maxNodeId)
		{
			System.err.println("This tBucket does not contain " + nodeId);
			System.exit(0);
		}
		saGenerator.deg[nodeId] += saGenerator.fit[nodeId]; //
		if(saGenerator.deg[nodeId] > maxDeg)
			maxDeg = saGenerator.deg[nodeId];
		
		double weightInc = saGenerator.fit[nodeId] / timeDecayFactor; //
		tBucketWeight += weightInc;
		saGenerator.sumTBucketWeight += weightInc;
	}
	public int selectNodeBySA()
	{
		int selectedNode;
		while(true)
		{
			selectedNode = minNodeId + saGenerator.random.nextInt(maxNodeId - minNodeId + 1);
			if(saGenerator.random.nextDouble() < (double)saGenerator.deg[selectedNode] / maxDeg)
				return selectedNode;
		}
	}
	public int size()
	{
		return maxNodeId - minNodeId + 1;
	}
	public void mergeTBucket(TBucketSA anotherTBucket)
	{
		// merge anotherTBucket into this tBucket
		minNodeId = Math.min(minNodeId, anotherTBucket.minNodeId);
		maxNodeId = Math.max(maxNodeId, anotherTBucket.maxNodeId);
		maxDeg = Math.max(maxDeg, anotherTBucket.maxDeg);
		
		saGenerator.sumTBucketWeight -= tBucketWeight;
		saGenerator.sumTBucketWeight -= anotherTBucket.tBucketWeight;
		
		if(saGenerator.tType.equals("power-law")){
			tBucketWeight = (tBucketWeight + anotherTBucket.tBucketWeight) * timeDecayFactor;
			timeDecayFactor = Math.pow(size(), saGenerator.tExp);
			tBucketWeight /= timeDecayFactor;
		}else if(saGenerator.tType.equals("log-normal")){
			tBucketWeight = (tBucketWeight + anotherTBucket.tBucketWeight) * timeDecayFactor;
			timeDecayFactor = Math.pow(Math.E, saGenerator.tExp * Math.log(size()) * Math.log(size()));
			tBucketWeight /= timeDecayFactor;
		}else if(saGenerator.tType.equals("exp")){
			tBucketWeight = (tBucketWeight + anotherTBucket.tBucketWeight) * timeDecayFactor;
			timeDecayFactor = Math.pow(Math.E, saGenerator.tExp * size());
			tBucketWeight /= timeDecayFactor;
		}else{
			System.err.println("invalid saGenerator.tType: " + saGenerator.tType);
			System.exit(0);
		}
		saGenerator.sumTBucketWeight += tBucketWeight;		
	}
}