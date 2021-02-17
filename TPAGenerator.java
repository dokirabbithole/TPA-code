import baseline.*;
import roulettewheel.*;
import sa.*;
import sa_nonlinear.*;
import mixed.*;
import mixed_nonlinear.*;
import java.io.IOException;
import java.io.File;

public class TPAGenerator {
	void usage(){
		System.out.println("java -Djava.ext.dirs=. TPAGenerator -model <BA/DPA/DPAVary/RW/SA/Mixed> <args>");
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("args of BA: <numOfNodesFinal> <numOfEdgesPerNode>");
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("args of DPA: <numOfNodesFinal> <numOfEdgesPerNode> <tType (type of time decay, \"power-law\" or \"log-normal\")> <tExp (tExp > 0)> <fType (type of fitness, \"poisson\" or \"power-law\" or \"exp\" )> <fExp (fExp > 0)> <fMax>");
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("args of DPAVary: <numOfNodesFinal> <initdType (\"power-law\" or \"poisson\")> <initdExp (initdExp > 0)> <initdMax> <tType> <tExp> <fType> <fExp> <fMax>");
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("args of RW: <numOfNodesFinal> <dType (\"power-law\" or \"logarithmic\")> <dExp (dExp > 0)> <tType> <tExp> <fType> <fExp> <fMax> <initAttract>");
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("args of SA: <numOfNodesFinal> <dType (\"power-law\")> <dExp (dExp > 0)> <tType> <tExp> <fType> <fExp> <fMax> <initAttract>");
		System.out.println("----------------------------------------------------------------------------");
		System.out.println("args of Mixed: <numOfNodesFinal> <dType (\"power-law\")> <dExp (dExp > 0)> <tType> <tExp> <fType> <fExp> <fMax> <initAttract> <saThreshold>");
	}
	void error(String msg){
		System.err.println(msg);
		System.err.println();
		usage();
		System.exit(0);
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TPAGenerator tpaGenerator = new TPAGenerator();
		String outputBase = "graph/";
		File outputDir = new File(outputBase);
		if(!outputDir.exists())
			outputDir.mkdir();
		//tpaGenerator.usage();
		if(args[0].equals("-model")){
			String model = args[1];
			int cnt = 2;
			int numOfNodesFinal = Integer.parseInt(args[cnt++]);
			if(numOfNodesFinal <= 0)
				tpaGenerator.error("numOfNodesFinal <= 0");
			switch(model){
			case "BA":
				// check args
				int numOfEdgesPerNode = Integer.parseInt(args[cnt++]);
				if(numOfEdgesPerNode <= 0)
					tpaGenerator.error("BA: numOfEdgesPerNode <= 0");
				BA baGraph = new BA(numOfNodesFinal, numOfEdgesPerNode);
				baGraph.generateGraph();				
				break;
			case "DPA":
				// check args
				numOfEdgesPerNode = Integer.parseInt(args[cnt++]);
				if(numOfEdgesPerNode <= 0)
					tpaGenerator.error("numOfEdgesPerNode <= 0");
				String tType = args[cnt++];
				if(!tType.equals("power-law") && !tType.equals("log-normal") && !tType.equals("exp"))
					tpaGenerator.error("DPA: invalid tType:" + tType);
				double tExp = Double.parseDouble(args[cnt++]);
				if(tExp <= 0)
					tpaGenerator.error("DPA: invalid tExp:" + tExp);
				String fType = args[cnt++];
				if(!fType.equals("poisson") && !fType.equals("power-law") && !fType.equals("exp"))
					tpaGenerator.error("DPA: invalid fType:" + fType);
				double fExp = Double.parseDouble(args[cnt++]); 
				if(fExp <= 0)
					tpaGenerator.error("DPA: invalid fExp:" + fExp);
				int fMax = Integer.parseInt(args[cnt++]);
				if(fMax <= 0)
					tpaGenerator.error("DPA: invalid fMax:" + fMax);
				DPA dpaGraph = new DPA(numOfNodesFinal, numOfEdgesPerNode, tType, tExp, fType, fExp, fMax);
				dpaGraph.generateGraph();
				break;
			case "DPAVary":
				// check args
				String initdType = args[cnt++];
				if(!initdType.equals("power-law") && !initdType.equals("poisson"))
					tpaGenerator.error("DPAVary: invalid initdType:" + initdType);
				double initdExp = Double.parseDouble(args[cnt++]); 
				if(initdExp <= 0)
					tpaGenerator.error("DPAVary: invalid initdExp:" + initdExp);
				int initdMax = Integer.parseInt(args[cnt++]);
				if(initdMax <= 0)
					tpaGenerator.error("DPAVary: invalid initdMax:" + initdMax);
				tType = args[cnt++];
				if(!tType.equals("power-law") && !tType.equals("log-normal") && !tType.equals("exp"))
					tpaGenerator.error("DPAVary: invalid tType:" + tType);
				tExp = Double.parseDouble(args[cnt++]);
				if(tExp <= 0)
					tpaGenerator.error("DPAVary: invalid tExp:" + tExp);
				fType = args[cnt++];
				if(!fType.equals("poisson") && !fType.equals("power-law") && !fType.equals("exp"))
					tpaGenerator.error("DPAVary: invalid fType:" + fType);
				fExp = Double.parseDouble(args[cnt++]); 
				if(fExp <= 0)
					tpaGenerator.error("DPAVary: invalid fExp:" + fExp);
				fMax = Integer.parseInt(args[cnt++]);
				if(fMax <= 0)
					tpaGenerator.error("DPAVary: invalid fMax:" + fMax);
				DPAVaryInitDegree dpaGraph2 = new DPAVaryInitDegree(numOfNodesFinal, initdType, initdExp, initdMax, tType, tExp, fType, fExp, fMax);
				dpaGraph2.generateGraph();
				break;
			case "RW":
				// check args
				String dType = args[cnt++];
				if(!dType.equals("power-law") && !dType.equals("logarithmic"))
					tpaGenerator.error("RW: invalid dType:" + dType);
				double dExp = Double.parseDouble(args[cnt++]);
				if(dExp <= 0)
					tpaGenerator.error("RW: invalid dExp:" + dExp);
				tType = args[cnt++];
				if(!tType.equals("power-law") && !tType.equals("log-normal") && !tType.equals("exp"))
					tpaGenerator.error("RW: invalid tType:" + tType);
				tExp = Double.parseDouble(args[cnt++]);
				if(tExp <= 0)
					tpaGenerator.error("RW: invalid tExp:" + tExp);
				fType = args[cnt++];
				if(!fType.equals("poisson") && !fType.equals("power-law") && !fType.equals("exp"))
					tpaGenerator.error("RW: invalid fType:" + fType);
				fExp = Double.parseDouble(args[cnt++]); 
				if(fExp <= 0)
					tpaGenerator.error("RW: invalid fExp:" + fExp);
				fMax = Integer.parseInt(args[cnt++]);
				if(fMax <= 0)
					tpaGenerator.error("RW: invalid fMax:" + fMax);
				int initAttract = Integer.parseInt(args[cnt++]);
				if(initAttract <= 0)
					tpaGenerator.error("RW: invalid initAttract:" + initAttract);
				
				if(dType.equals("logarithmic")){
					LogRWModel rwModel = new LogRWModel(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
					String edgeFile = "graph/" + "rw-log-" + numOfNodesFinal + "-" + dType + "-" + rwModel.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
					rwModel.createGraph(edgeFile);
				}
				else{
					RouletteWheelModel rwModel = new RouletteWheelModel(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
					String edgeFile = "graph/" + "rw-" + numOfNodesFinal + "-" + dType + "-" + rwModel.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
					rwModel.createGraph(edgeFile);
				}					
				break;
			case "SA":
				// check args
				dType = args[cnt++];
				if(!dType.equals("power-law"))
					tpaGenerator.error("SA: invalid dType:" + dType);
				dExp = Double.parseDouble(args[cnt++]);
				if(dExp <= 0)
					tpaGenerator.error("SA: invalid dExp:" + dExp);
				tType = args[cnt++];
				if(!tType.equals("power-law") && !tType.equals("log-normal") && !tType.equals("exp"))
					tpaGenerator.error("SA: invalid tType:" + tType);
				tExp = Double.parseDouble(args[cnt++]);
				if(tExp <= 0)
					tpaGenerator.error("SA: invalid tExp:" + tExp);
				fType = args[cnt++];
				if(!fType.equals("poisson") && !fType.equals("power-law") && !fType.equals("exp"))
					tpaGenerator.error("SA: invalid fType:" + fType);
				fExp = Double.parseDouble(args[cnt++]); 
				if(fExp <= 0)
					tpaGenerator.error("SA: invalid fExp:" + fExp);
				fMax = Integer.parseInt(args[cnt++]);
				if(fMax <= 0)
					tpaGenerator.error("SA: invalid fMax:" + fMax);
				initAttract = Integer.parseInt(args[cnt++]);
				if(initAttract <= 0)
					tpaGenerator.error("SA: invalid initAttract:" + initAttract);
				
				if(dExp == 1.0)
				{
					SAGenerator saGen = new SAGenerator(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
					String edgeFile = "graph/" + "sa-" + numOfNodesFinal + "-" + dType + "-" + saGen.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
					saGen.createGraph(edgeFile);
				}
				else
				{
					SAGeneratorNonLinear saGenNonLinear = new SAGeneratorNonLinear(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract);
					String edgeFile = "graph/" + "sa-nl-" + numOfNodesFinal + "-" + dType + "-" + saGenNonLinear.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
					saGenNonLinear.createGraph(edgeFile);
				}
				break;
			case "Mixed":
				// check args
				dType = args[cnt++];
				if(!dType.equals("power-law"))
					tpaGenerator.error("Mixed: invalid dType:" + dType);
				dExp = Double.parseDouble(args[cnt++]);
				if(dExp <= 0)
					tpaGenerator.error("Mixed: invalid dExp:" + dExp);
				tType = args[cnt++];
				if(!tType.equals("power-law") && !tType.equals("log-normal") && !tType.equals("exp"))
					tpaGenerator.error("Mixed: invalid tType:" + tType);
				tExp = Double.parseDouble(args[cnt++]);
				if(tExp <= 0)
					tpaGenerator.error("Mixed: invalid tExp:" + tExp);
				fType = args[cnt++];
				if(!fType.equals("poisson") && !fType.equals("power-law") && !fType.equals("exp"))
					tpaGenerator.error("Mixed: invalid fType:" + fType);
				fExp = Double.parseDouble(args[cnt++]); 
				if(fExp <= 0)
					tpaGenerator.error("Mixed: invalid fExp:" + fExp);
				fMax = Integer.parseInt(args[cnt++]);
				if(fMax <= 0)
					tpaGenerator.error("Mixed: invalid fMax:" + fMax);
				initAttract = Integer.parseInt(args[cnt++]);
				if(initAttract <= 0)
					tpaGenerator.error("Mixed: invalid initAttract:" + initAttract);
				int saThreshold = Integer.parseInt(args[cnt++]);
				if(saThreshold <= 0)
					tpaGenerator.error("Mixed: invalid saThreshold:" + saThreshold);
				
				if(dExp == 1.0){
					GraphGenerator tpaUGraphGen = new GraphGenerator(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract, saThreshold);
					String edgeFile = "graph/" + "mix-" + numOfNodesFinal + "-" + dType + "-" + tpaUGraphGen.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
					tpaUGraphGen.createGraph(edgeFile);
				}
				else{
					GraphGenerator2 tpaUGraphGen2 = new GraphGenerator2(numOfNodesFinal, dType, dExp, tType, tExp, fType, fExp, fMax, initAttract, saThreshold);
					String edgeFile = "graph/" + "mix-nl-" + numOfNodesFinal + "-" + dType + "-" + tpaUGraphGen2.dExp + "-" + tType + "-" + tExp + "-" + fType + "-" + fExp + "-" + initAttract + ".txt";
					tpaUGraphGen2.createGraph(edgeFile);
				}
				break;
			default:
				tpaGenerator.error("invalid args[1]: " + args[1]);
				break;
			}
		}
		else{
			tpaGenerator.error("invalid args[0]: " + args[0]);
		}
	}

}
