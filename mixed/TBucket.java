package mixed;

public class TBucket {
	double tBucketWeight = 0;
	GraphGenerator generator;
	/*
	public void addNodeDegreeByOne(int nodeId)
	{
		// Stub. Do nothing.
		// This is for SA_TBucket
	}
	*/
	/*
	public void addNodeDegreeByOne(int nodeId, int nodeIndex)
	{
		// Stub. Do nothing.
		// This is for ROLL_TBucket, where the node index is needed.
	}
	*/
	public void addNodeDegreeByOne(int nodeId, int nodeIndex, int degreeBefore, int degreeAfter)
	{
		// Stub. Do nothing.
		// For SA_TBucket, only nodeId is needed.
		// For ROLL_TBucket, node index is needed.
	}
	public int[] selectNodeInTBucket()
	{
		return null; // Stub. Do nothing.
	}
	public int size()
	{
		return 0; // Stub. Do nothing.
	}
	public void mergeTBucket(TBucket anotherTBucket)
	{
		// Stub. Do nothing.
	}
}
