package org.rcsb.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.spark.broadcast.Broadcast;
import org.rcsb.clusters.Cluster;
import org.rcsb.clusters.GroupsToStructuresBridge;
import org.rcsb.clusters.MemberId;
import org.rcsb.structures.utils.WritableCluster;
import org.rcsb.structures.utils.WritableSegment;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

/**
 * Cluster Writable Segments based on grouping provided as Cluster 
 * 
 */
public class GroupSegmentsInClusters implements Function <Cluster, WritableCluster> {

	private static final long serialVersionUID = -8746314796113234949L;
	private Broadcast<List<Tuple2<String, WritableSegment>>> data = null;
	public GroupSegmentsInClusters(Broadcast<List<Tuple2<String,WritableSegment>>> data) {
		this.data = data; // accommodate the broadcasted data
	}
	/**
	 * 
	 * 
	 */ 
	public WritableCluster call(Cluster cluster) throws Exception {
		
		// get cluster members
		Set<MemberId> segmqntIds = data.getValue()
				.stream()
				.map(t -> MemberId.create(t._1, "."))
				.collect(Collectors.toSet());
		
		// filter entries that belong to the cluster from the pool of all segments
		Cluster clusteredSegments = GroupsToStructuresBridge.clusterSegments(cluster.getId(), cluster, segmqntIds);
		if (clusteredSegments == null)
			return null;
		
		// get cluster of writable segments
		int i=0;
		Set<MemberId> clusterMembers = clusteredSegments.getMembers();
		WritableSegment[] segments = new WritableSegment[clusterMembers.size()];
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
