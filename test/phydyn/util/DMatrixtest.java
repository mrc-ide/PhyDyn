package phydyn.util;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DMatrixtest {
	private static final int BUFFER_SIZE_1 = 100;
	
	private static double[] buffer1 = null;
	
			


	@Before
	public void setUp() throws Exception {
		buffer1 = new double[BUFFER_SIZE_1];
		for(int i=0; i < BUFFER_SIZE_1; i++) {
			buffer1[i] = i;
		}
	}

	@After
	public void tearDown() throws Exception {
	}	
	
	
	@Test
	public void matrixShape() {
		DMatrix m1 = new DMatrix(5,20, buffer1);
		
		assertEquals("Error: DMatrix access ",m1.get(4, 0),4, 0.00001);
		
		assertEquals("Error: DMatrix access ",m1.get(1, 1),6, 0.00001);
		
		try {
			m1.reshape(10, 11);
			assertTrue("DMatrix: Expected reshape to fail", false);
		} catch (IllegalArgumentException e) {
			m1.reshape(10, 10);
		}
				
		assertEquals("Error: DMatrix access ",m1.get(1, 1),11, 0.00001);
		
	}
	
}
