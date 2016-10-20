package org.rcsb.structures.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.broadcast.Broadcast;
import org.rcsb.clusters.Cluster;
import org.rcsb.clusters.MemberId;
import org.rcsb.mappers.GroupSegmentsInClusters;

import scala.Tuple2;

/**
 * This class passes the data from Hadoop sequence file 
 * to a class holding protein chains (as WritableSegments).
 * 
 * @author Yana Valasatava
 *
 */
public class WritableSegmentProvider extends WritableProvider {
	
	private String dataPath;
	JavaPairRDD<String, WritableSegment> segments;
	
	public WritableSegmentProvider (String path) {
		super();
		this.dataPath = path;
	}
	
	public void readSegments() {
		
		if (sc == null)
			startSpark();
		segments = sc
	    		.sequenceFile(dataPath, Text.class, WritableSegment.class)
	    		.mapToPair(t -> new Tuple2<String, WritableSegment> (new String(t._1.toString()), new WritableSegment(t._2)) )
	    		.cache();
	}
	
	public void reduceToBlastClusters(List<Cluster> clusters, String outPath) {
		
		startSpark();
		
		// read and broadcast segment data to all nodes
		readSegments();
		List<Tuple2<String, WritableSegment>> s = segments.collect();
	    final Broadcast<List<Tuple2<String,WritableSegment>>> data = sc.broadcast(s);
	    
	    // send  
	    sc.parallelize(clusters).repartition(8)
	    		.map(new GroupSegmentsInClusters(data))
	    		.filter(t -> t != null)
	    		.mapToPair(t -> new Tuple2<Text, WritableCluster>(new Text(Integer.toString(t.getId())), t))
	    		.saveAsHadoopFile(outPath, Text.class, WritableCluster.class, SequenceFileOutputFormat.class);
    	
	    stopSpark();
	}
		
	public List<String> getSegmentIds() {
		
		if (sc == null) {
			readSegments();
		}
		List<String> segmentIds = segments.keys().collect();
		return segmentIds;
	}
	
	public List<Tuple2<String, WritableSegment>> getSegments() {
		
		if (sc==null) {
			startSpark();
			readSegments();
		}
		
		List<Tuple2<String, WritableSegment>> s = segments.collect();
		sc.close();
		return s;
	}
	
	public WritableSegment getSegmentById(String id) {
		
		if (sc==null) {
			startSpark();
			readSegments();
		}
		List<Tuple2<String, WritableSegment>> segment = segments
				.filter(t->t._1.equals(id))
				.collect();
		
		if (segment.size() != 1)
			return null;
		else
			return segment.get(0)._2;
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
