package org.rcsb.structural_neighbors.clustering;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.rcsb.mmtf.spark.mappers.FlatMapIntList;
import org.rcsb.mmtf.spark.mappers.MapToPairs;
import org.rcsb.structural_neighbors.mappers.SetAlignmentScores;
import org.rcsb.structural_neighbors.structures.SimilarityMatrix;
import org.rcsb.structural_neighbors.structures.WritableSegment;

import com.google.common.io.Files;

import loschmidt.clustering.api.Cluster;
import loschmidt.clustering.api.ClusterFactory;
import loschmidt.clustering.api.Clusters;
import loschmidt.clustering.hierarchical.CompleteLinkage;
import loschmidt.clustering.hierarchical.Merge;
import loschmidt.clustering.hierarchical.Tree;
import loschmidt.clustering.hierarchical.murtagh.Murtagh;
import loschmidt.clustering.hierarchical.murtagh.MurtaghParams;

public class HierarchicalClusteringFactory {
	
	private String[] names;
	private WritableSegment[] segments;
	private SimilarityMatrix matrix;
	private ClusterFactory<Integer> cf;
	Clusters<Integer> partition;
	
	public HierarchicalClusteringFactory(WritableSegment[] data) {
		segments = data;
		int i =0;
		names = new String[segments.length];
		for (WritableSegment s : segments) {
			names[i]=s.getId();
			i++;
		}
		matrix = new SimilarityMatrix(segments.length); 
		matrix.setNames(names);
	}
	
	private void buildSimilarityMatrix(JavaSparkContext sc) {

		// get indices for all unique pairs given the number of segments
		int numMembers = names.length;
		JavaRDD<Integer> singleInt = sc.parallelize(Arrays.asList(numMembers));
		JavaRDD<Integer> multipleInts = singleInt.flatMap(new FlatMapIntList());
		JavaPairRDD<Integer, Integer> pairs = multipleInts.flatMapToPair(new MapToPairs(numMembers));

		Broadcast<SimilarityMatrix> mData = sc.broadcast(matrix);
		Broadcast<WritableSegment[]> sData = sc.broadcast(segments);
		pairs.repartition(8).foreach( new SetAlignmentScores(mData, sData));
	}
	
	private SimilarityMatrix getSimilarityMatrix() {
		return matrix;
	}
	
	private MurtaghParams getPatameters() {
		int murtaghMatrixSize = 100000;
        MurtaghParams params = new MurtaghParams().setDistanceMatrixThreshold(murtaghMatrixSize);
        return params.setLinkage(new CompleteLinkage<Double>());
	}
	
	public void run(JavaSparkContext sc) {
		buildSimilarityMatrix(sc);
        cf = new ClusterFactory<Integer>(matrix, getPatameters());
	}
	
	public void partition(double clusteringThreshold) {
		
		partition = cf.cluster(clusteringThreshold);
        List<Cluster<Integer>> p = partition.get();
        int cId = 0;
        for (Cluster<Integer> cluster : p) {
        	cluster.setId(cId++);
        }
	}
	
	public void writeTree(String base) throws ClassNotFoundException, IOException {
		
		// Write members IDs
		File fm = new File(base+"_ids.out");
		String[] names = getSimilarityMatrix().getNames();
		Files.append("ID\tPDBID.CHAINID\n", fm, Charset.defaultCharset());
		for (int i=0; i<names.length; i++) {
			Files.append(String.format("%d\t", (i+1))+names[i]+"\n", fm, Charset.defaultCharset());
		}
		
		// Write the tree
		File ft = new File(base+"_tree.out");
		Files.append("CID\tCID\tDistance\tMID\n", ft, Charset.defaultCharset());
		Murtagh a = new Murtagh(getSimilarityMatrix(), getPatameters());
		Tree tree = a.contructTree();
		tree.generateMergeIDs();
		for (Merge m : tree.getMerges()) {
			Files.append(String.format("%d\t%d\t%f\t%d", (m.getX() + 1), (m.getY() + 1), m.getDistance(), (m.getID() + 1))+"\n", ft, Charset.defaultCharset());
		}
	}
	
	private WritableSegment selectRepresentative(Cluster<Integer> cluster) {
		
		int representative = -1;
		float avr;
		float avrScore = Float.MAX_VALUE;
		
		if ( cluster.get().size() < 3 ) {
			return segments[0];
		}
		
		for (Integer m : cluster.get()) {
			avr = matrix.getAverageScoreToMembers(m, cluster.get(), "RMSD");
			if ( avr < avrScore) {
				avrScore = avr;
				representative = m;
			}
		}
		return segments[representative];
	}
	
	public Map<Integer, WritableSegment> getRepresentatives() {
		
		Map<Integer, WritableSegment> representatives = new HashMap<Integer, WritableSegment>();		
		for (Cluster<Integer> cluster : partition.get()) {
			representatives.put(cluster.getId(), selectRepresentative(cluster));
		}
		return representatives;
	}
}
