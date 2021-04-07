package phydyn.model.translate;

import java.util.LinkedList;
import java.util.List;

import phydyn.model.DefinitionObj;
import phydyn.model.MatrixEquationObj;
import phydyn.model.PopModelODE;
import phydyn.model.TrajectoryParameters;

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
	
	public String generateR() {
		StringBuffer buf = new StringBuffer();
		generateR(buf);
		return buf.toString();
	}
	
	public void generateR(StringBuffer buf) {
		
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
		final double t0 = popModel.getStartTime();
		final double t1 = popModel.getEndTime();
		buf.append(" , T0 = "+t0+"\n");
		buf.append(" , T1 = "+t1+"\n");
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
		
		// PopModelRPrinter printer = new PopModelRPrinter(popModel);		
		PopModelRPrinter printer = PopModelRPrinter.create(popModel, RTarget.PHYDYNR);	
		
		str = "\n";
		for(MatrixEquationObj eq: popModel.equations) {
			str += this.getLHSR(eq) + " = '" + printer.visit(eq.rhsExprCtx)+"';\n";
		}
		buf.append(str+"\n");
		
		
		str = "dm <- build.demographic.process( births = births\n";
		str += " , migrations = migs \n , deaths = deaths \n";
		str += " , nonDemeDynamics = nonDemeDynamics\n , parameterNames = names(parms)\n";
		str += " , sde = FALSE\n , rcpp = FALSE\n";
		str += ")\n";
		buf.append("\n"+str+"\n");
		
		
		str = "show.demographic.process(dm\n";
		str += " , theta = parms\n , x0 = x0\n";
		str += " , t0 = parms$T0 \n";
		str += " , t1 = parms$T1\n";
		str += " , integrationMethod = 'rk4'";  // todo change
		str += " , res = 1000\n"; // todo change
		str += ")\n";
		buf.append("\n"+str+"\n");
		
		str = "dm.run <- dm(theta = parms\n";
		str += " , x0 = x0\n";
		str += " , t0 = parms$T0 \n";
		str += " , t1 = parms$T1 \n";
		str += " , integrationMethod = 'rk4'";  // todo change
		str += " , res = 1000\n"; // todo change
		str += ")\n";
		buf.append("\n"+str+"\n");
		
		return;
	}
	
	public String generateOdin() {
		StringBuffer buf = new StringBuffer();
		generateOdin(buf);
		return buf.toString();
	}
	
	public void generateOdin(StringBuffer buf) {
		
		// dot(deme) and dot(non-deme)
		PopModelRPrinter printer = PopModelRPrinter.create(popModel, RTarget.ODIN);
		
		List<MatrixEquationObj> nondemeEqs = new LinkedList<MatrixEquationObj>();
		List<MatrixEquationObj> birthEqs = new LinkedList<MatrixEquationObj>();
		List<MatrixEquationObj> migrationEqs = new LinkedList<MatrixEquationObj>();
		List<MatrixEquationObj> deathEqs = new LinkedList<MatrixEquationObj>();
		
		for(MatrixEquationObj eq: popModel.equations) {
			switch(eq.type) {
				case BIRTH:
					birthEqs.add(eq); break;
				case MIGRATION:
					migrationEqs.add(eq); break;
				case DEATH:
					deathEqs.add(eq); break;	
				case NONDEME:
					nondemeEqs.add(eq); break;	
			}
			
		}
		
		String[] demes = popModel.demeNames;
		for(int i = 0; i < demes.length; i++) {
			final String deme = demes[i];
			boolean first = true;
			buf.append("deriv("+demes[i]+") <- ");
			for(MatrixEquationObj eq: birthEqs) {
				if (deme.equals(eq.destinationName)) {
					if (first) first=false;
					else buf.append(" + ");
					buf.append(printer.visit(eq.rhsExprCtx));
				}
			}
			for(MatrixEquationObj eq: migrationEqs) {
				if (deme.equals(eq.destinationName)) {
					if (first) first=false;
					else buf.append(" + ");
					buf.append(printer.visit(eq.rhsExprCtx));
				}
			}
			for(MatrixEquationObj eq: migrationEqs) {
				if (deme.equals(eq.originName)) {
					if (first) first=false;
					else buf.append(" - ");
					buf.append(printer.visit(eq.rhsExprCtx));
				}
			}
			for(MatrixEquationObj eq: deathEqs) {
				if (deme.equals(eq.originName)) {
					if (first) first=false;
					else buf.append(" - ");
					buf.append(printer.visit(eq.rhsExprCtx));
				}
			}
			buf.append("\n");
		}
		
		buf.append("\n");
		for(MatrixEquationObj eq: nondemeEqs) {
			buf.append("deriv("+eq.originName+") <- "+printer.visit(eq.rhsExprCtx)+"\n");
		}
		buf.append("\n");		
		
		buf.append("\n");
		for(DefinitionObj def: popModel.definitions) {
			buf.append(printer.visit(def.stm)+"\n");
		}
		buf.append("\n");
		
		// initial values
		buf.append("# t0 = "+popModel.getStartTime()+"  t1 = "+popModel.getEndTime()+"\n");
		TrajectoryParameters trajParams = popModel.trajParams;
		String[] initialNames = trajParams.paramNames;
		for(int i=0; i < initialNames.length;i++) {
			buf.append("initial("+initialNames[i]+") <- "+trajParams.getParam(initialNames[i])+"\n");
		}
		buf.append("\n");
		
		String[] paramNames = popModel.modelParams.paramNames;		
		if (paramNames.length>0) {
			for(int i=0; i < paramNames.length; i++) {
				buf.append(paramNames[i]+" <- "+popModel.modelParams.getParam(paramNames[i])+"\n");
			}
		}
		buf.append("\n");
		
		
		
		
		return;
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
