package org.rcsb.structures.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.rcsb.clusters.SimilarityMatrix;
import org.rcsb.mmtf.spark.mappers.FlatMapIntList;
import org.rcsb.mmtf.spark.mappers.MapToPairs;
import org.rcsb.structures.mappers.SetAlignmentScores;
import org.rcsb.structures.utils.WritableCluster;
import org.rcsb.structures.utils.WritableClusterProvider;
import org.rcsb.structures.utils.WritableSegment;

import loschmidt.clustering.api.Cluster;
import loschmidt.clustering.api.ClusterFactory;
import loschmidt.clustering.api.Clusters;
import loschmidt.clustering.hierarchical.CompleteLinkage;
import loschmidt.clustering.hierarchical.Merge;
import loschmidt.clustering.hierarchical.Tree;
import loschmidt.clustering.hierarchical.murtagh.Murtagh;
import loschmidt.clustering.hierarchical.murtagh.MurtaghParams;
import loschmidt.clustering.io.MCUPGMATreeWriter;

public class Sandbox {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
//		test();
		
		long start = System.nanoTime();
		
		//writeMatrix();
		//testClustering();
		writeTree();
		long end = System.nanoTime();
		System.out.println("Time: " + ((end-start)/1E9)+"(sec)");
	}
	
	public static void test() {
		
		long start = System.nanoTime();
		
		String dataPath = "/pdb/x-rayChains_bc-95.seq.TEST";
		WritableClusterProvider provider = new WritableClusterProvider(dataPath);
		provider.startSpark();
		List<WritableCluster> clusters = provider.getClusters();
		for (WritableCluster cluster : clusters) {
			System.out.println(cluster.getId()+": "+cluster.getMembers().length);
		}

		provider.stopSpark();
		
		long end = System.nanoTime();
		System.out.println("Time: " + ((end-start)/1E9)/60+"(min)");
	}
	
	public static void writeMatrix() throws IOException {
		
		String dataPath = "/pdb/x-rayChains_bc-95.seq.TEST";
		WritableClusterProvider provider = new WritableClusterProvider(dataPath);
		provider.startSpark();
		WritableCluster cluster = provider.getClusterById(1);

		WritableSegment[] segments = cluster.getMembers();
		String[] names = cluster.getMembersId();
		
		SimilarityMatrix matrix = new SimilarityMatrix(segments.length); 
		matrix.setNames(names);
				
		// get indices for all unique pairs given the number of segments
		int numMembers = names.length;
		JavaRDD<Integer> singleInt = provider.getSparkContext().parallelize(Arrays.asList(numMembers));
		JavaRDD<Integer> multipleInts = singleInt.flatMap(new FlatMapIntList());
		JavaPairRDD<Integer, Integer> pairs = multipleInts.flatMapToPair(new MapToPairs(numMembers));

		Broadcast<SimilarityMatrix> mData = provider.getSparkContext().broadcast(matrix);
		Broadcast<WritableSegment[]> sData = provider.getSparkContext().broadcast(segments);
		pairs.repartition(8).foreach( new SetAlignmentScores(mData, sData));
		
		provider.stopSpark();
		
		String filePath = "/pdb/matrix.test";
		File f = new File(filePath);
	    if (f.exists()) {
	       f.delete();     
	    }
		matrix.writeToFile(filePath);
	}
	
	public static SimilarityMatrix readMatrix(String filePath) throws ClassNotFoundException, IOException {
		SimilarityMatrix matrix = SimilarityMatrix.readFromFile(filePath);
		return matrix;
	}
	
	public static void testClustering() throws ClassNotFoundException, IOException {
		
		int murtaghMatrixSize = 100000;
        MurtaghParams params = new MurtaghParams().setDistanceMatrixThreshold(murtaghMatrixSize);
        params.setLinkage(new CompleteLinkage());
  
		String filePath = "/pdb/matrix.test";
		SimilarityMatrix matrix = readMatrix(filePath);
		
		ClusterFactory<Integer> cf = new ClusterFactory<Integer>(matrix, params);

		double clusteringThreshold = 1.0;
        Clusters<Integer> cs = cf.cluster(clusteringThreshold);
        cs.print();
        
        List<Cluster<Integer>> clusters = cs.get();
        for (Cluster<Integer> cluster : clusters) {
        	System.out.println(cluster.get().get(0));
        	System.out.println(matrix.getNameById(cluster.get().get(0)));
		}
	}
	
	public static void writeTree() throws ClassNotFoundException, IOException {
		
		int murtaghMatrixSize = 100000;
        MurtaghParams params = new MurtaghParams().setDistanceMatrixThreshold(murtaghMatrixSize);
        params.setLinkage(new CompleteLinkage());
  
		String filePath = "/pdb/matrix.test";
		SimilarityMatrix matrix = readMatrix(filePath);
		
//		Murtagh a = new Murtagh(matrix, params);
//		Tree tree = a.contructTree();
//		
//		String treeFile = "/pdb/tree.out";
//		Writer writer = new BufferedWriter(new OutputStreamWriter(
//		          new FileOutputStream(treeFile), "utf-8"));
//		new MCUPGMATreeWriter().write(tree, writer);
		
		String[] names = matrix.getNames();
		for (int i=0; i<names.length; i++) {
			System.out.println((i+1)+"\t"+names[i]);
		}
	}
}
