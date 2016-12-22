package org.rcsb.structural_neighbors.mappers;

import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.rcsb.structural_neighbors.methods.WritableSegmentSuperposer;
import org.rcsb.structural_neighbors.structures.SimilarityMatrix;
import org.rcsb.structural_neighbors.structures.WritableSegment;

import scala.Tuple2;

public class SetAlignmentScores implements VoidFunction<Tuple2<Integer,Integer>>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1009358932688571977L;
	private Broadcast<SimilarityMatrix> mData = null;
	private Broadcast<WritableSegment[]> sData = null;
	
	public SetAlignmentScores(Broadcast<SimilarityMatrix> mData, Broadcast<WritableSegment[]> sData) {
		this.mData = mData;
		this.sData = sData;
	}

	@Override
	public void call(Tuple2<Integer, Integer> pair) throws Exception {
		
		SimilarityMatrix matrix = mData.getValue();
		
		WritableSegment segment1 = this.sData.getValue()[pair._1];
		WritableSegment segment2 = this.sData.getValue()[pair._2];
		 
		WritableSegmentSuperposer sup = new WritableSegmentSuperposer(segment1, segment2);
		float[] scores = sup.run();
		if (Double.isNaN(scores[0])) {
			scores[0] = 999.9f;
		}
		matrix.setRMSD(scores[0], pair._1, pair._2);
		matrix.setTMScore(scores[1], pair._1, pair._2);
	}
}
