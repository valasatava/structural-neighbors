package org.rcsb.structures.sandbox;

import org.apache.spark.api.java.JavaPairRDD;
import org.rcsb.mmtf.api.StructureDataInterface;
import org.rcsb.mmtf.spark.data.StructureDataRDD;

public class Test {
	
	public static void main(String[] args) {
		
		String dataPath = "/pdb/t/reduced";
		JavaPairRDD<String, StructureDataInterface> data = new StructureDataRDD(dataPath).getJavaRdd();
		System.out.println(data.count());
	}
}
