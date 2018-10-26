package phydyn.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


enum DistType { NORMAL, LOGNORMAL };


public class Prior {
	Parameter parameter;
	DistType distType;
	String id;
	
	List<String> argNames;
	List<String> argValues;
	
	private static String errorMsg=null;
	private static String[] distNames = { "normal" , "lognormal" }; 
	private static String[] realNames = { "Normal" , "LogNormal" }; 
	private static DistType[] distTypes = { DistType.NORMAL, DistType.LOGNORMAL  };
	private static String[] distSyntax = {
		"normal(mean-real,sigma-real)" , 
		"lognormal(M-real,S-real,[meanInRealSpace-boolean])"
	};
	
	public Prior(Parameter p, DistType type, List<String> aNames, List<String> aValues) {
		parameter = p;
		distType = type;
		argValues =  aValues;
		argNames = aNames;
		id = "prior:"+p.name;
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
		
	public static Prior createPrior(Parameter param,String dist,List<String> aValues, List<ArgType> aTypes) {
		Prior.setError();
		DistType type;
		dist = dist.toLowerCase();
		int i = 0;
		while (i < distNames.length) {
			if (dist.equals(distNames[i])) {
				break;
			}
			i++;
		}
		if (i < distNames.length) {
			type = distTypes[i];
		} else {
			addError("- Invalid distribution name: "+dist);
			return null;
		}
		
		// distribution name ok
		List<ArgType> cTypes = new ArrayList<ArgType>();
		List<String> aNames = new ArrayList<String>();
		switch (type) {
		case NORMAL: // mean,sigma
			if (aValues.size()!=2) {
				addError("- Incorrect number of arguments");
				break;
			}
			aNames.add("mean");
			cTypes.add(ArgType.ARGREAL);
			aNames.add("sigma");
			cTypes.add(ArgType.ARGREAL);
			// both arguments need to be numbers
			checkArgumentTypes(cTypes,aTypes);
			break;
		case LOGNORMAL:  // M,S,[meanInRealSpace]
			if (aValues.size()!=2 && aValues.size()!=3 ) {
				addError("- Incorrect number of arguments");
				break;
			}
			aNames.add("M");
			cTypes.add(ArgType.ARGREAL);
			aNames.add("S");
			cTypes.add(ArgType.ARGREAL);
			aNames.add("meanInRealSpace");
			cTypes.add(ArgType.ARGBOOLEAN);
			if (aValues.size()==2) { // add third argument
				aValues.add("false"); // default
				aTypes.add(ArgType.ARGBOOLEAN);
			} 
			// both arguments need to be numbers
			checkArgumentTypes(cTypes,aTypes);
			break;
		}
		
		if (errorMsg!=null) {
			addError("- SYNTAX: "+distSyntax[i]);
			return null;
		} else {
			return new Prior(param, type, aNames, aValues);
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
		System.out.print("prior "+distNames[distType.ordinal()]+"(");
		if (argValues.size()>0) {
			System.out.print(argValues.get(0));
		}
		for(int i=1; i < argValues.size(); i++) {
			System.out.print(","+argValues.get(i));
		}
		System.out.println(")");
	}
	
	public String writeXML(XMLFileWriter writer) throws IOException {
		String priorXML = "<prior id=\"*pid*\" name=\"distribution\" x=\"@*paid*\">\n";
		//<prior id="initI0prior" name="distribution" x="@initI0">
		//<LogNormal id="LogNormal:initI0" M="0" S="1" name="distr" />
		//</prior>
		String s = priorXML.replace("*pid*", id);
		s = s.replace("*paid*", parameter.id);
		writer.tabAppend(s);
		// contents
		s = "  <"+ realNames[this.distType.ordinal()];
		// distribution
		s += (" id=\"" + realNames[this.distType.ordinal()]+":"+parameter.id+"\"");
		s += " name=\"distr\" ";
		for(int i=0; i<argNames.size(); i++) {
			s += " "+argNames.get(i)+"=\""+argValues.get(i)+"\"";
		}
		s += "/>\n";
		writer.tabAppend(s);		
		writer.tabAppend("</prior>\n");
		return "priorID";
	}
	

}


// LogNormal distribution 
// new Input<>("M", "M parameter of lognormal distribution. Equal to the mean of the log-transformed distribution.");
// ("S", "S parameter of lognormal distribution. Equal to the standard deviation of the log-transformed distribution.");
// boolean meanInRealSpace or in logTransformed. default false
// defaults of (M,S) = 1

// Normal
// ("mean", "mean of the normal distribution, defaults to 0");
//("sigma", "standard deviation of the normal distribution, defaults to 1");
// ("tau", "precision of the normal distribution, defaults to 1", Validate.XOR, sigmaInput);

// todo:
// "Uniform distribution over a given interval (including lower and upper values)")
// ("lower", "lower bound on the interval, default 0", 0.0)
// ("upper", "lower bound on the interval, default 1", 1.0)




