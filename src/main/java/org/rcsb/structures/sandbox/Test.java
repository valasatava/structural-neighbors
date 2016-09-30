package org.rcsb.structures.sandbox;

import java.util.List;

import org.rcsb.clusters.WritableCluster;
import org.rcsb.clusters.WritableClusterProvider;
import org.rcsb.structures.utils.WritableSegment;



public class Test {
	
	public static void main(String[] args) {
		test_clusters();
	}
	
	public static void test_clusters() {	
		
		String dataPath = "/pdb/x-rayChains_bc-95.seq";
		List<WritableCluster> clusters = new WritableClusterProvider(dataPath).getClusters();
		System.out.println(clusters.size());
		
//		for (WritableCluster cluster : clusters) {
//			System.out.println(cluster.getId());
//			for (WritableSegment segment : cluster.getMembers()) {
//				System.out.println(segment.getId());
//			}
//		}
	}
}
