package org.rcsb.structures.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.rcsb.clusters.Cluster;
import org.rcsb.clusters.MemberId;
import org.rcsb.clusters.WritableCluster;
import org.rcsb.mappers.GroupSegmentsInClusters;
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
	
	public String dataPath;
	
	SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(SparkUtils.class.getSimpleName()); 
	JavaSparkContext sc = new JavaSparkContext(conf);
	
	public WritableSegmentProvider (String filePath) {
		this.dataPath = filePath;
	}

	private JavaPairRDD<String, WritableSegment> readSegments() {
		
		JavaPairRDD<String, WritableSegment> segments = sc
	    		.sequenceFile(dataPath, Text.class, WritableSegment.class)
	    		.mapToPair(t -> new Tuple2<String, WritableSegment> (new String(t._1.toString()), t._2) );
		return segments;
	}
	
	public void reduceToBlastClusters(List<Cluster> clusters, String outPath) {
		
		// read and broadcast segment data to all nodes
		List<Tuple2<String, WritableSegment>> segments = readSegments().collect();
	    final Broadcast<List<Tuple2<String,WritableSegment>>> data = sc.broadcast(segments);
	    
	    // send  
	    sc.parallelize(clusters).repartition(8)
	    		.map(new GroupSegmentsInClusters(data))
	    		.mapToPair(t -> new Tuple2<Text, WritableCluster>(new Text(Integer.toString(t.getId())), t))
	    		.saveAsHadoopFile(outPath, Text.class, WritableCluster.class, SequenceFileOutputFormat.class);
    	
		sc.close();
	}
		
	public List<String> getSegmentIds() {
		
		JavaPairRDD<String, WritableSegment> segments = readSegments();
		List<String> segmentIds = segments.keys().collect();
		sc.close();
		return segmentIds;
	}
	
	public Set<MemberId> getMemberIds() {
		
		List<String> segmentIds = this.getSegmentIds();
		
		Set<MemberId> memberIds = new HashSet<>();
		for (String segmentId : segmentIds) {
			MemberId m = MemberId.create(segmentId, ".");
			memberIds.add(m);
		}
		return memberIds;
	}
}
