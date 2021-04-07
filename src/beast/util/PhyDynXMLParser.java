package beast.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import beast.core.BEASTInterface;
import beast.core.Distribution;
import beast.core.Operator;
import beast.core.Runnable;
import beast.core.parameter.Parameter;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.TaxonSet;
import beast.evolution.tree.Tree;
import phydyn.distribution.STreeLikelihood;
import phydyn.distribution.STreeLikelihoodODE;
import phydyn.model.ParamValue;
import phydyn.model.PopModel;

/*
 * Parses xml BEAST file and collects BEAST objects by class
 * Note: Declared inside beast.util to access IDMap (package visibility)
 */

public class PhyDynXMLParser {
	
	XMLParser parser;
	public List<Alignment> alignments = new ArrayList<Alignment>();
	public List<Distribution> distributions = new ArrayList<Distribution>();
	public List<STreeLikelihood> stlhs = new ArrayList<STreeLikelihood>();
	public List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();
	public List<Tree> trees = new ArrayList<Tree>();
	public List<Operator> operators = new ArrayList<Operator>();
	public List<ParamValue> paramvalues = new ArrayList<ParamValue>();
	public List<PopModel> popmodels = new ArrayList<PopModel>();
	public List<TaxonSet> taxa = new ArrayList<TaxonSet>();
	
	
	public PhyDynXMLParser() {
		parser = new XMLParser();		
	}
	
	public Runnable parseFile(File f) throws SAXException, IOException, ParserConfigurationException, XMLParserException  {
		return parser.parseFile(f);
	}
	
	
	public void collectBEASTObjects() {
		for (HashMap.Entry<String, BEASTInterface> entry : parser.IDMap.entrySet()) {
			final BEASTInterface bo = entry.getValue();
			if (bo instanceof Alignment) {
				alignments.add((Alignment) bo);
			} 
			if (bo instanceof Distribution) {
				distributions.add((Distribution) bo);
			} 
			if (bo instanceof STreeLikelihood) {
				stlhs.add((STreeLikelihood) bo);
			} 
			if (bo instanceof Parameter<?>) {
				parameters.add((Parameter<?>) bo);
			} 
			if (bo instanceof Tree) {
				trees.add((Tree) bo);
			} 
			if (bo instanceof Operator) {
				operators.add((Operator) bo);
			} 
			if (bo instanceof ParamValue) {
				paramvalues.add((ParamValue) bo);
			} 
			if (bo instanceof PopModel) {
				popmodels.add((PopModel) bo);
			} 
			if (bo instanceof TaxonSet) {
				taxa.add((TaxonSet) bo);
			} 
		}
	}
	
	
	public List<BEASTInterface>[] getObjectsbyClass(Class<?>[] classNames) {
				
		@SuppressWarnings("unchecked")
		List<BEASTInterface>[] answer =  new ArrayList[classNames.length];
		
		for(int i=0; i < answer.length; i++) {
			answer[i] = new ArrayList<BEASTInterface>();
		}
		
		for (HashMap.Entry<String, BEASTInterface> entry : parser.IDMap.entrySet()) {
			final BEASTInterface bo = entry.getValue();
			for(int i=0; i< classNames.length; i++) {
				if (classNames[i].isInstance(bo)  ) {
					answer[i].add(bo);
				}		
			}
		
		}
		
		return answer;
	}
	
	public STreeLikelihoodODE getLikelihood() {
		
		BEASTInterface bo = null;
		for (HashMap.Entry<String, BEASTInterface> entry : parser.IDMap.entrySet()) {
		    System.out.println(entry.getKey());  
		    bo = entry.getValue();
		    if (bo instanceof STreeLikelihoodODE) {
		    	System.out.println("------------  Found likehood!: "+ bo + " id = "+ bo.getID());
		    	return (STreeLikelihoodODE) bo;
		    }
		}
		
		return null;
	}
	
	public Alignment getAlignment() {
				
		BEASTInterface bo = null;
		for (HashMap.Entry<String, BEASTInterface> entry : parser.IDMap.entrySet()) {
		    System.out.println(entry.getKey());  
		    bo = entry.getValue();
		    if (bo instanceof Alignment) {
		    	System.out.println("------------  Found Alignment !: "+ bo + " id = "+ bo.getID());
		    	return (Alignment) bo;
		    }
		}
		
		return null;
	}

}
