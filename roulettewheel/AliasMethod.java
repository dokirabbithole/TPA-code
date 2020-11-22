package roulettewheel;

import java.util.ArrayList;
import java.util.HashMap;

public class AliasMethod {
	double[] probability;
	int[] alias;
	double error = Math.pow(10, -14);
	public AliasMethod(double[] p)
	{
		double sumP = 0;
		for(int i = 0; i < p.length; i++)
			sumP += p[i];
		
		probability = new double[p.length];
		alias = new int[p.length];
		for(int i = 0; i < alias.length; i++)
		{
			probability[i] = p[i] / sumP * p.length;
			alias[i] = i;
		}
		createAliasTable();
	}
	public int sampleIndex()
	{
		int pos = (int)(Math.random() * alias.length);
		double random = Math.random();
		if(random < probability[pos])
			return pos;
		else
			return alias[pos];
	}
	private void createAliasTable()
	{
		ArrayList<Integer> highList = new ArrayList<Integer>();
		ArrayList<Integer> lowList = new ArrayList<Integer>();
		for(int i = 0; i < probability.length; i++)
		{
			if(probability[i] > 1.0)
				highList.add(i);
			else if(probability[i] < 1.0)
				lowList.add(i);
		}
//		System.out.println("high list:");
//		for(int i = 0; i < highList.size(); i++)
//			System.out.println(highList.get(i) + " , " + probability[highList.get(i)]);
//		System.out.println("low list:");
//		for(int i = 0; i < lowList.size(); i++)
//			System.out.println(lowList.get(i) + " , " + probability[lowList.get(i)]);
		while(!lowList.isEmpty())
		{
//			System.out.println("lowList.size() = " + lowList.size() + " , " + "highList.size() = " + highList.size());
			if(highList.size() == 0)
				break;
			int highIndex = highList.get(0);
			int lowIndex = lowList.get(0);
//			System.out.println("highIndex = " + highIndex + " , probability[highIndex] = " + probability[highIndex] + " , lowIndex = " + lowIndex + " , probability[lowIndex] = " + probability[lowIndex]);
			probability[highIndex] -= 1.0 - probability[lowIndex];
			alias[lowIndex] = highIndex;
//			System.out.println("highIndex = " + highIndex + " , probability[highIndex] = " + probability[highIndex] + " , lowIndex = " + lowIndex + " , probability[lowIndex] = " + probability[lowIndex]);
			lowList.remove(0);
			if(Math.abs(probability[highIndex] - 1.0) <= error)
			{
				highList.remove(0);
			}
			else if(probability[highIndex] < 1.0)
			{
				highList.remove(0);
				lowList.add(highIndex);
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double sumP = 0;
		double[] p = new double[30];
		for(int i = 0; i < p.length; i++)
		{
			p[i] = Math.random();
//			p[i] = Math.pow(Math.E, -1 * (i+1));
//			p[i] = Math.pow(Math.E, -3 * Math.log(i+1) * Math.log(i+1));
			sumP += p[i];
		}
		
		AliasMethod testAlias = new AliasMethod(p);
		HashMap<Integer, Integer> sampleFreq = new HashMap<Integer, Integer>();
		int numTest = 10000000;
		for(int i = 0; i < numTest; i++)
		{
			int id = testAlias.sampleIndex();
			if(!sampleFreq.containsKey(id))
				sampleFreq.put(id, 1);
			else
				sampleFreq.put(id, sampleFreq.get(id) + 1);
		}
		for(int i = 0; i < p.length; i++)
		{
			if(sampleFreq.containsKey(i))
				System.out.println(p[i] + "\t\t\t\t\t" + p[i]/sumP + "\t\t\t\t\t" + sampleFreq.get(i) * 1.0 / numTest);
			else
				System.out.println(p[i] + "\t\t\t\t\t" + p[i]/sumP + "\t\t\t\t\t" + 0);
		}
	}

}
