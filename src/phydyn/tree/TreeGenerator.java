package phydyn.tree;

import java.util.List;

import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Runnable;
import beast.evolution.tree.Tree;
import phydyn.model.PopModel;
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
	
    final public Input<String> sampleDatesInput = new Input<>("sampleDates", "Sample dates encoded as deme=value pairs separated by commas", Validate.REQUIRED);

    final public Input<String> sampleSizesInput = new Input<>("sampleSizes", "demes at tips encoded as deme=deme-numbers pairs separated by commas", Validate.XOR, sampleDatesInput);

	
	public Input<Integer> forgiveAgtYInput = new Input<>(
			"numSimulations", "Number of simulations", 
			new Integer(1));

	PopModel popModel;
	int numDemes,numSamples;
	int[] sampleDemes;  // deme idx (as stored in popModel.demeNames)
	double[] sampleTimes;
	double[] sampleSizes;  // number of sampled demes per type. sum total = numSamples.
	double maxSampleTime;
	
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
		sampleSizes = new double[numDemes];
		boolean[] demesUsed = new boolean[numDemes];
		System.out.print("demes = { ");
		for(int i=0; i<numDemes;i++) {
			System.out.print(popModel.demeNames[i]+" ");
			demesUsed[i]=false;
		}
		System.out.println("}");
		if (sampleDatesInput.get()!=null) 
			parseDates(demesUsed);
		else
			parseDemes(demesUsed);
		for(int i=0;i<numDemes;i++) {
			if (!demesUsed[i]) 
				System.out.println("WARNING: Deme "+popModel.demeNames[i]+" not in sample");
		}
		System.out.println("numSamples "+numSamples);
		int brk=0;
		for(int i=0; i < numSamples; i++) {
			System.out.print(popModel.demeNames[sampleDemes[i]]+"="+sampleTimes[i]+" ");
			if (brk++ == 5) {
				brk=0; System.out.println("");
			}
		}
		// Create Simulation object
		simulator = new TreeSimulator(popModel,sampleDemes, sampleTimes, sampleSizes);
		
	}
	
	private void parseDates(boolean[] demesUsed) {
		System.out.println("parsing Dates");
		String[] datePairs = sampleDatesInput.get().split(",");
		for(int i = 0; i < datePairs.length; i++)
			System.out.println(datePairs[i]);
		numSamples = datePairs.length;
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
            sampleSizes[i]++;
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
		System.out.println("parsing Demes");
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
                sampleSizes[i] = Double.parseDouble(strs[1]);
                numSamples += sampleSizes[i];
                demesUsed[i]= true;
            } catch (NumberFormatException e) {
            	throw new IllegalArgumentException("Couldn't parse size in sample size pair "+sizePair);
            }
		}
		// Get maxSampleTime from Model
		maxSampleTime = popModel.getEndTime();
		System.out.println("Sampling time taken from population model: "+maxSampleTime);
		sampleDemes = new int[numSamples];
		sampleTimes = new double[numSamples];
		int idx=0;
		for(int demeIdx=0; demeIdx < numDemes; demeIdx++) {
			for(int j=0; j < sampleSizes[demeIdx]; j++) {
				sampleDemes[idx] = demeIdx;
				sampleTimes[idx] = maxSampleTime;
				idx++;
			}
		}
		// System.out.println("numSamples "+numSamples+" -- " + idx);
		
	}
	


	@Override
	public void run() throws Exception {
		// initialize TreeSim object
		

	}

}
