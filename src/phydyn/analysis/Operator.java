package phydyn.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

enum OperatorType { RANDOMWALK, SCALE, UNIFORM };

public class Operator {
	Parameter parameter;
	String id;
	String weight;
	OperatorType opType;
	
	List<String> argNames;
	List<String> argValues;
	
	private static String errorMsg=null;
	private static String[] opNames = { "randomwalk" , "scale", "uniform" }; 
	private static String[] realNames = { "RealRandomWalkOperator" , "ScaleOperator", "UniformOperator" }; 
	private static OperatorType[] opTypes = { OperatorType.RANDOMWALK, OperatorType.SCALE, OperatorType.UNIFORM };
	private static String[] opSyntax = {
		"RandomWalk(windowSize-real,[useGaussian-boolean])" , 
		"Scale(M-real,S-real,[meanInRealSpace-boolean])",
		"Uniform([howMay])"
	};
	private static int numOperators=0;
	
	public Operator(Parameter p, OperatorType type, String w, List<String> aNames, List<String> aValues) {
		parameter = p;
		opType = type;
		weight = w;
		argNames = aNames;
		argValues = aValues;
		id = "operator"+numOperators+":"+parameter.name;
		numOperators++;
	}
	
	private static void setError() {
		errorMsg=null;
	}
	
	public static String getError() {
		return errorMsg;
	}
	
	private static void addError(String msg) {
		if (errorMsg==null)
			errorMsg = msg;
		else
			errorMsg = errorMsg + "\n" + msg;
	}
	
	
	public static Operator createOperator(Parameter param,String w,String opName,List<String> aValues, List<ArgType> aTypes) {
		Operator.setError();
		OperatorType opType;
		opName = opName.toLowerCase();
		int i = 0;
		while (i < opNames.length) {
			if (opName.equals(opNames[i])) {
				break;
			}
			i++;
		}
		if (i < opNames.length) {
			opType = opTypes[i];
		} else {
			addError("- Invalid operator name: "+opName);
			return null;
		}
		// operator name OK
		
		List<ArgType> cTypes = new ArrayList<ArgType>();
		List<String> aNames = new ArrayList<String>();
		
		// check arguments
		switch (opType) {
		case RANDOMWALK:  // M,S,[meanInRealSpace]
			if (aValues.size()!=1 && aValues.size()!=2) {
				addError("- Incorrect number of arguments");
				break;
			}
			aNames.add("windowSize");
			cTypes.add(ArgType.ARGREAL);
			aNames.add("useGaussian");
			cTypes.add(ArgType.ARGBOOLEAN);
			if (aValues.size()==1) { // add second argument with default
				aValues.add("false"); // default
				aTypes.add(ArgType.ARGBOOLEAN);
			} 
			// both arguments need to be numbers
			checkArgumentTypes(cTypes,aTypes);
			break;
		case UNIFORM: // [howMany]
			if (aValues.size() > 1) {
				addError("- Incorrect number of arguments");
				break;
			}
			aNames.add("howMany");
			cTypes.add(ArgType.ARGINT);
			if (aValues.size()==0) {
				aValues.add("1");  // default
				aTypes.add(ArgType.ARGINT);  // correct type 
			}
			checkArgumentTypes(cTypes,aTypes);
			break;
		case SCALE:
			addError("- not implemented");
		}
		
				
		if (errorMsg!=null) {
			addError("- SYNTAX: "+opSyntax[i]);
			return null;
		} else {
			return new Operator(param, opType, w, aNames, aValues);
		}
	}
	
	// returns true if error
	// reports error via addError()
	// assumption: lists have equal lengths
	private static void checkArgumentTypes(List<ArgType> cTypes, List<ArgType> aTypes) {
		int i=0;
		ArgType aType;
		for (ArgType ctype : cTypes) {
			aType = aTypes.get(i);
			switch(ctype) {
			case ARGINT:
				if (aType!=ArgType.ARGINT) {
					addError("- Parameter "+(i+1)+" must be an Integer");
				}
				break;
			case ARGREAL:
				if ((aType!=ArgType.ARGREAL) && (aType!=ArgType.ARGINT)) {
					addError("- Parameter "+(i+1)+" must be Numeric");
				}
				break;
			case ARGIDENT:
				if (aType!=ArgType.ARGIDENT) {
					addError("- Parameter "+(i+1)+" must be a string");
				}
				break;
			case ARGBOOLEAN:
				if (aType!=ArgType.ARGBOOLEAN) {
					addError("- Parameter "+(i+1)+" must be a boolean");
				}
				break;	
			}			
			i++;
		}
		return;
	}
	
	public void print() {
		System.out.print("operator("+weight+") = " + opNames[opType.ordinal()]+"(");
		if (argValues.size()>0) {
			System.out.print(argNames.get(0)+"="+argValues.get(0));
		}
		for(int i=1; i < argValues.size(); i++) {
			System.out.print(","+ argNames.get(i)+"="+argValues.get(i));
		}
		System.out.println(")");
	}
	
	public String writeXML(XMLFileWriter writer) throws IOException {
		//<operator id="rwoperator:tau" spec="RealRandomWalkOperator"  parameter="@tau" ... />
		String s = "<operator id=\""+this.id+"\"";
		s += " spec=\""+ realNames[this.opType.ordinal()]+"\"";
		s += " parameter=\"@"+parameter.id+"\"";
		s += " weight=\""+weight+"\"";
		for(int i=0; i<argNames.size(); i++) {
			s += " "+argNames.get(i)+"=\""+argValues.get(i)+"\"";
		}
		s += "/>\n";
		writer.tabAppend(s);
		return this.id;
	}

}

// Single parameter operators

//  RealRandomWalkOperator
// @Description("A random walk operator that selects a random dimension of the real 
// parameter and perturbs the value a random amount within +/- windowSize.")
//  Input<Double> ("windowSize", "the size of the window both up and down when using uniform interval OR 
//   standard deviation when using Gaussian", Input.Validate.REQUIRED);
// "useGaussian", "Use Gaussian to move instead of uniform interval. Default false.", false);
//Input<RealParameter> "parameter", "the parameter to operate a random walk on.", Validate.REQUIRED);
// Gaussian:  newValue += windowSize * Z = X, X ~ N(0,windowSize^2)
// Non-gauss: newValue +=  windowSize * [-1,1]

// Scale
// Input<Double> ("scaleFactor", "scaling factor: larger means more bold proposals", 1.0);
// Input<Boolean> scaleAllInput =("scaleAll", "if true, all elements of a parameter (not beast.tree) are scaled, 
//                                             otherwise one is randomly selected",false);
// Input<Boolean> scaleAllIndependentlyInput =("scaleAllIndependently", "if true, all elements of a parameter 
//      (not beast.tree) are scaled with a different factor, otherwise a single factor is used", false);
// Input<Integer> degreesOfFreedomInput =("degreesOfFreedom", "Degrees of freedom used when " +
//        "scaleAllIndependently=false and scaleAll=true to override default in calculation of Hasting ratio. " +
//        "Ignored when less than 1, default 0.", 0);
// Input<BooleanParameter> indicatorInput = ("indicator", "indicates which of the dimension " +
//        "of the parameters can be scaled. Only used when scaleAllIndependently=false and scaleAll=false. If not specified " +
//        "it is assumed all dimensions are allowed to be scaled.");

// Input<Boolean> optimiseInput = new Input<>("optimise", "flag to indicate that the scale factor is automatically changed in order to achieve a good acceptance rate (default true)", true);
// Input<Double> scaleUpperLimit = new Input<>("upper", "Upper Limit of scale factor", 1.0 - 1e-8);
// Input<Double> scaleLowerLimit = new Input<>("lower", "Lower limit of scale factor", 1e-8);


// UniformOperator (operates on upper and lower boundaries of parameter)
// Input<Parameter<?>> parameterInput = new Input<>("parameter", "a real or integer parameter to sample individual values for", Validate.REQUIRED, Parameter.class);
// (not included) if parameter is a vector
// Input<Integer> howManyInput = new Input<>("howMany", "number of items to sample, default 1, must be less than the dimension of the parameter", 1);





