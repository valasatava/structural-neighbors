package org.rcsb.structural_neighbors.io;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.structural_neighbors.structures.WritableCluster;

import scala.Tuple2;

public class WritableClusterProvider {
	
	static SparkConf conf = new SparkConf()
			.setMaster("local[*]")
			.set("spark.driver.maxResultSize", "8g")
			.setAppName("WritableClusterProvider");
	static JavaSparkContext sc = new JavaSparkContext(conf);
	
	public static void writeToHadoop(String outPath, List<WritableCluster> clusters) {
		sc.parallelize(clusters)
		.mapToPair(t -> new Tuple2<Text, WritableCluster>(new Text(String.valueOf(t.getId())), t)) // convert to Text key value
		.saveAsHadoopFile(outPath, Text.class, WritableCluster.class, SequenceFileOutputFormat.class);
	}
	
	public static List<WritableCluster> getFromHadoop(String path) {
		
		List<Tuple2<String, WritableCluster>> data = sc.sequenceFile(path, Text.class, WritableCluster.class)
		.mapToPair(t -> new Tuple2<String, WritableCluster> (new String(t._1.toString()), 
															 new WritableCluster(t._2)) )
		.collect();
		sc.stop();
		return data.stream().map(t->t._2).collect(Collectors.toList());
	}
}
