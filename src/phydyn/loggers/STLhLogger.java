package phydyn.loggers;

import java.io.PrintStream;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Loggable;
import beast.core.Input.Validate;
import phydyn.distribution.STreeLikelihood;
import phydyn.distribution.STreeLikelihoodLogs;
import phydyn.model.PopModelODE;

public class STLhLogger extends CalculationNode implements Loggable {
	
	final public Input<STreeLikelihood> stlhInput =  new Input<>("stlh",
	        "Structured Tree Likelihood to log.", Validate.REQUIRED);
	
	private STreeLikelihood stlh;

	@Override
	public void initAndValidate() {
		stlh = stlhInput.get();
		stlh.setLogLikelihood(true);
	}

	@Override
	public void init(PrintStream out) {
		out.print("node");
		out.print("\t"+"intervalLogLh");
		out.print("\t"+"coalLogLh");
		out.print("\t"+"accumLogLh");
	}

	@Override
	public void log(int sample, PrintStream out) {
		// get logged values from stlh
		final STreeLikelihoodLogs logs = stlh.getSTLhLogs();
		final int n = logs.nodes.length;
		// print first line
		out.println(logs.nodes[0]+"\t"+logs.interval[0]+"\t"+logs.coal[0]+"\t"+logs.accum[0]);
		for(int i=1; i<n; i++) {
			out.println(sample+"\t"+logs.nodes[i]+"\t"+logs.interval[i]+"\t"+logs.coal[i]+"\t"+logs.accum[i]);
		}
		// print them
	}
	
	@Override
	public void close(PrintStream out) {
		// TODO Auto-generated method stub

	}

}
