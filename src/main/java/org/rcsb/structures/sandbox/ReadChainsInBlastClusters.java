package org.rcsb.structures.sandbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.rcsb.clusters.Cluster;
import org.rcsb.sequences.ReadBlastClusters;

import com.google.common.io.Files;

/**
 *
 */
public class ReadChainsInBlastClusters {
	
    public static void main( String[] args ) throws FileNotFoundException, IOException {
    	
    	long start = System.nanoTime();
    	
    	// Blast clusters sequence identity
    	String perc = "95"; 
    	String bcPath = "/pdb/bc-clusters/bc-"+perc+".out";

    	List<Cluster> clusters = new ReadBlastClusters().read(bcPath);
    	
    	File f = new File("/pdb/bc-"+perc+".stats");
    	for (Cluster cluster : clusters) {
    		Files.append(Integer.toString(cluster.getId())+","+Integer.toString(cluster.getSize())+"\n", f, Charset.defaultCharset());
		}
    	
    	long end = System.nanoTime();
		System.out.println("Time: " + (end-start)/1E9);
    }
}
