package org.rcsb.structures.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.mmtf.spark.utils.SparkUtils;

public class WritableProvider {
	static JavaSparkContext sc;
	
	public void startSpark() {
		SparkConf conf = new SparkConf()
				.setMaster("local[*]")
				.setAppName(SparkUtils.class.getSimpleName())
				.set("spark.driver.maxResultSize", "2g");
		sc = new JavaSparkContext(conf);
	}
	
	public void stopSpark() {
		sc.close();
	}
}
