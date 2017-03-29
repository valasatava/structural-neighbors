package org.rcsb.structural_neighbors.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point3d;

import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.mmtf.decoder.GenericDecoder;

public class StructureDecoder {

	private GenericDecoder decoder;
	
	public StructureDecoder(MmtfStructure inputData) {	
		decoder = new GenericDecoder(inputData);
	}
	
	private int getChainIndex(String chainId) {
		
		String[] chains = decoder.getChainNames();
		// Public chain ID (not unique - ligands will have same id as polymer chains)
		// TODO check the entity type
		return Arrays.asList(chains).indexOf(chainId);
	}
	
	private int getGroupPointer(int chainInd) {
		int groupInd = 0;
		for (int i=0; i < chainInd; i++) {
			groupInd+=decoder.getGroupsPerChain()[i];
		}
		return groupInd;
	}

	private int getAtomPointer(int chainInd) {
		
		int groupInd = 0;
		int atomInd = 0;
		
		for (int i=0; i < chainInd; i++) {
			int groups = decoder.getGroupsPerChain()[i];
			for (int gi=0; gi<groups; gi++) {
				atomInd += decoder.getNumAtomsInGroup(decoder.getGroupTypeIndices()[groupInd]);
				groupInd++;
			}
		}
		return atomInd;
	}
	
	public String getChainSequence(String chainId) {

		int chainInd = getChainIndex(chainId);
		int groupInd = getGroupPointer(chainInd);

		String sequence = "";
		int groups = groupInd+decoder.getGroupsPerChain()[chainInd];
		for (int j=groupInd; j < groups; j++) {
			int groupTypeInd = decoder.getGroupTypeIndices()[j];
			sequence += decoder.getGroupSingleLetterCode(groupTypeInd);
		}
		return sequence;
	}
	
	public Point3d[] getChainAtoms(String chainId) {
		
		int chainInd = getChainIndex(chainId);
		int groupInd = getGroupPointer(chainInd);
		int atomInd = getAtomPointer(chainInd);
		
		char[] altLocIds = decoder.getAltLocIds();
		
		List<Point3d> atoms = new ArrayList<Point3d>();
		int groups = groupInd+decoder.getGroupsPerChain()[chainInd];
		for (int j=groupInd; j < groups; j++) {
			int numAtoms = decoder.getNumAtomsInGroup(decoder.getGroupTypeIndices()[j]);
			for (int ai=0; ai<numAtoms; ai++) {
				char al = altLocIds[atomInd+ai];
				if ( al != '\0') {
					if (al != 'A') { continue; } // ignore alternative locations
				}
				float x = decoder.getxCoords()[atomInd+ai];
				float y = decoder.getyCoords()[atomInd+ai];
				float z = decoder.getzCoords()[atomInd+ai];
				atoms.add(new Point3d(x,y,z));
			}
			atomInd+=numAtoms;
		}
		Point3d[] array = atoms.toArray(new Point3d[atoms.size()]);
		return array;
	}
}
