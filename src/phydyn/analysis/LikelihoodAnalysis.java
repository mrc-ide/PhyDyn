package phydyn.analysis;

import beast.base.evolution.tree.Node;
import beast.base.evolution.tree.TraitSet;
import beast.base.evolution.tree.TreeParser;
import phydyn.distribution.STreeLikelihoodODE;
import phydyn.evolution.tree.coalescent.STreeIntervals;
import phydyn.model.PopModel;
import phydyn.run.LikelihoodOut;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Igor Siveroni
 * PhyDyn analysis class. Considers the following analyses types:
 * - Likelihood, modelmcmc, treemcmc.
 * One of the main objectives of this class is to produce a Runnable object that can be 
 * used to generate a BEAST XML file.
 */


public class LikelihoodAnalysis extends Analysis {
	
	public enum AType { LH, FTMCMC, TMCMC};
	
	public AType analysisType;
	public PopModel popModel;
	public String popModelName;
	
	// tree stuff
	private TreeParser tree;
	private int numTips;
	private String[] tipNames;
	private Node[] tipNodes;
	private double treeHeight;
	private double mrTipDate; // most recent date 
	
	
	
	public LikelihoodAnalysis(PopModel m) {
		super(m);
		analysisType = AType.LH;
		popModel = m;
		popModelName = m.getName();

	}
	

	// Create tree from newick string
	public void addTree(String newick, Boolean adjust) {
		 /**
	     * @param newick                a string representing a tree in newick format
	     * @param adjustTipHeights      true if the tip heights should be adjusted to 0 (i.e. contemporaneous) after reading in tree.
	     * @param allowSingleChildNodes true if internal nodes with single children are allowed
	     * @param isLabeled             true if nodes are labeled with taxa labels
	     * @param offset                if isLabeled == false and node labeling starts with x
	     *                              then offset should be x. When isLabeled == true offset should
	     *                              be 1 as by default.
	     */	   
		tree = new TreeParser(newick, adjust, false, true, 0);
		tree.setID(popModelName+".t");
		
		taxa = tree.m_taxonset.get();
		taxa.setID(popModelName+".taxa");
		
		// taxon set is set a the end using tips labels - can we extract it?
		// tree.m_taxonset.get().setID("oeee");  ;
		Node root = tree.getRoot();
		//System.out.println("root nr: "+root.getNr());
		System.out.println("root height: "+root.getHeight());
			
		/* Visit Nodes  */
		Node[] nodes = tree.listNodesPostOrder(null, null);
		/* Node[] nodes = tree.getNodesAsArray(); */
		/* List<Node> nodes = root.getAllChildNodes(); */
		numTips = 0;
		List<String> ids = new ArrayList<String>();
		List<Node> leaves = new ArrayList<Node>();
		for(Node node: nodes) {
			if (node.isLeaf()) {
				//System.out.print(node.isLeaf() ? "Leaf     " : "Internal ");
				ids.add(node.getID());
				leaves.add(node);
				//System.out.println(node.getHeight());
				//System.out.print("Nr: "+node.getNr()+ " id:"+node.getID()+" h:"+node.getHeight());
				//System.out.println(" toParent " + node.getLength()+" meta: "+node.metaDataString);
			}
		}			
		numTips = ids.size();
		tipNames = new String[numTips];
		tipNodes = new Node[numTips];
		ids.toArray(tipNames);
		leaves.toArray(tipNodes);
		ids.clear(); leaves.clear();
		// Dates
		treeHeight = root.getHeight();
		System.out.println("num tips = "+numTips);
		
		
	}
	
	// Create a date trait using information from the tree tips.
	public void addDateTrait() {
		dateTrait = new TraitSet();
		if (popModel.hasEndTime()) {
			mrTipDate = popModel.getEndTime();
			popModel.unsetEndTime();
	    } else {
	    	mrTipDate = treeHeight;
	    }
		// date is extracted from tipname;
		String datePairs = tipNames[0]+"="+(mrTipDate-tipNodes[0].getHeight());
		for(int i = 1; i < numTips; i++) {
			datePairs += ","+tipNames[i]+"="+(mrTipDate-tipNodes[i].getHeight());
		}	
		
		dateTrait.traitNameInput.setValue(TraitSet.DATE_TRAIT, dateTrait);
		dateTrait.traitsInput.setValue(datePairs, dateTrait);
		dateTrait.taxaInput.setValue(taxa, dateTrait);
		dateTrait.initAndValidate();
		dateTrait.setID(popModelName+".dates");
		// add date trait to tree
		
		tree.setDateTrait(dateTrait);
	}
	
	// TypeTrait to be added to stlikelihood. type extracted from tree tips
	public void addTypeTrait() {
		typeTrait = new TraitSet();
		
		String[] splits = tipNames[0].split("_");
		String stateName = splits[splits.length-1];
		String typePairs = tipNames[0]+"="+stateName;
		for(int i = 1; i < numTips; i++) {
			splits = tipNames[i].split("_");
			stateName = splits[splits.length-1];
			typePairs += ","+tipNames[i]+"="+stateName;
		}	
		typeTrait.traitNameInput.setValue("type-trait", typeTrait);
		typeTrait.traitsInput.setValue(typePairs, typeTrait);
		typeTrait.taxaInput.setValue(taxa, typeTrait);
		typeTrait.initAndValidate();
		typeTrait.setID(popModelName+".types");
		
		
	}
	
	public beast.base.inference.Runnable getRunnableObject() {
		if (this.analysisType!=AType.LH)
			return null;
		
		STreeIntervals intervals = new STreeIntervals(tree);
		intervals.setID(popModelName+".intervals");
		STreeLikelihoodODE stlh  = new STreeLikelihoodODE(popModel,intervals,typeTrait);
		stlh.setID(popModelName+".stlh");
		LikelihoodOut lhout = new LikelihoodOut(stlh);
		lhout.setID(popModelName+".stlhout");
		
		return  lhout;
				
		
	
	}
	
}
