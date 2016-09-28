package org.rcsb.clusters;

import java.util.HashSet;
import java.util.Set;

public class Cluster {
	
	private int id;
	private Set<MemberId> members = new HashSet<MemberId>();

	public Cluster(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public Set<MemberId> getMembers() {
		return members;
	}
	
	public void setMembers(Set<MemberId> members) {
		for (MemberId member : members) {
			addMember(member);
		}
	}
	
	public int getSize() {
		return this.members.size();
	}
	
	public void addMember(MemberId member) {
		members.add(member);
	}
}
