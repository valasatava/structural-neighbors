package org.rcsb.structures.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.clusters.MemberId;
import org.rcsb.mmtf.spark.utils.SparkUtils;

import scala.Tuple2;

/**
 * This class passes the data from Hadoop sequence file 
 * to a class holding protein chains (as WritableSegments).
 * 
 * @author Yana Valasatava
 *
 */
public class WritableSegmentProvider {
	
	SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(SparkUtils.class.getSimpleName()); 
	JavaSparkContext sc = new JavaSparkContext(conf);
	
	private JavaPairRDD<String, WritableSegment> readSegments(String filePath) {
		
		JavaPairRDD<String, WritableSegment> segments = sc
	    		.sequenceFile(filePath, Text.class, WritableSegment.class)
	    		.mapToPair(t -> new Tuple2<String, WritableSegment> (new String(t._1.toString()), t._2) );
		return segments;
	}
	
	public List<Tuple2<String, WritableSegment>> foo(String filePath) {

		List<Tuple2<String, WritableSegment>> segments = readSegments(filePath).collect();
		sc.close();
		return segments;
	}
	
	public List<String> getSegmentIds(String filePath) {
		
		JavaPairRDD<String, WritableSegment> segments = readSegments(filePath);
		List<String> segmentIds = segments.keys().collect();
		sc.close();
		return segmentIds;
	}
	
	public Set<MemberId> getMemberIds(String filePath) {
		
		List<String> segmentIds = this.getSegmentIds(filePath);
		
		Set<MemberId> memberIds = new HashSet<>();
		for (String segmentId : segmentIds) {
			MemberId m = MemberId.create(segmentId, ".");
			memberIds.add(m);
		}
		return memberIds;
	}
}
