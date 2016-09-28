package org.rcsb.structures.clustering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rcsb.clusters.Cluster;
import org.rcsb.clusters.GroupsToStructuresBridge;
import org.rcsb.clusters.MemberId;
import org.rcsb.sequences.ReadBlastClusters;
import org.rcsb.structures.utils.WritableSegmentProvider;


/**
 *
 */
public class AssignToBlastClusters {
	
    public static void main( String[] args ) throws FileNotFoundException, IOException {
    	
    	// Chains
    	String filePath = "/pdb/x-rayChains.seq";
    	WritableSegmentProvider provider = new WritableSegmentProvider();
    	Set<MemberId> segmentIds = provider.getMemberIds(filePath);
    	
    	// Blast clusters
    	List<Cluster> clusters = new ReadBlastClusters().read("/pdb/bc-100.out");
    	
    	
    	// Segments clusters
    	List<Cluster> clusteredSegments = new ArrayList<>();
    	
    	int j = 0;
    	for (Cluster cluster : clusters) {
    		
    		Set<MemberId> s = cluster.retainAll(segmentIds);
    		if (s.isEmpty())
    			continue;
    		
    		GroupsToStructuresBridge.printSet(s);
    		
    		Cluster c = new Cluster(j++);
    		c.setMembers( s );
    		clusteredSegments.add(c);
		}
    	
    	System.out.println("ok");
    }
}
