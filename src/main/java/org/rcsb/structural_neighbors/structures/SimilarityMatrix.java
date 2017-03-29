package org.rcsb.structural_neighbors.structures;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import loschmidt.clustering.distance.DistanceProvider;

public class SimilarityMatrix implements Serializable, DistanceProvider<Integer>{
	
	private static final long serialVersionUID = -1750298400106716087L;
	
	String[] names;
	float[][] disatancesRMSD;
	float[][] disatancesTMScore;
	
	public SimilarityMatrix() {	
	}
	
	public SimilarityMatrix(int n) {
		names = new String[n];
		disatancesRMSD = new float[n][n];
		disatancesTMScore = new float[n][n];
	}

	public String[] getNames() {
		return names;
	}
	
	public String getNameById(int id) {
		return names[id];
	}
	
	public void setNames(String[] names) {
		this.names = names;
	}
	
	public void setRMSD(float score, int i1, int i2) {
		disatancesRMSD[i1][i2] = score;
		disatancesRMSD[i2][i1] = score;
	}
	
	public void setTMScore(float score, int i1, int i2) {
		disatancesTMScore[i1][i2] = score;
		disatancesTMScore[i2][i1] = score;
	}
	
	public void writeToFile(String filePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
	}
	
	public static SimilarityMatrix readFromFile(String filePath) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		SimilarityMatrix matrix = (SimilarityMatrix) ois.readObject();
		ois.close();
		return matrix;
	}

	@Override
	public float getDistance(int x, int y) {
		return disatancesRMSD[x][y];
	}

	@Override
	public int size() {
		return names.length;
	}

	@Override
	public Integer get(int x) {
		return x;
	}

	@Override
	public int getElementSize(int x) {
		return 1;
	}
	
	public float getAverageScoreToMembers(int id, List<Integer> members, String score) {
		
		float avrScore = 0.0f;
		if (score.equals("RMSD")) {
			for (Integer i : members) {
				if (i != id ) {
					avrScore += getDistance(id, i);
				}
			}
		}
		if (members.size() > 2)
			avrScore = avrScore/members.size();
		return avrScore;
	}
}
