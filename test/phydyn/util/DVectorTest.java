package phydyn.util;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DVectorTest {
	static Random rand = new Random();
	static final int BUFFER_SIZE=100;
	static double[] randomBuffer=null;
	
	static double[] getBuffer() {
		if (randomBuffer==null) {
			rand.setSeed(127);
			randomBuffer = new double[BUFFER_SIZE];
			for(int i=0; i<BUFFER_SIZE;i++) {
				randomBuffer[i] = 10*(1 - 2*rand.nextDouble());
				// [-10,10]
			}
		}
		return randomBuffer;
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDotProduct() {
		System.out.println("DVector: dot product test");
		final int n=10;
	
		double[] buffer = getBuffer();
		DVector v1 = new DVector(n,buffer);
		double rdot = v1.dot(v1);
		double sum=0;
		for(int i=0; i < n; i++) {
			sum += v1.get(i)*v1.get(i);
		}
		assertEquals("Error dot product v1*v1",sum,rdot, 0.00001);
		
		DVector v2 = new DVector(n,buffer,25);
		rdot = v2.dot(v2);
		sum=0;
		for(int i=0; i < n; i++) {
			sum += v2.get(i)*v2.get(i);
		}
		assertEquals("Error: dot product v2*v2",sum,rdot, 0.00001);
		
		rdot = v1.dot(v2);
		sum=0;
		for(int i=0; i < n; i++) {
			sum += v1.get(i)*v2.get(i);
		}
		assertEquals("Error: dot product v1*v2",sum,rdot, 0.00001);
		
		rdot = v2.dot(v1);
		assertEquals("Error: dot product v2*v1",sum,rdot, 0.00001);
		
	}
	
	

}
