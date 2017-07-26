package phydyn.loggers;


import beast.core.CalculationNode;
import beast.core.Description;
import beast.core.Distribution;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.Loggable;
import java.io.PrintStream;



/**
 * Used to define an end criterion 
 * @author Nicola Felix Mueller
 * 
 */
@Description("Kills a run if the posterior is below a the percentage of the maximum posterior")
public class EndCriterion extends CalculationNode implements Loggable {


    public Input<Distribution> posteriorInput = new Input<>(
        "posterior",
        "Posterior used to identify MAP tree",
        Validate.REQUIRED);
    
    final public Input<Double> lowerBoundInput = new Input<>("lowerBound", 
    		"defines a lower bound for the posterior value. If the posterior is Input*maximum posterior smaller " +
    		" than the maximum posterior, the MCMC will be finished");
    
    double maxPosterior;
    
    enum EndCriterionName{
    	lowerBoundPosterior, ESS
    }
    EndCriterionName endCriterionName;

    @Override
    public void initAndValidate() {
        maxPosterior = Double.NEGATIVE_INFINITY;
        if(lowerBoundInput!=null)
        	endCriterionName = EndCriterionName.lowerBoundPosterior;
    }

    @Override
    public void init(PrintStream out) {
    }

    @Override
    public void log(int nSample, PrintStream out) {
    	System.out.println("Logging endcriterion:");
    	if(lowerBoundInput!=null){
	        if (posteriorInput.get().getCurrentLogP()>maxPosterior) {
	            maxPosterior = posteriorInput.get().getCurrentLogP();
	        // else if (maxPosterior*-lowerBoundInput.get() > posteriorInput.get().getCurrentLogP()){
	        }else if ( (maxPosterior - Math.abs(maxPosterior*lowerBoundInput.get()))  > posteriorInput.get().getCurrentLogP()){	        	
	        	System.out.println("The end criteria is reached, the run will be terminated by the logger");
	        	System.exit(1);
	        }
    	}else {
    		
    	}
    }

    @Override
    public void close(PrintStream out) {
    }
    

}
