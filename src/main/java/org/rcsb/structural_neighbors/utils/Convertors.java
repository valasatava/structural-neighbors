package org.rcsb.structural_neighbors.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rcsb.structural_neighbors.structures.WritableSegment;

public class Convertors {
	
	public static Map<String, WritableSegment> arrayToMap(WritableSegment[] array) {
		Map<String, WritableSegment> map = new HashMap<String, WritableSegment>();
		for (WritableSegment segment : array) {
			map.put(segment.getId(), segment);
		}
		return map;
	}
	
	public static WritableSegment[] listToArray(List<WritableSegment> list) {
		WritableSegment[] array = new WritableSegment[list.size()];
		int i=-1;
		for (WritableSegment segment : list) {
			array[i++]=segment;
		}
		return array;
	}
}
