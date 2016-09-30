package org.rcsb.clusters;

import java.io.Serializable;
import java.util.StringTokenizer;

public class MemberId implements Serializable {

	private static final long serialVersionUID = -1389093499160900134L;
	
	private String pdbCode;
	private String chainId;

	private MemberId(String s1, String s2) {
		this.pdbCode = s1;
		this.chainId = s2;
	}

	public static MemberId create(String id, String sep) {

		MemberId memberId = null;
		if (sep == "") {
			memberId = new MemberId(id.substring(0, 4), id.substring(4));
		} else {
			StringTokenizer st = new StringTokenizer(id, sep);
			memberId = new MemberId(st.nextToken(), st.nextToken());
		}
		return memberId;
	}

	public String getPdbCode() {
		return pdbCode;
	}

	public void setPdbCode(String pdbCode) {
		this.pdbCode = pdbCode;
	}

	public String getChainId() {
		return chainId;
	}

	public void setChainId(String chainId) {
		this.chainId = chainId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chainId == null) ? 0 : chainId.hashCode());
		result = prime * result + ((pdbCode == null) ? 0 : pdbCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		MemberId memberId = (MemberId) o;	
		return pdbCode.equals(memberId.pdbCode) && chainId.equals(memberId.chainId);
	}
}
