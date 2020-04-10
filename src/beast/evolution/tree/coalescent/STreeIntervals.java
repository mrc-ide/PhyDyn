package beast.evolution.tree.coalescent;

import java.util.Comparator;
import java.util.List;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.evolution.tree.coalescent.TreeIntervals;
import beast.util.HeapSort;

/*
 * @author Igor Siveroni
 * Extends the original TreeIntervals class to take into account the zero-length branch
 * issue: sorting by tree heights can potentially generate out-of-order intervals i.e.
 * parents showing up before children, if internal branches are zero. 
 * Bug? times and indices need to be 'stored' because they are used
 *  by public methodgetIntervalTime(int)
 */

public class STreeIntervals extends TreeIntervals {
	
	/** No need to re-define - access is allowed if class is defined in the same package **/
    //protected double[] times;
    //protected int[] indices;
    
    private Node[] events;
    private int[] pOrder; // post-order traversal
    
    double[] storedTimes;
    int[] storedIndices;

	public STreeIntervals() {
	}

	public STreeIntervals(Tree tree) {
		init(tree);
	}
	
	 @Override
	 public void initAndValidate() {		 
		 super.initAndValidate();
	 }
	 
	 @Override
	 protected void restore() {
	        
		 int[] tmp1 = storedIndices;
		 storedIndices = indices;
		 indices = tmp1;
		 
		 double[] tmp2 = storedTimes;
		 storedTimes = times;
		 times = tmp2;
		 
		 //events = new Node[intervalCount];
		 //Node[] nodes = treeInput.get().getNodesAsArray();
		 //for(int i=0; i < intervalCount; i++)
		 //	events[i] = nodes[indices[i]];
		 events= null;
		 
		 super.restore();
		 // added to fix problems with MCMC check - state.robustlyCalcPosterior(posterior);
		 intervalsKnown = false;
	   	        	
	 } 
	    
	    
	 @Override
	 protected void store() {
		 System.arraycopy(indices, 0, storedIndices, 0, intervals.length);
		 System.arraycopy(times, 0, storedTimes, 0, intervals.length);
		 super.store();
	 }    
	    
	 @Override
	 public double getIntervalTime(int i) {
		 if (!intervalsKnown) {
			 calculateIntervals();
		 }
		 return times[indices[i]];
	 } 
	 
	 // Produce array of sorted events
	 private void calculateEvents() {
		 events = new Node[intervalCount];
		 Node[] nodes = treeInput.get().getNodesAsArray();
		 for(int i=0; i < intervalCount; i++)
		 	events[i] = nodes[indices[i]];
	 }
	 
	 // new public method
	 // Returns Node that marks the end of the interval
	 public Node getEvent(int i) {
		 if (!intervalsKnown) {
			 calculateIntervals();
		 }
		 if (i >= intervalCount) throw new IllegalArgumentException();
		 if (events==null) 
			 calculateEvents();
		 return events[i];
		
	 }
	 
	 /**
	  * Recalculates all the intervals for the given beast.tree
	  * Main functionality copied from super-class and adapted to use
	  * local arrays and local sorting algorithm.
	  * Ignores calls to generate lineagesAdded/Removed lists.
	  * events array used intead. Assumption: multifurcationLimit=0
	  */
	 @Override
	 protected void calculateIntervals() {
		 Tree tree = treeInput.get();

		 final int nodeCount = tree.getNodeCount();
		 
		 // moved initialisation up
		 if (intervals == null || intervals.length != nodeCount) {
			 intervals = new double[nodeCount];
			 lineageCounts = new int[nodeCount];
			 //lineagesAdded = new List[nodeCount];
			 //lineagesRemoved = new List[nodeCount];
			 
			 times = new double[nodeCount];
			 indices = new int[nodeCount];
			 pOrder = new int[nodeCount];

			 storedIntervals = new double[nodeCount];
			 storedLineageCounts = new int[nodeCount];
			 
			 storedIndices = new int[nodeCount];
	         storedTimes = new double[nodeCount];

		 } 
		 /* removing functionality related to lineagesAdded/Removed
	        else {
	            for (List<Node> l : lineagesAdded) {
	                if (l != null) {
	                    l.clear();
	                }
	            }
	            for (List<Node> l : lineagesRemoved) {
	                if (l != null) {
	                    l.clear();
	                }
	            }
	        }
		  */

		 
		 int[] childCounts = new int[nodeCount];
		 
		 // needed for new compare method
		 traversePostOrder(tree,pOrder);
		 
		 // check correctness - debug
		 //for(Node node :  tree.getNodesAsArray() ) {
		 //	 for(Node child : node.getChildren()) {
		 //		 if (!(pOrder[node.getNr()] > pOrder[child.getNr()])) {
		 //			 throw new IllegalArgumentException("post-order traversal wrong");
		 //		 }
		 //	 }
		 //}

		 collectTimes(tree, times, childCounts);

		 //heapsort(times,indices,pOrder);
		 heapsort(times,indices,createComparator());
		 //HeapSort.sort(times, indices);

		 // debug
		 //if (!checkOrder()) {
		 //	 throw new IllegalArgumentException("Out of order");
		 //}
		
		 // start is the time of the first tip
		 double start = times[indices[0]];
		 int numLines = 0;
		 int nodeNo = 0;
		 intervalCount = 0;
		 while (nodeNo < nodeCount) {

			 int lineagesRemoved = 0;
			 int lineagesAdded = 0;

			 double finish = times[indices[nodeNo]];
			 double next;

			 do {
				 final int childIndex = indices[nodeNo];
				 final int childCount = childCounts[childIndex];
				 // don't use nodeNo from here on in do loop
				 nodeNo += 1;
				 if (childCount == 0) {
					 // addLineage(intervalCount, tree.getNode(childIndex)); -- igor
					 lineagesAdded += 1;
				 } else {
					 lineagesRemoved += (childCount - 1);

					 // record removed lineages
					 // commented out - igor
					 //final Node parent = tree.getNode(childIndex);
					 //for (int j = 0; j < childCount; j++) {
					 //    Node child = j == 0 ? parent.getLeft() : parent.getRight();
					 //    removeLineage(intervalCount, child);
					 // }

					 // record added lineages
					 // addLineage(intervalCount, parent);  -- igor
					 // no mix of removed lineages when 0 th
					 if (multifurcationLimit == 0.0) {
						 break;
					 }
				 }

				 if (nodeNo < nodeCount) {
					 next = times[indices[nodeNo]];
				 } else break;
			 } while (Math.abs(next - finish) <= multifurcationLimit);

			 if (lineagesAdded > 0) {

				 if (intervalCount > 0 || ((finish - start) > multifurcationLimit)) {
					 intervals[intervalCount] = finish - start;
					 lineageCounts[intervalCount] = numLines;
					 intervalCount += 1;
				 }

				 start = finish;
			 }

			 // add sample event
			 numLines += lineagesAdded;

			 if (lineagesRemoved > 0) {

				 intervals[intervalCount] = finish - start;
				 lineageCounts[intervalCount] = numLines;
				 intervalCount += 1;
				 start = finish;
			 }
			 // coalescent event
			 numLines -= lineagesRemoved;
		 }

		 intervalsKnown = true;
	        
		 // NEW - igor
		 calculateEvents();
	 }
	    
	 public void printIntervals() {
		 if (!intervalsKnown) {
			 calculateIntervals();
		 }   
		 if (events==null) calculateEvents();
		 System.out.print("\nIntervals: "+this.getIntervalCount());
		 System.out.println(" -- duration: "+this.getTotalDuration());
		 for(int interval=0; interval < this.getIntervalCount(); interval++) {
			 System.out.print(Math.round(this.getInterval(interval)*100)/100.0+" ");
			 System.out.print(this.getIntervalType(interval));
				
			 Node event = events[interval];
			 System.out.print("[" + event.getNr()+" " + event.getID() +"] "+Math.round(event.getDate()*100)/100.0);
			 
			 List<Node> outgoingLines = event.getChildren();
			 if (outgoingLines != null) {
				 System.out.print(" removed: ");
				 for(Node node: outgoingLines){
					 System.out.print(node.getNr()+" ");
				 }	
			 }
			 System.out.println(" ");
		 }
		 System.out.println("--- end intervals--");
	    	
	 }
	 
	 /*
	  * Write post order traversal into array order.
	  */
	 public static void traversePostOrder(Tree tree, int[] order) {
		 // start from root
		 traversePostOrder(tree.getRoot(), order, 0);
	 }
	 
	 public static int traversePostOrder(Node node, int[] order, int idx) {
		 // traverse children
		 List<Node> children = node.getChildren();
		 // traverse 'right-to-left' to keep similar node numbering
		 for(int i=node.getChildCount()-1; i >= 0 ;i--) {
			 final Node child = children.get(i);
			 idx = traversePostOrder(child, order, idx);
		 }
		 //for (final Node child : node.getChildren()) {
         //  idx = traversePostOrder(child, order, idx);
		 //}		 
		 order[node.getNr()] = idx;
		 return idx+1;
	 }
	 
	 /**
	  * Checks that sorted nodes (indices array) have the parent-child property.
	  * This is a quick check: it doesn't keep track of active lineages nor requires other
	  * structures, just the sorted array of nodes.
	  *
	  * @param array of indices used to access Tree node array.
	  */
	 protected boolean checkOrder() {
		 boolean[] visited = new boolean[indices.length];
		 Node[] nodes = treeInput.get().getNodesAsArray();
		 
		 for(int i=0 ; i < indices.length; i++) {   		
			 Node node = nodes[indices[i]];
			 
			 List<Node> children = node.getChildren();
			 for(Node child: children) {
				 if (!visited[child.getNr()]) {
					 System.out.println("Child node: "+child.getNr()+" not visited");
					 return false;
				 }
			 }
			 visited[node.getNr()] = true;
		 }   
		 return true;
	 }
	 
	 // Sorting
	 // Heapsort adapted from Heapsort class to use a Comparator
	    
	 /**
	  * Method that creates a Comparator<Integer> object that implements compare(n1,n2).
	  * Compares two nodes of a tree based on height (first) and level (root has heighest level)
	  * It implements a strict (non-reflexive) partial order on tree nodes: equal-height nodes
	  * are ordered based on level. Level is based on post-order traversal indexing. 
	  * This is important for the zero-branch case: we want children to
	  * show up before their respective parents (and vice-versa).
	  * compare(n1,n2) returns a negative number if n1 'less-than' n2, positive otherwise.
	  * compare(n1,n2) = (h(n1)==h(n2))?(level(n1)-level(n2)):(h(n1)<h(n2)?-1:1)
	  *
	  * @param array   an array of doubles
	  * @param indices an array of node nr's
	  * @param n1 node nr
	  * @param n2 node nr
	  */
	    
	 
	 Comparator<Integer> createComparator() {
		 Comparator<Integer> c = new Comparator<Integer>() {
			 @Override
	         public int compare(Integer i1, Integer i2) {
				 if (times[indices[i1]] < times[indices[i2]]) {
					 return -1;
				 }
				 else if (times[indices[i1]] > times[indices[i2]]) {
					 return 1;
				 }
				 else {
					 //return (indices[i1]-indices[i2]);
					 return (pOrder[indices[i1]]-pOrder[indices[i2]]);
				 }  
	         }
		 };
		 return c;
	 }
	    
	 // Use Java Comparator
	 public static void heapsort(double[] array, int[] indices, Comparator<Integer> cp) {
		 
		 // ensures we are starting with valid indices
		 for (int i = 0; i < indices.length; i++) {
			 indices[i] = i;
		 }

		 
		 int temp;
		 int j, n = indices.length;
		 
		 // turn input array into a heap
		 for (j = n / 2; j > 0; j--) {
			 adjust(array, indices, j, n, cp); // added pOrder
		 }

		 // remove largest elements and put them at the end
		 // of the unsorted region until you are finished
		 for (j = n - 1; j > 0; j--) {
			 temp = indices[0];
			 indices[0] = indices[j];
			 indices[j] = temp;
			 adjust(array, indices, 1, j, cp);  // added pOrder
		 }
	 }
	  
	 /**
	  * helps sort an array of indices into an array of doubles.
	  * Assumes that array[lower+1] through to array[upper] is
	  * already in heap form and then puts array[lower] to
	  * array[upper] in heap form.
	  *
	  * @param array   array of doubles
	  * @param indices array of indices into double array to sort
	  * @param lower   lower index of heapify
	  * @param upper   upper index of heapify
	  */
	 private static void adjust(double[] array, int[] indices, int lower, int upper, 
			 Comparator<Integer> cp) {

		 int j, k;
		 int temp;

		 j = lower;
		 k = lower * 2;
		 
		 while (k <= upper) {
			 // if ((k < upper) && (array[indices[k - 1]] < array[indices[k]])) {
			 if ((k < upper) && (cp.compare(k-1,k) <= 0) ) {  // added pOrder
				 k += 1;
			 }
			 // if (array[indices[j - 1]] < array[indices[k - 1]]) {
			 if (cp.compare(j-1,k-1) <= 0) {  // added pOrder
				 temp = indices[j - 1];
				 indices[j - 1] = indices[k - 1];
				 indices[k - 1] = temp;
			 }
			 j = k;
			 k *= 2;
		 }
	 }
	    	 	 
	 
	 /**
	  * Compares two nodes of a tree based on height (first) and level (root has heighest level)
	  * Implements a strict (non-reflexive) partial order on tree nodes: equal-height nodes
	  * are ordered based on level. This is important for the zero-branch case: we want children to
	  * show up before their respective parents (and vice-versa).
	  * Returns a negative number if n1 'less-than' n2, positive otherwise.
	  * Assumption: n1 < n2 ifi level(n1) < level(n2)
	  * compare(n1,n2) = (h(n1)==h(n2))?(n1-n2):(h(n1)<h(n2)?-1:1)
	  *
	  * @param array   an array of doubles
	  * @param indices an array of node nr's
	  * @param n1 node nr
	  * @param n2 node nr
	  */
	    
	 static int compare(double[] array, int[] indices, int n1, int n2, int[] pOrder) {
		 if (array[indices[n1]]<array[indices[n2]]) {
			 return -1;
		 }
		 else if (array[indices[n1]] > array[indices[n2]]) {
			 return 1;
		 }
		 else {
			 return (n1-n2);
		 }
	 }
	 
	 	    
	 /**
	  * Heap sort implementation taken from BEAST's HeapSort class.
	  * 
	  * Sorts an array of indices into an array of doubles
	  * into increasing order.
	  *
	  * @param array   an array of doubles
	  * @param indices an array of indices to be sorted so that they describe an ascending order of values in array
	  */
	 public static void heapsort(double[] array, int[] indices, int[] pOrder) {
		 
		 // ensures we are starting with valid indices
		 for (int i = 0; i < indices.length; i++) {
			 indices[i] = i;
		 }

		 
		 int temp;
		 int j, n = indices.length;
		 
		 // turn input array into a heap
		 for (j = n / 2; j > 0; j--) {
			 adjust(array, indices, j, n, pOrder); // added pOrder
		 }

		 // remove largest elements and put them at the end
		 // of the unsorted region until you are finished
		 for (j = n - 1; j > 0; j--) {
			 temp = indices[0];
			 indices[0] = indices[j];
			 indices[j] = temp;
			 adjust(array, indices, 1, j, pOrder);  // added pOrder
		 }
	 }
	  
	 /**
	  * helps sort an array of indices into an array of doubles.
	  * Assumes that array[lower+1] through to array[upper] is
	  * already in heap form and then puts array[lower] to
	  * array[upper] in heap form.
	  *
	  * @param array   array of doubles
	  * @param indices array of indices into double array to sort
	  * @param lower   lower index of heapify
	  * @param upper   upper index of heapify
	  */
	 private static void adjust(double[] array, int[] indices, int lower, int upper, int[] pOrder) {

		 int j, k;
		 int temp;

		 j = lower;
		 k = lower * 2;
		 
		 while (k <= upper) {
			 // if ((k < upper) && (array[indices[k - 1]] < array[indices[k]])) {
			 if ((k < upper) && (compare(array,indices,k-1,k,pOrder) <= 0) ) {  // added pOrder
				 k += 1;
			 }
			 // if (array[indices[j - 1]] < array[indices[k - 1]]) {
			 if (compare(array,indices,j-1,k-1,pOrder) <= 0) {  // added pOrder
				 temp = indices[j - 1];
				 indices[j - 1] = indices[k - 1];
				 indices[k - 1] = temp;
			 }
			 j = k;
			 k *= 2;
		 }
	 }
	    	 
	 
	 
	    
	 
}
