package phydyn.model;

import java.util.List;

public class PopModelODETranslator {
	PopModelODE popModel;
	
	public PopModelODETranslator(PopModelODE popModel) { 
		this.popModel = popModel;
	}
	
	public String generateString() {
		String s="";
		PopModelODEPrinter printer = new PopModelODEPrinter();
		s += ("model-name = "+popModel.getID()+";\n");
		s += "definitions = {\n";		
		for(DefinitionObj def: popModel.definitions) {	
			s += "  "+printer.visit(def.stm)+"\n";
		}
		s += "}\nequations = {\n";		
		for(MatrixEquationObj eq: popModel.equations) {
			s += "  "+eq.getLHS() + " = " + printer.visit(eq.rhsExprCtx)+";\n";
			//System.out.print("  "+eq.getLHS() + " = ");
			//System.out.println(printer.visit(eq.rhsExprCtx)+";");
		}
		s +="}\n";
		s += (popModel.modelParams.toString())+"\n";
		s += (popModel.trajParams.toString());
		return s;
		
	}
	
	public String GenerateR() {
		StringBuffer buf = new StringBuffer();
		buf.append("library(phydynR)\n\n");
		
		final String[] demeNames = popModel.demeNames;
		final int numDemes = popModel.numDemes;
		String str = "demes <- c( '"+demeNames[0]+"'";
		for(int i=1; i < numDemes; i++)
			str += ", '"+demeNames[i]+"'";
		str += " )";
		buf.append(str+"\n");

		final String[] nonDemeNames = popModel.nonDemeNames;
		final int numNonDemes = popModel.numNonDemes;
		str = "nondemes <- c( '"+nonDemeNames[0]+"'";
		for(int i=1; i < numNonDemes; i++)
			str += ", '"+nonDemeNames[i]+"'";
		str += " )";
		buf.append(str+"\n\n");
		
	
		String[] paramNames = popModel.modelParams.paramNames;		
		buf.append("parms <- list(\n");
		if (paramNames.length>0) {
			buf.append("   "+paramNames[0]+" = "+popModel.modelParams.getParam(paramNames[0])+"\n");
			for(int i=1; i < paramNames.length; i++) {
				buf.append(" , "+paramNames[i]+" = "+popModel.modelParams.getParam(paramNames[i])+"\n");
			}
		}
		buf.append(")\n\n");
		
		TrajectoryParameters trajParams = popModel.trajParams;
		String[] initialNames = trajParams.paramNames;
		buf.append("x0 <- c(");
		if (initialNames.length>0) 
			buf.append(" "+initialNames[0]+" = "+trajParams.getParam(initialNames[0]));
		for(int i=1; i < initialNames.length;i++) {
			buf.append(", "+initialNames[i]+" = "+trajParams.getParam(initialNames[i]));
		}
		buf.append(")\n\n");
		
		// create matrices
		buf.append("births <- matrix('0', nrow="+numDemes+", ncol="+numDemes+")\n");
		buf.append("rownames(births)=colnames(births) <- demes\n\n");
		buf.append("migs <- matrix('0', nrow="+numDemes+", ncol="+numDemes+")\n");
		buf.append("rownames(migs)=colnames(migs) <- demes\n\n");
		buf.append("deaths <- rep(0,"+numDemes+")\n");
		buf.append("names(deaths) <- demes \n\n");
		buf.append("nonDemeDynamics <- c() \n");
		
		PopModelRPrinter printer = new PopModelRPrinter(popModel);		
		str = "\n";
		for(MatrixEquationObj eq: popModel.equations) {
			str += this.getLHSR(eq) + " = '" + printer.visit(eq.rhsExprCtx)+"';\n";
		}
		buf.append(str+"\n");
		
		final double t0 = popModel.getStartTime();
		final double t1 = popModel.getEndTime();
		str = "dm <- build.demographic.process( births = births\n";
		str += " , migrations = migs \n , deaths = deaths \n";
		str += " , nonDemeDynamics = nonDemeDynamics\n , parameterNames = names(parms)\n";
		str += " , sde = FALSE\n , rcpp = FALSE\n";
		str += ")\n";
		buf.append("\n"+str+"\n");
		
		str = "t0t1 <- list(";
		str += "t0 = "+ t0 +" , ";
		str += "t1 = "+ t1;
		str += " )\n";
		buf.append("\n"+str+"\n");		
		
		str = "show.demographic.process(dm\n";
		str += " , theta = parms\n , x0 = x0\n";
		str += " , t0 = t0t1$t0 \n";
		str += " , t1 = t0t1$t1\n";
		str += " , integrationMethod = 'rk4'";  // todo change
		str += " , res = 1000\n"; // todo change
		str += ")\n";
		buf.append("\n"+str+"\n");
		
		str = "dm.run <- dm(theta = parms\n";
		str += " , x0 = x0\n";
		str += " , t0 = t0t1$t0 \n";
		str += " , t1 = t0t1$t1 \n";
		str += " , integrationMethod = 'rk4'";  // todo change
		str += " , res = 1000\n"; // todo change
		str += ")\n";
		buf.append("\n"+str+"\n");
		
		return buf.toString();
	}
	
	private String getLHSR(MatrixEquationObj eq) {
		String s;
		switch(eq.type) {
		case BIRTH:
			s = "births['"+eq.originName+"','"+eq.destinationName+"']"; break;
		case MIGRATION:
			s = "migs['"+eq.originName+"','"+eq.destinationName+"']"; break;
		case DEATH:
			s = "deaths['"+eq.originName+"']"; break;
		default: // non-deme
			s = "nonDemeDynamics['" + eq.originName +"']";
		}
		return s;
	}
	

}
