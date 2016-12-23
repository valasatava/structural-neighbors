package org.rcsb.structural_neighbors.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import org.apache.hadoop.io.Text;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.mmtf.codec.StringCodecs;
import org.rcsb.mmtf.dataholders.Entity;
import org.rcsb.mmtf.dataholders.MmtfStructure;
import org.rcsb.structural_neighbors.structures.WritableSegment;
import org.rcsb.structural_neighbors.utils.StructureDecoder;

import scala.Tuple2;

/**
 * 
 * @author Yana Valasatava
 *
 */
public class WritableSegmentProvider {
			
	public static WritableSegment[] getMoleculesFromMmtfStructures(List<MmtfStructure> structures, String moleculeName) throws IOException {

		List<WritableSegment> segments = new ArrayList<WritableSegment>();
		for (MmtfStructure mmtf : structures) {
			String[] chainNameList = StringCodecs.decodeArr(mmtf.getChainNameList());
			StructureDecoder api = new StructureDecoder(mmtf);
			Entity[] entities = mmtf.getEntityList();
			for (Entity entity : entities) {
				if ( entity.getDescription().toLowerCase().contains(moleculeName)) {
					int[] chainIds = entity.getChainIndexList();
					for (int i : chainIds) {
						String id = mmtf.getStructureId()+"."+chainNameList[i];
						String sequence = api.getChainSequence(chainNameList[i]);
						Point3d[] structure = api.getChainAtoms(chainNameList[i]);
						WritableSegment segment = new WritableSegment(id, sequence, structure);
						segments.add(segment);
					}
				}
			}
		}
		WritableSegment[] array = segments.toArray(new WritableSegment[segments.size()]);
		return array;
	}
	
	public static WritableSegment[] getFromHadoop(JavaSparkContext sc, String path) {

		List<Tuple2<String, WritableSegment>> data = sc.sequenceFile(path, Text.class, WritableSegment.class)
		.mapToPair(t -> new Tuple2<String, WritableSegment> (new String(t._1.toString()), 
															 new WritableSegment(t._2)) )
		.collect();
		sc.stop();
		
		WritableSegment[] segments = new WritableSegment[data.size()];
		for (int i=0; i < data.size(); i++) {
			segments[i] = data.get(i)._2;
		}
		return segments;
	}
}
