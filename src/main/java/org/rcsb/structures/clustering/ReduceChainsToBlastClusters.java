package org.rcsb.structures.clustering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.rcsb.clusters.Cluster;
import org.rcsb.sequences.ReadBlastClusters;
import org.rcsb.structures.utils.WritableSegmentProvider;

/**
 *
 */
public class ReduceChainsToBlastClusters {
	
    public static void main( String[] args ) throws FileNotFoundException, IOException {
    	
    	long start = System.nanoTime();
    	
    	// Blast clusters sequence identity
    	String perc = "95"; 
    	//String bcPath = "/pdb/bc-clusters/bc-"+perc+".out";
    	String bcPath = "/pdb/bc-95.out.TEST";
    	
    	List<Cluster> clusters = new ReadBlastClusters().read(bcPath);
    	
    	// Clusters of chains
    	String outPath = "/pdb/x-rayChains_bc-"+perc+".seq";
    	WritableSegmentProvider provider = new WritableSegmentProvider("/pdb/x-rayChains.seq");
    	provider.reduceToBlastClusters(clusters, outPath);
    	
    	long end = System.nanoTime();
		System.out.println("Time: " + (end-start)/1E9);
    }
}
