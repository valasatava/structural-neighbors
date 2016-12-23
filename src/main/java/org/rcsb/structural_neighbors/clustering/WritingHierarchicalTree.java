package org.rcsb.structural_neighbors.clustering;

import java.io.IOException;
import java.util.List;

import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.spark.utils.SparkUtils;
import org.rcsb.structural_neighbors.io.MmtfStructuresProvider;
import org.rcsb.structural_neighbors.io.WritableSegmentProvider;
import org.rcsb.structural_neighbors.structures.WritableSegment;

public class WritingHierarchicalTree {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		long start = System.nanoTime();

		writeTree();
		
		long end = System.nanoTime();
		System.out.println("Time: " + ((end-start)/1E9)+"(sec)");
	}
	
	public static void writeTree() throws ClassNotFoundException, IOException {
		
		//String name = "cdk_9";
		//String name = "tpk_btk_95";
		String name = "thkinase";
		
		// Get MMTF structures
		MmtfStructuresProvider mp = new MmtfStructuresProvider("http://mmtf.rcsb.org/v1.0/reduced/");
		List<MmtfStructure> structures = mp.getListedInFile("/pdb/kinases_clustering/data/"+name+".csv");
		
		String moleculeName = "kinase";
		WritableSegment[] segments = WritableSegmentProvider.getMoleculesFromMmtfStructures(structures, moleculeName);
		
		HierarchicalClusteringFactory cp = new HierarchicalClusteringFactory(segments);
		cp.run(SparkUtils.getSparkContext());
		cp.writeTree("/pdb/kinases_clustering/"+name);
	}
}
