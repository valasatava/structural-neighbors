package org.rcsb.structural_neighbors.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.decoder.ReaderUtils;
import org.rcsb.mmtf.serialization.MessagePackSerialization;

public class MmtfStructuresProvider {
	
	private static String base;
	public MmtfStructuresProvider(String path) {
		base = path;
	}
	
	public static List<MmtfStructure> getFromHadoop() {
		//TODO
		return null;
	}
	
 	public static MmtfStructure getMmtfFromURL(URL url) throws IOException {
		
		// Get these as an inputstream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream inputStream = null;
		try {
			inputStream = url.openStream();
			byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
			int n;
			while ( (n = inputStream.read(byteChunk)) > 0 ) {
				baos.write(byteChunk, 0, n);
			}
		} finally {
			if (inputStream != null) { inputStream.close(); }
		}
		byte[] byteArr = baos.toByteArray();
		
		// Now return the gzip deflated and deserialized byte array
		MessagePackSerialization mmtfBeanSeDeMessagePackImpl = new MessagePackSerialization();
		return mmtfBeanSeDeMessagePackImpl.deserialize(new ByteArrayInputStream(ReaderUtils.deflateGzip(byteArr)));
	}
	
	private List<String> readIdsFromFile(String uri, String sep) throws IOException {
		
		List<String> ids = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(uri));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    StringTokenizer st = new StringTokenizer(everything, sep);
		    while ( st.hasMoreTokens() ) {
		    	ids.add(st.nextToken());
		    }
		    
		} finally {
		    br.close();
		}
		return ids;
	}
	
	public List<MmtfStructure> getListedInFile(String uri) throws IOException {
		
		List<String> ids = readIdsFromFile(uri, ",");
		List<MmtfStructure> structures = new ArrayList<MmtfStructure>();
		for (String pdbCode : ids) {
			URL url = new URL(base+pdbCode);
			MmtfStructure mmtf = getMmtfFromURL(url);
			structures.add(mmtf);
		}
		return structures;
	}
}
