package phydyn.util;

import org.jblas.DoubleMatrix;

/**
 * Double vector implementation. Double vectors are column vectors, dim-by-1 matrices.
 * DVectors are stored in double arrays (data). 
 * They can start at any position of the data buffer, provided that the whole length 
 * of the vector can be stored by the host buffer.
 * @author Igor Siveroni
 *
 */

public class DVector {
	public double[] data;
	public int start, length;

	public DVector() {
		// TODO Auto-generated constructor stub
	}
	
	public DVector(int length) {
		this.length = length;
		data = new double[length];
		start=0;
	}
	
	public DVector(int length, double[] data) {
		this.start=0;
		if (length<0)
			throw new IllegalArgumentException("DVector constructor: negative length");
		if (length>data.length)
			throw new IllegalArgumentException("DVector constructor: wrong length argument");
		this.length = length;
		this.data = data;
	}
	
	public DVector(int length, double[] data, int start) {
		if ((length<0)||(start<0))
			throw new IllegalArgumentException("DVector: negative start/length");
		if ((start+length)>data.length)
			throw new IllegalArgumentException("DVector: wrong start/length arguments");
		this.length = length;
		this.data = data;
		this.start=start;
	}
	
	public DVector(DVector V) {
		this(V.length);
		System.arraycopy(V.data, V.start, this.data, this.start, V.length);
	}
	
	public String toString() {
		String r = "[ ";
		for(int idx =start; idx < (start+length); idx++)
			r += data[idx]+" ";
		return r+"]";
	}
	
	public DVector copy(DVector rhs) {
		if (this==rhs) return this;
		if (this.length!=rhs.length)
			throw new IllegalArgumentException("DVector.copy: different lengths");
		System.arraycopy(rhs.data, rhs.start, this.data, this.start, length);
		return this;
	}
	
	public void put(int pos, double v) {
		if (pos>=length) 
			throw new IllegalArgumentException("DVector: index out of bounds");
		data[start+pos] = v;
	}
	
	public double get(int pos) {
		if (pos>=length)
			throw new IllegalArgumentException("DVector: index out of bounds");
		return data[start+pos];
	}
	
	public DVector zero() {
		// is there a system version of this? ie how to zero a buffer
		for(int i=start; i < start+length; i++)
			data[i] = 0;
		return this;
	}
	
	public double dot(DVector V) {
		if (this.length!=V.length)
			throw new IllegalArgumentException("DVector.dot: wrong vector lengths");
		double sum=0;
		for(int idx =start, idx2=V.start; idx < (start+length); idx++,idx2++)
			sum += this.data[idx] * V.data[idx2];		
		return sum;
	}
	
	
	public DVector add(double v) {
		DVector result = new DVector(length);
		result.addi(v);
		return result;
	}
	
	public DVector addi(double v) {
		for(int idx =start; idx < (start+length); idx++)
			data[idx] += v;
		return this;
	}
	

	
	public DVector muli(double v) {
		for(int idx =start; idx < (start+length); idx++)
			data[idx] *= v;
		return this;
	}	
	
	public DVector subi(double v) {
		for(int idx =start; idx < (start+length); idx++)
			data[idx] -= v;
		return this;
	}	
	
	/* subtract scalar from vector */ 
	public DVector rsubi(double v) {
		for(int idx =start; idx < (start+length); idx++)
			data[idx] = v - data[idx];
		return this;
	}	
	
	public DVector maxi(double v) {
		for(int idx =start; idx < (start+length); idx++)
			if (v>data[idx]) data[idx]=v;
		return this;
	}
	
	public DVector mini(double v) {
		for(int idx =start; idx < (start+length); idx++)
			if (v<data[idx]) data[idx]=v;
		return this;
	}
	
	public DVector divi(double v) {
		for(int idx =start; idx < (start+length); idx++)
			data[idx] /= v;
		return this;
	}
	
	public double sum() {
		double sum=0;
		for(int idx =start; idx < (start+length); idx++)
			sum += data[idx];
		return sum;
	}
	
	public DVector addi(DVector dv) {
		if (this.length!=dv.length)
			throw new IllegalArgumentException("DVector.addi: wrong vector lengths");
		for(int idx =start, idx2=dv.start; idx < (start+length); idx++,idx2++)
			data[idx] += dv.data[idx2];		
		return this;
	}
	
	public DVector muli(DVector dv) {
		if (this.length!=dv.length)
			throw new IllegalArgumentException("DVector.muli: wrong vector lengths");
		for(int idx =start, idx2=dv.start; idx < (start+length); idx++,idx2++)
			data[idx] *= dv.data[idx2];		
		return this;
	}
	
	
	
	// result = this-other
	public DVector subi(DVector other, DVector result) {
		for(int idx =start, idxother=other.start, ridx=result.start; 
				idx < (start+length); idx++,idxother++,ridx++)
			result.data[ridx] = this.data[idx] - other.data[idxother];		
		return this;
	}
	
	public DVector subi(DVector V) {
		for(int idx =start, vidx=V.start; idx < (start+length); idx++,vidx++)
			this.data[idx] -=  V.data[vidx];		
		return this;
	}
	
	public DVector mul(DVector V) {
		DVector R = new DVector(this.length);
		for(int idx =start, idx2=V.start, ridx=0; idx < (start+length); idx++,idx2++, ridx++)
			R.data[ridx] = this.data[idx] * V.data[idx2];		
		return R;
	}
	
	public DVector muli(DVector V, DVector R ) {
		for(int idx =start, idx2=V.start, ridx=R.start; idx < (start+length); idx++,idx2++, ridx++)
			R.data[ridx] = this.data[idx] * V.data[idx2];		
		return this;
	}
	
	public DVector div(DVector dv) {
		DVector r = new DVector(this.length);
		for(int idx =start, idx2=dv.start, ridx=0; idx < (start+length); idx++,idx2++, ridx++)
			r.data[ridx] = this.data[idx] / dv.data[idx2];		
		return r;
	}
	
	public DVector divi(DVector V, DVector R ) {
		for(int idx =start, idx2=V.start, ridx=R.start; idx < (start+length); idx++,idx2++, ridx++)
			R.data[ridx] = this.data[idx] / V.data[idx2];		
		return this;
	}
	
	public DVector sub(DVector dv) {
		DVector r = new DVector(this.length);
		for(int idx =start, idx2=dv.start, ridx=0; idx < (start+length); idx++,idx2++, ridx++)
			r.data[ridx] = this.data[idx] - dv.data[idx2];		
		return r;
	}
	
	public DVector squarei(DVector R) {
		int idx = this.start;
		int ridx = R.start;
		for(int i=0; i < this.length; i++,ridx++,idx++)
			R.data[ridx] = this.data[idx]*this.data[idx];
		return this;
	}
	
	public DVector squarei() {
		int idx = this.start;
		for(int i=0; i < this.length; i++,idx++)
			this.data[idx] *= this.data[idx];
		return this;
	}	
		
	// right matrix multiplication: M x vector = vector
	/*
	public DVector rmul(DoubleMatrix m) {
		if (m.rows != this.length)
			throw new IllegalArgumentException("DVector rmmul: Incorrect matrix dimensions");
		DVector r = new DVector(this.length);
		double[] matrix = m.data;
		int midx=0, idxthis = this.start, ridx;
		for(int col=0; col < m.columns; col++) {
			ridx=0;
			for(int row=0; row < m.rows; row++) {
				r.data[ridx] += matrix[midx] * this.data[idxthis];
				ridx++; midx++;
			}
			idxthis++;
		}
		return r;
	}
	*/
	
	// M x this
	public DVector rmul(DMatrix M) {
		if (M.columns != this.length)  // was (M.rows != this.length)
			throw new IllegalArgumentException("DVector: wrong matrix dimensions");
		DVector r = new DVector(M.rows);  // new DVector(this.length);
		double[] matrix = M.data;
		int midx=M.start, idxthis = this.start, ridx;
		for(int col=0; col < M.columns; col++) {
			ridx=0;
			for(int row=0; row < M.rows; row++) {
				r.data[ridx] += matrix[midx] * this.data[idxthis];
				ridx++; midx++;
			}
			idxthis++;
		}
		return r;
	}
	
	/*
	public DVector lmul(DoubleMatrix M) {
		if (this.length!=M.rows)
			throw new IllegalArgumentException("Wrong vector/matrix dimensions");
		DVector R = new DVector(M.columns);
		double sum;
		int midx, idx;
		midx=0;
		for(int col=0; col<M.columns; col++) {
			sum=0;
			idx = this.start;
			for(int row=0; row < M.rows; row++) {
				sum += data[idx] * M.data[midx];
				midx++;
			}
			R.data[col]=sum;
		}
		return R;
	}
	*/
	
	// Assumes row-vector	
	// this x M
	public DVector lmul(DMatrix M) {
		if (this.length!=M.rows)
			throw new IllegalArgumentException("DVector: Wrong vector/matrix dimensions");
		DVector R = new DVector(M.columns);
		double sum;
		int midx, idx;
		midx=M.start;
		for(int col=0; col<M.columns; col++) {
			sum=0;
			idx = this.start;
			for(int row=0; row < M.rows; row++) {
				sum += data[idx] * M.data[midx];
				midx++;
			}
			R.data[col]=sum;
		}
		return R;
	}
	
	
	public int copyToArray(double[] buffer, int pos) {
		int bidx = pos;
		for(int idx =start; idx < (start+length); idx++, bidx++)
			buffer[bidx] = data[idx];
		return bidx;
	}

	public int copyFromArray(double[] buffer, int pos) {
		int bidx = pos;
		for(int idx =start; idx < (start+length); idx++, bidx++)
			data[idx] = buffer[bidx];
		return bidx;
	}

	
	
}
