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
    	String perc = "100"; 
    	String bcPath = "/pdb/bc-"+perc+".out.TEST";
    	List<Cluster> clusters = new ReadBlastClusters().read(bcPath);
    	
    	// Clusters of chains
    	String outPath = "/pdb/x-rayChains_bc-"+perc+"-clusters.seq";
    	WritableSegmentProvider provider = new WritableSegmentProvider("/pdb/x-rayChains.seq");
    	provider.reduceToBlastClusters(clusters, outPath);
    	
    	long end = System.nanoTime();
		System.out.println("Time: " + (end-start)/1E9);
    }
}
