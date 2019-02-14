package phydyn.run;


import beast.core.Runnable;
import beast.core.StateNode;
import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.evolution.tree.Tree;
import phydyn.distribution.STreeIntervals;
import beast.evolution.tree.Node;
import beast.evolution.tree.TraitSet;
import beast.evolution.alignment.Taxon;
import beast.evolution.alignment.TaxonSet;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;


public class BasicTest extends Runnable {
	
	public Input<List<Integer>> aInput =  new Input<List<Integer>>("a","Example of Input List parameters",new ArrayList<Integer>());
	public Input<Double> bInput =  new Input<Double>("b","Example of input double parameters",1.0);
	
	public Input<Tree> treeInput = new Input<Tree>("tree","Tree: main Input",Validate.REQUIRED);
	public Input<TaxonSet> taxaInput = new Input<TaxonSet>("taxa","List of taxa names",Validate.REQUIRED);
	public Input<TraitSet> traitInput = new Input<TraitSet>("trait",
			"Trait: mapping of taxa names to values",Validate.REQUIRED);
	public Input<STreeIntervals> intervalsInput = new Input<STreeIntervals>("intervals","Structured tree intervals",Validate.REQUIRED);
	
	protected Tree tree;
	protected TaxonSet taxa;
	protected TraitSet trait;
	protected STreeIntervals intervals;
	

	
	
	@Override
	public void initAndValidate()  {
		System.out.println("Initializing...");
		List<Integer> aParam = aInput.get();
		Double bParam = bInput.get();
		
		System.out.print(aParam);
		System.out.println(" / "+bParam);
		
		tree = treeInput.get();
		taxa = taxaInput.get();
		trait = traitInput.get();
		intervals = intervalsInput.get();
	}

	@Override
	public void run() throws Exception {
		System.out.println("running...");
		String msg;
		
		/* Inspect Tree using TreeInterface */
		int ntips, ninternal, nnodes;
		ntips = tree.getLeafNodeCount();
		ninternal = tree.getInternalNodeCount();
		nnodes = tree.getNodeCount();
		System.out.println("ID : "+tree.getID());
		System.out.println("tips: "+ntips+" internal: "+ninternal);
		System.out.println("total: "+ nnodes + " check: "+ (ntips+ninternal));
		
		Node root = tree.getRoot();
		System.out.println(root.getNr());
		
		/* Visit Nodes  */
		Node[] nodes = tree.listNodesPostOrder(null, null);
		/* Node[] nodes = tree.getNodesAsArray(); */
		/* List<Node> nodes = root.getAllChildNodes(); */
		for(Node node: nodes) {
			System.out.print(node.isLeaf() ? "Leaf     " : "Internal ");
			System.out.print("Nr: "+node.getNr()+ " id:"+node.getID()+" h:"+node.getHeight());
			System.out.println(" toParent " + node.getLength()+" meta: "+node.metaDataString);
		}
		
		/* Inspect Taxon set */
		TaxonSet taxon = tree.getTaxonset();
		Set<String> taxanames = taxon.getTaxaNames();
		System.out.print("taxanames[ ");
		for (String name: taxanames) {
			System.out.print(name+" ");
		}
		System.out.println("]");
		
		//String newick = root.toNewick(false);
		//System.out.println(newick);
		
		/* The tree */
		System.out.println("Tree ID="+tree.getID());
		List<String> taxaList =taxa.asStringList();
		System.out.println("Trait Name: "+trait.getTraitName());
		msg = "";
		for(String taxonStr: taxaList) {
			msg += "{"+taxonStr+"="+trait.getValue(taxonStr) + trait.getStringValue(taxonStr)+"} ";
		}
		System.out.println("Trait values: "+msg);
		/* Tree.printTranslate(root, System.out, nnodes);
		Tree.printTaxa(root, System.out, nnodes); */
		/* NEXUS */
		/* tree.init(System.out);
		System.out.println();
		tree.log(10, System.out); */
		
		
		/* Intervals Manipulation */
		//int numSamples = intervals.getSampleCount();   -- internal nodes
		int numIntervals = intervals.getIntervalCount();
		int interval;
		double duration = intervals.getTotalDuration();
		System.out.println("Intervals: -- duration "+duration);
		for(interval=0; interval<numIntervals;interval++) {
			System.out.print(intervals.getIntervalLength(interval)+" ");
			System.out.print(intervals.getIntervalType(interval));
			
			Node event = intervals.getEvent(interval);		
			System.out.print(" added: ");
			System.out.print(event.getNr()+"[" + event.getID() +"] "+event.getDate());
			
			List<Node> outgoingLines = event.getChildren();
			
			System.out.print(" removed: ");
			if (outgoingLines != null) {
				for(Node node: outgoingLines){
					System.out.print(node.getNr()+" ");
				}
			}
			System.out.println(" ");
		}
		
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
