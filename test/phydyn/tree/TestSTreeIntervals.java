package phydyn.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.evolution.tree.coalescent.STreeIntervals;
import beast.util.TreeParser;

// @FixMethodOrder(MethodSorters.NAME_ASCENDING)
// Valid from JUnit 4.11 onwards



public class TestSTreeIntervals {
	
	private static String[] zeroNewick = { 
			"(((a:2.5,b:8):4,(c:10,d:6):5.8):27,(e:10,f:20):22):0.0;",
			"(((a:2,b:2):1,(c:2,d:2):1):10,(e:0,f:0):10):0.0;",
			"(((a:0,b:0):1,(c:2,d:2):1):10,(e:1,f:1):10):0.0;",
			"(((a:2,b:2):0,(c:2,d:2):0):10,(e:1,f:1):10):0.0;",
			"((e:1,f:1):20,((a:2,b:2):5,(c:2,d:2):5):10):0.0;"
			};

	public TestSTreeIntervals() {
		// TODO Auto-generated constructor stub
	}

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testIntervalsRootAndLength() {
		String newick = "(((a:2.5,b:8):4,(c:10,d:6):5.8):27,(e:10,f:20):22):0.0;";
		Tree tree = new TreeParser(newick,false, false, true, 0);
		STreeIntervals intervals = new STreeIntervals(tree);
		double l=0;
		for(int i=0; i < intervals.getIntervalCount(); i++) {
			l += intervals.getInterval(i);
		}
		assertEquals(intervals.getTotalDuration(),l,0.00001);
		
		Node root = tree.getRoot();
		Node lastEvent = intervals.getEvent(intervals.getIntervalCount()-1);
		assertEquals(root.getNr(),lastEvent.getNr());
		
	}
	
	
	 /**
	  * Checks that sorted nodes (indices array) have the parent-child property.
	  * This is a quick check: it doesn't keep track of active lineages nor requires other
	  * structures, just the sorted array of nodes.
	  *
	  * @param array of indices used to access Tree node array.
	  */
	 protected static boolean checkOrder(STreeIntervals intervals) {
		 boolean[] visited = new boolean[intervals.getIntervalCount()];
		 // traverse intervals
		 for(int i=0; i < intervals.getIntervalCount(); i++) {
			 Node parent = intervals.getEvent(i);
			 for(Node child: parent.getChildren()) {
				 if (!visited[child.getNr()]) {
					 //System.out.println("Child node: "+child.getNr()+" not visited");
					 return false;
				 }
			 }
			 visited[parent.getNr()] = true;
		 }
		 return true;
	 }
	 
	@Test
	public void testZeroLengthOne() {
		Tree tree = new TreeParser(zeroNewick[0],false, false, true, 0);
		STreeIntervals intervals = new STreeIntervals(tree);
		assertTrue("Intervals out of order", checkOrder(intervals));
	}
	
	@Test
	public void testZeroLengthTwo() {
		Tree tree = new TreeParser(zeroNewick[1],false, false, true, 0);
		STreeIntervals intervals = new STreeIntervals(tree);
		assertTrue("Intervals out of order", checkOrder(intervals));
	}
	
	@Test
	public void testZeroLengthThree() {
		Tree tree = new TreeParser(zeroNewick[2],false, false, true, 0);
		STreeIntervals intervals = new STreeIntervals(tree);
		assertTrue("Intervals out of order", checkOrder(intervals));
	}
	
	@Test
	public void testZeroLengthFour() {
		Tree tree = new TreeParser(zeroNewick[3],false, false, true, 0);
		STreeIntervals intervals = new STreeIntervals(tree);
		assertTrue("Intervals out of order", checkOrder(intervals));
	}
	
	@Test
	public void testZeroLengthFive() {
		Tree tree = new TreeParser(zeroNewick[4],false, false, true, 0);
		STreeIntervals intervals = new STreeIntervals(tree);
		assertTrue("Intervals out of order", checkOrder(intervals));
	}	
	
	
}
