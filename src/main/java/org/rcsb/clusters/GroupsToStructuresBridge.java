package org.rcsb.clusters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rcsb.sequences.ReadBlastClusters;

import com.google.common.collect.Sets;

public class GroupsToStructuresBridge {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		// Blast clusters
    	List<Cluster> clusters = new ReadBlastClusters().read("/pdb/bc-100.out.TEST");

		MemberId m1 = MemberId.create("5LF3_U", "_");
		MemberId m2 = MemberId.create("5LEX_G", "_");
		MemberId m3 = MemberId.create("5LEY_U", "_");
		Set<MemberId> subset = Sets.newHashSet(m1, m2, m3);
		
		run(clusters, subset);
    	
	}
	
	public static void run(List<Cluster> clusters, Set<MemberId> subset) {
		
		for (Cluster cluster : clusters) {

			printSet(cluster.getMembers());
    		
			Set<MemberId> s = new HashSet<>(subset);
			printSet(s);
			
    		s.retainAll(cluster.getMembers());
    		printSet(s);
		}
	}
	
	public static void printSet(Set<MemberId> s) {
		
		List<String> l = new ArrayList<>();
		Iterator<MemberId> i = s.iterator();
		while (i.hasNext()) {
			MemberId m = i.next();
			l.add(m.getPdbCode()+"_"+m.getChainId());
		}
		System.out.println(String.join(" ", l));
	}
}
