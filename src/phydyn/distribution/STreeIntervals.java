package phydyn.distribution;

import beast.core.Description;
import beast.evolution.tree.coalescent.IntervalType;
import beast.evolution.tree.coalescent.TreeIntervals;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts the intervals from a beast.tree.
 * Extended original beast.evolution.tree.coalescent.TreeIntervals to work with structured coalescent models
 * Extensions taken from StructuredTreeIntervals, MAscot package
 * by David Rasmussen/Nicola Mueller.
 * We have used inheritance instead to avoid code duplication.
 * 
 * TODO: Verify that all extensions are in place
 * TODO: Avoid calling swap() unnecessarily - can we improve TreeIntervals?
 */
@Description("Extracts the intervals from a tree. Points in the intervals " +
        "are defined by the heights of nodes in the tree.")
public class STreeIntervals extends TreeIntervals {
	
	// Added these so can restore when needed for structured models
    protected List<Node>[] storedLineagesAdded;
    protected List<Node>[] storedLineagesRemoved;
    
    public STreeIntervals() {    }
       
    @Override
    public void initAndValidate() {
        super.initAndValidate();
    }

    @Override
    protected void restore() {
    	
        // Added these last two for structured coalescent models
        List<Node>[] tmp4 = storedLineagesAdded;
        storedLineagesAdded = lineagesAdded;
        lineagesAdded = deepCopyListArray( tmp4);
        
        List<Node>[] tmp5 = storedLineagesRemoved;
        storedLineagesRemoved = lineagesRemoved;
        lineagesRemoved = deepCopyListArray(tmp5);
        
        super.restore();
        
    }
    
    
    @Override
    protected void store() {
       
    	// Safe: make copies of lists in array
        storedLineagesAdded = deepCopyListArray(lineagesAdded);
        storedLineagesRemoved = deepCopyListArray(lineagesRemoved);
     
        super.store(); /* restores lineagesCounts and intervals */
    }
    
    @SuppressWarnings("unchecked")
	public List<Node>[] deepCopyListArray(List<Node>[] listArray) {
    	// assertion intervals.length = listarray.length
    	List<Node>[] newList = new List[listArray.length];
    	for (int i = 0; i < intervalCount; i++) {
    		List<Node> nodeList = null;
    		if (listArray[i] != null) {
    			nodeList = new ArrayList<Node>(listArray[i]);		
    		}
    		newList[i] = nodeList;
    	}
    	return newList;   	
    }
    
    
    /**
     * Recalculates all the intervals for the given beast.tree.
     */
    @SuppressWarnings("unchecked")
    protected void calculateIntervals() {
    	
        final int nodeCount = treeInput.get().getNodeCount();
        /* Not needed since we are doing a deep copy now
        if (intervals == null || intervals.length != nodeCount) {          
            storedLineagesAdded = new List[nodeCount];
            storedLineagesRemoved = new List[nodeCount];            
        } else { 
            for (List<Node> l : storedLineagesAdded) {
            	if (l != null) l.clear();
            }
            for (List<Node> l : storedLineagesRemoved) {
                if (l != null) l.clear();
            }
        }
        */
        super.calculateIntervals();
        // force correct order - alternatively, treeIntervals need to be modified
        swap();
    }    
    
    protected void addLineage(int interval, Node node) {
    		super.addLineage(interval, node);
        //if (storedLineagesAdded[interval] == null) storedLineagesAdded[interval] = new ArrayList<Node>();
        //storedLineagesAdded[interval].add(node);
    }
    
    protected void removeLineage(int interval, Node node) {
    		super.removeLineage(interval,  node);;
        //if (storedLineagesRemoved[interval] == null) storedLineagesRemoved[interval] = new ArrayList<Node>();
        //storedLineagesRemoved[interval].add(node);
    }
    
    public List<Node> getLineagesAdded(int i) {
        if (!intervalsKnown) {
            calculateIntervals();
        }
        if (i >= intervalCount) throw new IllegalArgumentException();
        return lineagesAdded[i];
    }
    
    // return node number
    // igor: new version should not use lists
    public int getLineageAdded(int i) {
    	 if (!intervalsKnown) {
             calculateIntervals();
         }
         if (i >= intervalCount) throw new IllegalArgumentException();
         // todo: check for binary trees
         return lineagesAdded[i].get(0).getNr();
    }
    
    public List<Node> getLineagesRemoved(int i) {
        if (!intervalsKnown) {
            calculateIntervals();
        }
        if (i >= intervalCount) throw new IllegalArgumentException();
        return lineagesRemoved[i];
    }
    
    // New version - copies node ids to array instead of returning a list
    public int getLineagesRemoved(int i, int[] lineages) {
        if (!intervalsKnown) {
            calculateIntervals();
        }
        if (i >= intervalCount) throw new IllegalArgumentException();
        List<Node> lrem = lineagesRemoved[i];
        // lrem must be <= 2 - checked by tree interval
        for (int l=0; l < lrem.size(); l++)
        		lineages[l] = lrem.get(l).getNr(); 
        return lrem.size();
    }
    
    /**
     * Added method
     */
    protected void swap(){    	
    	ArrayList<Integer> activeLineages = new ArrayList<Integer>();
        
        for (int i = 0; i < intervalCount; i++){
        	if(IntervalType.SAMPLE == getIntervalType(i)){
        		List<Node> incomingLines = getLineagesAdded(i);
        		for (Node l : incomingLines) {
        			activeLineages.add(l.getNr());
        		}
        	}
       	
        	if(IntervalType.COALESCENT == getIntervalType(i)){
        		List<Node> daughter = getLineagesRemoved(i);
            	
            	if (daughter.size() > 2) {
            		System.err.println("Multifurcation");
        		}
        		
        		int d1 = activeLineages.indexOf(daughter.get(0).getNr());
        		int d2 = activeLineages.indexOf(daughter.get(1).getNr());
        		int j = i;
        		boolean swap = false;
        		
        		while (d1 == -1 || d2 == -1 && j < intervalCount){	// If true the nodes are in the wrong order
        			// Go to next event
        			j++;
                	if(IntervalType.COALESCENT == getIntervalType(j)){
                		daughter = getLineagesRemoved(j);
                		d1 = activeLineages.indexOf(daughter.get(0).getNr());
                		d2 = activeLineages.indexOf(daughter.get(1).getNr());
                	}
            		swap = true;
        		} 
        		
        		if(!swap){
	        		if (d1 > d2){
	        			activeLineages.remove(d1);
	        			activeLineages.remove(d2);
	        		}
	        		else{
	        			activeLineages.remove(d2);
	        			activeLineages.remove(d1);
	        		}
	        		List<Node> incomingLines = getLineagesAdded(i);
	        		activeLineages.add(incomingLines.get(0).getNr());
        		}
        		
        		// Add parent
        		if (j == intervalCount){
        			break;
        		}       		
        		
        		if(swap){
        		    double inter = intervals[j];
        		    intervals[j] = intervals[i];
        		    intervals[i] = inter;        		    		
        		    double storedInter = storedIntervals[j];
        		    storedIntervals[j] = storedIntervals[i];
        		    storedIntervals[i] = storedInter;

        		    /**
        		     * The number of uncoalesced lineages within a particular interval.
        		     */

        		    /**
        		     * The lineages in each interval (stored by node ref).
        		     */
        		    List<Node> lineageAdded = lineagesAdded[j];
        		    lineagesAdded[j] = lineagesAdded[i];
        		    lineagesAdded[i] = lineageAdded;
        		    List<Node> lineageRemoved = lineagesRemoved[j];
        		    lineagesRemoved[j] = lineagesRemoved[i];
        		    lineagesRemoved[i] = lineageRemoved;
        			i--;
      			
        		}
        	}
        }
    }
    
    /* print utilities */
    public void printLineagesRemoved() {
    	
        if (!intervalsKnown) {
            calculateIntervals();
        }
    	
    	System.out.println("RemovedLineages: ");
    	for (int i = 0; i < lineagesRemoved.length; i++) {
    		List<Node> list = lineagesRemoved[i];
    		if (list != null) {
	    		for (int j = 0; j < list.size(); j++) {
	    			if (j > 0 ) {
	    				System.out.print(",");
	    			}
	    			System.out.print(list.get(j).getNr());
	    		}
	    		System.out.print(":");
    		}
    	}
    	
    	System.out.println();
    	
    	if (storedLineagesRemoved != null) {
	    	for (int i = 0; i < storedLineagesRemoved.length; i++) {
	    		List<Node> list = storedLineagesRemoved[i];
	    		if (list != null) {
		    		for (int j = 0; j < list.size(); j++) {
		    			if (j > 0 ) {
		    				System.out.print(",");
		    			}
		    			System.out.print(list.get(j).getNr());
		    		}
	    		}
	    		System.out.print(":");
	    	}
    	}
    	
    }
    
    public void printIntervals() {
    	
        if (!intervalsKnown) {
            calculateIntervals();
        }
    	
    	System.out.println("Intervals: ");
    	for (int i = 0; i < intervals.length; i++) {
    		System.out.print(intervals[i]);
    		System.out.print(":");
    	}
    	
    	System.out.println();
    	
    	for (int i = 0; i < intervals.length; i++) {
	    	System.out.print(storedIntervals[i]);
    		System.out.print(":");
    	}
    	
    }

    
    protected void printERR(){
    	for (int i = 0; i<intervals.length; i++){
    		System.out.print(String.format("%.2f",intervals[i]) + "\t");
    	}
    }
    
    

}
