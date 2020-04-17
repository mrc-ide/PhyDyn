package phydyn.distribution;


import java.util.List;
import java.util.Random;


import beast.core.Description;
import beast.core.Distribution;
import beast.core.Input;
import beast.core.State;
import beast.core.parameter.BooleanParameter;
import beast.core.parameter.RealParameter;
import beast.core.Input.Validate;
import beast.evolution.tree.Node;
import beast.evolution.tree.TraitSet;
import beast.evolution.tree.Tree;
import beast.evolution.tree.TreeDistribution;
import beast.evolution.tree.coalescent.STreeIntervals;
import beast.evolution.tree.coalescent.TreeIntervals;
import phydyn.model.PopModel;


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
	 * that taxa names are suffixed with deme names or numbers e.g taxaname_I0 or taxaname_0 */
	/* TODO: It is also possible the tree contains a typeTrait TraitSet */
	 public Input<TraitSet> typeTraitInput = new Input<>(
	            "typeTrait", "Type trait set maps taxa to state number.");
	 
	 public Input<Boolean> useStateNameInput = new Input<>(
	    		"useStateName",
	            "whether to use a state's name or number when extracting type annotation or reading trait value (default true)");
	 
	 public Input<Boolean> ancestralInput = new Input<>("ancestral",
				"Compute ancestral states",new Boolean(false));
	 
	 
	 public PopModel popModel;
	 public Tree tree;
	 
	 public STreeIntervals intervals;
	 	 
	 public int numStates;
	 	 
	 protected int[] nodeNrToState;
	 
	 private boolean traitInput = false;
	 private boolean useStateName = true;
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
			 throw new IllegalArgumentException("Trre Intervals must be of class STreeIntervals");
		 }
		 
		 tree = intervals.treeInput.get();
		 
		 if (typeTraitInput.get() != null) traitInput = true;
		 if (useStateNameInput.get() != null) {
			 useStateName = useStateNameInput.get();
		 }	
		 computeAncestral = ancestralInput.get();
		 numStates = popModel.getNumStates(); 
		 
		 System.out.println("PhyDyn v1.3.6 - patch 6 - forceRecalculation");
		 popModel.printModel();
	 }
	 
	 public boolean initValues() {
		 mapNodesToStates();
		 return false;
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
		 for(Node node : tree.getExternalNodes()) {
			 /* assumption: node.nr < numNodes */
			 final int nr = node.getNr();
			 if (traitInput) { /* use type trait and extract state number */
				 if (useStateName) {
					 sampleState = popModel.getStateFromName(typeTraitInput.get().getStringValue(node.getID()) );
				 } else {
					 // important: if trait values are, by mistake, deme names, then
					 // the sampleState will be zero (parseDouble)
					 // may want to check for valid state strings
					 sampleState = (int) typeTraitInput.get().getValue(node.getID());  // should be a number -- this doesn't make sense anymore
				 }
			 } else {
				/* state number is encoded in node id after last underscore */
				//System.out.println("Encoded:"+ node.getID());
				String[] splits = node.getID().split("_");
				stateName = splits[splits.length-1];
				if (useStateName) {
					// e.g I0 maps to 0, its state number 
					sampleState = popModel.getStateFromName(stateName);  // flag incorrect stateName ie when idx is -1
				} else {	
					sampleState = Integer.parseInt(stateName);
				}
			 }
			 if ((sampleState >= numStates)||(sampleState<0)) {
				 throw new RuntimeException("Incorrect state number for taxon "+node.getID()+": "+sampleState+" must be in [0,"+(numStates-1)+"]");
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
