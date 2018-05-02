package phydyn.loggers;

import java.io.PrintStream;

import org.jblas.DoubleMatrix;

import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Loggable;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import phydyn.distribution.STreeGenericLikelihood;
import phydyn.distribution.StateProbabilities;
import phydyn.util.DVector;

public class STreeRootLogger extends Tree implements Loggable {
	
	 public Input<Tree> treeInput = new Input<Tree>("tree", "tree to be logged", Validate.REQUIRED);
	 public Input<STreeGenericLikelihood> densityInput = new Input<STreeGenericLikelihood>(
				"density",
				"density / structured tree likelihood");
	 
	 int numStates = 0;
	 
	public void initAndValidate() {
		numStates = densityInput.get().numStates;
	}

	
	/**
     * write header information, e.g. labels of a parameter,
     * or Nexus tree preamble
     *
     * @param out log stream
     * @throws Exception
     */
    public void init(PrintStream out)  {
    	out.print("Sample\t");
    	for(int i=0; i < numStates; i++)
    		out.print("state"+i+"\t");
    	// out.println("");
    }

    /**
     * log this sample for current state to PrintStream,
     * e.g. value of a parameter, list of parameters or Newick tree
     *
     * @param nSample chain sample number
     * @param out     log stream
     */
    public void log(int nSample, PrintStream out) {
    	out.print(nSample+"\t");
    	Tree tree = (Tree)treeInput.get().getCurrent();
    	Node root = tree.getRoot();
    	StateProbabilities sp = densityInput.get().getStateProbabilities();
    	DVector probs = sp.getRootProbs();
    	if (probs != null) {
    		for(int i=0; i < numStates; i++)
    			out.print(probs.get(i)+"\t");
    	} else {
    		for(int i=0; i < numStates; i++)
    			out.print(0.00+"\t");
    	}
    	// out.println("");
    }

    /**
     * close log. An end of log message can be left (as in End; for Nexus trees)
     *
     * @param out log stream
     */
    public void close(PrintStream out) {
    	// last line null
    }

}
