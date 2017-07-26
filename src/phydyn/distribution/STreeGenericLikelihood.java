package phydyn.distribution;


import java.util.List;
import java.util.Random;

import org.jblas.DoubleMatrix;

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
import beast.evolution.tree.TreeInterface;
import phydyn.model.PopModelODE;


@Description("Distribution on a structured tree")
public class STreeGenericLikelihood extends Distribution {
	
	/* XML/Beast Input objects */
	//public Input<Tree> treeInput = new Input<Tree>("tree", "tree over which to calculate a prior or likelihood");
	
	
	// removed: calculated from Tree input
	public Input<STreeIntervals> treeIntervalsInput = new Input<STreeIntervals>("treeIntervals",
	  		 "Structured Intervals for a phylogenetic beast tree", Validate.REQUIRED);
	// "Structured Intervals for a phylogenetic beast tree", Validate.XOR, treeInput);
	
	public Input<PopModelODE> popModelInput = new Input<>(
			 "popmodel","Population Model",Validate.REQUIRED);
	
	public Input<RealParameter> NeInput = new Input<>("Ne","Effective Population Size");
	
	/* Type trait that maps taxa to demes in the population model - if type trait is missing, the program assumes
	 * that taxa names are suffixed with deme names or numbers e.g taxaname_I0 or taxaname_0 */
	/* It is also possible the tree contains a typeTrait TraitSet */
	 public Input<TraitSet> typeTraitInput = new Input<>(
	            "typeTrait", "Type trait set maps taxa to state number.");
	 public Input<BooleanParameter> useStateNameInput = new Input<>(
	    		"useStateName",
	            "whether to use a state's name or number when extracting type annotation or reading trait value (default true)");
	 
	 public PopModelODE popModel;
	 //public TreeInterface tree;
	 public Tree tree;
	 public STreeIntervals intervals;
	 	 
	 public int numStates;
	 	 
	 protected int[] nodeNrToState;
	 
	 private boolean traitInput = false;
	 private boolean useStateName = true;
	 
	 public StateProbabilities stateProbabilities; // initialised by subclasses
	 public double Ne;
	  
	 
	 @Override
	 public void initAndValidate() {
		 popModel = popModelInput.get();
		 //if (treeIntervalsInput.get() == null)
		 //    throw new Exception("Expected treeIntervals to be specified");
		 
		 intervals = treeIntervalsInput.get();
		 
		 tree = intervals.treeInput.get();
		 
		 if (typeTraitInput.get() != null) traitInput = true;
		 if (useStateNameInput.get() != null) {
			 useStateName = useStateNameInput.get().getValue();
		 }	
		     	
		 numStates = popModel.getNumStates(); 
		 mapNodesToStates();
	   	    	    
	 }
	 
	 public void initValues() {

		 tree = intervals.treeInput.get();
		 		 
		 if (typeTraitInput.get() != null) traitInput = true;
		 if (useStateNameInput.get() != null) {
			 useStateName = useStateNameInput.get().getValue();
		 }	
		     	
		 numStates = popModel.getNumStates(); 
		 mapNodesToStates();
	 }
	 
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
				 //System.out.println("TraitInput");
				 if (useStateName) {
					 sampleState = popModel.getStateFromName(typeTraitInput.get().getStringValue(node.getID()) );
				 } else {
					 sampleState = (int) typeTraitInput.get().getValue(node.getID());  // should be a number
				 }
			 } else {
				/* state number is encoded in node id after last underscore */
				//System.out.println("Encoded:"+ node.getID());
				String[] splits = node.getID().split("_");
				stateName = splits[splits.length-1];
				if (useStateName) {
					// e.g I0 maps to 0, its state number 
					sampleState = popModel.getStateFromName(stateName);
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
