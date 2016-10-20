package org.rcsb.sequences;

/**
 * Given two string sequences, write an algo­rithm to find the length of longest sub­se­quence present in both of them.
 * 
 * @author Yana Valasatava
 * @author http://algorithms.tutorialhorizon.com/dynamic-programming-longest-common-subsequence/ 
 *
 */

public class LongestCommonSubsequence {
	
	private char[] firstString;
	private char[] secondString;
	
	public LongestCommonSubsequence(String string1, String string2) {
		
		this.firstString = string1.toCharArray();
		this.secondString = string2.toCharArray();
	}
	
	public char[] getFirstString() {
		return this.firstString;
	}
	
	public char[] getSecondString() {
		return this.secondString;
	}
	
	public String find() {
		
		int[][] LCS = new int[this.firstString.length + 1][this.secondString.length + 1];
		String[][] solution = new String[this.firstString.length + 1][this.secondString.length + 1];
		
		// if A is null then LCS of A, B = 0
		for (int i = 0; i <= this.secondString.length; i++) {
			LCS[0][i] = 0;
			solution[0][i] = "0";
		}

		// if B is null then LCS of A, B = 0
		for (int i = 0; i <= this.firstString.length; i++) {
			LCS[i][0] = 0;
			solution[i][0] = "0";
		}

		for (int i = 1; i <= this.firstString.length; i++) {
			for (int j = 1; j <= this.secondString.length; j++) {
				if (this.firstString[i - 1] == this.secondString[j - 1]) {
					LCS[i][j] = LCS[i - 1][j - 1] + 1;
					solution[i][j] = "diagonal";
				} else {
					LCS[i][j] = Math.max(LCS[i - 1][j], LCS[i][j - 1]);
					if (LCS[i][j] == LCS[i - 1][j]) {
						solution[i][j] = "top";
					} else {
						solution[i][j] = "left";
					}
				}
			}
		}
		// below code is to just print the result
		String x = solution[this.firstString.length][this.secondString.length];
		String answer = "";
		int a = this.firstString.length;
		int b = this.secondString.length;
		while (x != "0") {
			if (solution[a][b] == "diagonal") {
				answer = this.firstString[a - 1] + answer;
				a--;
				b--;
			} else if (solution[a][b] == "left") {
				b--;
			} else if (solution[a][b] == "top") {
				a--;
			}
			x = solution[a][b];
		}
		
		return answer;
	}
	
	public int[][] alignSequences() {
		
		String common = this.find();

		int[][] inds = new int[common.length()][2];

		int j = 0; int k = 0;
		int ind1 = 0; int ind2 = 0;
		for (int i = 0; i < common.length(); i++){

			char aa = common.charAt(i);

			while ( this.getFirstString()[j] != aa ) { 
				j++; ind1=j; 
			}
			while ( this.getSecondString()[k] != aa ) { 
				k++; ind2=k;
			}

			inds[i][0] = ind1; inds[i][1] = ind2;
			ind1++; ind2++;
		}
		return inds;
	}
	
	public static void main(String[] args) {
		
		String A = "AREGLAK";
		String B = "LAPREGK";
		
		LongestCommonSubsequence lcs = new LongestCommonSubsequence(A, B);
		System.out.println("LCS : " + lcs.find());
	}
}
