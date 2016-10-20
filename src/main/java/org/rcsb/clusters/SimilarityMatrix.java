package org.rcsb.clusters;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SimilarityMatrix implements Serializable {
	
	private static final long serialVersionUID = -1750298400106716087L;
	
	String[] names;
	float[][] disatances;
	public SimilarityMatrix(int n) {
		disatances = new float[n][n];
		names = new String[n];
	}

	public void writeToFile(String filePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
	}
	
	public SimilarityMatrix readFromFile(String filePath) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		SimilarityMatrix matrix = (SimilarityMatrix) ois.readObject();
		ois.close();
		return matrix;
	}
}
