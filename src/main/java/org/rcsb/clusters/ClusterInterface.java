package org.rcsb.clusters;

public interface ClusterInterface {
	
	void setId(int i);
	void setMembersIds(String[] split);
	int getSize();
}
