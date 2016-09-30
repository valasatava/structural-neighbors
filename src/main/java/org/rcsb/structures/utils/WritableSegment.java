package org.rcsb.structures.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import javax.vecmath.Point3d;

import org.apache.hadoop.io.Writable;

/**
 * Simple data structure for segments of proteins, e.g. fragments and chains that can be
 * written and read from a Hadoop sequence file.
 * 
 * @author Anthony Bradley
 * @author Peter Rose
 *
 */
public class WritableSegment implements Writable, Serializable {
	
	private static final long serialVersionUID = 6036726463051044340L;
	
	private String id;
	private String sequence;
	private Point3d[] structure;

	public WritableSegment() {
		
	}
	/**
	 * Constructor for the {@link Segment} object.
	 * @param sequence the {@link String} id of the object
	 * @param sequence the {@link String} sequence of the object
	 * @param structure the {@link Point3d} array of the structure of the object
	 */
	public WritableSegment(String id, String sequence, Point3d[] structure) {
		this.id = id;
		this.sequence = sequence;
		this.structure = structure;
	}
	
	/**
	 * Constructor for the {@link Segment} object.
	 * @param sequence the {@link String} id of the object
	 * @param sequence the {@link String} sequence of the object
	 * @param structure the {@link Point3d} array of the structure of the object
	 */
	public WritableSegment(WritableSegment segment) {
		this.id = segment.id;
		this.sequence = segment.sequence;
		this.structure = segment.structure;
	}

	/**
	 * Returns the ID of the segment.
	 * @return the ID of this segment as one string 
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the sequence of the segment.
	 * @return the sequence of this segment as one letter 
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Returns the 3D coordinate of this segment.
	 * @return the {@link Point3d} array specifying the structure of this segment.
	 */
	public Point3d[] getCoordinates() {
		return structure;
	}
	
	// TODO this only works for the reduced MMTF structures

	public void write(DataOutput out) throws IOException {
		
		out.writeInt(this.id.length());
		out.write(this.id.getBytes());
		
		out.writeInt(this.sequence.length());
		out.write(this.sequence.getBytes());
		
		for (Point3d p: this.structure) {
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

	public void readFields(DataInput in) throws IOException {
		
		int lId = in.readInt();
		byte[] bId = new byte[lId];
		in.readFully(bId);
		this.id = new String(bId);
		
		int length = in.readInt();
		byte[] bytes = new byte[length];
		in.readFully(bytes);
		this.sequence = new String(bytes);
		
		this.structure = new Point3d[length];
		for (int i = 0; i < length; i++) {
			short x = in.readShort();
			short y = in.readShort();
			short z = in.readShort();
			if (x < Short.MAX_VALUE) {
				this.structure[i] = new Point3d(x*0.1, y*0.1, z*0.1);
			} 
		}	
	}
}
