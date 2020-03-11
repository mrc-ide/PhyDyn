package phydyn.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;
import org.apache.commons.math3.ode.nonstiff.AdamsMoultonIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import org.apache.commons.math3.ode.nonstiff.GillIntegrator;
import org.apache.commons.math3.ode.nonstiff.HighamHall54Integrator;
import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import beast.core.BEASTObject;
import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;
import phydyn.analysis.PopModelAnalysis;
import phydyn.analysis.XMLFileWriter;
import phydyn.util.DMatrix;
import phydyn.util.DVector;
import phydyn.util.General.IntegrationMethod;


/**
 * Class describing a birth-death population genetics model 
 * in terms of Ordinary Differential Equations.
 *
 * @author Igor Siveroni
 *
 */
	
	
public class PopModelODE extends PopModel  implements FirstOrderDifferentialEquations, StepHandler {
	 
	 public Input<List<MatrixEquation>> matrixEquationsInput = new Input<>(
	            "matrixeq",
	            "Equations used to generate the rate matrices",
	            new ArrayList<>() );
	 
	 public Input<String> matrixEquationsStringInput = new Input<>(
	            "matrixeqs",
	            "Equations used to generate the rate matrices");
		 
	 public Input<List<Definition>> definitionsInput = new Input<>(
			 "definition","List of definitions var = exp",new ArrayList<>());
	 
	 public Input<String> definitionsStringInput = new Input<>(
			 "definitions","Semi-colon separated list of assignment statements");
	 
	 
	 public Input<String> evaluatorTypeInput = new Input<>(
			 "evaluator","Evaluator type: interpreter, compiled, optimised","compiled");
	 
	 // Potential parameters
	 public Input<TrajectoryParameters> popParametersInput = new Input<>(
				"popParams", "Population Model Trajectory Parameters",Validate.REQUIRED);
		
	 public Input<ModelParameters> modelParametersInput = new Input<>(
				"modelParams", "Population Model Rate Parameters",Validate.REQUIRED);
		
	 public TrajectoryParameters trajParams;	
	 public ModelParameters modelParams;

	 
	 public int yLength;
	 public String[] yNames;
	 public String alldemes;
	 
	 protected List<String> defNames;
	 public List<DefinitionObj> definitions;
	 
	 protected Set<String> allParameters;
	 
	 	 
	 // now part of modelParams
	 //public String[] rateNames;
	 //protected double[] rateValues;
	
	 /* Integration parameters */
	 protected double[] y0, y1;
	 protected double[] t0t1;
	 public boolean fixedStepSize;
	 // protected double t0, t1;
	 //protected int nsteps;
	 // y = (I0,I1,I2,S)
	 TimeSeriesFGY timeseries;
	 DMatrix births,migrations;
	 DVector deaths;
	 double[] nondemeYdot;
	 double[] eqValues;
	 
	 EquationEvaluatorAPI eqEvaluator;
	 String evaluatorType;
	 private boolean useT, useT0T1;
	 
	 public List<MatrixEquationObj> equations;
	 
	@Override
	public void initAndValidate() {
		List<MatrixEquation> equationsXML = matrixEquationsInput.get();
		// Deme and NonDeme names are extracted from Matrix Equations
		List<String> ldemes = new ArrayList<>();
		List<String> lnondemes = new ArrayList<>();
		
		// Create/Collect equations
		equations = new ArrayList<>();
		for(MatrixEquation eqxml: equationsXML) {
			equations.add(eqxml.createMatrixEquation());
		}
		if (matrixEquationsStringInput.get() != null) {
			final String eqsString = matrixEquationsStringInput.get();		
			List<MatrixEquationObj> eqs = MatrixEquations.createMatrixEquations(eqsString);
			for(MatrixEquationObj eq: eqs) {
				equations.add(eq);
			}
		}
			
		// todo: use Set<String>
		// Collect deme and non-deme names from equations
		String eqName;
		for(MatrixEquationObj eq: equations) {
			eqName = eq.originName;
			if (eq.type == EquationType.NONDEME) {
				if (!lnondemes.contains(eqName)) lnondemes.add(eqName);
			} else {
				if (!ldemes.contains(eqName)) ldemes.add(eqName);
			}
		}
			
		numDemes = ldemes.size();
		demeNames = new String[numDemes];
		ldemes.toArray(demeNames);
		// collect non-deme names
		numNonDemes = lnondemes.size();
		nonDemeNames = new String[numNonDemes];
		lnondemes.toArray(nonDemeNames);
		
		yLength = numDemes+numNonDemes;
		yNames = new String[yLength];
		int i, j;
		alldemes = "";
		for(i=0,j=0; j < numDemes; i++,j++) { yNames[i] = demeNames[j]; alldemes+=demeNames[i]; }
		for(j=0; j < numNonDemes; i++,j++) yNames[i] = nonDemeNames[j];
		
		// y values - auxiliary arrays
		y0 = new double[yLength];
		y1 = new double[yLength];
		t0t1 = new double[2];
		
		modelParams = modelParametersInput.get();
		
		// process equations
		diagF = true;
		
		for(MatrixEquationObj eq: equations) {
			eq.completeValidation(this);
		}
		eqValues = new double[equations.size()];
		
		// Definitions
		if (definitionsStringInput.get()==null ) {			
			definitions = new ArrayList<>();
			for (Definition defxml:  definitionsInput.get()) {
				definitions.add(  defxml.createDefinition()  );
			}
		} else {
			if (definitionsInput.get().size()>0) {
				System.out.println("PopModelODE Input Error. Definitions must be introduced by either:");
				System.out.println("- A single Definitions object (a semi-colon separated string) XOR");
				System.out.println("- A series of Definition objects");
				throw new IllegalArgumentException("PopModelODE Input error");
			}
 			definitions = Definitions.createDefinitions(definitionsStringInput.get());
		}
		
		
		// Collect definition names
		defNames = new ArrayList<>();
		for (DefinitionObj def: definitions) {
			defNames.add(def.name);
		}
		
		
		/* Derivatives related */
		births = migrations= null;
		deaths = null;
		nondemeYdot = null;
		
		// Check if variables are defined
		// Keep track of special variables and constants used.
		SemanticChecker checker = new SemanticChecker();
		if (checker.check(this)) {
			throw new IllegalArgumentException("Error(s) found in model formulae");
		}
		useT = checker.useT;
		useT0T1 = checker.useT0T1;
		
		/* Equation Evaluator */
		evaluatorType = evaluatorTypeInput.get();
		if (evaluatorType.equals("interpreter")) {
			eqEvaluator = new EquationInterpreter(this,checker);
		} else if (evaluatorType.equals("compiled")) {
			eqEvaluator = new EquationMachine(this, checker);
		} else {
			evaluatorType = "interpreter";
			System.out.println("Warning: Unknown Evaluator type. Using default");
			eqEvaluator = new EquationInterpreter(this,checker);
		}
		
		// Process input parameters
		trajParams = popParametersInput.get();
		if (trajParams.initAndValidateModel(this)) {
			throw new IllegalArgumentException("Error(s) found in Trajectory Parameters");
		}
		
		// collect all parameters
		allParameters = new HashSet<String>();
		for (i=0; i < yNames.length; i++) {
			allParameters.add(yNames[i]);
		}
		for(i=0; i < modelParams.paramNames.length; i++) {
			if (!allParameters.add(modelParams.paramNames[i])) {
				System.out.println("Repeated model parameter name ("+modelParams.paramNames[i]+")");
				throw new IllegalArgumentException("Error(s) found in model parameters");
			}
		}
	}	
	
	
	/* CalculationNode Interface */
	@Override
	public boolean requiresRecalculation() {
		// called if any of the Inputs was updated - no need to check more
		//modifiedValues = true;
		return true;
	}
	
	@Override
	public void store() {
		//System.arraycopy(paramValues, 0, storedParamValues, 0, numParams);
		//modifiedValues = false;
	}
	
	public void restore() {
		//double[] tmp = paramValues;
		//paramValues = storedParamValues;
		//storedParamValues = tmp;
		//modifiedValues = false;
	}
	
	@Override
	public String getName() {
		return this.getID();
	}
	
	@Override
	public void printModel() {
		System.out.println(this.toString());	
		//printer.printModel(this);
	}
	
	@Override
	public String toString() {
		String s="";
		PopModelODEPrinter printer = new PopModelODEPrinter();
		s += ("model-name = "+this.getID()+";\n");
		s += "definitions = {\n";		
		for(DefinitionObj def: this.definitions) {	
			s += "  "+printer.visit(def.stm)+"\n";
		}
		s += "}\nequations = {\n";		
		for(MatrixEquationObj eq: this.equations) {
			s += "  "+eq.getLHS() + " = " + printer.visit(eq.rhsExprCtx)+";\n";
			//System.out.print("  "+eq.getLHS() + " = ");
			//System.out.println(printer.visit(eq.rhsExprCtx)+";");
		}
		s +="}\n";
		s += (this.modelParams.toString())+"\n";
		s += (this.trajParams.toString());
		return s;
	}
	
	
	/* End CalculationNode Interface */
	
	@Override
	public boolean hasEndTime() { return  trajParams.t1Input.get()!= null; }
	public double getEndTime() { return trajParams.t1; }
	public void setStartTime(double newT0) { 
		// keep this for the time being
		throw new IllegalArgumentException("Can't change t0 for non-constant populations");
	}
	public double getStartTime() { return trajParams.getStartTime(); }
	public void setEndTime(double newt1)  {
		trajParams.setEndTime(newt1); 
	} // integrate again
	// (below) removed until we figure out how to keep track changes of t0Input/t0
	//public void setStartTime(double newt0) { trajParams.t0 = newt0; } // integrate again
	public void unsetEndTime() {
		trajParams.unsetEndTime();
	}
	
	/* needed by Density class */
	public boolean update() {
		integrate(trajParams, modelParams);
		return false; // reject = false
	}
	
	public TimeSeriesFGY getTimeSeries() { return timeseries; }
	
	
	protected void init_population(TrajectoryParameters params) {
		params.updateValues();
		double[] initialValues = params.paramValues;
		/* <deme,nondeme> order as entered in model	*/
		
		for(int i=0; i < initialValues.length; i++) {
			y0[i] = initialValues[i];			
		}
		//params.print();
	}
	
	
	/* NEW: used for maximum likelihood parameter inference */
	/* Re-think this */
	public void updateRate(String rateName, double v) {
		int i = this.indexOf(modelParams.paramNames, rateName);
		if (i==-1) 
			throw new IllegalArgumentException("Rate name invalid: "+rateName);
		modelParams.paramValues[i] = v;
		// update in evaluator
		eqEvaluator.updateRate(rateName, v);
	}
	
	public boolean updateParam(String paramName, double paramValue) {
		return trajParams.updateParam(paramName, paramValue);
	}
		
	//public double getEndTime() { return t1; }
	//public void setEndTime(double newt1) { t1 = newt1; } // integrate again
	
	@Override
	public boolean isParameter(String paramName) {
		return allParameters.contains(paramName);
	}
	
	@Override
	public String getParameterValue(String paramName) {
		Double r;
		String result = "null";
		r = trajParams.getParam(paramName);
		if (r==null) {
			r = modelParams.getParam(paramName);
			if (r!=null) {
				result = Double.toString(r);
			}
		} else {
			result = Double.toString(r);
		}
		return result;
	}

	/*  Rates, etc  */
	public int getParamIndex(String paramName) {
		return this.indexOf(modelParams.paramNames, paramName);
	}
	
	public int getYindex(String yName) {
		return this.indexOf(yNames, yName);
	}
	
	public TimeSeriesFGY integrate() {
		return integrate(trajParams,modelParams);
	}
	
	// integrate(double[] y0, double t0, double t1, int nsteps, double[] y1) 
	public TimeSeriesFGY integrate(TrajectoryParameters trajParams, ModelParameters modelParams) {
		//double t0,t1;
		int nsteps;
		IntegrationMethod method;
		
		/* extract arguments */
		if (!trajParams.hasEndTime()) {
			throw new IllegalArgumentException("PopModelODE: end-time (t1) missing.");
		}
		t0t1[0] = getStartTime();
		t0t1[1] = trajParams.t1;
		// System.out.println("t0="+t0t1[0]+"  t1="+t0t1[1]);
	
		nsteps = trajParams.integrationSteps;
		method = trajParams.method;
		fixedStepSize = trajParams.fixedStepSize;
		
		/* we should include the initial deme/nondeme values as parameters as well */
		/* extract and update environment here */
		
		init_population(trajParams);
		// if (interpreter) eqEvaluator.clear_env()
		// Performed once before evaluation of ODE equations
		modelParams.updateValues();  // update paramValues array in case there was change in Parameters - overkill
		//modelParams.print();
		eqEvaluator.updateRates(modelParams.paramValues); // assuming RateParameters object agrees with model
		eqEvaluator.updateRateVectors(modelParams.paramVectorValues); // testing vectors
		
		
		if (useT0T1) {
			eqEvaluator.updateT0T1(t0t1);
		}
		
		
		int i;
		FirstOrderIntegrator foi=null;
		double fixedStepSize = (t0t1[1]-t0t1[0])/(nsteps-1);
		double minStep,maxStep;
		minStep = 0.0;
		maxStep = 0.5;
		// adaptive size: order and tolerances provided by user
		switch(method) {
		case EULER: foi = new EulerIntegrator(fixedStepSize); break;
		case MIDPOINT: foi = new MidpointIntegrator(fixedStepSize); break;
		case CLASSICRK: foi = new ClassicalRungeKuttaIntegrator(fixedStepSize); break;
		case GILL: foi = new GillIntegrator(fixedStepSize); break;
		case ADAMSBASHFORTH: foi = new AdamsBashforthIntegrator(trajParams.order,minStep,maxStep,trajParams.aTol,trajParams.rTol); break;
		case ADAMSMOULTON: foi = new AdamsMoultonIntegrator(trajParams.order,minStep,maxStep,trajParams.aTol,trajParams.rTol); break;
		case HIGHAMHALL: foi = new HighamHall54Integrator(minStep,maxStep,trajParams.aTol,trajParams.rTol); break;
		}
		// --- could not import LutherIntegrator
		// FirstOrderIntegrator foi = new ThreeEighthesIntegrator((t1-t0)/(steps-1));
		/*
		public AdamsMoultonIntegrator(int nSteps,
                double minStep,
                double maxStep,
                double scalAbsoluteTolerance,
                double scalRelativeTolerance)
                 throws NumberIsTooSmallException
		*/
		
		/*
		 * public HighamHall54Integrator(double minStep,
                double maxStep,
                double scalAbsoluteTolerance,
                double scalRelativeTolerance)
		 */
		
		foi.addStepHandler(this);
		
		/* initialize matrices */
		births = new DMatrix(numDemes,numDemes);
		migrations= new DMatrix(numDemes,numDemes);
		deaths = new DVector(numDemes);
		if (numNonDemes > 0) {
			nondemeYdot = new double[numNonDemes];
			for(i=0; i < numNonDemes; i++) { nondemeYdot[i] = 0.0; }
		}
		// System.out.println("t0 "+t0t1[0]+" t1 "+t0t1[1]);
		foi.integrate(this, t0t1[0], y0, t0t1[1], y1);
	
		// the stephandler will generate a new TimeSeriesFGY
		
		if (trajParams.timeseriesSteps < trajParams.integrationSteps) {
			timeseries = timeseries.toFixedStep(trajParams.timeseriesSteps, this); // fix: for memory leak
			trajParams.integrationSteps = trajParams.timeseriesSteps;
			
		}
		timeseries.reverse();
		
		return timeseries;
	}
	
	/* Matrix updates s*/
	void updateMatrices(double t, double[] y) {
		double val;
		
		/*
		if (debugn<10) {
			System.out.println("\n----- debug ------");
			System.out.println("t="+t);
			System.out.print("y= ");
			for(int i=0; i < numDemes; i++) {
				System.out.print(y[i]+" ");
			}
			System.out.println(" ");
		}
		*/
		
		eqEvaluator.updateYs(y);
		if (useT)
			eqEvaluator.updateT(t);
		
		// for(Definition def: definitions)
		eqEvaluator.executeDefinitions();
		
		// evaluate and store results in eqValues
		eqEvaluator.evaluateEquations(eqValues);
		int i=0;
		for(MatrixEquationObj eq: equations) {
			/* evaluate equation */
			val = eqValues[i++];
			//System.out.println(val + " - " + eqValues[i++]);
			if (eq.type==EquationType.BIRTH) {
				births.put(eq.row,  eq.column, val);
			} else if (eq.type==EquationType.MIGRATION) {
				migrations.put(eq.row,  eq.column, val);
			} else if (eq.type==EquationType.DEATH) {
				deaths.put(eq.row,val);
			} else { /* non deme dynamics */
				nondemeYdot[eq.row] = val;
			}
		}
		
		/*
		if (debugn<10) {
			System.out.println("births="+births);
			System.out.println("migrations="+migrations);
			System.out.println("deaths="+deaths);
		}
		debugn++;
		*/
		
	}
	
	
	@Override
	public void computeDerivatives(double t, double[] y, double[] yDot)
			throws MaxCountExceededException, DimensionMismatchException {
		int i,j;
		
		DVector demeYdot;
		
		updateMatrices(t,y);
		
		
		demeYdot = births.columnSums(); // transpose();
		demeYdot.addi(migrations.columnSums()); //transpose());
		demeYdot.subi(migrations.rowSums());
		demeYdot.subi(deaths);
		
		for(i=0; i<numDemes; i++) { yDot[i] = demeYdot.get(i); }  // demeYdot.get(i,0);
		for(j=0; i<numDemes+numNonDemes; i++,j++) { yDot[i] = nondemeYdot[j]; }
		
		/* -- previous code optimisation
		if (this.fixedStepSize) {
			timeseries.addFG(t, births, migrations);
		}
		*/
		
	}


	@Override
	public int getDimension() {
		return numDemes+numNonDemes;
	}
	
	/* ***** Step Handler interface ***** */
	
	/* Handle the last accepted step (point) */
	@Override
    public void handleStep(StepInterpolator interpolator, boolean isLast) throws MaxCountExceededException {
        double   t = interpolator.getCurrentTime();
        double[] y = interpolator.getInterpolatedState();
        
        // make sure all y's are positive. Yes, then can become negative
        for(int i=0; i<y.length;i++)
        	if (y[i]< 0) y[i]=0.0;
        
        // System.out.println("--> Step handler: "+t);
        
        //timeseries.addY(t,y);
        this.updateMatrices(t, y);
        timeseries.addFGY(t, births, migrations,y, deaths);
        
        /*  -- trying to optimise -too many special cases
        if (this.fixedStepSize) {
        	timeseries.addY(t,y);       
        	if (isLast) { // Final step in series 
        		//System.out.println("Last iteration");
        		double[] yDot = new double[y.length];
        		this.computeDerivatives(t, y, yDot);
        	}
        } else {
        	timeseries.addY(t,y);
        	// add last-computed FG matrices 
        	timeseries.addFG(t,this.births,this.migrations);
        }
        */
        
    }
	/* End step Handler interface */
	
	@Override
	public void init(double t0, double[] y0, double t) {
		timeseries = new TimeSeriesFGYStd(this);
    	//timeseries.addY(t0,y0);
    	this.updateMatrices(t0, y0);
    	timeseries.addFGY(t0,this.births,this.migrations,y0,this.deaths);
    }
	

	
	
	
	

}
