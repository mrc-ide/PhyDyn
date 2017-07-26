package phydyn.distribution;


import java.util.List;
import java.util.Random;

import beast.core.Description;
import beast.core.Distribution;
import beast.core.Input;
import beast.core.State;
import beast.core.Input.Validate;
import beast.evolution.tree.TreeInterface;

/*
 * Re-implements TreeDistribution using STreeIntervals instead of plain TreeIntervals.
 * NOTE: However, it's use in its only subclass suggests that only the tree intervals are required.
 */

@Description("Distribution on a tree, typically a prior such as Coalescent or Yule")
public class STreeDistribution extends Distribution {
	
	public Input<TreeInterface> treeInput = new Input<TreeInterface>("tree", "tree over which to calculate a prior or likelihood");
	public Input<STreeIntervals> treeIntervalsInput = new Input<STreeIntervals>("intervals",
	    		"Structured Intervals for a phylogenetic beast tree", Validate.XOR, treeInput);

	    
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
		final STreeIntervals ti = treeIntervalsInput.get();
		if (ti != null) {
			assert ti.isDirtyCalculation();
			return true;
		}
		return treeInput.get().somethingIsDirty();
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
