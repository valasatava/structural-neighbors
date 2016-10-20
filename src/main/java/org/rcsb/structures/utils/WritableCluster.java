package org.rcsb.structures.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import javax.vecmath.Point3d;

import org.apache.hadoop.io.Writable;

/**
 * Simple data structure for clusters of writable segments that can be
 * written and read from a Hadoop sequence file.
 * 
 * @author Yana Valasatava
 *
 */
public class WritableCluster implements Writable, Serializable {
	
	private static final long serialVersionUID = 8160857785236516943L;
	
	private int id;
	private WritableSegment[] members;

	public WritableCluster() {
		
	}
	/**
	 * Constructor for the {@link Segment} object.
	 * @param sequence the {@link String} id of the object
	 * @param sequence the {@link String} sequence of the object
	 * @param structure the {@link Point3d} array of the structure of the object
	 */
	public WritableCluster(int id, WritableSegment[] members) {
		
		this.id = id;
		this.members = members;
	}
	
	/**
	 * Constructor for the {@link Segment} object.
	 * @param sequence the {@link String} id of the object
	 * @param sequence the {@link String} sequence of the object
	 * @param structure the {@link Point3d} array of the structure of the object
	 */
	public WritableCluster(WritableCluster cluster) {
		this.id = cluster.id;
		this.members = cluster.members;
	}

	/**
	 * Returns the ID of the cluster.
	 * @return the ID of this cluster as integer 
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the array of cluster members.
	 * @return the {@link WritableSegment} array specifying the structures in this cluster.
	 */
	public WritableSegment[] getMembers() {
		return members;
	}
	
	// TODO this only works for the reduced structures
	
	public void write(DataOutput out) throws IOException {
		
		out.writeInt(id);
		out.writeInt(members.length);
		
		for (WritableSegment segment : members) {
			
			// write segment id
			out.writeInt(segment.getId().length());
			out.write(segment.getId().getBytes());
			
			// write segment sequence
			out.writeInt(segment.getSequence().length());
			out.write(segment.getSequence().getBytes());
			
			// write segment structure
			for (Point3d p: segment.getCoordinates()) {
				if (p == null) {
					out.writeShort(Short.MAX_VALUE);
					out.writeShort(Short.MAX_VALUE);
					out.writeShort(Short.MAX_VALUE);
				} else {
					if (p.x*10 > Short.MAX_VALUE || p.y*10 > Short.MAX_VALUE || p.z*10 > Short.MAX_VALUE) {
						System.out.println("Overflow");
					}
					out.writeShort((short)Math.round(p.x*10));
					out.writeShort((short)Math.round(p.y*10));
					out.writeShort((short)Math.round(p.z*10));
				}
			}
		}
	}

	public void readFields(DataInput in) throws IOException {
		
		id = in.readInt();
		
		int length = in.readInt();
		members = new WritableSegment[length];
		
		for (int i = 0; i < length; i++) {
			
			// read segment id
			int lenId = in.readInt();
			byte[] bytesId = new byte[lenId];
			in.readFully(bytesId);
			String id = new String(bytesId);
			
			// read segment sequence
			int lenSeq = in.readInt();
			byte[] bytesSeq = new byte[lenSeq];
			in.readFully(bytesSeq);
			String sequence = new String(bytesSeq);
			
			// read segment structure
			Point3d[] structure = new Point3d[lenSeq];
			for (int j = 0; j < lenSeq; j++) {
				short x = in.readShort();
				short y = in.readShort();
				short z = in.readShort();
				if (x < Short.MAX_VALUE) {
				   structure[j] = new Point3d(x*0.1, y*0.1, z*0.1);
				}
			}
			members[i] = new WritableSegment(id, sequence, structure);
		}
	}
}
