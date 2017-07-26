package phydyn.loggers;

import java.io.PrintStream;

import beast.core.CalculationNode;
import beast.core.Loggable;

public class MemoryLogger extends CalculationNode implements Loggable {
	private Runtime runtime; 
	public MemoryLogger() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(PrintStream out) {
		out.print("used available");

	}

	@Override
	public void log(int nSample, PrintStream out) {

	   	//Runtime runtime = Runtime.getRuntime();
    	//long maxMemory = runtime.maxMemory();
    	//long allocatedMemory = runtime.totalMemory();
    	//long freeMemory = runtime.freeMemory();
    	//System.out.println("free memory: " + freeMemory / 1024 );
    	//System.out.println("allocated memory: " + allocatedMemory / 1024 );
    	//System.out.println("max memory: " + maxMemory / 1024 );
    	//System.out.println("total free memory: "+(freeMemory + (maxMemory - allocatedMemory)) / 1024);
		
		 runtime = Runtime.getRuntime();
		 long totalMemory = runtime.totalMemory();
		 long freeMemory = runtime.freeMemory();
		 long maxMemory = runtime.maxMemory();
		 long usedMemory = totalMemory - freeMemory;
		 long availableMemory = maxMemory - usedMemory;
		
		out.print(usedMemory+" "+availableMemory);

	}

	@Override
	public void close(PrintStream out) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initAndValidate() {
		// TODO Auto-generated method stub
		
	}



}
