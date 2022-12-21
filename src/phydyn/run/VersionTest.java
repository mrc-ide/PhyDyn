package phydyn.run;

/*
 * Compares solveQL and solvePL
 */

import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.evolution.alignment.TaxonSet;
import beast.base.evolution.tree.TraitSet;
import beast.base.evolution.tree.Tree;
import beast.base.inference.Runnable;
import phydyn.distribution.STreeLikelihood;
import phydyn.evolution.tree.coalescent.STreeIntervals;

public class VersionTest extends Runnable {
	
	//public Input<STreeGenericLikelihood> generalInput = new Input<STreeGenericLikelihood>("general","General likelihood",Validate.REQUIRED);
	public Input<STreeLikelihood> stlikelihoodInput = new Input<STreeLikelihood>("stlikelihood","General likelihood",Validate.REQUIRED);
	
	protected Tree tree;
	protected TaxonSet taxa;
	protected TraitSet trait;
	protected STreeIntervals intervals;
	
	private STreeLikelihood stlh;
	
	@Override
	public void initAndValidate(){
		System.out.println("Structured tree likelihood");
		stlh = stlikelihoodInput.get();
	}

	@Override
	public void run() throws Exception {
		long startTime = System.nanoTime();
		stlh.calculateLogP();
		System.out.println("logP="+stlh.getCurrentLogP());
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000000;
		System.out.println("Time:"+duration);
		
		/* Tree.printTranslate(root, System.out, nnodes);
		Tree.printTaxa(root, System.out, nnodes); */
		/* NEXUS */
		/* tree.init(System.out);
		System.out.println();
		tree.log(10, System.out); */
		
		
		/* Intervals Manipulation */
		/*
		int numSamples = intervals.getSampleCount();
		int numIntervals = intervals.getIntervalCount();
		int interval;
		double duration = intervals.getTotalDuration();
		System.out.println("Intervals: -- duration "+duration);
		for(interval=0; interval<numIntervals;interval++) {
			System.out.print(intervals.getInterval(interval)+" ");
			System.out.print(intervals.getIntervalType(interval));
			List<Node> incomingLines = intervals.getLineagesAdded(interval);
			System.out.print(" added: ");
			for(Node node: incomingLines){
				System.out.print(node.getNr()+"[" + node.getID() +"] "+node.getDate());
			}
			List<Node> outgoingLines = intervals.getLineagesRemoved(interval);
			System.out.print(" removed: ");
			if (outgoingLines != null) {
				for(Node node: outgoingLines){
					System.out.print(node.getNr()+" ");
				}
			}
			System.out.println(" ");
		}
		*/
		
		/* Code useful for compiling java code on the fly */
		/*
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String fileToCompile = "MyClass.java";
		if (compiler==null) {
			System.out.println("Couldn,t find compiler");
		} else {
			System.out.println("Found compiler");
			System.out.println(compiler.toString());
			int compilationResult = compiler.run(null, null, null, fileToCompile);		
		}
		*/
		/* followed by: load class, instantiate object, getMethod
		 * and invoke method.
		 * It's also possible to compile from string using SimpleJavaFileObject
		 * check: http://www.beyondlinux.com/2011/07/20/3-steps-to-dynamically-compile-instantiate-and-run-a-java-class/
		 */
		
		//System.out.println("\nTaxa format ");
		//for (String name: taxanames) {
		//	//<taxon id='x' spec='Taxon'/>
		//	System.out.print("<taxon id=\'"+name+"\' spec=\'Taxon\'/>");
		//}
		//System.out.println(" ");
		
	}

}
