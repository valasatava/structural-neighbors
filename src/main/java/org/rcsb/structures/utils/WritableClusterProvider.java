package org.rcsb.structures.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.hadoop.io.Text;
import org.apache.spark.api.java.JavaPairRDD;

import scala.Tuple2;

public class WritableClusterProvider extends WritableProvider {

	public String dataPath;
	
	SparkConf conf = new SparkConf()
			.setMaster("local[*]")
			.set("spark.driver.maxResultSize", "8g")
			.setAppName(SparkUtils.class.getSimpleName());
	JavaSparkContext sc = new JavaSparkContext(conf);

	public WritableClusterProvider (String filePath) {
		this.dataPath = filePath;
	}

	private JavaPairRDD<String, WritableCluster> readClusters() {

		if (sc==null)
			startSpark();
		JavaPairRDD<String, WritableCluster> clusters = sc
	    		.sequenceFile(dataPath, Text.class, WritableCluster.class)
	    		.mapToPair(t -> new Tuple2<String, WritableCluster> (new String(t._1.toString()),
	    				new WritableCluster(t._2)) );
		return clusters;
	}

	public List<WritableCluster> getClusters() {
		List<WritableCluster> clusters = readClusters()
				.collect()
				.stream()
				.map(t -> t._2)
				.collect(Collectors.toList());
		return clusters;
	}

	public WritableCluster getClusterById(int id) {

		List<WritableCluster> clusters = getClusters();
		for (WritableCluster cluster : clusters) {
			if (cluster.getId()==id) {
				return cluster;
			}
		}
		return null;
	}
}
