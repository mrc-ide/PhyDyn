package phydyn.tree;

import java.io.FileWriter;
import java.util.List;
import java.util.Set;

import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Runnable;
import beast.evolution.alignment.TaxonSet;
import beast.evolution.tree.Node;
import beast.evolution.tree.TraitSet;
import beast.evolution.tree.Tree;
import beast.util.HeapSort;
import phydyn.model.PopModel;
import phydyn.tree.TreeSimulator.SimMethod;
import phydyn.util.General;

/*
 * @author Igor Siveroni
 * BEAST Runnable object for generating structured coalescent trees.
 * Provides XML interface to TreeSimulator, a class of objects that generate
 * trees by simulating the structured coalescent process.
 * 
 * Under developement
 */

/*
 * TODO:
 * Add DateTrait, TypeTrait and Taxa as input.
 * 
 */

public class TreeGenerator extends Runnable {
	
	public Input<PopModel> popModelInput = new Input<>(
			 "popModel","Population Model",Validate.REQUIRED);
	
    final public Input<TaxonSet> taxaInput = new Input<>("taxa","Taxa names");
    public Input<TraitSet> typeTraitInput = new Input<>(
            "typeTrait", "Type trait set maps taxa to deme name.");
    public Input<TraitSet> dateTraitInput = new Input<>(
            "dateTrait", "Date trait set maps taxa to sampling date.");
	
	final public Input<String> sampleDatesInput = new Input<>("sampleDates", "Sample dates encoded as deme=value pairs separated by commas");

    final public Input<String> sampleSizesInput = new Input<>("sampleSizes", "demes at tips encoded as deme=deme-numbers pairs separated by commas");

	
	public Input<Integer> numSimulationsInput = new Input<>(
			"numSimulations", "Number of simulations", 
			new Integer(1));
	
	public Input<Integer> seedInput = new Input<>(
			"seed", "Set seed of random number generator", 
			new Integer(127));
	
	public Input<String> simMethodInput = new Input<>("simulationMethod", 
			"Simulation method (GLSP1,GLSP2)");
	
	public Input<String> outputInput = new Input<>("output",
			"Output file");

	PopModel popModel;
	int numDemes,numSamples;
	private boolean[] demesUsed;
	private int[] sampleDemes;  // deme idx (as stored in popModel.demeNames)
	private double[] sampleTimes;
	private String[] tipNames;
	int[] sampleSizes;  // number of sampled demes per type. sum total = numSamples.
	double maxSampleTime;
	boolean needSorting;
	String fileName;
	
	private TreeSimulator simulator;
	// Each simulation can produce multiple trees due to multifurcation
	private List<List<Tree>> trees;
	
	public TreeGenerator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initAndValidate() {
		popModel = popModelInput.get();
		numDemes = popModel.numDemes;
		sampleSizes = new int[numDemes];
		needSorting=true;
		
		// report popmodel demes
		demesUsed = new boolean[numDemes];
		System.out.print("demes = { ");
		for(int i=0; i<numDemes;i++) {
			System.out.print(popModel.demeNames[i]+" ");
			demesUsed[i]=false;
		}
		System.out.println("}");
		
		if (sampleDatesInput.get()!=null && sampleSizesInput.get()!=null)
			throw new IllegalArgumentException("Either input sampleDates or sampleSizes (but not both)");
		
		if (sampleDatesInput.get()!=null || sampleSizesInput.get()!=null) {
			processDefaults();
		} else {
			if (taxaInput.get()==null)
				throw new IllegalArgumentException("Expecting Taxa -- not found");
			if (typeTraitInput.get()==null|| dateTraitInput.get()==null) {
				throw new IllegalArgumentException("Date or Type trait missing");
			}
			processTraits();
		}
		
		
		if (numSamples<2) return;
		
		System.out.println("numSamples = "+numSamples);
		// printing taxa so it can be easily copied
		System.out.println("Taxa:");
		int counter=0;
		for(int i=0; i<numSamples-1; i++) {
			counter++;
			if (counter == 5) {
				System.out.println("   "+tipNames[i]+",");
				counter = 0;
			} else {
				System.out.print("   "+tipNames[i]+",");
			}
		}
		if (counter==5) System.out.println("");
		System.out.print("   "+tipNames[numSamples-1]);
		System.out.println("\nEnd of Taxa");
		System.out.println("Taxon/TipName\tDeme\tDate");
		for(int i=0; i < numSamples; i++) {
			System.out.print(tipNames[i]+"\t"+popModel.demeNames[sampleDemes[i]]+"\t");
			System.out.println(sampleTimes[i]);

		}
		System.out.println(" ");
		
		if (numSamples<2) return;
		
		if (needSorting)
			sortDates();
		
		// remove after debugging
		if (maxSampleTime < sampleTimes[0])
			new IllegalArgumentException("Problem with sorting dates");
		
		System.out.println("Setting t1 to maxSampleTime="+maxSampleTime);
		popModel.setEndTime(maxSampleTime);
		
		SimMethod method;
		if (simMethodInput.get()==null) {
			System.out.println("No method specified");
			method = SimMethod.GLSP1;
		} else {
			if (simMethodInput.get().equals("GLSP1")) {
				method = SimMethod.GLSP1;
			} else if (simMethodInput.get().equals("GLSP2")) {
				method = SimMethod.GLSP2;
			} else {
				throw new IllegalArgumentException("Incorrect simulation method. \nPick from: GLSP1 | GLSP2");
			}
		}
		
		if (outputInput.get()==null) {
			fileName = "outputTreeSimulation.txt";
		} else
			fileName = outputInput.get();
		
		// Create Simulation object
		simulator = new TreeSimulator(popModel,tipNames,sampleDemes, sampleTimes, sampleSizes);
		simulator.setMethod(method);
	}
	
	private void processTraits() {
		System.out.println("processing traits...");
		
		TaxonSet taxa = taxaInput.get();
		numSamples = taxa.getTaxonCount();
		
		tipNames = new String[numSamples];
		sampleDemes = new int[numSamples];
		sampleTimes = new double[numSamples];
		taxa.getTaxaNames().toArray(tipNames);
		
		
		// type trait
		TraitSet types =  typeTraitInput.get();
		TraitSet dates =  dateTraitInput.get();
				
		for(int i =0; i < numSamples; i++) {
			String taxonName = tipNames[i];
			// Get deme name from type trait
			String demeName = types.getStringValue(taxonName);
			if(demeName==null) {
				System.out.println("Taxon without deme type: "+taxonName);
				throw new IllegalArgumentException("Unknown type for taxon: "+taxonName);
			}
			int demeIdx = General.indexOf(demeName,popModel.demeNames);
			if (demeIdx==-1) {
				throw new IllegalArgumentException("Unknown deme name in Type trait: "+taxonName+" = "+demeName);
			}
			sampleDemes[i] = demeIdx;
			// get sampling date for each taxa
			String dateString = dates.getStringValue(taxonName);
			if(dateString==null) {
				System.out.println("Taxon not dated: "+taxonName);
				throw new IllegalArgumentException("Taxon not dated: "+taxonName);
			}
			
			sampleTimes[i] = Double.parseDouble(dateString);
		}
		
		
	}
	
	private void processDefaults() {
				
		if (sampleDatesInput.get()!=null) 
			parseDates(demesUsed);
		else
			parseDemes(demesUsed);
		for(int i=0;i<numDemes;i++) {
			if (!demesUsed[i]) 
				System.out.println("WARNING: Deme "+popModel.demeNames[i]+" not in sample");
		}
		
	}
	
	private void parseDates(boolean[] demesUsed) {
		System.out.println("processing sample dates...");
		String[] datePairs = sampleDatesInput.get().split(",");
		//for(int i = 0; i < datePairs.length; i++)
		//	System.out.println(datePairs[i]);
		numSamples = datePairs.length;
		tipNames = new String[numSamples];
		sampleDemes = new int[numSamples];
		sampleTimes = new double[numSamples];
		String datePair;
		for(int i=0; i < numSamples; i++) {
			datePair = datePairs[i].replaceAll("\\s+", " ");
            String[] strs = datePair.split("=");
            if (strs.length != 2) {
                throw new IllegalArgumentException("could not parse sample date pair: " + datePair);
            }
            String demeName = General.normalize(strs[0]);
            int demeIdx = General.indexOf(demeName,popModel.demeNames);
            if (demeIdx==-1)
            	throw new IllegalArgumentException("Unknown deme name in sample date pair "+datePair);
            //let's work fir Double literals first
            try {
                sampleTimes[i] = Double.parseDouble(strs[1]);
            } catch (NumberFormatException e) {
            	throw new IllegalArgumentException("Couldn't parse date in sample date pair "+datePair);
            }
            tipNames[i] = i+"_"+strs[1]+"_"+demeName;
            sampleSizes[demeIdx]++;
            sampleDemes[i] = demeIdx;
            demesUsed[demeIdx]=true;
		}
		maxSampleTime = sampleTimes[0];
		for(int i= 1; i < numSamples; i++) {
			if (sampleTimes[i] > maxSampleTime)
				maxSampleTime = sampleTimes[i];
		}
		
	}
	
	private void parseDemes(boolean[] demesUsed) {
		System.out.println("processing sample deme sizes...");
		String[] sizesPairs = sampleSizesInput.get().split(",");
		numSamples=0;
		String sizePair;
		for(int i=0; i < sizesPairs.length; i++) {
			sizePair = sizesPairs[i].replaceAll("\\s+", " ");
			String[] strs = sizePair.split("=");
			if (strs.length != 2) {
                throw new IllegalArgumentException("could not parse sample size pair: " + sizePair);
            }
			String demeName = General.normalize(strs[0]);
			int demeIdx = General.indexOf(demeName,popModel.demeNames);
			if (demeIdx==-1)
            	throw new IllegalArgumentException("Unknown deme name in sample size pair "+sizePair);
			try {
                sampleSizes[i] = Integer.parseInt(General.normalize(strs[1]));
                numSamples += sampleSizes[i];
                demesUsed[i]= true;
            } catch (NumberFormatException e) {
            	throw new IllegalArgumentException("Couldn't parse size value in sample size pair "+sizePair);
            }
		}
		// Get maxSampleTime from Model
		maxSampleTime = popModel.getEndTime();
		System.out.println("Sampling time taken from population model: "+maxSampleTime);
		tipNames = new String[numSamples];
		sampleDemes = new int[numSamples];
		sampleTimes = new double[numSamples];
		needSorting=false;
		int idx=0;
		for(int demeIdx=0; demeIdx < numDemes; demeIdx++) {
			for(int j=0; j < sampleSizes[demeIdx]; j++) {
				tipNames[idx] = idx+"_"+maxSampleTime+"_"+popModel.demeNames[demeIdx];
				sampleDemes[idx] = demeIdx;
				sampleTimes[idx] = maxSampleTime;
				idx++;
			}
		}
		// System.out.println("numSamples "+numSamples+" -- " + idx);
		
	}
	
	private void sortDates() {
		int[] indices = new int[sampleTimes.length];
		HeapSort.sort(sampleTimes,indices);
		String[] tempNames = new String[numSamples];
		double[] tempTimes = new double[numSamples];
		int[] tempDemes = new int[numSamples];
		int idx = 0;
		for(int i=indices.length-1; i>=0;i--) {
			tempNames[idx] = tipNames[indices[i]];
			tempTimes[idx] = sampleTimes[indices[i]];
			tempDemes[idx] = sampleDemes[indices[i]];
			idx++;
		}
		tipNames = tempNames;
		sampleTimes = tempTimes;
		sampleDemes = tempDemes;
	}
	
	


	@Override
	public void run() throws Exception {
		// initialize TreeSim object
		if (numSamples < 2) {
			System.out.println("Insufficient sample data");
			return;
		}
		System.out.println("Starting simulations");
		List<TreeSimulator.Result> rs = simulator.simulate(numSimulationsInput.get(),seedInput.get());
		
		System.out.println("Num Multifurcating="+simulator.numMultifurcating);
		
		FileWriter writer = new FileWriter(fileName);
		System.out.println("Writing output to "+fileName);
				
		writer.append("sim\tmulti\tt_root\th\tnewick\n");
		int sim=0, simGood=0;
		double avg=0, avgAll=0;
		for(TreeSimulator.Result r : rs) {
			avgAll += r.h; sim++;
			if (r.numCollapsed==0) { avg += r.h; simGood++; }
			writer.append(sim+"\t"+r.numCollapsed+"\t"+r.t+"\t"+r.h+"\t");
			final String newick = toNewick(r.tree.getRoot(), true);
			writer.append(newick+"\n");
		}
		avgAll /= sim;
		avg /= (simGood);
		System.out.println("Avg Height: "+avgAll);
		System.out.println("Avg Height / no multifurcating = "+avg);
		writer.flush();
	    writer.close();
	}
	
	public String toNewick(Node node, boolean isRoot) {
		final StringBuilder buf = new StringBuilder();
		if (!node.isLeaf()) {
			buf.append("(");
			boolean isFirst = true;
			for (Node child : node.getChildren()) {
				if (isFirst)
					isFirst = false;
				else
					buf.append(",");
	                buf.append(toNewick(child,false));
			}
			buf.append(")");

			if (getID() != null)
				buf.append(node.getID());
		} else {  // node is leaf
			if (node.getID() != null)
				buf.append(node.getID());
			else
				buf.append(node.getNr());
		}
		
		buf.append(node.getNewickMetaData());
		if (isRoot)
			buf.append(";");
		else
			buf.append(":").append(node.getNewickLengthMetaData()).append(node.getLength());
	       
		return buf.toString();
	}

}
