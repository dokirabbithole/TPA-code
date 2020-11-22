package mixed_nonlinear;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;

import com.google.common.collect.TreeMultiset;

public class ROLL_TBucket extends TBucket{
	// double tBucketWeight; // inherited
	// GraphGenerator2 generator; // inherited
	double timeDecayFactor; //
	int size; // size of node set
	protected TreeNode root = new TreeNode(true);
	protected Int2ObjectOpenHashMap<Bucket> buckets = new Int2ObjectOpenHashMap<Bucket>();
	
	public ROLL_TBucket(GraphGenerator2 generator)
	{
		// this method should never been invoked
		System.err.println("ROLL_TBucket(GraphGenerator2 generator): this method should never been invoked");
		
		root = new TreeNode(true);
		this.generator = generator;
		
		tBucketWeight = 0;
		if(generator.tType.equals("power-law") || generator.tType.equals("log-normal"))
			timeDecayFactor = 1;
		else if(generator.tType.equals("exp"))
			timeDecayFactor = Math.pow(Math.E, generator.tExp);
		else{
			System.err.println("invalid generator.tType: " + generator.tType);
			System.exit(0);
		}
		size = 0;
	}
	public void addNode(int nodeId, int nodeDegree)
	{
		TreeNode currentNode = root;
		Bucket bucket = buckets.get(nodeDegree);
		if(bucket != null)
		{
			bucket.addNode(nodeId, generator.rollNodeIndex);
			updateTree(bucket.correspondingTreeNode.getParent()); // bucket.correspondingTreeNode is a leaf node and does not need to store weight
			return;
		}
		// insert a new bucket
		bucket = new Bucket(nodeDegree); // empty
		bucket.exp = generator.dExp; // modify here
		bucket.addNode(nodeId, generator.rollNodeIndex);
		buckets.put(bucket.getDegree(), bucket);
		TreeNode newTreeNode = new TreeNode(bucket);
		while(true) // while it is not a leaf
		{
			if(currentNode.isDataNode())
			{
				TreeNode midNode = new TreeNode(false);
				midNode.setParent(currentNode.getParent());
				midNode.setLchild(currentNode);
				currentNode.setParent(midNode);
				midNode.setRchild(newTreeNode);
				newTreeNode.setParent(midNode);
				if(midNode.getParent().getRchild() == currentNode)
					midNode.getParent().setRchild(midNode);
				else if(midNode.getParent().getLchild() == currentNode)
					midNode.getParent().setLchild(midNode);
				else
					System.err.println("WHERE DOES THIS CurrentNode came from?");
				updateTree(midNode);
				break;
			}
			if(currentNode.getLchild() == null)
			{
				currentNode.setLchild(newTreeNode);
				newTreeNode.setParent(currentNode); // update later
				break;
			}
			if(currentNode.getRchild() == null)
			{
				currentNode.setRchild(newTreeNode);
				newTreeNode.setParent(currentNode); // update later
				break;
			}
			// else select the child with lower weight
			if(currentNode.getLchildWeight() <= currentNode.getRchildWeight())
				currentNode = currentNode.getLchild();
			else
				currentNode = currentNode.getRchild();
		}
		updateTree(newTreeNode);
	}
	public void updateTree(TreeNode treeNode)
	{
		// if treeNode.isDataNode(), children's weight = 0
		treeNode.setWeight(treeNode.getLchildWeight() + treeNode.getRchildWeight());
		if(treeNode.getParent() != null)
			updateTree(treeNode.getParent());
	}
	public void updateRouletteWheel(int nodeID, int nodeIndex, int degreeBefore, int degreeAfter)
	{
		// 注意：removeBucket和addNode只应该在本方法中调用
		Bucket oldBucket = buckets.get(degreeBefore);
		oldBucket.removeNodeAt(nodeIndex, generator.rollNodeIndex);
		updateTree(oldBucket.correspondingTreeNode.getParent());
		
		if(oldBucket.getSize() == 0)
			removeBucket(oldBucket);
		
		addNode(nodeID, degreeAfter);
	}
	protected void removeBucket(Bucket oldBucket)
	{
		buckets.remove(oldBucket.getDegree());
		TreeNode oldNode = oldBucket.correspondingTreeNode;
		TreeNode father = oldNode.getParent();
		TreeNode sibling = null;
		if(father.getLchild() == oldNode)
		{
			sibling = father.getRchild();
			if(father.isRoot())
				father.setLchild(null); // never decreasing the tree height
		}
		else if(father.getRchild() == oldNode)
		{
			sibling = father.getLchild();
			if(father.isRoot())
				father.setRchild(null); // never decreasing the tree height
		}
		else
			System.err.println("Sibling is a motherfucker."); //It should not happen, just an integrity check.
		
		if(!father.isRoot())
		{
			TreeNode grandFather = father.getParent();
			if(grandFather.getLchild() == father)
				grandFather.setLchild(sibling);
			else if(grandFather.getRchild() == father)
				grandFather.setRchild(sibling);
			sibling.setParent(grandFather);
		}
		updateTree(father); // very careful here, should be equivalent to updateTree(sibling)
	}
	public int sampleBucket() // sample a specific degree, not a specific nodeID
	{
		TreeNode sampleNode = root;
		do
		{
			double r = generator.random.nextDouble();
			if( r < 1.0 * sampleNode.getLchildWeight() / (sampleNode.getLchildWeight() + sampleNode.getRchildWeight()) )
				sampleNode = sampleNode.getLchild();
			else
				sampleNode = sampleNode.getRchild();
		}while(!sampleNode.isDataNode());
		return sampleNode.getBucket().getDegree();
	}
	public Map<Integer, Bucket> getBuckets() {
		return buckets;
	}
	public void printDegreeDistribution(){
		for(int deg : buckets.keySet())
			System.out.println(deg + "\t" + buckets.get(deg).getWeight());
	}
	public TreeNode getRoot() {
		return root;
	}
	
	// now overriding methods of TBucket
	public int size()
	{
		return size; // size is not defined yet
	}
	public int[] selectNodeInTBucket()
	{
		int sampledDegree = sampleBucket();
		int selectedNodeIndex = generator.random.nextInt(buckets.get(sampledDegree).getSize());
		int selectedNode = buckets.get(sampledDegree).getNodeAt(selectedNodeIndex);
		int[] returnArray = new int[2];
		returnArray[0] = selectedNode;
		returnArray[1] = selectedNodeIndex;
		//System.out.println("ROLL_TBucket.selectNodeInTBucket(): selectedNode = " + selectedNode + " , selectedNodeIndex = " + selectedNodeIndex + " , list_sz = " + buckets.get(sampledDegree).getSize());
		return returnArray;
	}
	public void addNodeDegreeByOne(int nodeId, int nodeIndex, int degreeBefore, int degreeAfter)
	{
		// integrity check
		//if(degreeAfter - degreeBefore != 1)
			//System.err.println("degreeAfter - degreeBefore != 1: " + degreeBefore + " , " + degreeAfter);
		
		//System.out.println("nodeId = " + nodeId + " , nodeIndex = " + nodeIndex);
		// wrapper function for updateRouletteWheel
		updateRouletteWheel(nodeId, nodeIndex, degreeBefore, degreeAfter);
		// update tBucketWeight here, where updateRouletteWheel and addNode is not responsible for weights update
		// size remains unchanged
		//double weightInc = (degreeAfter - degreeBefore) / timeDecayFactor;
		double weightInc = wgtInc(generator.deg[nodeId], generator.fit[nodeId]) / timeDecayFactor; // deg[nodeId] is in fact deg[v] * fit[v]
		tBucketWeight += weightInc;
		generator.sumTBucketWeight += weightInc;
		// still we maintain the deg[] for generator
		generator.deg[nodeId] = degreeAfter;
	}
	private double wgtInc(int oldWeight, int inc)
	{
		return Math.pow(oldWeight + inc, generator.dExp) - Math.pow(oldWeight, generator.dExp);
	}
	//batch insertion to ROLL_TBucket
	public ROLL_TBucket(HashMap<Integer, IntArrayList> bucketMap, GraphGenerator2 generator)
	{
		root = new TreeNode(true);
		this.generator = generator;
		
		tBucketWeight = 0;
		timeDecayFactor = 1; // should be reset later
		size = 0;
		
		Iterator<Integer> degItor = bucketMap.keySet().iterator();
		while(degItor.hasNext())
		{
			int degree = degItor.next(); // degree is in fact deg[v] * fit[v]
			IntArrayList degBucket = bucketMap.get(degree);
			
			addNodes(degBucket, degree);
			
			tBucketWeight += Math.pow(degree, generator.dExp) * degBucket.size();
			size += degBucket.size();
		}
		
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
		tBucketWeight /= timeDecayFactor;
		generator.sumTBucketWeight += tBucketWeight;
	}
	public void addNodes(IntArrayList degBucket, int nodeDegree)
	{
		TreeNode currentNode = root;
		Bucket bucket = buckets.get(nodeDegree);
		if(bucket != null)
		{
			bucket.addNodes(degBucket, generator.rollNodeIndex);
			updateTree(bucket.correspondingTreeNode.getParent()); // bucket.correspondingTreeNode is a leaf node and does not need to store weight
			return;
		}
		// insert a new bucket
		bucket = new Bucket(nodeDegree); // empty
		bucket.exp = generator.dExp;
		bucket.addNodes(degBucket, generator.rollNodeIndex);
		buckets.put(bucket.getDegree(), bucket);
		TreeNode newTreeNode = new TreeNode(bucket);
		while(true) // while it is not a leaf
		{
			if(currentNode.isDataNode())
			{
				TreeNode midNode = new TreeNode(false);
				midNode.setParent(currentNode.getParent());
				midNode.setLchild(currentNode);
				currentNode.setParent(midNode);
				midNode.setRchild(newTreeNode);
				newTreeNode.setParent(midNode);
				if(midNode.getParent().getRchild() == currentNode)
					midNode.getParent().setRchild(midNode);
				else if(midNode.getParent().getLchild() == currentNode)
					midNode.getParent().setLchild(midNode);
				else
					System.err.println("WHERE DOES THIS CurrentNode came from?");
				updateTree(midNode);
				break;
			}
			if(currentNode.getLchild() == null)
			{
				currentNode.setLchild(newTreeNode);
				newTreeNode.setParent(currentNode); // update later
				break;
			}
			if(currentNode.getRchild() == null)
			{
				currentNode.setRchild(newTreeNode);
				newTreeNode.setParent(currentNode); // update later
				break;
			}
			// else select the child with lower weight
			if(currentNode.getLchildWeight() <= currentNode.getRchildWeight())
				currentNode = currentNode.getLchild();
			else
				currentNode = currentNode.getRchild();
		}
		updateTree(newTreeNode);
	}
	public void mergeTBuckets(TBucket anotherTBucket)
	{
		// integrity check
		if(!(anotherTBucket instanceof ROLL_TBucket))
		{
			System.err.println("trying to merge a SA_TBucket and a ROLL_TBucket");
			System.exit(0);
		}
		if(size() != anotherTBucket.size())
		{
			System.err.println("trying to merge two ROLL_TBucket of different size: " + size() + " , " + anotherTBucket.size());
			System.exit(0);
		}
		
		ROLL_TBucket another_ROLL_TBucket = (ROLL_TBucket)anotherTBucket;
		IntIterator anotherItor = another_ROLL_TBucket.buckets.keySet().iterator();
		while(anotherItor.hasNext())
		{
			int degree = anotherItor.nextInt();
			Bucket degBucket = another_ROLL_TBucket.buckets.get(degree); // here is wrong			
			addNodes(degBucket.nodeIDs, degree);
		}
		// update weights
		size *= 2;
		generator.sumTBucketWeight -= tBucketWeight;
		generator.sumTBucketWeight -= another_ROLL_TBucket.tBucketWeight;
		
		double temp = (tBucketWeight + anotherTBucket.tBucketWeight) * timeDecayFactor;
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
		generator.sumTBucketWeight += tBucketWeight; // should have this, no T-Buckets are renewed
	}
}

