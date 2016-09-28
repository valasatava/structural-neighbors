package org.rcsb.sequences.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.rcsb.clusters.Cluster;
import org.rcsb.clusters.MemberId;

public class ReadBlastClusters {
		
	public List<Cluster> read (String dataPath) throws FileNotFoundException, IOException {
		
		List<Cluster> clusters = new ArrayList<Cluster>();

		int i = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(dataPath))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	
		    	Cluster cluster = new Cluster(i++);
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
