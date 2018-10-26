package phydyn.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import phydyn.model.PopModel;

/*
 * Parameter class implementation oriented towards XML generation
 */

public class Parameter {

	enum ParamType { PARAM_INT, PARAM_REAL, PARAM_BOOLEAN };
	
	public ParamType type;
	public String name, id, lbound, ubound;
	Prior prior;
	List<Operator> operators;
	
	public Parameter(String name, ParamType type) {
		this.name = name;
		this.type = type;
		this.id = "param:"+name; // default
		lbound = null;
		ubound = null;
		prior=null;
		operators = new ArrayList<Operator>();
	}
	
	public void setLowerBound(String b) {
		lbound = b;
	}
	
	public void setUpperBound(String b) {
		ubound = b;
	}
	
	public void setPrior(Prior p) {
		prior = p;
	}
	
	public void addOperator(Operator op) {
		operators.add(op);
	}
	
	public void print() {
		String s = "parameter "+name+" ";
		switch (type) {
			case PARAM_INT: s+="int "; break;
			case PARAM_REAL: s+="real "; break;
			case PARAM_BOOLEAN: s+="bool "; break;
		}
		s+="(";
		if (lbound==null) {
			s += "-inf";
		} else {
			s+=lbound;
		}
		s += ",";
		if (ubound==null) {
			s += "inf";
		} else {
			s+=ubound;
		}
		s += ")";
		System.out.println(s);		
		prior.print();
		for(Operator op:operators) {
			op.print();
		}
	}

	public String writeXML(XMLFileWriter writer, PopModel popModel) throws IOException {
		String xml="<parameter id=\"*id*\" lower=\"*lb*\" upper=\"*ub*\" "
				+ "name=\"stateNode\">*v*</parameter>\n";
		String s = xml.replace("*id*", id);
		// lower bound
		String bound;
		if (lbound==null) {
			bound = "-Infinity";
		} else {
			bound = lbound;
		}
		s = s.replace("*lb*",bound);
		// upper bound
		if (ubound==null) {
			bound = "Infinity";
		} else {
			bound = ubound;
		}
		s = s.replace("*ub*",bound);		
		// initial value
		String v = popModel.getParameterValue(name);
		s = s.replace("*v*",v);
		writer.tabAppend(s);
		return this.id;
	}
	
}
