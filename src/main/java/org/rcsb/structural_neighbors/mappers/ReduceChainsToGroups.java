package org.rcsb.structural_neighbors.mappers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.structural_neighbors.io.ClustersReader;
import org.rcsb.structural_neighbors.io.WritableClusterProvider;
import org.rcsb.structural_neighbors.io.WritableSegmentProvider;
import org.rcsb.structural_neighbors.structures.Group;
import org.rcsb.structural_neighbors.structures.MemberId;
import org.rcsb.structural_neighbors.structures.WritableCluster;
import org.rcsb.structural_neighbors.structures.WritableSegment;
import org.rcsb.structural_neighbors.utils.Convertors;

/**
 *
 */
public class ReduceChainsToGroups {
	
    public static void main( String[] args ) throws FileNotFoundException, IOException {
    	
    	long start = System.nanoTime();
    	
    	// Blast clusters sequence identity
    	String perc = "95"; 
    	String bcPath = "/pdb/bc-clusters/bc-"+perc+".out";

    	List<WritableCluster> clusters = groupChains(bcPath);
    	System.out.println("Writing out clusters...");
		String outPath = "/pdb/bc-"+perc+".seq";
    	WritableClusterProvider.writeToHadoop(outPath, clusters);
		
    	long end = System.nanoTime();
		System.out.println("Time: " + (end-start)/1E9);
    }
    
    public static List<WritableCluster> groupChains(String bcPath) throws FileNotFoundException, IOException {
		
    	SparkConf conf = new SparkConf()
    			.setMaster("local[*]")
    			.set("spark.driver.maxResultSize", "8g")
    			.setAppName("WritableSegmentProvider");
    	JavaSparkContext sc = new JavaSparkContext(conf);
    	
    	String dataPath = "/pdb/x-rayChains.seq";
		WritableSegment[] data = WritableSegmentProvider.getFromHadoop(sc, dataPath);
		Map<String, WritableSegment> proteins = Convertors.arrayToMap(data);
		
		int cluserId = 0;
		List<WritableCluster> clusters = new ArrayList<WritableCluster>();
		
    	List<Group> groups = new ClustersReader().readBlastClusters(bcPath);
    	for (Group group : groups) {
    		List<WritableSegment> segments = new ArrayList<WritableSegment>();
			Set<MemberId> members = group.getMembers();
			for (MemberId memberId : members) {
				String key = memberId.getPdbCode()+"."+memberId.getChainId();
				if (proteins.containsKey(key)) {
					segments.add(proteins.get(key));
				}
			}
			if (segments.size() != 0 ) {
				WritableSegment[] cluster = segments.toArray(new WritableSegment[segments.size()]);
				clusters.add(new WritableCluster(cluserId, cluster));
				cluserId++;
			}
		}
		return clusters;
	}
}
