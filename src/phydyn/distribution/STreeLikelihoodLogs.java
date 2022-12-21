package phydyn.distribution;

import beast.base.evolution.tree.Node;

public class STreeLikelihoodLogs {
	
	public int[] nodes;
	public double[] interval, coal, accum;
	
	STreeLikelihoodLogs(int n) {
		nodes = new int[n];
		interval = new double[n];
		coal = new double[n];
		accum = new double[n];
	}
	
	void logInterval(int interval, Node node, double li, double lc, double la) {
		this.nodes[interval] = node.getNr();
		this.interval[interval] = li;
		this.coal[interval] = lc;
		this.accum[interval] = la;
	}

}
