package mixed_nonlinear;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/*
 * ROLL
 */

public class Bucket {
	IntArrayList nodeIDs;
	private int degree;
	public double exp;
	public TreeNode correspondingTreeNode = null;
	
	@Override
	public String toString() {
		String result = "(D:" + degree + ", S:" + nodeIDs.size() + ")"; 
		return result;
	}

	public Bucket(int bdegree) {
		degree = bdegree;
		nodeIDs = new IntArrayList();
	}
	
	public int getDegree() {
		return degree;
	}
	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	public double getWeight(){
		return Math.pow(degree, exp) * nodeIDs.size();
	}
	
	public int getSize(){
		return nodeIDs.size();
	}
	/*
	public void addNode(int newNodeID){
		nodeIDs.add(newNodeID);
	}
	*/
	public void addNode(int newNodeID, int[] rollNodeIndex){
		nodeIDs.add(newNodeID);
		// update rollNodeIndex
		rollNodeIndex[newNodeID] = nodeIDs.size() - 1;
	}
	
	public void removeNodeAt(int index, int[] rollNodeIndex){
		nodeIDs.set(index, nodeIDs.get(nodeIDs.size() - 1));
		rollNodeIndex[nodeIDs.get(nodeIDs.size() - 1)] = index; // update rollNodeIndex
		nodeIDs.remove(nodeIDs.size() - 1);		
	}
	
	public int getNodeAt(int index){
		return nodeIDs.get(index);
	}
	
	// batch insertion
	public void addNodes(IntArrayList l, int[] rollNodeIndex)
	{
		//nodeIDs.addAll(l);
		int shift = nodeIDs.size();
		for(int i = 0; i < l.size(); i++)
		{
			nodeIDs.add(l.get(i));
			rollNodeIndex[l.get(i)] = shift + i;
		}
	}
}

