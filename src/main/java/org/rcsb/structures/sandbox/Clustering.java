//package org.rcsb.structures.sandbox;
//
//import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
//import com.apporiented.algorithm.clustering.Cluster;
//import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
//import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
//import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;
//
//
//public class Clustering {
//
//	public static void main(String[] args) {
//		
//		String[] names = new String[] { "O1", "O2", "O3", "O4", "O5", "O6" };
//		double[][] distances = new double[][] { 
//		    { 0.0,  1.0, 9.0,  7.0,  11.0, 14.0 },
//		    { 1.0,  0.0, 4.0,  3.0,  8.0,  10.0 }, 
//		    { 9.0,  4.0, 0.0,  9.0,  2.0,  8.0 },
//		    { 7.0,  3.0, 9.0,  0.0,  6.0,  13.0 }, 
//		    { 11.0, 8.0, 2.0,  6.0,  0.0,  10.0 },
//		    { 14.0, 10.0, 8.0, 13.0, 10.0, 0.0 }};
//
//		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
//		
//		@SuppressWarnings("unchecked")
//		Cluster<String> c = alg.performClustering(distances, names, new AverageLinkageStrategy());
//		
//		@SuppressWarnings("rawtypes")
//		DendrogramPanel dp = new DendrogramPanel();
//		dp.setModel(c);
//	
//	}
//}
