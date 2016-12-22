package org.rcsb.structural_neighbors.clustering;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.mmtf.spark.utils.SparkUtils;
import org.rcsb.structural_neighbors.mappers.ReduceChainsToGroups;
import org.rcsb.structural_neighbors.structures.WritableCluster;
import org.rcsb.structural_neighbors.structures.WritableSegment;

import com.google.common.io.Files;

public class Clustering {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		run();
	}
	
	public static void run() throws FileNotFoundException, IOException {
		
		long start = System.nanoTime();
		
		String perc = "95";
		String bcPath = "/pdb/bc-clusters/bc-"+perc+".out";		
		List<WritableCluster> seqClusters = ReduceChainsToGroups.groupChains(bcPath);
		
        String uri = "/pdb/bc-"+perc+".report";
        File f = new File(uri);
        JavaSparkContext sc = SparkUtils.getSparkContext();

		for ( WritableCluster seqCluster : seqClusters) {
        	HierarchicalClusteringFactory hcf = new HierarchicalClusteringFactory(seqCluster.getMembers());
        	hcf.run(sc);
        	hcf.partition(1.5);
        	Map<Integer, WritableSegment> representatives = hcf.getRepresentatives();
        	Iterator<Entry<Integer, WritableSegment>> it = representatives.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Integer, WritableSegment> pair = it.next();
                Files.append(pair.getValue().getId()+";"+String.format("%d;%d", seqCluster.getId(), pair.getKey())+"\n", f, Charset.defaultCharset());
            }
		}
        sc.stop();
        
        long end = System.nanoTime();
		System.out.println("Time: " + ((end-start)/1E9)+"(sec)");
	}  
}
