package org.rcsb.structural_neighbors.methods;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import org.biojava.nbio.structure.symmetry.geometry.SuperPosition;
import org.rcsb.structural_neighbors.structures.WritableSegment;

import scala.Tuple2;

public class WritableSegmentSuperposer {
	
	private WritableSegment firstSegment;
	private WritableSegment secondSegment;
	
	public WritableSegmentSuperposer (WritableSegment firstSegment, WritableSegment secondSegment) {
		this.firstSegment = firstSegment;
		this.secondSegment = secondSegment;
	}
	
	public WritableSegment getFirstSegment() {
		return firstSegment;
	}

	public void setFirstSegment(WritableSegment segment) {
		this.firstSegment = segment;
	}

	public WritableSegment getSecondSegment() {
		return secondSegment;
	}

	public void setSecondSegment(WritableSegment segment) {
		this.secondSegment = segment;
	}
	
	public int[][] alignSequences() {
		
		LongestCommonSubsequence lcs = new LongestCommonSubsequence(this.firstSegment.getSequence(), 
																	this.secondSegment.getSequence());
		String common = lcs.find();

		int[][] inds = new int[common.length()][2];

		int j = 0; int k = 0;
		int ind1 = 0; int ind2 = 0;
		for (int i = 0; i < common.length(); i++){

			char aa = common.charAt(i);

			while ( lcs.getFirstString()[j] != aa ) { 
				j++; ind1=j; 
			}
			while ( lcs.getSecondString()[k] != aa ) { 
				k++; ind2=k;
			}

			inds[i][0] = ind1; inds[i][1] = ind2;
			ind1++; ind2++;
		}
		return inds;
	}
		
	public int[][] refineSequenceAlignment(int[][] alignment) {
		
		List<List<Integer>> fin1 = new ArrayList<List<Integer>>();
		List<List<Integer>> fin2 = new ArrayList<List<Integer>>();
		
		List<Integer> temp1 = new ArrayList<Integer>();
		List<Integer> temp2 = new ArrayList<Integer>();
		
		temp1.add(alignment[0][0]);
		temp2.add(alignment[0][1]);
		
		int n=0;
		int i=1;
		while ( i < alignment.length ) {
			
			if ( ((alignment[i][0] - alignment[i-1][0])==1) && ((alignment[i][1] - alignment[i-1][1])==1) ) {
				temp1.add(alignment[i][0]);
				temp2.add(alignment[i][1]);
			}
			else {
				if ( temp1.size() > 2 ) {
					fin1.add(new ArrayList<Integer>(temp1));
					fin2.add(new ArrayList<Integer>(temp2));
					n+=temp1.size();
				}
				
				temp1.clear();
				temp2.clear();
				
				temp1.add(alignment[i][0]);
				temp2.add(alignment[i][1]);
			}
			i++;
		}
		
		if ( temp1.size() > 2 ) {
			fin1.add(new ArrayList<Integer>(temp1));
			fin2.add(new ArrayList<Integer>(temp2));
			n+=temp1.size();
		}
		
		int[][] refinedAlignment = new int[n][2];
		
		int k = 0;
		for ( int j=0; j < fin1.size(); j++ ) {
			temp1=fin1.get(j);
			temp2=fin2.get(j);
			for ( int l=0; l < temp1.size(); l++ ) {
				refinedAlignment[k][0]=temp1.get(l);
				refinedAlignment[k][1]=temp2.get(l);
				k++;
			}
		}
		
		return refinedAlignment;
	}
	
	public Tuple2<Point3d[], Point3d[]> getAlignedCoordinates(int[][] alignment) {
		
		List<Point3d> temp1 = new ArrayList<Point3d>();
		List<Point3d> temp2 = new ArrayList<Point3d>();
		for ( int i=0; i < alignment.length; i++ ) {
			if ( (this.firstSegment.getCoordinates()[alignment[i][0]] == null) || ( this.secondSegment.getCoordinates()[alignment[i][1]] == null) )
				continue;
			else {
				temp1.add(this.firstSegment.getCoordinates()[alignment[i][0]]);
				temp2.add(this.secondSegment.getCoordinates()[alignment[i][1]]);
			}
		}
		
		Tuple2<Point3d[], Point3d[]> pairs = new Tuple2<Point3d[], Point3d[]>(new Point3d[temp1.size()], new Point3d[temp1.size()]);
		for (int j=0; j < temp1.size(); j++) {
			pairs._1[j] = temp1.get(j);
			pairs._2[j] = temp2.get(j);
		}
		return pairs;	
	} 
	
	public Point3d[] superposeCoordinates(Point3d[] v1, Point3d[] v2) {
		
		Superposition lsf = new LeastSquaresFitting();
		lsf.run(v1, v2);
		return lsf.getSuperposedCoordanates();
	}
	
	public Matrix4d getTransformation(Point3d[] v1, Point3d[] v2) {
		
		Superposition lsf = new LeastSquaresFitting();
		lsf.run(v1, v2);
		
		return lsf.getTransformationMatrix();
		
	}
	
	public Point3d[] applyTransformation(Matrix4d tm, Point3d[] v) {
		Point3d[] clone =  SuperPosition.clonePoint3dArray(v);
		SuperPosition.transform(tm, clone);
		return clone;
	}
	
	public float[] run() {
		
		int[][] alignedInds = alignSequences();
		int[][] alignment = refineSequenceAlignment(alignedInds);
		
		Tuple2<Point3d[], Point3d[]> coordinates = getAlignedCoordinates(alignment);
		Point3d[] superposed = superposeCoordinates( coordinates._1, coordinates._2 );
		
		float rmsd = (float) SuperPosition.rmsd(coordinates._2, superposed);
		int lengthNative = Math.min(this.firstSegment.getSequence().length(), this.secondSegment.getSequence().length());
		float tmScore = (float) SuperPosition.TMScore(coordinates._2, superposed, lengthNative);
		
		float[] scores = {rmsd, tmScore};
		return scores;
	}
}