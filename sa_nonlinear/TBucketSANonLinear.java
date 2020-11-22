package sa_nonlinear;

public class TBucketSANonLinear {
	int minNodeId, maxNodeId;
	int maxDeg; // in fact, max deg[v] * fit[v]
	double tBucketWeight;
	double timeDecayFactor;
	SAGeneratorNonLinear saGenerator;
	
	public TBucketSANonLinear(int nodeId, SAGeneratorNonLinear saGenerator)
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
		
		// double weightInc = saGenerator.fit[nodeId] / timeDecayFactor; //
		double weightInc = wgtInc(saGenerator.deg[nodeId], saGenerator.fit[nodeId]) / timeDecayFactor; // deg[nodeId] is in fact deg[v] * fit[v]
		tBucketWeight += weightInc;
		saGenerator.sumTBucketWeight += weightInc;
	}
	private double wgtInc(int oldWeight, int inc)
	{
		return Math.pow(oldWeight + inc, saGenerator.dExp) - Math.pow(oldWeight, saGenerator.dExp);
	}
	public int selectNodeBySA()
	{
		int selectedNode;
		while(true)
		{
			selectedNode = minNodeId + saGenerator.random.nextInt(maxNodeId - minNodeId + 1);
			if(saGenerator.random.nextDouble() < Math.pow(saGenerator.deg[selectedNode], saGenerator.dExp) / Math.pow(maxDeg, saGenerator.dExp))
				return selectedNode;
		}
	}
	public int size()
	{
		return maxNodeId - minNodeId + 1;
	}
	public void mergeTBucket(TBucketSANonLinear anotherTBucket)
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
