package org.rcsb.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.spark.broadcast.Broadcast;
import org.rcsb.clusters.Cluster;
import org.rcsb.clusters.GroupsToStructuresBridge;
import org.rcsb.clusters.MemberId;
import org.rcsb.clusters.WritableCluster;
import org.rcsb.structures.utils.WritableSegment;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

/**
 * 
 */
public class GroupSegmentsInClusters implements Function <Cluster, WritableCluster> {

	private static final long serialVersionUID = -8746314796113234949L;
	
	private Broadcast<List<Tuple2<String, WritableSegment>>> data = null;
	
	public GroupSegmentsInClusters(Broadcast<List<Tuple2<String,WritableSegment>>> data) {
		this.data = data;
	}

	
	public WritableCluster call(Cluster cluster) throws Exception {
		
		Set<MemberId> segmqntIds = data.getValue()
				.stream()
				.map(t -> MemberId.create(t._1, "."))
				.collect(Collectors.toSet());
		
		Cluster clusteredSegments = GroupsToStructuresBridge.clusterSegments(cluster.getId(), cluster, segmqntIds);
		if (clusteredSegments == null)
			return null;
		
		Set<MemberId> clusterMembers = clusteredSegments.getMembers();

		WritableSegment[] segments = new WritableSegment[clusterMembers.size()];
		
		int i=0;
		for (MemberId m : clusterMembers) {
			
			WritableSegment s = data.getValue()
					.stream()
					.filter(t -> t._1.equals(m.getPdbCode()+"."+m.getChainId()))
					.collect(Collectors.toList())
					.get(0)._2;
			
			segments[i] = new WritableSegment(s);
			i++;
		}
		
		return new WritableCluster(cluster.getId(), segments);
	}
}
