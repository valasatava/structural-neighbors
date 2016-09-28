package org.rcsb.clusters;

import java.util.StringTokenizer;

public class MemberId {

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
	public boolean equals(Object o) {

		MemberId memberId = (MemberId) o;
		System.out.println(memberId.pdbCode);
		
		if ((pdbCode.equals("1IRU")) || (memberId.pdbCode).equals("1IRU"))
			System.out.println("Found!"+memberId.pdbCode);
		
		return pdbCode.equals(memberId.pdbCode) && chainId.equals(memberId.chainId);
	}
}
