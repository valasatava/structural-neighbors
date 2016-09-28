package org.rcsb.structures.utils;

import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
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
	
	public List<Tuple2<String, WritableSegment>> readSegments(String filePath) {
		
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(SparkUtils.class.getSimpleName()); 
		JavaSparkContext sc = new JavaSparkContext(conf);
		
	   List<Tuple2<String, WritableSegment>> segments = sc
	    		.sequenceFile(filePath, Text.class, WritableSegment.class)
	    		.mapToPair(t -> new Tuple2<String, WritableSegment> (new String(t._1.toString()), t._2) )
	    		.collect();
		sc.close();
		
		return segments;
	}
}
