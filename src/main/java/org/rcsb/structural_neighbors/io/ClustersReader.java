package org.rcsb.structural_neighbors.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.rcsb.structural_neighbors.structures.Group;
import org.rcsb.structural_neighbors.structures.MemberId;

public class ClustersReader {
		
	public List<Group> readBlastClusters (String dataPath) throws FileNotFoundException, IOException {
		
		List<Group> clusters = new ArrayList<Group>();

		int i = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(dataPath))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	Group cluster = new Group(i++);
		    	StringTokenizer st = new StringTokenizer(line, " ");
		    	while (st.hasMoreTokens()) {
		    		MemberId m = MemberId.create(st.nextToken(), "_");
		    		cluster.addMember(m);
		    	}
		    	clusters.add(cluster);
		    }
		}
		return clusters;
	}
}
