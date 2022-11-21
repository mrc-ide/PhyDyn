package phydyn.command;

import beast.base.evolution.alignment.Alignment;
import beast.base.evolution.alignment.Sequence;
import beast.base.inference.MCMC;
import beast.base.inference.Runnable;
import phydyn.model.PopModel;
import phydyn.model.PopModelODE;
import phydyn.model.translate.PopModelODETranslator;
import phydyn.util.PhyDynXMLParser;

import java.io.File;
import java.io.FileWriter;
import java.util.List;



public class CommandLine {
	
	static String version = "0.0.0";
	
	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println("Command name missing ... quitting");
		}
		
		String command =  args[0].toLowerCase();
		
		if (command.equals("--version")) {
			doVersion(args);
		} else if (command.equals("--help") || command.equals("help")) {
			doHelp(args);
		} else if (command.equals("generate-phydynr")) {
			doGeneratePhydynR(args);
		} else if (command.equals("generate-odin")) {
			doGenerateOdin(args);
		} else if (command.equals("extract-taxa")) {
			doExtractTaxa(args);
		} else if (command.equals("extract-sequences")) {
			doExtractSequences(args);
		} else {
			System.out.println("Command unknown: "+command);
		}
		
		System.out.println("Executed Command");
		return;
		
		/*
		STreeLikelihoodODE stllh = parser.getLikelihood();		
		Alignment algn = parser.getAlignment();
		
		if (algn!=null) {		
			int numTaxa = algn.getTaxonCount();
			int numSites = algn.getSiteCount();			
			System.out.println("("+numTaxa+" , "+numSites+")");		
		}
		*/
		
		
		/*
		final String[] classNames = { "Alignment" , "STreelikelihoodODE", "Distribution" };
		Class<?>[] clazzes = new Class<?>[classNames.length];		
		for(int i = 0; i < classNames.length; i++) {
			try {
				clazzes[i] = BEASTClassLoader.forName(classNames[i]);
			} catch (Exception e) {
				System.out.println(classNames[i]);
				throw new IllegalArgumentException("Couldn't find class : "+ classNames[i]);			
			}
		}
		List<BEASTInterface>[] boArray = parser.getObjectsbyClass(clazzes);
		
		for(int i=0;  i < boArray.length; i++) {
			System.out.println("Objects of class:"+classNames[i]);
			final List<BEASTInterface>bos = boArray[i];
			for(BEASTInterface obj : bos) {
				System.out.println(" -- "+obj.getID());
			}
		}
		*/
		
		
		
	}  // Main(...)
	
	static void exitProgram(String msg) {
		System.exit(1);
	}
	
	
	static void doVersion(String[] args) {
		System.out.println("Phydyn Utils. Version "+version);
	}
	
	static void doHelp(String[] args) {
		// accepts 0 or 1 arguments
		System.out.println("PhyDyn Utils help");
	}
	
	static void doGeneratePhydynR(String[] args) {
		generateModel(args, "r");
	}
		
	static void doGenerateOdin(String[] args) {
	   generateModel(args, "odin");
	}
	
	static void generateModel(String[] args, String target) {
		if (args.length<2) {
			System.out.println("Input file missing ... quitting");
			return;
		}	
		String fin = args[1];
		if (args.length<3) {
			System.out.println("Output file missing ... quitting");
			return;
		}	
		String fout = args[2];
		File f = new File(fin);
		// check if file exists?
		Runnable runnable=null;
		PhyDynXMLParser parser = new PhyDynXMLParser();
		try {
			runnable = parser.parseFile(f); // will print stuff during object creation
		} catch (Exception e) {
			System.out.println("Couldn't parse file "+fin+" ... quitting");
		}
		parser.collectBEASTObjects();
		List<PopModel> pmlist = parser.popmodels;
		if (pmlist.size()<1) {
			System.out.println("Couldn't find PopModel in "+fin+" ... quitting");
			return;
		}
		PopModelODE pmODE;
		if (pmlist.get(0) instanceof PopModelODE) {
			pmODE = (PopModelODE) pmlist.get(0);
		} else {
			System.out.println("Couldn't find PopModelODE in "+fin+" ... quitting");
			return;
		}
		System.out.println("Found popmodel "+pmODE.getName());
		PopModelODETranslator trans = new PopModelODETranslator(pmODE);
		FileWriter writer;
		StringBuffer buf;
		try {		
			buf = new StringBuffer();
			if (target.equals("r")) {  // targets should be checked before-hand
				trans.generateR(buf);
			} else if (target.equals("odin")) {
				trans.generateOdin(buf);
			} else {
				System.out.println("Unknown target type ... quitting");
				return;
			}
			writer = new FileWriter(fout);
			writer.append(buf.toString());
			writer.flush();
		    writer.close();
		} catch (Exception e) {
			System.out.println("Couldn't create output file "+fout+" ... quiting");
			return;
		}
		
		System.out.println("R code generated in :"+fout);
		System.out.println("code:\n" + buf.toString());
	}
	
	static void doExtractSequences(String[] args) {
		if (args.length<2) {
			System.out.println("Input file missing ... quitting");
			return;
		}	
		if (args.length<3) {
			System.out.println("Output file missing ... quitting");
			return;
		}	
		String fin = args[1];
		String fout = args[2];
		File f = new File(fin);
		// check if file exists?
		Runnable runnable=null;
		PhyDynXMLParser parser = new PhyDynXMLParser();
		try {
			runnable = parser.parseFile(f); // will print stuff during object creation
		} catch (Exception e) {
			System.out.println("Couldn't parse file "+fin+" ... quitting");
			return;
		}
		
		parser.collectBEASTObjects();
				
		List<Alignment> alignments = parser.alignments;	
		if (alignments.size()<1) {
			System.out.println("Couldn't find any Alignment object in file");
			return;
		}
		System.out.println("Alignments:");
		for(int i = 0; i < parser.alignments.size(); i++){
			System.out.println("  -- "+alignments.get(i).getID());
		}		
		Alignment algn = alignments.get(0);		
		List<Sequence> seqs = algn.sequenceInput.get();
		StringBuffer buf = new StringBuffer();
		for(Sequence seq : seqs) {
			buf.append(">"+seq.getTaxon()+"\n");
			buf.append(seq.getData()+"\n");
		}
		try {
			FileWriter writer = new FileWriter(fout);
			writer.append(buf.toString());
			writer.flush();
		    writer.close();
		} catch (Exception e) {
			System.out.println("Couldn't create Fasta file "+fout);
		}
		System.out.println("Saved sequences in Fasta format to "+fout);
	}
	
	static void doExtractTaxa(String[] args) {
		if (args.length<2) {
			System.out.println("Input file missing ... quitting");
			return;
		}		
		
		String fin = args[1];
		File f = new File(fin);
		// check if file exists?
		
		
		Runnable runnable=null;
		PhyDynXMLParser parser = new PhyDynXMLParser();
		try {
			runnable = parser.parseFile(f); // will print stuff during object creation
		} catch (Exception e) {
			System.out.println("Couldn't parse file "+fin+" ... quitting");
			return;
		}
		parser.collectBEASTObjects();
		List<PopModel> pmlist = parser.popmodels;
		if (pmlist.size()<1) {
			System.out.println("Couldn't find PopModel in "+fin+" ... quitting");
			return;
		}
	}
	

		
	
	
	
	
	
	
	static void rambling() {
				
		System.out.println("testing command line");
		System.out.println("Working Directory = " + System.getProperty("user.dir"));	
			
		File f = new File("extest/corona/seirBeauti.xml");
			
		System.out.println("read file:\n"+f.toString());
		PhyDynXMLParser parser = new PhyDynXMLParser();
	
		Runnable runnable;
		
		try {
			runnable = parser.parseFile(f); // will print stuff during object creation
		} catch (Exception e) {
			throw new IllegalArgumentException("Couldn't parse file");
		}
		System.out.println("Runnable object: "+runnable.toString());
			
		if (runnable instanceof MCMC) {
			System.out.println("MCMC object!!");
		}
			
		parser.collectBEASTObjects();
			
		System.out.println("Alignment");
		for(int i = 0; i < parser.alignments.size(); i++){
			System.out.println("  -- "+parser.alignments.get(i));
		}
		System.out.println("Distribution");
		for(int i = 0; i < parser.distributions.size(); i++){
			System.out.println("  -- "+parser.distributions.get(i));
		}
		System.out.println("STreeLikelihood");
		for(int i = 0; i < parser.stlhs.size(); i++){
			System.out.println("  -- "+parser.stlhs.get(i));
		}
		System.out.println("Parameters");
		for(int i = 0; i < parser.parameters.size(); i++){
			System.out.println("  -- "+parser.parameters.get(i));
		}
		System.out.println("Parameter Values");
		for(int i = 0; i < parser.paramvalues.size(); i++){
			System.out.println("  -- "+parser.paramvalues.get(i));
		}
			
	}
	

		
	
		/* 
		 * Traverse DOM beast.tree and grab all nodes that have an 'id' attribute
		 * IDNodeMap.put(id, node);
		 * 
		 * BEASTInterface beastObject 
		 * 
		 * final BEASTInterface beastObject = IDMap.get(id);
		 * 
		 * if (IDMap.containsKey(dRef))
		 * if (IDNodeMap.containsKey(dRef))
		 * 
		 * init : class beast.base.evolution.alignment.Alignment
		 * Alignment(algn)
		 * 53 taxa
		 * 29903 sites
		 * 200 patterns
		 * siteCount = getSiteCount();
		 * getTaxonCount() + " taxa" 
		 * siteCount + (siteCount == 1 ? " site" : " sites") 
		 * getPatternCount() + " patterns
		 * 
		 * 
		 * 
		 * 
		 */
		
		/**
	     * records id in IDMap, for ease of retrieving beast objects associated with idrefs *
	     */
		/*
	    void register(final Node node, final BEASTInterface beastObject) {
	        final String id = getID(node);
	        if (id != null) {
	            IDMap.put(id, beastObject);
	        }
	    }
		*/
		
		
		/*
		 boolean checkType(final String className, final BEASTInterface beastObject, Node node) throws XMLParserException  {
		        try {
					if (className.equals(INPUT_CLASS) || BEASTClassLoader.forName(className).isInstance(beastObject)) {
					    return true;
					}
				} catch (ClassNotFoundException e) {
					throw new XMLParserException(node, "Class not found:" + e.getMessage(), 444);
				}
		        // parameter clutch
		        if (className.equals("RealParameter") && beastObject instanceof Parameter<?>) {
		            return true;
		        }
		        return false;
		    } // checkType
		    */
		
		

}
