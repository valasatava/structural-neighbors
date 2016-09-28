package org.rcsb.clusters;

import java.util.HashSet;
import java.util.Set;

public class Sandbox {
	
	public static void main(String[] args) {
		
		MemberId m1 = MemberId.create("1111_A", "_");
		MemberId m2 = MemberId.create("2222_A", "_");
		MemberId m3 = MemberId.create("1111_B", "_");
		
		Set<MemberId> s1 = new HashSet<>();
		s1.add(m1);
		
		Set<MemberId> s2 = new HashSet<>();
		s2.add(m1);
		s2.add(m2);
		s2.add(m3);
		
		s1.retainAll(s2);
		for (MemberId m : s1) {
			System.out.println(m.getPdbCode()+"_"+m.getChainId());
		}
	}
}
