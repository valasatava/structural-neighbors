package org.rcsb.structural_neighbors.structures;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Group implements Serializable {
	
	private static final long serialVersionUID = -1844152292150244102L;
	
	private int id;
	private Set<MemberId> members = new HashSet<MemberId>();

	public Group(int id) {
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
	
	public Set<MemberId> retainAll(Set<MemberId> entries) {
		Set<MemberId> s = new HashSet<>(entries);
		s.retainAll(this.getMembers());
		return s;	
	}
}
