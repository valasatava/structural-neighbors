package org.rcsb.clusters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GroupsToStructuresBridge {
	
	public static Cluster clusterSegments(int id, Cluster cluster, Set<MemberId> entries) {
		
		Set<MemberId> s = cluster.retainAll(entries);
		if (s.isEmpty())
			return null;
		Cluster c = new Cluster(id);
		c.setMembers(s);
		
		return c;
		
	}
	public static List<Cluster> run(List<Cluster> clusters, Set<MemberId> entries) {
		
		// Segments clusters
    	List<Cluster> clusteredEntries= new ArrayList<>();
    	
    	int j = 0;
    	for (Cluster cluster : clusters) {
    		
    		Cluster c = clusterSegments(j++, cluster, entries);
    		clusteredEntries.add(c);
		}
    	return clusteredEntries;
	}
}
