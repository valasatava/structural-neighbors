package org.rcsb.clusters;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.mmtf.spark.utils.SparkUtils;

import scala.Tuple2;

public class WritableClusterProvider {
	
	public String dataPath;
	
	SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(SparkUtils.class.getSimpleName()); 
	JavaSparkContext sc = new JavaSparkContext(conf);
	
	public WritableClusterProvider (String filePath) {
		this.dataPath = filePath;
	}
	
	private JavaPairRDD<String, WritableCluster> readClusters() {
		
		JavaPairRDD<String, WritableCluster> clusters = sc
	    		.sequenceFile(dataPath, Text.class, WritableCluster.class)
	    		.mapToPair(t -> new Tuple2<String, WritableCluster> (new String(t._1.toString()), t._2) );
		return clusters;
	}
	
	public List<WritableCluster> getClusters() {
		
		List<WritableCluster> clusters = readClusters()
				.collect()
				.stream()
				.map(t -> t._2)
				.collect(Collectors.toList());
		sc.close();
		
		return clusters;
	}
}
