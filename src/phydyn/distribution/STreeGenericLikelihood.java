package phydyn.distribution;


import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.core.Log;
import beast.base.evolution.tree.Node;
import beast.base.evolution.tree.TraitSet;
import beast.base.evolution.tree.Tree;
import beast.base.evolution.tree.TreeDistribution;
import beast.base.inference.State;
import beast.base.inference.parameter.RealParameter;
import phydyn.evolution.tree.coalescent.STreeIntervals;
import phydyn.model.PopModel;

import java.util.List;
import java.util.Random;


@Description("Distribution on a structured tree")
public abstract class STreeGenericLikelihood extends TreeDistribution {
	
	// Igor: Important change - used to be STreeIntervals
	// Required for Beauti template - tree prior needs a Tree or TreeIntervals object
	// unless it's ok if a subclass is used
	
	// already part of TreeDistribution
	//public Input<TreeIntervals> treeIntervalsInput = new Input<TreeIntervals>("treeIntervals",
	//  		 "Structured Intervals for a phylogenetic beast tree", Validate.REQUIRED);
	
	public Input<PopModel> popModelInput = new Input<>(
			 "popmodel","Population Model",Validate.REQUIRED);
	
	public Input<Boolean> forgiveT0Input = new Input<>("forgiveT0",
			"Use Constant Coalescent if root precedes t0",new Boolean(true));
	public Input<RealParameter> NeInput = new Input<>("Ne","Effective Population Size");
	
	/* Type trait that maps taxa to demes in the population model.
	 * If type trait is missing, the program assumes
	 * that taxa names are suffixed with deme names  e.g taxaname_I0  */
	/* TODO: It is also possible the tree contains a typeTrait TraitSet */
	 public Input<TraitSet> typeTraitInput = new Input<>(
	            "typeTrait", "Type trait set maps taxa to state number.");
	 
	 // deprecated: useStateName is always true.
	 public Input<Boolean> useStateNameInput = new Input<>(
	    		"useStateName",
	            "whether to use a state's name or number when extracting type annotation or reading trait value (default true)");
	 
	 public Input<String> splitSymbolInput = new Input<>("splitSymbol","Character used to split taxa id "
	 		+ "in order to extract deme associated with sequence/tip", "_");
	 public Input<Integer> splitIndexInput = new Input<>("splitIndex", "Index used to extract deme from taxa id "
			 + "associated with sequence/tip",-1);
	 
	 public Input<Boolean> ancestralInput = new Input<>("ancestral",
				"Compute ancestral states",new Boolean(false));
	 
	 
	 public boolean logLikelihood = false;
	 public STreeLikelihoodLogs stlhLogs;
	 
	 public PopModel popModel;
	 public Tree tree;
	 
	 public STreeIntervals intervals;
	 	 
	 public int numStates;
	 	 
	 protected int[] nodeNrToState;
	 
	 //private boolean traitInput = false;
	 private TraitSet typeTrait;
	 protected boolean computeAncestral;
	 
	 // the state
	 // double logP; -- defined by Distribution
	 public StateProbabilities stateProbabilities; 
	  
	 
	 @Override
	 public void initAndValidate() {
		 popModel = popModelInput.get();
		 try {
			 intervals = (STreeIntervals) treeIntervalsInput.get();	 
		 } catch (Exception e) {
			 throw new IllegalArgumentException("Tree Intervals must be of class STreeIntervals");
		 }
		 
		 tree = intervals.treeInput.get();
		 
		 typeTrait = null;
		 if (typeTraitInput.get() != null) {
			 typeTrait = typeTraitInput.get();
		 } else {
			 // check inside tree for type trait
			 for(TraitSet trait : tree.m_traitList.get()) {
				 final String traitName = trait.getTraitName().toLowerCase();
				 if (traitName.equals("types") || traitName.equals("type")) {
					 typeTrait = trait;
					 break;
				 }
			 }
		 }
		 
		 
		 if (useStateNameInput.get() != null) {
			 if (!useStateNameInput.get()) {
				 Log.warning("(STreeLikelihood): useStateName option deprecated. Always use state name to annotate tips.");
			 }
		 }
		 if (typeTrait==null) {
			 if (splitIndexInput.get() == 0)
				 throw new IllegalArgumentException("Invalid splitIndex value: 0 not allowed");
			 System.out.println("(PhyDyn) No Type trait provided. Extracting structured population information from taxa Ids");
		 }
		 
		 computeAncestral = ancestralInput.get();
		 numStates = popModel.getNumStates(); 
		 
		 mapNodesToStates();
		 
		 logLikelihood = false;
	 }
	 
	 public boolean initValues() {
		 // mapNodesToStates();
		 return false;
	 }
	 
	 public void setLogLikelihood(boolean b) {
		 logLikelihood = b;
	 }
	 
	 public STreeLikelihoodLogs getSTLhLogs() {
		 if (logLikelihood) return stlhLogs;
		 else return null;
	 }
	 
	 public PopModel getModel() { return popModel; }
	 
	 public StateProbabilities getStateProbabilities() {
		 return stateProbabilities;
	 }
	 
	 /* Let nr be the number assigned to each node of the tree: nr = node.nr
	  * Tip nr's should be between in [0 and numTips>. However, I am not sure if this property
	  * is satisfied by future tree transformations.
	  * I am assuming a weaker property: forall node. <= 0 node.nr < numNodes
	  * Hence, the choice of a bigger array size for nodeNrToState.
	  * */
	 protected void mapNodesToStates()  {
		 int sampleState;
		 String stateName;
		 int numNodes = tree.getNodeCount();
		 nodeNrToState = new int[numNodes];
		 for(int i=0; i<numNodes; i++) {
			 nodeNrToState[i] = -1;
		 }
		 if (numStates==1) { // unstructured population
			 for(Node node : tree.getExternalNodes()) {
				 nodeNrToState[ node.getNr()] = 0; // single deme index
			 }
			 return;
		 }
		 //  num_demes = > 1
		 String splitBy = splitSymbolInput.get();
		 int index = splitIndexInput.get();
		 for(Node node : tree.getExternalNodes()) {
			 /* assumption: node.nr < numNodes */
			 final int nr = node.getNr();
			 if (typeTrait!=null) { /* use type trait and extract state number */
				 stateName = typeTrait.getStringValue(node.getID());
				 // sampleState = (int) typeTraitInput.get().getValue(node.getID()); -- if trait maps indices (deprecated)			 
			 } else {
				/* state number is encoded in node id after last underscore */
				//System.out.println("Encoded:"+ node.getID());
				String[] splits = node.getID().split(splitBy);
				final int idx = (index>0)?(index-1):(splits.length+index);
				if ((idx<0)||(idx>=splits.length)) {
					System.out.println("Error while accessing deme name from tip/taxon: "+node.getID());
					System.out.print("Array index out of bounds error: ");
					System.out.println("splitSymbol='"+splitSymbolInput.get()+"' splitIndex="+splitIndexInput.get());
					throw new IllegalArgumentException("Error while mapping deme name to tip: "+node.getID());
				}
				stateName = splits[idx];
				//if (index>0) {
				//	stateName = splits[index-1];
				//} else {
				//	stateName = splits[splits.length+index];
				//}
				
				// sampleState = Integer.parseInt(stateName); -- if annotated with index (deprecated)
			 }
			 sampleState = popModel.getStateFromName(stateName);
			 if ((sampleState >= numStates)||(sampleState<0)) {
				 System.out.print("Unknown deme name '"+stateName+"' extracted from ");
				 System.out.println("taxa/sequence: "+node.getID());
				 System.out.println("Valid deme names: "+popModel.getDemesString(","));
				 throw new RuntimeException("Error while mapping deme name to tip: "+node.getID());
			 }
			 nodeNrToState[nr] = sampleState;
		 }
		 return;
	 }

	 
	 @Override
	 public List<String> getArguments() {
		 return null;	
	 }

	 @Override
	 public List<String> getConditions() {
		 return null;
	 }


	 @Override
	 protected boolean requiresRecalculation() {
		 // final STreeIntervals ti = treeIntervalsInput.get();
		 final STreeIntervals ti = intervals;  // change this
		 if (ti != null) {
			 assert ti.isDirtyCalculation();
			 return true;
		 }
		 return treeIntervalsInput.get().treeInput.get().somethingIsDirty();
	 }
	    
	 /** Indicate that the tree distribution can deal with dated tips in the tree
	  * Some tree distributions like the Yule prior cannot handle this.
	  * @return true by default
	  */
	 public boolean canHandleTipDates() {
		 return true;
	 }

	 @Override
	 public void sample(State state, Random random) {
		 // TODO Auto-generated method stub
		
	 }	

}
