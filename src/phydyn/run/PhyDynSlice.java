package phydyn.run;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import java.io.FileWriter;

import beast.core.Runnable;
import beast.core.Distribution;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.evolution.tree.Tree;
import phydyn.distribution.STreeLikelihood;
import phydyn.model.PopModelODE;


public class PhyDynSlice extends Runnable implements UnivariateFunction, MultivariateFunction {
	
	//public Input<STreeLikelihood> likelihoodInput = new Input<>("likelihood", "Tree likelihood", Validate.REQUIRED);
	//public Input<STreeDistribution> densityInput = new Input<>("density", "Tree density", Validate.REQUIRED);
	public Input<STreeLikelihood> densityInput = new Input<>("density", "Tree density", Validate.REQUIRED);
	
//	public Input<PopTrajectory> trajectoryInput = new Input<>("trajectory","Population Trajectory", Validate.REQUIRED);
	
	public Input<String> paramNameInput = new Input<>(
			 "paramName","Model Parameter Name",Validate.REQUIRED);	
	public Input<Double> startInput = new Input<>(
			 "start", "Slice lower bound / start of sequence" ,Validate.REQUIRED);
	public Input<Double> endInput = new Input<>(
			 "end", "Slice upper bound / end of sequence" ,Validate.REQUIRED);
	public Input<Integer> numPointsInput = new Input<>(
			 "numPoints", "Number of points in sequence" ,Validate.REQUIRED);
	
	 
	public Input<List<Double>> pfInitialInput = new Input<>(
			"pfInitial","Profiled Parameters initial values",new ArrayList<>());
	public Input<List<String>> pfNamesInput = new Input<>(
			 "pfNames","Profiled Parameter Names",new ArrayList<>());
	public Input<List<Double>> pfBoundsInput = new Input<>(
			"pfBounds","Profiled Parameters Bounds (lower and upper)",new ArrayList<>());
	 
	 
	 public Input<String> outputFileInput = new Input<>(
			 "outputFile","Output file (csv) name",Validate.REQUIRED);
	 public Input<Boolean> verboseInput = new Input<>(
			 "verbose","Print slice values");
	 
	 /* To do: two-dimensional slice and Initial Conditions calibration */
	
	//protected STreeDistribution density;
	protected STreeLikelihood density;
	//protected PopTrajectory trajectory;	
	protected String paramName, outputFile;
	boolean paramIsRate;
	int paramIndex;
	protected double startSeq, endSeq;
	protected int numPoints;
	// Profiled parameters
	protected int pfNum, pfBoundsNum;
	protected String[] pfNames;
	protected double[]  pfInitial, pfBoundsLower, pfBoundsUpper;
	protected boolean[] pfIsRate;
	protected int[] pfIndex;
	
	protected boolean verbose;
	
	protected PopModelODE model=null;
	
	// auxuliary variable - keeps track of value of IC that maximizes likelihhod
	protected double[] optPoint;
	
	@Override
	public void initAndValidate() { 
		int i,j,index;
		density = densityInput.get();
		
		//trajectory = trajectoryInput.get();		
		//model = trajectory.getModel();
		model = density.popModel;
		
		paramName = paramNameInput.get();
		paramIndex = model.getParamIndex(paramName);
		if (paramIndex == -1) { /* try initial conditions */
			paramIndex = model.getYindex(paramName);
			if (paramIndex==-1) {
				throw new IllegalArgumentException("Unknown parameter name: "+paramName);
			} else {
				paramIsRate=false;
			}
		} else {
			paramIsRate=true;
		}
		
	
		startSeq = startInput.get();
		endSeq =  endInput.get();
		numPoints = numPointsInput.get();
		
		/* Profiling */
		
		List<String> pfNamesList = pfNamesInput.get();
		pfNum = pfNamesList.size();
		pfBoundsNum=0; 
		if (pfNum > 0) {
			List<Double> pfInitialList = pfInitialInput.get();
			if (pfInitialList.size()==0)
				throw new IllegalArgumentException("Profiled parameters: initial values missing");
			if (pfInitialList.size() != pfNum) 
				throw new IllegalArgumentException("Profiled parameters: incorrect number of initial values");
			List<Double> pfBoundsList = pfBoundsInput.get();		
			pfBoundsNum = pfBoundsList.size();
			if (pfNum==1) { /* Univariate optimization needs bounds */
				if (pfBoundsNum==0)
					throw new IllegalArgumentException("Profiled parameters (Univariate): Bounds missing");
				if (pfBoundsNum != 2) 
					throw new IllegalArgumentException("Profiled Parameters: incorrect number Bound values");
			} else { /* Multivariate: Bounds optional */
				if ((pfBoundsNum>0)&&(pfBoundsNum != 2*pfNum))
					throw new IllegalArgumentException("Profiled parameters: incorrect number of Bound values");
			}
		
			// pfNames = (String[])pfNamesList.toArray();
			pfNames = new String[pfNum];
			pfIsRate = new boolean[pfNum];
			pfIndex = new int[pfNum];
			i=0;
			for(String pfName :pfNamesList) { 
				pfNames[i] = pfName; 
				index = model.getParamIndex(pfName);
				if (index==-1) {
					index = model.getYindex(pfName);
					if (index==-1)
						throw new IllegalArgumentException("Unknown model/trajectory parameter: "+pfName);
					pfIsRate[i] = false;
				} else {
					pfIsRate[i] = true;
				}
				pfIndex[i] = index;
				i++;
			}
			pfInitial = new double[pfNum];
			i=0;
			for(Double val: pfInitialList) 
				pfInitial[i++] = val;			
			pfBoundsLower = new double[pfNum];
			pfBoundsUpper = new double[pfNum];
			for (i=0,j=0; i < pfBoundsList.size();j++) {
				pfBoundsLower[j] = pfBoundsList.get(i++);
				pfBoundsUpper[j] = pfBoundsList.get(i++);
			}
			optPoint = new double[pfNum];
		}
		// Summary
		if (pfNum>0) {
			System.out.println("Profiled Parameters:");
			for(i=0; i < pfNum;i++) {
				if (pfIsRate[i]) System.out.print("Model Parameter");
				else System.out.print("Model Variable");
				System.out.print("("+pfIndex[i]+") ");
				System.out.print(pfNames[i]+": initial "+pfInitial[i]);
				if (pfBoundsNum>0)
					System.out.print(" [ "+pfBoundsLower[i]+" - "+pfBoundsUpper[i]+" ]");
				System.out.println("");
			}
		}
		
		outputFile = outputFileInput.get();
		if (verboseInput.get()==null) verbose = true;
		else verbose=verboseInput.get();
		

	}
	
	@Override
	public void run() throws Exception {
		System.out.println("running...");
	
		System.out.println("Slicing:"+startSeq+" / "+endSeq+" / "+numPoints);
		
		density.calculateLogP();
		//System.out.println("logP="+densityInput.get().getCurrentLogP());
				
		// timeTrajectory();
		computeLikelihoods(); 
		
		System.out.println("Done");
	}
	
	
	public void computeLikelihoods() throws Exception {
		double lh, max_lh = Double.NEGATIVE_INFINITY, max_coef=0.0;
		double stepSize;
		//long sum = 0;
		if (verbose) { 
			System.out.print(paramName+",logLH");
			for(int i=0; i < pfNum; i++)
				System.out.print(","+pfNames[i]);
			System.out.println("");
		}
		
		FileWriter writer = new FileWriter(outputFile);
		
		writer.append(paramName+",lh");
		for(int i=0; i < pfNum; i++)
			writer.append(","+pfNames[i]);
		writer.append("\n");
		
		stepSize = (endSeq-startSeq)/(numPoints-1);
		int num=0;
		for(double coef=startSeq; num<numPoints; coef += stepSize) {
			num++;
			if (num==numPoints) coef=endSeq;
			if (paramIsRate) {
				model.updateRate(paramName, coef);
			} else {
				model.updateParam(paramName,coef);
			}
			
			if (pfNum==0) {
				model.update();
				density.calculateLogP();// throws exception
				lh  = density.getCurrentLogP();
			} else if (pfNum==1) {
				lh = optimizeUni();
			} else {  // multivariate
				lh = optimizeMulti();
			}
			
			
			if (lh > max_lh) { max_lh = lh; max_coef = coef; }
			if (verbose) {
				System.out.print(coef+ "," + lh);
				for(int i=0; i < pfNum; i++)
					System.out.print(","+optPoint[i]);
				System.out.println("");
			}
			if ((new Double(lh)).compareTo(Double.NEGATIVE_INFINITY)==0) {
				writer.append(coef+","+"-Inf");
			} else {
				writer.append(coef+","+lh);
			}
			for(int i=0; i < pfNum; i++) {
				writer.append(","+optPoint[i]);
			}
			writer.append("\n");
		}
		System.out.println("\n--- MAX ----\ncoef: "+max_coef+" maxLH="+max_lh);
		writer.flush();
	    writer.close();
		//System.out.println("Total:"+sum);
		
	}
	
	public double optimizeUni() throws Exception {
		double maxLh=0;
		
		BrentOptimizer optimizer = new BrentOptimizer(1e-6, 1e-12);
        UnivariatePointValuePair optimum =
                optimizer.optimize(new UnivariateObjectiveFunction(this),
                                   new MaxEval(100),
                                   GoalType.MAXIMIZE,
                                   new SearchInterval(pfBoundsLower[0],pfBoundsUpper[0])); 
		
		
		//double point = optimum.getPoint();
		//System.out.print("--> "+paramName+": "+paramValue+" point= "+point);		
		//System.out.print(" ");
		//System.out.println("value = "+ optimum.getValue());
		maxLh = optimum.getValue();	
		optPoint[0] = optimum.getPoint();
		return maxLh;
	}

	
	public double optimizeMulti() {
		double maxLh=0;
		PointValuePair optimum;
		if (pfBoundsNum>0) {
			BOBYQAOptimizer optimizer = new BOBYQAOptimizer(2*pfNum); // 2*point.length + 1+additional
			optimum = optimizer.optimize(
					new MaxEval(150), 
	                new ObjectiveFunction(this), 
	                GoalType.MAXIMIZE, 
	                new InitialGuess(pfInitial), 
	                new SimpleBounds(pfBoundsLower, pfBoundsUpper)
	                );
		} else {	
			NelderMeadSimplex optMethod = new NelderMeadSimplex(pfNum);
			SimplexOptimizer optimizer = new SimplexOptimizer(1e-5, 1e-10);
			optimum = optimizer.optimize(
					new MaxEval(200), 
	                new ObjectiveFunction(this), 
	                GoalType.MAXIMIZE, 
	                new InitialGuess(pfInitial), 
	                optMethod);
		}
			
		double[] point = optimum.getPoint();
		maxLh = optimum.getValue();
		// System.out.println("CHECKING: "+pfNum+" must be "+point.length);
		for(int i=0; i< point.length; i++) optPoint[i] = point[i];
		
		return maxLh;
	}
	

	@Override
	public double value(double arg0) { // Univariate
		double logP;
		if (pfIsRate[0]) {
			model.updateRate(pfNames[0], arg0);
		} else {
			model.updateParam(pfNames[0],arg0);
		}
		model.update();
		
		try {
			logP = density.calculateLogP();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logP =  Double.NaN;
		}
		//System.out.println("arg0 "+arg0+" value"+logP);
		return logP;
		
		
	}

	@Override
	public double value(double[] arg0) {  // Multivariate
		double logP;
		//System.out.println("MULTI VALUE");
		for(int i=0; i < pfNum; i++) {
			if (pfIsRate[i]) {
				model.updateRate(pfNames[0], arg0[i]);
			} else {
				model.updateParam(pfNames[0],arg0[i]);
			}
		}
		model.update();
		
		try {
			logP = density.calculateLogP();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logP =  Double.NaN;
		}
		//System.out.println("arg0 "+arg0+" value"+logP);
		return logP;
		
	
	}

}
