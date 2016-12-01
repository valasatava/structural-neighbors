package org.rcsb.structures.sandbox;

import java.util.List;

import org.rcsb.sequences.WritableSegmentSuperposer;
import org.rcsb.structures.utils.WritableCluster;
import org.rcsb.structures.utils.WritableClusterProvider;
import org.rcsb.structures.utils.WritableSegment;
import org.rcsb.structures.utils.WritableSegmentProvider;

public class Test {
	
	public static void main(String[] args) {
		//testClusters();
		testSegmentSuperposition();
	}
	
	public static void testChains() {
		
		String dataPath = "/pdb/x-rayChains.seq";
		String pdbCode = "4WQU.BB";
		
		WritableSegmentProvider provider = new WritableSegmentProvider(dataPath);
		provider.readSegments();
		
		WritableSegment segment = provider.getSegmentById(pdbCode);

		if (segment==null)
			System.out.println("Not found");
		else {
			System.out.println(segment.getId());
			System.out.println(segment.getSequence());
		}
	}
	
	public static void testClusters() {	
		
		String dataPath = "/pdb/x-rayChains_bc-95.seq.TEST";
		List<WritableCluster> clusters = new WritableClusterProvider(dataPath).getClusters();
		System.out.println(clusters.size());
		
		for (WritableCluster cluster : clusters) {
			System.out.println(cluster.getId());
			for (WritableSegment segment : cluster.getMembers()) {
				System.out.println(segment.getId());
				System.out.println(segment.getSequence());
			}
		}
	}

	public static void testSegmentSuperposition() {
		
		String dataPath = "/pdb/x-rayChains.seq";
		WritableSegmentProvider provider = new WritableSegmentProvider(dataPath);
		provider.readSegments();
		
		//String pdbCode1 = "5J4B.1p";
		//String pdbCode2 = "5FDU.1p";
		
		String pdbCode1 = "2OOK.A";
		String pdbCode2 = "2Q3L.A";

		WritableSegment segment1 = provider.getSegmentById(pdbCode1);
		WritableSegment segment2 = provider.getSegmentById(pdbCode2);
		
		provider.stopSpark();
		
		WritableSegmentSuperposer superposer = new WritableSegmentSuperposer(segment1, segment2);
		superposer.run();
		
	}
}
