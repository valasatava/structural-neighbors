package org.rcsb.structures.clustering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

import org.rcsb.clusters.Cluster;
import org.rcsb.clusters.MemberId;
import org.rcsb.sequences.utils.ReadBlastClusters;
import org.rcsb.structures.utils.WritableSegment;
import org.rcsb.structures.utils.WritableSegmentProvider;

import scala.Tuple2;

/**
 *
 */
public class AssignToBlastClusters {
	
    public static void main( String[] args ) throws FileNotFoundException, IOException {

    	WritableSegmentProvider provider = new WritableSegmentProvider();
    	List<Tuple2<String, WritableSegment>> segments = provider.readSegments("/pdb/x-rayChains.seq");
    	
    	List<String> segmentIds = segments.stream().map( t -> (String) t._1).collect(Collectors.toList());
    	
    	Set<MemberId> segmentMemberIds = new HashSet<>();
    	for (String segmentId : segmentIds) {
    		MemberId m = MemberId.create(segmentId, ".");
    		segmentMemberIds.add(m);
		}
    	
    	// Blast clusters
    	List<Cluster> clusters = new ReadBlastClusters().read("/pdb/bc-100.out.TEST");
    	
    	// Segments clusters
    	List<Cluster> clustersOfSegments = new ArrayList<>();
    	
    	int j = 0;
    	MemberId o = MemberId.create("1IRU_A", "_");
    	//System.out.println(o.getPdbCode()+"_"+o.getChainId());
    	
    	for (Cluster cluster : clusters) {
    		
    		Set<MemberId> cs = cluster.getMembers();
//    		for (MemberId m : cs) {
//    			System.out.println(m.getPdbCode()+"_"+m.getChainId());
//			}
    		if (cs.contains(o))
    			System.out.println("ok");
    		else
    			continue;
    		
    		Set<MemberId> s = new HashSet<>(segmentMemberIds);   		
    		s.retainAll(cluster.getMembers());
    		if (s.isEmpty())
    			continue;
    		
    		System.out.println("ok");
    		Cluster c = new Cluster(j++);
    		c.setMembers( s );
    		clustersOfSegments.add(c);
		}
    	
    	System.out.println("ok");
    }
}
