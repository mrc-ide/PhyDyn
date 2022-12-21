package phydyn.run;


import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.evolution.alignment.TaxonSet;
import beast.base.evolution.tree.Node;
import beast.base.evolution.tree.Tree;
import beast.base.inference.Runnable;
import phydyn.evolution.tree.coalescent.STreeIntervals;

import java.util.Set;


public class TreeIntervalTest extends Runnable {
	
	
	public Input<Tree> treeInput = new Input<Tree>("tree","Tree: main Input",Validate.REQUIRED);
	
	public Input<STreeIntervals> intervalsInput = new Input<STreeIntervals>("intervals","Structured tree intervals",Validate.REQUIRED);
	
	protected Tree tree;
	
	protected STreeIntervals intervals;
	

	
	
	@Override
	public void initAndValidate()  {
			
		tree = treeInput.get();

		intervals = intervalsInput.get();

	}

	@Override
	public void run() throws Exception {
		
		/* Inspect Tree using TreeInterface */
		int ntips, ninternal, nnodes;
		ntips = tree.getLeafNodeCount();
		ninternal = tree.getInternalNodeCount();
		nnodes = tree.getNodeCount();
		System.out.println("ID : "+tree.getID());
		System.out.println("tips: "+ntips+" internal: "+ninternal);
		System.out.println("total: "+ nnodes + " check: "+ (ntips+ninternal));
		
		Node root = tree.getRoot();
		System.out.println("root: "+root.getNr());
		
		/* Visit Nodes  */
		System.out.println("\nNodes postorder:");
		Node[] nodes = tree.listNodesPostOrder(null, null);
		/* Node[] nodes = tree.getNodesAsArray(); */
		/* List<Node> nodes = root.getAllChildNodes(); */
		for(Node node: nodes) {
			System.out.print(node.isLeaf() ? "Leaf     " : "Internal ");
			System.out.print("Nr: "+node.getNr()+ " id:"+node.getID()+" h:"+Math.round(node.getHeight()*100)/100.0);
			Node parent = node.getParent();	
			System.out.print(" toParent " + node.getLength());
			System.out.print(" parent: ");
			if (parent==null) System.out.println("root");
			else System.out.println(parent.getNr());
		}
		
		/* Inspect Taxon set */
		TaxonSet taxon = tree.getTaxonset();
		Set<String> taxanames = taxon.getTaxaNames();
		System.out.print("\ntaxanames[ ");
		for (String name: taxanames) {
			System.out.print(name+" ");
		}
		System.out.println("]");
		
		//String newick = root.toNewick(false);
		//System.out.println(newick);
		
		/* The tree */
		System.out.println("Tree ID="+tree.getID());
		
		
		/* Intervals Manipulation */
		
		intervals.printIntervals();
		

		
	}

}
