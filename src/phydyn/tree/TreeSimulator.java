package phydyn.tree;

/*
 * Structured tree simulation using the structured coalescent process
 * Birth and migration rates are pre-computed by the population model given as input. 
 * 
 * Under development
 */

import beast.base.evolution.tree.Node;
import beast.base.evolution.tree.Tree;
import beast.base.util.Randomizer;
import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;
import phydyn.distribution.StateProbabilities;
import phydyn.distribution.StateProbabilitiesVectors;
import phydyn.model.PopModel;
import phydyn.model.TimeSeriesFGY;
import phydyn.model.TimeSeriesFGY.FGY;
import phydyn.util.DMatrix;
import phydyn.util.DVector;

import java.util.ArrayList;
import java.util.List;



public class TreeSimulator  {
	
	enum EventType { SAMPLE, COALESCENCE };
	public enum SimMethod { GLSP1, GLSP2 };
	
	final static  double A_EPS = 0.00;
	final static double Y_EPS = 1.E-12;
	final static double T_EPS = 0.000001;

	private PopModel popModel;
	private SimMethod method;
	private TimeSeriesFGY ts;
	private int numPoints;
	
	private StateProbabilities sp;
	private DMatrix rates;
	
	private double ts_t0, ts_t1;
	private String[] demes; 

	private int[] sampleDemes; // deme index
	private String[] tipNames;
	
	private int numDemes, numTips, numInternal,numNodes;
	private double maxSampleTime;
	
	private boolean finiteSizeCorrection;
	

	private double[] sampleTimes;
	private double[] sampleHeights;
	Node[] treeNodes;
	
	// loop variables
	int simNumber;
	int nextTip, nextInternal;
	double h,t;
	int it;
	
	// stats
	int numMultifurcating;
	
	// Used as a C struct
	protected class Event {  // backwards in time
		double dhCoal;
		double tSample;
		double tCoal;
		double t; // max(tSample, tCoal)
		double lambda;
		EventType type;
	}
	
	public class Result {
		Tree tree;
		int numCollapsed;  // >0 then multifurcation
		double h, t;
		Result(Tree tree, int n, double t,double h) {
			this.tree = tree;
			this.numCollapsed = n;
			this.h = h;
			this.t = t;
		}
	}
	
	
	// Simplifications compared with phydynR Cpp version
	// sortedSampleStates: unit vectors representing position of sampled deme.
	
	// first version with pre-processed arrays
	// assume t1 = maxSampleTime
	public TreeSimulator(PopModel popModel, String[] tipNames, int[] sampleDemes, double[] sampleTimes, int[] sampleSizes) {
		this.popModel = popModel;
		this.method = SimMethod.GLSP1;
		numDemes = this.popModel.getNumStates();
		demes = this.popModel.demeNames;
		// sampleDemes contains deme indices used to access demeNames
		numTips = sampleDemes.length;
		if (numTips!= sampleTimes.length)
			throw new IllegalArgumentException("Mismatch in sample sizes (deme name and sample time)");
		if (numDemes != sampleSizes.length)
			throw new IllegalArgumentException("Problem with population sizes array");
		
		/* Maximum dimension = max extant lineages 
		 * Only bottom left corner is used. Could save memory but complicates things */
		rates = new DMatrix(numTips,numTips);
		
		this.sampleTimes = sampleTimes;
		this.sampleDemes = sampleDemes;
		this.tipNames = tipNames;
		
		/* At this point, samples must be ordered by date i.e. sampleTimes must
		 * be sorted in descending order.
		 * sampleDemes must be accessed using the same index.
		 */
		maxSampleTime = sampleTimes[0];
		sampleHeights = new double[sampleTimes.length];
		sampleHeights[0] = 0;
		for(int i=1; i < sampleTimes.length; i++) {
			sampleHeights[i] = maxSampleTime - sampleTimes[i];
		}
		popModel.setEndTime(maxSampleTime+T_EPS);
		
		ts = popModel.getTimeSeries();
	
		ts_t0 = popModel.getStartTime();
		ts_t1 = popModel.getEndTime();
		// get timeseries after potential parameter updates
		
		
		numPoints = ts.getNumTimePoints();
		System.out.println(ts_t0+" - "+ts.getTime(numPoints-1));
		System.out.println(ts_t1+" - "+ts.getTime(0));
		// fix ts so  ts_t1 = ts.getTime(0) after reverse
		
				
		//if (Math.abs(maxSampleTime-ts_t1) > 0.000001)
		//	throw new IllegalArgumentException("max sample time must match t1");
		
		finiteSizeCorrection = true;
		// n = numSamples nNoe = numInternal
		numInternal = numTips-1;
		numNodes = numTips+numInternal;
		treeNodes = new Node[numNodes];
		// sort times
				
		sp = new StateProbabilitiesVectors(numNodes,numDemes);
		
	}
	
	public void setMethod(SimMethod m) {
		this.method = m;
		System.out.println("Method = "+m);
	}
	
	public List<Result> simulate(int numSimulations, int seed) {
		List<Result> trees = new ArrayList<Result>();
		
		System.out.println("Num points = "+numPoints);
		// initialize random generator - we may need a seed argument/field
		
		System.out.println("num simulations = "+numSimulations);
		Randomizer.setSeed(seed);
		System.out.println("Seed="+Randomizer.getSeed());
		
		// double check in case there's a problem with floating point precision
		if (maxSampleTime > ts.getTime(0))
			throw new IllegalArgumentException("Max sample time outside trajectory times (too high)");
		
		numMultifurcating = 0;
		for(simNumber=0; simNumber<numSimulations;simNumber++) {
			for(int j=0; j<numNodes;j++) {
				treeNodes[j]=null;
			}
			Result t = simulateTree();
			trees.add(t);
			sp.clear();
		}
		
		return trees;	
	}
	
	public Result simulateTree() {
		
		// find index of first time *after* maxSampleTime	
		// note that times appear in reverse order: time(0) = t1
		it = 0; 
		t = maxSampleTime; // = sampleTimes[0]
		h = 0;
		updateInterval();
				
		//System.out.println("Starting from interval: "+it);
		
		double dh;		
		double totalLambda;		
		double tnextInterval;
		
		FGY fgy = ts.getFGY(it);
		
		nextTip=0; // used as index
		nextInternal = numTips;  //
		
		// h : height, t=time in descending order
		
		int numExtant =  processSamplingEvents();
		
		// if only one sampling event was found , move to next one and update sp
		if (numExtant < 2) {
			double tnextSample = this.getNextSampleTime();
			dh = t - tnextSample;
			update_states(dh, fgy);			
			numExtant += processSamplingEvents();										
			fgy = ts.getFGY(it);
			updateInterval();
			//System.out.println("updating  interval: "+it);
			
		} 
		
		fgy = ts.getFGY(it);
		fgy.Y.maxi(Y_EPS); // Y(i) >= Y_EPS
		
		Event event = new Event();
		
		sampleCoalEvent(event,fgy, true);			
		tnextInterval =ts.getTime(it+1);
		
		//sp.check();
		
		// first version: 
		// Assume constant coalescence rate between events for event generation
		// but update state probabilities per tree interval
		
		//System.out.println("t="+t+" numextant="+numExtant);
		//DVector[] v = sp.getExtantProbs();
		//for(int i=0; i < v.length; i++) System.out.println(v[i]);
		
		int[] coalPair = new int[2];
		boolean changeInterval=false;
		
		while(it < numPoints-1)
		{	
			//System.out.println("numExtant="+numExtant);
			//System.out.println("t="+t+" tnext="+event.t+" tCoal="+event.tCoal+" dh="+event.dhCoal);
			//if (numExtant>0) break;
			
			if (changeInterval) {  // have I just changed interval?
				tnextInterval =ts.getTime(it+1);  
				fgy = ts.getFGY(it);
				fgy.Y.maxi(Y_EPS); // Y(i) >= Y_EPS
				changeInterval = false;
				// second method: resample every time we move to a new interval
				if (this.method == SimMethod.GLSP2 ) {
					sampleCoalEvent(event,fgy, false);	
				}
			}
			
			if (event.t < tnextInterval) {
				update_states(t-tnextInterval, fgy);
				t = tnextInterval;
				it++; changeInterval = true;
				continue; // go to next interval
			}
			
			// assert time condition
			if (event.t > t)
				throw new RuntimeException("Error TreeSimulator: nextEvent must happen before current t");
			
			// process Event
			// move to next event
			update_states(t-event.t, fgy);
			t = event.t;
			// process event
			if (event.type == EventType.SAMPLE) {			
				numExtant += processSamplingEvents(); // updates t & h
				// the tEPS issue
			} else {  // COALESCENT
				totalLambda = sp.updateRateMatrix(fgy, rates);
				double u = Randomizer.nextDouble();
				sp.sampleCoalescentPair(rates, coalPair, totalLambda, u);
				DVector pa = sp.getCoalProbVectorNr(coalPair, fgy);
			    pa.divi(pa.sum()); // normalise
				
			    // update extant lineages
			    sp.addLineage(nextInternal,pa);		 
				sp.removeLineage(coalPair[0]);
				sp.removeLineage(coalPair[1]); 
				numExtant--;
				
				h = this.maxSampleTime - t;
				final Node left = treeNodes[coalPair[0]];
				final Node right = treeNodes[coalPair[1]];
				final Node node = this.createInternalNode(nextInternal, h, left, right);
				//treeNodes[internalNr] = node;
				nextInternal++;
				//if (fsCorrectionsInput.get()) {
				//	doFiniteSizeCorrections(coalNode,pa);								
			}
			// sp.check();
			    			
			// assert number of entant lineages
			if (numExtant != sp.getNumExtant()) {
				throw new RuntimeException("TreeSimulator: number of extant lineages mismatch");
			}
			
			if (numExtant==1) break;
			
			sampleCoalEvent(event,fgy,true);
						
			// Is event not in current interval?
			if (event.t < tnextInterval) {
				update_states(t-tnextInterval, fgy);
				t = tnextInterval;
				it++; changeInterval = true;
			}
					
		}
		System.out.print("sim "+(simNumber+1));
		h = this.maxSampleTime-t;
		System.out.println(" t="+t+" h="+h);
		int[] extantLineages = sp.getExtantLineages();
		Node root = treeNodes[extantLineages[0]];		
		if (numExtant > 1) {
			numMultifurcating++;
			System.out.println(numExtant+" extant nodes left -- joining at root");
			for(int i=1; i < numExtant; i++) {
				root = this.createInternalNode(nextInternal++, h, 
						root, treeNodes[extantLineages[i]]);
			}
		}
		Tree tree = new Tree(root);		
		Result r =  new Result(tree,numExtant-1,t,h);
		//final String newick = tree.getRoot().toNewick(false);
		//System.out.println(newick);
		return r;
	}
	
	// Sample next coal arrival and determine next event	
	void sampleCoalEvent(Event event, FGY fgy, boolean sample) {
		// Sample next coal arrival and determine next event			
		final double totalLambda = sp.updateRateMatrix(fgy, rates);	
		if (sample) {
			event.dhCoal = getNextArrival(totalLambda);		
			event.tCoal = t - event.dhCoal;		
		} else {  // adjust dh
			double tPrev = event.tCoal + event.dhCoal;
			double dh = event.dhCoal - (tPrev - t);
			//System.out.println(totalLambda+"  "+event.lambda);
			event.dhCoal = dh*(event.lambda/totalLambda); // adjust dh
			event.tCoal = t - event.dhCoal;	// recalculate coal time
		}
		// resolve next event
		event.lambda = totalLambda;
		event.tSample = this.getNextSampleTime();
		// determine next event (compare against next sample)
		if (Double.isNaN(event.tSample)) {
			event.t = event.tCoal; event.type= EventType.COALESCENCE;
		} else {
			if (event.tSample > event.tCoal) {
				event.t = event.tSample; event.type = EventType.SAMPLE;
			} else {
				event.t = event.tCoal; event.type= EventType.COALESCENCE;
			}
		}
					
	}
	
	// updates interval number for a given t: such that ts[interval] <= t < ts[interval+1]
	// I think ts has this functionality
	int updateInterval() {
		while ((it < numPoints)&&(t <= ts.getTime(it))) {
			it++;
		}
		if (it >= numPoints)
			throw new IllegalArgumentException("Time outside trajectory times (too low)");
		if (it>0)
			it--;
		
		return 0;
	}
	
	// Adds sampling events (tips) to extant lineages
	// Adds tips sampled at the same time as (or very close to) sample number tipNr.
	// It considers sampling events in [sampleTimes[tipNr],  sampleTimes[tipNr]+T_EPS]
	// return: number of sampling events processed
	int processSamplingEvents() {
		// tipNr index to next samplign event
		if (nextTip >= numTips) return 0; // no more samples to process
		int n=0;
		t = sampleTimes[nextTip];
		h = sampleHeights[nextTip];
		// System.out.println("Sampling: "+tipNames[tipNr]);
		sp.addSample(nextTip, sampleDemes[nextTip]);
		// tip Nr = index in sampleTimes
		createTipNode(nextTip,h);
		nextTip++; n++;
		// process sample
		double nextT;
		while (nextTip < numTips) {
			nextT = sampleTimes[nextTip];
			// System.out.println("nextT="+nextT);
			if ((t-nextT) <= T_EPS) {
				// process sampling event
				h = sampleHeights[nextTip];
				sp.addSample(nextTip, sampleDemes[nextTip]);
				createTipNode(nextTip,h);
				n++;
				nextTip++;
			} else
				break;
		}
		t = sampleTimes[nextTip-1]; // last sample processed
		return n;
	}
	
	double getNextSampleTime() {
		if (nextTip >= numTips) return Double.NaN;
		else return sampleTimes[nextTip];
	}
	
	/* Node creation 
	 * Need to check if ID is important 
	*/

	Node createTipNode(int nr, double h) {
		Node node = createNode(nr,h);
		String id;
		if (tipNames==null) {
			id = tipNames[nr];
		} else {
			String demeId = demes[sampleDemes[nr]];
			double sampleTime = sampleTimes[nr];
			id = nr+"_"+sampleTime+"_"+demeId;
		}
		node.setID(id);
		return node;
	}
	
	Node createInternalNode(int nr, double h, Node left, Node right) {
		Node node = createNode(nr, h);
		node.addChild(left);
		node.addChild(right);
		return node;
	}
	
	Node createNode(int nr, double h) {
		Node node = new Node();
		node.setNr(nr);
		node.setHeight(h);
		treeNodes[nr] = node; // node bookkeeping
		return node;
	}
	
	// Assumes constant Q in tree interval. It uses matrix exponentiation
	void update_states(double dh, FGY fgy)
	{
		DVector A = sp.getLineageStateSum();	
	
		DMatrix F = fgy.F;
		DMatrix G = fgy.G;
		DVector Y = fgy.Y;
		
		DVector A_Y = A.div(Y);
		A_Y.maxi(0); 
		A_Y.mini(1);
		
		// make R & Q 
		//~ (F(k,l) + G(k,l)) *  Q(x, l,z)/  std::max(Q(x,l,z), Y(l));
		
		DMatrix R = F.add(G); // R = (F+G)
		R.diviRowVector(Y);    // R = (F+G) /row Y
				
		DVector sumCols = R.columnSums(); // row vector
		for(int i=0; i < numDemes; i++)
			R.put(i,i,R.get(i,i)-sumCols.get(i)); // diagonal		
		R.muli(dh);
		
		
		// jblas is also column-wise
		DoubleMatrix expBlas = new DoubleMatrix(numDemes,numDemes, R.data); // shared data
		DoubleMatrix Qblas = MatrixFunctions.expm(expBlas);
		// normalise columns
		DMatrix Q = new DMatrix(numDemes,numDemes,Qblas.data);
		Q.diviRowVector(Q.columnSums()); 
		
		// P = Q.mmul(P);
		sp.mulExtantProbabilities(Q, true);
		// normalise columns
		Q.diviRowVector(Q.columnSums());
				
	}
	
	double getNextArrival(double lambda) {
		return rexp( Randomizer.nextDouble() ,lambda);
	}
	
	
	
	double rexp(double u, double lambda) {
		return -Math.log(u)/lambda;
	}
	
	double rpoisson(double lambda) {
		return Randomizer.nextPoisson(lambda);
	}
	
	// for sim genetic distances
	//DVector edge_length_substPerSite; 
	//DVector edge_length_substPerSite_nodeWise;
	
	void testLayout() {
		DoubleMatrix M = new DoubleMatrix(2,3);
		double n=0;
		for(int i=0; i < 2; i++) {
			for(int j=0; j < 3; j++) {
				M.put(i,j, n);
				n++;
			}
		}
		for(int i=0; i < 2; i++) {
			System.out.println(M.getRow(i));
		}
		System.out.println("Columns");
		for(int j=0; j < 3; j++) {
			System.out.println(M.getColumn(j));
		}
		System.out.println("Data: [");
		for(int i=0; i < 2*3; i++)
			System.out.print("  "+M.data[i]);
		System.out.println("  ]");
		
		DoubleMatrix M2 = new DoubleMatrix(2,3,M.data);
		M2.put(0, 0,55);
		for(int i=0; i < 2*3; i++)
			System.out.print("  "+M.data[i]);
		System.out.println("  ]");
		for(int i=0; i < 2*3; i++)
			System.out.print("  "+M2.data[i]);
		System.out.println("  ]");
		
	}

}
