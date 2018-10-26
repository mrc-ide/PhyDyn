package phydyn.analysis;

import java.util.ArrayList;
import java.util.List;


import phydyn.analysis.Parameter.ParamType;
import phydyn.model.PopModel;
import phydyn.model.parser.PopModelBaseVisitor;
import phydyn.model.parser.PopModelParser.AdeclBodyContext;
import phydyn.model.parser.PopModelParser.AnalysisDeclContext;
import phydyn.model.parser.PopModelParser.AnalysisSpecContext;
import phydyn.model.parser.PopModelParser.ArgContext;
import phydyn.model.parser.PopModelParser.BoundContext;
import phydyn.model.parser.PopModelParser.OperatorDeclContext;
import phydyn.model.parser.PopModelParser.PriorDeclContext;


enum ArgType { ARGINT, ARGREAL, ARGIDENT, ARGBOOLEAN };



public class PMAnalysisChecker extends PopModelBaseVisitor<Boolean> {
	boolean error;
	List<String> argValues;
	List<ArgType> argTypes;
	List<Parameter> parameters;
	List<Prior> priors;
	List<Operator> operators;
	Parameter parameter;
	PopModel popModel;

	
	public PMAnalysisChecker() {
		// TODO Auto-generated constructor stub
	}
	
	public Boolean check(AnalysisSpecContext aCtx, PopModel popModel, List<Parameter> parameters,
			List<Prior> priors, List<Operator> operators) {
		error = false;
		this.popModel = popModel;
		this.parameters = parameters;
		this.priors = priors;
		this.operators = operators;
		//argValues = new ArrayList<String>();
		argTypes = new ArrayList<ArgType>();
		visit(aCtx);
		
		return error;
	}
	
	public Boolean setError(String element,String message) {
		if (message==null) {
			System.out.println("Error in "+element);
		} else {
			System.out.println("Error in "+element+"\n--> "+message);
		}
		error = true;
		return true;
	}
	
	
	public Boolean visitAnalysisDecl(AnalysisDeclContext aCtx) {
		String decl = aCtx.getText();
		String name  = aCtx.IDENT(0).getText();
		String typeStr = aCtx.IDENT(1).getText().toLowerCase();
		double lboundValue=Double.NEGATIVE_INFINITY, rboundValue=Double.POSITIVE_INFINITY;
		ParamType type;
		parameter = null;
		
		// before going further, check if parameter is part of the model
		if (!popModel.isParameter(name)) {
			return setError(decl, "Parameter ("+name+") not found in model");			
		}
		
		if (typeStr.equals("int")) {
			type = ParamType.PARAM_INT;
		} else if (typeStr.equals("real")) {
			type = ParamType.PARAM_REAL;
		} else if (typeStr.equals("boolean")) {
			type = ParamType.PARAM_BOOLEAN;
		} else {
			System.out.println("Error: Unknown parameter type: "+typeStr);
			error = true; return true;
		}
		// bounds are optional
		parameter = new Parameter(name,type);
		
		List<BoundContext> bounds = aCtx.bound();		
		if (bounds.size()>0) {
			if (type==ParamType.PARAM_BOOLEAN) {
				return setError(decl,": illegal use of Bounds with boolean parameter");
			}
			// left bound
			BoundContext bound = aCtx.bound(0);
			String boundText = bound.getText();
			if (boundText.equals("inf")) {
				// plus infinity
				return setError(decl,"plus-infinity not allowed as lower bound");
			} else if (!boundText.equals("-inf")) {
				if ((bound.FLOAT()!=null) && (type==ParamType.PARAM_INT)) {
					return setError(decl, "bound must be integer");
				}
				lboundValue=Double.parseDouble(boundText);
				parameter.setLowerBound(boundText);
			}
			// right bound
			bound = aCtx.bound(1);
			boundText = bound.getText();
			if (boundText.equals("-inf")) {
				// plus infinity
				return setError(decl,"minus-infinity not allowed as upper bound");
			} else if (!boundText.equals("inf")) {
				if ((bound.FLOAT()!=null) && (type==ParamType.PARAM_INT)) {
					return setError(decl,"bound must be integer");
				}
				rboundValue=Double.parseDouble(boundText);
				parameter.setUpperBound(boundText);
			}		
			if (rboundValue < lboundValue) {
				return setError(decl,"lower bound greater than upper bound");
			}
		} // else no bound specified
		if (visit(aCtx.adeclBody())) {
			return setError("declaration of parameter "+name,null);
		} else {
			parameters.add(parameter);
		}
		return false;
	}
	
	
	
	public Boolean visitAdeclBody(AdeclBodyContext adeclCtx) {
		List<PriorDeclContext> priorsCtx = adeclCtx.priorDecl();
		List<OperatorDeclContext> operatorsCtx = adeclCtx.operatorDecl();
		if (priorsCtx.size() < 1) {
			return setError(parameter.name,"Must define parameter prior");
		}
		if (priorsCtx.size() > 1) {
			return setError(parameter.name,"Only one prior allowed");
		}
		if (operatorsCtx.size() < 1) {
			return setError(parameter.name,"Must define parameter operator(s)");
		}
		boolean localError = false;
		// all good so far. traverse declarations.
		for(PriorDeclContext p: priorsCtx) {
			localError = localError || visit(p);
		}
		for(OperatorDeclContext o: operatorsCtx) {
			localError = localError || visit(o);
		}
		return localError;
	}
	
	
	public Boolean visitPriorDecl(PriorDeclContext pCtx) {		
		String distName = pCtx.IDENT().getText();
		//System.out.println("prior "+parameter.name+" dist="+distName);
		List<ArgContext> args = pCtx.arg();
		argValues = new ArrayList<String>();
		argTypes.clear();
		for(ArgContext arg:args) {
			visit(arg);
		}
		Prior prior = Prior.createPrior(parameter, distName,argValues,argTypes);
		if (prior!=null) {
			parameter.setPrior(prior);
			priors.add(prior);
		} else {
			System.out.println("Error in prior declaration: "+pCtx.getText());
			System.out.println(Prior.getError());
		}
		return (prior==null);
	}
	
	public Boolean visitOperatorDecl(OperatorDeclContext opCtx) {
		String weight;
		if (opCtx.INT()==null) {
			weight = "1";
		} else {
			weight = opCtx.INT().getText();
		}		
		String opName = opCtx.IDENT().getText();		
		//System.out.println("operator "+opName+" param "+parameter.name+" weight "+weight);
		List<ArgContext> args = opCtx.arg();
		argValues = new ArrayList<String>();
		argTypes.clear();
		for(ArgContext arg:args) {
			visit(arg);
		}
		Operator operator = Operator.createOperator(parameter,weight,opName,argValues,argTypes);
		if (operator!=null) {
			parameter.addOperator(operator);
			operators.add(operator);
		} else {
			System.out.println("Error in operator declaration: "+opCtx.getText());
			System.out.println(Operator.getError());
		}
		return (operator==null);
	}
	
	
	public Boolean visitArg(ArgContext argCtx) {
		ArgType type;
		String aValue = argCtx.getText();
		if (argCtx.IDENT()!=null) {
			if ((aValue.toLowerCase().equals("true"))||(aValue.toLowerCase().equals("false"))) {
				type = ArgType.ARGBOOLEAN;
				aValue = aValue.toLowerCase();
			} else {
				type = ArgType.ARGIDENT;
			}
			// if true/false -> ARGBOOLEAN
		} else if (argCtx.INT()!=null) {
			type = ArgType.ARGINT;
		} else if (argCtx.FLOAT()!=null) {
			type = ArgType.ARGREAL;
		} else {
			throw new IllegalArgumentException("Programmer error: unknown "
					+ "token type in prior decl: "+argCtx.getText());
		}
		argValues.add(aValue);
		argTypes.add(type);
		return false;
	}
	
	
	

	
	//private static Operator createOperator(Parameter param,String w, String opName,List<String> aValues, List<ArgType> aTypes) {
	//	Operator operator = new Operator(param,Integer.parseInt(w));
	//	operator.setOperator(opName);
	//	return operator;
	//}
}
