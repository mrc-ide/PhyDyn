package phydyn.distribution;

import java.util.List;
import java.lang.Math;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import beast.core.Citation;
import beast.core.Description;
import beast.core.Input;

import beast.evolution.tree.Node;
import beast.evolution.tree.TraitSet;

import beast.evolution.tree.coalescent.IntervalType;
import phydyn.model.PopModelODE;
import phydyn.model.TimeSeriesFGY;

// import beast.phylodynamics.model.EpidemiologicalModel;


/**
 * @author Igor Siveroni
 * based on original code by David Rasmussen, Nicola Muller
 */

@Description("Calculates the probability of a beast.tree  under a structured population model "
		+ "using the framework of Volz (2012).")
@Citation("Erik M. Volz. 2012. Complex population dynamics and the coalescent under neutrality."
		+ "Genetics. 2013 Nov;195(3):1199. ")
public abstract class STreeLikelihood extends STreeGenericLikelihood  {
	
	
	// replace above with model - already inherited
	public Input<Boolean> fsCorrectionsInput = new Input<>(
			"finiteSizeCorrections", "Finite Size Corrections", new Boolean(false));
	
	public Input<Boolean> approxLambdaInput = new Input<>(
			"approxLambda", "Use approximate calculation of Lambda (sum)", new Boolean(false));
	
	public Input<Double> forgiveAgtYInput = new Input<>(
			"forgiveAgtY", "Threshold for tolerating A (extant lineages) > Y (number demes in simulation): 1 (always), 0 (never)", 
			new Double(1.0));
	
	public Input<Double> penaltyAgtYInput = new Input<>(
			"penaltyAgtY", "Penalty applied to likelihod after evaluating A>Y at the end of each interval",
			new Double(1.0));
	
	public Input<Boolean> forgiveYInput = new Input<>("forgiveY",
			"Tolerates Y < 1.0 and sets Y = max(Y,1.0)",  new Boolean(false));
	
	/* inherits from STreeGenericLikelihood:
	 *  public PopModelODE popModel;
	 	public TreeInterface tree;
	 	public STreeIntervals intervals;
	 	public int numStates; 	 
	 	protected int[] nodeNrToState; 
	 */
		   
    public TimeSeriesFGY ts;
	public int samples;
	public int nrSamples;
	
	// state probabilities array
	public DoubleMatrix[] sp; 
	
	// loop variables
	protected int tsPoint;
	double h, t, tsTimes0;
      
    // debug
    protected int numCoal=0;
    double lhnocoal, lhcoaldiag, lhcoal, lhinterval;
    
    @Override
    public void initAndValidate() {
    	super.initAndValidate(); /* important: call first */
    	stateProbabilities = new StateProbabilitiesArray(); // declared in superclass
    	initValues();
 
    }
    
    public void initValues()   {
    	// if t1 has t1 Input - do nothing
    	// Set t1 to Tree's height
    	//System.out.print("interval duration: "+intervals.getTotalDuration());
    	if (!popModel.hasEndTime()) {
    		if (tree.getDateTrait()==null) {
    			//System.out.println(" NO date trait, setting t1 = "+intervals.getTotalDuration());
    			popModel.setEndTime( intervals.getTotalDuration() );
    		} else {
    			if (tree.getDateTrait().getTraitName().equals( TraitSet.DATE_BACKWARD_TRAIT)) {
    				//System.out.println(" backward date trait, setting t1 = "+intervals.getTotalDuration());
    				popModel.setEndTime( intervals.getTotalDuration());
    			} else {  
    				//System.out.println(" Date trait, setting t1 = "+ tree.getDateTrait().getDate(0) );
    				popModel.setEndTime( tree.getDateTrait().getDate(0));
    			}
    		}
    	} else {
    		//System.out.println(" Using t1 = "+popModel.getEndTime());
    	}
    	
        nrSamples = intervals.getSampleCount();
        sp = new DoubleMatrix[nrSamples];               
        nrSamples++;
        // Extant Lineages and state probabilities
        stateProbabilities.init(tree.getNodeCount(), numStates);
    	    	
    }
    
       
    public PopModelODE getModel() { return popModel; }
    
    public double calculateLogP() {
 
    	//System.gc();
    	//System.runFinalization();
    	   	
    	super.initValues();
    	initValues();
    	
    	// TODO: if popModel is constant then popModel only needs to compute a single point
    	boolean reject = popModel.update(); // compute timeseries
    	ts = popModel.getTimeSeries();
    	
    	double trajDuration = popModel.getEndTime() - popModel.getStartTime();
    	   	
    	if (trajDuration < intervals.getTotalDuration()) {
	    	System.out.println("t0 too low - using constant population coalescent for missing population info");   		
        }
        
        if (reject) {
        	System.out.println("rejecting population update");
            logP = Double.NEGATIVE_INFINITY;
            return logP;
        }
        
        // stateProbabilities.init(tree.getNodeCount()); -- done in initValues
        logP = 0;  
        
        // declarations
        final int intervalCount = intervals.getIntervalCount();
        final double[] tsTimes  =  ts.getTimePoints(); 	// array of time points
        // final int tsCount = tsTimes.length;				// number of points in time-series
        double pairCoalRate;
  
        IntervalType intervalType;
       
        // initialisations        		
        int interval = 0, numLineages, numExtant, numLeaves;  	// first interval
        tsPoint = 0;
        h = 0.0;		// used to initialise first (ho,h1) interval 
        t = tsTimes0 = tsTimes[0];
        lhnocoal = lhcoal = 0.0;
        numLeaves = tree.getLeafNodeCount();
        double duration;
       
        
        do { 
        	duration = intervals.getInterval(interval);
        	if (trajDuration < (h+duration)) break;
        	lhinterval = processInterval(interval, duration, tsTimes);
        	    	
        	if (lhinterval == Double.NEGATIVE_INFINITY) {
    			logP = Double.NEGATIVE_INFINITY;
				return logP;
    		} 	
        		
        	// Assess Penalty
        	numExtant = stateProbabilities.getNumExtant();
        	double YmA = ts.getYs()[tsPoint].sum() - numExtant;
        	if (YmA < 0) {
        		//System.out.println("Y-A < 0");
        		if ((numExtant/numLeaves) > forgiveAgtYInput.get()) {
        			System.out.println("Minus Infinity: A > Y");
        			lhinterval = Double.NEGATIVE_INFINITY;
        			logP = Double.NEGATIVE_INFINITY;
        			return logP;
        		} else {
        			lhinterval += lhinterval*Math.abs(YmA)*penaltyAgtYInput.get();
        		}
        	}      	
        	lhnocoal += lhinterval;
        	logP += lhinterval;
 
        	
        	// Make sure times and heights are in sync
        	// assert(h==hEvent) and assert(t = tsTimes[0] - h)
        	     	
        	intervalType = intervals.getIntervalType(interval);
        	// Process Event
        	if (intervalType == IntervalType.SAMPLE) {
       			processSampleEvent(interval);
        	} else if (intervalType == IntervalType.COALESCENT) {
        		//yTotal = ts.getYs()[tsPoint].sum();  // Y total
        		
        		//if (yTotal < 1.0) {
        		//	System.out.println("Minus infinity: ytotal < 1.0");
        		//	logP = Double.NEGATIVE_INFINITY;
        		//	return logP;
        		//}
        		
        		// tsClosestPoint = ((tsTimes[tsPoint]-t) <(t-tsTimes[tsPoint+1]))?tsPoint:(tsPoint+1);
        		pairCoalRate = processCoalEvent(tsPoint, interval);
        		
        		if (pairCoalRate >= 0) {
        			logP += Math.log(pairCoalRate);
        			lhcoal +=  Math.log(pairCoalRate);
        		}
        	}
        	
        	
        	if (Double.isNaN(logP)) {
        		System.out.println("NAN - quitting likelihood");
        		logP = Double.NEGATIVE_INFINITY;
				return logP;
        	}
        	
        	
        	if (logP == Double.NEGATIVE_INFINITY) {
    			//logP = Double.NEGATIVE_INFINITY;
				return logP;
    		} 
    		       	
        	interval++;
        } while (interval < intervalCount);
        
        // Default: Coalescent with constant population size
        // double Ne = ?
        if (interval < intervalCount) { // root < t0
        	double comb, coef, lambda;
        	// process first half of interval
        	duration = trajDuration - h;
        	lhinterval = processInterval(interval, duration, tsTimes);
        	// at this point h = trajDuration
        	// process second half of interval
        	duration = intervals.getInterval(interval)-duration;
        	numLineages = intervals.getIntervalCount();
        	comb = numLineages*(numLineages-1)/2.0;
        	coef = comb/Ne;
        	if (NeInput.get()==null) {
        		lambda = computeLambdaSum(tsPoint);
        		Ne = comb/lambda;  // should be user input - first trying this
        	} else {
        		Ne = NeInput.get().getValue();
        	}       	
        	logP += coef*duration + Math.log(1/Ne);
        	interval++;
        	// process remaining intervals
        	while (interval < intervalCount) {
        		duration = intervals.getInterval(interval);       		
        		numLineages = intervals.getIntervalCount();
        		coef = numLineages*(numLineages-1)/Ne;
            	logP += coef*duration + Math.log(coef);
        		interval++;
        	}
        	
        }       

        ts = null;
        // System.out.println("Likelihood="+logP);
        if (Double.isInfinite(logP)) logP = Double.NEGATIVE_INFINITY;
        return logP;
   	
    }
    
    // Fastest way to sort this out if we decide to fix all Ys beforehand
    public void fixYs(TimeSeriesFGY ts, double minY) {
    	DoubleMatrix[] Ys = ts.getYs();
    	for(int i=0; i < Ys.length; i++) {
    		Ys[i].maxi(minY);
    	}
    }
    
    /* updates t,h,tsPoint and lineage probabilities */
    /* default version: logLh=0, state probabilities remain unchanged */
    protected double processInterval(int interval, double intervalDuration, double[] tsTimes ) {
        double segmentDuration;
        
    	double hEvent = h + intervalDuration; 		// event height
    	double tEvent = tsTimes[0] - hEvent;      // event time
    	
    	// traverse timeseries until closest latest point is found
    	// tsTimes[tsPoint+1] <= tEvent < tsTimes[tsPoint] -- note that ts points are in reverse time
    	// t = tsTimes[0] - h;
    	
    	// Process Interval
    	double lhinterval = 0;
    	while (tsTimes[tsPoint+1] > tEvent) {
    		segmentDuration = t - tsTimes[tsPoint+1];
    		// lhinterval += processIntervalSegment(tsPoint,segmentDuration);
    		if (lhinterval == Double.NEGATIVE_INFINITY) {
    			return Double.NEGATIVE_INFINITY;
    		} 				
    		t = tsTimes[tsPoint+1];
    		h += segmentDuration;
    		tsPoint++;
    		// tsTimes[0] = t + h -- CONSTANT
    	}
    	// process (sub)interval before event
    	segmentDuration = hEvent - h;  // t - tEvent
    	if (segmentDuration > 0) {
    		// lhinterval += processIntervalSegment(tsPoint,segmentDuration);
    		
    		if (lhinterval == Double.NEGATIVE_INFINITY) {
    			return Double.NEGATIVE_INFINITY;
    		} 	
    	}
    	// update h and t to match tree node/event
    	h = hEvent;
    	t = tsTimes[0] - h;
    	return lhinterval;
    }
    
          
    protected double processCoalEvent(int t, int currTreeInterval) {
    	List<DoubleMatrix> coalVectors = stateProbabilities.getCoalescentVectors(intervals, currTreeInterval);
    	DoubleMatrix pvec1, pvec2;	
    	pvec1 = coalVectors.get(0);
    	pvec2 = coalVectors.get(1);
    	
    	
		List<Node> parentLines = intervals.getLineagesAdded(currTreeInterval);
		if (parentLines.size() > 1) throw new RuntimeException("Unsupported coalescent at non-binary node");			
		//Add parent to activeLineage and initialise parent's state prob vector
		Node parentNode = parentLines.get(0);
		

		//Compute parent lineage state probabilities in p				
		DoubleMatrix F,Y;
    	Y = ts.getYs()[t];
    	F = ts.getFs()[t];		
    	
    	 if (forgiveYInput.get()) {
 			Y.maxi(1.0);
 		} else { // however, Y_i > 1e-12 by default
 			Y.maxi(1e-12); 
 		}
 
    	 double lambda;
	    /* Compute Lambda Sum */
		DoubleMatrix pa;
	    DoubleMatrix pi_Y = pvec1.div(Y);
	    pi_Y.reshape(numStates, 1);	    	
	    DoubleMatrix pj_Y = pvec2.div(Y);
	    pj_Y.reshape(numStates, 1);	
	    pa = pi_Y.mul(F.mmul(pj_Y));	    	
	    pa.addi(pj_Y.mul(F.mmul(pi_Y)));
	    pa.reshape(1,numStates);
	    lambda = pa.sum();
	    pa.divi(lambda);
					
		stateProbabilities.addLineage(parentNode.getNr(),pa);
	
		sp[parentNode.getNr() - nrSamples] = (pa);
 
		//Remove child lineages
		List<Node> coalLines = intervals.getLineagesRemoved(currTreeInterval);
		stateProbabilities.removeLineageNr(coalLines.get(0).getNr() );
		stateProbabilities.removeLineageNr(coalLines.get(1).getNr()); 
	
		if (fsCorrectionsInput.get()) {
			doFiniteSizeCorrections(parentNode,pa);
		}
		
		return lambda;
    }
        
    /*
     * tspoint: Point in time series (trajectory)
     */
    protected double computeLambdaSum(int tsPoint) {
    	DoubleMatrix A = stateProbabilities.getLineageStateSum();
		double lambdaSum = 0.0; // holds the sum of the pairwise coalescent rates over all lineage pairs
		int numExtant = stateProbabilities.getNumExtant();

		if (numExtant < 2) return lambdaSum;	// Zero probability of two lineages coalescing	
	
		DoubleMatrix F, Y;
		F = ts.getFs()[tsPoint];
		Y = ts.getYs()[tsPoint];
		
		Y.maxi(1e-12);  // Fixes Y lower bound
		/*
		 * Sum over line pairs (scales better with numbers of demes and lineages)
		 */	
			
		if (approxLambdaInput.get()) {  // (A/Y)' * F * (A/Y)
			DoubleMatrix A_Y = A.div(Y);
			return A_Y.dot(F.mmul(A_Y));
		}
		
		
		/*
		 * Simplify if F is diagonal.
		 */
		DoubleMatrix popCoalRates,pI;
    	if(popModel.isDiagF()){
			popCoalRates = new DoubleMatrix(numStates);
			for (int k = 0; k < numStates; k++){
				final double Yk = Y.get(k); 
				if(Yk>0)
					popCoalRates.put(k, (F.get(k,k) / (Yk*Yk)));
			}
			DoubleMatrix sumStates = DoubleMatrix.zeros(numStates);
			DoubleMatrix diagElements = DoubleMatrix.zeros(numStates);
			for (int linI = 0; linI < numExtant; linI++) {
				pI = stateProbabilities.getStateProbsFromIndex(linI);
				sumStates = sumStates.add( pI);
				diagElements = diagElements.add(pI.mul(pI));
			}
			DoubleMatrix M = sumStates.mul(sumStates);
			lambdaSum = M.sub(diagElements).mul(popCoalRates).sum();
			
			// System.out.println("coalRates:"+2*popCoalRates);
			
		/*
		 * if the transmission matrix has off diagonal elements, use the standard
		 * way of calculating the interval contribution	
		 */
    	} else {
    		DoubleMatrix pi_Y, pj_Y, pa, pJ;
			for (int linI = 0; linI < numExtant; linI++) {
				pI = stateProbabilities.getStateProbsFromIndex(linI);
				pi_Y = pI.div(Y);
				pi_Y.reshape(numStates, 1);
				for (int linJ = linI+1; linJ < numExtant; linJ++) {
					pJ = stateProbabilities.getStateProbsFromIndex(linJ);
					pj_Y = pJ.div(Y);
					pj_Y.reshape(numStates, 1);
					pa = pi_Y.mul(F.mmul(pj_Y));
					pa.addi(pj_Y.mul(F.mmul(pi_Y)));
					lambdaSum += pa.sum();
				
				}	
			}
    	}
  
    	return lambdaSum;    	
    }
    
  
 
 

 
    private void doFiniteSizeCorrections(Node alphaNode, DoubleMatrix pAlpha) {
    	DoubleMatrix p, AminusP;
    	int numExtant = stateProbabilities.getNumExtant();    	
    	int alphaIdx =  stateProbabilities.getLineageIndex(alphaNode.getNr());
    	
    	DoubleMatrix A = stateProbabilities.getLineageStateSum();
    	double sum;    	
    	// traverse all extant lineages
    	for(int lineIdx=0; lineIdx < numExtant; lineIdx++) {
    		if (lineIdx!=alphaIdx) {
    			p = stateProbabilities.getStateProbsFromIndex(lineIdx);
    			AminusP = A.sub(p);
    			AminusP.maxi(1e-12);
    			// rterm = p_a / clamp(( A - p_u), 1e-12, INFINITY );
    		    DoubleMatrix rterm = pAlpha.div(AminusP);
    		    //rho = A / clamp(( A - p_u), 1e-12, INFINITY );
    		    DoubleMatrix rho = A.div(AminusP);
                //lterm = dot( rho, p_a); //
    		    double lterm = rho.dot(pAlpha);
                //p_u = p_u % clamp((lterm - rterm), 0., INFINITY) ;
    		    rterm.rsubi(lterm);
    		    //rterm.subi(lterm);
    		    //rterm.muli(-1);
    		    rterm.maxi(0.0);
    		    // p = p.muli(rterm); 
    		    sum = p.dot(rterm);
    		    if (sum > 0) {  // update p
    		    	p.muli(rterm); // in-place element-wise multiplication,
    		    	p.divi(sum);  // in-pace normalisation
    		    }
    		}   		
    	}
    	
    }
    
    /* currTreeInterval must be a SAMPLE interval i.e. the incoming lineage must be a Leaf */
    protected void processSampleEvent(int interval) {
    	int sampleState;
		List<Node> incomingLines = intervals.getLineagesAdded(interval);
		for (Node l : incomingLines) {			
			/* uses pre-computed nodeNrToState */
			sampleState = nodeNrToState[l.getNr()];	/* suceeds if node is a leaf, otherwise state=-1 */	
			DoubleMatrix sVec = DoubleMatrix.zeros(1,numStates); // row-vector
			sVec.put(sampleState, 1.0);
			stateProbabilities.addLineage(l.getNr(),sVec);
		}	
    }
 
    
    @Override
    protected boolean requiresRecalculation() {
    	return true;
    }
    
   
    
   
}
