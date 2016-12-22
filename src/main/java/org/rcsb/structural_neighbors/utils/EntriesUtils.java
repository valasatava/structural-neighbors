package org.rcsb.structural_neighbors.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rcsb.structural_neighbors.structures.MemberId;

public class EntriesUtils {
	
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
