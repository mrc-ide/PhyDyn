package phydyn.util;

import java.io.IOException;

import phydyn.analysis.PopModelAnalysis;
import phydyn.analysis.XMLFileWriter;
import phydyn.model.DefinitionObj;
import phydyn.model.MatrixEquationObj;
import phydyn.model.ModelParameters;
import phydyn.model.PopModelODE;
import phydyn.model.TrajectoryParameters;
import phydyn.model.translate.PopModelODEPrinter;

public class XMLWriter {

	
	public static String writeXML(ModelParameters o, XMLFileWriter writer, PopModelAnalysis analysis, String mparamID) throws IOException {
		String paramxml = "<param spec=\"ParamValue\" names=\"*x*\" values=\"*v*\"/>";
		String vectorxml = "<param spec=\"ParamValue\" vector=\"true\" names=\"*x*\" values=\"*v*\"/>";
		writer.tabAppend("<rates spec=\"ModelParameters\" id='**'> ".replace("**",mparamID)+"\n");
		writer.tab();
		// <param spec="ParamValue" names="beta0" values="0.0001"/>
		String s, paramName, paramID;
	    //<param spec="ParamValue" vector="true" names="KP" values="1 2 3"/>		
		for(int i=0; i < o.paramNames.length; i++) {
			paramName = o.paramNames[i];
			s = paramxml.replace("*x*",paramName);
			// bug: analysis can be null e.g. likelihood
			paramID = null;
			if (analysis!=null)
				paramID = analysis.getParamID(paramName); // is it being sampled?
			if (paramID==null) {	
				s = s.replace("*v*",Double.toString(o.paramValues[i]));
			} else {
				s = s.replace("*v*","@"+paramID);
			}
			writer.tabAppend(s+"\n");
		}
		String vs;
		for(int i=0; i < o.paramVectorNames.length; i++) {
			s = vectorxml.replace("*x*",o.paramVectorNames[i]);
			vs = Double.toString(o.paramVectorValues[i][0]);  // there should at least be one element
			for(int j=1; j < o.paramVectorValues[i].length; j++) {
				vs += " "+  Double.toString(o.paramVectorValues[i][j]);
			}
			s = s.replace("*v*",vs);
			writer.tabAppend(s+"\n");
		}
	    //</rates>  
		writer.untab();
		writer.tabAppend("</rates>\n");
		return mparamID;
	}
	
	
	
	// Currently not used - replaced by BEAST's XML writer
	// to be deprecated
	public static String writeXML(TrajectoryParameters o,  XMLFileWriter writer, PopModelAnalysis analysis, String tparamID) throws IOException {
		String xml1 = "<trajparams id=\"*id*\" spec=\"TrajectoryParameters\" method=\"*m*\" ";
		String xml2 = "<initialValue spec=\"ParamValue\" names=\"*n*\" values=\"*v*\"/>";
		// order="*o* aTol="*a* rTol="*r*"
		String xml3 = "order=\"*o*\" aTol=\"*a*\" rTol=\"*r*\" ";
		//<trajparams id="initValues" spec="TrajectoryParameters" method="classicrk"
		//	    integrationSteps="1001"  order="3" t0="-0.01" t1="10">
		//      <initialValue spec="ParamValue" names="I0" values="1"/>
		//      <initialValue spec="ParamVprotectedalue" names="I1" values="1"/>
		//      <initialValue spec="ParamValue" names="S" values="12000.0"/>
		//</trajparams>
		String s = xml1.replace("*id*", tparamID);
		writer.tabAppend(s.replace("*m*", o.method.name())+"\n");
		writer.tabAppend("  integrationSteps=\""+o.integrationSteps+"\" ");
		if (!o.fixedStepSize) {
			s = xml3.replace("*o*",Integer.toString(o.order));
			s = s.replace("*a*", Double.toString(o.aTol));
			s = s.replace("*r*", Double.toString(o.rTol));
			writer.tabAppend(s);
		}
		writer.tabAppend("t0=\""+Double.toString(o.getStartTime())+"\" ");
		if (o.t1Set) {
			writer.tabAppend("t1=\""+Double.toString(o.t1)+"\" ");
		}
		writer.tabAppend("\n  >\n");
		String paramName, paramID;
		writer.tab();
		for(int i=0; i < o.paramNames.length; i++) {
			paramName = o.paramNames[i];
			paramID = null;
			// bug: analysis can be null
			if (analysis!=null)
				paramID = analysis.getParamID(paramName); // is it being sampled?
				s = xml2.replace("*n*", o.paramNames[i]);
			if (paramID==null) {
				writer.tabAppend(s.replace("*v*", Double.toString(o.paramValues[i]))+"\n");
			} else {
				writer.tabAppend(s.replace("*v*", "@"+paramID)+"\n");
			}
		}
		writer.untab();
		writer.tabAppend("</trajparams>\n");
		return tparamID;
	}
	
	public static String writeXML(PopModelODE o, XMLFileWriter writer, PopModelAnalysis analysis) throws IOException {
		String modelID = o.getName();
		String initID = modelID+"-init";
		String paramID = modelID+"-param";
		//<model spec="PopModelODE" id="twodeme" evaluator="compiled"
		//		 popParams='@initValues' modelParams='@rates'  >
		writer.tabAppend("<model spec=\"PopModelODE\" id=\""+modelID+"\" evaluator=\"");
		writer.tabAppend(o.evaluatorTypeInput.get()+"\"\n");
		writer.tabAppend("    popParams='@"+initID+"' modelParams='@"+paramID+"'>\n");
		PopModelODEPrinter printer = new PopModelODEPrinter();
		writer.tabAppend("<definitions spec='Definitions'>\n");
		writer.tab();
		for(DefinitionObj def: o.definitions) {	
			writer.tabAppend(printer.visit(def.stm)+"\n");
		}
		writer.untab();
		writer.tabAppend("</definitions>\n");
		writer.tabAppend("<matrixeqs spec=\"MatrixEquations\"> \n"); 
		writer.tab();
		for(MatrixEquationObj eq: o.equations) {
			writer.tabAppend(eq.getLHS() + " = " + printer.visit(eq.rhsExprCtx)+";\n" );			
		}
		writer.untab();
		writer.tabAppend("</matrixeqs>\n");
		writer.tabAppend("\n</model>\n");
		
		// pass analysis
		
		writeXML(o.modelParams, writer,analysis,paramID);
		writer.tabAppend("\n");
		writeXML(o.trajParams, writer,analysis,initID);
		return modelID;
	}
		

}
