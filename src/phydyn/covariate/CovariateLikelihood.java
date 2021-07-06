package phydyn.covariate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import beast.core.Input;
import beast.core.Input.Validate;
import phydyn.covariate.distribution.ParamDistribution;
import phydyn.model.PopModelInterpreter;
import phydyn.model.SemanticChecker;
import phydyn.model.TimeSeriesFGY;
import phydyn.model.parser.PopModelLexer;
import phydyn.model.parser.PopModelParser;
import phydyn.model.parser.PopModelParser.ExprContext;
import phydyn.util.DVector;
import phydyn.util.General;

public class CovariateLikelihood extends TrajectoryFit {	
	
	
	public Input<Boolean> quietInput = new Input<>("quiet","Print seropevalences",new Boolean(true));
	public Input<Boolean> ignoreInput = new Input<>("ignore","Ignore SeroP likelihood",new Boolean(false));
	
	//public Input<String> definitionsInput = new Input<>(
	//		 "definitions","id=value pairs separated by semi-colons");
	public Input<String> covariateInput = new Input<>(
			 "covariate-expression","Expression denoting the covariate value", Validate.REQUIRED);
	
	public Input<ParamDistribution>  distInput = new Input<>("covariate-distribution", "Data point distribution");
	public Input<String> distExpInput = new Input<>("distribution-expression", "Data point distribution", Validate.XOR, distInput);
	
	boolean ignore, quiet;
	
	
	//org.apache.commons.math.distribution.NormalDistribution dist;	
	
	//double[] colTime, colCov;
	int idxTime, idxCov;
	String idCov, idCovExp;

	// New
	ParamDistribution dist;
	List<String> distVars;
		
	
	PopModelInterpreter interpreter;
	ExprContext covExprCtx, distExprCtx;
	boolean useT0T1, useT, useModelParameters;
	double[] t0t1;
	
	double[][] data;
	Map<String,Double> localEnv;
	
	@Override
	public void initAndValidate() {
		super.initAndValidate();
		
		if (!headerInput.get())
			throw new IllegalArgumentException("header flag must be set to true");
		
		quiet = quietInput.get();
		ignore = ignoreInput.get();
		
		
		
		idxTime = 0;
		String timeHeader = headers[idxTime];
		// idxTime = General.indexOf(timeHeaderInput.get(), headers);
		if (timeHeader.compareTo("time")!=0 && timeHeader.contains("t")) {
			throw new IllegalArgumentException("CovariateLikelihood: First column must be named 't' or 'time'");
		}
		idxCov = 1;			
		idCov = headers[idxCov];
		idCovExp = idCov.concat("Val");
		

		// Process Covariate Expression
		useT0T1 = useT = useModelParameters = false;
		/* parse equation string */
		CodePointCharStream  input = CharStreams.fromString( covariateInput.get()  );
		try {
			PopModelLexer lexer = new PopModelLexer(input);		
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PopModelParser parser = new PopModelParser(tokens);
			covExprCtx =  parser.expr();
		} catch (Exception e) {
			System.out.println( "Error while parsing covariate expression: "+covariateInput.get());
			throw new IllegalArgumentException("Parsing error");
		}	
		SemanticChecker checker = new SemanticChecker(popModel);
		
		// add headers
		for(int i=0; i < headers.length; i++)
			checker.addExternalVariable(headers[i]);
		
		if (checker.check(covExprCtx)) {
			throw new IllegalArgumentException("Error(s) found in covariate expression");
		}
		
		
		
		// Check that distribution can be computed
		dist = distInput.get();
		if (dist != null) {
			distVars = dist.getParameters();
			for (String id: distVars) {
				// 
				if (General.indexOf(id, headers) == -1) 
					throw new IllegalArgumentException("Distribution Parameter not found: "+id);			
			}
		} 
		// This is XOR
		distExprCtx = null;
		if (distExpInput.get() != null) {
			input = CharStreams.fromString(distExpInput.get());
			try {
				PopModelLexer lexer = new PopModelLexer(input);		
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				PopModelParser parser = new PopModelParser(tokens);
				distExprCtx =  parser.expr();
			} catch (Exception e) {
				System.out.println( "Error while parsing distribution expression: "+ distExpInput.get());
				throw new IllegalArgumentException("Parsing error");
			}	
			// checker: add headers  -- already added
			//for(int i=0; i < headers.length; i++)
			//	checker.addExternalVariable(headers[i]);
			checker.addExternalVariable(idCovExp);
			if (checker.check(distExprCtx)) {
				throw new IllegalArgumentException("Error(s) found in distribution expression");
			}
		}

						
		
		interpreter = new PopModelInterpreter(checker);
		
		if (checker.useT0T1) {
			useT0T1 = true;
			t0t1 = new double[2];
		}
		if (checker.useT)
			useT = true;
		
		
		// Variables used by expressions
		for (Map.Entry<String, Integer> entry : checker.envTypesUsed.entrySet()) {
		    String id = entry.getKey();
		    int value = entry.getValue();
		    //System.out.println("used: "+id+" = "+value);
		    if (value == 3)
		    	useModelParameters = true;
		    if (value == 0) {
		    	throw new IllegalArgumentException("(Not implemented) Illegal use of popmodel "
		    			+ "definition variable in covariate expression: "+id);
		    }
		}
		
		// and, finally
		//localEnv = new HashMap<String,Double>(defMap);  // values from definitions
		localEnv = new HashMap<String,Double>();
		
		data = this.getDataAsMatrix();
		

	}
	
	//String[] defIds;
	//double[] defValues;
	//Map<String,Double> defMap;
	private static Map<String,Double> parseDefinitions(String defs) {
		// Double.parseDouble(columns[col].get(row));
		Map<String,Double>  defMap = new HashMap<>();
		String[] pairs = defs.split(";");
		for(int i = 0; i < pairs.length; i++) {
			final String[] id_value = pairs[i].split("=");
			final String id = id_value[0].trim();
			if (id_value.length != 2) {
				throw new IllegalArgumentException("Incorrect syntax of definition: "+pairs[i]);
			}
			try {
				final double v = Double.parseDouble(id_value[1].trim());
				defMap.put(id, v);
			} catch(Exception e) {
				throw new IllegalArgumentException("Incorrect syntax (number format) of definition: "+pairs[i]);
			}
		}
		return defMap;		
	}
	
	//final int n = defMap.size();		
	//defIds = new String[n];
	//defValues = new double[n];
	private static void definitiosnMapToArrays(Map<String,Double> defMap, String[] defIds,   double[] defValues ) {
		int i = 0;
		for (Map.Entry<String, Double> entry : defMap.entrySet()) {
		    String id = entry.getKey();
		    double value = entry.getValue();
		    defIds[i] = id;
		    defValues[i] = value;
		    i++;
		}		
	}

	@Override
    public double calculateLogP() {
		
		TimeSeriesFGY ts = popModel.getTimeSeries();
		
		// new 
		if (useT0T1) {
			t0t1[0] = popModel.getStartTime();
			t0t1[1] = popModel.getEndTime();
			interpreter.updateEnv(SemanticChecker.T0T1, t0t1);
		}
		if (useModelParameters) {
			//eqEvaluator.updateRateVectors(modelParams.paramVectorValues); // testing vectors
			interpreter.updateEnv(popModel.modelParams.paramNames,popModel.modelParams.paramValues );
		}
		
		int nps = ts.getNumTimePoints();  // reverse time
		
		// Get time using datapoint
		//double tst1 = ts.getTime(0);
		//double tst0 = ts.getTime(nps-1);
		//System.out.println("tst0 = "+tst0+" tst1 = "+tst1);
		
		
		int tpStart = nps/2;
			
		logP = 0;
		DVector Ys = null;
		int tp = tpStart;
		double pointLogP;
		for(int i = 0; i < numrows; i++) {

			final double[] dataRow = data[i];
			
			final double t = dataRow[this.idxTime];
			final double covData = dataRow[this.idxCov];  // mean
			
			tp = ts.getTimePoint(t, tp);			
			//final double tback = ts.getTime(tp);		
			//System.out.println("time="+t+" after search="+tback);
			
			//new
			if (useT)
				interpreter.updateEnv(SemanticChecker.T, t);
					
			Ys  = ts.getYall(tp);
		
			// new
			interpreter.updateEnv(popModel.yNames, Ys.data);
			interpreter.updateEnv(headers, dataRow);  // added to allow data to be part of cov-exp
			final double covValue = interpreter.evaluate(covExprCtx);
			

			pointLogP = 0;
					
			if (dist != null) {
				for(int j = 0; j < headers.length; j++) {
					localEnv.put(headers[j], dataRow[j]);				
				}
				dist.updateParameters(localEnv);
				pointLogP = dist.logDensity(covValue);
				
			} else { // if (distExprCtx !=null)
				interpreter.updateEnv(idCovExp, covValue);
				pointLogP = interpreter.evaluate(distExprCtx);
			}
			
			logP += pointLogP;
			
			// only valid for Normal distribution sigma="sigma"					
			//final double sgm = interpreter.get_env().get("sigma");
			//final double lll = -Math.log(sgm*Math.sqrt(2*Math.PI))- 0.5*(covValue - covData)*(covValue-covData)/(sgm*sgm);
			//NormalDistribution distNormal = new NormalDistributionImpl(covData, sgm);			
			//System.out.println("dist densities:"+ lll +"  /  "+ pointLogP + "/ " + Math.log(distNormal.density(covValue)));		
			//System.out.println("cov-value = "+covValue);
			
			
		}
	
		if (ignore)
			logP=0;
			
        return logP;
    }
	

	
	
}
